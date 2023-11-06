
<template>
  <div>
    <!-- 面包屑导航 -->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>写邮件</el-breadcrumb-item>
    </el-breadcrumb>
    <!-- 编辑表 -->
    <div style="margin-top: 5%; padding: 20%; padding-top: 0;" >
        <el-form :model="editForm" ref="editForm">

            
            <el-form-item label="发件人邮箱">
                <el-input size="small" readonly :value="editForm.senderEmailAddress" auto-complete="off">
                </el-input>
            </el-form-item>


            <el-form-item label="收件人邮箱" prop="title">
                <el-input size="small" v-model="editForm.receiverEmailAddress" auto-complete="off" @blur="addEmailSuffix" placeholder="请输入邮箱">
                    <el-select v-model="emailType" slot="append" @change="addEmailSuffix" placeholder="请选择">
                        <el-option
                            v-for="emailType in emailTypes"
                            :key="emailType.value"
                            :label="emailType.label"
                            :value="emailType.value">
                        </el-option>
                    </el-select>
                </el-input>
            </el-form-item>
            <el-form-item label="主题">
                <el-input size="small" v-model="editForm.subject" auto-complete="off" placeholder="请输入主题">
                </el-input>
            </el-form-item>
            <el-form-item>
                <el-upload style="float: left;" v-model="editForm.attachment"
                    action="string"
                    :auto-upload="false" :on-change="selectAttachment"
                    :show-file-list="false">
                    <el-button>{{ editForm.attachmentFileName }}</el-button>
                </el-upload>
                <el-button v-if="editForm.hasAttachment" type="warning" @click="removeAttachment">删除附件</el-button>
            </el-form-item>
            <el-form-item>
                <editor style="height: 70vh" ref="myEditor"></editor>
            </el-form-item>
            
            <div style="margin-top: 15%;">
                <el-button :loading="loading" @click="submitEmail">发送邮件</el-button>
            </div>


        </el-form>

    </div>
    
  </div>
</template>

<script>
import Pagination from '../../components/Pagination'
import editor from '../../components/RichTextEditor.vue'
import { sendEmail } from '../../api/basisMG'
import Axios from 'axios'

export default {
  data() {
    return {
      nshow: true, //switch开启
      fshow: false, //switch关闭
      loading: false, //是显示加载
      editFormVisible: false, //控制编辑页面显示与隐藏
      authorTableVisible: false,  // 作者详情页面显示与隐藏
      detailTableVisible: false, // 控制详情页面显示与隐藏
      isPublishAtVisible: false,
      content: '',
      title: '',
      author: '',
      editTitle: '添加',
      emailType: '',
      editForm: {
        senderEmailAddress: this.$globle.EMAILS[this.$globle.CURRENT_EMAIL_INDEX].emailAddress,
        receiverEmailAddress: '',
        subject:'',
        hasAttachment: false,
        attachmentFileName: '选择附件',
        attachment: '',
        content: ''
      },
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
    }
  },
  // 注册组件
  components: {
    Pagination,
    editor
  },
  /**
   * 数据发生改变
   */

  /**
   * 创建完毕
   */
  created() {
  },

  /**
   * 里面的方法只有被调用才会执行
   */
  methods: {
    addEmailSuffix() {

        if (this.editForm.receiverEmailAddress.indexOf("@") != -1 && this.emailType != '') {
          this.editForm.receiverEmailAddress = this.editForm.receiverEmailAddress.substring(0, this.editForm.receiverEmailAddress.indexOf("@")) + this.emailType
        } else {
          this.editForm.receiverEmailAddress += this.emailType
        }


    },
    selectAttachment(file) {
        console.log(file)

        this.editForm.hasAttachment = true;
        this.editForm.attachment = file.raw;
        this.editForm.attachmentFileName = file.name;

    },
    removeAttachment() {
        this.editForm.attachment = '';
        this.editForm.hasAttachment = false;
        this.editForm.attachmentFileName = '选择附件';
    },
    submitEmail() {
      this.loading = true;

      
      const editorComponent = this.$refs.myEditor;
      this.editForm.content = editorComponent.getContent();

      if(this.editForm.senderEmailAddress == '' || this.editForm.receiverEmailAddress == '' || this.editForm.subject == '' || this.editForm.content == '') {
        this.loading = false;
        return ;
      } else {
        var reg = /^([a-zA-Z]|[0-9])(\w|\-)+@[a-zA-Z0-9]+\.([a-zA-Z]{2,4})$/;
        if (!reg.test(this.editForm.receiverEmailAddress)) {
          this.$message({
              message: '收件人邮箱不正确',
              type: 'error'
          })
          this.loading = false;
          return;
        }
      }
    

      const uploadForm = new FormData();
      uploadForm.append('senderEmailAddress', this.editForm.senderEmailAddress);
      uploadForm.append('receiverEmailAddress', this.editForm.receiverEmailAddress);
      uploadForm.append('subject', this.editForm.subject);
      uploadForm.append('content', this.editForm.content);
      uploadForm.append('hasAttachment', this.editForm.hasAttachment);
      uploadForm.append('hasAttachment', this.editForm.hasAttachment);
      uploadForm.append('attachment', this.editForm.attachment);
      uploadForm.append('attachmentFileName', this.editForm.attachmentFileName);
  
      let that = this;

      sendEmail(uploadForm)
        .then(res => {
          this.loading = false;

          if(res.code == 200) {
              that.$message({
              message: '发送成功',
              type: 'success'
              })

              that.editForm = {
                  senderEmailAddress: this.$globle.EMAILS[this.$globle.CURRENT_EMAIL_INDEX].emailAddress,
                  receiverEmailAddress: '',
                  subject:'',
                  hasAttachment: false,
                  attachmentFileName: '选择附件',
                  attachment: '',
                  content: ''
              }
              editorComponent.setContent('')

          } else {
            that.$message({
              message: res.data,
              type: 'error'
              })
          }
          
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
.summary:hover {
  cursor: pointer;
}
.uid:hover {
  cursor: pointer;
}
.article-content{
  height: 600px;
  overflow: scroll;
}
</style>


