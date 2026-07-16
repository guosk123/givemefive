import { serverConfig } from './config/server';

App<IAppOption>({
  globalData: {
    apiBaseUrl: serverConfig.apiBaseUrl
  }
});

interface IAppOption {
  globalData: {
    apiBaseUrl: string;
  };
}
