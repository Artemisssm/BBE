import request from '@/utils/request'

// 查询基带单元列表
export function listBasebandUnit(query) {
  return request({
    url: '/system/baseband/unit/list',
    method: 'get',
    params: query
  })
}

// 查询基带单元详细
export function getBasebandUnit(unitId) {
  return request({
    url: '/system/baseband/unit/' + unitId,
    method: 'get'
  })
}

// 新增基带单元
export function addBasebandUnit(data) {
  return request({
    url: '/system/baseband/unit',
    method: 'post',
    data: data
  })
}

// 修改基带单元
export function updateBasebandUnit(data) {
  return request({
    url: '/system/baseband/unit',
    method: 'put',
    data: data
  })
}

// 删除基带单元
export function delBasebandUnit(unitId) {
  return request({
    url: '/system/baseband/unit/' + unitId,
    method: 'delete'
  })
}

// 导出基带单元
export function exportBasebandUnit(query) {
  return request({
    url: '/system/baseband/unit/export',
    method: 'post',
    params: query
  })
}
