import {extendObservable, action, runInAction, toJS} from 'mobx';
import * as api from '../services/users';
import { inject } from 'mobx-react';
import { CACHE_USER, CACHE_SYSTEMS, CACHE_MENUS, CACHE_GLOBAL} from '../constants';
import Cookie from 'js-cookie';
import { Jt, localCache, sessionCache } from '../utils';
import _ from 'lodash';

const defaults = {
    loading: false,
    list: [],
    role: {},
    dataScopes: [],
    offices: [],
    menus: [],
    sites:[],
    categoryList:{},
    frontIsInit:'true',
    backIsInit:'true',
    pagination:{
        size: 'middle',
        pageSize: 10,
        current: 1,
        total: null,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: total => `共 ${total} 条`
    },
}

export default class Roles {
    constructor() {
        extendObservable(this, {
            ...defaults
        })
    }

    @action.bound async getInit(params = {}) {
       await this.rolesList()
    }

    @action.bound async rolesList(params = {}) {
        this.loading = true;
        const query = {
            pageNumber:this.pagination.current,
            pageSize:this.pagination.pageSize,
            ...params
        }
        await api.queryRoles(query).then((res) => {
            this.loading = true;
            const {code, data} = res;
            if(code === 0) {
                runInAction(() => {
                    this.list = data.list
                    this.loading = false
                    this.pagination = {
                        ...this.pagination,
                        total: data.pager.recordCount,
                        current: data.pager.pageNumber,
                        pageSize: data.pager.pageSize
                    }
                })
            }
        })
    }

    @action.bound async onPageChange(page) {
        const query = {
            ...this.query,
            pageNumber:page.current,
            pageSize:page.pageSize
        }

        this.rolesList(query)
    }

    @action.bound async getRole(id) {
        
        return api.queryRole({id}).then((res) => {
            const { code, data } = res;
            if(code == 0) {
                runInAction(() => {
                    this.role = data || {};
                    this.frontIsInit = 'true'
                    this.backIsInit = 'true'
                    this.getMenus()
                })
            }            
        })
    }

    @action.bound async getSites() {
        let sites = []
        api.querySites({role:'true'}).then((res) => {
            const {code, data} = res
            if(code === 0) {
                runInAction(() => {
                    sites = data || []
                    this.sites = sites
                    this.getCategories(sites)
                })
            }
            
        })

    }

    @action.bound async getCategories(sites) {
        let {categoryList} = this
            let categoryCallMap = []
            let siteId = ''
            !!sites && sites.length > 0 ?(
                categoryCallMap =sites.map((item,index) => {
                    let siteId = item.id + ''
                    return api.queryCategory({siteId:item.id,role:'true'}).then((res) => {
                        const {code, data} = res
                        if(code === 0) {
                            runInAction(() => {                                           
                                categoryList[siteId] = data  
                            })                          
                        }
                    })
                }),
                runInAction(() => { 
                    this.categoryList = categoryList
                })
            ):null
            
    }

    @action.bound async getMenus() {
        let menus = sessionCache.get(CACHE_MENUS) || [];
        //if (_.isEmpty(menus)) {
            api.queryMenuTree({role:'true'}).then((res) => {
                const {code, data} = res
                if(code === 0) {
                    //sessionCache.set(CACHE_MENUS, data);
                    runInAction(() => {
                        this.menus = Jt.tree.formatChild(data) || []                       
                    })
                }
            
            })
        //}
        
    }

    @action.bound async deleteRoleItem(id,cb) {
        api.deleteRole({id}).then((res) => {
            const {code, data} = res
            if(code === 0) {
                !!cb && cb()
            }
        })
    }

    @action.bound async saveRole(role,cb) {
        if(!!role.id) {
            api.updateRole(role).then((res) => {
                const {code, data} = res
                if(code === 0) {
                    !!cb && cb()
                }
            })
        }
        else {
            api.createRole(role).then((res) => {
                const {code, data} = res
                if(code === 0) {
                    !!cb && cb()
                }
            })
        }
    }

    @action.bound updateStore(data){
        Object.assign(this,data);
    }
    @action.bound reset() {
        Object.assign(this, defaults);
    }

}
