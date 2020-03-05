import { observable, action, extendObservable, runInAction } from 'mobx';
import * as api from '../services/cms';
import { Jt } from 'utils';
import { toJS } from 'mobx'

const defaults = {
    siteId:null,
    selectSiteName:'',
    catTree:[],
    expandedKeys:[],
    dataSource:[],
    curArt:{},
    status:[],
    selArts:[],
    articleId: '',
    selSouArts: [],
    channelList: [], // （发布到其他频道）其他频道列表
    query:{}, //搜索条件
    modalVisible: false,
    pagination: {
        size: 'middle',
        pageSize: 20,
        current: 1,
        total: null,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: total => `共 ${total} 条`
    },
    albumNum:0,
    audioNum:0,
    videoNum:0
}

class Cms {
    constructor() {
        extendObservable(this, {
            ...defaults
        });
    }
    @action.bound async getInit(params) {
        this.loading = true;
        await this.getCategory(params);  // params为selectSiteId用来请求网站对应的频道树
        if (!!this.catTree.length){
            const query = {
                categoryId: !!this.catTree ? this.catTree[0].id : '1', //首次进来请求顶层频道和草稿箱
                delFlag: 1,
                ...this.query
            }
            this.expandedKeys = [(query.categoryId?query.categoryId.toString():'1')]  //进来展开选中频道
            this.articleList(query);
            this.query = query;
        }else{
            this.dataSource = [];  //无频道则无数据
        }
    }
    @action.bound async getCategory(params) {
        await api.getCatList(params).then((res) => {
            const { code, data } = res;
            if (code == 0) {
                const CatData = Jt.tree.format(data) || [];
                if (!!CatData && !!CatData.length) {
                    CatData[0].name = this.selectSiteName;
                    CatData[0].label = this.selectSiteName;
                }
                this.catTree = CatData;
            }
        })
    }
    @action.bound async getStatus(params){
        await api.getDict(params).then((res)=>{
            const {code, data} = res;
            if(code==0){
                this.status = data.list||[];
            }
        })
    }
    @action.bound async getArtItem(id,cb){
        await api.getItem(id).then((res) => {
            const { code, data } = res;
            if (code == 0) {
                runInAction(()=>{
                    this.curArt = data || {};
                    const type = data.type;
                    if (type) {
                        this.albumNum = type === 'image' ? 1 : 0;
                        this.videoNum = type === 'video' ? 1 : 0;
                        this.audioNum = type === 'audio' ? 1 : 0;
                    }
                });
                cb&&cb();
            }
        })
    }

    @action.bound async getChannelItem(id, cb) {
        await api.getChannelList(id).then((res) => {
            const { code ,data } = res;
            var tempArr = [];
            tempArr = data.map((id) => id + '');
            if(code == 0) {
                runInAction(() => {
                    this.channelList = tempArr ;
                })
                cb && cb();
            }
        })
    }
    @action.bound async preview(id,cb){
        await api.preview(id).then((res) => {
            const { code, data } = res;
            cb && cb(res);
        })
    }
    @action.bound async deleteArt(id,cb){
        this.loading = true;
        await api.deleteItem(id).then((res) => {
            this.loading = false;
            const { code, data } = res;
            cb && cb();
        })
    }
    @action.bound async articleList(params={}){
        this.loading = true;
        const query = {
            pageNumber: this.pagination.current,
            pageSize: this.pagination.pageSize,
            ...params
        };
        if(!!this.catTree.length && query.categoryId==this.catTree[0].id){ // 如果是顶级频道则删除categoryId传入siteId来查询全部文章，其他频道只需要传categoryId不需要传siteId
            delete query.categoryId
            query.siteId = this.siteId;
        }else{
            delete query.siteId
        }
        if(query.status){
            query.delFlag = query.status;
        }
        await api.getArticleList(query).then((res) => {
            this.loading = false;
            const { code, data } = res;
            if (code == 0) {
                runInAction(()=>{
                    this.dataSource = data.list || [];
                    this.pagination = {
                        ...this.pagination,
                        ...{
                            total: data.pager.recordCount,
                            current: data.pager.pageNumber,
                            pageSize: data.pager.pageSize
                        }
                    }
                })
            }
        })
    }
    @action.bound async onSaveDraft(params,cb) {
        api.saveArtDraft(params).then((res) => {
            const {code,data} = res;
            if(code===0){
                cb&&cb(res)
            }
        })
    }
    /**
     *
     * @param {*object} params
     * @param {*function} cb
     * 一稿多发
     */
    @action.bound async onSaveChannel(params, cb) {
        api.saveArtChannel(params).then((res) => {
            const {code, data} = res;
            if(code===0) {
                cb && cb()
            }
        })
    }

    @action.bound async onSaveAudit(params, cb){
        this.loading=true;
        api.saveArtAudit(params).then((res) => {
            const { code, data } = res;
            if (code === 0) {
                cb && cb()
            }
        })
    }
    @action.bound async onSave(params, cb) {
        api.saveArt(params).then((res) => {
            const { code, data } = res;
            if (code === 0) {
                cb && cb()
            }
        })
    }
    @action.bound async onOffArts(ids, cb){
        api.onOffArts({ articleIds:ids}).then((res)=>{
            const {code,data} = res;
            if(code==0){
                this.articleList(this.query)
            }
        })
    }
    @action.bound async batchAuditArts(ids,cb){
        console.log(ids)
        api.batchAuditArts({ articleIds: ids }).then((res) => {
            const { code, data } = res;
            if (code == 0) {
                this.articleList(this.query)
            }
        })
    }
    @action.bound async onOffArt(params, cb){
        api.onOffArt(params).then((res)=>{
            const {code,data} = res;
            if(code==0){
                cb&&cb();
            }
        })
    }
    @action.bound async deleteArts(ids, cb) {
        api.removeArts({ ids: ids }).then((res) => {
            const { code, data } = res;
            if (code == 0) {
                this.articleList(this.query)   //批量操作后刷新列表
            }
        })
    }

    @action.bound async insertArts(list, cb) {
        api.insertArts(list).then((res) => {
            const { code, data } = res;
            if(code == 0) {
                this.articleList(this.query);
                cb && cb();
            }
        })
    }
    @action.bound async updateSorts(params){
        api.updateSorts(params).then((res)=>{
            const { code, data } = res;
        })
    }
    @action.bound updateStore(data) {
        Object.assign(this, data);
    }
    @action.bound reset() {
        for (let i in defaults) {
            this[i] = defaults[i];
        }
    }
}

export default Cms;
