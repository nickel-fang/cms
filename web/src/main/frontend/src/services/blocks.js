export function getCatList(params) {
    return request('/api/sys/category/tree', {
        method: 'get',
        data: params
    });
}

export function getTemplateItem(id) {
    return request(`/api/templates/${id}`, {
        method: 'get'
    })
}

export function getBlockItem(params) {
    return request(`/api/cms/block/relation/list`, {
        method: 'get',
        data: params
    })
}

export function getArticleList(params) {
    return request('/api/cms/article/list', {
        method: 'get',
        data: params
    });
}

export function saveTxt(params){
    return request('/api/cms/block/relation/input',{
        method:'post',
        data:params
    })
}

export function saveArts(params){
    return request('/api/cms/block/relation/article', {
        method: 'post',
        data: params
    })
}

export function saveCatg(params) {
    return request('/api/cms/block/relation/menu', {
        method: 'post',
        data: params
    })
}

export function saveImage(params) {
    return request('/api/cms/block/relation/images', {
        method: 'post',
        data: params
    })
}

export function deleteBlk(params){
    return request(`/api/cms/block/relation/del`, {
        method: 'post',
        data:params
    })
}

export function previewPage(params){
    return request(`/api/cms/block/relation/preview`, {
        method: 'get',
        data: params
    })
}

export function editBlockItem(params) {
    return request('/api/cms/block/relation', {
        method: 'post',
        data: params
    })
}

export function batchOnOff(params){
    return request('/api/cms/block/relation/batchOnOff', {
        method: 'post',
        data: params
    })
}

export function batchDelete(params){
    return request('/api/cms/block/relation/batchDelete', {
        method: 'post',
        data: params
    })
}

export function changeOnOff(params){
    return request('/api/cms/block/relation/status', {
        method: 'get',
        data: params
    })
}

export function updateSorts(params) {
    return request('/api/cms/block/relation/batchSort', {
        method: 'post',
        data: params
    });
}

export function refreshAll(params){
    return request('/api/cms/block/relation/details', {
        method: 'get',
        data: params
    });
}
