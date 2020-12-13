import {AonElement} from '../../components/AonElement.js';
import {Transactions} from '../../services/transaction.js';
import {Paymethods} from '../../services/paymethod.js';
import {TaxType, TaxIVAPercentage, TaxIRPFPercentage, InvoiceAction} from './invoiceEnums.js';
import {getInvoiceCategories} from '../../services/invoiceCategory.js';
import {insertInvoice, deleteInvoices, getUserAppRole} from '../../services/service.js';
import {isNumber, round} from '../../services/utils.js';
import {Invoice} from './Invoice.js';
import {getNextInvoice, getPreviousInvoice} from './InvoiceCache.js';
import {ToolbarType} from '../../models/enums.js';
import '../../components/aon-card.js';
import '../../components/aon-date.js';
import '../../components/aon-select.js';
import '../../components/aon-input.js';
import '../../components/aon-number.js';
import '../../components/aon-checkbox.js';
import '../../components/aon-icon-button.js';
import '../../components/aon-switch.js';
import '../../components/aon-dialog.js';
import '../../components/aon-dialog-menu.js';
import '../../components/aon-viewer.js';

import * as CONSTANT from "../../environments/constants.js";
import * as MSG from "../../environments/msg.js";
import * as MATERIAL_ICONS from "../../environments/materialIcons.js";

export class AonInvoice extends AonElement {

	_invoice;
	_roles;
	TOOLBAR;

  static get observedAttributes() {
    return ['invoice'];
 	}

	get type() {
	  return this.getAttriute('type');
 	}

  set type(type) {
     this.setAttribute('type', type);
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
		this.id = this.id || 'aonInvoiceSheet';
		this.TOOLBAR = this.id + 'Toolbar';
		this._invoice = new Invoice(this.getAttribute('type'));
		if(this.hasAttribute('invoice')){
			this.setInvoice(JSON.parse(this.getAttribute('invoice')));
		}
  }

	getInvoice() {
		return this._invoice;
	}

