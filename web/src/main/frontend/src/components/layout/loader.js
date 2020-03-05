import React, {Component} from 'react';
import '../../styles/loader.less';

class Loader extends Component{
    constructor(props){
        super(props);
    }
    render(){
        const { fullScreen, loading} = this.props;
        return (<div className={['loader',fullScreen?'fullScreen':undefined, loading?undefined:'hidden'].join(' ')}>
            <div className="warpper">
                <div className="inner"/>
                <div className="text">加载中...</div>
            </div>
        </div>)
    }
}

export default Loader
