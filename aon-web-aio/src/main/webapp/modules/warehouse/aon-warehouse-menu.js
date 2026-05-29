import { MSG, CSS, EVENT, TAG } from '../../environments/environments.js'; 
import { AonSuiteMenu } from '../aon-suite-menu.js';
import * as GWT from '../../gwt/gwt.js';
import * as JSF from '../aon-jsf-app.js';

export class AonWarehouseMenu extends AonSuiteMenu {

    AON_MENU;
    AON_HEADER;
    ROOT_PANEL;
    RIGHT_PANEL;

    constructor () {
        super();
		this.almacenInitialize()
    }

    connectedCallback () {
        this.clear();
        this.initialize();
        this.build();
    }

    almacenInitialize() {
        this.AON_MENU = 'aonMenu';
        this.AON_HEADER = 'aonHeader';
        this.ROOT_PANEL = 'rootPanel';
        this.RIGHT_PANEL = 'rightPanel';
        this.last = MSG.LAST_ELABORATIONS;
        this.new = MSG.NEW_DELIVERY_NOTE;
        this.cardData={
            title: MSG.ACTIVITY,
            info:[MSG.EXPEDITIONS_OPEN, MSG.OPEN_ELABORATIONS]
        };
        this.selectOptions= [{
            title: MSG.ELABORATION,
            action: () => alert("description")
        },{
            title: MSG.PURCHASE_DELIVERY,
            action: () => alert("description")
        },{
            title: MSG.SALE_DELIVERY,
            action: () => alert("description")
        }];
        this.initOptions();
    }

    initOptions() {
        this.options = [{
            title: MSG.MOVEMENTS,
            options: [{
                description: "Entradas (Albaranes de Compra)",
                title: "Entradas (Albaranes de Compra)",
                action: () => this.rootPanel(new JSF.AonJsfIncome)
            }, {
                description: "Salidas (Albaranes de Venta)",
                title: "Salidas (Albaranes de Venta)",
                action: () => this.rootPanel(new JSF.AonJsfDelivery)
            }, {
                description: "Traspasos entre almacenes",
                title: "Traspasos entre almacenes",
                action: () => this.rootPanel(new JSF.AonJsfWarehouseTransfer)
            }, {
                description: "Servir pedidos",
                title: "Servir pedidos",
                action: () => this.rootPanel(new JSF.AonJsfOrderServer)
            }]
        }, {
            title: MSG.REPORTS,
            options: [{
                description: "Listado por almacén.",
                title: "Listado por almacén.",
                action: () => this.rootPanel(new JSF.AonJsfStockReportWarehouse)
            }, {
                description: "Listado por articulo.",
                title: "Listado por articulo.",
                action: () => this.rootPanel(new JSF.AonJsfStockReportItem)
            }, {
                description: "Listado valorado por almacén.",
                title: "Listado valorado por almacén.",
                action: () => this.rootPanel(new JSF.AonJsfStockReportWarehouseValued)
            }, {
                description: "Listado valorado por articulo.",
                title: "Listado valorado por articulo.",
                action: () => this.rootPanel(new JSF.AonJsfStockReportItemValued)
            }, {
                description: "Aprovisionamiento según consumo",
                title: "Aprovisionamiento según consumo",
                action: () => GWT.iLoad(GWT.STOCK_FORECAST)
            }, {
                description: "Listado de movimientos",
                title: "Listado de movimientos",
                action: () => GWT.iLoad(GWT.MOVEMENT_LIST)
            }]
        }, {
            title: MSG.STOCK_CONTROL,
            options: [{
                description: "Cierre de inventario",
                title: "Cierre de inventario",
                action: () => this.rootPanel(new JSF.AonJsfInventoryClose)
            }, {
                description: "Gestión de Inventarios",
                title: "Gestión de Inventarios",
                action: () => this.rootPanel(new JSF.AonJsfInventory)
            }, {
                description: "Control de Existencias",
                title: "Control de Existencias",
                action: () => this.rootPanel(new JSF.AonJsfStock)
            }, {
                description: "Propuesta de pedidos",
                title: "Propuesta de pedidos",
                action: () => this.rootPanel(new JSF.AonJsfOrderProposal)
            }]
        }, {
            title: MSG.ELABORATIONS,
            options: [{
                description: "Orden de elaboración",
                title: "Orden de elaboración",
                action: () => GWT.iLoad(GWT.ELABORATION)
            }, {
                description: "Packing List",
                title: "Packing List",
                action: () => GWT.iLoad(GWT.PACKING_LIST)
            }, {
                description: "Etiquetas Envio",
                title: "Etiquetas Envio",
                action: () => this.rootPanel(new JSF.AonJsfCorporateIdentityLabel)
            }]
        }, {
            title: MSG.WAREHOUSES,
            options: [{
                description: "Definicion de Almacenes",
                title: "Definicion de Almacenes",
                action: () => this.rootPanel(new JSF.AonJsfWarehouse)
            }, {
                description: "Agencias de transporte",
                title: "Agencias de transporte",
                action: () => this.rootPanel(new JSF.AonJsfCarrier)
            }, {
                description: "Impresión de etiquetas de productos",
                title: "Impresión de etiquetas de productos",
                action: () => this.rootPanel(new JSF.AonJsfItemTagPrint)
                
            }]
        }];
    }
}
if(!window.customElements.get(TAG.AON_WAREHOUSE_MENU)){
    window.customElements.define(TAG.AON_WAREHOUSE_MENU, AonWarehouseMenu);
}