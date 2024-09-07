import { AonElement } from '../../components/AonElement.js';

import { CONSTANT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js'; 

import { Elaboration } from '../../models/elaboration/Elaboration.js';
import { AonBasicTable } from '../../components/aon-basic-table.js';
import * as LS from '../../services/localStorageService.js';
import {openFileUrl} from '../../services/service.js';

import * as ACTION from '../actions.js';
import { createCard, createInput } from '../../components/CreateComponent.js';

export class AonMobileDeliveryPackaging extends AonElement {

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

	packaging;

	ELABORATION_TOOLBAR;

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
		this.id = this.id || 'aonPackage';
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

		this.packaging = this.packaging || new Elaboration(this.packaging);
	}

	build() {
		let div = this.createElement(TAG.DIV);
		div.style.width = "100%";
		this.appendChild(div);
		this.buildPackage(div);

		let toolbar = this.getElement(this.ELABORATION_TOOLBAR);		
		toolbar.removeButton(ACTION.PRINT.id);
		toolbar.addButtonAfter(ACTION.PRINT, () => this.print());
	}

  	buildPackage(parent){
		this.buildPackageGeneral(parent);
		this.buildPackageComposition(parent);
		if(this.packaging.composition.length === 1)
			this.buildTag(parent);
	}

	buildPackageGeneral(parent){
		let card = createCard(this.PACKAGE_CARD, MSG.PACKAGING, parent);
		card.addTitleButton(MSG.PRINT, MATERIAL_ICONS.PRINT, false, () => this.print());

		let table = new AonBasicTable();
		table.id = this.PACKAGE_TABLE;
		card.setContent(table);

		table.addRow();

		let product = createInput(this.PACKAGE_PRODUCT, "Envase");
		product.value = this.packaging.item.name;
		product.disabled = true;
		table.addCell(product);

		table.addRow();

		let serialNumber = createInput(this.PACKAGE_SERIAL_NUMBER, "SSCC");
		serialNumber.value = this.packaging.item.serialNumber;
		serialNumber.disabled = true;
		table.addCell(serialNumber);

	}

	buildPackageComposition(parent){
		let card = createCard(this.COMPOSITION_CARD, MSG.COMPOSITION, parent);

		let div = this.createElement(TAG.DIV);
		card.setContent(div);

		let table = new AonBasicTable();
		table.id = this.PACKAGE_COMPOSITION_TABLE;
		div.appendChild(table);
		
		// Array.prototype.forEach.call(this.packaging.composition, i => {
		this.packaging.item.itemComposition.forEach((c, i) => {
			table.addRow();
			
			let comp1 = createInput(this.COMPOSITION_ITEM + i, "Producto");
			comp1.value = c.composition.description.isEmpty()
				? c.composition.product.code
				: c.composition.description;
			comp1.disabled = CONSTANT.TRUE;
			table.addCell(comp1);

			let comp2 = createInput(this.COMPOSITION_QUANTITY + i, "Cantidad");
			comp2.value = c.quantity;
			comp2.disabled = CONSTANT.TRUE;
			table.addCell(comp2);
		});
	}

	

	buildTag(){

	
	}

	// ACTIONS

	back() {

	}

	save() {
	
	}

	print() {
		let json = {
			container: this.packaging.item.id,
			domain_id: LS.getDomainId(),
			domain_name: LS.getDomainName(),
			login: LS.getDomainLogin()
		};

		let fileUrl = '/ms/api/download_packaging_pdf?json=' + btoa(JSON.stringify(json));

		openFileUrl(fileUrl, 'application/pdf');
	}

	
	setPackaging(packaging) {
		this.packaging = packaging; //new Package(elaboration);
	}

	setElaborationToolbar(toolbar) {
		this.ELABORATION_TOOLBAR = toolbar;
	}
}

if(!window.customElements.get(TAG.AON_MOBILE_DELIVERY_PACKAGING)){
	window.customElements.define(TAG.AON_MOBILE_DELIVERY_PACKAGING, AonMobileDeliveryPackaging);
}