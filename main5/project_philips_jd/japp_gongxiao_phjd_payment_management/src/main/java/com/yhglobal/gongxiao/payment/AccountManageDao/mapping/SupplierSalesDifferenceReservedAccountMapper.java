package com.yhglobal.gongxiao.payment.AccountManageDao.mapping;

import com.yhglobal.gongxiao.common.mapper.BaseMapper;
import com.yhglobal.gongxiao.payment.model.SupplierSalesDifferenceReservedAccount;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.math.BigDecimal;
import java.util.Date;

public interface SupplierSalesDifferenceReservedAccountMapper extends BaseMapper{
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table supplier_sales_difference_reserved_account
     *
     * @mbggenerated
     */
    @Delete({
        "delete from supplier_sales_difference_reserved_account",
        "where supplierId = #{supplierId,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long supplierId);

    @Select({
            "select",
            "supplierId, supplierName, supplierCode, projectId, projectName, status, currencyCode, ",
            "accountAmount, dataVersion, createTime, lastUpdateTime, tracelog",
            "from ${tablePrefix}_supplier_sales_difference_reserved_account",
            "where projectId = #{projectId,jdbcType=BIGINT}"
    })
    SupplierSalesDifferenceReservedAccount selectByProjectId(@Param("projectId") Long projectId,@Param("tablePrefix")  String tablePrefix);

    @Update({
            "update ${tablePrefix}_supplier_sales_difference_reserved_account",
            "set accountAmount = #{frozenAmount,jdbcType=DECIMAL},",
            "dataVersion = #{dataVersion,jdbcType=BIGINT} + 1,",
            "lastUpdateTime = #{lastUpdateTime,jdbcType=TIMESTAMP},",
            "tracelog = #{tracelog,jdbcType=LONGVARCHAR}",
            "where supplierId = #{supplierId,jdbcType=BIGINT} and dataVersion = #{dataVersion,jdbcType=BIGINT}"
    })
    int updateAccountAmount(@Param("supplierId") Long supplierId,@Param("frozenAmount") BigDecimal frozenAmount, @Param("dataVersion") Long dataVersion,
                            @Param("lastUpdateTime")Date lastUpdateTime, @Param("tracelog")String tracelog, @Param("tablePrefix") String tablePrefix);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table supplier_sales_difference_reserved_account
     *
     * @mbggenerated
     */
    @Insert({
        "insert into supplier_sales_difference_reserved_account (supplierId, supplierName, ",
        "supplierCode, projectId, ",
        "projectName, status, ",
        "currencyCode, accountAmount, ",
        "dataVersion, createTime, ",
        "lastUpdateTime, tracelog)",
        "values (#{supplierId,jdbcType=BIGINT}, #{supplierName,jdbcType=VARCHAR}, ",
        "#{supplierCode,jdbcType=VARCHAR}, #{projectId,jdbcType=BIGINT}, ",
        "#{projectName,jdbcType=VARCHAR}, #{status,jdbcType=TINYINT}, ",
        "#{currencyCode,jdbcType=VARCHAR}, #{accountAmount,jdbcType=DECIMAL}, ",
        "#{dataVersion,jdbcType=BIGINT}, #{createTime,jdbcType=TIMESTAMP}, ",
        "#{lastUpdateTime,jdbcType=TIMESTAMP}, #{tracelog,jdbcType=LONGVARCHAR})"
    })
    int insert(SupplierSalesDifferenceReservedAccount record);



    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table supplier_sales_difference_reserved_account
     *
     * @mbggenerated
     */
    @Select({
        "select",
        "supplierId, supplierName, supplierCode, projectId, projectName, status, currencyCode, ",
        "accountAmount, dataVersion, createTime, lastUpdateTime, tracelog",
        "from supplier_sales_difference_reserved_account",
        "where supplierId = #{supplierId,jdbcType=BIGINT}"
    })
    @Results({
        @Result(column="supplierId", property="supplierId", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="supplierName", property="supplierName", jdbcType=JdbcType.VARCHAR),
        @Result(column="supplierCode", property="supplierCode", jdbcType=JdbcType.VARCHAR),
        @Result(column="projectId", property="projectId", jdbcType=JdbcType.BIGINT),
        @Result(column="projectName", property="projectName", jdbcType=JdbcType.VARCHAR),
        @Result(column="status", property="status", jdbcType=JdbcType.TINYINT),
        @Result(column="currencyCode", property="currencyCode", jdbcType=JdbcType.VARCHAR),
        @Result(column="accountAmount", property="accountAmount", jdbcType=JdbcType.DECIMAL),
        @Result(column="dataVersion", property="dataVersion", jdbcType=JdbcType.BIGINT),
        @Result(column="createTime", property="createTime", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="lastUpdateTime", property="lastUpdateTime", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="tracelog", property="tracelog", jdbcType=JdbcType.LONGVARCHAR)
    })
    SupplierSalesDifferenceReservedAccount selectByPrimaryKey(Long supplierId);



    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table supplier_sales_difference_reserved_account
     *
     * @mbggenerated
     */
    @Update({
        "update supplier_sales_difference_reserved_account",
        "set supplierName = #{supplierName,jdbcType=VARCHAR},",
          "supplierCode = #{supplierCode,jdbcType=VARCHAR},",
          "projectId = #{projectId,jdbcType=BIGINT},",
          "projectName = #{projectName,jdbcType=VARCHAR},",
          "status = #{status,jdbcType=TINYINT},",
          "currencyCode = #{currencyCode,jdbcType=VARCHAR},",
          "accountAmount = #{accountAmount,jdbcType=DECIMAL},",
          "dataVersion = #{dataVersion,jdbcType=BIGINT},",
          "createTime = #{createTime,jdbcType=TIMESTAMP},",
          "lastUpdateTime = #{lastUpdateTime,jdbcType=TIMESTAMP},",
          "tracelog = #{tracelog,jdbcType=LONGVARCHAR}",
        "where supplierId = #{supplierId,jdbcType=BIGINT}"
    })
    int updateByPrimaryKeyWithBLOBs(SupplierSalesDifferenceReservedAccount record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table supplier_sales_difference_reserved_account
     *
     * @mbggenerated
     */
    @Update({
        "update supplier_sales_difference_reserved_account",
        "set supplierName = #{supplierName,jdbcType=VARCHAR},",
          "supplierCode = #{supplierCode,jdbcType=VARCHAR},",
          "projectId = #{projectId,jdbcType=BIGINT},",
          "projectName = #{projectName,jdbcType=VARCHAR},",
          "status = #{status,jdbcType=TINYINT},",
          "currencyCode = #{currencyCode,jdbcType=VARCHAR},",
          "accountAmount = #{accountAmount,jdbcType=DECIMAL},",
          "dataVersion = #{dataVersion,jdbcType=BIGINT},",
          "createTime = #{createTime,jdbcType=TIMESTAMP},",
          "lastUpdateTime = #{lastUpdateTime,jdbcType=TIMESTAMP}",
        "where supplierId = #{supplierId,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(SupplierSalesDifferenceReservedAccount record);
}