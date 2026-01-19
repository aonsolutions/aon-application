import { AonViewer } from '../../components/aon-viewer.js';
import { AonElement } from '../../components/AonElement.js';
import { CONSTANT, CSS, TAG } from '../../environments/environments.js'; 
import { AonInvoiceList } from "./aon-invoice-list.js";
import { AonMobileInvoiceList } from "./aon-mobile-invoice-list.js";

export class AonInvoiceProcessing extends AonElement {

    DIV;
    LIST;
    FILE;

    fileOpened;

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
        this.buildDur().then(() => this.build());
    }

    initialize() {
        this.id = this.id || 'aonInvoiceProcessing';
        this.DIV = this.id + CONSTANT.DIV.initCap();
        this.LIST = this.id + CONSTANT.LIST.initCap();
        this.FILE = this.id + CONSTANT.FILE.initCap();
    }

    build() {
        this.clear(); 

        let div = this.createDiv(this.DIV, CSS.AON_FLEX);
        this.appendChild(div);

        let list = this.createDiv(this.LIST, CSS.AON_SUB_CONTENT);
        list.style.width = this.fileOpened ? '50%' : '100%';
        div.appendChild(list);

        let file = this.createDiv(this.FILE, CSS.AON_SUB_CONTENT);
        file.style.display = 'none';
        div.appendChild(file);

        let filter = { status: CONSTANT.PROCESSING };
        this.buildList(filter);
    }


    buildList(filter) {
        this.getApplication().getParent().buildInvoiceToolbarOptions(filter && filter.status === 'accounting', filter && filter.status === CONSTANT.PROCESSING);

        let table = this.isMobile()
            ? new AonMobileInvoiceList()
            : new AonInvoiceList();
        table.id = "aonInvoiceList";
        if(!this.isMobile()) {
            table.setFn((invoice, i) => {
                if(this.getDur().hasInvofox()) {
                    if((invoice.status == 'pending' || invoice.status == 'processed') 
                            && this.getDur().isInvofox()) {
                        invoice.status = 'processed';
                        this.aonInvoice(invoice, i);
                    } else this.showFile(invoice, i);
                } else if(this.getDur().hasOcr()) {
                    if((invoice.status == 'pending' || invoice.status == 'processed') 
                            && this.getDur().isOcr()) {
                        invoice.status = 'processed';
                        this.aonInvoice(invoice, i);
                    } else this.showFile(invoice, i);
                } else {
                    invoice.status = 'processed';
                    this.aonInvoice(invoice, i);
                }
            });
        }
        table.setFilter(filter);

        let list = this.getElement(this.LIST);
        this.clearElement(list);
        list.appendChild(table);
    }

    aonInvoice(invoice, i){
        let aip = document.querySelector('aon-invoice-panel');
        aip.aonInvoice(invoice.type, invoice);
    }

    showFile(invoice, i){
        let fileDiv = this.getElement(this.FILE);
        let listDiv = this.getElement(this.LIST);
        if (invoice.file) {
            this.fileOpened = true;
 
            fileDiv.style.display = 'block';
            fileDiv.style.width = '50%';
            listDiv.style.width = '50%';
            
            this.clearElement(fileDiv);
            let viewer = new AonViewer();
            viewer.type = invoice.file.content_type;
            viewer.file = invoice.file.path;
            viewer.width = fileDiv.offsetWidth;
            // viewer.addEventListener(EVENT.SEND_MAIL, () => this.sendInvoice());
            // viewer.addEventListener(EVENT.PRINT_IMAGE, () => { getInvofoxTextContent(invoice.insight.invofoxId).then(t => viewer.printImageTextLayer(t)); } );
            // viewer.addEventListener(EVENT.PRINT_PDF_PAGE, (e) => { if ( !e.detail.text ) getInvofoxTextContent(invoice.insight.invofoxId).then(t => viewer.printPdfTextLayer(e.detail.page, t)); } );
            fileDiv.appendChild(viewer);
        } else {
			fileDiv.style.display = 'none';
			listDiv.style.width = '100%';
        }
    }
}
if(!window.customElements.get(TAG.AON_INVOICE_PROCESSING)){
    window.customElements.define(TAG.AON_INVOICE_PROCESSING, AonInvoiceProcessing);
}