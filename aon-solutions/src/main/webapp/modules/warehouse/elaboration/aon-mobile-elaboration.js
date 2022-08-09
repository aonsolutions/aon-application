import {AonElement} from '../../../components/AonElement.js';
import { ToolbarType} from '../../../models/enums.js';

import {AonToolbar} from "../../../components/aon-toolbar.js";
import {AonCard} from "../../../components/aon-card.js";

import {CONSTANT, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../../environments/environments.js'; 

import * as ACTION from '../../actions.js';
import { AonInput } from '../../../components/aon-input.js';
import { AonSelect } from '../../../components/aon-select.js';

import { AonNumber } from '../../../components/aon-number.js';
import { Elaboration } from '../../../models/elaboration/Elaboration.js';
import { AonTabs } from '../../../components/aon-tabs.js';
import { AonBasicTable } from '../../../components/aon-basic-table.js';
import { AonIconButton } from '../../../components/aon-icon-button.js';
import { AonMobilePackageList } from './aon-mobile-package-list.js';

export class AonMobileElaboration extends AonElement {

	ELABORATION_TOOLBAR;
	ELABORATION_TABS;
	ELABORATION_TABS_BUTTON;

	ELABORATION_CARD;
	ELABORATION_TABLE;

	ELABORATION_PRODUCT;
	ELABORATION_QUANTITY;
	ELABORATION_SERIAL_NUMBER;
	ELABORATION_SERIAL_DATE;

	COMPOSITION;
	COMPOSITION_CARD;

	elaboration;

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
		this.id = this.id || 'aonElaboration';
		this.ELABORATION_TOOLBAR = this.id + CONSTANT.TOOLBAR.initCap();
		this.ELABORATION_TABS = this.id + CONSTANT.TABS.initCap();
		this.ELABORATION_CARD = this.id + CONSTANT.CARD.initCap();
		this.ELABORATION_TABLE = this.id + CONSTANT.TABLE.initCap();
		this.ELABORATION_PRODUCT = this.id + CONSTANT.PRODUCT.initCap();	
		this.ELABORATION_QUANTITY = this.id + CONSTANT.QUANTITY.initCap();
		this.ELABORATION_SERIAL_NUMBER = this.id + 'SerialNumber';
		this.ELABORATION_SERIAL_DATE = this.id + 'SerialDate';
		
		this.COMPOSITION = this.id + CONSTANT.COMPOSITION.initCap();
		this.COMPOSITION_CARD = this.COMPOSITION + CONSTANT.CARD.initCap();
		this.ELABORATION_TABS_BUTTON = [
			{
				name: MSG.ELABORATION,
				id: CONSTANT.ELABORATION,
				icon: MATERIAL_ICONS.PRECISION_MANUFACTURING,
			},
			{
				name: MSG.PACKAGING,
				id: CONSTANT.PACKAGING,
				icon: MATERIAL_ICONS.PALLET,
			}
		];
		this.elaboration = new Elaboration(this.elaboration);
	}

	build() {
		let toolbar = new AonToolbar();
		toolbar.id = this.ELABORATION_TOOLBAR;
		toolbar.type = ToolbarType.SECONDARY;
		toolbar.title = this.elaboration.getId()
            ? this.elaboration.getReference() : MSG.NEW_ELABORATION.toUpperCase(); 
		this.appendChild(toolbar);
		toolbar.addButton2(ACTION.SAVE, () => this.save());
		toolbar.addButton2(ACTION.BACK, () => this.back());

		let div = this.createElement(TAG.DIV);
		div.style.width = "100%";

		let tabs = new AonTabs();
		tabs.id = this.ELABORATION_TABS;
		tabs.setBackgrounColor('white');
		tabs.addEventListener(EVENT.CHANGE, ({ detail }) => {
     		if (detail) this.changeTabs(div, detail.position);
    	});

    	tabs.setButtons(this.ELABORATION_TABS_BUTTON);

	    this.appendChild(tabs);

		this.appendChild(div);
		this.changeTabs(div, 0);
	}

	changeTabs(parent, position = 0) {
		parent.innerHTML = "";
		switch (position) {
		  case 0:
			this.buildElaboration(parent);
			break;
		  case 1:
			this.buildPackaging(parent);
			break;
		}
	  }

  	buildElaboration(parent){
		this.buildElaborationGeneral(parent);
		this.buildElaborationComposition(parent);
	}
	buildElaborationGeneral(parent){
		let card = this.createCard(this.ELABORATION_CARD, MSG.ELABORATION);
		parent.appendChild(card);

		let table = new AonBasicTable();
		table.id = this.ELABORATION_TABLE;
		card.setContent(table);

		table.addRow();

		let product = this.createInput(this.ELABORATION_PRODUCT, MSG.PRODUCT);
		product.value = this.elaboration.detail.item.name;
		table.addCell(product);

		let quantity = this.createInput(this.ELABORATION_QUANTITY, MSG.QUANTITY);
		quantity.value = this.elaboration.detail.quantity;
		table.addCell(quantity);

		table.addRow();

		let serialNumber = this.createInput(this.ELABORATION_SERIAL_NUMBER, "Nº Lote");
		serialNumber.value = this.elaboration.detail.item.serialNumber;
		table.addCell(serialNumber);

		let serialDate = this.createInput(this.ELABORATION_SERIAL_DATE, "Fecha Lote");
		serialDate.value = this.elaboration.detail.item.serialDate;
		table.addCell(serialDate);
	}

	buildElaborationComposition(parent){
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

	buildPackaging(parent){
		let div = this.createElement(TAG.DIV, "aonPackageDiv")
		let packaging = new AonMobilePackageList();
		packaging.setElaborationPackages(this.elaboration.packaging);
		div.appendChild(packaging);
		parent.appendChild(div);
	}

	
	// ACTIONS

	back() {

	}

	save() {
	
	}

	
	setElaboration(elaboration) {
		this.elaboration = new Elaboration(elaboration);
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

if(!window.customElements.get(TAG.AON_MOBILE_ELABORATION)){
	window.customElements.define(TAG.AON_MOBILE_ELABORATION, AonMobileElaboration);
}