webpackJsonp([2],{"+vu8":function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var n,r=w(a("IidI")),i=w(a("kr0b")),l=w(a("aCj6")),o=w(a("Dd8w")),s=w(a("uuhB")),u=w(a("Zx67")),d=w(a("Zrlr")),c=w(a("wxAW")),f=w(a("zwoO")),p=w(a("Pf15")),h=w(a("NJOH")),m=w(a("37+n")),v=w(a("afSG"));a("1uA9"),a("2xSi"),a("kJGR"),a("TZP0"),a("mKHw"),a("8QNH");var g=a("GiK3"),y=w(g),b=a("Mn8c"),k=a("y986"),E=a("LkMq"),S=(a("SFkB"),w(a("mw3O"))),C=w(a("Q15a"));function w(e){return e&&e.__esModule?e:{default:e}}var O=m.default.Item,I=(h.default.Option,{labelCol:{span:4},wrapperCol:{span:12}}),_=(0,b.inject)("app")(n=(0,b.inject)("roles")(n=(0,b.observer)(n=function(e){function t(e){(0,d.default)(this,t);var a=(0,f.default)(this,(t.__proto__||(0,u.default)(t)).call(this,e));a.onSave=function(){var e=a.props,t=e.roles,n=t.role,r=t.saveRole,i=e.form,l=i.validateFields,o=i.getFieldsValue;l(function(e,t){var i=o(),l=a.formatSitesCategories(i);if(!e){(0,v.default)(t.menuIds,function(e){return 0===e.indexOf("system-")}),t.id=n.id,t.frontSC=l.frontSC,t.backSC=l.backSC,delete t.frontsites,delete t.frontcategories,delete t.backsites,delete t.backcategories,delete t.sitesCategories;r(t,function(){a.props.history.push(E.ROUTER_PATHS.USERS_ROLE)})}})};var n=e.location.search,r=e.roles,i=r.getRole,l=r.updateStore,o=r.getMenus,s=r.getSites,c=(r.categoryList,S.default.parse(n.substr(1)));return c.id?i(c.id):(l({role:{},frontIsInit:"true",backIsInit:"true"}),o()),s(),a}return(0,p.default)(t,e),(0,c.default)(t,[{key:"componentWillUnmount",value:function(){this.props.roles.reset()}},{key:"goBack",value:function(){this.props.history.goBack()}},{key:"formatSitesCategories",value:function(e){var t=e.frontsites,a=e.frontcategories,n=e.backsites,r=e.backcategories,i=[],l=[];return t.length>0&&t.map(function(e,a){t=t.filter(function(e){return"notSet"!==e})}),t&&t.length>0&&t.map(function(e,t){e&&a.length>0&&i.push({siteId:e,categoryIds:a[t].join()})}),n.length>0&&n.map(function(e,t){n=n.filter(function(e){return"notSet"!==e})}),n&&n.length>0&&n.map(function(e,t){e&&r.length>0&&l.push({siteId:e,categoryIds:r[t].join()})}),{frontSC:i,backSC:l}}},{key:"render",value:function(){var e=this,t=this.props,a=t.roles,n=a.menus,u=a.role,d=a.backIsInit,c=a.frontIsInit,f=t.form,p=f.getFieldDecorator,h={form:{getFieldDecorator:p,setFieldsValue:f.setFieldsValue,getFieldValue:f.getFieldValue}};return y.default.createElement(m.default,null,y.default.createElement(O,(0,o.default)({label:"角色名称"},I,{hasFeedback:!0}),p("name",{initialValue:u.name,rules:[{required:!0,message:"请输入角色名称"}]})(y.default.createElement(s.default,null))),y.default.createElement(l.default,{title:"前台权限配置",bordered:!1,style:{width:"100%"}},y.default.createElement(C.default,(0,o.default)({},h,{type:"front",sitesCategories:u.frontSC,isInit:c}))),y.default.createElement(l.default,{title:"后台权限配置",bordered:!1,style:{width:"100%"}},y.default.createElement(O,(0,o.default)({label:"角色授权"},I,{hasFeedback:!0}),p("menuIds",{initialValue:u.menuIds&&u.menuIds.length>0?u.menuIds.map(function(e){return e+""}):void 0,rules:[{required:!0,message:"请选择角色授权"}]})(y.default.createElement(i.default,{allowClear:!0,showSearch:!0,showCheckedStrategy:i.default.SHOW_ALL,treeNodeFilterProp:"label",dropdownStyle:{maxHeight:300,overflow:"auto"},treeData:(0,k.toJS)(n),multiple:!0,treeCheckable:!0}))),y.default.createElement(C.default,(0,o.default)({},h,{type:"back",sitesCategories:u.backSC,isInit:d}))),y.default.createElement(O,(0,o.default)({label:"备注"},I),p("remark",{initialValue:u.remark})(y.default.createElement(s.default,{type:"textarea",rows:5}))),y.default.createElement(m.default.Item,{wrapperCol:{span:I.wrapperCol.span,offset:I.labelCol.span}},y.default.createElement(r.default,{type:"primary",style:{marginRight:20},onClick:this.onSave},"保存"),y.default.createElement(r.default,{onClick:function(){return e.goBack()}},"返回")))}}]),t}(g.Component))||n)||n)||n;t.default=m.default.create()(_)},"4O1h":function(e,t,a){a("jEnx"),e.exports=a("FeBl").Object.is},"636j":function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var n=l(a("Dd8w")),r=function(e){if(e&&e.__esModule)return e;var t={};if(null!=e)for(var a in e)Object.prototype.hasOwnProperty.call(e,a)&&(t[a]=e[a]);return t.default=e,t}(a("GiK3")),i=l(a("HW6M"));function l(e){return e&&e.__esModule?e:{default:e}}var o=function(e,t){var a={};for(var n in e)Object.prototype.hasOwnProperty.call(e,n)&&t.indexOf(n)<0&&(a[n]=e[n]);if(null!=e&&"function"==typeof Object.getOwnPropertySymbols){var r=0;for(n=Object.getOwnPropertySymbols(e);r<n.length;r++)t.indexOf(n[r])<0&&(a[n[r]]=e[n[r]])}return a};t.default=function(e){var t=e.prefixCls,a=void 0===t?"ant-card":t,l=e.className,s=e.avatar,u=e.title,d=e.description,c=o(e,["prefixCls","className","avatar","title","description"]),f=(0,i.default)(a+"-meta",l),p=s?r.createElement("div",{className:a+"-meta-avatar"},s):null,h=u?r.createElement("div",{className:a+"-meta-title"},u):null,m=d?r.createElement("div",{className:a+"-meta-description"},d):null,v=h||m?r.createElement("div",{className:a+"-meta-detail"},h,m):null;return r.createElement("div",(0,n.default)({},c,{className:f}),p,v)},e.exports=t.default},"CmP/":function(e,t){e.exports=Object.is||function(e,t){return e===t?0!==e||1/e==1/t:e!=e&&t!=t}},E508:function(e,t,a){var n=a("Vi3P"),r=a("ZGh9"),i=Array.prototype.splice;e.exports=function(e,t){for(var a=e?t.length:0,l=a-1;a--;){var o=t[a];if(a==l||o!==s){var s=o;r(o)?i.call(e,o,1):n(e,o)}}return e}},"Ll+M":function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var n,r=g(a("Zx67")),i=g(a("Zrlr")),l=g(a("wxAW")),o=g(a("zwoO")),s=g(a("Pf15")),u=a("GiK3"),d=g(u),c=a("Mn8c"),f=(a("y986"),a("F8kA")),p=a("LkMq"),h=g(a("h7W9")),m=g(a("+vu8")),v=g(a("z4MD"));function g(e){return e&&e.__esModule?e:{default:e}}a("WSjw");var y=(0,c.inject)("app")(n=(0,c.inject)("roles")(n=(0,c.observer)(n=function(e){function t(e){return(0,i.default)(this,t),(0,o.default)(this,(t.__proto__||(0,r.default)(t)).call(this,e))}return(0,s.default)(t,e),(0,l.default)(t,[{key:"render",value:function(){return d.default.createElement("div",{className:"p-common"},d.default.createElement(f.Switch,null,d.default.createElement(f.Route,{exact:!0,path:p.ROUTER_PATHS.USERS_ROLE,component:h.default}),d.default.createElement(f.Route,{exact:!0,path:p.ROUTER_PATHS.USERS_ROLE_EDIT,component:m.default}),d.default.createElement(f.Route,{component:v.default})))}}]),t}(u.Component))||n)||n)||n;t.default=(0,f.withRouter)(y)},Q15a:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var n,r=O(a("Dd8w")),i=O(a("0b0c")),l=O(a("IidI")),o=O(a("G3dI")),s=O(a("1cZb")),u=O(a("kr0b")),d=O(a("woOf")),c=O(a("g4PW")),f=O(a("Zx67")),p=O(a("Zrlr")),h=O(a("wxAW")),m=O(a("zwoO")),v=O(a("Pf15")),g=O(a("VCGi")),y=O(a("NJOH")),b=O(a("37+n")),k=O(a("kvU2"));a("kxBb"),a("1uA9"),a("/Hhv"),a("YaoS"),a("2xSi"),a("6opi"),a("mKHw"),a("8QNH");var E=O(a("GiK3")),S=a("Mn8c"),C=a("SFkB"),w=a("y986");function O(e){return e&&e.__esModule?e:{default:e}}var I=b.default.Item,_=y.default.Option,P={labelCol:{span:4},wrapperCol:{span:12}},x=g.default.Panel,j=(0,S.inject)("app")(n=(0,S.inject)("roles")(n=(0,S.observer)(n=function(e){function t(e){(0,p.default)(this,t);var a=(0,m.default)(this,(t.__proto__||(0,f.default)(t)).call(this,e)),n=e.roles.categoryList,r=(e.app,e.roles),i=(0,w.toJS)(r.sites),l=(0,k.default)(i)||[],o=[{key:0,siteId:void 0,categoryIds:[]}];return a.state={sites:l,sitesCategories:o,categoryList:n||{},treeData:[],isInit:e.isInit||"true"},a}return(0,v.default)(t,e),(0,h.default)(t,[{key:"componentDidMount",value:function(){this.checkSites()}},{key:"componentWillReceiveProps",value:function(e){var t=e.roles,a=e.isInit||this.state.isInit||"true",n=this.state.sitesCategories||[{key:0,siteId:void 0,categoryIds:[]}],r=e.sitesCategories||[{key:0,siteId:void 0,categoryIds:[]}],i=t.role.id||"",l=(0,w.toJS)(t.sites),o="true"===a&&!(0,c.default)(r,n)&&i?r:n,s=!(this.state.sites.length>0)?(0,k.default)(l):this.state.sites,u=(0,c.default)(t.categoryList,this.state.treeData)?this.state.treeData:(0,k.default)(t.categoryList);s&&s.length>0&&s.map(function(e){e.id=e.id+"",o&&o.length>0&&o.map(function(t){e.id==t.siteId&&(e.disabled=!0)})}),o&&o.length>0&&o[0].siteId&&(o.map(function(e,t){e.key=t;e.siteId;"string"==typeof e.categoryIds?e.categoryIds=e.categoryIds.split(","):e.categoryIds=e.categoryIds}),a="false"),this.setState((0,d.default)({},{sitesCategories:o,treeData:u,isInit:a,sites:s}))}},{key:"checkSites",value:function(){var e=this.state,t=e.sites,a=e.sitesCategories;t&&t.length>0&&t.map(function(e,t){e.disabled=!1,a&&a.length>0&&a.map(function(t,a){e.id==t.siteId&&(e.disabled=!0)})}),this.setState((0,d.default)({},{sites:t}))}},{key:"getSiteList",value:function(e,t){var a=this,n=this.props,r=n.form,i=r.getFieldDecorator,l=(r.setFieldsValue,n.type),o=this.state,s=o.sites;o.sitesCategories;return e.siteId||(e.siteId=void 0),E.default.createElement(I,{key:t},i(l+"sites["+t+"]",{initialValue:e.siteId?e.siteId+"":void 0})(E.default.createElement(y.default,{placeholder:"请选择站点权限",onSelect:function(e){return a.selectSite(e,t)}},E.default.createElement(_,{value:"notSet",key:"notSet"},"未选择"),s.length>0?s.map(function(e,t){return E.default.createElement(_,{disabled:e.disabled,value:e.id+"",key:e.id},e.name)}):null)))}},{key:"getCategoryList",value:function(e,t){var a=this,n=this.props,r=n.form.getFieldDecorator,i=n.type,l=this.state.treeData;return e.categoryIds||(e.categoryIds=[]),E.default.createElement(I,{key:t},r(i+"categories["+t+"]",{initialValue:(0,w.toJS)(e.categoryIds)})(E.default.createElement(u.default,{allowClear:!0,showSearch:!0,showCheckedStrategy:u.default.SHOW_ALL,treeNodeFilterProp:"label",dropdownStyle:{maxHeight:300,overflow:"auto"},treeData:C.Jt.tree.format(l[e.siteId]),multiple:!0,treeCheckable:!0,onChange:function(t){return a.changeCategory(t,e.siteId)},placeholder:"请选择频道权限"})))}},{key:"getCategoryData",value:function(e){var t=this.state,a=t.treeData,n=t.categoryList;e&&n&&(a[e]=C.Jt.tree.format(n[e]),this.setState((0,d.default)({},{treeData:a})))}},{key:"changeCategory",value:function(e,t){var a=this.state.sitesCategories;a&&a.length>0&&a.map(function(a,n){a.siteId==t&&(a.categoryIds=e)}),this.setState((0,d.default)({},{sitesCategories:a}))}},{key:"selectSite",value:function(e,t){var a=this,n=this.props,r=n.roles.updateStore,i=n.form.setFieldsValue,l=n.type,o=this.state.sitesCategories;o&&o.length>0&&(o.map(function(a,n){if(a.key==t){a.siteId=e,a.categoryIds=[];var r={};r[l+"categories["+n+"]"]=[],i(r)}}),o.map(function(e,t){e.key=t})),r("front"===l?{frontIsInit:"false"}:{backIsInit:"false"}),this.setState((0,d.default)({},{sitesCategories:o,isInit:"false"}),function(){a.checkSites(),a.getCategoryData(e)})}},{key:"addSite",value:function(){var e=this,t=this.props,a=t.roles.updateStore,n=t.type,r=this.state,i=r.sites,l=r.sitesCategories,o=(0,w.toJS)(l);i.length<=l.length?s.default.info("已无可加站点"):(o.push({key:l.length,siteId:"",categoryIds:""}),o.map(function(e,t){e.key=t}),this.setState((0,d.default)({},{sitesCategories:o,isInit:"false"}),function(){e.checkSites(),a("front"===n?{frontIsInit:"false"}:{backIsInit:"false"})}))}},{key:"delSite",value:function(e){var t=this,a=this.props,n=a.roles.updateStore,r=a.type,i=a.form,l=i.setFieldsValue,o=i.getFieldValue,s=this.state.sitesCategories,u=(0,w.toJS)(s),c=o(r+"sites"),f=o(r+"categories"),p=[],h=[];u.map(function(t,a){u=u.filter(function(t){return t.key!==e})}),u.map(function(e,t){e.key=t}),c.map(function(t,a){a!=e&&p.push(t)}),f.map(function(t,a){a!=e&&h.push(t)}),this.setState((0,d.default)({},{sitesCategories:u}),function(){t.checkSites(),n("front"===r?{frontIsInit:"false"}:{backIsInit:"false"});var e=r+"categories",a={};a[r+"sites"]=p,a[e]=h,l(a)})}},{key:"render",value:function(){var e=this,t=this.state.sitesCategories,a=this.props,n=a.form.getFieldDecorator;a.sites;return E.default.createElement(I,(0,r.default)({label:"站点频道权限"},P),n("sitesCategories",{initialValue:t})(E.default.createElement(i.default,{className:"panel"},E.default.createElement(g.default,{defaultActiveKey:["1"]},E.default.createElement(x,{key:"1"},t&&t.length>0?t.map(function(t,a){return E.default.createElement(i.default,{className:"siteRow",gutter:16,key:a},E.default.createElement(o.default,{span:10},e.getSiteList(t,a)),E.default.createElement(o.default,{span:10},e.getCategoryList(t,a)),0===a?E.default.createElement(o.default,{span:4,style:{textAlign:"right"}},E.default.createElement(l.default,{type:"primary",onClick:e.addSite.bind(e),shape:"circle",icon:"plus"})):E.default.createElement(o.default,{span:4,style:{textAlign:"right"}},E.default.createElement(l.default,{type:"primary",shape:"circle",onClick:function(){return e.delSite(a)},icon:"minus"})))}):E.default.createElement(i.default,null))))))}}]),t}(E.default.Component))||n)||n)||n;t.default=j},WSjw:function(e,t){},aCj6:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var n=b(a("Dd8w")),r=b(a("bOdI")),i=b(a("Zrlr")),l=b(a("wxAW")),o=b(a("zwoO")),s=b(a("Pf15")),u=b(a("pFYg")),d=function(e){if(e&&e.__esModule)return e;var t={};if(null!=e)for(var a in e)Object.prototype.hasOwnProperty.call(e,a)&&(t[a]=e[a]);return t.default=e,t}(a("GiK3")),c=b(a("HW6M")),f=b(a("SQfk")),p=b(a("JkBm")),h=b(a("jwyZ")),m=b(a("636j")),v=b(a("qB1w")),g=a("mLGO"),y=b(a("/lIq"));function b(e){return e&&e.__esModule?e:{default:e}}var k=function(e,t,a,n){var r,i=arguments.length,l=i<3?t:null===n?n=Object.getOwnPropertyDescriptor(t,a):n;if("object"===("undefined"==typeof Reflect?"undefined":(0,u.default)(Reflect))&&"function"==typeof Reflect.decorate)l=Reflect.decorate(e,t,a,n);else for(var o=e.length-1;o>=0;o--)(r=e[o])&&(l=(i<3?r(l):i>3?r(t,a,l):r(t,a))||l);return i>3&&l&&Object.defineProperty(t,a,l),l},E=function(e,t){var a={};for(var n in e)Object.prototype.hasOwnProperty.call(e,n)&&t.indexOf(n)<0&&(a[n]=e[n]);if(null!=e&&"function"==typeof Object.getOwnPropertySymbols){var r=0;for(n=Object.getOwnPropertySymbols(e);r<n.length;r++)t.indexOf(n[r])<0&&(a[n[r]]=e[n[r]])}return a},S=function(e){function t(){(0,i.default)(this,t);var e=(0,o.default)(this,(t.__proto__||Object.getPrototypeOf(t)).apply(this,arguments));return e.state={widerPadding:!1},e.onTabChange=function(t){e.props.onTabChange&&e.props.onTabChange(t)},e.saveRef=function(t){e.container=t},e}return(0,s.default)(t,e),(0,l.default)(t,[{key:"componentDidMount",value:function(){this.updateWiderPadding(),this.resizeEvent=(0,f.default)(window,"resize",this.updateWiderPadding),"noHovering"in this.props&&((0,y.default)(!this.props.noHovering,"`noHovering` of Card is deperated, you can remove it safely or use `hoverable` instead."),(0,y.default)(!!this.props.noHovering,"`noHovering={false}` of Card is deperated, use `hoverable` instead."))}},{key:"componentWillUnmount",value:function(){this.resizeEvent&&this.resizeEvent.remove(),this.updateWiderPadding.cancel()}},{key:"updateWiderPadding",value:function(){var e=this;if(this.container){this.container.offsetWidth>=936&&!this.state.widerPadding&&this.setState({widerPadding:!0},function(){e.updateWiderPaddingCalled=!0}),this.container.offsetWidth<936&&this.state.widerPadding&&this.setState({widerPadding:!1},function(){e.updateWiderPaddingCalled=!0})}}},{key:"isContainGrid",value:function(){var e=void 0;return d.Children.forEach(this.props.children,function(t){t&&t.type&&t.type===h.default&&(e=!0)}),e}},{key:"getAction",value:function(e){return e&&e.length?e.map(function(t,a){return d.createElement("li",{style:{width:100/e.length+"%"},key:"action-"+a},d.createElement("span",null,t))}):null}},{key:"getCompatibleHoverable",value:function(){var e=this.props,t=e.noHovering,a=e.hoverable;return"noHovering"in this.props?!t||a:!!a}},{key:"render",value:function(){var e,t=this.props,a=t.prefixCls,i=void 0===a?"ant-card":a,l=t.className,o=t.extra,s=t.bodyStyle,u=(t.noHovering,t.hoverable,t.title),f=t.loading,h=t.bordered,m=void 0===h||h,g=t.type,y=t.cover,b=t.actions,k=t.tabList,S=t.children,C=t.activeTabKey,w=t.defaultActiveTabKey,O=E(t,["prefixCls","className","extra","bodyStyle","noHovering","hoverable","title","loading","bordered","type","cover","actions","tabList","children","activeTabKey","defaultActiveTabKey"]),I=(0,c.default)(i,l,(e={},(0,r.default)(e,i+"-loading",f),(0,r.default)(e,i+"-bordered",m),(0,r.default)(e,i+"-hoverable",this.getCompatibleHoverable()),(0,r.default)(e,i+"-wider-padding",this.state.widerPadding),(0,r.default)(e,i+"-padding-transition",this.updateWiderPaddingCalled),(0,r.default)(e,i+"-contain-grid",this.isContainGrid()),(0,r.default)(e,i+"-contain-tabs",k&&k.length),(0,r.default)(e,i+"-type-"+g,!!g),e)),_=d.createElement("div",{className:i+"-loading-content"},d.createElement("p",{className:i+"-loading-block",style:{width:"94%"}}),d.createElement("p",null,d.createElement("span",{className:i+"-loading-block",style:{width:"28%"}}),d.createElement("span",{className:i+"-loading-block",style:{width:"62%"}})),d.createElement("p",null,d.createElement("span",{className:i+"-loading-block",style:{width:"22%"}}),d.createElement("span",{className:i+"-loading-block",style:{width:"66%"}})),d.createElement("p",null,d.createElement("span",{className:i+"-loading-block",style:{width:"56%"}}),d.createElement("span",{className:i+"-loading-block",style:{width:"39%"}})),d.createElement("p",null,d.createElement("span",{className:i+"-loading-block",style:{width:"21%"}}),d.createElement("span",{className:i+"-loading-block",style:{width:"15%"}}),d.createElement("span",{className:i+"-loading-block",style:{width:"40%"}}))),P=void 0!==C,x=(0,r.default)({},P?"activeKey":"defaultActiveKey",P?C:w),j=void 0,R=k&&k.length?d.createElement(v.default,(0,n.default)({},x,{className:i+"-head-tabs",size:"large",onChange:this.onTabChange}),k.map(function(e){return d.createElement(v.default.TabPane,{tab:e.tab,key:e.key})})):null;(u||o||R)&&(j=d.createElement("div",{className:i+"-head"},d.createElement("div",{className:i+"-head-wrapper"},u&&d.createElement("div",{className:i+"-head-title"},u),o&&d.createElement("div",{className:i+"-extra"},o)),R));var N=y?d.createElement("div",{className:i+"-cover"},y):null,M=d.createElement("div",{className:i+"-body",style:s},f?_:S),F=b&&b.length?d.createElement("ul",{className:i+"-actions"},this.getAction(b)):null,A=(0,p.default)(O,["onTabChange"]);return d.createElement("div",(0,n.default)({},A,{className:I,ref:this.saveRef}),j,N,M,F)}}]),t}(d.Component);t.default=S,S.Grid=h.default,S.Meta=m.default,k([(0,g.throttleByAnimationFrameDecorator)()],S.prototype,"updateWiderPadding",null),e.exports=t.default},afSG:function(e,t,a){var n=a("JyYQ"),r=a("E508");e.exports=function(e,t){var a=[];if(!e||!e.length)return a;var i=-1,l=[],o=e.length;for(t=n(t,3);++i<o;){var s=e[i];t(s,i,e)&&(a.push(s),l.push(i))}return r(e,l),a}},g4PW:function(e,t,a){e.exports={default:a("4O1h"),__esModule:!0}},h7W9:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var n,r=b(a("yF52")),i=b(a("LoBt")),l=b(a("wgAv")),o=b(a("IidI")),s=b(a("Zx67")),u=b(a("Zrlr")),d=b(a("wxAW")),c=b(a("zwoO")),f=b(a("Pf15"));a("tNTX"),a("/eDY"),a("sF8O"),a("1uA9");var p=a("GiK3"),h=b(p),m=a("Mn8c"),v=a("y986"),g=a("LkMq"),y=(a("SFkB"),b(a("mw3O")));function b(e){return e&&e.__esModule?e:{default:e}}var k=(0,m.inject)("app")(n=(0,m.inject)("roles")(n=(0,m.observer)(n=function(e){function t(e){(0,u.default)(this,t);var a=(0,c.default)(this,(t.__proto__||(0,s.default)(t)).call(this,e));a.titleRender=function(){return h.default.createElement("div",{style:{textAlign:"right"}},h.default.createElement(o.default,{type:"primary",onClick:function(){return a.addRole()}},"添加角色"))},a.getColumns=function(){var e=a.props;e.editRole,e.deleteRole;return[{title:"角色名称",dataIndex:"name",width:200},{title:"备注",dataIndex:"remark",width:200},{title:"操作",dataIndex:"action",width:200,className:"action-col",render:function(e,t){return[h.default.createElement("a",{key:"edit",onClick:function(){return a.editRole(t.id)}},"修改"),h.default.createElement(l.default,{key:"divider",type:"vertical"}),h.default.createElement(i.default,{key:"delete",title:"确定要删除吗？",onConfirm:function(){return a.deleteRole(t.id,a)}},h.default.createElement("a",null,"删除"))]}}]};var n=e.roles;e.app.selectSiteId;return n.getInit(),a}return(0,f.default)(t,e),(0,d.default)(t,[{key:"addRole",value:function(){this.props.history.push(g.ROUTER_PATHS.USERS_ROLE_EDIT)}},{key:"editRole",value:function(e){this.props.history.push({pathname:g.ROUTER_PATHS.USERS_ROLE_EDIT,search:"?"+y.default.stringify({id:e})})}},{key:"deleteRole",value:function(e){console.log(e);var t=this.props.roles,a=t.deleteRoleItem,n=t.getInit;a(e,function(){n()})}},{key:"render",value:function(){var e=this.props.roles,t=e.list,a=e.loading,n=e.pagination,i=e.onPageChange;return h.default.createElement(r.default,{simple:!0,bordered:!0,loading:a,columns:this.getColumns(),rowKey:"id",dataSource:(0,v.toJS)(t),pagination:n,title:this.titleRender,onChange:function(e){return i(e)}})}}]),t}(p.Component))||n)||n)||n;t.default=k},jEnx:function(e,t,a){var n=a("kM2E");n(n.S,"Object",{is:a("CmP/")})},jwyZ:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var n=l(a("Dd8w")),r=function(e){if(e&&e.__esModule)return e;var t={};if(null!=e)for(var a in e)Object.prototype.hasOwnProperty.call(e,a)&&(t[a]=e[a]);return t.default=e,t}(a("GiK3")),i=l(a("HW6M"));function l(e){return e&&e.__esModule?e:{default:e}}var o=function(e,t){var a={};for(var n in e)Object.prototype.hasOwnProperty.call(e,n)&&t.indexOf(n)<0&&(a[n]=e[n]);if(null!=e&&"function"==typeof Object.getOwnPropertySymbols){var r=0;for(n=Object.getOwnPropertySymbols(e);r<n.length;r++)t.indexOf(n[r])<0&&(a[n[r]]=e[n[r]])}return a};t.default=function(e){var t=e.prefixCls,a=void 0===t?"ant-card":t,l=e.className,s=o(e,["prefixCls","className"]),u=(0,i.default)(a+"-grid",l);return r.createElement("div",(0,n.default)({},s,{className:u}))},e.exports=t.default},kJGR:function(e,t,a){"use strict";a("QhmJ"),a("u0e4"),a("FV/t")},kvU2:function(e,t,a){var n=a("Fkvj"),r=1,i=4;e.exports=function(e){return n(e,r|i)}},mLGO:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var n=i(a("Gu7T"));t.default=o,t.throttleByAnimationFrameDecorator=function(){return function(e,t,a){var n=a.value,r=!1;return{configurable:!0,get:function(){if(r||this===e.prototype||this.hasOwnProperty(t))return n;var a=o(n.bind(this));return r=!0,Object.defineProperty(this,t,{value:a,configurable:!0,writable:!0}),r=!1,a}}}};var r=a("62CO");function i(e){return e&&e.__esModule?e:{default:e}}var l=(0,i(r).default)();function o(e){var t=void 0,a=function(){for(var a=arguments.length,r=Array(a),i=0;i<a;i++)r[i]=arguments[i];null==t&&(t=l(function(a){return function(){t=null,e.apply(void 0,(0,n.default)(a))}}(r)))};return a.cancel=function(){return(0,r.cancelRequestAnimationFrame)(t)},a}},s2WY:function(e,t){},sF8O:function(e,t,a){"use strict";a("QhmJ"),a("s2WY")},u0e4:function(e,t){},wgAv:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var n=o(a("Dd8w")),r=o(a("bOdI"));t.default=function(e){var t,a=e.prefixCls,o=void 0===a?"ant":a,u=e.type,d=void 0===u?"horizontal":u,c=e.orientation,f=void 0===c?"":c,p=e.className,h=e.children,m=e.dashed,v=s(e,["prefixCls","type","orientation","className","children","dashed"]),g=f.length>0?"-"+f:f,y=(0,l.default)(p,o+"-divider",o+"-divider-"+d,(t={},(0,r.default)(t,o+"-divider-with-text"+g,h),(0,r.default)(t,o+"-divider-dashed",!!m),t));return i.createElement("div",(0,n.default)({className:y},v),h&&i.createElement("span",{className:o+"-divider-inner-text"},h))};var i=function(e){if(e&&e.__esModule)return e;var t={};if(null!=e)for(var a in e)Object.prototype.hasOwnProperty.call(e,a)&&(t[a]=e[a]);return t.default=e,t}(a("GiK3")),l=o(a("HW6M"));function o(e){return e&&e.__esModule?e:{default:e}}var s=function(e,t){var a={};for(var n in e)Object.prototype.hasOwnProperty.call(e,n)&&t.indexOf(n)<0&&(a[n]=e[n]);if(null!=e&&"function"==typeof Object.getOwnPropertySymbols){var r=0;for(n=Object.getOwnPropertySymbols(e);r<n.length;r++)t.indexOf(n[r])<0&&(a[n[r]]=e[n[r]])}return a};e.exports=t.default}});