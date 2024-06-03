import { MSG, CSS, EVENT, TAG } from 'aonsolutions/environments/environments.js'; 
import { AonSuiteMenu } from '../aon-suite-menu.js';
import * as GWT from 'aonsolutions/gwt/gwt.js';

export class AonMarketingMenu extends AonSuiteMenu {

    AON_MENU;
    AON_HEADER;
    ROOT_PANEL;
    RIGHT_PANEL;

    constructor () {
        super();
    }

    connectedCallback () {
        this.clear();
        this.marketingInitialize()
        this.initialize();
        this.build();
        this.setTitle("Opciones de marketing");
    }

    marketingInitialize() {
        this.AON_MENU = 'aonMenu';
        this.AON_HEADER = 'aonHeader';
        this.ROOT_PANEL = 'rootPanel';
        this.RIGHT_PANEL = 'rightPanel';
        this.last = "Últimas acciones";
        this.new = "Nueva Acción";
        this.cardData={
            title: "Actividad",
            info:["Campañas activas", "Acciones activas", "Lead sin asignar"]
        };
        this.selectOptions= [{
            title: "Campaña",
            action: () => alert("description")
        },{
            title: "Acción",
            action: () => alert("description")
        }];
        this.options = [{
            title: 'Contenidos',
            options: [ {
                description: "Mensajes",
                title: "Mensajes",
                action: () => alert("description")
            },{
                description: "Noticias",
                title: "Noticias",
                action: () => alert("description")
            },{
                description: "Boletines",
                title: "Boletines",
                action: () => alert("description")
            }]
        },{
            title: 'Comunicaciones',
            options: [{
                description: "Campañas",
                title: "Campañas",
                action: () => alert("description")
            },{
                description: "Communication Center",
                title: "Communication Center",
                action: () => alert("description")
            }]
        },{
            title: 'Cuestionarios',
            options: [{
                description: "Preguntas",
                title: "Preguntas",
                action: () => alert("description")
            },{
                description: "Cuestionarios",
                title: "Cuestionarios",
                action: () => alert("description")
            },{
                description: "Respuestas de Cuestionarios",
                title: "Respuestas de Cuestionarios",
                action: () => alert("description")
            }]
        },{
            title: 'Plantillas',
            options: [{
                description: "Cabeceras y Pies de Plantilla",
                title: "Cabeceras y Pies de Plantilla",
                action: () => alert("description")
            },{
                description: "Plantillas de Correos",
                title: "Plantillas de Correos",
                action: () => alert("description")
            },{
                description: "Imagenes",
                title: "Imagenes",
                action: () => alert("description")
            },{
                description: "Personalización de Envio Email",
                title: "Personalización de Envio Email",
                action: () => alert("description")
            }]
        }];
    }
    /*
    build() {
        
    }
    */
}
if(!window.customElements.get(TAG.AON_MARKETING_MENU)){
    window.customElements.define(TAG.AON_MARKETING_MENU, AonMarketingMenu);
}