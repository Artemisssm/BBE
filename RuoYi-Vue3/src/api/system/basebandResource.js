import request from '@/utils/request'

// 查询板卡资源统计
export function getResourceStats() {
  return request({
    url: '/system/basebandResource/stats',
    method: 'get'
  })
}
