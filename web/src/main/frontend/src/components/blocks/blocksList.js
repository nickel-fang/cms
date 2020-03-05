import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import moment from 'moment';
import { observer, inject } from 'mobx-react';
import { toJS } from 'mobx';
import { Tree, Button, Card, Modal, Form, Row, Col, Input, Table, Upload, Icon, Popconfirm, Select, Radio, message, Menu, Dropdown, DatePicker, Switch, InputNumber, Tooltip } from 'antd';
import { UPLOAD_FILE, urlPath } from '../../constants';
import Sortable from 'sortablejs'
import _ from 'lodash';
import { Jt } from '../../utils'
import UEditor from '../cms/ueditor';
import '../../styles/blocks.less'
import { saveArt } from '../../services/cms';

const RangePicker = DatePicker.RangePicker;
const RadioButton = Radio.Button;
const RadioGroup = Radio.Group;
const TreeNode = Tree.TreeNode;
const FormItem = Form.Item;
const TextArea = Input.TextArea;
const Option = Select.Option;
const MenuItem = Menu.Item;
const dateFormat = 'YYYY-MM-DD HH:mm:ss';
@inject('app')
@inject('blocks')
@observer
class BlockList extends Component {
    constructor(props) {
        super(props);
        const { blocks, app: { selectSiteId, sites } } = props;
        this.state = {
            citeArtQuery: {  //插入文章query
                categoryId: '1',
                delFlag: 0,
                title: '',
            },
            mWidth: '40%',
            curCatgItem: {},
            curItem: {},  //当前编辑的区块项
            insertType: 'text',
            selectedKeys: ['1'],
            uploadedImg: '',
            showBlkCon: false,  //展示区块内容列表
            visible: false,
            evisible: false,
            htmlCon: '',
            selArts: [], //引用文章选中项
            checkedKeys: [],
            onCancel: null,
            onOk: null,   //modal点击确定保存
            onEditOk: null,
            onEditCancel: null,
            switchChecked: false, //频道编辑框中 自动新闻switch是否打开
            selectedRowKeys: [],  //批量操作选中项的key
            selectedRowItems: [] //批量操作选中项的对象
        }
        this.sortable = null;
        this.selectSiteId = selectSiteId;
        this.selectSiteName = _.find(sites, { id: selectSiteId }) ? _.find(sites, { id: selectSiteId }).name : '';
        blocks.getInit({ siteId: selectSiteId });
        blocks.updateStore({
            selectSiteName: this.selectSiteName
        });
    }
    componentWillReact() {
        const { app: { selectSiteId, sites }, blocks } = this.props;
        if (this.selectSiteId != selectSiteId) {  //如果选中站点selecetSiteId变了才重新请求频道树
            blocks.getInit({ siteId: selectSiteId });
            this.selectSiteName = _.find(sites, { id: selectSiteId }) ? _.find(sites, { id: selectSiteId }).name : '';
            blocks.updateStore({
                expandedKeys: ['1'],
                expandedCiteArtKeys: ['1'],
                expandedCatgKeys: ['1'],
                selectSiteName: this.selectSiteName
            })
        }
        this.selectSiteId = selectSiteId;  //记录当前siteid以便比对
    }
    componentDidUpdate() {
        if (!!this.state.showBlkCon) {
            const container = ReactDOM.findDOMNode(this.refs.table).querySelector('.ant-table-tbody');
            this.sortable = new Sortable(container, {
                draggable: '.ant-table-row',
                onEnd: ({ oldIndex, newIndex }) => {
                    if (oldIndex === newIndex) {
                        return;
                    }
                    let minIndex = oldIndex, maxIndex = newIndex;
                    if (oldIndex > newIndex) {
                        minIndex = newIndex;
                        maxIndex = oldIndex;
                    }
                    const { curBlk, updateStore, updateSorts, query } = this.props.blocks;
                    const CcurBlk = toJS(curBlk)
                    const relatedRecord = CcurBlk[newIndex];
                    const draggedRecord = CcurBlk.splice(oldIndex, 1)[0];
                    draggedRecord.stick = relatedRecord.stick;
                    CcurBlk.splice(newIndex, 0, draggedRecord);
                    const chgRecords = CcurBlk.slice(minIndex, maxIndex + 1);
                    const ids = [], weights = [];
                    for (let i = 0, len = chgRecords.length; i < len; i++) {
                        ids.push(chgRecords[i].id);
                        weights.push(chgRecords[i].weight);
                    }
                    weights.sort((a, b) => {
                        return a - b;
                    });
                    for (let i = 0, len = chgRecords.length; i < len; i++) {
                        chgRecords[i].weight = weights[i];
                    }
                    const updateArr = [];
                    for (let i = 0, len = ids.length; i < len; i++) {
                        updateArr.push({ id: ids[i], weight: weights[i] });
                    }
                    CcurBlk.splice(minIndex, chgRecords.length, ...chgRecords);
                    updateStore({ curBlk: CcurBlk });
                    if (query.templateType == "category") {
                        updateSorts({ list: updateArr, categoryId: query.categoryId, templateId: query.pageTemplateId });
                    } else if (query.templateType == "detail") {
                        updateSorts({ list: updateArr, categoryId: query.categoryId, templateId: query.templateId });
                    }
                }
            });
        } else {
            this.sortable = null
        }
    }
    renderTreeNode = (data) => {
        return data.map((item, index) => {
            return (
                <TreeNode
                    title={item.name}
                    key={item.key}
                >
                    {item.children && this.renderTreeNode(item.children)}
                </TreeNode>
            )
        })
    }
    onSelect = (id) => {
        const { blocks: { catTree, updateStore, query, template } } = this.props;
        if (!id.length || !id[0]) {  //如果点击同一个频道
            return
        }
        this.setState({
            selectedKeys: id,
            showBlkCon: false
        })
        const item = Jt.tree.getNode(toJS(catTree), id[0]);
        this.setState({
            curCatgItem: item
        })
        if (query.templateType == "category") {
            if (!item.pageTemplateId) {
                updateStore({ query: { ...query, categoryId: item.id }, blocks: [], showTempCon: false })
            } else {
                template(item.pageTemplateId)
                updateStore({ query: { ...query, categoryId: item.id, pageTemplateId: item.pageTemplateId, templateType: "category" }, showTempCon: true })
            }
        } else if (query.templateType == "detail") {
            if (!item.templateId) {
                updateStore({ query: { ...query, categoryId: item.id }, blocks: [], showTempCon: false })
            } else {
                template(item.templateId)
                updateStore({ query: { ...query, categoryId: item.id, templateId: item.templateId, templateType: "detail" }, showTempCon: true })
            }
        } else if (!query.templateType && !!item.pageTemplateId) {
            template(item.pageTemplateId)
            updateStore({ query: { ...query, categoryId: item.id, pageTemplateId: item.pageTemplateId, templateType: "category" }, showTempCon: true })
        } else if (!query.templateType && !!item.templateId) {
            template(item.templateId)
            updateStore({ query: { ...query, categoryId: item.id, pageTemplateId: item.templateId, templateType: "detail" }, showTempCon: true })
        } else {
            updateStore({ query: { ...query, categoryId: item.id }, blocks: [], showTempCon: false })
        }
    }
    clickBlk = (blkId, catgId) => {
        const { blocks: { blocks, updateStore, getBlock, query } } = this.props;
        const CBlocks = toJS(blocks);
        CBlocks.map((item, index) => {
            if (item.id == blkId) {
                item.selected = true
            } else {
                item.selected = false
            }
        })
        getBlock({ blockId: blkId, categoryId: catgId })
        updateStore({ blocks: CBlocks, query: { ...toJS(query), categoryId: catgId, blockId: blkId } });
        this.setState({
            showBlkCon: true
        })
    }
    renderBlocks = () => {
        const { blocks: { blocks, query } } = this.props;
        if (!!blocks.length) {
            return blocks.map((item, index) => {
                return (
                    <Tooltip key={item.id} title={item.description}>
                        <Button
                            style={{ 'marginRight': '10px', 'marginBottom': '10px' }}
                            type={item.selected ? 'primary' : 'default'}
                            onClick={this.clickBlk.bind(this, item.id, query.categoryId)}
                        >
                            {item.name}
                        </Button>
                    </Tooltip>
                )
            })
        } else {
            return <div style={{ 'textAlign': 'center' }}>无区块</div>
        }
    }
    insert = (type) => {
        this.setState({
            visible: true
        })
    }
    onPageChange = (page) => {
        const { blocks: { query, getBlock } } = this.props;
        getBlock({ blockId: toJS(query.blockId), categoryId: toJS(query).categoryId, pageNumber: page.current, pageSize: page.pageSize });
    }
    onPreview = () => {
        const { blocks: { query, preview } } = this.props;
        var newTab = window.open('', '_blank');
        if (query.templateType == "category") {
            const callback = (res) => {
                const { code } = res;
                if (code == 0) {
                    const location = res.data;
                    if(location){
                        newTab.location.href = location;
                    }else{
                        newTab.close();
                        message.error('该频道未绑定模板')
                    }
                }
            }
            preview({ templateId: toJS(query.pageTemplateId), categoryId: toJS(query).categoryId }, callback)
        } else if (query.templateType == "detail") {
            preview({ templateId: toJS(query.templateId), categoryId: toJS(query).categoryId })
        }
    }
    onBatchMenuClick = ({ key }) => {
        const { blocks: { query, getBlock, batchOnOff, batchDelete } } = this.props;
        const { selectedRowItems, selectedRowKeys } = this.state;
        let templateId = null;
        if (query.templateType == "category") {
            templateId = query.pageTemplateId;
        } else if (query.templateType == "detail") {
            templateId = query.templateId;
        }
        const callback = () => {
            getBlock({ blockId: toJS(query.blockId), categoryId: toJS(query).categoryId });
            this.setState({
                selectedRowItems: [],
                selectedRowKeys: []
            })
        }
        if (key == "on") {
            batchOnOff({ ids: selectedRowKeys, delFlag: 0, categoryId: query.categoryId, templateId }, callback)
        } else if (key == "off") {
            batchOnOff({ ids: selectedRowKeys, delFlag: 1, categoryId: query.categoryId, templateId }, callback)
        } else if (key == "delete") {
            batchDelete({ ids: selectedRowKeys, categoryId: query.categoryId, templateId }, callback)
        }
    }
    batchMenus = () => {
        return (
            <Menu onClick={this.onBatchMenuClick}>
                <MenuItem key="on">
                    批量上线
                </MenuItem>
                <MenuItem key="off">
                    批量下线
                </MenuItem>
                <MenuItem key="delete">
                    批量删除
                </MenuItem>
            </Menu>
        )
    }
    onRefreshAll = () => {
        const { blocks: { refreshAll, query } } = this.props;
        const callback = (res) => {
            const { code } = res;
            if (code == 0) {
                message.success('更新成功')
            }
        }
        refreshAll({ categoryId: toJS(query).categoryId }, callback)
    }
    renderBlkCon = () => {
        const { blocks: { blocks, query, curBlk, loading, pagination } } = this.props;
        const { showBlkCon, selectedRowKeys } = this.state;
        const rowSelection = {
            selectedRowKeys,
            onChange: (ids, items) => {
                this.setState({
                    selectedRowKeys: ids,
                    selectedRowItems: items
                })
            }
        };
        return (
            <div>
                <Row style={{ 'margin': "10px" }}>
                    <Col span={12}>
                        {query.templateType !== "detail" && <Button type="primary" onClick={this.onPreview}>预览</Button>}
                        {query.templateType == "detail" && <Button type="primary" onClick={this.onRefreshAll}>一键更新</Button>}
                    </Col>
                    {showBlkCon ?
                        <Col span={12} style={{ "textAlign": 'right' }}>
                            <Dropdown.Button overlay={this.batchMenus()} style={{ 'marginRight': '20px' }}>
                                批量操作
                        </Dropdown.Button>
                            <Button
                                type="primary"
                                onClick={this.insert}
                            >
                                插入内容
                        </Button>
                        </Col> : null}
                </Row>
                {showBlkCon ?
                    <Table
                        ref="table"
                        className="draggable-table"
                        simple
                        bordered
                        columns={this.getBlkConColumns()}
                        rowKey={record => record.id}
                        loading={loading}
                        dataSource={toJS(curBlk)}
                        pagination={pagination}
                        onChange={this.onPageChange}
                        rowSelection={rowSelection}
                    /> : null}
            </div>
        )
    }
    hideModal = () => {
        this.setState({
            visible: false,
            selArts: [],
            uploadedImg: ''
        })
    }
    categoryOnOk = () => {
        const {blocks: {query, saveCatg, getBlock, catTree}} = this.props;
        const {checkedKeys} = this.state;
        const data = { type: 'category', categoryId: toJS(query).categoryId, blockId: toJS(query).blockId, ids: checkedKeys};
        const callback = () => {
            getBlock({ blockId: toJS(query.blockId), categoryId: toJS(query).categoryId })
        }
        saveCatg(data, callback)
        this.setState({
            visible: false,
            checkedKeys: []
        })
    }
    artOnOk = () => {
        const { blocks: { query, saveCiteArt, getBlock } } = this.props;
        const { selArts } = this.state;
        const data = { type: 'article', categoryId: toJS(query).categoryId, blockId: toJS(query.blockId), items: selArts }
        const callback = () => {
            getBlock({ blockId: toJS(query.blockId), categoryId: toJS(query).categoryId })
        }
        saveCiteArt(data, callback)
        this.setState({
            visible: false,
            selArts: []
        })
    }
    imgOnOk = () => {
        const { blocks: { query, saveImg, getBlock } } = this.props;
        const { uploadedImg } = this.state;
        this.setState({
            visible: false
        })
        if (!uploadedImg) {
            return
        }
        const callback = () => {
            getBlock({ blockId: toJS(query.blockId), categoryId: toJS(query).categoryId })
        }
        const data = { type: 'image', categoryId: toJS(query).categoryId, blockId: toJS(query.blockId), image: { title: uploadedImg } }
        this.setState({ uploadedImg: '' })
        saveImg(data, callback)
    }
    textOnOk = () => {
        const { form: { validateFields, getFieldsValue, resetFields }, blocks: { query, saveTxt, getBlock } } = this.props;
        validateFields((errors, val) => {
            if (errors) {
                return;
            }
            const values = {
                ...getFieldsValue()
            };
            const data = { type: 'text', input: { ...values }, blockId: toJS(query.blockId), categoryId: toJS(query).categoryId }
            const callback = () => {
                getBlock({ blockId: toJS(query.blockId), categoryId: toJS(query).categoryId })
            }
            saveTxt(data, callback)
            this.setState({
                visible: false,
            })
            resetFields()
        })
    }
    modalOpts = () => {
        const { visible, mWidth, onOk, onCancel } = this.state;
        return {
            visible,
            width: mWidth,
            onCancel: onCancel || this.hideModal,
            onOk: onOk || this.textOnOk
        }
    }
    ueditorOnOk = (ref) => {
        const { form: { setFieldsValue } } = this.props
        const { ueditorDataKey } = this.state;
        const ueditorCon = this.refs.ueditor.getContent();
        const itemEl = ReactDOM.findDOMNode(this.refs[ref])
        const flipEl = itemEl.querySelector('.flip-wrap');
        $(flipEl).find('.front').css({ 'display': 'block' });
        $(flipEl).toggleClass('flipped');
        const obj = {};
        obj[ueditorDataKey] = ueditorCon
        setFieldsValue(obj);
        if (ref == "model") {
            this.setState({
                onOk: this.textOnOk
            })
        } else if (ref == "editModel") {
            this.setState({
                onEditOk: this.editModalOnOk
            })
        }
    }
    hideUEditor = (ref) => {
        const itemEl = ReactDOM.findDOMNode(this.refs[ref])
        const flipEl = itemEl.querySelector('.flip-wrap');
        $(flipEl).find('.front').css({ 'display': 'block' });
        $(flipEl).toggleClass('flipped');
        if (ref == "model") {
            this.setState({
                onOk: this.textOnOk,
                onCancel: this.hideModal,
            })
        } else if (ref == "editModel") {
            this.setState({
                onEditOk: this.editModalOnOk,
                onEditCancel: this.hideEditModal
            })
        }
    }
    showUEditor = (key, ref) => {
        const itemEl = ReactDOM.findDOMNode(this.refs[ref]);
        const flipEl = itemEl.querySelector('.flip-wrap');
        $(flipEl).toggleClass('flipped');
        setTimeout(() => {
            $(flipEl).find('.front').css({ 'display': 'none' });
        }, 800);
        const { form: { validateFields, getFieldsValue } } = this.props;
        const values = {
            ...getFieldsValue()
        };
        this.state.ueditorDataKey = key;  //对应的key description还是title
        this.state.htmlCon = values[key];
        if (ref == "model") {
            this.setState({
                onOk: this.ueditorOnOk.bind(this, 'model'),
                onCancel: this.hideUEditor.bind(this, 'model'),
            })
        } else if (ref == "editModel") {
            this.setState({
                onEditOk: this.ueditorOnOk.bind(this, 'editModel'),
                onEditCancel: this.hideUEditor.bind(this, 'editModel'),
            })
        }
        if (!!this.initedUeditor) {
            this.refs.ueditor.setContent(values[key]);
        }
        this.initedUeditor = true;
    }
    getArtsColumns = () => {
        return [
            {
                title: '标题',
                key: 'title',
                dataIndex: 'title',
                width: 200,
            },
            {
                title: '类型',
                key: 'type',
                dataIndex: 'type',
                width: 100,
                render: (text, record) => {
                    switch (text) {
                        case 'common':
                            return '新闻'
                        case 'video':
                            return '视频'
                        case 'audio':
                            return '音频'
                        case 'image':
                            return '图集'
                        default:
                            return ''
                    }
                }
            },
            {
                title: '录入时间',
                key: 'createAt',
                dataIndex: 'createAt',
                width: 200,
            }
        ]
    }
    getBlkConColumns = () => {
        return [
            {
                title: '名称',
                key: 'title',
                dataIndex: 'title',
                width: 200,
                render: (text, record) => {
                    switch (record.type) {
                        case 'text':
                            return (
                                <span>{record.input ? record.input.title : ''}</span>
                            )
                        case 'article':
                            return (
                                <span>{record.item ? record.item.title : ''}</span>
                            )
                        case 'category':
                            return (
                                <span>{record.menu ? record.menu.title : ''}</span>
                            )
                        case 'image':
                            return (
                                <span>{record.image ? <img src = {record.image.title} width="200" height="80"/> : ''}</span>
                            )
                        default:
                            return ''
                    }
                }
            },
            {
                title: '状态',
                key: 'delFlag',
                dataIndex: 'delFlag',
                width: 100,
                render: (text, record) => {
                    switch (text) {
                        case 0:
                            return '上线'
                        case 1:
                            return '下线'
                        default:
                            return ''
                    }
                }
            },
            {
                title: '类型',
                key: 'type',
                dataIndex: 'type',
                width: 100,
                render: (text, record) => {
                    switch (text) {
                        case 'text':
                            return '文字'
                        case 'article':
                            return '文章'
                        case 'category':
                            return '频道'
                        case 'image':
                            return '图片'
                        default:
                            return ''
                    }
                }
            },
            {
                title: '创建时间',
                key: 'updateAt',
                dataIndex: 'updateAt',
                width: 200,
            },
            {
                title: '操作',
                width: 100,
                className: 'action',
                render: (text, record) => {
                    const id = record.id;
                    let renderArr = [
                        <a
                            key="action-edit"
                            style={{ 'marginRight': '5px' }}
                            onClick={this.onEdit.bind(this, id)}
                        >
                            编辑
                        </a>,
                        <a
                            key="action-onoff"
                            style={{ 'marginRight': '5px' }}
                            onClick={this.onOnOFF.bind(this, record)}
                        >
                            {record.delFlag == 0 ? '下线' : '上线'}
                        </a>,
                        <Popconfirm
                            key="action-delete"
                            title="确定删除吗"
                            onConfirm={this.onDelete.bind(this, id)}
                        >
                            <a style={{ 'marginRight': '5px' }}>删除</a>
                        </Popconfirm>
                    ]
                    return renderArr
                }
            }
        ]
    }
    onOnOFF = (record) => {
        const { blocks: { query, getBlock, changeOnOff } } = this.props;
        let templateId = null;
        if (query.templateType == "category") {
            templateId = query.pageTemplateId;
        } else if (query.templateType == "detail") {
            templateId = query.templateId;
        }
        const callback = () => {
            getBlock({ blockId: toJS(query).blockId, categoryId: toJS(query).categoryId })
        }
        changeOnOff({ id: record.id, categoryId: query.categoryId, templateId }, callback)
    }
    onEdit = (id) => {
        const { blocks: { curBlk } } = this.props;
        const item = _.find(toJS(curBlk), { id })
        const curItem = {};
        curItem.id = id;
        if (item.type == 'text') {
            curItem.type = "text";
            curItem.title = item.input.title;
            curItem.info = item.input.info;
        } else if (item.type == 'article') {
            curItem.type = "article";
            curItem.title = item.item.title;
            curItem.url = item.item.url;
            curItem.info = item.item.info;
        } else if (item.type == 'category') {
            curItem.type = "category";
            curItem.title = item.menu.title;
            curItem.info = item.menu.info;
            curItem.isAutoImport = item.menu.isAutoImport;
            curItem.count = item.menu.count;
            curItem.oriName = item.menu.oriName;
        } else if (item.type == 'image') {
            curItem.type = "image";
            curItem.title = item.image.title;
            curItem.info = item.image.info;
        }
        this.setState({
            curItem,
            evisible: true
        })
    }
    onDelete = (id) => {
        const { blocks: { deleteBlock, getBlock, query } } = this.props;
        const success = () => {
            getBlock({ blockId: toJS(query.blockId), categoryId: toJS(query).categoryId })
        }
        if (query.templateType == "category") {
            deleteBlock({ id, categoryId: toJS(query).categoryId, templateId: query.pageTemplateId }, success);
        } else if (query.templateType == "detail") {
            deleteBlock({ id, categoryId: toJS(query).categoryId, templateId: query.templateId }, success);
        }
    }
    onUploadedImageChange = (info) => {
        let uploadedImg = this.state.uploadedImg;
        if (info.file.status == 'done') {
            if (info.file.response && info.file.response.code == 0 && info.file.response.data) {
                uploadedImg = info.file.response.data.fileUrl;
            }
        }
        this.setState({ uploadedImg });
    }
    onCheckCatg = (checkedKeys, e) => {
        const { blocks: { getBlock, query, catTree } } = this.props;
        const CcatTree = toJS(catTree);
        const keys = checkedKeys.checked || [];
        const checkedKey = _.last(keys);  //新点击的key
        let finalCheckedKeys = [...keys];
        // if (!!e.checked) {   //check时
        //     const ids = Jt.tree.isParentGetLeafIds(CcatTree, checkedKey);
        //     if (!ids) {
        //         finalCheckedKeys = _.union(finalCheckedKeys, [checkedKey]);
        //     } else {
        //         finalCheckedKeys = _.union(finalCheckedKeys, ids);
        //     }
        // } else {   //取消check时
        //     finalCheckedKeys = keys;
        // }
        this.setState({ checkedKeys: finalCheckedKeys });
    }
    onExpandChange = (expandedKeys,{node,expanded}) => {
        const {blocks:{updateStore, catTree}} = this.props;
        if (!expanded) {
            const key = node.props.eventKey;
            const children = Jt.tree.getAllChildren(toJS(catTree), key);   // 如果有子频道展开的情况下关闭父频道，直接也关闭所有子频道
            if (!!children.length) {
                children.forEach(function (value, index) {
                    if (_.indexOf(expandedKeys, value) !== -1) {
                        expandedKeys.splice(_.indexOf(expandedKeys, value), 1);
                    }
                })
            }
        }
        updateStore({
            expandedKeys
        })
    }
    onExpandCiteArtChange = (expandedKeys, { node, expanded }) => {
        const { blocks: { updateStore, catTree } } = this.props;
        if (!expanded) {
            const key = node.props.eventKey;
            const children = Jt.tree.getAllChildren(toJS(catTree), key);   // 如果有子频道展开的情况下关闭父频道，直接也关闭所有子频道
            if (!!children.length) {
                children.forEach(function (value, index) {
                    if (_.indexOf(expandedKeys, value) !== -1) {
                        expandedKeys.splice(_.indexOf(expandedKeys, value), 1);
                    }
                })
            }
        }
        updateStore({
            expandedCiteArtKeys: expandedKeys
        })
    }
    onCatExpandChange = (expandedKeys,{node,expanded}) => {
        const { blocks: { updateStore, catTree} } = this.props;
        if (!expanded) {
            const key = node.props.eventKey;
            const children = Jt.tree.getAllChildren(toJS(catTree), key);   // 如果有子频道展开的情况下关闭父频道，直接也关闭所有子频道
            if (!!children.length) {
                children.forEach(function (value, index) {
                    if (_.indexOf(expandedKeys, value) !== -1) {
                        expandedKeys.splice(_.indexOf(expandedKeys, value), 1);
                    }
                })
            }
        }
        updateStore({
            expandedCatgKeys: expandedKeys
        })
    }
    onCiteArtSelect = (id) => {
        const { blocks: { articleList } } = this.props;
        const { citeArtQuery } = this.state;
        citeArtQuery.categoryId = id[0];
        articleList({ ...citeArtQuery, pageNumber: 1 });
        this.setState({
            citeArtQuery
        })
    }
    handleInsertTypeChange = (value) => {
        const { blocks: { articleList } } = this.props;
        const { citeArtQuery } = this.state;
        if (value == "text") {
            this.state.onOk = this.textOnOk;
            this.state.mWidth = "40%";
        } else if (value == "articles") {
            articleList({ ...citeArtQuery })
            this.state.onOk = this.artOnOk;
            this.state.mWidth = "70%";
        } else if (value == "image") {
            this.state.onOk = this.imgOnOk;
            this.state.mWidth = "30%";
        } else if (value == "category") {
            this.state.onOk = this.categoryOnOk;
            this.state.mWidth = "20%";
        }
        this.setState({
            insertType: value
        })
    }
    onCitePageChange = (page) => {
        const { blocks: { articleList } } = this.props;
        const { citeArtQuery } = this.state;
        articleList({ ...citeArtQuery, pageNumber: page.current, pageSize: page.pageSize });
    }
    renderModalContentType = () => {
        const { form: { getFieldDecorator }, blocks: { query, catTree, artsList, articleList, updateStore, loading, expandedCatgKeys } } = this.props;
        const { insertType, uploadedImg, selArts, checkedKeys, citeArtQuery, htmlCon } = this.state;
        if (insertType == "text") {
            return (
                <div className="flip-wrap">
                    <Form className="front">
                        <FormItem label="名称" {...{ labelCol: { span: 4 }, wrapperCol: { span: 18 } }}>
                            <Row gutter={8}>
                                <Col span={20}>
                                    {getFieldDecorator('title', {
                                        initialValue: '',
                                        rules: [
                                            { required: true, message: '请输入名称' }
                                        ]
                                    })(
                                        <Input placeholder="请输入名称" />
                                    )}
                                </Col>
                                <Col span={4}>
                                    <Button type="primary" onClick={this.showUEditor.bind(this, 'title', 'model')}>可视化</Button>
                                </Col>
                            </Row>
                        </FormItem>
                        <FormItem style={{ 'marginBottom': '10px' }} label="说明" {...{ labelCol: { span: 4 }, wrapperCol: { span: 18 } }}>
                            <Row gutter={8}>
                                <Col span={20}>
                                    {getFieldDecorator('info', {
                                        initialValue: ''
                                    })(
                                        <TextArea placeholder="请输入说明" rows={7} />
                                    )}
                                </Col>
                                <Col span={4}>
                                    <Button type="primary" onClick={this.showUEditor.bind(this, 'info', 'model')}>可视化</Button>
                                </Col>
                            </Row>
                        </FormItem>
                    </Form>
                    <Row className="back">
                        <Col span={24}>
                            {<UEditor
                                ref="ueditor"
                                enterTag='br'
                                height="100"
                                initialContent={htmlCon}
                            />}
                        </Col>
                    </Row>
                </div>
            )
        } else if (insertType == "articles") {
            const { blocks: { catTree, citePagination, expandedCiteArtKeys } } = this.props;
            const { citeArtQuery } = this.state
            const selectedRowKeys = selArts.map((art) => art.id);
            const rowSelection = {
                selectedRowKeys,
                onChange: (ids, arts) => {
                    this.setState({
                        selArts: arts
                    })
                }
            };
            return (
                <div className="insert-article-wrap">
                    <Tree
                        className="tree"
                        defaultSelectedKeys={[citeArtQuery.categoryId]}
                        expandedKeys={toJS(expandedCiteArtKeys)}
                        onExpand={this.onExpandCiteArtChange}
                        onSelect={this.onCiteArtSelect}
                    >
                        {this.renderTreeNode(toJS(catTree))}
                    </Tree>
                    <Form className="con">
                        <Row>
                            <Col span={5}>
                                <FormItem label="标题" {...{ labelCol: { xs: { span: 24 }, sm: { span: 6 } }, wrapperCol: { xs: { span: 24 }, sm: { span: 18 } } }}>
                                    {getFieldDecorator('title', {
                                        initialValue: citeArtQuery.title || ''
                                    })(
                                        <Input />
                                    )}
                                </FormItem>
                            </Col>
                            <Col span={14}>
                                <FormItem label="时间" {...{ labelCol: { xs: { span: 24 }, sm: { span: 4 } }, wrapperCol: { xs: { span: 24 }, sm: { span: 20 } } }}>
                                    {getFieldDecorator('publishDate', {
                                        initialValue: citeArtQuery.beginTime ? [moment(citeArtQuery.beginTime), moment(citeArtQuery.endTimes)] : null
                                    })(
                                        <RangePicker showTime format={dateFormat} />
                                    )}
                                </FormItem>
                            </Col>
                            <Col span={5} style={{ 'marginTop': '4px' }}>
                                <Button type="primary" onClick={this.onArtSearch}>查询</Button>
                                <Button
                                    style={{ 'marginLeft': '10px', 'marginRight': '10px' }}
                                    onClick={this.onArtSearchReset}
                                >重置</Button>
                            </Col>
                        </Row>
                        <Table
                            simple
                            bordered
                            loading={loading}
                            columns={this.getArtsColumns()}
                            rowKey={record => record.id}
                            dataSource={toJS(artsList)}
                            rowSelection={rowSelection}
                            pagination={citePagination}
                            onChange={this.onCitePageChange}
                        />
                    </Form>
                </div>
            )
        } else if (insertType == "image") {
            return (
                <div>
                    <div style={{ 'float': 'left' }}>
                        点击插入图片：
                    </div>
                    <div>
                        <Upload
                            className="cover-uploader"
                            name="file"
                            showUploadList={false}
                            action={urlPath.UPLOAD_FILE}
                            onChange={(info) => this.onUploadedImageChange(info)}
                        >
                            {
                                uploadedImg ?
                                    <img src={uploadedImg} alt="" className="cover-img" /> :
                                    <Icon type="plus" className="cover-uploader-trigger" />
                            }
                        </Upload>
                    </div>
                </div>
            )
        } else if (insertType == "category") {
            return (
                <div style={{ 'overflow': 'scroll' }}>
                    <Tree
                        checkable
                        expandedKeys={toJS(expandedCatgKeys)}
                        onCheck={this.onCheckCatg}
                        checkedKeys={checkedKeys}
                        onExpand={this.onCatExpandChange}
                        checkStrictly={true}
                    >
                        {this.renderTreeNode(catTree)}
                    </Tree>
                </div>
            )
        }
    }
    onArtSearchReset = () => {
        const { form: { getFieldsValue, resetFields }, blocks: { articleList } } = this.props;
        let { citeArtQuery } = this.state;
        resetFields();
        citeArtQuery.title = "";
        delete citeArtQuery.publishDate;
        delete citeArtQuery.beginTime;
        delete citeArtQuery.endTime;
        this.setState({
            citeArtQuery
        })
        articleList(citeArtQuery)
    }
    onArtSearch = () => {
        const { form: { validateFields, getFieldsValue, resetFields }, blocks: { articleList } } = this.props;
        let { citeArtQuery } = this.state;
        let data = getFieldsValue();
        if (!Jt.array.isEmpty(data.publishDate)) {
            data.beginTime = data.publishDate[0].format(dateFormat);
            data.endTime = data.publishDate[1].format(dateFormat);
        } else {
            delete citeArtQuery.endTime;
            delete citeArtQuery.beginTime;
        }
        delete data.publishDate;
        data = { ...citeArtQuery, ...data };
        this.setState({
            citeArtQuery: data
        });
        for (let key in data) {  //去掉为空的搜索项
            if (!data[key]) {
                delete data[key]
            }
        }
        articleList(data);
    }
    renderModalContent = () => {
        return (
            <div className="blk-modal-wrap">
                <Row style={{ 'marginBottom': '10px' }}>
                    <Select style={{ 'width': '100px' }} defaultValue={this.state.insertType} onChange={this.handleInsertTypeChange}>
                        <Option key="category" value="category">频道</Option>
                        <Option key="article" value="articles">文章</Option>
                        <Option key="text" value="text">文字</Option>
                        <Option key="image" value="image">图片</Option>
                    </Select>
                </Row>
                {this.renderModalContentType()}
            </div>
        )
    }
    editModalOnOk = () => {
        const { form: { validateFields, getFieldsValue, resetFields }, blocks: { curBlk, editBlockItem, getBlock, query } } = this.props;
        const { curItem } = this.state;
        const type = curItem.type;
        const item = _.find(toJS(curBlk), { id: curItem.id })
        validateFields((errors, val) => {
            if (errors) {
                return;
            }
            let values = {};
            if (type == "text") {
                values = {
                    id: curItem.id,
                    input: {
                        ...item.input,
                        ...getFieldsValue()
                    }
                }
            } else if (type == "article") {
                values = {
                    id: curItem.id,
                    item: {
                        ...item.item,
                        ...getFieldsValue()
                    }
                }
            } else if (type == "image") {
                values = {
                    id: curItem.id,
                    image: {
                        ...item.image,
                        ...getFieldsValue()
                    }
                }
            } else if (type == "category") {
                values = {
                    id: curItem.id,
                    menu: {
                        ...item.menu,
                        ...getFieldsValue()
                    }
                }
            }
            const callback = () => {
                this.setState({
                    evisible: false
                })
                getBlock({ blockId: toJS(query.blockId), categoryId: toJS(query).categoryId })
            }
            editBlockItem(values, callback)
        })
    }
    hideEditModal = () => {
        this.setState({
            evisible: false
        })
    }
    editModalOpts = () => {
        const { evisible, onEditOk, onEditCancel } = this.state;
        return {
            visible: evisible,
            width: '50%',
            onOk: onEditOk || this.editModalOnOk,
            onCancel: onEditCancel || this.hideEditModal
        }
    }
    switchChg = (checked) => {
        this.setState({
            switchChecked: checked
        })
    }
    renderEditModal = () => {
        const { form: { getFieldDecorator } } = this.props;
        const { insertType, curItem, htmlCon, switchChecked } = this.state;
        let height = 100;
        if (curItem.type == "category") {
            height = 200;
            return (
                <div className="flip-wrap category">
                    <Form className="front" style={{ 'marginTop': '20px' }}>
                        <Row gutter={8}>
                            {curItem.oriName ? <Col offset={4} style={{ 'marginBottom': '20px', 'fontWeight': 'bold', 'paddingLeft': '16px' }}>频道：{curItem.oriName}</Col>:null}
                        </Row>
                        <FormItem label="名称" {...{ labelCol: { span: 6 }, wrapperCol: { span: 16 } }}>
                            <Row gutter={8}>
                                <Col span={14}>
                                    {getFieldDecorator('title', {
                                        initialValue: curItem.title ? curItem.title : '',
                                        rules: [
                                            { required: true, message: '请输入名称' }
                                        ]
                                    })(
                                        <Input placeholder="请输入名称" />
                                    )}
                                </Col>
                                <Col span={4}>
                                    <Button type="primary" onClick={this.showUEditor.bind(this, 'title', 'editModel')}>可视化</Button>
                                </Col>
                            </Row>
                        </FormItem>
                        <FormItem label="说明" {...{ labelCol: { span: 6 }, wrapperCol: { span: 16 } }}>
                            <Row gutter={8}>
                                <Col span={14}>
                                    {getFieldDecorator('info', {
                                        initialValue: curItem.info ? curItem.info : ''
                                    })(
                                        <TextArea placeholder="请输入说明" rows={5} />
                                    )}
                                </Col>
                                <Col span={4}>
                                    <Button type="primary" onClick={this.showUEditor.bind(this, 'info', 'editModel')}>可视化</Button>
                                </Col>
                            </Row>
                        </FormItem>
                        <FormItem
                            {...{ labelCol: { span: 8 }, wrapperCol: { span: 10 } }}
                            label="是否自动插入新闻"
                        >
                            {getFieldDecorator('isAutoImport', {
                                valuePropName: 'checked',
                                initialValue: curItem.isAutoImport || false
                            })(
                                <Switch onChange={this.switchChg} />
                            )}
                        </FormItem>
                        {!!switchChecked && <FormItem
                            {...{ labelCol: { span: 8 }, wrapperCol: { span: 10 } }}
                            label="自动插入新闻条数"
                        >
                            {getFieldDecorator('count', {
                                initialValue: curItem.count || 0
                            })(
                                <InputNumber />
                            )}
                        </FormItem>}
                    </Form>
                    <Row className="back">
                        <Col span={24}>
                            {<UEditor
                                ref="ueditor"
                                enterTag='br'
                                height={height}
                                initialContent={htmlCon}
                            />}
                        </Col>
                    </Row>
                </div>
            )
        } else {
            return (
                <div className="flip-wrap">
                    <Form className="front" style={{ 'marginTop': '20px' }}>
                        <Row>
                            {!!curItem.url && <p style={{ 'marginLeft': '71px' }}>URL：{curItem.url}</p>}
                        </Row>
                        <FormItem label="名称" {...{ labelCol: { span: 4 }, wrapperCol: { span: 18 } }}>
                            <Row gutter={8}>
                                <Col span={14}>
                                    {getFieldDecorator('title', {
                                        initialValue: curItem.title ? curItem.title : '',
                                        rules: [
                                            { required: true, message: '请输入名称' }
                                        ]
                                    })(
                                        <Input placeholder="请输入名称" />
                                    )}
                                </Col>
                                <Col span={4}>
                                    <Button type="primary" onClick={this.showUEditor.bind(this, 'title', 'editModel')}>可视化</Button>
                                </Col>
                            </Row>
                        </FormItem>
                        <FormItem label="说明" {...{ labelCol: { span: 4 }, wrapperCol: { span: 18 } }}>
                            <Row gutter={8}>
                                <Col span={14}>
                                    {getFieldDecorator('info', {
                                        initialValue: curItem.info ? curItem.info : ''
                                    })(
                                        <TextArea placeholder="请输入说明" rows={5} />
                                    )}
                                </Col>
                                <Col span={4}>
                                    <Button type="primary" onClick={this.showUEditor.bind(this, 'info', 'editModel')}>可视化</Button>
                                </Col>
                            </Row>
                        </FormItem>
                    </Form>
                    <Row className="back">
                        <Col span={24}>
                            {<UEditor
                                ref="ueditor"
                                enterTag='br'
                                height={height}
                                initialContent={htmlCon}
                            />}
                        </Col>
                    </Row>
                </div>
            )
        }
    }
    onRadioChange = (e) => {
        const value = e.target.value;
        const { blocks: { template, updateStore, query, templateType } } = this.props;
        const { curCatgItem } = this.state;
        if (templateType == value) {
            return
        }
        if (value == "category") {
            if (!!curCatgItem.pageTemplateId) {
                template(curCatgItem.pageTemplateId)
                updateStore({ query: { ...query, pageTemplateId: curCatgItem.pageTemplateId, templateType: "category" }, curBlk: [], showTempCon: true })
                this.setState({
                    showBlkCon: false
                })
            } else {
                updateStore({ blocks: [], query: { ...query, templateType: "category" }, curBlk: [], showTempCon: false })
                this.setState({
                    showBlkCon: false
                })
            }
        }
        if (value == "detail") {
            if (!!curCatgItem.templateId) {
                template(curCatgItem.templateId)
                updateStore({ query: { ...query, templateId: curCatgItem.templateId, templateType: "detail" }, curBlk: [], showTempCon: true })
                this.setState({
                    showBlkCon: false
                })
            } else {
                updateStore({ blocks: [], query: { ...query, templateType: "detail" }, curBlk: [], showTempCon: false })
                this.setState({
                    showBlkCon: false
                })
            }
        }
    }
    componentWillUnmount() {
        const { blocks } = this.props;
        blocks.reset();
    }
    render() {
        const { blocks: { catTree, query, blocks, showTempCon, expandedKeys }, app: { selectSiteId } } = this.props;
        const { selectedKeys, visible, evisible } = this.state;
        const CcatTree = toJS(catTree)
        return (
            <div>
                <Modal ref="editModel" {...this.editModalOpts()}>
                    {evisible ? this.renderEditModal() : null}
                </Modal>
                <Modal ref="model" {...this.modalOpts()}>
                    {visible ? this.renderModalContent() : null}
                </Modal>
                <div className="blks-wrap">
                    <div className="blks-tree">
                        <Tree
                            expandedKeys={toJS(expandedKeys)}
                            selectedKeys={toJS(selectedKeys)}
                            onSelect={this.onSelect}
                            onExpand={this.onExpandChange}
                        >
                            {this.renderTreeNode(CcatTree)}
                        </Tree>
                    </div>
                    <div className="blks-con">
                        <RadioGroup style={{ 'marginBottom': '20px' }} onChange={this.onRadioChange} value={query.templateType} defaultValue={query.templateType}>
                            <RadioButton value="category">频道模版</RadioButton>
                            <RadioButton value="detail">详情模版</RadioButton>
                        </RadioGroup>
                        {toJS(showTempCon) ? (
                            <div>
                                <Card style={{ width: '100%' }}>
                                    {this.renderBlocks()}
                                </Card>
                                {this.renderBlkCon()}
                            </div>
                        ) :
                            (
                                <div style={{ 'margin': '20px', 'fontSize': '20px' }}>
                                    {query.templateType == "detail" ? "该频道没有绑定详情模版" : '该频道没有绑定模版'}
                                </div>
                            )}
                    </div>
                </div>
            </div>
        )
    }
}

export default Form.create()(BlockList);
