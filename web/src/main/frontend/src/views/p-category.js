import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { toJS } from 'mobx';
import { Route, Switch, withRouter } from 'react-router-dom';
import { Button } from 'antd';
import CatList from '../components/category/catList';
import CatEdit from '../components/category/catEdit';
import error from '../components/error';
@inject('app')
@inject('category')
@observer
class Cat extends Component {
    constructor(props) {
        super(props);
    }
    render() {
        return (
            <div className="p-common">
                <Switch>
                    <Route exact path={'/category'} component={CatList}></Route>
                    <Route exact path={'/category/edit'} component={CatEdit}></Route>
                    <Route component={error} />
                </Switch>
            </div>
        )
    }
}

export default Cat;
