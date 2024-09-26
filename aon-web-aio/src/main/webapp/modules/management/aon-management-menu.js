import { MSG, CSS, EVENT, TAG } from 'aonsolutions/environments/environments.js'; 
import { AonSuiteMenu } from '../aon-suite-menu.js';
import * as GWT from 'aonsolutions/gwt/gwt.js';
import * as JSF from '../aon-jsf-app.js';



export class AonManagementMenu extends AonSuiteMenu {

    AON_MENU;
    AON_HEADER;
    ROOT_PANEL;
    RIGHT_PANEL;

    constructor () {
        super();
    }

    connectedCallback () {
        this.clear();
        this.gestionInitialize()
        this.initialize();
        this.build();
        this.setTitle("Opciones de gestión");
    }

    gestionInitialize() {
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
        this.options = [{
            title: 'Ventas',
            options: [ {
                description: "Clientes",
                title: "Clientes",
                action: () => this.rootPanel(new JSF.AonJsfCustomer())
            },{
                description: "Facturas de Venta",
                title: "Facturas de Venta",
                action: () => this.rootPanel(new JSF.AonJsfSaleInvoice())
            },{
                description: "Impresión / eMail de Facturas",
                title: "Impresión / eMail de Facturas",
                action: () => this.rootPanel(new JSF.AonJsfInvoicePrint())
            },{
                description: "Pedidos de Venta",
                title: "Pedidos de Venta",
                action: () => this.rootPanel(new JSF.AonJsfSale())
            },{
                description: "Facturación masiva de Albaranes",
                title: "Facturación masiva de Albaranes",
                action: () => this.rootPanel(new JSF.AonJsfInvoiceDelivery())
            }]
        },{
            title: 'Compras',
            options: [{
                description: "Proveedores",
                title: "Proveedores",
                action: () => this.rootPanel(new JSF.AonJsfSupplier())
            },{
                description: "Facturas de Compra",
                title: "Facturas de Compra",
                action: () => this.rootPanel(new JSF.AonJsfPurchaseInvoice())
            },{
                description: "Pedidos de Compra",
                title: "Facturas de Compra",
                action: () => this.rootPanel(new JSF.AonJsfPurchase())
            }]
        },{
            title: 'Gastos',
            options: [{
                description: "Acreedores",
                title: "Acreedores",
                action: () => this.rootPanel(new JSF.AonJsfCreditor())
            },{
                description: "Facturas de Gastos",
                title: "Facturas de Gastos",
                action: () => this.rootPanel(new JSF.AonJsfExpenseInvoice())
            },{
                description: "Gastos no Deducibles en IVA",
                title: "Gastos no Deducibles en IVA",
                action: () => this.rootPanel(new JSF.AonJsfUndeductibleInvoice())
            }]
        },{
            title: 'Tesorería',
            options: [{
                description: "Gestión de Cobros",
                title: "Gestión de Cobros",
                action: () => this.rootPanel(new JSF.AonJsfFinanceCharge())
            },{
                description: "Gestión de Pagos",
                title: "Gestión de Cobros",
                action: () => this.rootPanel(new JSF.AonJsfFinancePayment())
            },{
                description: "Cartera de cobros y pagos",
                title: "Cartera de cobros y pagos",
                action: () => GWT.iLoad(GWT.FINANCE)
            },{
                description: "Formas de Pago",
                title: "Formas de Pago",
                action: () => this.rootPanel(new JSF.AonJsfPayMethod())
            },{
                description: "Borrado de Facturas",
                title: "Borrado de Facturas",
                action: () => this.rootPanel(new JSF.AonJsfInvoiceRemove())
            },{
                description: "Estadisticas Globales",
                title: "Estadisticas Globales",
                action: () => GWT.iLoad(GWT.INVOICE_STAT)
            }]
        },{
            title: 'Impuestos',
            options: [{
                description: "Panel de control de IVA",
                title: "Panel de control de IVA",
                action: () => GWT.iLoad(GWT.VAT_REPORT)
            },{
                description: "Panel de Control de IRPF",
                title: "Panel de Control de IRPF",
                action: () => GWT.iLoad(GWT.IRPF_REPORT)
            },{
                description: "SII - Suministro Inmediato de Información",
                title: "SII - Suministro Inmediato de Información",
                action: () => GWT.iLoad(GWT.MODEL_SII)
            },{
                description: "Modelo 347 - Declaración anual operaciones con terceras personas.",
                title: "Modelo 347 - Declaración anual operaciones con terceras personas.",
                action: () => GWT.iLoad(GWT.MODEL_347)
            }]
        },{
            title: 'Maestros',
            options: [{
                description: "Productos",
                title: "Productos",
                action: () => this.rootPanel(new JSF.AonJsfProduct())
            },{
                description: "Gastos",
                title: "Gastos",
                action: () => this.rootPanel(new JSF.AonJsfExpense())
            },{
                description: "Categorías",
                title: "Categorías",
                action: () => this.rootPanel(new JSF.AonJsfProductCategory())
            },{
                description: "Pais/Provincia",
                title: "Pais/Provincia",
                action: () => this.rootPanel(new JSF.AonJsfGeotree())
            },{
                description: "Segmentación",
                title: "Segmentación",
                action: () => this.rootPanel(new JSF.AonJsfSegment())
            }]
        }];
    }
    /*
    build() {
        
    }
    */
}
if(!window.customElements.get(TAG.AON_MANAGEMENT_MENU)){
    window.customElements.define(TAG.AON_MANAGEMENT_MENU, AonManagementMenu);
}
