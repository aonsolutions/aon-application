import {AonInvoice} from './aon-invoice.js';
import {insertInvoice, deleteInvoices, getUserAppRole, getGlobalRegistries, getInvoiceAccounts} from '../../services/service.js';
import {Transactions} from '../../services/transaction.js';
import {Paymethods} from '../../services/paymethod.js';
import {TaxType, TaxIVAPercentage, TaxIRPFPercentage, InvoiceAction} from './invoiceEnums.js';
import {isNumber, round} from '../../services/utils.js';
import {Invoice} from './Invoice.js';

import * as CONSTANT from "../../environments/constants.js";
import * as MSG from "../../environments/msg.js";
import * as MATERIAL_ICONS from "../../environments/materialIcons.js";

export class AonMobileInvoice extends AonInvoice {

  constructor () {
    super();
  }

  connectedCallback () {
    this.innerHTML = `
      <aon-card id="aonInvoiceItemFileCard" title="${MSG.AON_MSG_FILE}" style="display:none;"> </aon-card>

      <aon-card id="aonInvoiceItemCommentsCard" title="${MSG.AON_MSG_COMMENTS}" style="display:none;"> </aon-card>

			<aon-card id="aonInvoiceItemDataCard" title="Datos Factura"> </aon-card>
			<aon-card id="aonInvoiceItemTaxesCard" title="Detalle Impuestos"> </aon-card>
			<aon-card id="aonInvoiceItemDetailCard" title="Conceptos Factura"> </aon-card>
			<aon-card id="aonInvoiceItemFinanceCard" title="Vencimientos"> </aon-card>

		`;

    getUserAppRole().then(roles => {
			this._roles = roles;
      this.buildOptions();
	    this.build();
		});

    if(this._invoice.file || this._invoice.isEmitida()) {
      let fileCard = this.getElement('aonInvoiceItemFileCard');
      fileCard.style.display = 'block';
      this.getElement(fileCard.TITLE).style.marginBottom = '0px';
      fileCard.cleanSection2();
      fileCard.addTitleButton('Visualizar', 'visibility', false, () => this.openFileCard());
    }
  }

  openFileCard() {
    let fileCard = this.getElement('aonInvoiceItemFileCard');
    let w = this.getElement(fileCard.CONTENT).offsetWidth;
    if(!this._invoice.file && this._invoice.isEmitida()){
      let json = btoa(JSON.stringify(this._invoice));
      let url = '/ms/api/download_invoice_pdf?json=' + json;
      fileCard.setContentHTML(`<aon-viewer type="application/pdf" file="${url}" width="${w}"><aon-viewer>`);
    } else {
      fileCard.setContentHTML(`<aon-viewer type="${this._invoice.file.type}" file="${this._invoice.file.url}" width="${w}"><aon-viewer>`);
    }
    fileCard.cleanSection2();
    fileCard.addTitleButton('Visualizar', 'visibility_off', false, () => this.closeFileCard());
  }

  closeFileCard() {
    let fileCard = this.getElement('aonInvoiceItemFileCard');
    fileCard.setContentHTML('');
    fileCard.cleanSection2();
    fileCard.addTitleButton('Visualizar', 'visibility', false, () => this.openFileCard());
  }


