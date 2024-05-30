import { MSG, CSS, EVENT, TAG } from 'aonsolutions/environments/environments.js'; 
import { AonSuiteMenu } from '../aon-suite-menu.js';
import * as GWT from 'aonsolutions/gwt/gwt.js';

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
        this.options = [{
            title: 'Expedientes',
            options: [ {
                description: "Expedientes",
                action: () => alert("description")
            },{
                description: "Tipo de Expediente",
                action: () => alert("description")
            },{
                description: "Tipo de Actividad",
                action: () => alert("description")
            }]
        },{
            title: 'Procesos',
            options: [{
                description: "Procesos",
                action: () => alert("description")
            },{
                description: "Tipos de Transiciones",
                action: () => alert("description")
            },{
                description: "Lanzador de procesos",
                action: () => alert("description")
            }]
        },{
            title: 'Tareas',
            options: [{
                description: "Bandeja de tareas",
                action: () => alert("description")
            },{
                description: "Diagrama de Gantt",
                action: () => alert("description")
            }]
        },{
            title: 'Campañas (Procesos Masivos)',
            options: [{
                description: "Monitor de campañas",
                action: () => alert("description")
            },{
                description: "Tipos de campañas",
                action: () => alert("description")
            }]
        },{
            title: 'Partes de Trabajo',
            options: [{
                description: "Partes de Trabajo",
                action: () => alert("description")
            },{
                description: "Informes",
                action: () => alert("description")
            },{
                description: "Tipos de Trabajos",
                action: () => alert("description")
            }]
        },{
            title: 'Operarios',
            options: [{
                description: "Operarios",
                action: () => alert("description")
            },{
                description: "Grupos de usuarios",
                action: () => alert("description")
            },{
                description: "Perfiles de coste",
                action: () => alert("description")
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