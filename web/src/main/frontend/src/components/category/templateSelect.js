import React,{Component} from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter, Link } from 'react-router-dom';
import { toJS } from 'mobx';
import { Button, Modal, Table, Icon, Input, Form, Row, Col} from 'antd';
const FormItem = Form.Item;

@inject('category')
@observer
class TempSelect extends Component{
    constructor(props){
        super(props);
        let { value = {}, category: { curCatg },type} = this.props;
        this.state = {
            templateId: value.templateId||null,
            pageTemplateId: value.pageTemplateId || null,
            templateName:'',
            visible: false,
        }
        if(type=="detail"){
            this.state.templateName = curCatg.template ? curCatg.template.name : ''
        }else if(type="category"){
            this.state.templateName = curCatg.pageTemplate ? curCatg.pageTemplate.name : ''
        }
    }
    componentWillReceiveProps(nextProps){  //触发说明curCatg有值了
        if (nextProps.value) {
            const value = nextProps.value;
            const templateId = value.templateId || null;
            const pageTemplateId = value.pageTemplateId || null;
            this.setState({ templateId, pageTemplateId });
        }
        if (!this.needchange){
            this.getTemplateName()
        }
    }
    getTemplateName = () =>{
        let { type, category: { curCatg } } = this.props;
        if(type=="category"){
            this.setState({
                templateName: curCatg.pageTemplate ? curCatg.pageTemplate.name : '',
            })
        } else if (type == "detail"){
            this.setState({
                templateName: curCatg.template ? curCatg.template.name : '',
            })
        }
    }
    getColumns = () => {
        return [
            {
                title: '模版名称',
                dataIndex: 'name',
                width: 100
            },
            {
                title: '模版描述',
                dataIndex: 'description',
                width: 100
            },
            {
                title: '操作',
                width: 100,
                className: 'action',
                render: (text, record) => {
                    const id = record.id;
                    const name = record.name;
                    return [
                        <a
                            key="action-1"
                            onClick={this.onSelect.bind(this, record)}
                            style={{ 'marginRight': '5px' }}
                        >
                            选择
                        </a>
                    ];
                }
            }
        ]
    }
    onSelect = (record) => {
        const {category:{templateDataSource},type} = this.props;
        this.needchange = true;
        this.setState({ visible: false, templateName: record.name });
        if(type=='detail'){
            this.triggerChange({ templateId: record.id });
        } else if (type == 'category'){
            this.triggerChange({ pageTemplateId: record.id });
        }
    }
    triggerChange(changedValue) {
        const { type } = this.props;
        const onChange = this.props.onChange;
        if (onChange) {
            if (type == 'detail'){
                onChange({ templateId: this.state.templateId, ...changedValue });
            } else if (type == 'category'){
                onChange({ pageTemplateId: this.state.pageTemplateId, ...changedValue });
            }
        }
    }
    hide = () =>{
        const { form: { resetFields }} = this.props;
        this.setState({
            visible: false
        });
        resetFields();
    }
    show = ()=>{
        const { category: { getTemplateList }, siteId } = this.props;
        getTemplateList({ siteId });
        this.setState({
            visible: true
        });
    }
    onDelete = () => {
        const { type } = this.props;
        this.needchange = true;
        this.setState({ templateName: '' });
        if (type == 'detail') {
            this.triggerChange({ templateId: null });
        } else if (type == 'category') {
            this.triggerChange({ pageTemplateId: null });
        }
    }
    onPageChange = (page) => {
        const { category: { getTemplateList, query }, siteId } = this.props;
        getTemplateList({ ...query,pageNumber: page.current, pageSize: page.pageSize, siteId });
    }
    onSearch = (e) => {
        const { form: { getFieldsValue }, category: { query, updateStore, getTempByName }} = this.props;
        let data = getFieldsValue();
        const Cquery = toJS(query);
        data = { ...Cquery, ...data }
        for(let key in data){  //去掉为空的搜索项
            if(!data[key]){
                delete data[key]
            }
        }
        data.pageNumber = "1"
        updateStore({query})
        getTempByName(data)
    }
    render(){
        const { category: { curCatg, templateDataSource, templatePagination, loading, query }, type, form: { getFieldDecorator }} = this.props;
        const { visible, templateName } = this.state;
        const modalOpts = {
            title: '',
            visible,
            width: '80%',
            footer: null,
            onCancel: this.hide.bind(this)
        };

        const pagination = toJS(templatePagination)
        const dataSource = toJS(templateDataSource)
        return (
            <div className="template-select">
                <Modal {...modalOpts}>
                    <Form style={{"marginBottom": "20px"}}>
                        <Row>
                            <Col span={8}>
                                <FormItem label="模板名称" {...{labelCol: {span: 6}, wrapperCol: {span: 18}}}>
                                    {getFieldDecorator('name', {
                                        initialValue: ''
                                    })(
                                        <Input />
                                    )}
                                </FormItem>
                            </Col>
                            <Col span={4} style={{"marginLeft": "20px", "marginTop": "4px"}}>
                                <Button type="primary" onClick={this.onSearch}>查询</Button>
                            </Col>
                        </Row>
                    </Form>
                    <Table
                        simple
                        size="middle"
                        columns={this.getColumns()}
                        dataSource={dataSource}
                        pagination={pagination}
                        onChange={this.onPageChange}
                        loading={loading}
                        rowKey={record => record.id}
                    />
                </Modal>
                {templateName ? (<span className="template-name">
                    {templateName}
                    <Icon className="template-delete" type="close" onClick={this.onDelete} />
                </span>):null}
                <Button onClick={this.show}>
                    选择模版
                </Button>
            </div>
        )
    }
}

export default Form.create()(TempSelect)