  buildOptions() {
    let aonInvoice = this.getElement('aonInvoice');
    let aonInvoiceToolbar = this.getElement(aonInvoice.TOOLBAR);
    aonInvoiceToolbar.removeButtons();
    aonInvoice.addToolbarOption('Options', 'more_vert', () => {
      let button = this.getElement(aonInvoiceToolbar.TOOL_SECTION + 'OptionsButton');
      const top  = button.getBoundingClientRect().top;
      const left = button.getBoundingClientRect().left;

      let d = document.getElementById(aonInvoice.OPTION_DIALOG);

      let record = InvoiceAction.RECORD;
      record.fn = () => this.recordInvoice();

      let reject = InvoiceAction.REJECT;
      reject.fn = () => this.rejectInvoice();

      let restore = InvoiceAction.RESTORE;
      restore.fn = () => this.restoreInvoice();

      let addComment = InvoiceAction.COMMENT;
      addComment.fn = () => this.addInvoiceComment();

      let deleteInvoice = InvoiceAction.DELETE;
      deleteInvoice.fn = () => this.trashInvoice();

      let deleteForever = InvoiceAction.DELETE_FOREVER;
      deleteForever.fn = () => this.removeInvoice();

      let rectify = InvoiceAction.RECTIFY;
      rectify.fn = () => this.rectifyInvoice();

      let duplicate = InvoiceAction.DUPLICATE;
      duplicate.fn = () => this.duplicateInvoice();

      let addFile = InvoiceAction.ADD_FILE;
      addFile.fn = () => this.addInvoiceFile();

      let actions = [];
      if(this._invoice.isRejected()) {
        actions = [restore, deleteInvoice];
      } else if(this._invoice.isDraft()) {
        actions = [restore, deleteForever];
      }  else if(this._invoice.isInbox()){
          if(this._roles.includes('ADMIN') || this._roles.includes('INVOICE_MANAGER')){
            actions = [addComment, deleteInvoice, reject, record, rectify, duplicate];
          } else {
            actions = [addComment, deleteInvoice, rectify, duplicate];
          }
      }
      if(!this._invoice.file && !this._invoice.isEmitida()){
        actions.push(addFile);
      }

      d.setMenuOptions(actions, top, left);
      d.open();
    });
  }

