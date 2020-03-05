import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import { observer, inject } from 'mobx-react';
import {withRouter} from 'react-router-dom';
import qs from 'qs';
import { toJS } from 'mobx';
import { Button, Table, Popconfirm,message} from 'antd';
import _ from 'lodash';
import Sortable from 'sortablejs';
import {Jt} from 'utils';
import '../../styles/category.less'

@inject('app')
@inject('category')
@observer
class CatList extends Component {
    constructor(props) {
        super(props);
        this.state = {
            unmounting: false
        }
        const { category, app: { selectSiteId,sites } } = props;
        this.selectSiteId = selectSiteId;
        //把频道顶层频道变成站点名称
        this.selectSiteName = _.find(sites, { id: selectSiteId }) ? _.find(sites, { id: selectSiteId }).name :'';
        category.updateStore({
            selectSiteName:this.selectSiteName
        })
        category.getCatTree({ siteId: selectSiteId });
    }
    componentDidMount(){
        const container = ReactDOM.findDOMNode(this.refs.table).querySelector('.ant-table-tbody');
        let parentId, draggedId, relatedId;
        this.sortable = new Sortable(container, {
            draggable: '.ant-table-row',
            onMove: ({ dragged, related }) => {
                if (dragged.children[0].getAttribute('data-pid') == related.children[0].getAttribute('data-pid')) {
                    parentId = dragged.children[0].getAttribute('data-pid');
                    draggedId = dragged.children[0].getAttribute('data-id');
                    relatedId = related.children[0].getAttribute('data-id');
                    return true;
                }
                return false;
            },
            onEnd: ({ oldIndex, newIndex, target }) => {
                if (oldIndex === newIndex) {
                    return;
                }
                const { category: { dataSource, updateStore, updateSorts} } = this.props;
                const CdataSource = toJS(dataSource);
                const draggedRecord = Jt.tree.getNode(CdataSource, draggedId);
                const relatedRecord = Jt.tree.getNode(CdataSource, relatedId);
                let records = Jt.tree.getNode(CdataSource, parentId).children;
                let oldPos, newPos;
                for (let i = 0; i < records.length; i++) {
                    if (records[i].id == draggedId) {
                        oldPos = i;
                    } else if (records[i].id == relatedId) {
                        newPos = i;
                    }
                    if (oldPos && newPos) {
                        break;
                    }
                }
                records.splice(oldPos, 1);
                records.splice(newPos, 0, draggedRecord);
                records = oldPos < newPos ? records.slice(oldPos, newPos + 1) : records.slice(newPos, oldPos + 1);
                const ids = [], sorts = [];
                for (let i = 0; i < records.length; i++) {
                    ids.push(records[i].id);
                    sorts.push(records[i].sort);
                }
                sorts.sort((a, b) => b - a);
                const updateArr = [];
                for (let i = 0; i < records.length; i++) {
                    records[i].sort = sorts[i];
                    updateArr.push({ id: ids[i], sort: sorts[i] });
                }
                updateStore({ dataSource: CdataSource });
                updateSorts(updateArr);
            }
        });
    }
    componentWillReact() {
        const { app: { selectSiteId, sites }, category } = this.props;
        if (this.selectSiteId != selectSiteId){  //如果选中站点selecetSiteId变了才重新请求频道树
            category.getCatTree({ siteId: selectSiteId });
            category.updateStore({   // 切换站点时清空选中频道树key
                expandedRows:[1]
            })
            this.selectSiteName = _.find(sites, { id: selectSiteId }) ? _.find(sites, { id: selectSiteId }).name : '';
            category.updateStore({
                selectSiteName:this.selectSiteName
            })
        }
        this.selectSiteId = selectSiteId;  //记录当前siteid以便比对
    }
    editCatg = (id,action) =>{  //添加下级频道
        const { history} = this.props;
        if(action === 'add'){
            history.push({
                pathname: '/category/edit',
                search: '?' + qs.stringify({ parentId: id })
            })
        }else{
            history.push({
                pathname: '/category/edit',
                search: '?' + qs.stringify({ id: id })
            })
        }
    }
    deleteCatg = (id) => {
        const { app: { selectSiteId }, category: { deleteCatg, getCatTree} } = this.props;
        const success = () => {
            message.success('删除成功',0.6);
            getCatTree({ siteId: selectSiteId });
        }
        deleteCatg(id,success);
    }
    onPreview = (id) => {
        const { category: { preview } } = this.props;
        var newTab = window.open('', '_blank');
        const callback = (res) => {
            const { code } = res;
            if (code == 0) {
                const location = res.data;
                if (!!location) {
                    newTab.location.href = location;
                } else {
                    newTab.close();
                    message.error('预览失败，请检查模版是否绑定')
                }
            }
        }
        preview(id, callback)
    }
    getColumns = () => {
        return [
            {
                title: '频道名称',
                key: 'name',
                dataIndex: 'name',
                width: 300,
                render: (val, record) => {
                    return {
                        children: <span>{val}</span>,
                        props: {
                            'data-id': record.id,
                            'data-pid': record.parentId
                        }
                    };
                }
            },
            {
                title: '操作',
                key: 'operation',
                width: 300,
                className: 'action-col',
                render: (val, record) => {
                    const actions = [];
                    if (record.parentId !== 0) {
                        actions.push(
                            <a key="action-1" onClick={() => this.editCatg(record.id,'edit')}>编辑</a>
                        );
                    }
                    if (record.parentId !== 0) {
                        actions.push(
                            <Popconfirm key="action-2" title='确定要删除吗？' onConfirm={() => this.deleteCatg(record.id)}>
                                <a>删除</a>
                            </Popconfirm>
                        );
                    }
                    if (record.parentId !== 0) {
                        actions.push(
                            <a key="action-preview" onClick={() => this.onPreview(record.id)}>
                                预览
                            </a>
                        );
                    }
                    actions.push([
                        <a key="action-3" onClick={() => this.editCatg(record.id,'add')}>
                            添加下级频道
                        </a>,
                    ]);
                    return actions;
                }
            },
        ];
    }
    onExpandedRowsChange = (expandedRows) => {
        const { category: { updateStore} } = this.props;
        if (!this.state.unmounting) {
            console.log(expandedRows)
            updateStore({expandedRows})
        }
    }
    componentWillUnmount() {
        this.state.unmounting = true;
    }
    render() {
        let { app: { selectSiteId }, category: { dataSource, loading, expandedRows} } = this.props;
        return (
            <div>
                <Table
                    simple
                    bordered
                    ref="table"
                    columns={this.getColumns()}
                    dataSource={toJS(dataSource)}
                    loading={loading}
                    rowKey={record => record.id}
                    pagination={false}
                    expandedRowKeys={toJS(expandedRows)}
                    onExpandedRowsChange={this.onExpandedRowsChange}
                />
            </div>
        )
    }
}

export default withRouter(CatList);
