import request from '@/utils/request'

// 查询基带参数定义列表
export function listBasebandParamDef(query) {
  return request({
    url: '/system/baseband/param/list',
    method: 'get',
    params: query
  })
}

// 查询基带参数定义详细
export function getBasebandParamDef(paramId) {
  return request({
    url: '/system/baseband/param/' + paramId,
    method: 'get'
  })
}

// 新增基带参数定义
export function addBasebandParamDef(data) {
  return request({
    url: '/system/baseband/param',
    method: 'post',
    data: data
  })
}

// 修改基带参数定义
export function updateBasebandParamDef(data) {
  return request({
    url: '/system/baseband/param',
    method: 'put',
    data: data
  })
}

// 删除基带参数定义
export function delBasebandParamDef(paramId) {
  return request({
    url: '/system/baseband/param/' + paramId,
    method: 'delete'
  })
}

// 导出基带参数定义
export function exportBasebandParamDef(query) {
  return request({
    url: '/system/baseband/param/export',
    method: 'post',
    params: query
  })
}

// 批量更新参数排序
export function batchUpdateSortOrder(data) {
  return request({
    url: '/system/baseband/param/sort',
    method: 'put',
    data: data
  })
}