  buildData(){
    let card = document.getElementById('aonInvoiceItemDataCard');
    let table = document.createElement('table');
    table.style.width = '100%';
    card.setContent(table);

    let tr = document.createElement('tr');
    table.appendChild(tr);

    if(this.isEmitida()) {
      // SERIE
      let tdSeries = document.createElement('td');
      tdSeries.innerHTML = `<aon-input id="series" description="Serie"></aon-input>`;
      tr.appendChild(tdSeries);
      let series = document.getElementById('series');
      series.value = this._invoice.series;
      if(this.isAccounting()) {
        series.readonly = 'readonly';
      }
      series.addEventListener('change', () => this.update('series'));

      // NUMBER
      let tdNumber = document.createElement('td');
      tdNumber.innerHTML = `<aon-input id="number" description="Número"></aon-input>`;
      tr.appendChild(tdNumber);
      let number = document.getElementById('number');
      number.value = this._invoice.number;
      if(this.isAccounting()) {
        number.readonly = 'readonly';
      }
      number.addEventListener('change', () => this.update('number'));
    } else {
      // REFERENCE CODE
      let tdReference = document.createElement('td');
      tdReference.setAttribute('colspan', '2');
      tdReference.innerHTML = `<aon-input id="reference" description="Nº Factura"></aon-input>`;
      tr.appendChild(tdReference);
      let reference = document.getElementById('reference');
      reference.value = this._invoice.reference;
      if(this.isAccounting()) {
        reference.readonly = 'readonly';
      }
      reference.addEventListener('change', () => this.update('reference'));
    }

    let tr1 = document.createElement('tr');
    table.appendChild(tr1);

    // DATE
    let tdDate = document.createElement('td');
    tdDate.innerHTML = `<aon-date id="date" title="Fecha"></aon-date>`;
    tr1.appendChild(tdDate);
    let date = document.getElementById('date');
    date.setDate(this._invoice.date);
    if(this.isAccounting()) {
      date.readonly = 'readonly';
    }
    date.addEventListener('change', () => this.update('date'));

    // TOTAL
    let tdTotal = document.createElement('td');
    tdTotal.innerHTML = `<aon-number id="total" description="Total" format="true" decimals="2"></aon-number>`;
    tr1.appendChild(tdTotal);
    let total = document.getElementById('total');
    total.value = this._invoice.total;
    if(this.isAccounting() || this._invoice.taxes.length > 1) {
      total.readonly = 'readonly';
    }
    total.addEventListener('change', () => this.updateTotal(total.value));

    let tr2 = document.createElement('tr');
    table.appendChild(tr2);

    // NIF
    let tdNif = document.createElement('td');
    tdNif.innerHTML = `<aon-suggestion id="nif" title="NIF"></aon-suggestion>`;
    tr2.appendChild(tdNif);
    let nif = document.getElementById('nif');
    nif.value = this.isEmitida()
      ? (this._invoice.receiver ? this._invoice.receiver.document : '')
      : (this._invoice.sender ? this._invoice.sender.document : '');
    if(this.isAccounting()) {
      nif.readonly = 'readonly';
    }
    nif.addEventListener('keyup', () => {
      if(nif.value.length > 2) {
        getGlobalRegistries({document:nif.value}).then( registries =>
          nif.buildOptions(registries.map(r => {return {name: r.document, value: r.document, registry: r};}))
        );
      } else {
        nif.closeOptions();
      }
    });
    nif.addEventListener('select', (event) => {
      let registry = event.detail.registry;
      let name = document.getElementById('name');
      name.value = registry.name;
      let address = document.getElementById('address');
      address.buildAddressValue(JSON.stringify(registry.address));
      if(this._invoice.isEmitida) {
        this._invoice.receiver = registry;
      } else {
        this._invoice.sender = registry;
      }
      this.save();
    });
    nif.addEventListener('change', () => this.updateRegistry());

    // NAME
    let tdName = document.createElement('td');
    tdName.innerHTML = `<aon-suggestion id="name" title="Razón Social"></aon-suggestion>`;
    tr2.appendChild(tdName);
    let name = document.getElementById('name');
    name.value = this.isEmitida()
      ? (this._invoice.receiver ? this._invoice.receiver.name : '')
      : (this._invoice.sender ? this._invoice.sender.name : '');
    if(this.isAccounting()) {
      name.readonly = 'readonly';
    }
    name.addEventListener('keyup', () => {
      if(name.value.length > 2) {
        getGlobalRegistries({name:name.value}).then( registries =>
          name.buildOptions(registries.map(r => {return {name: r.name, value: r.name, registry: r};}))
        );
      } else {
        name.closeOptions();
      }
    });
    name.addEventListener('select', (event) => {
      let registry = event.detail.registry;
      let nif = document.getElementById('nif');
      nif.value = registry.document;
      let address = document.getElementById('address');
      address.buildAddressValue(JSON.stringify(registry.address));
      if(this._invoice.isEmitida) {
        this._invoice.receiver = registry;
      } else {
        this._invoice.sender = registry;
      }
      this.save();
    });
    name.addEventListener('change', () => this.updateRegistry());

    let tr3 = document.createElement('tr');
    table.appendChild(tr3);

    // ADDRESS
    let tdAddress = document.createElement('td');
    tdAddress.setAttribute('colspan', '2');
    tdAddress.innerHTML = `<aon-input id="address" type="address" description="Dirección"></aon-input>`;
    tr3.appendChild(tdAddress);
    let address = document.getElementById('address');
    address.buildAddressValue(this.isEmitida()
      ? JSON.stringify(this._invoice.receiver ? this._invoice.receiver.address : {})
      : JSON.stringify(this._invoice.sender ? this._invoice.sender.address : {}));
    if(this.isAccounting()) {
      address.readonly = 'readonly';
    }
    address.addEventListener('change', () => this.updateRegistry());

    let tr4 = document.createElement('tr');
    table.appendChild(tr4);

    // CATEGORY
    let tdCategory = document.createElement('td');
    tdCategory.setAttribute('colspan', '2');
    tdCategory.innerHTML = `<aon-select id="category" title="${MSG.AON_MSG_CATEGORY}" autocomplete="true"></aon-select>`;
    tr4.appendChild(tdCategory);
    getInvoiceAccounts({type: this._invoice.getInvoiceType()}).then(accounts => {
			let accs = accounts.map(acc => {return {name: acc.name, value: acc.code};});
			let category = document.getElementById('category');
			category.options = JSON.stringify(accs);
			category.value = this._invoice.category;
			if(this.isAccounting()) {
				category.readonly = 'readonly';
			}
+
			category.addEventListener('select', () => this.update('category'));
		});

    let tr5 = document.createElement('tr');
    table.appendChild(tr5);
    // PAYMETHOD
    let tdPaymethod = document.createElement('td');
    tdPaymethod.setAttribute('colspan', '2');
    tdPaymethod.innerHTML = `<aon-select id="pay_method" title="Forma de Pago"></aon-select>`;
    tr5.appendChild(tdPaymethod);
    let paymethod = document.getElementById('pay_method');
    paymethod.options = JSON.stringify(Paymethods);
    if(this._invoice.finances.length === 1) {
      paymethod.value = this._invoice.finances[0].paymethod;
    }
    if(this.isAccounting()) {
      paymethod.readonly = 'readonly';
    }
    paymethod.addEventListener('select', () =>  {
      if(this._invoice.finances.length < 1)
        this.addFinance(document.getElementById('pay_method').value);
    });
  }

