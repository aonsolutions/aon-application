import {Apps} from  '../../services/app.js';
import {getDomainApps, setUserAppRole, setUser} from  '../../services/service.js';

import '../../components/aon-select.js';

class AonCompany extends HTMLElement {

	static get observedAttributes() {
		return ['company'];
	}

	get company() {
		return this.getAttribute('company');
	}

	set company(company) {
		this.setAttribute('company', company);
	}

	attributeChangedCallback(name, oldValue, newValue) {
		if('company' === name) {
			this.build();
		}
	}

	constructor () {
		super();
	}

	connectedCallback () {
		//this.build();
  }

	build() {
		let company = this.getAttribute('company') ? JSON.parse(this.getAttribute('company')) : undefined;

		let content = document.getElementById('aonConfigurationContent');
		content.style.display = "flex";
		content.innerHTML = `
			<aon-card id="aonConfigurationGeneralCard" title="Información General"></aon-card>
			<aon-card id="aonConfigurationGeneral2Card" title="Información Adicional"></aon-card>
		`;
		let card = document.getElementById('aonConfigurationGeneralCard');
		let cardDiv = document.getElementById('aonConfigurationGeneralCard-div');
		cardDiv.style.width = '500px';
		card.addContent(`
			<form action="#" class="aon-margin-0">
				<aon-input-text class="aonWidth25" id="aonConfigurationGeneralNif" description="NIF" value="${company.document}"></aon-input-text>
				<aon-input-text class="aonWidth75" id="aonConfigurationGeneralName" description="Razón Social" value="${company.name}"></aon-input-text>
			</form>
			<form action="#" class="aon-margin-0">
				<aon-address class="aon-width-100" id="aonConfigurationGeneralAddress" description="Dirección"></aon-address>
			</form>
		`);

		let card2 = document.getElementById('aonConfigurationGeneral2Card');
		let cardDiv2 = document.getElementById('aonConfigurationGeneral2Card-div');
		cardDiv2.style.width = '500px';
		card2.addContent(`
			<form action="#" class="aon-margin-0">
				<aon-input-text class="aonWidth50" id="aonConfigurationGeneral2Phone" description="Teléfono" value=""></aon-input-text>
				<aon-input-text class="aonWidth50" id="aonConfigurationGeneral2Fax" description="Fax" value=""></aon-input-text>
			</form>

			<form action="#" class="aon-margin-0">
				<aon-input-text class="aon-width-100" id="aonConfigurationGeneral2Email" description="Email" value=""></aon-input-text>
			</form>

			<form action="#" class="aon-margin-0">
				<aon-input-text class="aon-width-100" id="aonConfigurationGeneral2Web" description="Web" value=""></aon-input-text>
			</form>

			<!-- LOGO -->
		`);

		componentHandler.upgradeAllRegistered();
	}

}

window.customElements.define('aon-company', AonCompany);
