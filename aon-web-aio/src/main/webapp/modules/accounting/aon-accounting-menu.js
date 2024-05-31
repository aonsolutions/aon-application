import { MSG, CSS, EVENT, TAG } from 'aonsolutions/environments/environments.js'; 
import { AonSuiteMenu } from '../aon-suite-menu.js';
import * as GWT from 'aonsolutions/gwt/gwt.js';

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
		this.setTitle("Opciones de contabilidad");
	}

	accountingInitialize() {
		this.AON_MENU = 'aonMenu';
		this.AON_HEADER = 'aonHeader';
		this.ROOT_PANEL = 'rootPanel';
		this.RIGHT_PANEL = 'rightPanel';
		this.last = "Últimos apuntes";
		this.new = "Nuevo Apunte"
		this.options = [{
			title: 'Apuntes contables',
			options: [ {
				description:"Mantenimiento de Apuntes",
				action: () => GWT.load(GWT.ACCOUNT_ENTRY)
			},{
				description: "Apuntes de Amortizaciones",
				action: () => alert("description")
			},{
				description: "Asientos de explotación, cierre y apertura",
				action: () => alert("description")
			},{
				description: "Contabilización de Facturas",
				action: () => alert("description")
			},{
				description: "Contabilización de Cobros y Pagos realizados",
				action: () => alert("description")
			},{
				description: "Documentos Pendientes",
				action: () => alert("description")
			}]
		},{
			title: 'Listados Contables',
			options: [{
				description: "Extracto de cuenta",
				action: () => alert("description")
			},{
				description: "Cuenta de Explotación (P y G)",
				action: () => alert("description")
			},{
				description: "Balance de Sumas y Saldos",
				action: () => alert("description")
			},{
				description: "Listado Diario de Movimientos",
				action: () => alert("description")
			},{
				description: "Listado Mayor de Cuentas",
				action: () => alert("description")
			},{
				description: "Balances oficiales",
				action: () => alert("description")
			},{
				description: "Cuenta de Explotación (P y G) ANALÍTICA",
				action: () => alert("description")
			}]
		},{
			title: 'Tablas Auxiliares',
			options: [{
				description: "Plan General Contable",
				action: () => alert("description")
			},{
				description: "Conceptos Automáticos",
				action: () => alert("description")
			},{
				description: "Centros de Costo",
				action: () => alert("description")
			},{
				description: "Ejercicios Contables",
				action: () => alert("description")
			},{
				description: "Clientes",
				action: () => alert("description")
			},{
				description: "Proveedores",
				action: () => alert("description")
			},{
				description: "Acredores",
				action: () => alert("description")
			}]
		},{
			title: 'Inmovilizado',
			options: [{
				description: "Ficha de Amortización",
				action: () => alert("description")
			},{
				description: "Tabla de tipos de Amortización",
				action: () => alert("description")
			},{
				description: "Bienes Afectos o de Inversión",
				action: () => alert("description")
			}]
		},{
			title: 'Listados de Hacienda',
			options: [{
				description: "Panel de control de IVA",
				action: () => alert("description")
			},{
				description: "Panel de Control de IRPF",
				action: () => alert("description")
			},{
				description: "Panel de Compras y Gastos/ Ventas e Ingresos",
				action: () => alert("description")
			},{
				description: "Listado de Excel de Impuestos aplicados en Facturas",
				action: () => alert("description")
			}]
		},{
			title: 'Utilidades',
			options: [{
				description: "Utilidades y chequeos contables",
				action: () => alert("description")
			},{
				description: "Cambio de Cuenta",
				action: () => alert("description")
			},{
				description: "Regenerar número en Facturas de IVA Soportado",
				action: () => alert("description")
			},{
				description: "Chequeo de Integridad de Facturas",
				action: () => alert("description")
			},{
				description: "Utilidades facturas/vencimientos",
				action: () => alert("description")
			}]
		},{
			title: 'Movimientos Bancarios',
			options: [{
				description: "Conciliador Bancario",
				action: () => alert("description")
			},{
				description: "Agregador Bancario",
				action: () => alert("description")
			},{
				description: "Cartera de cobros y pagos",
				action: () => alert("description")
			},{
				description: "Formas de pago",
				action: () => alert("description")
			}]
		},{
			title: 'Registro Mercantil',
			options: [{
				description: "Emisión de libros contables (LEGALIA)",
				action: () => alert("description")
			},{
				description: "Depósito de cuentas (D2)",
				action: () => alert("description")
			}]
		},{
			title: 'Tesorería de Gestión (TEMPORAL)',
			options: [{
				description: "Gestión de Cobros",
				action: () => alert("description")
			},{
				description: "Remesas de Cobro",
				action: () => alert("description")
			},{
				description: "Gestión de Pagos",
				action: () => alert("description")
			},{
				description: "Remesas de Pago",
				action: () => alert("description")
			}]
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
