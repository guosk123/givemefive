import { request } from '@/utils/request';

export interface WxAppUserConfig {
  id: string;
  openid: string;
  unionid: string | null;
  nickname: string;
  avatarUrl: string;
  phone: string;
  profile: Record<string, string>;
  status: 'ACTIVE' | 'DISABLED';
  lastLoginAt: string;
  createdAt: string;
  updatedAt: string;
}

export function fetchUserConfig(): Promise<WxAppUserConfig> {
  return request<WxAppUserConfig>({
    path: '/api/app/user-config',
    method: 'GET'
  });
}
