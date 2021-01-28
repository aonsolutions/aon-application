import {AonElement} from '../../components/AonElement.js';
import {AonApplication} from '../../components/aon-application.js';
import {insertInvoice, deleteInvoices} from '../../services/service.js';
import {Invoice} from './Invoice.js';
import {InvoiceAction} from './invoiceEnums.js';

import './aon-invoice.js';
import './aon-mobile-invoice.js';
import './aon-invoice-list.js';
import './aon-mobile-invoice-list.js';
import './aon-invoice-print.js';

import '../../components/aon-application.js';
import '../../components/aon-dialog-menu.js';

import * as MSG from "../../environments/msg.js";

export class AonInvoicePanel extends AonElement {

	selected;
	_filter;

	INVOICE;
	INPUTFILE;

	get status() {
		return this.getAttribute('status');
	}

	set status(status) {
		this.setAttribute('status', status);
	}

	constructor () {
		super();
		this.INVOICE = 'aonInvoice';
		this.INPUTFILE = this.INVOICE + 'InputFile';
		this._filter = {
			status: 'inbox',
			page: 0,
			per_page: 50
		}
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-application id="${this.INVOICE}" title="${MSG.AON_MSG_INVOICES}" drag_and_drop="true"></aon-application>
			<aon-dialog-menu id="aonDialogAddOption"> </aon-dialog-menu>
			<input id="${this.INPUTFILE}" style='display:none;' type='file' name='file' multiple>
		`;
		this.build();
	}

  build(){
		let aonInvoice = this.getElement(this.INVOICE);
		let input = this.getElement(this.INPUTFILE);

		input.addEventListener('change', () => this.preview(input.files));

		aonInvoice.addEventListener('drop', (event) => {
			if(event && event.dataTransfer && event.dataTransfer.files){
				this.preview(event.dataTransfer.files);
			}
		});

		if(this.isMobile()) {
			aonInvoice.addFloatOption(InvoiceAction.ADD_INVOICE, () => this.addInvoice());
		} else {
			aonInvoice.addToolbarOption('Add', 'add', () => this.addInvoice());
			aonInvoice.addToolbarOption('Upload', 'file_upload', () => this.addInvoiceFile());

			aonInvoice.addSearchOption();
      aonInvoice.addEventListener('search', (event) => this.search(event.detail));
		}

		this.appendChild(input);

		let pendingOptions = [
			{
				name: MSG.AON_MSG_INBOX,
				icon: 'inbox',
				fn: () => this.aonInvoiceList({status:'inbox'})
			},
			{
				name: MSG.AON_MSG_REJECTEDS,
				icon: 'report',
				fn: () => this.aonInvoiceList({status:'refused'})
			},
			{
				name: MSG.AON_MSG_TRASH,
				icon: 'delete',
				fn: () => this.aonInvoiceList({status:'trash'})
			}
		];
		aonInvoice.addSidenavOptions(MSG.AON_MSG_PENDINGS.toUpperCase(), pendingOptions);

		let accountingOptions = [
			{
				name: MSG.AON_MSG_ISSUEDS,
				icon: 'unarchive',
				fn: () => this.aonInvoiceList({status:'accounting', type:'sales', page:1, per_page: 50})
			},
			{
				name: MSG.AON_MSG_RECEIVEDS,
				icon: 'archive',
				fn: () => this.aonInvoiceList({status:'accounting', type:'purchase,expenses', page:1, per_page: 50})
			},
			{
				name: MSG.AON_MSG_TICKETS,
				icon: 'receipt',
				fn: () => this.aonInvoiceList({status:'accounting', type:'ticket', page:1, per_page: 50})
			}
		];
		aonInvoice.addSidenavOptions(MSG.AON_MSG_ACCOUNTEDS.toUpperCase(), accountingOptions);

		let settingOptions = [
			{
				name: MSG.AON_MSG_PRINTING_INVOICES,
				icon: 'print',
				fn: () => {this.aonInvoicePrint()}
			}
		];
		aonInvoice.addSidenavOptions(MSG.AON_MSG_SETTING.toUpperCase(), settingOptions);

		let filter = {status:'inbox'};

		if(this.hasAttribute('status')) {
			filter = {status: this.getAttribute('status')};
		}

		if(!this.isMobile()){
			let toolbar = this.getElement(aonInvoice.TOOLBAR);
			if(filter.status === 'inbox') toolbar.setAttribute('option', MSG.AON_MSG_INBOX);
			else if(filter.status === 'refused') toolbar.setAttribute('option', MSG.AON_MSG_REJECTEDS);
		}
		this.aonInvoiceList(filter);
	}

	search(value) {
		this._filter.description = value;
		this.aonInvoiceList();
	}

	aonInvoiceList(filter) {
		filter = filter || this._filter;
		this._filter = filter;
		let invoiceList = document.getElementById('aonInvoiceList');
		if(invoiceList) {
			invoiceList.setFilter(filter);
			invoiceList.init();
		} else {
			let aonInvoice = document.getElementById('aonInvoice');
			if(this.isMobile()) {
				aonInvoice.setContentHTML(filter
					? `<aon-mobile-invoice-list id="aonInvoiceList" filter='${JSON.stringify(filter)}'></aon-mobile-invoice-list>`
					: `<aon-mobile-invoice-list id="aonInvoiceList"></aon-mobile-invoice-list>`);
			}  else {
				aonInvoice.setContentHTML(filter
					? `<aon-invoice-list id="aonInvoiceList" filter='${JSON.stringify(filter)}'></aon-invoice-list>`
					: `<aon-invoice-list id="aonInvoiceList"></aon-invoice-list>`);
			}
		}
	}

	aonInvoicePrint() {
		aonInvoice.setContentHTML(`<aon-invoice-print></aon-invoice-print>`)
	}

	addInvoice() {
		let aonInvoice = this.getElement('aonInvoice');
		let aonInvoiceToolbar = this.getElement(aonInvoice.TOOLBAR);
		let button = this.isMobile()
			?	this.getElement('aonInvoiceAddInvoiceButton')
			: this.getElement(aonInvoiceToolbar.TOOL_SECTION + 'AddButton');

		let height = window.innerHeight;
		let top  = button.getBoundingClientRect().top;
		const left = button.getBoundingClientRect().left;

		if((height - top) < (height / 2)) {
				top = top - 135;
		}

		let d = document.getElementById('aonDialogAddOption');

		let options = [{
				name: 'Emitidas',
				icon: 'unarchive',
				fn: () => this.aonInvoice('emitida')
			}, {
				name: 'Recibidas',
				icon: 'archive',
				fn: () => this.aonInvoice('recibida')
			}, {
				name: 'Tickets/Justificantes',
				icon: 'receipt',
				fn: () => this.aonInvoice('ticket')
			}];
		d.setMenuOptions(options, top, left);
		d.open();
	}

	addInvoiceFile() {
		let el = this.getElement(this.INPUTFILE);
		el.click();
	}

	preview(files) {
		for(let i = 0; i < files.length; i++) {
			const READER = new FileReader();
			READER.readAsDataURL(files[i]);
			READER.onload = (_event) => {
				this.attach(READER.result, files[i].type);
			};
		}
	}

	attach(fileDataUri,  mimetype){
		if (fileDataUri.length > 0) {
			const base64File = fileDataUri.split(',')[1];
			const data = {
				file: {
					content: base64File,
					contentType: mimetype,
					contentEncoding: 'base64'
				},
				invoice: new Invoice('recibida')
			};
			let aonInvoice = document.getElementById('aonInvoice');
			aonInvoice.startLoader();
			insertInvoice(data).then((r) => {
 				this.aonInvoiceList({status:'inbox'})
 				aonInvoice.stopLoader();
				//this.getInvoice().id = r.id;
			});
		}
	}

	aonInvoice(type, invoice) {
		let aonInvoice = document.getElementById('aonInvoice');
		if(this.isMobile()) {
			aonInvoice.setContentHTML(invoice
				? `<aon-mobile-invoice invoice='${JSON.stringify(invoice)}'> </aon-mobile-invoice>`
				: `<aon-mobile-invoice type="${type}"> </aon-mobile-invoice>`);
		} else {
			aonInvoice.setContentHTML(invoice
				? `<aon-invoice invoice='${JSON.stringify(invoice)}'> </aon-invoice>`
				: `<aon-invoice type="${type}"> </aon-invoice>`);
		}
	}

}

window.customElements.define('aon-invoice-panel', AonInvoicePanel);
