import { AonElement } from "../../components/AonElement.js";
import { getCompany } from "../../services/service.js";
import { AonApplication } from '../../components/aon-application.js';
import { CONSTANT, MATERIAL_ICONS, MSG } from '../../environments/environments.js';
import { AonPayrollMenu } from './aon-payroll-menu.js';
import * as JSF from '../aon-jsf-app.js';
import * as GWT from '../../gwt/gwt.js';
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
			let utilitiesOptions = [];
			
			utilitiesOptions.push({
				id: "observations",
				icon: "speaker_notes",
				name: "Observaciones",
				fn: () => this.getApplication().buildObservations('PAYROLL'),
			});
			
			if(this.getDur().isDomainManagementAvailable()){
				utilitiesOptions.push({
					id: "resumenActividad",
					icon: "speaker_notes",
					name: "Resumen Actividad",
					fn: () => GWT.iLoad(GWT.ACTIVITY_SUMMARY, this.getApplication().CONTENT),
				});
				
				utilitiesOptions.push({
					id: "calculoNominas",
					icon: "speaker_notes",
					name: "Cálculo Nóminas",
					fn: () => GWT.iLoad(GWT.MAIN_CALCULATOR, this.getApplication().CONTENT),
				});
				
				utilitiesOptions.push({
					id: "impresionMailNominas",
					icon: "speaker_notes",
					name: "Impresión / eMail Nóminas",
					fn: () => GWT.iLoad(GWT.MAIN_SALARY_PRINT, this.getApplication().CONTENT),
				});
			}
			
			if(!this.getDur().isDomainManagementAvailable()){
				utilitiesOptions.push({
					id: "parameteraPayroll",
					icon: "settings_applications",
					name: "Parametros Laboral",
					fn: () => this.getApplication().setContent(new JSF.AonJsfPayrollParams()),
				});
	
				utilitiesOptions.push({
					id: "parametersContracts",
					icon: "settings_applications",
					name: "Parametros Contratos",
					fn: () => this.getApplication().setContent(new JSF.AonJsfContractParams()),
				});
			}

			this.getApplication().addSidenavOptions(MSG.UTILITIES.toUpperCase(), utilitiesOptions);
		}
		
		if(this.getDur().isDomainManagementAvailable()){
			let tgssOptions = [];
			
			tgssOptions.push({
					id: "creata",
					icon: "speaker_notes",
					name: "Cret@",
					fn: () => GWT.iLoad(GWT.MAIN_CRETA, this.getApplication().CONTENT),
				});
				
			tgssOptions.push({
					id: "cra",
					icon: "speaker_notes",
					name: "CRA",
					fn: () => GWT.iLoad(GWT.MAIN_CRA, this.getApplication().CONTENT),
				});
				
			tgssOptions.push({
					id: "afiAgrario",
					icon: "speaker_notes",
					name: "AFI - Jornadas Agrarias",
					fn: () => GWT.iLoad(GWT.MAIN_AFI, this.getApplication().CONTENT),
				});
				
			tgssOptions.push({
					id: "afiPensiones",
					icon: "speaker_notes",
					name: "AFI - Planes Pensiones",
					fn: () => GWT.iLoad(GWT.PENSION_PLAN_AFI, this.getApplication().CONTENT),
				});
				
			tgssOptions.push({
					id: "fie",
					icon: "speaker_notes",
					name: "FIE - Importación masiva ITs",
					fn: () => GWT.iLoad(GWT.MASSIVE_FIE, this.getApplication().CONTENT),
				});
				
			this.getApplication().addSidenavOptions("Procesos TGSS".toUpperCase(), tgssOptions);
		}
		
		let paramsOptions = [];
		
		if(this.getDur().isDomainManagementAvailable()){
			paramsOptions.push({
					id: "convenios",
					icon: "speaker_notes",
					name: "Convenios",
					fn: () => GWT.iLoad(GWT.CONVENIOS, this.getApplication().CONTENT),
				});
				
			paramsOptions.push({
					id: "contractModel",
					icon: "speaker_notes",
					name: "Modelos de contrato",
					fn: () => this.getApplication().setContent(new JSF.AonJsfContractOption()),
				});
				
			paramsOptions.push({
					id: "formationWorkplace",
					icon: "speaker_notes",
					name: "Centros Formación",
					fn: () => this.getApplication().setContent(new JSF.AonJsfTrainningCenter()),
				});
				
			paramsOptions.push({
					id: "festivos",
					icon: "speaker_notes",
					name: "Festivos",
					fn: () => this.getApplication().setContent(new JSF.AonJsfHolidays()),
				});
				
		}
			
		if (this.getDur().isSaltraManager()) {
			paramsOptions.push({
				id: AON_SALTRA.title,
				name: AON_SALTRA.title,
				icon: MATERIAL_ICONS.ALTERNATE_EMAIL,
				fn: () => this.buildComunicaConfiguration(),
			});
		} else if (this.getDur().isComunicaManager()) {
			paramsOptions.push({
				id: COMUNICA.title,
				name: COMUNICA.title,
				icon: MATERIAL_ICONS.ALTERNATE_EMAIL,
				fn: () => this.buildComunicaConfiguration(),
			});
		}
		
		if(paramsOptions.length > 0)
			this.getApplication().addSidenavOptions("Parámetros".toUpperCase(), paramsOptions);

		if (localStorage.getItem("aon_domain_id") && this.isBeta()) {
			let menuOptions = [];

			menuOptions.push({
				id: "options panel",
				icon: "dashboard",
				name: "Panel Opciones",
				fn: () => this.buildPayrollMenu(),
			});

			this.getApplication().addSidenavOptions(MSG.MENU.toUpperCase(), menuOptions);
		}

		this.buildPayrollMenu();
		
		// Load payroll activity sumary by default if its Entorno
		if (this.getDur().isDomainManagementAvailable()) {
			let resumenActividad = this.getElement('aonPayrollBetaSidenavresumenActividad');
			resumenActividad && resumenActividad.click();
			//GWT.iLoad(GWT.ACTIVITY_SUMMARY, this.getApplication().CONTENT);
		}
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
