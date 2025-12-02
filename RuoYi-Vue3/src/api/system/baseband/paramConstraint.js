import request from '@/utils/request'

// 查询参数约束列表
export function listParamConstraint(query) {
  return request({
    url: '/system/paramConstraint/list',
    method: 'get',
    params: query
  })
}

// 查询参数约束详细
export function getParamConstraint(constraintId) {
  return request({
    url: '/system/paramConstraint/' + constraintId,
    method: 'get'
  })
}

// 新增参数约束
export function addParamConstraint(data) {
  return request({
    url: '/system/paramConstraint',
    method: 'post',
    data: data
  })
}

// 修改参数约束
export function updateParamConstraint(data) {
  return request({
    url: '/system/paramConstraint',
    method: 'put',
    data: data
  })
}

// 删除参数约束
export function delParamConstraint(constraintIds) {
  return request({
    url: '/system/paramConstraint/' + constraintIds,
    method: 'delete'
  })
}

// 验证参数约束
export function validateParamConstraint(data) {
  return request({
    url: '/system/paramConstraint/validate',
    method: 'post',
    data: data
  })
}

// 获取受影响的参数列表
export function getAffectedParams(unitName, unitType, modeType, paramName) {
  return request({
    url: `/system/paramConstraint/affected/${unitName}/${unitType}/${modeType}/${paramName}`,
    method: 'get'
  })
}

// 获取单元的所有约束
export function getAllConstraintsForUnit(unitName, unitType, modeType) {
  return request({
    url: `/system/paramConstraint/unit/${unitName}/${unitType}/${modeType}`,
    method: 'get'
  })
}
