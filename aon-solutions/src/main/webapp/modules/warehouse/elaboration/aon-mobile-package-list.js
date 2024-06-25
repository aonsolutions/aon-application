import { AonMobileList } from '../../../components/aon-mobile-list.js';
import { EVENT, MATERIAL_ICONS, TAG } from '../../../environments/environments.js';
import { AonMobilePackage } from './aon-mobile-package.js';

export class AonMobilePackageList extends AonMobileList {

    Packages;
    TOOLBAR;

    constructor () {
        super();
    }

    connectedCallback () {
        this.initialize();
        this.init();
        this.buildSearch()
    }

    initialize() {

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

    init(searchValue) {
        this.build();
        if(this.packages.length <= 0){
            this.empty();
        }
        if(searchValue) this.packages.filter(f => f.item.serialNumber.includes(searchValue)).forEach((packaging, i) => this.addRow(packaging, i));
        else this.packages.forEach((packaging, i) => this.addRow(packaging, i));
    }

    addRow(packaging, i) {
        let liValue = {
            icon: MATERIAL_ICONS.PALLET,
            title: packaging.item.name,
            subtitle: packaging.item.serialNumber
        }
        this.addLi(liValue, i, () => this.aonPackage(packaging, i));
    }

    aonPackage(packaging, i) {
        let aonPackage = new AonMobilePackage();
        aonPackage.setElaborationToolbar(this.TOOLBAR);
        aonPackage.setPackaging(packaging);
        let div = this.getElement('aonPackageDiv');
        this.clearElement(div);
        div.appendChild(aonPackage);
    }

    setPackages(packages){
        this.packages = packages;
    }

    setToolbar(toolbar) {
        this.TOOLBAR = toolbar;
    }
}
if(!window.customElements.get(TAG.AON_MOBILE_PACKAGE_LIST)){
    window.customElements.define(TAG.AON_MOBILE_PACKAGE_LIST, AonMobilePackageList);
}