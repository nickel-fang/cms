
const FOOTER = {
    text: '©2018'
};
const INDEX_URL = '/cms/articles';
const LOGO_URL = './public/timg.jpeg';
const NAME = '内容管理系统';
const ROUTER_PATHS = {
    DASHBOARD: '/dashboard',
    ARTICLES: '/cms/articles',
    ARTICLES_EDIT: '/cms/articles/edit',
    SOURCE: '/cms/source',
    SOURCE_EDIT: '/cms/source/edit',
    SITE:'/site',
    SITE_EDIT:'/site/edit',
    CATEGORY: '/category',
    TEMPLATE: '/template',
    TEMPLATE_EDIT:'/template/edit',
    BLOCKS:'/blocks',
    BLOCKS_EDIT: '/blocks/edit',
    SYS:'/sys',
    SYS_SETTING:'/sys/setting',
    SYS_SETTING_EDIT: '/sys/setting/edit',
    SYS_MENU:'/sys/menu',
    SYS_MENUS_EDIT:'/sys/menu/edit',
    USERS_ROLE: '/users/role',
    USERS_ROLE_EDIT: '/users/role/edit',
    USER_PERMISSION:'/users/permission',
    USERS_MENU:'/users/menu',
    USERS_MENU_EDIT: '/users/menu/edit',
}
const MENU_TYPES = [
    {
        label: '普通',
        value: 'NORMAL'
    },
    {
        label: '类目数据',
        value: 'CATEGORY'
    }
];
const CMS_STATUS = [
    {
        value: "99",
        label: "全部"
    },
    {
        value: "1",
        label: "草稿"
    },
    {
        value: "2",
        label: "待审核"
    },
    {
        value: "4",
        label: "待修改"
    },
    {
        value: "0",
        label: "已上线"
    },
    {
        value:"6",
        label:"已下线"
    }
]
const TYPES = [
    {
       key:'common',
       label:'新闻'
    },
    {
        key: 'image',
        label: '图集'
    },
    {
        key: 'video',
        label: '视频'
    },
    {
        key: 'audio',
        label: '音频'
    }
]
const PAGE_SIZE = 10;
const CACHE_PREFIX = 'theone-';
const CACHE_PREFIX_LOCAL = 'theone-cms-';
const CACHE_GLOBAL = CACHE_PREFIX + 'global';
const CACHE_USER = CACHE_PREFIX_LOCAL + 'user';
const CACHE_SYSTEMS = CACHE_PREFIX_LOCAL + 'systems';
const CACHE_MENUS = CACHE_PREFIX_LOCAL + 'menus';
const IMG_UPLOAD_TYEPS = ['gif', 'png', 'jpg', 'jpeg'];
const pathname_array = window.location.pathname.split("/");
pathname_array.pop();
const url_path_pre = window.location.host + pathname_array.join("/");
const url_path_full = window.location.protocol + '//' + url_path_pre;
const BASE_URL = url_path_full;
const urlPath = {
    'UPLOAD_FILE': url_path_full + '/api/upload/file',
    'UPLOAD_FILES': url_path_full + '/api/upload/files'
}
module.exports = {
    FOOTER,
    ROUTER_PATHS,
    CACHE_PREFIX,
    CACHE_PREFIX_LOCAL,
    CACHE_GLOBAL,
    CACHE_USER,
    CACHE_MENUS,
    CACHE_SYSTEMS,
    BASE_URL,
    TYPES,
    IMG_UPLOAD_TYEPS,
    urlPath,
    url_path_pre,
    url_path_full,
    PAGE_SIZE,
    INDEX_URL,
    NAME,
    LOGO_URL,
    CMS_STATUS,
    MENU_TYPES
}
