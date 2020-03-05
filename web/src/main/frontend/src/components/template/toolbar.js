import React from 'react';
import { withRouter} from 'react-router-dom';
import {observer,inject} from 'mobx-react';
import {toJS} from 'mobx';
import { Button, Icon, Upload, message, Row, Col, Form, Input, Select } from 'antd';

const FormItem = Form.Item;
const Option = Select.Option;

@inject('app')
@inject('template')
@observer
class Toolbar extends React.Component {
    constructor(props) {
        super(props);
    }
    onCreate = () => {
        const { history } = this.props;
        history.push('/template/edit');
    }
    onUploaderChange = (info) => {
        const {
            template: { getTemplateList },
            app: { selectSiteId }
        } = this.props;
        const status = info.file.status;
        if (status === 'done') {
            message.success(`${info.file.name} 导入成功.`);
            getTemplateList({ siteId: selectSiteId });
        } else if (status === 'error') {
            message.error(`${info.file.name} 导入失败.`);
        }
    }
    onSearch = () => {
        const { form: { getFieldsValue, resetFields }, template: { getTemplateList, query } } = this.props;
        let data = {...query, ...getFieldsValue()};
        for(let key in data){  //去掉为空的搜索项
            if(!data[key]){
                delete data[key]
            }
        }
        data.pageNumber = 1;
        getTemplateList({...data});
    }
    onReset = () => {
        const { form: { getFieldsValue, resetFields }, template: { getTemplateList, query } } = this.props;
        resetFields();
        delete query.name;
        getTemplateList({...query,pageNumber:1});
    }
    onTypeChange = (value) => {
        const { template: { getTemplateList, query }, form: { getFieldsValue }} = this.props;
        const data = {...query, ...getFieldsValue(), type: value};
        for(let key in data){  //去掉为空的搜索项
            if(!data[key]){
                delete data[key]
            }
        }
        data.pageNumber = 1;
        getTemplateList({...data});
    }
    render() {
        const { app: { selectSiteId }, template:{query}, form:{getFieldDecorator} } = this.props;
        const uploaderProps = {
            name:'file',
            multiple:true,
            accept:'application/x-zip-compressed',
            showUploadList: false,
            data:{
                siteId: selectSiteId
            },
            onChange:this.onUploaderChange,
            action: '/auth/import/template'
        }
        return (
            <div>
                <Row gutter={8}>
                    <Col span={8}>
                        <FormItem label="模版名称" {...{labelCol: {xs: {span: 24 },sm: {span: 6 }},wrapperCol: {xs: {span: 24 },sm: {span: 18 }}}}>
                            {getFieldDecorator('name', {
                                initialValue: query.name || ''
                            })(
                                <Input />
                            )}
                        </FormItem>
                    </Col>
                    <Col span={4}>
                        <FormItem label="模板类型" {...{labelCol: {span: 10 },wrapperCol:  {span: 14 }}}>
                            {getFieldDecorator('type', {
                                initialValue: query.type || ''
                            })(
                               <Select onChange={this.onTypeChange}>
                                   <Option value="">全部</Option>
                                   <Option value="category">频道</Option>
                                   <Option value="block">区块</Option>
                                   <Option value="detail">详情</Option>
                               </Select>
                            )}
                        </FormItem>
                    </Col>
                    <Col span={8}>
                        <FormItem>
                            <Button type="primary" onClick={this.onSearch}>查询</Button>
                            <Button style={{ 'marginLeft': '10px' }} onClick={this.onReset}>重置</Button>
                        </FormItem>
                    </Col>
                </Row>
                <Row>
                    <Col span={8} style={{'marginTop': '4px'}}>
                        <Button
                            type="primary"
                            onClick={this.onCreate}
                            style={{ 'marginBottom': '20px' }}
                        >
                            新建模版
                        </Button>
                        <Upload {...uploaderProps}>
                            <Button
                                type="primary"
                                style={{ 'marginBottom': '20px', 'marginLeft': '20px'}}
                            >
                                <Icon type="upload" />导入模版
                            </Button>
                        </Upload>
                    </Col>
                </Row>
                <Row>
                    <p style={{ "marginTop": "-10px", "color": "grey", "marginBottom": "20px", "marginLeft": "90px" }}>(仅支持上传.zip格式文件)</p>
                </Row>

            </div>
        )
    }
}

export default withRouter(Form.create()(Toolbar));