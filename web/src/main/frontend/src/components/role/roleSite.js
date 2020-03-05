import React from 'react'
import {observer,inject} from 'mobx-react';
import {Form, TreeSelect, Checkbox, Input, Button, Select, Row, Col, message, Collapse} from 'antd';
import { Jt } from '../../utils'
import _ from 'lodash';
import {toJS, get} from 'mobx';
const FormItem = Form.Item;
const Option = Select.Option;


const layout = {
    labelCol: {span: 4},
    wrapperCol: {span: 12}
};
const Panel = Collapse.Panel;

@inject('app')
@inject('roles')
@observer
class UserSite extends React.Component{
    constructor(props){
        super(props)        
        const {roles:{categoryList}, app, roles, } = props
        let clone = toJS(roles.sites)
        let sites = _.cloneDeep(clone) || []    
        let sitesCategories = [
            {
                key:0,
                siteId:undefined,
                categoryIds:[],
            }
        ]       
        this.state = {
            sites:sites,
            sitesCategories: sitesCategories,
            categoryList:categoryList || {},
            treeData:[],
            isInit:props.isInit || 'true'
        }
    }

    componentDidMount() {
        this.checkSites()
    }

    componentWillReceiveProps(nextProps) {
        const {roles} = nextProps
        let isInit  = nextProps.isInit || this.state.isInit || 'true'
        let stateSitesCategories = this.state.sitesCategories || [
            {
                key:0,
                siteId:undefined,
                categoryIds:[],
            }
        ]

        let propsSiteCategory = nextProps.sitesCategories || [
            {
                key:0,
                siteId:undefined,
                categoryIds:[],
            }
        ]

        let userId = roles.role.id || ''
        let clone = toJS(roles.sites)
             
        let sitesCategories = isInit==='true' && !Object.is(propsSiteCategory, stateSitesCategories) && !!userId ? propsSiteCategory : stateSitesCategories
        let isSitesEmpty = this.state.sites.length > 0 ? false : true
        let sites = isSitesEmpty ? _.cloneDeep(clone) : this.state.sites
        let treeData = !Object.is(roles.categoryList, this.state.treeData)? _.cloneDeep(roles.categoryList) : this.state.treeData
        
        !!sites && sites.length > 0?(
            sites.map((item) => {
                item.id = item.id + ''
                if(!!sitesCategories && sitesCategories.length > 0){
                    sitesCategories.map((selectedItem)=>{
                        if(item.id == selectedItem.siteId){
                            item.disabled = true
                        }
                    })
                }
            })
        ):null
        !!sitesCategories && sitesCategories.length > 0 && !!sitesCategories[0].siteId?(
            sitesCategories.map((item,index)=>{
                item.key = index
                let siteId = item.siteId
                typeof(item.categoryIds) == 'string'?(item.categoryIds = item.categoryIds.split(',')):(item.categoryIds = item.categoryIds)
            }),
            isInit = 'false'
        ):null  
   
        this.setState(
            Object.assign({}, {
                sitesCategories,
                treeData:treeData,
                isInit:isInit,
                sites,
            }) 
        )
       
                
    }

    checkSites() {
        const { sites, sitesCategories } = this.state
        !!sites && sites.length > 0 ?(
            sites.map((item,index) => {
                item.disabled = false
                !!sitesCategories && sitesCategories.length > 0?(
                    sitesCategories.map((selectedItem,selectedIndex) => {
                        if(item.id == selectedItem.siteId) {
                            item.disabled = true
                        }
                    })
                ):null
            })
        ):null
        this.setState(Object.assign({}, {sites}))
    }

    getSiteList(site,siteIndex){
        const { form:{getFieldDecorator, setFieldsValue}, type } = this.props
        let { sites, sitesCategories } = this.state      
        if(!site.siteId){
            site.siteId = undefined
        }
        return(
            <FormItem key={siteIndex}>
                {
                    getFieldDecorator(type + `sites[${siteIndex}]`, {
                        initialValue:!!site.siteId?site.siteId+'':undefined,
                      })( 
                            <Select              
                                placeholder="请选择站点权限"
                                onSelect={(value) => this.selectSite(value,siteIndex)}                              
                            >
                                <Option value={'notSet'} key={'notSet'}>未选择</Option>
                                {
                                    sites.length > 0?(
                                        sites.map((item,index)=>{
                                            return <Option disabled={item.disabled} value={item.id+''} key={item.id}>{item.name}</Option> 
                                        })
                                    ):null
                                }
                            </Select>)
                }
            </FormItem>
        )
    }

    getCategoryList(item, index){
        const { form:{getFieldDecorator}, type } = this.props
        const { treeData} = this.state
        
        if(!item.categoryIds){
            item.categoryIds = []
        }
        return (
            <FormItem key={index}>
                {
                    getFieldDecorator(type + `categories[${index}]`, {
                        initialValue:toJS(item.categoryIds),
                      })(
                        <TreeSelect 
                            allowClear
                            showSearch
                            showCheckedStrategy={TreeSelect.SHOW_ALL}
                            treeNodeFilterProp="label"
                            dropdownStyle={{maxHeight: 300, overflow: 'auto'}}
                            treeData={Jt.tree.format(treeData[item.siteId])}
                            multiple={true}
                            treeCheckable={true}                           
                            onChange={(value) => this.changeCategory(value,item.siteId)}                           
                            placeholder="请选择频道权限"
                            
                        /> 
                      )
                }       
                
            </FormItem>
        )
                      
    }

