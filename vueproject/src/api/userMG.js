import axios from 'axios';
import { loginRequest, commonRequest } from './axiosFun';

// 登录接口
export const login = (params) => { return loginRequest("post", "/api/auth/login", params) };
// 退出接口
// export const logout = ()
export const loginout = () =>  { return commonRequest("delete", "/api/auth/logout", {}).then(res => res.data) };
// 获取用户菜单
export const menu = (params) => { return commonRequest("get", "/api/menus", {}).then(res => res.data) };

// 用户管理-获取单个用户
export const getSelf = () => { return commonRequest("get", "/api/user/self", {}).then(res => res.data) };
