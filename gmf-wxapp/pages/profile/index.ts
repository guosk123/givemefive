import { serverConfig } from '@/config/server';
import { fetchUserConfig, WxAppUserConfig } from '@/services/userConfig';
import { getCurrentOpenid } from '@/utils/request';

interface ProfilePageData {
  user: WxAppUserConfig | null;
  loading: boolean;
  errorMessage: string;
  displayName: string;
  avatarUrl: string;
  avatarText: string;
  openidText: string;
  unionidText: string;
  phoneText: string;
  statusText: string;
  lastLoginText: string;
  serverBaseUrl: string;
}

function formatDateTime(value: string | null | undefined): string {
  if (!value) {
    return '-';
  }
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return '-';
  }
  const pad = (part: number) => part.toString().padStart(2, '0');
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`;
}

function firstCharacter(value: string): string {
  const trimmed = value.trim();
  if (!trimmed) {
    return '我';
  }
  return trimmed.slice(0, 1).toUpperCase();
}

Page({
  data: {
    user: null,
    loading: false,
    errorMessage: '',
    displayName: '未设置昵称',
    avatarUrl: '',
    avatarText: '我',
    openidText: getCurrentOpenid(),
    unionidText: '-',
    phoneText: '-',
    statusText: '-',
    lastLoginText: '-',
    serverBaseUrl: serverConfig.apiBaseUrl
  } as ProfilePageData,

  onLoad() {
    this.loadUserConfig();
  },

  refreshUserConfig() {
    this.loadUserConfig();
  },

  loadUserConfig() {
    this.setData({ loading: true, errorMessage: '' });
    fetchUserConfig()
      .then((user) => {
        const displayName = user.nickname || '未设置昵称';
        this.setData({
          user,
          displayName,
          avatarUrl: user.avatarUrl || '',
          avatarText: firstCharacter(displayName),
          openidText: user.openid,
          unionidText: user.unionid || '-',
          phoneText: user.phone || '-',
          statusText: user.status === 'ACTIVE' ? '正常' : '停用',
          lastLoginText: formatDateTime(user.lastLoginAt),
          loading: false
        });
      })
      .catch((error: Error) => {
        this.setData({
          errorMessage: error.message || '用户信息获取失败',
          loading: false
        });
      });
  }
});
