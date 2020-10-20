import {Transactions} from '../../services/transaction.js';
import {Paymethods} from '../../services/paymethod.js';
import {TaxType, TaxIVAPercentage, TaxIRPFPercentage} from './invoiceEnums.js';
import {getInvoiceCategories} from '../../services/invoiceCategory.js';
import {insertInvoice, deleteInvoices} from '../../services/service.js';

import '../../components/aon-card.js';
import '../../components/aon-input.js';
import '../../components/aon-checkbox.js';
import '../../components/aon-dialog.js';
import '../../components/aon-viewer.js';

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
				<div id="aonInvoiceData">
					<div id="aonInvoiceDiv" style="display:flex;">
						<aon-card id="aonInvoiceItemDataCard" title="Datos Factura"> </aon-card>
						<div>
							<aon-card id="aonInvoiceItemTaxesCard" title="Detalle Impuestos"> </aon-card>
							<aon-card id="aonInvoiceItemIRPFCard"> </aon-card>
						</div>
					</div>
					<aon-card id="aonInvoiceItemDetailCard" title="Conceptos Factura"> </aon-card>
					<aon-card id="aonInvoiceItemFinanceCard" title="Vencimientos"> </aon-card>
				</div>
				<div id="aonInvoiceFile">

				</div>
				</div>
				<aon-dialog id="aonDialogInvoiceOption" type="menu" > </aon-dialog>
			`;

			this.build();
	  }

		build() {
			this.buildData();
			this.buildTaxes();
			this.buildIRPF();
			this.buildDetail();
			this.buildFinance();
			this.printTaxes();
			this.printDetails();
			this.printFinances();

			let aonInvoice = document.getElementById('aonInvoice');
			aonInvoice.addToolbarOption('Options', 'more_vert', () => {
				let button = document.getElementById('aonInvoiceToolbarOptionsButton');

				const top  = button.getBoundingClientRect().top;
				const left = button.getBoundingClientRect().left;
				let d = document.getElementById('aonDialogInvoiceOption');
				d.setMenuOptions(this.getOptions(), top, left);
				d.open();
			});

			if(!this._invoice.file) {
				let fileDiv = document.getElementById('aonInvoiceFile');
				fileDiv.style.display = 'none';
				aonInvoice.addToolbarOption('AddFile', 'attach_file', () => {
					let el = document.getElementById('aonInvoiceToolbarAddFileButtonInput');
					el.click();
				});
				let button = document.getElementById('aonInvoiceToolbarAddFileButton');

				let input = document.createElement('input');
				input.id = 'aonInvoiceToolbarAddFileButtonInput'
				input.style.display = 'none';
				input.type = 'file';
				input.addEventListener('change', () => this.preview());
				button.appendChild(input);
			}
		}

		preview() {
			let fileDiv = document.getElementById('aonInvoiceFile');
			let dataDiv = document.getElementById('aonInvoiceData');

			let fileInput = document.getElementById('aonInvoiceToolbarAddFileButtonInput');
			const file = fileInput.files[0];
			fileDiv.style.display = 'block';
			fileDiv.style.width = '50%';
			dataDiv.style.width = '50%';

			fileDiv.innerHTML = `<aon-viewer type="${file.type}"><aon-viewer>`;

			const READER = new FileReader();
			READER.readAsDataURL(file);
			READER.onload = (_event) => {
				fileDiv.style.display = 'block';
				fileDiv.style.width = '50%';
				dataDiv.style.width = '50%';
				document.getElementById('aonInvoiceDiv').style.display = 'block';
				fileDiv.innerHTML = `<aon-viewer type="${file.type}" file="${READER.result}"><aon-viewer>`;
			};
		}

		// attach(fileDataUri: string,  mimetype: string): void {
			// if (fileDataUri.length > 0) {
			// 	const base64File = fileDataUri.split(',')[1];
			// 	const cp = this.service.getActualCompany();
			// 	const data = {
			// 		company: cp ,
			// 		content: base64File,
			// 		contentType: mimetype,
			// 		contentEncoding: 'base64',
			// 		invoice: undefined,
			// 		status: undefined
			// 	};
			// 	if (this.invoiceService.isNew) { data.invoice = this.invoiceService.invoice; }
			// 	this.service.loading = true;
			// 	this.aonService.createInvoice(data)
			// 	.subscribe((r: Invoice) => {
			// 		if (!this.invoiceService.invoice.id) {
			// 			this.invoiceService.setInvoice(r);
			// 		}
			// 		this.invoiceService.invoiceFile = undefined;
			// 		this.invoiceService.getFile().subscribe();
			// 		this.invoiceService.expandFile(true);
			// 		this.service.loading = false;
			// 	});
			// }
		// }

		getOptions() {
			const status = this._invoice.status;
			if('refused' === status) {
				return [{
						name: 'Restaurar Factura',
						icon: '360',
						fn: () => this.restoreInvoice()
					}, {
						name: 'Enviar a la Papelera',
						icon: 'delete',
						fn: () => this.trashInvoice()
					}];
			} else if('trash' === status) {
				return [{
						name: 'Restaurar Factura',
						icon: '360',
						fn: () => this.restoreInvoice()
					}, {
						name: 'Borrar Definitivamente',
						icon: 'delete_sweep',
						fn: () => this.removeInvoice()
					}];
			} else return [{
					name: 'Rechazar Factura',
					icon: 'reply',
					fn: () => this.refuseInvoice()
				}, {
					name: 'Adjuntar Fichero',
					icon: 'attach_file',
					fn: () => this.addInvoiceFile()
				}, {
					name: 'Imprimir Factura',
					icon: 'print',
					fn: () => this.printInvoice()
				}, {
					name: 'Enviar Factura',
					icon: 'mail',
					fn: () => this.sendInvoice()
				}, {
					name: 'Añadir Comentario',
					icon: 'comment',
					fn: () => this.addInvoiceComment()
				}, {
					name: 'Enviar a la Papelera',
					icon: 'delete',
					fn: () => this.trashInvoice()
				}];
		}

		refuseInvoice() {
			this._invoice.status = 'refused';
			this.save();
		}

		trashInvoice() {
			this._invoice.status = 'trash';
			this.save();
		}

		restoreInvoice() {
			this._invoice.status = 'inbox';
			this.save();
		}

		removeInvoice() {

		}

		addInvoiceFile() {

		}

		printInvoice() {

		}

		sendInvoice() {

		}

		addInvoiceComment() {

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
			tdSerie.innerHTML = `<aon-input id="serie" description="Serie"></aon-input>`;
			tr.appendChild(tdSerie);
			let series = document.getElementById('serie');
			series.value = this._invoice.serie;
			series.addEventListener('change', () => this.update('serie'));

			// NUMBER
			let tdNumber = document.createElement('td');
			tdNumber.innerHTML = `<aon-input id="number" description="Número"></aon-input>`;
			tr.appendChild(tdNumber);
			let number = document.getElementById('number');
			number.value = this._invoice.number;
			number.addEventListener('change', () => this.update('number'));

			// DATE
			let tdDate = document.createElement('td');
			tdDate.innerHTML = `<aon-input id="date" type="date" description="Fecha"></aon-input>`;
			tr.appendChild(tdDate);
			let date = document.getElementById('date');
			date.value = this._invoice.date;
			date.addEventListener('change', () => this.update('date'));

			// TOTAL
			let tdTotal = document.createElement('td');
			tdTotal.innerHTML = `<aon-input id="total" description="Total"></aon-input>`;
			tr.appendChild(tdTotal);
			let total = document.getElementById('total');
			total.value = this._invoice.total;
			if(this._invoice.taxes.length > 1) {
				total.readonly = 'readonly';
			}
			total.addEventListener('change', () => this.updateTotal(total.value));
			let tr2 = document.createElement('tr');
			table.appendChild(tr2);

			// NIF
			let tdNif = document.createElement('td');
			tdNif.innerHTML = `<aon-input id="nif" description="NIF"></aon-input>`;
			tr2.appendChild(tdNif);
			let nif = document.getElementById('nif');
			nif.value = this.isEmitida()
				? (this._invoice.receiver ? this._invoice.receiver.document : '')
				: (this._invoice.sender ? this._invoice.sender.document : '');
			nif.addEventListener('change', () => this.updateRegistry());

			// NAME
			let tdName = document.createElement('td');
			tdName.setAttribute('colspan','3');
			tdName.innerHTML = `<aon-input id="name" description="Razón Social"></aon-input>`;
			tr2.appendChild(tdName);
			let name = document.getElementById('name');
			name.value = this.isEmitida()
				? (this._invoice.receiver ? this._invoice.receiver.name : '')
				: (this._invoice.sender ? this._invoice.sender.name : '');
			name.addEventListener('change', () => this.updateRegistry());

			let tr3 = document.createElement('tr');
			table.appendChild(tr3);

			// ADDRESS
			let tdAddress = document.createElement('td');
			tdAddress.setAttribute('colspan', '4');
			tdAddress.innerHTML = `<aon-input id="address" type="address" description="Dirección"></aon-input>`;
			tr3.appendChild(tdAddress);
			let address = document.getElementById('address');
			address.buildAddressValue(this.isEmitida()
				? JSON.stringify(this._invoice.receiver ? this._invoice.receiver.address : {})
				: JSON.stringify(this._invoice.sender ? this._invoice.sender.address : {}));
			address.addEventListener('change', () => this.updateRegistry());


			let tr4 = document.createElement('tr');
			table.appendChild(tr4);

			// CATEGORY
			let tdCategory = document.createElement('td');
			tdCategory.setAttribute('colspan', '2');
			tdCategory.innerHTML = `<aon-input id="category" type="list" description="Categoría"></aon-input>`;
			tr4.appendChild(tdCategory);
			let category = document.getElementById('category');
			category.options = JSON.stringify(getInvoiceCategories(this._invoice.type));
			category.value = this._invoice.category;
			category.addEventListener('select', () => this.update('category'));

			// PAYMETHOD
			let tdPaymethod = document.createElement('td');
			tdPaymethod.setAttribute('colspan', '2');
			tdPaymethod.innerHTML = `<aon-input id="pay_method" type="list" description="Forma de Pago"></aon-input>`;
			tr4.appendChild(tdPaymethod);
			let paymethod = document.getElementById('pay_method');
			paymethod.options = JSON.stringify(Paymethods);
			if(this._invoice.finances.length === 1) {
				paymethod.value = this._invoice.finances[0].paymethod;
			}
			paymethod.addEventListener('select', () =>  {
				if(this._invoice.finances.length < 1)
					this.addFinance(document.getElementById('pay_method').value);
			});
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
			tdTransaction.innerHTML = `<aon-input id="transaction" type="list" description="Tipo Transacción"></aon-input>`;
			tr1.appendChild(tdTransaction);
			let transaction = document.getElementById('transaction');
			transaction.options = JSON.stringify(Transactions);
			transaction.value = this._invoice.transaction;
			transaction.addEventListener('select', () => this.update('transaction'));

			let taxesTable = document.createElement('table');
			taxesTable.id = 'aonInvoiceItemTaxesCardTable';
			taxesTable.style.width = '100%';
			div.appendChild(taxesTable);

			let addDiv = document.createElement('div');
			div.appendChild(addDiv);

			addDiv.innerHTML = `<aon-icon-button id="aonInvoiceItemTaxesCardAddButton" icon="add"> </aon-icon-button>`;

			let addButton = document.getElementById('aonInvoiceItemTaxesCardAddButton');
			addButton.addEventListener('click', () => this.addTax());
		}

		buildIRPF(){
			let card = document.getElementById('aonInvoiceItemIRPFCard');
			let div = document.createElement('div');
			card.setContent(div);

			let table = document.createElement('table');
			table.style.width = '100%';
			div.appendChild(table);

			let tr1 = document.createElement('tr');
			table.appendChild(tr1);

			// IRPF
			let tdIRPF = document.createElement('td');
			tdIRPF.setAttribute('colspan', '2');
			tdIRPF.innerHTML = `<aon-checkbox id="irpfCheckbox" description="IRPF"></aon-checkbox>`;
			tr1.appendChild(tdIRPF);
			let irpf = document.getElementById('irpfCheckbox');
			irpf.addEventListener('change', () => this.changeIRPF());

			// SUPLIDOS
			let tdSuplidos = document.createElement('td');
			tdSuplidos.innerHTML = `<aon-checkbox id="suplidosCheckbox" description="Suplidos"></aon-checkbox>`;
			tr1.appendChild(tdSuplidos);
			let suplidos = document.getElementById('suplidosCheckbox');
			suplidos.addEventListener('change', () => this.changeSuplidos());

			// TOTAL SUPLIDOS
			let tdTotalSuplidos = document.createElement('td');
			tdTotalSuplidos.innerHTML = `<aon-input id="t-suplidos" description="Total Suplidos" visible="false"></aon-input>`;
			tr1.appendChild(tdTotalSuplidos);

			let irpfTable = document.createElement('table');
			irpfTable.id = 'aonInvoiceItemIRPFCardTable';
			irpfTable.style.width = '100%';
			div.appendChild(irpfTable);
		}

		buildDetail(){
			let card = document.getElementById('aonInvoiceItemDetailCard');
			let div = document.createElement('div');
			card.setContent(div);

			let table = document.createElement('table');
			table.id = 'aonInvoiceItemDetailCardTable';
			table.style.width = '100%';
			div.appendChild(table);

			let addDiv = document.createElement('div');
			div.appendChild(addDiv);

			addDiv.innerHTML = `<aon-icon-button id="aonInvoiceItemDetailCardAddButton" icon="add"> </aon-icon-button>`;

			let addButton = document.getElementById('aonInvoiceItemDetailCardAddButton');
			addButton.addEventListener('click', () => this.addDetail());
		}

		buildFinance(){
			let card = document.getElementById('aonInvoiceItemFinanceCard');
			let div = document.createElement('div');
			card.setContent(div);

			let table = document.createElement('table');
			table.id = 'aonInvoiceItemFinanceCardTable';
			table.style.width = '100%';
			div.appendChild(table);

			let addDiv = document.createElement('div');
			div.appendChild(addDiv);

			addDiv.innerHTML = `<aon-icon-button id="aonInvoiceItemFinanceCardAddButton" icon="add"> </aon-icon-button>`;

			let addButton = document.getElementById('aonInvoiceItemFinanceCardAddButton');
			addButton.addEventListener('click', () => this.addFinance());
		}

		save(invoice) {
			insertInvoice(invoice ? invoice : this._invoice).then((r) => this._invoice.id = r.id);
		}

		update(param, value) {
			if(!value) {
				value = document.getElementById(param).value;
			}
			this._invoice[param] = value;
			this.save();
		}

		updateRegistry() {
			let registry = {
				document: document.getElementById('nif').value,
				name: document.getElementById('name').value,
				address: JSON.parse(document.getElementById('address').value)
			};
			if(this.isEmitida()) {
				this._invoice.receiver = registry;
			} else {
				this._invoice.sender = registry;
			}
			this.save();
		}

		updateTotal(value) {
			this._invoice.total = Number(value);
			this.createTaxeFromTotal();
			this.save();
		}

		createTaxeFromTotal() {
			if (this._invoice.taxes.length === 0) {
				let tax = {
					type: 'IVA',
					percentage: 21.0,
					base: this.round(Number(this._invoice.total) / 1.21),
					quota: this.round(Number(this._invoice.total / 1.21) * 0.21)
			 	};
				this._invoice.taxes.push(tax);
				this.printTax(tax, 0);
			} else if(this._invoice.taxes.length === 1){
				this._invoice.taxes[0].base = this.round(this._invoice.total / (1 + this._invoice.taxes[0].percentage / 100));
				this._invoice.taxes[0].quota = this.round(this._invoice.total - this._invoice.taxes[0].base);
				document.getElementById('taxBase0').value = this._invoice.taxes[0].base;
				document.getElementById('taxQuota0').value = this._invoice.taxes[0].quota;
			}
		}

		addDetail() {
			let i = this._invoice.details.length;
			let detail = {
				description: '',
				price: 0,
				amount: 0,
				quantity: 0,
				discount: 0,
				vat: 0,
				irpf: false,
				suplidos: false
			};
			this._invoice.details.push(detail);
			this.printDetail(detail, i);
			this.save();
		}

		printDetail(detail, i) {
			let table = document.getElementById('aonInvoiceItemDetailCardTable');

			let tr = document.createElement('tr');
			tr.id = 'detail' + i;
			table.appendChild(tr);

			// DETAIL DESCRIPTION
			let tdDetailDescription = document.createElement('td');
			tdDetailDescription.setAttribute('colspan','3');
			tdDetailDescription.innerHTML = `<aon-input id="detailDescription${i}" description="Concepto"></aon-input>`;
			tr.appendChild(tdDetailDescription);
			let detailDescription = document.getElementById('detailDescription' + i);
			detailDescription.value = detail.description;
			detailDescription.addEventListener('change', () => this.changeDescription(i));

			// DETAIL QUANTITY
			let tdDetailQuantity = document.createElement('td');
			tdDetailQuantity.innerHTML = `<aon-input id="detailQuantity${i}" description="Cantidad"></aon-input>`;
			tr.appendChild(tdDetailQuantity);
			let detailQuantity = document.getElementById('detailQuantity' + i);
			detailQuantity.value = detail.quantity;
			detailQuantity.addEventListener('change', () => this.changeQuantity(i));

			// DETAIL PRICE
			let tdDetailPrice = document.createElement('td');
			tdDetailPrice.innerHTML = `<aon-input id="detailPrice${i}" description="Precio"></aon-input>`;
			tr.appendChild(tdDetailPrice);
			let detailPrice = document.getElementById('detailPrice' + i);
			detailPrice.value = detail.price;
			detailPrice.addEventListener('change', () => this.changePrice(i));

			// DETAIL DISCOUNT
			let tdDetailDiscount = document.createElement('td');
			tdDetailDiscount.innerHTML = `<aon-input id="detailDiscount${i}" description="%Dto"></aon-input>`;
			tr.appendChild(tdDetailDiscount);
			let detailDiscount = document.getElementById('detailDiscount' + i);
			detailDiscount.value = detail.discount;
			detailDiscount.addEventListener('change', () => this.changeDiscount(i));

			// DETAIL AMOUNT
			let tdDetailAmount = document.createElement('td');
			tdDetailAmount.innerHTML = `<aon-input id="detailAmount${i}" description="Importe"></aon-input>`;
			tr.appendChild(tdDetailAmount);
			let detailAmount = document.getElementById('detailAmount' + i);
			detailAmount.readonly = 'readonly';
			detailAmount.value = detail.amount;

			// DETAIL VAT
			let tdDetailVat = document.createElement('td');
			tdDetailVat.innerHTML = `<aon-input id="detailVat${i}" type="list" description="%IVA"></aon-input>`;
			tr.appendChild(tdDetailVat);
			let detailVat = document.getElementById('detailVat' + i);
			detailVat.options = JSON.stringify(TaxIVAPercentage);
			detailVat.value = detail.vat;
			detailVat.addEventListener('select', () => this.changeVat(i));

			// DETAIL IRPF
			let tdDetailIrpf = document.createElement('td');
			tdDetailIrpf.innerHTML = `<aon-checkbox id="detailIrpf${i}" description="IRPF"></aon-checkbox>`;
			tr.appendChild(tdDetailIrpf);
			let detailIrpf = document.getElementById('detailIrpf' + i);
			detailIrpf.value = detail.irpf;
			detailIrpf.disabled = !this._invoice.irpf ? 'disabled' : undefined;
			detailIrpf.addEventListener('change', () => this.updateDetailIRPF(i));

			// DETAIL SUPLIDOS
			let tdDetailSuplidos = document.createElement('td');
			tdDetailSuplidos.innerHTML = `<aon-checkbox id="detailSuplidos${i}" description="Suplidos"></aon-checkbox>`;
			tr.appendChild(tdDetailSuplidos);
			let detailSuplidos = document.getElementById('detailSuplidos' + i);
			detailSuplidos.value = detail.suplidos;
			detailSuplidos.disabled = !this._invoice.suplidos ? 'disabled' : undefined;
			detailSuplidos.addEventListener('change', () => this.updateDetailSuplidos(i));

			// REMOVER DETAIL
			let tdRemoveButton = document.createElement('td');
			tdRemoveButton.innerHTML = `<aon-icon-button id="detailRemove${i}" icon="remove_circle" ></aon-icon-button>`;
			tr.appendChild(tdRemoveButton);
			let removeButton = document.getElementById('detailRemove' + i);
			removeButton.addEventListener('click', () => this.removeDetail(i));
		}

		printDetails() {
			for(let i = 0; i < this._invoice.details.length; i++) {
				this.printDetail(this._invoice.details[i], i);
			}
		}

		changeDescription(index, value) {
			if(!value) {
				value = document.getElementById('detailDescription' + index).value;
			}
			this._invoice.details[index].description = value;
			this.save();
		}

		changeQuantity(index, value) {
			if(!value) {
				value = document.getElementById('detailQuantity' + index).value;
			}
			this._invoice.details[index].quantity = Number(value);
			this.calculatePrice(index);
			this.updateDetail(index);
			this.save();
		}

		changePrice(index, value) {
			if(!value) {
				value = document.getElementById('detailPrice' + index).value;
			}
			this._invoice.details[index].price = Number(value);
			this.calculatePrice(index);
			this.updateDetail(index);
			this.save();
		}

		changeDiscount(index, value) {
			if(!value) {
				value = document.getElementById('detailDiscount' + index).value;
			}
		 	this._invoice.details[index].discount = Number(value);
		 	this.calculatePrice(index);
			this.updateDetail(index);
			this.save();
		}

		updateDetail(index) {
			if (this._invoice.details[index].suplidos) {
				this.updateSuplidos();
			} else {
				if(this._invoice.details[index].irpf) {
					this.updateIRPF();
				}
				this.updateTaxes();
			}
		}

		changeVat(index, value) {
			if(!value) {
				value = document.getElementById('detailVat' + index).value;
			}
		 	this._invoice.details[index].vat = Number(value);
			this.updateTaxes();
			this.save();
		}

		updateDetailIRPF(index, value) {
			if(!value) {
				value = document.getElementById('detailIrpf' + index).value;
			}
			this._invoice.details[index].irpf = value;
			this.updateIRPF();
			this.save();
		}

		calculatePrice(index) {
			 if (this._invoice.details[index].price && this._invoice.details[index].quantity) {
					 let price = this._invoice.details[index].price;
					 if (!this.isNumber(price)) {
							 price = price.replace(' ', '').replace(',', '.');
					 }
					 let quantity = this._invoice.details[index].quantity;
					 if (!this.isNumber(quantity)) {
							 quantity = quantity.replace(' ', '').replace(',', '.');
					 }
					 let amount = this.round(Number(quantity) * Number(price));
					 amount = amount - amount * (this._invoice.details[index].discount / 100);
					 this._invoice.details[index].amount = this.round(amount);
					 document.getElementById('detailAmount' + index).value = this._invoice.details[index].amount;
				}
		}

		removeDetail(index) {
			document.getElementById('detail' + index).remove();
			this._invoice.details.splice(index, 1);
			this.updateDetail(index);
			this.save();
		}

		// TAXES

		addTax() {
			let i = this._invoice.taxes.length;
			let tax = {
				type: 'IVA',
				percentage: 0.0,
				base: 0,
				quota: 0
			};
			this._invoice.taxes.push(tax);
			this.printTax(tax, i);
			this.save();
		}

		printTax(tax, i) {
			let table = tax.type === 'IVA'
				? document.getElementById('aonInvoiceItemTaxesCardTable')
				: document.getElementById('aonInvoiceItemIRPFCardTable');


			let tr = document.createElement('tr');
			tr.id = 'tax' + i;
			table.appendChild(tr);

			// TAXTYPE
			let tdTaxType = document.createElement('td');
			tdTaxType.innerHTML = `<aon-input id="taxType${i}" type="list" description="Tipo"></aon-input>`;
			tr.appendChild(tdTaxType);
			let taxType = document.getElementById('taxType' + i);
			taxType.options = JSON.stringify(TaxType);
			taxType.value = tax.type;

			// TAXPERCENT
			let tdTaxPercentage= document.createElement('td');
			tdTaxPercentage.innerHTML = `<aon-input id="taxPercentage${i}" type="list" description="%"></aon-input>`;
			tr.appendChild(tdTaxPercentage);
			let taxPercentage = document.getElementById('taxPercentage' + i);
			taxPercentage.options = JSON.stringify( tax.type === 'IVA' ? TaxIVAPercentage : TaxIRPFPercentage);
			taxPercentage.value = tax.percentage;
			taxPercentage.addEventListener('select', () => this.updateTaxPercentage(i));

			// TAXBASE
			let tdTaxBase = document.createElement('td');
			tdTaxBase.innerHTML = `<aon-input id="taxBase${i}" description="Base"></aon-input>`;
			tr.appendChild(tdTaxBase);
			let taxBase = document.getElementById('taxBase' + i);
			taxBase.value = tax.base;
			taxBase.addEventListener('change', () => this.updateTaxBase(i));

			// TAXQUOTA
			let tdTaxQuota = document.createElement('td');
			tdTaxQuota.innerHTML = `<aon-input id="taxQuota${i}" description="Cuota"></aon-input>`;
			tr.appendChild(tdTaxQuota);
			let taxQuota = document.getElementById('taxQuota' + i);
			taxQuota.value = tax.quota;
			taxQuota.addEventListener('change', () => this.updateTaxQuota(i));

			// REMOVE TAX
			let tdRemoveButton = document.createElement('td');
			tdRemoveButton.innerHTML = `<aon-icon-button id="taxRemove${i}" icon="remove_circle" ></aon-icon-button>`;
			tr.appendChild(tdRemoveButton);
			let removeButton = document.getElementById('taxRemove' + i);
			removeButton.addEventListener('click', () => this.removeTax(i));
		}

		printTaxes() {
			for(let i = 0; i < this._invoice.taxes.length; i++) {
				this.printTax(this._invoice.taxes[i], i);
			}
		}

		updateTaxPercentage(index, value) {
			if(!value) {
				value = document.getElementById('taxPercentage' + index).value;
			}
			this._invoice.taxes[index].percentage = Number(value);
			this._invoice.taxes[index].quota = this.round(this._invoice.taxes[index].base / 100 * Number(value));
			document.getElementById('taxQuota' + index).value = this._invoice.taxes[index].quota;

			this.updateTaxesTotal();
			this.save();
		}

		updateTaxBase(index, value) {
			if(!value) {
				value = document.getElementById('taxBase' + index).value;
			}
			this._invoice.taxes[index].base = Number(value);
			this._invoice.taxes[index].quota = this.round(Number(value) / 100 * this._invoice.taxes[index].percentage);
			document.getElementById('taxQuota' + index).value = this._invoice.taxes[index].quota;
			this.updateTaxesTotal();
			this.save();
		}

		updateTaxQuota(index, value) {
			if (!value) {
				value = document.getElementById('taxQuota' + index).value;
			}
			this._invoice.taxes[index].quota = Number(value);
			this._invoice.taxes[index].base = this.round((value * 100) / this._invoice.taxes[index].percentage);
			document.getElementById('taxBase' + index).value = this._invoice.taxes[index].base;

			this.updateTaxesTotal();
			this.save();
		}

		removeTax(index) {
			document.getElementById('tax' + index).remove();
			this._invoice.taxes.splice(index, 1);
			this.updateTaxesTotal();
			this.save();
		}

		// IRPF FUNCTIONS
		changeIRPF(val) {
			if(!val) {
				val = document.getElementById('irpfCheckbox').value;
			}

			this._invoice.irpf = val;
			if (val) {
				let i = this._invoice.taxes.length;
				let tax = {
					type: 'IRPF',
					percentage: 19.0,
					base: 0,
					quota: 0
				};
				this._invoice.taxes.push(tax);
				this.printTax(tax, i);
			} else {
				for(let i = 0; i < this._invoice.taxes.length; i++) {
					if(tax.type === 'IRPF') {
						this.removeTax(i);
					}
				}
			}
			for(let j = 0; j < this._invoice.details.length; j++){
				let detailIrpf = document.getElementById('detailIrpf' + j)
				if(!val){
					this._invoice.details[j].irpf = false;
					detailIrpf.value = false;
				}
				detailIrpf.disabled = !val ? 'disabled' : undefined;
			}
			this.save();
		}

		updateIRPF() {
			for(let i = 0; i < this._invoice.taxes.length; i++) {
				let tax = this._invoice.taxes[i];
				if(tax.type === 'IRPF') {
					this._invoice.taxes[i].base = this.totalBaseIRPF();
					this._invoice.taxes[i].quota = this.round(this._invoice.taxes[i].base / 100 * this._invoice.taxes[i].percentage);
					let base = document.getElementById('taxBase' + i);
					base.value = this._invoice.taxes[i].base;
					let quota = document.getElementById('taxQuota' + i);
					quota.value = this._invoice.taxes[i].quota;
				}
			}
			this.updateTaxesTotal();
		}

		updateTaxes(vat) {
			for(let i = 0; i < this._invoice.taxes.length; i++) {
				let tax = this._invoice.taxes[i];
				if(tax.type === 'IVA' && (!vat || tax.percentage === vat)) {
					this._invoice.taxes[i].base = this.totalBase(tax.percentage);
					this._invoice.taxes[i].quota = this.round(this._invoice.taxes[i].base / 100 * this._invoice.taxes[i].percentage);
					let base = document.getElementById('taxBase' + i);
					base.value = this._invoice.taxes[i].base;
					let quota = document.getElementById('taxQuota' + i);
					quota.value = this._invoice.taxes[i].quota;
				}
			}
			this.updateTaxesTotal();
		}

		updateTaxesTotal() {
			this._invoice.total = this.round(this.totalImpuestos() + this.totalSuplidos());
			let total = document.getElementById('total');
			total.value = this._invoice.total;
			if(this._invoice.taxes.length > 1) {
				total.readonly = 'readonly';
			}

		}
		// END IRPF FUNCTIONS


		// SUPLIDOS FUNCTIONS

		changeSuplidos(value) {
			if(!value) {
				value = document.getElementById('suplidosCheckbox').value;
			}
			this._invoice.suplidos = value;
			document.getElementById('t-suplidos').visible = value;
			for(let j = 0; j < this._invoice.details.length; j++){
				let detailSuplidos = document.getElementById('detailSuplidos' + j);
				if(!value) {
					this._invoice.details[j].suplidos = false;
					detailSuplidos.value = false;
				}
				detailSuplidos.disabled = !value ? 'disabled' : undefined;
			}
			this.save();
		}

		updateDetailSuplidos(index, value) {
			if(!value) {
				value = document.getElementById('detailSuplidos' + index).value;
			}
			this._invoice.details[index].suplidos = value;
			let vat = document.getElementById('detailVat' + index);
			let irpf = document.getElementById('detailIrpf' + index);

			this._invoice.details[index].irpf = value ? false : this._invoice.details[index].irpf;
			this._invoice.details[index].vat = value ? undefined : 21;
			irpf.disabled = value ? 'disabled' : undefined;
			irpf.value = this._invoice.details[index].irpf;
			if(value) {
				vat.readonly = 'readonly';
			}
			vat.value = this._invoice.details[index].vat;

			this.updateIRPF();
			this.updateTaxes();
			this.updateSuplidos();
		}

		updateSuplidos() {
			this._invoice.totalSuplidos = this.totalSuplidos();
			document.getElementById('t-suplidos').value = this._invoice.totalSuplidos;
			this.updateTaxesTotal();
		}

		// FINANCES

		addFinance(paymethod) {
			let i = this._invoice.finances.length;
			let finance = {
					due_date: this._invoice.date,
					paymethod: paymethod ? paymethod : 'CASH',
					amount: this._invoice.total,
					iban: ''
			};
			this._invoice.finances.push(finance);
			this.printFinance(finance, i);
			this.save();
		}

		printFinance(finance, i) {
			let table = document.getElementById('aonInvoiceItemFinanceCardTable');

			let tr = document.createElement('tr');
			tr.id = 'finance' + i;
			table.appendChild(tr);

			// FINANCE DUE DATE
			let tdFinanceDueDate = document.createElement('td');
			tdFinanceDueDate.innerHTML = `<aon-input id="financeDueDate${i}" description="Fecha Vencimiento" type="date"></aon-input>`;
			tr.appendChild(tdFinanceDueDate);
			let financeDueDate = document.getElementById('financeDueDate' + i);
			financeDueDate.value = finance.due_date;
			financeDueDate.addEventListener('change', () => this.updateFinanceDate(i));

			// FINANCE PAYMETHOD
			let tdDetailPaymethod = document.createElement('td');
			tdDetailPaymethod.innerHTML = `<aon-input id="financePaymethod${i}" type="list" description="Forma de Pago"></aon-input>`;
			tr.appendChild(tdDetailPaymethod);
			let financePaymethod = document.getElementById('financePaymethod' + i);
			financePaymethod.options = JSON.stringify(Paymethods);
			financePaymethod.value = finance.paymethod;
			financePaymethod.addEventListener('select', () => this.updateFinancePaymethod(i));

			// FINANCE AMOUNT
			let tdFinanceAmount = document.createElement('td');
			tdFinanceAmount.innerHTML = `<aon-input id="financeAmount${i}" description="Importe"></aon-input>`;
			tr.appendChild(tdFinanceAmount);
			let financeAmount = document.getElementById('financeAmount' + i);
			financeAmount.value = finance.amount;
			financeAmount.addEventListener('change', () => this.updateFinanceAmount(i));

			// FINANCE IBAN
			let tdFinanceIban = document.createElement('td');
			tdFinanceIban.innerHTML = `<aon-input id="financeIban${i}" description="IBAN"></aon-input>`;
			tr.appendChild(tdFinanceIban);
			let financeIban = document.getElementById('financeIban' + i);
			financeIban.value = finance.iban;
			financeIban.addEventListener('change', () => this.updateFinanceIban(i));

			// REMOVE FINANCE
			let tdRemoveButton = document.createElement('td');
			tdRemoveButton.innerHTML = `<aon-icon-button id="financeRemove${i}" icon="remove_circle" ></aon-icon-button>`;
			tr.appendChild(tdRemoveButton);
			let removeButton = document.getElementById('financeRemove' + i);
			removeButton.addEventListener('click', () => this.removeFinance(i));
		}

		printFinances() {
			for(let i = 0; i < this._invoice.finances.length; i++) {
				this.printFinance(this._invoice.finances[i], i);
			}
		}

		// Fecha Vencimiento
		updateFinanceDate(index, value) {
			if(!value) {
				value = document.getElementById('finance_date' + index).value;
			}
			this._invoice.vencimientos[index].due_date = value;
			this.save();
		}

		// Forma Pago Vencimiento
		updateFinancePaymethod(index, value) {
			if(!value) {
				value = document.getElementById('finance_paymethod' + index).value;
			}
			this._invoice.vencimientos[index].paymethod = value;
			this.save();
		}

		// Importe Vencimiento
		updateFinanceAmount(index, value) {
			if(!value) {
				value = document.getElementById('finance_amount' + index).value;
			}
			this._invoice.vencimientos[index].amount = Number(value);
			this.save();
		}

		// Iban Vencimiento
		updateFinanceIban(index, value) {
			if(!value) {
				value = document.getElementById('finance_amount' + index).value;
			}
			this._invoice.vencimientos[index].iban = value;
			this.save();
		}

		// Delete Vencimiento
		removeFinance(index) {
			document.getElementById('finance' + index).remove();
			this._invoice.finances.splice(index, 1);
			this.save();
		}

		// ================================ Aux Functions  ================================ //

		totalSuplidos() {
			let total = 0;
			for (let i = 0; i < this._invoice.details.length; i++) {
				if (this._invoice.details[i].suplidos) {
					total += this._invoice.details[i].amount;
				}
			}
			return this.round(total);
		}

		totalImpuestos() {
			 var total = 0;
			 for (var i = 0; i < this._invoice.taxes.length; i++) {
				 if(this._invoice.taxes[i].type === 'IVA'){
					 total += (this._invoice.taxes[i].base + this._invoice.taxes[i].quota);
				 } else {
					 total -= this._invoice.taxes[i].quota;
				 }
			 }
			 return this.round(total);
		}


		totalBase(vat) {
			let total = 0;
			if(vat) {
				for (let i = 0; i < this._invoice.details.length; i++) {
					if (this._invoice.details[i].vat === vat) {
						total += this._invoice.details[i].amount;
					}
				}
			}
			return total;
		}

		totalBaseIRPF() {
			let total = 0;
			for (let i = 0; i < this._invoice.details.length; i++) {
				if (this._invoice.details[i].irpf) {
					total += this._invoice.details[i].amount;
				}
			}
			return total;
		}

		// UTILS

		isNumber(n) {
			return !isNaN(parseFloat(n)) && isFinite(n);
		}

		round(value) {
			return this.decimalAdjust('round', value, -2);
		}

		decimalAdjust(type, value, exp) {
			// Si el exp no está definido o es cero...
			if (typeof exp === 'undefined' || +exp === 0) {
				return Math[type](value);
			}
			value = +value;
			exp = +exp;
			// Si el valor no es un número o el exp no es un entero...
			if (isNaN(value) || !(typeof exp === 'number' && exp % 1 === 0)) {
				return NaN;
			}
			// Shift
			value = value.toString().split('e');
			value = Math[type](+(value[0] + 'e' + (value[1] ? (+value[1] - exp) : -exp)));
			// Shift back
			value = value.toString().split('e');
			return +(value[0] + 'e' + (value[1] ? (+value[1] + exp) : exp));
		}

		isEmitida() {
			return this._invoice.type === 'Emitida';
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
				sender: {
					document: '',
					name: '',
					address: {
						country: 'ES',
						address: '',
						zip: '',
						city: '',
						province: ''
					}
				},
				receiver: {
					document: '',
					name: '',
					address: {
						country: 'ES',
						address: '',
						zip: '',
						city: '',
						province: ''
					}
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
