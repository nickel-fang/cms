export function templateList(params) {
    return request('/api/templates/list', {
        method: 'get',
        data: params
    });
}

export function saveTemplate(params) {
    return request('/api/templates', {
        method: 'post',
        data: params
    });
}

export function getTemplateItem(id){
    return request(`/api/templates/${id}`, {
        method: 'get'
    })
}
export function deleteBlockItem(id){
    return request(`/api/cms/block/${id}`,{
        method:'delete'
    })
}
export function deleteItem(id) {
    return request(`/api/templates/${id}`, {
        method: 'delete'
    })
}
