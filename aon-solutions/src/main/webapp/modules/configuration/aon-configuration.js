import {AonElement} from '../../components/AonElement.js';
import {getAuth} from '../../services/service.js';

import '../../components/aon-application.js';
import '../../components/aon-card.js';
import '../../components/aon-input.js';
import '../marketplace/aon-marketplace.js';
import '../user/aon-user-list.js';
import '../user/aon-user.js';
import '../company/aon-company-list.js';

export class AonConfiguration extends AonElement {
	AON_CONFIGURATION;
	COMPANY;
	COMPANY_LIST;
	selected;

	static get observedAttributes() {
		return ['company', 'user'];
	}

	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

	get company() {
		return this.getAttribute('company');
	}

	set company(company) {
		this.setAttribute('company', company);
	}

	get user() {
		return this.getAttribute('user');
	}

	set user(user) {
		this.setAttribute('user', user);
	}

	get option() {
		return this.getAttribute('option');
	}

	set option(option) {
		this.setAttribute('option', option);
	}

	attributeChangedCallback(name, oldValue, newValue) {
		if('company' === name || 'user' === name){

		}
	}

	constructor () {
		super();
		this.AON_CONFIGURATION = 'aonConfiguration';
		this.COMPANY = this.AON_CONFIGURATION + 'Company';
		this.COMPANY_LIST = this.AON_CONFIGURATION + 'CompanyList';
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-application id="${this.AON_CONFIGURATION}" title="CONFIGURACIÓN"></aon-application>
		`;
		this.build();
  }

	build() {
		let aonConfiguration = this.getElement(this.AON_CONFIGURATION);

		let userOptions = [{
			name: 'Datos Usuario',
			icon: 'person',
			fn: () => this.buildPersonal()
		}];
		aonConfiguration.addSidenavOptions('USUARIO', userOptions);

		let companyOptions = [{
			name: 'Información General',
			icon: 'business',
			hidden: this.hiddenGeneral(),
			fn: () => this.buildGeneral()
		},{
			name: 'Gestión de Usuarios',
			icon: 'people',
			hidden: this.hiddenUser(),
			fn: () => this.buildUser()
		},{
			name: 'Gestión de Empresas',
			icon: 'business',
			hidden: this.hiddenCompany(),
			fn: () => this.buildCompany()
		},{
			name: 'Contratación',
			icon: 'store_mall_directory',
			hidden: this.hiddenStore(),
			fn: () => this.buildStore()
		}];

		aonConfiguration.addSidenavOptions('EMPRESA', companyOptions);

		let company = this.getAttribute('company') ? JSON.parse(this.getAttribute('company')) : undefined;
		let user = this.getAttribute('user') ? JSON.parse(this.getAttribute('user')) : undefined;

		this.buildPersonal()
	}

	buildPersonal() {
		getAuth().then( user => {
			let aonConfiguration = this.getElement(this.AON_CONFIGURATION);
			aonConfiguration.removeToolbarOptions();
			aonConfiguration.setContentHTML('<aon-user id="aonUserPersonal" ><aon-user>');
			let aonUser = this.getElement('aonUserPersonal');
			aonUser.style.display = "flex";
			aonUser.style.width = "100%";
			aonUser.setAttribute('user', JSON.stringify(user));
		});
	}

	buildGeneral() {
		let company = this.getAttribute('company') ? JSON.parse(this.getAttribute('company')) : undefined;
		let aonConfiguration = this.getElement(this.AON_CONFIGURATION);
		aonConfiguration.removeToolbarOptions();
		aonConfiguration.setContentHTML( `
			<div style="display:flex;">
				<aon-card id="aonConfigurationGeneralCard" style="width:50%;" title="Información General"></aon-card>
				<aon-card id="aonConfigurationGeneral2Card" style="width:50%;" title="Información Adicional"></aon-card>
			</div>
		`);

		let card = this.getElement('aonConfigurationGeneralCard');
		card.setContentHTML(`
			<form action="#" class="aon-margin-0">
				<aon-input class="aonWidth25" id="aonConfigurationGeneralNif" description="NIF" value="${company.document}"></aon-input>
				<aon-input class="aonWidth75" id="aonConfigurationGeneralName" description="Razón Social" value="${company.name}"></aon-input>
			</form>
			<form action="#" class="aon-margin-0">
				<aon-input type="address" class="aon-width-100" id="aonConfigurationGeneralAddress" description="Dirección"></aon-input>
			</form>
		`);

		let card2 = this.getElement('aonConfigurationGeneral2Card');
		card2.setContentHTML(`
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
	}

	buildUser() {
		let aonConfiguration = this.getElement(this.AON_CONFIGURATION);
		aonConfiguration.removeToolbarOptions();

		aonConfiguration.addToolbarOption('UserShare', 'share', () => this.buildCreateUser(true));
		aonConfiguration.addToolbarOption('UserAdd', 'add', () => this.buildCreateUser(true));

		aonConfiguration.setContentHTML('<aon-user-list> </aon-user-list>');
	}

	buildCompany() {
		let aonConfiguration = this.getElement(this.AON_CONFIGURATION);
		aonConfiguration.removeToolbarOptions();
		aonConfiguration.addToolbarOption('CompanyAdd', 'share', () => {});

		let companyId = JSON.parse(this.getAttribute('company')).id;
		let filter = JSON.stringify({id: companyId});
		aonConfiguration.setContentHTML(`<aon-company-list id="${this.COMPANY_LIST}" filter="${filter}"> </aon-company-list>`);

		let companyList = this.getElement(this.COMPANY_LIST);
		companyList.addEventListener('select', (event) => {
			aonConfiguration.setContentHTML(`<aon-company id="${this.COMPANY}"> </aon-company>`);
			this.getElement(this.COMPANY).company = event.company;
		});
	}

	buildCreateUser(share) {
		let content = document.getElementById('aonConfigurationContent');
		content.innerHTML = '<aon-user id="aonUserCreate" showApps="true" ><aon-user>';
		let aonUser = document.getElementById('aonUserCreate');
		aonUser.style.display = "flex";
		aonUser.style.width = "100%";
		if(share)	aonUser.setAttribute('share', share);
	}

	buildStore() {
		let aonConfiguration = this.getElement(this.AON_CONFIGURATION);
		aonConfiguration.removeToolbarOptions();

		aonConfiguration.setContentHTML(`<aon-marketplace id="aonMarketplace" > </aon-marketplace>`);

		if(this.getAttribute('company')){
			let aonMarketplace = this.getElement('aonMarketplace');
			aonMarketplace.setAttribute('company', this.getAttribute('company'));
		}
	}

	hiddenGeneral(){
		return false;
	}

	hiddenUser(){
		return false;
	}

	hiddenCompany(){
		return false;
	}

	hiddenStore(){
		return false;
	}

}

window.customElements.define('aon-configuration', AonConfiguration);
