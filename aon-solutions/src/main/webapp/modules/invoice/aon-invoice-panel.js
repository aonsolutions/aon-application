import {AonElement} from '../../components/AonElement.js';

import './aon-invoice.js';
import './aon-invoice-mobile.js';
import './aon-invoice-list.js';


export class AonInvoicePanel extends AonElement {

	selected;

	get status() {
		return this.getAttribute('status');
	}

	set status(status) {
		this.setAttribute('status', status);
	}

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-application id="aonInvoice" title="Facturas"></aon-application>
			<aon-dialog id="aonDialogAddOption" type="menu" > </aon-dialog>
		`;
		this.build();
	}

  build(){
		let aonInvoice = document.getElementById('aonInvoice');

		aonInvoice.addToolbarOption('Add', 'add', () => this.addInvoice());
		aonInvoice.addToolbarOption('Upload', 'file_upload', () => this.addInvoiceFile());

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
		this.hasAttribute
		let filter = {status:'inbox'};

		if(this.hasAttribute('status')) {
			filter = {status: this.getAttribute('status')};
		}

		let toolbar = this.getElement(aonInvoice.TOOLBAR);
		if(filter.status === 'inbox') toolbar.setAttribute('option', 'Inbox');
		else if(filter.status === 'refused') toolbar.setAttribute('option', 'Rechazadas');

		this.aonInvoiceList(filter);
	}

	aonInvoiceList(filter) {
		let invoiceList = document.getElementById('aonInvoiceList');
		if(invoiceList) {
			invoiceList.setFilter(filter);
		} else {
			let aonInvoice = document.getElementById('aonInvoice');
			aonInvoice.removeToolbarOptions();
			aonInvoice.addToolbarOption('Add', 'add', () => this.addInvoice());
			aonInvoice.addToolbarOption('Upload', 'file_upload', () => this.addInvoiceFile());

			aonInvoice.setContentHTML(filter
				? `<aon-invoice-list id="aonInvoiceList" filter='${JSON.stringify(filter)}'></aon-invoice-list>`
				: `<aon-invoice-list id="aonInvoiceList"></aon-invoice-list>`);
		}
	}

	addInvoice() {
		let button = document.getElementById('aonInvoiceToolbarAddButton');

		const top  = button.getBoundingClientRect().top;
		const left = button.getBoundingClientRect().left;
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
		alert('upload Invoice');
	}

	aonInvoice(type, invoice) {
		let aonInvoice = document.getElementById('aonInvoice');
		if(this.isMobile()) {
			aonInvoice.setContentHTML(invoice
				? `<aon-invoice-mobile invoice='${JSON.stringify(invoice)}'> </aon-invoice-mobile>`
				: `<aon-invoice-mobile type="${type}"> </maon-invoice-mobile>`);
		} else {
			aonInvoice.removeToolbarOptions();
			aonInvoice.addToolbarOption('Add', 'add', () => this.addInvoice());
			aonInvoice.setContentHTML(invoice
				? `<aon-invoice invoice='${JSON.stringify(invoice)}'> </aon-invoice>`
				: `<aon-invoice type="${type}"> </aon-invoice>`);
		}
	}
}

window.customElements.define('aon-invoice-panel', AonInvoicePanel);
