import React from 'react'
import {observer, inject} from 'mobx-react'
import {withRouter} from 'react-router-dom'
import {Form, Input, Button, Row, Col} from 'antd'
import {ROUTER_PATHS} from '../../constants'
import qs from 'qs';


const FormItem = Form.Item
const layout = {
    labelCol:{
        span:4
    },
    wrapperCol:{
        span:18
    }
}

@inject('permissions')
@observer

class Search extends React.Component {
    constructor(props) {
        super(props)
    }

    serach() {
        const {form:{getFieldsValue}, permissions:{ pagination, getInit }, history} = this.props
        const query = {
            ...getFieldsValue(),
            pageNumber:pagination.current,
            pageSize:pagination.pageSize
        }
        history.push({
            pathname: ROUTER_PATHS.USER_PERMISSION,
            search: '?' + qs.stringify(query)
        })
        getInit(query)
        
    }

    render() {
        const {form: {getFieldDecorator}, permissions:{query}} = this.props
        return(
            <div>
                <Form>
                    <Row>
                        <Col span={8}>
                            <FormItem {...layout} label="用户名">
                                {
                                    getFieldDecorator('username',{
                                        initialValue:query.username || '',

                                    })(<Input />)
                                }
                            </FormItem>
                        </Col>
                        <Col span={8}>
                            <FormItem {...layout} label="姓名">
                                {
                                    getFieldDecorator('name',{
                                        initialValue:query.name || '',

                                    })(<Input />)
                                }
                            </FormItem>
                        </Col>
                        <Col span={2} style={{textAlign:'right'}}>
                            <Button type="primary" onClick={this.serach.bind(this)}>搜索</Button>
                        </Col>
                    </Row>
                </Form>
            </div>
        )
        
    }
}

export default withRouter(Form.create()(Search))