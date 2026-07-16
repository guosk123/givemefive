import { serverConfig } from '@/config/server';

type RequestMethod = 'OPTIONS' | 'GET' | 'HEAD' | 'POST' | 'PUT' | 'DELETE' | 'TRACE' | 'CONNECT';

interface RequestOptions {
  path: string;
  method?: RequestMethod;
  data?: unknown;
  header?: Record<string, string>;
}

function buildUrl(path: string): string {
  const baseUrl = serverConfig.apiBaseUrl.replace(/\/$/, '');
  const normalizedPath = path.startsWith('/') ? path : `/${path}`;
  return `${baseUrl}${normalizedPath}`;
}

export function getCurrentOpenid(): string {
  const storedOpenid = wx.getStorageSync(serverConfig.openidStorageKey);
  if (typeof storedOpenid === 'string' && storedOpenid.trim()) {
    return storedOpenid.trim();
  }
  return serverConfig.devOpenid;
}

export function setCurrentOpenid(openid: string): void {
  wx.setStorageSync(serverConfig.openidStorageKey, openid);
}

export function request<T>(options: RequestOptions): Promise<T> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: buildUrl(options.path),
      method: options.method ?? 'GET',
      data: options.data as string | WechatMiniprogram.IAnyObject | ArrayBuffer,
      timeout: serverConfig.timeout,
      header: {
        'content-type': 'application/json',
        [serverConfig.openidHeader]: getCurrentOpenid(),
        ...options.header
      },
      success(response) {
        if (response.statusCode >= 200 && response.statusCode < 300) {
          resolve(response.data as T);
          return;
        }
        reject(new Error(`request failed: ${response.statusCode}`));
      },
      fail(error) {
        reject(new Error(error.errMsg));
      }
    });
  });
}
