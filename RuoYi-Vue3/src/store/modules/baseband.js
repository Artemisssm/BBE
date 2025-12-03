import { defineStore } from 'pinia'

// 是否开启调试日志（生产环境关闭）
const DEBUG = process.env.NODE_ENV === 'development'

export const useBasebandStore = defineStore('baseband', {
  state: () => ({
    // 参数定义更新时间戳，用于触发参数配置页面刷新
    paramDefUpdateTime: 0,
    // 参数定义更新的单元类型
    paramDefUpdateUnitType: null,
    // 更新历史记录（最多保留10条）
    updateHistory: []
  }),
  
  getters: {
    /**
     * 获取最近一次更新的信息
     */
    lastUpdate: (state) => {
      return state.updateHistory.length > 0 
        ? state.updateHistory[state.updateHistory.length - 1] 
        : null
    }
  },
  
  actions: {
    /**
     * 通知参数定义已更新
     * @param {String} unitType - 更新的单元类型（可选）
     */
    notifyParamDefUpdated(unitType = null) {
      const updateTime = Date.now()
      this.paramDefUpdateTime = updateTime
      this.paramDefUpdateUnitType = unitType
      
      // 记录更新历史
      this.updateHistory.push({
        unitType,
        time: updateTime,
        timestamp: new Date().toISOString()
      })
      
      // 只保留最近10条记录
      if (this.updateHistory.length > 10) {
        this.updateHistory.shift()
      }
      
      // 调试日志
      if (DEBUG) {
        console.log('[Baseband Store] 参数定义已更新', { 
          unitType, 
          time: updateTime,
          historyCount: this.updateHistory.length 
        })
      }
    },
    
    /**
     * 重置更新状态
     */
    resetUpdateStatus() {
      this.paramDefUpdateTime = 0
      this.paramDefUpdateUnitType = null
      if (DEBUG) {
        console.log('[Baseband Store] 更新状态已重置')
      }
    },
    
    /**
     * 清空更新历史
     */
    clearHistory() {
      this.updateHistory = []
      if (DEBUG) {
        console.log('[Baseband Store] 更新历史已清空')
      }
    }
  }
})
