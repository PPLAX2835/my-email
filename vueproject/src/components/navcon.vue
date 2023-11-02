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

        <el-menu-item v-for="(email, index) in emails" @click="switchEmailIndex(index)">{{ email.emailAddress }}</el-menu-item>

        <el-menu-item @click="exit()" index="2-3">添加邮箱</el-menu-item>
        <el-menu-item @click="exit()" index="2-3">退出账号</el-menu-item>
      </el-submenu>
    </el-menu>

  </div>
</template>
<script>
import {
  loginout ,
  getSelf,
  userSave
} from '../api/userMG'
import { EmailList } from '../api/basisMG'

export default {
  name: 'navcon',
  data() {
    return {
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
      emails: [],
      // rules表单验证
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
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
              if (res.code == 200) {
                localStorage.removeItem("userInfo");
                localStorage.removeItem("logintoken");

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
                this.$message.error(res.message)
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
    // 获取数据
    getData() {

      /**
       * 获取用户邮箱
       */
      EmailList().then(res => {
        if (res.code == 200) {
          // 放到全局变量
          this.$globle.setEmails(res.data)
          // 保存到当前变量
          this.emails = res.data
        } else {
          this.user = res.data
        }
      })


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
    /**
     * 记录当前正在使用的邮箱的下标
     */
    switchEmailIndex(index) {
        this.$globle.setCurrentEmailIndex(index)
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
