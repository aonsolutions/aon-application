import { MSG, CSS, EVENT, TAG } from 'aonsolutions/environments/environments.js'; 
import { AonSuiteMenu } from '../aon-suite-menu.js';
import * as GWT from 'aonsolutions/gwt/gwt.js';
import * as JSF from '../aon-jsf-app.js';

export class AonGroupwareMenu extends AonSuiteMenu {

    AON_MENU;
    AON_HEADER;
    ROOT_PANEL;
    RIGHT_PANEL;

    constructor () {
        super();
    }

    connectedCallback () {
        this.clear();
        this.expedientesInitialize()
        this.initialize();
        this.build();
        this.setTitle("Opciones de expedientes");
    }

    expedientesInitialize() {
        this.AON_MENU = 'aonMenu';
        this.AON_HEADER = 'aonHeader';
        this.ROOT_PANEL = 'rootPanel';
        this.RIGHT_PANEL = 'rightPanel';
        this.last = "Últimos expedientes";
        this.new = "Nuevo Parte de trabajo";
        this.cardData={
            title: "Actividad",
            info:["Expedientes abiertos", "Solicitudes ptes."]
        };
        this.selectOptions= [{
            title: "Parte de trabajo",
            action: () => alert("description")
        },{
            title: "Operario",
            action: () => alert("description")
        },{
            title: "Proceso", 
            action: () => alert("description")
        },{
            title: "Expediente", 
			action: () => this.rootPanel(new JSF.AonJsfProject)
        }];
        this.options = [{
            title: 'Expedientes',
            options: [ {
                description: "Expedientes",
                title: "Expedientes",
                action: () => this.rootPanel(new JSF.AonJsfProject)
            },{
                description: "Tipo de Expediente",
                title: "Tipo de Expediente",
                action: () => this.rootPanel(new JSF.AonJsfProjectType)
            },{
                description: "Tipo de Actividad",
                title: "Tipo de Actividad",
                action: () => this.rootPanel(new JSF.AonJsfActivityType)
            }]
        },{
            title: 'Procesos',
            options: [{
                description: "Procesos",
                title: "Procesos",
				action: () => this.rootPanel(new JSF.AonJsfProcess)
            },{
                description: "Tipos de Transiciones",
                title: "Tipos de Transiciones",
				action: () => this.rootPanel(new JSF.AonJsfProcessTransactionType)
            },{
                description: "Lanzador de procesos",
                title: "Lanzador de procesos",
				action: () => this.rootPanel(new JSF.AonJsfProcessWizard)
            }]
        },{
            title: 'Tareas',
            options: [{
                description: "Bandeja de tareas",
                title: "Bandeja de tareas",
                action: () => this.rootPanel(new JSF.AonJsfTask)
            },{
                description: "Diagrama de Gantt",
                title: "Diagrama de Gantt",
				action: () => this.rootPanel(new JSF.AonJsfGantt)
            }]
        },{
            title: 'Campañas (Procesos Masivos)',
            options: [{
                description: "Monitor de campañas",
                title: "Monitor de campañas",
				action: () => this.rootPanel(new JSF.AonJsfCampaign)
            },{
                description: "Tipos de campañas",
                title: "Tipos de campañas",
				action: () => this.rootPanel(new JSF.AonJsfCampaignType)
            }]
        },{
            title: 'Partes de Trabajo',
            options: [{
                description: "Partes de Trabajo",
                title: "Partes de Trabajo",
				action: () => this.rootPanel(new JSF.AonJsfDailyTracking)
            },{
                description: "Informes",
                title: "Informes",
				action: () => this.rootPanel(new JSF.AonJsfDailyTrackingReport)
            },{
                description: "Tipos de Trabajos",
                title: "Tipos de Trabajos",
				action: () => this.rootPanel(new JSF.AonJsfJobType)
            }]
        },{
            title: 'Operarios',
            options: [{
                description: "Operarios",
                title: "Operarios",
				action: () => this.rootPanel(new JSF.AonJsfTaskHolder)
            },{
                description: "Grupos de usuarios",
                title: "Grupos de usuarios",
				action: () => this.rootPanel(new JSF.AonJsfWorkgroup)
            },{
                description: "Perfiles de coste",
                title: "Perfiles de coste",
				action: () => this.rootPanel(new JSF.AonJsfCostProfile)
            }]
        }];
    }
    /*
    build() {
        
    }
    */
}
if(!window.customElements.get(TAG.AON_GROUPWARE_MENU)){
    window.customElements.define(TAG.AON_GROUPWARE_MENU, AonGroupwareMenu);
}