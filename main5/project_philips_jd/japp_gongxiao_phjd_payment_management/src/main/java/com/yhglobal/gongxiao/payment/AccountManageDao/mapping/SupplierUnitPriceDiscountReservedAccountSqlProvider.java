package com.yhglobal.gongxiao.payment.AccountManageDao.mapping;

import com.yhglobal.gongxiao.payment.model.SupplierUnitPriceDiscountReservedAccount;

import static org.apache.ibatis.jdbc.SqlBuilder.BEGIN;
import static org.apache.ibatis.jdbc.SqlBuilder.INSERT_INTO;
import static org.apache.ibatis.jdbc.SqlBuilder.SET;
import static org.apache.ibatis.jdbc.SqlBuilder.SQL;
import static org.apache.ibatis.jdbc.SqlBuilder.UPDATE;
import static org.apache.ibatis.jdbc.SqlBuilder.VALUES;
import static org.apache.ibatis.jdbc.SqlBuilder.WHERE;


public class SupplierUnitPriceDiscountReservedAccountSqlProvider {

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table supplier_unit_price_discount_reserved_account
     *
     * @mbggenerated
     */
    public String insertSelective(SupplierUnitPriceDiscountReservedAccount record) {
        BEGIN();
        INSERT_INTO("supplier_unit_price_discount_reserved_account");
        
        if (record.getSupplierId() != null) {
            VALUES("supplierId", "#{supplierId,jdbcType=BIGINT}");
        }
        
        if (record.getSupplierName() != null) {
            VALUES("supplierName", "#{supplierName,jdbcType=VARCHAR}");
        }
        
        if (record.getSupplierCode() != null) {
            VALUES("supplierCode", "#{supplierCode,jdbcType=VARCHAR}");
        }
        
        if (record.getProjectId() != null) {
            VALUES("projectId", "#{projectId,jdbcType=BIGINT}");
        }
        
        if (record.getProjectName() != null) {
            VALUES("projectName", "#{projectName,jdbcType=VARCHAR}");
        }
        
        if (record.getStatus() != null) {
            VALUES("status", "#{status,jdbcType=TINYINT}");
        }
        
        if (record.getCurrencyCode() != null) {
            VALUES("currencyCode", "#{currencyCode,jdbcType=VARCHAR}");
        }
        
        if (record.getAccountAmount() != null) {
            VALUES("accountAmount", "#{accountAmount,jdbcType=DECIMAL}");
        }
        
        if (record.getDataVersion() != null) {
            VALUES("dataVersion", "#{dataVersion,jdbcType=BIGINT}");
        }
        
        if (record.getCreateTime() != null) {
            VALUES("createTime", "#{createTime,jdbcType=TIMESTAMP}");
        }
        
        if (record.getLastUpdateTime() != null) {
            VALUES("lastUpdateTime", "#{lastUpdateTime,jdbcType=TIMESTAMP}");
        }
        
        if (record.getTracelog() != null) {
            VALUES("tracelog", "#{tracelog,jdbcType=LONGVARCHAR}");
        }
        
        return SQL();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table supplier_unit_price_discount_reserved_account
     *
     * @mbggenerated
     */
    public String updateByPrimaryKeySelective(SupplierUnitPriceDiscountReservedAccount record) {
        BEGIN();
        UPDATE("supplier_unit_price_discount_reserved_account");
        
        if (record.getSupplierName() != null) {
            SET("supplierName = #{supplierName,jdbcType=VARCHAR}");
        }
        
        if (record.getSupplierCode() != null) {
            SET("supplierCode = #{supplierCode,jdbcType=VARCHAR}");
        }
        
        if (record.getProjectId() != null) {
            SET("projectId = #{projectId,jdbcType=BIGINT}");
        }
        
        if (record.getProjectName() != null) {
            SET("projectName = #{projectName,jdbcType=VARCHAR}");
        }
        
        if (record.getStatus() != null) {
            SET("status = #{status,jdbcType=TINYINT}");
        }
        
        if (record.getCurrencyCode() != null) {
            SET("currencyCode = #{currencyCode,jdbcType=VARCHAR}");
        }
        
        if (record.getAccountAmount() != null) {
            SET("accountAmount = #{accountAmount,jdbcType=DECIMAL}");
        }
        
        if (record.getDataVersion() != null) {
            SET("dataVersion = #{dataVersion,jdbcType=BIGINT}");
        }
        
        if (record.getCreateTime() != null) {
            SET("createTime = #{createTime,jdbcType=TIMESTAMP}");
        }
        
        if (record.getLastUpdateTime() != null) {
            SET("lastUpdateTime = #{lastUpdateTime,jdbcType=TIMESTAMP}");
        }
        
        if (record.getTracelog() != null) {
            SET("tracelog = #{tracelog,jdbcType=LONGVARCHAR}");
        }
        
        WHERE("supplierId = #{supplierId,jdbcType=BIGINT}");
        
        return SQL();
    }
}