import { AonElement } from '../../components/AonElement.js';
import { getDomainUserRoles, getInvoice, getInvoiceAccounts, insertInvoice, acceptInvoice} from '../../services/service.js';
import { Invoice } from './Invoice.js';
import { getNextInvoice, getPreviousInvoice } from './InvoiceCache.js';
import { ToolbarType } from '../../models/enums.js';
import { DomainUserRoles } from '../../models/DomainUserRoles.js';
 
import { AonToolbar } from '../../components/aon-toolbar.js';
import { AonCard } from '../../components/aon-card.js';
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
import { AonViewer } from '../../components/aon-viewer.js';
import { AonBasicTable, AonDate, AonDialog, AonIconButton, AonInput, AonNumber, AonRegistry, AonSelect, AonSuggestion, AonSwitch } from '../../components/components.js';
import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js';

import * as ACTION from '../actions.js';
import { Paymethods } from '../../services/paymethod.js';
import { DIV } from '../../environments/aonTag.js';
import { Transactions } from '../../services/transaction.js';
import { getTaxPercentageOption, getTaxTypeName, TaxIRPFPercentage, TaxIVAPercentage, TaxType, TaxVATPercentage } from './invoiceEnums.js';
import { getItems} from '../../services/productService.js';

export class AonNewInvoice extends AonElement {

