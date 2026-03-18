import { AonElement } from "../../components/AonElement.js";
import { getCompany } from "../../services/service.js";
import { AonApplication } from '../../components/aon-application.js';
import { CONSTANT, MSG } from '../../environments/environments.js';
import * as JSF from '../aon-jsf-app.js';
import { AonFiscalMenu } from './aon-fiscal-menu.js';

export class AonFiscalBeta extends AonElement {

	AON_FISCAL_BETA;

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	constructor() {
		super();
	}

	connectedCallback() {
		this.initialize();
		this.createApplication(this.AON_FISCAL_BETA, MSG.FISCAL, new AonApplication());

		this.buildDur().then(() => {
			getCompany().then(company => {
				this.company = company;
				this.build();
			});
		});
	}

	initialize() {
		this.AON_FISCAL_BETA = "aonFiscalBeta";
	}

	build() {
		this.buildToolbar();
		
		let aonFiscalBeta = this.getElement(this.AON_FISCAL_BETA);

		if (localStorage.getItem("aon_domain_id")) {
			let configurationOptions = [];
			configurationOptions.push({
				id: "parameters",
				icon: "settings_applications",
				name: "Parametros",
				fn: () => this.getApplication().setContent(new JSF.AonJsfFiscalParams()),
			});

			configurationOptions.push({
				id: "observations",
				icon: "speaker_notes",
				name: "Observaciones",
				fn: () => this.buildObservations(),
			});

			aonFiscalBeta.addSidenavOptions(MSG.CONFIGURATION.toUpperCase(), configurationOptions);
		}

		if (localStorage.getItem("aon_domain_id") && this.isBeta()) {
			let menuOptions = [];

			menuOptions.push({
				id: "options panel",
				icon: "dashboard",
				name: "Panel Contabilidad",
				fn: () => this.buildFiscalMenu(),
			});

			aonFiscalBeta.addSidenavOptions(MSG.MENU.toUpperCase(), menuOptions);
		}

		this.buildFiscalMenu();
	}
	
	buildToolbar(){
		this.getApplication().removeToolbarOptions();
		this.getApplication().addCompanyNotes(this.company.id, 'FISCAL');
	}

	buildFiscalMenu() {
		this.getApplication().setContent(new AonFiscalMenu());
	}

	getOptions(){
		let aonFiscalMenu = new AonFiscalMenu();
		aonFiscalMenu.setDur(this.getDur());
		return aonFiscalMenu.getOptions();
	}

}
if(!window.customElements.get("aon-fiscal-beta")) {
	window.customElements.define("aon-fiscal-beta", AonFiscalBeta);
}
