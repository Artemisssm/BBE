import request from '@/utils/request'

// 查询宏列表
export function listMacro(query) {
  return request({
    url: '/system/basebandMacro/list',
    method: 'get',
    params: query
  })
}

// 查询宏详细
export function getMacro(macroId) {
  return request({
    url: '/system/basebandMacro/' + macroId,
    method: 'get'
  })
}

// 新增宏
export function addMacro(data) {
  return request({
    url: '/system/basebandMacro',
    method: 'post',
    data: data
  })
}

// 修改宏
export function updateMacro(data) {
  return request({
    url: '/system/basebandMacro',
    method: 'put',
    data: data
  })
}

// 删除宏
export function delMacro(macroId) {
  return request({
    url: '/system/basebandMacro/' + macroId,
    method: 'delete'
  })
}

// 导出宏
export function exportMacro(query) {
  return request({
    url: '/system/basebandMacro/export',
    method: 'post',
    params: query
  })
}

// 获取可用宏列表
export function getAvailableMacros(unitType, modeType) {
  return request({
    url: '/system/basebandMacro/available',
    method: 'get',
    params: {
      unitType,
      modeType
    }
  })
}

// 获取宏的参数配置
export function getMacroParams(macroId) {
  return request({
    url: `/system/basebandMacro/${macroId}/params`,
    method: 'get'
  })
}

// 保存宏的参数配置
export function saveMacroParams(macroId, params) {
  return request({
    url: `/system/basebandMacro/${macroId}/params`,
    method: 'post',
    data: params
  })
}
