import { AonElement } from "../../components/AonElement.js";
import { getCompany } from "../../services/service.js";
import { AonApplication } from '../../components/aon-application.js';
import { CONSTANT, MATERIAL_ICONS, MSG } from '../../environments/environments.js';
import { AonPayrollMenu } from './aon-payroll-menu.js';
import * as JSF from '../aon-jsf-app.js';
import { AonComunicaConfig } from "../laboral/aon-comunica-config.js";
import { AON_SALTRA, COMUNICA } from "../../services/app.js";

export class AonPayrollBeta extends AonElement {
	
	AON_PAYROLL_BETA;

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
		this.createApplication(this.AON_PAYROLL_BETA, MSG.PAYROLL, new AonApplication());
	
		this.buildDur().then(() => {
			getCompany().then(company => {
				this.company = company;
				this.build();
			});
		});
	}

	initialize() {
		this.AON_PAYROLL_BETA = "aonPayrollBeta";
	}

	build() {
		this.buildToolbar();

		if (localStorage.getItem("aon_domain_id")) {
			let configurationOptions = [];
			configurationOptions.push({
				id: "parameteraPayroll",
				icon: "settings_applications",
				name: "Parametros Laboral",
				fn: () => this.getApplication().setContent(new JSF.AonJsfPayrollParams()),
			});

			configurationOptions.push({
				id: "parametersContracts",
				icon: "settings_applications",
				name: "Parametros Contratos",
				fn: () => this.getApplication().setContent(new JSF.AonJsfContractParams()),
			});
			
			if (this.getDur().isSaltraManager()) {
				configurationOptions.push({
					id: AON_SALTRA.title,
					name: AON_SALTRA.title,
					icon: MATERIAL_ICONS.ALTERNATE_EMAIL,
					fn: () => this.buildComunicaConfiguration(),
				});
			} else if (this.getDur().isComunicaManager()) {
				configurationOptions.push({
					id: COMUNICA.title,
					name: COMUNICA.title,
					icon: MATERIAL_ICONS.ALTERNATE_EMAIL,
					fn: () => this.buildComunicaConfiguration(),
				});
			}

			configurationOptions.push({
				id: "observations",
				icon: "speaker_notes",
				name: "Observaciones",
				fn: () => this.getApplication().buildObservations('PAYROLL'),
			});

			this.getApplication().addSidenavOptions(MSG.CONFIGURATION.toUpperCase(), configurationOptions);
		}

		if (localStorage.getItem("aon_domain_id") && this.isBeta()) {
			let menuOptions = [];

			menuOptions.push({
				id: "options panel",
				icon: "dashboard",
				name: "Panel Laboral",
				fn: () => this.buildPayrollMenu(),
			});

			this.getApplication().addSidenavOptions(MSG.MENU.toUpperCase(), menuOptions);
		}

		this.buildPayrollMenu();
	}
	
	buildToolbar(){
		this.getApplication().removeToolbarOptions();
		this.getApplication().addCompanyNotes(this.company.id, 'PAYROLL');
	}

	buildPayrollMenu() {
		this.getApplication().setContent(new AonPayrollMenu());
		if (this.rightPanel && this.rightPanel.isOpen()) {
			this.rightPanel.close();
			this.getApplication().style.gridTemplateColumns = "";
		}
	}

	getOptions(){
		let aonPayrollMenu = new AonPayrollMenu();
		aonPayrollMenu.setDur(this.getDur());
		return aonPayrollMenu.getOptions();
	}
	
	buildComunicaConfiguration() {
		this.getApplication().setContent(new AonComunicaConfig());
	}
}
if(!window.customElements.get("aon-payroll-beta")) {
	window.customElements.define("aon-payroll-beta", AonPayrollBeta);
}
