import {AonElement} from '../../components/AonElement.js';
import '../../components/aon-application.js';

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
		this.paintView();
		this.applicationEl = this.getApplication();
		this.applicationParentEl = this.getApplicationParent();
	}

	paintView(){
		this.innerHTML = /*html*/`<aon-application id="${this.AON_EXAMPLE}" title="Example"></aon-application>`;
	}
}
window.customElements.define('aon-example', AonExample);
