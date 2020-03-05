import React from 'react'
import {Modal, Form, Select, Button, Row} from 'antd'
import {observer, inject} from 'mobx-react'
import {toJS} from 'mobx'

const FormItem = Form.Item
const Option = Select.Option
const layout = {
    labelCol:{
        span:6
    },
    wrapperCol:{
        span:16
    }
}

@inject('permissions')
@observer
class PermissionModal extends React.Component {
    constructor(props) {
        super(props)
    }

    handleOk() {
        const {permissions:{savePermission, currentUser}, form:{getFieldsValue}} = this.props
        const values = {
            ...getFieldsValue(),
            id:currentUser.id
        }
        savePermission(values)
        
    }

    handleCancel() {
        const {permissions:{updateState}} = this.props
        updateState({
            modalVisible:false,
            
        })
    }

    render() {
        const {form:{getFieldDecorator}, permissions:{modalVisible, roles, currentUser}} = this.props
        const modalProps = {
            visible:modalVisible,
            onOk:()=>{this.handleOk()},
            onCancel:()=>{this.handleCancel()},
            closable:false
        }
        return (
            modalVisible && <Modal 
                {...modalProps} 
                footer={[
                    <Button type="primary" key="save" onClick={this.handleOk.bind(this)}>保存</Button>,
                    <Button key="cancel" onClick={this.handleCancel.bind(this)}>关闭</Button>  
                ]}
            >
                <Form>
                    <Row>
                        <FormItem label="当前用户" {...layout}>
                            <span>{currentUser.name}</span>
                        </FormItem>
                    </Row>
                    <Row>
                        <FormItem label="所属角色" {...layout}>
                            {
                                getFieldDecorator('roleIds',{
                                    initialValue:!!currentUser.roleIds ? toJS(currentUser.roleIds) : []
                                })(
                                    <Select
                                        mode="multiple"
                                        placeholder="Please select"
                                        onChange={this.handleChange}
                                    >
                                        {
                                            !!roles?(
                                                roles.map((item,index) => {
                                                    return <Option key={item.id} value={item.id}>{item.name}</Option>
                                                })
                                            ):null
                                        }
                                    </Select>
                                )
                            }
                        </FormItem>
                    </Row>
                </Form>
            </Modal>
        )
    }
}

export default Form.create()(PermissionModal)