	invoice;
	dur;
	focusId;
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
		return this.getAttribute(CONSTANT.ID);
	   }
  
	set id(id) {
	   this.setAttribute(CONSTANT.ID, id);
	}

	get type() {
	  return this.getAttribute(CONSTANT.TYPE);
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
		this.COMMENT_CARD = this.DATA + 'CommentsCard';
		this.FILE = this.id + 'File';
		this.invoice = this.invoice || new Invoice(this.getAttribute('type'));

		this.SERIE = CONSTANT.AON_INVOICE + CONSTANT.SERIE.initCap(); 
		this.SERVICE = CONSTANT.AON_INVOICE + CONSTANT.SERVICE.initCap();
		this.INVESTMENT = CONSTANT.AON_INVOICE + CONSTANT.INVESTMENT.initCap();
		this.RECTIFIED = CONSTANT.AON_INVOICE + CONSTANT.RECTIFIED.initCap();
		this.NUMBER = CONSTANT.AON_INVOICE + CONSTANT.NUMBER.initCap();
		this.REFERENCE = CONSTANT.AON_INVOICE + CONSTANT.REFERENCE.initCap();
		this.DATE = CONSTANT.AON_INVOICE + CONSTANT.DATE.initCap();
		this.TOTAL = CONSTANT.AON_INVOICE + CONSTANT.TOTAL.initCap(); 
		this.REGISTRY = CONSTANT.AON_INVOICE + CONSTANT.REGISTRY.initCap();
		this.CATEGORY = CONSTANT.AON_INVOICE + CONSTANT.CATEGORY.initCap();
		this.PAYMETHOD = CONSTANT.AON_INVOICE + CONSTANT.PAYMETHOD.initCap();
		this.DIALOG_BLANK = CONSTANT.AON_INVOICE + 'DialogBlank';


		// ----- TAX

		this.TAX = CONSTANT.AON_INVOICE_TAX;
		this.TAX_TABLE = this.TAX + CONSTANT.TABLE.initCap();
		this.TAX_TABLE2 = this.TAX_TABLE + '2';
		
		this.TRANSACTION_TYPE = CONSTANT.AON_INVOICE + CONSTANT.TRANSACTION_TYPE.initCap(); 
		this.SURCHARGE = CONSTANT.AON_INVOICE + CONSTANT.SURCHARGE.initCap();
		this.WITHHOLDING = CONSTANT.AON_INVOICE + CONSTANT.WITHHOLDING.initCap();
		this.WITHHOLDING_FARMER = CONSTANT.AON_INVOICE + CONSTANT.WITHHOLDING_FARMER.initCap();

		this.TAX_TYPE = this.TAX + CONSTANT.TYPE.initCap();
		this.TAX_PERCENTAGE = this.TAX + CONSTANT.PERCENTAGE.initCap();
		this.TAX_BASE = this.TAX + CONSTANT.BASE.initCap();
		this.TAX_QUOTA = this.TAX  + CONSTANT.QUOTA.initCap();
		this.TAX_DELETE = this.TAX  + CONSTANT.DELETE.initCap();
		this.TAX_ADD = this.TAX  + CONSTANT.ADD.initCap();
		
		// ----- DETAIL

		this.DETAIL = CONSTANT.AON_INVOICE_DETAIL;
		this.DETAIL_TABLE = this.DETAIL + CONSTANT.TABLE.initCap();
		this.DETAIL_ADD = this.DETAIL + CONSTANT.ADD.initCap();
		this.DETAIL_DELETE = this.DETAIL + CONSTANT.DELETE.initCap();
		this.DETAIL_OPTIONS = this.DETAIL + CONSTANT.OPTIONS.initCap();
		this.DETAIL_DESCRIPTION = this.DETAIL + CONSTANT.DESCRIPTION.initCap();
		this.DETAIL_QUANTITY = this.DETAIL + CONSTANT.QUANTITY.initCap();
		this.DETAIL_PRICE = this.DETAIL + CONSTANT.PRICE.initCap();
		this.DETAIL_DISCOUNT = this.DETAIL + CONSTANT.DISCOUNT.initCap();
		this.DETAIL_AMOUNT = this.DETAIL + CONSTANT.AMOUNT.initCap();
		this.DETAIL_VAT = this.DETAIL + CONSTANT.VAT.initCap();
		this.DETAIL_WITHHOLDING = this.DETAIL + CONSTANT.WITHHOLDING.initCap();
		this.DETAIL_PREPAYMENT = this.DETAIL + CONSTANT.PREPAYMENT.initCap();
		this.DETAIL_CATEGORY = this.DETAIL + CONSTANT.CATEGORY.initCap();
		// TODO BIEN AFECTO

		// ----- FINANCE

		this.FINANCE = CONSTANT.AON_INVOICE_FINANCE;
		this.FINANCE_TABLE = this.FINANCE + CONSTANT.TABLE.initCap();
		this.FINANCE_ADD = this.FINANCE + CONSTANT.ADD.initCap();
		this.FINANCE_DUE_DATE = this.FINANCE + CONSTANT.DUE_DATE.initCap();
		this.FINANCE_PAYMETHOD = this.FINANCE + CONSTANT.PAYMETHOD.initCap();
		this.FINANCE_BANK_ACCOUNT = this.FINANCE + CONSTANT.BANK_ACCOUNT.initCap();
		this.FINANCE_AMOUNT = this.FINANCE + CONSTANT.AMOUNT.initCap();
		this.FINANCE_DELETE = this.FINANCE + CONSTANT.DELETE.initCap();
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
		this.clear();
		this.buildToolbar();
		this.buildContent();
		this.focus();
	}

	reload(){
		this.clear();
		this.build();
	}

	setFocus(focusId) {
		this.focusId = focusId; 
	}

	focus() {
		console.log(this.focusId);
		if(this.focusId)
			this.getElement(this.focusId).focus();	
	}

	buildToolbar() {
		let invoiceToolbar = new AonToolbar();
		invoiceToolbar.id = this.TOOLBAR;
		invoiceToolbar.type = ToolbarType.SECONDARY;
		invoiceToolbar.title = this.getInvoiceTitle();
		this.appendChild(invoiceToolbar);
		invoiceToolbar.removeButtons();
		invoiceToolbar.addButton2(ACTION.NEXT, () => this.nextInvoice());
		invoiceToolbar.addButton2(ACTION.PREVIOUS, () => this.previousInvoice());
		invoiceToolbar.addSeparator();
		if(this.getInvoice().isInbox() || this.getInvoice().isPending() || this.getInvoice().isScored()){
			invoiceToolbar.addButton2(ACTION.DUPLICATE, () => this.duplicateInvoice());
			invoiceToolbar.addButton2(ACTION.RECTIFY, () => this.rectifyInvoice());			
		//	invoiceToolbar.addButton('Options', 'more_vert', (e) => this.more(e));
			invoiceToolbar.addSeparator();
		}

		if(this.getInvoice().isInbox() && this.getDur().isInvoiceManager()) {
			invoiceToolbar.addButton2(ACTION.RECORD, () => this.recordInvoice());
			invoiceToolbar.addButton2(ACTION.ACCEPT, () => this.acceptInvoice());
			invoiceToolbar.addButton2(ACTION.REJECT, () => this.rejectInvoice());
			invoiceToolbar.addSeparator();
		}
		
		if(this.getInvoice().isPending() && this.getDur().isInvoiceManager()) {
			invoiceToolbar.addButton2(ACTION.RECORD, () => this.recordInvoice());
			invoiceToolbar.addButton2(ACTION.REJECT, () => this.rejectInvoice());
			invoiceToolbar.addSeparator();
		}

		if(this.getInvoice().isRejected()) {
			invoiceToolbar.addButton2(ACTION.DELETE, () => this.trashInvoice());
			invoiceToolbar.addButton2(ACTION.RESTORE, () => this.restoreInvoice());
		} else if(this.getInvoice().isDraft()) {
			invoiceToolbar.addButton2(ACTION.DELETE_FOREVER, () => this.removeInvoice());
			invoiceToolbar.addButton2(ACTION.RESTORE, () => this.restoreInvoice());
		} else if(this.getInvoice().isInbox() || this.getInvoice().isPending()){
			invoiceToolbar.addButton2(ACTION.DELETE, () => this.trashInvoice());
			invoiceToolbar.addButton2(ACTION.COMMENT, () => this.addInvoiceComment());
			if(!this.autosave && this.getInvoice().isInbox()){
				invoiceToolbar.addButton2(ACTION.SAVE, () => this.save());
			}
		} 
		invoiceToolbar.addButton2(ACTION.BACK, () => this.back());
		if(!this.getInvoice().file && !this.invoice.isEmitida()){
			invoiceToolbar.addButtonTitle(ACTION.ADD_FILE, () => this.addInvoiceFile());
		} else {
			invoiceToolbar.addButtonTitle(ACTION.SHOW_FILE, () => this.showFile());
		}
	}

	buildContent() {
		let form = this.createElement(TAG.FORM);
		this.appendChild(form);
		let div = this.createElement(TAG.DIV);
		div.className = CSS.AON_FLEX;
		form.appendChild(div);

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
		let hasComment = this.invoice.comments && this.invoice.comments.length > 0;

		let card = new AonCard();
		card.id = this.COMMENT_CARD;
		card.title = MSG.COMMENTS;
		if(!hasComment) card.className = CSS.AON_NONE;
		parent.appendChild(card);

		card.setContentHTML('');
		card.setBackground('#ffc');

		if(hasComment) {
			let ul = this.createElement(TAG.UL);
			ul.style.width = '100%';
			card.setContent(ul);
			this.invoice.comments.forEach((item, i) => {
				if(item.reason) {
					let li = this.createElement(TAG.LI);
					li.style.backgrounColor = 'transparent !important';
					li.innerHTML = item.reason;
					ul.appendChild(li);
				}
			});
		}
	}

	buildGeneralCard(parent) {
		let card = new AonCard();
		card.id = this.GENERAL_CARD;
		card.title = MSG.INVOICE_DATA;
		card.style.width = '50%';
		parent.appendChild(card);

		let dialog = new AonDialog();
		dialog.id = this.DIALOG_BLANK;
		dialog.type = CONSTANT.BLANK;
		this.appendChild(dialog);
		card.addTitleButton(MSG.OPTIONS, MATERIAL_ICONS.MORE_VERT, false, () => {
			let div = this.createElement(TAG.DIV);
			div.style.margin = '15px';
			let button  = this.getElement(card.TITLE_SECTION2 + MSG.OPTIONS + 'Button');

			// ----- SERVICE
			
			let service = new AonSwitch();
			service.id = this.SERVICE;
			service.title = MSG.SERVICE;
			service.readonly = this.invoice.isReadonly();
			service.checked = this.invoice.isService();
			div.appendChild(service);
			service.addEventListener(EVENT.CHANGE, () => {
				this.invoice.setService(service.checked);
			});

			// ----- BIENES INVERSION
			
			let investment = new AonSwitch();
			investment.id = this.INVESTMENT;
			investment.title = MSG.INVESTMENT;
			investment.readonly = this.invoice.isReadonly();
			investment.checked = this.invoice.isInvestment();
			div.appendChild(investment);
			investment.addEventListener(EVENT.CHANGE, () => {
				this.invoice.setInvestment(investment.checked);
			});


			// ----- RECTIFICATIVA
			
			let rectified = new AonSwitch();
			rectified.id = this.RECTIFIED;
			rectified.title = MSG.RECTIFIED;
			rectified.readonly = this.invoice.isReadonly();
			div.appendChild(rectified);
			rectified.addEventListener(EVENT.CHANGE, () => {
				this.invoice.setRectified(rectified.checked);
			});
			rectified.checked = this.invoice.isRectified();
			 
			const top  = button.getBoundingClientRect().top;
			const left = button.getBoundingClientRect().left;
			dialog.setContent(div, top, left);
			dialog.open();
		});

		let table = new AonBasicTable();
		table.id = this.GENERAL_CARD_TABLE;
		card.setContent(table);

		table.addRow(); // ----- ROW 1
		if(this.invoice.isEmitida()){

			// ----- SERIE

			let serie = new AonInput();
			serie.id = this.SERIE;
			serie.description = MSG.SERIE;
			table.addCell(serie);
			serie.readonly = this.invoice.isReadonly();
			serie.value = this.invoice.serie;
			serie.addEventListener(EVENT.CHANGE, () => {
				this.invoice.setSerie(serie.value);
				if(this.autosave) this.save();
			});

			// ----- NUMBER

			let number = new AonInput();
			number.id = this.NUMBER;
			number.description = MSG.NUMBER;
			number.value = this.invoice.number;
			
			table.addCell(number);
			number.readonly = CONSTANT.READONLY;
			
		} else {

			// ----- REFERENCE
			
			let reference = new AonInput();
			reference.id = this.REFERENCE;
			reference.description = MSG.REFERENCE;
			reference.value = this.invoice.reference;
			// reference.readonly = this.invoice.isReadonly();
			reference.addEventListener(EVENT.CHANGE, () => {
				this.invoice.setReference(reference.value);
				if(this.autosave) this.save();
			});
			table.addCell(reference, '2');
		}

		// ----- DATE

		let date = new AonDate();
		date.id = this.DATE;
		date.title = MSG.DATE;

		date.readonly = this.invoice.isReadonly();
		date.addEventListener(EVENT.CHANGE, () => {
			this.invoice.setDate(date.value);
			if(this.autosave) this.save();
		});
		table.addCell(date, this.invoice.isEmitida() ? '1' : '2');
		date.value = this.invoice.date;
		// ----- TOTAL

		let total = new AonNumber();
		total.id = this.TOTAL;
		total.description = MSG.TOTAL;
		total.format = CONSTANT.TRUE;
		total.decimals = "2";
		total.addEventListener(EVENT.CHANGE, () => {
			this.invoice.setTotal(total.value);
			this.setFocus(total.id);
			this.reload();
			if(this.autosave) this.save();
		});
		table.addCell(total, this.invoice.isEmitida() ? '1' : '2');
		total.readonly = this.invoice.isReadonly()
			|| this.invoice.taxes.length > 1 
			|| this.invoice.details.length > 0;
		total.value = this.invoice.total;

		table.addRow(); // ----- ROW 2

		// ----- REGISTRY

		let registry = new AonRegistry();
		registry.id = this.REGISTRY;
		registry.types = this.invoice.getRegistryType();
		registry.value = this.invoice.getRegistry();
		registry.setRegistry(this.invoice.getRegistry());
		registry.readonly = this.invoice.isReadonly();
		registry.addEventListener(EVENT.CHANGE, () => {
			this.invoice.setRegistry(registry.getRegistry());
			if(this.autosave) this.save();
		});
		registry.addEventListener(EVENT.SELECT, () => {
			this.invoice.setRegistry(registry.getRegistry());
			if(this.autosave) this.save();
		});
		table.addCell(registry, this.invoice.isEmitida() ? '4' : '6');

		table.addRow(); // ----- ROW 3

		// ----- CATEGORY

		let category = new AonSelect();
		category.id = this.CATEGORY;
		category.title = MSG.CATEGORY;
		category.autocomplete = true;
		//category.readonly = this.invoice.isReadonly();
		category.addEventListener(EVENT.SELECT, () => {
			this.invoice.setCategory(category.value);
			if(this.autosave) this.save();
		});
		table.addCell(category, this.invoice.isEmitida() ? '2' : '3');
		getInvoiceAccounts({type: this.invoice.getInvoiceType()}).then(accounts => {
			let accs = accounts.map(acc => {return {name: acc.name, value: acc.code};});
			category.options = JSON.stringify(accs);
			category.value = this.invoice.getCategory();
		});

		// ----- PAYMETHOD

		let paymethod = new AonSelect();
		paymethod.id = this.PAYMETHOD;
		paymethod.title = MSG.PAYMETHOD;
		paymethod.autocomplete = true;
		paymethod.options = JSON.stringify(Paymethods);
		paymethod.readonly = this.invoice.isReadonly();
		paymethod.addEventListener(EVENT.SELECT, () => {
			this.invoice.setPaymethod(paymethod.value);
			this.setFocus(paymethod.id);
			this.reload();
			if(this.autosave) this.save();
		});
		table.addCell(paymethod, this.invoice.isEmitida() ? '2' : '3')
		if(this.invoice.finances.length === 1) {
			paymethod.value = this.invoice.finances[0].paymethod;
		}
	}	

	buildTaxCard(parent) {
		let card = new AonCard();
		card.id = this.TAX;
		card.title = MSG.TAXES_DETAIL;
		card.style.width = '50%';
		parent.appendChild(card);

		let table = new AonBasicTable();
		table.id = this.TAX_TABLE;
		card.setContent(table);

		table.addRow(); // ----- ROW 1

		// ----- TRANSACTION TYPE

		let transaction = new AonSelect();
		transaction.id = this.TRANSACTION_TYPE;
		transaction.title = MSG.TRANSACTION_TYPE;
		transaction.options = JSON.stringify(Transactions);
		transaction.value = this.invoice.transaction;
		// transaction.readonly = this.invoice.isReadonly();
		transaction.addEventListener(EVENT.SELECT, () => {
			this.invoice.setTransaction(transaction.value);
			if(this.autosave) this.save();
		});
		table.addCell(transaction, '2');

		// ----- SURCHARGE

		let surcharge = new AonSwitch();
		surcharge.id = this.SURCHARGE;
		surcharge.title = MSG.SURCHARGE_RE;
		surcharge.checked = this.invoice.isSurcharge();
		surcharge.readonly = this.invoice.isReadonly();
		surcharge.addEventListener(EVENT.CHANGE, () => {
			this.invoice.setSurcharge(surcharge.checked);
			this.setFocus(surcharge.id);
			this.reload();
			if(this.autosave) this.save();
		});
		table.addCell(surcharge);
		
		// ----- WITHHOLDING FARMER

		let farmer = new AonSwitch();
		farmer.id = this.WITHHOLDING_FARMER;
		farmer.title = MSG.WITHHOLDING_FARMER;
		farmer.checked = this.invoice.isWithholdingFarmer();
		farmer.readonly = this.invoice.isReadonly();
		farmer.addEventListener(EVENT.CHANGE, () => {
			this.invoice.setWithholdingFarmer(farmer.checked);
			this.setFocus(farmer.id);
			this.reload();
			if(this.autosave) this.save();
		});
		table.addCell(farmer);

		// ----- TAXES
		
		let taxesTable = new AonBasicTable();
		taxesTable.id = this.TAX_TABLE2;
		card.addContent(taxesTable);

		for(let i = 0; i < this.invoice.taxes.length; i++) {
			let tax = this.invoice.taxes[i];
			if(TaxType.IVA === tax.tax) 
				this.printTax(taxesTable, tax, i);
		}

		for(let i = 0; i < this.invoice.taxes.length; i++) {
			let tax = this.invoice.taxes[i];
			if(TaxType.IRPF === tax.tax) {
				this.invoice.withholding = true;
				this.printTax(taxesTable, tax, i);
			}
		}

		let div = this.createElement(TAG.DIV);
		card.addContent(div);
		
		if(!this.invoice.isReadonly() && this.invoice.details.length === 0) {
			// ----- ADD TAX

			let addButton = new AonIconButton();
			addButton.id = this.TAX_ADD;
			addButton.title = MSG.ADD_TAX;
			addButton.icon = MATERIAL_ICONS.ADD;
			addButton.addEventListener('click', () => {
				this.setFocus(this.TAX_TYPE + this.invoice.taxes.length);
				this.invoice.addTax();
				this.reload();
				if(this.autosave) this.save();
			});
			div.appendChild(addButton);
		}
		
		// ----- WITHHOLDING

		let irpf = new AonSwitch();
		irpf.id = this.WITHHOLDING;
		irpf.title = MSG.IRPF; // MSG.WITHHOLDING;
		if(!this.invoice.isReadonly() && this.invoice.details.length === 0)
			irpf.style.position = 'absolute';
		irpf.style.marginTop = '10px';
		irpf.style.marginLeft = '10px';
		irpf.checked = this.invoice.taxes.filter(r => TaxType.IRPF === r.type || TaxType.IRPF === r.tax).length > 0;
		irpf.readonly = this.invoice.isReadonly() || this.invoice.details.length > 0;
		irpf.addEventListener(EVENT.CHANGE, () => {
			this.invoice.setWithholding(irpf.checked); 
			this.reload();
			if(this.autosave) this.save();
		});
		div.appendChild(irpf);
	}

	printTax(taxesTable, tax, i) {
		taxesTable.addRow(); // ----- ROW i

		// ----- TAX TYPE

		tax.type = tax.type || tax.tax;
		let taxType = new AonInput();
		taxType.id = this.TAX_TYPE + i;
		taxType.description = MSG.TYPE;
		taxesTable.addCell(taxType);
		taxType.readonly = CONSTANT.TRUE;
		taxType.value = getTaxTypeName(tax.type);

		// ----- TAX PERCENT

		let percentage = new AonSelect();
		percentage.id = this.TAX_PERCENTAGE + i;
		percentage.title = '%';
		percentage.options = JSON.stringify(getTaxPercentageOption(tax.type));
		percentage.addEventListener(EVENT.SELECT, () => {
			tax.percentage = percentage.value;
			this.invoice.setTax(tax, i);
			this.reload();
			if(this.autosave) this.save();
		});
		taxesTable.addCell(percentage);
		percentage.readonly = this.invoice.isReadonly() || this.invoice.details.length > 0;
		percentage.value = tax.percentage;		
	
		// ----- TAX BASE
		
		let base = new AonNumber();
		base.id = this.TAX_BASE + i;
		base.description = MSG.BASE;
		base.format = CONSTANT.TRUE;
		base.decimals = "2";
		base.addEventListener(EVENT.CHANGE, () => {
			tax.base = base.value;
			this.invoice.setTax(tax, i);
			this.reload();
			if(this.autosave) this.save();
		});
		taxesTable.addCell(base);
		base.readonly = this.invoice.isReadonly() || this.invoice.details.length > 0;
		base.value = tax.base;

		// ----- TAX QUOTA
		
		let quota = new AonNumber();
		quota.id = this.TAX_QUOTA + i;
		quota.description = MSG.QUOTA;
		quota.format = CONSTANT.TRUE;
		quota.decimals = "2";
		// quota.addEventListener(Event.CHANGE, () => {
		// 	tax.quota = quota.value;
		// 	this.invoice.setTax(tax, i);
		// 	if(this.autosave) this.save();
		// });
		taxesTable.addCell(quota);
		quota.value = tax.quota + tax.surcharge_quota;
		quota.readonly = CONSTANT.TRUE; //this.invoice.isReadonly() || this.invoice.details.length > 0;

		// ----- TAX DELETE
		if(!this.invoice.isReadonly() && this.invoice.details.length === 0) {
			let taxDelete = new AonIconButton();
			taxDelete.id = this.TAX_DELETE + i;
			taxDelete.title = MSG.DELETE_TAX;
			taxDelete.icon = MATERIAL_ICONS.REMOVE_CIRCLE;
			taxDelete.addEventListener(EVENT.CLICK, () => {
				this.invoice.deleteTax(tax, i);
				this.setFocus(undefined);
				this.reload();
				if(this.autosave) this.save();
			});
			taxesTable.addCell(taxDelete);
		}
	}

	buildDetailCard(parent) {
		let card = new AonCard();
		card.id = this.DETAIL;
		card.title = MSG.INVOICE_CONCEPTS;
		parent.appendChild(card);

		let table = new AonBasicTable();
		table.id = this.DETAIL_TABLE;
		card.setContent(table);

		for(let i = 0; i < this.invoice.details.length; i++) {
			let detail = this.invoice.details[i];
			this.printDetail(table, detail, i);
		}

		let div = this.createElement(TAG.DIV);
		card.addContent(div);
		
		if(!this.invoice.isReadonly()) {
			let addButton = new AonIconButton();
			addButton.id = this.DETAIL_ADD;
			addButton.title = MSG.ADD_DETAIL;
			addButton.icon = MATERIAL_ICONS.ADD;
			addButton.addEventListener(EVENT.CLICK, () => {
				this.setFocus(this.DETAIL_DESCRIPTION + this.invoice.details.length);
				this.invoice.addDetail();
				this.reload();
				if(this.autosave) this.save();
			});
			div.appendChild(addButton);
		}
	}

	printDetail(table, detail, i) {
		table.addRow(); // ----- ROW i

		// ----- DETAIL CONCEPT | DESCRIPTION | PRODUCT
		
		let description = new AonSuggestion();
		description.id = this.DETAIL_DESCRIPTION + i;
		description.title = MSG.CONCEPT;
		description.addEventListener(EVENT.KEYUP, () => {
			if(description.value.length > 2) {
				let data = { value: description.value};
				getItems(data).then(r => {
					description.buildOptions(r.map(r => {return {
						name: r.name,
						value: r.id,
						item: r};}));
				}).catch(e => this.showError(e));
			  } 
		});

		description.addEventListener(EVENT.CHANGE, () => {
			this.invoice.details[i].description = description.value;
			this.setFocus(this.DETAIL_QUANTITY + i);
		});
		
		description.addEventListener(EVENT.SELECT,(e) => {
			console.log(e.detail);
			detail.description = e.detail.name; 
			detail.item = e.detail.item.id;
			detail.price = e.detail.item.price;
			this.invoice.setDetail(detail, i);
			this.setFocus(this.DETAIL_DESCRIPTION + i);
			this.reload();
		});

		let td = table.addCell(description);
		td.style.width = '50%';
		description.readonly = this.invoice.isReadonly();
		description.value = detail.description;
		
		// ----- DETAIL QUANTITY

		let quantity = new AonNumber();
		quantity.id = this.DETAIL_QUANTITY + i;
		quantity.description = MSG.QUANTITY;
		quantity.format = CONSTANT.TRUE;
		quantity.decimals = "2";
		quantity.addEventListener(EVENT.CHANGE, () => {
			detail.quantity = quantity.value;
			this.invoice.setDetail(detail, i);
			this.setFocus(this.DETAIL_PRICE + i);
			this.reload();
			if(this.autosave) this.save();
		});
		table.addCell(quantity);
		quantity.readonly = this.invoice.isReadonly();
		quantity.value = detail.quantity;

		// ----- DETAIL PRICE

		let price = new AonNumber();
		price.id = this.DETAIL_PRICE + i;
		price.description = MSG.PRICE;
		price.format = CONSTANT.TRUE;
		price.decimals = "2";
		price.addEventListener(EVENT.CHANGE, () => {
			detail.price = price.value;
			this.invoice.setDetail(detail, i);
			this.setFocus(this.DETAIL_DISCOUNT + i);
			this.reload();
			if(this.autosave) this.save();
		});
		table.addCell(price);
		price.readonly = this.invoice.isReadonly();
		price.value = detail.price;

		// ----- DETAIL DISCOUNT
		
		let discount = new AonNumber();
		discount.id = this.DETAIL_DISCOUNT + i;
		discount.description = '%Dto'//MSG.DISCOUNT;
		discount.format = CONSTANT.TRUE;
		discount.decimals = "2";
		discount.addEventListener(EVENT.CHANGE, () => {
			detail.discount = discount.value;
			this.invoice.setDetail(detail, i);
			this.setFocus(this.DETAIL_VAT + i);
			this.reload();
			if(this.autosave) this.save();
		});
		table.addCell(discount);
		discount.readonly = this.invoice.isReadonly();
		discount.value = detail.discount;

		// ----- DETAIL AMOUNT
		
		let amount = new AonNumber();
		amount.id = this.DETAIL_AMOUNT + i;
		amount.description = MSG.AMOUNT;
		amount.format = CONSTANT.TRUE;
		amount.decimals = "2";
		amount.readonly = CONSTANT.TRUE;
		amount.value = detail.amount;
		table.addCell(amount);
		
		// ----- DETAIL VAT
	
		let vat = new AonSelect();
		vat.id = this.DETAIL_VAT + i;
		vat.title = '%IVA';
		vat.options = JSON.stringify(TaxIVAPercentage);
		if(detail.prepayment === undefined) detail.prepayment = false;
		vat.readonly = this.invoice.isReadonly() || detail.prepayment;
		vat.addEventListener(EVENT.SELECT, () => {
			console.log(vat.value);
			detail.percentage = vat.value;
			this.invoice.setDetail(detail, i);
			this.reload();
			if(this.autosave) this.save();
		});
		table.addCell(vat);
		detail.percentage = detail.percentage || detail.vat;
		if(detail.percentage) vat.value = detail.percentage;
		// ----- DETAIL OPTIONS

		let detailOptions = new AonIconButton();
		detailOptions.id = this.DETAIL_OPTIONS + i;
		detailOptions.title = MSG.OPTIONS;
		detailOptions.icon = MATERIAL_ICONS.MORE_VERT;
		detailOptions.addEventListener(EVENT.CLICK, () => {
			this.printDetailDialog(detailOptions, detail, i);
		});
		table.addCell(detailOptions);

		// ----- DETAIL DELETE
		
		if(!this.invoice.isReadonly()) {
			let detailDelete = new AonIconButton();
			detailDelete.id = this.DETAIL_DELETE + i;
			detailDelete.title = MSG.DELETE_DETAIL;
			detailDelete.icon = MATERIAL_ICONS.REMOVE_CIRCLE;
			detailDelete.addEventListener(EVENT.CLICK, () => {
				this.setFocus(undefined);
				this.invoice.deleteDetail(detail, i);
				this.reload();
				if(this.autosave) this.save();
			});
			table.addCell(detailDelete);
		}
	}

	printDetailDialog(button, detail, i) {
		let dialog = this.getElement(this.DIALOG_BLANK);

		let div = this.createElement(TAG.DIV);
		div.style.margin = '15px';

		// ----- WITHHOLDING
			
		// let withholding = new AonSwitch();
		// withholding.id = this.DETAIL_WITHHOLDING + i;
		// withholding.title = MSG.WITHHOLDING;
		// withholding.readonly = this.invoice.isReadonly();
		// withholding.checked = detail.withholding;
		// div.appendChild(withholding);
		// withholding.addEventListener(EVENT.CHANGE, () => {
		// 	detail.withholding = withholding.checked;
		// 	this.invoice.setDetail(detail, i);
		// 	if(this.autosave) this.save();
		// });

		// ----- PREPAYMENT | SUPLIDO
		
		let prepayment = new AonSwitch();
		prepayment.id = this.DETAIL_PREPAYMENT + i;
		prepayment.title = 'Suplido';//MSG.DETAIL_PREPAYMENT;
		prepayment.readonly = this.invoice.isReadonly();
		prepayment.checked = detail.prepayment;
		div.appendChild(prepayment);
		prepayment.addEventListener(EVENT.CHANGE, () => {
			detail.prepayment = prepayment.checked;
			this.invoice.setDetail(detail, i);
			this.reload();
			if(this.autosave) this.save();
		});

		// ----- CATEGORY

		let category = new AonSelect();
		category.id = this.DETAIL_CATEGORY + i;
		category.title = MSG.CATEGORY;
		category.autocomplete = true;
		category.readonly = this.invoice.isReadonly();
		category.addEventListener(EVENT.SELECT, () => {
			detail.category = category.value;
			this.invoice.setDetail(detail, i);
			if(this.autosave) this.save();
		});
		div.appendChild(category);
		getInvoiceAccounts({type: this.invoice.getInvoiceType()}).then(accounts => {
			let accs = accounts.map(acc => {return {name: acc.name, value: acc.code};});
			category.options = JSON.stringify(accs);
			category.value = detail.category || this.invoice.getCategory();
		});

		// ----- BIEN AFECTO 
		// TODO
		let bienAfecto = new AonSelect();
		bienAfecto.id = 'aonInvoiceDetailBienAfecto';
		bienAfecto.title = 'Bien Afecto'; //MSG.CATEGORY;
		bienAfecto.autocomplete = true;
		bienAfecto.readonly = this.invoice.isReadonly();
		bienAfecto.addEventListener(EVENT.SELECT, () => {
			// detail.category = bienAfecto.value;
			// this.invoice.setDetail(detail, i);
			// if(this.autosave) this.save();
		});
		div.appendChild(bienAfecto);
		// getInvoiceAccounts({type: this.invoice.getInvoiceType()}).then(accounts => {
		// 	let accs = accounts.map(acc => {return {name: acc.name, value: acc.code};});
		// 	category.options = JSON.stringify(accs);
		// 	category.value = detail.category || this.invoice.getCategory();
		// });
			
		const top  = button.getBoundingClientRect().top;
		const left = button.getBoundingClientRect().left;
		dialog.setContent(div, top, left);
		dialog.open();
	}

	buildFinanceCard(parent) {
		let card = new AonCard();
		card.id = this.FINANCE;
		card.title = MSG.EXPIRATIONS;
		parent.appendChild(card);

		let table = new AonBasicTable();
		table.id = this.FINANCE_TABLE;
		card.setContent(table);

		for(let i = 0; i < this.invoice.finances.length; i++) {
			let finance = this.invoice.finances[i];
			this.printFinance(table, finance, i);
		}

		let div = this.createElement(TAG.DIV);
		card.addContent(div);
		
		if(!this.invoice.isReadonly()) {
			let addButton = new AonIconButton();
			addButton.id = this.FINANCE_ADD;
			addButton.title = MSG.ADD_FINANCE;
			addButton.icon = MATERIAL_ICONS.ADD;
			addButton.addEventListener('click', () => {
				this.setFocus(this.FINANCE_DUE_DATE + this.invoice.finances.length);
				this.invoice.addFinance();
				this.reload();
				if(this.autosave) this.save();
			});
			div.appendChild(addButton);
		}
	}

	printFinance(table, finance, i) {
		table.addRow(); // ----- ROW i
		
		// ----- FINANCE DUE DATE

		let date = new AonDate();
		date.id = this.FINANCE_DUE_DATE + i;
		date.title = MSG.DATE; //MSG.DUE_DATE;
		date.readonly = this.invoice.isReadonly();
		date.addEventListener(EVENT.CHANGE, () => {
			finance.due_date = date.value;
			this.setFocus(date.id);
			this.invoice.setFinance(finance, i);
			if(this.autosave) this.save();
		});
		table.addCell(date);
		date.value = finance.due_date;
		
		// ----- FINANCE PAYMETHOD

		let paymethod = new AonSelect();
		paymethod.id = this.FINANCE_PAYMETHOD + i;
		paymethod.title = MSG.PAYMETHOD;
		paymethod.autocomplete = true;
		paymethod.options = JSON.stringify(Paymethods);
		paymethod.readonly = this.invoice.isReadonly();
		paymethod.addEventListener(EVENT.SELECT, () => {
			this.setFocus(this.FINANCE_BANK_ACCOUNT + i);
			finance.paymethod = paymethod.value;
			this.invoice.setFinance(finance, i);
			if(this.autosave) this.save();
		});
		table.addCell(paymethod);
		paymethod.value = finance.paymethod;
	
		// ----- FINANCE BANK ACCOUNT | RBANK
		
		let bankAccount = new AonSuggestion();
		bankAccount.id = this.FINANCE_BANK_ACCOUNT + i;
		bankAccount.title = 'Cuenta Bancaria'; //MSG.BANK_ACCOUNT;
		bankAccount.readonly = this.invoice.isReadonly();

		bankAccount.addEventListener(EVENT.KEYUP, () => {

		});

		bankAccount.addEventListener(EVENT.CHANGE, () => {
			this.setFocus(this.FINANCE_AMOUNT + i);
		});
		
		bankAccount.addEventListener(EVENT.SELECT,() => {
			this.setFocus(this.FINANCE_AMOUNT + i);
		})
		
		table.addCell(bankAccount);
		finance.bank_account = finance.bank_account || finance.iban;
		bankAccount.value = finance.bank_account;

		// ----- FINANCE AMOUNT
		
		let amount = new AonNumber();
		amount.id = this.FINANCE_AMOUNT + i;
		amount.description = MSG.AMOUNT;
		amount.format = CONSTANT.TRUE;
		amount.decimals = "2";
		amount.readonly = this.invoice.isReadonly();
		amount.addEventListener(EVENT.SELECT, () => {
			this.setFocus(this.FINANCE_AMOUNT + i);
			finance.amount = amount.value;
			this.invoice.setFinance(finance, i);
			if(this.autosave) this.save();
		});
		table.addCell(amount);
		amount.value = finance.amount;

		// ----- FINANCE DELETE
		
		if(!this.invoice.isReadonly()) {
			let financeDelete = new AonIconButton();
			financeDelete.id = this.FINANCE_DELETE + i;
			financeDelete.title = MSG.DELETE_FINANCE;
			financeDelete.icon = MATERIAL_ICONS.REMOVE_CIRCLE;
			financeDelete.addEventListener(EVENT.CLICK, () => {
				this.invoice.deleteFinance(finance, i);
				if(this.autosave) this.save();
			});
			table.addCell(financeDelete);
		}
	}

	getInvoiceTitle() {
		if(this.isEmitida()) {
			return MSG.INVOICE_ISSUED;
		} else if(this.isTicket()){
			return MSG.TICKET;
		} else return MSG.INVOICE_RECEIVED;
	}

	save() {
		insertInvoice(this.getInvoice())
			.then(r => {
				this.getInvoice().id = r.id;
				this.getApplication().getToast().start({
					type: CONSTANT.SUCCESS,
					message: MSG.SAVED_DATA
				});
			})
			.catch(e => this.showError(e));
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

		let d = this.getApplication().getOptionDialog();
		let rectify = ACTION.RECTIFY;
		rectify.fn = () => this.rectifyInvoice();
		let duplicate = ACTION.DUPLICATE;
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
		this.changeInvoice(invoice);
	}

	nextInvoice() {
		let invoice = getNextInvoice();
		this.changeInvoice(invoice);
	}

	changeInvoice(invoice) {
		if(invoice && !invoice.details){
			getInvoice(invoice.id).then((inv) => {
				let aip = document.querySelector('aon-invoice-panel');
				aip.aonInvoice(invoice.type, inv);
			});
		} else if(invoice)  {
			this.getApplication().getParent().aonInvoice(invoice.type, invoice);
		}
	}

	acceptInvoice() {
		acceptInvoice(this.getInvoice()).then(r => {
			this.invoice.createInvoice(r);
			this.reload();
		}).catch(e => this.showError(e));
	}

	recordInvoice() {
		if(this.invoice.isSelfconta()){
			recordSelfconta(this.getInvoice())
				.then(r => this.back())
				.catch(e => this.showError(e));
		}	else {
				let aonInvoice = this.getElement('aonInvoice');
				let d = document.getElementById(aonInvoice.DIALOG);
				d.clear();
				if(!this.isMobile())d.width = '400px';
				d.setTitle(MSG.RECORD_INVOICE);
				d.setContentHTML('Esta opción está en desarrollo...');
				d.addAcceptAction(() => {});
				d.open();
		}
	}


	rejectInvoice() {
		let d = this.getApplication().getDialog();
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
				this.invoice.comments.push(comment);
			}
			this.invoice.status = CONSTANT.REFUSED;
			this.build();
			this.save();
		});

		let ta = this.getElement('commentTextArea');
		ta.style.outline = 'none';
		ta.style.width = '100%';
		ta.style.height = '100px';
		d.open();
	}

	addInvoiceComment() {
		let aonInvoice = this.getElement('aonInvoice');
		let d = document.getElementById(aonInvoice.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.ADD_COMMENT);
		d.setContentHTML('<textarea id="commentTextArea"> </textarea>');
		d.addAcceptAction(() => {
			let ta = this.getElement('commentTextArea');
			let dt = new Date()
			let m = dt.getMonth() + 1;
			let month = m < 10 ? '0' + m : m;
			let dateStr = dt.getDay() + '/' + month  + '/' + dt.getYear() + ' ' + dt.getHours() + ':' + dt.getMinutes() + ':' + dt.getSeconds();
			let comment = {
				date: dateStr,
				user: '',
				status: this.getCommentStatus(),
				reason: ta.value
			};
			this._invoice.comments.push(comment);
			this.save();
			this.build();
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
			let d = this.getApplication().getDialog();
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
