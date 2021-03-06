package org.mochou.mymall.admin.service;

import com.github.pagehelper.PageInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mochou.mymall.admin.dao.GoodsAllinone;
import org.mochou.mymall.admin.util.CatVo;
import org.mochou.mymall.core.qcode.QCodeService;
import org.mochou.mymall.core.util.ResponseUtil;
import org.mochou.mymall.db.domain.*;
import org.mochou.mymall.db.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mochou.mymall.admin.util.AdminResponseCode.GOODS_NAME_EXIST;
import static org.mochou.mymall.admin.util.AdminResponseCode.GOODS_UPDATE_NOT_ALLOWED;

@Service
public class AdminGoodsService {
    private final Log logger = LogFactory.getLog(AdminGoodsService.class);

    @Autowired
    private MymallGoodsService goodsService;
    @Autowired
    private MymallGoodsSpecificationService specificationService;
    @Autowired
    private MymallGoodsAttributeService attributeService;
    @Autowired
    private MymallGoodsProductService productService;
    @Autowired
    private MymallCategoryService categoryService;
    @Autowired
    private MymallBrandService brandService;
    @Autowired
    private MymallCartService cartService;
    @Autowired
    private MymallOrderGoodsService orderGoodsService;

    @Autowired
    private QCodeService qCodeService;

    public Object list(String goodsSn, String name,
                       Integer page, Integer limit, String sort, String order) {
        List<MymallGoods> goodsList = goodsService.querySelective(goodsSn, name, page, limit, sort, order);
        long total = PageInfo.of(goodsList).getTotal();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", goodsList);

        return ResponseUtil.ok(data);
    }

    private Object validate(GoodsAllinone goodsAllinone) {
        MymallGoods goods = goodsAllinone.getGoods();
        String name = goods.getName();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.badArgument();
        }
        String goodsSn = goods.getGoodsSn();
        if (StringUtils.isEmpty(goodsSn)) {
            return ResponseUtil.badArgument();
        }
        // ?????????????????????????????????????????????????????????????????????
        Integer brandId = goods.getBrandId();
        if (brandId != null && brandId != 0) {
            if (brandService.findById(brandId) == null) {
                return ResponseUtil.badArgumentValue();
            }
        }
        // ???????????????????????????????????????????????????????????????
        Integer categoryId = goods.getCategoryId();
        if (categoryId != null && categoryId != 0) {
            if (categoryService.findById(categoryId) == null) {
                return ResponseUtil.badArgumentValue();
            }
        }

        MymallGoodsAttribute[] attributes = goodsAllinone.getAttributes();
        for (MymallGoodsAttribute attribute : attributes) {
            String attr = attribute.getAttribute();
            if (StringUtils.isEmpty(attr)) {
                return ResponseUtil.badArgument();
            }
            String value = attribute.getValue();
            if (StringUtils.isEmpty(value)) {
                return ResponseUtil.badArgument();
            }
        }

        MymallGoodsSpecification[] specifications = goodsAllinone.getSpecifications();
        for (MymallGoodsSpecification specification : specifications) {
            String spec = specification.getSpecification();
            if (StringUtils.isEmpty(spec)) {
                return ResponseUtil.badArgument();
            }
            String value = specification.getValue();
            if (StringUtils.isEmpty(value)) {
                return ResponseUtil.badArgument();
            }
        }

        MymallGoodsProduct[] products = goodsAllinone.getProducts();
        for (MymallGoodsProduct product : products) {
            Integer number = product.getNumber();
            if (number == null || number < 0) {
                return ResponseUtil.badArgument();
            }

            BigDecimal price = product.getPrice();
            if (price == null) {
                return ResponseUtil.badArgument();
            }

            String[] productSpecifications = product.getSpecifications();
            if (productSpecifications.length == 0) {
                return ResponseUtil.badArgument();
            }
        }

