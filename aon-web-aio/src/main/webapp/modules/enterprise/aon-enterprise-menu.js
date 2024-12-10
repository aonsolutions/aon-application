import { MSG, CSS, EVENT, TAG } from 'aonsolutions/environments/environments.js'; 
import { AonSuiteMenu } from '../aon-suite-menu.js';
import * as JSF from '../aon-jsf-app.js';

export class AonEnterpriseMenu extends AonSuiteMenu {

	AON_MENU;
	AON_HEADER;
	ROOT_PANEL;
	RIGHT_PANEL;

	constructor () {
		super();
	}

	connectedCallback () {
		this.clear();
		this.accountingInitialize()
		this.initialize();
		this.build();
		this.setTitle("Opciones de TPV");
	}

	accountingInitialize() {
		this.AON_MENU = 'aonMenu';
		this.AON_HEADER = 'aonHeader';
		this.ROOT_PANEL = 'rootPanel';
		this.RIGHT_PANEL = 'rightPanel';
		this.last = "Borrado de Empresas";
		this.new = "Creación de Empresas"
		this.selectOptions= [];
		this.options = [{
			title: 'Empresas',
			options: [{
				description:"Creación de Empresas",
				title:"Creación de Empresas",
				action: () => this.rootPanel(new JSF.AonJsfNewDomain)
			},{
				description:"Borrado de Empresas",
				title:"Borrado de Empresas",
				action: () => this.rootPanel(new JSF.AonJsfRemoveDomain)
			}]
		}];
	}

}
if(!window.customElements.get(TAG.AON_ENTERPRISE_MENU)){
	window.customElements.define(TAG.AON_ENTERPRISE_MENU, AonEnterpriseMenu);
}
