import {AonElement} from '../../components/AonElement.js';

import '../../components/aon-card.js';
import '../../components/aon-input.js';


export class AonCompany extends AonElement {

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
		card.addContent(`
			<form action="#" class="aon-margin-0">
				<aon-input class="aonWidth25" id="aonConfigurationGeneralNif" description="NIF" value="${company.document}"></aon-input>
				<aon-input class="aonWidth75" id="aonConfigurationGeneralName" description="Razón Social" value="${company.name}"></aon-input>
			</form>
			<form action="#" class="aon-margin-0">
				<aon-input type="address" class="aon-width-100" id="aonConfigurationGeneralAddress" description="Dirección"></aon-input>
			</form>
		`);

		let card2 = document.getElementById('aonConfigurationGeneral2Card');
		card2.addContent(`
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

}

window.customElements.define('aon-company', AonCompany);
