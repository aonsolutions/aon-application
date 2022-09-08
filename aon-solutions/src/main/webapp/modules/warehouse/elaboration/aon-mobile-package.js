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

export class AonMobilePackage extends AonElement {

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
	fileUrl;

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
		let card = this.createCard(this.PACKAGE_CARD, MSG.PACKAGING);
		parent.appendChild(card);

		let table = new AonBasicTable();
		table.id = this.PACKAGE_TABLE;
		card.setContent(table);

		table.addRow();

		let product = this.createInput(this.PACKAGE_PRODUCT, MSG.PRODUCT);
		product.value = this.packaging.item.name;
		table.addCell(product);

		let quantity = this.createInput(this.PACKAGE_QUANTITY, MSG.QUANTITY);
		quantity.value = this.packaging.quantity || 0.0;
		table.addCell(quantity);

		table.addRow();

		let serialNumber = this.createInput(this.PACKAGE_SERIAL_NUMBER, "Nº Lote");
		serialNumber.value = this.packaging.item.serialNumber;
		table.addCell(serialNumber);

		let serialDate = this.createInput(this.PACKAGE_SERIAL_DATE, "Fecha Lote");
		serialDate.value = this.packaging.item.serialDate;
		table.addCell(serialDate);
	}

	buildPackageComposition(parent){
		let card = this.createCard(this.COMPOSITION_CARD, MSG.COMPOSITION);
		parent.appendChild(card);

		let div = this.createElement(TAG.DIV);
		card.setContent(div);

		let table = new AonBasicTable();
		table.id = this.PACKAGE_COMPOSITION_TABLE;
		div.appendChild(table);
		
		this.packaging.composition.forEach((composition, i) => {
			table.addRow();
			let comp1 = this.createInput(this.COMPOSITION_ITEM + i, "Producto");
			comp1.value = composition.item.description;
			table.addCell(comp1);

			let comp2 = this.createInput(this.COMPOSITION_QUANTITY + i, "Cantidad");
			comp2.value = composition.quantity;
			table.addCell(comp2);
		});

		let addButton = new AonIconButton();
		addButton.id = this.COMPOSITION_ADD_BUTTON;
		addButton.title = MSG.ADD;
		addButton.icon = MATERIAL_ICONS.ADD;
		addButton.addEventListener(EVENT.CLICK, () => this.addComposition());
		div.appendChild(addButton);		
	}

	addComposition(){
		let d = this.getApplication().getDialog();
		d.clear();
		if(!this.isMobile())d.width = '400px';
		d.setTitle(MSG.ADD_COMPOSITION);
		d.setContentHTML('Esta opción está en desarrollo...');
		d.addAcceptAction(() => {});
		d.open();
	}

	buildTag(parent){
		let card = this.createCard(this.TAG_CARD, MSG.TAG);
		parent.appendChild(card);

		let json = {
			container: this.packaging.item.id,
			domain_id: LS.getDomainId(),
			domain_name: LS.getDomainName(),
			login: LS.getDomainLogin()
		};

		let w = this.getElement(card.CONTENT).offsetWidth;
		let type = 'application/pdf';
		let url = '/ms/api/download_packaging_pdf?json=' + btoa(JSON.stringify(json));
		this.fileUrl = '/ms/api/download_packaging_pdf?json=' + btoa(JSON.stringify(json));
		card.setContentHTML(`<aon-viewer type="${type}" file="${url}" width="${w}"></aon-viewer>`);
	}

	// ACTIONS

	back() {

	}

	save() {
	
	}

	print() {
		openFileUrl(this.fileUrl, 'application/pdf');
	}

	
	setPackaging(packaging) {
		this.packaging = packaging; //new Package(elaboration);
	}

	// Create Components

	createCard(id, title) {
		let card = new AonCard();
		card.id = id;
		card.title = title;
		return card;
	}

	createSelect(id, title) {
		let select = new AonSelect();
		select.id = id;
		select.title = title;
		return select;
	}

	createInput(id, title) {
		let select = new AonInput();
		select.id = id;
		select.description = title;
		return select;
	}

	createNumber(id, title) {
		let number = new AonNumber();
		number.id = id;
		number.description = title;
		return number;
	}

	setElaborationToolbar(toolbar) {
		this.ELABORATION_TOOLBAR = toolbar;
	}
}

if(!window.customElements.get(TAG.AON_MOBILE_PACKAGE)){
	window.customElements.define(TAG.AON_MOBILE_PACKAGE, AonMobilePackage);
}