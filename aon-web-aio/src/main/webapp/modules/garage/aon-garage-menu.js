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
            title: "Cuenta contable",
            action: () => alert("description")
        },{
            title: "Ficha de amortización",
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
                description: "Órdenes de Reparación",
                title: "Órdenes de Reparación",
                action: () => this.rootPanel(new JSF.AonJsfProjectTas())
            },
            {
                description: "Órdenes de Reparación (Excel)",
                title: "Órdenes de Reparación (Excel)",
                action: () => GWT.iLoad(GWT.PROJECT_TAS_MODULE)
            }, {
                description: "Operarios",
                title: "Operarios",
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
                description: "Agentes Comerciales",
                title: "Agentes Comerciales",
                action: () => this.rootPanel(new JSF.AonJsfSeller())
            }, {
                description: "Condiciones Comerciales",
                title: "Condiciones Comerciales",
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
                description: "Vehículos y Titulares",
                title: "Vehículos y Titulares",
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
