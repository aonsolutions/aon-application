import { MSG, TAG , EVENT} from '../../environments/environments.js'; 
import { AonSuiteMenu } from '../aon-suite-menu.js';
import { AonInvoiceRecord } from '../../modules/invoice/aon-invoice-record.js';
import * as GWT from '../../gwt/gwt.js';
import * as JSF from '../aon-jsf-app.js';
import { AonAccountingBeta } from './aon-accounting-beta.js';
import { AonIconButton } from '../../components/aon-icon-button.js';
import { PAYMETHODS } from '../MenuOptions.js';

export class AonAccountingMenu extends AonSuiteMenu {

	AON_MENU;
	AON_HEADER;
	ROOT_PANEL;
	RIGHT_PANEL;

	constructor () {
		super();
		this.accountingInitialize()
	}

	connectedCallback () {
		this.buildDur().then(() => {
           this.clear();
           this.initialize();
           this.build();
		})
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
		
		this.initOptions();
	}
	
	initOptions() {
		this.options = [{
			title: 'Apuntes contables',
			options: [ {
				description:"Mantenimiento de Apuntes",
				title:"Mantenimiento de Apuntes",
				action: () => GWT.iLoad(GWT.ACCOUNT_ENTRY),
			},{
				description: "Apuntes de Amortizaciones",
				title: "Apuntes de Amortizaciones",
				action: () => this.rootPanel(new JSF.AonJsfPeriodAmortization()),
			},{
				description: "Asientos de explotación, cierre y apertura",
				title: "Asientos de explotación, cierre y apertura",
				action: () => this.rootPanel(new JSF.AonJsfEndPeriodEntries()),
			},{
				description: "Contabilización de Facturas y Documentos Pendientes",
				title: "Contabilización de Facturas",
				action: () => this.rootPanel(new AonInvoiceRecord()),
			},
			// {
			// 	description: "Contabilización de Facturas",
			// 	title: "Contabilización de Facturas",
			// 	action: () => this.rootPanel(new JSF.AonJsfInvoiceRecorder),
			// },
			{
				description: "Contabilización de Cobros y Pagos realizados",
				title: "Contabilización de Cobros y Pagos realizados",
				action: () => this.rootPanel(new JSF.AonJsfFinanceTrackingEntry()),
			}
			// ,{
			// 	description: "Documentos Pendientes",
			// 	title: "Documentos Pendientes",
			// 	action: () => GWT.iLoad(GWT.RAWDOC),
			// }
			],
			filter: () => this.isNotDomainManagementAvailable()
		},{
			title: 'Listados Contables',
			options: [{
				description: "Extracto de cuenta",
				title: "Extracto de cuenta",
				action: () => GWT.iLoad(GWT.STATEMENT_REPORT)
			},{
				description: "Cuenta de Explotación (P y G)",
				title: "Cuenta de Explotación (P y G)",
				action: () => GWT.iLoad(GWT.ACCOUNT_OPERATION_STATEMENT)
			},{
				description: "Balance de Sumas y Saldos",
				title: "Balance de Sumas y Saldos",
				action: () => GWT.iLoad(GWT.ACCOUNT_TRIAL_BALANCE_REPORT)
			},{
				description: "Listado Diario de Movimientos",
				title: "Listado Diario de Movimientos",
				action: () => GWT.iLoad(GWT.JOURNAL_REPORT)
			},{
				description: "Listado Mayor de Cuentas",
				title: "Listado Mayor de Cuentas",
				action: () => GWT.iLoad(GWT.LEDGER_REPORT)
			},{
				description: "Balances oficiales",
				title: "Balances oficiales",
				action: () => GWT.iLoad(GWT.ACCOUNT_BALANCE_REPORT)
			}/*,{
				description: "Cuenta de Explotación (P y G) ANALÍTICA",
				title: "Cuenta de Explotación (P y G) ANALÍTICA",
				action: () => GWT.iLoad(GWT.ACCOUNT_ANALYTICAL_REPORT)
			}*/],
			filter: () => this.isNotDomainManagementAvailable()
		},{
			title: 'Tablas Auxiliares',
			options: [{
				description: "Plan General Contable",
				title: "Plan General Contable",
				action: () => GWT.iLoad(GWT.ACCOUNT_MODULE)
			},{
				description: "Conceptos Automáticos",
				title: "Conceptos Automáticos",
				action: () => this.rootPanel(new JSF.AonJsfAutConcept())
			},{
				description: "Ejercicios Contables",
				title: "Ejercicios Contables",
				action: () => GWT.iLoad(GWT.ACCOUNTING_PERIOD)
			},{
				description: MSG.CUSTOMER,
				title: MSG.CUSTOMER,
				action: () => GWT.iLoad(GWT.CUSTOMER)
			},{
				description: MSG.SUPPLIER,
				title: MSG.SUPPLIER,
				action: () => GWT.iLoad(GWT.SUPPLIER)
			},{
				description: MSG.CREDITOR,
				title: MSG.CREDITOR,
				action: () => GWT.iLoad(GWT.CREDITOR)
			}],
			filter: () => this.isNotDomainManagementAvailable()
		},{
			title: 'Inmovilizado',
			options: [{
				description: "Ficha de Amortización",
				title: "Ficha de Amortización",
				action: () => this.rootPanel(new JSF.AonJsfAmortization())
			},{
				description: "Tabla de tipos de Amortización",
				title: "Tabla de tipos de Amortización",
				action: () => GWT.iLoad(GWT.AMORTIZATION_TYPE)
			},{
				description: "Bienes Afectos o de Inversión",
				title: "Bienes Afectos o de Inversión",
				action: () => GWT.iLoad(GWT.INVEST_ASSET)
			}],
			filter: () => this.isNotDomainManagementAvailable()
		},{
			title: 'Listados de Hacienda',
			options: [{
				description: "Panel de control de IVA",
				title: "Panel de control de IVA",
				action: () => GWT.iLoad(GWT.VAT_REPORT)
			},{
				description: "Panel de Control de IRPF",
				title: "Panel de Control de IRPF",
				action: () => GWT.iLoad(GWT.IRPF_REPORT)
			},{
				description: "Libros Registro AEAT",
				title: "Libros Registro AEAT",
				action: () => GWT.iLoad(GWT.ACCOUNT_OPERATION_REPORT)
			},{
				description: "Listado de Excel de Impuestos aplicados en Facturas",
				title: "Listado de Excel de Impuestos aplicados en Facturas",
				action: () => this.rootPanel(new JSF.AonJsfInvoiceReport())
			}],
			filter: () => this.isNotDomainManagementAvailable()
		},{
			title: 'Utilidades',
			options: [{
				description: "Centros de Costo",
				title: "Centros de Costo",
				action: () => GWT.iLoad(GWT.COST_CENTER)
			},{
				description: "Utilidades y chequeos contables",
				title: "Utilidades y chequeos contables",
				action: () => GWT.iLoad(GWT.ACCOUNTING_UTILITIES)
			},/*{
				description: "Cambio de Cuenta",
				title: "Cambio de Cuenta",
				action: () => alert("description")
			},{
				description: "Regenerar número en Facturas de IVA Soportado",
				title: "Regenerar número en Facturas de IVA Soportado",
				action: () => alert("Regenerar número en Facturas de IVA Soportado")
			},*/
			{
				description: "Chequeo de Integridad de Facturas",
				title: "Chequeo de Integridad de Facturas",
				action: () => this.rootPanel(new JSF.AonJsfInvoiceIntegrity())
			},{
				description: "Utilidades facturas/vencimientos",
				title: "Utilidades facturas/vencimientos",
				action: () => GWT.iLoad(GWT.FINANCE_UTILITIES)
			}],
			filter: () => this.isNotDomainManagementAvailable()
		},{
			title: 'Movimientos Bancarios',
			options: [{
				description: "Conciliador Bancario",
				title: "Conciliador Bancario",
				action: () => this.rootPanel(new JSF.AonJsfBankStatement())
			},{
				description: "Agregador Bancario",
				title: "Agregador Bancario",
				action: () => GWT.iLoad(GWT.NORDIGEN),
				filter: () => this.hasBank()
			},{
				description: "Cartera de cobros y pagos",
				title: "Cartera de cobros y pagos",
				action: () => GWT.iLoad(GWT.FINANCE)
			}, PAYMETHODS],
			filter: () => this.isNotDomainManagementAvailable()
		},{
			title: 'Registro Mercantil',
			options: [{
				description: "Emisión de libros contables (LEGALIA)",
				title: "Emisión de libros contables (LEGALIA)",
				action: () => this.rootPanel(new JSF.AonJsfAccountingBook())
			},{
				description: "Depósito de cuentas (D2)",
				title: "Depósito de cuentas (D2)",
				action: () => GWT.iLoad(GWT.DEPOSIT)
			}],
			filter: () => this.isNotDomainManagementAvailable()
		},{
			title: 'Contabilidad',
			options: [{
				description: "Plan General Contable",
				title: "Plan General Contable",
				action: () => this.rootPanel(new JSF.AonJsfAccount())
			},{
				description: "Conceptos Automáticos",
				title: "Conceptos Automáticos",
				action: () => this.rootPanel(new JSF.AonJsfAutConcept())
			},{
				description: "Definición de Balances",
				title: "Definición de Balances",
				action: () => this.rootPanel(new JSF.AonJsfBalance())
			},{
				description: "Tabla de Tipos de Amortización",
				title: "Tabla de Tipos de Amortización",
				action: () => this.rootPanel(new JSF.AonJsfAmortizationType())
			},{
				description: "Utilidades y chequeos contables",
				title: "Utilidades y chequeos contables",
				action: () =>  GWT.iLoad(GWT.ACCOUNTING_UTILITIES)
			},{
				description: "Utilidades y chequeos contables",
				title: "Utilidades y chequeos contables",
				action: () => alert("Utilidades y chequeos contables"),
				filter : () => false
			}],
			filter: () => this.isDomainManagementAvailable()
		},{
			title: 'Registro Mercantil',
			options: [{
				description: "Depósito de cuentas (D2)",
				title: "Depósito de cuentas (D2)",
				action: () => GWT.iLoad(GWT.DEPOSIT_TEXT_MODE)
			}
		],
			filter: () => this.isDomainManagementAvailable()
		}/*,{
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
		}*/];
	}

	/*
	build() {
        
	}
	*/
	
}
if(!window.customElements.get(TAG.AON_ACCOUNTING_MENU)){
	window.customElements.define(TAG.AON_ACCOUNTING_MENU, AonAccountingMenu);
}
