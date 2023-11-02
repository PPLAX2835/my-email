/**
 * 系统管理  角色管理
 */
<template>
  <div>
    <!-- 面包屑导航 -->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>文章分类管理</el-breadcrumb-item>
    </el-breadcrumb>
    <!-- 搜索筛选 -->
    <el-form :inline="true" :model="formInline" class="user-search">

      <el-form-item label="搜索：">
        <el-input size="small" v-model="formInline.name" placeholder="输入分类名称"></el-input>
      </el-form-item>
      <el-form-item>
        <el-input size="small" v-model="formInline.id" placeholder="输入分类id"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button size="small" type="primary" icon="el-icon-search" @click="search">搜索</el-button>
        <el-button size="small" type="primary" icon="el-icon-plus" @click="handleEdit()">添加</el-button>
        <el-button size="small" type="danger" icon="el-icon-delete" @click="deleteCategory()">删除所选项</el-button>
      </el-form-item>
    </el-form>
    <!--列表-->
    <el-table size="small" :data="listData" @selection-change="selectChange" highlight-current-row v-loading="loading" border element-loading-text="拼命加载中" style="width: 100%;">
      <el-table-column align="center" type="selection" width="60">
      </el-table-column>
      <el-table-column sortable prop="cateId" label="分类id" width="80">
      </el-table-column>
      <el-table-column sortable prop="name" label="分类名称" width="150">
      </el-table-column>
      <el-table-column sortable prop="description" label="分类描述" width="300">
      </el-table-column>
      <el-table-column sortable prop="parentCateId" label="父级分类id" width="120">
      </el-table-column>
      <el-table-column sortable prop="updatedAt" label="修改时间" width="200">
        <template slot-scope="scope">
          <div>{{scope.row.updatedAt|timestampToTime}}</div>
        </template>
      </el-table-column>
      <el-table-column align="center" label="操作" width="150">
        <template slot-scope="scope">
          <el-button size="mini" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="deleteCategory(scope.$index, scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <Pagination v-bind:child-msg="pageparm" @callFather="callFather"></Pagination>
    <!-- 编辑界面 -->
    <el-dialog :title="title" :visible.sync="editFormVisible" width="30%" @click='closeDialog("edit")'>
      <el-form label-width="120px" :model="editForm" ref="editForm" :rules="rules">
        <el-form-item label="分类名称" prop="name">
          <el-input size="small" v-model="editForm.name" auto-complete="off" placeholder="请输入分类名称"></el-input>
        </el-form-item>
        <el-form-item label="分类描述" prop="description">
           <el-input size="small" v-model="editForm.description" type="textarea" :rows="2" auto-complete="off" placeholder="请输入角色描述" ></el-input>
        </el-form-item>
        <el-form-item label="父分类" prop="parentCategory">
          <el-input size="small" v-model="editForm.parentCategoryName" auto-complete="off" placeholder="请输入父分类名称"></el-input>
        </el-form-item>
        <el-form-item label="修改时间" prop="updatedAt">
          <el-date-picker size="small" v-model="editForm.updatedAt" placeholder="请输入修改时间">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button size="small" @click='closeDialog("edit")'>取消</el-button>
        <el-button size="small" type="primary" :loading="loading" class="title" @click="submitForm('editForm')">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  RoleRightTree,
  loginout
} from '../../api/userMG'
import {
  CategoryList,
  CategoryParent,
  CategorySave,
  CategoryDelete
} from '../../api/basisMG'
import Pagination from '../../components/Pagination'
export default {
  data() {
    return {
      nshow: true, //switch开启
      fshow: false, //switch关闭
      loading: false, //是显示加载
      editFormVisible: false, //控制编辑页面显示与隐藏
      menuAccessshow: false, //控制数据权限显示与隐藏
      title: '添加',
      editForm: {
        cate_id: '',
        name: '',
        description: '',
        parentCategoryName:'',
        updatedAt: 0,
        token: localStorage.getItem('logintoken')
      },
      // rules 表单验证
      rules: {
        name: [
          { required: true, message: '请输入分类名称', trigger: 'blur' }
        ]
      },
      formInline: {
        page: 1,
        limit: 10,
        id: '',
        name: '',
        token: localStorage.getItem('logintoken')
      },
      // 删除
      selectdata: {
        ids: [],
        token: localStorage.getItem('logintoken')
      },
      userparm: [], //搜索权限
      listData: [], //用户数据
      // 数据权限
      RoleRight: [],
      RoleRightProps: {
        children: 'children',
        label: 'name'
      },
      // 选中
      checkmenu: [],
      //参数role
      saverid: '',
      // 分页参数
      pageparm: {
        currentPage: 1,
        pageSize: 10,
        total: 10
      }
    }
  },
  // 注册组件
  components: {
    Pagination
  },
  /**
   * 数据发生改变
   */

  watch: {},

  /**
   * 创建完毕
   */

  created() {
    this.getdata(this.formInline)
  },

  /**
   * 里面的方法只有被调用才会执行
   */

  methods: {
    // 获取角色列表
    getdata(parameter) {

      /***
       * 调用接口
       */
      CategoryList(parameter)
        .then(res => {
          this.loading = false
          if (res.success == false) {
            this.$message({
              type: 'info',
              message: res.msg
            })
          if (res.operation === "toLogin") {
            loginout()
              .then(res => {
                setTimeout(() => {
                  this.$store.commit('logout', 'false')
                  this.$router.push({ path: '/login' })
                }, 1000)}
              )
          }
          } else {
            this.listData = res.data
            // 分页赋值
            this.pageparm.currentPage = this.formInline.page
            this.pageparm.pageSize = this.formInline.limit
            this.pageparm.total = res.count
          }
        })
        .catch(err => {
          this.loading = false
          this.$message.error('获取角色列表失败，请稍后再试！')
        })
    },
    // 分页插件事件
    callFather(parm) {
      this.formInline.page = parm.currentPage
      this.formInline.limit = parm.pageSize
      this.getdata(this.formInline)
    },
    // 搜索事件
    search() {
      this.getdata(this.formInline)
    },
    //显示编辑界面
    handleEdit: function(index, row) {
      this.editFormVisible = true
      if (row != undefined && row != 'undefined') {
        this.title = '修改'
        this.editForm.cate_id = row.cateId
        this.editForm.name = row.name
        this.editForm.description = row.description
        if (!(row.parentCateId === undefined)) {
          CategoryParent(row.parentCateId)
            .then(res => {
              if (res.code == 200) {
                this.editForm.parentCategoryName = res.data.name
              } else {
                this.editForm.parentCategoryName = ''
              }
            })
        } else {
            this.editForm.parentCategoryName = ''
        }
        this.editForm.updatedAt = row.updatedAt
      } else {
        this.title = '添加'
        this.editForm.cate_id = ''
        this.editForm.name = ''
        this.editForm.description = ''
        this.editForm.parentCategoryName = ''
        this.editForm.updatedAt = Date.now()
      }
    },
    // 编辑、增加页面保存方法
    submitForm(editData) {
      this.$refs[editData].validate(valid => {
        if (valid) {
          CategorySave(this.editForm)
            .then(res => {
              this.editFormVisible = false
              this.loading = false
              if (res.success) {
                this.getdata(this.formInline)
                this.$message({
                  type: 'success',
                  message: '分类保存成功！'
                })
              } else {
                this.$message({
                  type: 'info',
                  message: res.msg
                })
              }
            })
            .catch(err => {
              this.editFormVisible = false
              this.loading = false
              this.$message.error('分类保存失败，请稍后再试！')
            })
        } else {
          return false
        }
      })
    },
    // 删除文章分类
    deleteCategory(index, row) {
      this.$confirm('确定要删除吗?', '信息', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          if (!(index === undefined) && !(row === undefined)) {
            // 删除一个
            CategoryDelete(row.cateId)
              .then(res => {
                if (res.success) {
                  this.$message({
                    type: 'success',
                    message: '文章类型已删除！'
                  })
                  this.getdata(this.formInline)
                } else {
                  this.$message({
                    type: 'info',
                    message: res.msg
                  })
                }
              })
              .catch(err => {
                this.loading = false
                this.$message.error('文章类型删除失败，请稍后再试！')
              })
          } else {
            // 批量删除
            let ids = [];
            for (let i = 0; i < this.selectdata.ids.length; i++){
              ids.push(this.selectdata.ids[i].cateId);
            }
            // 调用接口
            CategoryDelete(ids)
              .then(res => {
                if (res.success) {
                  this.$message({
                    type: 'success',
                    message: '文章类型已删除！'
                  })
                  this.getdata(this.formInline)
                } else {
                  this.$message({
                    type: 'info',
                    message: res.msg
                  })
                }
              })
              .catch(err => {
                this.loading = false
                this.$message.error('文章类型删除失败，请稍后再试！')
              })
          }
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    },
    // 选择复选框事件
    selectChange(val) {
      this.selectdata.ids = val
    },
    // 选中菜单
    changemenu(arr) {
      let change = []
      for (let i = 0; i < arr.length; i++) {
        if (arr[i].checked) {
          change.push(arr[i].id)
        }
      }
      this.checkmenu = change
    },
    // tree 递归
    changeArr(list) {
      var temptree = [],
        tree = [],
        items = []
      for (var i in list) {
        if (!temptree[list[i].id]) {
          var trow = {
            id: list[i].id,
            pId: list[i].pId,
            name: list[i].name,
            open: list[i].open,
            checked: list[i].checked,
            children: []
          }
          temptree[list[i].id] = trow
          items.push(trow)
        }
        if (list[i].uid > 0) {
          temptree[list[i].id]['children'].push({
            id: list[i].id,
            pId: list[i].pId,
            name: list[i].name,
            open: list[i].open,
            checked: list[i].checked,
            children: []
          })
        }
      }

      for (var j in items) {
        if (temptree[items[j].pId]) {
          temptree[items[j].pId]['children'].push(temptree[items[j].id])
        } else {
          tree.push(temptree[items[j].id])
        }
      }
      temptree = null
      items = null
      return tree
    },
    // 关闭编辑、增加弹出框
    closeDialog(dialog) {
      if (dialog == 'edit') {
        this.editFormVisible = false
      } else if (dialog == 'perm') {
        this.menuAccessshow = false
      }
    }
  }
}
</script>

<style scoped>
.user-search {
  margin-top: 20px;
}
.userRole {
  width: 100%;
}
</style>

