import React,{Component} from 'react';
import { observer, inject } from 'mobx-react';
import { toJS} from 'mobx';
import { withRouter} from 'react-router-dom';
import { Form, TreeSelect, Input, InputNumber, Button, message, Select, Col, Tree} from 'antd';
import {Jt} from 'utils';
import qs from 'qs';
import _ from 'lodash';
import TempSelect from './templateSelect';

const TreeNode = Tree.TreeNode;
const Option = Select.Option;
const FormItem = Form.Item;
const layout = {
    labelCol: {
        span: 4
    },
    wrapperCol: {
        span: 9
    }
};

@inject('app')
@inject('category')
@observer
class CatEdit extends Component{
    constructor(props){
        super(props);
        const { history,location: { search }, category, app: { selectSiteId } } = props;
        let query = qs.parse(search.substr(1));
        const curCatg = {};
        const CdataSource = toJS(category.dataSource);
        if (!CdataSource.length) {  //直接进编辑页无数据时 直接退回列表页，以防操作站点引起频道改变而编辑页无法处理
            history.push('/category');
        }
        category.getBlogTree()
        if (!!query.id) {  //点击编辑
            category.getCatgItem(query.id)
        } else if(!!query.parentId){  //点击添加下级频道
            curCatg.parentId = query.parentId;
            category.updateStore({curCatg})
        }
    }
    fromTemplateId = (value,key) => {
        return value[key] || null;
    }
    onSave = () => {
        const { history, location: { search }, app: { selectSiteId }, category: { expandedRows, getCatTree, curCatg, saveCatg, updateStore, bbsId }, form: { validateFieldsAndScroll, resetFields } } = this.props;
        const CcurCatg = toJS(curCatg);
        const CexpandedRows = toJS(expandedRows);
        validateFieldsAndScroll((errors, values) => {
            if (errors) {
                return;
            }
            values.templateId = this.fromTemplateId(values.templateId,'templateId');
            values.pageTemplateId = this.fromTemplateId(values.pageTemplateId, 'pageTemplateId');
            const data = { ...CcurCatg, ...values, siteId: toJS(selectSiteId) };
            delete data.template;
            delete data.pageTemplate;
            data.bbsId = bbsId;
            if (!selectSiteId){
                message.error('请先建站点');
                return;
            }
            if (parseInt(data.parentId) !== 0 && (_.indexOf(CexpandedRows, parseInt(data.parentId)) === -1)) {
                CexpandedRows.push(parseInt(data.parentId))
                updateStore({
                    expandedRows: CexpandedRows
                })
            }
            const success = () => {
                let query = qs.parse(search.substr(1));
                const addSuccess=()=>message.success('保存成功',1,this.goBack());
                const saveSuccess=()=>message.success('添加成功',1,this.goBack());
                if(query.id){
                    addSuccess();
                }else{
                    saveSuccess();
                }
            }
            saveCatg(data, success);
        });
    }
    goBack = () => {
        const {history} = this.props;
        let { category: { updateStore } } = this.props;
        updateStore({
            curCatg:{}
        })
        history.goBack();
    }
    handleTree = (e,label,extra) => {
        let { category: { dataSource } } = this.props;
        const id = e || extra.triggerValue;
        const item = Jt.tree.getNode(toJS(dataSource), id);
        return item.id + '';
    }
    onTreeChange = (id) => {
        const { category: { bbsId, updateStore }} = this.props;
        updateStore({bbsId : id})
    }
    renderTreeNode = (data) => {
        if(data.length === 0) {
            return;
        }
        return data.map((item, index) => {
            return (
                <TreeNode
                    title={item.name}
                    key={item.fid}
                    value={item.fid}
                    disabled={ item.allowpost === 0 ? true: false }
                >
                    { item.children && this.renderTreeNode(item.children) }
                </TreeNode>
            )
        })
    };
    render(){
        let { app: { selectSiteId }, category: { dataSource, curCatg, forumsData, bbsId }, form: { getFieldDecorator } } = this.props;
        const CcurCatg = toJS(curCatg);
        const CdataSource = toJS(dataSource);
        const CforumsData = toJS(forumsData) || [];
        return (
            <div>
                <Form>
                    <FormItem label="上级频道" {...layout}>
                        {
                            getFieldDecorator('parentId', {
                                getValueFromEvent: this.handleTree,
                                initialValue: CcurCatg.parentId ? CcurCatg.parentId+'' : (CcurCatg.parentId === 0 ? CcurCatg.parentId + '' : '')
                            })(
                                <TreeSelect
                                    showSearch
                                    treeNodeFilterProp="label"
                                    dropdownStyle={{ maxHeight: 300, overflow: 'auto' }}
                                    treeData={toJS(dataSource)}
                                />
                            )
                        }
                    </FormItem>
                    <FormItem label="频道名称" {...layout}>
                        {
                            getFieldDecorator('name', {
                                initialValue: CcurCatg.name || '',
                                rules: [
                                    {required: true, message: '请输入频道名称',type:'string'},{
                                        validator(rule, values, callback){
                                            if(/^\s+/i.test(values)){
                                                callback(`频道名称不能以空格开头`)
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
                    <FormItem label="对应频道模版" {...layout}>
                        {
                            getFieldDecorator('pageTemplateId', {
                                initialValue: { pageTemplateId: CcurCatg.pageTemplateId }
                            })(
                                <TempSelect siteId={selectSiteId} type="category" />
                            )
                        }
                    </FormItem>
                    <FormItem label="对应详情模版" {...layout}>
                        {
                            getFieldDecorator('templateId', {
                                initialValue: {templateId:CcurCatg.templateId}
                            })(
                                <TempSelect siteId={selectSiteId} type="detail"/>
                            )
                        }
                    </FormItem>
                    <FormItem label="对应论坛版块" {...layout} >
                        <TreeSelect
                            // selectedKeys={[`${toJS(bbsId)}` || '1']}
                            treeDefaultExpandAll
                            value={ bbsId || undefined }
                            allowClear
                            placeholder='请选择'
                            onChange={this.onTreeChange}
                        >
                            {this.renderTreeNode(CforumsData)}
                        </TreeSelect>
                    </FormItem>
                    <FormItem wrapperCol={{ span: 6, offset: 5 }}>
                        <Button type="primary" style={{'marginRight':'10px'}} onClick={this.onSave}>保存</Button>
                        <Button onClick={this.goBack}>返回</Button>
                    </FormItem>
                </Form>
            </div>
        )
    }
}

export default withRouter(Form.create()(CatEdit));
