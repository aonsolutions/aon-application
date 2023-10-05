import {AonElement} from '../../components/AonElement.js';
import { ToolbarType} from '../../models/enums.js';

import {AonToolbar} from "../../components/aon-toolbar.js";
import {AonCard} from "../../components/aon-card.js";
import {AonButton} from "../../components/aon-button.js";

import {CONSTANT, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js'; 

import * as ACTION from '../actions.js';
import { AonInput } from '../../components/aon-input.js';
import { AonSelect } from '../../components/aon-select.js';

import { AonNumber } from '../../components/aon-number.js';
import { AonBasicTable } from '../../components/aon-basic-table.js';
import { AonIconButton } from '../../components/aon-icon-button.js';
import { AonTabs } from '../../components/aon-tabs.js';
import { getDeliveries } from '../../services/warehouseService.js';
import { getDeliveryPackaging, getItem, getPackaging, getProducts, saveDeliveryPackaging } from '../../services/productService.js';
import { AonMobilePackageList } from '../warehouse/elaboration/aon-mobile-package-list.js';

export class AonMobileDelivery extends AonElement {

	DELIVERY_TOOLBAR;
	DELIVERY_CARD;
	DETAIL_TABLE;
	DELIVERY_SAVE_BUTTON;
	DELIVERY_TABS
	DELIVERY_TABS_BUTTON;

	delivery;
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
		this.id = this.id || 'aonDelivery';
		this.DELIVERY_TOOLBAR = this.id + CONSTANT.TOOLBAR.initCap();
		this.DELIVERY_CARD = this.id + CONSTANT.CARD.initCap();
		this.DETAIL_TABLE = this.DELIVERY_CARD + 'DetailTable';
		this.delivery = this.delivery || {};
		this.packaging = {};
		this.DELIVERY_SAVE_BUTTON = this.id + 'DeliverySaveButton';

		this.DELIVERY_TABS = this.id + CONSTANT.TABS.initCap();
		this.DELIVERY_TABS_BUTTON = [
			{
				name: MSG.DELIVERY,
				id: CONSTANT.DELIVERY,
				icon: MATERIAL_ICONS.LOCAL_SHIPPING,
			},
			{
				name: MSG.PACKAGING,
				id: CONSTANT.PACKAGING,
				icon: MATERIAL_ICONS.PALLET,
			}
		];
	}

	build() {
		let toolbar = new AonToolbar();
		toolbar.id = this.SALE_TOOLBAR;
		toolbar.type = ToolbarType.SECONDARY;
		toolbar.title = this.sale.id
            ? this.sale.reference : MSG.NEW_SALE.toUpperCase(); 
		this.appendChild(toolbar);
		toolbar.addButton2(ACTION.BACK, () => this.back());

		this.buildSale();
	}

	build() {
		let toolbar = new AonToolbar();
		toolbar.id = this.DELIVERY_TOOLBAR;
		toolbar.type = ToolbarType.SECONDARY;
		toolbar.title = this.delivery.reference; 
		this.appendChild(toolbar);
		// toolbar.addButton2(ACTION.SAVE, () => this.save());
		toolbar.addButton2(ACTION.BACK, () => this.back());

		let div = this.createElement(TAG.DIV);
		div.style.width = "100%";

		let tabs = new AonTabs();
		tabs.id = this.DELIVERY_TABS;
		tabs.setBackgrounColor('white');
		tabs.addEventListener(EVENT.CHANGE, ({ detail }) => {
     		if (detail) this.changeTabs(div, detail.position);
    	});

    	tabs.setButtons(this.DELIVERY_TABS_BUTTON);

	    this.appendChild(tabs);

		this.appendChild(div);
		this.changeTabs(div, 0);
	}

	changeTabs(parent, position = 0) {
		parent.innerHTML = "";
		switch (position) {
		  case 0:
			this.buildDelivery(parent);
			break;
		  case 1:
			this.buildPackaging(parent);
			break;
		}
	}

	buildDelivery(parent){
		this.buildDeliveryGeneral(parent);
		this.buildDeliveryDetail(parent);
	}

	buildDeliveryGeneral(parent) {

	}

	buildDeliveryDetail(parent) {
		let card = this.createCard(this.DELIVERY_DETAIL_CARD, 'Detalles');
		parent.appendChild(card);

		let table = new AonBasicTable();
		table.id = this.DETAIL_TABLE;
		card.setContent(table);
		this.getElement(table.TABLE).style.borderSpacing = '0px 10px';


		for(let i = 0; i < this.delivery.details.length; i++) {
			table.addRow(); // ----- ROW i
		
			let span = this.createElement(TAG.SPAN);
			span.innerHTML = this.delivery.details[i].description;
			table.addCell(span);

			let span2 = this.createElement(TAG.SPAN);
			span2.innerHTML = this.delivery.details[i].quantity;
			table.addCell(span2);
		}
	}

	buildPackaging(parent){
		let div = this.createElement(TAG.DIV, "aonPackageDiv")
		let packaging = new AonMobilePackageList();
		packaging.setToolbar(this.DELIVERY_TOOLBAR);
		packaging.setPackages(this.delivery.packaging);
		div.appendChild(packaging);
		parent.appendChild(div);
	}


	setDelivery(delivery) {
		this.delivery = delivery;
	}

	// ACTIONS

	back() {
		this.getApplication().getParent().aonDelivery();
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

if(!window.customElements.get(TAG.AON_MOBILE_DELIVERY)){
	window.customElements.define(TAG.AON_MOBILE_DELIVERY, AonMobileDelivery);
}