import React, { Component } from 'react';
import lodash from 'lodash';
import { Jt } from '../../utils';

const toolbars = [[
    'source', '|', 'undo', 'redo', '|',
    'bold', 'italic', 'underline', 'fontborder', 'strikethrough', 'removeformat', 'formatmatch', 'autotypeset', 'blockquote', 'pasteplain', '|', 'forecolor', 'backcolor', 'insertorderedlist', 'insertunorderedlist', 'lineheight', '|',
    'paragraph', 'fontfamily', 'fontsize', '|',
    'indent',
    'justifyleft', 'justifycenter', 'justifyright', 'justifyjustify', '|', 'touppercase', 'tolowercase', '|',
    'link', 'unlink', '|', 'imagenone', 'imageleft', 'imageright', 'imagecenter', '|',
    'simpleupload', 'attachment', '|',
    'horizontal',
    'inserttable', 'deletetable', 'insertparagraphbeforetable', 'insertrow', 'deleterow', 'insertcol', 'deletecol', 'mergecells', 'mergeright', 'mergedown', 'splittocells', 'splittorows', 'splittocols', '|',
    'searchreplace'
]];

class UEditor extends Component{
    constructor(props) {
        super(props);
        this.inited = false;
        this.timer = null;
        this.editor = null;
    }
    onContentChange = () => {
        if (this.inited && this.props.onContentChange) {
            this.props.onContentChange(this.editor.getContent);
        }
    }
    onEditorReady = () => {
        const props = this.props;
        const config = lodash.omit(props, ['id', 'width', 'height']);
        config.initialFrameWidth = props.initialFrameWidth || props.width;
        config.initialFrameHeight = props.initialFrameHeight || props.height;
        this.editor = UE.getEditor(props.id, config);
        this.editor.ready(() => {
            this.editor.addListener('contentchange', this.onContentChange);
            if (this.props.initialContent) {
                this.editor.setContent(this.props.initialContent);
                this.inited = true;
            }
        });
    }
    initUeditor = () => {
        if (window.UE && UE.getEditor) {
            this.onEditorReady();
        } else {
            this.timer = setInterval(() => {
                if (window.UE && UE.getEditor) {
                    clearInterval(this.timer);
                    this.timer = null;
                    this.onEditorReady();
                }
            }, 300);
        }
    }
    setContent = (data) => {
        this.editor.setContent(data)
    }
    getContent = () => {
        return this.editor.getContent();
    }
    getHtml = () => {
        return this.editor.getAllHtml();
    }
    componentWillReceiveProps(nextProps) {
        if (!this.inited && this.editor && this.editor.isReady && !lodash.isEmpty(nextProps.initialContent)) {
            this.editor.setContent(nextProps.initialContent);
            this.inited = true;
        }
    }
    componentDidMount() {
        const { ueditorConfigSrc, ueditorSrc } = this.props;
        if(!this.inited){
            Jt.loader.loadScripts([ueditorConfigSrc, ueditorSrc], this.initUeditor, 'body');
        }
    }
    componentWillUnmount() {
        if (this.editor) {
            this.editor.removeListener('contentChange', this.onContentChange);
            this.editor.destroy();
            this.editor = null;
        }
    }
    render() {
        const { id } = this.props;
        return (
            <script id={id} type="text/plain" style={{ marginBottom: '24px' }}>
            </script>
        );
    }
}

UEditor.defaultProps = {
    toolbars,
    id: 'ueditor-container',
    width: '100%',
    height: 700,
    initialContent: '',
    ueditorConfigSrc: "./public/ueditor/ueditor.config.js",
    ueditorSrc: "./public/ueditor/ueditor.all.js",
    maximumWords: 1000000,
    scaleEnabled: false,
    autoFloatEnabled: false,
    autoHeightEnabled: false,
    elementPathEnabled:false,
    enableAutoSave:false
};

export default UEditor;
