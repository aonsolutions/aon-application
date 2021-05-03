import {AonElement} from '../../components/AonElement.js';
import {ToolbarType} from '../../models/enums.js';

import { getCompanyOne, getCompanyMedia, saveCompany } from "../../services/service.js";


import '../../components/aon-address.js';
import '../../components/aon-input.js';

import {AonToolbar} from "../../components/aon-toolbar.js";
import {AonCard} from "../../components/aon-card.js";
import {AonCompanyList} from "./aon-company-list.js";

import {CONSTANT, EVENT, MSG, TAG } from '../../environments/environments.js'; 

import * as ACTION from '../actions.js';
import { GENERAL_INFORMATION } from '../../environments/msg.js';
import { handleError } from '../../services/utils.js';

export class AonCompany extends AonElement {

	company;

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
		this.COMPANY_TOOLBAR = this.id + 'Toolbar';
		this.GENERAL_CARD = this.id + 'GeneralCard';
		this.INFO_CARD = this.id + 'InfoCard';
		this.company = this.company || {
			name: '',
			document: ''
		}
	}

	build() {
		let toolbar = new AonToolbar();
		toolbar.id = this.COMPANY_TOOLBAR;
		toolbar.type = ToolbarType.SECONDARY;
		toolbar.title = this.company.id ? this.company.name : MSG.NEW_COMPANY;
		this.appendChild(toolbar);
		toolbar.addButton2(ACTION.SAVE, () => this.save());
		toolbar.addButton2(ACTION.BACK, () => this.back());

		let div = this.createElement(TAG.DIV);
		div.style.display = "flex";
		div.style.width = "100%";
		this.appendChild(div);

		this.buildGeneralCard(div);
		this.buildInfoCard(div);
		this.loadData();

	}

	buildGeneralCard(parent){
			let card = new AonCard();
			card.id = this.GENERAL_CARD;
			card.title = GENERAL_INFORMATION;
			parent.appendChild(card);


			card.setContentHTML(`
				<form action="#" class="aon-margin-0">
					<aon-input class="aonWidth25" id="aonConfigurationGeneralNif" description="NIF" value="${this.company.document}"></aon-input>
					<aon-input class="aonWidth75" id="aonConfigurationGeneralName" description="${MSG.BUSINESS_NAME}" value="${this.company.name}"></aon-input>
				</form>
				<form action="#" class="aon-margin-0">
					<aon-address class="aon-width-100" id="aonConfigurationGeneralAddress" title="${MSG.ADDRESS}"></aon-address>
				</form>
			`);

			// ADDRESS
			let address = document.getElementById('aonConfigurationGeneralAddress');
			address.buildAddressValue({});
			address.addEventListener('change', () => this.company.address = JSON.parse(address.value));

			let doc = this.getElement('aonConfigurationGeneralNif');
			doc.addEventListener(EVENT.CHANGE, () => this.company.document =  doc.value);
			let name = this.getElement('aonConfigurationGeneralName');
			name.addEventListener(EVENT.CHANGE, () => this.company.name =  name.value);
	}

	buildInfoCard(parent){
			let infoCard = new AonCard();
			infoCard.id = this.INFO_CARD;
			infoCard.title = MSG.ADDITIONAL_INFORMATION;
			parent.appendChild(infoCard);

			infoCard.setContentHTML(`
				<form action="#" class="aon-margin-0">
					<aon-input class="aonWidth50" id="aonConfigurationGeneral2Phone" description="Teléfono" value=""></aon-input>
					<aon-input class="aonWidth50" id="aonConfigurationGeneral2Fax" description="Fax" value=""></aon-input>
				</form>

				<form action="#" class="aon-margin-0">
					<aon-input class="aon-width-100" id="aonConfigurationGeneral2Email" description="Email" value=""></aon-input>
				</form>

				<form action="#" class="aon-margin-0">
					<aon-input class="aon-width-100" id="aonConfigurationGeneral2Web" description="Web" value=""></aon-input>
				</form>

				<!-- LOGO -->
			`);

			let phone = this.getElement('aonConfigurationGeneral2Phone');
			phone.addEventListener(EVENT.CHANGE, () => {
				if(!this.company.media) this.company.media = {};
				if(!this.company.media.phone) this.company.media.phone = {};
				this.company.media.phone.value = phone.value;
			});

			let fax = this.getElement('aonConfigurationGeneral2Fax');
			fax.addEventListener(EVENT.CHANGE, () => {
				if(!this.company.media) this.company.media = {};
				if(!this.company.media.fax) this.company.media.fax = {};
				this.company.media.fax.value = fax.value;
			});

			let email = this.getElement('aonConfigurationGeneral2Email');
			email.addEventListener(EVENT.CHANGE, () => {
				if(!this.company.media) this.company.media = {};
				if(!this.company.media.email) this.company.media.email = {};
				this.company.media.email.value = email.value;
			});

			let web = this.getElement('aonConfigurationGeneral2Web');
			web.addEventListener(EVENT.CHANGE, () => {
				if(!this.company.media) this.company.media = {};
				if(!this.company.media.web) this.company.media.web = {};
				this.company.media.web.value = web.value;
			});
	}

	loadData(){
		if(this.company.id) {
			getCompanyOne({id: this.company.id}).then(cp => {
				this.company = cp;
				this.getCompanyAddress().buildAddressValue(cp.address);
				this.getCompanyPhone().value = cp.media.fixed_phone ? cp.media.fixed_phone.value : '';
				this.getCompanyFax().value = cp.media.fax ? cp.media.fax.value : '';
				this.getCompanyEmail().value = cp.media.email ? cp.media.email.value : '';
				this.getCompanyWeb().value = cp.media.web ? cp.media.web.value : '';
			});
		}
	}

	// INPUT COMPONENTS

	getCompanyDocument(){
		return this.getElement('aonConfigurationGeneralNif');
	}

	getCompanyName(){
		return this.getElement('aonConfigurationGeneralName');
	}

	getCompanyAddress(){
		return this.getElement('aonConfigurationGeneralAddress');
	}

	getCompanyPhone(){
		return this.getElement('aonConfigurationGeneral2Phone');
	}

	getCompanyFax(){
		return this.getElement('aonConfigurationGeneral2Fax');
	}

	getCompanyEmail(){
		return this.getElement('aonConfigurationGeneral2Email');
	}

	getCompanyWeb(){
		return this.getElement('aonConfigurationGeneral2Web');
	}

	// ACTIONS

	back() {
		let aonConfiguration = this.getApplication();

		aonConfiguration.removeToolbarOptions();
		aonConfiguration.addToolbarOption("UserAdd", "add", () => this.buildCompany());

		let aonCompanyList = new AonCompanyList();
		aonCompanyList.id = aonConfiguration.id + 'CompanyList';
		aonCompanyList.filter = {parent: true};
		aonConfiguration.setContent(aonCompanyList);
	}

	save() {
		saveCompany(this.company).then(cp => {
			this.company.id = cp.id;
			this.getApplication().getToast().start({
			 	type: 'success',
			 	message: 'Datos Guardados Correctamente'
		 	});
		}).catch(error => {
			this.getApplication().getToast().start(handleError(error));
		});
	}

	setCompany(company) {
		this.company = company;
	}

}

if(!window.customElements.get(TAG.AON_COMPANY)){
	window.customElements.define(TAG.AON_COMPANY, AonCompany);
}
