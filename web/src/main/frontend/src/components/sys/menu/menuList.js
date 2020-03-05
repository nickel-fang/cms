import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import Sortable from 'sortablejs';
import { observer, inject } from 'mobx-react';
import { toJS } from 'mobx';
import { Table, Icon, Popconfirm, Row, Col, Button} from 'antd';
import _ from 'lodash';
import qs from 'qs';
import { withRouter } from 'react-router-dom';
import { MENU_TYPES, CACHE_MENUS, ROUTER_PATHS} from '../../../constants';
import { sessionCache, Jt } from '../../../utils';
import '../../../styles/sys/menu-list.less';

const types = _.keyBy(MENU_TYPES, 'value');
@inject('app')
@inject('sys')
@observer
class MenuList extends Component {
    constructor(props) {
        super(props);
        const {sys, app:{user}} = props;
        sys.getMenuList({systemId:user.isystem.id});
    }
    componentDidMount() {
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
            onEnd: ({ oldIndex, newIndex }) => {
                const { app: { user, queryMenus }, sys: { menus, getMenuList, updateSorts, updateStore } } = this.props;
                const dataSource = toJS(menus);
                if (oldIndex === newIndex) {
                    return;
                }
                const draggedRecord = Jt.tree.getNode(dataSource, draggedId);
                const relatedRecord = Jt.tree.getNode(dataSource, relatedId);
                let records = parentId == 0 ? dataSource : Jt.tree.getNode(dataSource, parentId).children;
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
                updateStore({ menus: dataSource });
                const cb = () => {
                    sessionCache.delete(CACHE_MENUS);
                    queryMenus({ systemId: user.isystem.id });
                    getMenuList({ systemId: user.isystem.id })
                }
                updateSorts(updateArr, cb);
            }
        });

    }
    getColumns = () => {
        return [
            {
                title: '名称',
                dataIndex: 'name',
                key: 'name',
                width: 300,
                render: (val, record) => {
                    return {
                        children: <span><Icon type={record.icon} style={{ marginRight: 8 }} />{val}</span>,
                        props: {
                            'data-id': record.id,
                            'data-pid': record.parentId
                        }
                    }
                }
            },
            {
                title: '链接',
                dataIndex: 'href',
                key: 'href',
                width: 150
            },
            {
                title: '类型',
                key: 'type',
                dataIndex: 'type',
                width: 100,
                render: (val = 'NORMAL', record) => {
                    return types[val] && types[val].label;
                }
            },
            {
                title: 'KEY',
                dataIndex: 'code',
                key: 'code',
                width: 100
            },
            {
                title: '操作',
                key: 'action',
                width: 150,
                className: 'action-col',
                render: (val, record) => {
                    return [
                        <a key="action-edit" onClick={() => this.editMenu(record)}>编辑</a>,
                        <Popconfirm title='确定要删除吗？' key="action-delete" onConfirm={() => this.onDeleteMenu(record)}>
                            <a>删除</a>
                        </Popconfirm>,
                        <a key="action-add" onClick={() => this.addMenu(record)}>添加下级菜单</a>
                    ];
                }
            }
        ]
    }
    editMenu = (record) => {
        const { history } = this.props;
        history.push({
            pathname: ROUTER_PATHS.USERS_MENU_EDIT,
            search: '?' + qs.stringify({ id: record.id })
        })
    }
    addMenu = (record) => {
        const { history } = this.props;
        if(record==="add"){
            history.push({
                pathname: ROUTER_PATHS.USERS_MENU_EDIT
            })
        }else{
            history.push({
                pathname: ROUTER_PATHS.USERS_MENU_EDIT,
                search: '?' + qs.stringify({ parentId:record.id })
            })
        }
    }
    onDeleteMenu = (record)=>{
        const { app: { queryMenus }, sys: { getMenuList, deleteMenu }, app: { user } } = this.props;
        const cb = () => {
            sessionCache.delete(CACHE_MENUS);
            queryMenus({ systemId: user.isystem.id });
            getMenuList({ systemId: user.isystem.id })
        }
        deleteMenu(record.id, cb)
    }
    componentWillUnmount() {
        this.sortable.destroy();
    }
    render() {
        const { sys: { menus, loading }} = this.props;
        return (
            <div>
                <Row>
                    <Col span={24} style={{ textAlign: 'right', marginBottom:'20px' }}>
                        <Button type="primary" onClick={this.addMenu.bind(this,'add')}>添加菜单</Button>
                    </Col>
                </Row>
                <Table
                    simple
                    bordered
                    ref="table"
                    loading={loading}
                    columns={this.getColumns()}
                    rowKey={record => record.id}
                    dataSource={toJS(menus)}
                    pagination={false}
                />
            </div>
        )
    }
}

export default withRouter(MenuList);
