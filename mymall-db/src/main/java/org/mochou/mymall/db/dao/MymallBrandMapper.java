package org.mochou.mymall.db.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.mochou.mymall.db.domain.MymallBrand;
import org.mochou.mymall.db.domain.MymallBrandExample;

public interface MymallBrandMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mymall_brand
     *
     * @mbg.generated
     */
    long countByExample(MymallBrandExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mymall_brand
     *
     * @mbg.generated
     */
    int deleteByExample(MymallBrandExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mymall_brand
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mymall_brand
     *
     * @mbg.generated
     */
    int insert(MymallBrand record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mymall_brand
     *
     * @mbg.generated
     */
    int insertSelective(MymallBrand record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mymall_brand
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    MymallBrand selectOneByExample(MymallBrandExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mymall_brand
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    MymallBrand selectOneByExampleSelective(@Param("example") MymallBrandExample example, @Param("selective") MymallBrand.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mymall_brand
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    List<MymallBrand> selectByExampleSelective(@Param("example") MymallBrandExample example, @Param("selective") MymallBrand.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mymall_brand
     *
     * @mbg.generated
     */
    List<MymallBrand> selectByExample(MymallBrandExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mymall_brand
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    MymallBrand selectByPrimaryKeySelective(@Param("id") Integer id, @Param("selective") MymallBrand.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mymall_brand
     *
     * @mbg.generated
     */
    MymallBrand selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mymall_brand
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    MymallBrand selectByPrimaryKeyWithLogicalDelete(@Param("id") Integer id, @Param("andLogicalDeleted") boolean andLogicalDeleted);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mymall_brand
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") MymallBrand record, @Param("example") MymallBrandExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mymall_brand
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") MymallBrand record, @Param("example") MymallBrandExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mymall_brand
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(MymallBrand record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mymall_brand
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(MymallBrand record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mymall_brand
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int logicalDeleteByExample(@Param("example") MymallBrandExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mymall_brand
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int logicalDeleteByPrimaryKey(Integer id);
}