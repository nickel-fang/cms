import React,{Component} from 'react';
import {observer,inject} from 'mobx-react';
import {toJS} from 'mobx';
import { Form, Row, Col, Button, Upload, Icon, Menu, DatePicker, Dropdown, Input, Table, Select, message, Popconfirm} from 'antd';
import _ from 'lodash';
import { ROUTER_PATHS } from '../../constants';
import {Jt} from '../../utils';
import qs from 'qs';
import VisibleWrap from '../layout/visibleWrap';

const MenuItem = Menu.Item;
const FormItem = Form.Item;
const RangePicker = DatePicker.RangePicker;
const dateFormat = 'YYYY-MM-DD HH:mm:ss';
const Option = Select.Option;

@inject('app')
@inject('source')
@observer
class SourceList extends Component{
    constructor(props){
        super(props);
        const { source, app: { selectSiteId, sites } } = props;
        this.selectSiteId = selectSiteId;
        source.getInit({ siteId: selectSiteId });
    }
    componentWillReact() {
        const { app: { selectSiteId, sites }, source: { articleList, getInit } } = this.props;
        if (this.selectSiteId != selectSiteId) {  //如果选中站点selecetSiteId变了才重新请求频道树
            getInit({ siteId: selectSiteId });
            this.selectSiteName = _.find(sites, { id: selectSiteId }) ? _.find(sites, { id: selectSiteId }).name : '';
        }
        this.selectSiteId = selectSiteId;  //记录当前siteid以便比对
    }
    onCreateArt = () => {
        const { history, source: { importType, setImportType}} = this.props;
        setImportType(0);
        history.push(ROUTER_PATHS.SOURCE_EDIT)
    }
    onEdit = (id) => {
        const { history } = this.props;
        history.push({
            pathname: ROUTER_PATHS.SOURCE_EDIT,
            search: '?' + qs.stringify({id})
        })
    }
    onDelete = (id) => {
        const { source: { deleteArt, getInit, query }} = this.props;
        const success = () => {
            const Cquery = toJS(query);
            getInit(Cquery);
        };
        deleteArt(id, success);
    }
    getColumns = () => {
        const columns = [{
            title: '标题',
            dataIndex: 'title',
            render: (text, record)=> <a onClick={this.onEdit.bind(this,record.id)}>{text}</a>,
          }, {
            title: '来源',
            dataIndex: 'importType',
            render: (text) => {
                switch(text) {
                    case 0: return <span>手工导入</span>;
                        break;
                    case 1: return <span>外部导入</span>;
                        break;
                    default: return <span>外部导入</span>;
                }
            }
          }, {
            title: '录入时间',
            dataIndex: 'createAt',
          }, {
              title: '操作',
              dataIndex: '',
              key: 'action-option',
              render: (text, record) =><div>
                    <VisibleWrap key="action-1" permis="cms:source:edit">
                        <a
                            style={{'marginRight': '5px'}}
                            onClick={this.onEdit.bind(this, record.id)}
                        >编辑</a>
                    </VisibleWrap>
                    <VisibleWrap key="action-2" permis="cms:source:delete">
                        <Popconfirm
                            title="确定删除吗"
                            onConfirm={this.onDelete.bind(this, record.id)}
                        >
                            <a style={{ 'marginRight': '5px' }}>删除</a>
                        </Popconfirm>
                    </VisibleWrap>
                </div>
              }
        ];
        return columns;
    }

    /**
     * 获取表单数据
     */
    getFormData = () => {
        const { source: { updateStore, articleList, dataSource}, form: { getFieldsValue }} = this.props;
        let data = getFieldsValue();
        if (!Jt.array.isEmpty(data.publishDate)) {
            data.beginTime = data.publishDate[0].format(dateFormat);
            data.endTime = data.publishDate[1].format(dateFormat);
        }
        delete data.publishDate;
        for(let key in data){  //去掉为空的搜索项
            if(!data[key]){
                delete data[key]
            }
        }
        return data;
    }

    onSearch = () => {
        const { source: { updateStore, articleList, dataSource}, form: { getFieldsValue }} = this.props;
        const data = this.getFormData();
        data.pageNumber = 1; // 当查询时，需要把当前页设置为第一页，否则会带入当前页数被查询。
        updateStore({query: data});
        articleList(data);
    }

    onPageChange = (page) => {
        const {
            source: {articleList, query}
        } = this.props;
        articleList({...query, pageNumber: page.current, pageSize: page.pageSize})
    }

