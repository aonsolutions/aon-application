import { AonToolbar } from "../../components/aon-toolbar.js";
import { AonElement } from "../../components/AonElement.js";
import { MSG, TAG } from "../../environments/environments.js";
import { ToolbarType } from "../../models/enums.js";
import { getInvoiceConfiguration } from "../../services/invoiceService.js";
import { AonInvoicePrint } from "./aon-invoice-print.js";
import * as ACTION from '../actions.js';
import { AonTab } from "../../components/aon-tab.js";
import { AonInvoiceCommunication } from "./aon-invoice-communication.js";

export class AonInvoiceConfiguration extends AonElement {
    
    configuration;
    options;

    TOOLBAR;
    TABS;
    CONTENT;
    
    connectedCallback () {
        getInvoiceConfiguration().then(r => {
            this.configuration = r;
            this.initialize();
            this.build();
        });
  	}

    initialize() {
        this.configuration = this.configuration || {};
        this.TOOLBAR = 'invoiceConfigurationToolbar';
        this.TABS = 'invoiceConfigurationTabs';
        this.CONTENT = 'invoiceConfigurationContent';
        this.options = this.options || [
			{ title: MSG.INVOICE_PRINTING, fn: () => this.buildPrintConfiguration()},
			{ title: MSG.COMMUNICATION, fn: () => this.buildCommunication()}
        ];
    }

    build() {
        let toolbar = new AonToolbar();
		toolbar.id = this.TOOLBAR;
		toolbar.type = ToolbarType.SECONDARY;
		toolbar.title = MSG.INVOICE_CONFIGURATION;
		this.appendChild(toolbar);
		toolbar.addButton2(ACTION.SAVE, () => this.save());
        this.buildTabs();

        let content = this.createElement(TAG.DIV);
        content.id = this.CONTENT;
        this.appendChild(content);

        this.buildPrintConfiguration();
    }
    
	buildTabs() {
		let tab = new AonTab();
		tab.id = this.TABS;
		tab.setOptions(this.options);
		this.appendChild(tab);
	}

    buildPrintConfiguration() {
        let content = this.getElement(this.CONTENT);
        this.clearElement(content);
        content.appendChild(new AonInvoicePrint());
    }

    buildCommunication() {
        let content = this.getElement(this.CONTENT);
        this.clearElement(content);
        let communication =  new AonInvoiceCommunication();
        communication.setConfiguration(this.configuration);
        content.appendChild(communication);
    }

    save() {

    }
}
if(!window.customElements.get(TAG.AON_INVOICE_CONFIGURATION)){
	window.customElements.define(TAG.AON_INVOICE_CONFIGURATION, AonInvoiceConfiguration);
}