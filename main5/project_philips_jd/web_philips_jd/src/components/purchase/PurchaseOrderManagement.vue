`
<template>
  <div class="purchase-order-management" id="purchase-order-management">
    <div class="purchase-order-management-form">
      <el-form :inline="true" :model="purchaseInfo" class="" labelWidth="100px">
        <el-row>
          <el-form-item label="采购订单号">
            <el-input v-model="purchaseInfo.purchaseOrderNumber" placeholder="采购订单号" clearable></el-input>
          </el-form-item>
          <el-form-item label="供应商">
            <el-select v-model="purchaseInfo.supplierId" placeholder="供应商"></el-select>
          </el-form-item>
        </el-row>
        <el-row>
          <el-form-item label="供应商订单号">
            <el-input v-model="purchaseInfo.supplierOrderNumber" placeholder="供应商订单号" clearable></el-input>
          </el-form-item>
          <el-form-item label="订单状态">
            <el-select v-model="purchaseInfo.warehouseId" placeholder="订单状态"></el-select>
          </el-form-item>
        </el-row>
        <el-row>
          <el-form-item label="仓库">
            <el-select v-model="purchaseInfo.supplierId" placeholder="仓库"></el-select>
          </el-form-item>
          <el-form-item label="货品信息">
            <el-select v-model="purchaseInfo.supplierId" placeholder="货品信息"></el-select>
          </el-form-item>
        </el-row>
        <el-row>
          <el-form-item label="创建时间">
            <el-date-picker
              v-model="purchaseInfo.createTime"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期">
            </el-date-picker>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="getTableData">查询</el-button>
          </el-form-item>
        </el-row>
      </el-form>
    </div>

    <div clasee="exportBtn" id="exportBtn">
      <el-button type="button" onclick="exportFile()">新增</el-button>
      <el-button type="button" onclick="exportFile()">导入</el-button>
    </div>

    <!--表格-->
    <div class="purchase-order-management-table">
      <el-table :data="tableData">
        <el-table-column align="center" header-align="center" prop="supplierOrderNo" label="采订单号"></el-table-column>
        <el-table-column align="center" header-align="center" prop="contractReferenceOrderNo" label="供应商订单号"></el-table-column>
        <el-table-column align="center" header-align="center" prop="supplierOrderNo" label="合同参考号"></el-table-column>
        <el-table-column align="center" header-align="center" prop="contractReferenceOrderNo" label="供应商"></el-table-column>
        <el-table-column align="center" header-align="center" prop="supplierOrderNo" label="采购方式"></el-table-column>
        <el-table-column align="center" header-align="center" prop="contractReferenceOrderNo" label="订单金额"></el-table-column>
        <el-table-column align="center" header-align="center" prop="supplierOrderNo" label="剩余收货数量"></el-table-column>
        <el-table-column align="center" header-align="center" prop="contractReferenceOrderNo" label="订单状态"></el-table-column>
        <el-table-column align="center" header-align="center" prop="supplierOrderNo" label="创建时间"></el-table-column>
        <el-table-column align="center" header-align="center" prop="contractReferenceOrderNo" label="创建人"></el-table-column>
        <el-table-column align="center" header-align="center" prop="supplierOrderNo" label="操作"></el-table-column>
      </el-table>
    </div>
    <!--分页-->
    <div class="fx-pagination">
      <span class="pagination-total">共 {{this.pagination.total}} 条</span>
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page.sync="pagination.pageNumber"
        :page-sizes="pagination.pageList"
        :page-size="pagination.pageSize"
        layout="sizes, prev, pager, next"
        :total="pagination.total">
      </el-pagination>
    </div>
  </div>
</template>
<script>
  import {mapGetters} from 'vuex'
  import eventBus from '../../assets/js/eventBus.js';
  import ElRow from "element-ui/packages/row/src/row";

  export default {
    components: {ElRow},
    data() {
      return {
        purchaseInfo: {
          purchaseOrderNumber: "",
          supplierOrderNumber: "",
          supplierId: "",
          warehouseId: "",
          createTime: ""
        },
        pagination: {
          // 每页显示数量
          pageSize: 60,
          // 页码
          pageNumber: 1,
          // 页码list
          pageList: [5, 10, 20],
          // 总条数
          total: 0
        },
        tableData: [],
        changedProjectId: 222,
      }
    },
    computed: {
      ...mapGetters([
        'getProjectInfo'
      ])
    },
    methods: {
      purchaseOrderNumberClick({row}) {
        let {purchaseOrderNumber} = row;
        console.log(purchaseOrderNumber)
      },
      tableRowClassName({row, rowIndex}) {
        if (rowIndex === 1) {
          return 'waring-row';
        } else if (rowIndex === 3) {
          return 'success-row';
        }
        return ''
      },
      handleSizeChange(size) {
        this.pagination.pageSize = size;
        this.getTableData();
      },
      handleCurrentChange(currentPage) {
        this.pagination.currentPage = currentPage;
        this.getTableData()
      },
      checkClick(scope) {
        let {purchaseOrderNumber} = scope;
        console.log(purchaseOrderNumber);
      },
      deleteRow(index, rows) {
        rows.splice(index, 1);
      },
    },
    watch: {
      getProjectInfo(newProjectId) {
        this.changedProjectId = newProjectId;
      }
    },
    mounted() {
      eventBus.$on('loadingDefaultProjectId', () => {
      });
    },
    created() {
    }
  }
</script>

<style scoped lang="less">

</style>
