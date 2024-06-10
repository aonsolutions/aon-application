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
        this.last = "Últimos potenciales";
        this.new = "Nuevo Presupuesto";
        this.cardData={
            title: "Actividad",
            info:["Presupuestos ptes.", "Operaciones abiertas", "Citas expiradas"]
        };
        this.selectOptions= [{
            title: "Cliente potencial",
            action: () => alert("description")
        },{
            title: "Operación comercial",
            action: () => alert("description")
        },{
            title: "Presupuesto", 
            action: () => alert("description")
        }];
        this.options = [{
            title: 'Actividad Comercial',
            options: [ {
                description: "Operación Comercial",
                title: "Operación Comercial",
                action: () => alert("description")
            },{
                description: "Agenda Comercial",
                title: "Agenda Comercial",
                action: () => alert("description")
            },{
                description: "Tipos de Actividades Comerciales",
                title: "Agenda Comercial",
                action: () => alert("description")
            }]
        },{
            title: 'Presupuestos',
            options: [{
                description: "Presupuesto de Ventas",
                title: "Presupuesto de Ventas",
                action: () => alert("description")
            },{
                description: "Pedidos de Venta",
                title: "Pedidos de Venta",
                action: () => alert("description")
            },{
                description: "Condiciones Comerciales",
                title: "Condiciones Comerciales",
                action: () => alert("description")
            }]
        },{
            title: 'Maestros',
            options: [{
                description: "Agentes Comerciales",
                title: "Agentes Comerciales",
                action: () => alert("description")
            },{
                description: "Clientes Potenciales",
                title: "Clientes Potenciales",
                action: () => alert("description")
            },{
                description: "Deduplicación de Clientes Potenciales",
                title: "Deduplicación de Clientes Potenciales",
                action: () => alert("description")
            }]
        },{
            title: 'Cuadro de Mando',
            options: [{
                description: "CM de Agentes Comerciales",
                title: "CM de Agentes Comerciales",
                action: () => alert("description")
            },{
                description: "CM de Clientes Potenciales",
                title: "CM de Clientes Potenciales",
                action: () => alert("description")
            },{
                description: "CM de Productos",
                title: "CM de Productos",
                action: () => alert("description")
            },{
                description: "CM de Categorias",
                title: "CM de Categorias",
                action: () => alert("description")
            }]
        },{
            title: 'Informes',
            options: [{
                description: "Presupuestos por Categoria y Producto",
                title: "Presupuestos por Categoria y Producto",
                action: () => alert("description")
            },{
                description: "Presupuestos por Zona",
                title: "Presupuestos por Zona",
                action: () => alert("description")
            },{
                description: "Presupuestos por Agente Comercial",
                title: "Presupuestos por Agente Comercial",
                action: () => alert("description")
            },{
                description: "Presupuestos por Cliente Potencial",
                title: "Presupuestos por Cliente Potencial",
                action: () => alert("description")
            },{
                description: "Presupuestos por Producto",
                title: "Presupuestos por Producto",
                action: () => alert("description")
            }]
        },{
            title: 'Comisiones',
            options: [{
                description: "Comisiones",
                title: "Comisiones",
                action: () => alert("description")
            },{
                description: "Cálculo de Comisiones",
                title: "Cálculo de Comisiones",
                action: () => alert("description")
            },{
                description: "Control de Comisiones Calculadas",
                title: "Control de Comisiones Calculadas",
                action: () => alert("description")
            },{
                description: "Tipos de Comisión",
                title: "Tipos de Comisión",
                action: () => alert("description")
            },{
                description: "Definición de tramos de comisiones",
                title: "Definición de tramos de comisiones",
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