import React, {Component} from 'react';
import { observer, inject } from 'mobx-react';
import { LocaleProvider } from 'antd';
import zhCN from 'antd/lib/locale-provider/zh_CN';
import { Route, Redirect, Switch, withRouter } from 'react-router-dom';
import Loadable from 'react-loadable';
import _ from 'lodash';
import Sidebar from '../components/layout/sidebar';
import Header from '../components/layout/header';
import Footer from '../components/layout/footer';
import Con from '../components/layout/content';
import Login from '../components/layout/login';
import Loader from '../components/layout/loader';
import '../styles/layout.less';

const Dashboard = Loadable({
    loader: () => import('./p-dashboard'),
    loading: () => null
});

@inject('layout')
@inject('app')
@observer
class Layout extends Component {
    constructor(props){
        super(props);
        const { app: { getUser }} = props;
        getUser();
    }
    componentWillReceiveProps(){
        const { app: { getUser }} = this.props;
        getUser();
    }
    render(){
        const { layout: { fold_sidebar }, app: { loading}} = this.props;
        return(
            <LocaleProvider locale={zhCN}>
                <Switch>
                    <Route path="/login" component={Login}/>
                    <Route render={() => <div>
                        <Loader fullScreen />
                        <div className={['layout', fold_sidebar ? 'fold' : undefined].join(' ')}>
                            <Sidebar />
                            <div className="layout-container">
                                <Header />
                                <Con />
                                <Footer />
                            </div>
                        </div>
                    </div>}/>
                </Switch>
            </LocaleProvider>
        )
    }
}

export default withRouter(Layout);
