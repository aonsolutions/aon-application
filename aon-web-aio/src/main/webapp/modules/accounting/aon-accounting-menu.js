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
		this.cardData={
            title: "Documentos",
            info:["Pendientes", "Rechazados", "Fras. sin contabilizar"]
        };
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
			title: 'Apuntes contables',
			options: [ {
				description:"Mantenimiento de Apuntes",
				title:"Mantenimiento de Apuntes",
				action: () => GWT.load(GWT.ACCOUNT_ENTRY)
			},{
				description: "Apuntes de Amortizaciones",
				title: "Apuntes de Amortizaciones",
				action: () => alert("description")
			},{
				description: "Asientos de explotación, cierre y apertura",
				title: "Asientos de explotación, cierre y apertura",
				action: () => alert("description")
			},{
				description: "Contabilización de Facturas",
				title: "Contabilización de Facturas",
				action: () => alert("description")
			},{
				description: "Contabilización de Cobros y Pagos realizados",
				title: "Contabilización de Facturas",
				action: () => alert("description")
			},{
				description: "Documentos Pendientes",
				title: "Documentos Pendientes",
				action: () => alert("description")
			}]
		},{
			title: 'Listados Contables',
			options: [{
				description: "Extracto de cuenta",
				title: "Extracto de cuenta",
				action: () => alert("description")
			},{
				description: "Cuenta de Explotación (P y G)",
				title: "Cuenta de Explotación (P y G)",
				action: () => alert("description")
			},{
				description: "Balance de Sumas y Saldos",
				title: "Balance de Sumas y Saldos",
				action: () => alert("description")
			},{
				description: "Listado Diario de Movimientos",
				title: "Listado Diario de Movimientos",
				action: () => alert("description")
			},{
				description: "Listado Mayor de Cuentas",
				title: "Listado Mayor de Cuentas",
				action: () => alert("description")
			},{
				description: "Balances oficiales",
				title: "Balances oficiales",
				action: () => alert("description")
			},{
				description: "Cuenta de Explotación (P y G) ANALÍTICA",
				title: "Cuenta de Explotación (P y G) ANALÍTICA",
				action: () => alert("description")
			}]
		},{
			title: 'Tablas Auxiliares',
			options: [{
				description: "Plan General Contable",
				title: "Plan General Contable",
				action: () => alert("description")
			},{
				description: "Conceptos Automáticos",
				title: "Conceptos Automáticos",
				action: () => alert("description")
			},{
				description: "Centros de Costo",
				title: "Centros de Costo",
				action: () => alert("description")
			},{
				description: "Ejercicios Contables",
				title: "Ejercicios Contables",
				action: () => alert("description")
			},{
				description: "Clientes",
				title: "Clientes",
				action: () => alert("description")
			},{
				description: "Proveedores",
				title: "Proveedores",
				action: () => alert("description")
			},{
				description: "Acredores",
				title: "Acredores",
				action: () => alert("description")
			}]
		},{
			title: 'Inmovilizado',
			options: [{
				description: "Ficha de Amortización",
				title: "Ficha de Amortización",
				action: () => alert("description")
			},{
				description: "Tabla de tipos de Amortización",
				title: "Tabla de tipos de Amortización",
				action: () => alert("description")
			},{
				description: "Bienes Afectos o de Inversión",
				title: "Bienes Afectos o de Inversión",
				action: () => alert("description")
			}]
		},{
			title: 'Listados de Hacienda',
			options: [{
				description: "Panel de control de IVA",
				title: "Panel de control de IVA",
				action: () => alert("description")
			},{
				description: "Panel de Control de IRPF",
				title: "Panel de Control de IRPF",
				action: () => alert("description")
			},{
				description: "Panel de Compras y Gastos/ Ventas e Ingresos",
				title: "Panel de Compras y Gastos/ Ventas e Ingresos",
				action: () => alert("description")
			},{
				description: "Listado de Excel de Impuestos aplicados en Facturas",
				title: "Listado de Excel de Impuestos aplicados en Facturas",
				action: () => alert("description")
			}]
		},{
			title: 'Utilidades',
			options: [{
				description: "Utilidades y chequeos contables",
				title: "Utilidades y chequeos contables",
				action: () => alert("description")
			},{
				description: "Cambio de Cuenta",
				title: "Cambio de Cuenta",
				action: () => alert("description")
			},{
				description: "Regenerar número en Facturas de IVA Soportado",
				title: "Regenerar número en Facturas de IVA Soportado",
				action: () => alert("description")
			},{
				description: "Chequeo de Integridad de Facturas",
				title: "Chequeo de Integridad de Facturas",
				action: () => alert("description")
			},{
				description: "Utilidades facturas/vencimientos",
				title: "Utilidades facturas/vencimientos",
				action: () => alert("description")
			}]
		},{
			title: 'Movimientos Bancarios',
			options: [{
				description: "Conciliador Bancario",
				title: "Conciliador Bancario",
				action: () => alert("description")
			},{
				description: "Agregador Bancario",
				title: "Agregador Bancario",
				action: () => alert("description")
			},{
				description: "Cartera de cobros y pagos",
				title: "Cartera de cobros y pagos",
				action: () => alert("description")
			},{
				description: "Formas de pago",
				title: "Formas de pago",
				action: () => alert("description")
			}]
		},{
			title: 'Registro Mercantil',
			options: [{
				description: "Emisión de libros contables (LEGALIA)",
				title: "Emisión de libros contables (LEGALIA)",
				action: () => alert("description")
			},{
				description: "Depósito de cuentas (D2)",
				title: "Depósito de cuentas (D2)",
				action: () => alert("description")
			}]
		},{
			title: 'Tesorería de Gestión (TEMPORAL)',
			options: [{
				description: "Gestión de Cobros",
				title: "Gestión de Cobros",
				action: () => alert("description")
			},{
				description: "Remesas de Cobro",
				title: "Remesas de Cobro",
				action: () => alert("description")
			},{
				description: "Gestión de Pagos",
				title: "Gestión de Pagos",
				action: () => alert("description")
			},{
				description: "Remesas de Pago",
				title: "Remesas de Pago",
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
