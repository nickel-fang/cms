import { extendObservable, action, runInAction } from "mobx";
import * as api from '../services/source';

const defaults = {
    selectedSiteId:null,
    loading: false,
    curArt:{},
    sourceList: [],
    query: {},
    importType: null, // 0:手工创建，1:trs导入
    dataSource: [],
    selArts:[],
    pagination: {
        showQuickJumper: true,
        showTotal: total => `共 ${total} 条`,
        current: 1,
        showSizeChanger: true,
        total: null,
        pageSize: 20
    },
    albumNum:0,
    audioNum:0,
    videoNum:0
}
class Source{
    constructor(){
        extendObservable(this, {
            ...defaults
        });
    }
    @action.bound async getInit(params={}) {
        const query = {
            ...this.query,
            ...params,
        }
        await this.articleList(query);
        this.query = query
    }

    @action.bound async articleList(params={}) {
        this.loading = true;
        const query = {
            pageNumber: this.pagination.current,
            pageSize: this.pagination.pageSize,
            delFlag: 5,
            ...params
        };
        await api.getArticleList(query).then((res) => {
            this.loading = false;
            const { code, data } = res;
            if( code == 0 ) {
                runInAction(() => {
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

    @action.bound async onSaveArticle(params,cb) {
        await api.saveArticleContent(params).then((res) => {
            const {code,data} = res;
            if(code===0){
                cb && cb(res);
            }
        })

    }

    /**
     *
     * 稿源库-文章列表-删除按钮（单个）
     * @param {number} id
     * @param {function} cb
     */
    @action.bound async deleteArt(id,cb) {
        this.loading = true;
        await api.deleteItem(id).then((res) => {
            this.loading = false;
            const { code, data } = res;
            cb && cb();
        })
    }

    /**
     *
     * 稿源库-文章列表-删除按钮（批量）
     * @param {number} ids
     * @param {function} cb
     */
    @action.bound async deleteArts(ids, cb) {
        this.loading = true;
        await api.removeArts({ ids: ids }).then((res) => {
            const { code, data } = res;
            if (code == 0) {
                this.loading = false;
                this.articleList(this.query)   //批量操作后刷新列表
            }
        })
    }

    /**
     *
     * @param {number} id
     * @param {function} cb
     * 点击编辑进入文章详情页
     */
    @action.bound async getArtItem(id, cb) {
        return api.getItem(id).then((res) => {
            const { code, data } = res;
            if(code == 0) {
                runInAction(() => {
                    this.curArt = data || {};
                    const type = data.type;
                    if (type) {
                        this.albumNum = type === 'image' ? 1 : 0;
                        this.videoNum = type === 'video' ? 1 : 0;
                        this.audioNum = type === 'audio' ? 1 : 0;
                    }
                })
            }
            cb && cb();
        })

    }

    /**
     * 稿源库-文章列表页-预览按钮
     * @param {*} id
     * @param {*} cb
     */
    // @action.bound async preview(id,cb){
    //     await api.preview(id).then((res) => {
    //         const { code, data } = res;
    //         cb && cb(res);
    //     })
    // }
    @action.bound updateStore(data){
        Object.assign(this, data);
    }

    @action.bound setImportType(value) {
        this.importType = value;
    }

}

export default Source;
