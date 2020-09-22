(function() {

	const html = `
	<table class="mdl-data-table mdl-js-data-table mdl-data-table--selectable mdl-shadow--2dp aon-table" style="width:100%;">
	  <thead>
	    <tr>
	      <th class="mdl-data-table__cell--non-numeric">Fecha</th>
	      <th class="mdl-data-table__cell--non-numeric">Nº Factura</th>
	      <th class="mdl-data-table__cell--non-numeric">Titular</th>
	      <th>Importe</th>
	      <th class="mdl-data-table__cell--non-numeric">Forma de Pago</th>
	      <th class="mdl-data-table__cell--non-numeric"></th>
	    </tr>
	  </thead>
	  <tbody id="invoice-tbody">

	  </tbody>
	</table>
	`;

class AonInvoiceList extends HTMLElement {

	_invoices;

	static get observedAttributes() {
		return ['invoices'];
	}

	get invoices() {
		return JSON.parse(this.getAttribute('invoices'));
	}

	set invoices(value) {
		 this.setAttribute('invoices', JSON.stringify(value));
	}

	attributeChangedCallback(name, oldValue, newValue) {
		if('invoices' === name) {
				let tbody = document.getElementById('invoice-tbody');
				this._invoices = JSON.parse(newValue);
				tbody.innerHTML = '';
				this.build();
		}
	}

	constructor () {
		super();
		if(this.hasAttribute('invoices')) {
			this._invoices =  JSON.parse(this.getAttribute('invoices'));
		} else {
			this._invoices = [];
		}
	}

	connectedCallback () {
		this.innerHTML = html;
		this.build();
 	}

 	build() {
		let tbody = document.getElementById('invoice-tbody');
		for(let i = 0; i < this._invoices.length; i++) {
			let invoice = this._invoices[i];
			var event = new CustomEvent('select', { 'detail': invoice });
			let tr = document.createElement('tr');
			tr.style.cursor = 'pointer';

			let td1 = document.createElement('td');
			td1.className = 'mdl-data-table__cell--non-numeric';
			td1.innerHTML = invoice.date;
			td1.addEventListener('click', () => this.dispatchEvent(event));

			let td2 = document.createElement('td');
			td2.className = 'mdl-data-table__cell--non-numeric';
			td2.innerHTML = invoice.reference;
			td2.addEventListener('click', () => this.dispatchEvent(event));

			let td3 = document.createElement('td');
			td3.className = 'mdl-data-table__cell--non-numeric';
			td3.innerHTML = invoice.name;
			td3.addEventListener('click', () => this.dispatchEvent(event));

			let td4 = document.createElement('td');
			td4.innerHTML = invoice.total;
			td4.addEventListener('click', () => this.dispatchEvent(event));

			// TODO:
			let td5 = document.createElement('td');
			td5.className = 'mdl-data-table__cell--non-numeric';
			td5.innerHTML = 'F. PAGO';
			td5.addEventListener('click', () => this.dispatchEvent(event));

			// TODO:
			let td6 = document.createElement('td');
			td6.className = 'mdl-data-table__cell--non-numeric';
			td6.innerHTML = 'INFO';
			td6.addEventListener('click', () => this.dispatchEvent(event));

			tr.appendChild(td1);
			tr.appendChild(td2);
			tr.appendChild(td3);
			tr.appendChild(td4);
			tr.appendChild(td5);
			tr.appendChild(td6);

			tbody.appendChild(tr);
		}

	}

}
window.customElements.define('aon-invoice-list', AonInvoiceList);

})();
