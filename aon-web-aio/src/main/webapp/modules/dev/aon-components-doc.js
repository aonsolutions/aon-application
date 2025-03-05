import {AonElement} from '../../components/AonElement.js';
import '../../components/aon-application.js';
import { MSG, TAG } from '../../environments/environments.js';
import { AonApplication } from '../../components/aon-application.js';

export class AonComponentsDoc extends AonElement {

	AON_COMPONENTS_DOC;

	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
		this.createApplication(this.AON_COMPONENTS_DOC, 
			MSG.COMPONENTS, new AonApplication());
    	this.build();
 	}

	initialize(){
		this.AON_COMPONENTS_DOC = 'aonComponentsDoc';
	}

 	build() {
		this.buildToolbar();
		this.buildSidenav();
		this.buildContent();
	}

	buildToolbar() {

	}

	buildSidenav() {
		

	}

	buildContent() {

	}

	
}
if(!window.customElements.get(TAG.AON_COMPONENTS_DOC)){
	window.customElements.define(TAG.AON_COMPONENTS_DOC, AonComponentsDoc);
 }
