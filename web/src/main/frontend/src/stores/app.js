import { observable, action, extendObservable, runInAction } from 'mobx';
import {message} from 'antd';
import { CACHE_USER, CACHE_SYSTEMS, CACHE_MENUS, CACHE_GLOBAL, NAME, LOGO_URL, INDEX_URL} from '../constants';
import Cookie from 'js-cookie';
import { Jt, localCache, sessionCache } from '../utils';
import _ from 'lodash';
import * as api from '../services/app';

const defaults = {
    user:{},
    systems:[],
    sites:[],
    selectSiteId:null,
    menus:[],
    topKeys:[],
    loading: false,
    mediaList:[],
    mediaListPagination: {
        showQuickJumper: true,
        showTotal: total => `共 ${total} 条`,
        current: 1,
        total: null,
        pageSize: 10
    }
}

class Layout {
    constructor() {
        extendObservable(this, {
            ...defaults
        });
    }
    @action.bound async getMediaList(params){
        await api.queryMediaList(params).then((res) => {
            const { code, data = [] } = res;
            if (code == 0) {
                const mediaListPagination = this.mediaListPagination;
                this.updateStore({
                    mediaList: data.list || [],
                    mediaListPagination: {
                        ...mediaListPagination,
                        total: _.get(data, 'pager.recordCount'),
                        current: _.get(data, 'pager.pageNumber'),
                        pageSize: _.get(data, 'pager.pageSize')
                    }
                })
            }
        })
    }
    @action.bound async querySystems(){
        let systems = sessionCache.get(CACHE_SYSTEMS);
        if (_.isEmpty(systems)) {
            await api.querySystems().then((res)=>{
                const { code, data = [] } = res;
                if (code === 0) {
                    systems = data;
                    // 缓存系统数据
                    sessionCache.set(CACHE_SYSTEMS, systems);
                }
            })
        }
        this.systems = systems;
    }
    @action.bound async queryMenus(systemId){
        let menus = sessionCache.get(CACHE_MENUS) || [];
        if (_.isEmpty(menus)) {
            await api.queryMenuView({systemId}).then((res)=>{
                const { code, data = [] } = res;
                if (code === 0) {
                    menus = data;
                    // 缓存菜单数据
                    sessionCache.set(CACHE_MENUS, menus);
                }
            })
        }
        runInAction(()=>{
            this.menus = menus;
            this.topKeys = menus.map(item => item.code);
        })
    }
    @action.bound async JumpToFirstMenu(systemId) {
        let menus = sessionCache.get(CACHE_MENUS) || [];
        if (_.isEmpty(menus)) {
            await api.queryMenuView({ systemId }).then((res) => {
                const { code, data = [] } = res;
                if (code === 0) {
                    menus = data;
                    // 缓存菜单数据
                    sessionCache.set(CACHE_MENUS, menus);
                }
            })
        }
        runInAction(() => {
            this.menus = menus;
            this.topKeys = menus.map(item => item.code);
        })
        location.hash = Jt.tree.getFirstUrl(menus);
    }
    @action.bound async querySites(){
        await api.getSiteList().then((res) => {
            const { code, data = [] } = res;
            if (code === 0) {
                this.sites = data||[];
            }
        })
    }
    @action.bound async saveSites() {
        await api.getSiteList().then((res) => {
            const { code, data = [] } = res;
            if (code === 0) {
                this.sites = data || [];
            }
        })
        if (!this.selectSiteId && this.sites.length ){
            this.selectSiteId = this.sites[0].id;
        }
    }
    @action.bound async deleteSites(){
        await this.querySites();
        if (!_.find(this.sites, { id: this.selectSiteId})){
            if(!!this.sites.length){
                this.selectSiteId = this.sites[0].id;
            }else{
                this.selectSiteId = null;
                message.error('系统无站点，请添加站点')
            }
        }
    }
    @action.bound async getUser(){
        this.loading = true;
        let user = sessionCache.get(CACHE_USER) || {};
        const cookie = Cookie.getJSON(CACHE_GLOBAL) || {};
        if (!cookie.token || !user.id) {
            if (location.href.indexOf('#/login') === -1) {
                this.loading = false;
                Jt.toLogin();
                return
            }
            this.loading = false;
            return;
        }
        const { name, subtitle } = user.isystem || {};
        if (name) {
            document.title = name + (subtitle ? `-${subtitle}` : '');
        }
        this.user = user;
        this.queryMenus(user.isystem.id);
        await this.querySites();
        if (!!this.sites.length && this.selectSiteId===null) {   // 设置初始化选中站点
            this.selectSiteId = this.sites[0].id;
        }
        this.loading = false;
    }
    @action.bound loginHandle(values, redirectUrl, cb){
        api.login(values).then((res) => {
            const { code, data } = res;
            if (code === 0) {
                const baseUrl = _.get(data, 'isystem.baseUrl');
                const cookie = _.pick(data, ['id', 'name', 'username', 'token']);
                // 设置全局缓存
                Cookie.set(CACHE_GLOBAL, cookie);
                // 缓存用户数据
                sessionCache.set(CACHE_USER, data);
                this.user = data;
                // if (redirectUrl) {     //因三元改造改变登录跳转路径逻辑
                //     location.href = redirectUrl;
                // } else {
                //     this.JumpToFirstMenu(data.isystem.id);
                // }
                this.JumpToFirstMenu(data.isystem.id);
            }else{
                const {msg} = res;
                cb();
                message.error(msg||'用户名或密码错误');
            }
        });
    }
    @action.bound logout(cb) {
        const cookie = Cookie.getJSON(CACHE_GLOBAL);
        Cookie.remove(CACHE_GLOBAL);
        sessionCache.delete(CACHE_USER);
        sessionCache.delete(CACHE_MENUS);
        cb && cb();
        Jt.toLogin();
    }
    @action.bound updateStore(data) {
        Object.assign(this, data);
    }
}

export default Layout;
