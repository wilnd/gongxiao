package com.yhglobal.gongxiao.warehouse.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.yhglobal.gongxiao.common.RpcHeader;
import com.yhglobal.gongxiao.common.TraceLog;
import com.yhglobal.gongxiao.common.spring.ApplicationContextProvider;
import com.yhglobal.gongxiao.foundation.product.basic.service.ProductService;
import com.yhglobal.gongxiao.foundation.project.service.ProjectService;
import com.yhglobal.gongxiao.foundation.warehouse.dao.WarehouseDao;
import com.yhglobal.gongxiao.foundation.warehouse.service.WarehouseService;
import com.yhglobal.gongxiao.id.BizNumberType;
import com.yhglobal.gongxiao.id.DateTimeIdGenerator;
import com.yhglobal.gongxiao.util.CommonUtil;
import com.yhglobal.gongxiao.util.TraceLogUtil;
import com.yhglobal.gongxiao.warehouse.bootstrap.WarehouseConfig;
import com.yhglobal.gongxiao.warehouse.customer.service.InstockService;
import com.yhglobal.gongxiao.warehouse.service.ManualInboundService;
import com.yhglobal.gongxiao.warehouse.task.NotificationWmsInboundTask;
import com.yhglobal.gongxiao.warehouse.type.WmsSourceChannel;
import com.yhglobal.gongxiao.warehousemanagement.bo.CreateNewInStockrder;
import com.yhglobal.gongxiao.warehousemanagement.dao.InBoundOrderDao;
import com.yhglobal.gongxiao.warehousemanagement.dao.InboundOrderItemDao;
import com.yhglobal.gongxiao.warehousemanagement.dao.ManualInboudDao;
import com.yhglobal.gongxiao.warehousemanagement.dao.ManualInboundItemDao;
import com.yhglobal.gongxiao.warehousemanagement.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ManualInboundServiceImpl implements ManualInboundService {

    private static Logger logger = LoggerFactory.getLogger(ManualInboundServiceImpl.class);

    @Autowired
    ManualInboudDao manualInboudDao;
    @Autowired
    ManualInboundItemDao manualInboundItemDao;
    @Autowired
    InBoundOrderDao inBoundOrderDao;
    @Autowired
    InboundOrderItemDao inboundOrderItemDao;
    @Autowired
    WarehouseDao warehouseDao;
    @Autowired
    ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Reference
    ProductService productService;
    @Reference
    WarehouseService warehouseService;
    @Reference
    ProjectService projectService;
    @Autowired
    WarehouseConfig warehouseConfig;

    @Autowired
    InstockService instockService;

    @Override
    public List<ManualInboundOrder> getManualInboundList(RpcHeader rpcHeader) {
        logger.info("#traceId={}# [IN][getManualInboundList] params: projectId={},inventoryNum={}", rpcHeader.getTraceId());
        List<ManualInboundOrder> resultList = manualInboudDao.selectAllRecord();
        logger.info("#traceId={}# [OUT] get getManualInboundList success: resultList.size:{}", rpcHeader.getTraceId(), resultList.size());
        return resultList;
    }

    @Override
    public int createManualInbound(RpcHeader rpcHeader, ManualInboundOrder manualInboundOrder, List<CreateNewInStockrder> inStockrderList) {
        logger.info("#traceId={}# [IN][createManualInbound] params: manualInboundOrder={},inStockrderList={}", rpcHeader.traceId, JSON.toJSONString(manualInboundOrder), JSON.toJSONString(inStockrderList));
        try {
            String orderNo = DateTimeIdGenerator.nextId(BizNumberType.OTHER_IN_NO);
            String batchNo = DateTimeIdGenerator.nextId(BizNumberType.STOCK_BATCH_NO);
            String gongxiaoInboundOrderNo = DateTimeIdGenerator.nextId(BizNumberType.STOCK_OTHER_IN_NO);
            manualInboundOrder.setOrderNo(orderNo);
            manualInboundOrder.setGongxiaoInboundOrderNo(gongxiaoInboundOrderNo);
            manualInboundOrder.setBatchNo(batchNo);
            int i = manualInboudDao.insertManualInboundInfo(manualInboundOrder);
            List<String> conectedGood = new ArrayList<>();
            List<String> conectedproduct = new ArrayList<>();
            int totalQuantity = 0;
            if (i > 0) {
                List<ManualInboundOrderIterm> manualInboundOrderItermList = new ArrayList<>();
                for (CreateNewInStockrder record : inStockrderList) {
                    ManualInboundOrderIterm manualInboundOrderIterm = new ManualInboundOrderIterm();
                    manualInboundOrderIterm.setOrderNo(orderNo);
                    manualInboundOrderIterm.setGongxiaoInboundOrderNo(gongxiaoInboundOrderNo);
                    manualInboundOrderIterm.setProjectId(manualInboundOrder.getProjectId());
                    manualInboundOrderIterm.setBatchNo(batchNo);
                    manualInboundOrderIterm.setProductId(record.getProductID());
                    manualInboundOrderIterm.setProductCode(record.getProductCode());
                    manualInboundOrderIterm.setProductName(record.getProductName());
                    manualInboundOrderIterm.setProductUnit("个");
                    manualInboundOrderIterm.setWarehouseId(manualInboundOrder.getWarehouseId());
                    manualInboundOrderIterm.setWarehouseName(manualInboundOrder.getWarehouseName());
                    manualInboundOrderIterm.setInboundType(manualInboundOrder.getInboundType());
                    manualInboundOrderIterm.setQuantity(record.getQuantity());
                    manualInboundOrderIterm.setGuidePrice(record.getGuidePrice());
                    manualInboundOrderIterm.setPurchasePrice(record.getPurchasePrice());
                    manualInboundOrderIterm.setCostPrice(record.getCostPrice());
                    manualInboundOrderIterm.setNote(manualInboundOrder.getNote());
                    manualInboundOrderIterm.setStatus(0);
                    manualInboundOrderIterm.setCreateTime(manualInboundOrder.getCreateTime());
                    manualInboundOrderItermList.add(manualInboundOrderIterm);
                    conectedGood.add(manualInboundOrderIterm.getProductCode());
                    conectedproduct.add(manualInboundOrderIterm.getProductCode());
                    totalQuantity += manualInboundOrderIterm.getQuantity();

                }
                manualInboundItemDao.insertOrderList(manualInboundOrderItermList);
            }

            //1、仓储模块记录入库单
            InboundOrder inboundOrder = new InboundOrder();
//            inboundOrder.setUniqueNo();
            inboundOrder.setProjectId(Long.valueOf(manualInboundOrder.getProjectId()));
            inboundOrder.setBatchNo(batchNo);
            inboundOrder.setConnectedGood(JSON.toJSONString(conectedGood));
            inboundOrder.setConnectedProduct(JSON.toJSONString(conectedproduct));
//            inboundOrder.setContactsPeople();
//            inboundOrder.setCreatedById(Long.valueOf(rpcHeader.getTraceId()));
            inboundOrder.setCreatedByName(rpcHeader.getUsername());
            inboundOrder.setCreateTime(new Date());
            inboundOrder.setDataVersion(0);
            inboundOrder.setDeliverAddress(manualInboundOrder.getRecieveAddress());
            inboundOrder.setGongxiaoInboundOrderNo(gongxiaoInboundOrderNo);
            inboundOrder.setImperfectQuantity(0);
            inboundOrder.setInTransitQuantity(0);
            inboundOrder.setInboundType(manualInboundOrder.getInboundType());
            inboundOrder.setInStockQuantity(manualInboundOrder.getQuantity());
            inboundOrder.setNote(manualInboundOrder.getNote());
            inboundOrder.setOrderStatus(0);
//            inboundOrder.setPhoneNum();
            inboundOrder.setPurchaseOrderNo(manualInboundOrder.getOrderNo());
//            inboundOrder.setShippingMode();
//            inboundOrder.setCustCode();
            inboundOrder.setSourceChannel(Integer.parseInt(WmsSourceChannel.CHANNEL_YHGLOBAL.getChannelId()));
            inboundOrder.setSupplierName(manualInboundOrder.getSupplierName());
            inboundOrder.setTotalQuantity(totalQuantity);
            TraceLog traceLog = new TraceLog();                                               //操作日志
            Date date = new Date();
            traceLog.setOpTime(date.getTime());
            traceLog.setContent("创建入库单");
            traceLog.setOpName(rpcHeader.getUsername());
            traceLog.setOpUid(String.valueOf(rpcHeader.getTraceId()));
            inboundOrder.setTracelog(TraceLogUtil.appendTraceLog(inboundOrder.getTracelog(), traceLog));
            inboundOrder.setWarehouseId(manualInboundOrder.getWarehouseId());
            inboundOrder.setWarehouseName(manualInboundOrder.getWarehouseName());

            List<InboundOrderItem> inboundOrderItemList = new ArrayList<>();
            for (CreateNewInStockrder createNewInStockrder : inStockrderList) {
                InboundOrderItem inboundOrderItem = new InboundOrderItem();
                inboundOrderItem.setProjectId(manualInboundOrder.getProjectId());
                inboundOrderItem.setBatchNo(batchNo);
                inboundOrderItem.setInboundOrderItemNo(String.valueOf(CommonUtil.getOderNumber()));
                inboundOrderItem.setCreateTime(manualInboundOrder.getCreateTime());
                inboundOrderItem.setDataVersion(Long.valueOf(0));
                inboundOrderItem.setGongxiaoInboundOrderNo(gongxiaoInboundOrderNo);
                inboundOrderItem.setImperfectQuantity(0);
                inboundOrderItem.setInStockQuantity(createNewInStockrder.getQuantity());
                inboundOrderItem.setInventoryType(manualInboundOrder.getInboundType());
                inboundOrderItem.setItemStatus(false);
                inboundOrderItem.setProductCode(createNewInStockrder.getProductCode());
                inboundOrderItem.setProductName(createNewInStockrder.getProductName());
                inboundOrderItem.setProductUnit("个");
                inboundOrderItem.setProductId(createNewInStockrder.getProductID());
                inboundOrderItem.setPurchaseOrderNo(orderNo);
                inboundOrderItem.setTotalQuantity(createNewInStockrder.getQuantity());
                inboundOrderItem.setWarehouseId(manualInboundOrder.getWarehouseId());
                inboundOrderItem.setWarehouseName(manualInboundOrder.getWarehouseName());
                inboundOrderItemList.add(inboundOrderItem);
            }

            inBoundOrderDao.insertInStorageInfo(inboundOrder);
            inboundOrderItemDao.inserInboundOrderItemInfo(inboundOrderItemList);

            //1、通知WMS入库
            NotificationWmsInboundTask task = new NotificationWmsInboundTask(rpcHeader,ApplicationContextProvider.getApplicationContext(), orderNo, inboundOrder, inboundOrderItemList,inBoundOrderDao,warehouseService,productService,projectService,warehouseConfig);
            threadPoolTaskExecutor.submit(task);
            instockService.createInstockRecor(inboundOrder.getGongxiaoInboundOrderNo(), inboundOrder, inboundOrderItemList);

            logger.info("#traceId={}# [OUT] createManualInbound success: ", rpcHeader.traceId);
            return i;
        } catch (Exception e) {
            logger.error("#traceId=" + rpcHeader.traceId + "# errorMessage:" + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public int updateManualInboundOrder(RpcHeader rpcHeader, ManualInboundOrder manualInboundOrder) {

        try {
            logger.info("#traceId={}# [IN][updateManualInboundOrder] params: ", rpcHeader.traceId);
            int i = manualInboudDao.updateManualInboundInfo(manualInboundOrder);
            logger.info("#traceId={}# [OUT] updateManualInboundOrder success: ", rpcHeader.traceId);
            return i;
        } catch (Exception e) {
            logger.error("#traceId=" + rpcHeader.traceId + "# errorMessage:" + e.getMessage(), e);
            throw e;
        }
    }
}
