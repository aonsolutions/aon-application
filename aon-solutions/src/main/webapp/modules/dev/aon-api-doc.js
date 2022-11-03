import {AonElement} from '../../components/AonElement.js';
import '../../components/aon-application.js';
import { MSG, TAG } from '../../environments/environments.js';
import { AonApplication } from '../../components/aon-application.js';
import * as OPTIONS from './AonApiOptions.js';
import * as OBJECTS from './AonApiObjects.js';
import * as EXAMPLES from './AonApiExamples.js';
import { DIV } from '../../environments/aonTag.js';
import { AonApiObject } from './aon-api-object.js';
import { AonApiRequest } from './aon-api-request.js';
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
		let invoiceObject = OPTIONS.INVOICE_OBJECT;
        invoiceObject.fn = () => this.buildInvoiceObject();
        
		let getInvoices = OPTIONS.GET_INVOICES;
        getInvoices.fn = () => this.buildGetInvoices();
        
		let getInvoice = OPTIONS.GET_INVOICE;
		getInvoice.fn = () => this.buildGetInvoice();

		let createInvoice = OPTIONS.CREATE_INVOICE;
		createInvoice.fn = () => this.buildCreateInvoice();
		
		let updateInvoice = OPTIONS.UPDATE_INVOICE;
		updateInvoice.fn = () => this.buildUpdateInvoice();
		
		let deleteInvoice = OPTIONS.DELETE_INVOICE;
		deleteInvoice.fn = () => this.buildDeleteInvoice();
		
		let invoice = OPTIONS.INVOICES;
		invoice.options = [invoiceObject, getInvoices, getInvoice, createInvoice, updateInvoice, deleteInvoice];
		let options = [invoice];

		this.getApplication().addSidenavOptions(MSG.REQUESTS, options);
	}

	buildContent() {
		let div = this.createElement(TAG.DIV);
	}

	buildInvoiceObject() {
		let invoiceObject = new AonApiObject();
		invoiceObject.setObject(OBJECTS.INVOICE_OBJECT);
		invoiceObject.setExample(EXAMPLES.INVOICE);
		invoiceObject.setTitle(MSG.INVOICE_OBJECT);
		this.getApplication().setContent(invoiceObject);		
	}

	buildGetInvoices() {
		let str = 'GET or POST https://aon.solutions/ms/api/invoices';
		let req = new AonApiRequest();
		req.setHttpRequest(str);
		req.setTitle(MSG.GET_INVOICES);
		this.getApplication().setContent(req);
	}
	
	
	buildGetInvoice() {
		let str = 'GET or POST https://aon.solutions/ms/api/invoices/:id';
		let req = new AonApiRequest();
		req.setHttpRequest(str);
		req.setTitle(MSG.GET_INVOICE);
		this.getApplication().setContent(req);
	}

	buildCreateInvoice() {
		let str = 'PUT https://aon.solutions/ms/api/invoices';
		let req = new AonApiRequest();
		req.setHttpRequest(str);
		req.setTitle(MSG.CREATE_INVOICE);
		this.getApplication().setContent(req);
	}

	buildUpdateInvoice() {
		let str = 'PUT https://aon.solutions/ms/api/invoices/:id';
		let req = new AonApiRequest();
		req.setHttpRequest(str);
		req.setTitle(MSG.UPDATE_INVOICE);
		this.getApplication().setContent(req);
	}

	buildDeleteInvoice() {
		let str = 'DELETE https://aon.solutions/ms/api/invoices/:id';
		let req = new AonApiRequest();
		req.setHttpRequest(str);
		req.setTitle(MSG.DELETE_INVOICE);
		this.getApplication().setContent(req);
	}
	
}
if(!window.customElements.get(TAG.AON_API_DOC)){
	window.customElements.define(TAG.AON_API_DOC, AonApiDoc);
 }
