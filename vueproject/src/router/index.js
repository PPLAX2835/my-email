// 导入组件
import Vue from 'vue';
import Router from 'vue-router';
// 登录
import login from '@/views/login';
// 首页
import index from '@/views/index';


import emails from '@/views/receive/emails'


// 启用路由
Vue.use(Router);

// 导出路由
export default new Router({
    routes: [{
        path: '/',
        name: '',
        component: login,
        hidden: true,
        meta: {
            requireAuth: false
        }
    }, {
        path: '/login',
        name: '登录',
        component: login,
        hidden: true,
        meta: {
            requireAuth: false
        }
    }, {
        path: '/index',
        name: '首页',
        component: index,
        iconCls: 'el-icon-tickets',
        children: [{
            path: '/receive/emails',
            name: '收件箱',
            component: emails,
            meta: {
                requireAuth: true
            }
        }, {
            path: '/receive/emails',
            name: '收件箱',
            component: emails,
            meta: {
                requireAuth: true
            }
        }]
    }]
})