    onUploaderChange = (info) => {
        const {
            source: { articleList }
        } = this.props;
        const status = info.file.status;
        if (status === 'done') {
            message.success(`${info.file.name} 导入成功.`);
            articleList();
        } else if (status === 'error') {
            message.error(`${info.file.name} 导入失败.`);
        }
    }
    componentWillUnmount() {
        const { source: { query, updateStore }} = this.props;
        updateStore({query: {}})
    }
    onImportTypeChange = (value) => {
        const { source: { updateStore, articleList, dataSource}, form: { getFieldsValue }} = this.props;
        const data = this.getFormData();
        data.pageNumber = 1;
        data.importType = value
        updateStore({query: data});
        articleList(data);
    }
    onBatchMenuClick = ({ key }) => {
        const { selArts, deleteArts } = this.props.source;
        if (key === 'delete') {
            if (selArts.length === 0) {
                message.error('请选择文章');
                return;
            }
            const ids_del = [];
            for (let i = 0; i < selArts.length; i++) {
                ids_del.push(selArts[i].id);
            }
            deleteArts(ids_del);
        }
    }
    getBatchMenu = () => {
        return (
            <Menu onClick={this.onBatchMenuClick}>
                <MenuItem key="delete">批量删除</MenuItem>
            </Menu>
        )
    }

    render() {
        const { source: { dataSource, pagination, selArts, loading, updateStore }, app: { selectSiteId }, form: { getFieldDecorator }} = this.props;
        const CselArts = toJS(selArts);
        const selectedRowKeys = CselArts.map((art) => art.id);

        const uploaderProps = {
            action: '/auth/import',
            accept: 'application/x-zip-compressed',
            name: 'file',
            multiple: true,
            showUploadList: false,
            data: {
                siteId: selectSiteId
            },
            onChange: this.onUploaderChange
        }
        const rowSelection = {
            selectedRowKeys,
            onChange: (ids, arts) => {
                updateStore({
                    selArts: arts
                });
            }
        };

        return (
            <div>
                <Form>
                    <Row  gutter={8}>
                        <Col span={8}>
                            <FormItem label="稿件标题" {...{ labelCol: { span: 6 }, wrapperCol: { span: 18 } }}>
                                {getFieldDecorator('title', {
                                    initialValue:  ''
                                })(
                                    <Input />
                                )}
                            </FormItem>
                        </Col>
                        <Col span={4}>
                            <FormItem label="来源" {...{labelCol: { span: 6 }, wrapperCol: { span: 18 }}}>
                                {getFieldDecorator('importType',{
                                    initialValue: ''
                                })(
                                    <Select placeholder="全部" onChange={this.onImportTypeChange}>
                                        <Option value=''>全部</Option>
                                        <Option value='0'>手工导入</Option>
                                        <Option value='1'>外部导入</Option>
                                    </Select>
                                )}
                            </FormItem>
                        </Col>
                        <Col span={10}>
                            <FormItem label="录入时间" {...{ labelCol: { span: 6 }, wrapperCol: { span: 18 } }}>
                                {getFieldDecorator('publishDate', {
                                    initialValue:  null
                                })(
                                    <RangePicker showTime format={dateFormat}/>
                                )}
                            </FormItem>
                        </Col>

                        <Col span={2}>
                            <FormItem>
                                <Button
                                    type="primary"
                                    style={{'marginLeft': '20px'}}
                                    onClick={this.onSearch}
                                >
                                查询
                                </Button>
                            </FormItem>
                        </Col>
                    </Row>
                    <Row gutter={8}>
                        <Col span={3}>
                            <Upload {...uploaderProps}>
                                <Button
                                    type="primary"
                                    style={{ 'marginBottom': '20px', 'marginLeft': '20px' }}
                                >
                                    <Icon type="upload" />导入文件
                                </Button>
                            </Upload>
                        </Col>
                        <Col span={4}>
                            <Button
                                type="primary"
                                onClick={this.onCreateArt}
                                style={{ 'marginBottom': '20px', 'marginLeft': '20px' }}
                            >
                                新建
                            </Button>
                        </Col>
                    </Row>
                    <Row>
                        <p style={{ "marginTop": "-10px", "color": "grey", "marginBottom": "20px", "marginLeft": "20px" }}>(仅支持上传.zip格式文件)</p>
                    </Row>
                    <Row>
                        <Col span={18} style={{"marginBottom": "30px"}}>
                            {
                                <Dropdown.Button overlay={this.getBatchMenu()}>
                                    批量操作
                                </Dropdown.Button>
                            }
                        </Col>
                    </Row>
                </Form>



                <Table
                    ref="table"
                    className="draggable-table"
                    simple
                    bordered
                    columns={this.getColumns()}
                    dataSource={toJS(dataSource)}
                    rowKey={record => record.id}
                    loading={loading}
                    pagination={pagination}
                    onChange={this.onPageChange}
                    rowSelection={rowSelection}
                />

            </div>
        )
    }
}

export default Form.create()(SourceList);
