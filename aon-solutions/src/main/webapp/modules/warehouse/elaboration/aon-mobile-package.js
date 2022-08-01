import {AonElement} from '../../../components/AonElement.js';

import {AonCard} from "../../../components/aon-card.js";

import {CONSTANT, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../../environments/environments.js'; 

import { AonInput } from '../../../components/aon-input.js';
import { AonSelect } from '../../../components/aon-select.js';

import { AonNumber } from '../../../components/aon-number.js';
import { Elaboration } from '../../../models/elaboration/Elaboration.js';
import { AonBasicTable } from '../../../components/aon-basic-table.js';
import { AonIconButton } from '../../../components/aon-icon-button.js';

export class AonMobilePackage extends AonElement {

	PACKAGE_CARD;
	PACKAGE_TABLE;

	PACKAGE_PRODUCT;
	PACKAGE_QUANTITY;
	PACKAGE_SERIAL_NUMBER;
	PACKAGE_SERIAL_DATE;

	COMPOSITION;
	COMPOSITION_CARD;

	packaging;

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
		
		this.packaging = new Elaboration(this.packaging);
	}

	build() {
		let div = this.createElement(TAG.DIV);
		div.style.width = "100%";
		this.appendChild(div);
		this.buildPackage(div);
	}

  	buildPackage(parent){
		this.buildPackageGeneral(parent);
		this.buildPackageComposition(parent);
	}

	buildPackageGeneral(parent){
		let card = this.createCard(this.PACKAGE_CARD, MSG.PACKAGING);
		parent.appendChild(card);

		let table = new AonBasicTable();
		table.id = this.PACKAGE_TABLE;
		card.setContent(table);

		table.addRow();

		let product = this.createInput(this.PACKAGE_PRODUCT, MSG.PRODUCT);
		table.addCell(product);

		let quantity = this.createInput(this.PACKAGE_QUANTITY, MSG.QUANTITY);
		table.addCell(quantity);

		table.addRow();

		let serialNumber = this.createInput(this.PACKAGE_SERIAL_NUMBER, "Nº Lote");
		table.addCell(serialNumber);

		let serialDate = this.createInput(this.PACKAGE_SERIAL_DATE, "Fecha Lote");
		table.addCell(serialDate);
	}

	buildPackageComposition(parent){
		let card = this.createCard(this.COMPOSITION_CARD, MSG.COMPOSITION);
		parent.appendChild(card);

		let div = this.createElement(TAG.DIV);
		
		let addButton = new AonIconButton();
		addButton.id = this.COMPOSITION_ADD_BUTTON;
		addButton.title = MSG.ADD;
		addButton.icon = MATERIAL_ICONS.ADD;
		addButton.addEventListener(EVENT.CLICK, () => alert('Añadir composición'));
		div.appendChild(addButton);		
		
		card.setContent(div);
	}

	// ACTIONS

	back() {

	}

	save() {
	
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
}

if(!window.customElements.get(TAG.AON_MOBILE_PACKAGE)){
	window.customElements.define(TAG.AON_MOBILE_PACKAGE, AonMobilePackage);
}