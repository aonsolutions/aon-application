import { MSG, CSS, EVENT, TAG } from 'aonsolutions/environments/environments.js'; 
import { AonSuiteMenu } from '../aon-suite-menu.js';

export class AonAccountingMenu extends AonSuiteMenu {

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
		this.setTitle("Accesos rápidos");
	}

	accountingInitialize() {
		this.AON_MENU = 'aonMenu';
		this.AON_HEADER = 'aonHeader';
		this.ROOT_PANEL = 'rootPanel';
		this.RIGHT_PANEL = 'rightPanel';
		this.options = [{
			title: 'Contabilidad',
			options: ["Mantenimiento de Apuntes","Apuntes de Amortizaciones","Asientos de explotación, cierre y apertura",
			"Contabilziación de Facturas","Contabilización de Cobros y Pagos realizados", "Documentos Pendientes"]
		},{
			title: 'Listados Contables',
			options: ["Extracto de cuenta","Cuenta de Explotación (P y G)","Balance de Sumas y Saldos","Listado Diario de Movimientos",
			"Listado Mayor de Cuentas", "Balances oficiales", "Cuenta de Explotación (P y G) ANALÍTICA"]
		},{
			title: 'Tablas Auxiliares',
			options: ["Plan General Contable","Conceptos Automáticos","Centros de Costo","Ejercicios Contables",
			"Clientes", "Proveedores", "Acreedores"]
		}];
	}
	/*
	build() {
        
	}
	*/
}
if(!window.customElements.get(TAG.AON_ACCOUNTING_MENU)){
	window.customElements.define(TAG.AON_ACCOUNTING_MENU, AonAccountingMenu);
}
