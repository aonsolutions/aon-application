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
        this.options = [{
            title: 'Contenidos',
            options: [ {
                description: "Mensajes",
                action: () => alert("description")
            },{
                description: "Noticias",
                action: () => alert("description")
            },{
                description: "Boletines",
                action: () => alert("description")
            }]
        },{
            title: 'Comunicaciones',
            options: [{
                description: "Campañas",
                action: () => alert("description")
            },{
                description: "Communication Center",
                action: () => alert("description")
            }]
        },{
            title: 'Cuestionarios',
            options: [{
                description: "Preguntas",
                action: () => alert("description")
            },{
                description: "Cuestionarios",
                action: () => alert("description")
            },{
                description: "Respuestas de Cuestionarios",
                action: () => alert("description")
            }]
        },{
            title: 'Plantillas',
            options: [{
                description: "Cabeceras y Pies de Plantilla",
                action: () => alert("description")
            },{
                description: "Plantillas de Correos",
                action: () => alert("description")
            },{
                description: "Imagenes",
                action: () => alert("description")
            },{
                description: "Personalización de Envio Email",
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