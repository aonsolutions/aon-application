import { AonElement } from "../../components/AonElement.js";
import { getCompany } from "../../services/service.js";
import { AonApplication } from '../../components/aon-application.js';
import { CONSTANT, MSG, TAG } from '../../environments/environments.js';
import { AonCommercialMenu } from "./aon-commercial-menu.js";

export class AonCommercial extends AonElement {

	COMMERCIAL;

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
		this.createApplication(this.COMMERCIAL, MSG.COMMERCIAL, new AonApplication());
		
		this.buildDur().then(() => {
			getCompany().then(company => {
				this.company = company;
				this.build();
			});
		});
	}

	initialize() {
		this.COMMERCIAL = "aonCommercial";
	}

	build() {
		this.buildToolbar();
		this.buildSidenav();
		this.buildCommercialMenu();
	}
	
	buildToolbar(){
		this.getApplication().removeToolbarOptions();
		this.getApplication().addCompanyNotes(this.company.id, 'COMMERCIAL');
	}

	buildSidenav() {
		let configurationOptions = [
			{
				id: "observations",
				icon: "speaker_notes",
				name: "Observaciones",
				fn: () => this.buildObservations('COMMERCIAL'),
			}
		];
		this.getApplication().addSidenavOptions(MSG.CONFIGURATION.toUpperCase(), configurationOptions);
	}

	buildCommercialMenu() {
		this.getApplication().setContent(new AonCommercialMenu());
		if (this.rightPanel && this.rightPanel.isOpen()) {
			this.rightPanel.close();
			this.getApplication().style.gridTemplateColumns = "";
		}
	}
}
if(!window.customElements.get(TAG.AON_COMMERCIAL)) {
	window.customElements.define(TAG.AON_COMMERCIAL, AonCommercial);
}