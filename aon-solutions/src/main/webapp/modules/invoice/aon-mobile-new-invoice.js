import { AonApplication } from '../../components/aon-application.js';
import { AonBasicTable } from '../../components/aon-basic-table.js';
import { AonCard } from '../../components/aon-card.js';
import { AonDate } from '../../components/aon-date.js';
import { AonDialog } from '../../components/aon-dialog.js';
import { AonIconButton } from '../../components/aon-icon-button.js';
import { AonInput } from '../../components/aon-input.js';
import { AonNumber } from '../../components/aon-number.js';
import { AonRegistry } from '../../components/aon-registry.js';
import { AonSelect } from '../../components/aon-select.js';
import { AonSuggestion } from '../../components/aon-suggestion.js';
import { AonSwitch } from '../../components/aon-switch.js';
import { AonToolbar } from '../../components/aon-toolbar.js';
import { AonViewer } from '../../components/aon-viewer.js';
import { CSS, CONSTANT, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js';
import { ToolbarType } from '../../models/enums.js';
import { getInvoiceAccounts } from '../../services/invoiceService.js';
import { Paymethods } from '../../services/paymethod.js';
import { getItems } from '../../services/productService.js';
import * as ACTION from '../actions.js';
import { AonNewInvoice } from './aon-new-invoice.js';
import { TaxIVAPercentage, TaxType, Transactions } from './invoiceEnums.js';

export class AonMobileNewInvoice extends AonNewInvoice {

  	constructor () {
    	super();
  	}	

	initialize(){
		super.initialize();
		this.DIALOG =  CONSTANT.AON_INVOICE + 'Dialog';
		this.FINANCE_OPTIONS =  this.FINANCE + CONSTANT.OPTIONS.initCap();
	}

	buildToolbar() {
		let invoiceToolbar = new AonToolbar();
		invoiceToolbar.id = this.TOOLBAR;
		invoiceToolbar.type = ToolbarType.SECONDARY;
		invoiceToolbar.title = this.getInvoiceTitle();
		this.appendChild(invoiceToolbar);
		invoiceToolbar.removeButtons();

    if(this.getInvoice().isInbox()){
      invoiceToolbar.addButton2(ACTION.ACCEPT, () => this.acceptInvoice());
			if(!this.autosave && this.getInvoice().isInbox()){
				invoiceToolbar.addButton2(ACTION.SAVE, () => this.save());
			}
		} 
		invoiceToolbar.addButton2(ACTION.BACK, () => this.back());
		if(this.getInvoice().file || this.invoice.isEmitida()){
			invoiceToolbar.addButtonTitle(ACTION.SHOW_FILE, () => this.showFile());
		}
	}

	buildContent() {
		let form = this.createElement(TAG.FORM);
		this.appendChild(form);
		let div = this.createElement(TAG.DIV);
		form.appendChild(div);

    	let file = this.createElement(TAG.DIV);
		file.id  = this.FILE;
	    file.className = CSS.AON_NONE;
	    div.appendChild(file);

		this.buildCommentCard(div);
		this.buildGeneralCard(div);
		this.buildTaxCard(div);
		this.buildDetailCard(div);
		this.buildFinanceCard(div);


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

		table.addRow(); // ----- ROW 2

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

		table.addRow(); // ----- ROW 3

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
		table.addCell(registry, '2');

		table.addRow(); // ----- ROW 4 

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
		table.addCell(category);
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
		table.addCell(paymethod)
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
			this.reload();
			if(this.autosave) this.save();
		});
		table.addCell(transaction, '2');

		table.addRow(); // ----- ROW 2

		// ----- SURCHARGE

		let surcharge = new AonSwitch();
		surcharge.id = this.SURCHARGE;
		surcharge.title = MSG.SURCHARGE_RE;
		surcharge.readonly = this.invoice.isReadonly();
		surcharge.addEventListener(EVENT.CHANGE, () => {
			this.invoice.setSurcharge(surcharge.checked);
			this.setFocus(surcharge.id);
			this.reload();
			if(this.autosave) this.save();
		});
		table.addCell(surcharge);
		if(this.invoice.isEmitida() && !this.invoice.isNacional()) {
			this.invoice.setSurcharge(false);
			surcharge.setDisabled(true);
		}
		surcharge.checked = this.invoice.isSurcharge();
		

		// ----- WITHHOLDING FARMER

		let farmer = new AonSwitch();
		farmer.id = this.WITHHOLDING_FARMER;
		farmer.title = MSG.WITHHOLDING_FARMER;
		farmer.readonly = this.invoice.isReadonly();
		farmer.addEventListener(EVENT.CHANGE, () => {
			this.invoice.setWithholdingFarmer(farmer.checked);
			this.setFocus(farmer.id);
			this.reload();
			if(this.autosave) this.save();
		});
		table.addCell(farmer);
		if(this.invoice.isEmitida() && !this.invoice.isNacional()) {
			this.invoice.setWithholdingFarmer(false);
			farmer.setDisabled(true);
		}
		farmer.checked = this.invoice.isWithholdingFarmer();

		// ----- TAXES
		
		let taxesTable = new AonBasicTable();
		taxesTable.id = this.TAX_TABLE2;
		card.addContent(taxesTable);

		if(this.invoice.isEmitida() && !this.invoice.isNacional()) {
			this.invoice.taxes = [];
		}

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
			if(this.invoice.isEmitida() && !this.invoice.isNacional()) {
				addButton.setDisabled(true);
			}
		}
		
		// ----- WITHHOLDING

		let irpf = new AonSwitch();
		irpf.id = this.WITHHOLDING;
		irpf.title = MSG.IRPF; // MSG.WITHHOLDING;
		if(!this.invoice.isReadonly() && this.invoice.details.length === 0)
			irpf.style.position = 'absolute';
		irpf.style.marginTop = '10px';
		irpf.style.marginLeft = '10px';
		irpf.readonly = this.invoice.isReadonly() || this.invoice.details.length > 0;
		irpf.addEventListener(EVENT.CHANGE, () => {
			this.invoice.setWithholding(irpf.checked); 
			this.reload();
			if(this.autosave) this.save();
		});
		div.appendChild(irpf);
		if(this.invoice.isEmitida() && !this.invoice.isNacional()) {
			this.invoice.setWithholding(false);
			irpf.setDisabled(true);
		}
		irpf.checked = this.invoice.taxes.filter(r => TaxType.IRPF === r.type || TaxType.IRPF === r.tax).length > 0;
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

		// ----- DETAIL AMOUNT
		
		let amount = new AonNumber();
		amount.id = this.DETAIL_AMOUNT + i;
		amount.description = MSG.AMOUNT;
		amount.format = CONSTANT.TRUE;
		amount.decimals = "2";
		amount.readonly = CONSTANT.TRUE;
		amount.value = detail.amount;
		table.addCell(amount);
		
		// ----- DETAIL OPTIONS

		let detailOptions = new AonIconButton();
		detailOptions.id = this.DETAIL_OPTIONS + i;
		detailOptions.title = MSG.OPTIONS;
		detailOptions.icon = MATERIAL_ICONS.EDIT;
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
		let dialog = this.getApplication().getDialog();
		dialog.setTitle("DETALLE");
		dialog.addAcceptAction(() => {
			
		});
		

		let div = this.createElement(TAG.DIV);
		div.style.margin = '15px';
		dialog.setContent(div);

		let table = new AonBasicTable();
		table.id = this.DIALOG + 'Detail';
		div.appendChild(table);

		table.addRow(); // ----- ROW 1

		// ----- DETAIL CONCEPT | DESCRIPTION | PRODUCT
		
		let description = new AonSuggestion();
		description.id = this.DETAIL_DESCRIPTION + 'Dialog' + i;
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
		});
		
		description.addEventListener(EVENT.SELECT,(e) => {
			console.log(e.detail);
			detail.description = e.detail.name; 
			detail.item = e.detail.item.id;
			detail.price = e.detail.item.price;
			this.invoice.setDetail(detail, i);
			this.reload();
			this.printDetailDialog(button, this.invoice.details[i], i);
		});

		table.addCell(description, '2');
		description.readonly = this.invoice.isReadonly();
		description.value = detail.description;


		table.addRow(); // ----- ROW 2

		// ----- DETAIL QUANTITY

		let quantity = new AonNumber();
		quantity.id = this.DETAIL_QUANTITY + 'Dialog' + i;
		quantity.description = MSG.QUANTITY;
		quantity.format = CONSTANT.TRUE;
		quantity.decimals = "2";
		quantity.addEventListener(EVENT.CHANGE, () => {
			detail.quantity = quantity.value;
			this.invoice.setDetail(detail, i);
			this.reload();
			this.printDetailDialog(button, this.invoice.details[i], i);
			if(this.autosave) this.save();
		});
		table.addCell(quantity);
		quantity.readonly = this.invoice.isReadonly();
		quantity.value = detail.quantity;
	
		// ----- DETAIL PRICE
	
		let price = new AonNumber();
		price.id = this.DETAIL_PRICE + 'Dialog' + i;
		price.description = MSG.PRICE;
		price.format = CONSTANT.TRUE;
		price.decimals = "2";
		price.addEventListener(EVENT.CHANGE, () => {
			detail.price = price.value;
			this.invoice.setDetail(detail, i);
			this.reload();
			this.printDetailDialog(button, this.invoice.details[i], i);
			if(this.autosave) this.save();
		});
		table.addCell(price);
		price.readonly = this.invoice.isReadonly();
		price.value = detail.price;

		table.addRow(); // ----- ROW 3

		// ----- DETAIL DISCOUNT
		
		let discount = new AonNumber();
		discount.id = this.DETAIL_DISCOUNT + 'Dialog' + i;
		discount.description = '%Dto'//MSG.DISCOUNT;
		discount.format = CONSTANT.TRUE;
		discount.decimals = "2";
		discount.addEventListener(EVENT.CHANGE, () => {
			detail.discount = discount.value;
			this.invoice.setDetail(detail, i);
			this.reload();
			this.printDetailDialog(button, this.invoice.details[i], i);
			if(this.autosave) this.save();
		});
		table.addCell(discount);
		discount.readonly = this.invoice.isReadonly();
		discount.value = detail.discount;

		// ----- DETAIL AMOUNT
		
		let amount = new AonNumber();
		amount.id = this.DETAIL_AMOUNT + 'Dialog' + i;
		amount.description = MSG.AMOUNT;
		amount.format = CONSTANT.TRUE;
		amount.decimals = "2";
		amount.readonly = CONSTANT.TRUE;
		amount.value = detail.amount;
		table.addCell(amount);
		

		table.addRow(); // ----- ROW 3

		// ----- PREPAYMENT | SUPLIDO
		
		let prepayment = new AonSwitch();
		prepayment.id = this.DETAIL_PREPAYMENT + 'Dialog' + i;
		prepayment.title = 'Suplido';//MSG.DETAIL_PREPAYMENT;
		prepayment.addEventListener(EVENT.CHANGE, () => {
			detail.prepayment = prepayment.checked;
			this.invoice.setDetail(detail, i);
			this.reload();
			this.printDetailDialog(button, this.invoice.details[i], i);
			if(this.autosave) this.save();
		});
		table.addCell(prepayment);
		prepayment.readonly = this.invoice.isReadonly();
		prepayment.checked = detail.prepayment;

		// ----- DETAIL VAT
	
		let vat = new AonSelect();
		vat.id = this.DETAIL_VAT + 'Dialog' + i;
		vat.title = '%IVA';
		vat.options = JSON.stringify(TaxIVAPercentage);
		if(detail.prepayment === undefined) detail.prepayment = false;
		vat.readonly = this.invoice.isReadonly() || detail.prepayment;
		vat.addEventListener(EVENT.SELECT, () => {
			console.log(vat.value);
			detail.percentage = vat.value;
			this.invoice.setDetail(detail, i);
			this.reload();
			this.printDetailDialog(button, this.invoice.details[i], i);
			if(this.autosave) this.save();
		});
		table.addCell(vat);
		if(this.invoice.isEmitida() && !this.invoice.isNacional()) {
			detail.percentage = undefined;
			detail.vat = undefined;
			vat.setDisabled(true);
		}
		detail.percentage = detail.percentage || detail.vat;
		if(detail.percentage) vat.value = detail.percentage;

		dialog.open();
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

		// ----- FINANCE AMOUNT
		
		let amount = new AonNumber();
		amount.id = this.FINANCE_AMOUNT + i;
		amount.description = MSG.AMOUNT;
		amount.format = CONSTANT.TRUE;
		amount.decimals = "2";
		amount.readonly = this.invoice.isReadonly();
		amount.addEventListener(EVENT.CHANGE, () => {
			this.setFocus(this.FINANCE_AMOUNT + i);
			finance.amount = amount.value;
			this.invoice.setFinance(finance, i);
			if(this.autosave) this.save();
		});
		table.addCell(amount);
		amount.value = finance.amount;

		// ----- FINANCE OPTIONS

		let financeOptions = new AonIconButton();
		financeOptions.id = this.FINANCE_OPTIONS + i;
		financeOptions.title = MSG.OPTIONS;
		financeOptions.icon = MATERIAL_ICONS.EDIT;
		financeOptions.addEventListener(EVENT.CLICK, () => {
			this.printFinanceDialog(financeOptions, finance, i);
		});
		table.addCell(financeOptions);

		// ----- FINANCE DELETE
		
		if(!this.invoice.isReadonly()) {
			let financeDelete = new AonIconButton();
			financeDelete.id = this.FINANCE_DELETE + i;
			financeDelete.title = MSG.DELETE_FINANCE;
			financeDelete.icon = MATERIAL_ICONS.REMOVE_CIRCLE;
			financeDelete.addEventListener(EVENT.CLICK, () => {
				this.invoice.deleteFinance(finance, i);
				this.reload();
				if(this.autosave) this.save();
			});
			table.addCell(financeDelete);
		}
	}

	printFinanceDialog(button, finance, i) {
		let dialog = this.getApplication().getDialog();
		dialog.setTitle("DETALLE");
		dialog.addAcceptAction(() => {
			
		});
		

		let div = this.createElement(TAG.DIV);
		div.style.margin = '15px';
		dialog.setContent(div);

		let table = new AonBasicTable();
		table.id = this.DIALOG + 'Detail';
		div.appendChild(table);

		table.addRow(); // ----- ROW 1

		// ----- FINANCE DUE DATE

		let date = new AonDate();
		date.id = this.FINANCE_DUE_DATE + 'Dialog' + i;
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
		
		table.addRow(); // ----- ROW 2

		// ----- FINANCE PAYMETHOD

		let paymethod = new AonSelect();
		paymethod.id = this.FINANCE_PAYMETHOD + 'Dialog' + i;
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

		table.addRow(); // ----- ROW 3

		// ----- FINANCE BANK ACCOUNT | RBANK
		
		let bankAccount = new AonSuggestion();
		bankAccount.id = this.FINANCE_BANK_ACCOUNT + 'Dialog' + i;
		bankAccount.title = 'Cuenta Bancaria'; //MSG.BANK_ACCOUNT;
		bankAccount.readonly = this.invoice.isReadonly();

		bankAccount.addEventListener(EVENT.KEYUP, () => {

		});

		bankAccount.addEventListener(EVENT.CHANGE, () => {
			this.setFocus(this.FINANCE_AMOUNT + i);	
			finance.bank_account = bankAccount.value;
			this.invoice.setFinance(finance, i);
		});
	
		// bankAccount.addEventListener(EVENT.SELECT,() => {
		// 	this.setFocus(this.FINANCE_AMOUNT + i);
		// })
		
		table.addCell(bankAccount);
		finance.bank_account = finance.bank_account || finance.iban;
		bankAccount.value = finance.bank_account;

		table.addRow(); // ----- ROW 3

		// ----- FINANCE AMOUNT
	
		let amount = new AonNumber();
		amount.id = this.FINANCE_AMOUNT + 'Dialog' +  i;
		amount.description = MSG.AMOUNT;
		amount.format = CONSTANT.TRUE;
		amount.decimals = "2";
		amount.readonly = this.invoice.isReadonly();
		amount.addEventListener(EVENT.CHANGE, () => {
			this.setFocus(this.FINANCE_AMOUNT + i);
			finance.amount = amount.value;
			this.invoice.setFinance(finance, i);
			if(this.autosave) this.save();
		});
		table.addCell(amount);
		amount.value = finance.amount;

		dialog.open();
	}

	showFile(){
		let invoiceToolbar = this.getElement(this.TOOLBAR);
		let button = this.getElement(invoiceToolbar.TITLE_SECTION + 'ShowFileButton');
		let visible = 'visibility_off' === button.icon;
		let fileDiv = this.getElement(this.FILE);
		if(visible) {
			button.icon = 'visibility';
			fileDiv.style.display = 'none'
		} else {
			button.icon = 'visibility_off';
			fileDiv.style.display = 'block';
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

}

window.customElements.define('aon-mobile-new-invoice',  AonMobileNewInvoice);
