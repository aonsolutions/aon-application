import { AonElement } from "../../components/AonElement.js";
import { getCompany } from "../../services/service.js";
import { AonApplication } from '../../components/aon-application.js';
import { CONSTANT, MSG, TAG } from '../../environments/environments.js';
import { AonGroupwareMenu } from "./aon-groupware-menu.js";

export class AonGroupware extends AonElement {

	GROUPWARE;

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
		this.createApplication(this.GROUPWARE, MSG.GROUPWARE, new AonApplication());
		
		this.buildDur().then(() => {
			getCompany().then(company => {
				this.company = company;
				this.build();
			});
		});
	}

	initialize() {
		this.GROUPWARE = "aonGroupware";
	}

	build() {
		this.buildToolbar();
		this.buildSidenav();
		this.buildGroupwareMenu();
	}
	
	buildToolbar(){
		this.getApplication().removeToolbarOptions();
		this.getApplication().addCompanyNotes(this.company.registry, 'GROUPWARE');
	}

	buildSidenav() {
		let configurationOptions = [
			{
				id: "observations",
				icon: "speaker_notes",
				name: "Observaciones",
				fn: () => this.buildObservations('GROUPWARE'),
			}
		];
		this.getApplication().addSidenavOptions(MSG.CONFIGURATION.toUpperCase(), configurationOptions);
	}

	buildGroupwareMenu() {
		this.getApplication().setContent(new AonGroupwareMenu());
	}
}
if(!window.customElements.get(TAG.AON_GROUPWARE)) {
	window.customElements.define(TAG.AON_GROUPWARE, AonGroupware);
}