import { extendObservable, action, runInAction, toJS } from "mobx";
import * as api from '../services/sys';
import { Jt } from 'utils';
import _ from 'lodash';

const defaults = {
    menus:[],
    iconModalVisible:false,
    menu:{
        parentId:''
    }
}

class Sys {
    constructor(){
        extendObservable(this, {
            ...defaults
        })
    }
    @action.bound saveMenu(data,cb){
        if(!!data.id){
            api.updateMenu(data).then((res) => {
                const { code } = res;
                if (code === 0) {
                    cb && cb();
                }
            })
        }else{
            api.createMenu(data).then((res) => {
                const { code } = res;
                if (code === 0) {
                    cb && cb();
                }
            })
        }
    }
    @action.bound getMenuItem(id){
        api.queryMenu(id).then((res)=>{
            const { code, data } = res;
            if (code === 0) {
                this.menu = data;
            }
        })
    }
    @action.bound getMenuList(params){
        this.loading = true;
        api.queryMenuTree(params).then((res)=>{
            const {code, data=[]} = res;
            if (code === 0) {
                this.loading = false;
                this.menus = Jt.tree.formatChild(data);
            }
        })
    }
    @action.bound deleteMenu(id,cb){
        api.deleteMenu({id}).then((res) => {
            const { code, data = [] } = res;
            if (code === 0) {
                cb&&cb();
            }
        })
    }
    @action.bound updateSorts(updateArr,cb){
        api.updateSorts(updateArr).then((res)=>{
            const { code, data } = res;
            if (code === 0) {
                cb && cb();
            }
        })
    }
    @action.bound updateStore(data) {
        Object.assign(this, data)
    }
    @action.bound reset() {
        for (let i in defaults) {
            this[i] = defaults[i]
        }
    }
}

export default Sys;
