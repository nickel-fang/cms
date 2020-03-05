import React,{Component} from 'react';
import { observer, inject } from 'mobx-react';
import { toJS } from 'mobx';
import { Table, Button, Popconfirm ,message, Form, Input, Row, Col} from 'antd';
import { withRouter } from 'react-router-dom';
import qs from 'qs';

const FormItem = Form.Item;
@inject('site')
@inject('app')
@observer
class SiteList extends Component{
    constructor(props){
        super(props)
    }

    onCreate = () => {
        const { history } = this.props;
        history.push('/site/edit');
    }
    onEdit = (id) => {
        const { history } = this.props;
        history.push({
            pathname:'/site/edit',
            search: '?' + qs.stringify({ id })
        })
    }
    onDelete = (id) => {
        const {
            site: { getList, deleteListItem },
            app
        } = this.props;

        const callback = (code, msg) => {
            if(code === 0) {
                message.success('删除成功',0.6)
                getList();
                app.deleteSites();
            }else{
                message.error(msg)
            }
        };
        deleteListItem(id, callback);
    }
    getColumns = () =>{
        return [
            // {
            //     title: 'ID',
            //     dataIndex: 'id',
            //     width: 100
            // },
            {
                title: '站点名称',
                dataIndex: 'name',
                width: 100
            },
            {
                title: '站点域名',
                dataIndex: 'domainPath',
                width: 100
            },
            {
                title: '站点路径',
                dataIndex: 'path',
                width: 100
            },
            {
                title: '站点描述',
                dataIndex: 'description',
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
                            style={{'marginRight':'5px'}}
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
    onSearch = () => {
        const { form: { getFieldsValue }, site: { getList, updateStore }} = this.props;
        const data = getFieldsValue();
        for(let key in data) {
            if(!data[key]) {
                delete data[key]
            }
        }
        data.pageNumber = 1;
        updateStore({ query: data})
        getList(data)
    }
    onPageChange = (page) => {
        const {
            site: { pagination, getList, query }
        } = this.props;
        getList({ ...query, pageNumber: page.current, pageSize: page.pageSize})
    }
    render(){
        const { site: { pagination, dataSource, loading}, form: { getFieldDecorator }} = this.props;
        return (
            <div>
                <Form>
                    <Row>
                        <Col span={8}>
                            <FormItem label="站点名称" {...{ labelCol: {span: 6}, wrapperCol: { span: 16} } }>
                                {getFieldDecorator('name', {
                                    initialValue : ''
                                })(
                                    <Input />
                                )}
                            </FormItem>
                        </Col>
                        <Col span={6} style={{"marginTop": "4px"}}>
                            <Button
                                type="primary"
                                onClick={this.onSearch}
                            >
                                查询
                            </Button>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <Button
                                type="primary"
                                onClick={this.onCreate}
                                style={{'marginBottom':'20px'}}
                            >
                                添加站点
                            </Button>
                        </Col>
                    </Row>
                </Form>

                <Table
                    bordered
                    columns={this.getColumns()}
                    rowKey={record => record.id}
                    dataSource={toJS(dataSource)}
                    onChange={this.onPageChange}
                    pagination={pagination}
                    loading={loading}
                />
            </div>
        )
    }
}

export default withRouter(Form.create()(SiteList));
