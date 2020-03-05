import React from 'react'
import {Switch, Route, withRouter} from 'react-router-dom'
import {ROUTER_PATHS} from '../constants'
import List from '../components/permission/list'
import {observer} from 'mobx-react'

@observer

class PagePermission extends React.Component {
    constructor (props) {
        super(props)
    }

    render(){
        return(
            <div className="p-common">
                <Switch>
                    <Route exact path={ROUTER_PATHS.USER_PERMISSION} component={List}></Route>
                </Switch>
            </div>
        )
    }
}

export default withRouter(PagePermission)