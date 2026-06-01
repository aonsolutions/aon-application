import { MSG, CSS, EVENT, TAG } from '../../environments/environments.js';
import { AonSuiteMenu } from '../aon-suite-menu.js';
import * as JSF from '../aon-jsf-app.js';

export class AonEnterpriseMenu extends AonSuiteMenu {

	AON_MENU;
	AON_HEADER;
	ROOT_PANEL;
	RIGHT_PANEL;

	constructor () {
		super();
		this.enterpriseInitialize()
	}

	connectedCallback () {
		this.clear();
		this.initialize();
		this.build();
		this.setTitle("Opciones de TPV");
	}

	enterpriseInitialize() {
		this.AON_MENU = 'aonMenu';
		this.AON_HEADER = 'aonHeader';
		this.ROOT_PANEL = 'rootPanel';
		this.RIGHT_PANEL = 'rightPanel';
		this.last = MSG.DELETE_COMPANIES;
		this.new = MSG.CREATE_COMPANIES;
		this.selectOptions= [];
		this.initOptions();
	}


    initOptions() {
        this.options = [{
            title: MSG.COMPANIES,
            options: [{
                description: MSG.CREATE_COMPANIES,
                title: MSG.CREATE_COMPANIES,
                action: () => this.rootPanel(new JSF.AonJsfNewDomain)
            }, {
                description: MSG.DELETE_COMPANIES,
                title: MSG.DELETE_COMPANIES,
                action: () => this.rootPanel(new JSF.AonJsfRemoveDomain)
            }]
        }];
    }
}
if(!window.customElements.get(TAG.AON_ENTERPRISE_MENU)){
	window.customElements.define(TAG.AON_ENTERPRISE_MENU, AonEnterpriseMenu);
}
