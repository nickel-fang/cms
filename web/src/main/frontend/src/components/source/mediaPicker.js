import React from 'react'
import PropTypes from 'prop-types'
import {
    Modal,
    Table,
    Select,
    Button,
    Tabs,
    Upload,
    Icon,
    message,
    DatePicker,
    Input,
    Progress,
    Form
} from 'antd';
import { observer, inject } from 'mobx-react';
import { toJS } from 'mobx';
import {
    PAGE_SIZE,
    urlPath
} from '../../constants'
import '../../styles/cms/mediaPicker.less'

const FormItem = Form.Item
const TabPane = Tabs.TabPane;
const Dragger = Upload.Dragger;
const Option = Select.Option;
const RangePicker = DatePicker.RangePicker;

const uploaderProps = {
    name: 'file',
    multiple: true,
    showUploadList: false,
    action: urlPath.UPLOAD_FILES,
};

const columns = [
    {
        title: '文件',
        dataIndex: 'file',
        render: function (value, row, index) {
            return {
                children:
                    <div >
                        {value.cover ? <img src={value.cover} className="coverImg" /> : ''}
                        <h3><a> {value.name}</a></h3>
                        <br />
                        <span> {value.autoName}</span>
                    </div>,
                props: {},
            };
        }
    },
    {
        title: '大小',
        dataIndex: 'size',
    },
    {
        title: '上传时间',
        dataIndex: 'uploadTime',
        render: function (value, row, index) {
            if (value) {
                return (new Date(value)).format('yyyy-MM-dd hh:mm:ss');
            } else {
                return '';
            }
        }
    }
];

