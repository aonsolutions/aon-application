import { MSG, CSS, EVENT, TAG } from '../../environments/environments.js'; 
import { AonSuiteMenu } from '../aon-suite-menu.js';
import * as GWT from '../../gwt/gwt.js';
import * as JSF from '../aon-jsf-app.js';

export class AonCommerceMenu extends AonSuiteMenu {

	AON_MENU;
	AON_HEADER;
	ROOT_PANEL;
	RIGHT_PANEL;

	constructor () {
		super();
		this.accountingInitialize()
	}

	connectedCallback () {
		this.clear();
		this.initialize();
		this.build();
		this.setTitle("Opciones de TPV");
	}

	accountingInitialize() {
		this.AON_MENU = 'aonMenu';
		this.AON_HEADER = 'aonHeader';
		this.ROOT_PANEL = 'rootPanel';
		this.RIGHT_PANEL = 'rightPanel';
		this.last = MSG.LAST_ENTRIES;
		this.new = MSG.NEW_ENTRY;
        this.uploadButton = true
		this.selectOptions= [{
            title: MSG.ACCOUNTING_ACCOUNT,
            action: () => alert("description")
        },{
            title: MSG.AMORTIZATION_SHEET,
            action: () => alert("description")
        },{
            title: MSG.ENTRY,
            action: () => alert("description")
        }];

		this.initOptions();
	}

	initOptions() {
		this.options = [{
			title: MSG.POS,
			options: [ {
				description: MSG.CASH_OPENING,
				title: MSG.CASH_OPENING,
				action: () => this.rootPanel(new JSF.AonJsfPosOpening )
			},{
				description: MSG.CASH_AUDIT,
				title: MSG.CASH_AUDIT,
				action: () => this.rootPanel(new JSF.AonJsfPosClosing )
			},{
				description: MSG.POS_SALES,
				title: MSG.POS_SALES,
				action: () => this.rootPanel(new JSF.AonJsfPosInvoice )
			},{
				description: MSG.BATCH_CASH_RECEIPTS,
				title: MSG.BATCH_CASH_RECEIPTS,
				action: () => this.rootPanel(new JSF.AonJsfPosFinance )
			},{
				description: MSG.PRODUCTS,
				title: MSG.PRODUCTS,
				action: () => this.rootPanel(new JSF.AonJsfProduct )
			}]
		},{
			title: MSG.AUXILIARIES,
			options: [{
				description: MSG.PRODUCT_TAGS,
				title: MSG.PRODUCT_TAGS,
				action: () => this.rootPanel(new JSF.AonJsfProductTag )
			},{
				description: MSG.CATEGORIES,
				title: MSG.CATEGORIES,
				action: () => this.rootPanel(new JSF.AonJsfProductCategory )
			},{
				description: MSG.CASH_DEFINITION,
				title: MSG.CASH_DEFINITION,
				action: () => this.rootPanel(new JSF.AonJsfPos )
			},{
				description: MSG.CASH_SHIFTS,
				title: MSG.CASH_SHIFTS,
				action: () => this.rootPanel(new JSF.AonJsfPosShift )
			},{
				description: MSG.PRODUCT_TAG_PRINT,
				title: MSG.PRODUCT_TAG_PRINT,
				action: () => this.rootPanel(new JSF.AonJsfItemTagPrint )
			}]
		}];
	}
	/*
	build() {
        
	}
	*/
}
if(!window.customElements.get(TAG.AON_COMMERCE_MENU)){
	window.customElements.define(TAG.AON_COMMERCE_MENU, AonCommerceMenu);
}
