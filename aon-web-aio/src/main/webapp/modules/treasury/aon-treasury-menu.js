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
            title: "Remesa de cobro",
            action: () => this.rootPanel(new JSF.AonJsfFBatchCharge())
        },{
            title: "Remesa de pago",
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
                description: "Gestión de Cobros",
                title: "Gestión de Cobros",
                action: () => this.rootPanel(new JSF.AonJsfFinanceCharge())
            }, {
                description: "Remesas de Cobro",
                title: "Remesas de Cobro",
                action: () => this.rootPanel(new JSF.AonJsfFBatchCharge())
            }, {
                description: "Orden de domiciliación de adeudo directo SEPA",
                title: "Orden de domiciliación de adeudo directo SEPA",
                action: () => this.rootPanel(new JSF.AonJsfSddMandate())
            }]
        }, {
            title: MSG.PAYMENTS,
            options: [{
                description: "Gestión de Pagos",
                title: "Gestión de Pagos",
                action: () => this.rootPanel(new JSF.AonJsfFinancePayment())
            }, {
                description: "Remesas de Pago",
                title: "Remesas de Pago",
                action: () => this.rootPanel(new JSF.AonJsfFBatchPayment())
            }, {
                description: "Remesas de Pago (Nuevo)",
                title: "Remesas de Pago",
                action: () => GWT.iLoad(GWT.FBATCH_PAYMENT_TREASURY)
            }, {
                description: "Impresión de Pagarés",
                title: "Impresión de Pagarés",
                action: () => this.rootPanel(new JSF.AonJsfFPaymentPrint())
            }]
        }, {
            title: MSG.FORECAST,
            options: [{
                description: "Gestión de Previsiones",
                title: "Gestión de Previsiones",
                action: () => this.rootPanel(new JSF.AonJsfCashFlowForecast())
            }, {
                description: "Listado de Previsión",
                title: "Listado de Previsión",
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
                description: "Conciliador Bancario",
                title: "Conciliador Bancario",
                action: () => this.rootPanel(new JSF.AonJsfBankStatement())
            }, {
                description: "Agregador Bancario",
                title: "Agregador Bancario",
                action: () => GWT.iLoad(GWT.NORDIGEN),
				filter: () => this.getDur().isBank()
            }, {
                description: "Gestión de Suplidos",
                title: "Gestión de Suplidos",
                action: () => this.rootPanel(new JSF.AonJsfPrepayment())
            }]
        }, {
            title: MSG.FEES,
            options: [{
                description: "Listado de Pre-facturación",
                title: "Listado de Pre-facturación",
                action: () => this.rootPanel(new JSF.AonJsfFeePreInvoicing())
            }, {
                description: "Facturación de Cuotas",
                title: "Facturación de Cuotas",
                action: () => this.rootPanel(new JSF.AonJsfFeeInvoicing())
            }, {
                description: "Asignar Cuotas a Clientes",
                title: "Asignar Cuotas a Clientes",
                action: () => this.rootPanel(new JSF.AonJsfFeeAssigment)
            }, {
                description: "Listado de Cuotas",
                title: "Listado de Cuotas",
                action: () => this.rootPanel(new JSF.AonJsfFeePrint())
            }, {
                description: "Panel Facturación de Cuotas",
                title: "Panel Facturación de Cuotas",
                action: () => GWT.iLoad(GWT.CUSTOMER_FEE)
            }, {
                description: "Panel Carga Trabajo",
                title: "Panel Carga Trabajo",
                action: () => GWT.iLoad(GWT.SELLER_WORKLOAD_MODULE)
            }
            ]
        }, {
            title: MSG.UTILITIES,
            options: [{
                description: "Recargos en Facturas",
                title: "Recargos en Facturas",
                action: () => this.rootPanel(new JSF.AonJsfIncreaseItem())
            }, {
                description: "Grupos de Facturación",
                title: "Grupos de Facturación",
                action: () => this.rootPanel(new JSF.AonJsfInvoicingGroup())
            }, {
                description: "Firma de Facturas",
                title: "Firma de Facturas",
                action: () => this.rootPanel(new JSF.AonJsfInvoiceSigner())
            }, {
                description: "Utilidades Tesoreria",
                title: "Utilidades Tesoreria",
                action: () => GWT.iLoad(GWT.FINANCE_UTILITIES)
            }, {
                description: "Chequeo de Datos Financieros",
                title: "Chequeo de Datos Financieros",
                action: () => this.rootPanel(new JSF.AonJsfFinancePrint())
            }, {
                description: "Chequeo de Facturas / Vencimientos",
                title: "Chequeo de Facturas / Vencimientos",
                action: () => this.rootPanel(new JSF.AonJsfFinanceChequing())
            }]
        }];
    }
}
if(!window.customElements.get(TAG.AON_TREASURY_MENU)){
    window.customElements.define(TAG.AON_TREASURY_MENU, AonTreasuryMenu);
}