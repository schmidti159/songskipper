const proxy = require('http-proxy-middleware');

module.exports = function(app) {
  app.use(proxy('/api', { target: 'http://127.0.0.1:8080' }));
  app.use(proxy('/ws', { target: 'ws://127.0.0.1:8080', ws: true }));
};