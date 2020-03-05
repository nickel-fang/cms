import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';


@inject('app')
@observer
class Footer extends Component {
    constructor(props){
        super(props)
    }
    render() {
        const { app: { user }} =this.props;
        return (
            <div className="layout-footer">
                人 民 科 技 版 权 所 有，未 经 书 面 授 权 禁 止 使 用 Copyright © 2018 by Peopletech. All Rights Reserved
                {/*user.isystem ? user.isystem.footerText:'@2018'*/}
            </div>
        );
    }
}

export default Footer;
