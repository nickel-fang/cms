const base_prefix = 'http://10.3.38.235:8081';
//const base_prefix = 'http://172.31.128.224:8081';
//const base_prefix = 'http://10.3.37.64:8081';
//const base_prefix = 'http://172.31.178.254:8081';
// const base_prefix = 'http://172.31.87.252:8081'; // 阿地力
// const base_prefix = 'http://172.31.128.224:8081'; // 李明璐
const urls = [
    '/auth',
    '/api'
];

const proxy = {};

urls.forEach(url => {
    let prefix = base_prefix;
    proxy[url] = {
        target: prefix + url,
        changeOrigin: true,
        pathRewrite: {
            ['^' + url]: ''
        }
    };
});

module.exports = proxy;
