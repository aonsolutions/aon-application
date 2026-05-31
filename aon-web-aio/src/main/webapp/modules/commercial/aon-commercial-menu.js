import { MSG, TAG } from '../../environments/environments.js';
import { AonSuiteMenu } from '../aon-suite-menu.js';
import * as GWT from '../../gwt/gwt.js';
import * as JSF from '../aon-jsf-app.js';

export class AonCommercialMenu extends AonSuiteMenu {

    AON_MENU;
    AON_HEADER;
    ROOT_PANEL;
    RIGHT_PANEL;

    constructor () {
        super();
		this.comercialInitialize()
    }

    connectedCallback () {
        this.clear();
        this.initialize();
        this.build();
    }

    comercialInitialize() {
        this.AON_MENU = 'aonMenu';
        this.AON_HEADER = 'aonHeader';
        this.ROOT_PANEL = 'rootPanel';
        this.RIGHT_PANEL = 'rightPanel';
        this.last = MSG.LAST_POTENTIALS;
        this.new = MSG.NEW_BUDGET;
        this.cardData={
            title: MSG.ACTIVITY,
            info:[MSG.BUDGETS_PENDING, MSG.OPEN_OPERATIONS, MSG.EXPIRED_APPOINTMENTS]
        };
        this.selectOptions= [{
            title: MSG.TARGET,
            action: () => this.rootPanel(new JSF.AonJsfTarget())
        },{
            title: MSG.COMMERCIAL_OPERATION,
            action: () => this.rootPanel(new JSF.AonJsfProjectCommercial())
        },{
            title: MSG.BUDGET,
            action: () => this.rootPanel(new JSF.AonJsfOffer())
        }];
        this.options = [{
            title: MSG.COMMERCIAL_ACTIVITY,
            options: [ {
                description: MSG.COMMERCIAL_OPERATION,
                title: MSG.COMMERCIAL_OPERATION,
                action: () => this.rootPanel(new JSF.AonJsfProjectCommercial())
            },{
                description: MSG.COMMERCIAL_CALENDAR,
                title: MSG.COMMERCIAL_CALENDAR,
                action: () => this.rootPanel(new JSF.AonJsfCommercialTracking())
            },{
                description: MSG.COMMERCIAL_ACTIVITY_TYPES,
                title: MSG.COMMERCIAL_ACTIVITY_TYPES,
                action: () => this.rootPanel(new JSF.AonJsfCommercialActivity())
            }]
        },{
            title: MSG.BUDGETS,
            options: [{
                description: MSG.SALES_QUOTE,
                title: MSG.SALES_QUOTE,
                action: () => this.rootPanel(new JSF.AonJsfOffer())
            },{
                description: MSG.SALE_ORDERS,
                title: MSG.SALE_ORDERS,
                action: () => this.rootPanel(new JSF.AonJsfSale())
            },{
                description: MSG.COMMERCIAL_TERMS,
                title: MSG.COMMERCIAL_TERMS,
                action: () => this.rootPanel(new JSF.AonJsfCommercialTerm())
            }]
        },{
            title: MSG.MASTERS,
            options: [{
                description: MSG.AGENTS_COMMERCIAL,
                title: MSG.AGENTS_COMMERCIAL,
                action: () => GWT.iLoad(GWT.SELLER_MODULE)
                //action: () => this.rootPanel(new JSF.AonJsfSeller())
            },{
                description: MSG.TARGETS,
                title: MSG.TARGETS,
                action: () => this.rootPanel(new JSF.AonJsfTarget())
            },{
                description: MSG.DEDUPLICATION_PROSPECTS,
                title: MSG.DEDUPLICATION_PROSPECTS,
                action: () => this.rootPanel(new JSF.AonJsfTargetDeduplication())
            }]
        },{
            title: MSG.DASHBOARD,
            options: [{
                description: MSG.DASHBOARD_SALES_AGENTS,
                title: MSG.DASHBOARD_SALES_AGENTS,
                action: () => this.rootPanel(new JSF.AonJsfCommercialStatSeller())
            },{
                description: MSG.DASHBOARD_PROSPECTS,
                title: MSG.DASHBOARD_PROSPECTS,
                action: () => this.rootPanel(new JSF.AonJsfCommercialStatTarget())
            },{
                description: MSG.DASHBOARD_PRODUCTS,
                title: MSG.DASHBOARD_PRODUCTS,
                action: () => this.rootPanel(new JSF.AonJsfCommercialStatProduct())
            },{
                description: MSG.DASHBOARD_CATEGORIES,
                title: MSG.DASHBOARD_CATEGORIES,
                action: () => this.rootPanel(new JSF.AonJsfCommercialStatCategory())
            }]
        },{
            title: MSG.REPORTS,
            options: [{
                description: MSG.QUOTES_BY_CATEGORY_PRODUCT,
                title: MSG.QUOTES_BY_CATEGORY_PRODUCT,
                action: () => this.rootPanel(new JSF.AonJsfCommercialCategoryStat())
            },{
                description: MSG.QUOTES_BY_ZONE,
                title: MSG.QUOTES_BY_ZONE,
				action: () => this.rootPanel(new JSF.AonJsfCommercialGeozoneStat())
            },{
                description: MSG.QUOTES_BY_AGENT,
                title: MSG.QUOTES_BY_AGENT,
				action: () => this.rootPanel(new JSF.AonJsfCommercialSellerStat())
            },{
                description: MSG.QUOTES_BY_PROSPECT,
                title: MSG.QUOTES_BY_PROSPECT,
				action: () => this.rootPanel(new JSF.AonJsfCommercialTargetStat())
            },{
                description: MSG.QUOTES_BY_PRODUCT,
                title: MSG.QUOTES_BY_PRODUCT,
                action: () => this.rootPanel(new JSF.AonJsfCommercialProductStat())
            }]
        },{
            title: MSG.COMMISSIONS,
            options: [{
                description: MSG.COMMISSIONS,
                title: MSG.COMMISSIONS,
                action: () => GWT.iLoad(GWT.COMMISSION_CALC)
            },{
                description: MSG.COMMISSION_CALCULATION,
                title: MSG.COMMISSION_CALCULATION,
                action: () => this.rootPanel(new JSF.AonJsfCommissionCalc())
            },{
                description: MSG.COMMISSION_CONTROL,
                title: MSG.COMMISSION_CONTROL,
                action: () => this.rootPanel(new JSF.AonJsfOfferDetailCommission())
            },{
                description: MSG.COMMISSION_TYPES,
                title: MSG.COMMISSION_TYPES,
                action: () => this.rootPanel(new JSF.AonJsfCommissionType())
            },{
                description: MSG.COMMISSION_BANDS,
                title: MSG.COMMISSION_BANDS,
                action: () => this.rootPanel(new JSF.AonJsfCommission())
            }]
        }];
    }
}
if(!window.customElements.get(TAG.AON_COMMERCIAL_MENU)){
    window.customElements.define(TAG.AON_COMMERCIAL_MENU, AonCommercialMenu);
}