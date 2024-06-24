import { MSG, CSS, EVENT, TAG } from 'aonsolutions/environments/environments.js'; 
import { AonSuiteMenu } from '../aon-suite-menu.js';
import * as GWT from 'aonsolutions/gwt/gwt.js';

export class AonCommerceMenu extends AonSuiteMenu {

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
		this.last = "Últimos apuntes";
		this.new = "Nuevo Apunte"
        this.uploadButton = true
		this.selectOptions= [{
            title: "Cuenta contable",
            action: () => alert("description")
        },{
            title: "Ficha de amortización",
            action: () => alert("description")
        },{
            title: "Apunte", 
            action: () => alert("description")
        }];
		this.options = [{
			title: 'TPV',
			options: [ {
				description:"Apertura de Caja",
				title:"Apertura de Caja",
				action: () => alert("Apertura de Caja")
			},{
				description: "Arqueo de Caja",
				title: "Arqueo de Caja",
				action: () => alert("Arqueo de Caja")
			},{
				description: "Ventas TPV",
				title: "Ventas TPV",
				action: () => alert("Ventas TPV")
			},{
				description: "Remesar Cobros de Cajas",
				title: "Remesar Cobros de Cajas",
				action: () => alert("Remesar Cobros de Cajas")
			},{
				description: "Productos",
				title: "Productos",
				action: () => alert("Productos")
			}]
		},{
			title: 'Auxiliares',
			options: [{
				description: "Etiquetas de Productos",
				title: "Etiquetas de Productos",
				action: () => alert("Etiquetas de Productos")
			},{
				description: "Categorías",
				title: "Categorías",
				action: () => alert("Categorías")
			},{
				description: "Definición de Caja",
				title: "Definición de Caja",
				action: () => alert("Definición de Caja")
			},{
				description: "Turnos de Caja",
				title: "Turnos de Caja",
				action: () => alert("Turnos de Caja")
			},{
				description: "Impresión de etiquetas de productos",
				title: "Impresión de etiquetas de productos",
				action: () => alert("Impresión de etiquetas de productos")
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