    getCategoryData(siteId) {
        let { treeData, categoryList } = this.state
        if(!!siteId && !!categoryList) {
            treeData[siteId] = Jt.tree.format(categoryList[siteId])
            this.setState(Object.assign({}, { treeData }))
        }
    }

    changeCategory(value, siteId) {       
        let {sitesCategories} = this.state
        !!sitesCategories && sitesCategories.length > 0? (
            sitesCategories.map((item,index) => {
                if(item.siteId == siteId){
                    item.categoryIds = value
                }
            })
        ):null
        
        this.setState(
            Object.assign({}, {sitesCategories})
        )
    }
    
    selectSite(value,siteIndex) {

        const {roles:{updateStore}, form:{setFieldsValue},type} = this.props
        let {sitesCategories} = this.state

        !!sitesCategories && sitesCategories.length > 0 ?(
            sitesCategories.map((item,index) => {
                if(item.key == siteIndex){
                    item.siteId = value
                    item.categoryIds = []
                    let categoriesIndex = type + `categories[${index}]`
                    let obj = {}
                    obj[categoriesIndex] = []
                    setFieldsValue(obj)
                }
            }),
            sitesCategories.map((item,index) => {
                item.key = index
            })
        ):null

        type==='front'?updateStore({frontIsInit:'false'}):updateStore({backIsInit:'false'});
        this.setState(
            Object.assign({}, { sitesCategories, isInit:'false'}),
            () => {
                this.checkSites()
                this.getCategoryData(value)            
            }           
        )              
           
    }

    addSite(){
        const {roles:{updateStore},type} = this.props
        const {sites, sitesCategories} = this.state;
        let clone = toJS(sitesCategories)
        if(sites.length <= sitesCategories.length){
            message.info('已无可加站点')
        }
        else{
            //debugger
            clone.push({
                key:sitesCategories.length,
                siteId:'',
                categoryIds:'',
    
            })
            clone.map((site, index) => {
                site.key = index
            });
            
            this.setState(
                Object.assign({}, { sitesCategories:clone, isInit:'false'}),
                () => {
                    this.checkSites()
                    type==='front'?updateStore({frontIsInit:'false'}):updateStore({backIsInit:'false'});
                }
            );
        }
		
    }

    delSite(siteKey) {
        const { roles:{updateStore}, type , form:{setFieldsValue, getFieldValue}} = this.props
        let {sitesCategories} = this.state;
        let clone = toJS(sitesCategories)
        let siteFormData = getFieldValue(type+ 'sites')
        let categoriesFormData =  getFieldValue(type+ 'categories')
        let newSiteArr = [], newCategoryArr = [] 
		clone.map((site, index) => {
            clone = clone.filter(site => site.key !== siteKey)
            //siteFormData.splice(siteKey,1)
        });
        clone.map((site, index) => {
            site.key = index
            
        });
        siteFormData.map((item,index) => {
            if(index != siteKey) {
                newSiteArr.push(item)
            }
        })
        categoriesFormData.map((item,index) => {
            if(index != siteKey) {
                newCategoryArr.push(item)
            }
        })
        
		this.setState(
            Object.assign({}, { sitesCategories:clone }),
            () => {
                this.checkSites();
                type==='front'?updateStore({frontIsInit:'false'}):updateStore({backIsInit:'false'});
                let sitesForm = type + `sites`;
                let categoryForm = type + 'categories';
                let obj = {};
                obj[sitesForm] = newSiteArr;
                obj[categoryForm] = newCategoryArr;
                setFieldsValue(obj);

            }
        );
        
	}

    render(){
         const {sitesCategories} = this.state
        // let clone = toJS(sitesCategories)
        const {form:{getFieldDecorator},sites} = this.props
        return (
            <FormItem label="站点频道权限"  {...layout}>
                {
                    getFieldDecorator('sitesCategories', {
                        initialValue: sitesCategories,
                    })(
                        <Row className="panel">
                            <Collapse defaultActiveKey={['1']} >
                                <Panel key="1">
                                    {
                                        sitesCategories && sitesCategories.length > 0?(
                                            sitesCategories.map((item,index)=>{
                                                return (
                                                        <Row className='siteRow' gutter={16} key={index}>        
                                                            <Col span={10}>    
                                                                {                           
                                                                    this.getSiteList(item,index)    
                                                                }   
                                                            </Col>
                                                            <Col span={10}>
                                                                {
                                                                    this.getCategoryList(item,index)    
                                                                }
                                                            </Col>    
                                                            {
                                                                index === 0?
                                                                <Col span={4} style={{textAlign:'right'}}>
                                                                    <Button type="primary" onClick={this.addSite.bind(this)} shape="circle" icon="plus"></Button>
                                                                </Col>
                                                                : 
                                                                <Col span={4} style={{textAlign:'right'}}>
                                                                    <Button type="primary" shape="circle" onClick={() => this.delSite(index)} icon="minus"></Button>
                                                                </Col>
                                                            }                                  
                                                    </Row>  
                                                     
                                                )
                                            })
                                        ):(
                                            <Row>
                                                
                                            </Row>     
                                        )
                                    }
                                </Panel>
                            </Collapse>
                        </Row>
                    )
                }
            </FormItem>
        )
    }
}

export default UserSite