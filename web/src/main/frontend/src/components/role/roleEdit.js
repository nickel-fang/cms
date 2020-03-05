import React,{Component} from 'react';
import {observer,inject} from 'mobx-react';
import {toJS, get} from 'mobx';
import { Form, TreeSelect, Select, Input, Button, Card} from 'antd';
import lodash from 'lodash';
import { ROUTER_PATHS } from '../../constants';
import {Jt} from '../../utils';
import qs from 'qs';
import UserSite from './roleSite'

const FormItem = Form.Item;
const SelectOption = Select.Option;

const layout = {
    labelCol: {span: 4},
    wrapperCol: {span: 12}
};

@inject('app')
@inject('roles')
@observer
class Edit extends Component{
    constructor(props) {
        super(props)
        const {location: {search}, roles: {getRole, updateStore, getMenus, getSites,categoryList}} = props;
        let query = qs.parse(search.substr(1));
        if (!!query.id) {
            getRole(query.id)
        } else {
            updateStore({role: {}, frontIsInit:'true', backIsInit:'true'});
            getMenus()

        }
        getSites()
    }

    // componentWillReact() {
    //     const {roles:{sites,getCategories,categoryList}} = this.props
    //     if(!!sites.length && categoryList.length == 0){
    //         getCategories(sites)
    //     }
    //     if(categoryList.length > 0) {
    //         this.setState({
    //             categoryList
    //         })
    //     }
    // }

    componentWillUnmount() {
        const { roles } = this.props;
        roles.reset();
    }

    goBack() {
        const {history} = this.props
        history.goBack()
    }

    onSave = () => {
        const {roles:{role, saveRole}, form: {validateFields, getFieldsValue}} = this.props;
        validateFields((errors, values) => {
            let data = getFieldsValue() 
            let obj = this.formatSitesCategories(data)
            
            if(errors) {
                return;
            }
            lodash.remove(values.menuIds, (menuId) => {
                return menuId.indexOf('system-') === 0;
            });
            values.id = role.id;
            values.frontSC = obj.frontSC
            values.backSC = obj.backSC
            delete values.frontsites
            delete values.frontcategories
            delete values.backsites
            delete values.backcategories
            delete values.sitesCategories
            //console.log(values)
            const success = () => {
                const {history} = this.props
                history.push(ROUTER_PATHS.USERS_ROLE)
            }
            saveRole(values,success);
        })
    }

    formatSitesCategories(data) {
        let {frontsites, frontcategories, backsites, backcategories} = data
        var frontSC = [], backSC = []

        frontsites.length > 0 && frontsites.map((site, index) => {
            frontsites = frontsites.filter(site => site !== 'notSet')
        })

        !!frontsites && frontsites.length > 0?(           
            frontsites.map((item, index) => {
                if(!!item && frontcategories.length > 0){
                    frontSC.push({
                        siteId: item,
                        categoryIds: frontcategories[index].join()
                    }) 
                }
            })
        ):null

        backsites.length > 0 && backsites.map((site, index) => {
            backsites = backsites.filter(site => site !== 'notSet')
        })

        !!backsites && backsites.length > 0?(
            backsites.map((item, index) => {
                if(!!item && backcategories.length > 0){
                    backSC.push({
                        siteId: item,
                        categoryIds: backcategories[index].join()
                    })
                }
            })
        ):null

        return {frontSC,backSC}
    }

    render() {
        const {roles:{ menus, role, backIsInit, frontIsInit}, form: {getFieldDecorator, setFieldsValue, getFieldValue}} = this.props;
        const sitesProps = {
            form: {getFieldDecorator, setFieldsValue, getFieldValue}
        }
        return (
            <Form>
                <FormItem label="角色名称" {...layout} hasFeedback>
                    {getFieldDecorator('name', {
                        initialValue: role.name,
                        rules: [
                            {required: true, message: '请输入角色名称'}
                        ]
                    })(<Input />)}
                </FormItem>
                <Card title="前台权限配置" bordered={false} style={{ width: '100%' }}>
                   
                        <UserSite {...sitesProps} type="front"  sitesCategories={role.frontSC} isInit={frontIsInit} />
                        
                        
                   
                </Card>
                <Card title="后台权限配置" bordered={false} style={{ width: '100%' }}>
                    <FormItem label="角色授权" {...layout} hasFeedback>
                        {getFieldDecorator('menuIds', {
                            initialValue: role.menuIds && role.menuIds.length > 0 ? role.menuIds.map(item => item + '') : undefined,
                            rules: [
                                {required: true, message: '请选择角色授权'}
                            ]
                        })(
                            <TreeSelect 
                                allowClear
                                showSearch
                                showCheckedStrategy={TreeSelect.SHOW_ALL}
                                treeNodeFilterProp="label"
                                dropdownStyle={{maxHeight: 300, overflow: 'auto'}}
                                treeData={toJS(menus)}
                                multiple={true}
                                treeCheckable={true}
                            />
                        )}
                    </FormItem>
                    
                        <UserSite {...sitesProps}  type="back"  sitesCategories={role.backSC} isInit={backIsInit}  />
                        
                   
                </Card>

                <FormItem label="备注" {...layout}>
                    {getFieldDecorator('remark', {
                        initialValue: role.remark
                    })(
                        <Input type="textarea" rows={5}/>
                    )}
                </FormItem>
                
                <Form.Item wrapperCol={{span: layout.wrapperCol.span, offset: layout.labelCol.span}}> 
                    <Button type="primary" style={{ marginRight: 20 }} onClick={this.onSave}>保存</Button>
                    <Button onClick={() => this.goBack()}>返回</Button>
                </Form.Item>
            </Form>
        );
    }
}
export default Form.create()(Edit);