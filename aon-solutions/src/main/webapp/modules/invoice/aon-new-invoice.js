import {AonElement} from '../../components/AonElement.js';
import {Transactions} from '../../services/transaction.js';
import {Paymethods} from '../../services/paymethod.js';
import {TaxType, TaxIVAPercentage, TaxIRPFPercentage, InvoiceAction} from './invoiceEnums.js';
import {getDomainUserRoles} from '../../services/service.js';
import {isNumber, round} from '../../services/utils.js';
import {Invoice} from './Invoice.js';
import {getNextInvoice, getPreviousInvoice} from './InvoiceCache.js';
import {ToolbarType, RegistryType} from '../../models/enums.js';
import {DomainUserRoles} from '../../models/DomainUserRoles.js';

import {AonToolbar} from '../../components/aon-toolbar.js';
import {AonCard} from '../../components/aon-card.js';
import '../../components/aon-date.js';
import '../../components/aon-select.js';
import '../../components/aon-suggestion.js';
import '../../components/aon-input.js';
import "../../components/aon-address.js";
import '../../components/aon-number.js';
import '../../components/aon-checkbox.js';
import '../../components/aon-icon-button.js';
import '../../components/aon-switch.js';
import '../../components/aon-dialog.js';
import '../../components/aon-dialog-menu.js';
import {AonViewer} from '../../components/aon-viewer.js';
import { AonBasicTable, AonInput, AonRegistry, AonSelect, AonSuggestion } from '../../components/components.js';
import { CONSTANT, CSS, MSG, TAG } from '../../environments/environments.js';

export class AonNewInvoice extends AonElement {

	invoice;
	dur;

	TOOLBAR;
	GENERAL
	GENERAL_CARD;
	COMMENT_CARD;
	INVOICE_CARD;
	DETAIL_CARD;
	FINANCE_CARD;
	DATA;
	FILE;
	;


	get id() {
		return this.getAttriute(CONSTANT.ID);
	   }
  
	set id(id) {
	   this.setAttribute(CONSTANT.ID, id);
	}

	get type() {
	  return this.getAttriute(CONSTANT.TYPE);
 	}

	set type(type) {
	    this.setAttribute(CONSTANT.TYPE, type);
  	}

	get autosave() {
		return this.getAttribute(CONSTANT.AUTOSAVE);
  	}

  	set autosave(autosave) {
		this.setAttribute(CONSTANT.AUTOSAVE, autosave);
	}

	constructor () {
   		super();
	}

	connectedCallback () {
		this.initialize();
		getDomainUserRoles({}).then(r => {
			this.dur = new DomainUserRoles(r);
			this.build();
		});
  	}

	initialize(){
		this.id = this.id || 'aonInvoiceSheet';
		this.TOOLBAR = this.id + 'Toolbar';
		this.DATA = this.id + 'Data';
		this.GENERAL = this.DATA + 'General';
		this.GENERAL_CARD = this.GENERAL + CONSTANT.CARD.initCap();
		this.GENERAL_CARD_TABLE = this.GENERAL_CARD + CONSTANT.TABLE.initCap();
		this.COMMENTS_CARD = this.DATA + 'CommentsCard';
		this.TAX_CARD = this.GENERAL + 'TaxCard';
		this.DETAIL_CARD = this.DATA + 'DetailCard';
		this.FINANCE_CARD = this.DATA + 'FinanceCard';
		this.FILE = this.id + 'File';
		this.invoice = this.invoice || new Invoice(this.getAttribute('type'));

		this.SERIE = CONSTANT.AON_INVOICE + CONSTANT.SERIE.initCap(); 
		this.NUMBER = CONSTANT.AON_INVOICE + CONSTANT.NUMBER.initCap();
		this.REFERENCE = CONSTANT.AON_INVOICE + CONSTANT.REFERENCE.initCap();
		this.DATE = CONSTANT.AON_INVOICE + CONSTANT.DATE.initCap();
		this.TOTAL = CONSTANT.AON_INVOICE + CONSTANT.TOTAL.initCap(); 
		this.REGISTRY = CONSTANT.AON_INVOICE + CONSTANT.REGISTRY.initCap();
		this.CATEGORY = CONSTANT.AON_INVOICE + CONSTANT.CATEGORY.initCap();
		this.PAYMETHOD = CONSTANT.AON_INVOICE + CONSTANT.PAYMETHOD.initCap();
	}

