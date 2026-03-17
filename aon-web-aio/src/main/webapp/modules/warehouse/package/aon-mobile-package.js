import { AonElement } from '../../../components/AonElement.js';
import { AonCard } from "../../../components/aon-card.js";
import { CONSTANT, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../../environments/environments.js'; 
import { Elaboration } from '../../../models/elaboration/Elaboration.js';
import { AonBasicTable } from '../../../components/aon-basic-table.js';
import { AonIconButton } from '../../../components/aon-icon-button.js';
import {openFileUrl} from '../../../services/service.js';
import { deleteElaborationPackage } from '../../../services/warehouseService.js';
import { getPackages, removePackage } from '../package/PackagesCache.js';
import { AonMobilePackageList } from './aon-mobile-package-list.js';
import { printFile } from '../../../services/actionService.js';

import * as ACTION from '../../actions.js';
import * as UA from '../../../services/userAgentService.js';
import * as LS from '../../../services/localStorageService.js';
import { createNumber, createSelect } from '../../../components/CreateComponent.js';

export class AonMobilePackage extends AonElement {

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
	fileUrl;

	ELABORATION_TOOLBAR;

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
		let toolbar = this.getElement(this.ELABORATION_TOOLBAR);		
		toolbar.removeButton(ACTION.PRINT.id);
		toolbar.removeButton(ACTION.DELETE.id);
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

		this.TAG = this.id + CONSTANT.TAG.initCap();
		this.TAG_CARD = this.TAG + CONSTANT.CARD.initCap();

		this.packaging = this.packaging || new Elaboration(this.packaging);
	}

	build() {
		this.getApplication().removeToolbarOptions();

		let div = this.createElement(TAG.DIV);
		div.style.width = "100%";
		this.appendChild(div);
		this.buildPackage(div);

		let toolbar = this.getElement(this.ELABORATION_TOOLBAR);		
		toolbar.removeButton(ACTION.PRINT.id);
		if(!this.isBlocked()) toolbar.addButtonAfter(ACTION.DELETE, () => this.delete());
		toolbar.addButtonAfter(ACTION.PRINT, () => this.print());
	}

  	buildPackage(parent){
		this.buildPackageGeneral(parent);
		this.buildPackageComposition(parent);
	}

	buildPackageGeneral(parent){
		let card = this.createCard(this.PACKAGE_CARD, MSG.PACKAGING);
		parent.appendChild(card);

		let table = new AonBasicTable();
		table.id = this.PACKAGE_TABLE;
		card.setContent(table);
		if(this.packaging.delivery && this.packaging.delivery.id) {
			table.addRow();

			let deliveryDiv = this.createDiv();
			deliveryDiv.innerHTML = 'Incluido en el albarán ' + this.packaging.delivery.reference;
			deliveryDiv.style.color = 'red';
			deliveryDiv.style.fontWeight = 'bold';
			table.addCell(deliveryDiv, 2);
		} else 	if(this.packaging.item.status === 'DISCONTINUED') {			
			table.addRow();

			let statusDiv = this.createDiv();
			statusDiv.innerHTML = 'El envase está descatalogado';
			statusDiv.style.color = 'red';
			statusDiv.style.fontWeight = 'bold';
			table.addCell(statusDiv, 2);
		} else if(this.packaging.item.itemComposition && //this.packaging.item.itemComposition.length > 0 &&
			(this.packaging.item.itemComposition.length === 0
			|| this.packaging.item.itemComposition.length > 1 
			|| this.packaging.item.itemComposition[0].compositionItem !== this.packaging.composition[0].item.id
			|| this.packaging.item.itemComposition[0].quantity !== this.packaging.composition[0].quantity)
		) {
			table.addRow();

			let statusDiv = this.createDiv();
			statusDiv.innerHTML = 'El envase ha sido modificado';
			statusDiv.style.color = 'orange';
			statusDiv.style.fontWeight = 'bold';
			table.addCell(statusDiv, 2);
		} 

		table.addRow();

		let product = this.createInput(this.PACKAGE_PRODUCT, MSG.PRODUCT);
		product.value = this.packaging.item.name;
		table.addCell(product);

		let quantity = this.createInput(this.PACKAGE_QUANTITY, MSG.QUANTITY);
		quantity.value = this.packaging.quantity || 0.0;
		table.addCell(quantity);

		table.addRow();

		let serialNumber = this.createInput(this.PACKAGE_SERIAL_NUMBER, "Nº Lote");
		serialNumber.value = this.packaging.item.serialNumber;
		table.addCell(serialNumber);

		let serialDate = this.createInput(this.PACKAGE_SERIAL_DATE, "Fecha Lote");
		serialDate.value = this.packaging.item.serialDate;
		table.addCell(serialDate);
	}

	buildPackageComposition(parent){
		let card = this.createCard(this.COMPOSITION_CARD, MSG.COMPOSITION);
		parent.appendChild(card);

		let div = this.createElement(TAG.DIV);
		card.setContent(div);

		let table = new AonBasicTable();
		table.id = this.PACKAGE_COMPOSITION_TABLE;
		div.appendChild(table);
		
		this.packaging.composition.forEach((composition, i) => {
			table.addRow();
			let comp1 = this.createInput(this.COMPOSITION_ITEM + i, "Producto");
			comp1.value = composition.item.description;
			table.addCell(comp1);

			let comp2 = this.createInput(this.COMPOSITION_QUANTITY + i, "Cantidad");
			comp2.value = composition.quantity;
			table.addCell(comp2);
		});

		let addButton = new AonIconButton();
		addButton.id = this.COMPOSITION_ADD_BUTTON;
		addButton.title = MSG.ADD;
		addButton.icon = MATERIAL_ICONS.ADD;
		addButton.addEventListener(EVENT.CLICK, () => this.addComposition());
		div.appendChild(addButton);		
	}

	addComposition(){
		let d = this.getApplication().getDialog();
		d.clear();
		if(!this.isMobile())d.width = '400px';
		d.setTitle(MSG.ADD_COMPOSITION);
		d.setContentHTML(MSG.IN_DEVELOPMENT);
		d.addAcceptAction(() => {});
		d.open();
	}

	// ACTIONS

	back() {

	}

	save() {
	
	}

	print() {
		if(!this.fileUrl) {
			let json = {
				container: this.packaging.item.id,
				domain_id: LS.getDomainId(),
				domain_name: LS.getDomainName(),
				login: LS.getDomainLogin()
			};
			this.fileUrl = '/ms/api/download_packaging_pdf?json=' + btoa(JSON.stringify(json));
		}
		if(UA.isAndroidApp()) {
			let file = {
				url: this.fileUrl,
				title: this.packaging.item.serialNumber
			};
			printFile(file);
		} else openFileUrl(this.fileUrl, 'application/pdf');
	}

	delete() {
		let aonWarehouse = this.getElement('aonWarehouse');
		let d = document.getElementById(aonWarehouse.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.DELETE);
		d.setContentHTML('Estás seguro de eliminar el Envase');
		d.addAcceptAction(() => deleteElaborationPackage(this.packaging.id)
			.then(() => {
				removePackage();
				let packageList = new AonMobilePackageList();
				packageList.setToolbar(this.ELABORATION_TOOLBAR);
				packageList.setPackages(getPackages());

				let div = this.getElement('aonPackageDiv');
				this.clearElement(div);
				div.appendChild(packageList);
			})
			.catch(e => this.showError(e))
		);
		d.open();
	}

	
	setPackaging(packaging) {
		this.packaging = packaging; //new Package(elaboration);
	}

	// Create Components

	createCard(id, title) {
		let card = new AonCard();
		card.id = id;
		card.title = title;
		return card;
	}

	createSelect(id, title) {
		return createSelect(id, title);
	}

	createInput(id, title) {
		return createInput(id, title);
	}

	createNumber(id, title) {
		return createNumber(id, title);
	}

	setElaborationToolbar(toolbar) {
		this.ELABORATION_TOOLBAR = toolbar;
	}

	isBlocked() {
		return this.packaging.item.status === 'DISCONTINUED' || (this.packaging.delivery && this.packaging.delivery.id)
			|| (this.packaging.item.itemComposition && // this.packaging.item.itemComposition.length > 0 &&
			(this.packaging.item.itemComposition.length === 0
				|| this.packaging.item.itemComposition.length > 1 
				|| this.packaging.item.itemComposition[0].compositionItem !== this.packaging.composition[0].item.id
				|| this.packaging.item.itemComposition[0].quantity !== this.packaging.composition[0].quantity)
			);
	}
}

if(!window.customElements.get(TAG.AON_MOBILE_PACKAGE)){
	window.customElements.define(TAG.AON_MOBILE_PACKAGE, AonMobilePackage);
}