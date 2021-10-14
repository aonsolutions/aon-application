import {AonElement} from '../../components/AonElement.js';
import {ToolbarType} from '../../models/enums.js';

import '../../components/aon-address.js';
import '../../components/aon-input.js';

import {AonToolbar} from "../../components/aon-toolbar.js";
import {AonCard} from "../../components/aon-card.js";

import {CONSTANT, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js'; 

import * as ACTION from '../actions.js';
import { AonBasicTable } from '../../components/aon-basic-table.js';
import { AonInput } from '../../components/aon-input.js';
import { AonAddress } from '../../components/aon-address.js';
import { AonUpload } from '../../components/aon-upload.js';
import { AonIconButton } from '../../components/aon-icon-button.js';
import { Media } from '../../models/registry/Media.js';
import { saveRegistry } from '../../services/registryService.js';
import { Registry } from '../../models/registry/Registry.js';
import { Address } from '../../models/registry/Address.js';
import { getReader } from '../../services/utils.js';
import { deleteAttach, getAttach, uploadAttach, uploadFile } from '../../services/fileService.js';

export class AonReg extends AonElement {

	registry;
	showLogo;
	logo;

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
		this.id = this.id || 'aonCompany';
		this.REGISTRY_TOOLBAR = this.id + 'Toolbar';
		this.GENERAL_CARD = this.id + 'GeneralCard';
		this.GENERAL_TABLE = this.GENERAL_CARD + 'Table';
		this.ADDRESS_TABLE = this.GENERAL_CARD + 'AddressTable';
		this.ADDRESS_ADD = this.ADDRESS_TABLE + 'Add';

		this.INFO_CARD = this.id + 'InfoCard';
		this.INFO_TABLE = this.INFO_CARD + 'Table';

		this.MEDIA_CARD = this.id + 'MediaCard';
		this.EMAIL_TABLE = this.MEDIA_CARD + 'EmailTable';
		this.EMAIL_INPUT = this.EMAIL_TABLE + 'Input';
		this.EMAIL_ADD= this.EMAIL_TABLE + 'Add';
		
		this.PHONE_TABLE = this.MEDIA_CARD + 'PhoneTable';
		this.PHONE_INPUT = this.PHONE_TABLE + 'Input';
		this.PHONE_ADD= this.PHONE_TABLE + 'Add';

		this.WEB_TABLE = this.MEDIA_CARD + 'WebTable';
		this.WEB_INPUT = this.WEB_TABLE + 'Input';
		this.WEB_ADD= this.WEB_TABLE + 'Add';

		this.registry = this.registry || new Registry();
		this.showLogo = this.showLogo || false;
		this.emails = [];
		this.webs = [];
		this.phones = [];
	}

	build() {
		let toolbar = new AonToolbar();
		toolbar.id = this.REGISTRY_TOOLBAR;
		toolbar.type = ToolbarType.SECONDARY;
		toolbar.title = this.registry.id ? this.registry.name : 'NUEVO REGISTRO';
		this.appendChild(toolbar);
		toolbar.addButton2(ACTION.SAVE, () => this.save());
		toolbar.addButton2(ACTION.BACK, () => this.back());

		let div = this.createElement(TAG.DIV);
		div.style.display = "flex";
		div.style.width = "100%";
		this.appendChild(div);

		let div2 = this.createElement(TAG.DIV);
		div2.style.display = "flex";
		div2.style.width = "100%";
		this.appendChild(div2);

		this.buildGeneralCard(div);
		this.buildMediaCard(div);
		this.buildInfoCard(div2);
	}

	buildGeneralCard(parent){
		let card = new AonCard();
		card.id = this.GENERAL_CARD;
		card.title = MSG.GENERAL_INFORMATION;
		card.style.width = '50%';
		parent.appendChild(card);

		let div = this.createElement(TAG.DIV);
		card.setContent(div);

		let table = new AonBasicTable();
		table.id = this.GENERAL_TABLE;
		div.appendChild(table);

		table.addRow();

		let documentInput = new AonInput();
		documentInput.id = 'aonConfigurationGeneralNif';
		documentInput.description = MSG.DOCUMENT;
		documentInput.value = this.registry.getDocument();
		documentInput.addEventListener(EVENT.CHANGE, () => this.registry.setDocument(documentInput.value));

		let td = table.addCell(documentInput);
		td.style.width = '25%';

		let nameInput = new AonInput();
		nameInput.id = 'aonConfigurationGeneralName';
		nameInput.description = MSG.NAME;
		nameInput.value = this.registry.getName();
		nameInput.addEventListener(EVENT.CHANGE, () => this.registry.setName(nameInput.value));

		let td1 = table.addCell(nameInput);
		td1.style.width = '75%';

		this.buildAddresses(div);

		if(this.showLogo && this.registry.getId()) {
			let uploadLogo = new AonUpload();
			uploadLogo.id = this.id + 'UploadLogo';
			uploadLogo.setMessage(MSG.ATTACH_FILES_DRAGGING_DROPPING_LOGO);
			uploadLogo.setDeleteMessage(MSG.DELETE_LOGO_CONFIRM);
			uploadLogo.addEventListener(EVENT.UPLOAD, (e) => {
			  getReader(e.detail).then(f => {
				let data = f
					.setAttachType('registry')
					.setAttachModule(this.registry.getId())
					.setType(0)
					.setId(this.logo ? this.logo.id: undefined);
				uploadAttach(data);
			  });
			});
			div.appendChild(uploadLogo);
			let filter = {
				attachType: 'registry',
				attachModule: this.registry.getId(),
				type: 0				
			};

			uploadLogo.addEventListener(EVENT.DELETE, (e) => {
				this.logo = {};
				deleteAttach(filter);
			});
			
			getAttach(filter).then(attach => {
				this.logo = attach;
				if(this.logo.id)
					uploadLogo.setAttach(attach);
			});
		}
	}

	buildMediaCard(parent){
		let card = new AonCard();
		card.id = this.MEDIA_CARD;
		card.title = MSG.CONTACT_DATA;
		card.style.width = '50%';
		parent.appendChild(card);

		let div = this.createElement(TAG.DIV);
		card.setContent(div);

		this.buildEmails(div);	
		this.buildPhones(div);
		this.buildWebs(div);
	}

	buildInfoCard(parent) {

	}

	buildAddresses(parent) {
		let table = new AonBasicTable();
		table.id = this.ADDRESS_TABLE;
		parent.appendChild(table);

		if(!this.registry.getAddresses() || this.registry.getAddresses().length <= 0)
			this.registry.addAddress(new Address().setRegistry(this.registry.getId()));
		
		this.registry.getAddresses().forEach((address, i) => 
			this.buildAddress(table, address, i));
	}

	buildAddress(table, address, i) {
		if(!address.isRemoved()){
			let rowNum = table.addRow();

			let aonAddress = new AonAddress();
			aonAddress.id = 'aonConfigurationGeneralAddress' + i;
			aonAddress.title = MSG.ADDRESS + ' ' + (this.registry.addresses.length > 1 ? i + 1 : '');
			aonAddress.setAddress(address);
			aonAddress.addEventListener(EVENT.CHANGE, () => this.registry.addresses[i] = aonAddress.getAddress());
			let td = table.addCell(aonAddress);
			td.style.width = '100%';

			let addAddress = new AonIconButton();
			addAddress.id = this.ADDRESS_ADD + i;
			addAddress.title = MSG.ADD;
			addAddress.icon = MATERIAL_ICONS.ADD_CIRCLE_OUTLINE;
			addAddress.visible = this.registry.addresses.length === i+1;
			addAddress.addEventListener(EVENT.CLICK, () => {
				let number = this.registry.addresses.length;
				let aux = new Address().setRegistry(this.registry.getId())
				this.registry.addAddress(aux);
				addAddress.visible = false;
				this.buildAddress(table, aux, number);
			});
			table.addCell(addAddress);

			aonAddress.addEventListener(EVENT.DELETE, () => {
				if(table.getRowsCount() === 1) {
					let aux = new Address().setRegistry(this.registry.getId())
					aonAddress.setAddress(aux);
					this.registry.getAddresses()[i] = aux;
				} else {
					let last = this.getElement(this.ADDRESS_ADD + i).isVisible();
					this.registry.getAddresses()[i].remove();
					table.removeRow(rowNum);
					if(last) {
						let j = 0;
						this.registry.getAddresses().forEach((item, h) => {
							if(!item.isRemoved())
								j = h;
						});
						this.getElement(this.ADDRESS_ADD + j).visible = true;
					}
				}
			});
		}
	}

	// EMAILS

	buildEmails(parent) {
		this.emails = this.registry.getMedia()
			.filter(f => f.isEmail());

		let table = new AonBasicTable();
		table.id = this.EMAIL_TABLE;
		parent.appendChild(table);

		if(!this.emails || this.emails.length <= 0){
			let media = new Media()
				.setRegistry(this.registry.getId())
				.setMedia('EMAIL');
			this.emails = [media];
			this.buildEmail(table, media, 0);
		} else {
			this.emails.forEach( (email, i) =>
				this.buildEmail(table, email, i));
		}
	}

	buildEmail(table, email, i) {
		if(!email.isRemoved()) {
			let rowNum = table.addRow();

			let aonInput = new AonInput();
			aonInput.id = this.EMAIL_INPUT + i;
			aonInput.description = MSG.EMAIL + ' ' + (this.emails.length > 1 ? i + 1 : '');
			aonInput.value = email.getValue();
			aonInput.addEventListener(EVENT.CHANGE, () => this.emails[i].setValue(aonInput.value));

			let addEmail = new AonIconButton();
			addEmail.id = this.EMAIL_ADD + i;
			addEmail.title = MSG.ADD;
			addEmail.icon = MATERIAL_ICONS.ADD_CIRCLE_OUTLINE;
			addEmail.visible = this.emails.length === i+1;
			addEmail.addEventListener(EVENT.CLICK, () => {
				let number = this.emails.length;
				let media = new Media()
					.setRegistry(this.registry.getId())
					.setMedia('EMAIL');
				this.emails.push(media);
				addEmail.visible = false;
				this.buildEmail(table, media, number);
			});
			let td = table.addCell(aonInput);
			td.style.width = '100%';
			table.addCell(addEmail);
			aonInput.addIconWithRemove(MATERIAL_ICONS.MAIL, undefined, () => {
				if(table.getRowsCount() === 1) {
					aonInput.value = '';
					this.emails[i].setValue('');
				} else {
					let last = this.getElement(this.EMAIL_ADD + i).isVisible();
					this.emails[i].remove();
					table.removeRow(rowNum);
					if(last) {
						let j = 0;
						this.emails.forEach((item, h) => {
							if(!item.isRemoved())
								j = h;
						});
						this.getElement(this.EMAIL_ADD + j).visible = true;
					}
				}
			});
		}
	}

	// PHONES

	buildPhones(parent) {
		this.phones = this.registry.getMedia()
			.filter(f => f.isPhone());
		let table = new AonBasicTable();
		table.id = this.PHONE_TABLE;
		parent.appendChild(table);
	
		if(!this.phones || this.phones.length <= 0){
			let media = new Media()
				.setRegistry(this.registry.getId())
				.setMedia('CELLULAR');
			this.phones = [media];
			this.buildPhone(table, media, 0);
		} else {
			this.phones.forEach((phone, i) => 
				this.buildPhone(table, phone, i));
		}
	}
	
	buildPhone(table, phone, i) {
		if(!phone.isRemoved()){
			let rowNum = table.addRow();
	
			let aonInput = new AonInput();
			aonInput.id = this.PHONE_INPUT + i;
			aonInput.description = MSG.PHONE + ' ' + (this.phones.length > 1 ? i + 1 : '');
			aonInput.value = phone.getValue();
	
			aonInput.addEventListener(EVENT.CHANGE, () => this.phones[i].setValue(aonInput.value));
	
			let addPhone = new AonIconButton();
			addPhone.id = this.PHONE_ADD + i;
			addPhone.title = MSG.ADD;
			addPhone.icon = MATERIAL_ICONS.ADD_CIRCLE_OUTLINE;
			addPhone.visible = this.phones.length === i+1;
			addPhone.addEventListener(EVENT.CLICK, () => {
				let number = this.phones.length;
				let media = new Media()
					.setRegistry(this.registry.getId())
					.setMedia('CELLULAR');
				this.phones.push(media);
				addPhone.visible = false;
				this.buildPhone(table, media, number);
			});
			let td = table.addCell(aonInput);
			td.style.width = '100%';
			table.addCell(addPhone);
			aonInput.addIconWithRemove(MATERIAL_ICONS.PHONE, undefined, () => {
				if(table.getRowsCount() === 1) {
					aonInput.value = '';
					this.phones[i].setValue('');
				} else {
					let last = this.getElement(this.PHONE_ADD + i).isVisible();
					this.phones[i].remove();
					table.removeRow(rowNum);
					if(last) {
						let j = 0;
						this.phones.forEach((item, h) => {
							if(!item.isRemoved())
								j = h;
						});
						this.getElement(this.PHONE_ADD + j).visible = true;
					}
				}
			});
		}
	}

	// WEBS

	buildWebs(parent) {
		this.webs = this.registry.getMedia()
			.filter(f => f.isWeb());

		let table = new AonBasicTable();
		table.id = this.WEB_TABLE;
		parent.appendChild(table);
	
		if(!this.webs || this.webs.length <= 0){
			let media = new Media()
				.setRegistry(this.registry.getId())
				.setMedia('WEB');
			this.webs = [media];
			this.buildWeb(table, media, 0);
		} else {
			this.webs.forEach((web, i) => 
				this.buildWeb(table, web, i));
		}
	}
	
	buildWeb(table, web, i) {
		if(!web.isRemoved()){
			let rowNum = table.addRow();
	
			let aonInput = new AonInput();
			aonInput.id = this.WEB_INPUT + i;
			aonInput.description = MSG.WEB + ' ' + (this.webs.length > 1 ? i + 1 : '');
			aonInput.value = web.getValue();
	
			aonInput.addEventListener(EVENT.CHANGE, () => this.webs[i].setValue(aonInput.value));
	
			let addWeb = new AonIconButton();
			addWeb.id = this.WEB_ADD + i;
			addWeb.title = MSG.ADD;
			addWeb.icon = MATERIAL_ICONS.ADD_CIRCLE_OUTLINE;
			addWeb.visible = this.webs.length === i+1;
			addWeb.addEventListener(EVENT.CLICK, () => {
				let number = this.webs.length;
				let media = new Media()
					.setRegistry(this.registry.getId())
					.setMedia('WEB');
				this.webs.push(media);
				addWeb.visible = false;
				this.buildWeb(table, media, number);
			});
			let td = table.addCell(aonInput);
			td.style.width = '100%';
			table.addCell(addWeb);
			aonInput.addIconWithRemove(MATERIAL_ICONS.LINK, undefined, () => {
				if(table.getRowsCount() === 1) {
					aonInput.value = '';
					this.webs[i].setValue('');
				} else {
					let last = this.getElement(this.WEB_ADD + i).isVisible();
					this.webs[i].remove();
					table.removeRow(rowNum);
					if(last) {
						let j = 0;
						this.webs.forEach((item, h) => {
							if(!item.isRemoved())
								j = h;
						});
						this.getElement(this.WEB_ADD + j).visible = true;
					}
				}
			});
		}
	}
	
	// ACTIONS

	back() {

	}

	save() {
		let medias = this.emails.concat(this.phones).concat(this.webs);
		this.registry.setMedia(medias);

		saveRegistry(this.registry).then(registry => {
			this.registry.id = registry.id;
			this.showToast({
				type: 'success',
	 			message: 'Datos Guardados Correctamente'
	 		});
		}).catch(error => {
	 		this.showToast(error);
	 	});
	}

	setRegistry(registry) {
		this.registry = new Registry(registry);
	}

	setShowLogo(showLogo) {
		this.showLogo = showLogo;
	}
}

if(!window.customElements.get(TAG.AON_REG)){
	window.customElements.define(TAG.AON_REG, AonReg);
}
