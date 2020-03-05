import {extendObservable, action, runInAction, toJS} from 'mobx';
import * as api from '../services/users'

const defaultState = {
    list:[],
    loading:false,
    query:{
        name:'',
        username:''
    },
    pagination:{
        size: 'middle',
        pageSize: 10,
        current: 1,
        total: null,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: total => `共 ${total} 条`
    },
    modalVisible:false,
    currentUser:{},
    roles:[]

}
export default class Permissions {
    constructor() {
        extendObservable(this,defaultState)
    }

    @action.bound async getInit (params={}) {
        await this.usersList(params)
        await this.rolesList()
    }

    @action.bound async usersList(params) {
        this.loading = true
        const query = {
            ...this.query,
            pageNumber:this.pagination.current,
            pageSize:this.pagination.pageSize,
            ...params,
        }
        return api.queryUsers(query).then((res) => {
            const {code, data} = res
            if(code === 0) {
                runInAction(()=> {
                    this.list = data.list || []
                    this.loading = false
                    this.query = query
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

    @action.bound async rolesList(params) {

        return api.queryAllRoles().then((res) => {
            const {code, data} = res
            if(code === 0) {
                runInAction(()=> {
                    this.roles = data || []
                })
            }
        })
    }

    @action.bound async getUserPermission() {
        return api.queryUserPermission({id:this.currentUser.id}).then((res) => {
            const {code, data} = res
            if(code === 0) {
                runInAction(() => {
                    this.currentUser = {
                        ...this.currentUser,
                        roleIds: data.roleIds || []
                    }
                })
            }
        })
    }

    @action.bound async savePermission(params) {
        return api.saveUserPermission(params).then((res) => {
            const {code} = res
            if(code === 0) {
                runInAction(() => {
                    this.modalVisible = false
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

        this.usersList(query)
    }

    @action.bound updateState(params) {
        Object.assign(this,params)
    }
    
}

