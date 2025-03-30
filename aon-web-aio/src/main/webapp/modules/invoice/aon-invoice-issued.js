import { AonTab } from '../../components/aon-tab.js';
import { AonElement } from '../../components/AonElement.js';
import { CONSTANT, MSG, TAG } from '../../environments/environments.js'; 
import { AonInvoiceList } from "./aon-invoice-list.js";
import { AonMobileInvoiceList } from "./aon-mobile-invoice-list.js";

export class AonInvoiceIssued extends AonElement {

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
        this.id = this.id || 'aonInvoiceIssued';
        this.TABS = this.id + 'Tabs';
        this.DIV = this.id + 'Div';
        this.options = this.options || [
            { title: MSG.PROFORMA_INVOICES, fn: () => this.buildRawdocInvoices() },
            { title: MSG.ISSUED_INVOICES, fn: () => this.buildInvoices() }
        ];
    }

    build() {
        this.buildTabs();

        let div = this.createElement(TAG.DIV);
        div.id = this.DIV;
        div.style.width = "100%";
        div.style.marginTop = '1px';
        this.appendChild(div);

        this.buildRawdocInvoices();
    }

    buildTabs() {
        let tab = new AonTab();
        tab.id = this.TABS;
        tab.setOptions(this.options);
        this.appendChild(tab);
    }

    buildRawdocInvoices() { 
        let filter = { status: CONSTANT.INBOX, type: "emitida" };
        this.buildList(filter)
       
    }

    buildInvoices() {
        let filter = {
            status: "accounting",
            type: "sales",
            page: 1,
            per_page: 50,
        }
        this.buildList(filter);
    }

    buildList(filter) {
        this.getApplication().getParent().buildInvoiceToolbarOptions(filter && filter.status === 'accounting', filter && filter.status === CONSTANT.PROCESSING);

        let table = this.isMobile()
            ? new AonMobileInvoiceList()
            : new AonInvoiceList();
        table.id = "aonInvoiceList";
        table.setFilter(filter);

        // this.getApplication().buildDragAndDrop(true);

        let div = this.getElement(this.DIV);
        this.clearElement(div);
        div.appendChild(table);
    }
}
if(!window.customElements.get(TAG.AON_INVOICE_ISSUED)){
    window.customElements.define(TAG.AON_INVOICE_ISSUED, AonInvoiceIssued);
}