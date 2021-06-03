const createProxyMiddleware = require('http-proxy-middleware');

module.exports = function (app) {
  app.use(
    '/restservices/',
    createProxyMiddleware({
      target: 'https://localhost:8443',
      secure: false,
      changeOrigin: true
    })
  );
  app.use(
    '/onap/',
    createProxyMiddleware({
      target: 'http://localhost:6969',
      secure: false,
      changeOrigin: true
    })
  );
};