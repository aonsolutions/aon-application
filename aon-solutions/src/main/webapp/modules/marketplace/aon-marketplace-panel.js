// COMPONENTS
import {AonElement} from '../../components/AonElement.js';
import {AonApplication} from '../../components/aon-application.js';
import {AonMarketplace} from './aon-marketplace.js';

// CONSTANTS
import { CONSTANT, MSG } from '../../environments/environments.js'; 

export class AonMarketplacePanel extends AonElement {

	AON_MARKETPLACE;

	connectedCallback () {
		this.initialize();
		this.innerHTML = `
			<aon-application id="${this.AON_MARKETPLACE}" title="${MSG.BOOKING}"></aon-application>
		`;

		this.build();
 	}

	initialize() {
		this.AON_MARKETPLACE = CONSTANT.AON_MARKETPLACE_PANEL;
	}

	getApplication(){
		return this.getElement(this.AON_MARKETPLACE);
	}

 	build() {
		let application = this.getApplication();
		application.closeSidenav();
		application.setContentHTML(
			`<aon-marketplace id="aonMarketplace" > </aon-marketplace>`
		);

	}
}

if(!window.customElements.get('aon-marketplace-panel')){
	window.customElements.define('aon-marketplace-panel', AonMarketplacePanel);
}