  buildTaxes(){
    let card = document.getElementById('aonInvoiceItemTaxesCard');
    card.setContentHTML('');
    let div = document.createElement('div');
    card.setContent(div);

    let table = document.createElement('table');
    table.style.width = '100%';
    div.appendChild(table);

    let tr1 = document.createElement('tr');
    table.appendChild(tr1);

    // TRANSACTION
    let tdTransaction = document.createElement('td');
    tdTransaction.setAttribute('colspan', '2');
    tdTransaction.style.width = '160px';
    tdTransaction.innerHTML = `<aon-select id="transaction" title="Tipo Transacción"></aon-select>`;
    tr1.appendChild(tdTransaction);
    let transaction = document.getElementById('transaction');
    transaction.options = JSON.stringify(Transactions);
    transaction.value = this._invoice.transaction;
    if(this.isAccounting()) {
			transaction.readonly = 'readonly';
		}
    transaction.addEventListener('select', () => this.update('transaction'));

    let tr2 = document.createElement('tr');
    table.appendChild(tr2);

    let tdCriterioCaja = document.createElement('td');
    tdCriterioCaja.setAttribute('colspan', '1');
    tdCriterioCaja.innerHTML = `<aon-switch id="criterioCaja" title="${MSG.AON_MSG_BOX_CRITERION}"></aon-switch>`;
    tr2.appendChild(tdCriterioCaja);
    let criterioCaja = this.getElement('criterioCaja');
    if(this.isAccounting()) {
      criterioCaja.disabled = 'true';
    }

    let tdSuplidos= document.createElement('td');
    tdSuplidos.setAttribute('colspan', '1');
    tdSuplidos.innerHTML = `<aon-switch id="suplidos" title="${MSG.AON_MSG_SUPPLIED}"></aon-switch>`;
    tr2.appendChild(tdSuplidos);
    let suplidos = this.getElement('suplidos');
    suplidos.checked = this._invoice.suplidos.active;
    if(this.isAccounting()) {
			suplidos.disabled = 'true';
		}
    suplidos.addEventListener('change', () => this.updateSuplidos());

    let tableSuplidos = document.createElement('table');
    tableSuplidos.style.width = '100%';
    div.appendChild(tableSuplidos);

    let trSuplidos = document.createElement('tr');
    trSuplidos.id = 'trSuplidos';
    trSuplidos.style.display = this._invoice.suplidos.active ? 'table-row' : 'none';
    tableSuplidos.appendChild(trSuplidos);

    let tdConceptoSuplidos = document.createElement('td');
    tdConceptoSuplidos.style.width = '65%';
    tdConceptoSuplidos.setAttribute('colspan', '3');
    tdConceptoSuplidos.innerHTML = `<aon-input id="conceptoSuplidos" description="${MSG.AON_MSG_CONCEPT}"></aon-input>`;
    trSuplidos.appendChild(tdConceptoSuplidos);
    let conceptoSuplidos = this.getElement('conceptoSuplidos');
    conceptoSuplidos.value = this._invoice.suplidos.description;
    if(this.isAccounting()) {
			conceptoSuplidos.readonly = 'readonly';
		}
    conceptoSuplidos.addEventListener('change', () => this.updateSuplidos());

    let tdTotalSuplidos = document.createElement('td');
    tdTotalSuplidos.setAttribute('colspan', '1');
    tdTotalSuplidos.innerHTML = `<aon-number id="totalSuplidos" description="${MSG.AON_MSG_TOTAL_SUPPLIED}" format="true" decimals="2"></aon-number>`;
    trSuplidos.appendChild(tdTotalSuplidos);
    let totalSuplidos = this.getElement('totalSuplidos');
    totalSuplidos.value = this._invoice.suplidos.total;
    if(this.isAccounting()) {
			totalSuplidos.readonly = 'readonly';
		}
    totalSuplidos.addEventListener('change', () => this.updateSuplidos());

    let taxesTable = document.createElement('table');
    taxesTable.id = 'aonInvoiceItemTaxesCardTable';
    taxesTable.style.width = '100%';
    div.appendChild(taxesTable);

    let irpfTable = document.createElement('table');
    irpfTable.id = 'aonInvoiceItemIRPFCardTable';
    irpfTable.style.width = '100%';
    div.appendChild(irpfTable);

    let addDiv = document.createElement('div');
    div.appendChild(addDiv);

    addDiv.innerHTML = `
      <aon-icon-button id="aonInvoiceItemTaxesCardAddButton" icon="add"> </aon-icon-button>
      <aon-switch id="irpf" title="IRPF" style="position:absolute;margin-top: 10px; margin-left:10px;"></aon-switch>
      `;

    let addButton = document.getElementById('aonInvoiceItemTaxesCardAddButton');
    addButton.addEventListener('click', () => {
      if(!this.isAccounting()) {
        this.addTax()
      }
    });

    let irpf = document.getElementById('irpf');
    irpf.checked = this._invoice.taxes.filter(r => TaxType.IRPF === r.type || TaxType.IRPF === r.tax).length > 0;
    if(this.isAccounting()) {
      irpf.disabled = 'true';
    }
    irpf.addEventListener('change', () => this.changeIRPF());
  }


