import {AonElement} from '../../components/AonElement.js';
import '../../components/aon-application.js';
import { MSG, TAG } from '../../environments/environments.js';
import { AonApplication } from '../../components/aon-application.js';

export class AonApiDoc extends AonElement {

	AON_API_DOC;

	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
		this.createApplication(this.AON_API_DOC, 
			MSG.API_DOCUMENTATION, new AonApplication());
    	this.build();
 	}

	initialize(){
		this.AON_API_DOC = 'aonApiDoc';
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
if(!window.customElements.get(TAG.AON_API_DOC)){
	window.customElements.define(TAG.AON_API_DOC, AonApiDoc);
 }
