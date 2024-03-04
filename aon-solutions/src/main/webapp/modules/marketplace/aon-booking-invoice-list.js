import { AonElement } from '../../components/AonElement.js';
import { getInvoices, getSigInvoices } from '../../services/service.js';


import {addInvoices, setInvoices, setIndex} from '../invoice/InvoiceCache.js';

import '../../components/aon-table.js';

import { CONSTANT, MSG } from '../../environments/environments.js';

import { formatNumber } from '../../services/utils.js';
import * as LS from '../../services/localStorageService.js';
import { INVOICE } from '../../services/app.js';
import { AonTable } from '../../components/aon-table.js';

export class AonBookingInvoiceList extends AonElement {

	more;
	dur;


	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
		this.build();
 	}

	initialize() {
		this.more = true;
		this.filter = this.filter || {
			registry: this.dur.domain.aonCustomer,
			status:'accounting',
			type:'sales',
			page:1, 
			per_page: 50
		};
	}

	getDur() {
		return this.dur;
	}

	setDur(dur) {
		this.dur = dur;
	}

 	build() {
		let aonInvoiceTable =  new AonTable();
		aonInvoiceTable.id = 'aonInvoiceTable';
		aonInvoiceTable.selectable = 'true';
		aonInvoiceTable.setApp(INVOICE);
		this.appendChild(aonInvoiceTable);
		aonInvoiceTable.addColumn(MSG.DATE, 'date', 'dateTable', '10%');
		aonInvoiceTable.addColumn(MSG.INVOICE_NUMBER, 'string', 'reference', '35%');
		aonInvoiceTable.addColumn(MSG.HOLDER, 'string', 'name', '35%');
		aonInvoiceTable.addColumn(MSG.AMOUNT, 'number', 'totalParse', '10%');

		// INFO
		// aonInvoiceTable.addColumn('', '', '');
		this.init();
		aonInvoiceTable.addEventListener('more', () => {
			if(this.more)
				this.loadMore();
		});


	}

	loadMore() {
		let aonInvoiceTable = document.getElementById('aonInvoiceTable');
		let filter = this.getFilter();
		
		if(aonInvoiceTable && filter.page && filter.status === 'accounting') {
			filter.page = filter.page + 1;
			this.setFilter(filter);
			getSigInvoices(filter).then(invoices => {
				if(invoices.length == 0)
					this.more = false;
				addInvoices(invoices);
				invoices.forEach((invoice, i) => {
					if(!invoice.name){
						invoice.name = invoice.type === 'emitida'
							? (invoice.receiver ? invoice.receiver.name : '')
							: (invoice.sender ? invoice.sender.name : '');
					}

					let date = new Date(invoice.date);
					let day = date.getDate();
					let month = date.getMonth() + 1;
					let year = date.getFullYear();
					invoice.dateTable = day + '/' + month + '/' + year;
					invoice.totalParse = formatNumber(invoice.total, 2, "EUR");
					aonInvoiceTable.addRow(invoice, () => {}, (e) => {});
				});
			});
		}
	}

	init() {
		this.more = true;
		let aonInvoiceTable = document.getElementById('aonInvoiceTable');
		if(aonInvoiceTable) {
			getSigInvoices(this.getFilter()).then(invoices => {
					if(!this.getDur().isInvoiceManager() && !this.getDur().isInvoicePortal()) {
						invoices = invoices.filter(f => f.creation_user === LS.getDomainLogin());
					} 

					if(this.getFilter().status === CONSTANT.INBOX && this.getFilter().type) {
						invoices = invoices.filter(f => f.type === this.getFilter().type);
					}
					invoices = invoices.sort((a, b) => new Date(b.date) - new Date(a.date));

					setInvoices(invoices);
				
					aonInvoiceTable.removeRows();
					aonInvoiceTable.selected = [];
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
						invoice.totalParse = formatNumber(invoice.total, 2, "EUR");

						aonInvoiceTable.addRow(invoice, () => {}, (e) => {});
					});
			});
			
		}
	}


	getFilter() {
		return this.filter;
	}

	setFilter(filter) {
		this.filter = filter;
	}
}
window.customElements.define('aon-booking-invoice-list', AonBookingInvoiceList);
