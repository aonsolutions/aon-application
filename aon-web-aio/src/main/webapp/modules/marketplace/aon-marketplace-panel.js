// COMPONENTS
import {AonElement} from '../../components/AonElement.js';
import '../../components/aon-application.js';
import './aon-booking.js';
import 'aoncss'
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
		if(application) {
			application.closeSidenav();
			application.setContentHTML(
				`<aon-booking id="aonMarketplace"></aon-booking>`
			);
		}

	}
}

if(!window.customElements.get('aon-marketplace-panel')){
	window.customElements.define('aon-marketplace-panel', AonMarketplacePanel);
}
