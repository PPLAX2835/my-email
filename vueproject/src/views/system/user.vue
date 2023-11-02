/**
 * 系统管理 用户管理
 */
<template>
  <div>
    <!-- 面包屑导航 -->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>用户管理</el-breadcrumb-item>
    </el-breadcrumb>
    <!-- 搜索筛选 -->
    <el-form :inline="true" :model="formInline" class="user-search">
      <el-form-item label="搜索：">
        <el-select size="small" v-model="formInline.isLock" placeholder="请选择">
          <el-option label="全部" value=""></el-option>
          <el-option label="正常" value="N"></el-option>
          <el-option label="已锁定" value="Y"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="">
        <el-input size="small" v-model="formInline.username" placeholder="输入用户名"></el-input>
      </el-form-item>
      <el-form-item label="">
        <el-input size="small" v-model="formInline.name" placeholder="输入昵称"></el-input>
      </el-form-item>
      <el-form-item label="">
        <el-input size="small" v-model="formInline.phone" placeholder="输入手机号"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button size="small" type="primary" icon="el-icon-search" @click="search">搜索</el-button>
        <el-button size="small" type="primary" icon="el-icon-plus" @click="handleEdit()">添加</el-button>
          <el-button size="small" type="danger" icon="el-icon-delete" @click="deleteUser()">删除所选项</el-button>
      </el-form-item>
    </el-form>
    <!--列表-->
    <el-table size="small" @selection-change="selectChange" :data="userData" highlight-current-row v-loading="loading" border element-loading-text="拼命加载中" style="width: 100%;">
      <el-table-column align="center" type="selection" width="50">
      </el-table-column>
      <el-table-column align="center" sortable prop="uid" label="uid" width="120">
      </el-table-column>
      <el-table-column align="center" sortable prop="username" label="用户名" width="120">
      </el-table-column>
      <el-table-column align="center" sortable prop="name" label="昵称" width="120">
      </el-table-column>
      <el-table-column align="center" sortable prop="phone" label="手机号" width="120">
      </el-table-column>
      <el-table-column align="center" sortable prop="gender" label="性别" min-width="50">
        <template slot-scope="scope">
          <div>{{scope.row.gender|genderText}}</div>
        </template>
      </el-table-column>
      <el-table-column align="center" sortable prop="email" label="邮箱" min-width="200">
      </el-table-column>
      <el-table-column align="center" sortable prop="address" label="地址" min-width="120">
      </el-table-column>
      <el-table-column align="center" sortable prop="favouriteMusician" label="最喜欢的音乐人" min-width="120">
      </el-table-column>
      <el-table-column align="center" sortable prop="updatedAt" label="修改时间" min-width="200">
        <template slot-scope="scope">
          <div>{{scope.row.updatedAt|timestampToTime}}</div>
        </template>
      </el-table-column>
      <el-table-column align="center" sortable prop="isLock" label="状态" min-width="50">
        <template slot-scope="scope">
          <el-switch v-model="scope.row.lockFlag?nshow:fshow" active-color="#13ce66" inactive-color="#ff4949" @change="editType(scope.$index, scope.row)">
          </el-switch>
        </template>
      </el-table-column>
      <el-table-column label="操作" min-width="500">
        <template slot-scope="scope">
          <el-button size="mini" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="deleteUser(scope.$index, scope.row)">删除</el-button>
          <el-button size="mini" type="success" @click="resetpwd(scope.$index, scope.row)">重置密码</el-button>
          <el-button size="mini" type="success" @click="offlineUser(scope.$index, scope.row)">下线</el-button>
          <el-button size="mini" type="success" @click="refreshCache()">刷新缓存</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <Pagination v-bind:child-msg="pageparm" @callFather="callFather"></Pagination>
    <!-- 编辑界面 -->
    <el-dialog :title="title" :visible.sync="editFormVisible" width="30%" @click='closeDialog("edit")'>
      <el-form label-width="80px" ref="editForm" :model="editForm" :rules="rules">
        <el-form-item label="用户名" prop="username">
          <el-input size="small" v-model="editForm.username" auto-complete="off" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="昵称" prop="name">
          <el-input size="small" v-model="editForm.name" auto-complete="off" placeholder="请输入昵称"></el-input>
        </el-form-item>
        <el-form-item label="角色" prop="rid">
          <el-select v-model="editForm.rid" placeholder="请选择">
            <el-option
              v-for="role in roleData"
              :key="role.rid"
              :label="role.roleName"
              :value="role.rid"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input size="small" v-model="editForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="邮件" prop="email">
          <el-input size="small" v-model="editForm.email" placeholder="请输入邮箱地址"></el-input>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio v-model="editForm.gender" :label="1" :checked="editForm.gender === 1">男</el-radio>
          <el-radio v-model="editForm.gender" :label="0" :checked="editForm.gender === 0">女</el-radio>
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input size="small" v-model="editForm.address" placeholder="请输入地址"></el-input>
        </el-form-item>
        <el-form-item label="音乐" prop="favouriteMusician">
          <el-input size="small" v-model="editForm.favouriteMusician" placeholder="请输入最喜欢的音乐人"></el-input>
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
// 导入请求方法
import {
  userList,
  userSave,
  userDelete,
  userPwd,
  userExpireToken,
  userFlashCache,
  userLock,
  UserDeptTree,
  UserDeptdeptTree,
  UserChangeDept,
  loginout,
  roleList
} from '../../api/userMG'
import Pagination from '../../components/Pagination'
export default {
  data() {
    return {
      nshow: true, //switch开启
      fshow: false, //switch关闭
      loading: false, //是显示加载
      title: '添加用户',
      editFormVisible: false, //控制编辑页面显示与隐藏
      unitAccessshow: false, //控制所属单位隐藏与显示
      // 编辑与添加
      editForm: {
        uid: '',
        username: '',
        name: '',
        rid: '',
        phone: '',
        email: '',
        gender: -1,
        address: '',
        favouriteMusician: '',
        updatedAt: 0,
        token: localStorage.getItem('logintoken')
      },
      // 参数
      unitparm: {
        uids: '',
        deptId: '',
        deptName: ''
      },
      // 选择数据
      selectdata: [],
      // rules表单验证
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        rid: [{ required: true, message: '请选择角色', trigger: 'blur' }],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          {
            pattern: /^1(3\d|47|5((?!4)\d)|7(0|1|[6-8])|8\d)\d{8,8}$/,
            required: true,
            message: '请输入正确的手机号',
            trigger: 'blur'
          }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          {
            pattern: /^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/,
            required: true,
            message: '请输入正确的邮箱',
            trigger: 'blur'
          }
        ],
        gender: [{ required: false, message: '请选择性别', trigger: 'blur' }],
        address: [{ required: false, message: '请选择地址', trigger: 'blur' }],
        favouriteMusician: [{ required: false, message: '请选择最喜欢的音乐人', trigger: 'blur' }],
        updatedAt: [{ required: false, message: '请选择修改时间', trigger: 'blur' }]
      },
      // 删除用户
      seletedata: {
        ids: '',
        token: localStorage.getItem('logintoken')
      },
      // 重置密码
      resetpsd: {
        uid: '',
        token: localStorage.getItem('logintoken')
      },
      // 用户下线
      offline: {
        token: localStorage.getItem('logintoken')
      },
      // 请求数据参数
      formInline: {
        page: 1,
        limit: 10,
        name: '',
        username: '',
        phone: '',
        isLock: '',
        token: localStorage.getItem('logintoken')
      },
      roleFormInline: {
        maxSize: 10,
        token: localStorage.getItem('logintoken')
      },
      //用户数据
      userData: [],
      // 角色数据
      roleData: [],
      // 数据权限
      UserDept: [],
      defaultProps: {
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
    this.getRoleData(this.roleFormInline)
  },

  /**
   * 过滤器
   */
  filters: {
    genderText(value) {
      console.log(value)
      return value == 0 ? '女' : '男';
    }
  },

  /**
   * 里面的方法只有被调用才会执行
   */
  methods: {
    // 获取数据方法
    getdata(parameter) {
      this.loading = true

      /***
       * 调用接口
       */
      // 获取用户列表
      userList(parameter).then(res => {
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
          this.userData = res.data
          // 分页赋值
          this.pageparm.currentPage = this.formInline.page
          this.pageparm.pageSize = this.formInline.limit
          this.pageparm.total = res.count
          // 复选框状态赋值
          for (let i = 0; i < res.count; i++) {
            this.options.push({
              "checked": false
            });
          }
        }
      })
    },
    // 获取角色列表
    getRoleData(parameter) {
      roleList(parameter).then(res => {
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
          this.roleData = res.data
        }
      })
    },
    // 根据rid获得角色
    getRole(rid) {
      for (let i = 0; i < this.roleData.length; i++) {
        if (rid == this.roleData[i].rid) {
          return this.roleData[i];
        }
      }
    },
    // 分页插件事件
    callFather(parm) {
      this.formInline.page = parm.currentPage
      this.formInline.limit = parm.pageSize
      this.getdata(this.formInline)
    },
    //搜索事件
    search() {
      this.getdata(this.formInline)
    },
    // 修改type
    editType: function(index, row) {
      this.loading = true
      let parm = {
        lock: '',
        uid: '',
        token: localStorage.getItem('logintoken')
      }
      parm.uid = row.uid
      let lock = row.isLock
      if (lock === 1) {
        parm.lock = 'N'
      } else {
        parm.lock = 'Y'
      }
      // 修改状态
      userLock(parm).then(res => {
        this.loading = false
        if (res.success == false) {
          this.$message({
            type: 'info',
            message: res.msg
          })
        } else {
          this.$message({
            type: 'success',
            message: '状态修改成功'
          })
          this.getdata(this.formInline)
        }
      })
    },
    //显示编辑界面
    handleEdit: function(index, row) {
      this.editFormVisible = true
      if (row != undefined && row != 'undefined') {
        this.title = '修改用户'
        this.editForm.uid = row.uid
        this.editForm.username = row.username
        this.editForm.name = row.name
        this.editForm.rid = row.rid
        this.editForm.address = row.address
        this.editForm.phone = row.phone
        this.editForm.email = row.email
        this.editForm.gender = row.gender
        this.editForm.updatedAt = row.updatedAt
        this.editForm.favouriteMusician = row.favouriteMusician
      } else {
        this.title = '添加用户'
        this.editForm.uid = ''
        this.editForm.username = ''
        this.editForm.name = ''
        this.editForm.rid = ''
        this.editForm.phone = ''
        this.editForm.email = ''
        this.editForm.gender = ''
        this.editForm.updatedAt = Date.now()
      }
    },
    // 编辑、添加提交方法
    submitForm(editData) {
      this.$refs[editData].validate(valid => {
        if (valid) {
          // 请求方法
          userSave(this.editForm)
            .then(res => {
              this.editFormVisible = false
              this.loading = false
              if (res.success) {
                this.getdata(this.formInline)
                this.$message({
                  type: 'success',
                  message: '数据保存成功！'
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
              this.$message.error('保存失败，请稍后再试！')
            })
        } else {
          return false
        }
      })
    },
    // 显示部门设置
    handleunit: function(index, row) {
      this.unitAccessshow = true
      let parms = 0
      UserDeptdeptTree(parms)
        .then(res => {
          if (res.data.success) {
            this.UserDept = this.changeArr(res.data.data)
          } else {
            this.$message({
              type: 'info',
              message: res.msg
            })
          }
        })
        .catch(err => {
          this.loading = false
          this.$message.error('配置权限失败,请稍后再试！')
        })
    },
    handleClick(data, checked, node) {
      if (checked) {
        this.$refs.tree.setCheckedNodes([])
        this.$refs.tree.setCheckedNodes([data])
        this.unitparm.deptId = data.id
        this.unitparm.deptName = data.name
        //交叉点击节点
      } else {
      }
    },
    // 保存部门
    unitPermSave() {
      let len = this.selectdata
      let ids = []
      if (len != 0) {
        for (let i = 0; i < len.length; i++) {
          ids.push(len[i].uid)
        }
      }
      this.unitparm.uids = ids.join(',')
      UserChangeDept(this.unitparm)
        .then(res => {
          this.unitAccessshow = false
          if (res.success) {
            this.$message({
              type: 'success',
              message: '部门设置成功！'
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
          this.$message.error('部门设置失败,请稍后再试！')
        })
    },
    // 选择复选框事件
    selectChange(val) {
      this.selectdata = val
    },
    // 关闭编辑、增加弹出框
    closeDialog(dialog) {
      if (dialog == 'edit') {
        this.editFormVisible = false
      } else if (dialog == 'unit') {
        this.unitAccessshow = false
      }
    },
    // 删除用户
    deleteUser(index, row) {
      this.$confirm('确定要删除吗?', '信息', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          if (!(index === undefined) && !(row === undefined)) {
            // 删除一个
            userDelete(row.uid)
              .then(res => {
                if (res.success) {
                  this.$message({
                    type: 'success',
                    message: '数据已删除!'
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
                this.$message.error('数据删除失败，请稍后再试！')
              })
          } else {
            // 批量删除
            let ids = [];
            for (let i = 0; i < this.selectdata.length; i++){
              ids.push(this.selectdata[i].uid);
            }
            // 调用接口
            userDelete(ids)
              .then(res => {
                if (res.success) {
                  this.$message({
                    type: 'success',
                    message: '数据已删除!'
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
                this.$message.error('数据删除失败，请稍后再试！')
              })
          }

        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除！'
          })
        })
    },
    // 重置密码
    resetpwd(index, row) {
      this.resetpsd.uid = row.uid
      this.$confirm('确定要重置密码吗?', '信息', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          userPwd(this.resetpsd)
            .then(res => {
              if (res.success) {
                this.$message({
                  type: 'success',
                  message: '重置密码成功！'
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
              this.$message.error('重置密码失败，请稍后再试！')
            })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '取消重置密码！'
          })
        })
    },
    //数据格式化
    changeArr(data) {
      var pos = {}
      var tree = []
      var i = 0
      while (data.length != 0) {
        if (data[i].pId == 0) {
          tree.push({
            id: data[i].id,
            name: data[i].name,
            pId: data[i].pId,
            open: data[i].open,
            checked: data[i].checked,
            children: []
          })
          pos[data[i].id] = [tree.length - 1]
          data.splice(i, 1)
          i--
        } else {
          var posArr = pos[data[i].pId]
          if (posArr != undefined) {
            var obj = tree[posArr[0]]
            for (var j = 1; j < posArr.length; j++) {
              obj = obj.children[posArr[j]]
            }

            obj.children.push({
              id: data[i].id,
              name: data[i].name,
              pId: data[i].pId,
              open: data[i].open,
              checked: data[i].checked,
              children: []
            })
            pos[data[i].id] = posArr.concat([obj.children.length - 1])
            data.splice(i, 1)
            i--
          }
        }
        i++
        if (i > data.length - 1) {
          i = 0
        }
      }
      return tree
    },
    // 选中菜单
    changemenu(arr) {
      let change = []
      for (let i = 0; i < arr.length; i++) {
        if (arr[i].checked) {
          change.push(arr[i].id)
        }
      }
      return change
    },
    // 菜单权限-保存
    menuPermSave() {
      let parm = {
        uid: this.saverid,
        deptIds: ''
      }
      let node = this.$refs.tree.getCheckedNodes()
      let moduleIds = []
      if (node.length != 0) {
        for (let i = 0; i < node.length; i++) {
          moduleIds.push(node[i].id)
        }
        parm.deptIds = JSON.stringify(moduleIds)
      }
    },
    // 下线用户
    offlineUser(index, row) {
      this.$confirm('确定要让' + row.username + '用户下线吗?', '信息', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          console.log(row.username)
          userExpireToken({
            "uid": row.uid,
            "token": localStorage.getItem('logintoken')
          })
            .then(res => {
              if (res.success) {
                this.$message({
                  type: 'success',
                  message: '用户' + row.username + '强制下线成功！'
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
              this.$message.error('用户下线失败，请稍后再试！')
            })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消'
          })
        })
    },
    // 刷新缓存
    refreshCache(index, row) {
      userFlashCache(row.username)
        .then(res => {
          if (res.success) {
            this.$message({
              type: 'success',
              message: '刷新成功！'
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
          this.$message.error('刷新失败，请稍后再试！')
        })
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

