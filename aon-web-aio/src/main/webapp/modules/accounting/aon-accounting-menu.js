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
				action: () => alert("Apuntes de Amortizaciones")
			},{
				description: "Asientos de explotación, cierre y apertura",
				title: "Asientos de explotación, cierre y apertura",
				action: () => alert("Asientos de explotación, cierre y apertura")
			},{
				description: "Contabilización de Facturas",
				title: "Contabilización de Facturas",
				action: () => alert("Contabilización de Facturas")
			},{
				description: "Contabilización de Cobros y Pagos realizados",
				title: "Contabilización de Cobros y Pagos realizados",
				action: () => alert("Contabilización de Cobros y Pagos realizados")
			},{
				description: "Documentos Pendientes",
				title: "Documentos Pendientes",
				action: () => GWT.load(GWT.RAWDOC)
			}]
		},{
			title: 'Listados Contables',
			options: [{
				description: "Extracto de cuenta",
				title: "Extracto de cuenta",
				action: () => GWT.load(GWT.STATEMENT_REPORT)
			},{
				description: "Cuenta de Explotación (P y G)",
				title: "Cuenta de Explotación (P y G)",
				action: () => GWT.load(GWT.ACCOUNT_OPERATING_REPORT)
			},{
				description: "Balance de Sumas y Saldos",
				title: "Balance de Sumas y Saldos",
				action: () => GWT.load(GWT.ACCOUNT_TRIAL_BALANCE_REPORT)
			},{
				description: "Listado Diario de Movimientos",
				title: "Listado Diario de Movimientos",
				action: () => GWT.load(GWT.JOURNAL_REPORT)
			},{
				description: "Listado Mayor de Cuentas",
				title: "Listado Mayor de Cuentas",
				action: () => GWT.load(GWT.LEDGER_REPORT)
			},{
				description: "Balances oficiales",
				title: "Balances oficiales",
				action: () => GWT.load(GWT.ACCOUNT_BALANCE_REPORT)
			},{
				description: "Cuenta de Explotación (P y G) ANALÍTICA",
				title: "Cuenta de Explotación (P y G) ANALÍTICA",
				action: () => GWT.load(GWT.ACCOUNT_ANALYTICAL_REPORT)
			}]
		},{
			title: 'Tablas Auxiliares',
			options: [{
				description: "Plan General Contable",
				title: "Plan General Contable",
				action: () => GWT.load(GWT.ACCOUNT_MODULE)
			},{
				description: "Conceptos Automáticos",
				title: "Conceptos Automáticos",
				action: () => alert("description")
			},{
				description: "Centros de Costo",
				title: "Centros de Costo",
				action: () => alert("description")//GWT.load(GWT.CostCenterModule)
			},{
				description: "Ejercicios Contables",
				title: "Ejercicios Contables",
				action: () => GWT.load(GWT.ACCOUNTING_PERIOD)
			},{
				description: MSG.CUSTOMER,
				title: MSG.CUSTOMER,
				action: () => GWT.load(GWT.CUSTOMER)
			},{
				description: MSG.SUPPLIER,
				title: MSG.SUPPLIER,
				action: () => GWT.load(GWT.SUPPLIER)
			},{
				description: MSG.CREDITOR,
				title: MSG.CREDITOR,
				action: () => GWT.load(GWT.CREDITOR)
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
				action: () => GWT.load(GWT.VAT_REPORT)
			},{
				description: "Panel de Control de IRPF",
				title: "Panel de Control de IRPF",
				action: () => GWT.load(GWT.IRPF_REPORT)
			},{
				description: "Panel de Compras y Gastos/ Ventas e Ingresos",
				title: "Panel de Compras y Gastos/ Ventas e Ingresos",
				action: () => GWT.load(GWT.OPERATION_REPORT)
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
				action: () => alert("Regenerar número en Facturas de IVA Soportado")
			},{
				description: "Chequeo de Integridad de Facturas",
				title: "Chequeo de Integridad de Facturas",
				action: () => alert("Chequeo de Integridad de Facturas")
			},{
				description: "Utilidades facturas/vencimientos",
				title: "Utilidades facturas/vencimientos",
				action: () => alert("Utilidades facturas/vencimientos")
			}]
		},{
			title: 'Movimientos Bancarios',
			options: [{
				description: "Conciliador Bancario",
				title: "Conciliador Bancario",
				action: () => alert("Conciliador Bancario")
			},{
				description: "Agregador Bancario",
				title: "Agregador Bancario",
				action: () => alert("Agregador Bancario")
			},{
				description: "Cartera de cobros y pagos",
				title: "Cartera de cobros y pagos",
				action: () => GWT.load(GWT.FINANCE)
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
				action: () => alert("Emisión de libros contables (LEGALIA)")
			},{
				description: "Depósito de cuentas (D2)",
				title: "Depósito de cuentas (D2)",
				action: () => GWT.load(GWT.DEPOSIT)
			}]
		},{
			title: 'Tesorería de Gestión (TEMPORAL)',
			options: [{
				description: "Gestión de Cobros",
				title: "Gestión de Cobros",
				action: () => alert("Gestión de Cobros")
			},{
				description: "Remesas de Cobro",
				title: "Remesas de Cobro",
				action: () => alert("Remesas de Cobro")
			},{
				description: "Gestión de Pagos",
				title: "Gestión de Pagos",
				action: () => alert("Gestión de Pagos")
			},{
				description: "Remesas de Pago",
				title: "Remesas de Pago",
				action: () => alert("Remesas de Pago")
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
