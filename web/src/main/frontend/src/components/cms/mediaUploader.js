import React from 'react'
import PropTypes from 'prop-types'
import { Tabs, Upload, Icon, message, Button, Input } from 'antd'
import { UPLOAD_FILE, urlPath } from '../../constants';
import '../../styles/cms/mediaUploader.less'
import MediaPicker from './mediaPicker'
import MediaPlayer from './mediaPlayer'

const TabPane = Tabs.TabPane;
const InputGroup = Input.Group;

class MediaUploader extends React.Component {

    constructor(props) {
        super(props)
        const state = props.value || {
            coverImg: '',
            url: '',
            id: ''
        }
        this.state = {
            ...state,
            type: this.props.type || 'video'
        }
    }
    componentWillReceiveProps(nextProps) {
        const state = nextProps.value;
        this.setState({
            ...state
        })
    }
    onCoverChange = (info) => {
        let coverImg = this.state.coverImg;
        if (info.file.status == 'done') {
            if (info.file.response && info.file.response.code == 0 && info.file.response.data) {
                coverImg = info.file.response.data.fileUrl;
            }
        }
        this.setState({ coverImg });
        this.triggerChange({ coverImg });
    }
    onUrlChange = (e) => {
        const url = e.target.value;
        const id = '';
        this.setState({ url, id });
        this.triggerChange({ url, id });
    }
    onMediaChange = (data) => {
        //根据传回来的mediaId判断媒体选择是否成功
        if (data && data.newMediaData && data.newMediaData.length > 0) {
            const newMediaData = data.newMediaData[0];
            const mediaData = {
                type: newMediaData.type,
                id: newMediaData.id,
                coverImg: newMediaData.cover || this.state.coverImg || '',
                url: newMediaData.hdUrl || newMediaData.mp3BigUrl,
                //type: newMediaData.hdUrl ? 'video' : 'audio',
                name: newMediaData.name
            }
            this.setState({ ...mediaData });
            this.triggerChange({ ...mediaData });
        }
    }
    triggerChange = (changedValue) => {
        // Should provide an event to pass value to Form.
        const onChange = this.props.onChange;
        if (onChange) {
            const { coverImg, url, id } = this.state;
            onChange(Object.assign({}, { coverImg, url, id }, changedValue));
        }
    }
    render() {
        const { id, type, url, coverImg, name } = this.state;
        const mediaPlayerProps = {
            type,
            name,
            url,
            onDelete: () => {
                this.triggerChange({ id: '', name: '', url: '', coverImg: '' });
            }
        };
        return (
            <div>
                <Tabs defaultActiveKey={url && !id ? '2' : '1'} className="tabCon">
                    <TabPane tab={<span><Icon type="appstore-o" />从媒体库选择</span>} key="1">
                        <MediaPicker type={type} onChange={this.onMediaChange} />
                        <MediaPlayer {...mediaPlayerProps} />
                    </TabPane>
                    <TabPane tab={<span><Icon type="link" />输入文件地址</span>} key="2">
                        <Input style={{ width: '80%' }} value={url} placeholder="请输入文件地址" onChange={this.onUrlChange} />
                        <span className="mediaPlayer">
                            <MediaPlayer {...mediaPlayerProps} />
                        </span>
                    </TabPane>
                </Tabs>
                <Upload
                    className="cover-uploader"
                    name="file"
                    showUploadList={false}
                    action={urlPath.UPLOAD_FILE}
                    onChange={(info) => this.onCoverChange(info)}
                >
                    {
                        coverImg ?
                            <img src={coverImg} alt="" className="cover-img" /> :
                            <Icon type="plus" className="cover-uploader-trigger" />
                    }
                </Upload>
                <div className="decWarper">
                    <span>封面图片</span>
                </div>
            </div>
        )
    }
}

MediaUploader.propTypes = {
    value: PropTypes.object
}
export default MediaUploader
