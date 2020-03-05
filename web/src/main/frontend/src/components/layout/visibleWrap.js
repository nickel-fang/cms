import React,{Component} from 'react';
import { observer, inject } from 'mobx-react';
import { toJS } from 'mobx';

@inject('app')
@observer
class VisibleWrap extends Component{
    constructor(props){
        super(props);
    }
    render(){
        const {children,app:{user},permis} = this.props;
        const permissions = toJS(user.permissions)||[];
        const index = permissions.findIndex(function (value, index) {
            const reg = new RegExp('^' + value);
            return reg.test(permis);
        });
        return (
            (index >= 0) ? <span>{children}</span>:null
        )
    }
}
export default VisibleWrap;