  printDetail(detail, i) {
		let table = document.getElementById('aonInvoiceItemDetailCardTable');

		let tr = document.createElement('tr');
		tr.id = 'detail' + i;
		table.appendChild(tr);

		// DETAIL DESCRIPTION
		let tdDetailDescription = document.createElement('td');
		tdDetailDescription.setAttribute('colspan','3');
		tdDetailDescription.style.width = '50%';
		tdDetailDescription.innerHTML = `<aon-input id="detailDescription${i}" description="Concepto"></aon-input>`;
		tr.appendChild(tdDetailDescription);
		let detailDescription = document.getElementById('detailDescription' + i);
		detailDescription.value = detail.description;
    if(this.isAccounting()) {
			detailDescription.readonly = 'readonly';
		}
		detailDescription.addEventListener('change', () => this.changeDescription(i));

		// DETAIL AMOUNT
		let tdDetailAmount = document.createElement('td');
		tdDetailAmount.innerHTML = `<aon-number id="detailAmount${i}" description="Importe" format="true"  decimals="2"></aon-number>`;
		tr.appendChild(tdDetailAmount);
		let detailAmount = document.getElementById('detailAmount' + i);
		detailAmount.readonly = 'readonly';
		detailAmount.value = detail.amount;

		// REMOVER DETAIL
		let tdEditButton = document.createElement('td');
		tdEditButton.innerHTML = `<aon-icon-button id="detailEdit${i}" icon="edit" ></aon-icon-button>`;
		tr.appendChild(tdEditButton);
		let editButton = document.getElementById('detailEdit' + i);
		editButton.addEventListener('click', () => this.editDetail(i));

		// REMOVER DETAIL
		let tdRemoveButton = document.createElement('td');
		tdRemoveButton.innerHTML = `<aon-icon-button id="detailRemove${i}" icon="remove_circle" ></aon-icon-button>`;
		tr.appendChild(tdRemoveButton);
		let removeButton = document.getElementById('detailRemove' + i);
    removeButton.addEventListener('click', () => {
      if(!this.isAccounting()) {
        this.removeDetail(i)
      }
    });
	}

	editDetail(i) {
		let detail = this._invoice.details[i];
		this.dialogDetail(detail, i);
	}

