import { AonTab } from '../../components/aon-tab.js';
import {AonElement} from '../../components/AonElement.js';
import { EVENT } from '../../environments/environments.js';

import {CONSTANT, MSG, TAG } from '../../environments/environments.js'; 
import { getInvoicesCounters } from '../../services/accountingService.js';

import * as GWT from "../../gwt/gwt.js";
import * as JSF from "../aon-jsf-app.js";
import { createCard } from '../../components/CreateComponent.js';

// com.code.aon.finance.enumeration.InvoiceType
export const INVOICE_TYPE = {
    SALES: 'SALES',
    PURCHASE: 'PURCHASE',
    EXPENSES: 'EXPENSES',
    UNDEDUCTIBLE: 'UNDEDUCTIBLE'
};

// com.esferalia.aon.occam.api.model.type.RawdocStatus
// ALL no es un valor del enumerado: el modulo GWT lo interpreta como "sin filtro de estado"
export const RAWDOC_STATUS = {
    ALL: 'ALL',
    INBOX: 'INBOX',
    REJECTED: 'REJECTED',
    TRASH: 'TRASH',
    PROCESSING: 'PROCESSING',
    PROCESSED: 'PROCESSED'
};

export class AonInvoiceRecord extends AonElement {

    TABS;
    DIV;
    COUNTERS_DIV;
    invoiceType;

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
        this.COUNTERS_DIV = this.id + 'CountersDiv';
        this.options = this.options || [
    	    { title: MSG.SUMMARY, fn: () => this.buildAccountingSummary()},
	       	{ title: MSG.INVOICES, fn: () => this.buildInvoiceRecord()},
	    	{ title: MSG.PENDING_DOCUMENTS, fn: () => this.buildRawdocRecord()}
    	];
    }

    build() {
        this.buildTabs();

        let div = this.createElement(TAG.DIV);
		div.id = this.DIV;
		div.style.width = "100%";
        div.style.marginTop = '1px';
		this.appendChild(div);
        this.buildAccountingSummary();
    }

    buildCountersDiv(parent) {
        let div = this.createElement(TAG.DIV);
        div.id = this.COUNTERS_DIV;
        div.style.width = "100%";
        div.style.display = "flex";
        div.style.justifyContent = "space-between";
        parent.appendChild(div);

        // TODO Enable this method when the front was pretty
        this.refreshCounters();
    }

    refreshCounters() {
        let params = { 
            invoices: true,
            rawdoc: true, 
            alcatraz: false 
        };
        getInvoicesCounters(params)
            .then(stat => {
                let invoices = [
                    {label: "Op. Interiores",       	value: stat.national || 0}, 
                    {label: "Intracomunitarias",    	value: stat.intracommunity || 0}, 
                    {label: "Extracomunitarias",    	value: stat.extracommunity || 0}, 
                    {label: "Canarias, Ceuta, Melilla", value: stat.canCeuMel || 0}, 
                    {label: "I.S.P.",             		value: stat.otherISP || 0}, 
                    {label: "Con Retención",        	value: stat.withholding || 0},
                    {label: "Proformas",                value: stat.proformas || 0},
                ];

                let unrecordes = [
                    {label: MSG.ISSUEDS,                value: stat.unrecordedIssued || 0, color: "orange", fn: () => this.buildInvoiceRecord(INVOICE_TYPE.SALES)}, 
                    {label: MSG.EXPENSES,               value: stat.unrecordedExpensed || 0, color: "orange", fn: () => this.buildInvoiceRecord(INVOICE_TYPE.EXPENSES)}, 
                    {label: MSG.PURCHASES,              value: stat.unrecordedPurchased || 0, color: "orange", fn: () => this.buildInvoiceRecord(INVOICE_TYPE.PURCHASE)}, 
                    {label: MSG.UNDEDUCTIBLE_EXPENSES,  value: stat.unrecordedSimplified || 0, color: "orange", fn: () => this.buildInvoiceRecord(INVOICE_TYPE.UNDEDUCTIBLE)}, 
                ];
                let rawdocs = [
                    {label: "Borrador",   	value: stat.draft || 0, color: "orange", fn: () => this.buildRawdocRecord(RAWDOC_STATUS.INBOX)}, 
                    {label: "En trámite",   value: stat.inProcess || 0, color: "orange", fn: () => this.buildRawdocRecord(RAWDOC_STATUS.PROCESSED)}, 
                    {label: "A revisar",    value: stat.review || 0, color: "red", fn: () => this.buildRawdocRecord(RAWDOC_STATUS.REJECTED)}, 
                    {label: "Papelera",     value: stat.trash || 0, color: "gray", fn: () => this.buildRawdocRecord(RAWDOC_STATUS.TRASH)}, 
                ];

                this.paintBlock(this.id + "invoices", "Resumen de Facturas", invoices);
                this.paintBlock(this.id + "unrecordes", "Facturas Pendientes de contabilizar", unrecordes);
                this.paintBlock(this.id + "rawdocs", "Documentos Pendientes de contabilizar", rawdocs);
                
            });
    }

    // TODO refactor this method to be pretty
    paintBlock(id, title, data) {
        let card = createCard(id, title, this.getElement(this.COUNTERS_DIV));
        card.style.width = "100%";
        let contentDiv = this.createDiv();
        card.setContent(contentDiv);   
        data.forEach(item => {
            let itemDiv = this.createElement(TAG.DIV);
            itemDiv.className = "aonInvoiceRecordDiv";

            let labelSpan = this.createElement(TAG.SPAN);
            labelSpan.textContent = item.label;
            labelSpan.className = "aonInvoiceRecordLabel";

            let valueSpan = this.createElement(TAG.SPAN);
            valueSpan.textContent = item.value;
            valueSpan.className = "aonInvoiceRecordValue";

			let foregroundColor = item.color;
			if (item.value && item.value > 0) {
                if(foregroundColor) {
				    labelSpan.style.color = foregroundColor;
				    valueSpan.style.color = foregroundColor;
                }
                if(item.fn) {
                    itemDiv.addEventListener(EVENT.CLICK, () => item.fn());
                }
			} else itemDiv.style.cursor = "default";

			itemDiv.appendChild(labelSpan);
            itemDiv.appendChild(valueSpan);
            contentDiv.appendChild(itemDiv);
        });
    }

    buildTabs() {
        let tab = new AonTab();
        tab.id = this.TABS;
        tab.setOptions(this.options);
        this.appendChild(tab);
    }

    buildAccountingSummary() {
        let div = this.getElement(this.DIV);
		this.clearElement(div);
        this.buildCountersDiv(div);
    }

    buildInvoiceRecord(invoiceType = this.invoiceType) {
        this.getElement(this.TABS).selectTab(1);
        let div = this.getElement(this.DIV);
		this.clearElement(div);
        let jsfInvoiceRecorder = new JSF.AonJsfInvoiceRecorder();
        if (invoiceType) {
            jsfInvoiceRecorder.setElExpression(`invoiceRecorderSearch.setDefaultType('${invoiceType}')`);
        }
        div.appendChild(jsfInvoiceRecorder);
    }

    buildRawdocRecord(rawdocStatus = RAWDOC_STATUS.PROCESSED) {
        let div = this.getElement(this.DIV);
		this.clearElement(div);
        GWT.iLoad(GWT.RAWDOC, this.DIV, { rawdocStatus });
    }
        
}
if(!window.customElements.get(TAG.AON_INVOICE_RECORD)){
    window.customElements.define(TAG.AON_INVOICE_RECORD, AonInvoiceRecord);
}