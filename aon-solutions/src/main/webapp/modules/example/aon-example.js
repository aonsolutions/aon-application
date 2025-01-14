import {AonElement} from '../../components/AonElement.js';
import { AonApplication } from '../../components/aon-application.js';
import { TAG, MSG} from '../../environments/environments.js';
import { ExampleSidenav } from './ExampleOptions.js';
import { AonExampleList } from './aon-example-list.js';

export class AonExample extends AonElement {

	AON_EXAMPLE;

	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
    	this.build();
 	}

	initialize(){
		this.AON_EXAMPLE = 'aonExample';
	}

 	build() {
		this.createApplication(this.AON_EXAMPLE, MSG.EXAMPLE, new AonApplication());
		this.buildSidenav();
		this.buildContent();
	}

	buildSidenav() {
		this.getApplication().addSidenavOptions3(ExampleSidenav.EXAMPLE, () => this.buildContent());
	}

	buildContent() {	
		this.getApplication().setContent(new AonExampleList());
	}
}
if(!window.customElements.get(TAG.AON_EXAMPLE)){
	window.customElements.define(TAG.AON_EXAMPLE, AonExample);
}