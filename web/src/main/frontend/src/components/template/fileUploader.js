import React,{Component} from 'react';
import {observer, inject} from 'mobx-react';
import PropTypes from 'prop-types'
import {
    Upload,
    Icon,
    message,
    Progress,
} from 'antd';
import _ from 'lodash';
import { CACHE_GLOBAL } from '../../constants';
import Cookie from 'js-cookie';
const Dragger = Upload.Dragger;

@inject('app')
@observer
class FileUploader extends Component{
    constructor(props) {
        super(props);
        this.state = {
            uploadFileList: []
        }
    }
    componentWillReceiveProps() {
        const { visible } = this.props;
        if (!visible) {
            this.setState({
                uploadFileList: []
            })
        }
    }
    onUploaderChange = (info) => {
        const { saveFileName } = this.props;
        const status = info.file.status;
        if (status === 'uploading') {
            this.setState({
                uploadFileList: info.fileList
            })
        }
        if (status === 'done') {
            message.success(`${info.file.name} 上传成功`);
            saveFileName(info.file.response.data);
        } else if (status === 'error') {
            message.error(`${info.file.name} 上传失败`);
        }
    }
    render() {
        const { visible,app: {selectSiteId} } = this.props;
        const progress = this.state.uploadFileList.map((item, index) => {
            return (
                <div className="progressWarp" key={item.uid + index}>
                    上传文件“{item.name}” <Progress percent={item.percent} />
                </div>
            );
        });
        const uploaderProps = {
            name: 'file',
            headers:{},
            multiple: true,
            showUploadList: false,
            action: '/api/templates/files?siteId='+selectSiteId
        };
        const token = _.get(Cookie.getJSON(CACHE_GLOBAL), 'token');
        if (!!token) {
            uploaderProps.headers.Authorization = token;
        }
        return (
            <div>
                {
                    visible ?
                        (
                            <div>
                                <Dragger {...uploaderProps} onChange={this.onUploaderChange}>
                                    <p className="ant-upload-drag-icon">
                                        <Icon type="inbox" />
                                    </p>
                                    <p className="ant-upload-text">点击或拖拽到此区域上传</p>
                                    <p className="ant-upload-hint">支持单个或多个文件</p>
                                </Dragger>
                                {progress}
                            </div>
                        ) : null
                }
            </div>
        );
    }
}

export default FileUploader;
