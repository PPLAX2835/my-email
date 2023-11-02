
/**
  收件箱
*/
<template>
  <div>
    <!-- 面包屑导航 -->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>收件箱</el-breadcrumb-item>
    </el-breadcrumb>
    <!-- 搜索筛选 -->
    <el-form :inline="true" :model="formInline" class="user-search">
      <el-form-item label="搜索：">
        <el-input size="small" v-model="formInline.keyword" placeholder="输入关键词"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button size="small" type="primary" icon="el-icon-search" @click="search">搜索</el-button>
        <el-button size="small" type="primary" icon="el-icon-plus" @click="handleEdit()">添加</el-button>
        <el-button size="small" type="danger" icon="el-icon-delete" @click="deleteArticle()">删除所选项</el-button>
      </el-form-item>
    </el-form>
    <!--列表-->
    <el-table size="small" :data="listData" highlight-current-row  @selection-change="selectChange" v-loading="loading" border element-loading-text="拼命加载中" style="width: 100%;">
      <el-table-column align="center" type="selection" width="60">
      </el-table-column>
      <el-table-column sortable prop="aid" label="文章id" width="100">
      </el-table-column>
      <el-table-column sortable prop="uid" label="作者id" width="100">
        <template slot-scope="scope">
          <div class="uid" @click="viewAuthor(scope.$index, scope.row)">{{scope.row.uid}}</div>
        </template>
      </el-table-column>
      <el-table-column sortable prop="title" label="标题" width="200">
      </el-table-column>
      <el-table-column sortable prop="summary" label="摘要" width="200">
        <template slot-scope="scope">
          <div class="summary" v-html="scope.row.summary" @click="viewDetail(scope.$index, scope.row)">
          </div>
        </template>
      </el-table-column>
      <el-table-column sortable prop="weight" label="权重" width="100">
      </el-table-column>
      <el-table-column sortable prop="status" label="状态" width="100">
      </el-table-column>
      <el-table-column sortable prop="category" label="类型" width="100">
      </el-table-column>
      <el-table-column sortable prop="views" label="浏览量" width="100">
      </el-table-column>
      <el-table-column sortable prop="likes" label="点赞数" width="100">
      </el-table-column>
      <el-table-column sortable prop="comments" label="评论数" width="100">
      </el-table-column>
      <el-table-column sortable prop="updatedAt" label="发布时间" width="200">
        <template slot-scope="scope">
          <div>{{scope.row.updatedAt|timestampToTime}}</div>
        </template>
      </el-table-column>
      <el-table-column sortable prop="publishedAt" label="修改时间" width="200">
        <template slot-scope="scope">
          <div>{{scope.row.publishedAt|timestampToTime}}</div>
        </template>
      </el-table-column>
      <el-table-column align="center" label="操作" min-width="200">
        <template slot-scope="scope">
          <el-button size="mini" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="deleteArticle(scope.$index, scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <Pagination v-bind:child-msg="pageparm" @callFather="callFather"></Pagination>
    <!-- 作者界面 -->
   <el-dialog :title="author.name" :visible.sync="authorTableVisible" width="30%" @click="closeDialog">
      <template>
        <div>
          <el-form>
            <el-form-item label="uid">
              <span>{{ author.uid }}</span>
            </el-form-item>
            <el-form-item label="地址">
              <span>{{ author.address }}</span>
            </el-form-item>
            <el-form-item label="Email">
              <span>{{ author.email }}</span>
            </el-form-item>
            <el-form-item label="性别">
              <span>{{ formatGender(author.gender) }}</span>
            </el-form-item>
            <el-form-item label="电话">
              <span>{{ author.phone }}</span>
            </el-form-item>
            <el-form-item label="最喜欢的音乐人">
              <span>{{ author.favouriteMusician }}</span>
            </el-form-item>
          </el-form>
        </div>
      </template>
      <div slot="footer" class="dialog-footer">
        <el-button size="small" @click="closeDialog">关闭</el-button>
      </div>
    </el-dialog>
    <!-- 查看详情界面 -->
    <el-dialog title="内容" :visible.sync="detailTableVisible" width="30%" @click="closeDialog">
      <div class="article-content" v-html="content"></div>
      <div slot="footer" class="dialog-footer">
        <el-button size="small" @click="closeDialog">关闭</el-button>
      </div>
    </el-dialog>
    <!-- 编辑界面 -->
    <el-dialog :title="editTitle" :visible.sync="editFormVisible" width="80%" @click="closeDialog">
      <el-form label-width="120px" :model="editForm" :rules="rules" ref="editForm">
        <el-form-item label="标题" prop="title">
          <el-input size="small" v-model="editForm.title" auto-complete="off" placeholder="请输入标题"></el-input>
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-input size="small" v-model="editForm.category" auto-complete="off" placeholder="请输入文章分类"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select size="small" v-model="editForm.status" placeholder="请选择状态">
            <el-option label="发布" value="publish"></el-option>
            <el-option label="草稿" value="draft"></el-option>
            <el-option label="删除" value="delete"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="权重" prop="weight">
          <el-slider size="small" v-model="editForm.weight"></el-slider>
        </el-form-item>
        <el-form-item label="发布时间" prop="publishedAt" v-if="isPublishAtVisible">
          <el-date-picker size="small" v-model="editForm.publishedAt" placeholder="请输入发布时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="修改时间" prop="updatedAt" v-if="!isPublishAtVisible">
          <el-date-picker size="small" v-model="editForm.updatedAt" placeholder="请输入修改时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="内容" prop="content">
            <editor ref="myEditor"></editor>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button size="small" @click="closeDialog">取消</el-button>
        <el-button size="small" type="primary" :loading="loading" class="title" @click="submitForm('editForm')">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { deptList, deptSave, deptDelete , getUser } from '../../api/userMG'
