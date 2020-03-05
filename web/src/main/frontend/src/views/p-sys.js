import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { toJS } from 'mobx';
import { Route, Switch, withRouter } from 'react-router-dom';
import { ROUTER_PATHS } from '../constants'
import SettingList from '../components/sys/setting/settingList';
import MenuList from '../components/sys/menu/menuList';
import MenuEdit from '../components/sys/menu/menuEdit';
import error from '../components/error';

@observer
class Sys extends Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div className="p-common">
                <Switch>
                    <Route exact path={ROUTER_PATHS.SYS_SETTING} component={SettingList}></Route>
                    <Route exact path={ROUTER_PATHS.USERS_MENU} component={MenuList}></Route>
                    <Route exact path={ROUTER_PATHS.USERS_MENU_EDIT} component={MenuEdit}></Route>
                    <Route exact path={ROUTER_PATHS.SYS_MENU} component={MenuList}></Route>
                    <Route exact path={ROUTER_PATHS.SYS_MENU_EDIT} component={MenuEdit}></Route>
                    <Route component={error} />
                </Switch>
            </div>
        )
    }
}

export default withRouter(Sys);
