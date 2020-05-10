const pages = {
  login: {
    entry: 'src/pages/login/main.ts',
    template: 'public/index.html',
    filename: 'app1.html',
  },
};

module.exports = {
  configureWebpack: {
    devtool: 'source-map',
  },
  pages: pages,
  outputDir: '../public/vuejs',
  filenameHashing: false,
  // delete HTML related webpack plugins
  chainWebpack: (config) => {
    Object.keys(pages).forEach((page) => {
      config.plugins.delete(`html-${page}`);
      config.plugins.delete(`preload-${page}`);
      config.plugins.delete(`prefetch-${page}`);
    });
  },
};
