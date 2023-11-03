import axios from 'axios';
import { req } from './axiosFun';


// 获得用户的邮箱们
export const EmailList = () => { return req("get", "/api/email/emails", {}).then(res => res.data) };
// 添加邮箱
export const addEmail = (params) => { return req("post", "/api/email/add", params).then(res => res.data) };
// 获得收件箱
export const InboxList = (params) => { return axios.get("/api/messages/inbox?emailAddress=" + params + "&token=" + localStorage.getItem('logintoken')).then(res => res.data) };
// 获得发件箱
export const SentList = (params) => { return axios.get("/api/messages/sent?emailAddress=" + params + "&token=" + localStorage.getItem('logintoken')).then(res => res.data) };
// 发送邮箱
export const sendEmail = (params) => {
    return axios.post('http://127.0.0.1:8080/api/messages/send', this.params, {headers: {'Content-Type': 'multipart/form-data'}}).then(res => res.data)
}



