import axios from 'axios';
import { loginreq, req } from './axiosFun';

// 登录接口
export const login = (params) => { return loginreq("post", "/api/auth/login", params) };
// 退出接口
// export const logout = ()
export const loginout = () =>  { return req("delete", "/api/auth/logout", {}).then(res => res.data) };
// 获取用户菜单
export const menu = (params) => { return req("get", "/api/menus", {}).then(res => res.data) };


// 用户管理-获取单个用户
export const getSelf = () => { return req("get", "/api/user/self", {}).then(res => res.data) };

/**
 * 用户管理
 **/
// 用户管理-获取用户列表
export const userList = (params) => { return req("post", "/api/User/list", params) };
// 用户管理-获取单个用户
export const getUser = (params) => { return axios.get("/api/User/getuser?uid=" + params + "&token=" + localStorage.getItem('logintoken')).then(res => res.data) };
// // 用户管理-获取单个用 = 户
// export const getSelf() => { return axios.get("/api/User/getSelf?token=" + localStorage.getItem('logintoken')).then(res => res.data) };
// 用户管理-保存（添加编辑）
export const userSave = (params) => { return req("post", "/api/User/save", params) };
// 用户管理-删除用户
export const userDelete = (params) => { return axios.delete("/api/User/delete?ids=" + params + "&token=" + localStorage.getItem('logintoken')).then(res => res.data) };
// 用户管理-重置密码
export const userPwd = (params) => { return req("post", "/api/User/pwd", params) };
// 用户管理-修改状态
export const userLock = (params) => { return axios.get("/api/User/lock?uid=" + params.uid + "&lock=" + params.lock + "&token=" + localStorage.getItem('logintoken')) };
// 用户管理-数据权限
export const UserDeptTree = (params) => { return axios.get("/api/UserDept/tree/" + params + "?token=" + localStorage.getItem('logintoken')) };
// 用户管理-数据权限保存
export const UserDeptSave = (params) => { return req("post", "/api/UserDept/save", params) };
// 用户管理-用户下线
export const userExpireToken = (params) => { return req("get", "/api/User/expireToken?uid=" + params.uid + "&token=" + params.token, {}) };
// 用户管理-刷新缓存
export const userFlashCache = (params) => { return req("get", "/api/User/flashCache/" + params, {}) };


/**
 * 角色管理
 **/
// 角色管理-获取角色列表
export const roleList = (params) => { return req("post", "/api/Role/list", params) };
// 角色管理-保存（添加编辑）
export const roleSave = (params) => { return req("post", "/api/Role/save", params) };
// 角色管理-删除角色
export const roleDelete = (params) => { return axios.delete("/api/Role/delete?ids=" + params + "&token=" + localStorage.getItem('logintoken')).then(res => res.data) };
// 角色管理-菜单权限（获取）
export const RoleRightTree = (params) => { return axios.get("/api/RoleRight/tree/" + params + "?token=" + localStorage.getItem('logintoken')) };
// 角色管理-菜单权限（保存）
export const RoleRightSave = (params) => { return req("post", "/api/RoleRight/save", params) };
