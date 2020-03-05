import React, { Component } from 'react';
import { Button, Icon, Row, Col, Input, Modal, Table, Tooltip, Collapse} from 'antd';
import { observer, inject } from 'mobx-react';
import { toJS } from 'mobx';
import '../../styles/template/blocks.less';
const CollapsePanel = Collapse.Panel;
@inject('template')
@observer
class Blocks extends Component{
    constructor(props){
        super(props)
        let { value = {} } = this.props;
        this.state = {
            blocks: value.blocks||[],
            currentBlkIndex:null
        };
    }
    componentWillReceiveProps(nextProps) {
        if (nextProps.value) {
            const value = nextProps.value;
            // const blocks = value.blocks || [];
            const blocks = this.handleBlocksSort(value.blocks) || [];
            this.setState({ blocks });
        }
    }

    handleBlocksSort = (blocks) => {
        if(!blocks) {
            return false;
        }
        let newBlocks = [];
        for(let i = 0; i < blocks.length; i++){
            for(let j = 0; j< blocks.length; j++){
            if(blocks[j].sort == i ){
                newBlocks.push(blocks[j])
            }
            }
        }
        return newBlocks
    }
    getColumns = () => {
        return [
            {
                title: '模版名称',
                dataIndex: 'name',
                width: 100
            },
            {
                title: '模版描述',
                dataIndex: 'description',
                width: 100
            },
            {
                title: '模版路径',
                dataIndex: 'ftlPath',
                width: 100
            },
            {
                title: '操作',
                width: 100,
                className: 'action',
                render: (text, record) => {
                    const id = record.id;
                    return [
                        <a
                            key="action-1"
                            onClick={this.onSelect.bind(this, id, record)}
                            style={{ 'marginRight': '5px' }}
                        >
                            选择
                        </a>
                    ];
                }
            }
        ]
    }
    onSelect = (id, record) =>{
        const blocks = this.state.blocks;
        const currentBlkIndex = this.state.currentBlkIndex;
        blocks.map((item,i) => {
            if (i === currentBlkIndex){
                item.blockTemplateId = id;
                item.blockTemplateName = record.name;
                item.blockTemplateUrl = record.ftlPath;
            }
        });
        this.setState({ blocks,visible:false });
        this.triggerChange({ blocks });
    }
    handleInputChange = (e, index, key) => {
        const value = e.target.value;
        const blocks = this.state.blocks;
        blocks.map(item => {
            if (item.sort == index) {
                item[key] = value;
            }
        });
        this.setState({ blocks });
        this.triggerChange({ blocks });
    }
    onDeleteTemplate = (index) => {
        const blocks = this.state.blocks;
        blocks.map((item,i) => {
            if (i === index) {
                item.blockTemplateId = null;
                item.blockTemplateName = '';
            }
        });
        this.setState({ blocks });
        this.triggerChange({ blocks });
    }
    add = () => {
        const blocks = this.state.blocks;
        blocks.map((item, i) => { item.sort = i });
        blocks.push({
            sort: blocks.length,
            name: '',
            description: '',
            blockTemplateId:null
        });
        this.setState({ blocks });
        this.triggerChange({ blocks });
    }
    triggerChange(changedValue) {
        const onChange = this.props.onChange;
        if (onChange) {
            onChange({ blocks:this.state.blocks, ...changedValue });
        }
    }
    show = (index) => {
        this.setState({
            visible: true,
            currentBlkIndex: index
        });
    }
    hide() {
        this.setState({
            visible: false
        });
    }
    remove = (item) => {
        const { template: { deleteBlock }} = this.props;
        const blocks = this.state.blocks.filter(block => block.sort !== item.sort);
        blocks.map((block, i) => { block.sort = i });   //张翔宇后加的
        if(!!item.id){
            deleteBlock(item.id)
        }
        this.setState({ blocks });
        this.triggerChange({ blocks });
    }
    move(sort, direc) {
        const blocks = this.state.blocks;
        if (direc == 'up' && sort == 0 || direc == 'down' && sort == blocks.length - 1) {
            return;
        }
        if (direc == 'up') {
            sort = sort - 1;
        }
        for (let i = 0, len = blocks.length; i < len; i++) {
            if (blocks[i].sort == sort) {
                const tmp1 = { ...blocks[i] };
                const tmp2 = { ...blocks[i + 1] }
                blocks[i] = { ...tmp2, sort: tmp1.sort };
                blocks[i + 1] = { ...tmp1, sort: tmp2.sort };
                break;
            }
        }
        this.setState({ blocks });
        this.triggerChange({ blocks });
    }
    getTemplateName = (item,index) => {
        if (item.blockTemplateName ){
            return (
                <span style={{ 'marginLeft': '10px' }}>{item.blockTemplateName}&nbsp;{item.blockTemplateUrl}
                    <Icon className="template-delete" type="close" onClick={this.onDeleteTemplate.bind(this, index)} />
                </span>
            )
        }
        return null
    }
    onPageChange = (page) => {
        const {
            siteId,
            template: { getTemplateList }
        } = this.props;
        getTemplateList({ siteId, pageNumber: page.current, pageSize: page.pageSize })
    }
    render(){
        const { blocks=[], visible } = this.state;
        const {template: { loading, dataSource, pagination }} = this.props;
        const formItems = blocks.map((item, index)=>{
            return (
                <div key={index} className="blocks-item">
                    <Row type="flex" justify="space-between" align="top">
                        <Col span={18}>
                            <Button className="template-btn" type="primary" onClick={this.show.bind(this, index)}>
                                对应模版
                            </Button>
                            {this.getTemplateName(item, index)}
                        </Col>
                    </Row>
                    <Row style={{'marginBottom':'10px'}}>
                        区块标签：{item.tag}
                    </Row>
                    <Row gutter={16}>
                        <Col span={7}>
                            <Input
                                className="template-input"
                                value={item.name}
                                placeholder="请输入区块名称"
                                onChange={(e) => this.handleInputChange(e, index, 'name')}
                            />
                        </Col>
                        <Col span={7}>
                            <Input
                                className="template-input"
                                value={item.description}
                                placeholder="请输入区块描述"
                                onChange={(e) => this.handleInputChange(e, index, 'description')}
                            />
                        </Col>
                        <Col span={5}>
                            <Tooltip title="删除" placement="top">
                                <Icon
                                    className="del-btn"
                                    type="minus-circle-o"
                                    onClick={() => this.remove(item)}
                                />
                            </Tooltip>
                            {(index!=0)&&<Tooltip title="向上移动" placement="top">
                                <Icon type="up-circle-o" disabled={index == 0} onClick={() => this.move(item.sort, 'up')}/>
                            </Tooltip>}
                            {((index+1)!=blocks.length)&&<Tooltip title="向下移动" placement="top">
                                <Icon type="down-circle-o" disabled={index == blocks.length - 1} onClick={() => this.move(item.sort, 'down')} />
                            </Tooltip>}
                        </Col>
                    </Row>
                </div>
            )
        });
        const modalOpts = {
            title: '',
            visible,
            width: '80%',
            footer: null,
            onCancel: this.hide.bind(this)
        };
        return (
            <div>
                <Modal {...modalOpts}>
                    <Table
                        simple
                        size="middle"
                        columns={this.getColumns()}
                        dataSource={toJS(dataSource)}
                        pagination={pagination}
                        onChange={this.onPageChange}
                        loading={loading}
                        rowKey={record => record.id}
                    />
                </Modal>
                <Col offset={5} span={18}>
                    <Collapse defaultActiveKey={['block']}>
                        <CollapsePanel key="block" header="区块">
                            {formItems}
                            <div>
                                <Button type="dashed" onClick={this.add.bind(this)}>
                                    <Icon type="plus" /> 添加区块
                                </Button>
                            </div>
                        </CollapsePanel>
                    </Collapse>
                </Col>
            </div>
        )
    }
}

export default Blocks;
