import {AonElement} from '../../components/AonElement.js';
import { AonApplication } from '../../components/aon-application.js';
import { EVENT, MSG, TAG } from '../../environments/environments.js';
import { AonMobileElaborationList } from './elaboration/aon-mobile-elaboration-list.js';
import Apps from '../../services/app.js';
import * as OPTION from './WarehouseOptions.js';
import { AonMobilePackaging } from './packaging/aon-mobile-packaging.js';

export class AonWarehouse extends AonElement {

	WAREHOUSE;

	constructor () {
		super();
	}

	connectedCallback() {
		this.initialize();
    	this.build();
 	}

	initialize() {
		this.WAREHOUSE = 'aonWarehouse';
	}

 	build() {
		this.createApplication(this.WAREHOUSE, MSG.WAREHOUSE, new AonApplication());
		this.buildSidenav();
		this.selectOption(OPTION.ELABORATION);
	}

	buildSidenav() {
		if(this.isMobile()){
			this.getApplication().addMobileSidenavHeader(Apps.WAREHOUSE);
		}
		this.getApplication().addEventListener(EVENT.SELECT_OPTION, 
			(e) => this.selectOption(e.detail));

		this.buildElaborationOptions();
	}

	buildElaborationOptions() {
		let options = [
			OPTION.ELABORATION,
			OPTION.PACKAGING
		];
	
		this.getApplication().addSidenavOptions(MSG.ELABORATION, options);
	}

	selectOption(option) {
		switch(option.id){
		case OPTION.ELABORATION.id:
			this.aonElaboration();
			break;
		case OPTION.PACKAGING.id:
			this.aonPackaging();
			break;
		default:
			this.aonElaboration();
			break;
		}
	}

	aonElaboration() {
		this.getApplication().setContent(new AonMobileElaborationList());
	}

	aonPackaging() {
		this.getApplication().setContent(new AonMobilePackaging());
	}

}
if(!window.customElements.get(TAG.AON_WAREHOUSE)){
	window.customElements.define(TAG.AON_WAREHOUSE, AonWarehouse);
}