import {ArticleContent, ArticleList, ArticleSave, ArticleDelete} from '../../api/basisMG.js'
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
      author: '',
      editTitle: '添加',
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
    // 获取文章列表
    getdata(parameter) {
      this.loading = true

      /***
       * 调用接口
       */
      ArticleList(parameter)
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
          this.$message.error('菜单加载失败，请稍后再试！')
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
    // 显示作者信息
    viewAuthor: function(index, row) {
      this.authorTableVisible = true
      // 查询
      getUser(row.uid)
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
            this.author = res.data
          }
        })
        .catch(err => {
          this.loading = false
          this.$message.error('菜单加载失败，请稍后再试！')
        })

    },
    formatGender(gender) {
      return gender === 0 ? "女" : "男";
    },
    formatDate(timestamp) {
      const date = new Date(timestamp);
      return date.toLocaleString();
    },
    // 选择复选框事件
    selectChange(val) {
      this.seletedata.ids = val
    },
    // 删除文章
    deleteArticle(index, row) {
      this.$confirm('确定要删除吗?', '信息', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          if (!(index === undefined) && !(row === undefined)) {
            // 删除一个
            ArticleDelete(row.aid)
              .then(res => {
                if (res.success) {
                  this.$message({
                    type: 'success',
                    message: '文章已删除！'
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
                this.$message.error('文章删除失败，请稍后再试！')
              })
          } else {
            // 批量删除
            console.log("批量删除")
            let ids = [];
            for (let i = 0; i < this.seletedata.ids.length; i++){
              ids.push(this.seletedata.ids[i].aid);
            }
            // 调用接口
            ArticleDelete(ids)
              .then(res => {
                if (res.success) {
                  this.$message({
                    type: 'success',
                    message: '文章已删除！'
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
                this.$message.error('文章删除失败，请稍后再试！')
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
    //显示查看详情界面
    viewDetail: function(index, row) {
      this.detailTableVisible = true

      // 调用请求文章内容接口
      ArticleContent({'aid': row.aid}).then(res => {
        console.log(res)
        this.content = res.data.content
      })
    },
    //显示编辑界面
    handleEdit: function(index, row) {
      this.editFormVisible = true
      if (row != undefined && row != 'undefined') {
        this.isPublishAtVisible = false;

        this.editTitle = '修改'
        this.editForm.aid = row.aid
        this.editForm.uid = row.uid
        this.editForm.category = row.category
        this.editForm.updatedAt = row.updatedAt
        this.editForm.publishedAt = row.publishedAt
        this.editForm.status = row.status
        this.editForm.title = row.title
        this.editForm.weight = row.weight
        // 文本内容
        this.editForm.content = row.content
        const editorComponent = this.$refs.myEditor;
        this.editForm.content = editorComponent.setContent(row.content);
        this.editForm.articleNo = editorComponent.getArticleNo();

      } else {
        this.isPublishAtVisible = true;

        this.editTitle = '添加'
        this.editForm.aid = ''
        this.editForm.uid = ''
        this.editForm.category = ''
        this.editForm.updatedAt = Date.now()
        this.editForm.publishedAt = Date.now()
        this.editForm.status = ''
        this.editForm.title = ''
        this.editForm.weight = 0
        // 文本内容
        const editorComponent = this.$refs.myEditor;
        this.editForm.content = editorComponent.clearContent();
        this.editForm.articleNo = editorComponent.getArticleNo();
      }
    },
    // 编辑、增加页面保存方法
    submitForm(editData) {
      const editorComponent = this.$refs.myEditor;
      this.editForm.content = editorComponent.getContent(this.editForm.uid, this.editForm.aid);
      this.$refs[editData].validate(valid => {
        if (valid) {
          ArticleSave(this.editForm)
            .then(res => {
              this.editFormVisible = false
              this.loading = false
              if (res.success) {
                this.getdata(this.formInline)
                this.$message({
                  type: 'success',
                  message: '文章保存成功！'
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
              this.$message.error('文章保存失败，请稍后再试！')
            })
        } else {
          return false
        }
      })
    },
    // 删除文章
    deleteUser(index, row) {
      this.$confirm('确定要删除吗?', '信息', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          deptDelete(row.deptId)
            .then(res => {
              if (res.success) {
                this.$message({
                  type: 'success',
                  message: '公司已删除!'
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
              this.$message.error('公司删除失败，请稍后再试！')
            })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    },
    // 关闭编辑、增加弹出框
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


