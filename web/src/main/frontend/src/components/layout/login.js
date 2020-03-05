import React, {Component} from 'react';
import { Button, Row, Form, Input, message, Checkbox, Icon, Col } from 'antd';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import qs from 'qs';
import { NAME, LOGO_URL } from '../../constants';
import '../../styles/login.less'
import logo from '../../images/logo.png';

const FormItem = Form.Item

@inject('app')
@observer
class Login extends Component{
    constructor(props){
        super(props);
        this.state = {
            timestamp : new Date().getTime()
        }
    }
    handleOk = () => {
        const { location:{search}, form: { validateFieldsAndScroll }, app: { loginHandle }} = this.props;
        validateFieldsAndScroll((errors, values) => {
            if (errors) {
                this.setState({
                    timestamp: new Date().getTime()
                })
                return
            }
            let query = qs.parse(search.substr(1));
            const redirectUrl = query.redirectUrl || '';
            delete values.rememberMe;
            const cb = () => {
                this.setState({
                    timestamp: new Date().getTime()
                })
            }
            loginHandle(values, redirectUrl, cb);
        })
    }
    chgImg = () => {
        this.setState({
            timestamp: new Date().getTime()
        })
    }
    render(){
        const { form: { getFieldDecorator }, app:{user} } = this.props;
        const { timestamp } = this.state;
        return (
            <div className="login">
                <div className="login-bg"></div>
                <div className="form-wrap">
                    <div className="text"></div>
                    <div className="form">
                        {/*<div className="logo">
                                <span className="icon" style={{ backgroundImage: 'url(' + (user.system ? user.system.icon : LOGO_URL ) + ')' }}></span>
                                <span className="name f-toe1">{user.system ? user.system.name : NAME}</span>
                            </div>*/}
                        <Form>
                            <FormItem>
                                {getFieldDecorator('username', {
                                    rules: [
                                        {
                                            required: true,
                                            message: '请填写用户名'
                                        },
                                    ],
                                })(<Input onPressEnter={this.handleOk} placeholder="用户名" prefix={<Icon type="user" style={{ fontSize: 13 }} />} />)}
                            </FormItem>
                            <FormItem>
                                {getFieldDecorator('password', {
                                    rules: [
                                        {
                                            required: true,
                                            message: '请填写密码'
                                        },
                                    ],
                                })(<Input type="password" placeholder="密码" prefix={<Icon type="lock" style={{ fontSize: 13 }} />} />)}
                            </FormItem>
                            {<FormItem className="code">
                                {getFieldDecorator('validateCode', {
                                    rules: [
                                        {
                                            required: true,
                                            message: '请输入验证码'
                                        },
                                    ],
                                })(
                                    <Row type="flex" align="top" gutter={16}>
                                        <Col span={12}>
                                            <Input placeholder="请输入验证码" onPressEnter={this.handleOk} />
                                        </Col>
                                        <Col span={12}>
                                            <img src={`auth/captcha.jpg?date=${timestamp}`} onClick={this.chgImg} />
                                        </Col>
                                    </Row>
                                )}
                            </FormItem>}
                            <FormItem className="remember">
                                {getFieldDecorator('rememberMe')(
                                    <Checkbox>记住登录</Checkbox>
                                )}
                            </FormItem>
                            <Row>
                                <Button type="primary" onClick={this.handleOk}>
                                    登录
                                    </Button>
                            </Row>
                        </Form>
                    </div>
                </div>
                <div className="login-text">
                    中宣部机关服务局网络技术中心主办
                    <span>联系电话:010-66025755</span>
                </div>
            </div>
        )
    }
}

export default withRouter(Form.create()(Login));
