import React,{Component} from 'react';
import { withRouter } from 'react-router-dom';
import { observer, inject } from 'mobx-react';
import { Form, Input, Row, Col,Collapse, Button, DatePicker, Select, message} from 'antd';
import { toJS } from 'mobx';
import {Jt} from '../../utils';

import qs from 'qs';
import _ from 'lodash';
import UEditor from '../cms/ueditor';
import moment from 'moment';
import { TYPES } from '../../constants';
import ImgUploader from './imgUploader';
import AlbumUploader from './albumUploader';
import MediaUploader from './mediaUploader';

const FormItem = Form.Item;
const layout = {
    labelCol: {
        span: 7
    },
    wrapperCol: {
        span: 17
    }
};
const dateFormat = 'YYYY-MM-DD HH:mm:ss';
const SelectOption = Select.Option;
const TextArea = Input.TextArea;
const CollapsePanel = Collapse.Panel;


@inject('source')
@observer
class SourceEdit extends Component{
    constructor(props){
        super(props);
        const { location: { search }, source: { getArtItem,updateStore, setImportType  } } = props;
        const urlQuery = qs.parse(search.substr(1));
        if (!!urlQuery.id) {  //点击编辑进入详情页
            getArtItem(urlQuery.id)
        } else {  //新建进入详情页
            setImportType(0);
            updateStore({ curArt: {} });
        }

    }
    goBack = () => {
        const { history } = this.props;
        history.goBack()
    }

