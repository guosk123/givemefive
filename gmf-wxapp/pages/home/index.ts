Page({
  data: {
    modules: [
      { name: '记账', path: '/pages/account/index' },
      { name: '借款', path: '/pages/debt/index' },
      { name: '小商户', path: '/pages/merchant/index' }
    ]
  },
  openModule(event: WechatMiniprogram.TouchEvent) {
    const path = event.currentTarget.dataset.path as string;
    wx.switchTab({ url: path });
  }
});
