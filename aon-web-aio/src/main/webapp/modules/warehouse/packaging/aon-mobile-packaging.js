import {AonElement} from '../../../components/AonElement.js';
import { ToolbarType} from '../../../models/enums.js';
import {AonToolbar} from "../../../components/aon-toolbar.js";
import {CONSTANT, MATERIAL_ICONS, MSG, TAG, EVENT} from '../../../environments/environments.js'; 
import {getPackaging, mobileAction, MOBILE_ACTION, savePackaging, openFileUrl} from '../../../services/service.js';
import { AonBasicTable } from '../../../components/aon-basic-table.js';
import { getWarehouses } from '../../../services/warehouseService.js';
import { createCard, createDate, createInput, createQuantity, createSelect } from '../../../components/CreateComponent.js';
import { openBarcode } from '../../../services/actionService.js';

import * as ACTION from '../../actions.js';
import * as LS from '../../../services/localStorageService.js';
import * as UA from '../../../services/userAgentService.js';

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
	PACKAGING_WAREHOUSE;
	PACKAGING_COPIES;

	TAG_CARD;

	VIEWER;

	warehouses;
	item;
	contenedor;
	barcode;
	packaging;
	packagingList;
	fileUrl;

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
		getWarehouses().then(warehouses => {
			this.warehouses = warehouses;
			this.build();
		})

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
		this.PACKAGING_WAREHOUSE = this.id + 'Warehouse';
		this.PACKAGING_COPIES = this.id + 'Copies';
		this.TAG_CARD = this.id + 'Tag' + CONSTANT.CARD.initCap();

		this.VIEWER = this.id + 'Viewer';

		this.packagingList = [];
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

		let downloadButton = toolbar.addButton2(ACTION.DOWNLOAD_PDF, () => this.print());
		downloadButton.style.display = 'none';

		let printButton = toolbar.addButton2(ACTION.PRINT, () => this.print());
		printButton.style.display = 'none';

		let saveButton = toolbar.addButton2(ACTION.SAVE, () =>  {
			this.save(div, saveButton, printButton, downloadButton);
		});
		// toolbar.addButton2(ACTION.BACK, () => this.back());

		this.buildPackaging(div);
	}

	resetPackaging() {
		this.clear();
		this.build();
	}

	buildPackaging(parent){
		let card = createCard(this.PACKAGING_CARD, MSG.PACKAGING, parent);

		let table = new AonBasicTable();
		table.id = this.PACKAGING_TABLE;
		card.setContent(table);

		table.addRow();

		let product = createInput(this.PACKAGING_PRODUCT, "Contenido");
		table.addCell(product, 2);
		product.addIcon(MATERIAL_ICONS.QR_CODE_SCANNER, undefined, () => this.openBarcode());
		// product.addIconButton(MATERIAL_ICONS.QR_CODE_SCANNER, () => this.openBarcode());



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

		let lote = createInput(this.PACKAGING_PRODUCT_SERIAL_NUMBER, "Nº Lote");
		lote.addEventListener(EVENT.CHANGE, (e) => {
			if(this.packaging.item)
				this.packaging.item.serialNumber = lote.value;
		});
		table.addCell(lote);

		let date = createDate(this.PACKAGING_PRODUCT_SERIAL_DATE, "Fecha Lote");
		date.addEventListener(EVENT.CHANGE, (e) => {
			if(this.packaging.item)
				this.packaging.item.serialDate = date.getDateValue();
		});
		table.addCell(date);

		table.addRow();	

		let quantity = createQuantity(this.PACKAGING_QUANTITY, MSG.QUANTITY);
		quantity.addEventListener(EVENT.CHANGE, () => {
			quantity.setQuantityFormat(quantity.value);
		});
		table.addCell(quantity, 2);
		
		table.addRow();

		let container = createSelect(this.PACKAGING_CONTAINER, "Contenedor");
		container.setAlias("id", "name");

		container.addEventListener(EVENT.SELECT, (event) => {
			quantity.setQuantity(event.detail.itemComposition[0].quantity);
			this.contenedor = container.value;
			this.packaging.container = event.detail;
			this.packaging.quantity = event.detail.itemComposition[0].quantity;
		});

		table.addCell(container, 2);

		product.addEventListener(EVENT.CHANGE, () => this.changeProduct());

		product.focus();

		table.addRow();
		if(this.warehouses.length > 1) {
			let warehouse = createSelect(this.PACKAGING_WAREHOUSE, MSG.WAREHOUSE);
			warehouse.setAlias("id", "name");
			warehouse.setOptions(this.warehouses);
			warehouse.value = this.warehouses[0].id;
			warehouse.addEventListener(EVENT.SELECT, (event) => {
				this.packaging.warehouse = event.detail;
			});
			table.addCell(warehouse, 2);

			table.addRow();
		}
		let copies = createInput(this.PACKAGING_COPIES, "Copias");
		copies.value = 1;

		table.addCell(copies, 2);

	}

	changeProduct() {
		let product = this.getElement(this.PACKAGING_PRODUCT);
		this.barcode = product.value;
		let data = { barcode: product.value};
		getPackaging(data).then(r => {
			let container = this.getElement(this.PACKAGING_CONTAINER);
			let lote = this.getElement(this.PACKAGING_PRODUCT_SERIAL_NUMBER);
			let date = this.getElement(this.PACKAGING_PRODUCT_SERIAL_DATE);
			let quantity = this.getElement(this.PACKAGING_QUANTITY);

			this.packaging = r;
			this.packaging.warehouse = this.warehouses[0];
			let val = r.base.description || r.base.name;
			product.setValue(val || '');
			container.setOptions(r.containers);
			lote.setValue(r.item.serialNumber);
			date.setDate(r.item.serialDate);
			container.value = r.containers[0].id;
			this.item = r.item.id;
			this.contenedor = container.value;
			quantity.setTags(this.packaging.item);
			quantity.setQuantity(r.containers[0].itemComposition[0].quantity);
			this.packaging.container = r.containers[0];
			this.packaging.quantity = r.containers[0].itemComposition[0].quantity;
		}).catch(e => this.showError(e));
	}

	openBarcode() {
		let ionicData = { action: MOBILE_ACTION.BARCODE, selector: TAG.AON_MOBILE_PACKAGING };
		if(UA.isAndroidApp()) {
		 	openBarcode(ionicData, (result) => console.log("aon mobile packaging - openbarcode - " + result.code));
		} else mobileAction(ionicData);
	}

	setBarcodeData(barcodeStr) {
		try {
			
			if(typeof barcodeStr === 'string') {
				barcodeStr = JSON.parse(barcodeStr);
			}

			const {text, format, cancelled} = barcodeStr;
			if(!cancelled) {
				// TODO
			}
		} catch (error) {
			this.showError(error);
		}
	}

	buildTag(parent){
		// let card = createCard(this.TAG_CARD, MSG.TAG, parent);

		// let div = this.createElement(TAG.DIV);

		// let viewer = new AonViewer();
		// viewer.type = 'application/pdf';			

		let arr = this.packagingList.map(p => {
			let json = {
				item: p.item.id,
				container: p.container.id,
				quantity: this.getElement(this.PACKAGING_QUANTITY).value,
				barcode: p.base.barcode,
			};
			return json;
		});
		let data = {
			packaging: arr,
			domain_id: LS.getDomainId(),
			domain_name: LS.getDomainName(),
			login: LS.getDomainLogin()
		}

	

		// viewer.file = '/ms/api/download_packaging_pdf?json=' + btoa(JSON.stringify(json));

		// viewer.width = div.offsetWidth;

		// div.appendChild(viewer);
		// card.setContent(div);

		// let w = this.getElement(card.CONTENT).offsetWidth;
		// let type = 'application/pdf';
		this.fileUrl  = '/ms/api/multiple_download_packaging_pdf?json=' + btoa(JSON.stringify(data));
		
		// card.setContentHTML(`<aon-viewer id=${this.VIEWER} type="${type}" file="${url}" width="${w}"></aon-viewer>`);
		// fileCard.cleanSection2();
		// fileCard.addTitleButton('Visualizar', 'visibility_off', false, () => this.closeFileCard());
	}
	
	// ACTIONS

	print() {
		//this.getElement(this.VIEWER).printDocument();
		openFileUrl(this.fileUrl, 'application/pdf');
	}

	back() {

	}

	save(div, saveButton, printButton, downloadButton) {
		if(!this.packaging.item.serialNumber){
			this.showError({message:`El número de Lote está vacío.`, type:CONSTANT.ERROR});
		} else {
			saveButton.style.display = 'none';
			this.getApplication().startLoader();
			this.packaging.quantity = this.getElement(this.PACKAGING_QUANTITY).getQuantity();
			this.packaging.copies = this.getElement(this.PACKAGING_COPIES).value || 1;
			savePackaging(this.packaging).then(r => {
				this.getApplication().stopLoader();
				printButton.style.display = 'block';
				downloadButton.style.display = 'block';
				this.packaging = r[0];
				this.packagingList = r;
				this.buildTag(div);
			});
		}
	}
}

if(!window.customElements.get(TAG.AON_MOBILE_PACKAGING)){
	window.customElements.define(TAG.AON_MOBILE_PACKAGING, AonMobilePackaging);
}