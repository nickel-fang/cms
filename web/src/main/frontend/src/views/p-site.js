import React,{Component} from 'react';
import {observer, inject} from 'mobx-react';
import { toJS} from 'mobx';
import { Route, Switch, withRouter } from 'react-router-dom';
import SiteList from '../components/site/siteList'
import SiteEdit from '../components/site/siteEdit'
import error from '../components/error'

@inject('site')
@observer
class PageSiteList extends Component{
    constructor(props){
        super(props);
        const {site:{getList}} = props
        getList();
    }
    render(){
        return (
            <div className="p-common">
                <Switch>
                    <Route exact path={'/site'} component={SiteList}></Route>
                    <Route exact path={'/site/edit'} component={SiteEdit}></Route>
                    <Route component={error} />
                </Switch>
            </div>
        )
    }
}

export default withRouter(PageSiteList);
