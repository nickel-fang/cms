import React, { Component } from 'react';
import { withRouter, Route, Switch} from 'react-router-dom';
import {ROUTER_PATHS} from '../constants';
import templateList from '../components/template/templateList'
import templateEdit from '../components/template/templateEdit'

class PageTemplate extends Component {
    constructor(props){
        super(props);
    }
    render() {
        return (
            <div className="p-common">
                <Switch>
                    <Route exact path={ROUTER_PATHS.TEMPLATE} component={templateList}></Route>
                    <Route exact path={ROUTER_PATHS.TEMPLATE_EDIT} component={templateEdit}></Route>
                </Switch>
            </div>
        )
    }
}

export default withRouter(PageTemplate);
