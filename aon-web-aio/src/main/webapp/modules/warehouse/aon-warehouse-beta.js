import { AonElement } from "../../components/AonElement.js";
import { getCompany } from "../../services/service.js";
import { AonApplication } from '../../components/aon-application.js';
import { CONSTANT, MSG } from '../../environments/environments.js';
import { AonWarehouseMenu } from "./aon-warehouse-menu.js";

export class AonWarehouseBeta extends AonElement {

	AON_WAREHOUSE_BETA;

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
		this.createApplication(this.AON_WAREHOUSE_BETA, MSG.WAREHOUSE, new AonApplication());
		
		this.buildDur().then(() => {
			getCompany().then(company => {
				this.company = company;
				this.build();
			});
		});
	}

	initialize() {
		this.AON_WAREHOUSE_BETA = "aonWarehouseBeta";
	}

	build() {
		this.buildToolbar();
		this.buildSidenav();
		this.buildWarehouseMenu();
	}
	
	buildToolbar(){
		this.getApplication().removeToolbarOptions();
		this.getApplication().addCompanyNotes(this.company.id, 'WAREHOUSE');
	}

	buildSidenav() {
		let configurationOptions = [
			{
				id: "observations",
				icon: "speaker_notes",
				name: "Observaciones",
				fn: () => this.getApplication().buildObservations('WAREHOUSE')
			}
		];
		this.getApplication().addSidenavOptions(MSG.CONFIGURATION.toUpperCase(), configurationOptions);
	}

	buildWarehouseMenu() {
		this.getApplication().setContent(new AonWarehouseMenu());
	}

	getApplication() {
		return this.getElement(this.AON_WAREHOUSE_BETA);
	}
}
if(!window.customElements.get("aon-warehouse-beta")) {
	window.customElements.define("aon-warehouse-beta", AonWarehouseBeta);
}