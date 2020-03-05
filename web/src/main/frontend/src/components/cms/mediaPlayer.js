import React from 'react'
import PropTypes from 'prop-types'
import { Modal, Row, Col, Icon } from 'antd'
import _ from 'lodash';
import '../../styles/cms/mediaPlayer.less';

class MediaPlayer extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            visible: props.visible || false,
            type: props.type || 'video',
            name: props.name || '',
            url: props.url || ''
        }
    }
    componentWillReceiveProps(nextProps) {
        this.setState({
            ..._.omit(nextProps, ['onDelete'])
        })
    }
    handleClose = () => {
        const type = this.state.type;
        const media = this.refs[type];
        if (media) {
            media.pause();
        }
        this.setState({ visible: false });
    }
    onOk = () => {
        this.handleClose()
    }
    onCancel = () => {
        this.handleClose()
    }
    showMediaPlayerModal = () => {
        this.setState({ visible: true });
    }
    render() {
        const { type, name, url, visible } = this.state;
        const { onDelete } = this.props;
        const modelProps = {
            visible,
            title: '预览',
            onOk: this.onOk,
            onCancel: this.onCancel,
            zIndex:'9999'
        }
        return (
            <span>
                <span style={{ marginLeft: 10 }}>{name}</span>
                <a onClick={this.showMediaPlayerModal} style={{ marginLeft: 10 }}>预览</a>
                {
                    url ?
                        <a onClick={onDelete} style={{ marginLeft: 10, fontSize: 16 }}>
                            <Icon type="delete" />
                        </a> :
                        undefined
                }
                <Modal {...modelProps} className="mediaPlayer">
                    {
                        type === 'video'
                            ? <video ref="video" src={url} controls>浏览器版本过低，不支持video</video>
                            : <audio ref="audio" src={url} controls>浏览器版本过低，不支持audio</audio>
                    }
                </Modal>
            </span>
        )
    }
}
MediaPlayer.propTypes = {
    type: PropTypes.string,
    visible: PropTypes.bool,
    url: PropTypes.string
}
export default MediaPlayer
