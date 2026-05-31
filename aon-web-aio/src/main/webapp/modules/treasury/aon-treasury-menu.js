import { MSG, TAG } from '../../environments/environments.js';
import { AonSuiteMenu } from '../aon-suite-menu.js';
import * as GWT from '../../gwt/gwt.js';
import * as JSF from '../aon-jsf-app.js';

export class AonTreasuryMenu extends AonSuiteMenu {

    AON_MENU;
    AON_HEADER;
    ROOT_PANEL;
    RIGHT_PANEL;

    constructor () {
        super();
		this.tesoreriaInitialize()
    }

    connectedCallback () {
        this.clear();
        this.initialize();
        this.build();
    }

    tesoreriaInitialize() {
        this.AON_MENU = 'aonMenu';
        this.AON_HEADER = 'aonHeader';
        this.ROOT_PANEL = 'rootPanel';
        this.RIGHT_PANEL = 'rightPanel';
        this.last = MSG.LAST_EXPIRATIONS;
        this.new = MSG.NEW_EXPIRATION;
        this.cardData={
            title: MSG.ACTIVITY,
            info:[MSG.EXPIRED_PAYMENTS, MSG.EXPIRED_CHARGES]
        };
        this.selectOptions= [{
            title: MSG.PAYMENT_BATCH_RECEIPT,
            action: () => this.rootPanel(new JSF.AonJsfFBatchCharge())
        },{
            title: MSG.PAYMENT_BATCH_SINGLE,
            action: () => this.rootPanel(new JSF.AonJsfFBatchPayment())
        },{
            title: MSG.EXPIRATIONS,
            action: () => alert("description")
        },{
            title: MSG.PAYMETHOD,
            action: () => this.rootPanel(new JSF.AonJsfPayMethod())
        }];
        this.initOptions();
    }

    initOptions() {
        this.options = [{
            title: MSG.CHARGES,
            options: [{
                description: MSG.CHARGES_MANAGEMENT,
                title: MSG.CHARGES_MANAGEMENT,
                action: () => this.rootPanel(new JSF.AonJsfFinanceCharge())
            }, {
                description: MSG.PAYMENT_BATCHES_RECEIPT,
                title: MSG.PAYMENT_BATCHES_RECEIPT,
                action: () => this.rootPanel(new JSF.AonJsfFBatchCharge())
            }, {
                description: MSG.SEPA_DIRECT_DEBIT_ORDER,
                title: MSG.SEPA_DIRECT_DEBIT_ORDER,
                action: () => this.rootPanel(new JSF.AonJsfSddMandate())
            }]
        }, {
            title: MSG.PAYMENTS,
            options: [{
                description: MSG.PAYMENTS_MANAGEMENT,
                title: MSG.PAYMENTS_MANAGEMENT,
                action: () => this.rootPanel(new JSF.AonJsfFinancePayment())
            }, {
                description: MSG.PAYMENT_BATCH_SINGLE,
                title: MSG.PAYMENT_BATCH_SINGLE,
                action: () => this.rootPanel(new JSF.AonJsfFBatchPayment())
            }, {
                description: MSG.PAYMENT_BATCH_NEW,
                title: MSG.PAYMENT_BATCH_NEW,
                action: () => GWT.iLoad(GWT.FBATCH_PAYMENT_TREASURY)
            }, {
                description: MSG.PROMISSORY_NOTE_PRINTING,
                title: MSG.PROMISSORY_NOTE_PRINTING,
                action: () => this.rootPanel(new JSF.AonJsfFPaymentPrint())
            }]
        }, {
            title: MSG.FORECAST,
            options: [{
                description: MSG.FORECAST_MANAGEMENT,
                title: MSG.FORECAST_MANAGEMENT,
                action: () => this.rootPanel(new JSF.AonJsfCashFlowForecast())
            }, {
                description: MSG.FORECAST_LIST,
                title: MSG.FORECAST_LIST,
                action: () => this.rootPanel(new JSF.AonJsfCashFlowForecastReport())
            } /*,{
                    description: "Proyección de Cuotas",
                    title: "Proyección de Cuotas",
                    action: () => GWT.iLoad(GWT.FEE_PROJECTION)
                }*/
            ]
        }, {
            title: MSG.BANK_MOVEMENTS,
            options: [{
                description: MSG.BANK_CONCILIATOR,
                title: MSG.BANK_CONCILIATOR,
                action: () => this.rootPanel(new JSF.AonJsfBankStatement())
            }, {
                description: MSG.BANK_AGGREGATOR,
                title: MSG.BANK_AGGREGATOR,
                action: () => GWT.iLoad(GWT.NORDIGEN),
				filter: () => this.getDur().isBank()
            }, {
                description: MSG.ADVANCES_MANAGEMENT,
                title: MSG.ADVANCES_MANAGEMENT,
                action: () => this.rootPanel(new JSF.AonJsfPrepayment())
            }]
        }, {
            title: MSG.FEES,
            options: [{
                description: MSG.PRE_INVOICING_LIST,
                title: MSG.PRE_INVOICING_LIST,
                action: () => this.rootPanel(new JSF.AonJsfFeePreInvoicing())
            }, {
                description: MSG.FEE_INVOICING,
                title: MSG.FEE_INVOICING,
                action: () => this.rootPanel(new JSF.AonJsfFeeInvoicing())
            }, {
                description: MSG.ASSIGN_FEES_CUSTOMERS,
                title: MSG.ASSIGN_FEES_CUSTOMERS,
                action: () => this.rootPanel(new JSF.AonJsfFeeAssigment)
            }, {
                description: MSG.FEES_LIST,
                title: MSG.FEES_LIST,
                action: () => this.rootPanel(new JSF.AonJsfFeePrint())
            }, {
                description: MSG.FEE_INVOICING_PANEL,
                title: MSG.FEE_INVOICING_PANEL,
                action: () => GWT.iLoad(GWT.CUSTOMER_FEE)
            }, {
                description: MSG.WORKLOAD_PANEL,
                title: MSG.WORKLOAD_PANEL,
                action: () => GWT.iLoad(GWT.SELLER_WORKLOAD_MODULE)
            }
            ]
        }, {
            title: MSG.UTILITIES,
            options: [{
                description: MSG.INVOICE_SURCHARGES,
                title: MSG.INVOICE_SURCHARGES,
                action: () => this.rootPanel(new JSF.AonJsfIncreaseItem())
            }, {
                description: MSG.BILLING_GROUPS,
                title: MSG.BILLING_GROUPS,
                action: () => this.rootPanel(new JSF.AonJsfInvoicingGroup())
            }, {
                description: MSG.INVOICE_SIGNATURE,
                title: MSG.INVOICE_SIGNATURE,
                action: () => this.rootPanel(new JSF.AonJsfInvoiceSigner())
            }, {
                description: MSG.TREASURY_UTILITIES,
                title: MSG.TREASURY_UTILITIES,
                action: () => GWT.iLoad(GWT.FINANCE_UTILITIES)
            }, {
                description: MSG.FINANCIAL_DATA_CHECK,
                title: MSG.FINANCIAL_DATA_CHECK,
                action: () => this.rootPanel(new JSF.AonJsfFinancePrint())
            }, {
                description: MSG.INVOICE_EXPIRATION_CHECK,
                title: MSG.INVOICE_EXPIRATION_CHECK,
                action: () => this.rootPanel(new JSF.AonJsfFinanceChequing())
            }]
        }];
    }
}
if(!window.customElements.get(TAG.AON_TREASURY_MENU)){
    window.customElements.define(TAG.AON_TREASURY_MENU, AonTreasuryMenu);
}