@inject('app')
@observer
class MediaPickerTmp extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            selectedRowKeys: [],
            loading: false,
            visible: false,
            uploadFileList: [],
            type: props.type || 'video'
        }
    }

    componentWillReceiveProps(nextProps) {
        if (nextProps.type !== this.props.type) {
            this.setState({
                type: this.props.type
            });
        }
    }

    queryMediaList = (payload = {})=>{
        const { getMediaList } = this.props.app
        let params = Object.assign({
            pageNumber: 1,
            pageSize: PAGE_SIZE
        }, payload)
        getMediaList({ ...params })
    }

    showModal = () => {
        this.setState({
            visible: true,
        });
        const { resetFields } = this.props.form;
        resetFields();
        //加载媒体列表
        this.queryMediaList({ type: this.state.type })

    }
    handleOk = () => {
        this.setState({
            loading: true
        });
        const newMediaData = this.props.app.mediaList.filter((item) => {
            return this.state.selectedRowKeys == item.id;
        });

        // Should provide an event to pass value to Form.
        const onChange = this.props.onChange;
        if (onChange) {
            onChange(Object.assign({}, { newMediaData }));
        }

        this.setState({
            loading: false,
            visible: false
        });

    }
    handleCancel = () => {
        this.setState({
            visible: false
        });
    }
    handleSearch = () => {
        const { getFieldsValue } = this.props.form
        const data = getFieldsValue()
        if (data.date && data.date.length !== 0) {
            data.startTime = data.date[0].format('YYYY-MM-DD')
            data.endTime = data.date[1].format('YYYY-MM-DD')
        }
        if (!data.keyword) {
            delete data.keyword;
        }
        delete data.date;
        data.pageNumber = 1;
        data.type = this.state.type;
        this.queryMediaList(data)
    }
    handleSelectTypeChange = (value) => {
        this.setState({
            type: value
        });
        this.queryMediaList({ type: value })
    }
    onMideaSelectChange = (selectedRowKeys) => {
        this.setState({
            selectedRowKeys
        });
    }
    onUploaderChange = (info) => {
        const status = info.file.status;
        if (status === 'uploading') {
            this.setState({
                uploadFileList: info.fileList
            })
        }
        if (status === 'done') {
            message.success(`${info.file.name} 上传成功.`);
        } else if (status === 'error') {
            message.error(`${info.file.name} 上传失败.`);
        }
    }
    render() {
        const { type, selectedRowKeys } = this.state;
        const { getFieldDecorator } = this.props.form
        const { mediaList, mediaListPagination, getMediaList} = this.props.app;
        const rowSelection = {
            type: 'radio',
            selectedRowKeys,
            onChange: this.onMideaSelectChange,
        };

        //待处理调整数据结构
        const listData = mediaList.map((item, index) => {
            const newItem = {
                key: item.id,
                file: {
                    cover: item.cover || '',
                    name: item.name,
                    autoName: item.autoName,
                    hdUrl: item.hdUrl
                },
                size: item.hdSize,
                uploadTime: item.uploadTime,
                id: item.id
            }
            return newItem;
        });

        const tabelProps = {
            dataSource: listData,
            pagination: mediaListPagination,
            onChange: (page) => {
                getMediaList({
                    type,
                    pageNumber: page.current,
                    pageSize: page.pageSize
                })
            }
        };

        const progress = this.state.uploadFileList.map((item, index) => {
            return (
                <div className="progressWarp" key={item.uid}>
                    上传文件“{item.name}” < Progress percent={item.percent} />
                </div>
            );
        });

        return (
            <span>
                <Button type="primary" icon="appstore-o" onClick={this.showModal}>
                    选择媒体文件
                </Button>
                <Modal
                    wrapClassName='media-modal-wrap'
                    visible={this.state.visible}
                    title="选择媒体文件"
                    width='90%'
                    onOk={this.handleOk}
                    onCancel={this.handleCancel}
                    footer={[
                        <Button key="back" onClick={this.handleCancel}>取消</Button>,
                        <Button key="submit" type="primary" loading={this.state.loading} onClick={this.handleOk}>确定 </Button>,
                    ]}
                >
                    <Tabs type="card" defaultActiveKey="2">
                        <TabPane tab="上传文件" key="1">
                            <Dragger {...uploaderProps} accept={type == 'audio' ? 'audio/mp3':'video/mp4'} onChange={this.onUploaderChange} className="draggerWarper">
                                <div className="draggerWarp">
                                    <p className="ant-upload-drag-icon"><Icon type="inbox" /></p>
                                    <p className="ant-upload-text">拖拽到此处上传</p>
                                    <p className="ant-upload-hint">支持拖拽一个或多个文件上传, 仅支持{type == 'audio' ? 'mp3':'mp4'}格式文件</p>
                                </div>
                            </Dragger>
                            {progress}
                        </TabPane>
                        <TabPane tab="媒体库" key="2">
                            <div>
                                <Form layout="inline">
                                    {/*<FormItem>
                                        <Select value={type} style={{ width: 120 }} onChange={this.handleSelectTypeChange}>
                                            <Option value="video">视频</Option>
                                            <Option value="audio">音频</Option>
                                        </Select>
                                    </FormItem>*/}
                                    <FormItem>
                                        {getFieldDecorator('date', {
                                            initialValue: null
                                        })(
                                            <RangePicker format={'YYYY-MM-DD'} />
                                        )}
                                    </FormItem>
                                    <FormItem>
                                        {getFieldDecorator('keyword', {
                                            initialValue: ''
                                        })(
                                            <Input placeholder="关键词" style={{ width: 200 }} />
                                        )}
                                    </FormItem>
                                    <FormItem >
                                        <Button type="primary" icon="search" onClick={this.handleSearch} />
                                    </FormItem>
                                </Form>
                            </div>
                            <div style={{ marginTop: 10 }}>
                                <Table rowSelection={rowSelection} columns={columns} {...tabelProps} />
                            </div>
                        </TabPane>
                    </Tabs>
                </Modal>
            </span>
        );
    }
}

MediaPickerTmp.propTypes = {
    value: PropTypes.object,
    onChange: PropTypes.func
}

export default Form.create()(MediaPickerTmp);
