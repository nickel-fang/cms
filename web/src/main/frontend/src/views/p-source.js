import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { toJS } from 'mobx';
import { Route, Switch, withRouter } from 'react-router-dom';
import SourceList from '../components/source/sourceList'
import SourceEdit from '../components/source/sourceEdit'
import error from '../components/error'

@inject('site')
@observer
class PageSiteList extends Component {
    constructor(props) {
        super(props);

    }
    render() {
        return (
            <div className="p-common">
                <Switch>
                    <Route exact path={'/cms/source'} component={SourceList}></Route>
                    <Route exact path={'/cms/source/edit'} component={SourceEdit}></Route>
                    <Route component={error} />
                </Switch>
            </div>
        )
    }
}

export default withRouter(PageSiteList);
