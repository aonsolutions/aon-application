import { MSG, CSS, EVENT, TAG } from 'aonsolutions/environments/environments.js'; 
import { AonSuiteMenu } from '../aon-suite-menu.js';
import * as GWT from 'aonsolutions/gwt/gwt.js';

export class AonPayrollMenu extends AonSuiteMenu {

    AON_MENU;
    AON_HEADER;
    ROOT_PANEL;
    RIGHT_PANEL;

    constructor () {
        super();
    }

    connectedCallback () {
        this.clear();
        this.laboralInitialize()
        this.initialize();
        this.build();
        this.setTitle("Opciones de laboral");
    }

    laboralInitialize() {
        this.AON_MENU = 'aonMenu';
        this.AON_HEADER = 'aonHeader';
        this.ROOT_PANEL = 'rootPanel';
        this.RIGHT_PANEL = 'rightPanel';
        this.options = [{
            title: 'Nóminas',
            options: [ {
                description: "Integral de Nominas",
                action: () => alert("description")
            },{
                description: "Contratos",
                action: () => alert("description")
            },{
                description: "Partes IT",
                action: () => alert("description")
            },{
                description: "Convenios",
                action: () => alert("description")
            },{
                description: "Calculo de Nóminas",
                action: () => alert("description")
            }]
        },{
            title: 'Seguridad Social',
            options: [{
                description: "Cret@ - Sistema de Liquidación Directa",
                action: () => alert("description")
            },{
                description: "CRA - Conceptos Retributivos Abonados",
                action: () => alert("description")
            },{
                description: "AFI - Altas, bajas y modificaciones de trabajadores",
                action: () => alert("description")
            },{
                description: "AFI - Régimen Especial Agrario Jornadas",
                action: () => alert("description")
            },{
                description: "AFI - Reduc. contribuciones planes de pensiones",
                action: () => alert("description")
            }]
        },{
            title: 'Procesos',
            options: [{
                description: "Impresión / eMail de Nóminas",
                action: () => alert("description")
            },{
                description: "Listado de costes",
                action: () => alert("description")
            },{
                description: "Resumen de actividad",
                action: () => alert("description")
            },{
                description: "Informe de personal asalariado",
                action: () => alert("description")
            },{
                description: "Cambio masivo contratos",
                action: () => alert("description")
            }]
        },{
            title: 'Gestión',
            options: [{
                description: "Remesa Transferencia de Nóminas",
                action: () => alert("description")
            },{
                description: "Vencimientos de Nóminas",
                action: () => alert("description")
            },{
                description: "Facturas de Gastos",
                action: () => alert("description")
            },{
                description: "Vencimientos de Nóminas",
                action: () => alert("description")
            },{
                description: "Remesa Transferencia de Nóminas",
                action: () => alert("description")
            }]
        },{
            title: 'SEPE',
            options: [{
                description: "Notificaciones Contrat@",
                action: () => alert("description")
            },{
                description: "Notificaciones Certific@2",
                action: () => alert("description")
            }]
        },{
            title: 'Auxiliares',
            options: [{
                description: "Modelos de contrato",
                action: () => alert("description")
            },{
                description: "Centros acreditados de formación",
                action: () => alert("description")
            },{
                description: "Festivos",
                action: () => alert("description")
            },{
                description: "Variables Calculo Trabajadores",
                action: () => alert("description")
            }]
        },{
            title: 'Utilidades',
            options: [{
                description: "Gestión de Certificados",
                action: () => alert("description")
            },{
                description: "Papelera",
                action: () => alert("description")
            },{
                description: "CCC",
                action: () => alert("description")
            },{
                description: "Comunic@",
                action: () => alert("description")
            }]
        },{
            title: 'Modelos Tributarios',
            options: [{
                description: "Modelo 145",
                action: () => alert("description")
            },{
                description: "Modelo 111",
                action: () => alert("description")
            },{
                description: "Modelo 190",
                action: () => alert("description")
            }]
        },{
            title: 'Antiguas Opciones (Obsoletas)',
            options: [{
                description: "Contratos",
                action: () => alert("description")
            },{
                description: "Personas",
                action: () => alert("description")
            },{
                description: "Bajas IT",
                action: () => alert("description")
            },{
                description: "CRA - Conceptos retributivos abonados",
                action: () => alert("description")
            }]
        }];
    }
    /*
    build() {
        
    }
    */
}
if(!window.customElements.get(TAG.AON_PAYROLL_MENU)){
    window.customElements.define(TAG.AON_PAYROLL_MENU, AonPayrollMenu);
}