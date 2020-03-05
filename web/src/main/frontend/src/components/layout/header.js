import React,{Component} from 'react';
import {observer,inject} from 'mobx-react';
import { toJS } from 'mobx';
import { withRouter } from 'react-router-dom';
import { Menu, Icon } from 'antd';
import qs from 'qs';
import _ from 'lodash';

const SubMenu = Menu.SubMenu;
const MenuItem = Menu.Item;

@inject('app')
@inject('layout')
@observer
class Header extends Component {
    toggleSider = () => {
        const { layout } = this.props;
        layout.fold_sidebar = !layout.fold_sidebar;
    }
    handleClickMenu = (e) => {
        const { app: { logout, updateStore},layout:{reset} } = this.props;
        const callback = () => {
            reset();
        }
        e.key === 'logout' && logout(callback);
        if(e.key.indexOf('site')!==-1){
            const obj = qs.parse(e.key);
            updateStore({ selectSiteId: parseInt(obj.site)})
        }
    }

    render(){
        const { layout: { fold_sidebar }, app: { user, systems, sites, selectSiteId} } = this.props;
        const selectSiteItem = _.find(sites, { id: selectSiteId});
        return(
            <div className="layout-hd">
                <div className="toggle-sider-btn" onClick={this.toggleSider}>
                    <Icon type={fold_sidebar ? 'menu-unfold' : 'menu-fold'} />
                </div>
                <Menu mode="horizontal" onClick={this.handleClickMenu} selectedKeys={[]}>
                    <SubMenu title={<span><Icon type='user' />{user.username||''}</span>}>
                        <MenuItem key="logout">
                            <a>注销</a>
                        </MenuItem>
                    </SubMenu>
                    {/* <SubMenu title={<span><Icon type="laptop" />用户系统</span>}></SubMenu> */}
                    <SubMenu title={<span><Icon type="bars" />{selectSiteItem ? '站点：'+selectSiteItem.name:'无站点'}</span>}>
                        {
                            sites.map(item => {
                                return (
                                    <MenuItem key={'site='+item.id}>
                                        <a>{item.name}</a>
                                    </MenuItem>
                                );
                            })
                        }
                    </SubMenu>
                </Menu>
            </div>
        )
    }
}

export default withRouter(Header);
