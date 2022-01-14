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
import { waitEl } from '../../services/utils.js';

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


		let options2 = [
			{
				id: 'VistaAnual',
				name: 'Vista Anual',
				icon: MATERIAL_ICONS.CALENDAR_TODAY,
				fn: () => {}
			},
			{
				id: 'VistaTrimestral',
				name: 'Vista Trimestral',
				icon: MATERIAL_ICONS.CALENDAR_TODAY,
				fn: () => {}
			},
			{
				id: 'VistaMensual',
				name: 'Vista Mensual',
				icon: MATERIAL_ICONS.CALENDAR_TODAY,
				fn: () => {}
			},];


		let options = [{
			id: 'PyG',
			name: 'Pérdidas y Ganancias',
			icon: MATERIAL_ICONS.BAR_CHART,
			fn: () => {
				application.removeSidenavById("Opciones");
				application.addSidenavOptions(MSG.OPTIONS , options2);
				this.getApplication().stopLoader();
				this.getApplication().startLoader();
				this.aonGraphicsTrialView ();
			}
		}];
		
		if(this.dur.isBank())
			options.push({
				id: 'banks',
				name: MSG.BANKS,
				icon: MATERIAL_ICONS.ACCOUNT_BALANCE,
				fn: () => {
					// this.getApplication().startLoader();
					/*application.removeSidenavById("Opciones");
					this.getApplication().stopLoader();
					this.getApplication().startLoader();
					GWT.load(GWT.CHECKIT, this.getApplication().CONTENT);*/
					this.go2CheckIt();
				}
			});

		application.addSidenavOptions(MSG.ACCOUNTING, options);


		
		application.addSidenavOptions(MSG.OPTIONS , options2);
	}

	async go2CheckIt () {
		this.getApplication().removeSidenavById("Opciones");
		this.clearElementById(this.getApplication().getContent().id);
		this.getApplication().startLoader();
		GWT.load(GWT.CHECKIT, this.getApplication().CONTENT);
		waitEl(`#${this.getApplication().getContent().id} .aon_toolbar`)
		.catch(e => console.log(e))
		.finally(() => this.getApplication().stopLoader());
	}

	aonGraphicsTrialView () {
		const aonGraphicsTrial = new AonGraphicsTrial();
		this.getApplication().setContent(aonGraphicsTrial);
	}


}
if(!window.customElements.get('aon-accounting')){
	window.customElements.define('aon-accounting', AonAccounting);
}
