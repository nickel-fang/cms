import WebStorageCache from 'web-storage-cache';

const localCache = new WebStorageCache({
    storage: 'localStorage'
});

const sessionCache = new WebStorageCache({
    storage: 'sessionStorage'
});

export default {
    localCache,
    sessionCache
}
