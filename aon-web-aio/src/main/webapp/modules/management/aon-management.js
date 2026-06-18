import { AonElement } from "../../components/AonElement.js";
import { getCompany } from "../../services/service.js";
import { AonApplication } from '../../components/aon-application.js';
import { CONSTANT, MSG, TAG } from '../../environments/environments.js';
import { AonManagementMenu } from "./aon-management-menu.js";

export class AonManagement extends AonElement {

	MANAGEMENT;

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
		this.createApplication(this.MANAGEMENT, MSG.MANAGEMENT, new AonApplication());
		
		this.buildDur().then(() => {
			getCompany().then(company => {
				this.company = company;
				this.build();
			});
		});
	}

	initialize() {
		this.MANAGEMENT = "aonManagementBeta";
	}

	build() {
		this.buildToolbar();
		this.buildSidenav();
		this.buildManagementMenu();
	}
	
	buildToolbar(){
		this.getApplication().removeToolbarOptions();
		this.getApplication().addCompanyNotes(this.company.id, 'MANAGEMENT');
	}

	buildSidenav() {
		let configurationOptions = [
			{
				id: "observations",
				icon: "speaker_notes",
				name: "Observaciones",
				fn: () => this.getApplication().buildObservations('MANAGEMENT')
			},
			{
				id: "series",
				name: "Series",
				fn: () => this.getApplication().setContent(new JSF.AonJsfSeries()),
			},
			{
				id: "impuestos",
				name: "Impuestos aplicables",
				fn: () => this.getApplication().setContent(new JSF.AonJsfTax()),
			}
		];
		this.getApplication().addSidenavOptions(MSG.CONFIGURATION.toUpperCase(), configurationOptions);
		
		let parametersOptions = [
			{
				id: "segmentation",
				name: MSG.SEGMENTATION,
				fn: () => this.getApplication().setContent(new JSF.AonJsfSegment()),
			},
			{
				id: "countryProvince",
				name: MSG.COUNTRY_PROVINCE,
				fn: () => this.getApplication().setContent(new JSF.AonJsfGeotree()),
			}
		];
		this.getApplication().addSidenavOptions(MSG.PARAMETERS.toUpperCase(), parametersOptions);
		
		let utilitiesOptions = [
			{
				id: "billignConsole",
				name: MSG.BILLING_CONSOLE,
				fn: () => GWT.iLoad(GWT.INVOICE_CONSOLE),
			}
		];
		this.getApplication().addSidenavOptions(MSG.UTILITIES.toUpperCase(), utilitiesOptions);
	}
	
	buildManagementMenu() {
		this.getApplication().setContent(new AonManagementMenu());
	}
}
if(!window.customElements.get(TAG.AON_MANAGEMENT)) {
	window.customElements.define(TAG.AON_MANAGEMENT, AonManagement);
}