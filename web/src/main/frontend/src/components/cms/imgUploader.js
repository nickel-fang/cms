import React from 'react';
import { Upload, Button, Icon, Modal, message } from 'antd';
import { urlPath, IMG_UPLOAD_TYEPS } from '../../constants';
import '../../styles/cms/imgUploader.less';

class ImgUploader extends React.Component {
    constructor(props) {
        super(props);
        let { accept, value = {} } = this.props;
        if (value.fileList) {
            value.fileList.map((item, i) => {
                if (typeof item.uid === undefined) {
                    item.uid = -(i + 1);
                }
                item.index = i;
            });
        }

        if (!accept) {
            accept = [];
            IMG_UPLOAD_TYEPS.forEach(type => {
                accept.push(`image/${type}`);
            });
            accept = accept.join(',');
        }

        this.state = {
            accept,
            previewVisible: false,
            previewImage: '',
            fileList: value.fileList || []
        };
    }

    imgSave = (imgId, imgUrl) => {
        const fileList = this.state.fileList;
        fileList[imgId].url = imgUrl;
        this.setState({ fileList, previewVisible: false });
    }

    handlePreview = (file) => {
        this.setState({ previewImage: file.url, previewVisible: true, previewIndex: file.index });
    }

    handleChange = (info) => {
        let { file, fileList } = info;
        if (file.status == 'error') {
            fileList = this.state.fileList;
        } else if (file.response && file.response.code !== 0) {
            message.error(file.response.msg);
            fileList = fileList.filter(item => item.uid !== file.uid);
        } else {
            if (this.props.single) {
                fileList = fileList.slice(-1);
            }

            fileList = fileList.map((file, i) => {
                if (file.response && file.response.code === 0 && file.response.data) {
                    file = {
                        name: file.name,
                        url: file.response.data.fileUrl,
                        uid: file.uid,
                        index: i
                    };
                }
                return file;
            });

            fileList = fileList.filter((file) => {
                if (file.response) {
                    return file.response.code === 0;
                }
                return true;
            });
        }

        this.setState({ fileList });

        const onChange = this.props.onChange;
        if (onChange && file.status == 'done' || file.status == 'removed') {
            onChange({ fileList });
        }
    }

    handleCancel = () => {
        this.setState({ previewVisible: false });
    }

    componentWillReceiveProps(nextProps) {
        if ('value' in nextProps && nextProps.value) {
            const value = nextProps.value;
            if (value.fileList) {
                value.fileList.map((file, i) => {
                    file.index = i;
                });
            }
            const state = {
                fileList: value.fileList || []
            }
            this.setState(state);
        }
    }

    render() {
        const { accept, previewVisible, previewImage, previewIndex, fileList } = this.state;
        const { width, height, ratio } = this.props;

        const uploadButton = (
            <div>
                <Icon type="plus" />
                <div className="ant-upload-text">{this.props.tip || '上传'}</div>
            </div>
        );
        return (
            <div className="clearfix">
                <Upload
                    accept={accept}
                    action={urlPath.UPLOAD_FILE}
                    listType="picture-card"
                    fileList={fileList}
                    onPreview={this.handlePreview}
                    onChange={this.handleChange}
                    multiple={this.props.single ? false : true}
                    className="img-uploader"
                >
                    {
                        this.props.single && fileList.length >= 1
                            ? null
                            : uploadButton
                    }
                </Upload>
                <Modal
                    title="图片预览"
                    width={640}
                    visible={previewVisible}
                    footer={null}
                    onCancel={this.handleCancel}
                    zIndex='9999'
                >
                    <img alt="example" style={{ width: '100%' }} src={previewImage} />
                </Modal>
            </div>
        );
    }
}

export default ImgUploader;
