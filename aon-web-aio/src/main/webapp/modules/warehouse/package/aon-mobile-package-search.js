import {AonElement} from '../../../components/AonElement.js';
import { CONSTANT, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../../environments/environments.js'; 
import { createInput } from '../../../components/CreateComponent.js';
import { MOBILE_ACTION, mobileAction } from '../../../services/mobileService.js';
import { openBarcode } from '../../../services/actionService.js';
import { AonButton } from '../../../components/aon-button.js';
import { getPackage } from '../../../services/productService.js';
import { AonMobileItemPackage } from './aon-mobile-item-package.js';

export class AonMobilePackageSearch extends AonElement {

    INPUT;
    DIV;
    BUTTON;

    get id() {
        return this.getAttribute(CONSTANT.ID);
    }

    set id(id) {
        this.setAttribute(CONSTANT.ID, id);
    }

    constructor () {
        super();
    }

    connectedCallback () {
        this.initialize();
        this.build();
    }

    initialize() {
        this.id = this.id || 'aonPackageSearch';
        this.INPUT = this.id + CONSTANT.INPUT.initCap();
        this.DIV = this.id + CONSTANT.DIV.initCap();    
        this.BUTTON = this.id + CONSTANT.BUTTON.initCap();
    }

    build() {
        let div = this.createDiv(this.DIV);
        div.style.margin = "10px";
        this.appendChild(div);

        let title = this.createDiv();
        title.innerHTML = 'Buscar envase por SSCC';
        title.style.fontWeight = 'bolder';
        title.style.textAlign = 'center';
        title.style.fontSize = '20px';
        title.style.margin = '40px';

        div.appendChild(title);

        let container = createInput(this.INPUT, MSG.CONTAINER + ' (SSCC)');
        div.appendChild(container);
        container.addIcon(MATERIAL_ICONS.QR_CODE_SCANNER, undefined, () => this.openBarcode(container));

        let searchButton = new AonButton();
        searchButton.id = this.BUTTON;
        searchButton.title = MSG.SEARCH;
        searchButton.style.margin = '10px';
        searchButton.style.marginTop = '40px';	
        searchButton.style.right = '0px';
        searchButton.style.position = 'absolute';
        div.appendChild(searchButton);	
        searchButton.addEventListener(EVENT.CLICK, () => this.search(container.value));

        container.focus();
    }

    search(value) {
        let data = {sscc: value, full:true};
        getPackage(data).then(itemPackage => {
            let aonMobilePackage= new AonMobileItemPackage();
            aonMobilePackage.setItemPackage(itemPackage);
            aonMobilePackage.back = () => this.getApplication().setContent(new AonMobilePackageSearch());
            this.getApplication().setContent(aonMobilePackage);
        });
    }

    barcodeId;
	openBarcode(element) {
		this.barcodeId = element.id;
		let ionicData = { action: MOBILE_ACTION.BARCODE, selector: TAG.AON_MOBILE_DELIVERY };
		if(UA.isAndroidApp()) {
			openBarcode(ionicData, (result) => element.value = result.code);
		} else mobileAction(ionicData);
	}

	setBarcodeData(barcodeStr) {
		try {
			if(typeof barcodeStr === 'string') {
				barcodeStr = JSON.parse(barcodeStr);
			}

			const {text, format, cancelled} = barcodeStr;
			if(!cancelled) {
				const element = this.getElement(this.barcodeId);
				element.value = text;
			}
		} catch (error) {
			this.showError(error);
		}
	}
}

if(!window.customElements.get(TAG.AON_MOBILE_PACKAGE_SEARCH)) {
    window.customElements.define(TAG.AON_MOBILE_PACKAGE_SEARCH, AonMobilePackageSearch);
}