import { AonElement } from '../../components/AonElement.js';
import { Paymethods } from '../../services/paymethod.js';
import { getInvoices, getInvoice, insertInvoice, deleteInvoices,
	 sendInvoiceMail, downloadInvoices, getUserAppRole } from '../../services/service.js';
import { Invoice } from './Invoice.js';

import {addInvoices, setInvoices, setIndex} from './InvoiceCache.js';

import '../../components/aon-table.js';

import { CONSTANT, MSG } from '../../environments/environments.js';

import * as ACTION from '../actions.js';

export class AonInvoiceList extends AonElement {

	more;
	_roles;

	static get observedAttributes() {
		return [CONSTANT.FILTER];
	}

	get filter() {
    return this.getAttribute(CONSTANT.FILTER);
  }

  set filter(filter) {
    this.setAttribute(CONSTANT.FILTER, filter);
  }

	attributeChangedCallback(name, oldValue, newValue) {
		if(CONSTANT.FILTER === name) {

		}
	}

	constructor () {
		super();
		this.more = true;
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-table id='aonInvoiceTable' selectable='true'></aon-table>
			`;
		getUserAppRole().then(roles => {
			this._roles = roles;
			this.build();
		});
 	}

 	build() {
		let aonInvoiceTable = document.getElementById('aonInvoiceTable');
		aonInvoiceTable.addColumn(MSG.AON_MSG_DATE, 'date', 'dateTable', '10%');
		aonInvoiceTable.addColumn(MSG.AON_MSG_INVOICE_NUMBER, 'string', 'reference', '25%');
		aonInvoiceTable.addColumn(MSG.AON_MSG_HOLDER, 'string', 'name', '35%');
		aonInvoiceTable.addColumn(MSG.AON_MSG_AMOUNT, 'number', 'total', '10%');
		aonInvoiceTable.addColumn(MSG.AON_MSG_PAYMETHOD, 'string', 'paymethod', '15%');
		// INFO
		// aonInvoiceTable.addColumn('', '', '');
		this.init();
		aonInvoiceTable.addEventListener('more', () => {
			if(this.more)
				this.loadMore();
		});

		aonInvoiceTable.addEventListener('select', () => {
			if(aonInvoiceTable.selected.length === 1) {
				this.addInvoiceActions();
			} else if(aonInvoiceTable.selected.length === 0){
				this.removeInvoiceActions();
			}
		});
	}

	loadMore() {
		let aonInvoiceTable = document.getElementById('aonInvoiceTable');
		let filter = this.getFilter();

		if(aonInvoiceTable && filter.page && filter.status === 'accounting') {
			filter.page = filter.page + 1;
			this.setFilter(filter);
			getInvoices(filter).then(invoices => {
				if(invoices.length == 0)
					this.more = false;
				addInvoices(invoices);
				invoices.forEach((invoice, i) => {
					if(!invoice.name){
						invoice.name = invoice.type === 'emitida'
							? (invoice.receiver ? invoice.receiver.name : '')
							: (invoice.sender ? invoice.sender.name : '');
					}
					invoice.paymethod = invoice.finances && invoice.finances.length > 0
						? this.getPaymethod(invoice.finances[0].paymethod) : '';
					let date = new Date(invoice.date);
					let day = date.getDate();
					let month = date.getMonth() + 1;
					let year = date.getFullYear();
					invoice.dateTable = day + '/' + month + '/' + year;
					aonInvoiceTable.addRow(invoice, () => this.aonInvoice(invoice, i), (e) => this.aonInvoiceContextMenu(e, invoice, i));
				});
			});
		}
	}

	init() {
		this.more = true;
		let aonInvoiceTable = document.getElementById('aonInvoiceTable');
		if(aonInvoiceTable) {
			getInvoices(this.getFilter()).then(invoices => {
				setInvoices(invoices);
				aonInvoiceTable.removeRows();
				aonInvoiceTable.selected = [];
				this.removeInvoiceActions();
				invoices.forEach((invoice, i) => {
					if(!invoice.name){
						invoice.name = invoice.type === 'emitida'
							? (invoice.receiver ? invoice.receiver.name : '')
							: (invoice.sender ? invoice.sender.name : '');
					}
					invoice.paymethod = invoice.finances && invoice.finances.length > 0
						? this.getPaymethod(invoice.finances[0].paymethod) : '';
					let date = new Date(invoice.date);
					let day = date.getDate();
					let month = date.getMonth() + 1;
					let year = date.getFullYear();
					invoice.dateTable = day + '/' + month + '/' + year;
					aonInvoiceTable.addRow(invoice, () => this.aonInvoice(invoice, i), (e) => this.aonInvoiceContextMenu(e, invoice, i));
				});
			});
		}
	}

	addInvoiceActions() {
		this.removeInvoiceActions();
		let aonInvoice = this.getElement('aonInvoice');
		let toolbar = this.getElement(aonInvoice.TOOLBAR);
		toolbar.addSeparator();
		if(this.getFilter().status === 'inbox') {
			aonInvoice.addToolbarOption2(ACTION.DELETE_TO_TRASH, () => this.deleteInvoices());
			aonInvoice.addToolbarOption2(ACTION.REJECT_INVOICE, () => this.rejectInvoices());
			toolbar.addSeparator();
			aonInvoice.addToolbarOption2(ACTION.DOWNLOAD_INVOICE, () => this.downloadInvoices());
			aonInvoice.addToolbarOption2(ACTION.SEND_INVOICE, () => this.sendInvoices());
		} else if(this.getFilter().status === 'refused' || this.getFilter().status === 'rejected'){
			aonInvoice.addToolbarOption2(ACTION.DELETE_TO_TRASH, () => this.deleteInvoices());
			aonInvoice.addToolbarOption2(ACTION.RESTORE_INVOICE, () => this.restoreInvoices());
		} else if(this.getFilter().status === 'trash' || this.getFilter().status === 'draft'){
			aonInvoice.addToolbarOption2(ACTION.DELETE_FOREVER, () => this.deleteForeverInvoices());
			aonInvoice.addToolbarOption2(ACTION.RESTORE_INVOICE, () => this.restoreInvoices());
		} else if(this.getFilter().status === 'accounting'){
			aonInvoice.addToolbarOption2(ACTION.DOWNLOAD_INVOICE, () => this.downloadInvoices());
			aonInvoice.addToolbarOption2(ACTION.SEND_INVOICE, () => this.sendInvoices());
		}
	}

	deleteInvoices() {
		let cont = 0;
		let aonInvoiceTable = document.getElementById('aonInvoiceTable');
		aonInvoiceTable.selected.forEach((invoice, i) => {
			invoice.status = CONSTANT.TRASH;
			insertInvoice(invoice).then(() => {
				cont = cont + 1;
				if(cont === aonInvoiceTable.selected.length) {
					this.init();
				}
			});
		});
	}

	deleteForeverInvoices() {
		let aonInvoiceTable = document.getElementById('aonInvoiceTable');
		let aonInvoice = this.getElement('aonInvoice');
		let d = document.getElementById(aonInvoice.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.AON_MSG_DELETE_FOREVER);
		d.setContentHTML('Estás seguro de eliminar las facturas seleccionadas');
		d.addAcceptAction(() => deleteInvoices(aonInvoiceTable.selected.map(r => r.id)).then(() => this.init()));
		d.open();
	}

	rejectInvoices() {
		let cont = 0;
		let aonInvoiceTable = document.getElementById('aonInvoiceTable');
		aonInvoiceTable.selected.forEach((invoice, i) => {
			invoice.status = CONSTANT.REFUSED;
			insertInvoice(invoice).then(() => {
				cont = cont + 1;
				if(cont === aonInvoiceTable.selected.length) {
					this.init();
				}
			});
		});
	}

	downloadInvoices() {
		let aonInvoiceTable = document.getElementById('aonInvoiceTable');

		let data = {
			domain_id: localStorage.getItem('aon_domain_id'),
			domain_name: localStorage.getItem('aon_domain_name'),
			domain_login: localStorage.getItem('aon_domain_login'),
			ids: aonInvoiceTable.selected.map(r => r.id),
			status: this.getFilter().status
		};
		let json = btoa(JSON.stringify(data));
		downloadInvoices(json);
	}

	sendInvoices() {
		let aonInvoice = this.getElement('aonInvoice');
		let d = document.getElementById(aonInvoice.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.AON_MSG_SEND_INVOICES);
		d.setContentHTML('<aon-input id="sendInvoicesMail" description="Email"></aon-input>');
		d.addAcceptAction(() => {
			let mail = this.getElement('sendInvoicesMail');
			let aonInvoiceTable = document.getElementById('aonInvoiceTable');
			let message = {
				to: mail.value,
				invoices: aonInvoiceTable.selected
			};
			sendInvoiceMail(message).then(() => {});
		});
		d.open();
	}

	restoreInvoices() {
		let cont = 0;
		let aonInvoiceTable = document.getElementById('aonInvoiceTable');
		aonInvoiceTable.selected.forEach((invoice, i) => {
			invoice.status = CONSTANT.INBOX;
			insertInvoice(invoice).then(() => {
				cont = cont + 1;
				if(cont === aonInvoiceTable.selected.length) {
					this.init();
				}
			});
		});
	}

	removeInvoiceActions() {
		let aonInvoice = this.getElement('aonInvoice');
		let toolbar = this.getElement(aonInvoice.TOOLBAR);
		toolbar.removeSeparators();
		aonInvoice.removeToolbarOption(ACTION.DELETE_TO_TRASH);
		aonInvoice.removeToolbarOption(ACTION.REJECT_INVOICE);
		aonInvoice.removeToolbarOption(ACTION.RESTORE_INVOICE);
		aonInvoice.removeToolbarOption(ACTION.DELETE_FOREVER);
		aonInvoice.removeToolbarOption(ACTION.DOWNLOAD_INVOICE);
		aonInvoice.removeToolbarOption(ACTION.SEND_INVOICE);
	}

	getPaymethod(paymethod) {
		let value = '';
		Paymethods.forEach((item, i) => {
			if(item.value === paymethod) {
				value = item.name;
			}
		});
		return value;
	}

	aonInvoice(invoice, i) {
		if(this.getFilter().status !== 'accounting') {
			setIndex(i);
			let aip = document.querySelector('aon-invoice-panel');
			aip.aonInvoice(invoice.type, invoice);
		} else {
			getInvoice(invoice.id).then((inv) => {
				setIndex(i);
				let aip = document.querySelector('aon-invoice-panel');
				aip.aonInvoice(invoice.type, inv);
			});
		}
	}

	aonInvoiceContextMenu(e, invoice, i) {
		let inv =	new Invoice(invoice.type);
		inv.createInvoice(invoice);

		e.preventDefault();
		let rect = e.target.getBoundingClientRect();
    	let x = e.clientX - rect.left;
		let y = e.clientY - rect.top;

		const top  = rect.top + y;
	  	const left = rect.left + x;

    	let aonInvoice = this.getElement('aonInvoice');
   		let d = document.getElementById(aonInvoice.OPTION_DIALOG);

		let send = ACTION.SEND_INVOICE;
		send.fn = () => {}; //this.recordInvoice();

		let download = ACTION.DOWNLOAD_INVOICE;
	    download.fn = () => {}; //this.recordInvoice();

    	let record = ACTION.RECORD_INVOICE;
    	record.fn = () => {}; //this.recordInvoice();

    	let reject = ACTION.REJECT_INVOICE;
    	reject.fn = () => {}; //this.rejectInvoice();

    	let restore = ACTION.RESTORE_INVOICE;
    	restore.fn = () => {}; //this.restoreInvoice();

    	let addComment = ACTION.COMMENT;
   		addComment.fn = () => {}; //this.addInvoiceComment();

  		let deleteInvoice = ACTION.DELETE_TO_TRASH;
	  	deleteInvoice.fn = () => {}; //this.trashInvoice();

	  	let deleteForever = ACTION.DELETE_FOREVER;
	  	deleteForever.fn = () => {}; //this.removeInvoice();

	  	let rectify = ACTION.RECTIFY_INVOICE;
	  	rectify.fn = () => {}; //this.rectifyInvoice();

	  	let duplicate = ACTION.DUPLICATE_INVOICE;
	  	duplicate.fn = () => {}; //this.duplicateInvoice();

    	let addFile = ACTION.ADD_FILE;
  		addFile.fn = () => {}; //this.addInvoiceFile();

	  	let actions = [];
	  	if(inv.isRejected()) {
	  		actions = [restore, deleteInvoice];
	  	} else if(inv.isDraft()) {
	  		actions = [restore, deleteForever];
	  	}  else if(inv.isInbox()){
	  		if(this._roles.includes('ADMIN') || this._roles.includes('INVOICE_MANAGER')){
	    		actions = [send, download, addComment, deleteInvoice, reject, record, rectify, duplicate];
	  		} else {
	    	  actions = [send, download, addComment, deleteInvoice, rectify, duplicate];
	    	}
		}
	  	if(!inv.file && !inv.isEmitida()){
	  		actions.push(addFile);
	  	}

	  	d.setMenuOptions(actions, top, left);
	  	d.open();
	}

	getFilter() {
		return this.hasAttribute('filter')
 			? JSON.parse(this.getAttribute('filter'))
			: {status: 'inbox'};
	}

	setFilter(filter) {
		return this.setAttribute('filter', JSON.stringify(filter));
	}
}
window.customElements.define('aon-invoice-list', AonInvoiceList);
