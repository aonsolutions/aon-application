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
import { AonDialog } from '../../components/aon-dialog.js';

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
		this.getElement(table.TABLE).style.borderSpacing = '0px 10px';


		for(let i = 0; i < this.sale.details.length; i++) {
			table.addRow(); // ----- ROW i
		
			let span = this.createElement(TAG.SPAN);
			span.innerHTML = this.sale.details[i].description;
			table.addCell(span);

			let span2 = this.createElement(TAG.SPAN);
			let pending =  this.sale.details[i].quantity - this.sale.details[i].delivered;
			span2.innerHTML = this.sale.details[i].delivered + '/' + this.sale.details[i].quantity;
			table.addCell(span2);

			let aonIconButton = this.createAonElement(new AonIconButton(), 'icon' + i, 'icon');
			if(pending === 0) {
				aonIconButton.icon = MATERIAL_ICONS.CHECK_CIRCLE_OUTLINE;
				aonIconButton.color = 'green';
			} else {
				aonIconButton.icon = MATERIAL_ICONS.ADD;
				aonIconButton.addEventListener(EVENT.CLICK, () => {
					this.addPackaging(this.sale.details[i]);
				});
			}
			table.addCell(aonIconButton);
		}
	}

	setSale(sale) {
		this.sale = sale;
	}

	// ACTIONS

	back() {
		this.getApplication().getParent().aonSalesPreparation();
	}

	addPackaging(detail) {
		let dialog = new AonDialog();
		dialog.id = this.id + 'Dialog';
		dialog.type = 'fullscreen';
		this.appendChild(dialog);
		dialog.setTitle('Seleccionar Envase')

		let div = this.createElement(TAG.DIV);
		dialog.setContent(div);

		let table = new AonBasicTable();
		table.id = this.id + 'Envasesss';
		div.appendChild(table);

		this.buildProductPackaging(table);

		let next = new AonButton();
		next.title = 'Siguiente';
		div.appendChild(next);
		next.addEventListener(EVENT.CLICK, () => {
			this.clearElement(div);
			dialog.setTitle('Elegir Producto/Lote');
			let product2 = this.createInput(this.PACKAGING_PRODUCT, "Contenedor Producto / Lote");
			product2.id = 'product2';
			div.appendChild(product2);
			product2.addIconButton(MATERIAL_ICONS.QR_CODE_SCANNER, () => this.openBarcode());

			let quantity = this.createInput(this.PACKAGING_PRODUCT, "Cantidad");
			quantity.id = 'quantity';
			div.appendChild(quantity);

			let next2 = new AonButton();
			next2.title = 'Siguiente';
			div.appendChild(next2);

			next2.addEventListener(EVENT.CLICK, () => {
				this.clearElement(div);
				dialog.setTitle('Resumen Envase');

				let card = this.createCard('Envase', 'Contenido Envase');
				
				div.appendChild(card);
				
				let table = new AonBasicTable();
				table.id = 'tablex';
				card.setContent(table);
				this.getElement(table.TABLE).style.borderSpacing = '0px 10px';
		
				table.addRow(); // ----- ROW i
				
				let span3 = this.createElement(TAG.SPAN);
				span3.innerHTML = 'Producto 1';
				table.addCell(span3);
		 
				let span2 = this.createElement(TAG.SPAN);
				span2.innerHTML = 'X Cajas';
				table.addCell(span2);

				let next3 = new AonButton();
				next3.title = 'Añadir al Albarán';
				div.appendChild(next3);
				next2.addEventListener(EVENT.CLICK, () => {
					dialog.close();
				});
			});
			
		});
		
		dialog.addAcceptAction(() => {});
		dialog.open();
	}

	buildProductPackaging(table) {
		table.removeRows();
		table.addRow();
		let product = this.createInput(this.PACKAGING_PRODUCT, "Envase");
		let td = table.addCell(product);
		td.style.width = '100%';
		product.addIconButton(MATERIAL_ICONS.QR_CODE_SCANNER, () => this.openBarcode());	
		
		let addButton = new AonIconButton();
		addButton.id = this.id + 'AddButton';
		addButton.title = MSG.ADD;
		addButton.icon = MATERIAL_ICONS.ADD_CIRCLE_OUTLINE;
		addButton.addEventListener(EVENT.CLICK, () => {
			this.buildNewPackaging(table);
		});
		table.addCell(addButton);
	}

	buildNewPackaging(table) {
		table.removeRows();
		table.addRow();
		let envase = new AonSelect();
		envase.id = this.id + 'DialogEnvase';
		envase.title = 'Nuevo Envase';
		
		let td = table.addCell(envase);
		td.style.width = '100%';

		let pButton = new AonIconButton();
		pButton.id = this.id + 'ProductButton';
		pButton.title = MSG.ADD;
		pButton.icon = MATERIAL_ICONS.QR_CODE_SCANNER;
		pButton.addEventListener(EVENT.CLICK, () => {
			this.buildProductPackaging(table);
		});
		table.addCell(pButton);
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