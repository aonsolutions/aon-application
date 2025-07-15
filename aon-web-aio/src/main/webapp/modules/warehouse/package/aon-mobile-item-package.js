import { AonElement } from '../../../components/AonElement.js';

import { AonCard } from "../../../components/aon-card.js";

import { CONSTANT, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../../environments/environments.js'; 

import { AonInput } from '../../../components/aon-input.js';
import { AonSelect } from '../../../components/aon-select.js';

import { AonNumber } from '../../../components/aon-number.js';
import { Elaboration } from '../../../models/elaboration/Elaboration.js';
import { AonBasicTable } from '../../../components/aon-basic-table.js';
import { AonIconButton } from '../../../components/aon-icon-button.js';
import * as LS from '../../../services/localStorageService.js';
import {openFileUrl} from '../../../services/service.js';

import * as ACTION from '../../actions.js';
import { deleteElaborationPackage } from '../../../services/warehouseService.js';
import { getPackages, removePackage } from '../package/PackagesCache.js';
import { AonMobilePackageList } from './aon-mobile-package-list.js';

import * as UA from '../../../services/userAgentService.js';
import { printFile } from '../../../services/actionService.js';
import { AonToolbar } from '../../../components/aon-toolbar.js';
import { ToolbarType } from '../../../models/enums.js';
import { createCard, createInput } from '../../../components/CreateComponent.js';
import { round } from '../../../services/utils.js';


export class AonMobileItemPackage extends AonElement {

	PACKAGE_TOOLBAR;

	PACKAGE_CARD;
	PACKAGE_TABLE;

	PACKAGE_PRODUCT;
	PACKAGE_QUANTITY;
	PACKAGE_SERIAL_NUMBER;
	PACKAGE_SERIAL_DATE;

	COMPOSITION;
	COMPOSITION_CARD;
	COMPOSITION_TABLE;
	COMPOSITION_ITEM;
	COMPOSITION_QUANTITY;
	
	TAG;
	TAG_CARD;

	fileUrl;

	itemPackage;

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

	disconnectedCallback() {

    }

	initialize() {
		this.id = this.id || 'aonPackage';
		this.PACKAGE_TOOLBAR = this.id + CONSTANT.TOOLBAR.initCap();
		this.PACKAGE_CARD = this.id + CONSTANT.CARD.initCap();
		this.PACKAGE_TABLE = this.id + CONSTANT.TABLE.initCap();
		this.PACKAGE_PRODUCT = this.id + CONSTANT.PRODUCT.initCap();	
		this.PACKAGE_QUANTITY = this.id + CONSTANT.QUANTITY.initCap();
		this.PACKAGE_SERIAL_NUMBER = this.id + 'SerialNumber';
		this.PACKAGE_SERIAL_DATE = this.id + 'SerialDate';
		
		this.COMPOSITION = this.id + CONSTANT.COMPOSITION.initCap();
		this.COMPOSITION_CARD = this.COMPOSITION + CONSTANT.CARD.initCap();
		this.COMPOSITION_TABLE = this.COMPOSITION + CONSTANT.TABLE.initCap();
		this.COMPOSITION_ITEM = this.COMPOSITION + CONSTANT.ITEM.initCap();	
		this.COMPOSITION_QUANTITY = this.COMPOSITION + CONSTANT.QUANTITY.initCap();

		this.TAG = this.id + CONSTANT.TAG.initCap();
		this.TAG_CARD = this.TAG + CONSTANT.CARD.initCap();

		this.itemPackage = this.itemPackage || {};
	}

	build() {
		this.buildToolbar();
		let div = this.createDiv();
		this.appendChild(div);
		this.buildPackage(div);		
	}

	buildToolbar() {
		let toolbar = new AonToolbar();
		toolbar.id = this.PACKAGE_TOOLBAR;
		toolbar.type = ToolbarType.SECONDARY;
		toolbar.title = MSG.PACKAGE; 
		this.appendChild(toolbar);
		if(this.back) toolbar.addButton2(ACTION.BACK, () => this.back());
		// if(!this.itemPackage.delivery)
		// 	toolbar.addButton2(ACTION.DELETE, () => this.delete());
	}

  	buildPackage(parent){
		this.buildPackageGeneral(parent);
		this.buildPackageComposition(parent);
	}

	buildPackageGeneral(parent){
		let card = createCard(this.PACKAGE_CARD, "Datos Envase", parent);
		card.addTitleButton(MSG.PRINT, MATERIAL_ICONS.PRINT, false, () => this.print());

		let table = new AonBasicTable();
		table.id = this.PACKAGE_TABLE;
		card.setContent(table);

		if(this.itemPackage.delivery) {
			table.addRow();

			let deliveryDiv = this.createDiv();
			deliveryDiv.innerHTML = 'Incluido en el albarán ' + this.itemPackage.delivery.reference;
			deliveryDiv.style.color = 'red';
			deliveryDiv.style.fontWeight = 'bold';
			table.addCell(deliveryDiv);
		}

		table.addRow();

		let product = createInput(this.PACKAGE_PRODUCT, "Envase");
		product.value = this.itemPackage.name;
		product.disabled = true;
		table.addCell(product);

		table.addRow();

		let serialNumber = createInput(this.PACKAGE_SERIAL_NUMBER, "SSCC");
		serialNumber.value = this.itemPackage.serialNumber;
		serialNumber.disabled = true;
		table.addCell(serialNumber);
	}

	buildPackageComposition(parent){
		let card = createCard(this.COMPOSITION_CARD, MSG.COMPOSITION, parent);

		let div = this.createDiv();
		card.setContent(div);

		let table = new AonBasicTable();
		table.id = this.PACKAGE_COMPOSITION_TABLE;
		div.appendChild(table);
		
		this.itemPackage.itemComposition.forEach((c) => {
			table.addRow();
			
			let span = this.createSpan();
			span.innerHTML = c.description;
			let td = table.addCell(span);
			td.style.paddingBottom = '10px';
			td.style.paddingRight = '10px';

			let span2 = this.createSpan();
			span2.innerHTML = this.getFormat(c.composition, c.quantity);
			span2.style.fontWeight = 'bold';
			let td2 = table.addCell(span2)
			td2.style.paddingBottom = '10px';
		});
	}

	getFormat(item, quantity) {
		let stockUnitTag = item.stockUnitTag.id;
		let packFormatTag = item.packFormatTag.id;
		let packUnitsTag = item.packUnitsTag.id;
		let packUnits = item.packUnits;
		let packMeasurementTag = item.packMeasurementTag.id;
		let packMeasurement = item.packMeasurement;
		
		let formatQuantity = quantity;
		if(stockUnitTag === packMeasurementTag) {
			formatQuantity = quantity / packMeasurement;
			formatQuantity = formatQuantity / packUnits;	
		} else if(stockUnitTag === packUnitsTag) {
			formatQuantity = quantity / packUnits;	
		}
		return round(formatQuantity);
	}

	// ACTIONS

	save() {
	
	}

	print() {
		let json = {
			delivery: this.itemPackage.delivery
				? this.itemPackage.delivery.id : undefined,
			container: this.itemPackage.id,
			domain_id: LS.getDomainId(),
			domain_name: LS.getDomainName(),
			login: LS.getDomainLogin()
		};
		
		let fileUrl = '/ms/api/deliveryPackagingTag?json=' + btoa(JSON.stringify(json));
		if(UA.isAndroidApp()) {
			let file = {
				url: fileUrl,
				title: this.packaging.item.serialNumber
			};
			printFile(file);
		} else openFileUrl(fileUrl, 'application/pdf');
	}

	delete() {
		let aonWarehouse = this.getElement('aonWarehouse');
		let d = document.getElementById(aonWarehouse.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.DELETE);
		d.setContentHTML('Estás seguro de eliminar el Envase ' + this.itemPackage.serialNumber);
		d.addAcceptAction(() => deletePackage(this.itemPackage.id)
			.then(() => {
				this.showMessage(MSG.DELETED_DATA);
				this.back();
			}).catch(e => this.showError(e))
		);
		d.open();
	}

	setItemPackage(itemPackage) {
		this.itemPackage = itemPackage;
	}
}

if(!window.customElements.get(TAG.AON_MOBILE_ITEM_PACKAGE)){
	window.customElements.define(TAG.AON_MOBILE_ITEM_PACKAGE, AonMobileItemPackage);
}