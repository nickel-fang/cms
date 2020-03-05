import React,{Component} from 'react'
import { observer, inject } from 'mobx-react';
import Cookie from 'js-cookie';
import { Icon,message } from 'antd'
import {CACHE_GLOBAL} from '../constants';
import '../styles/error.less'

@inject('app')
@observer
class Error extends Component{
    constructor(props){
        super(props);
        const cookie = Cookie.getJSON(CACHE_GLOBAL) || {};
        if (!cookie.token) {
            message.info('请登陆');
            location.href = '#/login';
            return
        }
    }
    render(){
        return (
            <div className="p-common">
                <div className="error">
                    <Icon type="frown-o" />
                    <h1>404 Not Found</h1>
                </div>
            </div>
        )
    }
}

export default Error
