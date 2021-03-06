package com.yhglobal.gongxiao.warehouseapi.eventnotification.sales.model;

import java.io.Serializable;

/**
 * eas出库单明细
 *
 * @author: 葛灿
 */
public class EasOutboundItem implements Serializable {

    /**
     * 供销平台商品编码
     */
    private String productCode;
    /**
     * 出库数量
     */
    private int quantity;
    /**
     * 批次号
     */
    private String batchNo;
    /**
     * 仓库id
     */
    private String warehouseId;
    /**
     * 是否良品
     */
    private boolean goodProduct;

    private boolean isGift;

    public boolean isGift() {
        return isGift;
    }

    public void setGift(boolean gift) {
        isGift = gift;
    }

    public boolean isGoodProduct() {
        return goodProduct;
    }

    public void setGoodProduct(boolean goodProduct) {
        this.goodProduct = goodProduct;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }
}
