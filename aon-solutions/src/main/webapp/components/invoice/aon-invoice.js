import {Transactions} from '../../services/transaction.js';
import {Paymethods} from '../../services/paymethod.js';
import {getInvoiceCategories} from '../../services/invoiceCategory.js';
import {insertInvoice} from '../../services/service.js';

import '../aon-card.js';
import '../aon-input-text.js';
import '../aon-select.js';
import '../aon-address.js';
import '../aon-checkbox.js';

(function() {


	class AonInvoice extends HTMLElement {

		_invoice;

    static get observedAttributes() {
      return ['invoice'];
   	}

    get invoice() {
   	  return JSON.parse(this.getAttribute('invoice'));
   	}

	  set invoice(value) {
	     this.setAttribute('invoice', JSON.stringify(value));
    }

    attributeChangedCallback(name, oldValue, newValue) {
      // TODO GUARDAR INVOICE...
    }

    constructor () {
      super();

      if(!this.hasAttribute('invoice')) {
				let inv = this.newInvoice();
				this.setAttribute('invoice', JSON.stringify(inv));
				this._invoice = inv;
			} else {
				let inv = JSON.parse(this.getAttribute('invoice'));
				this._invoice = inv;
			}
    }

		connectedCallback () {
			this.innerHTML = `
				<div style="display:flex;">
					<aon-card id="aonInvoiceItemDataCard" title="Datos Factura" style="width:50%;"> </aon-card>
					<div style="width:50%;">
						<aon-card id="aonInvoiceItemTaxesCard" title="Detalle Impuestos"> </aon-card>
						<aon-card id="aonInvoiceItemIRPFCard"> </aon-card>
					</div>
				</div>
				<aon-card id="aonInvoiceItemDetailCard" title="Conceptos Factura"> </aon-card>
				<aon-card id="aonInvoiceItemFinanceCard" title="Vencimientos"> </aon-card>
			`;

			this.build();
	  }

		save(invoice) {
			insertInvoice(invoice ? invoice : this._invoice).then(() => alert('ok'));
		}

		update(param, value) {
			if(!value) {
				value = document.getElementById(param).value;
			}
		 	this._invoice[param] = value;
			this.save();
		}

		build() {
			this.buildData();
			this.buildTaxes();
			this.buildIRPF();
			this.buildDetail();
			this.buildFinance();
		}


		buildData(){
			let card = document.getElementById('aonInvoiceItemDataCard');
			let table = document.createElement('table');
			table.style.width = '100%';
			card.setContent(table);

			let tr = document.createElement('tr');
			table.appendChild(tr);

			// SERIE
			let tdSerie = document.createElement('td');
			tdSerie.innerHTML = `<aon-input-text id="serie" description="Serie"></aon-input-text>`;
			tr.appendChild(tdSerie);
			let series = document.getElementById('serie');
			series.value = this._invoice.serie;
			series.addEventListener('change', () => this.update('serie'));

			// NUMBER
			let tdNumber = document.createElement('td');
			tdNumber.innerHTML = `<aon-input-text id="number" description="Número"></aon-input-text>`;
			tr.appendChild(tdNumber);
			let number = document.getElementById('number');
			number.value = this._invoice.number;
			number.addEventListener('change', () => this.update('number'));

			// DATE
			let tdDate = document.createElement('td');
			tdDate.innerHTML = `<aon-input-text id="date" type="date" description="Fecha"></aon-input-text>`;
			tr.appendChild(tdDate);
			let date = document.getElementById('date');
			date.value = this._invoice.date;
			date.addEventListener('change', () => this.update('date'));

			// TOTAL
			let tdTotal = document.createElement('td');
			tdTotal.innerHTML = `<aon-input-text id="total" description="Total"></aon-input-text>`;
			tr.appendChild(tdTotal);
			let total = document.getElementById('total');
			total.value = this._invoice.total;
			total.addEventListener('change', () => this.updateTotal());

			let tr2 = document.createElement('tr');
			table.appendChild(tr2);

			// NIF
			let tdNif = document.createElement('td');
			tdNif.innerHTML = `<aon-input-text id="nif" description="NIF"></aon-input-text>`;
			tr2.appendChild(tdNif);
			let nif = document.getElementById('nif');
			nif.value = this._invoice.nif;
			nif.addEventListener('change', () => this.update('nif'));

			// NAME
			let tdName = document.createElement('td');
			tdName.setAttribute('colspan','3');
			tdName.innerHTML = `<aon-input-text id="name" description="Razón Social"></aon-input-text>`;
			tr2.appendChild(tdName);
			let name = document.getElementById('name');
			name.value = this._invoice.name;
			name.addEventListener('change', () => this.updateNif('name'));

			let tr3 = document.createElement('tr');
			table.appendChild(tr3);

			// ADDRESS
			let tdAddress = document.createElement('td');
			tdAddress.setAttribute('colspan', '4');
			tdAddress.innerHTML = `<aon-address id="address" description="Dirección"></aon-address>`;
			tr3.appendChild(tdAddress);
			let address = document.getElementById('address');
			address.value = JSON.stringify(this._invoice.address);
			address.addEventListener('change', () => this.updateAddress());


			let tr4 = document.createElement('tr');
			table.appendChild(tr4);

			// CATEGORY
			let tdCategory = document.createElement('td');
			tdCategory.setAttribute('colspan', '2');
			tdCategory.innerHTML = `<aon-select id="category" description="Categoría"></aon-select>`;
			tr4.appendChild(tdCategory);
			let category = document.getElementById('category');
			category.options = JSON.stringify(getInvoiceCategories(this._invoice.type));
			category.value = this._invoice.category;
			category.addEventListener('select', () => this.update('category'));

			// PAYMETHOD
			let tdPaymethod = document.createElement('td');
			tdPaymethod.setAttribute('colspan', '2');
			tdPaymethod.innerHTML = `<aon-select id="pay_method" description="Forma de Pago"></aon-select>`;
			tr4.appendChild(tdPaymethod);
			let paymethod = document.getElementById('pay_method');
			paymethod.options = JSON.stringify(Paymethods);
			// TODO: VALUE
			paymethod.addEventListener('select', () => ticketPaymethod(document.getElementById('pay_method')));



		}

		buildTaxes(){
			let card = document.getElementById('aonInvoiceItemTaxesCard');
			let div = document.createElement('div');
			card.setContent(div);

			let table = document.createElement('table');
			table.style.width = '100%';
			div.appendChild(table);

			let tr1 = document.createElement('tr');
			table.appendChild(tr1);

			// TRANSACTION
			let tdTransaction = document.createElement('td');
			tdTransaction.setAttribute('colspan', '4');
			tdTransaction.innerHTML = `<aon-select id="transaction" description="Tipo Transacción"></aon-select>`;
			tr1.appendChild(tdTransaction);
			let transaction = document.getElementById('transaction');
			transaction.options = JSON.stringify(Transactions);
			transaction.value = this._invoice.transaction;
			transaction.addEventListener('select', () => this.update('transaction'));

			let addDiv = document.createElement('div');
			div.appendChild(addDiv);

			addDiv.innerHTML = `<aon-icon-button id="aonInvoiceItemTaxesCardAddButton" icon="add"> </aon-icon-button>`;

			let addButton = document.getElementById('aonInvoiceItemTaxesCardAddButton');
			addButton.addEventListener('click', () => {
				alert('ADD TAX');
			});
		}

		buildIRPF(){
			let card = document.getElementById('aonInvoiceItemIRPFCard');

			let table = document.createElement('table');
			table.style.width = '100%';
			card.setContent(table);

			let tr1 = document.createElement('tr');
			table.appendChild(tr1);

			// IRPF
			let tdIRPF = document.createElement('td');
			tdIRPF.setAttribute('colspan', '2');
			tdIRPF.innerHTML = `<aon-checkbox id="irpfCheckbox" description="IRPF"></aon-checkbox>`;
			tr1.appendChild(tdIRPF);

			// SUPLIDOS
			let tdSuplidos = document.createElement('td');
			tdSuplidos.innerHTML = `<aon-checkbox id="suplidosCheckbox" description="Suplidos"></aon-checkbox>`;
			tr1.appendChild(tdSuplidos);

			// TOTAL SUPLIDOS
			let tdTotalSuplidos = document.createElement('td');
			tdTotalSuplidos.innerHTML = `<aon-input-text id="t-suplidos" description="Total Suplidos" visible="false"></aon-input-text>`;
			tr1.appendChild(tdTotalSuplidos);

		}

		buildDetail(){
			let card = document.getElementById('aonInvoiceItemDetailCard');
			let div = document.createElement('div');
			card.setContent(div);

			let table = document.createElement('table');
			table.style.width = '100%';
			div.appendChild(table);

			let tr1 = document.createElement('tr');
			table.appendChild(tr1);

			let addDiv = document.createElement('div');
			div.appendChild(addDiv);

			addDiv.innerHTML = `<aon-icon-button id="aonInvoiceItemDetailCardAddButton" icon="add"> </aon-icon-button>`;

			let addButton = document.getElementById('aonInvoiceItemDetailCardAddButton');
			addButton.addEventListener('click', () => {
				alert('ADD DETAIL');
			});
		}

		buildFinance(){
			let card = document.getElementById('aonInvoiceItemFinanceCard');
			let div = document.createElement('div');
			card.setContent(div);

			let table = document.createElement('table');
			table.style.width = '100%';
			div.appendChild(table);

			let tr1 = document.createElement('tr');
			table.appendChild(tr1);

			let addDiv = document.createElement('div');
			div.appendChild(addDiv);

			addDiv.innerHTML = `<aon-icon-button id="aonInvoiceItemFinanceCardAddButton" icon="add"> </aon-icon-button>`;

			let addButton = document.getElementById('aonInvoiceItemFinanceCardAddButton');
			addButton.addEventListener('click', () => {
				alert('ADD FINANCE');
			});
		}

		newInvoice() {
			var d = new Date();
			var month = d.getMonth() + 1;
			var day = d.getDate();
			let curDate = d.getFullYear() + '-' + (month < 10 ? '0' : '') + month + '-' + (day < 10 ? '0' : '') + day;
			let inv = {
				type: "Emitida",
				serie: '',
				number: 0,
				reference: '',
				date: curDate,
				total: 0,
				nif: '',
				name: '',
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

			};
			return inv;
		}
	}

	window.customElements.define('aon-invoice',  AonInvoice);

})();
