import {AonElement} from '../../components/AonElement.js';
import {Paymethods} from '../../services/paymethod.js';
import {getInvoices} from '../../services/service.js';

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
		aonInvoiceTable.addColumn(MSG.AON_MSG_DATE, 'date', 'dateTable', '15%');
		aonInvoiceTable.addColumn(MSG.AON_MSG_INVOICE_NUMBER, 'string', 'reference', '20%');
		aonInvoiceTable.addColumn(MSG.AON_MSG_HOLDER, 'string', 'name', '35%');
		aonInvoiceTable.addColumn(MSG.AON_MSG_AMOUNT, 'number', 'total', '10');
		aonInvoiceTable.addColumn(MSG.AON_MSG_PAYMETHOD, 'string', 'paymethod', '15%');
		// INFO
		// aonInvoiceTable.addColumn('', '', '');
		this.init();
		aonInvoiceTable.addEventListener('more', () => {
			if(this.more)
				this.loadMore()
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
