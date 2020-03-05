
import Cookie from 'js-cookie';
import { CACHE_GLOBAL } from '../constants';
import _ from 'lodash'
function getUpmsUrl() {
    return _.get(Cookie.getJSON(CACHE_GLOBAL), 'upmsUrl')
}

export function getCatList(params) {
    return request('/api/sys/category/tree', {
        method: 'get',
        data: params
    });
}

export function getArticleList(params) {
    return request('/api/cms/article/list', {
        method: 'get',
        data: params
    });
}

export function getDict(params) {
    return request('/api/sys/dict', {
        method: 'get',
        data:params,
    });
}
export function getItem(id){
    return request(`/api/cms/article/${id}`, {
        method: 'get',
    });
}

/**
 *
 * @param {number} id
 * 获取一稿多发功能中的多个渠道列表信息
 */
export function getChannelList(id) {
    return request(`/api/cms/article/publish/cids?id=${id}`, {
        method: 'get',
    });
}
export function deleteItem(id){
    return request(`/api/cms/article/${id}`, {
        method: 'delete',
    });
}
export function saveArtDraft(params) {
    return request('/api/cms/article/draft', {
        method: 'post',
        data: params
    });
}
export function saveArtChannel(params) {
    return request('/api/cms/article/publish', {
        method: 'post',
        // data: params.channelData
        data: params
    })
}
export function saveArtAudit(params){
    return request('/api/cms/article/audit', {
        method: 'post',
        data: params
    });
}
export function saveArt(params) {
    return request('/api/cms/article', {
        method: 'post',
        data: params
    });
}
export async function onOffArt(id) {
    return request(`/api/cms/article/onOff/${id}`, {
        method: 'get'
    })
}
export async function batchAuditArts(params) {
    return request(`/api/cms/article/batchAuditPublish?articleIds=${params.articleIds}`, {
        method: 'get'
    })
}
export function onOffArts(params){
    return request(`/api/cms/article/batchOnOff?articleIds=${params.articleIds}`, {
        method: 'post'
    })
}
export function removeArts(params){
    return request(`/api/cms/article/batchDelete`, {
        method: 'post',
        data: params.ids
    })
}
export function preview(id){
    return request(`/api/cms/article/preview/${id}`, {
        method: 'get'
    })
}
export function updateSorts(params){
    return request('/api/cms/article/batchSort', {
        method: 'post',
        data: params
    });
}

/**
 *
 * @param {array} params
 * 稿源库选稿，选中文章插入文章列表里
 */
export function insertArts(params) {
    return request(`/api/cms/article/source/import?categoryId=${params.categoryId}`, {
        method: 'post',
        data: params.list
    })
}
