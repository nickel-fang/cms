
import React,{Component} from 'react';
import {observer,inject} from 'mobx-react';
import {toJS} from 'mobx';
import { Form, Row, Col, Button, Upload, Icon, DatePicker, Input, Table, Select, message, Popconfirm, Divider} from 'antd';
import _ from 'lodash';
import { ROUTER_PATHS } from '../../constants';
import {Jt} from '../../utils';
import qs from 'qs';

@inject('app')
@inject('roles')
@observer
class List extends Component{
    constructor(props) {
        super(props)
        const {roles, app: { selectSiteId} } = props

        roles.getInit()
    }
    // componentWillReact() {
    //     const {roles} = this.props

    // }
    addRole() {
        const {history} = this.props
        history.push(ROUTER_PATHS.USERS_ROLE_EDIT)
    }

    editRole(id) {
        const { history } = this.props;
        history.push({
            pathname: ROUTER_PATHS.USERS_ROLE_EDIT,
            search: '?' + qs.stringify({id})
        })
    }

    deleteRole(id) {
        console.log(id)
        const { roles: { deleteRoleItem, getInit }} = this.props;
        const success = () => {
            getInit();
        };
        deleteRoleItem(id, success);
    }

    titleRender = () => {
        return (
            <div style={{textAlign: 'right'}}>
                <Button type="primary" onClick={() => this.addRole()}>添加角色</Button>
            </div>
        );
    }

    getColumns = () => {
        const {editRole, deleteRole} = this.props;
        return [
            {
                title: '角色名称',
                dataIndex: 'name',
                width: 200
            },
            {
                title: '备注',
                dataIndex: 'remark',
                width: 200
            },
            {
                title: '操作',
                dataIndex: 'action',
                width: 200,
                className: 'action-col',
                render: (val, record) => {
                    return [                       
                        <a key="edit" onClick={() => this.editRole(record.id)}>修改</a>,
                        <Divider key="divider" type="vertical" />,
                        <Popconfirm key="delete" title='确定要删除吗？' onConfirm={() => this.deleteRole(record.id,this)}>
                            <a>删除</a>
                        </Popconfirm>
                    ];
                }
            }
        ];
    }

    render() {
        const {roles:{list,loading, pagination, onPageChange}} = this.props
            return (
                <Table
                    simple
                    bordered
                    loading={loading}
                    columns={this.getColumns()}
                    rowKey='id'
                    dataSource={toJS(list)}
                    pagination={pagination}
                    title={this.titleRender}
                    onChange={(page) => onPageChange(page)}
                />
            );
    }
}
export default List;