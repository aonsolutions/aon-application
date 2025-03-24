import { CONSTANT, MSG, TAG } from '../../../environments/environments.js';
import { AonPackage } from './aon-package.js';
import { getItems } from '../../../services/productService.js';
import { AonList } from '../../../components/aon-list.js';

export class AonPackageList extends AonList {

    packages;

    constructor () {
        super();
    }

    initialize() {
        this.id = this.id || 'aonPackageList';
		this.TABLE = this.id + CONSTANT.TABLE.initCap();
        this.filter = this.filter || {
            page:1,
            perPage:30,
            productType: 'AUXILIARY',
            active: true
        }
        this.more = this.packages ? false : true;
        this.columns = [{
                name: MSG.CODE,
                type: 'string',
                id: 'code',
                width: '120px'
            }, {
                name: MSG.NAME,
                type: 'string',
                id: 'name',
                width: 'auto'
            }, {
                name: 'SSCC',
                type: 'string',
                id: 'serialNumber',
                width: '200px'
            }];
    }

    aonObject(object, i) {
        // let aonPackage = new AonPackage();
        // aonPackage.setPackage(object);
        // this.getApplication().setContent(aonPackage);
    }

    getObjects() {
        return this.packages 
            ? new Promise((resolve, reject) => resolve(this.packages))
            : getItems(this.getFilter());
    }

    getPackages() {
        return this.packages;
    }

    setPackages(packages) {
        this.packages = packages;
    }
}

if(!window.customElements.get(TAG.AON_PACKAGE_LIST)) {
    window.customElements.define(TAG.AON_PACKAGE_LIST, AonPackageList);
}