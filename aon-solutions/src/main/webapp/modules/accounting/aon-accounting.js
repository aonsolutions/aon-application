// COMPONENTS
import {AonElement} from '../../components/AonElement.js';
import {AonApplication} from '../../components/aon-application.js';

// SERVICES
import {getDomainUserRoles} from '../../services/service.js';
// import {createApplication} from '../../services/utils.js';

// MODELS
import {DomainUserRoles} from '../../models/DomainUserRoles.js';

// CONSTANTS
import * as CONSTANT from "../../environments/constants.js";
import * as MSG from "../../environments/msg.js";
import * as MATERIAL_ICONS from "../../environments/materialIcons.js";

export class AonAccounting extends AonElement {

	_roles;

	AON_ACCOUNTING;

	connectedCallback () {
		this.initialize();

		// let application = createApplication(this.AON_ACCOUNTING, MSG.AON_MSG_ACCOUNTING);
		// this.appendChild(application);


		getDomainUserRoles({}).then(r => {
			this._roles = new DomainUserRoles(r);
			// this.build();
		});
 	}

	initialize() {
		this.AON_ACCOUNTING = CONSTANT.AON_ACCOUNTING;
	}

	getApplication(){
		return this.getElement(this.AON_ACCOUNTING);
	}

 	build() {
		let application = this.getApplication();
		application.addToolbarOption('Add', 'add', () => {alert('Add Example')});

		let options = [{
			name: 'Prueba',
			icon: 'accessibility',
			fn: () => alert('PRUEBA!!')
		}];
		application.addSidenavOptions('OPCIONES', options);
	}
}
if(!window.customElements.get('aon-accounting')){
	window.customElements.define('aon-accounting', AonAccounting);
}
