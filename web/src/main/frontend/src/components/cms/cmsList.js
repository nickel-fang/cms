import React, {Component} from 'react';
import ReactDOM from 'react-dom';
import {observer, inject} from 'mobx-react';
import {toJS} from 'mobx';
import {withRouter} from 'react-router-dom';
import { Tree, Table, Popconfirm, Row, Col, message} from 'antd';
import Sortable from 'sortablejs'
import _ from 'lodash';
import qs from 'qs';
import {ROUTER_PATHS} from '../../constants'
import Toolbar from './toolbar';
import VisibleWrap from '../layout/visibleWrap';
import { Jt } from 'utils';

const TreeNode = Tree.TreeNode;

@inject('app')
@inject('cms')
@observer
class CmsList extends Component {
    constructor(props) {
        super(props);
        const {cms, app: {selectSiteId, sites}} = props;
        this.selectSiteId = selectSiteId;
        //把频道的顶层频道变成站点名称
        this.selectSiteName = _.find(sites, {id: selectSiteId}) ? _.find(sites, {id: selectSiteId}).name : '';
        cms.updateStore({
            selectSiteName:this.selectSiteName,
            siteId: selectSiteId
        });
        cms.getInit({siteId: selectSiteId});
    }

    componentWillReact() {
        const { app: { selectSiteId, sites }, cms: { query, getInit, updateStore}} = this.props;
        if (this.selectSiteId != selectSiteId) {  //如果选中站点selecetSiteId变了才重新请求频道树
            delete query.categoryId;
            getInit({siteId: selectSiteId});
            this.selectSiteName = _.find(sites, {id: selectSiteId}) ? _.find(sites, {id: selectSiteId}).name : '';
            updateStore({
                selectSiteName:this.selectSiteName,
                siteId: selectSiteId
            });
        }
        this.selectSiteId = selectSiteId;  //记录当前siteid以便比对
    }

    componentDidMount() {
        const container = ReactDOM.findDOMNode(this.refs.table).querySelector('.ant-table-tbody');
        this.sortable = new Sortable(container, {
            draggable: '.ant-table-row',
            onEnd: ({oldIndex, newIndex}) => {
                if (oldIndex === newIndex) {
                    return;
                }
                let minIndex = oldIndex, maxIndex = newIndex;
                if (oldIndex > newIndex) {
                    minIndex = newIndex;
                    maxIndex = oldIndex;
                }
                const {dataSource, updateStore, updateSorts} = this.props.cms;
                const CdataSource = toJS(dataSource)
                const relatedRecord = CdataSource[newIndex];
                const draggedRecord = CdataSource.splice(oldIndex, 1)[0];
                draggedRecord.stick = relatedRecord.stick;
                CdataSource.splice(newIndex, 0, draggedRecord);
                const chgRecords = CdataSource.slice(minIndex, maxIndex + 1);
                const ids = [], weights = [];
                for (let i = 0, len = chgRecords.length; i < len; i++) {
                    ids.push(chgRecords[i].id);
                    weights.push(chgRecords[i].weight);
                }
                weights.sort((a, b) => {
                    return b - a;
                });

                for (let i = 0, len = chgRecords.length; i < len; i++) {
                    chgRecords[i].weight = weights[i];
                }

                const updateArr = [];
                for (let i = 0, len = ids.length; i < len; i++) {
                    updateArr.push({id: ids[i], weight: weights[i]});
                }

                CdataSource.splice(minIndex, chgRecords.length, ...chgRecords);
                updateStore({dataSource: CdataSource});
                updateSorts(updateArr);
            }
        });
    }

    componentWillUnmount() {
        this.sortable.destroy();
    }

