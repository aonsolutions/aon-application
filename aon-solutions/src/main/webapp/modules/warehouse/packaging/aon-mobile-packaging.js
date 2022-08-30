import {AonElement} from '../../../components/AonElement.js';
import { ToolbarType} from '../../../models/enums.js';

import {AonToolbar} from "../../../components/aon-toolbar.js";
import {AonCard} from "../../../components/aon-card.js";

import {CONSTANT, MATERIAL_ICONS, MSG, TAG, EVENT} from '../../../environments/environments.js'; 
import {getPackaging, mobileAction, MOBILE_ACTION, savePackaging } from '../../../services/service.js';

import * as ACTION from '../../actions.js';
import { AonInput } from '../../../components/aon-input.js';
import { AonSelect } from '../../../components/aon-select.js';

import { AonNumber } from '../../../components/aon-number.js';

import { AonViewer } from '../../../components/aon-viewer.js';
import { AonBasicTable } from '../../../components/aon-basic-table.js';
import { AonSuggestion } from '../../../components/aon-suggestion.js';

import * as LS from '../../../services/localStorageService.js';
import { AonDate } from '../../../components/aon-date.js';

export class AonMobilePackaging extends AonElement {

	PACKAGING_TOOLBAR;

	PACKAGING_CARD;
	PACKAGING_TABLE;

	PACKAGING_CONTAINER;
	PACKAGING_PRODUCT;
	PACKAGING_PRODUCT_SERIAL_NUMBER;
	PACKAGING_PRODUCT_SERIAL_DATE;
	PACKAGING_PRODUCT_DESC;
	PACKAGING_QUANTITY;

	TAG_CARD;

	VIEWER;

	item;
	contenedor;
	barcode;
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
		this.id = this.id || 'aonPackaging';
		this.PACKAGING_TOOLBAR = this.id + CONSTANT.TOOLBAR.initCap();
		this.PACKAGING_CARD = this.id + CONSTANT.CARD.initCap();
		
		this.PACKAGING_CONTAINER = this.id + 'Container';
		this.PACKAGING_PRODUCT = this.id + CONSTANT.PRODUCT.initCap();
		this.PACKAGING_PRODUCT_SERIAL_NUMBER = this.PACKAGING_PRODUCT + 'SerialNumber';
		this.PACKAGING_PRODUCT_SERIAL_DATE = this.PACKAGING_PRODUCT + 'SerialDate';
		this.PACKAGING_PRODUCT_DESC = this.PACKAGING_PRODUCT + 'Desc';
		this.PACKAGING_QUANTITY = this.id + CONSTANT.QUANTITY.initCap();

		this.TAG_CARD = this.id + 'Tag' + CONSTANT.CARD.initCap();

		this.VIEWER = this.id + 'Viewer';
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

		let print = toolbar.addButton2(ACTION.PRINT, () => this.print());
		print.style.display = 'none';


