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
		this.last = MSG.LAST_ENTRIES;
		this.new = MSG.NEW_ENTRY;
		this.cardData={
            title: MSG.DOCUMENTS,
            info:[MSG.DOCS_PENDING, MSG.DOCS_REJECTED, MSG.INVOICES_UNACCOUNTED_ABBR]
        };
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
			title: MSG.ACCOUNTING_ENTRIES,
			options: [ {
				description:MSG.ENTRY_MAINTENANCE,
				title:MSG.ENTRY_MAINTENANCE,
				action: () => GWT.iLoad(GWT.ACCOUNT_ENTRY),
			},{
				description: MSG.AMORTIZATION_ENTRIES,
				title: MSG.AMORTIZATION_ENTRIES,
				action: () => this.rootPanel(new JSF.AonJsfPeriodAmortization()),
			},{
				description: MSG.OPERATING_ENTRIES,
				title: MSG.OPERATING_ENTRIES,
				action: () => this.rootPanel(new JSF.AonJsfEndPeriodEntries()),
			},{
				description: MSG.PENDING_INVOICE_ACCOUNTING,
				title: MSG.PENDING_INVOICE_ACCOUNTING,
				action: () => this.rootPanel(new AonInvoiceRecord()),
			},
			// {
			// 	description: "Contabilización de Facturas",
			// 	title: "Contabilización de Facturas",
			// 	action: () => this.rootPanel(new JSF.AonJsfInvoiceRecorder),
			// },
			{
				description: MSG.RECEIPTS_PAYMENTS_ACCOUNTING,
				title: MSG.RECEIPTS_PAYMENTS_ACCOUNTING,
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
			title: MSG.ACCOUNTING_LISTINGS,
			options: [{
				description: MSG.ACCOUNT_STATEMENT,
				title: MSG.ACCOUNT_STATEMENT,
				action: () => GWT.iLoad(GWT.STATEMENT_REPORT)
			},{
				description: MSG.INCOME_STATEMENT,
				title: MSG.INCOME_STATEMENT,
				action: () => GWT.iLoad(GWT.ACCOUNT_OPERATION_STATEMENT)
			},{
				description: MSG.TRIAL_BALANCE,
				title: MSG.TRIAL_BALANCE,
				action: () => GWT.iLoad(GWT.ACCOUNT_TRIAL_BALANCE_REPORT)
			},{
				description: MSG.DAILY_JOURNAL,
				title: MSG.DAILY_JOURNAL,
				action: () => GWT.iLoad(GWT.JOURNAL_REPORT)
			},{
				description: MSG.LEDGER_REPORT,
				title: MSG.LEDGER_REPORT,
				action: () => GWT.iLoad(GWT.LEDGER_REPORT)
			},{
				description: MSG.OFFICIAL_BALANCES,
				title: MSG.OFFICIAL_BALANCES,
				action: () => GWT.iLoad(GWT.ACCOUNT_BALANCE_REPORT),
				filter: false, // remove 
			}/*,{
				description: "Cuenta de Explotación (P y G) ANALÍTICA",
				title: "Cuenta de Explotación (P y G) ANALÍTICA",
				action: () => GWT.iLoad(GWT.ACCOUNT_ANALYTICAL_REPORT)
			}*/],
			filter: () => this.isNotDomainManagementAvailable()
		},{
			title: MSG.AUX_TABLES,
			options: [{
				description: MSG.CHART_OF_ACCOUNTS,
				title: MSG.CHART_OF_ACCOUNTS,
				action: () => GWT.iLoad(GWT.ACCOUNT_MODULE)
			},{
				description: MSG.AUTOMATIC_CONCEPTS,
				title: MSG.AUTOMATIC_CONCEPTS,
				action: () => this.rootPanel(new JSF.AonJsfAutConcept())
			},{
				description: MSG.ACCOUNTING_PERIODS,
				title: MSG.ACCOUNTING_PERIODS,
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
			title: MSG.FIXED_ASSETS,
			options: [{
				description: MSG.AMORTIZATION_SHEET,
				title: MSG.AMORTIZATION_SHEET,
				action: () => this.rootPanel(new JSF.AonJsfAmortization())
			},{
				description: MSG.NEW_AMORTIZATION_SHEET,
				disabled: !this.isBeta(),
				title: MSG.NEW_AMORTIZATION_SHEET,
				action: () => GWT.iLoad(GWT.AMORTIZATION)
			},{
				description: MSG.NEW_ACCOUNTING_AMORTIZATION_SHEET,
				disabled: !this.isBeta(),
				title: MSG.NEW_ACCOUNTING_AMORTIZATION_SHEET,
				action: () => GWT.iLoad(GWT.ACCOUNTING_AMORTIZATION)
			},{
				description: MSG.AMORTIZATION_TYPE_TABLE,
				title: MSG.AMORTIZATION_TYPE_TABLE,
				action: () => GWT.iLoad(GWT.AMORTIZATION_TYPE)
			},{
				description: MSG.INVEST_ASSETS_SHEET,
				title: MSG.INVEST_ASSETS_SHEET,
				action: () => GWT.iLoad(GWT.INVEST_ASSET)
			}],
			filter: () => this.isNotDomainManagementAvailable()
		},{
			title: MSG.TAX_REPORTS,
			options: [{
				description: MSG.IVA_PANEL_CONTROL,
				title: MSG.IVA_PANEL_CONTROL,
				action: () => GWT.iLoad(GWT.VAT_REPORT)
			},{
				description: MSG.IRPF_PANEL_CONTROL,
				title: MSG.IRPF_PANEL_CONTROL,
				action: () => GWT.iLoad(GWT.IRPF_REPORT)
			},{
				description: MSG.AEAT_BOOKS,
				title: MSG.AEAT_BOOKS,
				action: () => GWT.iLoad(GWT.ACCOUNT_OPERATION_REPORT)
			},{
				description: MSG.TAX_INVOICE_EXCEL_REPORT,
				title: MSG.TAX_INVOICE_EXCEL_REPORT,
				action: () => this.rootPanel(new JSF.AonJsfInvoiceReport())
			}],
			filter: () => this.isNotDomainManagementAvailable()
		},{
			title: MSG.UTILITIES,
			options: [{
				description: MSG.COST_CENTER,
				title: MSG.COST_CENTER,
				action: () => GWT.iLoad(GWT.COST_CENTER)
			},{
				description: MSG.ACCOUNTING_UTILITIES,
				title: MSG.ACCOUNTING_UTILITIES,
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
				description: MSG.INVOICE_INTEGRITY_CHECK,
				title: MSG.INVOICE_INTEGRITY_CHECK,
				action: () => this.rootPanel(new JSF.AonJsfInvoiceIntegrity())
			},{
				description: MSG.UTILITIES_INVOICES_EXPIRATIONS,
				title: MSG.UTILITIES_INVOICES_EXPIRATIONS,
				action: () => GWT.iLoad(GWT.FINANCE_UTILITIES)
			}],
			filter: () => this.isNotDomainManagementAvailable()
		},{
			title: MSG.BANK_MOVEMENTS,
			options: [{
				description: MSG.BANK_CONCILIATOR,
				title: MSG.BANK_CONCILIATOR,
				action: () => this.rootPanel(new JSF.AonJsfBankStatement())
			},{
				description: MSG.BANK_AGGREGATOR,
				title: MSG.BANK_AGGREGATOR,
				action: () => GWT.iLoad(GWT.NORDIGEN),
				filter: () => this.hasBank()
			},{
				description: MSG.BANK_PORTFOLIO,
				title: MSG.BANK_PORTFOLIO,
				action: () => GWT.iLoad(GWT.FINANCE)
			}, PAYMETHODS],
			filter: () => this.isNotDomainManagementAvailable()
		},{
			title: MSG.COMMERCIAL_REGISTRY,
			options: [{
				description: MSG.TEMPLATES_AND_REPORTS,
				title: MSG.TEMPLATES_AND_REPORTS,
				action: () => this.rootPanel(new JSF.AonJsfReportTemplate())
			}, {
				description: MSG.ACCOUNTING_BOOKS_EMISSION,
				title: MSG.ACCOUNTING_BOOKS_EMISSION,
				action: () => this.rootPanel(new JSF.AonJsfAccountingBook())
			},{
				description: MSG.ACCOUNT_DEPOSIT_D2,
				title: MSG.ACCOUNT_DEPOSIT_D2,
				action: () => GWT.iLoad(GWT.DEPOSIT)
			}],
			filter: () => this.isNotDomainManagementAvailable()
		},{
			title: MSG.ACCOUNTING,
			options: [{
				description: MSG.CHART_OF_ACCOUNTS,
				title: MSG.CHART_OF_ACCOUNTS,
				action: () => this.getApplication().setContent(new JSF.AonJsfAccount())
			},{
				description: MSG.AUTOMATIC_CONCEPTS,
				title: MSG.AUTOMATIC_CONCEPTS,
				action: () => this.getApplication().setContent(new JSF.AonJsfAutConcept())
			},{
				description: MSG.BALANCE_DEFINITION,
				title: MSG.BALANCE_DEFINITION,
				action: () => this.getApplication().setContent(new JSF.AonJsfBalance())
			},{
				description: MSG.AMORTIZATION_TYPE_TABLE,
				title: MSG.AMORTIZATION_TYPE_TABLE,
				action: () => this.getApplication().setContent(new JSF.AonJsfAmortizationType())
			},{
				description: MSG.ACCOUNTING_UTILITIES,
				title: MSG.ACCOUNTING_UTILITIES,
				action: () => GWT.iLoad(GWT.ACCOUNTING_UTILITIES, this.getApplication().CONTENT)
			},{
				description: MSG.INVOICE_COUNTERS,
				title: MSG.INVOICE_COUNTERS,
				action: () => GWT.iLoad(GWT.DOMAIN_INVOICE_STAT, this.getApplication().CONTENT),
			}],
			filter: () => this.isDomainManagementAvailable()
		},{
			title: MSG.COMMERCIAL_REGISTRY,
			options: [{
				description: MSG.ACCOUNT_DEPOSIT_D2,
				title: MSG.ACCOUNT_DEPOSIT_D2,
				action: () => GWT.iLoad(GWT.DEPOSIT_TEXT_MODE, this.getApplication().CONTENT)
				//action: () => GWT.iLoad(GWT.DEPOSIT_TEXT_MODE)
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
