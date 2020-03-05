import { observable, action, extendObservable, runInAction } from 'mobx';
import { toJS } from 'mobx';
import * as api from '../services/category';
import { Jt } from '../utils';
import _ from 'lodash';

const defaults = {
    selectSiteName:'',
    loading: false,
    dataSource:[],
    curCatg:{},
    expandedRows: [1],
    templateDataSource:[],
    query:{}, //搜索条件
    forumsData: [],
    bbsId: null,
    templatePagination:{
        size: 'middle',
        pageSize: 10,
        current: 1,
        total: null,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: total => `共 ${total} 条`
    }
}

class Category {
    constructor(){
        extendObservable(this, {
            ...defaults
        });
    }

    @action.bound getCatTree(params){
        this.loading = true;
        api.getCatList(params).then((res)=>{
            this.loading = false;
            const {code,data} = res;
            const CatData = Jt.tree.format(data) || [];
            if (!!CatData && !!CatData.length) {
                CatData[0].name = this.selectSiteName;
                CatData[0].label = this.selectSiteName;
            }
            this.dataSource = CatData;
            if(data && !!data.length){
                if (_.indexOf(toJS(this.expandedRows), parseInt(data[0].id)) === -1){
                    this.expandedRows.push(data[0].id);
                }
            }
        })
    }
    @action.bound getCatgItem(id){
        this.loading = true;
        api.getCatItem(id).then((res) => {
            this.loading = false;
            const { code, data } = res;
            this.curCatg = data;
            this.bbsId = data.bbsId ? data.bbsId : null;
        })
    }
    @action.bound  getBlogTree(){
        this.loading = true;
        api.getBlogItem().then((res) => {
            this.loading = false;
            const { code, data } = res;
            if(data) {
                const tempData = JSON.parse(data)
                if(tempData.code === 0 ) {
                    this.forumsData = tempData.data;
                }
            }
        })
    }
    @action.bound saveCatg(data,cb){
        this.loading = true;
        api.changeCatg(data).then((res)=>{
            this.loading = false;
            cb && cb();
        })
    }
    @action.bound deleteCatg(id, cb) {
        this.loading = true;
        api.removeCatg(id).then((res) => {
            this.loading = false;
            cb && cb();
        })
    }
    @action.bound getTemplateList(params) {
        this.loading = true;
        const query = {
            pageNumber: this.templatePagination.current,
            pageSize: this.templatePagination.pageSize,
            ...params
        }
        this.query = query;
        api.templateList(query).then((res) => {
            this.loading = false;
            const { code, data } = res;
            if (!!data && code === 0) {
                runInAction(() => {
                    this.templateDataSource = data.list || [];
                    this.templatePagination = {
                        ...this.templatePagination,
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

    @action.bound async getTempByName(params) {
        this.loading = true;
        const query = {
            pageNumber: this.templatePagination.current,
            pageSize: this.templatePagination.pageSize,
            ...params
        }
        this.query = query;
        api.findTempByName(query).then((res) => {
            this.loading = false;
            const { data, code } = res;
            if(!!data && code === 0) {
                runInAction(() => {
                    this.templateDataSource = data.list || [];
                    this.templatePagination = {
                        ...this.templatePagination,
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

    @action.bound async preview(id, cb) {
        await api.preview({id}).then((res) => {
            const { code, data } = res;
            cb && cb(res);
        })
    }
    @action.bound updateSorts(data){
        this.loading = true;
        api.updateSorts(data).then((res) => {
            this.loading = false;
        })
    }
    @action.bound updateStore(data){
        Object.assign(this,data)
    }

}

export default Category;
