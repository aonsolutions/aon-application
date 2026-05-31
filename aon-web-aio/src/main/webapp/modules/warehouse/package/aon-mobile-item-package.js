import { AonElement } from '../../../components/AonElement.js';

import { CONSTANT, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../../environments/environments.js'; 

import { AonBasicTable } from '../../../components/aon-basic-table.js';
import * as LS from '../../../services/localStorageService.js';
import {getItems, getItemsByBarcode, getPackage, openFileUrl} from '../../../services/service.js';

import * as ACTION from '../../actions.js';

import * as UA from '../../../services/userAgentService.js';
import { printFile } from '../../../services/actionService.js';
import { AonToolbar } from '../../../components/aon-toolbar.js';
import { ToolbarType } from '../../../models/enums.js';
import { createCard, createInput, createQuantity, createSelect } from '../../../components/CreateComponent.js';
import { round } from '../../../services/utils.js';
import { addPackageStock, adjustComposition, deletePackage, getWarehouse, getWarehouses, movePackageStock, saveStock } from '../../../services/warehouseService.js';
import { AonIconButton } from '../../../components/aon-icon-button.js';


export class AonMobileItemPackage extends AonElement {

	PACKAGE_TOOLBAR;
	PACKAGE_DIV;

	PACKAGE_CARD;
	PACKAGE_TABLE;

	PACKAGE_PRODUCT;
	PACKAGE_QUANTITY;
	PACKAGE_SERIAL_NUMBER;
	PACKAGE_SERIAL_DATE;
	PACKAGE_COMPOSITION_TABLE;
	PACKAGE_WAREHOUSE;
	PACKAGE_ITEM_WAREHOUSE;


	COMPOSITION;
	COMPOSITION_CARD;
	COMPOSITION_TABLE;
	COMPOSITION_ITEM;
	COMPOSITION_QUANTITY;

	COMPOSITION_ADJUST_QUANTITY

	COMPOSITION_ADD_PRODUCT;
	COMPOSITION_ADD_ITEM;
	COMPOSITION_ADD_QUANTITY;

	TAG;
	TAG_CARD;

	fileUrl;

	itemPackage;

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

	disconnectedCallback() {

    }

	initialize() {
		this.id = this.id || 'aonPackage';
		this.PACKAGE_DIV = this.id + CONSTANT.DIV.initCap();
		this.PACKAGE_TOOLBAR = this.id + CONSTANT.TOOLBAR.initCap();
		this.PACKAGE_CARD = this.id + CONSTANT.CARD.initCap();
		this.PACKAGE_TABLE = this.id + CONSTANT.TABLE.initCap();
		this.PACKAGE_PRODUCT = this.id + CONSTANT.PRODUCT.initCap();	
		this.PACKAGE_QUANTITY = this.id + CONSTANT.QUANTITY.initCap();
		this.PACKAGE_SERIAL_NUMBER = this.id + 'SerialNumber';
		this.PACKAGE_SERIAL_DATE = this.id + 'SerialDate';
		this.PACKAGE_COMPOSITION_TABLE = this.id + 'CompositionTable';
		this.PACKAGE_WAREHOUSE = this.id + 'Warehouse';
		this.PACKAGE_ITEM_WAREHOUSE = this.id + 'ItemWarehouse';
		
		this.COMPOSITION = this.id + CONSTANT.COMPOSITION.initCap();
		this.COMPOSITION_CARD = this.COMPOSITION + CONSTANT.CARD.initCap();
		this.COMPOSITION_TABLE = this.COMPOSITION + CONSTANT.TABLE.initCap();
		this.COMPOSITION_ITEM = this.COMPOSITION + CONSTANT.ITEM.initCap();	
		this.COMPOSITION_QUANTITY = this.COMPOSITION + CONSTANT.QUANTITY.initCap();
		this.COMPOSITION_ADJUST_QUANTITY = this.COMPOSITION + 'Adjust' + CONSTANT.QUANTITY.initCap();

		this.COMPOSITION_ADD_PRODUCT = this.COMPOSITION + 'AddProduct';
		this.COMPOSITION_ADD_ITEM = this.COMPOSITION + 'AddItem';
		this.COMPOSITION_ADD_QUANTITY = this.COMPOSITION + 'AddQuantity';

		this.TAG = this.id + CONSTANT.TAG.initCap();
		this.TAG_CARD = this.TAG + CONSTANT.CARD.initCap();

		this.itemPackage = this.itemPackage || {};
	}

	build() {
		this.buildToolbar();
		let div = this.createDiv(this.PACKAGE_DIV);		
		this.appendChild(div);
		this.buildPackage(div);		
	}

	buildToolbar() {
		let toolbar = new AonToolbar();
		toolbar.id = this.PACKAGE_TOOLBAR;
		toolbar.type = ToolbarType.SECONDARY;
		toolbar.title = MSG.PACKAGE; 
		this.appendChild(toolbar);

		if(!this.itemPackage.stock || this.itemPackage.stock.quantity == 0) {
			toolbar.addButton2(ACTION.ADD_TO_STOCK, () => this.addToStock());
		} else toolbar.addButton2(ACTION.MOVE_STOCK, () => this.moveToWarehouse());

		if(!this.itemPackage.delivery && this.itemPackage.status === 'ACTIVE')
			toolbar.addButton2(ACTION.DELETE, () => this.delete());
		if(this.back) toolbar.addButton2(ACTION.BACK, () => this.back());
	}

  	buildPackage(parent){
		this.buildPackageGeneral(parent);
		this.buildPackageComposition(parent);
	}

	buildPackageGeneral(parent){
		let card = createCard(this.PACKAGE_CARD, "Datos Envase", parent);
		card.addTitleButton(MSG.PRINT, MATERIAL_ICONS.PRINT, false, () => this.print());

		let table = new AonBasicTable();
		table.id = this.PACKAGE_TABLE;
		card.setContent(table);

		if(this.itemPackage.delivery) {
			table.addRow();

			let deliveryDiv = this.createDiv();
			deliveryDiv.innerHTML = 'Incluido en el albarán ' + this.itemPackage.delivery.reference;
			deliveryDiv.style.color = 'red';
			deliveryDiv.style.fontWeight = 'bold';
			table.addCell(deliveryDiv);
		} else if(this.itemPackage.status === 'DISCONTINUED') {
			table.addRow();

			let deliveryDiv = this.createDiv();
			deliveryDiv.innerHTML = 'El envase está descatalogado';
			deliveryDiv.style.color = 'red';
			deliveryDiv.style.fontWeight = 'bold';
			table.addCell(deliveryDiv);	
		}

		table.addRow();

		let product = createInput(this.PACKAGE_PRODUCT, "Envase");
		product.value = this.itemPackage.name;
		product.disabled = true;
		table.addCell(product);

		table.addRow();

		let serialNumber = createInput(this.PACKAGE_SERIAL_NUMBER, "SSCC");
		serialNumber.value = this.itemPackage.serialNumber;
		serialNumber.disabled = true;
		table.addCell(serialNumber);

		if(this.itemPackage.stock && this.itemPackage.stock.quantity > 0) {
			getWarehouse(this.itemPackage.stock.warehouse).then(w => {
				table.addRow();

				let warehouse = createInput(this.PACKAGE_ITEM_WAREHOUSE, MSG.WAREHOUSE);
				warehouse.value = w.name;
				warehouse.disabled = true;
				table.addCell(warehouse);
			}).catch(e => this.showError(e));
		}	
	}

	buildPackageComposition(parent){
		let card = createCard(this.COMPOSITION_CARD, MSG.COMPOSITION, parent);

		let div = this.createDiv();
		card.setContent(div);

		let table = new AonBasicTable();
		table.id = this.PACKAGE_COMPOSITION_TABLE;
		div.appendChild(table);
		
		this.itemPackage.itemComposition.forEach((c, i) => {
			table.addRow();
			
			let span = this.createSpan();
			span.innerHTML = c.description;
			let td = table.addCell(span);
			td.style.paddingBottom = '10px';
			td.style.paddingRight = '10px';

			let span2 = this.createSpan();
			span2.innerHTML = this.getFormat(c.composition, c.quantity);
			span2.style.fontWeight = 'bold';
			let td2 = table.addCell(span2)
			td2.style.paddingBottom = '10px';
			if(!this.itemPackage.delivery && this.itemPackage.status === 'ACTIVE') {
				let aonIconButton = this.createAonElement(new AonIconButton(), 'AAAAicon' + i, 'icon'); 
				aonIconButton.icon = MATERIAL_ICONS.DRAFT_ORDERS;
				aonIconButton.addEventListener(EVENT.CLICK, () => {
					this.adjustDialog(c);
				});
				let td3 = table.addCell(aonIconButton);
				td3.style.paddingBottom = '10px';
			}
		});

		if(!this.itemPackage.delivery && this.itemPackage.status === 'ACTIVE') {
			table.addRow();

			let aonIconButton = this.createAonElement(new AonIconButton(), 'AddIcon', 'icon'); 
			aonIconButton.icon = MATERIAL_ICONS.ADD_CIRCLE;
			aonIconButton.addEventListener(EVENT.CLICK, () => {
				this.addDialog();
			});
			let td3 = table.addCell(aonIconButton);
			td3.style.paddingBottom = '10px';

		}
	}

	getFormat(item, quantity) {
		let stockUnitTag = item.stockUnitTag.id;
		let packFormatTag = item.packFormatTag.id;
		let packUnitsTag = item.packUnitsTag.id;
		let packUnits = item.packUnits;
		let packMeasurementTag = item.packMeasurementTag.id;
		let packMeasurement = item.packMeasurement;
		
		let formatQuantity = quantity;
		if(stockUnitTag === packMeasurementTag) {
			formatQuantity = quantity / packMeasurement;
			formatQuantity = formatQuantity / packUnits;	
		} else if(stockUnitTag === packUnitsTag) {
			formatQuantity = quantity / packUnits;	
		}
		return round(formatQuantity);
	}

	// ACTIONS

	save() {
	
	}

	addToStock() {
		getWarehouses()
		.then(warehouses => {
			let aonWarehouse = this.getElement('aonWarehouse');

			let warehouse = createSelect(this.PACKAGE_WAREHOUSE, MSG.WAREHOUSE);
			warehouse.setAlias("id", "name");
			warehouse.setOptions(warehouses);
			warehouse.value = warehouses[0].id;

			let d = document.getElementById(aonWarehouse.DIALOG);
			d.clear();
			if(!this.isMobile()) d.width = '400px';
			d.setTitle(MSG.ADD_TO_STOCK);
			d.setContent(warehouse);
			d.addAcceptAction(() => {
				let data = {
					item: this.itemPackage.id,
					warehouse: warehouse.value,
				}
				addPackageStock(data)
				.then(stock => {})
				.catch(err => this.showError(err));
			});
			d.open();
		})
		.catch(e => this.showError(e));
	}

	moveToWarehouse() {
		getWarehouses()
		.then(warehouses => {
			let aonWarehouse = this.getElement('aonWarehouse');

			let warehouse = createSelect(this.PACKAGE_WAREHOUSE, MSG.WAREHOUSE);
			warehouse.setAlias("id", "name");
			warehouse.setOptions(warehouses);
			warehouse.value = warehouses[0].id;

			let d = document.getElementById(aonWarehouse.DIALOG);
			d.clear();
			if(!this.isMobile()) d.width = '400px';
			d.setTitle(MSG.MOVE_STOCK);
			d.setContent(warehouse);
			d.addAcceptAction(() => {
				let warehouseItem = this.getElement(this.PACKAGE_ITEM_WAREHOUSE);			
				warehouseItem.setValue(warehouse.getValueObject().name);
				let data = {
					item: this.itemPackage.id,
					sourceWarehouse: this.itemPackage.stock.warehouse,
					destinyWarehouse: warehouse.value,
				}
				movePackageStock(data)
				.then(stock => {})
				.catch(err => this.showError(err));
			});
			d.open();
		})
		.catch(e => this.showError(e));
	}

	print() {
		let json = {
			delivery: this.itemPackage.delivery
				? this.itemPackage.delivery.id : undefined,
			container: this.itemPackage.id,
			domain_id: LS.getDomainId(),
			domain_name: LS.getDomainName(),
			login: LS.getDomainLogin()
		};
		
		let fileUrl = '/ms/api/deliveryPackagingTag?json=' + btoa(JSON.stringify(json));
		if(UA.isAndroidApp()) {
			let file = {
				url: fileUrl,
				title: this.packaging.item.serialNumber
			};
			printFile(file);
		} else openFileUrl(fileUrl, 'application/pdf');
	}

	delete() {
		let aonWarehouse = this.getElement('aonWarehouse');
		let d = document.getElementById(aonWarehouse.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.DELETE);
		d.setContentHTML('¿Estás seguro de eliminar el Envase <b>' + this.itemPackage.serialNumber + '</b>? <p>También se restará el stock del contenido del envase.<p>');
		d.addAcceptAction(() => deletePackage(this.itemPackage.id)
			.then(() => {
				this.showMessage(MSG.DELETED_DATA);
				this.back();
			}).catch(e => this.showError(e))
		);
		d.open();
	}

	adjustDialog(composition) {
		let aonWarehouse = this.getElement('aonWarehouse');
		let quantityBox = createQuantity(this.COMPOSITION_ADJUST_QUANTITY, MSG.QUANTITY);
		quantityBox.addEventListener(EVENT.CHANGE, () => {
			quantityBox.setQuantityFormat(quantityBox.value);
		});
		quantityBox.setTags(composition.composition);

		let d = document.getElementById(aonWarehouse.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.ADJUST_QUANTITY);
		d.setContent(quantityBox);
		d.addAcceptAction(() => {
			composition.quantity = quantityBox.getQuantity();
			adjustComposition(composition)
			.then(() => {
				this.showMessage("Se ha actualizado correctamente.");
				let div = this.getElement(this.PACKAGE_DIV);	
				this.clearElement(div);
				this.buildPackage(div);
			}).catch(e => this.showError(e))
		});
		d.open();
	}

	addDialog() {
		let aonWarehouse = this.getElement('aonWarehouse');

		let div =  this.createDiv(this.id + 'AddDiv');

		let quantityBox = createQuantity(this.COMPOSITION_ADD_QUANTITY, MSG.QUANTITY);
		quantityBox.addEventListener(EVENT.CHANGE, () => {
			quantityBox.setQuantityFormat(quantityBox.value);
		});
		
		let itemSelect = createSelect(this.COMPOSITION_ADD_ITEM, "Lote");

		let product = createInput(this.COMPOSITION_ADD_PRODUCT, "Contenido");
		product.addEventListener(EVENT.CHANGE, () => {
			getItemsByBarcode({barcode: product.value}).then(items => {
				product.setValue(items[0].product.name);
				itemSelect.setAlias("id", "serialNumber");
				itemSelect.setOptions(items);
				itemSelect.addEventListener(EVENT.SELECT, () => {
					quantityBox.setTags(itemSelect.getValueObject());
					div.appendChild(quantityBox);
				})
				div.appendChild(itemSelect);
			});	
		});
		// product.addIcon(MATERIAL_ICONS.QR_CODE_SCANNER, undefined, () => this.openBarcode());

		div.appendChild(product);

		let d = document.getElementById(aonWarehouse.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.ADD_CONTENT);
		d.setContent(div);
		d.addAcceptAction(() => {	
			let item = itemSelect.getValueObject();
			let composition = {
				domain: this.itemPackage.domain.id,
				item: this.itemPackage.id,
				compositionItem: item.id,
				composition: item,
				description: item.product.name + ' #' + item.serialNumber,
				quantity: quantityBox.getQuantity(),
				sequence: this.itemPackage.itemComposition ? this.itemPackage.itemComposition.length + 1 : 1

			}
			adjustComposition(composition)
			.then(() => {
				let data = {id: this.itemPackage.id, full:true};
				getPackage(data).then(ip => {
					this.itemPackage = ip;
					this.showMessage("Se ha actualizado correctamente.");
					let div = this.getElement(this.PACKAGE_DIV);	
					this.clearElement(div);
					this.buildPackage(div);
				});
			}).catch(e => this.showError(e))
		});
		d.open();
	}


	setItemPackage(itemPackage) {
		this.itemPackage = itemPackage;
	}
}

if(!window.customElements.get(TAG.AON_MOBILE_ITEM_PACKAGE)){
	window.customElements.define(TAG.AON_MOBILE_ITEM_PACKAGE, AonMobileItemPackage);
}