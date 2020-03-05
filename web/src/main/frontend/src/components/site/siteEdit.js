import React, {Component} from 'react';
import {observer, inject} from 'mobx-react';
import {Button, Form, Input, notification, Icon, message} from 'antd'
import {toJS} from 'mobx';
import {withRouter} from 'react-router-dom';
import qs from 'qs';


const FormItem = Form.Item;
const {TextArea} = Input;
const layout = {
    labelCol: {
        span: 4
    },
    wrapperCol: {
        span: 9
    }
};

@inject('site')
@inject('app')
@observer
class PageSiteEdit extends Component {
    constructor(props) {
        super(props);
        const {location: {search}, site: {getListItem, updateStore}} = props;
        let query = qs.parse(search.substr(1));
        if (!!query.id) {
            getListItem(query.id)
        } else {
            updateStore({curItem: {}});
        }
    }

    componentWillReceiveProps({location: {search, pathname}, site: {getListItem}}) {
        if (pathname !== this.props.location.pathname) {
            let query = qs.parse(search.substr(1));
            if (!!query.id) {
                getListItem(query.id)
            } else {
                updateStore({curItem: {}});
            }
        }
    }

    goBack = () => {
        const {history} = this.props;
        history.goBack();
    };

    handleOnSave = () => {
        const {history, location, app, site: {onSave, getList, curItem}, form: {validateFieldsAndScroll, resetFields}} = this.props;
        validateFieldsAndScroll((errors, values) => {
            if (errors) {
                return;
            }
            const data = {...curItem, ...values};
            //callBack执行说明向后台发送请求的状态成功了
            const callback = (flag, msg) => {
                if(flag === -1){
                    message.error(msg);
                    return;
                }
                const toRenderCallBack = () => {
                    resetFields();
                    history.goBack();
                    getList();
                    app.saveSites(); //重制header的站点名和selectSiteId
                };
                const saveSuccess = () => message.success('保存成功', 1, toRenderCallBack);
                const addSuccess = () => message.success('添加成功', 1, toRenderCallBack);
                location.search?saveSuccess():addSuccess();
            };
            onSave(data, callback);
        });
    };

    render() {
        const {form: {getFieldDecorator}, site: {curItem}} = this.props;
        return (
            <div>
                <Form>
                    <FormItem label="站点名称" {...layout}>
                        {
                            getFieldDecorator('name', {
                                initialValue: curItem.name || '',
                                rules: [
                                    {required: true, message: '请输入站点名称',type:'string'}, {
                                        validator(rule, values, callback){
                                            if (values.length>20) {
                                                callback(`站点名称超出${values.length-20}个字符`)
                                            }else{
                                                callback();
                                            }
                                        }
                                    }
                                ]
                            })(
                                <Input type="text"/>
                            )
                        }
                    </FormItem>
                    <FormItem label="站点域名" {...layout}>
                        {
                            getFieldDecorator('domainPath', {
                                initialValue: curItem.domainPath || '',
                                rules: [
                                    {required: true, message: '请输入站点域名'},{
                                        validator(rule, values, callback){
                                            if (values.length>40) {
                                                callback(`站点域名超出${values.length-40}个字符`)
                                            }else{
                                                callback();
                                            }
                                        }
                                    }
                                ]
                            })(
                                <Input type="text"/>
                            )
                        }
                    </FormItem>
                    <FormItem label="站点路径" {...layout}>
                        {
                            getFieldDecorator('path', {
                                initialValue: curItem.path || '',
                                rules: [
                                    {required: true, message: '请输入站点路径',type:'string'},{
                                        validator(rule, values, callback){
                                            if (values.length>40) {
                                                callback(`站点路径超出${values.length-40}个字符`)
                                            }else{
                                                callback();
                                            }
                                        }
                                    }
                                ]
                            })(
                                <Input type="text"/>
                            )
                        }
                    </FormItem>
                    <FormItem label="站点别名" {...layout}>
                        {
                            getFieldDecorator('slug',{
                                initialValue: curItem.slug || '',
                                rules: [
                                    {required: true, message: '请输入站点别名', type: 'string'}
                                ]
                            })(
                                <Input type="text" />
                            )
                        }
                    </FormItem>
                    <FormItem label="站点简称" {...layout}>
                        {
                            getFieldDecorator('simpleName', {
                                initialValue: curItem.simpleName || '',
                                rules:[
                                    {type:'string'},{
                                        validator(rule, values, callback){
                                            if (values.length>20) {
                                                callback(`站点简称超出${values.length-20}个字符`)
                                            }else{
                                                callback();
                                            }
                                        }
                                    }
                                ]
                            })(
                                <Input type="text"/>
                            )
                        }
                    </FormItem>
                    <FormItem label="站点描述" {...layout}>
                        {
                            getFieldDecorator('description', {
                                initialValue: curItem.description || '',
                                rules:[
                                    {type:'string'},{
                                        validator(rule, values, callback){
                                            if (values.length>120) {
                                                callback(`站点描述超出${values.length-120}个字符`)
                                            }else{
                                                callback();
                                            }
                                        }
                                    }
                                ]
                            })(
                                <TextArea rows={5}/>
                            )
                        }
                    </FormItem>
                    <FormItem wrapperCol={{span: 6, offset: 6}}>
                        <Button style={{'marginRight': '20px'}} type="primary"
                                onClick={this.handleOnSave}>保存</Button>
                        <Button onClick={this.goBack}>返回</Button>
                    </FormItem>
                </Form>
            </div>
        )
    }
}

export default withRouter(Form.create()(PageSiteEdit))