	getDur(){
		return this.dur;
	}

	getInvoice() {
		return this.invoice;
	}

	setInvoice(invoice) {
		this.invoice = this.invoice || new Invoice(this.getAttribute('type'));
		this.invoice.createInvoice(invoice);
	}

	build() {
		this.buildToolbar();
		this.buildContent();
	}

	buildToolbar() {
		let invoiceToolbar = new AonToolbar();
		invoiceToolbar.id = this.TOOLBAR;
		invoiceToolbar.type = ToolbarType.SECONDARY;
		invoiceToolbar.title = this.getInvoiceTitle();
		this.appendChild(invoiceToolbar);
		invoiceToolbar.removeButtons();
		invoiceToolbar.addButton2(InvoiceAction.NEXT, () => this.nextInvoice());
		invoiceToolbar.addButton2(InvoiceAction.PREVIOUS, () => this.previousInvoice());
		invoiceToolbar.addSeparator();
		invoiceToolbar.addButton('Options', 'more_vert', (e) => this.more(e));

		if(this.getInvoice().isInbox() && this.getDur().isInvoiceManager()) {
			invoiceToolbar.addButton2(InvoiceAction.RECORD, () => this.recordInvoice());
			invoiceToolbar.addButton2(InvoiceAction.REJECT, () => this.rejectInvoice());
			invoiceToolbar.addSeparator();
		}
		if(this.getInvoice().isRejected()) {
			invoiceToolbar.addButton2(InvoiceAction.DELETE, () => this.trashInvoice());
			invoiceToolbar.addButton2(InvoiceAction.RESTORE, () => this.restoreInvoice());
		} else if(this.getInvoice().isDraft()) {
			invoiceToolbar.addButton2(InvoiceAction.DELETE_FOREVER, () => this.removeInvoice());
			invoiceToolbar.addButton2(InvoiceAction.RESTORE, () => this.restoreInvoice());
		} else if(this.getInvoice().isInbox()){
			invoiceToolbar.addButton2(InvoiceAction.DELETE, () => this.trashInvoice());
			invoiceToolbar.addButton2(InvoiceAction.COMMENT, () => this.addInvoiceComment());
		}
		invoiceToolbar.addButton2(InvoiceAction.BACK, () => this.back());
		if(!this.getInvoice().file && !this._invoice.isEmitida()){
			invoiceToolbar.addButtonTitle(InvoiceAction.ADD_FILE, () => this.addInvoiceFile());
		} else {
			invoiceToolbar.addButtonTitle(InvoiceAction.SHOW_FILE, () => this.showFile());
		}
	}

	buildContent() {
		let div = this.createElement(TAG.DIV);
		div.className = CSS.AON_FLEX;
		this.appendChild(div);

		let data = this.createElement(TAG.DIV);
		data.id = this.DATA;
		data.className = CSS.AON_SUB_CONTENT;
		data.style.width = '100%';
		div.appendChild(data);

		this.buildCommentCard(data);

		let general = this.createElement(TAG.DIV);
		general.id = this.GENERAL;
		general.className = CSS.AON_FLEX;
		data.appendChild(general);

		this.buildGeneralCard(general);
		this.buildTaxCard(general);

		this.buildDetailCard(data);
		this.buildFinanceCard(data);

		let file = this.createElement(TAG.DIV);
		file.id  = this.FILE;
		file.className = CSS.AON_SUB_CONTENT;
		div.appendChild(file);
	}

	buildCommentCard(parent) {
		let card = new AonCard();
		card.id = this.COMMENT_CARD;
		card.title = MSG.COMMENTS;
		card.className = CSS.AON_NONE;
		parent.appendChild(card);
	}

