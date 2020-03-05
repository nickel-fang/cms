import { observable, action, extendObservable } from 'mobx';

const defaults = {
    fold_sidebar: false,
    activeTabs: []
}

class Layout {
    constructor(){
        extendObservable(this, {
            ...defaults
        });
    }
    @action.bound updateStore(data) {
        Object.assign(this, data);
    }
    @action.bound reset() {
        for (let i in defaults) {
            this[i] = defaults[i];
        }
    }
}

export default Layout;
