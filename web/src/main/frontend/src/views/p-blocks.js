import React,{Component} from 'react';
import { Route, Switch, withRouter } from 'react-router-dom';
import {ROUTER_PATHS} from '../constants'
import blocksList from '../components/blocks/blocksList'
import error from '../components/error'

class Blocks extends Component{
    constructor(props){
        super(props);
    }
    render(){
        return (
            <div className="p-common">
                <Switch>
                    <Route exact path={ROUTER_PATHS.BLOCKS} component={blocksList}></Route>
                    <Route component={error} />
                </Switch>
            </div>
        )
    }
}

export default withRouter(Blocks);
