import React,{Component} from 'react';

import { toJS } from 'mobx';
import { Button, Row, Col, Form, Input, DatePicker, Modal, Select, Table, message} from 'antd';
import { observer, inject } from 'mobx-react';
import {Jt} from '../../utils';


const RangePicker = DatePicker.RangePicker;
const FormItem = Form.Item;
const Option = Select.Option;
const dateFormat = 'YYYY-MM-DD HH:mm:ss';


@inject('cms')
@inject('app')
@inject('source')
@observer
class SourceModal extends Component {
  constructor(props){
    super(props);
    this.state = {
      selectedRowKeys: []
    }
  }

  componentWillReceiveProps(nextProps){
    const { cms: { updateStore }} = this.props;
    if(nextProps.cms.modalVisible !== this.props.cms.modalVisible) {
      updateStore({ modalVisible: nextProps.cms.modalVisible})
    }
  }

  handleModalOk = (e) => {
    const { cms: { selSouArts, insertArts, query }, app: { selectSiteId, sites }, form: { resetFields}} = this.props;
    const list = []

    this.selectSiteId = selectSiteId;
    if (selSouArts.length === 0) {
      message.error('请选择文章');
      return;
    }

    for (let i = 0; i < selSouArts.length; i++) {
      if (selSouArts[i].delFlag !== 0) {
        list.push(selSouArts[i]);
      }
    }
    let callback = function () {
      resetFields();
    }
    insertArts({list, categoryId: query.categoryId}, callback);
    this.handleModalCancel();

  }

  handleModalCancel = () => {
    this.setState({ selectedRowKeys: []})
    this.props.handleModalCancel()
  }

  onPageChange = (page) => {
    const {
        source: {articleList, query}
    } = this.props;
    articleList({...query, pageNumber: page.current, pageSize: page.pageSize})
  }
  getFormData = () => {
    const { form: {getFieldsValue }, app: { selectSiteId, sites }, source: {updateStore, articleList}} = this.props;
    this.selectSiteId = selectSiteId;
    let data = getFieldsValue();
    if (!Jt.array.isEmpty(data.publishDate)) {
        data.beginTime = data.publishDate[0].format(dateFormat);
        data.endTime = data.publishDate[1].format(dateFormat);
    }
    delete data.publishDate;
    for(let key in data){  //去掉为空的搜索项
        if(!data[key]){
            delete data[key]
        }
    }
    return data;
  }
  onSearch = () => {
    const { source: {updateStore, articleList}} = this.props;
    const data = this.getFormData();
    data.pageNumber = 1;  //当查询时，需要把当前页设置为第一页，否则会带入当前页数被查询。
    updateStore({query: data});
    articleList(data);
  }
  onImportTypeChange = (value) => {
    const {  source: {updateStore, articleList}} = this.props;
    const data = this.getFormData();
    data.importType = value;
    data.pageNumber = 1;  //当查询时，需要把当前页设置为第一页，否则会带入当前页数被查询。
    updateStore({query: data});
    articleList(data);
  }
  getColumns = () => {
    const columns = [{
      title: '标题',
      dataIndex: 'title',
      // render: (text, record)=> <a onClick={this.onEdit.bind(this,record.id)}>{text}</a>,
    }, {
      title: '来源',
      dataIndex: 'importType',
      width: 100,
      render: (text) => {
        switch(text) {
          case 0: return <span>手工导入</span>;
            break;
          case 1: return <span>外部导入</span>;
            break;
          default: return <span>外部导入</span>;
        }
      }
    }, {
      title: '录入时间',
      width: 200,
      dataIndex: 'createAt',
    }];
    return columns;
  }
  render(){
    const { form: { getFieldDecorator }, source: { loading, pagination }, cms: { updateStore, modalVisible }} = this.props;
    const dataSource = [...this.props.source.dataSource];
    const rowSelection = {
      selectedRowKeys: this.state.selectedRowKeys ,
      onChange: (selectedRowKeys, selectedRows) => {
        this.setState({selectedRowKeys})
        updateStore({
          selSouArts: selectedRows
        })
      },
      getCheckboxProps: record => ({
        disabeld: record.name === 'Disabled User',
        name: record.name,
      }),
    }

    return(
      <Modal
        width='1000px'
        visible={ modalVisible }
        onCancel={this.handleModalCancel}
        footer = {null}
        >
        <Form>
          <Row>
          <Col span={5} style={{"marginRight": "10px"}}>
                <FormItem label="标题" {...{labelCol: {span: 6}, wrapperCol: {span: 18}}}>
                    {getFieldDecorator('title', {
                        initialValue: ''
                    })(
                        <Input />
                    )}
                </FormItem>
            </Col>
            <Col span={4}>
              <FormItem
                label="来源"
                {...{labelCol: {span: 6}, wrapperCol: {span: 18}}}
                >
                  {getFieldDecorator('importType',{
                    initialValue: '',
                    rules:[{message:'请输入'}]
                  })(
                    <Select placeholder="please" onChange={this.onImportTypeChange}>
                      <Option value=''>全部</Option>
                      <Option value='0'>手工导入</Option>
                      <Option value='1'>外部导入</Option>
                    </Select>
                  )}

              </FormItem>
            </Col>
            <Col span={12}>
                <FormItem label="录入时间" {...{labelCol: {span: 5}, wrapperCol:{span:18}}}>
                    {getFieldDecorator('publishDate',{
                        initialValue:''
                    })(
                        <RangePicker showTime format={dateFormat} />
                    )}

                </FormItem>
            </Col>

            <Col span={2}>
                <Button
                  type="primary"
                  style={{'marginLeft': '10px', 'marginTop': '4px'}}
                  onClick={this.onSearch}
                  >
                    查询
                  </Button>
            </Col>
          </Row>
        </Form>
        <Table
          simple
          bordered
          columns={this.getColumns()}
          dataSource={dataSource}
          rowSelection={rowSelection}
          rowKey={record => record.id}
          loading={loading}
          pagination={pagination}
          onChange={this.onPageChange}
          />

        <div style={{"marginTop": '20px'}}>
            <Button type="primary" onClick={this.handleModalOk} style={{marginRight: 20}}>插入</Button>
            <Button type="primary" onClick={this.handleModalCancel}>取消</Button>
        </div>
    </Modal>
    )



  }
}


export default Form.create()(SourceModal);