  dialogDetail(detail, i) {
    let aonInvoice = this.getElement('aonInvoice');
    let d = document.getElementById(aonInvoice.DIALOG);
    d.clear();
    d.setTitle(MSG.AON_MSG_INVOICE_CONCEPTS);

    let t = document.createElement('table');
    t.style.width = '100%';
    d.setContent(t);

    let tr1 = document.createElement('tr');
    t.appendChild(tr1);

    let tdDetailDescription = document.createElement('td');
    tdDetailDescription.setAttribute('colspan','2');
    tdDetailDescription.innerHTML = `<aon-input id="dialogDetailDescription${i}" description="Concepto"></aon-input>`;
    tr1.appendChild(tdDetailDescription);
    let detailDescription = document.getElementById('dialogDetailDescription' + i);
    detailDescription.value = detail.description;
    if(this.isAccounting()) {
			detailDescription.readonly = 'readonly';
		}
    detailDescription.addEventListener('change', () => this.changeDescription(i, detailDescription.value));

    let tr2 = document.createElement('tr');
    t.appendChild(tr2);

    // DETAIL QUANTITY
    let tdDetailQuantity = document.createElement('td');
    tdDetailQuantity.setAttribute('colspan','1');
    tdDetailQuantity.innerHTML = `<aon-number id="dialogDetailQuantity${i}" description="Cantidad" format="true" decimals="2"></aon-number>`;
    tr2.appendChild(tdDetailQuantity);
    let detailQuantity = document.getElementById('dialogDetailQuantity' + i);
    detailQuantity.value = detail.quantity;
    if(this.isAccounting()) {
			detailQuantity.readonly = 'readonly';
		}
    detailQuantity.addEventListener('change', () => this.changeQuantity(i, detailQuantity.value));

    // DETAIL PRICE
    let tdDetailPrice = document.createElement('td');
    tdDetailPrice.setAttribute('colspan','1');
    tdDetailPrice.innerHTML = `<aon-number id="dialogDetailPrice${i}" description="Precio" format="true" decimals="2"></aon-number>`;
    tr2.appendChild(tdDetailPrice);
    let detailPrice = document.getElementById('dialogDetailPrice' + i);
    detailPrice.value = detail.price;
    if(this.isAccounting()) {
			detailPrice.readonly = 'readonly';
		}
    detailPrice.addEventListener('change', () => this.changePrice(i, detailPrice.value));

    let tr3 = document.createElement('tr');
    t.appendChild(tr3);

    // DETAIL DISCOUNT
    let tdDetailDiscount = document.createElement('td');
    tdDetailDiscount.setAttribute('colspan','1');
    tdDetailDiscount.innerHTML = `<aon-number id="dialogDetailDiscount${i}" description="%Dto" format="true" decimals="2"></aon-number>`;
    tr3.appendChild(tdDetailDiscount);
    let detailDiscount = document.getElementById('dialogDetailDiscount' + i);
    detailDiscount.value = detail.discount;
    if(this.isAccounting()) {
			detailDiscount.readonly = 'readonly';
		}
    detailDiscount.addEventListener('change', () => this.changeDiscount(i, detailDiscount.value));

    // DETAIL VAT
    let tdDetailVat = document.createElement('td');
    tdDetailVat.setAttribute('colspan','1');
    tdDetailVat.innerHTML = `<aon-select id="dialogDetailVat${i}" title="%IVA"></aon-select>`;
    tr3.appendChild(tdDetailVat);
    let detailVat = document.getElementById('dialogDetailVat' + i);
    detailVat.options = JSON.stringify(TaxIVAPercentage);
    detailVat.value = detail.vat;
    if(this.isAccounting()) {
			detailVat.readonly = 'readonly';
		}
    detailVat.addEventListener('select', () => this.changeVat(i, detailVat.value));

    d.addAcceptAction(() => {});
    d.open();
  }

