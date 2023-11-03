<template>
  <div class="editor-page">
    <!-- 图片上传组件辅助-->
    <!-- <el-upload
      class="avatar-uploader"
      :action="serverUrl"
      name="file"
      :data="uploadData"
      :headers="header"
      :show-file-list="false"
      :on-success="uploadSuccess"
      :on-error="uploadError"
      :before-upload="beforeUpload"
    ></el-upload> -->

    <quill-editor
      class="editor"
      v-model="content"
      :disabled="disabled"
      ref="myQuillEditor"
      :options="editorOption"
      @blur="onEditorBlur($event)"
      @focus="onEditorFocus($event)"
      @change="onEditorChange($event)"
    ></quill-editor>
  </div>
</template>
<script>
// 工具栏配置


const toolbarOptions = [
  ["bold", "italic", "underline", "strike"], // 加粗 斜体 下划线 删除线
  [{ list: "ordered" }, { list: "bullet" }], // 有序、无序列表
  // [{ script: "sub" }, { script: "super" }], // 上标/下标
  [{ indent: "-1" }, { indent: "+1" }], // 缩进
  [{ size: ["small", false, "large", "huge"] }], // 字体大小
  [{ header: [1, 2, 3, 4, 5, 6, false] }], // 标题
  [{ color: [] }, { background: [] }], // 字体颜色、字体背景颜色
  [{ font: [] }], // 字体种类
  [{ align: [] }], // 对齐方式
  ["clean"], // 清除文本格式
  // ['link', 'image'], // 链接、图片
];

import { quillEditor } from "vue-quill-editor";
import "quill/dist/quill.core.css";
import "quill/dist/quill.snow.css";
import "quill/dist/quill.bubble.css";
// 自定义插入a链接
import { Quill } from 'vue-quill-editor'
let Link = Quill.import('formats/link')
class FileBlot extends Link {
  // 继承Link Blot
  static create(value) {
    let node = undefined
    if (value && !value.href) {
      // 适应原本的Link Blot
      node = super.create(value)
    } else {
      // 自定义Link Blot
      node = super.create(value.href)
      // node.setAttribute('download', value.innerText);  // 左键点击即下载
      node.innerText = value.innerText
      node.download = value.innerText
    }
    return node
  }
}
FileBlot.blotName = 'link'
FileBlot.tagName = 'A'
Quill.register(FileBlot)


export default {
  props: {
    /*编辑器的内容*/
    value: {
      type: String,
    },
    disabled: {
      type: Boolean,
    },
    /*图片大小*/
    maxSize: {
      type: Number,
      default: 4000, //kb
    },
  },
  components: {
    quillEditor,
  },
  watch: {
    value(newValue) {
      this.content = newValue
    },
  },
  data() {
    return {
      uploadData: {
        articleNo: '',
        imgId: 0
      },
      content: this.value,
      previewShow: false,  //预览弹框
      quillUpdateImg: false, // 根据图片上传状态来确定是否显示loading动画，刚开始是false,不显示
      TiLength: 0, //富文本框里的文字长度
      editorOption: {
        theme: "snow", // or 'bubble'
        placeholder: "请输入文本内容",
        modules: {
          toolbar: {
            container: toolbarOptions,
            // container: "#toolbar",
            handlers: {
              image: function (value) {
                if (value) {
                  // 触发input框选择图片文件
                  document.querySelector(".avatar-uploader input").click()
                } else {
                  this.quill.format("image", false);
                }
              },
              link: function(value) {
                if(value)
                  document.querySelector(".editor-display-button").click()
                else
                  this.quill.format("link", false)
              }
            },
          },
        },
      },
      serverUrl: "", // 这里写你要上传的图片服务器地址
      header: {
        Authorization: localStorage.getItem('logintoken')
      }, // 有的图片服务器要求请求头需要有token
    };
  },
  mounted() {
    this.TiLength =this.$refs.myQuillEditor.quill.getLength() - 1
    this.content = this.value
    this.uploadData.articleNo = this.generateRandomString(20);
    this.serverUrl = "/api/file/saveImage"
  },
  methods: {
    // 生成文章代码
    generateRandomString(length) {
      let result = '';
      const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
      const charactersLength = characters.length;
      for (let i = 0; i < length; i++) {
        result += characters.charAt(Math.floor(Math.random() * charactersLength));
      }
      return result;
    },
    // 获得文本代码
    getArticleNo () {
      return this.uploadData.articleNo;
    },
    // 获得富文本内容
    getContent() {
      // HTML 文本
      return this.content;
    },
    // 添加文本内容
    setContent(content) {
      this.content = content
    },
    // 清除文本内容
    clearContent() {
      this.content = ''
    },
    onEditorBlur() {
      //失去焦点事件
    },
    onEditorFocus() {
      //获得焦点事件
    },
    onEditorChange(event) {
      event.quill.deleteText(2000,1);
      if(this.content === ''){
        this.TiLength = 0
      }
      else{
        this.TiLength = event.quill.getLength()-1
      }
      this.$emit('change', event.html)
    },
    // 富文本图片上传前
    beforeUpload(file) {
      // 显示loading动画
      this.quillUpdateImg = true;
    },
    // 图片上传
    uploadSuccess(res, file) {
      // res为图片服务器返回的数据
      // 获取富文本组件实例
      let quill = this.$refs.myQuillEditor.quill;
      // 如果上传成功
      if (res.code === 200) {
        // 获取光标所在位置
        let length = quill.getSelection().index;
        // 插入图片  res.url为服务器返回的图片地址
        quill.insertEmbed(length, "image", res.data);
        // 调整光标到最后
        quill.setSelection(length + 1);
        // 图片数加一
        this.uploadData.imgId += parseInt(Math.random()*1000);
      } else {
        this.$message.error("图片插入失败");
      }
      // loading动画消失
      this.quillUpdateImg = false;
    },
    // 富文本图片上传失败
    uploadError() {
      // loading动画消失
      this.quillUpdateImg = false;
      this.$message.error("上传失败");
    },
    previewArticle() {
      console.log(121212);
    },
    handleCancel() {
      this.previewShow = false
    }
  },
};
</script>

<style scoped>
.editor-page{
  height: calc(100%);
}
.editor {
  line-height: normal !important;
  height: calc(100%);
}
.avatar-uploader{
  display: none;
}
.editor-counter{
  padding-right: 20px;
}
</style>
