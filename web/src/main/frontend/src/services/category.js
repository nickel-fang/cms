export function getCatList(params) {
    return request('/api/sys/category/tree', {
        method: 'get',
        data: params
    });
}

export function getCatItem(id) {
    return request(`/api/sys/category/${id}`, {
        method: 'get'
    });
}

export function getBlogItem() {
    return request(`/api/sys/category/getBBSInfo`, {
        method: 'get'
    });
}
export async function changeCatg(params) {
    return request('/api/sys/category', {
        method: 'post',
        data: params
    })
}

export async function removeCatg(id) {
    return request(`/api/sys/category/${id}`, {
        method: 'delete'
    })
}

export async function updateSorts(params) {
    return request('/api/sys/category/batchSort', {
        method: 'post',
        data: params
    });
}
export function preview(params) {
    return request(`/api/sys/category/preview/list`, {
        method: 'get',
        data: params
    })
}
export function templateList(params) {
    return request('/api/templates/list', {
        method: 'get',
        data: params
    });
}


export function findTempByName(params) {
    return request('/api/templates/findTempByName', {
        method: 'get',
        data: params
    })
}