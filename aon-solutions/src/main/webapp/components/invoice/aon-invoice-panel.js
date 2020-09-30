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

		aonInvoice.addToolbarOption('Add', 'add', () => {});

		let pendingOptions = [
			{
				name: 'Inbox',
				icon: 'inbox',
				fn: () => {}
			},
			{
				name: 'Rechazadas',
				icon: 'report',
				fn: () => {}
			},
			{
				name: 'Papelera',
				icon: 'delete',
				fn: () => {}
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

		aonInvoice.setContentHTML('<aon-invoice-list id="aonInvoiceList" ></aon-invoice-list>');

		let invoiceList = document.getElementById('aonInvoiceList');
		invoiceList.addEventListener('select', (e) => this.aonInvoice(e.detail));
		var d = new Date();
		var month = d.getMonth() + 1;
		var day = d.getDate();
		let curDate = d.getFullYear() + '-' + (month < 10 ? '0' : '') + month + '-' + (day < 10 ? '0' : '') + day;
		invoiceList.setAttribute('invoices', JSON.stringify( [
			{
        type: "Emitida",
        serie: 'A',
        number: 2,
        reference: 'A-2',
        date: curDate,
        total: 100,
        nif: '12345678G',
        name: 'AON SOLUTIONS SL.',
        address: {
            country: 'ES',
            address: '',
            zip: '',
            city: '',
            province: ''
        },
        category: '',
				transaction: 'NAC',
        taxes: [],
        details: [],
        finances: [],
        irpf: undefined,
        suplidos: false,
        totalSuplidos: 0
      }
		]));
	}

	aonInvoice(invoice) {
		let aonInvoice = document.getElementById('aonInvoice');
		aonInvoice.setContentHTML(`<aon-invoice invoice='${JSON.stringify(invoice)}'> </aon-invoice>`);
	}
}

window.customElements.define('aon-invoice-panel', AonInvoicePanel);