    fromAlbum = ({ fileList = [] }) => {
        const images = [];
        fileList.forEach(item => {
            if (item.url) {
                images.push({
                    index: +item.key,
                    image: item.url,
                    description: item.dec,
                    width: item.width,
                    height: item.height
                });
            }
        });
        return images;
    }
    fromThumbnail = ({ fileList = [] }) => {
        if (fileList.length === 0) {
            return '';
        }
        const urls = [];
        fileList.forEach(file => {
            urls.push(file.url);
        });
        return urls.join(',');
    }
    handleOnSave = (saveType) => {
        const { source: { curArt, onSaveArticle, importType, setImportType, updateStore, albumNum, audioNum, videoNum }, form: { getFieldsValue, resetFields }, history} = this.props;
        const values = {
            articleData: { ..._.omit(curArt.articleData, ['audios', 'audioJson', 'videos', 'videoJson', 'images', 'imageJson']) },
            ..._.omit(toJS(curArt), 'articleData'),
            ...getFieldsValue()
        }
        if (values.publishDate) {
            values.publishDate = values.publishDate.format(dateFormat);
        }
        if (values.thumbnail) {
            values.imageUrl = this.fromThumbnail(values.thumbnail);
        }
        if (albumNum === 1) {
            values.articleData.imageJson = this.fromAlbum(values.album);
        } else {
            delete values.articleData.imageJson;
        }
        delete values.album;
        const mediaIds = [];
        if (audioNum === 1) {
            if (values.audio.id) {
                mediaIds.push(values.audio.id);
            } else {
                values.audioUrl = values.audio.url;
            }
            values.audioCover = values.audio.coverImg;
        } else {
            delete values.audioUrl;
            delete values.audioCover;
        }
        delete values.audio;
        if (videoNum === 1) {
            if (values.video.id) {
                mediaIds.push(values.video.id);
            } else {
                values.videoUrl = values.video.url;
            }
            values.videoCover = values.video.coverImg;
        } else {
            delete values.videoUrl;
            delete values.videoCover;
        }
        delete values.video;
        values.mediaIds = mediaIds;

        const { articleData: { imageJson }, type } = values;
        //图集判断
        if (type === 'image') {
            if (imageJson.length < 1) {
                message.error('图集请至少上传一张图片');
                return false;
            }
        }
        const data = { ...values };
        if(importType !== null ) {
            data.importType = importType;
        }
        let callback = () => {
            resetFields();
            this.goBack();
        }

        data.articleData.content = this.refs.ueditor.getContent();
        if(saveType === 1) {
            callback = () => {
                resetFields();
                updateStore({
                    curArt:{
                       type:data.type
                    }
                });
                this.refs.ueditor.setContent('');
            }
        }else if(saveType === 2) {
            callback = (res) => {
                const { data } = res;
                updateStore({
                    curArt: data
                })
                message.info('保存成功！');
            }
        }else {
            callback = () => {
                resetFields();
                this.goBack();
                message.success('保存成功');
            }
        }
        onSaveArticle(data, callback);
    }
    onTypeChg = (type) => {
        const { source: { curArt, updateStore} } = this.props;
        updateStore({
            albumNum: type === 'image' ? 1 : 0,
            videoNum: type === 'video' ? 1 : 0,
            audioNum: type === 'audio' ? 1 : 0,
            curArt: { ...curArt, type }
        });
    }
    toThumbnail = (imageUrl = '') => {
        if (!imageUrl) {
            return {};
        }
        const urls = imageUrl.split(',http');
        const fileList = [];
        urls.forEach((url, i) => {
            if (i > 0) {
                url = 'http' + url;
            }
            fileList.push({
                uid: -(i + 1),
                name: url,
                url: url
            });
        });
        return { fileList };
    }
    getThumbnailTpl = () => {
        const { source: { curArt }, form: { getFieldDecorator } } = this.props;
        return (
            <FormItem>
                {getFieldDecorator('thumbnail', {
                    initialValue: this.toThumbnail(curArt.imageUrl)
                })(
                    <ImgUploader single={true}
                        tip="上传缩略图"
                    />
                )}
            </FormItem>
        );
    }
    getAlbumTpl = () => {
        const { source: {albumNum, curArt}, form: { getFieldDecorator } } = this.props;
        if (albumNum === 0) {
            return null;
        }
        return (
            <Collapse defaultActiveKey={['album']}>
                <CollapsePanel key="album" header="图集">
                    <FormItem>
                        {getFieldDecorator('album', {
                            initialValue: this.toAlbum()
                        })(<AlbumUploader />)}
                    </FormItem>
                </CollapsePanel>
            </Collapse>
        );
    }
    getAudioTpl = () => {
        const { source: {audioNum, curArt}, form: { getFieldDecorator } } = this.props;
        if (audioNum === 0) {
            return null;
        }
        return (
            <Collapse defaultActiveKey={['audio']}>
                <CollapsePanel key="audio" header="音频">
                    <FormItem>
                        {getFieldDecorator('audio', {
                            initialValue: this.toMedia('audio')
                        })(<MediaUploader type="audio" />)}
                    </FormItem>
                </CollapsePanel>
            </Collapse>
        );
    }
    getVideoTpl = () => {
        const { source: {videoNum, curArt}, form: { getFieldDecorator } } = this.props;
        if (videoNum === 0) {
            return null;
        }
        return (
            <Collapse defaultActiveKey={['video']}>
                <CollapsePanel key="video" header="视频">
                    <FormItem>
                        {getFieldDecorator('video', {
                            initialValue: this.toMedia('video')
                        })(<MediaUploader type="video" />)}
                    </FormItem>
                </CollapsePanel>
            </Collapse>
        );
    }
    toAlbum = () => {
        const { curArt: { articleData = {} } } = this.props.source;
        const imageJson = articleData.imageJson || [];
        if (Jt.array.isEmpty(imageJson)) {
            return { fileList: [] };
        }
        const fileList = [];
        imageJson.forEach((item, index) => {
            fileList.push({
                key: index,
                url: item.image,
                dec: item.description,
                width: item.width,
                height: item.height
            });
        });
        return { fileList };
    }
    toMedia = (type) => {
        const { curArt: { articleData = {} } } = this.props.source;
        const mediaJson = type === 'video' ? articleData.videoJson : articleData.audioJson;
        if (Jt.array.isEmpty(mediaJson)) {
            return { id: '', url: '', coverImg: '', name: '' };
        }
        const media = mediaJson[0];
        return {
            id: media.id,
            url: media.resources[0].url,
            coverImg: media.image,
            name: media.title
        }
    }
    // componentDidMount() {
    //     const that = this;
    //     window.onkeydown = function(e) {  //给保存和继续保存设置快捷键
    //         const currKey = e.keyCode;
    //         console.log("currKey-------keycode")
    //         console.log(currKey)
    //         if(currKey === 83 && (e.ctrlKey || e.metaKey) && e.shiftKey) {
    //             console.log('first')
    //             e.preventDefault();
    //             that.handleOnSave(1, true);
    //             return;
    //         }
    //         if(currKey === 83 && (e.ctrlKey || e.metaKey)) {
    //             e.preventDefault();
    //             that.handleOnSave()
    //             return;
    //         }
    //     }
    // }
    componentWillUnmount() {
        // window.onkeydown = null;
    }
    render(){
        const { form: { getFieldDecorator },source:{curArt}} = this.props;
        return (
            <Form>
                <Row>
                    <Col span={12}>
                        <FormItem label="肩标题" {...{labelCol: { span:4 }, wrapperCol: { span: 20}}}>
                            {getFieldDecorator('introTitle', {
                                initialValue: curArt.introTitle
                            })(
                                <Input placeholder="请输入肩标题" />
                            )}

                        </FormItem>
                    </Col>
                    <Col span={12}>
                        <FormItem label="列表标题" {...{ labelCol: { span: 4 }, wrapperCol: { span: 20 } }}>
                            {getFieldDecorator('listTitle', {
                                initialValue: curArt.listTitle
                            })(
                                <Input placeholder="请输入列表标题" />
                            )}
                        </FormItem>
                    </Col>
                </Row>
                <Row>
                    <Col span={12}>
                        <FormItem label="标题" {...{ labelCol: { span: 4 }, wrapperCol: { span: 20 } }}>
                            {getFieldDecorator('title', {
                                initialValue: curArt.title,
                                rules: [
                                    { required: true, message: '请输入标题' }
                                ]
                            })(
                                <Input placeholder="请输入标题" />
                            )}
                        </FormItem>
                    </Col>
                    <Col span={6}>
                        <FormItem label="作者" {...{ labelCol: { span: 8 }, wrapperCol: { span: 16 } }}>
                            {getFieldDecorator('authors', {
                                initialValue: curArt.authors
                            })(
                                <Input placeholder="请输入作者" />
                            )}
                        </FormItem>
                    </Col>
                    <Col span={6}>
                        <FormItem label="责任编辑" {...{ labelCol: { span: 8 }, wrapperCol: { span: 16 } }}>
                            {getFieldDecorator('responsibleUser', {
                                initialValue: curArt.responsibleUser
                            })(
                                <Input placeholder="请输入责任编辑" />
                            )}
                        </FormItem>
                    </Col>
                </Row>
                <Row>
                    <Col span={12}>
                        <FormItem label="副标题" {...{ labelCol: { span: 4 }, wrapperCol: { span: 20}}}>
                            {getFieldDecorator('subTitle',{
                                initialValue: curArt.subTitle
                            })(
                                <Input placeholder="请输入副标题" />
                            )}
                        </FormItem>
                    </Col>
                    <Col span={6}>
                        <FormItem label="来源" {...{ labelCol: { span: 8 }, wrapperCol: { span: 16 } }}>
                            {getFieldDecorator('source', {
                                initialValue: curArt.source
                            })(
                                <Input placeholder="请输入来源" />
                            )}
                        </FormItem>
                    </Col>
                    <Col span={6}>
                        <FormItem label="发布时间" {...{ labelCol: { span: 8 }, wrapperCol: { span: 16 } }}>
                            {
                                getFieldDecorator('publishDate', {
                                    initialValue: curArt.publishDate !== undefined ? moment(curArt.publishDate, dateFormat) : moment((new Date().format('yyyy-MM-dd hh:mm:ss')))
                                })(<DatePicker style={{ 'width': '200px' }} popupStyle={{ 'zIndex': '9999' }} showTime format={dateFormat} />)
                            }
                        </FormItem>
                    </Col>
                </Row>
                <Row>
                    <Col span={12}>
                        <Col span={24}>
                            <FormItem label="摘要" {...{ labelCol: { span: 4 }, wrapperCol: { span: 20 } }}>
                                {getFieldDecorator('description', {
                                    initialValue: curArt.description
                                })(<TextArea autosize={{ minRows: 4, maxRows: 6 }} placeholder="请输入摘要" />)}
                            </FormItem>
                        </Col>
                    </Col>
                    <Col span={6}>
                        <FormItem label="稿件类型" {...{ labelCol: { span: 8 }, wrapperCol: { span: 16 } }}>
                            {getFieldDecorator('type', {
                                initialValue: curArt.type || 'common'
                            })(
                                <Select onChange={this.onTypeChg}>
                                    {
                                        TYPES.map(item => {
                                            return <SelectOption key={item.key} value={item.key}>{item.label}</SelectOption>
                                        })
                                    }
                                </Select>
                            )}
                        </FormItem>
                    </Col>
                </Row>
                <Row type="flex" justify="center">
                    <Button onClick={this.handleOnSave.bind(this, 2)} type="primary" style={{'marginBottom': '20px', 'marginRight': '20px'}}>暂存</Button>
                    <Button onClick={this.handleOnSave.bind(this, 0)} type="primary" style={{'marginBottom': '20px', 'marginRight': '20px'}}>保存并返回</Button>
                    <Button onClick={this.handleOnSave.bind(this, 1)} type="primary" style={{'marginBottom': '20px', 'marginRight': '20px'}}>继续添加</Button>
                    <Button style={{'marginBottom':'20px'}} onClick={this.goBack}>取消</Button>
                </Row>
                <div>
                    <UEditor
                        ref="ueditor"
                        initialContent = {_.get(curArt, 'articleData.content')}
                    />
                </div>
                {this.getThumbnailTpl()}
                {this.getAlbumTpl()}
                {this.getAudioTpl()}
                {this.getVideoTpl()}
            </Form>
        )
    }
}

export default withRouter(Form.create()(SourceEdit));
