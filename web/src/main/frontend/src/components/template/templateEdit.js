import React,{Component} from 'react';
import {withRouter} from 'react-router-dom';
import {observer,inject} from 'mobx-react';
import {toJS} from 'mobx';

import { Button, Form, Input, Select, Modal, message } from 'antd';
import qs from 'qs';
import Template from '../../stores/template';
import Blocks from './blocks';
import File from './file';
import FileUploader from './fileUploader';
import '../../styles/template/template-edit.less'
const Option = Select.Option;
const FormItem = Form.Item;
const TextArea = Input.TextArea;


@inject('app')
@inject('template')
@observer
class TemplateEdit extends Component{
    constructor(props){
        super(props);
        const { location: { search }, app: { selectSiteId, sites }, template: { getItem, getTemplateList, updateStore } } = props;
        this.state = {
            visible:false,
        }
        this.selectSiteId = selectSiteId;
        getTemplateList({ siteId: selectSiteId });
        let query = qs.parse(search.substr(1));
        if (!!query.id) {
            getItem(query.id)
        } else {
            updateStore({ curItem: {} });
        }
    }
    componentWillReact() {
        const { app: { selectSiteId, sites }, template } = this.props;
        if (this.selectSiteId != selectSiteId) {  //如果选中站点selecetSiteId变了才重新请求频道树
            template.getTemplateList({ siteId: selectSiteId });
        }
        this.selectSiteId = selectSiteId;  //记录当前siteid以便比对
    }
    componentWillUnmount() {
        const { template } = this.props;
        template.reset();
    }
    goBack = () => {
        const { history,template:{updateStore} } = this.props;
        history.goBack();
    }
    fromBlocks = ({ blocks })=> {
        return toJS(blocks)||null;
    }
    onSave = () => {
        const { location: { search }, form: { validateFieldsAndScroll, resetFields }, app: { selectSiteId }, template: { saveItem, curItem, allFileList,updateStore, selectedType } } = this.props;
        const CcurItem = toJS(curItem);
        validateFieldsAndScroll((errors, values) => {
            if (errors) {
                return;
            }
            const callback = () => {
                const toRenderCallBack=()=>{
                  resetFields(); // 此处会导致保存后列表重置为未保存之前的状态
                  this.goBack(); //重制header的站点名和selectSiteId
                };
                const saveSuccess=()=>message.success('保存成功',1,toRenderCallBack);
                const addSuccess=()=>message.success('添加成功',1,toRenderCallBack);
                if(search){
                    saveSuccess();
                }else{
                    addSuccess();
                }
            }
            values.blocks = this.fromBlocks(values.blocks||{});
            if (selectedType=="block"){
                values.blocks = null;
            }
            const templateId = qs.parse(search.substr(1)).id || null;
            const data = { ...CcurItem, siteId: selectSiteId, ...values, resourceJson: JSON.stringify(allFileList) };
            saveItem(data, callback);
        });
    }
    handleModal = () => {
        this.setState({
            visible: !this.state.visible
        })
    }
    saveFileName = (obj) => {
        const { template: { updateStore, addedFileList}} = this.props;
        let CaddedFileList = toJS(addedFileList);
        CaddedFileList = { ...CaddedFileList, ...obj};
        updateStore({
            addedFileList: CaddedFileList
        })
    }
    deleteFile = (key) => {
        const { template:{allFileList,updateStore} } = this.props;
        let allFileListClone = toJS(allFileList);
        delete allFileListClone[key];
        updateStore({
            allFileList: allFileListClone
        })
    }
    handleChange = (value) => {
        const {template:{updateStore}} = this.props;
        updateStore({
            selectedType:value
        })
    }
    render(){
        const { form: { getFieldDecorator }, app: { selectSiteId }, template: { curItem, allFileList, addedFileList, updateStore, selectedType }} = this.props;
        let CAllFileList = toJS(allFileList);
        const CcurItem = toJS(curItem);
        const modalProps = {
            visible: this.state.visible,
            onOk: () => {
                CAllFileList = { ...CAllFileList, ...addedFileList}
                updateStore({
                    allFileList: CAllFileList,
                    addedFileList:{}
                })
                this.setState({
                    visible: false
                })
            },
            onCancel: () => {
                this.setState({
                    visible: false,
                })
            }
        }
        const fileProps = {
            allFileList: CAllFileList,
            deleteFile: this.deleteFile,
            handleModal: this.handleModal
        }
        const fileLoaderProps = {
            visible: this.state.visible,
            saveFileName: this.saveFileName
        }
        return (
            <div className="template-edit">
                <Modal {...modalProps}>
                    <FileUploader
                        {...fileLoaderProps}
                    />
                </Modal>
                <Form>
                    <FormItem label="模版类型" {...{ labelCol: { span: 5 }, wrapperCol: { span: 18 } }}>
                        {
                            getFieldDecorator('type', {
                                initialValue: CcurItem.type||'',
                                rules: [
                                    { required: true, message: '请选择类型' }
                                ]
                            })(
                                <Select onChange={this.handleChange}>
                                    <Option key="category" value="category">频道模版</Option>
                                    <Option key="block" value="block">区块模版</Option>
                                    <Option key="detail" value="detail">详情模版</Option>
                                </Select>
                            )
                        }
                    </FormItem>
                    <FormItem label="模版名称" {...{ labelCol: { span: 5 }, wrapperCol: { span: 18 } }}>
                        {
                            getFieldDecorator('name', {
                                initialValue: CcurItem.name || '',
                                rules: [
                                    { required: true, message: '请输入模版名称',type:'string'},{
                                        validator(rule, values, callback){
                                            if (values.length>20) {
                                                callback(`模版名称超出${values.length-20}个字符`)
                                            }else{
                                                callback();
                                            }
                                        }
                                    }
                                ]
                            })(
                                <Input type="text" />
                            )
                        }
                    </FormItem>
                    <FormItem label="模版内容" {...{ labelCol: { span: 5 }, wrapperCol: { span: 18 } }}>
                        {getFieldDecorator('content', {
                            initialValue: CcurItem.content || ''
                        })(
                            <TextArea rows={10} placeholder="请输入模版内容" />
                        )}
                    </FormItem>
                    <FormItem label="描述" {...{ labelCol: { span: 5 }, wrapperCol: { span: 18 } }}>
                        {getFieldDecorator('description', {
                            initialValue: CcurItem.description || '',
                            rules:[
                                {type:'string'},{
                                    validator(rule, values, callback){
                                        if (values.length>50) {
                                            callback(`模版名称超出${values.length-50}个字符`)
                                        }else{
                                            callback();
                                        }
                                    }
                                }
                            ]
                        })(
                            <Input placeholder="请输入描述" />
                        )}
                    </FormItem>
                    <FormItem label="文件" {...{ labelCol: { span: 5 }, wrapperCol: { span: 18 } }}>
                        {
                            getFieldDecorator('resourceJson', {
                                initialValue: CcurItem.resourceJson || {}
                            })(
                                <File {...fileProps} />
                            )
                        }
                    </FormItem>
                    {(selectedType == "block")?null:(<FormItem>
                        {getFieldDecorator('blocks', {
                            initialValue: { blocks: CcurItem.blocks }
                        })(
                            <Blocks siteId={selectSiteId} />
                        )}
                    </FormItem>)}
                </Form>
                <FormItem wrapperCol={{ span: 6, offset: 6 }}>
                    <Button style={{ 'marginRight': '20px' }} type="primary" onClick={this.onSave}>保存</Button>
                    <Button onClick={this.goBack}>返回</Button>
                </FormItem>
            </div>
        )
    }
}

export default withRouter(Form.create()(TemplateEdit));
