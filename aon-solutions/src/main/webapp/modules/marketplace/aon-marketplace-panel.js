// COMPONENTS
import {AonElement} from '../../components/AonElement.js';
import {AonApplication} from '../../components/aon-application.js';
import {AonMarketplace} from './aon-marketplace.js';

// CONSTANTS
import * as CONSTANT from "../../environments/constants.js";
import * as MSG from "../../environments/msg.js";
import * as MATERIAL_ICONS from "../../environments/materialIcons.js";

export class AonMarketplacePanel extends AonElement {

	AON_MARKETPLACE;

	connectedCallback () {
		this.initialize();
		this.innerHTML = `
			<aon-application id="${this.AON_MARKETPLACE}" title="${MSG.AON_MSG_BOOKING}"></aon-application>
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
