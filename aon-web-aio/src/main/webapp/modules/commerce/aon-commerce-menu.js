import { MSG, CSS, EVENT, TAG } from '../../environments/environments.js'; 
import { AonSuiteMenu } from '../aon-suite-menu.js';
import * as GWT from '../../gwt/gwt.js';
import * as JSF from '../aon-jsf-app.js';

export class AonCommerceMenu extends AonSuiteMenu {

	AON_MENU;
	AON_HEADER;
	ROOT_PANEL;
	RIGHT_PANEL;

	constructor () {
		super();
		this.accountingInitialize()
	}

	connectedCallback () {
		this.clear();
		this.initialize();
		this.build();
		this.setTitle("Opciones de TPV");
	}

	accountingInitialize() {
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
			title: 'TPV',
			options: [ {
				description:"Apertura de Caja",
				title:"Apertura de Caja",
				action: () => this.rootPanel(new JSF.AonJsfPosOpening )
			},{
				description: "Arqueo de Caja",
				title: "Arqueo de Caja",
				action: () => this.rootPanel(new JSF.AonJsfPosClosing )
			},{
				description: "Ventas TPV",
				title: "Ventas TPV",
				action: () => this.rootPanel(new JSF.AonJsfPosInvoice )
			},{
				description: "Remesar Cobros de Cajas",
				title: "Remesar Cobros de Cajas",
				action: () => this.rootPanel(new JSF.AonJsfPosFinance )
			},{
				description: "Productos",
				title: "Productos",
				action: () => this.rootPanel(new JSF.AonJsfProduct )
			}]
		},{
			title: MSG.AUXILIARIES,
			options: [{
				description: "Etiquetas de Productos",
				title: "Etiquetas de Productos",
				action: () => this.rootPanel(new JSF.AonJsfProductTag )
			},{
				description: "Categorías",
				title: "Categorías",
				action: () => this.rootPanel(new JSF.AonJsfProductCategory )
			},{
				description: "Definición de Caja",
				title: "Definición de Caja",
				action: () => this.rootPanel(new JSF.AonJsfPos )
			},{
				description: "Turnos de Caja",
				title: "Turnos de Caja",
				action: () => this.rootPanel(new JSF.AonJsfPosShift )
			},{
				description: "Impresión de etiquetas de productos",
				title: "Impresión de etiquetas de productos",
				action: () => this.rootPanel(new JSF.AonJsfItemTagPrint )
			}]
		}];
	}
	/*
	build() {
        
	}
	*/
}
if(!window.customElements.get(TAG.AON_COMMERCE_MENU)){
	window.customElements.define(TAG.AON_COMMERCE_MENU, AonCommerceMenu);
}
