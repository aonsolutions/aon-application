import {AonElement} from '../../components/AonElement.js';
import '../../components/aon-application.js';
import { MSG, TAG } from '../../environments/environments.js';
import { AonApplication } from '../../components/aon-application.js';
import * as OPTIONS from './AonApiOptions.js';
import { DIV } from '../../environments/aonTag.js';
import { AonApiObject } from './aon-api-object.js';
export class AonApiDoc extends AonElement {

	AON_API_DOC;

	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
		this.createApplication(this.AON_API_DOC, 
			MSG.API_DOCUMENTATION, new AonApplication());
    	this.build();
 	}

	initialize(){
		this.AON_API_DOC = 'aonApiDoc';
	}

 	build() {
		this.buildToolbar();
		this.buildSidenav();
		this.buildContent();
	}

	buildToolbar() {

	}

	buildSidenav() {
		let invoiceOptions = [];

		let invoiceObject = OPTIONS.INVOICE_OBJECT;
        invoiceObject.fn = () => this.buildInvoiceObject();
        invoiceOptions.push(invoiceObject);

		let getInvoices = OPTIONS.GET_INVOICES;
        getInvoices.fn = () => this.buildGetInvoices();
        invoiceOptions.push(getInvoices);
		
		let getInvoice = OPTIONS.GET_INVOICE;
		getInvoice.fn = () => this.buildGetInvoice();
		invoiceOptions.push(getInvoice);
		
		this.getApplication().addSidenavOptions(MSG.INVOICES, invoiceOptions);
	}

	buildContent() {
		let div = this.createElement(TAG.DIV);

	}

	buildInvoiceObject() {
		let invoiceObject = new AonApiObject();
		invoiceObject.setObject([{name: 'id', type: 'number', description: 'Description del id.'}]);
		invoiceObject.setExample({id: 1, hola:'asdasd'});
		this.getApplication().setContent(invoiceObject);		
	}

	buildGetInvoices() {
		let div = this.createElement(TAG.DIV);
		div.style.margin = '20px';
		div.style.padding = '20px';
		div.style.backgroundColor = '#1E2224';
		div.style.color = 'white';
		div.innerHTML = 'GET or POST https://aon.solutions/ms/api/invoices';
		this.getApplication().setContent(div);
	}
	
	
	buildGetInvoice() {
		
	}

	
}
if(!window.customElements.get(TAG.AON_API_DOC)){
	window.customElements.define(TAG.AON_API_DOC, AonApiDoc);
 }
