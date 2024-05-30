import { MSG, CSS, EVENT, TAG } from 'aonsolutions/environments/environments.js'; 
import { AonSuiteMenu } from '../aon-suite-menu.js';
import * as GWT from 'aonsolutions/gwt/gwt.js';

export class AonCommercialMenu extends AonSuiteMenu {

    AON_MENU;
    AON_HEADER;
    ROOT_PANEL;
    RIGHT_PANEL;

    constructor () {
        super();
    }

    connectedCallback () {
        this.clear();
        this.comercialInitialize()
        this.initialize();
        this.build();
        this.setTitle("Opciones comerciales");
    }

    comercialInitialize() {
        this.AON_MENU = 'aonMenu';
        this.AON_HEADER = 'aonHeader';
        this.ROOT_PANEL = 'rootPanel';
        this.RIGHT_PANEL = 'rightPanel';
        this.options = [{
            title: 'Actividad Comercial',
            options: [ {
                description: "Operación Comercial",
                action: () => alert("description")
            },{
                description: "Agenda Comercial",
                action: () => alert("description")
            },{
                description: "Tipos de Actividades Comerciales",
                action: () => alert("description")
            }]
        },{
            title: 'Presupuestos',
            options: [{
                description: "Presupuesto de Ventas",
                action: () => alert("description")
            },{
                description: "Pedidos de Venta",
                action: () => alert("description")
            },{
                description: "Condiciones Comerciales",
                action: () => alert("description")
            }]
        },{
            title: 'Maestros',
            options: [{
                description: "Agentes Comerciales",
                action: () => alert("description")
            },{
                description: "Clientes Potenciales",
                action: () => alert("description")
            },{
                description: "Deduplicación de Clientes Potenciales",
                action: () => alert("description")
            }]
        },{
            title: 'Cuadro de Mando',
            options: [{
                description: "CM de Agentes Comerciales",
                action: () => alert("description")
            },{
                description: "CM de Clientes Potenciales",
                action: () => alert("description")
            },{
                description: "CM de Productos",
                action: () => alert("description")
            },{
                description: "CM de Categorias",
                action: () => alert("description")
            }]
        },{
            title: 'Informes',
            options: [{
                description: "Presupuestos por Categoria y Producto",
                action: () => alert("description")
            },{
                description: "Presupuestos por Zona",
                action: () => alert("description")
            },{
                description: "Presupuestos por Agente Comercial",
                action: () => alert("description")
            },{
                description: "Presupuestos por Cliente Potencial",
                action: () => alert("description")
            },{
                description: "Presupuestos por Producto",
                action: () => alert("description")
            }]
        },{
            title: 'Comisiones',
            options: [{
                description: "Comisiones",
                action: () => alert("description")
            },{
                description: "Cálculo de Comisiones",
                action: () => alert("description")
            },{
                description: "Control de Comisiones Calculadas",
                action: () => alert("description")
            },{
                description: "Tipos de Comisión",
                action: () => alert("description")
            },{
                description: "Definición de tramos de comisiones",
                action: () => alert("description")
            }]
        }];
    }
    /*
    build() {
        
    }
    */
}
if(!window.customElements.get(TAG.AON_COMMERCIAL_MENU)){
    window.customElements.define(TAG.AON_COMMERCIAL_MENU, AonCommercialMenu);
}