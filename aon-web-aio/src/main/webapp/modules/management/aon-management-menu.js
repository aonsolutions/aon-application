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
            action: () => alert("description")
        },{
            title: "Cliente", 
            action: () => alert("description")
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
                action: () => alert("description")
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
                action: () => alert("description")
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
                action: () => alert("description")
            }]
        },{
            title: 'Tesorería',
            options: [{
                description: "Gestión de Cobros",
                title: "Gestión de Cobros",
                action: () => alert("description")
            },{
                description: "Gestión de Pagos",
                title: "Gestión de Cobros",
                action: () => alert("description")
            },{
                description: "Cartera de cobros y pagos",
                title: "Cartera de cobros y pagos",
                action: () => alert("description")
            },{
                description: "Formas de Pago",
                title: "Formas de Pago",
                action: () => alert("description")
            },{
                description: "Borrado de Facturas",
                title: "Borrado de Facturas",
                action: () => alert("description")
            },{
                description: "Estadisticas Globales",
                title: "Estadisticas Globales",
                action: () => alert("description")
            }]
        },{
            title: 'Impuestos',
            options: [{
                description: "Panel de control de IVA",
                title: "Panel de control de IVA",
                action: () => alert("description")
            },{
                description: "Panel de Control de IRPF",
                title: "Panel de Control de IRPF",
                action: () => alert("description")
            },{
                description: "SII - Suministro Inmediato de Información",
                title: "SII - Suministro Inmediato de Información",
                action: () => alert("description")
            },{
                description: "Modelo 347 - Declaración anual operaciones con terceras personas.",
                title: "Modelo 347 - Declaración anual operaciones con terceras personas.",
                action: () => alert("description")
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
                action: () => alert("description")
            },{
                description: "Pais/Provincia",
                title: "Pais/Provincia",
                action: () => alert("description")
            },{
                description: "Segmentación",
                title: "Segmentación",
                action: () => alert("description")
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
