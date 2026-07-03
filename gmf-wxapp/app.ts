App<IAppOption>({
  globalData: {
    apiBaseUrl: 'http://localhost:8080'
  }
});

interface IAppOption {
  globalData: {
    apiBaseUrl: string;
  };
}
