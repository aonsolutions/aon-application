import {AonElement} from '../../components/AonElement.js';
import { ToolbarType} from '../../models/enums.js';

import {AonToolbar} from "../../components/aon-toolbar.js";
import {AonCard} from "../../components/aon-card.js";
import {AonButton} from "../../components/aon-button.js";

import {CONSTANT, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js'; 

import * as ACTION from '../actions.js';
import { AonBasicTable } from '../../components/aon-basic-table.js';
import { AonIconButton } from '../../components/aon-icon-button.js';
import { AonDialog } from '../../components/aon-dialog.js';
import { getDeliveries, getDelivery } from '../../services/warehouseService.js';
import { getDeliveryPackaging, getItem, getPackaging, getProducts, saveDeliveryPackaging } from '../../services/productService.js';
import { MOBILE_ACTION, mobileAction } from '../../services/mobileService.js';
import { openBarcode } from '../../services/actionService.js';
import { createInput, createSelect } from '../../components/CreateComponent.js';

export class AonMobileSale extends AonElement {

	SALE_TOOLBAR;
	SALE_CARD;
	DETAIL_TABLE;
	DELIVERY_SAVE_BUTTON;
	DELIVERY_PRODUCT;
	DELIVERY_PRODUCT2;
	DELIVERY_QUANTITY;

	sale;
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
		this.id = this.id || 'aonSales';
		this.SALE_TOOLBAR = this.id + CONSTANT.TOOLBAR.initCap();
		this.SALE_CARD = this.id + CONSTANT.CARD.initCap();
		this.DETAIL_TABLE = this.SALE_CARD + 'DetailTable';
		this.sale = this.sale || {};
		this.packaging = {};
		this.DELIVERY_SAVE_BUTTON = this.id + 'DeliverySaveButton';
		this.DELIVERY_PRODUCT = this.id + 'DeliveryProduct';
		this.DELIVERY_PRODUCT2 = this.id + 'DeliveryProduct2';
		this.DELIVERY_QUANTITY = this.id + 'DeliveryQuantity';
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

	reload() {
		this.clear();
		this.build();
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
		dialog.setTitle('Seleccionar Envase');

		let div = this.createElement(TAG.DIV);
		dialog.setContent(div);

		let table = new AonBasicTable();
		table.id = this.id + 'Envasesss';
		div.appendChild(table);

		this.buildDelivery(table, detail);
		// this.buildProductPackaging(table);
		let saveButton = new AonButton();
		saveButton.id = this.DELIVERY_SAVE_BUTTON;
		saveButton.title = 'Añadir al Albarán';
		saveButton.disabled = true;
		div.appendChild(saveButton);	
		saveButton.addEventListener(EVENT.CLICK, () => {
			this.packaging.salesDetail = detail.id;
			saveDeliveryPackaging(this.packaging);
			dialog.close();
			this.reload();
		});

		
		dialog.addAcceptAction(() => {});
		dialog.open();
	}

	buildDelivery(table, detail) {
		let deliveryFilter = {
			status: 'PENDING',
			customer: this.sale.customer.id,
			carrierPacking: this.sale.carrierPacking,
			full:true
		};
		if(this.sale.carrierPacking) {
			getDelivery(deliveryFilter).then( delivery => {
				this.packaging.delivery = delivery;
				this.buildProductPackaging(table, detail);
			});
		} else {
			table.addRow();
			let deliverySelect = createSelect(this.id + 'DialogDelivery', MSG.DELIVERY);
			deliverySelect.addEventListener(EVENT.SELECT, () => {
				this.packaging.delivery = deliverySelect.value;
				this.buildProductPackaging(table, detail);
			});
			
			let td = table.addCell(deliverySelect);
			td.style.width = '100%';
			
			let addButton = new AonIconButton();
			addButton.id = this.id + 'DeliveryAddButton';
			addButton.title = MSG.ADD;
			addButton.icon = MATERIAL_ICONS.ADD_CIRCLE_OUTLINE;
			addButton.addEventListener(EVENT.CLICK, () => {
				this.packaging = {};
				table.removeRows();
				this.buildNewDelivery(table, detail);
				this.buildProductPackaging(table, detail);
			});
			table.addCell(addButton);
			getDeliveries(deliveryFilter).then( deliveries => {
				deliverySelect.setOptions(deliveries.map(d => {
					return {
						value: d.id,
						name: d.series + '/' + d.number
					  }
				}))
				
			});
		}

	}

	buildNewDelivery(table, detail) {
		table.addRow();
		let deliverySelect = createSelect(this.id + 'DialogDelivery', MSG.DELIVERY);
		deliverySelect.disabled = true;
		
		let td = table.addCell(deliverySelect);
		td.style.width = '100%';
		
		let listButton = new AonIconButton();
		listButton.id = this.id + 'DeliveryListButton';
		listButton.title = MSG.ADD;
		listButton.icon = MATERIAL_ICONS.LIST;
		listButton.addEventListener(EVENT.CLICK, () => {
			this.packaging = {};
			table.removeRows();
			this.buildDelivery(table, detail);
			let saveButton = this.getElement(this.DELIVERY_SAVE_BUTTON)
			saveButton.setDisabled(true);
		});
		table.addCell(listButton);
	}

	buildProductPackaging(table, detail) {
		table.addRow();
		
		let product = createInput(this.PACKAGING_PRODUCT, MSG.CONTAINER + ' (SSCC)');
		product.id = this.DELIVERY_PRODUCT;
		let td = table.addCell(product);
		td.style.width = '100%';
		product.addIconButton(MATERIAL_ICONS.QR_CODE_SCANNER, () => this.openBarcode());	
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
				delivery: this.packaging.delivery,
				product: detail.item.product.id
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

				if(r.delivery && r.delivery.id && r.delivery.id === this.packaging.delivery) {
					this.buildNewPackagingContent(table, detail);
				} else if(!r.delivery || !r.delivery.id) {
					this.buildPackagingContent(table, detail, r);
				}

			}).catch(e => this.showError(e));
		});
		

		let addButton = new AonIconButton();
		addButton.id = this.id + 'AddButton';
		addButton.title = MSG.ADD;
		addButton.icon = MATERIAL_ICONS.ADD_CIRCLE_OUTLINE;
		addButton.addEventListener(EVENT.CLICK, () => {
			let cont = this.sale.carrierPacking ? 1 : 2;
			
			while(table.rows >= cont) {
				table.removeRow(table.rows);
			}
			this.buildNewPackaging(table, detail);
			let saveButton = this.getElement(this.DELIVERY_SAVE_BUTTON)
			saveButton.setDisabled(true);
		});
		table.addCell(addButton);
	}

	buildNewPackaging(table, detail) {
		table.addRow();
		let envaseSelect = createSelect(this.id + 'DialogEnvase', 'Nuevo Envase');
		envaseSelect.addEventListener(EVENT.SELECT, () => {
			let cont = this.sale.carrierPacking ? 1 : 2;
			while(table.rows > cont) {
				table.removeRow(table.rows);
			}
			this.packaging.container = {
				product: envaseSelect.value
			};
			this.buildNewPackagingContent(table, detail);
		});
		
		let td = table.addCell(envaseSelect);
		td.style.width = '100%';

		let pButton = new AonIconButton();
		pButton.id = this.id + 'ProductButton';
		pButton.title = MSG.ADD;
		pButton.icon = MATERIAL_ICONS.QR_CODE_SCANNER;
		pButton.addEventListener(EVENT.CLICK, () => {
			let cont = this.sale.carrierPacking ? 1 : 2;

			while(table.rows >= cont) {
				table.removeRow(table.rows);
			}
			this.buildProductPackaging(table, detail);
		});
		table.addCell(pButton);

		let productFilter = {
			type: 'AUXILIARY'
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

	buildNewPackagingContent(table, detail) {
		table.addRow();

		let product2 = createInput(this.PACKAGING_PRODUCT, "Envase Origen");
		product2.id = 'product2';
		let td = table.addCell(product2);
		td.style.width = '100%';
		product2.addIconButton(MATERIAL_ICONS.QR_CODE_SCANNER, () => this.openBarcode());
	
		product2.addEventListener(EVENT.CHANGE, () => {
			product2.setDisabled(true);
			let data = { 
				sscc: product2.value,
				product: detail.item.product.id
			};
			getDeliveryPackaging(data).then(r => {
				// si no esta en ningun albaran  
					// comprobar que lo que tenga el albarán es lo que se quiere añadir si no ERROR
					//añadir cantidad 
				let composition = [];
				Array.prototype.forEach.call(r.item.itemComposition, i => {
					table.addRow();
					let product3 = createInput(this.PACKAGING_PRODUCT, "Contenedor Producto / Lote");
					product3.value = i.description;
					product3.id = this.DELIVERY_PRODUCT2;
					product3.disabled = true;
					let td = table.addCell(product3);
					td.style.width = '100%';
				
					table.addRow();

					let q = detail.quantity - detail.delivered;
					product3.value = i.description;
					let quantityValue = (q < i.quantity) 
						? q : i.quantity;
					detail.delivered = detail.delivered + quantityValue;
					let quantity = createInput(this.PACKAGING_PRODUCT, "Cantidad");
					quantity.id = this.DELIVERY_QUANTITY;
					quantity.value = quantityValue;
					let td2 = table.addCell(quantity);
					td2.style.width = '100%';	

					let saveButton = this.getElement(this.DELIVERY_SAVE_BUTTON)
					saveButton.setDisabled(false);

					composition.put({
						item: i.item,
						quantity: quantityValue
					})
				});
					
				if(r.item.itemComposition.length > 0) {
					
				}				

				this.packaging.content = {
					source: r.item.serialNumber,
					composition
				}
			}).catch(e => this.showError(e));

		});
	}

	buildPackagingContent(table, detail, container) {
		Array.prototype.forEach.call(container.item.itemComposition, i => {
			table.addRow();
			let product2 = createInput(this.PACKAGING_PRODUCT, "Contenedor Producto / Lote");
			product2.id = this.DELIVERY_PRODUCT2;
			product2.value = i.description;
			product2.id = 'product2';
			product2.disabled = true;
			let td = table.addCell(product2);
			td.style.width = '100%';
	
			table.addRow();
	
			let quantity = createInput(this.PACKAGING_PRODUCT, "Cantidad");
			quantity.id = this.DELIVERY_QUANTITY;
			quantity.value = i.quantity;
			let td2 = table.addCell(quantity);
			td2.style.width = '100%';
			detail.delivered = detail.delivered + i.quantity;
			let saveButton = this.getElement(this.DELIVERY_SAVE_BUTTON)
			saveButton.setDisabled(false);
		});
	}


	openBarcode() {
		let ionicData = { action: MOBILE_ACTION.BARCODE, selector: 'aon-mobile-sale' };
		if(UA.isAndroidApp()) {
			openBarcode(ionicData, (result) => this.setBarcodeAction(result.code));
		} else mobileAction(ionicData);
	}

	setBarcodeData(barcodeStr) {
		try {
			
			if(typeof barcodeStr === 'string') {
				barcodeStr = JSON.parse(barcodeStr);
			}

			const {text, format, cancelled} = barcodeStr;
			if(!cancelled) {
				this.setBarcodeAction(text);
			}
		} catch (error) {
			this.showError(error);
		}
	}

	setBarcodeAction(code) {
		const product = this.getElement(this.PACKAGING_PRODUCT);
		product.value = code;
		this.changeProduct();
	}
	// Create Components

	createCard(id, title) {
		let card = new AonCard();
		card.id = id;
		card.title = title;
		return card;
	}
}

if(!window.customElements.get(TAG.AON_MOBILE_SALE)){
	window.customElements.define(TAG.AON_MOBILE_SALE, AonMobileSale);
}