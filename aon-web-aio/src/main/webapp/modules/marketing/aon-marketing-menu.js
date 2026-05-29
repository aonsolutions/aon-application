import { MSG, TAG } from '../../environments/environments.js';
import { AonSuiteMenu } from '../aon-suite-menu.js';
import * as GWT from '../../gwt/gwt.js';
import * as JSF from '../aon-jsf-app.js';


export class AonMarketingMenu extends AonSuiteMenu {

    AON_MENU;
    AON_HEADER;
    ROOT_PANEL;
    RIGHT_PANEL;

    constructor () {
        super();
		this.marketingInitialize()
    }

    connectedCallback () {
        this.clear();
        this.initialize();
        this.build();
    }

    marketingInitialize() {
        this.AON_MENU = 'aonMenu';
        this.AON_HEADER = 'aonHeader';
        this.ROOT_PANEL = 'rootPanel';
        this.RIGHT_PANEL = 'rightPanel';
        this.last = MSG.LAST_ACTIONS;
        this.new = MSG.NEW_ACTION;
        this.cardData={
            title: MSG.ACTIVITY,
            info:[MSG.ACTIVE_CAMPAIGNS, MSG.ACTIVE_ACTIONS, MSG.UNASSIGNED_LEAD]
        };
        this.selectOptions= [{
            title: MSG.CAMPAIGN,
            action: () => alert("description")
        },{
            title: "Acción",
            action: () => alert("description")
        }];
        this.initOptions();
    }

    initOptions() {
        this.options = [{
            title: MSG.CONTENTS,
            options: [{
                description: "Mensajes",
                title: "Mensajes",
                action: () => this.rootPanel(new JSF.AonJsfMessages)
            }, {
                description: "Noticias",
                title: "Noticias",
                action: () => this.rootPanel(new JSF.AonJsfNews())
            }, {
                description: "Boletines",
                title: "Boletines",
                action: () => this.rootPanel(new JSF.AonJsfNewsletter())
            }]
        }, {
            title: MSG.COMMUNICATIONS,
            options: [{
                description: "Campañas",
                title: "Campañas",
                action: () => GWT.iLoad(GWT.MARKETING_CAMPAIGN)
                //action: () => this.rootPanel(new JSF.AonJsfMarketingCampaign())
            }, {
                description: "Communication Center",
                title: "Communication Center",
                action: () => this.rootPanel(new JSF.AonJsfCommunicationCenter())
            }]
        }, {
            title: MSG.SURVEYS,
            options: [{
                description: "Preguntas",
                title: "Preguntas",
                action: () => GWT.iLoad(GWT.QUESTION)
            }, {
                description: "Cuestionarios",
                title: "Cuestionarios",
                action: () => this.rootPanel(new JSF.AonJsfSurvey())
            }, {
                description: "Respuestas de Cuestionarios",
                title: "Respuestas de Cuestionarios",
                action: () => this.rootPanel(new JSF.AonJsfSurveyResponse())
            }]
        }, {
            title: MSG.TEMPLATES,
            options: [{
                description: "Cabeceras y Pies de Plantilla",
                title: "Cabeceras y Pies de Plantilla",
                action: () => this.rootPanel(new JSF.AonJsfHtmlTemplate())
            }, {
                description: "Plantillas de Correos",
                title: "Plantillas de Correos",
                action: () => this.rootPanel(new JSF.AonJsfMarketingTemplate())
            }, {
                description: "Imagenes",
                title: "Imagenes",
                action: () => this.rootPanel(new JSF.AonJsfCompanyImages())
            }, {
                description: "Personalización de Envio Email",
                title: "Personalización de Envio Email",
                action: () => this.rootPanel(new JSF.AonJsfMailProcess())
            }]
        }];
    }
}
if(!window.customElements.get(TAG.AON_MARKETING_MENU)){
    window.customElements.define(TAG.AON_MARKETING_MENU, AonMarketingMenu);
}