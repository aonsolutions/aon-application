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
        this.last = "Últimos clientes";
        this.new = "Nuevo Cliente";
        this.cardData={
            title: "Actividad",
            info:["Pedidos ptes.", "Cuotas abiertas", "Fras. sin contabilizar"]
        };
        this.selectOptions= [{
            title: "Factura de venta",
            action: () => this.rootPanel(new JSF.AonJsfSaleInvoice())
        },{
            title: "Factura de gastos",
            action: () => this.rootPanel(new JSF.AonJsfExpenseInvoice())
        },{
            title: "Factura de compras", 
            action: () => this.rootPanel(new JSF.AonJsfPurchaseInvoice())
        },{
            title: "Gasto no deducible en IVA", 
            action: () => this.rootPanel(new JSF.AonJsfUndeductibleInvoice())
        },{
            title: "Cliente", 
            action: () => this.rootPanel(new JSF.AonJsfCustomer())
        }];
        await this.initOptions();
    }

    async initOptions() {
		await this.initInvoiceConfiguration();
        let checkConfiguration = this.checkConfiguration();
        this.options = [{
            title: 'Ventas',
            options: [{
                description: MSG.CUSTOMERS,
                title: MSG.CUSTOMERS,
                action: () => this.rootPanel(new JSF.AonJsfCustomer())
            }, {
                description: "Facturas de Venta",
                title: "Facturas de Venta",
                disabled: this.icc?.isNoSif() || !checkConfiguration,
                action: () => this.rootPanel(new JSF.AonJsfSaleInvoice())
            }, {
                description: "Impresión / eMail de Facturas",
                title: "Impresión / eMail de Facturas",
                disabled: this.icc?.isNoSif() || !checkConfiguration,
                action: () => this.rootPanel(new JSF.AonJsfInvoicePrint())
            }, {
                description: "Pedidos de Venta",
                title: "Pedidos de Venta",
                action: () => this.rootPanel(new JSF.AonJsfSale())
            }, {
                description: "Facturación masiva de Albaranes",
                title: "Facturación masiva de Albaranes",
                disabled: this.icc?.isNoSif() || !checkConfiguration,
                action: () => this.rootPanel(new JSF.AonJsfInvoiceDelivery())
            }]
        }, {
            title: 'Compras',
            options: [{
                description: MSG.SUPPLIERS,
                title: MSG.SUPPLIERS,
                action: () => this.rootPanel(new JSF.AonJsfSupplier())
            }, {
                description: "Facturas de Compra",
                title: "Facturas de Compra",
                action: () => this.rootPanel(new JSF.AonJsfPurchaseInvoice())
            }, {
                description: "Pedidos de Compra",
                title: "Facturas de Compra",
                action: () => this.rootPanel(new JSF.AonJsfPurchase())
            }]
        }, {
            title: 'Gastos',
            options: [{
                description: MSG.CREDITORS,
                title: MSG.CREDITORS,
                action: () => this.rootPanel(new JSF.AonJsfCreditor())
            }, {
                description: "Facturas de Gastos",
                title: "Facturas de Gastos",
                action: () => this.rootPanel(new JSF.AonJsfExpenseInvoice())
            }, {
                description: "Gastos no Deducibles en IVA",
                title: "Gastos no Deducibles en IVA",
                action: () => this.rootPanel(new JSF.AonJsfUndeductibleInvoice())
            }]
        }, {
            title: 'Tesorería',
            options: [{
                description: "Gestión de Cobros",
                title: "Gestión de Cobros",
                action: () => this.rootPanel(new JSF.AonJsfFinanceCharge())
            }, {
                description: "Gestión de Pagos",
                title: "Gestión de Cobros",
                action: () => this.rootPanel(new JSF.AonJsfFinancePayment())
            }, {
                description: "Cartera de cobros y pagos",
                title: "Cartera de cobros y pagos",
                action: () => GWT.iLoad(GWT.FINANCE)
            }, PAYMETHODS 
            , {
                description: "Estadisticas Globales",
                title: "Estadisticas Globales",
                action: () => GWT.iLoad(GWT.INVOICE_STAT)
            }]
        }, {
            title: 'Impuestos',
            options: [{
                description: "Panel de control de IVA",
                title: "Panel de control de IVA",
                action: () => GWT.iLoad(GWT.VAT_REPORT)
            }, {
                description: "Panel de Control de IRPF",
                title: "Panel de Control de IRPF",
                action: () => GWT.iLoad(GWT.IRPF_REPORT)
            }, {
                description: "SII - Suministro Inmediato de Información",
                title: "SII - Suministro Inmediato de Información",
                disabled: !this.icc?.isSii() || !checkConfiguration,
                action: () => GWT.iLoad(GWT.NEW_MODEL_SII)
            }, {
                description: "Modelo 347 - Declaración anual operaciones con terceras personas.",
                title: "Modelo 347 - Declaración anual operaciones con terceras personas.",
                action: () => GWT.iLoad(GWT.MODEL_347)
            }]
        }, {
            title: 'Maestros',
            options: [{
                description: MSG.PRODUCTS,
                title: MSG.PRODUCTS,
                action: () => this.rootPanel(new JSF.AonJsfProduct())
            }, {
                description: "Gastos",
                title: "Gastos",
                action: () => this.rootPanel(new JSF.AonJsfExpense())
            }, {
                description: MSG.CATEGORIES,
                title: MSG.CATEGORIES,
                action: () => this.rootPanel(new JSF.AonJsfProductCategory())
            }, {
                description: "Pais/Provincia",
                title: "Pais/Provincia",
                action: () => this.rootPanel(new JSF.AonJsfGeotree())
            }, {
                description: "Segmentación",
                title: "Segmentación",
                action: () => this.rootPanel(new JSF.AonJsfSegment())
            }]
        }];
    }
}
if(!window.customElements.get(TAG.AON_MANAGEMENT_MENU)){
    window.customElements.define(TAG.AON_MANAGEMENT_MENU, AonManagementMenu);
}
