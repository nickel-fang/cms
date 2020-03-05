import axios from 'axios';
import qs from 'qs';
import _ from 'lodash';
import { CACHE_GLOBAL, BASE_URL, CACHE_USER, CACHE_MENUS } from '../constants';
import Cookie from 'js-cookie';
import {message} from 'antd';
const toLogin = () => {
    console.log(location.href)
    if(location.href.indexOf('redirectUrl')===-1){
        location.href = '#/login?redirectUrl=' + encodeURIComponent(location.href);
    }else{
        location.href = '#/login'
    }
}
function request(url, options, file){
    if (options.cross) { //跨域
        return axios({
            url: 'http://query.yahooapis.com/v1/public/yql',
            method: 'get',
            params: {
                q: "select * from json where url='" + url + '?' + qs.stringify(options.data) + "'",
                format: 'json'
            }
        });
    }else{
        const method = options.method.toLowerCase() || 'get';
        const baseURL = options.baseURL || BASE_URL;
        const opts = {
            url,
            method,
            baseURL,
            headers: { 'X-Requested-With': 'XMLHttpRequest' }
        };
        const token = _.get(Cookie.getJSON(CACHE_GLOBAL), 'token');
        if (options.token !== false && token) {
            opts.headers.Authorization = token;
        }
        let data = options.data;
        data = data && typeof (data) !== 'object' ? JSON.parse(data) : data;
        method === 'get' ? (opts.params = data) : (opts.data = data);
        return axios(opts).then(({ data }) => {
            if(data.code === -10000){
                const cookie = Cookie.getJSON(CACHE_GLOBAL);
                Cookie.remove(CACHE_GLOBAL);
                sessionStorage.clear();
                message.error('登陆过期，请重新登录');
                toLogin();
                return data;
            }else{
                return data;
            }
            // if (data.code === -4) {
            //     return Promise.reject(new Error('logout'));
            // } else if (data.code === -3 && data.data && data.data.direct && data.data.direct != '') {
            //     window.location.href = data.data.direct;
            // } else if (data.code === -2) {
            //     return Promise.reject(new Error('系统错误，请联系管理员'));
            // } else if (data.code === -10000) {
            //     return Promise.reject(new Error('login failed'));
            // } else if (data.code < 0) {
            //     return Promise.reject(new Error(data.msg));
            // } else {
            //     return data;
            // }
        });
    }
}
module.exports = request;
