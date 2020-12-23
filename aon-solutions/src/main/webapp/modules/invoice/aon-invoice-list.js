import {AonElement} from '../../components/AonElement.js';
import {Paymethods} from '../../services/paymethod.js';
import {getInvoices, getInvoice, insertInvoice, deleteInvoices, sendInvoiceMail, downloadInvoices} from '../../services/service.js';
import {InvoiceAction} from './invoiceEnums.js';

import {addInvoices, setInvoices, setIndex} from './InvoiceCache.js';

import '../../components/aon-table.js';

import * as CONSTANT from "../../environments/constants.js";
import * as MSG from "../../environments/msg.js";

export class AonInvoiceList extends AonElement {

	more;

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
		this.build();
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
					aonInvoiceTable.addRow(invoice, () => this.aonInvoice(invoice, i));
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
					aonInvoiceTable.addRow(invoice, () => this.aonInvoice(invoice, i));
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
			aonInvoice.addToolbarOption2(InvoiceAction.DELETE, () => this.deleteInvoices());
			aonInvoice.addToolbarOption2(InvoiceAction.REJECT, () => this.rejectInvoices());
			toolbar.addSeparator();
			aonInvoice.addToolbarOption2(InvoiceAction.DOWNLOAD, () => this.downloadInvoices());
			aonInvoice.addToolbarOption2(InvoiceAction.SEND, () => this.sendInvoices());
		} else if(this.getFilter().status === 'refused' || this.getFilter().status === 'rejected'){
			aonInvoice.addToolbarOption2(InvoiceAction.DELETE, () => this.deleteInvoices());
			aonInvoice.addToolbarOption2(InvoiceAction.RESTORE, () => this.restoreInvoices());
		} else if(this.getFilter().status === 'trash' || this.getFilter().status === 'draft'){
			aonInvoice.addToolbarOption2(InvoiceAction.DELETE_FOREVER, () => this.deleteForeverInvoices());
			aonInvoice.addToolbarOption2(InvoiceAction.RESTORE, () => this.restoreInvoices());
		} else if(this.getFilter().status === 'accounting'){
			aonInvoice.addToolbarOption2(InvoiceAction.DOWNLOAD, () => this.downloadInvoices());
			aonInvoice.addToolbarOption2(InvoiceAction.SEND, () => this.sendInvoices());
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
		aonInvoice.removeToolbarOption(InvoiceAction.DELETE);
		aonInvoice.removeToolbarOption(InvoiceAction.REJECT);
		aonInvoice.removeToolbarOption(InvoiceAction.RESTORE);
		aonInvoice.removeToolbarOption(InvoiceAction.DELETE_FOREVER);
		aonInvoice.removeToolbarOption(InvoiceAction.DOWNLOAD);
		aonInvoice.removeToolbarOption(InvoiceAction.SEND);
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
