import { MSG, CSS, EVENT, TAG } from 'aonsolutions/environments/environments.js'; 
import { AonSuiteMenu } from '../aon-suite-menu.js';
import * as GWT from 'aonsolutions/gwt/gwt.js';

export class AonGarageMenu extends AonSuiteMenu {

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
		this.setTitle("Opciones de taller");
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
			title: 'Órdenes de reparación',
			options: [ {
				description:"Órdenes de Reparación",
				title:"Órdenes de Reparación",
				action: () => alert("Órdenes de Reparación")
			},{
				description: "Operarios",
				title: "Operarios",
				action: () => alert("Operarios")
			}]
		},{
			title: 'Presupuestos',
			options: [{
				description: "Modelos",
				title: "Modelos",
				action: () => alert("Modelos")
			},{
				description: "Clientes Potenciales",
				title: "Clientes Potenciales",
				action: () => alert("Clientes Potenciales")
			},{
				description: "Agentes Comerciales",
				title: "Agentes Comerciales",
				action: () => alert("Agentes Comerciales")
			},{
				description: "Condiciones Comerciales",
				title: "Condiciones Comerciales",
				action: () => alert("Condiciones Comerciales")
			}]
		},{
			title: 'General',
			options: [{
				description: "Marcas",
				title: "Marcas",
				action: () => alert("Marcas")
			},{
				description: "Modelos",
				title: "Modelos",
				action: () => alert("Modelos")
			},{
				description: "Vehículos",
				title: "Vehículos",
				action: () => alert("Vehículos")
			}]
		},{
			title: 'Informes',
			options: [{
				description: "Vehículos y Titulares",
				title: "Vehículos y Titulares",
				action: () => alert("Vehículos y Titulares")
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
