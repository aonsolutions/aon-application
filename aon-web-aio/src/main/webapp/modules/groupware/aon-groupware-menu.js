import { MSG, TAG } from '../../environments/environments.js';
import { AonSuiteMenu } from '../aon-suite-menu.js';
import * as JSF from '../aon-jsf-app.js';

export class AonGroupwareMenu extends AonSuiteMenu {

    AON_MENU;
    AON_HEADER;
    ROOT_PANEL;
    RIGHT_PANEL;

    constructor () {
        super();
		this.expedientesInitialize()
    }

    connectedCallback () {
        this.clear();
        this.initialize();
        this.build();
    }

    expedientesInitialize() {
        this.AON_MENU = 'aonMenu';
        this.AON_HEADER = 'aonHeader';
        this.ROOT_PANEL = 'rootPanel';
        this.RIGHT_PANEL = 'rightPanel';
        this.last = MSG.LAST_EXPEDIENTS;
        this.new = MSG.NEW_WORK_ORDER;
        this.cardData={
            title: MSG.ACTIVITY,
            info:[MSG.OPEN_EXPEDIENTS, MSG.REQUESTS_PENDING]
        };
        this.selectOptions= [{
            title: MSG.WORK_ORDER,
            action: () => alert("description")
        },{
            title: MSG.OPERATOR,
            action: () => alert("description")
        },{
            title: MSG.PROCESS,
            action: () => alert("description")
        },{
            title: MSG.GROUPWARE,
            action: () => this.rootPanel(new JSF.AonJsfProject)
        }];
        this.initOptions();
    }

    initOptions() {
        this.options = [{
            title: MSG.EXPEDIENTS,
            options: [{
                description: MSG.EXPEDIENTS,
                title: MSG.EXPEDIENTS,
                action: () => this.rootPanel(new JSF.AonJsfProject)
            }, {
                description: MSG.EXPEDIENT_TYPE,
                title: MSG.EXPEDIENT_TYPE,
                action: () => this.rootPanel(new JSF.AonJsfProjectType)
            }, {
                description: MSG.ACTIVITY_TYPE,
                title: MSG.ACTIVITY_TYPE,
                action: () => this.rootPanel(new JSF.AonJsfActivityType)
            }]
        }, {
            title: MSG.PROCESSES,
            options: [{
                description: MSG.PROCESSES,
                title: MSG.PROCESSES,
                action: () => this.rootPanel(new JSF.AonJsfProcess)
            }, {
                description: MSG.TRANSITION_TYPES,
                title: MSG.TRANSITION_TYPES,
                action: () => this.rootPanel(new JSF.AonJsfProcessTransactionType)
            }, {
                description: MSG.PROCESS_LAUNCHER,
                title: MSG.PROCESS_LAUNCHER,
                action: () => this.rootPanel(new JSF.AonJsfProcessWizard)
            }]
        }, {
            title: MSG.TASKS,
            options: [{
                description: MSG.TASK_TRAY,
                title: MSG.TASK_TRAY,
                action: () => this.rootPanel(new JSF.AonJsfTask)
            }, {
                description: MSG.GANTT_CHART,
                title: MSG.GANTT_CHART,
                action: () => this.rootPanel(new JSF.AonJsfGantt)
            }]
        }, {
            title: MSG.CAMPAIGNS,
            options: [{
                description: MSG.CAMPAIGN_MONITOR,
                title: MSG.CAMPAIGN_MONITOR,
                action: () => this.rootPanel(new JSF.AonJsfCampaign)
            }, {
                description: MSG.CAMPAIGN_TYPES,
                title: MSG.CAMPAIGN_TYPES,
                action: () => this.rootPanel(new JSF.AonJsfCampaignType)
            }]
        }, {
            title: MSG.WORK_ORDERS,
            options: [{
                description: MSG.WORK_ORDERS,
                title: MSG.WORK_ORDERS,
                action: () => this.rootPanel(new JSF.AonJsfDailyTracking)
            }, {
                description: MSG.REPORTS,
                title: MSG.REPORTS,
                action: () => this.rootPanel(new JSF.AonJsfDailyTrackingReport)
            }, {
                description: MSG.WORK_TYPES,
                title: MSG.WORK_TYPES,
                action: () => this.rootPanel(new JSF.AonJsfJobType)
            }]
        }, {
            title: MSG.OPERATORS,
            options: [{
                description: MSG.OPERATORS,
                title: MSG.OPERATORS,
                action: () => this.rootPanel(new JSF.AonJsfTaskHolder)
            }, {
                description: MSG.USER_GROUPS,
                title: MSG.USER_GROUPS,
                action: () => this.rootPanel(new JSF.AonJsfWorkgroup)
            }, {
                description: MSG.COST_PROFILES,
                title: MSG.COST_PROFILES,
                action: () => this.rootPanel(new JSF.AonJsfCostProfile)
            }]
        }];
    }
}
if(!window.customElements.get(TAG.AON_GROUPWARE_MENU)){
    window.customElements.define(TAG.AON_GROUPWARE_MENU, AonGroupwareMenu);
}