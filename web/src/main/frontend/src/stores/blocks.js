import { extendObservable, action, runInAction, toJS } from "mobx";
import * as api from '../services/blocks';
import { Jt } from 'utils';
import _ from 'lodash';

const defaults = {
    loading: false,
    selectSiteName:null,
    catTree: [],
    curBlk: [],  //选中区块的列表
    expandedKeys: [],
    expandedCiteArtKeys: [],
    expandedCatgKeys:[],
    showTempCon:false, //是否展示区块内容
    selectedBlockId:null,
    blocks:[],
    artsList:[],  //引用文章列表
    showTempCon: false, //是否展示区块内容
    selectedBlockId: null,
    query: {  //搜索条件
        templateType: null,
        categoryId: null,
        blockId: null,
        pageTemplateId: null,     //频道模版id，根据模版id请求区块
        templateId: null,      //详情模版id
    },
    pagination: {
        size: 'middle',
        pageSize: 10,
        current: 1,
        total: null,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: total => `共 ${total} 条`
    },
    citePagination: {
        size: 'small',
        pageSize: 10,
        current: 1,
        total: null,
        showSizeChanger: false,
        showQuickJumper: false,
        showTotal: total => `共 ${total} 条`
    }
}

class Blocks {
    constructor() {
        extendObservable(this, {
            ...defaults
        })
    }
    @action.bound async getInit(params) {
        this.loading = true;
        await this.getCategory(params)   //params为selectSiteId用来请求网站对应的频道树
        this.loading = false;
        if (!!this.catTree.length) {   //默认一进来请求顶层频道的频道模版
            if (this.catTree[0].pageTemplateId) {
                const query = {
                    ...this.query,
                    pageTemplateId: this.catTree[0].pageTemplateId,
                    templateType: 'category'
                }
                this.query = query;
                this.template(query.pageTemplateId);
            } else if (this.catTree[0].templateId) {
                const query = {
                    ...this.query,
                    templateId: this.catTree[0].templateId,
                    templateType: 'detail'
                }
                this.query = query;
                this.template(query.templateId);
            }
            if (_.indexOf(toJS(this.expandedKeys), parseInt(this.catTree[0].id)) === -1) {
                this.expandedKeys.push(this.catTree[0].id+'');
            }
            if (_.indexOf(toJS(this.expandedCiteArtKeys), parseInt(this.catTree[0].id)) === -1) {
                this.expandedCiteArtKeys.push(this.catTree[0].id + '');
            }
            if (_.indexOf(toJS(this.expandedCatgKeys), parseInt(this.catTree[0].id)) === -1) {
                this.expandedCatgKeys.push(this.catTree[0].id + '');
            }
        }
    }
    @action.bound async template(id) {
        api.getTemplateItem(id).then((res) => {
            this.loading = false;
            const { code, data } = res;
            if (code == 0) {
                runInAction(() => {
                    this.blocks = data.blocks || []
                })
            }
        })
    }
    @action.bound async getBlock(params) {
        this.loading = true;
        const query = {
            pageNumber: this.pagination.current,
            pageSize: this.pagination.pageSize,
            ...params
        }
        api.getBlockItem(query).then((res) => {
            this.loading = false;
            const { code, data } = res;
            if (code == 0) {
                this.curBlk = data.list || [];
                this.pagination = {
                    ...this.pagination,
                    ...{
                        total: data.pager.recordCount,
                        current: data.pager.pageNumber,
                        pageSize: data.pager.pageSize
                    }
                }
            }
        })
    }
    @action.bound async changeOnOff(params, cb) {
        await api.changeOnOff(params).then((res) => {
            cb && cb();
        })
    }
    @action.bound async getCategory(params) {
        await api.getCatList(params).then((res) => {
            const { code, data } = res;
            this.loading = false;
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
    @action.bound async articleList(params) {
        runInAction(() => {
            this.loading = true;
        })
        const query = {
            pageNumber: this.citePagination.current,
            pageSize: this.citePagination.pageSize,
            ...params
        }
        await api.getArticleList(query).then((res) => {
            const { code, data } = res;
            if (code == 0) {
                runInAction(() => {
                    this.loading = false;
                    this.artsList = data.list || [];
                    this.citePagination = {
                        ...this.citePagination,
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
    @action.bound async editBlockItem(params, cb) {
        await api.editBlockItem(params).then((res) => {
            cb && cb();
        })
    }
    @action.bound async batchOnOff(params, cb) {
        await api.batchOnOff(params).then((res) => {
            cb && cb();
        })
    }
    @action.bound async batchDelete(params, cb) {
        await api.batchDelete(params).then((res) => {
            cb && cb();
        })
    }
    @action.bound async saveCiteArt(params, cb) {
        await api.saveArts(params).then((res) => {
            cb && cb();
        })
    }
    @action.bound async saveCatg(params, cb) {
        await api.saveCatg(params).then((res) => {
            cb && cb();
        })
    }
    @action.bound async saveTxt(params, cb) {
        await api.saveTxt(params).then((res) => {
            cb && cb();
        })
    }
    @action.bound async saveImg(params, cb) {
        await api.saveImage(params).then((res) => {
            cb && cb();
        })
    }
    @action.bound async deleteBlock(id, cb) {
        await api.deleteBlk(id).then((res) => {
            cb && cb();
        })
    }
    @action.bound async preview(params, cb) {
        await api.previewPage(params).then((res) => {
            cb && cb(res);
        })
    }
    @action.bound async refreshAll(params, cb) {
        await api.refreshAll(params).then((res) => {
            cb && cb(res);
        })
    }
    @action.bound async updateSorts(params) {
        api.updateSorts(params).then((res) => {
            const { code, data } = res;
        })
    }
    @action.bound reset() {
        for (let i in defaults) {
            this[i] = defaults[i]
        }
    }
    @action.bound updateStore(data) {
        Object.assign(this, data)
    }
}

export default Blocks;
