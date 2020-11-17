import {AonElement} from '../../components/AonElement.js';
import {Paymethods} from '../../services/paymethod.js';
import {getInvoices} from '../../services/service.js';
import '../../components/aon-table.js';

export class AonInvoiceList extends AonElement {

	static get observedAttributes() {
		return ['filter'];
	}

	get filter() {
    return this.getAttribute('filter');
  }

  set filter(filter) {
    this.setAttribute('filter', filter);
  }

	attributeChangedCallback(name, oldValue, newValue) {
		if('filter' === name) {
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
		aonInvoiceTable.addColumn('Fecha', 'date', 'dateTable');
		aonInvoiceTable.addColumn('Nº Factura', 'string', 'reference');
		aonInvoiceTable.addColumn('Titular', 'string', 'name');
		aonInvoiceTable.addColumn('Importe', 'number', 'total');
		aonInvoiceTable.addColumn('Forma de Pago', 'string', 'paymethod');
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
						? invoice.receiver.name
						: invoice.sender.name;
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
