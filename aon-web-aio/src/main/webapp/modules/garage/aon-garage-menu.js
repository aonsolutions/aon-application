import { MSG, TAG } from '../../environments/environments.js'; 
import { AonSuiteMenu } from '../aon-suite-menu.js';
import * as JSF from '../aon-jsf-app.js';
import * as GWT from '../../gwt/gwt.js';

export class AonGarageMenu extends AonSuiteMenu {

	AON_MENU;
	AON_HEADER;
	ROOT_PANEL;
	RIGHT_PANEL;

	constructor () {
		super();
		this.garageInitialize()
	}

	connectedCallback () {
		this.clear();
		this.initialize();
		this.build();
		this.setTitle("Opciones de taller");
	}

	garageInitialize() {
		this.AON_MENU = 'aonMenu';
		this.AON_HEADER = 'aonHeader';
		this.ROOT_PANEL = 'rootPanel';
		this.RIGHT_PANEL = 'rightPanel';
		this.last = MSG.LAST_ENTRIES;
		this.new = MSG.NEW_ENTRY;
        this.uploadButton = true
		this.selectOptions= [{
            title: MSG.ACCOUNTING_ACCOUNT,
            action: () => alert("description")
        },{
            title: MSG.AMORTIZATION_SHEET,
            action: () => alert("description")
        },{
            title: MSG.ENTRY,
            action: () => alert("description")
        }];
		this.initOptions();
	}


    initOptions() {
        this.options = [{
            title: MSG.REPAIR_ORDERS,
            options: [{
                description: MSG.REPAIR_ORDERS,
                title: MSG.REPAIR_ORDERS,
                action: () => this.rootPanel(new JSF.AonJsfProjectTas())
            },
            {
                description: MSG.REPAIR_ORDERS_EXCEL,
                title: MSG.REPAIR_ORDERS_EXCEL,
                action: () => GWT.iLoad(GWT.PROJECT_TAS_MODULE)
            }, {
                description: MSG.OPERATORS,
                title: MSG.OPERATORS,
                action: () => this.rootPanel(new JSF.AonJsfTaskHolder())
            }]
        }, {
            title: MSG.BUDGETS,
            options: [{
                description: MSG.OFFERS,
                title: MSG.OFFERS,
                action: () => this.rootPanel(new JSF.AonJsfOffer())
            }, {
                description: MSG.TARGETS,
                title: MSG.TARGETS,
                action: () => this.rootPanel(new JSF.AonJsfTarget())
            }, {
                description: MSG.AGENTS_COMMERCIAL,
                title: MSG.AGENTS_COMMERCIAL,
                action: () => this.rootPanel(new JSF.AonJsfSeller())
            }, {
                description: MSG.COMMERCIAL_TERMS,
                title: MSG.COMMERCIAL_TERMS,
                action: () => this.rootPanel(new JSF.AonJsfCommercialTerm())
            }]
        }, {
            title: MSG.GENERAL_DATA,
            options: [{
                description: MSG.MAKES,
                title: MSG.MAKES,
                action: () => this.rootPanel(new JSF.AonJsfMake())
            }, {
                description: MSG.MODEL,
                title: MSG.MODEL,
                action: () => this.rootPanel(new JSF.AonJsfModel())
            }, {
                description: MSG.VEHICLES,
                title: MSG.VEHICLES,
                action: () => this.rootPanel(new JSF.AonJsfTasItem())
            }]
        }, {
            title: MSG.REPORTS,
            options: [{
                description: MSG.VEHICLES_AND_OWNERS,
                title: MSG.VEHICLES_AND_OWNERS,
                action: () => this.rootPanel(new JSF.AonJsfTasStat())
            }]
        }];
    }
	/*
	build() {
        
	}
	*/
}
if(!window.customElements.get(TAG.AON_GARAGE_MENU)){
	window.customElements.define(TAG.AON_GARAGE_MENU, AonGarageMenu);
}
