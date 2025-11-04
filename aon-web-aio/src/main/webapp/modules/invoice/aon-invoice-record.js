import { AonTab } from '../../components/aon-tab.js';
import {AonElement} from '../../components/AonElement.js';

import {CONSTANT, MSG, TAG } from '../../environments/environments.js'; 

import * as GWT from "../../gwt/gwt.js";
import * as JSF from "../aon-jsf-app.js";

export class AonInvoiceRecord extends AonElement {

    TABS;
    DIV;

    get id() {
        return this.getAttribute(CONSTANT.ID);
    }

    set id(id) {
        this.setAttribute(CONSTANT.ID, id);
    }

    constructor () {
        super();
    }

    connectedCallback () {
        this.initialize();
        this.build();
    }

    initialize() {
        this.id = this.id || 'aonInvoiceRecord';
        this.TABS = this.id + 'Tabs';
        this.DIV = this.id + 'Div';
        this.options = this.options || [
			{ title: MSG.INVOICES, fn: () => this.buildInvoiceRecord()},
			{ title: MSG.PENDING_DOCUMENTS, fn: () => this.buildRawdocRecord()}
		];
    }

    build() {
        this.buildTabs();
		this.style.height = "100%";

        let div = this.createElement(TAG.DIV);
		div.id = this.DIV;
		div.style.width = "100%";
		div.style.height = "100%";
        div.style.marginTop = '1px';
		this.appendChild(div);

        this.buildInvoiceRecord();
    }

    buildTabs() {
        let tab = new AonTab();
        tab.id = this.TABS;
        tab.setOptions(this.options);
        this.appendChild(tab);
    }

    buildInvoiceRecord() {
        let div = this.getElement(this.DIV);
		this.clearElement(div);
        div.appendChild(new JSF.AonJsfInvoiceRecorder());
    }

    buildRawdocRecord() {
        let div = this.getElement(this.DIV);
		this.clearElement(div);
        GWT.iLoad(GWT.RAWDOC, this.DIV);
    }
        
}
if(!window.customElements.get(TAG.AON_INVOICE_RECORD)){
    window.customElements.define(TAG.AON_INVOICE_RECORD, AonInvoiceRecord);
}