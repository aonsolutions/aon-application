import { AonElement } from "../../components/AonElement";
import { CONSTANT, CSS, MATERIAL_ICONS, MSG, TAG } from "../../environments/environments";
import { AonMobilePackageSearch } from "../warehouse/package/aon-mobile-package-search";
import { AonPaturpatDeliveries } from "./aon-paturpat-deliveries.js";
import { AonPaturpatElaborations } from "./aon-paturpat-elaborations.js";
import { AonPaturpatPackaging } from "./aon-paturpat-packaging.js";
import * as UA from "../../services/userAgentService.js";
import { MOBILE_ACTION, mobileAction } from "../../services/mobileService.js";
import { AonMobileDelivery } from "../delivery/aon-mobile-delivery.js";
import { getDelivery } from "../../services/warehouseService.js";
import { openBarcode } from "../../services/actionService.js";

export class AonPaturpatApps extends AonElement {

    get id() {
        return this.getAttribute(CONSTANT.ID);
    }

    set id(id) {
        this.setAttribute(CONSTANT.ID, id);
    }

    constructor() {
        super();
    }

    connectedCallback() {
        this.initialize();
        this.build();
    }

    initialize() {
        this.id = this.id || 'aonPaturpatApps';

        this.newPackaging = {
            id: 'newPackaging',
            title: 'Empaquetar',
            icon: MATERIAL_ICONS.ORDERS,
            backgroundColor: "#005f2c",
            fn: () => {
                let rootPanel = this.getElement('mobileRootPanel');
                rootPanel.innerHTML = '';
                rootPanel.appendChild(new AonPaturpatPackaging());               
            }
        };

        this.deliveryPreparation = {
            id: 'deliveryPreparation',
            title: 'Preparar Albarán',
            icon: MATERIAL_ICONS.QR_CODE_SCANNER,
            backgroundColor: "#005f2c",
            fn: () => {
                this.openBarcode();
            }
        };

        this.searchPackage = {
            id: 'searchPackage',
            title: 'Envases',
            icon: MATERIAL_ICONS.PALLET,
            backgroundColor: "#005f2c",
            fn: () => {
                let rootPanel = this.getElement('mobileRootPanel');
                rootPanel.innerHTML = '';
                rootPanel.appendChild(new AonMobilePackageSearch());
            }
        };

        this.elaborations = {
            id: 'elaborations',
            title: MSG.ELABORATIONS,
            icon: MATERIAL_ICONS.PRECISION_MANUFACTURING,
            backgroundColor: "#005f2c",
            fn: () => {
                let rootPanel = this.getElement('mobileRootPanel');
                rootPanel.innerHTML = '';
                rootPanel.appendChild(new AonPaturpatElaborations());
            }
        };

        this.deliveries = {
            id: 'deliveries',
            title: 'Albaranes de Venta',
            icon: MATERIAL_ICONS.LOCAL_SHIPPING,
            backgroundColor: "#005f2c",
            fn: () => {
                let rootPanel = this.getElement('mobileRootPanel');
                rootPanel.innerHTML = '';
                rootPanel.appendChild(new AonPaturpatDeliveries());
            }
        };
        
        this.apps = [
            this.newPackaging,
            this.deliveryPreparation,
            this.searchPackage,
            this.elaborations,
            this.deliveries
        ];
    }

    openBarcode() {
        let ionicData = { action: MOBILE_ACTION.BARCODE, selector: TAG.AON_PATURPAT_APPS };
        if(UA.isAndroidApp()) {
            openBarcode(ionicData, (result) => this.setBarcodeAction(result.code));
        } else mobileAction(ionicData);
    }


    setBarcodeAction(code) {
        console.log("delivery: " + code);
        let data = {
            id: option.delivery,
            full: true
        }
        getDelivery(data).then(r => {
            let aonDelivery = new AonMobileDelivery();
            aonDelivery.setDelivery(r);
            let rootPanel = this.getElement('mobileRootPanel');
            rootPanel.innerHTML = '';
            rootPanel.appendChild(aonDelivery);
        });
    }

    build() {
        let ul = this.createElement(TAG.UL);
        ul.id = "aonMobileAppSelection";
        ul.style.listStyleType = 'none';
        ul.style.display = 'grid';
        ul.style.gridTemplateColumns = 'repeat(3,1fr)';
        ul.style.padding = '0px';
        ul.style.marginTop = '20px';
        
        for (let app of this.apps) {
            let li = this.createElement(TAG.LI);
            li.id = "aonMobileAppSelectionApp-" + app.id;
            // li.classList.add(CSS.AON_LIST_GROUP_ITEM);
            // li.classList.add(CSS.AON_APP_LI);
            // li.classList.add("fixLi");
            li.style.borderRight = '0px';
            li.style.borderLeft = '0px';
            li.style.cursor = 'pointer';
            li.style.textAlign = 'center';
            li.style.margin = '10px';
            // ONLY IN OLD VERSION
            li.style.display = 'ruby';
            li.addEventListener('click', () => app.fn());



            let icon = this.createDiv();
            icon.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
            icon.id = "aonMobileSelectionIcon-" + app.id;
            icon.innerHTML = app.icon;
            icon.style.backgroundColor = '#005f2c';
            icon.style.color = "white";
            icon.style.fontVariationSettings = "'FILL' 0, 'wght' 300, 'GRAD' 0, 'opsz' 24";
            icon.style.borderRadius = "5px";
            // ONLY IN NEW VERSION
            icon.style.padding = "15px";
            li.appendChild(icon);
  
            let span2 = this.createDiv();
            span2.id = "aonAppTitle-" + app.id;
            span2.innerHTML = app.title;
            span2.style.margin = '10px';
            li.appendChild(span2);
            ul.appendChild(li);
        }

        this.appendChild(ul);
    }

}
if (!window.customElements.get(TAG.AON_PATURPAT_APPS)) {
    window.customElements.define(TAG.AON_PATURPAT_APPS, AonPaturpatApps);
}