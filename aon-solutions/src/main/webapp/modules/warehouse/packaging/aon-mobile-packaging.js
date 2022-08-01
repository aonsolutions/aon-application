import {AonElement} from '../../../components/AonElement.js';
import { ToolbarType} from '../../../models/enums.js';

import {AonToolbar} from "../../../components/aon-toolbar.js";
import {AonCard} from "../../../components/aon-card.js";

import {CONSTANT, MATERIAL_ICONS, MSG, TAG, EVENT} from '../../../environments/environments.js'; 
import { getProducts, getItems, getItem } from '../../../services/service.js';

import * as ACTION from '../../actions.js';
import { AonInput } from '../../../components/aon-input.js';
import { AonSelect } from '../../../components/aon-select.js';

import { AonNumber } from '../../../components/aon-number.js';
import { AonBasicTable } from '../../../components/aon-basic-table.js';
import { AonSuggestion } from '../../../components/aon-suggestion.js';

export class AonMobilePackaging extends AonElement {

	PACKAGING_TOOLBAR;

	PACKAGING_CARD;
	PACKAGING_TABLE;

	PACKAGING_CONTAINER;
	PACKAGING_PRODUCT;
	PACKAGING_PRODUCT_DESC;
	PACKAGING_QUANTITY;

	TAG_CARD;

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
		this.id = this.id || 'aonPackaging';
		this.PACKAGING_TOOLBAR = this.id + CONSTANT.TOOLBAR.initCap();
		this.PACKAGING_CARD = this.id + CONSTANT.CARD.initCap();
		
		this.PACKAGING_CONTAINER = this.id + 'Container';
		this.PACKAGING_PRODUCT = this.id + CONSTANT.PRODUCT.initCap();
		this.PACKAGING_PRODUCT_DESC = this.PACKAGING_PRODUCT + 'Desc';
		this.PACKAGING_QUANTITY = this.id + CONSTANT.QUANTITY.initCap();

		this.TAG_CARD = this.id + 'Tag' + CONSTANT.CARD.initCap();

	}

	build() {
		this.getApplication().addFloatOption(ACTION.ADD, () => this.resetPackaging());
		
		let toolbar = new AonToolbar();
		toolbar.id = this.PACKAGING_TOOLBAR;
		toolbar.type = ToolbarType.SECONDARY;
		toolbar.title = MSG.PACKAGING.toUpperCase(); 
		this.appendChild(toolbar);
		
		let div = this.createElement(TAG.DIV);
		div.style.width = "100%";
		this.appendChild(div);

		let print = toolbar.addButton2(ACTION.PRINT, () => alert('Imprimir etiqueta'));
		print.style.display = 'none';


		toolbar.addButton2(ACTION.SAVE, () => {
			print.style.display = 'block';
			this.buildTag(div);
			this.save()
		});
		// toolbar.addButton2(ACTION.BACK, () => this.back());

		this.buildPackaging(div);
	}

	resetPackaging() {
		this.clear();
		this.build();
	}

	buildPackaging(parent){
		let card = this.createCard(this.PACKAGING_CARD, MSG.PACKAGING);
		parent.appendChild(card);

		let table = new AonBasicTable();
		table.id = this.PACKAGING_TABLE;
		card.setContent(table);

		table.addRow();

		let container = this.createSelect(this.PACKAGING_CONTAINER, "Contenedor");
		container.setAlias("id", "name");
		getProducts({type: "AUXILIARY"}).then(products => {
			container.setOptions(products);
		});
		table.addCell(container, 2);

		table.addRow();

		let product = this.createInput(this.PACKAGING_PRODUCT, "Contenido (Nº Lote)");
		table.addCell(product);
		product.addIconButton(MATERIAL_ICONS.QR_CODE_SCANNER, () => alert("Escanear Codigo de barras qr o lo que sea"));
		// product.addEventListener(EVENT.AON_KEYUP, (e) => {
		// 	if(product.value.length > 2) {
		// 		let data = { serialNumber: product.value};
		// 		getItems(data).then(r => {
		// 			product.buildOptions(r.map(r => {return {
		// 				name: r.serialNumber,
		// 				value: r.id,
		// 				item: r};}));
		// 		}).catch(e => this.showError(e));
		// 	  }
		// });

		product.addEventListener(EVENT.CHANGE, () => {
			let data = { serialNumber: product.value};
			getItem(data).then(r => {
				let desc = this.getElement(this.PACKAGING_PRODUCT_DESC);
				let val = r.description || r.name;
				desc.innerHTML = val || '';
			}).catch(e => this.showError(e));
		});

		// product.addEventListener(EVENT.SELECT,(e) => {
		// 	let desc = this.getElement(this.PACKAGING_PRODUCT_DESC);
		// 	desc.innerHTML = e.detail.description;
		// });


		let quantity = this.createInput(this.PACKAGING_QUANTITY, MSG.QUANTITY);
		table.addCell(quantity);

		table.addRow();
		let span = this.createElement(TAG.SPAN);
		span.id = this.PACKAGING_PRODUCT_DESC;
		span.style.fontWeight = 'bold';
		span.innerHTML = '';
		table.addCell(span, 2);

	}

	buildTag(parent){
		let card = this.createCard(this.TAG_CARD, MSG.TAG);
		parent.appendChild(card);

		let div = this.createElement(TAG.DIV);
		
		let image = this.createElement(TAG.IMG);
		image.style.width = '100%';
		image.src = 'https://es.activebarcode.com/codes/imagesmainexample/sscc18.gif';
		div.appendChild(image);		
		
		card.setContent(div);
	}

	// ACTIONS

	back() {

	}

	save() {
	
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

	createSuggestion(id, title) {
		let suggestion = new AonSuggestion();
		suggestion.id = id;
		suggestion.title = title;
		return suggestion;
	}

	createNumber(id, title) {
		let number = new AonNumber();
		number.id = id;
		number.description = title;
		return number;
	}
}

if(!window.customElements.get(TAG.AON_MOBILE_PACKAGING)){
	window.customElements.define(TAG.AON_MOBILE_PACKAGING, AonMobilePackaging);
}