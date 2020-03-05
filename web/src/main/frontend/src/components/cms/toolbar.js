import React,{Component} from 'react';
import moment from 'moment';
import { Button, Row, Col, Form, Radio, Input, DatePicker, Dropdown, Menu, message, Select} from 'antd';
import { observer, inject } from 'mobx-react';
import { toJS } from 'mobx';
import {withRouter} from 'react-router-dom';
import {Jt} from '../../utils'
import { ROUTER_PATHS, CMS_STATUS} from '../../constants';
import SourceModal from './sourceModal';
const MenuItem = Menu.Item;
const RangePicker = DatePicker.RangePicker;
const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const RadioButton = Radio.Button;
const Option = Select.Option;
const dateFormat = 'YYYY-MM-DD HH:mm:ss';
const layout = {
    labelCol: {
        span: 6,
    },
    wrapperCol: {
        span: 18,
    }
};

@inject('cms')
@inject('app')
@inject('source')
@observer
class ToolBar extends Component{
    constructor(props){
        super(props);
    }
    onCreateArt = () => {
        const {history} = this.props;
        history.push(ROUTER_PATHS.ARTICLES_EDIT)
    }
    onDelFlagChg = (e) => {
        const { cms: { query, updateStore, articleList },app:{user} } = this.props;
        const Cquery = toJS(query);
        Cquery.delFlag = e.target.value;
        const permissions = toJS(user.permissions) || [];
        let permis;
        if (e.target.value==2){   //待审核
            permis = "cms:articles:audit";
        } else if (e.target.value == 1){  //草稿
            permis = "cms:articles:view";
        } else if (e.target.value == 4){   //待修改
            permis = "cms:articles:view";
        } else if (e.target.value == 0 || e.target.value == 6){  //上下线
            permis = "cms:articles:onoff";
        } else if (e.target.value == 99){
            permis = "cms:articles:view";
        }
        let index = permissions.findIndex(function (value, index) {
            const reg = new RegExp('^' + value);
            return reg.test(permis);
        })
        Cquery.pageNumber = 1;
        updateStore({
            query: Cquery,
            selArts: []
        })
        if (index === -1) {
            updateStore({
                dataSource: []
            })
            message.error('您没有该权限')
            return;
        }
        articleList(Cquery);
    }
    getDelFlagTpl = () => {
        return (
            <RadioGroup onChange={this.onDelFlagChg}>
                {CMS_STATUS.map((item, index) => {
                    return <RadioButton key={index} value={item.value}>{item.label}</RadioButton>
                })}
            </RadioGroup>
        );
    }
    onSearch = () => {
        const { cms: { query, updateStore, articleList }, form: { getFieldsValue}} = this.props;
        let data = getFieldsValue();
        if (!Jt.array.isEmpty(data.publishDate)) {
            data.beginTime = data.publishDate[0].format(dateFormat);
            data.endTime = data.publishDate[1].format(dateFormat);
        }
        delete data.publishDate;
        const Cquery = toJS(query);
        data = { ...Cquery, ...data};
        for(let key in data){  //去掉为空的搜索项
            if(!data[key]){
                delete data[key]
            }
        }
        data.pageNumber = 1;
        updateStore({ query: data})
        articleList(data)
    }
    onReset = () => {
        const { form: { resetFields, getFieldsValue }, cms: { updateStore, query, articleList} } = this.props;
        resetFields();
        let Cquery = toJS(query);
        delete Cquery['title'];
        delete Cquery['publishDate'];
        delete Cquery['operateUser'];
        delete Cquery['beginTime'];
        delete Cquery['endTime'];
        delete Cquery['status'];
        updateStore({ query: Cquery})
        articleList(Cquery)
    }
    onBatchMenuClick = ({ key }) => {
        const { selArts, onOffArts, deleteArts, batchAuditArts } = this.props.cms;
        if (key === 'online' || key === 'offline') {
            if (selArts.length === 0) {
                message.error('请选择文章');
                return;
            }
            const ids_on = [];
            const ids_off = [];
            for (let i = 0; i < selArts.length; i++) {
                if (selArts[i].delFlag === 0) {
                    ids_off.push(selArts[i].id);
                } else {
                    ids_on.push(selArts[i].id);
                }
            }
            if (key === 'online' && ids_off.length > 0) {
                message.error('选择的文章中包含上线文章');
                return;
            } else if (key === 'offline' && ids_on.length > 0) {
                message.error('选择的文章中包含下线文章');
                return;
            }
            const ids = ids_on.length > 0 ? ids_on : ids_off;
            onOffArts(ids.join(','));
        } else if (key === 'delete') {
            if (selArts.length === 0) {
                message.error('请选择文章');
                return;
            }
            const ids_del = [];
            for (let i = 0; i < selArts.length; i++) {
                if (selArts[i].delFlag !== 0) {
                    ids_del.push(selArts[i].id);
                }
            }
            if (ids_del.length !== selArts.length) {
                message.error('选择的文章中包含上线文章');
                return;
            }
            deleteArts(ids_del);
        } else if (key === 'audit'){
            if (selArts.length === 0) {
                message.error('请选择文章');
                return;
            }
            const ids_audit = [];
            for (let i = 0; i < selArts.length; i++) {
                ids_audit.push(selArts[i].id);
            }
            batchAuditArts(ids_audit.join(','));
        }
    }
    getBatchMenu = () => {
        const { cms: { query } } = this.props;
        const delFlag = query.delFlag;
        return (
            <Menu onClick={this.onBatchMenuClick}>
                {(delFlag == 6 || delFlag == 2) && <MenuItem key="online">批量上线</MenuItem>}
                {delFlag==0 && <MenuItem key="offline">批量下线</MenuItem>}
                {delFlag == 1 && <MenuItem key="audit">批量送审</MenuItem>}
                {(delFlag == 6 || delFlag == 1 || delFlag == 4) && <MenuItem key="delete">批量删除</MenuItem>}
            </Menu>
        )
    }
    showModal = () => {
        const { source, app: { selectSiteId, sites }, cms: { modalVisible, updateStore }} = this.props;
        this.selectSiteId = selectSiteId;
        source.getInit({ siteId: selectSiteId });
        updateStore({ modalVisible : true });
    }
    handleModalCancel = () => {
        const { cms: { modalVisible, updateStore }} = this.props;
        updateStore({ modalVisible : false });
    }
    onStatusChange = (value) => {
        const { cms: { query, updateStore, articleList } } = this.props;
        // updateStore({ query: data })
        const data = query;
        data.pageNumber = 1;
        data.status = value;
        updateStore({ query: data })
        articleList(data)
    }
    render(){
        const { cms: { query, modalVisible, catTree }, form: { getFieldDecorator }} = this.props;
        return (
            <div>
                <Form style={{'marginBottom':'15px'}}>
                    <Row gutter={8}>
                        <Col span={11}>
                            <FormItem label="录入时间" {...{ labelCol: { span: 4 }, wrapperCol: { span: 20 } }}>
                                {getFieldDecorator('publishDate', {
                                    initialValue: query.beginTime ? [moment(query.beginTime, dateFormat), moment(query.endTimes)] : null
                                })(
                                    <RangePicker showTime format={dateFormat} />
                                )}
                            </FormItem>
                        </Col>
                        <Col span={12} push={1}>
                            <FormItem>
                                {getFieldDecorator('delFlag', {
                                    initialValue: query.delFlag !== undefined ? query.delFlag + '' : '1'
                                })(
                                    this.getDelFlagTpl()
                                )}
                            </FormItem>
                        </Col>

                    </Row>
                    <Row gutter={8}>
                        <Col span={8}>
                            <FormItem label="稿件标题" {...{ labelCol: { span: 6 }, wrapperCol: { span: 18 } }}>
                                {getFieldDecorator('title', {
                                    initialValue: query.title || ''
                                })(
                                    <Input />
                                )}
                            </FormItem>
                        </Col>
                        <Col span={8}>
                            <FormItem label="操作人" {...{ labelCol: { span: 6 }, wrapperCol: { span: 16 } }}>
                                {getFieldDecorator('operateUser', {
                                    initialValue: query.operateUser || ''
                                })(
                                    <Input />
                                )}
                            </FormItem>
                        </Col>
                        {query.delFlag == '99' && <Col span={4}>
                            <FormItem label="状态" {...{ labelCol: { span: 6 }, wrapperCol: { span: 14 } }}>
                                {getFieldDecorator('status', {
                                    initialValue: '99'
                                })(
                                    <Select placeholder="全部" onChange={this.onStatusChange}>
                                        <Option value='99'>全部</Option>
                                        <Option value='1'>草稿</Option>
                                        <Option value='2'>待审核</Option>
                                        <Option value='4'>待修改</Option>
                                        <Option value='0'>已上线</Option>
                                        <Option value='6'>已下线</Option>
                                    </Select>
                                )}
                            </FormItem>
                        </Col>}
                        <Col span={4}>
                            <FormItem>
                                <Button type="primary" onClick={this.onSearch}>查询</Button>
                                <Button style={{ 'marginLeft': '10px' }} onClick={this.onReset}>重置</Button>
                            </FormItem>
                        </Col>
                    </Row>
                    {/* {!!toJS(catTree).length && toJS(query).categoryId != toJS(catTree)[0].id && query.delFlag !== "99" && <Row> */}
                    {!!toJS(catTree).length && query.delFlag !== '99' && <Row>
                        <Col span={18}>
                        {
                            <Dropdown.Button overlay={this.getBatchMenu()}>
                                批量操作
                            </Dropdown.Button>
                        }
                        </Col>
                        {!!toJS(catTree).length && toJS(query).categoryId != toJS(catTree)[0].id && <Col span={3} style={{'textAlign':'right'}}>
                            {
                                (query.delFlag == 1)&& <div>
                                    <Button type="primary" onClick={this.showModal}>稿源库选稿</Button>
                                    <SourceModal modalVisible={ modalVisible } handleModalCancel={this.handleModalCancel}/>
                                </div>
                            }
                        </Col>}
                        {!!toJS(catTree).length && toJS(query).categoryId != toJS(catTree)[0].id && <Col span={3} style={{'textAlign':'right'}}>
                        {
                            (query.delFlag == 1) && <Button type="primary" onClick={this.onCreateArt}>
                                新建文章
                            </Button>
                        }
                        </Col>}
                    </Row>}
                </Form>
            </div>
        )
    }
}

export default withRouter(Form.create()(ToolBar));
