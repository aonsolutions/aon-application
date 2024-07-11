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
        this.last = "Últimos contratos";
        this.new = "Nuevo Contrato";
        this.cardData={
            title: "Actividad",
            info:["Nóminas ptes.", "Finiquitos ptes.", "Liquidaciones ptes."]
        };
        this.selectOptions= [{
            title: "Parte IT",
            action: () => alert("description")
        },{
            title: "Contrato",
            action: () => alert("description")
        }];
        this.options = [{
            title: 'Nóminas',
            options: [ {
                description: "Integral de Nominas",
                title: "Integral de Nominas",
                action: () => GWT.load(GWT.EMPLOYEES)
            },{
                description: "Contratos",
                title: "Contratos",
                action: () => GWT.load(GWT.MAIN_CONTRATA)
            },{
                description: "Partes IT",
                title: "Partes IT",
                action: () => alert("description")
            },{
                description: "Convenios",
                title: "Convenios",
                action: () => alert("description")
            },{
                description: "Calculo de Nóminas",
                title: "Calculo de Nóminas",
                action: () => alert("description")
            }]
        },{
            title: 'Seguridad Social',
            options: [{
                description: "Cret@ - Sistema de Liquidación Directa",
                title: "Cret@ - Sistema de Liquidación Directa",
                action: () => alert("description")
            },{
                description: "CRA - Conceptos Retributivos Abonados",
                title: "CRA - Conceptos Retributivos Abonados",
                action: () => alert("description")
            },{
                description: "AFI - Altas, bajas y modificaciones de trabajadores",
                title: "AFI - Altas, bajas y modificaciones de trabajadores",
                action: () => alert("description")
            },{
                description: "AFI - Régimen Especial Agrario Jornadas",
                title: "AFI - Régimen Especial Agrario Jornadas",
                action: () => alert("description")
            },{
                description: "AFI - Reduc. contribuciones planes de pensiones",
                title: "AFI - Reduc. contribuciones planes de pensiones",
                action: () => alert("description")
            }]
        },{
            title: 'Procesos',
            options: [{
                description: "Impresión / eMail de Nóminas",
                title: "Impresión / eMail de Nóminas",
                action: () => alert("description")
            },{
                description: "Listado de costes",
                title: "Listado de costes",
                action: () => alert("description")
            },{
                description: "Resumen de actividad",
                title: "Resumen de actividad",
                action: () => alert("description")
            },{
                description: "Informe de personal asalariado",
                title: "Informe de personal asalariado",
                action: () => alert("description")
            },{
                description: "Cambio masivo contratos",
                title: "Cambio masivo contratos",
                action: () => alert("description")
            }]
        },{
            title: 'Gestión',
            options: [{
                description: "Remesa Transferencia de Nóminas",
                title: "Remesa Transferencia de Nóminas",
                action: () => alert("description")
            },{
                description: "Vencimientos de Nóminas",
                title: "Vencimientos de Nóminas",
                action: () => alert("description")
            },{
                description: "Facturas de Gastos",
                title: "Facturas de Gastos",
                action: () => alert("description")
            },{
                description: "Vencimientos de Nóminas",
                title: "Vencimientos de Nóminas",
                action: () => alert("description")
            },{
                description: "Remesa Transferencia de Nóminas",
                title: "Remesa Transferencia de Nóminas",
                action: () => alert("description")
            }]
        },{
            title: 'SEPE',
            options: [{
                description: "Notificaciones Contrat@",
                title: "Notificaciones Contrat@",
                action: () => alert("description")
            },{
                description: "Notificaciones Certific@",
                title: "Notificaciones Certific@",
                action: () => alert("description")
            }]
        },{
            title: 'Auxiliares',
            options: [{
                description: "Modelos de contrato",
                title: "Modelos de contrato",
                action: () => alert("description")
            },{
                description: "Centros acreditados de formación",
                title: "Centros acreditados de formación",
                action: () => alert("description")
            },{
                description: "Festivos",
                title: "Festivos",
                action: () => alert("description")
            },{
                description: "Variables Calculo Trabajadores",
                title: "Variables Calculo Trabajadores",
                action: () => alert("description")
            }]
        },{
            title: 'Utilidades',
            options: [{
                description: "Gestión de Certificados",
                title: "Gestión de Certificados",
                action: () => alert("description")
            },{
                description: "Papelera",
                title: "Papelera",
                action: () => alert("description")
            },{
                description: "CCC",
                title: "CCC",
                action: () => alert("description")
            },{
                description: "Comunic@",
                title: "Comunic@",
                action: () => alert("description")
            }]
        },{
            title: 'Modelos Tributarios',
            options: [{
                description: "Modelo 145",
                title: "Modelo 145",
                action: () => alert("description")
            },{
                description: "Modelo 111",
                title: "Modelo 111",
                action: () => GWT.load(GWT.MODEL_111)
            },{
                description: "Modelo 190",
                title: "Modelo 190",
                action: () => GWT.load(GWT.MODEL_190)
            }]
        },{
            title: 'Antiguas Opciones (Obsoletas)',
            options: [{
                description: "Contratos",
                title: "Contratos",
                action: () => alert("description")
            },{
                description: "Personas",
                title: "Personas",
                action: () => alert("description")
            },{
                description: "Bajas IT",
                title: "Bajas IT",
                action: () => alert("description")
            },{
                description: "CRA - Conceptos retributivos abonados",
                title: "CRA - Conceptos retributivos abonados",
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