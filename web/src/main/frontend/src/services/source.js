
/**
 *
 * @param {*object} params
 * 获取稿源库文章列表
 */
export function getArticleList(params) {
  // return request('/api/cms/article/list', {
  return request('/api/cms/article/source', {
      method: 'get',
      data: params
  });
}

/**
 *
 * @param {*object} params
 * 内容管理-稿源库-手动新建稿源库文章编辑后保存
 */
export function saveArticleContent(params) {
  return request('/api/cms/article/source', {
    method: 'post',
    data: params
  })
}

/**
 *
 * @param {*number} id
 *
 */
export function deleteItem(id){
  return request(`/api/cms/article/${id}`, {
      method: 'delete',
  });
}

/**
 * 批量删除
 * @param {object} params
 */
export function removeArts(params){
  return request(`/api/cms/article/batchDeleteSource`, {
      method: 'post',
      data: params.ids
  })
}

/**
 *
 * @param {*number} id
 * 点击编辑进入文章详情页，获取文章详情数据
 */
export function getItem(id){
  return request(`/api/cms/article/${id}`, {
      method: 'get',
  });
}