    renderTreeNode = (data) => {
        return data.map((item, index) => {
            return (
                <TreeNode
                    title={item.name}
                    key={item.key}
                >
                    {item.children && this.renderTreeNode(item.children)}
                </TreeNode>
            )
        })
    };
    onPreview = (id) => {
        const { cms:{preview} } = this.props;
        var newTab = window.open('', '_blank');
        const callback = (res) => {
            const { code } = res;
            if (code == 0) {
                const location = res.data;
                if(!!location){
                    newTab.location.href = location;
                }else{
                    newTab.close();
                    message.error('该频道没有绑定详情模版')
                }
            }
        }
        preview(id,callback)
    }
    getColumns = () => {
        const {cms: {query}} = this.props;
        let colArr = [
            {
                title: '标题',
                key: 'title',
                dataIndex: 'title',
                width: 300,
                render: (text, record) => {
                    {
                        if(query.delFlag === '99') {
                            return (
                                <span>{text}</span>
                            )
                        }else {
                            return (
                                <a onClick={this.onEdit.bind(this, record.id, record.articleId)}>{text}</a>
                            )
                        }
                    }
                }
            },
            {
                title: '类型',
                key: 'type',
                dataIndex: 'type',
                width: 60,
                render: (text, record) => {
                    switch (text) {
                        case 'common':
                            return '新闻';
                        case 'video':
                            return '视频';
                        case 'audio':
                            return '音频';
                        case 'image':
                            return '图集';
                        default:
                            return ''
                    }
                }
            },
            {
                title: '录入时间',
                key: 'createAt',
                dataIndex: 'createAt',
                width: 150,
            },
            {
                title: '发布时间',
                key: 'publishDate',
                dataIndex: 'publishDate',
                width: 150,
            }
        ];
        if (query.delFlag == "99") {
            colArr.push(
                {
                    title: '状态',
                    key: 'status',
                    dataIndex: 'delFlag',
                    width: 100,
                    render: (text, record) => {
                        if(text==1){
                            return '草稿'
                        } else if (text == 2){
                            return '待审核'
                        } else if (text == 4){
                            return '待修改'
                        } else if (text == 0){
                            return '已上线'
                        } else if (text == 6) {
                            return '已下线'
                        } else if (text == 99) {
                            return '全部'
                        }
                    }
                }
            )
        }
        let renderColArr = colArr.concat([
            {
                title: '操作人',
                key: 'operator',
                dataIndex: 'operator',
                width: 120,
                render: (text, record) => {
                    let renderArr = [];
                    if (record.createUser) {
                        renderArr.push(
                            <div key="ope-1">录入人：{record.createUser}</div>
                        )
                    }
                    if (record.auditUser) {
                        renderArr.push(
                            <div key="ope-2">审核人：{record.auditUser}</div>
                        )
                    }
                    return renderArr;
                }
            },
            {
                title: '操作',
                width: 120,
                className: 'action',
                render: (text, record) => {   //此处之所以注释多是因为业务逻辑没确定
                    const id = record.id;
                    let renderArr = [
                        <VisibleWrap key="action-1" permis="cms:articles:edit">
                            <a
                                key="action-1"
                                onClick={this.onEdit.bind(this, id, record.articleId)}
                                style={{ 'marginRight': '5px' }}
                            >
                                编辑
                            </a>
                        </VisibleWrap>
                    ]
                    if (query.delFlag != 0 && query.delFlag !== '99') {  //不是上线
                        renderArr.push(
                            <VisibleWrap key="action-2" permis="cms:articles:delete">
                                <Popconfirm
                                    title="确定删除吗"
                                    onConfirm={this.onDelete.bind(this, id)}
                                >
                                    <a style={{ 'marginRight': '5px' }}>删除</a>
                                </Popconfirm>
                            </VisibleWrap>
                        )
                    }
                    if (query.delFlag == 0) {  //上线
                        renderArr.push([
                            <VisibleWrap key="action-3" permis="cms:articles:onoff">
                                <a
                                    style={{ 'marginRight': '5px' }}
                                    onClick={this.offLine.bind(this, id, 6)}
                                >
                                    下线
                                </a>
                            </VisibleWrap>
                        ])
                    }
                    else if (query.delFlag == 2) {   //待审核
                        renderArr.push(
                            <VisibleWrap key="action-6" permis="cms:articles:onoff">
                                <a
                                    style={{ 'marginRight': '5px' }}
                                    onClick={this.handleOnSave.bind(this, id, 0)}
                                >
                                    上线
                                </a>
                            </VisibleWrap>
                        )
                    }
                    else if (query.delFlag == 6) {  //下线
                        renderArr.push(
                            <VisibleWrap key="action-9" permis="cms:articles:onoff">
                                <a
                                    style={{ 'marginRight': '5px' }}
                                    onClick={this.handleOnSave.bind(this, id, 0)}
                                >
                                    上线
                                </a>
                            </VisibleWrap>
                        )
                    }else if(query.delFlag === '99') {
                        renderArr = []
                    }
                    renderArr.push(
                        <a
                            key="action-preview"
                            style={{ 'marginRight': '5px' }}
                            onClick={this.onPreview.bind(this, id)}
                        >
                            预览
                        </a>
                    )
                    return renderArr;
                }
            }
        ])
        return renderColArr;
    };
    onEdit = (id, articleId) => {
        const { history, cms: { updateStore }} = this.props;
        updateStore({articleId})
        history.push({
            pathname: ROUTER_PATHS.ARTICLES_EDIT,
            search: '?' + qs.stringify({id})
        })
    };
    onDelete = (id) => {
        const {cms: {deleteArt, articleList, getInit, query}} = this.props;
        const success = () => {
            const Cquery = toJS(query);
            getInit(Cquery);
        };
        deleteArt(id, success);
    };
    handleOnSave = (id, delFlag) => {//点击上线
        const {cms: {getArtItem, loading}} = this.props;
        const returnValues = () => {
            const {cms: {curArt, onSaveAudit, dataSource, articleList, getInit, query}} = this.props;
            const CdataSource = toJS(dataSource);
            const data = {
                articleData: toJS(curArt).articleData,
                ...CdataSource.find(item => item.id === id),
                delFlag
            };
            const callback = () => {
                const Cquery = toJS(query);
                getInit(Cquery)
            };
            onSaveAudit(data, callback)
        };
        getArtItem(id, returnValues);
    };
    offLine = (id) => { //点击下线
        const {cms: {getInit, onOffArt, query}} = this.props;
        const callBack = () => {
            const Cquery = toJS(query);
            getInit(Cquery)
        };
        onOffArt(id, callBack);
    };
    onExpandChange = (expandedKeys, {node,expanded}) => {
        const { cms: { updateStore, catTree }} = this.props;
        if(!expanded){
            const key = node.props.eventKey;
            const children = Jt.tree.getAllChildren(toJS(catTree), key);   // 如果有子频道展开的情况下关闭父频道，直接也关闭所有子频道
            if(!!children.length){
                children.forEach(function(value,index){
                    if (_.indexOf(expandedKeys,value)!==-1){
                        expandedKeys.splice(_.indexOf(expandedKeys, value),1);
                    }
                })
            }
        }
        updateStore({ expandedKeys})
    }
    onSelect = (id) =>{
        const { cms: { articleList, updateStore,query } } = this.props;
        const Cquery = toJS(query);
        if(id.length === 0) { return; } // 当用户重复点击一个树节点时，该节点不被取消
        Cquery.categoryId = id[0];
        Cquery.pageNumber = 1;
        articleList(Cquery);
        updateStore({query: Cquery});
    };
    onPageChange = (page) => {
        const {
            cms: {articleList, query}
        } = this.props;
        articleList({...query, pageNumber: page.current, pageSize: page.pageSize})
    };

