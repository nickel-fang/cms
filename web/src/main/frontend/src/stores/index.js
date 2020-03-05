import Layout from './layout';
import App from './app';
import Site from './site';
import Cateogry from './category';
import Cms from './cms';
import Template from './template';
import Blocks from './blocks';
import Source from './source';
import Sys from './sys';
import Roles from './roles'
import Permissions from './permission'

const layout = new Layout();
const app = new App();
const site = new Site();
const category = new Cateogry();
const cms = new Cms();
const template = new Template();
const blocks = new Blocks();
const source = new Source();
const sys = new Sys();
const roles = new Roles();
const permissions = new Permissions() 

export default {
    layout,
    app,
    site,
    category,
    cms,
    template,
    blocks,
    source,
    sys,
    roles,
    permissions
};
