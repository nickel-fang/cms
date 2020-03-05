import React, { Component } from 'react';
import {withRouter} from 'react-router-dom';
import { observer, inject } from 'mobx-react';
import { toJS } from 'mobx';
import { Button, Form, Row, Col, Input, Select, Collapse, TreeSelect, DatePicker, Popconfirm, message, Modal} from 'antd';
import '../../styles/cms/articleEdit.less';
import qs from 'qs';
import _ from 'lodash';
import moment from 'moment';
import {Jt} from '../../utils';
import { TYPES } from '../../constants';
import ImgUploader from './imgUploader';
import AlbumUploader from './albumUploader';
import MediaUploader from './mediaUploader';
import { ROUTER_PATHS } from '../../constants'
import UEditor from './ueditor';
const FormItem = Form.Item;
const SelectOption = Select.Option;
const CollapsePanel = Collapse.Panel;
const TextArea = Input.TextArea;
const layout = {
    labelCol: {
        span: 7
    },
    wrapperCol: {
        span: 17
    }
};
const SHOW_PARENT = TreeSelect.SHOW_PARENT;
const dateFormat = 'YYYY-MM-DD HH:mm:ss';
@inject('app')
@inject('cms')
@observer
class CmsEdit extends Component{
    constructor(props){
        super(props)
        const {cms,history} = props;
        const Cquery = toJS(cms.query);
        this.state = {
            tCategoryId: [],
            rejectModal: false
        }
        if (!Cquery.delFlag) {  //直接进编辑页无query时 直接退回列表页，以防操作站点引起改变而编辑页无法处理
            history.push(ROUTER_PATHS.ARTICLES);
        }
        const { location: { search }, cms: { getArtItem,updateStore, getChannelItem, query, articleId } } = props;
        const urlQuery = qs.parse(search.substr(1));
        if (!!urlQuery.id) {  //点击编辑进入详情页
            getArtItem(urlQuery.id);
            articleId && getChannelItem(articleId); // 获取一稿多发中的其他频道列表
        } else {  //新建进入详情页
            let temp = []
            query.categoryId ? temp.push(query.categoryId+'') : temp.push('1');
            updateStore({ curArt: {}, channelList: temp });
        }
    }
    goBack = () => {
        const {history} = this.props;
        history.goBack()
    }
    handleOnSave = (delFlag, saveType) => {
        //savaType: 0 正常保存  1 继续添加  2 暂时保存
        const { cms: { onSaveDraft, onSaveChannel, curArt, query, onSaveAudit, onSave, albumNum, audioNum, videoNum, updateStore, channelList}, form: { validateFields, getFieldsValue, resetFields } } = this.props;
        validateFields((errors, val) => {
            if (errors) {
                return;
            }
            const values = {
                categoryId: query.categoryId,
                articleData: { ..._.omit(curArt.articleData, ['audios', 'audioJson', 'videos', 'videoJson', 'images', 'imageJson']) },
                ..._.omit(toJS(curArt), 'articleData'),
                delFlag,
                ...getFieldsValue()
            };
            if (values.publishDate) {
                values.publishDate = values.publishDate.format(dateFormat);
            }
            if (values.thumbnail) {
                values.imageUrl = this.fromThumbnail(values.thumbnail);
            }
            delete values.thumbnail;
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
            //各种判断
            const { articleData: { imageJson }, type } = values;
            //图集判断
            if (type === 'image') {
                if (imageJson.length < 1) {
                    message.error('图集请至少上传一张图片');
                    return false;
                }
            }
            const data = { ...values };
            data.articleData.content = this.refs.ueditor.getContent();
            const channelData = {
                list: toJS(channelList),
                article: data
            }
            let callback = () => {
                resetFields();
                this.goBack();
                message.success('保存成功');
            }
            if(saveType === 1){
                callback = () => {
                    resetFields();
                    const channelList = [];
                    channelList.push(query.categoryId + '')
                    updateStore({
                        curArt:{
                           type:data.type
                        },
                        channelList,
                    });
                    this.refs.ueditor.setContent('');
                    message.success('保存成功');
                }
            }else if(saveType === 2){
                callback = () => {
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
            onSaveChannel({list: toJS(channelList), article: data});  // 一稿多发-保存发布的渠道
            if (query.delFlag == 2) {  //待审核状态需要调用audit接口
                onSaveAudit(data, callback)
            } else if (query.delFlag == 1 || query.delFlag == 4) { //草稿，
                onSaveDraft(data, callback);   //delFlag为保存为那种状态，草稿或待审核等
            } else {  //上线，下线
                onSave(data, callback)
            }
        });
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
    toAlbum = () => {
        const { curArt: { articleData = {} } } = this.props.cms;
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
        const { curArt: { articleData = {} } } = this.props.cms;
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
    getAlbumTpl = () => {
        const { cms:{albumNum, curArt}, form: { getFieldDecorator } } = this.props;
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
        const { cms:{audioNum, curArt}, form: { getFieldDecorator } } = this.props;
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
        const { cms:{videoNum, curArt}, form: { getFieldDecorator } } = this.props;
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
    getThumbnailTpl = () => {
        const { cms:{curArt}, form: { getFieldDecorator } } = this.props;
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
    onTypeChg = (type) => {
        const { cms: { curArt, updateStore} } = this.props;
        updateStore({
            albumNum: type === 'image' ? 1 : 0,
            videoNum: type === 'video' ? 1 : 0,
            audioNum: type === 'audio' ? 1 : 0,
            curArt: { ...curArt, type }
        });
    }
    onDelete = (id) => {
        const { cms: { deleteArt }, history } = this.props;
        const cb = () => {
            history.push(ROUTER_PATHS.ARTICLES);
        }
        deleteArt(id,cb)
    }
    onPreview = (id) => {
        const { cms:{preview} } = this.props;
        var newTab = window.open('', '_blank');
        const callback = (res) => {
            const { code } = res;
            if (code == 0) {
                const location = res.data;
                if(!!location){
                    newTab.location.href = location;
                }else{
                    newTab.close();
                    message.error('该频道没有绑定详情模版')
                }
            }
        }
        preview(id,callback)
    }
    setModalVisible(status) {
        this.setState({rejectModal: status})
    }
    getStatusBtn = () => {
        const { cms: { query, curArt}, form: { getFieldDecorator }} = this.props;
        if(query.delFlag==1){  //草稿
            return (
                <Row style={{'marginTop':'5px'}}>
                    <Button
                        style={{ 'marginRight': '20px', 'marginBottom': '20px'  }}
                        type="primary"
                        size="default"
                        onClick={this.handleOnSave.bind(this, 1, 2)}
                    >
                        暂存
                    </Button>
                    <Button
                        style={{ 'marginRight': '20px', 'marginBottom': '20px'  }}
                        type="primary"
                        size="default"
                        onClick={this.handleOnSave.bind(this, 1, 0)}
                    >
                        保存并返回
                    </Button>
                    <Button style={{ 'marginRight': '20px', 'marginBottom': '20px'  }} type="primary" size="default" onClick={this.handleOnSave.bind(this, 1, 1)}>
                        继续录入
                    </Button>
                    <Button style={{ 'marginRight': '20px', 'marginBottom': '20px'  }} type="primary" size="default" onClick={this.handleOnSave.bind(this, 2, 0)}>
                        提交审核
                    </Button>
                    {curArt.id &&
                        <Button type="primary" style={{ 'marginRight': '20px', 'marginBottom': '20px'  }} size="default" onClick={this.onPreview.bind(this, curArt.id)}>
                            预览
                        </Button>
                    }
                    {curArt.id && <Popconfirm title="确定删除吗" onConfirm={() => this.onDelete(curArt.id)}>
                        <Button style={{ 'marginRight': '20px', 'marginBottom': '20px'  }} size="default">
                            删除
                        </Button>
                    </Popconfirm>}
                    <Button style={{ 'marginRight': '20px', 'marginBottom': '20px'  }} size="default" onClick={this.goBack}>
                        返回
                    </Button>
                </Row>
            )
        }else if(query.delFlag==2){  //待审核
            return (
                <div>
                    <Button style={{ 'marginRight': '20px', 'marginBottom': '20px' }} type="primary" size="default" onClick={this.handleOnSave.bind(this, query.delFlag, 2)}>
                        暂存
                    </Button>
                    <Button style={{ 'marginRight': '20px', 'marginBottom': '20px' }} type="primary" size="default" onClick={this.setModalVisible.bind(this, true)}>
                        驳回
                    </Button>
                    <Button style={{ 'marginRight': '20px', 'marginBottom': '20px' }} type="primary" size="default" onClick={this.handleOnSave.bind(this, 0, 0)}>
                        上线
                    </Button>
                    {curArt.id &&
                        <Button type="primary" style={{ 'marginRight': '20px', 'marginBottom': '20px' }} size="default" onClick={this.onPreview.bind(this, curArt.id)}>
                            预览
                        </Button>
                    }
                    {curArt.id && <Popconfirm title="确定删除吗" onConfirm={() => this.onDelete(curArt.id)}>
                        <Button style={{ 'marginRight': '20px', 'marginBottom': '20px' }} size="default">
                            删除
                        </Button>
                    </Popconfirm>}
                    <Button style={{ 'marginRight': '20px', 'marginBottom': '20px' }} size="default" onClick={this.goBack}>
                        返回
                    </Button>
                    <Modal
                        title="驳回原因"
                        visible={this.state.rejectModal}
                        onOk={this.handleOnSave.bind(this, 4, 0)}
                        onCancel={this.setModalVisible.bind(this, false)}
                        zIndex='9999'
                    >
                        <FormItem>
                            {getFieldDecorator('rejectReason', {
                                initialValue: ''
                            })(
                                <TextArea rows={4} />
                            )}
                        </FormItem>
                    </Modal>
                </div>
            )
        } else if (query.delFlag == 4){ //待修改
            return (
                <div>
                    <Button style={{ 'marginRight': '20px', 'marginBottom': '20px' }} type="primary" size="default" onClick={this.handleOnSave.bind(this, 4, 2)}>
                        暂存
                    </Button>
                    <Button style={{ 'marginRight': '20px', 'marginBottom': '20px' }} type="primary" size="default" onClick={this.handleOnSave.bind(this, 4, 0)}>
                        保存并返回
                    </Button>
                    <Button style={{ 'marginRight': '20px', 'marginBottom': '20px' }} type="primary" size="default" onClick={this.handleOnSave.bind(this, 2, 0)}>
                        提交审核
                    </Button>
                    {curArt.id &&
                        <Button type="primary" style={{ 'marginRight': '20px', 'marginBottom': '20px' }} size="default" onClick={this.onPreview.bind(this, curArt.id)}>
                            预览
                        </Button>
                    }
                    {curArt.id && <Popconfirm title="确定删除吗" onConfirm={() => this.onDelete(curArt.id)}>
                        <Button style={{ 'marginRight': '20px', 'marginBottom': '20px' }} size="default">
                            删除
                        </Button>
                    </Popconfirm>}
                    <Button style={{ 'marginRight': '20px', 'marginBottom': '20px' }} size="default" onClick={this.goBack}>
                        返回
                    </Button>
                </div>
            )
        }else{   //上线下线
            return (
                <div>
                    <Button style={{ 'marginRight': '20px', 'marginBottom': '20px' }} type="primary" size="default" onClick={this.handleOnSave.bind(this, query.delFlag,2)}>
                       暂存
                    </Button>
                    <Button style={{ 'marginRight': '20px', 'marginBottom': '20px' }} type="primary" size="default" onClick={this.handleOnSave.bind(this, query.delFlag,0)}>
                        保存并返回
                    </Button>
                    {curArt.id &&
                        <Button type="primary" style={{ 'marginRight': '20px', 'marginBottom': '20px' }} size="default" onClick={this.onPreview.bind(this, curArt.id)}>
                            预览
                        </Button>
                    }
                    {curArt.id && query.delFlag !=0 && <Popconfirm title="确定删除吗" onConfirm={() => this.onDelete(curArt.id)}>
                        <Button style={{ 'marginRight': '20px', 'marginBottom': '20px' }} size="default">
                            删除
                        </Button>
                    </Popconfirm>}
                    <Button style={{ 'marginRight': '20px', 'marginBottom': '20px' }} size="default" onClick={this.goBack}>
                        返回
                    </Button>
                </div>
            )
        }
    }
    getRejectReason = () => {
        const { cms: { query, curArt }, form: { getFieldDecorator }} = this.props;
        if( query.delFlag == 4 ) {
            return (
                <Col span={12}>
                    <FormItem label="驳回理由" {...{labelCol: {span: 4}, wrapperCol: {span: 20}}}>
                        {getFieldDecorator('rejectReason', {
                            initialValue:curArt.rejectReason || ''
                        })(
                            <TextArea autosize={{ minRows: 4, maxRows: 6 }} disabled={true}/>
                        )}
                    </FormItem>
                </Col>
            )
        }else{
            return '';
        }
    }
    componentWillUnmount(){
        const {cms:{updateStore, channelList}} = this.props;
        updateStore({ curArt:{}, channelList: []})
        // window.onkeydown = null;
    }

    componentWillReact(){
        const { cms: { curArt, query, channelList }} = this.props;
        let tChannelListValue = [];
        channelList.categoryId && tChannelListValue.push(channelList.categoryId + '');
        const thisCategoryId = curArt.categoryId !== undefined ? curArt.categoryId + '' : (query.categoryId+''||'1')
        if(thisCategoryId !== this.state.tCategoryId){
            this.setState({tCategoryId : thisCategoryId})
        }
    }

    onTpropsChange = (value) => {
        const { cms: { updateStore } } = this.props;
        updateStore({ channelList : value})
    }
    onCategoryIdChange = (value) => {
        const { form: {getFieldsValue }, cms: { channelList, updateStore }} = this.props;
        const { tCategoryId } = this.state;
        let tPropsValue = channelList;
        if( tCategoryId !== value ) {
            const index = tPropsValue.indexOf(tCategoryId);
            index !== -1 && tPropsValue.splice(index, 1);
            if(tPropsValue.indexOf(value) === -1 && value !== undefined) {
                tPropsValue.push(value);
            }
            updateStore({ channelList : tPropsValue})
            this.setState({ tCategoryId: value});
        }
    }
    render(){
        const { cms: { curArt, catTree, query, channelList }, form: { getFieldDecorator, getFieldsValue }, app} = this.props;
        const tProps = {
            // showSearch: true,
            treeData: toJS(catTree),
            value:  toJS(channelList),
            onChange: this.onTpropsChange,
            multiple: true,
            // onClick: this.onTpropsClick,
            // treeCheckable: true,
            // treeCheckStrictly: false,
            // labelInValue: true,
            // showCheckedStrategy: TreeSelect.SHOW_ALL,
            searchPlaceholder: '请选择',
        }
        return (
                <Form className="article-edit">
                    <Row>
                        <Col span={12}>
                            <FormItem label="肩标题" {...{ labelCol: { span: 4 }, wrapperCol: { span: 20 } }}>
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
                        <Col span={12}>
                            <FormItem label="外部链接" {...{ labelCol: { span: 4 }, wrapperCol: { span: 20 } }}>
                                {getFieldDecorator('link', {
                                    initialValue: curArt.link
                                })(
                                    <Input placeholder="请输入外部链接" />
                                )}
                            </FormItem>
                        </Col>
                    </Row>
                    <Row>
                        <Col span={12}>
                            <FormItem label="副标题" {...{ labelCol: { span: 4 }, wrapperCol: { span: 20 } }}>
                                {getFieldDecorator('subTitle', {
                                    initialValue: curArt.subTitle
                                })(
                                    <Input placeholder="请输入副标题" />
                                )}
                            </FormItem>
                        </Col>
                        <Col span={12}>
                            <FormItem label="发布到频道" {...{ labelCol: { span: 4 }, wrapperCol: { span: 20 } }}>
                                <TreeSelect showSearch {...tProps} />
                            </FormItem>
                        </Col>
                    </Row>
                    <Row>
                        <Col span={6}>
                            <FormItem label="作者" {...{ labelCol: { span: 8 }, wrapperCol: { span: 16 }}}>
                                {getFieldDecorator('authors', {
                                    initialValue: curArt.authors
                                })(
                                    <Input placeholder="请输入作者" />
                                )}
                            </FormItem>
                        </Col>
                        <Col span={6}>
                            <FormItem label="责任编辑" {...layout}>
                                {getFieldDecorator('responsibleUser', {
                                    initialValue: curArt.responsibleUser || (app.user.username || '')
                                })(
                                    <Input placeholder="请输入责任编辑" />
                                )}
                            </FormItem>
                        </Col>
                        <Col span={6}>
                            <FormItem label="来源" {...{ labelCol: { span: 8 }, wrapperCol: { span: 16 }}}>
                                {getFieldDecorator('source', {
                                    initialValue: curArt.source
                                })(
                                    <Input placeholder="请输入来源" />
                                )}
                            </FormItem>
                        </Col>
                        <Col span={6}>
                            <FormItem label="发布时间" {...{ labelCol: { span: 8 }, wrapperCol: { span: 16 }}}>
                                {
                                    getFieldDecorator('publishDate', {
                                        initialValue: curArt.publishDate !== undefined ? moment(curArt.publishDate, dateFormat) : moment((new Date().format('yyyy-MM-dd hh:mm:ss')))
                                })(<DatePicker style={{ 'minWidth': '200px' }} popupStyle={{'zIndex':'9999'}} showTime format={dateFormat} />)
                                }
                            </FormItem>
                        </Col>
                    </Row>
                    <Row>
                        <Col span={6}>
                            <FormItem label="稿件类型" { ...{ labelCol: { span: 8 }, wrapperCol: { span: 16 }}}>
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

                        <Col span={12} style={{"display": "none"}}>
                            <FormItem label="所属频道" {...layout}>
                                {getFieldDecorator('categoryId', {
                                    initialValue: curArt.categoryId !== undefined ? curArt.categoryId + '' : (query.categoryId+''||'0')
                                })(
                                    <TreeSelect
                                        showSearch
                                        treeNodeFilterProp="label"
                                        treeData={toJS(catTree)}
                                        onChange={this.onCategoryIdChange}
                                    />
                                )}
                            </FormItem>
                        </Col>
                        <Col span={6}>
                            <FormItem label="文章同步到论坛" {...{labelCol: { span: 12 }, wrapperCol: { span: 12 }}}>
                                {getFieldDecorator('allowPost', {
                                    initialValue: curArt.allowPost !== undefined ? curArt.allowPost + '' : '0'
                                })(
                                    <Select>
                                        <SelectOption key="1" value="1">是</SelectOption>
                                        <SelectOption key="2" value="0">否</SelectOption>
                                    </Select>
                                )}
                            </FormItem>
                        </Col>
                        {
                            query.delFlag === "0"
                            ? <Col span={12}>
                                <FormItem label="文章链接" {...{ labelCol: { span: 4 }, wrapperCol: { span: 20 } }}>
                                    <a href={curArt.url}>{curArt.url}</a>
                                </FormItem>
                            </Col>
                            : ''
                        }
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
                        {this.getRejectReason()}
                    </Row>
                    <Row type="flex" justify="center">
                        <Col>
                            {this.getStatusBtn()}
                        </Col>
                    </Row>
                    <div>
                        {!!query.delFlag ?  // 解决直接进编辑页无query时 退回列表页ueditor渲染报错问题
                            <UEditor
                                ref="ueditor"
                                initialContent={_.get(curArt, 'articleData.content')}
                            /> : null
                        }
                    </div>
                    {this.getThumbnailTpl()}
                    {this.getAlbumTpl()}
                    {this.getAudioTpl()}
                    {this.getVideoTpl()}
                </Form>
        )
    }
}

export default withRouter(Form.create()(CmsEdit));