	setInvoice(invoice) {
		this._invoice.createInvoice(invoice);
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-toolbar id="${this.TOOLBAR}" type="${ToolbarType.SECONDARY}" title="${this.getInvoiceTitle()}"> </aon-toolbar>

			<div style="display:flex;">
				<div id="aonInvoiceData" class="aonSubContent" style="width:100%">
					<aon-card id="aonInvoiceItemCommentsCard" title="${MSG.AON_MSG_COMMENTS}" style="display:none;"> </aon-card>
					<div id="aonInvoiceDiv" style="display:flex;">
						<aon-card id="aonInvoiceItemDataCard" title="${MSG.AON_MSG_INVOICE_DATA}" style="width:50%"> </aon-card>
						<aon-card id="aonInvoiceItemTaxesCard" title="${MSG.AON_MSG_TAXES_DETAIL}" style="width:50%"> </aon-card>
					</div>
					<aon-card id="aonInvoiceItemDetailCard" title="${MSG.AON_MSG_INVOICE_CONCEPTS}"> </aon-card>
					<aon-card id="aonInvoiceItemFinanceCard" title="${MSG.AON_MSG_EXPIRATIONS}"> </aon-card>
				</div>
				<div id="aonInvoiceFile" class="aonSubContent">
				</div>
			</div>
		`;

		getUserAppRole().then(roles => {
			this._roles = roles;
			this.build();
		});

  }

	getInvoiceTitle() {
		if(this._invoice.isEmitida()) {
			return MSG.AON_MSG_INVOICE_ISSUED;
		} else if(this._invoice.isTicket()){
			return MSG.AON_MSG_TICKET;
		} else return MSG.AON_MSG_INVOICE_RECEIVED;
	}

	build() {
		this.buildComments();
		this.buildData();
		if(!this.isTicket()){
			this.buildTaxes();
			this.buildDetail();
			this.buildFinance();
			this.printTaxes();
			this.printDetails();
			this.printFinances();
		} else {
			document.getElementById('aonInvoiceItemTaxesCard').style.display = 'none';
			document.getElementById('aonInvoiceItemDetailCard').style.display = 'none';
			document.getElementById('aonInvoiceItemFinanceCard').style.display = 'none';
		}

		if(!this.isMobile())
			this.buildInvoiceToolbar();

		if(!this._invoice.file) {
			let input = document.createElement('input');
		 	input.id = 'aonInvoiceToolbarAddFileButtonInput'
			input.style.display = 'none';
			input.type = 'file';
			input.addEventListener('change', () => this.preview());
			this.appendChild(input);
		}
	}

	buildInvoiceToolbar() {
		let aonInvoice = this.getElement('aonInvoice');
		let invoiceToolbar = this.getElement(this.TOOLBAR);
		invoiceToolbar.removeButtons();

		invoiceToolbar.addButton2(InvoiceAction.NEXT, () => this.nextInvoice());
		invoiceToolbar.addButton2(InvoiceAction.PREVIOUS, () => this.previousInvoice());

		invoiceToolbar.addSeparator();

		if(this._invoice.isInbox()) {
			invoiceToolbar.addButton('Options', 'more_vert', () => {
				let button = this.getElement(invoiceToolbar.TOOL_SECTION + 'OptionsButton');
				const top  = button.getBoundingClientRect().top;
				const left = button.getBoundingClientRect().left;
				let d = document.getElementById(aonInvoice.OPTION_DIALOG);
				let rectify = InvoiceAction.RECTIFY;
				rectify.fn = () => this.rectifyInvoice();
				let duplicate = InvoiceAction.DUPLICATE;
				duplicate.fn = () => this.duplicateInvoice();
				d.setMenuOptions([rectify, duplicate], top, left);
				d.open();
			});
			// invoiceToolbar.addButton2(InvoiceAction.DUPLICATE, () => {});
			// invoiceToolbar.addButton2(InvoiceAction.RECTIFY, () => this.printInvoice());
			// invoiceToolbar.addSeparator();
		}

		if(this._invoice.isInbox() && (this._roles.includes('ADMIN') || this._roles.includes('INVOICE_MANAGER'))) {
			invoiceToolbar.addButton2(InvoiceAction.RECORD, () => this.recordInvoice());
			invoiceToolbar.addButton2(InvoiceAction.REJECT, () => this.rejectInvoice());
			invoiceToolbar.addSeparator();
		}

		if(this._invoice.isRejected()) {
			invoiceToolbar.addButton2(InvoiceAction.DELETE, () => this.trashInvoice());
			invoiceToolbar.addButton2(InvoiceAction.RESTORE, () => this.restoreInvoice());
		} else if(this._invoice.isDraft()) {
			invoiceToolbar.addButton2(InvoiceAction.DELETE_FOREVER, () => this.removeInvoice());
			invoiceToolbar.addButton2(InvoiceAction.RESTORE, () => this.restoreInvoice());
		} else if(this._invoice.isInbox()){
			invoiceToolbar.addButton2(InvoiceAction.DELETE, () => this.trashInvoice());
			invoiceToolbar.addButton2(InvoiceAction.COMMENT, () => this.addInvoiceComment());
		}

		invoiceToolbar.addButton2(InvoiceAction.BACK, () => this.back());

		if(!this._invoice.file && !this._invoice.isEmitida()){
			invoiceToolbar.addButtonTitle(InvoiceAction.ADD_FILE, () => this.addInvoiceFile());
		} else {
			invoiceToolbar.addButtonTitle(InvoiceAction.SHOW_FILE, () => {
				let button = this.getElement(invoiceToolbar.TITLE_SECTION + 'ShowFileButton');
				let visible = 'visibility_off' === button.icon;
				let fileDiv = document.getElementById('aonInvoiceFile');
				let dataDiv = document.getElementById('aonInvoiceData');
				if(visible) {
					button.icon = 'visibility';
					fileDiv.style.display = 'none'
					dataDiv.style.width = '100%'
					document.getElementById('aonInvoiceDiv').style.display = 'flex';
					document.getElementById('aonInvoiceItemTaxesCard').style.width = '50%';
				} else {
					button.icon = 'visibility_off';
					fileDiv.style.display = 'block';
					fileDiv.style.width = '50%';
					dataDiv.style.width = '50%';
					document.getElementById('aonInvoiceDiv').style.display = 'block';
					document.getElementById('aonInvoiceItemTaxesCard').style.width = '100%';

					if(!this._invoice.file && this._invoice.isEmitida()){
						let json = btoa(JSON.stringify(this._invoice));
						let url = '/ms/api/download_invoice_pdf?json=' + json;
						fileDiv.innerHTML = `<aon-viewer type="application/pdf" file="${url}" width="${fileDiv.offsetWidth}"><aon-viewer>`;
					} else {
						fileDiv.innerHTML = `<aon-viewer type="${this._invoice.file.type}" file="${this._invoice.file.url}" width="${fileDiv.offsetWidth}"><aon-viewer>`;
					}
				}
			});
		}
	}

	preview() {
		let fileDiv = document.getElementById('aonInvoiceFile');
		let dataDiv = document.getElementById('aonInvoiceData');

		let fileInput = document.getElementById('aonInvoiceToolbarAddFileButtonInput');
		const file = fileInput.files[0];

		const READER = new FileReader();
		READER.readAsDataURL(file);
		READER.onload = (_event) => {
			fileDiv.style.display = 'block';
			fileDiv.style.width = '50%';
			dataDiv.style.width = '50%';
			document.getElementById('aonInvoiceDiv').style.display = 'block';
			document.getElementById('aonInvoiceItemTaxesCard').style.width = '100%';
			this.attach(READER.result, file.type);
			fileDiv.innerHTML = `<aon-viewer type="${file.type}" file="${READER.result}" width="${fileDiv.offsetWidth}"><aon-viewer>`;
		};
	}

	attach(fileDataUri,  mimetype){
		if (fileDataUri.length > 0) {
			const base64File = fileDataUri.split(',')[1];
			const data = {
				file: {
					content: base64File,
					contentType: mimetype,
					contentEncoding: 'base64'
				},
				invoice: this.getInvoice()
			};
			let aonInvoice = document.getElementById('aonInvoice');
			aonInvoice.startLoader();
			insertInvoice(data).then((r) => {
				aonInvoice.stopLoader();
				this._invoice.createInvoice(r);
				this.build();
			});
		}
	}

	back() {
		let aip = document.querySelector('aon-invoice-panel');
		aip.aonInvoiceList();
	}

	previousInvoice() {
		let invoice = getPreviousInvoice();
		if(invoice) {
			let aip = document.querySelector('aon-invoice-panel');
			aip.aonInvoice(invoice.type, invoice);
		}
	}

	nextInvoice() {
		let invoice = getNextInvoice();
		if(invoice) {
			let aip = document.querySelector('aon-invoice-panel');
			aip.aonInvoice(invoice.type, invoice);
		}
	}

	rejectInvoice() {
		let aonInvoice = this.getElement('aonInvoice');
		let d = document.getElementById(aonInvoice.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.AON_MSG_REJECT_INVOICE);
		d.setContentHTML('<textarea id="commentTextArea"> </textarea>');
		d.addAcceptAction(() => {
			let ta = this.getElement('commentTextArea');
			if(!ta.value.isEmpty()){
				let comment = {
					date: new Date(),
					user: '',
					comment: ta.value
				};
				this._invoice.comments.push(comment);
				this.buildComments();
			}
			this._invoice.status = CONSTANT.REFUSED;
			this.save();
			if(!this.isMobile())
				this.buildInvoiceToolbar();
		});

		let ta = this.getElement('commentTextArea');
		ta.style.outline = 'none';
		ta.style.width = '100%';
		ta.style.height = '100px';
		d.open();
	}

	trashInvoice() {
		this._invoice.status = CONSTANT.TRASH;
		this.save();
		if(!this.isMobile())
			this.buildInvoiceToolbar();
	}

	restoreInvoice() {
		this._invoice.status = CONSTANT.INBOX;
		this.save();
		if(!this.isMobile())
			this.buildInvoiceToolbar();
	}

	removeInvoice() {
		let aonInvoice = this.getElement('aonInvoice');
		deleteInvoices([this._invoice.id]).then(() => {
			let d = document.getElementById(aonInvoice.DIALOG);
			d.clear();
			if(!this.isMobile()) d.width = '400px';
			d.setTitle(MSG.AON_MSG_DELETE_FOREVER);
			d.setContentHTML(MSG.AON_MSG_DELETE_FOREVER_INVOICE_CONFIRMATION);
			d.addAcceptAction(() => this.back());
			d.open();
		});
	}

	addInvoiceFile() {
		let el = document.getElementById('aonInvoiceToolbarAddFileButtonInput');
		el.click();
	}

	rectifyInvoice() {
		let aonInvoice = this.getElement('aonInvoice');
		let d = document.getElementById(aonInvoice.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.AON_MSG_RECTIFY_INVOICE);
		d.setContentHTML('Esta opción está en desarrollo...');
		d.addAcceptAction(() => {});
		d.open();
	}

	duplicateInvoice() {
		let aonInvoice = this.getElement('aonInvoice');
		let d = document.getElementById(aonInvoice.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.AON_MSG_DUPLICATE_INVOICE);
		d.setContentHTML('Esta opción está en desarrollo...');
		d.addAcceptAction(() => {});
		d.open();
	}

	printInvoice() {
		let json = btoa(JSON.stringify(this._invoice));
		console.log(json);
		open('/ms/api/download_invoice_pdf?json=' + json);
	}

	sendInvoice() {

	}

	addInvoiceComment() {
		let aonInvoice = this.getElement('aonInvoice');
		let d = document.getElementById(aonInvoice.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.AON_MSG_ADD_COMMENT);
		d.setContentHTML('<textarea id="commentTextArea"> </textarea>');
		d.addAcceptAction(() => {
			let ta = this.getElement('commentTextArea');
			let comment = {
				date: new Date(),
				user: '',
				comment: ta.value
			};
			this._invoice.comments.push(comment);
			this.save();
			this.buildComments();
		});

		let ta = this.getElement('commentTextArea');
		ta.style.outline = 'none';
		ta.style.width = '100%';
		ta.style.height = '100px';
		d.open();
	}

	recordInvoice() {
		let aonInvoice = this.getElement('aonInvoice');
		let d = document.getElementById(aonInvoice.DIALOG);
		d.clear();
		if(!this.isMobile())d.width = '400px';
		d.setTitle(MSG.AON_MSG_RECORD_INVOICE);
		d.setContentHTML('Esta opción está en desarrollo...');
		d.addAcceptAction(() => {});
		d.open();
	}

	buildComments(){
		let card = document.getElementById('aonInvoiceItemCommentsCard');
		card.setContentHTML('');
		card.setBackground('#ffc');
		if(this._invoice.comments && this._invoice.comments.length > 0) {
			card.style.display = 'block';
			let ul = document.createElement('ul');
			ul.style.width = '100%';
			card.setContent(ul);
			this._invoice.comments.forEach((item, i) => {
				let li = this.createElement('li');
				li.style.backgrounColor = 'transparent !important';
				li.innerHTML = item.comment;
				ul.appendChild(li);
			});
		}
	}

	buildData(){
		let card = document.getElementById('aonInvoiceItemDataCard');
		card.setContentHTML('');
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
			series.addEventListener('change', () => {
				this._invoice.reference = series.value
					? series.value + '/' + this._invoice.number
					: this._invoice.number;
				this.update('series');
			});

			// NUMBER
			let tdNumber = document.createElement('td');
			tdNumber.innerHTML = `<aon-input id="number" description="Número"></aon-input>`;
			tr.appendChild(tdNumber);
			let number = document.getElementById('number');
			number.value = this._invoice.number;
			number.addEventListener('change', () => {
				this._invoice.reference = this._invoice.series
					? this._invoice.series  + '/' + number.value
					: number.value;
				this.update('number')
			});
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
		// DATE
		let tdDate = document.createElement('td');
		tdDate.innerHTML = `<aon-date id="date" title="${MSG.AON_MSG_DATE}"></aon-date>`;
		tr.appendChild(tdDate);
		let date = document.getElementById('date');
		date.setDate(this._invoice.date);
		date.addEventListener('change', () => this.update('date'));

		// TOTAL
		let tdTotal = document.createElement('td');
		tdTotal.innerHTML = `<aon-number id="total" description="${MSG.AON_MSG_TOTAL}" format="true"></aon-number>`;
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
			? JSON.stringify(this._invoice.receiver && this._invoice.receiver.address ? this._invoice.receiver.address : {})
			: JSON.stringify(this._invoice.sender && this._invoice.sender.address ? this._invoice.sender.address : {}));
		address.addEventListener('change', () => this.updateRegistry());

		let tr4 = document.createElement('tr');
		table.appendChild(tr4);

		// CATEGORY
		let tdCategory = document.createElement('td');
		tdCategory.setAttribute('colspan', '2');
		tdCategory.innerHTML = `<aon-select id="category" title="${MSG.AON_MSG_CATEGORY}"></aon-select>`;
		tr4.appendChild(tdCategory);
		let category = document.getElementById('category');
		category.options = JSON.stringify(getInvoiceCategories(this._invoice.type));
		category.value = this._invoice.category;
		category.addEventListener('select', () => this.update('category'));

		// PAYMETHOD
		let tdPaymethod = document.createElement('td');
		tdPaymethod.setAttribute('colspan', '2');
		tdPaymethod.innerHTML = `<aon-select id="pay_method" title="${MSG.AON_MSG_PAYMETHOD}"></aon-select>`;
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
		tdTransaction.setAttribute('colspan', '1');
		tdTransaction.style.width = '160px';
		tdTransaction.innerHTML = `<aon-select id="transaction" title="Tipo Transacción"></aon-select>`;
		tr1.appendChild(tdTransaction);
		let transaction = document.getElementById('transaction');
		transaction.options = JSON.stringify(Transactions);
		transaction.value = this._invoice.transaction;
		transaction.addEventListener('select', () => this.update('transaction'));

		// let tr2 = document.createElement('tr');
		// table.appendChild(tr2);

		let tdCriterioCaja = document.createElement('td');
		tdCriterioCaja.setAttribute('colspan', '1');
		tdCriterioCaja.innerHTML = `<aon-switch id="criterioCaja" title="${MSG.AON_MSG_BOX_CRITERION}"></aon-switch>`;
		tr1.appendChild(tdCriterioCaja);

		let tdSuplidos= document.createElement('td');
		tdSuplidos.setAttribute('colspan', '1');
		tdSuplidos.innerHTML = `<aon-switch id="suplidos" title="${MSG.AON_MSG_SUPPLIED}"></aon-switch>`;
		tr1.appendChild(tdSuplidos);
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
		tdConceptoSuplidos.style.width = '70%';
		tdConceptoSuplidos.setAttribute('colspan', '3');
		tdConceptoSuplidos.innerHTML = `<aon-input id="conceptoSuplidos" description="${MSG.AON_MSG_CONCEPT}"></aon-input>`;
		trSuplidos.appendChild(tdConceptoSuplidos);
		let conceptoSuplidos = this.getElement('conceptoSuplidos');
		conceptoSuplidos.value = this._invoice.suplidos.description;
		conceptoSuplidos.addEventListener('change', () => this.updateSuplidos());

		let tdTotalSuplidos = document.createElement('td');
		tdTotalSuplidos.setAttribute('colspan', '1');
		tdTotalSuplidos.innerHTML = `<aon-number id="totalSuplidos" description="${MSG.AON_MSG_TOTAL_SUPPLIED}" format="true"></aon-number>`;
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

	buildDetail(){
		let card = document.getElementById('aonInvoiceItemDetailCard');
		card.setContentHTML('');
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
		card.setContentHTML('');
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

	save() {
		insertInvoice(this.getInvoice()).then((r) => this.getInvoice().id = r.id);
	}

	update(param, value) {
		value = value || document.getElementById(param).value;
		this.getInvoice()[param] = value;
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
		alert(value);
		this._invoice.total = Number(value);
		if(!this.isTicket()) this.createTaxeFromTotal();
		this.save();
	}

	createTaxeFromTotal() {
		if (this._invoice.taxes.length === 0) {
			let tax = {
				tax: TaxType.IVA,
				type: TaxType.IVA,
				percentage: 21.0,
				base: round(Number(this._invoice.total) / 1.21),
				quota: round(Number(this._invoice.total / 1.21) * 0.21)
		 	};
			this._invoice.taxes.push(tax);
			this.getElement('total').readonly = 'readonly';
			this.printTax(tax, 0);
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
		tdDetailDescription.innerHTML = `<aon-input id="detailDescription${i}" description="${MSG.AON_MSG_CONCEPT}"></aon-input>`;
		tr.appendChild(tdDetailDescription);
		let detailDescription = document.getElementById('detailDescription' + i);
		detailDescription.value = detail.description;
		detailDescription.addEventListener('change', () => this.changeDescription(i));

		// DETAIL QUANTITY
		let tdDetailQuantity = document.createElement('td');
		tdDetailQuantity.innerHTML = `<aon-number id="detailQuantity${i}" description="${MSG.AON_MSG_QUANTITY}" format="true"></aon-number>`;
		tr.appendChild(tdDetailQuantity);
		let detailQuantity = document.getElementById('detailQuantity' + i);
		detailQuantity.value = detail.quantity;
		detailQuantity.addEventListener('change', () => this.changeQuantity(i));

		// DETAIL PRICE
		let tdDetailPrice = document.createElement('td');
		tdDetailPrice.innerHTML = `<aon-number id="detailPrice${i}" description="${MSG.AON_MSG_PRICE}" format="true"></aon-number>`;
		tr.appendChild(tdDetailPrice);
		let detailPrice = document.getElementById('detailPrice' + i);
		detailPrice.value = detail.price;
		detailPrice.addEventListener('change', () => this.changePrice(i));

		// DETAIL DISCOUNT
		let tdDetailDiscount = document.createElement('td');
		tdDetailDiscount.innerHTML = `<aon-number id="detailDiscount${i}" description="%Dto" format="true"></aon-number>`;
		tr.appendChild(tdDetailDiscount);
		let detailDiscount = document.getElementById('detailDiscount' + i);
		detailDiscount.value = detail.discount;
		detailDiscount.addEventListener('change', () => this.changeDiscount(i));

		// DETAIL AMOUNT
		let tdDetailAmount = document.createElement('td');
		tdDetailAmount.innerHTML = `<aon-number id="detailAmount${i}" description="${MSG.AON_MSG_AMOUNT}" format="true"></aon-number>`;
		tr.appendChild(tdDetailAmount);
		let detailAmount = document.getElementById('detailAmount' + i);
		detailAmount.readonly = 'readonly';
		detailAmount.value = detail.amount;

		// DETAIL VAT
		let tdDetailVat = document.createElement('td');
		tdDetailVat.innerHTML = `<aon-select id="detailVat${i}" title="%IVA"></aon-select>`;
		tr.appendChild(tdDetailVat);
		let detailVat = document.getElementById('detailVat' + i);
		detailVat.options = JSON.stringify(TaxIVAPercentage);
		detailVat.value = detail.vat;
		detailVat.addEventListener('select', () => this.changeVat(i));

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
		this.updateTaxes();
		this.save();
	}

	changePrice(index, value) {
		if(!value) {
			value = document.getElementById('detailPrice' + index).value;
		}
		this._invoice.details[index].price = Number(value);
		this.calculatePrice(index);
		this.updateTaxes();
		this.save();
	}

	changeDiscount(index, value) {
		if(!value) {
			value = document.getElementById('detailDiscount' + index).value;
		}
	 	this._invoice.details[index].discount = Number(value);
	 	this.calculatePrice(index);
		this.updateTaxes();
		this.save();
	}

	changeVat(index, value) {
		if(!value) {
			value = document.getElementById('detailVat' + index).value;
		}
		this._invoice.details[index].vat = Number(value);
		this.updateTaxes();
		this.save();
	}

	calculatePrice(index) {
		if (this._invoice.details[index].price && this._invoice.details[index].quantity) {
			let price = this._invoice.details[index].price;
			if (!isNumber(price)) {
				price = price.replace(' ', '').replace(',', '.');
			}
			let quantity = this._invoice.details[index].quantity;
			if (!isNumber(quantity)) {
				quantity = quantity.replace(' ', '').replace(',', '.');
			}
			let amount = round(Number(quantity) * Number(price));
			amount = amount - amount * (this._invoice.details[index].discount / 100);
			this._invoice.details[index].amount = round(amount);
			document.getElementById('detailAmount' + index).value = this._invoice.details[index].amount;
		}
	}

	removeDetail(index) {
		document.getElementById('detail' + index).remove();
		this._invoice.details.splice(index, 1);
		this.updateTaxes();
		this.save();
	}

	// TAXES

	addTax() {
		let i = this._invoice.taxes.length;
		let tax = {
			tax: TaxType.IVA,
			type: TaxType.IVA,
			percentage: 0.0,
			base: 0,
			quota: 0
		};
		this._invoice.taxes.push(tax);
		this.printTax(tax, i);
		this.save();
	}

	printTax(tax, i) {
		let table = TaxType.IVA === tax.type || TaxType.IVA === tax.tax
			? document.getElementById('aonInvoiceItemTaxesCardTable')
			: document.getElementById('aonInvoiceItemIRPFCardTable');

		let tr = document.createElement('tr');
		tr.id = 'tax' + i;
		table.appendChild(tr);

		// TAXTYPE
		let tdTaxType = document.createElement('td');
		tdTaxType.innerHTML = `<aon-select id="taxType${i}" title="Tipo" readonly="true"></aon-select>`;
		tr.appendChild(tdTaxType);
		let taxType = this.getElement('taxType' + i);
		taxType.setEnumOptions(TaxType);
		taxType.value = tax.type || tax.tax;

		// TAXPERCENT
		let tdTaxPercentage= document.createElement('td');
		tdTaxPercentage.innerHTML = `<aon-select id="taxPercentage${i}" title="%"></aon-select>`;
		tr.appendChild(tdTaxPercentage);
		let taxPercentage = document.getElementById('taxPercentage' + i);
		taxPercentage.options = JSON.stringify( TaxType.IVA === tax.type || TaxType.IVA === tax.tax ? TaxIVAPercentage : TaxIRPFPercentage);
		taxPercentage.value = tax.percentage;
		taxPercentage.addEventListener('select', () => this.updateTaxPercentage(i));

		// TAXBASE
		let tdTaxBase = document.createElement('td');
		tdTaxBase.innerHTML = `<aon-number id="taxBase${i}" description="Base" format="true"></aon-number>`;
		tr.appendChild(tdTaxBase);
		let taxBase = document.getElementById('taxBase' + i);
		taxBase.value = tax.base;
		taxBase.addEventListener('change', () => this.updateTaxBase(i));

		// TAXQUOTA
		let tdTaxQuota = document.createElement('td');
		tdTaxQuota.innerHTML = `<aon-number id="taxQuota${i}" description="Cuota" format="true"></aon-number>`;
		tr.appendChild(tdTaxQuota);
		let taxQuota = document.getElementById('taxQuota' + i);
		taxQuota.addEventListener('change', () => this.updateTaxQuota(i));
		taxQuota.value = tax.quota;

		// REMOVE TAX
		let tdRemoveButton = document.createElement('td');
		tdRemoveButton.innerHTML = `<aon-icon-button id="taxRemove${i}" icon="remove_circle" ></aon-icon-button>`;
		tr.appendChild(tdRemoveButton);
		let removeButton = document.getElementById('taxRemove' + i);
		removeButton.addEventListener('click', () => {
			this.removeTax(i);
			if(TaxType.IRPF === tax.type || TaxType.IRPF === tax.tax ) {
				this.getElement('irpf').checked = false;
			}
		});
	}

	printTaxes() {
		this.clearElement('aonInvoiceItemTaxesCardTable');
		this.clearElement('aonInvoiceItemIRPFCardTable');
		for(let i = 0; i < this._invoice.taxes.length; i++) {
			this.printTax(this._invoice.taxes[i], i);
		}
	}

	updateTaxPercentage(index, value) {
		if(!value) {
			value = document.getElementById('taxPercentage' + index).value;
		}
		this._invoice.taxes[index].percentage = Number(value);
		this._invoice.taxes[index].quota = round(this._invoice.taxes[index].base / 100 * Number(value));
		document.getElementById('taxQuota' + index).value = this._invoice.taxes[index].quota;
		this.updateTax();
		this.save();
	}

	updateTaxBase(index, value) {
		if(!value) {
			value = document.getElementById('taxBase' + index).value;
		}
		this._invoice.taxes[index].base = Number(value);
		this._invoice.taxes[index].quota = round(Number(value) / 100 * this._invoice.taxes[index].percentage);
		document.getElementById('taxQuota' + index).value = this._invoice.taxes[index].quota;
		this.updateTax();
		this.save();
	}

	updateTaxQuota(index, value) {
		if (!value) {
			value = document.getElementById('taxQuota' + index).value;
		}
		this._invoice.taxes[index].quota = Number(value);
		this._invoice.taxes[index].base = round((value * 100) / this._invoice.taxes[index].percentage);
		document.getElementById('taxBase' + index).value = this._invoice.taxes[index].base;
		this.updateTax();
		this.save();
	}

	removeTax(index) {
		this._invoice.taxes.splice(index, 1);
		document.getElementById('tax' + index).remove();
		this.updateTax();
		this.save();
	}

	// IRPF FUNCTIONS
	changeIRPF(val) {
		if(!val) {
			val = this.getElement('irpf').isChecked();
		}
		if (val) {
			let i = this._invoice.taxes.length;
			let base = this.totalBaseIRPF();
			let tax = {
				tax: TaxType.IRPF,
				type: TaxType.IRPF,
				percentage: 19.0,
				base: base,
				quota: round(base / 100 * 19.0)
			};
			this._invoice.taxes.push(tax);
			this.printTax(tax, i);
		} else {
			for(let i = 0; i < this._invoice.taxes.length; i++) {
				let tax = this._invoice.taxes[i];
				if(TaxType.IRPF === tax.type || TaxType.IRPF === tax.tax) {
					this._invoice.taxes.splice(i, 1);
				}
			}
			this.clearElement('aonInvoiceItemIRPFCardTable');
		}
		this.updateTaxesTotal();
		this.save();
	}

	updateIRPF() {
		for(let i = 0; i < this._invoice.taxes.length; i++) {
			let tax = this._invoice.taxes[i];
			if(TaxType.IRPF === tax.type || TaxType.IRPF === tax.type) {
				this._invoice.taxes[i].base = this.totalBaseIRPF();
				this._invoice.taxes[i].quota = round(this._invoice.taxes[i].base / 100 * this._invoice.taxes[i].percentage);
			}
		}
	}

	updateVat() {
		this._invoice.taxes = this._invoice.taxes.filter(r => TaxType.IRPF === r.type || TaxType.IRPF === r.tax)
		TaxIVAPercentage.forEach((item, i) => {
			let base = this.totalBase(item.value);
			if(base != 0) {
				let tax = {
					tax: TaxType.IVA,
					type: TaxType.IVA,
					percentage: item.value,
					base: base,
					quota:  round(base / 100 * item.value)
				}
				this._invoice.taxes.push(tax);
			}
		});
	}

	updateTaxes() {
		this.updateVat();
		this.updateIRPF();
		this.printTaxes();
		this.updateTaxesTotal();

	}

	updateTax() {
		this.updateIRPF();
		this.printTaxes();
		this.updateTaxesTotal();
	}

	updateTaxesTotal() {
		this._invoice.total = round(this.totalImpuestos() + this._invoice.suplidos.total);
		let total = this.getElement('total');
		total.value = this._invoice.total;
		if(this._invoice.taxes.length > 0) {
			total.readonly = 'readonly';
		}
	}

	updateSuplidos() {
		let suplidos = this.getElement('suplidos');
		let conceptoSuplidos = this.getElement('conceptoSuplidos');
		let totalSuplidos = this.getElement('totalSuplidos');
		if(suplidos.isChecked()) {
			this._invoice.suplidos = {
				active: suplidos.isChecked(),
				description: conceptoSuplidos.value,
				total: Number(totalSuplidos.value)
			}
		} else {
			this._invoice.suplidos = {
				active: false,
				description: '',
				total: 0
			}
		}
		this.getElement('trSuplidos').style.display = suplidos.isChecked() ? 'table-row' : 'none';
		this.updateTaxesTotal();
		this.save();
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
		tdFinanceDueDate.innerHTML = `<aon-date id="financeDueDate${i}" title="Fecha Vto" ></aon-date>`;
		tr.appendChild(tdFinanceDueDate);
		let financeDueDate = document.getElementById('financeDueDate' + i);
		financeDueDate.setDate(finance.due_date);
		financeDueDate.addEventListener('change', () => this.updateFinanceDate(i));

		// FINANCE PAYMETHOD
		let tdDetailPaymethod = document.createElement('td');
		tdDetailPaymethod.innerHTML = `<aon-select id="financePaymethod${i}" title="F. de Pago"></aon-select>`;
		tr.appendChild(tdDetailPaymethod);
		let financePaymethod = document.getElementById('financePaymethod' + i);
		financePaymethod.options = JSON.stringify(Paymethods);
		financePaymethod.value = finance.paymethod;
		financePaymethod.addEventListener('select', () => this.updateFinancePaymethod(i));

		// FINANCE AMOUNT
		let tdFinanceAmount = document.createElement('td');
		tdFinanceAmount.innerHTML = `<aon-number id="financeAmount${i}" description="${MSG.AON_MSG_AMOUNT}" format="true"></aon-number>`;
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
			value = document.getElementById('financeDueDate' + index).value;
		}
		this._invoice.finances[index].due_date = value;
		this.save();
	}

	// Forma Pago Vencimiento
	updateFinancePaymethod(index, value) {
		if(!value) {
			value = document.getElementById('financePaymethod' + index).value;
		}
		this._invoice.finances[index].paymethod = value;
		this.save();
	}

	// Importe Vencimiento
	updateFinanceAmount(index, value) {
		if(!value) {
			value = document.getElementById('financeAmount' + index).value;
		}
		this._invoice.finances[index].amount = Number(value);
		this.save();
	}

	// Iban Vencimiento
	updateFinanceIban(index, value) {
		if(!value) {
			value = document.getElementById('financeIban' + index).value;
		}
		this._invoice.finances[index].iban = value;
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
		return round(total);
	}

	totalImpuestos() {
 		let total = 0;
		for (let i = 0; i < this._invoice.taxes.length; i++) {
			if(TaxType.IVA === this._invoice.taxes[i].type || TaxType.IVA ===  this._invoice.taxes[i].tax){
				total += (this._invoice.taxes[i].base + this._invoice.taxes[i].quota);
			} else {
				total -= this._invoice.taxes[i].quota;
			}
		}
		return round(total);
	}

	totalBase(vat) {
		let total = 0;
		for (let i = 0; i < this._invoice.details.length; i++) {
			if (this._invoice.details[i].vat == vat) {
				total += this._invoice.details[i].amount;
			}
		}
		return total;
	}

	totalBaseIRPF() {
		let total = 0;
		for (let i = 0; i < this._invoice.taxes.length; i++) {
			if (TaxType.IVA === this._invoice.taxes[i].type || TaxType.IVA === this._invoice.taxes[i].tax) {
				total += this._invoice.taxes[i].base;
			}
		}
		return total;
	}

	isEmitida() {
		return this.getInvoice().isEmitida();
	}

	isRecibida() {
		return this.getInvoice().isRecibida();
	}

	isTicket() {
		return this.getInvoice().isTicket();
	}
}

window.customElements.define('aon-invoice',  AonInvoice);
