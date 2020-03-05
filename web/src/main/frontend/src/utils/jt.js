const Jt = {};
import lodash from 'lodash';

Jt.toLogin = () => {
    if (location.href.indexOf('redirectUrl') === -1) {
        location.href = '#/login?redirectUrl=' + encodeURIComponent(location.href);
    } else {
        location.reload();
    }
}

Jt.array = {
    isEmpty(arr) {
        return arr === undefined || arr === null || arr.length === 0;
    },
    isArray(arr) {
        return Object.prototype.toString.call(arr) === "[object Array]";
    },
    listToMap(arr = [], key) {
        if (key) {
            const map = {};
            arr.map(item => {
                map[item[key]] = item;
            });
            return map;
        }
        else {
            return arr.map(function (v, i) {
                return { id: v };
            });
        }
    }
};
Jt.tree = {
    format(data = []) {
        for (let i = 0, len = data.length; i < len; i++) {
            data[i].key = data[i].value = data[i].id + '';
            data[i].label = data[i].name;
            if (data[i].children) {
                data[i].children = this.format(data[i].children);
            }
        }
        return data;
    },
    formatChild(data = []) {
        for (let i = 0, len = data.length; i < len; i++) {
            data[i].key = data[i].value = data[i].id + '';
            data[i].label = data[i].name;
            if (data[i].child) {
                data[i].children = this.formatChild(data[i].child);
                delete data[i].child;
            }
        }
        return data;
    },
    getFirstUrl(data=[], parentPath='/'){
        let path = parentPath;
        if(!!data.length){
            if(data[0].child){
                path = path + data[0].code;
                return this.getFirstUrl(data[0].child, path+'/');
            }else{
                path = path + data[0].code;
                return path;
            }
        }
        return path;
    },
    isParentGetLeafIds(data = [], id) {
        for (let i = 0, len = data.length; i < len; i++) {
            if (data[i].id == id) {
                if (data[i].children) {
                    const children = data[i].children;
                    let arr = [];
                    for (let j = 0, length = children.length; j < length;j++){
                        arr.push((children[j].id+''));
                        const flag = this.isParentGetLeafIds(children, children[j].id);
                        if(flag){
                            arr = [...arr,...flag];
                        }
                    }
                    arr.unshift((id + ''));
                    return arr;
                }
                return false;
            }
            else if (data[i].children) {
                const flag = this.isParentGetLeafIds(data[i].children, id);
                if(flag){
                    return flag
                }
            }
        }
    },
    getNode(data, id) {
        for (let i = 0, len = data.length; i < len; i++) {
            if(data[i].id==id){
                return data[i];
            }
            if(data[i].children){
                const node = this.getNode(data[i].children,id);
                if(node){
                    return node;
                }
            }
        }
    },
    getAllChildren(data,id){
        const children = [];
        const node = this.getNode(data,id);
        if(node.children){
            for (let i = 0, len = node.children.length; i < len; i++) {
                children.push(node.children[i].id+'')
            }
        }
        return children;
    }
}
Jt.loader = {
    fileIsExist(tag, url) {
        let exist = false;
        const files = document.getElementsByTagName(tag);
        const type = tag === 'script' ? 'src' : 'href';
        for (let i = 0, len = files.length; i < len; i++) {
            if (files[i].getAttribute(type) === url) {
                exist = true;
                break;
            }
        }
        return exist;
    },

    removeExistFiles(tag, urls) {
        const files = document.getElementsByTagName(tag);
        const type = tag === 'script' ? 'src' : 'href';
        const paths = [];
        for (let i = 0, len = files.length; i < len; i++) {
            paths.push(files[i].getAttribute(type));
        }
        return lodash.difference(urls, paths);
    },

    loadScripts(scripts, callback, parent = 'head') {
        scripts = Jt.loader.removeExistFiles('script', scripts);
        if (scripts.length === 0) {
            callback && callback();
            return;
        }
        parent = document.getElementsByTagName(parent)[0] || document.documentElement;
        let loaded = 0;
        for (let i = 0, len = scripts.length; i < len; i++) {
            let node = document.createElement('script');
            node.onload = node.onreadystatechange = function () {
                const rs = this.readyState;
                if ('undefined' === typeof rs || 'loaded' === rs || 'complete' === rs) {
                    loaded++;
                    this.onload = this.onreadystatechange = null;
                    node = null;
                    if (loaded === scripts.length) {
                        callback && callback();

                    }
                }
            }
            node.src = scripts[i];
            parent.appendChild(node);
        }
    }
}

module.exports = exports = Jt;
