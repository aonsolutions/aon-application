import { AonElement } from "../../components/AonElement.js";
import { getCompany } from "../../services/service.js";
import { AonApplication } from '../../components/aon-application.js';
import { CONSTANT, MSG, TAG } from '../../environments/environments.js';
import * as LS from '../../services/localStorageService.js';
import { AonTreasuryMenu } from "./aon-treasury-menu.js";

export class AonTreasury extends AonElement {
	
	TREASURY;
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
		this.createApplication(this.TREASURY, MSG.TREASURY, new AonApplication());
		
		this.buildDur().then(() => {
			getCompany().then(company => {
				this.company = company;
				this.build();
			});
		});
	}

	initialize() {
		this.TREASURY = "aonTreasury";
	}

	build() {
		this.buildToolbar();
		this.buildSidenav();
		this.buildTreasuryMenu();		
	}
	
	buildToolbar(){
		this.getApplication().removeToolbarOptions();
		this.getApplication().addCompanyNotes(this.company.id, 'TREASURY');
	}

	buildSidenav() {
		let configurationOptions = [
			{
				id: "observations",
				icon: "speaker_notes",
				name: "Observaciones",
				fn: () => this.getApplication().buildObservations('TREASURY'),
			}
		];
		this.getApplication().addSidenavOptions(MSG.CONFIGURATION.toUpperCase(), configurationOptions);
	}

	buildTreasuryMenu() {
		let menu = new AonTreasuryMenu();
		menu.setDur(this.getDur());
		this.getApplication().setContent(menu);
	}
}

window.customElements.define(TAG.AON_TREASURY, AonTreasury);
