import {AonInvoice} from './aon-invoice.js';
import {insertInvoice, deleteInvoices, getUserAppRole} from '../../services/service.js';
import {getInvoiceCategories} from '../../services/invoiceCategory.js';
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
      <aon-card id="aonInvoiceItemCommentsCard" title="${MSG.AON_MSG_COMMENTS}" style="display:none;"> </aon-card>

			<aon-card id="aonInvoiceItemDataCard" title="Datos Factura"> </aon-card>
			<aon-card id="aonInvoiceItemTaxesCard" title="Detalle Impuestos"> </aon-card>
			<aon-card id="aonInvoiceItemDetailCard" title="Conceptos Factura"> </aon-card>
			<aon-card id="aonInvoiceItemFinanceCard" title="Vencimientos"> </aon-card>

			<aon-dialog-menu id="aonDialogInvoiceOption"> </aon-dialog-menu>
			<aon-dialog id="aonDialogInvoice"> </aon-dialog>
		`;

    getUserAppRole().then(roles => {
			this._roles = roles;
      this.buildOptions();
	    this.build();
		});
  }

  buildOptions() {
    let aonInvoice = this.getElement('aonInvoice');
    let aonInvoiceToolbar = this.getElement(aonInvoice.TOOLBAR);
    aonInvoice.addToolbarOption('Options', 'more_vert', () => {
      let button = this.getElement(aonInvoiceToolbar.TOOL_SECTION + 'OptionsButton');
      const top  = button.getBoundingClientRect().top;
      const left = button.getBoundingClientRect().left;

      let d = document.getElementById('aonDialogInvoiceOption');

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
      series.addEventListener('change', () => this.update('series'));

      // NUMBER
      let tdNumber = document.createElement('td');
      tdNumber.innerHTML = `<aon-input id="number" description="Número"></aon-input>`;
      tr.appendChild(tdNumber);
      let number = document.getElementById('number');
      number.value = this._invoice.number;
      number.addEventListener('change', () => this.update('number'));
    } else {
      // REFERENCE CODE
      let tdReference = document.createElement('td');
      tdReference.setAttribute('colspan', '2');
      tdReference.innerHTML = `<aon-input id="reference" description="Nº Factura"></aon-input>`;
      tr.appendChild(tdReference);
      let reference = document.getElementById('reference');
      reference.value = this._invoice.reference;
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
    date.addEventListener('change', () => this.update('date'));

    // TOTAL
    let tdTotal = document.createElement('td');
    tdTotal.innerHTML = `<aon-input id="total" description="Total"></aon-input>`;
    tr1.appendChild(tdTotal);
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
    tdAddress.setAttribute('colspan', '2');
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
    tdCategory.innerHTML = `<aon-select id="category" title="Categoría"></aon-select>`;
    tr4.appendChild(tdCategory);
    let category = document.getElementById('category');
    category.options = JSON.stringify(getInvoiceCategories(this._invoice.type));
    category.value = this._invoice.category;
    category.addEventListener('select', () => this.update('category'));

    // PAYMETHOD
    let tdPaymethod = document.createElement('td');
    tdPaymethod.innerHTML = `<aon-select id="pay_method" title="Forma de Pago"></aon-select>`;
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
    transaction.addEventListener('select', () => this.update('transaction'));

    let tr2 = document.createElement('tr');
    table.appendChild(tr2);

    let tdCriterioCaja = document.createElement('td');
    tdCriterioCaja.setAttribute('colspan', '1');
    tdCriterioCaja.innerHTML = `<aon-switch id="criterioCaja" title="${MSG.AON_MSG_BOX_CRITERION}"></aon-switch>`;
    tr2.appendChild(tdCriterioCaja);

    let tdSuplidos= document.createElement('td');
    tdSuplidos.setAttribute('colspan', '1');
    tdSuplidos.innerHTML = `<aon-switch id="suplidos" title="${MSG.AON_MSG_SUPPLIED}"></aon-switch>`;
    tr2.appendChild(tdSuplidos);
    let suplidos = this.getElement('suplidos');
    suplidos.checked = this._invoice.suplidos.active;
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
    conceptoSuplidos.addEventListener('change', () => this.updateSuplidos());

    let tdTotalSuplidos = document.createElement('td');
    tdTotalSuplidos.setAttribute('colspan', '1');
    tdTotalSuplidos.innerHTML = `<aon-input id="totalSuplidos" description="${MSG.AON_MSG_TOTAL_SUPPLIED}"></aon-input>`;
    trSuplidos.appendChild(tdTotalSuplidos);
    let totalSuplidos = this.getElement('totalSuplidos');
    totalSuplidos.value = this._invoice.suplidos.total;
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
    addButton.addEventListener('click', () => this.addTax());

    let irpf = document.getElementById('irpf');
    irpf.checked = this._invoice.taxes.filter(r => TaxType.IRPF === r.type || TaxType.IRPF === r.tax).length > 0;
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
		detailDescription.addEventListener('change', () => this.changeDescription(i));

		// DETAIL AMOUNT
		let tdDetailAmount = document.createElement('td');
		tdDetailAmount.innerHTML = `<aon-input id="detailAmount${i}" description="Importe"></aon-input>`;
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
		removeButton.addEventListener('click', () => this.removeDetail(i));
	}

	editDetail(i) {
		let detail = this._invoice.details[i];
		this.dialogDetail(detail, i);
	}

  dialogDetail(detail, i) {
    let d = document.getElementById('aonDialogInvoice');
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
    detailDescription.addEventListener('change', () => this.changeDescription(i, detailDescription.value));

    let tr2 = document.createElement('tr');
    t.appendChild(tr2);

    // DETAIL QUANTITY
    let tdDetailQuantity = document.createElement('td');
    tdDetailQuantity.setAttribute('colspan','1');
    tdDetailQuantity.innerHTML = `<aon-input id="dialogDetailQuantity${i}" description="Cantidad"></aon-input>`;
    tr2.appendChild(tdDetailQuantity);
    let detailQuantity = document.getElementById('dialogDetailQuantity' + i);
    detailQuantity.value = detail.quantity;
    detailQuantity.addEventListener('change', () => this.changeQuantity(i, detailQuantity.value));

    // DETAIL PRICE
    let tdDetailPrice = document.createElement('td');
    tdDetailPrice.setAttribute('colspan','1');
    tdDetailPrice.innerHTML = `<aon-input id="dialogDetailPrice${i}" description="Precio"></aon-input>`;
    tr2.appendChild(tdDetailPrice);
    let detailPrice = document.getElementById('dialogDetailPrice' + i);
    detailPrice.value = detail.price;
    detailPrice.addEventListener('change', () => this.changePrice(i, detailPrice.value));

    let tr3 = document.createElement('tr');
    t.appendChild(tr3);

    // DETAIL DISCOUNT
    let tdDetailDiscount = document.createElement('td');
    tdDetailDiscount.setAttribute('colspan','1');
    tdDetailDiscount.innerHTML = `<aon-input id="dialogDetailDiscount${i}" description="%Dto"></aon-input>`;
    tr3.appendChild(tdDetailDiscount);
    let detailDiscount = document.getElementById('dialogDetailDiscount' + i);
    detailDiscount.value = detail.discount;
    detailDiscount.addEventListener('change', () => this.changeDiscount(i, detailDiscount.value));

    // DETAIL VAT
    let tdDetailVat = document.createElement('td');
    tdDetailVat.setAttribute('colspan','1');
    tdDetailVat.innerHTML = `<aon-select id="dialogDetailVat${i}" title="%IVA"></aon-select>`;
    tr3.appendChild(tdDetailVat);
    let detailVat = document.getElementById('dialogDetailVat' + i);
    detailVat.options = JSON.stringify(TaxIVAPercentage);
    detailVat.value = detail.vat;
    detailVat.addEventListener('select', () => this.changeVat(i, detailVat.value));

    d.addAcceptAction(() => {});
    d.open();
  }

  dialogFinance(finance, i) {
		let d = document.getElementById('aonDialogInvoice');
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
		financePaymethod.addEventListener('select', () => this.updateFinancePaymethod(i, financePaymethod.value));

		let tr3 = document.createElement('tr');
		t.appendChild(tr3);

		// FINANCE AMOUNT
		let tdFinanceAmount = document.createElement('td');
		tdFinanceAmount.setAttribute('colspan','2');
		tdFinanceAmount.innerHTML = `<aon-input id="dialogFinanceAmount${i}" description="Importe"></aon-input>`;
		tr3.appendChild(tdFinanceAmount);
		let financeAmount = document.getElementById('dialogFinanceAmount' + i);
		financeAmount.value = finance.amount;
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
		financeDueDate.addEventListener('change', () => this.updateFinanceDate(i));

		// FINANCE AMOUNT
		let tdFinanceAmount = document.createElement('td');
		tdFinanceAmount.innerHTML = `<aon-input id="financeAmount${i}" description="Importe"></aon-input>`;
		tr.appendChild(tdFinanceAmount);
		let financeAmount = document.getElementById('financeAmount' + i);
		financeAmount.value = finance.amount;
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
		removeButton.addEventListener('click', () => this.removeFinance(i));
	}

	editFinance(i) {
		let finance = this._invoice.finances[i];
		this.dialogFinance(finance, i);
	}

}

window.customElements.define('aon-mobile-invoice',  AonMobileInvoice);
