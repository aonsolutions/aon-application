import { AonElement } from '../../components/AonElement.js';
import { COLORS, CONSTANT, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js'; 
import { Elaboration } from '../../models/elaboration/Elaboration.js';
import { AonBasicTable } from '../../components/aon-basic-table.js';
import { deleteDeliveryPackaging, getItem, openFileUrl, subtractDeliveryPackagingComposition, addDeliveryPackagingComposition} from '../../services/service.js';
import { createCard, createInput, createQuantity } from '../../components/CreateComponent.js';
import { AonIconButton } from '../../components/aon-icon-button.js';
import { round } from '../../services/utils.js';

import * as LS from '../../services/localStorageService.js';
import * as ACTION from '../actions.js';
import * as UA from '../../services/userAgentService.js';
import { AonCheckbox } from '../../components/aon-checkbox.js';

export class AonMobileDeliveryPackaging extends AonElement {

	PACKAGE_CARD;
	PACKAGE_TABLE;

	PACKAGE_PRODUCT;
	PACKAGE_QUANTITY;
	PACKAGE_SERIAL_NUMBER;
	PACKAGE_SERIAL_DATE;

	COMPOSITION;
	COMPOSITION_CARD;
	COMPOSITION_TABLE;
	COMPOSITION_ITEM;
	COMPOSITION_QUANTITY;
	
	TAG;
	TAG_CARD;

	packaging;
	delivery;
	deliveryDetails;

	ELABORATION_TOOLBAR;
	DELIVERY_TOOLBAR;

	SUBTRACT_PACKAGING_PRODUCT;
	SUBTRACT_PACKAGING_QUANTITY;

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
		this.getElement('aonDelivery').package = this.packaging;
		this.build();
    }

	disconnectedCallback() {
		this.removeToolbar();
		this.getElement('aonDelivery').package = undefined;
	}

	initialize() {
		this.id = this.id || 'aonPackage';
		this.PACKAGE_CARD = this.id + CONSTANT.CARD.initCap();
		this.PACKAGE_TABLE = this.id + CONSTANT.TABLE.initCap();
		this.PACKAGE_PRODUCT = this.id + CONSTANT.PRODUCT.initCap();	
		this.PACKAGE_QUANTITY = this.id + CONSTANT.QUANTITY.initCap();
		this.PACKAGE_SERIAL_NUMBER = this.id + 'SerialNumber';
		this.PACKAGE_SERIAL_DATE = this.id + 'SerialDate';
		
		this.COMPOSITION = this.id + CONSTANT.COMPOSITION.initCap();
		this.COMPOSITION_CARD = this.COMPOSITION + CONSTANT.CARD.initCap();
		this.COMPOSITION_TABLE = this.COMPOSITION + CONSTANT.TABLE.initCap();
		this.COMPOSITION_ITEM = this.COMPOSITION + CONSTANT.ITEM.initCap();	
		this.COMPOSITION_QUANTITY = this.COMPOSITION + CONSTANT.QUANTITY.initCap();

		this.SUBTRACT_PACKAGING_PRODUCT = this.id + 'SubtractPackagingProduct';
		this.SUBTRACT_PACKAGING_QUANTITY = this.id + 'SubtractPackagingQuantity';

		this.TAG = this.id + CONSTANT.TAG.initCap();
		this.TAG_CARD = this.TAG + CONSTANT.CARD.initCap();

		this.packaging = this.packaging || new Elaboration(this.packaging);
	}

	build() {
		let div = this.createElement(TAG.DIV);
		div.style.width = "100%";
		this.appendChild(div);
		this.buildPackage(div);

		this.buildToolbar();
	}

	buildToolbar() {
		if(this.ELABORATION_TOOLBAR) {
			let elaborationToolbar = this.getElement(this.ELABORATION_TOOLBAR);	
			elaborationToolbar.removeButton(ACTION.PRINT.id);
			elaborationToolbar.addButtonAfter(ACTION.PRINT, () => this.print());
		}	

		if(this.DELIVERY_TOOLBAR) {
			// let deliveryToolbar = this.getElement(this.DELIVERY_TOOLBAR);
			// deliveryToolbar.addButtonAfter(ACTION.DELETE, () => this.deleteFromDelivery());
		}
	}

	removeToolbar() {
		if(this.ELABORATION_TOOLBAR) {
			let elaborationToolbar = this.getElement(this.ELABORATION_TOOLBAR);	
			if(elaborationToolbar) elaborationToolbar.removeButton(ACTION.PRINT.id);
		}

		if(this.DELIVERY_TOOLBAR) {
			// let deliveryToolbar = this.getElement(this.DELIVERY_TOOLBAR);	
			// if(deliveryToolbar) deliveryToolbar.removeButton(ACTION.DELETE.id);
		}
	}

  	buildPackage(parent){
		this.buildPackageGeneral(parent);
		this.buildPackageComposition(parent);
		if(this.packaging.composition && this.packaging.composition.length === 1)
			this.buildTag(parent);
	}

	buildPackageGeneral(parent){
		let card = createCard(this.PACKAGE_CARD, MSG.PACKAGING, parent);
		card.style.backgroundColor = 'transparent';
		card.addTitleButton(MSG.OPTIONS, MATERIAL_ICONS.MORE_HORIZ, false, (e) => this.options(e));

		let table = new AonBasicTable();
		table.id = this.PACKAGE_TABLE;
		card.setContent(table);

		table.addRow();

		let product = createInput(this.PACKAGE_PRODUCT, "Envase");
		product.value = this.packaging.item.name;
		product.disabled = true;
		table.addCell(product);

		table.addRow();

		let serialNumber = createInput(this.PACKAGE_SERIAL_NUMBER, "SSCC");
		serialNumber.value = this.packaging.item.serialNumber;
		serialNumber.disabled = true;
		table.addCell(serialNumber);
	}

	buildPackageComposition(parent){
		let card = createCard(this.COMPOSITION_CARD, MSG.COMPOSITION, parent);
		card.style.backgroundColor = 'transparent';
		
		let div = this.createDiv();
		card.setContent(div);

		let table = new AonBasicTable();
		table.id = this.COMPOSITION_TABLE;
		div.appendChild(table);
		
		// Array.prototype.forEach.call(this.packaging.composition, i => {
		this.packaging.item.itemComposition.forEach((c, i) => {
			table.addRow();
			
			let span = this.createSpan();
			span.innerHTML = c.composition.description.isEmpty()
				? c.composition.product.code
				: c.composition.description
			let td = table.addCell(span);
			td.style.paddingBottom = '10px';
			td.style.paddingRight = '10px';

			let span2 = this.createSpan();
			span2.innerHTML = this.getFormat(this.getItemFromDelivery(c.compositionItem) ,c.quantity);
			span2.style.fontWeight = 'bold';
			let td2 = table.addCell(span2)
			td2.style.paddingBottom = '10px';

			let aonIconButton = this.createAonElement(new AonIconButton(), 'icon' + i, 'icon');
			aonIconButton.icon = MATERIAL_ICONS.DO_NOT_DISTURB_ON; ;
			aonIconButton.addEventListener(EVENT.CLICK, () => {
				this.subtractDialog(c);
			});
			let td3 = table.addCell(aonIconButton);
			td3.style.paddingBottom = '10px';

			if(!this.isMobile()){
				let addIconButton = this.createAonElement(new AonIconButton(), 'icon' + i, 'icon');
				addIconButton.icon = MATERIAL_ICONS.ADD_CIRCLE;
				addIconButton.addEventListener(EVENT.CLICK, () => {
					this.addDialog(c);
				});
				let td4 = table.addCell(addIconButton);
				td4.style.paddingBottom = '10px';
			}
		});
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

	getItemFromDelivery(item) {
		let detail = this.deliveryDetails.filter(f => f.item.id === item)[0];
		return detail.item;
	}

	subtractDialog(composition) {
		getItem({id: composition.composition.id, full: true}).then(item => {
			let table = new AonBasicTable();
			table.id = this.id + 'SubstractTable';

			let d = this.getApplication().getDialog();
			d.clear();

			if(!this.isMobile()) d.width = '400px';
			d.setTitle(MSG.ACCEPT);
			d.setContent(table);

			table.addRow();
			let product = createInput(this.SUBTRACT_PACKAGING_PRODUCT, MSG.CONTAINER + ' (SSCC) Destino');
			table.addCell(product);
			// product.addIcon(MATERIAL_ICONS.QR_CODE_SCANNER, undefined, () => this.openBarcode(product));			
			let skipDestiny = false;
			if(!this.isMobile()){
				table.addRow();
	
				let checkBox = new AonCheckbox();
				checkBox.id = this.id + 'SubstractCheckbox';
				checkBox.description = 'Restar sin destino.';
				checkBox.addEventListener(EVENT.CHANGE, () => {
					skipDestiny = checkBox.isChecked();
					if(checkBox.isChecked()) {
						product.setValue("");
						product.setDisabled(true);
					} else product.setDisabled(false);
				});
				let cell = table.addCell(checkBox);
				cell.style.padding = '10px';
			}

			table.addRow();
			let quantityBox = createQuantity(this.SUBTRACT_PACKAGING_QUANTITY, MSG.QUANTITY);
			quantityBox.addEventListener(EVENT.CHANGE, () => {
			 	quantityBox.setQuantityFormat(quantityBox.value);
			});
			quantityBox.setTags(item);
			table.addCell(quantityBox);
			quantityBox.setQuantity(composition.quantity);

			d.addAcceptAction(() => {
				subtractDeliveryPackagingComposition({
					delivery: this.delivery,
					composition,
					quantity: quantityBox.getQuantity(),
					destiny: product.value,
					skipDestiny: skipDestiny
				})
				.then(() => this.aonDelivery())
				.catch(err => this.showError(err));
			});
			d.open();
		});
	}	

	addDialog(composition) {
		getItem({id: composition.composition.id, full: true}).then(item => {
			let table = new AonBasicTable();
			table.id = this.id + 'SubstractTable';

			let d = this.getApplication().getDialog();
			d.clear();

			if(!this.isMobile()) d.width = '400px';
			d.setTitle(MSG.ACCEPT);
			d.setContent(table);

			table.addRow();
			let product = createInput(this.SUBTRACT_PACKAGING_PRODUCT, MSG.CONTAINER + ' (SSCC) Origen');
			table.addCell(product);
			// product.addIcon(MATERIAL_ICONS.QR_CODE_SCANNER, undefined, () => this.openBarcode(product));			
			let skipSource = false;
			if(!this.isMobile()){
				table.addRow();
	
				let checkBox = new AonCheckbox();
				checkBox.id = this.id + 'AddCheckbox';
				checkBox.description = 'Sumar sin origen.';
				checkBox.addEventListener(EVENT.CHANGE, () => {
					skipSource = checkBox.isChecked();
					if(checkBox.isChecked()) {
						product.setValue("");
						product.setDisabled(true);
					} else product.setDisabled(false);
				});
				let cell = table.addCell(checkBox);
				cell.style.padding = '10px';
			}

			table.addRow();
			let quantityBox = createQuantity(this.SUBTRACT_PACKAGING_QUANTITY, MSG.QUANTITY);
			quantityBox.addEventListener(EVENT.CHANGE, () => {
			 	quantityBox.setQuantityFormat(quantityBox.value);
			});
			quantityBox.setTags(item);
			table.addCell(quantityBox);
			quantityBox.setQuantity(0.0);

			d.addAcceptAction(() => {
				addDeliveryPackagingComposition({
					delivery: this.delivery,
					composition,
					quantity: quantityBox.getQuantity(),
					source: product.value,
					skipSource: skipSource
				})
				.then(() => this.aonDelivery())
				.catch(err => this.showError(err));
			});
			d.open();
		});
	}	
	
	buildTag(){

	
	}

	// ACTIONS

	back() {

	}

	save() {
	
	}

	options(e) {
		e.preventDefault();
		let rect = e.target.getBoundingClientRect();
		let x = e.clientX - rect.left;
		let y = e.clientY - rect.top;
	
		const top  = rect.top + y;
		const left = rect.left + x;
	
		let d = document.getElementById(this.getApplication().OPTION_DIALOG);
		let moreActions = [];

		let printAction = {
			id: CONSTANT.PRINT,
			name: "Imprimir Envase",
			title: "Imprimir Envase",
			permission: true,
			backgroundColor: `var(${COLORS.AON_BLUE})`,
			icon: MATERIAL_ICONS.PRINT,
			fn: () => this.print()
		};
		moreActions.push(printAction);

		let deleteAction = {
			id: CONSTANT.DELETE,
			name: "Eliminar Envase",
			title: "Eliminar Envase",
			permission: true,
			backgroundColor: `var(${COLORS.AON_BLUE})`,
			icon: MATERIAL_ICONS.DELETE,
			fn: () => this.deleteFromDelivery()
		};
		moreActions.push(deleteAction);
		
		d.setMenuOptions(moreActions, top, left);
		d.open();
	}

	print() {
		let json = {
			delivery: this.delivery,
			container: this.packaging.item.id,
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

	deleteFromDelivery() {
		let d = this.getApplication().getDialog();
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.ACCEPT);
		d.setContentHTML(`Estás seguro de restar el palet ${this.packaging.item.serialNumber}`);
		d.addAcceptAction(() => {
			deleteDeliveryPackaging({
				delivery: this.delivery,
				package: this.packaging.item.serialNumber
			}).then(() => this.aonDelivery());
		});
		d.open();
	}

	aonDelivery() {
		this.getElement('aonDelivery').backToDelivery(this.delivery);
	}
	
	setPackaging(packaging) {
		this.packaging = packaging; //new Package(elaboration);
	}

	setDelivery(delivery) {
		this.delivery = delivery;
	}

	setDeliveryDetails(deliveryDetails) {
		this.deliveryDetails = deliveryDetails;
	}

	setElaborationToolbar(toolbar) {
		this.ELABORATION_TOOLBAR = toolbar;
	}

	setDeliveryToolbar(toolbar) {
		this.DELIVERY_TOOLBAR = toolbar;
	}
}

if(!window.customElements.get(TAG.AON_MOBILE_DELIVERY_PACKAGING)){
	window.customElements.define(TAG.AON_MOBILE_DELIVERY_PACKAGING, AonMobileDeliveryPackaging);
}