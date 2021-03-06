package com.yhglobal.gongxiao.inventory.inventorysync;

import com.alibaba.fastjson.JSON;
import com.yhglobal.gongxiao.constant.ErrorCode;
import com.yhglobal.gongxiao.foundation.product.microservice.ProductServiceGrpc;
import com.yhglobal.gongxiao.foundation.product.microservice.ProductStructure;
import com.yhglobal.gongxiao.foundation.warehouse.microservice.WarehouseServiceGrpc;
import com.yhglobal.gongxiao.foundation.warehouse.microservice.WarehouseStructure;
import com.yhglobal.gongxiao.grpc.client.GlobalRpcStubStore;
import com.yhglobal.gongxiao.inventory.account.microservice.ProductInventoryStructure;
import com.yhglobal.gongxiao.inventory.dao.ProductInventoryDao;
import com.yhglobal.gongxiao.inventory.dao.ProductInventoryFlowDao;
import com.yhglobal.gongxiao.inventory.inventorysync.microservice.InventorysyncServiceGrpc;
import com.yhglobal.gongxiao.inventory.inventorysync.microservice.InventorysyncStructure;
import com.yhglobal.gongxiao.inventory.model.ProductInventory;
import com.yhglobal.gongxiao.inventory.model.ProductInventoryFlow;
import com.yhglobal.gongxiao.inventory.util.TablePrefixUtil;
import com.yhglobal.gongxiao.microservice.GongxiaoRpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
public class InventorysyncServiceImpl extends InventorysyncServiceGrpc.InventorysyncServiceImplBase {
    private static Logger logger = LoggerFactory.getLogger(InventorysyncServiceImpl.class);

    @Autowired
    ProductInventoryFlowDao productInventoryFlowDao;

    @Autowired
    ProductInventoryDao productInventoryDao;

