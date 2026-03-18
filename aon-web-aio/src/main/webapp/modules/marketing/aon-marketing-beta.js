import { AonElement } from "../../components/AonElement.js";
import { getCompany } from "../../services/service.js";
import { AonApplication } from '../../components/aon-application.js';
import { CONSTANT, MSG } from '../../environments/environments.js';
import { AonMarketingMenu } from "./aon-marketing-menu.js";

export class AonMarketingBeta extends AonElement {

	AON_MARKETING_BETA;

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
		this.createApplication(this.AON_MARKETING_BETA, MSG.MARKETING, new AonApplication());
		
		this.buildDur().then(() => {
			getCompany().then(company => {
				this.company = company;
				this.build();
			});
		});
	}

	initialize() {
		this.AON_MARKETING_BETA = "aonMarketingBeta";
	}

	build() {
		this.buildToolbar();
		this.buildSidenav();
		this.buildMarketingMenu();
	}
	
	buildToolbar(){
		this.getApplication().removeToolbarOptions();
		this.getApplication().addCompanyNotes(this.company.id, 'MARKETING');
	}

	buildSidenav() {
		let configurationOptions = [
			{
				id: "observations",
				icon: "speaker_notes",
				name: "Observaciones",
				fn: () => this.getApplication().buildObservations('MARKETING')
			}
		];
		this.getApplication().addSidenavOptions(MSG.CONFIGURATION.toUpperCase(), configurationOptions);
	}

	buildMarketingMenu() {
		this.getApplication().setContent(new AonMarketingMenu());
	}
}
if(!window.customElements.get("aon-marketing-beta")) {
	window.customElements.define("aon-marketing-beta", AonMarketingBeta);
}