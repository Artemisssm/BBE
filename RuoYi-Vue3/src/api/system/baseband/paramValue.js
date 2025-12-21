import request from '@/utils/request'

// 查询基带参数值列表（根据单元ID）
export function listBasebandParamValue(unitId) {
  return request({
    url: '/system/baseband/value/' + unitId,
    method: 'get'
  })
}

// 保存基带参数值配置
export function saveBasebandParamValue(unitId, data) {
  return request({
    url: '/system/baseband/value/' + unitId,
    method: 'put',
    data: data
  })
}

// 下发基带参数
export function dispatchBasebandParam(unitId) {
  return request({
    url: '/system/baseband/value/' + unitId + '/dispatch',
    method: 'post'
  })
}

// 智能生成符合约束的参数值
export function generateSmartValues(unitId) {
  return request({
    url: '/system/baseband/value/' + unitId + '/smart-values',
    method: 'get'
  })
}
