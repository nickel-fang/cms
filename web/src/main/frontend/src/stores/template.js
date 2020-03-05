import { extendObservable, action, runInAction } from "mobx";
import * as api from '../services/template'

const defaults = {
    curItem: {},
    dataSource: [],
    allFileList: {},
    addedFileList:{},
    selectedType:null,  //选中模版类型
    loading: false,
    query: {},
    pagination: {
        size: 'middle',
        pageSize: 10,
        current: 1,
        total: null,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: total => `共 ${total} 条`
    }
}

class Template{
    constructor(){
        extendObservable(this,{
            ...defaults
        })
    }
    @action.bound getTemplateList(params){
        this.loading = true;
        const query = {
            pageNumber: this.pagination.current,
            pageSize: this.pagination.pageSize,
            ...params
        }
        this.query = query;
        api.templateList(query).then((res) => {
            this.loading = false;
            const { code, data } = res;
            if (!!data && code === 0) {
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
    @action.bound async deleteTemp(id, cb) {
        this.loading = true;
        await api.deleteItem(id).then((res) => {
            this.loading = false;
            const { code, data } = res;
            cb && cb();
        })
    }
    @action.bound async getItem(id){
        await api.getTemplateItem(id).then((res) => {
            const { data, code } = res;
            if (!!data && code === 0) {
                runInAction(()=>{
                    this.curItem = data || {};
                    this.selectedType = data.type || null;
                    this.allFileList = data.resourceJson ? JSON.parse(data.resourceJson) : {}
                })
            }
        })
    }
    @action.bound async saveItem(params, cb) {
        await api.saveTemplate(params).then((res) => {
            const { data, code } = res;
            if (!!data && code === 0) {
                cb && cb();
            }
        })
    }
    @action.bound async deleteBlock(id){
        await api.deleteBlockItem(id).then((res)=>{
            const {data, code} = res;
        })
    }
    @action.bound updateStore(data) {
        Object.assign(this, data);
    }
    @action.bound reset(){
        for(let k in defaults){
            this[k] = defaults[k]
        }
    }
}

export default Template;
