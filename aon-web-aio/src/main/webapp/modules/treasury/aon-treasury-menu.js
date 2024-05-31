import { MSG, CSS, EVENT, TAG } from 'aonsolutions/environments/environments.js'; 
import { AonSuiteMenu } from '../aon-suite-menu.js';
import * as GWT from 'aonsolutions/gwt/gwt.js';

export class AonTreasuryMenu extends AonSuiteMenu {

    AON_MENU;
    AON_HEADER;
    ROOT_PANEL;
    RIGHT_PANEL;

    constructor () {
        super();
    }

    connectedCallback () {
        this.clear();
        this.tesoreriaInitialize()
        this.initialize();
        this.build();
        this.setTitle("Opciones de tesorería");
    }

    tesoreriaInitialize() {
        this.AON_MENU = 'aonMenu';
        this.AON_HEADER = 'aonHeader';
        this.ROOT_PANEL = 'rootPanel';
        this.RIGHT_PANEL = 'rightPanel';
        this.last = "Últimos vencidos";
        this.new = "Nuevo vencimiento";
        this.options = [{
            title: 'Cobros',
            options: [ {
                description: "Gestión de Cobros",
                action: () => alert("description")
            },{
                description: "Remesas de Cobro",
                action: () => alert("description")
            },{
                description: "Orden de domiciliación de adeudo directo SEPA",
                action: () => alert("description")
            }]
        },{
            title: 'Pagos',
            options: [{
                description: "Gestión de Pagos",
                action: () => alert("description")
            },{
                description: "Remesas de Pago",
                action: () => alert("description")
            },{
                description: "Remesas de Pago",
                action: () => alert("description")
            },{
                description: "Impresión de Pagarés",
                action: () => alert("description")
            }]
        },{
            title: 'Previsión',
            options: [{
                description: "Gestión de Previsiones",
                action: () => alert("description")
            },{
                description: "Listado de Previsión",
                action: () => alert("description")
            },{
                description: "Proyección de Cuotas",
                action: () => alert("description")
            }]
        },{
            title: 'Movimientos Bancarios',
            options: [{
                description: "Conciliador Bancario",
                action: () => alert("description")
            },{
                description: "Agregador Bancario",
                action: () => alert("description")
            },{
                description: "Gestión de Suplidos",
                action: () => alert("description")
            }]
        },{
            title: 'Cuotas',
            options: [{
                description: "Listado de Pre-facturación",
                action: () => alert("description")
            },{
                description: "Facturación de Cuotas",
                action: () => alert("description")
            },{
                description: "Asignar Cuotas a Clientes",
                action: () => alert("description")
            },{
                description: "Listado de Cuotas",
                action: () => alert("description")
            },{
                description: "Panel Facturación de Cuotas",
                action: () => alert("description")
            }]
        },{
            title: 'Utilidades',
            options: [{
                description: "Recargos en Facturas",
                action: () => alert("description")
            },{
                description: "Grupos de Facturación",
                action: () => alert("description")
            },{
                description: "Firma de Facturas",
                action: () => alert("description")
            },{
                description: "Utilidades Tesoreria",
                action: () => alert("description")
            },{
                description: "Chequeo de Datos Financieros",
                action: () => alert("description")
            },{
                description: "Chequeo de Facturas / Vencimientos",
                action: () => alert("description")
            }]
        }];
    }
    /*
    build() {
        
    }
    */
}
if(!window.customElements.get(TAG.AON_TREASURY_MENU)){
    window.customElements.define(TAG.AON_TREASURY_MENU, AonTreasuryMenu);
}