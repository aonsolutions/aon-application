import { AonElement } from "../../components/AonElement.js";
import { getCompany } from "../../services/service.js";
import { AonApplication } from '../../components/aon-application.js';
import { CONSTANT, MSG, TAG } from '../../environments/environments.js';
import { AonCommerceMenu } from "./aon-commerce-menu.js";

export class AonCommerce extends AonElement {

	COMMERCE;

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
		this.createApplication(this.COMMERCE, MSG.COMMERCE, new AonApplication());
		
		this.buildDur().then(() => {
			getCompany().then(company => {
				this.company = company;
				this.build();
			});
		});
	}

	initialize() {
		this.COMMERCE = "aonCommerce";
	}

	build() {
		this.buildToolbar();
		this.buildSidenav();
		this.buildCommerceMenu();
	}
	
	buildToolbar(){
		this.getApplication().removeToolbarOptions();
		this.getApplication().addCompanyNotes(this.company.registry, 'COMMERCE');
	}

	buildSidenav() {
		let configurationOptions = [
			{
				id: "observations",
				icon: "speaker_notes",
				name: "Observaciones",
				fn: () => this.buildObservations('COMMERCE'),
			}
		];
		this.getApplication().addSidenavOptions(MSG.CONFIGURATION.toUpperCase(), configurationOptions);
	}

	buildCommerceMenu() {
		this.getApplication().setContent(new AonCommerceMenu());
	}
}
if(!window.customElements.get(TAG.AON_COMMERCE)) {
	window.customElements.define(TAG.AON_COMMERCE, AonCommerce);
}