    render() {
        const {cms: {catTree, expandedKeys, selArts, updateStore, query, dataSource, loading, pagination}, app: {selectSiteId}} = this.props;
        const CcatTree = toJS(catTree);
        const CselArts = toJS(selArts);
        const selectedRowKeys = CselArts.map((art) => art.id);
        const rowSelection = {
            selectedRowKeys,
            onChange: (ids, arts) => {
                updateStore({
                    selArts: arts
                });
            }
        };
        return (
            <div className="cms-wrap">
                <div className="cat-tree">
                    <Tree
                        selectedKeys={[`${toJS(query.categoryId)}` || '1']}
                        expandedKeys={toJS(expandedKeys)}
                        onExpand={this.onExpandChange}
                        onSelect={this.onSelect}
                        defaultExpandedKeys={['1']}
                    >
                        {this.renderTreeNode(CcatTree)}
                    </Tree>
                </div>
                <div className="cms-list">
                    {<Toolbar />}
                    <Table
                        ref="table"
                        className="draggable-table"
                        simple
                        bordered
                        columns={this.getColumns()}
                        dataSource={toJS(dataSource)}
                        loading={loading}
                        rowKey={record => record.id}
                        pagination={pagination}
                        onChange={this.onPageChange}
                        rowSelection = { query.delFlag === '99' ? null : rowSelection }

                    />
                </div>
            </div>
        )
    }
}

export default CmsList
