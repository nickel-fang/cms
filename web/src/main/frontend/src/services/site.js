export function getSiteList(params) {
    return request('/api/cms/site', {
        method: 'get',
        data: params
    });
}

export function saveSite(params) {
    return request('/api/cms/site', {
        method: 'post',
        data: params
    });
}

export function getSiteItem(id) {
    return request(`/api/cms/site/${id}`, {
        method: 'get',
    });
}

export function deleteItem(id) {
    return request(`/api/cms/site/${id}`, {
        method: 'delete',
    });
}
