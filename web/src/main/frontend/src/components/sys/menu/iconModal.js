import React, { Component } from 'react';
import { Modal, Tabs, Icon } from 'antd';
import '../../../styles/sys/icon-modal.less'

const TabPane = Tabs.TabPane;

const icons = [
    {
        key: '1',
        label: '网站通用图标',
        items: [
            'lock', 'unlock', 'area-chart', 'pie-chart', 'bar-chart', 'dot-chart',
            'bars', 'book', 'calendar', 'cloud', 'cloud-download', 'code',
            'code-o', 'copy', 'credit-card', 'delete', 'desktop', 'download',
            'edit', 'ellipsis', 'file', 'file-text', 'file-unknown', 'file-pdf',
            'file-excel', 'file-jpg', 'file-ppt', 'file-add', 'folder', 'folder-open',
            'folder-add', 'hdd', 'frown', 'frown-o', 'meh', 'meh-o',
            'smile', 'smile-o', 'inbox', 'laptop', 'appstore-o', 'appstore',
            'line-chart', 'link', 'mail', 'mobile', 'notification', 'paper-clip',
            'picture', 'poweroff', 'reload', 'search', 'setting', 'share-alt',
            'shopping-cart', 'tablet', 'tag', 'tag-o', 'tags', 'tags-o',
            'to-top', 'upload', 'user', 'video-camera', 'home', 'loading',
            'loading-3-quarters', 'cloud-upload-o', 'cloud-download-o', 'cloud-upload', 'cloud-o', 'star-o',
            'star', 'heart-o', 'heart', 'environment', 'environment-o', 'eye',
            'eye-o', 'camera', 'camera-o', 'save', 'team', 'solution',
            'phone', 'filter', 'exception', 'export', 'customer-service', 'qrcode',
            'scan', 'like', 'like-o', 'dislike', 'dislike-o', 'message',
            'pay-circle', 'pay-circle-o', 'calculator', 'pushpin', 'pushpin-o', 'bulb',
            'select', 'switcher', 'rocket', 'bell', 'disconnect', 'database',
            'compass', 'barcode', 'hourglass', 'key', 'flag', 'layout',
            'printer', 'sound', 'usb', 'skin', 'tool', 'sync',
            'wifi', 'car', 'schedule', 'user-add', 'user-delete', 'usergroup-add',
            'usergroup-delete', 'man', 'woman', 'shop', 'gift', 'idcard',
            'medicine-box', 'red-envelope', 'coffee', 'copyright', 'trademark', 'safety',
            'wallet', 'bank', 'trophy', 'contacts', 'global', 'shake',
            'api', 'fork'
        ]
    },
    {
        key: '2',
        label: '方向性图标',
        items: [
            'step-backward', 'step-forward', 'fast-backward', 'fast-forward', 'shrink', 'arrows-alt',
            'down', 'up', 'left', 'right', 'caret-up', 'caret-down',
            'caret-left', 'caret-right', 'up-circle', 'down-circle', 'left-circle', 'right-circle',
            'up-circle-o', 'down-circle-o', 'right-circle-o', 'left-circle-o', 'double-right', 'double-left',
            'verticle-left', 'verticle-right', 'forward', 'backward', 'rollback', 'enter',
            'retweet', 'swap', 'swap-left', 'swap-right', 'arrow-up', 'arrow-down',
            'arrow-left', 'arrow-right', 'play-circle', 'play-circle-o', 'up-square', 'down-square',
            'left-square', 'right-square', 'up-square-o', 'down-square-o', 'left-square-o', 'right-square-o',
            'login', 'logout', 'menu-fold', 'menu-unfold'
        ]
    },
    {
        key: '3',
        label: '提示建议性图标',
        items: [
            'question', 'question-circle-o', 'question-circle', 'plus', 'plus-circle-o', 'plus-circle',
            'pause', 'pause-circle-o', 'pause-circle', 'minus', 'minus-circle-o', 'minus-circle',
            'plus-square', 'plus-square-o', 'minus-square', 'minus-square-o', 'info', 'info-circle-o',
            'info-circle', 'exclamation', 'exclamation-circle-o', 'exclamation-circle', 'close', 'close-circle',
            'close-circle-o', 'close-square', 'close-square-o', 'check', 'check-circle', 'check-circle-o',
            'check-square', 'check-square-o', 'clock-circle-o', 'clock-circle'
        ]
    },
    {
        key: '4',
        label: '品牌和标识',
        items: [
            'android', 'android-o', 'apple', 'apple-o', 'windows', 'windows-o',
            'ie', 'chrome', 'github', 'aliwangwang', 'aliwangwang-o', 'dingding',
            'dingding-o'
        ]
    }
];

class IconModal extends Component {

    render() {
        const {
            visible,
            onCancel,
            onSelect,
            width = 640
        } = this.props;
        const modalProps = {
            visible,
            onCancel,
            width,
            title: '选择图标',
            footer: false,
            wrapClassName: 'vertical-center-modal'
        }
        return (
            <Modal {...modalProps} className='icon-modal'>
                <Tabs defaultActiveKey="1">
                    {
                        icons.map(record => {
                            return (
                                <TabPane key={record.key} tab={record.label}>
                                    <ul>
                                        {
                                            record.items.map(item => {
                                                return (
                                                    <li className="icon-item" key={item} onClick={() => onSelect(item)}>
                                                        <Icon type={item} />
                                                    </li>
                                                );
                                            })
                                        }
                                    </ul>
                                </TabPane>
                            );
                        })
                    }
                </Tabs>
            </Modal>
        );
    }
}

export default IconModal;
