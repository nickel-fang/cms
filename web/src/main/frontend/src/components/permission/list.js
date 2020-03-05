import React from 'react'
import {Table, Button} from 'antd'
import {observer,inject} from 'mobx-react'
import {toJS} from 'mobx'
import Search from './search'
import PermissionModal from './modal'
import qs from 'qs';
import {ROUTER_PATHS} from '../../constants'


@inject('permissions')
@observer

class List extends React.Component {
    constructor(props) {
        const {location: {search}, permissions} = props
        super(props)
        let query = qs.parse(search.substr(1));
        permissions.getInit(query)
    }

    onEdit(record) {
        const {permissions:{updateState, getUserPermission}} = this.props
        updateState({
            modalVisible:true,
            currentUser:{
                name:record.name,
                id:record.id
            }
        })
        getUserPermission()
    }

    onPageChange(page) {
        const {permissions:{ query, getInit }, history} = this.props
        const searchQuery = {
            ...query,
            pageNumber:page.current,
            pageSize:page.pageSize
        }
        history.push({
            pathname: ROUTER_PATHS.USER_PERMISSION,
            search: '?' + qs.stringify(searchQuery)
        })
        getInit(searchQuery)
    }

    getColumns() {
        const columns = [
            {
                title:'用户名',
                dataIndex:'username',
                key:'username'
            },
            {
                title:'姓名',
                dataIndex:'name',
                key:'name'
            },
            {
                title:'备注',
                dataIndex:'remark',
                key:'remark'
            },
            {
                title:'操作',
                dataIndex:'action',
                render:(text, record) => {
                    return (
                        <a onClick={this.onEdit.bind(this,record)}>修改</a>
                    )
                }
            }
        ]

        return columns
    }

    render() {
        const {permissions:{list, loading, pagination}} = this.props
        return (
            
            <div>
                <Search />
                <PermissionModal/>
                <Table
                    bordered
                    simple
                    columns={this.getColumns()}
                    dataSource={toJS(list)}
                    rowKey='id'
                    pagination={pagination}
                    loading={loading}
                    onChange={(page) => this.onPageChange(page)}
                />
            </div>
        )
    }
}

export default List
