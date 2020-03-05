import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { toJS } from 'mobx';
import { Route, Switch, withRouter } from 'react-router-dom';
import { ROUTER_PATHS} from '../constants'
import RoleList from '../components/role/roleList'
import RoleEdit from '../components/role/roleEdit'
import error from '../components/error'
import '../styles/role.less';

@inject('app')
@inject('roles')

@observer
class PageRoleList extends Component {
    constructor(props) {
        super(props)
    }

    render() { 
        return (
            <div className="p-common">
                <Switch>
                    <Route exact path={ROUTER_PATHS.USERS_ROLE} component={RoleList}></Route>
                    <Route exact path={ROUTER_PATHS.USERS_ROLE_EDIT} component={RoleEdit}></Route>
                    <Route component={error} />
                </Switch>
            </div>
        )
    }
}

export default withRouter(PageRoleList)