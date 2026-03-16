import { AonElement } from "../../components/AonElement.js";
import { getCompany } from "../../services/service.js";
import { AonApplication } from '../../components/aon-application.js';
import { CONSTANT, MSG, TAG } from '../../environments/environments.js';
import { AonAcademyMenu } from "./aon-academy-menu.js";

export class AonAcademy extends AonElement {

	ACADEMY;

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
		this.createApplication(this.ACADEMY, MSG.ACADEMY, new AonApplication());
		
		this.buildDur().then(() => {
			getCompany().then(company => {
				this.company = company;
				this.build();
			});
		});
	}

	initialize() {
		this.ACADEMY = "aonAcademy";
	}

	build() {
		this.buildToolbar();
		this.buildSidenav();
		this.buildAcademyMenu();
	}
	
	buildToolbar(){
		this.getApplication().removeToolbarOptions();
		this.getApplication().addCompanyNotes(this.company.registry, 'ACADEMY');
	}

	buildSidenav() {
		let configurationOptions = [
			{
				id: "observations",
				icon: "speaker_notes",
				name: "Observaciones",
				fn: () => this.buildObservations('ACADEMY'),
			}
		];
		this.getApplication().addSidenavOptions(MSG.CONFIGURATION.toUpperCase(), configurationOptions);
	}

	buildAcademyMenu() {
		this.getApplication().setContent(new AonAcademyMenu());
	}
}
if(!window.customElements.get(TAG.AON_ACADEMY)) {
	window.customElements.define(TAG.AON_ACADEMY, AonAcademy);
}