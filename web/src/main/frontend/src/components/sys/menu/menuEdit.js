import React,{Component} from 'react';
import {withRouter} from 'react-router-dom';
import { observer, inject } from 'mobx-react';
import { Form, TreeSelect, Switch, Icon, Input, Button} from 'antd';
import qs from 'qs';
import { toJS } from 'mobx';
import IconModal from './iconModal';
import '../../../styles/sys/menu-edit.less';
import { CACHE_MENUS } from '../../../constants'
import { sessionCache } from '../../../utils';
const FormItem = Form.Item;
const layout = {
    labelCol: { span: 4 },
    wrapperCol: { span: 12 }
};

@inject('app')
@inject('sys')
@observer
class MenuEdit extends Component{
    constructor(props){
        super(props);
        const { location: { search }, sys: { getMenuList, getMenuItem, updateStore, getMenu},app:{user} } = props;
        const query = qs.parse(search.substr(1));
        updateStore({
            menu: {
                parentId: query.parentId ? query.parentId+'': ''
            }
        })
        getMenuList({systemId: user.isystem.id});
        if(!!query.id){
            getMenuItem(query.id)
        }
    }
    showIconModal = () => {
        const { sys: { updateStore } } = this.props;
        updateStore({
            iconModalVisible: true
        });
    }
    hideIconModal = () => {
        const { sys: { updateStore } } = this.props;
        updateStore({
            iconModalVisible: false
        });
    }
    selectIcon = (icon) => {
        const { sys: { menu, updateStore } } = this.props;
        updateStore({
            menu: { ...menu, icon },
            iconModalVisible: false
        });
    }
    deleteIcon = () => {
        const { sys: { menu, updateStore } } = this.props;
        updateStore({
            menu: { ...menu, icon: undefined }
        });
    }
    goBack = () => {
        const { history } = this.props;
        history.goBack();
    }
    onSave = () => {
        const { history, app: { user, queryMenus},sys: { menu, saveMenu, getMenuList}, form: { validateFields } } = this.props;
        validateFields((error, values) => {
            if (error) {
                return;
            }
            const data = { ...menu, ...values };
            const cb = () => {
                history.goBack();
                sessionCache.delete(CACHE_MENUS);
                getMenuList({systemId: user.isystem.id});
                queryMenus({systemId: user.isystem.id});
            }
            console.log(data)
            saveMenu(data,cb);
        });
    }
    render(){
        const { sys: { menu, menus, iconModalVisible }, form: { getFieldDecorator }} = this.props;
        const modalProps = {
            visible: iconModalVisible,
            onCancel: this.hideIconModal,
            onSelect: this.selectIcon
        };
        return (
            <div className="edit-form">
                <Form>
                    <FormItem label="上级菜单" {...layout}>
                        {getFieldDecorator('parentId', {
                            initialValue: menu.parentId ? menu.parentId + '' : undefined
                    })(
                            <TreeSelect
                                showSearch
                                allowClear={true}
                                treeNodeFilterProp="label"
                                dropdownStyle={{ maxHeight: 300, overflow: 'auto' }}
                                treeData={toJS(menus)}
                            />
                        )}
                    </FormItem>
                    <FormItem label="名称" hasFeedback {...layout}>
                        {getFieldDecorator('name', {
                            initialValue: menu.name,
                            rules: [
                                { required: true, message: '请输入名称' }
                            ]
                        })(<Input />)}
                    </FormItem>
                    <FormItem label="key" {...layout}>
                        {getFieldDecorator('code', {
                            initialValue: menu.code,
                            rules: [
                                { required: true, message: '请填写key' }
                            ]
                        })(<Input />)}
                    </FormItem>
                    <FormItem label="链接" {...layout}>
                        {getFieldDecorator('href', {
                            initialValue: menu.href
                        })(<Input />)}
                    </FormItem>
                    <FormItem label="权限标识" {...layout}>
                        {getFieldDecorator('permission', {
                            initialValue: menu.permission
                        })(
                            <Input />
                        )}
                    </FormItem>
                    <FormItem label="图标" {...layout} className="icon-item">
                        {
                            menu.icon ?
                                [
                                    <Icon type={menu.icon} key="icon"/>,
                                    <a className="del-btn" key="icon-delete" onClick={this.deleteIcon}>删除</a>
                                ] : undefined
                        }
                        <a className="sel-btn" onClick={this.showIconModal}>选择</a>
                    </FormItem>
                    <FormItem label="可见" {...layout}>
                        {getFieldDecorator('show', {
                            valuePropName: 'checked',
                            initialValue: menu.show !== undefined ? menu.show : true
                        })(
                            <Switch checkedChildren={<Icon type="check" />} unCheckedChildren={<Icon type="cross" />} />
                        )}
                    </FormItem>
                    <FormItem wrapperCol={{ span: layout.wrapperCol.span, offset: layout.labelCol.span }}>
                        <Button type="primary" onClick={this.onSave} style={{ marginRight: 20 }}>保存</Button>
                        <Button onClick={this.goBack}>返回</Button>
                    </FormItem>
                </Form>
                <IconModal {...modalProps}/>
            </div>
        )
    }
}

export default withRouter(Form.create()(MenuEdit));
