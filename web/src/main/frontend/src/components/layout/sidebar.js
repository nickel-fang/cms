import React, { Component } from 'react';
import { withRouter, Link } from 'react-router-dom';
import { Menu, Icon } from 'antd';
import { observer, inject } from 'mobx-react';
import {toJS} from 'mobx';
import _ from 'lodash';

const MenuItem = Menu.Item;
const SubMenu = Menu.SubMenu;
const formatUrl = function(path){
    const beforeHash = location.href.split('#')[0];
    if (beforeHash.indexOf('index.html') == -1){
        return beforeHash.substr(0, location.href.split('#')[0].length - 1)+path;
    }else{
        return beforeHash.substr(0, beforeHash.indexOf('index.html')-1) + path;
    }
}

@inject('layout')
@inject('app')
@observer
class SideBar extends Component{
    constructor(props){
        super(props);
        this.state = {
            openKeys: this.getOpenKeys(props.location)
        };
    }
    getPathArray = ({ pathname }) => {
        return pathname.substr(1).split('/');
    }
    getKeysFromLocation = (location) => {
        const arr_1 = this.getPathArray(location);
        const arr_2 = [];
        if (location.pathname.indexOf('user.html') === -1) {   //408定制化加入的判断，如果菜单首项是用户中心则不被选中
            for (let i = 0, len = arr_1.length; i < len; i++) {
                i > 0 ? arr_2.push(arr_2[i - 1] + '-' + arr_1[i]) : arr_2.push(arr_1[i]);
            }
        }
        return arr_2;
    }
    getOpenKeys = (location) => {
        const keys = this.getKeysFromLocation(location);
        return _.dropRight(keys, 1);
    }
    getSelectedKeys = (location) => {
        let keys = this.getKeysFromLocation(location)||[];
        let lastKey = _.last(keys);
        if (!!lastKey&&lastKey.indexOf('edit') !== -1){  //如果路径有edit说明事编辑页，则取倒数第二个key为selectkey
            keys = _.dropRight(keys)
        }
        return [_.last(keys)];
    }
    onMenuOpenChg = (openKeys) => {
        const { app: { topKeys}} = this.props;
        const CtopKeys = toJS(topKeys);
        const latestOpenKey = openKeys.find(key => this.state.openKeys.indexOf(key) === -1);
        if (CtopKeys.indexOf(latestOpenKey) === -1) {
            this.setState({ openKeys });
        } else {
            this.setState({
                openKeys: latestOpenKey ? [latestOpenKey] : []
            });
        }
    }

    getMenus = (items, parentPath = '/') => {
        return items.map(item => {
            let path = parentPath + item.code;
            const key = path.substr(1).replace(/\//g, '-');
            if (item.child) {
                return (
                    <SubMenu
                        key={key}
                        title={
                            <span>
                                <Icon type={item.icon} />
                                <span>{item.name}</span>
                            </span>
                        }
                    >
                        {this.getMenus(item.child, path + '/')}
                    </SubMenu>
                );
            } else {
                return (
                    <MenuItem key={key}>
                    {
                        path.indexOf('html')===-1?(
                            <Link to={path}>
                                <Icon type={item.icon} />
                                <span>{item.name}</span>
                            </Link>
                        ):(
                            <a href={formatUrl(path)} target="_blank">
                                <Icon type={item.icon} />
                                <span>{item.name}</span>
                            </a>
                        )
                    }
                    </MenuItem>
                );
            }
        });
    }
    componentWillReceiveProps(nextProps) {
        const { location, layout: { fold_sidebar } } = nextProps;
        if (fold_sidebar) {
            this.setState({
                openKeys: []
            });
        } else {
            this.setState({
                openKeys: this.getOpenKeys(location)
            });
        }
    }
    render(){
        const { location, layout: { fold_sidebar, activeTabs},app:{menus,user} } = this.props;
        const { openKeys } = this.state;
        const CMenus = toJS(menus);
        return (
            <div className="layout-sidebar">
                <div className="sidebar-hd">
                    <span className="logo" style={{ 'backgroundImage': 'url(' + (user.system ? user.system.icon :'./public/computer.png') +')'}}></span>
                    {/*fold_sidebar ? undefined : <span className="name">CMS</span>*/}
                </div>
                <Menu
                    theme="light"
                    mode="inline"
                    inlineCollapsed={fold_sidebar}
                    onOpenChange={this.onMenuOpenChg}
                    openKeys={openKeys}
                    selectedKeys={this.getSelectedKeys(location)}
                >
                    {this.getMenus(CMenus)}
                </Menu>
            </div>
        )
    }
}

export default withRouter(SideBar);
