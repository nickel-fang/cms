import React,{Component} from 'react';

class Tree extends Component{
    constructor(props){
        super(props);
    }
    mouseOver = (info,title) => {
        console.log(info)
        console.log(title)
    }
    render(){
        const {title} = this.props;
        return (
            <div onMouseOver={this.mouseOver.bind(this,title)}>ad{title}</div>
        )
    }
}

export default Tree;
