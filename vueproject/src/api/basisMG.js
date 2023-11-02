import axios from 'axios';
import { req } from './axiosFun';


// 获得用户的邮箱们
export const EmailList = (params) => { return req("get", "/api/email/emails", {}).then(res => res.data) };
// 添加邮箱
export const addEmail = (params) => { return req("post", "/api/email/add", params).then(res => res.data) };


/**
 * 文章管理
 */
// 获取文章管理列表
export const ArticleList = (params) => { return req("post", "/api/Article/list", params) };
// 保存文章
export const ArticleSave = (params) => { return req("post", "/api/Article/save", params) };
// 获取文章对应的content
export const ArticleContent = (params) => { return axios.get("/api/Article/content?aid=" + params.aid + "&token=" + localStorage.getItem('logintoken')).then(res => res.data) };
// 删除文章
export const ArticleDelete = (params) => { return axios.delete("/api/Article/delete?ids=" + params + "&token=" + localStorage.getItem('logintoken')).then(res => res.data) };

/**
 * 文章分类管理
 */
// 获取分类管理管理列表
export const CategoryList = (params) => { return req("post", "/api/Category/list", params) };
// 获取父级分类
export const CategoryParent = (params) => { return axios.get("/api/Category/parent?id=" + params + "&token=" + localStorage.getItem('logintoken')).then(res => res.data) };
// 获取通过名字获取分类
export const getCategoryByName = (params) => { return axios.get("/api/Category/getCategory?name=" + params + "&token=" + localStorage.getItem('logintoken')).then(res => res.data) };
// 保存分类
export const CategorySave = (params) => { return req("post", "/api/Category/save", params) };
// 删除文章
export const CategoryDelete = (params) => { return axios.delete("/api/Category/delete?ids=" + params + "&token=" + localStorage.getItem('logintoken')).then(res => res.data) };


