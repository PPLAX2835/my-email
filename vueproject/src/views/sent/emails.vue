
/**
  收件箱
*/
<template>
  <div>
    <!-- 面包屑导航 -->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>发件箱</el-breadcrumb-item>
    </el-breadcrumb>
    <!--列表-->
    <el-table size="small" :data="listData" highlight-current-row  v-loading="loading" border element-loading-text="拼命加载中" style="width: 100%;">
      <el-table-column sortable prop="senderEmailAddress" label="发件人" width="400">
      </el-table-column>
      <el-table-column sortable prop="subject" label="标题" width="500">
      </el-table-column>
      <el-table-column sortable prop="sentDate" label="接收时间" width="150">
      </el-table-column>
      
      <el-table-column align="center" label="操作" min-width="100">
        <template slot-scope="scope">
          <el-button size="mini" @click="viewDetail(scope.$index, scope.row)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 查看详情界面 -->
    <el-dialog :title="title" :visible.sync="detailTableVisible" width="80%" @click="closeDialog">
      <div class="article-content" v-html="content"></div>
      <div slot="footer" class="dialog-footer">
        <el-button v-if="hasAttachment" type="primary" size="small" @click="downLoadAttachment">下载附件：{{ attachmentFileName }}</el-button>
        <el-button size="small" @click="closeDialog">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {SentList, EmailList} from '../../api/basisMG.js'
import Pagination from '../../components/Pagination'
import editor from '../../components/RichTextEditor.vue'

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
      attachmentFileName: '',
      hasAttachment: false,
      editForm: {
        aid: '',
        uid: '',
        category: '',
        content: '',
        updatedAt: 0,
        publishedAt: 0,
        status: '',
        title: '',
        weight: '',
        articleNo: '',
        token: localStorage.getItem('logintoken')
      },
      getInboxForm: {
        emailAddress: ''
      },
      // rules表单验证
      rules: {
        title: [
          { required: true, message: '请输入标题', trigger: 'blur' }
        ],
        category: [
          { required: true, message: '请输入分类', trigger: 'blur' }
        ],
        status: [
          { required: true, message: '请选择状态', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入内容', trigger: 'blur' }
        ],
      },
      formInline: {
        page: 1,
        limit: 10,
        keyword: '',
        token: localStorage.getItem('logintoken')
      },
      // 删除参数
      seletedata: {
        ids: [],
        token: localStorage.getItem('logintoken')
      },
      userparm: [], //搜索权限
      listData: [], //用户数据
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
    this.getdata(this.formInline)
  },

  /**
   * 里面的方法只有被调用才会执行
   */
  methods: {
    // 获取数据列表
    getdata(parameter) {
      this.loading = true


      if (this.$globle.EMAILS == 0) {
        /**
         * 获取用户邮箱
         */
        EmailList().then(res => {
          if (res.code == 200) {
            // 放到全局变量
            this.$globle.setEmails(res.data)
            
            
            SentList(this.$globle.EMAILS[this.$globle.CURRENT_EMAIL_INDEX].emailAddress)
              .then(res => {
                this.loading = false
                if (res.code == 200) {
                  this.listData = res.data
                  // 分页赋值
                  this.pageparm.currentPage = this.formInline.page
                  this.pageparm.pageSize = this.formInline.limit
                  this.pageparm.total = res.count
                } else {
                  this.$message({
                    type: 'info',
                    message: res.message
                  })
                }
              })
              .catch(err => {
                this.loading = false
                this.$message.error('加载失败，请稍后再试！')
              })

          } else {
            this.user = res.data
          }
        })
      } else {
        SentList(this.$globle.EMAILS[this.$globle.CURRENT_EMAIL_INDEX].emailAddress)
          .then(res => {
            this.loading = false
            if (res.code == 200) {
              this.listData = res.data
              // 分页赋值
              this.pageparm.currentPage = this.formInline.page
              this.pageparm.pageSize = this.formInline.limit
              this.pageparm.total = res.count
            } else {
              this.$message({
                type: 'info',
                message: res.message
              })
            }
          })
          .catch(err => {
            this.loading = false
            this.$message.error('加载失败，请稍后再试！')
          })
      }
    },
    
    //显示查看详情界面
    viewDetail: function(index, row) {

      this.attachmentFileName = row .attachmentFileName,
      this.hasAttachment = row.hasAttachment,

      this.detailTableVisible = true
      this.content = row.text
      this.title = row.subject
    },
    
    // 下载附件
    downLoadAttachment: function() {
      window.open(
        '/api/messages/attachment?fileName=' + this.attachmentFileName,
        '_self'
      );
    },
    
    // 关闭弹出框
    closeDialog() {
      this.detailTableVisible = false
      this.editFormVisible = false
      this.authorTableVisible = false
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