  dialogFinance(finance, i) {
    let aonInvoice = this.getElement('aonInvoice');
		let d = document.getElementById(aonInvoice.DIALOG);
    d.clear();
		let t = document.createElement('table');
		t.style.width = '100%';
		d.setContent(t);

		let tr = document.createElement('tr');
		t.appendChild(tr);

		let td = document.createElement('td');
		td.setAttribute('colspan','2');
		td.innerHTML =  'VENCIMIENTOS';
		tr.appendChild(td);

		let tr1 = document.createElement('tr');
		t.appendChild(tr1);

		// FINANCE DUE DATE
		let tdFinanceDueDate = document.createElement('td');
		tdFinanceDueDate.setAttribute('colspan','2');
		tdFinanceDueDate.innerHTML = `<aon-date id="dialogFinanceDueDate${i}" title="Fecha Vencimiento" ></aon-date>`;
		tr1.appendChild(tdFinanceDueDate);
		let financeDueDate = document.getElementById('dialogFinanceDueDate' + i);
		financeDueDate.setDate(finance.due_date);
    if(this.isAccounting()) {
      financeDueDate.readonly = 'readonly';
    }
		financeDueDate.addEventListener('change', () => this.updateFinanceDate(i, financeDueDate.value));

		let tr2 = document.createElement('tr');
		t.appendChild(tr2);

		// FINANCE PAYMETHOD
		let tdDetailPaymethod = document.createElement('td');
		tdDetailPaymethod.setAttribute('colspan','2');
		tdDetailPaymethod.innerHTML = `<aon-select id="dialogFinancePaymethod${i}" title="Forma de Pago"></aon-select>`;
		tr2.appendChild(tdDetailPaymethod);
		let financePaymethod = document.getElementById('dialogFinancePaymethod' + i);
		financePaymethod.options = JSON.stringify(Paymethods);
		financePaymethod.value = finance.paymethod;
    if(this.isAccounting()) {
			financePaymethod.readonly = 'readonly';
		}
		financePaymethod.addEventListener('select', () => this.updateFinancePaymethod(i, financePaymethod.value));

		let tr3 = document.createElement('tr');
		t.appendChild(tr3);

		// FINANCE AMOUNT
		let tdFinanceAmount = document.createElement('td');
		tdFinanceAmount.setAttribute('colspan','2');
		tdFinanceAmount.innerHTML = `<aon-number id="dialogFinanceAmount${i}" description="Importe" format="true" decimals="2"></aon-number>`;
		tr3.appendChild(tdFinanceAmount);
		let financeAmount = document.getElementById('dialogFinanceAmount' + i);
		financeAmount.value = finance.amount;
    if(this.isAccounting()) {
			financeAmount.readonly = 'readonly';
		}
		financeAmount.addEventListener('change', () => this.updateFinanceAmount(i, financeAmount.value));

		let tr4 = document.createElement('tr');
		t.appendChild(tr4);

		// FINANCE IBAN
		let tdFinanceIban = document.createElement('td');
		tdFinanceIban.setAttribute('colspan','2');
		tdFinanceIban.innerHTML = `<aon-input id="dialogFinanceIban${i}" description="IBAN"></aon-input>`;
		tr4.appendChild(tdFinanceIban);
		let financeIban = document.getElementById('dialogFinanceIban' + i);
		financeIban.value = finance.iban;
    if(this.isAccounting()) {
      financeIban.readonly = 'readonly';
    }
		financeIban.addEventListener('change', () => this.updateFinanceIban(i, financeIban.value));

    d.addAcceptAction(() => {});
		d.open();
	}

	printFinance(finance, i) {
		let table = document.getElementById('aonInvoiceItemFinanceCardTable');
		let tr = document.createElement('tr');
		tr.id = 'finance' + i;
		table.appendChild(tr);

		// FINANCE DUE DATE
		let tdFinanceDueDate = document.createElement('td');
		tdFinanceDueDate.innerHTML = `<aon-date id="financeDueDate${i}" title="Fecha Vencimiento"></aon-date>`;
		tr.appendChild(tdFinanceDueDate);
		let financeDueDate = document.getElementById('financeDueDate' + i);
		financeDueDate.setDate(finance.due_date);
    if(this.isAccounting()) {
      financeDueDate.readonly = 'readonly';
    }
		financeDueDate.addEventListener('change', () => this.updateFinanceDate(i));

		// FINANCE AMOUNT
		let tdFinanceAmount = document.createElement('td');
		tdFinanceAmount.innerHTML = `<aon-number id="financeAmount${i}" description="Importe" format="true" decimals="2"></aon-number>`;
		tr.appendChild(tdFinanceAmount);
		let financeAmount = document.getElementById('financeAmount' + i);
		financeAmount.value = finance.amount;
    if(this.isAccounting()) {
      financeAmount.readonly = 'readonly';
    }
		financeAmount.addEventListener('change', () => this.updateFinanceAmount(i));

		// REMOVER DETAIL
		let tdEditButton = document.createElement('td');
		tdEditButton.innerHTML = `<aon-icon-button id="financeEdit${i}" icon="edit" ></aon-icon-button>`;
		tr.appendChild(tdEditButton);
		let editButton = document.getElementById('financeEdit' + i);
		editButton.addEventListener('click', () => this.editFinance(i));

		// REMOVE FINANCE
		let tdRemoveButton = document.createElement('td');
		tdRemoveButton.innerHTML = `<aon-icon-button id="financeRemove${i}" icon="remove_circle" ></aon-icon-button>`;
		tr.appendChild(tdRemoveButton);
		let removeButton = document.getElementById('financeRemove' + i);
    removeButton.addEventListener('click', () => {
      if(!this.isAccounting()) {
        this.removeFinance(i);
      }
    });
  }

	editFinance(i) {
		let finance = this._invoice.finances[i];
		this.dialogFinance(finance, i);
	}

}

window.customElements.define('aon-mobile-invoice',  AonMobileInvoice);
