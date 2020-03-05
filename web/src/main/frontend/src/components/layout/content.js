import React, { Component } from 'react';
import { Route, Switch, Redirect, withRouter } from 'react-router-dom';
import Loadable from 'react-loadable';
import { observer, inject } from 'mobx-react';
import {toJS} from 'mobx';
import { Tabs } from 'antd';
import { ROUTER_PATHS } from '../../constants';
import _ from 'lodash';

const TabPane = Tabs.TabPane;
const Dashboard = Loadable({
    loader: () => import('../../views/p-dashboard'),
    loading: () => null
});
const Site = Loadable({
    loader: () => import('../../views/p-site'),
    loading: () => null
});
const Category = Loadable({
    loader: () => import('../../views/p-category'),
    loading: () => null
});
const Cms = Loadable({
    loader: () => import('../../views/p-cms'),
    loading: () => null
});
const Template = Loadable({
    loader: () => import('../../views/p-template'),
    loading: () => null
});
const Blocks = Loadable({
    loader: () => import('../../views/p-blocks'),
    loading: () => null
});
const Source = Loadable({
    loader: () => import('../../views/p-source'),
    loading: () => null
});
const Sys = Loadable({
    loader: () => import('../../views/p-sys'),
    loading: () => null
});

const Role = Loadable({
    loader: () => import('../../views/p-role'),
    loading: () => null
});

const Permission = Loadable({
    loader: () => import('../../views/p-permission'),
    loading: () => null
})

const TEST = Loadable({
    loader: () => import('../../views/p-test'),
    loading: () => null
});

const error = Loadable({
    loader: () => import('../../views/p-error'),
    loading: () => null
});

