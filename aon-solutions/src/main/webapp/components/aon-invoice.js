import {Transactions} from '../services/transaction.js';
import {Paymethods} from '../services/paymethod.js';
import {getInvoiceCategories} from '../services/invoiceCategory.js';

import './aon-card.js';
import './aon-input-text.js';
import './aon-select.js';
import './aon-address.js';
import './aon-checkbox.js';

(function() {

	const html =  `

		<style>
		  	.aon-card-invoice-100 {
				width: 100%;
			}
		  	.aon-card-invoice-50 {
				width: 50%;
			}

			.aon-card-content {
				width: 98%;
				margin: 10px;
				overflow: visible !important;
				z-index: auto;
				min-height: 100px;
			}
			.aon-card-content-100 {
				width: 99%;
				margin: 10px;
				overflow: visible !important;
				z-index: auto;
				min-height: 100px;
			}

		  	.form-center-invoice {
				display: flex;
				justify-content: center;
			}
				.demo-card-wide.mdl-card {

					padding-bottom:50px;
					padding-top: 20px;
					padding-left: 20px;
					padding-right: 20px;
				}

				.demo-card-wide > .mdl-card__title {

				}
				.demo-card-wide > .mdl-card__menu {

				}

				.aon-width-100{
				    width: 98%;
    				display: inline-block !important;
				}
				.aon-width-90{
					width: 90%;
    				display: inline-block !important;
				}

				.aon-width-30{
					width: 29%;
    			display: inline-block !important;
				}
				.aon-width-10{
					width: 9%;
    			display: inline-block !important;
				}

				.aon-margin-0{
					margin: 0px;
				}
	    </style>

		<div class="form-center-invoice">
		<!-- DATOS FACTURA -->
			<div class="aon-card-invoice-50">
				<div class="demo-card-wide mdl-card mdl-shadow--2dp aon-card-content">
					<div class="mdl-card__title">
						<h2 class="mdl-card__title-text"> Datos Factura </h2>
					</div>

					<form action="#" class="aon-margin-0">
						<aon-input-text class="aonWidth25" id="serie" description="Serie"></aon-input-text>
						<aon-input-text class="aonWidth25" id="number" description="Número"></aon-input-text>
						<aon-input-text class="aonWidth25" id="reference" visible="false" description="Nº Factura"></aon-input-text>
						<aon-input-text class="aonWidth25" id="date" type="date" description="Fecha"></aon-input-text>
						<aon-input-text class="aonWidth25" id="total" description="Total"></aon-input-text>
					</form>

					<form action="#" class="aon-margin-0">
						<aon-input-text class="aonWidth25" id="nif" description="NIF"></aon-input-text>
						<aon-input-text class="aonWidth75" id="name" description="Razón Social"></aon-input-text>
					</form>

					<form action="#" class="aon-margin-0">
						<aon-address class="aon-width-100" id="address" description="Dirección"></aon-address>
					</form>

					<form action="#" class="aon-margin-0">
						<aon-select class="aonWidth50" id="category" description="Categoría"></aon-select>
						<aon-select class="aonWidth50" id="pay_method" description="Forma de Pago"></aon-select>
					</form>
				</div>
			</div>
		<!-- DETALLES IMPUESTOS -->
			<div class="aon-card-invoice-50">
				<div class="demo-card-wide mdl-card mdl-shadow--2dp aon-card-content">
					<div class="mdl-card__title">
						<h2 class="mdl-card__title-text">  Detalle Impuestos</h2>
					</div>

					<form>
						<aon-select class="aon-width-100" id="transaction" description="Tipo Transacción"></aon-select>
					</form>
					<div id="impuesto"></div>
					<div>
						<button id="add_impuesto" class="mdl-button mdl-js-button mdl-button--icon">
							<i class="material-icons">add</i>
						</button>
					</div>
				</div>
				<div class="demo-card-wide mdl-card mdl-shadow--2dp aon-card-content">
					<form action="#">
						<aon-checkbox class="aonWidth50" id="irpfCheckbox" description="IRPF"></aon-checkbox>
						<aon-checkbox class="aonWidth25" id="suplidosCheckbox" description="Suplidos"></aon-checkbox>
						<aon-input-text class="aonWidth25" id="t-suplidos" description="Total Suplidos" visible="false"></aon-input-text>
					</form>
					<div class="row" id="irpf-taxe"></div>
				</div>
			</div>
		</div>

		<!-- CONCEPTOS -->

		<div class="form-center-invoice">
			<div class="aon-card-invoice-100">
				<div class="demo-card-wide mdl-card mdl-shadow--2dp aon-card-content-100">
					<div class="mdl-card__title">
						<h2 class="mdl-card__title-text"> Conceptos Factura </h2>
					</div>
					<div id="concepto"></div>
					<div>
						<button id="add_concepto" class="mdl-button mdl-js-button mdl-button--icon">
							<i class="material-icons">add</i>
						</button>
					</div>
				</div>
			</div>
		</div>

		<!-- VENCIMIENTOS -->

		<div class="form-center-invoice">
			<div class="aon-card-invoice-100">
				<div class="demo-card-wide mdl-card mdl-shadow--2dp aon-card-content-100">
					<div class="mdl-card__title">
						<h2 class="mdl-card__title-text"> Vencimientos </h2>
					</div>
					<div id="vencimiento"></div>
					<div>
						<button id="add_vencimiento" class="mdl-button mdl-js-button mdl-button--icon">
							<i class="material-icons">add</i>
						</button>
					</div>
				</div>
			</div>
		</div>

		`;

	class AonInvoice extends HTMLElement {

    static get observedAttributes() {
      return ['invoice'];
   	}

		_invoice;

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
        this.setAttribute('invoice', JSON.stringify(inv));
				this._invoice = inv;
      } else {
				let inv = JSON.parse(this.getAttribute('invoice'));
				this._invoice = inv;
			}
    }

	  connectedCallback () {
 			this.innerHTML = html;
	  	this.build();
	  }

		build() {
			let series = document.getElementById('serie');
			series.value = this._invoice.serie;
			series.addEventListener('change', () => this.update('serie'));

			let number = document.getElementById('number');
			number.value = this._invoice.number;
			number.addEventListener('change', () => this.update('number'));

			let reference = document.getElementById('reference');
			reference.value = this._invoice.reference;
			reference.addEventListener('change', () => this.update('reference'));

			let date = document.getElementById('date');
			date.value = this._invoice.date;
			date.addEventListener('change', () => this.update('date'));

			let total = document.getElementById('total');
			total.value = this._invoice.total;
			total.addEventListener('change', () => this.updateTotal());

			let nif = document.getElementById('nif');
			nif.value = this._invoice.nif;
			nif.addEventListener('change', () => this.update('nif'));

			let name = document.getElementById('name');
			name.value = this._invoice.name;
			name.addEventListener('change', () => this.updateNif('name'));

			let address = document.getElementById('address');
			address.value = JSON.stringify(this._invoice.address);
			address.addEventListener('change', () => this.updateAddress());

			let category = document.getElementById('category');
			category.options = JSON.stringify(getInvoiceCategories(this._invoice.type));
			category.value = this._invoice.category;
			category.addEventListener('select', () => this.update('category'));

			let paymethod = document.getElementById('pay_method');
			paymethod.options = JSON.stringify(Paymethods);
			// TODO: VALUE
			paymethod.addEventListener('select', () => ticketPaymethod(document.getElementById('pay_method')));

			let transaction = document.getElementById('transaction');
			transaction.options = JSON.stringify(Transactions);
			transaction.value = this._invoice.transaction;
			transaction.addEventListener('select', () => this.update('transaction'));

			document.getElementById('add_impuesto')
        		.addEventListener('click', () => this.addImpuesto());

			document.getElementById('add_concepto')
				.addEventListener('click', () => this.addConcepto());

      document.getElementById('add_vencimiento')
        .addEventListener('click', () => this.addVencimiento());

			document.getElementById('irpfCheckbox')
				.addEventListener('change', () => this.showIRPFLine())

			document.getElementById('suplidosCheckbox')
				.addEventListener('change', () => this.showSuplidoLine());
		}

		update(param, value) {
			if(!value) {
				value = document.getElementById(param).value;
			}
		 	this._invoice[param] = value;
		}

		updateAddress() {
			this._invoice.address = JSON.parse(document.getElementById('address').value);
		}

		updateTotal(value) {
			this.update('total', value);
			this.createTaxeFromTotal();
		}

		createTaxeFromTotal(value) {
			if (this._invoice.taxes.length === 0) {
		      	this._invoice.taxes.push({
        			type: 'IVA',
        			percentage: 21.0,
        			base: this.round(this._invoice.total / 1.21),
        		  quota: this.round((this._invoice.total / 1.21) * 0.21)
       			 });
			} else {
				this._invoice.taxes[0].quota = this.round(this._invoice.total * this._invoice.taxes[0].percentage / 100);
				this._invoice.taxes[0].base = this.round(this._invoice.total - this._invoice.taxes[0].quota);
			}
			this.printTaxes();
    }

		addConcepto() {
			if(!this.hasAttribute('add_concepto_click')){
				this.setAttribute('add_concepto_click', 'true');
				this._invoice.details.push({
						description: '',
						price: 0,
						amount: 0,
						quantity: 0,
						discount: 0,
						vat: 0,
						irpf: false,
						suplidos: false
				});
				this.printDetails();
			} else {
				this.removeAttribute('add_impuesto_click');
			}
    }

		printDetails() {
			let taxPercentageOptions = [
				{value:'21', name:'21%'},
				{value:'10', name:'10%'},
				{value:'4', name:'4%'},
				{value:'0', name:'0%'},
			];

			let line = '';
			for(let i = 0; i < this._invoice.details.length; i++) {
				let button = `
					<button id='${'detail_remove_button' + i}' class="mdl-button mdl-js-button mdl-button--icon">
						<i class="material-icons">remove_circle</i>
					</button>
				`;

				let disableIRPF = !this._invoice.irpf ? 'disabled' : '';
				let disableSuplido = !this._invoice.suplidos ? 'disabled' : '';
				line = line + `
					<form action="#" class="aon-margin-0 aon-width-90">
						<aon-input-text class="aon-width-30" id='${'detail_description' + i}' value='${this._invoice.details[i].description}' description="Concepto" ></aon-input-text>
						<aon-input-text class="aon-width-10" id='${'detail_quantity' + i}'  value='${this._invoice.details[i].quantity}' description="Cantidad" ></aon-input-text>
						<aon-input-text class="aon-width-10" id='${'detail_price' + i}'  value='${this._invoice.details[i].price}' description="Precio" ></aon-input-text>
						<aon-input-text class="aon-width-10" id='${'detail_discount' + i}'  value='${this._invoice.details[i].discount}' description="%Dto" ></aon-input-text>
						<aon-input-text class="aon-width-10" id='${'detail_amount' + i}'  value='${this._invoice.details[i].amount}' description="Importe" readonly ></aon-input-text>
						<aon-select class="aon-width-10" id='${'detail_iva' + i}' options='${JSON.stringify(taxPercentageOptions)}' value='${this._invoice.details[i].vat}' description="%IVA"></aon-select>
						<aon-checkbox class="aon-width-10" id='${'detail_irpf'+ i}'  value="${this._invoice.details[i].irpf}" description="IRPF" ${disableIRPF}></aon-checkbox>
						<aon-checkbox class="aon-width-10" id='${'detail_suplido'+ i}' value="${this._invoice.details[i].suplidos}" description="Suplidos" ${disableSuplido}></aon-checkbox>
					</form>
					${button}
				`;
			}

			let con = document.getElementById('concepto');
			con.innerHTML = line;

			if(this._invoice.details.length > 1) {
				document.getElementById('total').readonly = 'readonly';
				document.getElementById('add_impuesto').disabled = 'disabled';
			}

			componentHandler.upgradeDom();

			for(let i = 0; i < this._invoice.details.length; i++) {
				document.getElementById('detail_description' + i)
					.addEventListener('change', () => this.changeDescription(i));
				document.getElementById('detail_quantity' + i)
					.addEventListener('change', () => this.changeQuantity(i));
				document.getElementById('detail_price' + i)
					.addEventListener('change', () => this.changePrice(i));
				document.getElementById('detail_discount' + i)
					.addEventListener('change', () => this.changeDiscount(i));
				document.getElementById('detail_iva' + i)
					.addEventListener('select', () => this.ivaConcepto(i));
				if(this._invoice.irpf){
					document.getElementById('detail_irpf' + i)
						.addEventListener('change', () => this.updateIRPF(i));
				}
				if(this._invoice.suplidos){
					document.getElementById('detail_suplido' + i)
						.addEventListener('change', () => this.updateSuplidos(i));
				}
				document.getElementById('detail_remove_button' + i)
					.addEventListener('click', () => this.borrarConcepto(i));
			}
		}

		changeDescription(index, value) {
			if(!value) {
				value = document.getElementById('detail_description' + index).value;
			}
			this._invoice.details[index].description = value;
		}

		changeQuantity(index, value) {
			if(!value) {
				value = document.getElementById('detail_quantity' + index).value;
			}
	 		this._invoice.details[index].quantity = Number(value);
	 		this.calculatePrice(index);

			if (this._invoice.details[index].suplidos) {
	 			this.createTaxe();
	 			this._invoice.totalSuplidos = this.totalSuplidos();
	 			document.getElementById('t-suplidos').value = this._invoice.totalSuplidos;
	 		} else {
	 			this.createTaxe();
	 		}
 			this.printTaxes();
	  }

	  changePrice(index, value) {
			if(!value) {
				value = document.getElementById('detail_price' + index).value;
			}
	 	 	this._invoice.details[index].price = Number(value);
	 		this.calculatePrice(index);
	 		if (this._invoice.details[index].suplidos) {
	 			this.createTaxe();
	 			this._invoice.totalSuplidos = this.totalSuplidos();
	 			document.getElementById('t-suplidos').value = this._invoice.totalSuplidos;
	 		} else {
	 			this.createTaxe();
	 		}
	 		this.printTaxes();
	  }

	  changeDiscount(index, value) {
			if(!value) {
				value = document.getElementById('detail_discount' + index).value;
			}
	 		 this._invoice.details[index].discount = Number(value);
	 		 this.calculatePrice(index);
	 		 if (this._invoice.details[index].suplidos) {
	 				 this.createTaxe();
	 				 this._invoice.totalSuplidos = totalSuplidos();
	 				 document.getElementById('t-suplidos').value = this._invoice.totalSuplidos;
	 		 } else {
	 				 this.createTaxe();
	 		 }
	 		 this.printTaxes();
	 		 document.getElementById('add_concepto').parentElement.style.display = 'block';
	  }

	  ivaConcepto(index, value) {
			if(!value) {
				value = document.getElementById('detail_iva' + index).value;
			}
	 		 this._invoice.details[index].vat = Number(value);
	 		 this.createTaxe();
	 		 this.printTaxes();
	  }

	  updateIRPF(index, value) {
			if(!value) {
				value = document.getElementById('detail_irpf' + index).value;
			}
	 		 if (value && value === 'true') {
	 				 this._invoice.details[index].irpf = true;
	 				 this._invoice.irpf.base = this.round(this.totalBaseIRPF());
	 				 this._invoice.irpf.quota = this.round(this._invoice.irpf.base / 100 * this._invoice.irpf.percentage);
	 				 document.getElementById('irpf_base').value = this._invoice.irpf.base;
	 				 document.getElementById('irpf_quota').value = this._invoice.irpf.quota;
			 } else {
	 				 this._invoice.details[index].irpf = false;
	 				 this._invoice.irpf.base = this.round(this.totalBaseIRPF());
	 				 this._invoice.irpf.quota = this.round(this._invoice.irpf.base / 100 * this._invoice.irpf.percentage);
	 				 document.getElementById('irpf_base').value = this._invoice.irpf.base;
	 				 document.getElementById('irpf_quota').value = this._invoice.irpf.quota;
	 		 }

	 		 this._invoice.total = this.totalImpuestos();
	 		 document.getElementById('total').value = this._invoice.total;
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
	 				 document.getElementById('detail_amount' + index).value = this._invoice.details[index].amount;

	 				 if (this._invoice.details[index].irpf) {
	 						 this._invoice.irpf.base = this.totalBaseIRPF();
	 						 this._invoice.irpf.quota = this.round(this._invoice.irpf.base / 100 * this._invoice.irpf.percentage);
	 						 document.getElementById('irpf_base').value = this._invoice.irpf.base;
	 						 document.getElementById('irpf_quota').value = this._invoice.irpf.quota;
	 				 }
				}
	  }

	  borrarConcepto(index) {
		if (this._invoice.details[index].irpf) {
			this._invoice.details[index].irpf = false;
	 		this._invoice.irpf.base = totalBaseIRPF();
	 		this._invoice.irpf.quota = round(this._invoice.irpf.base / 100 * this._invoice.irpf.percentage);
	 		document.getElementById('irpf_base').value = this._invoice.irpf.base;
	 		document.getElementById('irpf_quota').value = this._invoice.irpf.quota;
	 	}

		var concepto_vat = this._invoice.details[index].vat;
	 	this._invoice.details.splice(index, 1);
	 	this.createTaxe(concepto_vat);
	 	this.printTaxes();
	 	this.printDetails();
	 	this._invoice.total = this.totalImpuestos();
	 	document.getElementById('total').value = this._invoice.total;
	  }

		createTaxe() {
			this._invoice.taxes = [];
			this._invoice.details.forEach(det => {
				let bool = true;
				for(let i = 0; i < this._invoice.taxes.length; i++){
					if(this._invoice.taxes[i].percentage === det.vat){
						this._invoice.taxes[i].base = this._invoice.taxes[i].base + det.amount;
						this._invoice.taxes[i].quota = this._invoice.taxes[i].quota + (det.amount * det.vat / 100);
					}
				}
				if(bool){
					this._invoice.taxes.push({
						tipo: 'IVA',
						percentage: det.vat,
						base: det.amount,
						quota: det.amount * det.vat / 100
					});
				}
			});

			this._invoice.total = this.round(this.totalImpuestos() + this.totalSuplidos());
			document.getElementById('total').value = this._invoice.total;
		}


		addImpuesto() {
			if(!this.hasAttribute('add_impuesto_click')){
				this.setAttribute('add_impuesto_click', 'true');
				this._invoice.taxes.push({
					type: 'IVA',
					percentage: 0.0,
					base: 0,
					quota: 0
				});
				this.printTaxes();
			} else {
				this.removeAttribute('add_impuesto_click');
			}
		}

		printTaxes() {
			let taxTypeOptions = [{value:'IVA', name:'IVA'}];
			let taxPercentageOptions = [
				{value:'21', name:'21%'},
				{value:'10', name:'10%'},
				{value:'4', name:'4%'},
				{value:'0', name:'0%'},
			];

			let readonly = this._invoice.details.length > 0 ? 'readonly' : '';
			let className = this._invoice.details.length > 0 ? 'aon-margin-0' : 'aon-margin-0 aon-width-90';
			let line = '';
			for(let i = 0; i < this._invoice.taxes.length; i++) {
				let button = this._invoice.details.length > 0 ? '' : `
					<button id='${'tax_remove_button' + i}' class="mdl-button mdl-js-button mdl-button--icon">
						<i class="material-icons">remove_circle</i>
					</button>
				`;
				line = line + `
					<form action="#" class="${className}">
						<aon-select class="aonWidth25" id='${'tax_type' + i}' options='${JSON.stringify(taxTypeOptions)}' value="IVA" description="Tipo" ${readonly}></aon-select>
						<aon-select class="aonWidth25" id='${'tax_percentage' + i}' options='${JSON.stringify(taxPercentageOptions)}' value='${this._invoice.taxes[i].percentage}' description="%" ${readonly}></aon-select>
						<aon-input-text class="aonWidth25" id='${'tax_base' + i}' value="${this._invoice.taxes[i].base}" description="Base Imponible" ${readonly}></aon-input-text>
						<aon-input-text class="aonWidth25" id='${'tax_quota' + i}' value="${this._invoice.taxes[i].quota}" description="Cuota" ${readonly}></aon-input-text>
					</form>
					${button}
				`;
			}

			let imp = document.getElementById('impuesto');
			imp.innerHTML = line;

			if(this._invoice.taxes.length > 1) {
				document.getElementById('total').readonly = 'readonly';
			}

			componentHandler.upgradeDom();
			if(this._invoice.details.length === 0) {
				for(let i = 0; i < this._invoice.taxes.length; i++) {
					document.getElementById('tax_percentage' + i)
						.addEventListener('select', () => this.updateTaxeOption(i));
					document.getElementById('tax_base' + i)
						.addEventListener('change', () => this.updateBaseImpuesto(i));
					document.getElementById('tax_quota' + i)
						.addEventListener('change', () => this.updateCuotaImpuesto(i));
					document.getElementById('tax_remove_button' + i)
						.addEventListener('click', () => this.borrarImpuesto(i));
				}
			}
		}

		// TAXES

		updateTaxeOption(index, value) {
			if(!value) {
				value = document.getElementById('tax_percentage' + index).value;
			}
			this._invoice.taxes[index].percentage = Number(value);
			this._invoice.taxes[index].quota = this.round(this._invoice.taxes[index].base / 100 * Number(value));
			this._invoice.total = this.round(this.totalImpuestos());

			document.getElementById('tax_quota' + index).value = this._invoice.taxes[index].quota;
			document.getElementById('total').value = this._invoice.total;
		}

		updateBaseImpuesto(index, value) {
				if(!value) {
					value = document.getElementById('tax_base' + index).value;
				}
				this._invoice.taxes[index].base = Number(value);
				this._invoice.taxes[index].quota = this.round(Number(value) / 100 * this._invoice.taxes[index].percentage);
				this._invoice.total = this.round(this.totalImpuestos());
				document.getElementById('total').value = this._invoice.total;
				document.getElementById('tax_quota' + index).value = this._invoice.taxes[index].quota;

		}

		updateCuotaImpuesto(index, value) {
			if (!value) {
				value = document.getElementById('tax_base' + index).value;
			}
			this._invoice.taxes[index].quota = Number(value);
			this._invoice.taxes[index].base = this.round((value * 100) / this._invoice.taxes[index].percentage);
			this._invoice.total = this.round(this.totalImpuestos());
			document.getElementById('total').value = this._invoice.total;
			document.getElementById('tax_base' + index).value = this._invoice.taxes[index].base;

		 }

		 borrarImpuesto(index) {
				this._invoice.taxes.splice(index, 1);
				this._invoice.total = this.totalImpuestos();
				this.printTaxes();
				document.getElementById('total').value = this._invoice.total;
		}

		// IRPF FUNCTIONS
		showIRPFLine(val) {
			if(!val) {
				val = document.getElementById('irpfCheckbox');
			}
			let irpfTaxe = document.getElementById('irpf-taxe');

			if (val.value && val.value === 'true') {
				this._invoice.irpf = {
					 id: 4,
					 tipo: 'IRPF',
					 percentage: 19.0,
					 base: 0,
					 quota: 0
			 	};
				let irpfTypeOptions = [{value:'IRPF', name:'IRPF'}];
				let irpfPercentageOptions = [
					{value:'19', name:'19%'},
					{value:'15', name:'15%'},
					{value:'7', name:'7%'}
				];
				let readonly = this._invoice.type === "Emitida" ? 'readonly' : '';
				let line = `
					<form action="#" class="aon-margin-0">
						<aon-select class="aonWidth25" id='irpf_type' options='${JSON.stringify(irpfTypeOptions)}' value="IRPF" description="Tipo" readonly></aon-select>
						<aon-select class="aonWidth25" id='irpf_percentage' options='${JSON.stringify(irpfPercentageOptions)}' value='${this._invoice.irpf.percentage}' description="%"></aon-select>
						<aon-input-text class="aonWidth25" id='irpf_base' value="${this._invoice.irpf.base}" description="Base Imponible" ${readonly} ></aon-input-text>
						<aon-input-text class="aonWidth25" id='irpf_quota' value="${this._invoice.irpf.quota}" description="Cuota" ${readonly}></aon-input-text>
					</form>
				`;

				irpfTaxe.innerHTML = line;

				componentHandler.upgradeDom();

				document.getElementById('irpf_percentage')
						.addEventListener('select', () => this.updateTaxeIRPF());
				if(this._invoice.type === "Recibida") {
					document.getElementById('irpf_base')
						.addEventListener('change', () => this.updateBaseIRPF());
					document.getElementById('irpf_quota')
						.addEventListener('change', () => this.updateCuotaIRPF());
				}
			} else {
				this._invoice.irpf = undefined;
				document.getElementById('total').value = this.totalImpuestos()
				irpfTaxe.innerHTML = "";
	    }
			this.printDetails();
		}

		updateTaxeIRPF(value) {
			if(!value) {
					value = document.getElementById('irpf_percentage').value;
			}
			let base = document.getElementById('irpf_base');
			let cuota = document.getElementById('irpf_quota');
			this._invoice.irpf.percentage = value;
			if (this._invoice.type == "Emitida") {
				this._invoice.irpf.base = this.totalBaseIRPF();
				this._invoice.irpf.quota = this.round(this.totalBaseIRPF() / 100 * this._invoice.irpf.percentage);
			} else if (this._invoice.type == "Recibida") {
				this._invoice.irpf.base = base.value;
				this._invoice.irpf.quota = this.round(base.value / 100 * this._invoice.irpf.percentage);
			}
				base.value = this._invoice.irpf.base;
				cuota.value = this._invoice.irpf.quota;

			// Total
			this._invoice.total = this.totalImpuestos();
			document.getElementById('total').value =this._invoice.total;
			this.printTaxes();
		}

		updateBaseIRPF() {
			let base = document.getElementById('irpf_base');
			let cuota = document.getElementById('irpf_quota');
			this._invoice.irpf.base = round(base.value);
			this._invoice.irpf.quota = round(base.value / 100 * this._invoice.irpf.percentage);
			cuota.value = this._invoice.irpf.quota;

			// Total
			this._invoice.total = this.totalImpuestos();
			document.getElementById('total').value = this._invoice.total;
		}

		updateCuotaIRPF() {
			let base = document.getElementById('irpf_base');
			let cuota = document.getElementById('irpf_quota');
			this._invoice.irpf.quota = cuota.value;
			this._invoice.irpf.base = round((cuota.value * 100) / this._invoice.irpf.percentage);
			base.value = this._invoice.irpf.base;

			// Total
			this._invoice.total = this.totalImpuestos();
			document.getElementById('total').value = this._invoice.total;
		}
		// END IRPF FUNCTIONS


		// SUPLIDOS FUNCTIONS

		showSuplidoLine(value) {
				if(!value) {
					value = document.getElementById('suplidosCheckbox');
				}
				this._invoice.suplidos = value.value === 'true';
				if (value.value && value.value === 'true') {
					document.getElementById('t-suplidos').visible = 'true';
				} else {
					document.getElementById('t-suplidos').visible = 'false';
				}

				this.printDetails();
		}

		updateSuplidos(index, value) {
			if(!value) {
				value = document.getElementById('detail_suplido' + index).value;
			}
				var taxe = this._invoice.details[index].vat;
				if (this._invoice.suplidos && value === 'true') { // Concepto con Suplidos!!
						this._invoice.details[index].suplidos = true;
						this._invoice.totalSuplidos += this._invoice.details[index].amount;

						if (this._invoice.irpf != undefined && this._invoice.details[index].irpf) {
							this._invoice.details[index].irpf = false;
							this._invoice.irpf.base = this.round(this.totalBaseIRPF());
							this._invoice.irpf.quota = this.round(this._invoice.irpf.base / 100 * this._invoice.irpf.percentage);
							document.getElementById('irpf_base').value = this._invoice.irpf.base;
							document.getElementById('irpf_quota').value = this._invoice.irpf.quota;
						}
				} else { // Concepto sin Suplidos!!
						this._invoice.details[index].irpf = this._invoice.irpf ? true : false;
						this._invoice.details[index].suplidos = false;
						this._invoice.details[index].vat = taxe;
						this._invoice.totalSuplidos -= this._invoice.details[index].amount;

						// Si tenia IRPF
						if(this._invoice.irpf) {
							this._invoice.irpf.base = this.round(this.totalBaseIRPF());
							this._invoice.irpf.quota = this.round(this._invoice.irpf.base / 100 * this._invoice.irpf.percentage);
							document.getElementById('irpf_base').value = this._invoice.irpf.base;
							document.getElementById('irpf_cuota').value = this._invoice.irpf.quota;
						}
				}

				document.getElementById('t-suplidos').value = this._invoice.totalSuplidos;
				this.createTaxe();
				this.printDetails();
				this.printTaxes();

				this._invoice.total = this.round(this.totalImpuestos() + this._invoice.totalSuplidos);
				document.getElementById('total').value = this._invoice.total;
		}

		totalSuplidos() {
			let total = 0;
			for (let i = 0; i < this._invoice.details.length; i++) {
					if (this._invoice.details[i].suplidos) {
							total += this._invoice.details[i].price;
					}
			}
			return this.round(total);
		}

		addVencimiento() {
			if(!this.hasAttribute('add_vencimiento_click')){
				this.setAttribute('add_vencimiento_click', 'true');
				this._invoice.finances.push({
						due_date: this._invoice.date,
						pay_method: 'CASH',
						amount: this._invoice.total,
						iban: ''
				});
				this.printVencimientos();
			} else {
				this.removeAttribute('add_vencimiento_click');
			}
		}

		printVencimientos() {
			let line = '';
			for(let i = 0; i < this._invoice.finances.length; i++) {
				let button = `
					<button id='${'finance_remove_button' + i}' class="mdl-button mdl-js-button mdl-button--icon">
						<i class="material-icons">remove_circle</i>
					</button>
				`;
				line = line + `
					<form action="#" class="aon-margin-0 aon-width-90">
						<aon-input-text class="aonWidth25" id='${'finance_date' + i}' value="${this._invoice.finances[i].due_date}" type="date" description="Fecha Vencimiento" ></aon-input-text>
						<aon-select class="aonWidth25" id='${'finance_paymethod' + i}' options='${JSON.stringify(Paymethods)}' value="${this._invoice.finances[i].pay_method}" description="Forma de Pago" ></aon-select>
						<aon-input-text class="aonWidth25" id='${'finance_amount' + i}' value="${this._invoice.finances[i].amount}" description="Importe"></aon-input-text>
						<aon-input-text class="aonWidth25" id='${'finance_iban' + i}' value="${this._invoice.finances[i].iban}" description="IBAN" ></aon-input-text>
					</form>
					${button}
				`;
			}

			let vto = document.getElementById('vencimiento');
			vto.innerHTML = line;
			componentHandler.upgradeDom();

			for(let i = 0; i < this._invoice.finances.length; i++) {
				document.getElementById('finance_date' + i)
					.addEventListener('change', () => this.updateFechaVencimiento(i));
				document.getElementById('finance_paymethod' + i)
					.addEventListener('select', () => this.updateFormaPagoVencimiento(i));
				document.getElementById('finance_amount' + i)
					.addEventListener('change', () => this.updateImporteVencimiento(i));
				document.getElementById('finance_iban' + i)
					.addEventListener('change', () => this.updateIbanVencimiento(i));
				document.getElementById('finance_remove_button' + i)
					.addEventListener('click', () => this.borrarVencimiento(i));
			}
		}

		// Fecha Vencimiento
		updateFechaVencimiento(index, value) {
			if(!value) {
				value = document.getElementById('finance_date' + index).value;
			}
			this._invoice.vencimientos[index].due_date = value;
		}

		// Forma Pago Vencimiento
		updateFormaPagoVencimiento(index, value) {
			if(!value) {
				value = document.getElementById('finance_paymethod' + index).value;
			}
			this._invoice.vencimientos[index].pay_method = value;
		}

		// Importe Vencimiento
		updateImporteVencimiento(index, value) {
			if(!value) {
				value = document.getElementById('finance_amount' + index).value;
			}
			this._invoice.vencimientos[index].amount = Number(value);
		}

		// Iban Vencimiento
		updateIbanVencimiento(index, value) {
			if(!value) {
				value = document.getElementById('finance_amount' + index).value;
			}
			this._invoice.vencimientos[index].iban = value;
		}

		// Delete Vencimiento
		borrarVencimiento(index) {
			this._invoice.finances.splice(index, 1);
			this.printVencimientos();
		}

		// ================================ Aux Functions  ================================ //
		existIRPF() {
				var existe = false;
				if (this._invoice.irpf != undefined) {
						existe = true;
				}
				return existe;
		}

		getIRPF() {
				return this._invoice.irpf;
		}

		existTaxeType(percent) {
				let existe = false;
				for (var i = 0; i < this._invoice.taxes.length; i++) {
						if (this._invoice.taxes[i].percentage == percent) {
								existe = true;
						}
				}
				return existe;
		}

		cantTipo(percent) {
				var cant = 0;
				for (var i = 0; i < this._invoice.details.length; i++) {
						if (this._invoice.details[i].vat == percent) {
								cant++;
						}
				}
				return cant;
		}

		totalDetailTaxe(percent) {
				var total = 0;
				for (var i = 0; i < this._invoice.details.length; i++) {
						if (this._invoice.details[i].vat == percent) {
								total += this._invoice.details[i].price;
						}
				}
				return total;
		}

		getTaxeFromType(percent) {
				for (var i = 0; i < this._invoice.taxes.length; i++) {
						if (this._invoice.taxes[i].percentage == percent) {
								return this._invoice.taxes[i];
						}
				}
		}

		totalImpuestos() {
		 	 var total = 0;
		 	 for (var i = 0; i < this._invoice.taxes.length; i++) {
		 			 total += (this._invoice.taxes[i].base + this._invoice.taxes[i].quota);
		 	 }
		 	 if (this._invoice.irpf != undefined) {
		 			 total -= this._invoice.irpf.quota;
		 	 }
		 	 return this.round(total);
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
	}

	window.customElements.define('aon-invoice',  AonInvoice);

})();
