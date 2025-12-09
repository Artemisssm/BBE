/**
 * 基带监控模块事件通知工具
 * 用于跨页面通信，实现实时数据同步
 */

// 使用 BroadcastChannel API 实现跨标签页通信
let channel = null

// 初始化广播频道
function initChannel() {
  if (!channel && typeof BroadcastChannel !== 'undefined') {
    channel = new BroadcastChannel('baseband-monitor')
  }
}

// 事件类型常量
export const BASEBAND_EVENTS = {
  UNIT_CHANGED: 'unit_changed',      // 基带单元变更（增删改）
  PARAM_CHANGED: 'param_changed',    // 参数配置变更
  RESOURCE_REFRESH: 'resource_refresh' // 资源刷新请求
}

/**
 * 发送事件通知
 * @param {string} eventType - 事件类型
 * @param {object} data - 事件数据
 */
export function emitBasebandEvent(eventType, data = {}) {
  initChannel()
  
  const message = {
    type: eventType,
    data: data,
    timestamp: Date.now()
  }
  
  // 使用 BroadcastChannel 发送（跨标签页）
  if (channel) {
    channel.postMessage(message)
  }
  
  // 同时使用自定义事件（同一页面内）
  window.dispatchEvent(new CustomEvent('baseband-event', { detail: message }))
  
  console.log('[基带事件] 发送:', eventType, data)
}

/**
 * 监听事件
 * @param {string} eventType - 事件类型，如果为 null 则监听所有事件
 * @param {function} callback - 回调函数
 * @returns {function} 取消监听的函数
 */
export function onBasebandEvent(eventType, callback) {
  initChannel()
  
  // BroadcastChannel 监听器（跨标签页）
  const channelHandler = (event) => {
    if (!eventType || event.data.type === eventType) {
      callback(event.data)
    }
  }
  
  // 自定义事件监听器（同一页面内）
  const windowHandler = (event) => {
    if (!eventType || event.detail.type === eventType) {
      callback(event.detail)
    }
  }
  
  if (channel) {
    channel.addEventListener('message', channelHandler)
  }
  window.addEventListener('baseband-event', windowHandler)
  
  // 返回取消监听的函数
  return () => {
    if (channel) {
      channel.removeEventListener('message', channelHandler)
    }
    window.removeEventListener('baseband-event', windowHandler)
  }
}

/**
 * 通知基带单元已变更
 * @param {string} action - 操作类型：add/update/delete
 * @param {object} unitData - 单元数据
 */
export function notifyUnitChanged(action, unitData = {}) {
  emitBasebandEvent(BASEBAND_EVENTS.UNIT_CHANGED, {
    action,
    unitData
  })
}

/**
 * 通知参数配置已变更
 * @param {number} unitId - 单元ID
 */
export function notifyParamChanged(unitId) {
  emitBasebandEvent(BASEBAND_EVENTS.PARAM_CHANGED, {
    unitId
  })
}

/**
 * 请求刷新资源监控
 */
export function requestResourceRefresh() {
  emitBasebandEvent(BASEBAND_EVENTS.RESOURCE_REFRESH)
}

/**
 * 清理资源
 */
export function cleanup() {
  if (channel) {
    channel.close()
    channel = null
  }
}