	buildGeneralCard(parent) {
		let card = new AonCard();
		card.id = this.GENERAL_CARD;
		card.title = MSG.TITULAR_DATA;
		card.style.width = '50%';
		parent.appendChild(card);

		let table = new AonBasicTable();
		table.id = this.GENERAL_CARD_TABLE;
		card.setContent(table);

		table.addRow(); // ----- ROW 1
		if(this.invoice.isEmitida()){

			// ----- SERIE

			let serie = new AonInput();
			serie.id = this.SERIE;
			serie.description = MSG.SERIE;
			serie.value = this.invoice.serie;
			serie.readonly = this.invoice.isReadonly();
			serie.addEventListener(EVENT.CHANGE, () => {
				this.invoice.setSerie(serie.value);
				if(this.autosave) this.save();
			});
			table.addCell(serie);

			// ----- NUMBER

			let number = new AonInput();
			number.id = this.NUMBER;
			number.description = MSG.NUMBER;
			number.value = this.invoice.number;
			number.readonly = CONSTANT.READONLY;
			table.addCell(number);
		} else {

			// ----- REFERENCE

			let reference = new AonInput();
			reference.id = this.REFERENCE;
			reference.description = MSG.REFERENCE;
			reference.value = this.invoice.reference;
			reference.readonly = this.invoice.isReadonly();
			reference.addEventListener(EVENT.CHANGE, () => {
				this.invoice.setReference(reference.value);
				if(this.autosave) this.save();
			});
			table.addCell(reference, 2);
		}

		// ----- DATE

		let date = new AonDate();
		date.id = this.DATE;
		date.title = MSG.DATE;
		date.value = this.invoice.date;
		date.readonly = this.invoice.isReadonly();
		date.addEventListener(EVENT.CHANGE, () => {
			this.invoice.setDate(date.value);
			if(this.autosave) this.save();
		});
		table.addCell(date, this.invoice.isEmitida() ? 1 : 2);

		// ----- TOTAL

		let total = new AonNumber();
		total.id = this.TOTAL;
		total.description = MSG.TOTAL;
		total.format = true;
		total.decimals = 2;
		total.value = this.invoice.total;
		total.readonly = this.invoice.isReadonly();
		total.addEventListener(EVENT.CHANGE, () => {
			this.invoice.setTotal(total.value);
			if(this.autosave) this.save();
		});
		table.addCell(date, this.invoice.isEmitida() ? 1 : 2);

		table.addRow(); // ----- ROW 2

		// ----- REGISTRY

		let registry = new AonRegistry();
		registry.id = this.REGISTRY;
		registry.type = 'CUSTOMER'; // CUSTOMER | SUPPLIER | CREDITOR
		registry.value = this.invoice.getRegistry();
		registry.readonly = this.invoice.isReadonly();
		registry.addEventListener(EVENT.CHANGE, () => {
			this.invoice.setRegistry(registry.value);
			if(this.autosave) this.save();
		});
		table.addCell(registry, this.invoice.isEmitida() ? 4 : 6);

		table.addRow(); // ----- ROW 3

		// ----- CATEGORY

		let category = new AonSelect();
		category.id = this.CATEGORY;
		category.title = MSG.CATEGORY;
		category.autocomplete = true;
		category.options = accounts // DATOS INICIALES AL CARGAR FACTURA!!!
		category.value = this.invoice.getCategory();
		category.readonly = this.invoice.isReadonly();
		category.addEventListener(EVENT.SELECT, () => {
			this.invoice.setCategory(category.value);
			if(this.autosave) this.save();
		});
		table.addCell(category, this.invoice.isEmitida() ? 2 : 3)

		let paymethod = new AonSelect();
		paymethod.id = this.PAYMETHOD;
		paymethod.title = MSG.PAYMETHOD;
		paymethod.autocomplete = true;
		paymethod.options = accounts // DATOS INICIALES AL CARGAR FACTURA!!!
		paymethod.value = this.invoice.getPaymethod();
		paymethod.readonly = this.invoice.isReadonly();
		paymethod.addEventListener(EVENT.SELECT, () => {
			this.invoice.setPaymethod(paymethod.value);
			if(this.autosave) this.save();
		});
		table.addCell(paymethod, this.invoice.isEmitida() ? 2 : 3)
	}	

	buildTaxCard(parent) {
		let card = new AonCard();
		card.id = this.INVOICE_CARD;
		card.title = MSG.INVOICE_DATA;
		card.style.width = '50%';
		parent.appendChild(card);
	}

	buildDetailCard(parent) {
		let card = new AonCard();
		card.id = this.DETAIL_CARD;
		card.title = MSG.INVOICE_CONCEPTS;
		parent.appendChild(card);
	}

	buildFinanceCard(parent) {
		let card = new AonCard();
		card.id = this.FINANCE_CARD;
		card.title = MSG.EXPIRATIONS;
		parent.appendChild(card);
	}

