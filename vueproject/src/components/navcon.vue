/**
* 头部菜单
*/
<template>
  <div>
    <el-menu class="el-menu-demo" mode="horizontal" background-color="#334157" text-color="#fff" active-text-color="#fff">
      <el-button class="buttonimg">
        <img class="showimg" :src="collapsed?imgsq:imgshow" @click="toggle(collapsed)">
      </el-button>
      <el-submenu index="2" class="submenu">
        <template slot="title">{{user.name}}</template>
        <el-menu-item index="2-1">设置</el-menu-item>
        <el-menu-item @click="content()" index="2-2">个人中心</el-menu-item>
        <el-menu-item @click="exit()" index="2-3">退出</el-menu-item>
      </el-submenu>
    </el-menu>
    <!-- 个人中心展示 -->
    <el-dialog :title="user.name" :visible.sync="contentShow" width="30%" @click='closeDialog()'>
      <template>
        <div>
          <el-form>
            <el-form-item label="uid">
              <span>{{ user.uid }}</span>
            </el-form-item>
            <el-form-item label="地址">
              <span>{{ user.address }}</span>
            </el-form-item>
            <el-form-item label="Email">
              <span>{{ user.email }}</span>
            </el-form-item>
            <el-form-item label="性别">
              <span>{{ formatGender(user.gender) }}</span>
            </el-form-item>
            <el-form-item label="电话">
              <span>{{ user.phone }}</span>
            </el-form-item>
            <el-form-item label="最喜欢的音乐人">
              <span>{{ user.favouriteMusician }}</span>
            </el-form-item>
          </el-form>
        </div>
      </template>
      <div slot="footer" class="dialog-footer">
        <el-button size="small" @click='closeDialog()'>关闭</el-button>
        <el-button size="small" type="primary" :loading="loading" class="title" @click="contentEditOpen">编辑</el-button>
      </div>
    </el-dialog>
    <!-- 个人中心编辑 -->
    <el-dialog title="编辑" :visible.sync="contentEditShow" width="30%" @click='closeDialog()'>
      <div>
        <el-form label-width="80px" ref="editForm" :model="editForm" :rules="rules">
          <el-form-item label="用户名" prop="username">
            <el-input size="small" v-model="editForm.username" auto-complete="off" placeholder="请输入用户名"></el-input>
          </el-form-item>
          <el-form-item label="昵称" prop="name">
            <el-input size="small" v-model="editForm.name" auto-complete="off" placeholder="请输入昵称"></el-input>
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
        </el-form>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button size="small" @click='closeDialog()'>取消</el-button>
        <el-button size="small" type="primary" :loading="loading" class="title" @click="selfContentSave">保存</el-button>
      </div>
    </el-dialog>

  </div>
</template>
<script>
import {
  loginout ,
  getSelf,
  userSave
} from '../api/userMG'
export default {
  name: 'navcon',
  data() {
    return {
      contentShow: false,
      contentEditShow: false,
      collapsed: true,
      // 编辑
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
        updatedAt: '',
        token: localStorage.getItem('logintoken')
      },
      imgshow: require('../assets/img/show.png'),
      imgsq: require('../assets/img/sq.png'),
      user: {},
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
        ]
      }
    }
  },
  // 创建完毕状态(里面是操作)
  created() {
    this.getData()
  },
  methods: {
    // 退出登录
    exit() {
      this.$confirm('退出登录, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          loginout()
            .then(res => {
              if (res.success) {
                //如果请求成功就让他2秒跳转路由
                setTimeout(() => {
                  this.$store.commit('logout', 'false')
                  this.$router.push({ path: '/login' })
                  this.$message({
                    type: 'success',
                    message: '已退出登录!'
                  })
                }, 1000)
              } else {
                this.$message.error(res.msg)
                this.logining = false
                return false
              }
            })
            .catch(err => {
              // 获取图形验证码
              this.getcode()
              this.logining = false
              this.$message.error('退出失败，请稍后再试！')
            })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消'
          })
        })
    },
    // 获取个人数据
    getData() {
      getSelf().then(res => {
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
          this.user = res.data
        }
      })
    },
    // 个人中心展示
    content() {
      this.contentShow = true
      this.contentEditShow = false
    },
    formatGender(gender) {
      return gender === 0 ? "女" : "男";
    },
    // 编辑界面展示
    contentEditOpen() {
      this.contentShow = false
      this.contentEditShow = true

      this.editForm.uid = this.user.uid
      this.editForm.rid = this.user.rid
      this.editForm.username = this.user.username
      this.editForm.name = this.user.name
      this.editForm.phone = this.user.phone
      this.editForm.email = this.user.email
      this.editForm.gender = this.user.gender
      this.editForm.address = this.user.address
      this.editForm.favouriteMusician = this.user.favouriteMusician

    },
    // 保存
    selfContentSave() {
        // 请求方法
        userSave(this.editForm)
          .then(res => {
            this.contentEditShow = false
            if (res.success) {
              this.user = this.editForm
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
            this.contentEditShow = false
            this.$message.error('保存失败，请稍后再试！')
          })
    },
    // 关闭弹窗
    closeDialog() {
      this.contentShow = false
      this.contentEditShow = false
    },
    // 切换显示
    toggle(showtype) {
      this.collapsed = !showtype
      this.$root.Bus.$emit('toggle', this.collapsed)
    }
  }
}
</script>
<style scoped>
.el-menu-vertical-demo:not(.el-menu--collapse) {
  border: none;
}
.submenu {
  float: right;
}
.buttonimg {
  height: 60px;
  background-color: transparent;
  border: none;
}
.showimg {
  width: 26px;
  height: 26px;
  position: absolute;
  top: 17px;
  left: 17px;
}
.showimg:active {
  border: none;
}
</style>
