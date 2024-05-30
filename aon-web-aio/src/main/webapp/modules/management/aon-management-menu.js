import { MSG, CSS, EVENT, TAG } from 'aonsolutions/environments/environments.js'; 
import { AonSuiteMenu } from '../aon-suite-menu.js';
import * as GWT from 'aonsolutions/gwt/gwt.js';

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
        this.options = [{
            title: 'Ventas',
            options: [ {
                description: "Clientes",
                action: () => alert("description")
            },{
                description: "Facturas de Venta",
                action: () => alert("description")
            },{
                description: "Impresión / eMail de Facturas",
                action: () => alert("description")
            },{
                description: "Pedidos de Venta",
                action: () => alert("description")
            },{
                description: "Facturación masiva de Albaranes",
                action: () => alert("description")
            }]
        },{
            title: 'Compras',
            options: [{
                description: "Proveedores",
                action: () => alert("description")
            },{
                description: "Facturas de Compra",
                action: () => alert("description")
            },{
                description: "Pedidos de Compra",
                action: () => alert("description")
            }]
        },{
            title: 'Gastos',
            options: [{
                description: "Acreedores",
                action: () => alert("description")
            },{
                description: "Facturas de Gastos",
                action: () => alert("description")
            },{
                description: "Gastos no Deducibles en IVA",
                action: () => alert("description")
            }]
        },{
            title: 'Tesorería',
            options: [{
                description: "Gestión de Cobros",
                action: () => alert("description")
            },{
                description: "Gestión de Pagos",
                action: () => alert("description")
            },{
                description: "Cartera de cobros y pagos",
                action: () => alert("description")
            },{
                description: "Formas de Pago",
                action: () => alert("description")
            },{
                description: "Borrado de Facturas",
                action: () => alert("description")
            },{
                description: "Estadisticas Globales",
                action: () => alert("description")
            }]
        },{
            title: 'Impuestos',
            options: [{
                description: "Panel de control de IVA",
                action: () => alert("description")
            },{
                description: "Panel de Control de IRPF",
                action: () => alert("description")
            },{
                description: "SIl - Suministro Inmediato de Información",
                action: () => alert("description")
            },{
                description: "Modelo 347 - Declaración anual operaciones con terceras personas.",
                action: () => alert("description")
            }]
        },{
            title: 'Maestros',
            options: [{
                description: "Productos",
                action: () => alert("description")
            },{
                description: "Gastos",
                action: () => alert("description")
            },{
                description: "Categorías",
                action: () => alert("description")
            },{
                description: "Pais/Provincia",
                action: () => alert("description")
            },{
                description: "Segmentación",
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