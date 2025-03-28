import { AonMobileList } from '../../../components/aon-mobile-list.js';
import { EVENT, MATERIAL_ICONS, TAG } from '../../../environments/environments.js';
import { getItems } from '../../../services/productService.js';
import { AonMobilePackage } from './aon-mobile-package.js';
import { addPackages, initializePackages, setIndex, setPackages } from '../package/PackagesCache.js';

export class AonMobilePackageList extends AonMobileList {

    more;
    filter;
    packages;
    TOOLBAR;

    constructor () {
        super();
    }

    connectedCallback () {
        this.initialize();
        this.init();
        if(!this.packages)
            this.addEventListener(EVENT.MORE, this.moreFn);
        this.buildSearch()
    }

    moreFn = () => {
        if(this.more)
            this.loadMore()
    };

    initialize() {
        this.more = this.packages ? false : true;
        this.filter = this.filter || {
            page:1,
            perPage:30,
            productType: 'AUXILIARY',
            active: true
        }
    }

    buildSearch(){
		const btnSearch = this.getApplication().addSearchOption();
		let searchFn = (event) => this.search(event.detail);
		btnSearch.addEventListener(EVENT.SEARCH_NEW, searchFn);
    }

    search(detail) {
        let value = detail.search;
        this.init(value);
	}

    loadMore() {
        this.more = false;
        if(!this.packages && this.filter.page) {
            this.filter.page = this.filter.page + 1;
            getItems(this.filter).then(items => {
                addPackages(items);
                this.more = items.length > 0;
                items.forEach((item, i) => this.addItemRow(item, i));
            });
        }
    }

    init(searchValue) {
        this.build();
        if(!this.packages) {
            initializePackages();
            if(searchValue) this.filter.value = searchValue;
            getItems(this.filter).then(items => {
                addPackages(items);
                if(items.length == 0){   
                    this.empty();
                }
                items.forEach((item, i) => this.addItemRow(item, i));
            }); 
        } else {
            if(this.packages.length <= 0){
                this.empty();
            }
            if(searchValue) this.packages.filter(f => f.item.serialNumber.includes(searchValue)).forEach((packaging, i) => this.addRow(packaging, i));
            else this.packages.forEach((packaging, i) => this.addRow(packaging, i));
        }
    }

    addRow(packaging, i) {
        let liValue = {
            icon: MATERIAL_ICONS.PALLET,
            title: packaging.item.name,
            subtitle: packaging.item.serialNumber
        }
        this.addLi(liValue, i, () => this.aonPackage(packaging, i));
    }

    addItemRow(item, i) {
        let liValue = {
            icon: MATERIAL_ICONS.PALLET,
            title: item.name,
            subtitle: item.serialNumber
        }
        this.addLi(liValue, i, () => this.aonPackage(item, i));
    }

    aonPackage(packaging, i) {
        setIndex(i);
        let aonPackage = new AonMobilePackage();
        aonPackage.setElaborationToolbar(this.TOOLBAR);
        aonPackage.setPackaging(packaging);
        let div = this.getElement('aonPackageDiv');
        this.clearElement(div);
        div.appendChild(aonPackage);
    }

    setPackages(packages){
        initializePackages();
        this.packages = packages;
        setPackages(this.packages);
        this.more = false;
    }

    setToolbar(toolbar) {
        this.TOOLBAR = toolbar;
    }
}
if(!window.customElements.get(TAG.AON_MOBILE_PACKAGE_LIST)){
    window.customElements.define(TAG.AON_MOBILE_PACKAGE_LIST, AonMobilePackageList);
}