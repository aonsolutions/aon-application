import {AonElement} from '../../components/AonElement.js';
import {Paymethods} from '../../services/paymethod.js';
import {getInvoices} from '../../services/service.js';
import '../../components/aon-table.js';

import * as CONSTANT from "../../environments/constants.js";
import * as MSG from "../../environments/msg.js";

export class AonInvoiceList extends AonElement {

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
			this.init();
		}
	}

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-table id='aonInvoiceTable' selectable='true'></aon-table>
			`;
		this.build();
 	}

 	build() {
		let aonInvoiceTable = document.getElementById('aonInvoiceTable');
		aonInvoiceTable.addColumn(MSG.AON_MSG_DATE, 'date', 'dateTable');
		aonInvoiceTable.addColumn(MSG.AON_MSG_INVOICE_NUMBER, 'string', 'reference');
		aonInvoiceTable.addColumn(MSG.AON_MSG_HOLDER, 'string', 'name');
		aonInvoiceTable.addColumn(MSG.AON_MSG_AMOUNT, 'number', 'total');
		aonInvoiceTable.addColumn(MSG.AON_MSG_PAYMETHOD, 'string', 'paymethod');
		// INFO
		// aonInvoiceTable.addColumn('', '', '');
		this.init();
	}

	init() {
		let aonInvoiceTable = document.getElementById('aonInvoiceTable');
		if(aonInvoiceTable) {
			getInvoices(this.getFilter()).then(invoices => {
				aonInvoiceTable.removeRows();
				invoices.forEach((invoice, i) => {
					invoice.name = invoice.type === 'emitida'
						? (invoice.receiver ? invoice.receiver.name : '')
						: (invoice.sender ? invoice.sender.name : '');
					invoice.paymethod = invoice.finances && invoice.finances.length > 0
						? this.getPaymethod(invoice.finances[0].paymethod) : '';
					let date = new Date(invoice.date);
					let day = date.getDate();
					let month = date.getMonth() + 1;
					let year = date.getFullYear();
					invoice.dateTable = day + '/' + month + '/' + year;
					aonInvoiceTable.addRow(invoice, () => this.aonInvoice(invoice));
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
	aonInvoice(invoice) {
		let aip = document.querySelector('aon-invoice-panel');
		aip.aonInvoice(invoice.type, invoice);
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
