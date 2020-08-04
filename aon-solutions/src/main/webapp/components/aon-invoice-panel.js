import './aon-invoice.js';
import './aon-invoice-list.js';

class AonInvoicePanel extends HTMLElement {
	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<!-- AON INVOICE PANEL TOOLBAR -->
			<aon-toolbar id="aonInvoicePanel" title="FACTURAS"></aon-toolbar>


			<!-- AON INVOICE PANEL MENU (SIDENAV) -->
			<div id="aonInvoicePanelSidenav" class="sidenav">
				<ul class="aonClip">
					<li id="aonInvoicePanelInbox" class="aonAppMenuSidenavList aonOpacity" >
						<i class="material-icons aonVerticalMiddle">inbox</i>
						<span class="aonMenuItemSpan"> Inbox </span>
					</li>

					<li id="aonInvoicePanelRefused" class="aonAppMenuSidenavList aonOpacity" >
						<i class="material-icons aonVerticalMiddle">report</i>
						<span class="aonMenuItemSpan"> Rechazadas	</span>
					</li>

					<li id="aonInvoicePanelTrash" class="aonAppMenuSidenavList aonOpacity" style="border-bottom: 1px solid #ddd;">
						<i class="material-icons aonVerticalMiddle">delete</i>
						<span class="aonMenuItemSpan"> Papelera </span>
					</li>

					<li id="aonInvoicePanelIssued" class="aonAppMenuSidenavList aonOpacity">
						<i class="material-icons aonVerticalMiddle">unarchive</i>
						<span class="aonMenuItemSpan"> Emitidas </span>
					</li>

					<li id="aonInvoicePanelReceived" class="aonAppMenuSidenavList aonOpacity" >
						<i class="material-icons aonVerticalMiddle">archive</i>
						<span class="aonMenuItemSpan"> Recibidas </span>
					</li>

					<li id="aonInvoicePanelTicket" class="aonAppMenuSidenavList aonOpacity" >
						<i class="material-icons aonVerticalMiddle">receipt</i>
						<span class="aonMenuItemSpan"> Tickets/Justificantes </span>
					</li>
				</ul>
			</div>

			<!-- AON INVOICE PANEL CONTENT -->
			<div id="aonInvoicePanelContent" class="aonContent">
				<aon-invoice-list id="aonInvoiceList" ></aon-invoice-list>
			</div>
		`;
		this.build();
  	}

  	build(){
			document.getElementById("aonInvoicePanelSidenav").style.width = "250px";
			document.getElementById("aonInvoicePanelContent").style['margin-left'] = "250px";

			let listIds = ['aonInvoicePanelInbox', 'aonInvoicePanelRefused', 'aonInvoicePanelTrash', 'aonInvoicePanelIssued', 'aonInvoicePanelReceived', 'aonInvoicePanelTicket']
			listIds.forEach((id, i) => {
					let el = document.getElementById(id);

					el.addEventListener('mouseover', () => {
						el.style.backgroundColor = '#f1f1f1';
					});
					el.addEventListener('mouseleave', () => {
						el.style.backgroundColor = 'white';
					});
					el.addEventListener('click', () => {
						el.style.backgroundColor = '#ddd';
					});
			});

			// let addButton = document.getElementById('aonConfigurationToolbarAddButton');
			// addButton.addEventListener('click', () => {
			//
			// });

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
		console.log(invoice);
			let invoiceContent = document.getElementById('aonInvoicePanelContent');
			invoiceContent.innerHTML = `<aon-invoice invoice='${JSON.stringify(invoice)}'> </aon-invoice>`;
		}
}

window.customElements.define('aon-invoice-panel', AonInvoicePanel);
