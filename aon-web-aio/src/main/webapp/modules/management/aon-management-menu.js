import { MSG, TAG } from '../../environments/environments.js'; 
import { AonSuiteMenu } from '../aon-suite-menu.js';
import * as GWT from '../../gwt/gwt.js';
import * as JSF from '../aon-jsf-app.js';
import { PAYMETHODS } from '../MenuOptions.js';
import { InvoiceCommunicationConfiguration } from '../../models/InvoiceCommunicationConfiguration.js';
import { getInvoiceConfiguration } from '../../services/invoiceService.js';
import { isPersonaFisica, isValid } from '../../services/documentUtils.js';

export class AonManagementMenu extends AonSuiteMenu {

    AON_MENU;
    AON_HEADER;
    ROOT_PANEL;
    RIGHT_PANEL;

    constructor () {
        super();
    }

    async connectedCallback () {
        this.clear();
        this.initialize();
   		await this.managementInitialize();
        this.build();
    }

    async initInvoiceConfiguration() {
        this.c = await getInvoiceConfiguration();
        this.icc = new InvoiceCommunicationConfiguration(this.c.communication);

    }

    checkConfiguration() {		
        if(!this.c || !this.c.company || !this.c.company.document || !this.c.communication){
            // this.getApplication().showMessageError("La configuración de facturación no está completa. Por favor, revise la configuración.");
            return false;
        } 
        if(!isValid(this.c.company.document)) {
            // this.getApplication().showMessageError("La configuración de facturación no está completa. El NIF/CIF de la empresa no es válido.");
            return false;
        }
        if(isPersonaFisica(this.c.company.document) && !(this.c.company.person || this.c.person)) {
            // this.getApplication().showMessageError("La configuración de facturación no está completa. Es obligatorio rellenar todos los datos de la persona física.");
            return false;
        }

        if(!this.icc.hasCommunication() && !this.icc.isNoSif()) {
            // this.getApplication().showMessageError("La configuración de facturación no está completa. Por favor, revise la configuración.");
            return false;
        }

        if(!this.icc.getAdministration().isUnknown() && (this.icc.hasCommunication() || this.icc.willBeCommunication() || this.icc.isNoSif())) {
            return true;
        } else {
            // this.showMessageError("La configuración de facturación no está completa. Es obligatorio selecionar una administración para la comunicación electrónica de facturas.");
            return false;
        }
    }

    async managementInitialize() {
        this.AON_MENU = 'aonMenu';
        this.AON_HEADER = 'aonHeader';
        this.ROOT_PANEL = 'rootPanel';
        this.RIGHT_PANEL = 'rightPanel';
        this.last = MSG.LAST_CUSTOMERS;
        this.new = MSG.NEW_CUSTOMER_ACTION;
        this.cardData={
            title: MSG.ACTIVITY,
            info:[MSG.ORDERS_PENDING, MSG.OPEN_FEES, MSG.INVOICES_UNACCOUNTED_ABBR]
        };
        this.selectOptions= [{
            title: MSG.SALE_INVOICE,
            action: () => this.rootPanel(new JSF.AonJsfSaleInvoice())
        },{
            title: MSG.EXPENSE_INVOICE,
            action: () => this.rootPanel(new JSF.AonJsfExpenseInvoice())
        },{
            title: MSG.PURCHASE_INVOICE_SINGLE,
            action: () => this.rootPanel(new JSF.AonJsfPurchaseInvoice())
        },{
            title: MSG.UNDEDUCTIBLE_VAT_EXPENSE,
            action: () => this.rootPanel(new JSF.AonJsfUndeductibleInvoice())
        },{
            title: MSG.CUSTOMER,
            action: () => this.rootPanel(new JSF.AonJsfCustomer())
        }];
        await this.initOptions();
    }

