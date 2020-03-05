import Cookie from 'js-cookie';
import { CACHE_GLOBAL } from '../constants';
import _ from 'lodash';
function getUpmsUrl() {
    return _.get(Cookie.getJSON(CACHE_GLOBAL), 'upmsUrl')
}
// 获取当前用户
export function queryUser(params) {
    return request('/auth/current', {
        method: 'get',
        data: params
    });
}

export function querySystems() {
    return request('/api/sys/system', {
        method: 'get',
    });
}

export function queryMenuView(params) {
    return request('/api/sys/menu/treeView', {
        method: 'get',
        //data: params,
    });
}

export function getSiteList() {
    return request('/api/cms/site/all', {
        method: 'get',
    });
}

export function queryMediaList(params){
    return request('/api/file/media/info/', {
        method: 'get',
        data: params
    })
}

export function login(params){
    return request('/auth/login', {
        method: 'post',
        data: params
    });
}
