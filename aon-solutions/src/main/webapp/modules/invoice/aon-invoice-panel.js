import './aon-invoice.js';
import './aon-invoice-list.js';

class AonInvoicePanel extends HTMLElement {

	selected;

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-application id="aonInvoice" title="Facturas"></aon-application>
		`;
		this.build();
	}

  build(){
		let aonInvoice = document.getElementById('aonInvoice');

		aonInvoice.addToolbarOption('Add', 'add', () => this.aonInvoice());

		let pendingOptions = [
			{
				name: 'Inbox',
				icon: 'inbox',
				fn: () => this.aonInvoiceList({status:'inbox'})
			},
			{
				name: 'Rechazadas',
				icon: 'report',
				fn: () => this.aonInvoiceList({status:'refused'})
			},
			{
				name: 'Papelera',
				icon: 'delete',
				fn: () => this.aonInvoiceList({status:'trash'})
			}
		];
		aonInvoice.addSidenavOptions('PENDIENTES', pendingOptions);

		let accountingOptions = [
			{
				name: 'Emitidas',
				icon: 'unarchive',
				fn: () => {}
			},
			{
				name: 'Recibidas',
				icon: 'archive',
				fn: () => {}
			},
			{
				name: 'Tickets/Justificantes',
				icon: 'receipt',
				fn: () => {}
			}
		];
		aonInvoice.addSidenavOptions('CONTABILIZADAS', accountingOptions);

		let settingOptions = [
			{
				name: 'Impresión Facturas',
				icon: 'print',
				fn: () => {}
			}
		];
		aonInvoice.addSidenavOptions('CONFIGURACIÓN', settingOptions);

		this.aonInvoiceList();
	}

	aonInvoiceList(filter) {
		let invoiceList = document.getElementById('aonInvoiceList');
		if(invoiceList) {
			invoiceList.setFilter(filter);
		} else {
			let aonInvoice = document.getElementById('aonInvoice');
			aonInvoice.removeToolbarOptions();
			aonInvoice.addToolbarOption('Add', 'add', () => this.aonInvoice());
			aonInvoice.setContentHTML(filter
				? `<aon-invoice-list id="aonInvoiceList" filter='${JSON.stringify(filter)}'></aon-invoice-list>`
				: `<aon-invoice-list id="aonInvoiceList"></aon-invoice-list>`);
		}
	}

	aonInvoice(invoice) {
		let aonInvoice = document.getElementById('aonInvoice');
		aonInvoice.setContentHTML(invoice
			? `<aon-invoice invoice='${JSON.stringify(invoice)}'> </aon-invoice>`
			: `<aon-invoice> </aon-invoice>`);
	}
}

window.customElements.define('aon-invoice-panel', AonInvoicePanel);
