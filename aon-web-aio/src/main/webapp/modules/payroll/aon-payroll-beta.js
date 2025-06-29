import { AonElement } from "../../components/AonElement.js";
import { getCompany, getDomainUserRoles } from "../../services/service.js";
import { DomainUserRoles } from '../../models/DomainUserRoles.js';
import { AonApplication } from '../../components/aon-application.js';
import { CONSTANT, MSG, TAG, EVENT } from '../../environments/environments.js';
import { AonPayrollMenu } from './aon-payroll-menu.js';
import { AonIconButton } from "../../components/aon-icon-button.js";
import * as JSF from '../aon-jsf-app.js';
import * as GWT from '../../gwt/gwt.js';
import * as LS from '../../services/localStorageService.js';

export class AonPayrollBeta extends AonElement {
	AON_PAYROLL_BETA;
	ROOT_PANEL;
	dur;
	righPanel;


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


		getDomainUserRoles({}).then(r => {
			this.dur = new DomainUserRoles(r);
			getCompany().then(company => {
				this.company = company;
				this.build();
			});

		}).catch(() => this.build());
	}

	initialize() {
		this.AON_PAYROLL_BETA = "aonPayrollBeta";
		this.ROOT_PANEL = "rootPanel";
	}

	build() {
		let aonPayrollBeta = this.getElement(this.AON_PAYROLL_BETA);

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

			configurationOptions.push({
				id: "observations",
				icon: "speaker_notes",
				name: "Observaciones",
				fn: () => this.buildObservations(),
			});

			aonPayrollBeta.addSidenavOptions(MSG.CONFIGURATION.toUpperCase(), configurationOptions);
		}

		if (localStorage.getItem("aon_domain_id") && this.isBeta()) {
			let menuOptions = [];

			menuOptions.push({
				id: "options panel",
				icon: "dashboard",
				name: "Panel Contabilidad",
				fn: () => this.buildPayrollMenu(),
			});

			aonPayrollBeta.addSidenavOptions(MSG.MENU.toUpperCase(), menuOptions);
		}

		this.buildPayrollMenu();
	}

	buildObservations() {
		let rightSidenav = this.getApplication().getRightSidenav();
		this.clearElement(rightSidenav);

		let div = this.createElement(TAG.DIV);
		div.style = `
				display: flex;
				flex-direction: column;
				gap: 10px;
				height: 100%;
			`;
		div.id = "customerNotesId";

		if (rightSidenav.style.flexBasis === "0px" || rightSidenav.style.flexBasis.length == 0) {
			rightSidenav.appendChild(div);

			// Loader
			let loaderSpan = this.createElement(TAG.SPAN);
			loaderSpan.className = CONSTANT.SPIN;
			loaderSpan.style.display = 'flex';
			loaderSpan.style.height = '100%';
			loaderSpan.style.justifyContent = 'center';
			loaderSpan.style.alignItems = 'center';

			let aib = new AonIconButton();
			aib.id = 'spinLoader';
			aib.icon = 'sync';
			aib.title = 'Cargando...';
			loaderSpan.appendChild(aib);

			div.appendChild(loaderSpan);

			let company = LS.getCompany();
			console.log(company);

			localStorage.setItem("customer", company.registry);
			localStorage.setItem("notesSource", "PAYROLL");

			GWT.iLoad(GWT.CUSTOMER_NOTES, div.id);
		}

		this.getApplication().toogleRightSidenav();
	}


	buildPayrollMenu() {
		this.getApplication().setContent(new AonPayrollMenu());
		if (this.rightPanel && this.rightPanel.isOpen()) {
			this.rightPanel.close();
			this.getApplication().style.gridTemplateColumns = "";
		}
	}

	getApplication() {
		return this.getElement(this.AON_PAYROLL_BETA);
	}
}

window.customElements.define("aon-payroll-beta", AonPayrollBeta);
