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
        <template slot="title">
          {{ user.username }}
          |
          {{ currentEmail }}
        </template>

        <el-menu-item v-for="(email, index) in emails" @click="switchEmailIndex(index)">{{ email.emailAddress }}</el-menu-item>

        <el-menu-item @click="addEmail()" index="2-3">添加邮箱</el-menu-item>
        <el-menu-item @click="exit()" index="2-3">退出账号</el-menu-item>
      </el-submenu>
    </el-menu>

    
    <el-dialog title="添加邮箱" :visible.sync="addEmailDialogState" width="30%" @click="closeDialog">
      <el-form :model="editForm" ref="editForm">

        <el-form-item label="邮箱" prop="title">
          <el-input size="small" v-model="editForm.emailAddress" auto-complete="off" @blur="addEmailSuffix" placeholder="请输入邮箱">
            <el-select v-model="editForm.type" slot="append" @change="addEmailSuffix" placeholder="请选择">
              <el-option
                v-for="emailType in emailTypes"
                :key="emailType.value"
                :label="emailType.label"
                :value="emailType.value">
              </el-option>
            </el-select>
          </el-input>
        </el-form-item>

        <el-form-item label="授权码" prop="title">
          <el-input size="small" v-model="editForm.emailPassword" auto-complete="off" @blur="addEmailSuffix" placeholder="请输入授权码"></el-input>
        </el-form-item>


      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button size="small" v-if="hasEmails" @click="closeDialog">取消</el-button>
        <el-button size="small" type="primary" :loading="loading" class="title" @click="submitForm()">保存</el-button>
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
import { EmailList, addEmail } from '../api/basisMG'

export default {
  name: 'navcon',
  data() {
    return {
      collapsed: true,
      imgshow: require('../assets/img/show.png'),
      imgsq: require('../assets/img/sq.png'),
      loading: false, //是显示加载
      user: {},
      emails: [],
      hasEmails: false,   // 如果为false，添加邮箱的取消按钮不可用
      addEmailDialogState: false,
      currentEmail: '',
      emailTypes: [
        {
            value: '@qq.com',
            label: '@qq.com'
        }, 
        {
            value: '@aliyun.com',
            label: '@aliyun.com'
        }, 
        {
            value: '@sina.cn',
            label: '@sina.cn'
        }, 
        {
            value: '@163.com',
            label: '@163.com'
        }
      ],
      editForm: {
        emailAddress: '',
        emailPassword: '',
        type: ''
      },
      // rules表单验证
      rules: {
        emailAddress: [
          { required: true, message: '请输入邮箱', trigger: 'blur' }
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

      if (this.$globle.EMAILS == 0) {
        /**
         * 获取用户邮箱
         */
        EmailList().then(res => {
          if (res.code == 200) {
            // 放到全局变量
            this.$globle.setEmails(res.data)
            // 保存到当前变量

            if (res.data.length != 0) {
              this.emails = res.data
              this.currentEmail = this.emails[0].emailAddress;
              this.hasEmails = true;
            } else {
              this.hasEmails = false;
              this.addEmail()
            }

          } else {
            this.user = res.data
          }
        })
      }


      getSelf().then(res => {
        this.loading = false
        if (res.code == 200) {
          this.user = res.data
        } else {
          this.$message({
            type: 'info',
            message: res.msg
          })
        }
      })
    },
    /**
     * 记录当前正在使用的邮箱的下标
     */
    switchEmailIndex(index) {
      this.$globle.setCurrentEmailIndex(index);
    },
    closeDialog() {
      this.addEmailDialogState = false;
    },
    addEmailSuffix() {

        if (this.editForm.emailAddress.indexOf("@") != -1) {
          this.editForm.emailAddress = this.editForm.emailAddress.substring(0, this.editForm.emailAddress.indexOf("@")) + this.editForm.type          
        } else {
          this.editForm.emailAddress += this.editForm.type
        }


    },
    addEmail() {
      this.addEmailDialogState = true
    },
    submitForm() {

      let that = this;

      addEmail(this.editForm).then(res => {
        that.loading = false
        if (res.code == 200) {
          that.closeDialog();
          that.$message({
            type: 'success',
            message: "添加成功"
          })

          that.getData()
        } else {
          that.$message({
            type: 'error',
            message: res.message
          })
        }
      })
    }
  }
}
</script>
<style scoped>
.el-select .el-input {
  width: 130px;
}
.input-with-select .el-input-group__prepend {
  background-color: #fff;
}
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
