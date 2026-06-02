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
            title: MSG.ACTION,
            action: () => alert("description")
        }];
        this.initOptions();
    }

    initOptions() {
        this.options = [{
            title: MSG.CONTENTS,
            options: [{
                description: MSG.MESSAGES,
                title: MSG.MESSAGES,
                action: () => this.rootPanel(new JSF.AonJsfMessages)
            }, {
                description: MSG.NEWS,
                title: MSG.NEWS,
                action: () => this.rootPanel(new JSF.AonJsfNews())
            }, {
                description: MSG.NEWSLETTERS,
                title: MSG.NEWSLETTERS,
                action: () => this.rootPanel(new JSF.AonJsfNewsletter())
            }]
        }, {
            title: MSG.COMMUNICATIONS,
            options: [{
                description: MSG.CAMPAIGNS,
                title: MSG.CAMPAIGNS,
                action: () => GWT.iLoad(GWT.MARKETING_CAMPAIGN)
                //action: () => this.rootPanel(new JSF.AonJsfMarketingCampaign())
            }, {
                description: MSG.COMMUNICATION_CENTER,
                title: MSG.COMMUNICATION_CENTER,
                action: () => this.rootPanel(new JSF.AonJsfCommunicationCenter())
            }]
        }, {
            title: MSG.SURVEYS,
            options: [{
                description: MSG.QUESTIONS,
                title: MSG.QUESTIONS,
                action: () => GWT.iLoad(GWT.QUESTION)
            }, {
                description: MSG.SURVEYS,
                title: MSG.SURVEYS,
                action: () => this.rootPanel(new JSF.AonJsfSurvey())
            }, {
                description: MSG.SURVEY_RESPONSES,
                title: MSG.SURVEY_RESPONSES,
                action: () => this.rootPanel(new JSF.AonJsfSurveyResponse())
            }]
        }, {
            title: MSG.TEMPLATES,
            options: [{
                description: MSG.TEMPLATE_HEADERS_FOOTERS,
                title: MSG.TEMPLATE_HEADERS_FOOTERS,
                action: () => this.rootPanel(new JSF.AonJsfHtmlTemplate())
            }, {
                description: MSG.EMAIL_TEMPLATES,
                title: MSG.EMAIL_TEMPLATES,
                action: () => this.rootPanel(new JSF.AonJsfMarketingTemplate())
            }, {
                description: MSG.IMAGES,
                title: MSG.IMAGES,
                action: () => this.rootPanel(new JSF.AonJsfCompanyImages())
            }, {
                description: MSG.EMAIL_SENDING_CUSTOMIZATION,
                title: MSG.EMAIL_SENDING_CUSTOMIZATION,
                action: () => this.rootPanel(new JSF.AonJsfMailProcess())
            }]
        }];
    }
}
if(!window.customElements.get(TAG.AON_MARKETING_MENU)){
    window.customElements.define(TAG.AON_MARKETING_MENU, AonMarketingMenu);
}