        return null;
    }

    /**
     * ????????????
     * <p>
     * TODO
     * ??????????????????????????????
     * 1. ??????mymall_goods???
     * 2. ????????????mymall_goods_specification???mymall_goods_attribute???mymall_goods_product
     * 3. ??????mymall_goods_specification???mymall_goods_attribute???mymall_goods_product
     * <p>
     * ?????????????????????????????????????????????????????????????????????
     * ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * ???????????????????????????????????????????????????????????????
     * ?????????????????????????????????????????????????????????????????????ID????????????????????????????????????
     * ???????????????????????????????????????????????????????????????????????????????????????
     * ???????????????????????????????????????
     */
    @Transactional
    public Object update(GoodsAllinone goodsAllinone) {
        Object error = validate(goodsAllinone);
        if (error != null) {
            return error;
        }

        MymallGoods goods = goodsAllinone.getGoods();
        MymallGoodsAttribute[] attributes = goodsAllinone.getAttributes();
        MymallGoodsSpecification[] specifications = goodsAllinone.getSpecifications();
        MymallGoodsProduct[] products = goodsAllinone.getProducts();

        Integer id = goods.getId();
        // ???????????????????????????????????????????????????
        // ????????????????????????????????????
        if (orderGoodsService.checkExist(id)) {
            return ResponseUtil.fail(GOODS_UPDATE_NOT_ALLOWED, "???????????????????????????????????????");
        }
        if (cartService.checkExist(id)) {
            return ResponseUtil.fail(GOODS_UPDATE_NOT_ALLOWED, "??????????????????????????????????????????");
        }

        //?????????????????????????????????????????????
        String url = qCodeService.createGoodShareImage(goods.getId().toString(), goods.getPicUrl(), goods.getName());
        goods.setShareUrl(url);

        // ?????????????????????mymall_goods
        if (goodsService.updateById(goods) == 0) {
            throw new RuntimeException("??????????????????");
        }

        Integer gid = goods.getId();
        specificationService.deleteByGid(gid);
        attributeService.deleteByGid(gid);
        productService.deleteByGid(gid);

        // ???????????????mymall_goods_specification
        for (MymallGoodsSpecification specification : specifications) {
            specification.setGoodsId(goods.getId());
            specificationService.add(specification);
        }

        // ???????????????mymall_goods_attribute
        for (MymallGoodsAttribute attribute : attributes) {
            attribute.setGoodsId(goods.getId());
            attributeService.add(attribute);
        }

        // ???????????????mymall_product
        for (MymallGoodsProduct product : products) {
            product.setGoodsId(goods.getId());
            productService.add(product);
        }
        qCodeService.createGoodShareImage(goods.getId().toString(), goods.getPicUrl(), goods.getName());

        return ResponseUtil.ok();
    }

    @Transactional
    public Object delete(MymallGoods goods) {
        Integer id = goods.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }

        Integer gid = goods.getId();
        goodsService.deleteById(gid);
        specificationService.deleteByGid(gid);
        attributeService.deleteByGid(gid);
        productService.deleteByGid(gid);
        return ResponseUtil.ok();
    }

    @Transactional
    public Object create(GoodsAllinone goodsAllinone) {
        Object error = validate(goodsAllinone);
        if (error != null) {
            return error;
        }

        MymallGoods goods = goodsAllinone.getGoods();
        MymallGoodsAttribute[] attributes = goodsAllinone.getAttributes();
        MymallGoodsSpecification[] specifications = goodsAllinone.getSpecifications();
        MymallGoodsProduct[] products = goodsAllinone.getProducts();

        String name = goods.getName();
        if (goodsService.checkExistByName(name)) {
            return ResponseUtil.fail(GOODS_NAME_EXIST, "?????????????????????");
        }

        // ?????????????????????mymall_goods
        goodsService.add(goods);

        //?????????????????????????????????????????????
        String url = qCodeService.createGoodShareImage(goods.getId().toString(), goods.getPicUrl(), goods.getName());
        if (!StringUtils.isEmpty(url)) {
            goods.setShareUrl(url);
            if (goodsService.updateById(goods) == 0) {
                throw new RuntimeException("??????????????????");
            }
        }

        // ???????????????mymall_goods_specification
        for (MymallGoodsSpecification specification : specifications) {
            specification.setGoodsId(goods.getId());
            specificationService.add(specification);
        }

        // ???????????????mymall_goods_attribute
        for (MymallGoodsAttribute attribute : attributes) {
            attribute.setGoodsId(goods.getId());
            attributeService.add(attribute);
        }

        // ???????????????mymall_product
        for (MymallGoodsProduct product : products) {
            product.setGoodsId(goods.getId());
            productService.add(product);
        }
        return ResponseUtil.ok();
    }

    public Object list2() {
        // http://element-cn.eleme.io/#/zh-CN/component/cascader
        // ?????????????????????????????????
        List<MymallCategory> l1CatList = categoryService.queryL1();
        List<CatVo> categoryList = new ArrayList<>(l1CatList.size());

        for (MymallCategory l1 : l1CatList) {
            CatVo l1CatVo = new CatVo();
            l1CatVo.setValue(l1.getId());
            l1CatVo.setLabel(l1.getName());

            List<MymallCategory> l2CatList = categoryService.queryByPid(l1.getId());
            List<CatVo> children = new ArrayList<>(l2CatList.size());
            for (MymallCategory l2 : l2CatList) {
                CatVo l2CatVo = new CatVo();
                l2CatVo.setValue(l2.getId());
                l2CatVo.setLabel(l2.getName());
                children.add(l2CatVo);
            }
            l1CatVo.setChildren(children);

            categoryList.add(l1CatVo);
        }

        // http://element-cn.eleme.io/#/zh-CN/component/select
        // ????????????????????????????????????
        List<MymallBrand> list = brandService.all();
        List<Map<String, Object>> brandList = new ArrayList<>(l1CatList.size());
        for (MymallBrand brand : list) {
            Map<String, Object> b = new HashMap<>(2);
            b.put("value", brand.getId());
            b.put("label", brand.getName());
            brandList.add(b);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("categoryList", categoryList);
        data.put("brandList", brandList);
        return ResponseUtil.ok(data);
    }

    public Object detail(Integer id) {
        MymallGoods goods = goodsService.findById(id);
        List<MymallGoodsProduct> products = productService.queryByGid(id);
        List<MymallGoodsSpecification> specifications = specificationService.queryByGid(id);
        List<MymallGoodsAttribute> attributes = attributeService.queryByGid(id);

        Integer categoryId = goods.getCategoryId();
        MymallCategory category = categoryService.findById(categoryId);
        Integer[] categoryIds = new Integer[]{};
        if (category != null) {
            Integer parentCategoryId = category.getPid();
            categoryIds = new Integer[]{parentCategoryId, categoryId};
        }

        Map<String, Object> data = new HashMap<>();
        data.put("goods", goods);
        data.put("specifications", specifications);
        data.put("products", products);
        data.put("attributes", attributes);
        data.put("categoryIds", categoryIds);

        return ResponseUtil.ok(data);
    }

}
