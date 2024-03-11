import {AonElement} from '../../components/AonElement.js';
import { ToolbarType} from '../../models/enums.js';

import {AonToolbar} from "../../components/aon-toolbar.js";
import {AonCard} from "../../components/aon-card.js";
import {AonButton} from "../../components/aon-button.js";

import {CONSTANT, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js'; 

import * as ACTION from '../actions.js';

import { AonBasicTable } from '../../components/aon-basic-table.js';
import { AonIconButton } from '../../components/aon-icon-button.js';
import { AonTabs } from '../../components/aon-tabs.js';
import { getDeliveries, getDelivery } from '../../services/warehouseService.js';
import { acceptDeliveryPackaging, getDeliveryPackaging, getItem, getPackaging, getProducts, saveDeliveryPackaging } from '../../services/productService.js';
import { AonDialog } from '../../components/aon-dialog.js';
import { getSalesDetails } from '../../services/salesService.js';
import { A } from '../../environments/aonTag.js';

import * as WAREHOUSE_OPTION from '../warehouse/WarehouseOptions.js';
import { AonMobileDeliveryPackagingList } from './aon-mobile-delivery-packaging-list.js';
import { AonNewSelect } from '../../components/aon-new-select.js';
import { AonNewInput } from '../../components/aon-new-input.js';
import { AonNewNumber } from '../../components/aon-new-number.js';

export class AonMobileDelivery extends AonElement {

	DELIVERY_TOOLBAR;
	DELIVERY_CARD;
	DETAIL_TABLE;
	DELIVERY_SAVE_BUTTON;
	DELIVERY_TABS
	DELIVERY_TABS_BUTTON;
	PACKAGING_PRODUCT
	PACKAGING_SOURCE_PRODUCT
	delivery;
	packaging;
	salesDetails;

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
		getSalesDetails({delivery: this.delivery.id, status:['PENDING','PARTIAL_SETTLED']})
		.then(r => this.salesDetails = r);
    }

	initialize() {
		this.id = this.id || 'aonDelivery';
		this.DELIVERY_TOOLBAR = this.id + CONSTANT.TOOLBAR.initCap();
		this.DELIVERY_CARD = this.id + CONSTANT.CARD.initCap();
		this.DETAIL_TABLE = this.DELIVERY_CARD + 'DetailTable';
		this.delivery = this.delivery || {};
		this.packaging = {};
		this.DELIVERY_SAVE_BUTTON = this.id + 'DeliverySaveButton';
		this.PACKAGING_PRODUCT = this.id + 'PackagingProduct';
		this.PACKAGING_SOURCE_PRODUCT = this.id + 'PackagingSourceProduct';
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
		toolbar.id = this.DELIVERY_TOOLBAR;
		toolbar.type = ToolbarType.SECONDARY;
		toolbar.title = this.delivery.reference; 
		this.appendChild(toolbar);
		// toolbar.addButton2(ACTION.SAVE, () => this.save());
		toolbar.addButton2(ACTION.ACCEPT, () => this.accept());
		toolbar.addButton2(ACTION.BACK, () => this.back());
		this.getApplication().addFloatOption(ACTION.ADD, () => this.addPackaging())

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
			if(this.delivery.details[i].description.isEmpty()) {
				this.delivery.details[i].description = this.delivery.details[i].item.description.isEmpty()
					? this.delivery.details[i].item.product.name
					: this.delivery.details[i].item.description;
			}
			span.innerHTML = this.delivery.details[i].description;
			table.addCell(span);

			let span2 = this.createElement(TAG.SPAN);
			span2.innerHTML = this.delivery.details[i].quantity;
			table.addCell(span2);
		}
	}

	buildPackaging(parent){
		let div = this.createElement(TAG.DIV, "aonPackageDiv")
		let packagingList = new AonMobileDeliveryPackagingList();
		packagingList.setToolbar(this.DELIVERY_TOOLBAR);
		packagingList.setPackages(this.delivery.packaging);
		div.appendChild(packagingList);
		parent.appendChild(div);
	}


	setDelivery(delivery) {
		this.delivery = delivery;
	}

	// ACTIONS

	accept() {
		let d = this.getApplication().getDialog();
   	 	d.clear();
    	if(!this.isMobile()) d.width = '400px';
    	d.setTitle(MSG.ACCEPT);
   	 	d.setContentHTML(`Estás seguro de finalizar el proceso.`);
    	d.addAcceptAction(() => {
			acceptDeliveryPackaging({id:this.delivery.id}).then(this.back());
    	});
    	d.open();
	}

	back() {
		this.getApplication().getParent().aonDelivery();
	}

	backToDelivery(delivery) {
		this.packaging = {};
		if(!delivery) {
			this.clear();
			this.build();
			getSalesDetails({delivery: this.delivery.id, status:['PENDING','PARTIAL_SETTLED']})
			.then(sd => this.salesDetails = sd);
		} else {
			let data = {
				id: delivery,
				full: true
			}
			getDelivery(data).then(r => {
				this.setDelivery(r);
				this.clear();
				this.build();
				getSalesDetails({delivery: this.delivery.id, status:['PENDING','PARTIAL_SETTLED']})
				.then(sd => this.salesDetails = sd);
			});
		}
	}

	// Create Components

	createCard(id, title) {
		let card = new AonCard();
		card.id = id;
		card.title = title;
		return card;
	}

	createSelect(id, title) {
		let select = new AonNewSelect();
		select.id = id;
		select.title = title;
		return select;
	}

	createInput(id, title) {
		let input = new AonNewInput();
		input.id = id;
		input.description = title;
		input.title = title;
		return input;
	}

	createNumber(id, title) {
		let number = new AonNewNumber();
		number.id = id;
		number.description = title;
		number.title = title;
		return number;
	}

	addPackaging() {
		this.getApplication().removeFloatOption();
		this.clear();
		let toolbar = new AonToolbar();
		toolbar.id = this.DELIVERY_TOOLBAR;
		toolbar.type = ToolbarType.SECONDARY;
		toolbar.title = 'Seleccionar Envase'; // this.delivery.reference; 
		this.appendChild(toolbar);
		toolbar.addButton2(ACTION.BACK, () => this.backToDelivery());

		let div = this.createDiv();
		this.appendChild(div);

		let packagingCard = new AonCard();
		packagingCard.id = this.id  + 'PackagingCard';
		packagingCard.title = 'Envase';
		div.appendChild(packagingCard);

		let packagingDiv = this.createDiv();
		packagingCard.setContent(packagingDiv);
		let table = new AonBasicTable();
		table.id = this.id + 'Envasesss';
		packagingDiv.appendChild(table);
		
		let table2 = new AonBasicTable();
		table2.id = this.id + 'Envasesss22';
		packagingDiv.appendChild(table2);

		this.buildProductPackaging(table, table2);


		let pendingCard = new AonCard();
		pendingCard.id = this.id  + 'PendingCard';
		pendingCard.title = 'Pendiente';
		div.appendChild(pendingCard);
		let pendingTable = new AonBasicTable();
		pendingTable.id = this.id + 'PendingTable';
		pendingCard.setContent(pendingTable);
		this.buildPendingTable(pendingTable);

		let saveButton = new AonButton();
		saveButton.id = this.DELIVERY_SAVE_BUTTON;
		saveButton.title = 'Añadir al Albarán';
		saveButton.disabled = true;
		saveButton.style.margin = '10px';	
		saveButton.style.right = '0px';
		saveButton.style.position = 'absolute';

		div.appendChild(saveButton);	
		saveButton.addEventListener(EVENT.CLICK, () => {
			this.packaging.delivery = this.delivery.id;
			saveDeliveryPackaging(this.packaging).then(r =>{
				this.backToDelivery(this.delivery.id);
			}).catch(e => this.backToDelivery(this.delivery.id));

			//RELOAD DELIVERY
			// let option = WAREHOUSE_OPTION.DELIVERY;
			// option.delivery = this.delivery.id;
			// this.getApplication().selectOption(option)

		});
	}

	buildProductPackaging(table, table2) {
		table.addRow();
		let product = this.createInput(this.PACKAGING_PRODUCT, MSG.CONTAINER + ' (SSCC)');
		product.id = this.DELIVERY_PRODUCT;
		table.addCell(product);
		product.addIcon(MATERIAL_ICONS.QR_CODE_SCANNER, undefined, () => this.openBarcode(product));	
		
		
		product.addEventListener(EVENT.CHANGE, () => {
			// buscar palet (item) y su contenido.
			// si existe albaran comprobar que esté en el albaran. 
			//		si está en el albaran. añadir nuevos productos al palet 
			//   	si no esta en ningun albaran añadir el palet con lo que tenga al albarán 
			//			comprobar que lo que tenga el albarán es lo que se quiere añadir si no ERROR
			//  	si está en otro albarán ERROR
			// si no existe albarán 
			//   	si no esta en ningun albaran añadir el palet con lo que tenga al albarán 
			//			comprobar que lo que tenga el albarán es lo que se quiere añadir si no ERROR
			//  	si está en otro albarán ERROR
			
			let data = { 
				sscc: product.value,
				delivery: this.delivery.id,
				// product: detail.item.product.id
			};
			getDeliveryPackaging(data).then(r => {
				// si r.delivery no esta vacio. añadir nuevos productos al palet 
				// si r.delivery está vacio. añadir el palet con lo que tenga al albarán.
				//		comprobar que lo que tenga el albarán es lo que se quiere añadir si no ERROR
				product.setDisabled(true);
				product.value = r.item.name;
				this.packaging.container = {
					item: r.item.id
				};
				this.buildPendingTable(this.getElement(this.id + 'PendingTable'));
				Array.prototype.forEach.call(r.item.itemComposition, i => {
					table2.addRow();
					let span = this.createSpan();
					span.innerHTML = i.composition.product.code + ' #' + i.composition.serialNumber;
					table2.addCell(span);
					let span2 = this.createSpan();
					span2.innerHTML = i.quantity;
					table2.addCell(span2);
					let saveButton = this.getElement(this.DELIVERY_SAVE_BUTTON)
					saveButton.setDisabled(false);
					if(!r.delivery || !r.delivery.id) {	
						for(let j = 0; j < this.salesDetails.length; j++) {
							if(this.salesDetails[j].item.product.id === i.composition.product.id) {
								this.salesDetails[j].delivered = this.salesDetails[j].delivered + i.quantity;
							}
						}						
					}
				});

				if(!r.delivery || !r.delivery.id) {	
					this.buildPendingTable(this.getElement(this.id + 'PendingTable'));
				}

			}).catch(e => {
				product.value = '';
				this.showError(e)
			});
		});
		

		let addButton = new AonIconButton();
		addButton.id = this.id + 'AddButton';
		addButton.title = MSG.ADD;
		addButton.icon = MATERIAL_ICONS.ADD_CIRCLE_OUTLINE;
		addButton.addEventListener(EVENT.CLICK, () => {
			this.packaging = {};
			let cont = 1;

			while(table.rows >= cont) {
				table.removeRow(table.rows);
			}

			table2.removeRows();
			this.buildNewPackaging(table, table2);
			let saveButton = this.getElement(this.DELIVERY_SAVE_BUTTON)
			saveButton.setDisabled(true);
			this.packaging.container = undefined;
			getSalesDetails({delivery: this.delivery.id, status:['PENDING','PARTIAL_SETTLED']})
			.then(sd => {
				this.salesDetails = sd
				this.buildPendingTable(this.getElement(this.id + 'PendingTable'));
			});
			
		});
		table.addCell(addButton);
	}

	buildNewPackaging(table, table2) {
		table.addRow();
		let envaseSelect = new AonNewSelect();
		envaseSelect.id = this.id + 'DialogEnvase';
		envaseSelect.title = 'Nuevo Envase';
		envaseSelect.addEventListener(EVENT.SELECT, () => {
			let cont = 1;
			while(table.rows > cont) {
				table.removeRow(table.rows);
			}
			this.packaging.container = {
				product: envaseSelect.value
			};
			this.buildPendingTable(this.getElement(this.id + 'PendingTable'));
			// this.buildNewPackagingContent(table, detail);
		});
		
		let td = table.addCell(envaseSelect);
		td.style.width = '100%';

		let pButton = new AonIconButton();
		pButton.id = this.id + 'ProductButton';
		pButton.title = MSG.ADD;
		pButton.icon = MATERIAL_ICONS.QR_CODE_SCANNER;
		pButton.addEventListener(EVENT.CLICK, () => {
			this.packaging.container = {};
			let cont = 1;

			while(table.rows >= cont) {
				table.removeRow(table.rows);
			}
			table2.removeRows();
			this.buildProductPackaging(table, table2);
			this.packaging.container = undefined;
			getSalesDetails({delivery: this.delivery.id, status:['PENDING','PARTIAL_SETTLED']})
			.then(sd => {
				this.salesDetails = sd
				this.buildPendingTable(this.getElement(this.id + 'PendingTable'));
			});
		});
		table.addCell(pButton);

		let productFilter = {
			type: 'AUXILIARY',
			mode: 'CUSTOMER',
			registry: this.delivery.customer.id
		};
		getProducts(productFilter).then(products => {
			envaseSelect.setOptions(products.map(p => {
				return {
					value: p.id,
					name: p.code + ' - ' + p.name
				  }
			}))
		});
	}

	buildPendingTable(pendingTable) {
		pendingTable.removeRows();
		for(let i = 0; i < this.salesDetails.length; i++) {
			let pending =  this.salesDetails[i].quantity - this.salesDetails[i].delivered;
			if(pending > 0) {
				pendingTable.addRow();
				
				let span = this.createSpan();
				span.innerHTML = this.salesDetails[i].item.product.code;
				pendingTable.addCell(span);

				let span2 = this.createSpan();
				span2.innerHTML = pending;
				pendingTable.addCell(span2)

				let aonIconButton = this.createAonElement(new AonIconButton(), 'icon' + i, 'icon');
				aonIconButton.icon = MATERIAL_ICONS.ADD;
				aonIconButton.addEventListener(EVENT.CLICK, () => {
					this.sourceDialog(this.salesDetails[i]);
				});
				pendingTable.addCell(aonIconButton);
				aonIconButton.setDisabled(this.packaging.container == undefined);
			}	
		}
	}


	sourceDialog(detail) {
		let id = this.id + 'SourceDialog';
		let dialog = this.getElement(id);
		if(!dialog) {
			dialog = new AonDialog();
			dialog.id = this.id + 'SourceDialog';
			this.appendChild(dialog);
		} else {
			dialog.clear();
		}
		dialog.setTitle('Origen');
		
		let table = new AonBasicTable();
		table.id = this.id + 'SourceTable';
		dialog.setContent(table);
	
		table.addRow();
	
		let product = this.createInput(this.PACKAGING_SOURCE_PRODUCT, "Envase Origen");
		product.id = id + 'Envase';
		table.addCell(product);
		product.addIconButton(MATERIAL_ICONS.QR_CODE_SCANNER, () => this.openBarcode(product));
	
		let source;
		let quantity = 0;
		let composition = [];
		product.addEventListener(EVENT.CHANGE, () => {
			product.setDisabled(true);
			let data = { 
				sscc: product.value,
				product: detail.item.product.id
			};
			getDeliveryPackaging(data).then(r => {
				// si no esta en ningun albaran  
					// comprobar que lo que tenga el albarán es lo que se quiere añadir si no ERROR
					//añadir cantidad 

				Array.prototype.forEach.call(r.item.itemComposition, i => {
					if(i.composition.product.id === data.product) {
						let saveButton = this.getElement(this.DELIVERY_SAVE_BUTTON)
						saveButton.setDisabled(false);
						let pendingQuantity = detail.quantity - detail.delivered;
						let quantityValue = i.quantity > pendingQuantity
							? pendingQuantity : i.quantity;
						
						let object = i;
						object.quantity = quantityValue;
						composition.push(object);

						quantity = quantity + quantityValue;
					}
				});
				source = r.item.id;
			}).catch(e => {
				product.value = "";
				product.setDisabled(false);
				this.showError(e);
			});
		});

		let magicButton = new AonIconButton();
		magicButton.id = this.id + 'MagicButton';
		magicButton.title = "magia";
		magicButton.icon = MATERIAL_ICONS.AUTO_FIX_HIGH;
		magicButton.addEventListener(EVENT.CLICK, () => {
			alert("MAGIA")
		});
		table.addCell(magicButton);

		dialog.addAcceptAction(() => {
			if(source) {
				let contentObject = {
					source,
					composition
				};
				composition.forEach(c => {
					// alert(JSON.stringify(c));
					let table2 = this.getElement(this.id + 'Envasesss22');
					table2.addRow();
					let span = this.createSpan();
					span.innerHTML = detail.item.product.code + ' #' + c.composition.serialNumber;
					table2.addCell(span);
					let span2 = this.createSpan();
					span2.innerHTML = c.quantity;
					table2.addCell(span2);
				});

				if(this.packaging.content) {
					this.packaging.content.push(contentObject);
				} else this.packaging.content = [contentObject];
	
				// alert(JSON.stringify(this.packaging.content));
				for(let j = 0; j < this.salesDetails.length; j++) {
					if(this.salesDetails[j].id === detail.id) {
						this.salesDetails[j].delivered = this.salesDetails[j].delivered + quantity;
					}
				}		

				this.buildPendingTable(this.getElement(this.id + 'PendingTable'));
			}
		});
		dialog.open();
	}

	barcodeId;
	openBarcode(element) {
		this.barcodeId = element.id;
		mobileAction({ action: MOBILE_ACTION.BARCODE, selector: TAG.AON_MOBILE_DELIVERY });
	}

	setBarcodeData(barcodeStr) {
		try {
			if(typeof barcodeStr === 'string') {
				barcodeStr = JSON.parse(barcodeStr);
			}

			const {text, format, cancelled} = barcodeStr;
			if(!cancelled) {
				const element = this.getElement(this.barcodeId);
				element.value = text;
			}
		} catch (error) {
			this.showError(error);
		}
	}



}

if(!window.customElements.get(TAG.AON_MOBILE_DELIVERY)){
	window.customElements.define(TAG.AON_MOBILE_DELIVERY, AonMobileDelivery);
}