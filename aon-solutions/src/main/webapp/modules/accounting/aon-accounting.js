// COMPONENTS
import {AonElement} from '../../components/AonElement.js';

// SERVICES
import {getDomainUserRoles} from '../../services/service.js';

// MODELS
import {DomainUserRoles} from '../../models/DomainUserRoles.js';

// CONSTANTS

import { CONSTANT, MATERIAL_ICONS, MSG } from "../../environments/environments.js";
import { AonGraphicsTrial } from './aon-graphics-trial.js';
import {ACCOUNTING } from '../../services/app.js';

import * as GWT from '../../gwt/gwt.js';
import { waitEl } from '../../services/utils.js';
import { AonApplication } from '../../components/aon-application.js';

export class AonAccounting extends AonElement {

	dur;
	AON_ACCOUNTING;
	constructor() {
		super();
	}

	connectedCallback () {
		this.initialize();
		this.createApplication(this.AON_ACCOUNTING, MSG.ACCOUNTING, new AonApplication());

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
			application.addMobileSidenavHeader(ACCOUNTING);
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
				this.clearElementById(this.getApplication().getContent().id);
				this.aonGraphicsTrialView ();
			}
		}];
		
		if(this.dur.isBank())
			options.push({
				id: 'banks',
				name: MSG.BANKS,
				icon: MATERIAL_ICONS.ACCOUNT_BALANCE,
				fn: () => {
					this.getApplication().removeSidenavById("Opciones");
					this.clearElementById(this.getApplication().getContent().id);
					GWT.load(GWT.CHECKIT, this.getApplication().CONTENT);
					this.loader(`#${this.getApplication().getContent().id} .aon_toolbar`);
				}
			});

		application.addSidenavOptions(MSG.ACCOUNTING, options);
		application.addSidenavOptions(MSG.OPTIONS , options2);
	}

	async loader(selector, doc=undefined) {
		this.getApplication().startLoader();
		try {
			await waitEl(selector, doc);
		} catch (e) {}
		this.getApplication().stopLoader();
	}
	
	aonGraphicsTrialView () {
		this.getApplication().setContent(new AonGraphicsTrial());
	}
}
if(!window.customElements.get('aon-accounting')){
	window.customElements.define('aon-accounting', AonAccounting);
}
