export async function queryMenuTree(params) {
    return request(`/api/sys/menu/tree`, {
        method: 'get',
        data: params
    });
}

export async function querySystems(params) {
    return request('/api/sys/system', {
        method: 'get'
    });
}

export async function deleteMenu(params) {
    return request(`/api/sys/menu/${params.id}`, {
        method: 'delete'
    });
}

export async function createMenu(params) {
    return request('/api/sys/menu', {
        method: 'post',
        data: params
    });
}

export async function updateMenu(params) {
    return request(`/api/sys/menu`, {
        method: 'patch',
        data: params
    })
}

export async function queryMenu(id) {
    return request(`/api/sys/menu/${id}`, {
        method: 'get'
    });
}

export async function updateSorts(params) {
    return request('/api/sys/menu/batchSort', {
        method: 'patch',
        data: params
    });
}