    @Override
    public void updateInventoryInfo(InventorysyncStructure.UpdateInventoryRequest request, StreamObserver<InventorysyncStructure.UpdateInventoryResponse> responseObserver) {
        InventorysyncStructure.UpdateInventoryResponse response;
        InventorysyncStructure.UpdateInventoryResponse.Builder respBuilder = InventorysyncStructure.UpdateInventoryResponse.newBuilder();
        GongxiaoRpc.RpcHeader rpcHeader = request.getRpcHeader();
        int id = request.getId();
        String projectId = request.getProjectId();
        int onHandQuantity = request.getOnHandQuantity();
        logger.info("#traceId={}# [IN][updateInventoryInfo]: id={},onHandQuantity={}", rpcHeader.getTraceId(), id, onHandQuantity);
        try {
            String projectPrefix = TablePrefixUtil.getTablePrefixByProjectId(Long.parseLong(projectId));
            productInventoryDao.updateAllocateInventory(id,onHandQuantity,projectPrefix);
            response = respBuilder.setErrorCode(ErrorCode.SUCCESS.getErrorCode()).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            logger.info("#traceId={}# [OUT][updateInventoryInfo]: get updateInventoryInfo success", rpcHeader.getTraceId());
        }catch (Exception e){
            logger.error("#traceId=" + rpcHeader.getTraceId() + "# errorMessage:" + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void resumeOnsalesQuantiry(InventorysyncStructure.ResumeOnsalesQuantiryRequest request, StreamObserver<InventorysyncStructure.ResumeOnsalesQuantiryResponse> responseObserver) {
        InventorysyncStructure.ResumeOnsalesQuantiryResponse response;
        InventorysyncStructure.ResumeOnsalesQuantiryResponse.Builder respBuilder = InventorysyncStructure.ResumeOnsalesQuantiryResponse.newBuilder();
        int totalQuantity=request.getTotalQuantity();
        int purchaseType=request.getPurchaseType();
        int inventoryType=request.getInventoryType();
        long projectId=request.getProjectId();
        String batchNo=request.getBatchNo();
        String productCode=request.getProductCode();
        int warehouseId=request.getWarehouseId();
        GongxiaoRpc.RpcHeader rpcHeader = request.getRpcHeader();
        logger.info("#traceId={}# [IN][resumeOnsalesQuantiry]: totalQuantity={},purchaseType={},inventoryType={},projectId={},batchNo={},productCode={},warehouseId={}", totalQuantity, purchaseType, inventoryType, projectId, batchNo,productCode,warehouseId);
        try {
            String projectPrefix = TablePrefixUtil.getTablePrefixByProjectId(projectId);
            productInventoryDao.resumeOnsalesQuantiry(totalQuantity,purchaseType,inventoryType,projectId,batchNo,productCode,String.valueOf(warehouseId),projectPrefix);
            response = respBuilder.setErrorCode(ErrorCode.SUCCESS.getErrorCode()).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            logger.info("#traceId={}# [OUT][resumeOnsalesQuantiry]: get updateInventoryInfo success", rpcHeader.getTraceId());
        }catch (Exception e){
            logger.error("#traceId=" + rpcHeader.getTraceId() + "# errorMessage:" + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void syncInboundInventory(InventorysyncStructure.SyncInboundRequest request, StreamObserver<InventorysyncStructure.SyncResponse> responseObserver) {
        InventorysyncStructure.SyncResponse response;
        InventorysyncStructure.SyncResponse.Builder respBuilder = InventorysyncStructure.SyncResponse.newBuilder();
        GongxiaoRpc.RpcHeader rpcHeader = request.getRpcHeader();
        List<ProductInventoryStructure.ProductInventory> productInventoryListList = request.getProductInventoryListList();
        String purchaseOrderNo = request.getPurchaseOrderNo();
        String projectId = request.getProjectId();
        String gonxiaoInboundOrderNo = request.getGonxiaoInboundOrderNo();
        int orderType = request.getOrderType();
        logger.info("#traceId={}# [IN][syncInboundInventory]: productInventoryList={},purchaseOrderNo={},purchaseOrderNo={},gonxiaoInboundOrderNo={},orderType={}", rpcHeader.getTraceId(), productInventoryListList, purchaseOrderNo, gonxiaoInboundOrderNo, orderType);
        try {
            String projectPrefix = TablePrefixUtil.getTablePrefixByProjectId(Long.parseLong(projectId));
            for (ProductInventoryStructure.ProductInventory record : productInventoryListList) {
                String batchNo = record.getBatchNo();
                String productCode = record.getProductCode();
                WarehouseServiceGrpc.WarehouseServiceBlockingStub warehouseService = GlobalRpcStubStore.getRpcStub(WarehouseServiceGrpc.WarehouseServiceBlockingStub.class);
                WarehouseStructure.GetWarehouseByIdReq getWarehouseByIdReq = WarehouseStructure.GetWarehouseByIdReq.newBuilder().setRpcHeader(request.getRpcHeader()).setWarehouseId(record.getWarehouseId()).build();
                WarehouseStructure.GetWarehouseByIdResp rpcResponse = warehouseService.getWarehouseById(getWarehouseByIdReq);
                WarehouseStructure.Warehouse warehouse = rpcResponse.getWarehouse();

                ProductServiceGrpc.ProductServiceBlockingStub productService = GlobalRpcStubStore.getRpcStub(ProductServiceGrpc.ProductServiceBlockingStub.class);
                ProductStructure.GetProductDetailByModelReq getProductDetailByModelReq = ProductStructure.GetProductDetailByModelReq.newBuilder().setRpcHeader(request.getRpcHeader()).setProjectId(Long.parseLong(projectId)).setProductModel(record.getProductCode()).build();
                ProductStructure.GetProductDetailByModelResp productRpcResponse = productService.getProductDetailByModel(getProductDetailByModelReq);
                ProductStructure.ProductBasicDetail productBasic = productRpcResponse.getProductBasicDetail();
                ProductInventory productInventory = productInventoryDao.selectRecordByProductNameAndProductCodeAndwarehouse(record.getPurchaseType(), record.getInventoryStatus(), record.getProjectId(), batchNo, productCode, record.getWarehouseId(),projectPrefix);
                if (productInventory == null) { //若《库存表》不存在该商品 则先创建该商品库存记录

                    /**
                     * 理论上以下参数将唯一确定t_product_inventory中的一行记录
                     *   purchaseType 采购类型（普通采购/赠品）
                     *   inventoryStatus 库存品质
                     *   projectId 项目Id
                     *   productCode 商品编码
                     *   batchNo 批次
                     *   warehouseId 仓库id
                     *
                     * 可能会有多个请求同时过来 需防止同时创建两条记录
                     */
                    synchronized (InventorysyncServiceImpl.class) {
                        productInventory = productInventoryDao.selectRecordByProductNameAndProductCodeAndwarehouse(record.getPurchaseType(), record.getInventoryStatus(), record.getProjectId(), record.getBatchNo(), record.getProductCode(), record.getWarehouseId(),projectPrefix);
                        if (productInventory == null) {
                            productInventory = new ProductInventory();
                            productInventory.setBatchNo(record.getBatchNo());
                            productInventory.setInboundOrderNo(record.getInboundOrderNo());
                            productInventory.setPurchaseOrderNo(record.getPurchaseOrderNo());
                            productInventory.setPurchaseType(record.getPurchaseType());
                            productInventory.setInventoryStatus(record.getInventoryStatus()); //库存状态
                            productInventory.setProjectId(record.getProjectId());
                            productInventory.setProductId(record.getProductId());
                            productInventory.setProductCode(record.getProductCode());
                            productInventory.setProductModel(productBasic.getProductModel());
                            productInventory.setProductName(productBasic.getProductName());
                            productInventory.setMaterial(productBasic.getEasCode());
                            productInventory.setPurchaseGuidPrice(productBasic.getPurchaseGuidePrice());
                            productInventory.setPurchasePrice(record.getPurchasePrice());
                            productInventory.setCostPrice(record.getCostPrice());
                            productInventory.setWarehouseId(record.getWarehouseId());
                            productInventory.setWarehouseCode(warehouse.getWarehouseCode());
                            productInventory.setWarehouseName(warehouse.getWarehouseName());
                            productInventory.setInboundQuantity(record.getOnHandQuantity());
                            productInventory.setOnHandQuantity(0); //库存数量初始化为0,这里只是新建一条库存记录
                            productInventory.setOnSalesOrderQuantity(0); //已下单未出库
                            productInventory.setCreateTime(new Date()); //创建时间
                            productInventoryDao.insert(productInventory,projectPrefix); //注意：这里插入后需要把id字段注入进来 否则下面根据inventoryId获取后会NPE
                        }
                    }
                }

                //写入库流水到product_inventory_flow
                ProductInventoryFlow flow = new ProductInventoryFlow();
                flow.setOrderNo(gonxiaoInboundOrderNo);
                flow.setBatchNo(record.getBatchNo());
                flow.setInventoryFlowType(record.getInventoryStatus());
                flow.setOrderType(orderType);
                flow.setProjectId(record.getProjectId());
                flow.setProductCode(record.getProductCode());
                flow.setProductModel(productBasic.getProductModel());
                flow.setProductName(productBasic.getProductName());
                flow.setWarehouseId(record.getWarehouseId());
                flow.setWarehouseCode(warehouse.getWarehouseCode());
                flow.setWarehouseName(warehouse.getWarehouseName());
                int amountBeforeTransaction = productInventory.getOnHandQuantity() + productInventory.getOnSalesOrderQuantity(); //发生前数量=原有可用数量+销售占用
                flow.setAmountBeforeTransaction(amountBeforeTransaction);
                flow.setTransactionAmount(record.getOnHandQuantity()); //变动数量
                int amountAfterTransaction = record.getOnHandQuantity() + productInventory.getOnHandQuantity() + productInventory.getOnSalesOrderQuantity(); //发生后数量=变动数量+原有可用数量+销售占用
                flow.setAmountAfterTransaction(amountAfterTransaction);
                flow.setTransactionTime(new Date()); //发生变动时间
                flow.setRelatedOrderId(purchaseOrderNo); //设定关联的订单(采购单)
                flow.setCreateTime(new Date());

                int num = productInventoryFlowDao.insert(flow,projectPrefix); //出入库流水记录入库
                if (num != 1) { //流水写入DB失败
                    logger.error("#traceId={}# [OUT] fail to create inbound inventory flow: {}", rpcHeader.getTraceId(), JSON.toJSONString(flow));
                }

                //则更新入库库存汇总信息
                long inventoryId = productInventory.getId();

                int maxTryTimes = 12; //当前最大尝试的次数
                boolean updateSuccess = false; //标记是否更新DB成功
                while (!updateSuccess && maxTryTimes > 0) { //若尚未更新db成功 并且剩余重试数大于0
                    try {
                        ProductInventory currentInventory = productInventoryDao.getProductInventoryById(inventoryId,projectPrefix);
                        if (currentInventory == null) {
                            logger.error("====>fail to get ProductInventory to update OnHandQuantity: inventoryId={}", inventoryId);
                        }
                        int targetOnHandQuantity = currentInventory.getOnHandQuantity() + record.getOnHandQuantity(); //新的库存数=原有的库存+实际入库数量
                        int row = productInventoryDao.updateOnHandQuantityById(productInventory.getId(), targetOnHandQuantity, currentInventory.getDataVersion(),projectPrefix);
                        if (row == 1) {
                            updateSuccess = true;
                            logger.info("update inventory OnHandQuantity success: inventoryId={} batchNo={}", inventoryId, record.getBatchNo());
                        }
                    } catch (Exception e) {
                        logger.error("#traceId=" + rpcHeader.getTraceId() + "# errorMessage:" + e.getMessage(), e);
                        throw e;
                    }
                    maxTryTimes--;
                }
                if (!updateSuccess && maxTryTimes <= 0) {
                    logger.info("#traceId={}# fail to update inventory OnHandQuantity: inventoryId={} batchNo={}", inventoryId, batchNo);
                }
            }
            logger.info("#traceId={}# [OUT][syncInboundInventory]: get syncInboundInventory success", rpcHeader.getTraceId());
            response = respBuilder.setErrorCode(ErrorCode.SUCCESS.getErrorCode()).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("#traceId=" + rpcHeader.getTraceId() + "# [OUT] errorMessage: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void syncOutboundInventory(InventorysyncStructure.SyncOutboundRequest request, StreamObserver<InventorysyncStructure.SyncResponse> responseObserver) {
        InventorysyncStructure.SyncResponse response;
        InventorysyncStructure.SyncResponse.Builder respBuilder = InventorysyncStructure.SyncResponse.newBuilder();
        GongxiaoRpc.RpcHeader rpcHeader = request.getRpcHeader();
        List<ProductInventoryStructure.ProductInventory> productInventoryListList = request.getProductInventoryListList();
        String salesOrderNo = request.getSalesOrderNo();
        String gonxiaoOutboundOrderNo = request.getGonxiaoOutboundOrderNo();
        int orderType = request.getOrderType();
        String projectId = request.getProjectId();

        logger.info("#traceId={}# [IN][syncOutboundInventory]: productInventoryList={}, salesOrderNo={}, gonxiaoOutboundOrderNo={}, orderType={}", rpcHeader.getTraceId(), productInventoryListList, salesOrderNo, gonxiaoOutboundOrderNo, orderType);
        try {
            String projectPrefix = TablePrefixUtil.getTablePrefixByProjectId(Long.parseLong(projectId));
            for (ProductInventoryStructure.ProductInventory record : productInventoryListList) {

                WarehouseServiceGrpc.WarehouseServiceBlockingStub warehouseService = GlobalRpcStubStore.getRpcStub(WarehouseServiceGrpc.WarehouseServiceBlockingStub.class);
                WarehouseStructure.GetWarehouseByIdReq getWarehouseByIdReq = WarehouseStructure.GetWarehouseByIdReq.newBuilder().setRpcHeader(request.getRpcHeader()).setWarehouseId(record.getWarehouseId()).build();
                WarehouseStructure.GetWarehouseByIdResp rpcResponse = warehouseService.getWarehouseById(getWarehouseByIdReq);
                WarehouseStructure.Warehouse warehouse = rpcResponse.getWarehouse();

                ProductServiceGrpc.ProductServiceBlockingStub productService = GlobalRpcStubStore.getRpcStub(ProductServiceGrpc.ProductServiceBlockingStub.class);
                ProductStructure.GetProductDetailByModelReq getProductDetailByModelReq = ProductStructure.GetProductDetailByModelReq.newBuilder().setRpcHeader(request.getRpcHeader()).setProjectId(Long.parseLong(projectId)).setProductModel(record.getProductCode()).build();
                ProductStructure.GetProductDetailByModelResp productRpcResponse = productService.getProductDetailByModel(getProductDetailByModelReq);
                ProductStructure.ProductBasicDetail productBasic = productRpcResponse.getProductBasicDetail();
//                ProductInventory productInventory = productInventoryDao.selectRecordByProductNameAndProductCodeAndwarehouse(record.getPurchaseType(), record.getInventoryStatus(), record.getProjectId(), record.getBatchNo(), record.getProductCode(), record.getWarehouseId(),projectPrefix);
//                if (productInventory == null) { //若《库存表》不存在该商品 说明有错误
//                    logger.error("#traceId={}# [OUT][syncOutboundInventory]: not have product inventory in fenxiao'DB , recor={}", rpcHeader.getTraceId(), JSON.toJSONString(record));
//                    continue;
//                }else {
                    synchronized (InventorysyncServiceImpl.class) {
                        ProductInventory productInventory = productInventoryDao.selectRecordByProductNameAndProductCodeAndwarehouse(record.getPurchaseType(), record.getInventoryStatus(), record.getProjectId(), record.getBatchNo(), record.getProductCode(), record.getWarehouseId(),projectPrefix);
                        if (productInventory != null) {
                            //写出库流水到product_inventory_flow
                            ProductInventoryFlow flow = new ProductInventoryFlow();
                            flow.setOrderNo(gonxiaoOutboundOrderNo);
                            flow.setBatchNo(record.getBatchNo());
                            flow.setInventoryFlowType(record.getInventoryStatus());
                            flow.setOrderType(orderType);
                            flow.setProjectId(record.getProjectId());
                            flow.setProductCode(record.getProductCode());
                            flow.setProductModel(productBasic.getProductModel());
                            flow.setProductName(productBasic.getProductName());
                            flow.setWarehouseId(record.getWarehouseId());
                            flow.setWarehouseCode(warehouse.getWarehouseCode());
                            flow.setWarehouseName(warehouse.getWarehouseName());
                            int amountBeforeTransaction = productInventory.getOnHandQuantity() + productInventory.getOnSalesOrderQuantity(); //发生前数量=可用数量+销售占用数量
                            flow.setAmountBeforeTransaction(amountBeforeTransaction);
                            flow.setTransactionAmount(record.getOnHandQuantity()); //变动数量
                            int amountAfterTransaction = productInventory.getOnHandQuantity() + productInventory.getOnSalesOrderQuantity() - record.getOnHandQuantity(); //发生后数量=可用数量+销售占用数量-变动数量
                            flow.setAmountAfterTransaction(amountAfterTransaction);
                            flow.setTransactionTime(new Date());
                            flow.setRelatedOrderId(salesOrderNo); //设定关联的订单(销售单)
                            flow.setCreateTime(new Date());

                            //下面日记仅为调试交易数量为零用 待去除
                            if (flow.getTransactionAmount() == 0) {
                                logger.warn("======> got zero TransactionAmount: ProductInventoryRow={}", JSON.toJSONString(record));
                            }

                            int num = productInventoryFlowDao.insert(flow,projectPrefix); //出入库流水记录入库
                            if (num == 1) { //流水写入DB成功 则更新出库库存信息
                                logger.info("开始修改库存占用数量,释放销售占用的库存,purchaseType={},inventoryStatus={},projectId={},batchNo={},productCode={},warehouseId={}", productInventory.getPurchaseType(), productInventory.getInventoryStatus(), productInventory.getProjectId(), productInventory.getBatchNo(), productInventory.getProductCode(), productInventory.getWarehouseId());
                                productInventoryDao.updateInventory(record.getOnHandQuantity(),record.getPurchaseType(),record.getInventoryStatus(),record.getProjectId(),record.getBatchNo(),record.getProductCode(),record.getWarehouseId(),projectPrefix);  //根据出入库流水更新库存信息,释放销售占用数量,
                            } else { //流水写入DB失败
                                logger.error("#traceId={}# [OUT] fail to create outbound inventory flow: {}", rpcHeader.getTraceId(), JSON.toJSONString(flow));
                            }
                        }

                        }
                    }
//                }
            logger.info("#traceId={}# [OUT][syncOutboundInventory]: get syncOutboundInventory success", rpcHeader.getTraceId());
            response = respBuilder.setErrorCode(ErrorCode.SUCCESS.getErrorCode()).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("#traceId=" + rpcHeader.getTraceId() + "# [OUT] errorMessage: " + e.getMessage(), e);
            throw e;
        }
    }

}