@inject('layout')
@inject('app')
@observer
class Content extends Component {
    constructor(props){
        super(props);
        const {location,history} = props;
        //首次进入根据路由激活tab页 以location.pathname当作激活key
        const activeKey = props.location.pathname;
        this.state = {
            activeKey
        }
    }
    componentWillMount(){
        const { app,location } = this.props;
        const activeKey = location.pathname;
        this.menuStash = toJS(app.menus);
        this.menus = this.addLocationToMenu(toJS(app.menus));
        this.setDefaultActiveTabs(activeKey);
    }
    componentWillReact() {  //处理首次进入menu数据请求的情况
        const { app, location,layout } = this.props;
        const activeKey = location.pathname;
        if (!_.isEqual(this.menuStash, toJS(app.menus))) {
            this.menuStash = toJS(app.menus);
            this.menus = this.addLocationToMenu(toJS(app.menus));
            this.setDefaultActiveTabs(activeKey);
        }
    }
    componentWillReceiveProps({ layout, location, history}) {
        const { activeTabs, updateStore} = layout;
        const CactiveTabs = toJS(activeTabs);
        const activeKey = location.pathname;
        const activeTab = this.findMenuItemByLocation(this.menus, activeKey)  //将要激活的页面
        if(activeKey==="/user.html"){   //为408定制加入的用户系统，假如为user.html则不出tab页签
            return;
        }
        if (activeTab){
            if (!_.find(CactiveTabs, { location: location.pathname })) {  //用于渲染后手动输入的路径是对的情况，没有找到就新建一个跳过去
                const ifEdit = _.last(location.pathname.substr(1).split('/'))
                const ifPrevEdit = _.last(this.props.location.pathname.substr(1).split('/'))
                //如果之前的页面是编辑页，则不跳出新页签
                if ((ifEdit.indexOf('edit') !== -1 && !!this.findMenuItemByLocation(this.menus, location.pathname)) || (ifPrevEdit.indexOf('edit') !== -1 && !!this.findMenuItemByLocation(this.menus, this.props.location.pathname))) {
                    return
                }
                CactiveTabs.push({...activeTab});
                updateStore({ activeTabs: CactiveTabs });
            }
            this.setState({  //如果找到就直接跳过去
                activeKey: activeKey
            })
        }else{  //如果路径错误跳出error tab
            if (location.pathname !== '/'&&location.pathname !== '/login'&&!_.find(CactiveTabs, { location: location.pathname})){  //如果有error页弹出过则直接跳过去
                let activeItem = {
                    code: 'error',
                    icon: 'idcard',
                    name: 'error',
                    location: location.pathname
                }
                const ifEdit = _.last(location.pathname.substr(1).split('/'))  //判断是否是进入edit页面
                if (ifEdit.indexOf('edit') !== -1) {  // 如果最后的路由项是edit 则显示列表和编辑公用页面
                    const arr_1 = _.dropRight(location.pathname.substr(1).split('/'));
                    let url = '';
                    for (let i = 0, len = arr_1.length; i < len; i++) {
                        url += '/' + arr_1[i];
                    }
                    const item = this.findMenuItemByLocation(this.menus, url)
                    if (!!item){  //如果菜单里存在edit的那个列表项
                        activeItem = item;
                        if (!_.find(CactiveTabs, { location: item.location})){  //如果tabs里没有 则冒出来，有则被选中
                            CactiveTabs.push(activeItem);
                            updateStore({ activeTabs: CactiveTabs })
                        }
                        this.setState({
                            activeKey: url
                        })
                        return
                    }
                }
                CactiveTabs.push(activeItem);
                updateStore({ activeTabs: CactiveTabs })
                this.setState({
                    activeKey: activeKey
                })
                return
            } else if(location.pathname == '/' || location.pathname == '/login'){  // catch当路径为/login或/ 进入主面板时不跳转到dashboard的情况
                //history.push(ROUTER_PATHS.ARTICLES);
            }
            this.setState({
                activeKey: location.pathname
            })
        }
    }
    addLocationToMenu = (data,parentLocation='') =>{
        for (let i = 0; i < data.length; i++) {
            data[i].location = '/'+parentLocation + data[i].code
            if (data[i].child) {
                this.addLocationToMenu(data[i].child, data[i].code+'/')
            }
        }
        return data;
    }
    setDefaultActiveTabs = (activeKey) => {
        const { layout: { updateStore }, location } = this.props;
        if (activeKey === "/user.html") {
            return;
        }
        const activeTab = this.findMenuItemByLocation(this.menus, activeKey)
        if (!!activeTab){
            updateStore({ activeTabs: [{ ...activeTab}] })
        } else if (location.pathname !== '/' && location.pathname !== '/login'){ //渲染器默认路径输入错误跳出error tab页
            this.state.activeKey = location.pathname;
            const ifEdit = _.last(location.pathname.substr(1).split('/'))  //判断是否是进入edit页面
            let activeItem = {
                code: 'error',
                icon: 'idcard',
                name: 'error',
                location: location.pathname
            }
            if (ifEdit.indexOf('edit') !== -1){
                const arr_1 = _.dropRight(location.pathname.substr(1).split('/'));
                let url = '';
                for (let i = 0, len = arr_1.length; i < len; i++) {
                    url += '/' + arr_1[i];
                }
                const item = this.findMenuItemByLocation(this.menus, url)
                if (!!item) {
                    activeItem = item;
                    this.setState({
                        activeKey: url
                    })
                }
            }
            updateStore({ activeTabs: [activeItem] })
        }
    }
    findMenuItemByLocation = (data,activeKey) => {
        for(let i=0;i<data.length;i++){
            if(data[i].location==activeKey&&!data[i].child){  // 菜单项没有children才能被选中
                return data[i]
            }
            if(data[i].child){
                const node = this.findMenuItemByLocation(data[i].child, activeKey)
                if(node){
                    return node
                }
            }
        }
    }
    getActiveKey = (location) => {
        const keysArr = location.pathname.substr(1).split('/');
        const lastKey = _.last(keysArr)
        return lastKey
    }
    onChange = (activeKey) => {
        const {history} = this.props;
        history.push(activeKey);
    }
    onEdit = (targetKey, action) =>{
        this[action](targetKey);
    }
    remove = (targetKey) => {
        const { layout: { activeTabs, updateStore },history } = this.props;
        const CactiveTabs = toJS(activeTabs);
        let activeKey = this.state.activeKey;
        let lastIndex;
        CactiveTabs.forEach((pane, i) => {
            if (pane.location === targetKey) {
                lastIndex = i - 1;
            }
        });
        const panes = CactiveTabs.filter(pane => pane.location !== targetKey);
        if (lastIndex >= 0 && activeKey === targetKey) { //非删除第一个tab，并且是删除自己时，跳至所剩最后一项
            activeKey = panes[lastIndex].location;
            history.push(activeKey);
        }
        else if (lastIndex === -1) { //删除第一个tab，并且是删除自己时，跳转到所剩第一项
            if (activeKey === targetKey && !!panes && !!panes.length) {
                history.push(panes[0].location);
            }
        }
        updateStore({ activeTabs: panes });
    }
    shouldRedirect= () => {
        const {location,match} = this.props;
        if(location.pathname == '/' || location.pathname=='/login'){
            return true
        }
        return false
    }
    render() {
        const { layout: { activeTabs, updateStore }, app, location } = this.props;
        const CactiveTabs = toJS(activeTabs);
        if (!_.isEqual(this.menuStash, toJS(app.menus))){  //处理首次进入menu数据请求的情况
            updateStore({ firstComeFlag: true });
        }
        return (
            <div className="container">
                <Tabs
                    activeKey={this.state.activeKey}
                    type="editable-card"
                    hideAdd={true}
                    onChange={this.onChange}
                    onEdit={this.onEdit}
                >
                    {CactiveTabs.map(pane => <TabPane tab={pane.name} key={pane.location} forceRender={false}>
                        {(this.state.activeKey==pane.location)?
                            (<Switch>
                                {this.shouldRedirect() && <Redirect to={ROUTER_PATHS.ARTICLES} />}
                                <Route path={ROUTER_PATHS.DASHBOARD} component={Dashboard} />
                                <Route path={ROUTER_PATHS.SITE} component={Site} />
                                <Route path={ROUTER_PATHS.CATEGORY} component={Category} />
                                <Route path={ROUTER_PATHS.ARTICLES} component={Cms} />
                                <Route path={ROUTER_PATHS.SOURCE} component={Source} />
                                <Route path={ROUTER_PATHS.TEMPLATE} component={Template} />
                                <Route path={ROUTER_PATHS.BLOCKS} component={Blocks} />
                                <Route path={ROUTER_PATHS.SYS_SETTING} component={Sys} />
                                <Route path={ROUTER_PATHS.SYS_MENU} component={Sys} />
                                <Route path={ROUTER_PATHS.SOURCE} component={Source} />
                                <Route path={ROUTER_PATHS.USERS_ROLE} component={Role} />
                                <Route path={ROUTER_PATHS.USERS_MENU} component={Sys} />
                                <Route path={ROUTER_PATHS.PERMISSION} component={Permission} />
                                <Route exact path={'/test'} component={TEST} />
                                <Route component={error} />
                            </Switch>):null}
                    </TabPane>)}
                </Tabs>
            </div>
        );
    }
}

export default withRouter(Content);
