import { AonMobileList } from '../../components/aon-mobile-list.js';
import { MATERIAL_ICONS, TAG } from '../../environments/environments.js';
import { AonMobileDeliveryPackaging } from './aon-mobile-delivery-packaging.js';

export class AonMobileDeliveryPackagingList extends AonMobileList {

    Packages;
    TOOLBAR;
    DELIVERY_TOOLBAR;

    delivery;
    deliveryDetails;

    constructor () {
        super();
    }

    connectedCallback () {
        this.initialize();
        this.init();
    }

    initialize() {
        this.id = this.id || 'aonMobileDeliveryPackagingList';
        super.initialize();
    }

    init() {
        this.build();
        if(this.packages.length <= 0){
            this.empty();
        }
        this.packages.forEach((packaging, i) => this.addRow(packaging, i));
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
        let aonPackage = new AonMobileDeliveryPackaging();
        aonPackage.setElaborationToolbar(this.TOOLBAR);
        aonPackage.setDeliveryToolbar(this.DELIVERY_TOOLBAR);
        aonPackage.setDelivery(this.delivery);
        aonPackage.setDeliveryDetails(this.deliveryDetails);
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

    setDeliveryToolbar(toolbar) {
        this.DELIVERY_TOOLBAR = toolbar;
    }

    setDelivery(delivery) {
        this.delivery = delivery;
    }

    setDeliveryDetails(deliveryDetails) {
        this.deliveryDetails = deliveryDetails;
    }

    
}
if(!window.customElements.get(TAG.AON_MOBILE_DELIVERY_PACKAGING_LIST)){
    window.customElements.define(TAG.AON_MOBILE_DELIVERY_PACKAGING_LIST, AonMobileDeliveryPackagingList);
}