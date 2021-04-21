// COMPONENTS
import {AonElement} from '../../components/AonElement.js';

// SERVICES
import {getDomainUserRoles} from '../../services/service.js';

// MODELS
import {DomainUserRoles} from '../../models/DomainUserRoles.js';

// CONSTANTS
import * as CONSTANT from "../../environments/constants.js";

import { AonGraphicsTrial } from './aon-graphics-trial.js';

export class AonAccounting extends AonElement {

	dur;
	AON_ACCOUNTING;

	connectedCallback () {
		this.initialize();

		this.innerHTML = `
			<aon-application id="${this.AON_ACCOUNTING}" title="Accounting"></aon-application>
		`;

		getDomainUserRoles({}).then(r => {
			this.dur = new DomainUserRoles(r);
			this.build();
		});
 	}

	initialize() {
		this.AON_ACCOUNTING = CONSTANT.AON_ACCOUNTING;
	}

 	build() {
		let application = this.getApplication();

		this.aonGraphicsTrialView();

		let options = [{
			name: 'Perdidas y Ganancias',
			icon: 'accessibility',
			fn: () => this.aonGraphicsTrialView ()
		}];
		application.addSidenavOptions('OPCIONES', options);
	}


	aonGraphicsTrialView () {
		const aonGraphicsTrial = new AonGraphicsTrial();
		this.getApplication().setContent(aonGraphicsTrial);
	}
}
if(!window.customElements.get('aon-accounting')){
	window.customElements.define('aon-accounting', AonAccounting);
}
