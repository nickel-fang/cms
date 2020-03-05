import { observable, action, extendObservable, runInAction } from 'mobx';
import * as api from '../services/site';

const defaults = {
    siteList:[],
    curItem:{},
    dataSource:[],
    loading:false,
    pagination:{
        size: 'middle',
        pageSize: 10,
        current: 1,
        total: null,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: total => `共 ${total} 条`
    }
}
class Site {
    constructor() {
        extendObservable(this, {
            ...defaults
        });
    }
    @action.bound async getListItem(id){
        await api.getSiteItem(id).then((res) => {
            const { data, code } = res;
            if(!!data&&code===0){
                this.curItem = data;
            }
        })
    }
    @action.bound async getList(params={}){
        this.loading = true;
        const query = {
            pageNumber:this.pagination.current,
            pageSize:this.pagination.pageSize,
            ...params
        }
        await api.getSiteList(query).then((res)=>{
            const {data,code} = res;
            this.loading = false;
            if(!!data && code === 0){
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
    @action.bound onSave(params,cb){
        api.saveSite(params).then((res)=>{
            if(res.code===0){
                cb&&cb();
            }else if(res.code === -1){
                console.log('get in error')
                cb&&cb( -1, res.msg)
            }
        })
    }
    @action.bound deleteListItem(id,cb){
        this.loading = true
        api.deleteItem(id).then((res) => {
            if (res.code === 0) {
                cb && cb(res.code);
                this.loading = false;
            }else {
                cb && cb(res.code, res.msg);
                this.loading = false;
            }
        })
    }
    @action.bound updateStore(data){
        Object.assign(this,data);
    }
    @action.bound reset() {
        Object.assign(this, defaults);
    }
}

export default Site;
