import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { toJS } from 'mobx';
import { Route, Switch, withRouter } from 'react-router-dom';
import { ROUTER_PATHS} from '../constants'
import { Button } from 'antd';
import cmsList from '../components/cms/cmsList';
import cmsEdit from '../components/cms/cmsEdit';
import '../styles/cms.less';
import error from '../components/error';

@inject('app')
@inject('cms')
@observer
class Cms extends Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div className="p-common">
                <Switch>
                    <Route exact path={ROUTER_PATHS.ARTICLES} component={cmsList}></Route>
                    <Route exact path={ROUTER_PATHS.ARTICLES_EDIT} component={cmsEdit}></Route>
                    <Route component={error} />
                </Switch>
            </div>
        )
    }
}

export default withRouter(Cms);
