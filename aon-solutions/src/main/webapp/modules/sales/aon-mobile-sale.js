import {AonElement} from '../../components/AonElement.js';
import { ToolbarType} from '../../models/enums.js';

import {AonToolbar} from "../../components/aon-toolbar.js";
import {AonCard} from "../../components/aon-card.js";

import {CONSTANT, MSG, TAG } from '../../environments/environments.js'; 

import * as ACTION from '../actions.js';
import { AonInput } from '../../components/aon-input.js';
import { AonSelect } from '../../components/aon-select.js';

import { AonNumber } from '../../components/aon-number.js';
import { AonBasicTable } from '../../components/aon-basic-table.js';

export class AonMobileSale extends AonElement {

	SALE_TOOLBAR;
	SALE_CARD;
	DETAIL_TABLE;

	sale;

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
		this.SALE_TOOLBAR = this.id + CONSTANT.TOOLBAR.initCap();
		this.SALE_CARD = this.id + CONSTANT.CARD.initCap();
		this.DETAIL_TABLE = this.SALE_CARD + 'DetailTable';
		this.sale = this.sale || {};
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

  	buildSale(){
		let card = this.createCard(this.SALE_CARD, 'Detalles');
		this.appendChild(card);

		let table = new AonBasicTable();
		table.id = this.DETAIL_TABLE;
		card.setContent(table);

		for(let i = 0; i < this.sale.details.length; i++) {
			table.addRow(); // ----- ROW i
		
			let span = this.createElement(TAG.SPAN);
			span.innerHTML = this.sale.details[i].description;
			table.addCell(span);

			let span2 = this.createElement(TAG.SPAN);
			span2.innerHTML = this.sale.details[i].delivered;
			table.addCell(span2);
		}
	}

	setSale(sale) {
		this.sale = sale;
	}

	// ACTIONS

	back() {
		this.getApplication().getParent().aonSalesPreparation();
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

if(!window.customElements.get(TAG.AON_MOBILE_SALE)){
	window.customElements.define(TAG.AON_MOBILE_SALE, AonMobileSale);
}