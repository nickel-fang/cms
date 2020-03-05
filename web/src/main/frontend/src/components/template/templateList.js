import React,{Component} from 'react';
import { withRouter} from 'react-router-dom';
import {observer,inject} from 'mobx-react';
import {toJS} from 'mobx';
import { Table, Button, Popconfirm, message, Modal, Upload, Icon} from 'antd';
import qs from 'qs';
import Toolbar from './toolbar';

@inject('app')
@inject('template')
@observer
class TemplateList extends Component{
    constructor(props){
        super(props);
        const { template, app: { selectSiteId, sites } } = props;
        this.selectSiteId = selectSiteId;
        template.getTemplateList({ siteId: selectSiteId });
    }
    componentWillReact() {
        const { app: { selectSiteId, sites }, template } = this.props;
        if (this.selectSiteId != selectSiteId) {  //如果选中站点selecetSiteId变了才重新请求频道树
            template.getTemplateList({ siteId: selectSiteId });
        }
        this.selectSiteId = selectSiteId;  //记录当前siteid以便比对
    }
    onEdit = (id) => {
        const { history } = this.props;
        history.push({
            pathname: '/template/edit',
            search: '?' + qs.stringify({ id })
        })
    };
    onDelete = (id) => {
        const { template: { deleteTemp, getTemplateList, query } } = this.props;
        this.loading = true;
        const success = () => {
            message.success('删除成功',0.6);
            const Cquery = toJS(query);
            getTemplateList(Cquery);
        };
        deleteTemp(id, success);
    };
    getColumns = () =>{
        return [
            {
                title: '模版名称',
                dataIndex: 'name',
                width: 100
            },
            {
                title: '模版类型',
                dataIndex: 'type',
                width: 100,
                render: (text, record) => {
                    if (text == "category") {
                        return '频道'
                    } else if (text == "block") {
                        return '区块'
                    } else if (text == "detail") {
                        return '详情'
                    }
                }
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
                            onClick={this.onEdit.bind(this, id)}
                            style={{ 'marginRight': '5px' }}
                        >
                            编辑
                        </a>,
                        <Popconfirm
                            key="action-2"
                            title="确定删除吗"
                            onConfirm={this.onDelete.bind(this, id)}
                        >
                            <a>删除</a>
                        </Popconfirm>
                    ];
                }
            }
        ]
    }
    onPageChange = (page) => {
        const {
            template: { pagination, getTemplateList, query }
        } = this.props;
        getTemplateList({...query, siteId: this.selectSiteId, pageNumber: page.current, pageSize: page.pageSize })
    }
    render(){
        const { template: { loading, dataSource, pagination } } = this.props;
        return (
            <div>
                <Toolbar />
                <Table
                    bordered
                    columns={this.getColumns()}
                    rowKey={record => record.id}
                    dataSource={toJS(dataSource)}
                    pagination={pagination}
                    onChange={this.onPageChange}
                    loading={loading}
                />
            </div>
        )
    }
}

export default withRouter(TemplateList);