	getInvoiceTitle() {
		if(this.isEmitida()) {
			return MSG.INVOICE_ISSUED;
		} else if(this.isTicket()){
			return MSG.TICKET;
		} else return MSG.INVOICE_RECEIVED;
	}

	back() {
		this.getApplication().getParent().aonInvoiceList();
	}

	more(e) {
		e.preventDefault();
		let rect = e.target.getBoundingClientRect();
		let x = e.clientX - rect.left;
		let y = e.clientY - rect.top;

		const top  = rect.top + y;
		const left = rect.left + x;

		let d = document.getElementById(this.getApplication().getOptionDialog());
		let rectify = InvoiceAction.RECTIFY;
		rectify.fn = () => this.rectifyInvoice();
		let duplicate = InvoiceAction.DUPLICATE;
		duplicate.fn = () => this.duplicateInvoice();
		d.setMenuOptions([rectify, duplicate], top, left);
		d.open();
	}

	showFile(){
		let invoiceToolbar = this.getElement(this.TOOLBAR);
		let button = this.getElement(invoiceToolbar.TITLE_SECTION + 'ShowFileButton');
		let visible = 'visibility_off' === button.icon;
		let fileDiv = this.getElement(this.FILE);
		let dataDiv = this.getElement(this.DATA);
		if(visible) {
			button.icon = 'visibility';
			fileDiv.style.display = 'none'
			dataDiv.style.width = '100%'
			this.getElement(this.GENERAL).style.display = 'flex';
		} else {
			button.icon = 'visibility_off';
			fileDiv.style.display = 'block';
			fileDiv.style.width = '50%';
			dataDiv.style.width = '50%';
			this.getElement(this.GENERAL).style.display = 'block';

			this.clearElement(fileDiv);
			let viewer = new AonViewer();
			viewer.type = !this.getInvoice().file && this.getInvoice().isEmitida()
				? 'application/pdf' : this.getInvoice().file.type;
			viewer.file = !this.getInvoice().file && this.getInvoice().isEmitida()
				? '/ms/api/download_invoice_pdf?json=' + btoa(JSON.stringify(this.getInvoice()))
				: this.getInvoice().file.url;
			viewer.width = fileDiv.offsetWidth;
			fileDiv.appendChild(viewer);
		}
	}

	previousInvoice() {
		let invoice = getPreviousInvoice();
		if(invoice) {
			this.getApplication().getParent().aonInvoice(invoice.type, invoice);
		}
	}

	nextInvoice() {
		let invoice = getNextInvoice();
		if(invoice) {
			this.getApplication().getParent().aonInvoice(invoice.type, invoice);
		}
	}

	rejectInvoice() {
		let d = document.getElementById(this.getApplication().getDialog());
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.REJECT_INVOICE);
		d.setContentHTML('<textarea id="commentTextArea"> </textarea>');
		d.addAcceptAction(() => {
			let ta = this.getElement('commentTextArea');
			if(!ta.value.isEmpty()){
				let dt = new Date()
				let m = dt.getMonth() + 1;
				let month = m < 10 ? '0' + m : m;
				let dateStr = dt.getDay() + '/' + month  + '/' + dt.getYear() + ' ' + dt.getHours() + ':' + dt.getMinutes() + ':' + dt.getSeconds();
				let comment = {
					date: dateStr,
					user: '',
					status:  'Rechazado',
					reason: ta.value
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
		this.getInvoice().status = CONSTANT.TRASH;
		this.save();
		if(!this.isMobile())
			this.buildInvoiceToolbar();
	}

	restoreInvoice() {
		this.getInvoice().status = CONSTANT.INBOX;
		this.save();
		if(!this.isMobile())
			this.buildInvoiceToolbar();
	}

	removeInvoice() {
		deleteInvoices([this.getInvoice().id]).then(() => {
			let d = document.getElementById(this.getApplication().getDialog());
			d.clear();
			if(!this.isMobile()) d.width = '400px';
			d.setTitle(MSG.DELETE_FOREVER);
			d.setContentHTML(MSG.DELETE_FOREVER_INVOICE_CONFIRMATION);
			d.addAcceptAction(() => this.back());
			d.open();
		});
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

	isAccounting() {
		return this.getInvoice().isAccounting();
	}
}

window.customElements.define('aon-new-invoice',  AonNewInvoice);
