import { AonMobileList } from '../../../components/aon-mobile-list.js';
import { MATERIAL_ICONS, TAG } from '../../../environments/environments.js';
import { AonMobilePackage } from './aon-mobile-package.js';

export class AonMobilePackageList extends AonMobileList {

    elaborationPackages;
    ELABORATION_TOOLBAR;

    constructor () {
        super();
    }

    connectedCallback () {
        this.initialize();
        this.init();
    }

    initialize() {

    }

    init() {
        this.build();
        if(this.elaborationPackages.length <= 0){
            this.empty();
        }
        this.elaborationPackages.forEach((packaging, i) => this.addRow(packaging, i));
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
        aonPackage.setElaborationToolbar(this.ELABORATION_TOOLBAR);
        aonPackage.setPackaging(packaging);
        let div = this.getElement('aonPackageDiv');
        this.clearElement(div);
        div.appendChild(aonPackage);
    }

    setElaborationPackages(elaborationPackages){
        this.elaborationPackages = elaborationPackages;
    }

    setElaborationToolbar(toolbar) {
        this.ELABORATION_TOOLBAR = toolbar;
    }
}
if(!window.customElements.get(TAG.AON_MOBILE_PACKAGE_LIST)){
    window.customElements.define(TAG.AON_MOBILE_PACKAGE_LIST, AonMobilePackageList);
}