		toolbar.addButton2(ACTION.SAVE, () =>  {
			print.style.display = 'block';
			this.save(div);
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

		let product = this.createInput(this.PACKAGING_PRODUCT, "Contenido");
		table.addCell(product, 2);
		product.addIconButton(MATERIAL_ICONS.QR_CODE_SCANNER, () => this.openBarcode());

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


		// product.addEventListener(EVENT.SELECT,(e) => {
		// 	let desc = this.getElement(this.PACKAGING_PRODUCT_DESC);
		// 	desc.innerHTML = e.detail.description;
		// });

		table.addRow();

		let lote = this.createInput(this.PACKAGING_PRODUCT_SERIAL_NUMBER, "Nº Lote");
		lote.addEventListener(EVENT.CHANGE, (e) => {
			if(this.packaging.item)
				this.packaging.item.serialNumber = lote.value;
		});
		table.addCell(lote);

		let date = this.createDate(this.PACKAGING_PRODUCT_SERIAL_DATE, "Fecha Lote");
		date.addEventListener(EVENT.CHANGE, (e) => {
			if(this.packaging.item)
				this.packaging.item.serialDate = date.value;
		});
		table.addCell(date);

		table.addRow();	

		let quantity = this.createInput(this.PACKAGING_QUANTITY, MSG.QUANTITY);
		table.addCell(quantity, 2);
		
		table.addRow();

		let container = this.createSelect(this.PACKAGING_CONTAINER, "Contenedor");
		container.setAlias("id", "name");

		table.addCell(container, 2);

		product.addEventListener(EVENT.CHANGE, () => this.changeProduct());
	}

	changeProduct() {
		const product = this.getElement(this.PACKAGING_PRODUCT);
		this.barcode = product.value;
		let data = { barcode: product.value};
		getPackaging(data).then(r => {
			const container = this.getElement(this.PACKAGING_CONTAINER);
			const lote = this.getElement(this.PACKAGING_PRODUCT_SERIAL_NUMBER);
			const date = this.getElement(this.PACKAGING_PRODUCT_SERIAL_DATE);
			const quantity = this.getElement(this.PACKAGING_QUANTITY);

			this.packaging = r;
			let val = r.base.description || r.base.name;
			product.value = val || '';
			container.setOptions(r.containers);
			lote.value = r.item.serialNumber;
			date.value = r.item.serialDate;
			container.value = r.containers[0].id;
			this.item = r.item.id;
			this.contenedor = container.value;
			quantity.value = r.containers[0].itemComposition[0].quantity;
			this.packaging.container = r.containers[0];
			this.packaging.quantity = r.containers[0].itemComposition[0].quantity;
		}).catch(e => this.showError(e));
	}

	openBarcode() {
		mobileAction({ action: MOBILE_ACTION.BARCODE, selector: 'aon-mobile-packaging' });
	}

	setBarcodeData(barcodeStr) {
		try {
			
			if(typeof barcodeStr === 'string') {
				barcodeStr = JSON.parse(barcodeStr);
			}

			const {text, format, cancelled} = barcodeStr;
			if(!cancelled) {
				const product = this.getElement(this.PACKAGING_PRODUCT);
				product.value = text;
				this.changeProduct();
			}
		} catch (error) {
			this.showError(error);
		}
	}

	buildTag(parent){
		let card = this.createCard(this.TAG_CARD, MSG.TAG);
		parent.appendChild(card);

		// let div = this.createElement(TAG.DIV);

		// let viewer = new AonViewer();
		// viewer.type = 'application/pdf';			

		let json = {
			item: this.packaging.item.id,
			container: this.packaging.container.id,
			quantity: this.getElement(this.PACKAGING_QUANTITY).value,
			barcode: this.packaging.base.barcode,
			domain_id: LS.getDomainId(),
			domain_name: LS.getDomainName(),
			login: LS.getDomainLogin()
		};

		// viewer.file = '/ms/api/download_packaging_pdf?json=' + btoa(JSON.stringify(json));

		// viewer.width = div.offsetWidth;

		// div.appendChild(viewer);
		// card.setContent(div);

		let w = this.getElement(card.CONTENT).offsetWidth;
		let type = 'application/pdf';
		let url = '/ms/api/download_packaging_pdf?json=' + btoa(JSON.stringify(json));
		card.setContentHTML(`<aon-viewer id=${this.VIEWER} type="${type}" file="${url}" width="${w}"></aon-viewer>`);
		// fileCard.cleanSection2();
		// fileCard.addTitleButton('Visualizar', 'visibility_off', false, () => this.closeFileCard());
	}
	
	// ACTIONS

	print() {
		this.getElement(this.VIEWER).printDocument();
	}

	back() {

	}

	save(div) {
		this.packaging.quantity = this.getElement(this.PACKAGING_QUANTITY).value;
		savePackaging(this.packaging).then(r => {
			this.packaging = r;
			this.buildTag(div);
		})
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

	createDate(id, title) {
		let date = new AonDate();
		date.id = id;
		date.title = title;
		return date;
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