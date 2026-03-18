import { AonElement } from "../../components/AonElement.js";
import { getCompany } from "../../services/service.js";
import { AonApplication } from '../../components/aon-application.js';
import { CONSTANT, MSG, TAG } from '../../environments/environments.js';
import { AonGarageMenu } from "./aon-garage-menu.js";

export class AonGarage extends AonElement {

	GARAGE;

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
		this.createApplication(this.GARAGE, MSG.GARAGE, new AonApplication());
		
		this.buildDur().then(() => {
			getCompany().then(company => {
				this.company = company;
				this.build();
			});
		});
	}

	initialize() {
		this.GARAGE = "aonGarage";
	}

	build() {
		this.buildToolbar();
		this.buildSidenav();
		this.buildGarageMenu();
	}
	
	buildToolbar(){
		this.getApplication().removeToolbarOptions();
		this.getApplication().addCompanyNotes(this.company.id, 'GARAGE');
	}

	buildSidenav() {
		let configurationOptions = [
			{
				id: "observations",
				icon: "speaker_notes",
				name: "Observaciones",
				fn: () => this.buildObservations('GARAGE'),
			}
		];
		this.getApplication().addSidenavOptions(MSG.CONFIGURATION.toUpperCase(), configurationOptions);
	}

	buildGarageMenu() {
		this.getApplication().setContent(new AonGarageMenu());
	}
}
if(!window.customElements.get(TAG.AON_GARAGE)) {
	window.customElements.define(TAG.AON_GARAGE, AonGarage);
}