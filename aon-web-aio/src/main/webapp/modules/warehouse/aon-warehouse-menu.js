import { MSG, CSS, EVENT, TAG } from 'aonsolutions/environments/environments.js'; 
import { AonSuiteMenu } from '../aon-suite-menu.js';
import * as GWT from 'aonsolutions/gwt/gwt.js';
import * as JSF from '../aon-jsf-app.js';

export class AonWarehouseMenu extends AonSuiteMenu {

    AON_MENU;
    AON_HEADER;
    ROOT_PANEL;
    RIGHT_PANEL;

    constructor () {
        super();
    }

    connectedCallback () {
        this.clear();
        this.almacenInitialize()
        this.initialize();
        this.build();
        this.setTitle("Opciones de almacén");
    }

    almacenInitialize() {
        this.AON_MENU = 'aonMenu';
        this.AON_HEADER = 'aonHeader';
        this.ROOT_PANEL = 'rootPanel';
        this.RIGHT_PANEL = 'rightPanel';
        this.last = "Últimas elaboraciones";
        this.new = "Nuevo Albarán de venta";
        this.cardData={
            title: "Actividad",
            info:["Expediones abiertas", "Elaboraciones abiertas"]
        };
        this.selectOptions= [{
            title: "Elaboración",
            action: () => alert("description")
        },{
            title: "Albarán de compra",
            action: () => alert("description")
        },{
            title: "Albarán de venta", 
            action: () => alert("description")
        }];
        this.options = [{
            title: 'Movimientos',
            options: [ {
                description: "Entradas (Albaranes de Compra)",
                title: "Entradas (Albaranes de Compra)",
				action: () => this.rootPanel(new JSF.AonJsfIncome)
            },{
                description: "Salidas (Albaranes de Compra)",
                title: "Salidas (Albaranes de Compra)",
				action: () => this.rootPanel(new JSF.AonJsfDelivery)
            },{
                description: "Traspasos entre almacenes",
                title: "Traspasos entre almacenes",
				action: () => this.rootPanel(new JSF.AonJsfWarehouseTransfer)
            },{
                description: "Servir pedidos",
                title: "Servir pedidos",
				action: () => this.rootPanel(new JSF.AonJsfOrderServer)
            }]
        },{
            title: 'Informes',
            options: [{
                description: "Listado por almacén.",
                title: "Listado por almacén.",
				action: () => this.rootPanel(new JSF.AonJsfStockReportWarehouse)
            },{
                description: "Listado por articulo.",
                title: "Listado por articulo.",
				action: () => this.rootPanel(new JSF.AonJsfStockReportItem)
            },{
                description: "Listado valorado por almacén.",
                title: "Listado valorado por almacén.",
				action: () => this.rootPanel(new JSF.AonJsfStockReportWarehouseValued)
            },{
                description: "Listado valorado por articulo.",
                title: "Listado valorado por articulo.",
				action: () => this.rootPanel(new JSF.AonJsfStockReportItemValued)
            },{
                description: "Aprovisionamiento según consumo",
                title: "Aprovisionamiento según consumo",
                action: () => alert("description")
            },{
                description: "Listado de movimientos",
                title: "Listado de movimientos",
                action: () => alert("description")
            }]
        },{
            title: 'Control de Existencias',
            options: [{
                description: "Cierre de inventario",
                title: "Cierre de inventario",
				action: () => this.rootPanel(new JSF.AonJsfInventoryClose)
            },{
                description: "Gestión de Inventarios",
                title: "Gestión de Inventarios",
				action: () => this.rootPanel(new JSF.AonJsfInventory)
            },{
                description: "Control de Existencias",
                title: "Control de Existencias",
				action: () => this.rootPanel(new JSF.AonJsfStock)
            },{
                description: "Propuesta de pedidos",
                title: "Propuesta de pedidos",
				action: () => this.rootPanel(new JSF.AonJsfOrderProposal)
            }]
        },{
            title: 'Elaboraciones',
            options: [{
                description: "Orden de elaboración",
                title: "Orden de elaboración",
                action: () => alert("description")
            },{
                description: "Packing List",
                title: "Packing List",
                action: () => alert("description")
            },{
                description: "Etiquetas Envio",
                title: "Etiquetas Envio",
                action: () => alert("description")
            }]
        },{
            title: 'Almacenes',
            options: [{
                description: "Definicion de Almacenes",
                title: "Definicion de Almacenes",
				action: () => this.rootPanel(new JSF.AonJsfWarehouse)
            },{
                description: "Agencias de transporte",
                title: "Agencias de transporte",
				action: () => this.rootPanel(new JSF.AonJsfCarrier)
            },{
                description: "Impresión de etiquetas de productos",
                title: "Impresión de etiquetas de productos",
                action: () => alert("description")
            }]
        }];
    }
    /*
    build() {
        
    }
    */
}
if(!window.customElements.get(TAG.AON_WAREHOUSE_MENU)){
    window.customElements.define(TAG.AON_WAREHOUSE_MENU, AonWarehouseMenu);
}