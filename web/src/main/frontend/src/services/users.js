export async function createRole(params) {
    return request('/api/users/role/add', {
        method: 'post',
        data: params
    });
}

export async function deleteRole(params) {
    return request(`/api/users/role/${params.id}`, {
        method: 'delete'
    });
}

export async function updateRole(params) {
    return request('/api/users/role/update', {
        method: 'patch',
        data: params
    });
}

export async function queryRole(params) {
    return request(`/api/users/role/${params.id}`, {
        method: 'get'
    });
}

export async function queryRoles(params) {                  
    return request('/api/users/role/list', {
        method: 'get',
        data: params
    });
}

export async function queryAllRoles(params) {                  
    return request('/api/users/role/listAll', {
        method: 'get',
        data: params
    });
}

export async function querySites(params) {
    return request('/api/cms/site/all', {
        method: 'get',
        data: params,
        //baseURL: getCmsUrl()
    });
}

export async function queryCategory(params) {
    return request('/api/sys/category/tree', {
        method: 'get',
        data: params,
        //baseURL: getCmsUrl()
    });
}

export async function queryMenuTree(params) {
    return request(`/api/sys/menu/tree`, {
        method: 'get',
        data: params
    });
}

export async function queryUsers(params) {
    return request(`/api/users/permission/list`, {
        method: 'get',
        data: params
    });
}

export async function saveUserPermission(params) {
    return request('/api/users/permission/update', {
        method: 'patch',
        data: params
    });
}

export async function queryUserPermission(params) {
    return request(`/api/users/permission/${params.id}`, {
        method: 'get'
    });
}