    async initOptions() {
		await this.initInvoiceConfiguration();
        let checkConfiguration = this.checkConfiguration();
        this.options = [{
            title: MSG.SALES,
            options: [{
                description: MSG.CUSTOMERS,
                title: MSG.CUSTOMERS,
                action: () => this.rootPanel(new JSF.AonJsfCustomer())
            }, {
                description: MSG.SALE_INVOICES,
                title: MSG.SALE_INVOICES,
                disabled: this.icc?.isNoSif() || !checkConfiguration,
                action: () => this.rootPanel(new JSF.AonJsfSaleInvoice())
            }, {
                description: MSG.INVOICE_PRINT_EMAIL,
                title: MSG.INVOICE_PRINT_EMAIL,
                disabled: this.icc?.isNoSif() || !checkConfiguration,
                action: () => this.rootPanel(new JSF.AonJsfInvoicePrint())
            }, {
                description: MSG.SALE_ORDERS,
                title: MSG.SALE_ORDERS,
                action: () => this.rootPanel(new JSF.AonJsfSale())
            }, {
                description: MSG.BULK_DELIVERY_INVOICING,
                title: MSG.BULK_DELIVERY_INVOICING,
                disabled: this.icc?.isNoSif() || !checkConfiguration,
                action: () => this.rootPanel(new JSF.AonJsfInvoiceDelivery())
            }]
        }, {
            title: MSG.PURCHASES,
            options: [{
                description: MSG.SUPPLIERS,
                title: MSG.SUPPLIERS,
                action: () => this.rootPanel(new JSF.AonJsfSupplier())
            }, {
                description: MSG.PURCHASE_INVOICES,
                title: MSG.PURCHASE_INVOICES,
                action: () => this.rootPanel(new JSF.AonJsfPurchaseInvoice())
            }, {
                description: MSG.PURCHASE_ORDERS,
                title: MSG.PURCHASE_ORDERS,
                action: () => this.rootPanel(new JSF.AonJsfPurchase())
            }]
        }, {
            title: MSG.EXPENSES,
            options: [{
                description: MSG.CREDITORS,
                title: MSG.CREDITORS,
                action: () => this.rootPanel(new JSF.AonJsfCreditor())
            }, {
                description: MSG.EXPENSE_INVOICES,
                title: MSG.EXPENSE_INVOICES,
                action: () => this.rootPanel(new JSF.AonJsfExpenseInvoice())
            }, {
                description: MSG.UNDEDUCTIBLE_VAT_EXPENSES,
                title: MSG.UNDEDUCTIBLE_VAT_EXPENSES,
                action: () => this.rootPanel(new JSF.AonJsfUndeductibleInvoice())
            }]
        }, {
            title: MSG.TREASURY,
            options: [{
                description: MSG.CHARGES_MANAGEMENT,
                title: MSG.CHARGES_MANAGEMENT,
                action: () => this.rootPanel(new JSF.AonJsfFinanceCharge())
            }, {
                description: MSG.PAYMENTS_MANAGEMENT,
                title: MSG.PAYMENTS_MANAGEMENT,
                action: () => this.rootPanel(new JSF.AonJsfFinancePayment())
            }, {
                description: MSG.CHARGES_PAYMENTS_PORTFOLIO,
                title: MSG.CHARGES_PAYMENTS_PORTFOLIO,
                action: () => GWT.iLoad(GWT.FINANCE)
            }, PAYMETHODS
            , {
                description: MSG.GLOBAL_STATISTICS,
                title: MSG.GLOBAL_STATISTICS,
                action: () => GWT.iLoad(GWT.INVOICE_STAT)
            }]
        }, {
            title: MSG.TAXES,
            options: [{
                description: MSG.IVA_PANEL_CONTROL,
                title: MSG.IVA_PANEL_CONTROL,
                action: () => GWT.iLoad(GWT.VAT_REPORT)
            }, {
                description: MSG.IRPF_PANEL_CONTROL,
                title: MSG.IRPF_PANEL_CONTROL,
                action: () => GWT.iLoad(GWT.IRPF_REPORT)
            }, {
                description: MSG.DECLARATION_SII,
                title: MSG.DECLARATION_SII,
                disabled: !this.icc?.isSii() || !checkConfiguration,
                action: () => GWT.iLoad(GWT.NEW_MODEL_SII)
            }, {
                description: MSG.MODEL_347,
                title: MSG.MODEL_347,
                action: () => GWT.iLoad(GWT.MODEL_347)
            }]
        }, {
            title: MSG.MASTERS,
            options: [{
                description: MSG.PRODUCTS,
                title: MSG.PRODUCTS,
                action: () => this.rootPanel(new JSF.AonJsfProduct())
            }, {
                description: MSG.EXPENSE,
                title: MSG.EXPENSE,
                action: () => this.rootPanel(new JSF.AonJsfExpense())
            }, {
                description: MSG.CATEGORIES,
                title: MSG.CATEGORIES,
                action: () => this.rootPanel(new JSF.AonJsfProductCategory())
            }, {
                description: MSG.COUNTRY_PROVINCE,
                title: MSG.COUNTRY_PROVINCE,
                action: () => this.rootPanel(new JSF.AonJsfGeotree())
            }, {
                description: MSG.SEGMENTATION,
                title: MSG.SEGMENTATION,
                action: () => this.rootPanel(new JSF.AonJsfSegment())
            }]
        }];
    }
}
if(!window.customElements.get(TAG.AON_MANAGEMENT_MENU)){
    window.customElements.define(TAG.AON_MANAGEMENT_MENU, AonManagementMenu);
}
