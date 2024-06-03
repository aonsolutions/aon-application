import { MSG, CSS, EVENT, TAG } from 'aonsolutions/environments/environments.js'; 
import { AonSuiteMenu } from '../aon-suite-menu.js';
import * as GWT from 'aonsolutions/gwt/gwt.js';

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
                action: () => alert("description")
            },{
                description: "Salidas (Albaranes de Compra)",
                title: "Salidas (Albaranes de Compra)",
                action: () => alert("description")
            },{
                description: "Traspasos entre almacenes",
                title: "Traspasos entre almacenes",
                action: () => alert("description")
            },{
                description: "Servir pedidos",
                title: "Servir pedidos",
                action: () => alert("description")
            }]
        },{
            title: 'Informes',
            options: [{
                description: "Listado por almacén.",
                title: "Listado por almacén.",
                action: () => alert("description")
            },{
                description: "Listado por articulo.",
                title: "Listado por articulo.",
                action: () => alert("description")
            },{
                description: "Listado valorado por almacén.",
                title: "Listado valorado por almacén.",
                action: () => alert("description")
            },{
                description: "Listado valorado por articulo.",
                title: "Listado valorado por articulo.",
                action: () => alert("description")
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
                action: () => alert("description")
            },{
                description: "Gestión de Inventarios",
                title: "Gestión de Inventarios",
                action: () => alert("description")
            },{
                description: "Control de Existencias",
                title: "Control de Existencias",
                action: () => alert("description")
            },{
                description: "Propuesta de pedidos",
                title: "Propuesta de pedidos",
                action: () => alert("description")
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
                action: () => alert("description")
            },{
                description: "Agencias de transporte",
                title: "Agencias de transporte",
                action: () => alert("description")
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