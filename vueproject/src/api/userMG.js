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
