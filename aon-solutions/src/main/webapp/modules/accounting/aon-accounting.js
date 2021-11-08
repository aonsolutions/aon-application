// COMPONENTS
import {AonElement} from '../../components/AonElement.js';

// SERVICES
import {getDomainUserRoles} from '../../services/service.js';

// MODELS
import {DomainUserRoles} from '../../models/DomainUserRoles.js';

// CONSTANTS

import { CONSTANT, MATERIAL_ICONS, MSG } from "../../environments/environments.js";
import { AonGraphicsTrial } from './aon-graphics-trial.js';
import Apps from '../../services/app.js';

import * as GWT from '../../gwt/gwt.js';

export class AonAccounting extends AonElement {

	dur;
	AON_ACCOUNTING;
	constructor() {
		super();
	}
	connectedCallback () {
		this.initialize();

		this.innerHTML = `
			<aon-application id="${this.AON_ACCOUNTING}" title="${MSG.ACCOUNTING}"></aon-application>
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

		if(this.isMobile()){
			application.addMobileSidenavHeader(Apps.ACCOUNTING);
		}

		let options = [{
			name: 'Pérdidas y Ganancias',
			icon: MATERIAL_ICONS.ACCOUNT_BALANCE,
			fn: () => this.aonGraphicsTrialView ()
		}];
		if(this.dur.isBank())
			options.push({
				id: 'banks',
				name: MSG.BANKS,
				icon: MATERIAL_ICONS.ACCOUNT_BALANCE,
				fn: () => GWT.load(GWT.CHECKIT, this.getApplication().CONTENT)
			});

		application.addSidenavOptions(MSG.ACCOUNTING, options);


		let options2 = [
		{
			name: 'Vista Anual',
			icon: MATERIAL_ICONS.CALENDAR_TODAY,
			fn: () => {}
		},
		{
			name: 'Vista Trimestral',
			icon: MATERIAL_ICONS.CALENDAR_TODAY,
			fn: () => {}
		},
		{
			name: 'Vista Mensual',
			icon: MATERIAL_ICONS.CALENDAR_TODAY,
			fn: () => {}
		},];
		application.addSidenavOptions(MSG.OPTIONS , options2);
	}


	aonGraphicsTrialView () {
		const aonGraphicsTrial = new AonGraphicsTrial();
		this.getApplication().setContent(aonGraphicsTrial);
	}


}
if(!window.customElements.get('aon-accounting')){
	window.customElements.define('aon-accounting', AonAccounting);
}
