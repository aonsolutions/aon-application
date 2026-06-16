import { AonBasicTable } from '../../components/aon-basic-table.js';
import { AonCard } from '../../components/aon-card.js';
import { AonDialog } from '../../components/aon-dialog.js';
import { AonIconButton } from '../../components/aon-icon-button.js';
import { AonSwitch } from '../../components/aon-switch.js';
import { AonToolbar } from '../../components/aon-toolbar.js';
import { AonViewer } from '../../components/aon-viewer.js';
import { CSS, CONSTANT, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js';
import { ToolbarType } from '../../models/enums.js';
import { getCompanyActivities } from '../../services/companyService.js';
import { getInvoiceAccounts, getPaymethods } from '../../services/invoiceService.js';
import { getItems } from '../../services/productService.js';
import * as ACTION from '../actions.js';
import { AonInvoice } from './aon-invoice.js';
import { TaxIVAPercentage, TaxType, Transactions } from './invoiceEnums.js';
import * as LS from '../../services/localStorageService.js';

import {INVOICE} from  '../../services/app.js';
import { AonCustomerSuggestion } from '../registry/customer/aon-customer-suggestion.js';
import { AonRegistrySuggestion } from '../registry/aon-registry-suggestion.js';
import { getWorkplaces } from '../../services/workplaceService.js';
import { createCard, createDate, createInput, createNumber, createSelect, createSuggestion, createTable } from '../../components/CreateComponent.js';
import { CARD } from '../../environments/constants.js';

export class AonMobileInvoice extends AonInvoice {

  	constructor () {
    	super();
  	}	 

	initialize() {
		super.initialize();
		this.REGISTRY_CARD = CONSTANT.AON_INVOICE + 'RegistryCard';
		this.REGISTRY_CARD_TABLE = this.REGISTRY_CARD + CONSTANT.TABLE.initCap();
		this.DIALOG =  CONSTANT.AON_INVOICE + 'Dialog';
		this.FINANCE_OPTIONS =  this.FINANCE + CONSTANT.OPTIONS.initCap();
	}

	buildToolbar() {
		this.buildOptions();
		let invoiceToolbar = new AonToolbar();
		invoiceToolbar.id = this.TOOLBAR;
		invoiceToolbar.type = ToolbarType.SECONDARY;
		invoiceToolbar.title = this.getInvoiceTitle();
		this.appendChild(invoiceToolbar);
		invoiceToolbar.removeButtons();

    	if(this.getInvoice().isInbox()){
      		if(this.getInvoice().isEmitida())
				invoiceToolbar.addButton2(ACTION.ACCEPT, () => this.acceptInvoice());
			invoiceToolbar.addButton2(ACTION.SAVE, () => this.save());
		} 
		invoiceToolbar.addButton2(ACTION.BACK, () => this.back());
		if(this.getInvoice().file || this.invoice.isEmitida()){
			invoiceToolbar.addButtonTitle(ACTION.SHOW_FILE, () => this.showFile());
		}
	}

	buildContent() {
		let div = this.createElement(TAG.DIV);
		this.appendChild(div);

    	let file = this.createElement(TAG.DIV);
		file.id  = this.FILE;
	    file.style.display = 'none';
	    div.appendChild(file);

		this.buildCommentCard(div);
		this.buildRegistryCard(div);
		this.buildGeneralCard(div);
		this.buildTaxCard(div);
		this.buildDetailCard(div);
		this.buildFinanceCard(div);
	}

	buildRegistryCard(parent) {
		let card = createCard(this.REGISTRY_CARD, this.invoice.isEmitida() ? "Datos Cliente" : "Datos Proveedor/Acreedor", parent);
		card.onToggle();
		
		let table = new AonBasicTable();
		table.id = this.REGISTRY_CARD_TABLE;
		card.setContent(table);

		table.addRow(); // ----- ROW 1

		if(this.invoice.isEmitida()) {
			let customer = new AonCustomerSuggestion();	
			customer.id = this.REGISTRY;
			customer.showAddress = true;
			customer.readonly = this.invoice.isReadonly();
			customer.setCustomer(this.invoice.getRegistry());
			customer.addEventListener(EVENT.SELECT_REGISTRY, () => this.onChangeRegistry(customer.getCustomer()));
			customer.addEventListener(EVENT.CUSTOMER_CHANGE, () => {
				this.invoice.receiver.address = customer.getCustomer().address;
			});
			table.addCell(customer, '2');	
		} else {
			let registry = new AonRegistrySuggestion();
			registry.id = this.REGISTRY;
			registry.showAddress = true;
			registry.types = this.invoice.getRegistryType();
			// registry.value = this.invoice.getRegistry();
			registry.setRegistry(this.invoice.getRegistry());
			registry.addEventListener(EVENT.SELECT_REGISTRY, () => this.onChangeRegistry(registry.getRegistry()));
			registry.addEventListener(EVENT.CUSTOMER_CHANGE, () => {
				this.invoice.receiver.address = registry.getRegistry().address;
			});
			table.addCell(registry, '2');
		}
	}

	buildGeneralCard(parent) {
		let card = createCard(this.GENERAL_CARD, MSG.INVOICE_DATA, parent);
		card.onToggle();

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

			service.setWidth('150px');
			service.setMarginBottom('10px');

			investment.setWidth('150px');
			investment.setMarginBottom('10px');

			rectified.setWidth('150px');
			rectified.setMarginBottom('10px');
		});

		let table = new AonBasicTable();
		table.id = this.GENERAL_CARD_TABLE;
		card.setContent(table);

		table.addRow(); // ----- ROW 1

		if (this.invoice.isRawdoc() && this.invoice.isEmitida() && !this.isInvofoxInvoice()) {

			// ----- SERIE

			let seriesOptions = this.configuration.series.filter(f => this.invoice.isRectifier() ? f.rectification : f.invoice);
			if (!seriesOptions.map(o => o.code).includes(this.invoice.series)) {
				this.invoice.setSeries(seriesOptions.length > 0 ? seriesOptions[0].code : undefined);
			}
			let serie = createSelect(this.SERIE, MSG.SERIE);
			serie.setOptions(seriesOptions);
			serie.setAlias("code", "code");
			serie.setValue(this.invoice.series);
			serie.readonly = this.invoice.isReadonly();
			serie.addEventListener(EVENT.SELECT, () => this.onChangeSerie(serie.value));
			table.addCell(serie, '2');			
			
		} else {

			// ----- REFERENCE
			
			let reference = createInput(this.REFERENCE, MSG.REFERENCE);
			reference.readonly = this.invoice.isReadonly();
			reference.value = this.invoice.reference;
			reference.addEventListener(EVENT.CHANGE, () => {
				this.invoice.setReference(reference.value);
			});
			table.addCell(reference, '2');			
		}

		table.addRow(); // ----- ROW 2

		// ----- DATE

		let date = createDate(this.DATE, MSG.DATE);
		date.readonly = this.invoice.isReadonly();
		date.setDate(this.invoice.date);
		date.addEventListener(EVENT.CHANGE, () => {
			this.invoice.setDate(date.getDateValue());
		});
		table.addCell(date);

		// ----- TOTAL

		let total = createNumber(this.TOTAL, MSG.TOTAL);
		total.format = CONSTANT.TRUE;
		total.decimals = "2";
		total.addEventListener(EVENT.CHANGE, () => {
			this.invoice.setTotal(total.value);
			this.setFocus(total.id);
			this.reload();
		});
		table.addCell(total);
		total.readonly = this.invoice.isReadonly()
			|| this.invoice.taxes.length > 1 
			|| this.invoice.details.length > 0;
		total.value = this.invoice.total;

		table.addRow(); // ----- ROW 3

		// ----- CATEGORY
		if(this.getDur().hasAccounting() || this.getDur().hasParentAccounting()) {
			let category = createSelect(this.CATEGORY, MSG.CATEGORY);
			category.autocomplete = true;
			category.readonly = this.invoice.isReadonly();
			category.addEventListener(EVENT.SELECT, () => {
				this.invoice.setCategory(category.value);
			});
			table.addCell(category, '2');
			getInvoiceAccounts({type: this.invoice.getInvoiceType()}).then(accounts => {
				let accs = accounts.map(acc => {return {name: acc.description, value: acc.code};});
				category.options = JSON.stringify(accs);
				category.value = this.invoice.getCategory();
			});
		}

		table.addRow(); // ----- ROW 4 

		// ----- WORKPLACES

		getWorkplaces().then(r => {
			if(!this.invoice.workplace && r.length > 0) {
				this.invoice.setWorkplace(r[0].id);
			}
			if(r.length > 1) {
				table.addRow();
				let workplaces = r.map(w => {return {name: w.description, value: w.id};});
				let workplace = createSelect(this.WORKPLACE, MSG.WORKPLACE);
				workplace.autocomplete = true;
				workplace.readonly = this.invoice.isReadonly();
				workplace.options = JSON.stringify(workplaces);
				workplace.value = this.invoice.getWorkplace();
				workplace.addEventListener(EVENT.SELECT, () => {
					this.invoice.setWorkplace(workplace.value);
				});
				table.addCell(workplace, this.invoice.isEmitida() ? '4' : '6');
			} else if(r.length === 1) this.invoice.setWorkplace(r[0].id);
		}).catch(e => {
			console.error(e);
		});
	}	

	buildTaxCard(parent) {
		let card = createCard(this.TAX, MSG.TAXES_DETAIL, parent);
		card.onToggle();

		let dialog = this.getElement(this.DIALOG_BLANK);
		card.addTitleButton(MSG.OPTIONS, MATERIAL_ICONS.MORE_VERT, false, () => {
			let div = this.createElement(TAG.DIV);
			div.style.margin = '15px';
			let button  = this.getElement(card.TITLE_SECTION2 + MSG.OPTIONS + 'Button');

			// ----- VAT ACCRUAL PAYMENT - CRITERIO DE CAJA

			let accrual = new AonSwitch();
			accrual.id = this.VAT_ACCRUAL_PAYMENT;
			accrual.title = MSG.VAT_ACCRUAL_PAYMENT;
			accrual.readonly = this.invoice.isReadonly();
			accrual.checked = this.invoice.isVatAccrualPayment();
			div.appendChild(accrual);
			accrual.addEventListener(EVENT.CHANGE, () => {
				this.invoice.setVatAccrualPayment(accrual.checked);
			});

			// ----- SURCHARGE - RECARGO DE EQUIVALENCIA

			let surcharge = new AonSwitch();
			surcharge.id = this.SURCHARGE;
			surcharge.title = MSG.SURCHARGE_RE;
			surcharge.readonly = this.invoice.isReadonly();
			surcharge.addEventListener(EVENT.CHANGE, () => {
				this.invoice.setSurcharge(surcharge.checked);
				this.reload();
			});
			div.appendChild(surcharge);
			surcharge.checked = this.invoice.isSurcharge();

			// ----- REGIMEN ESPECIAL AGRARIO

			let farmer = new AonSwitch();
			farmer.id = this.WITHHOLDING_FARMER;
			farmer.title = MSG.WITHHOLDING_FARMER;
			farmer.readonly = this.invoice.isReadonly();
			farmer.addEventListener(EVENT.CHANGE, () => {
				this.invoice.setWithholdingFarmer(farmer.checked);
				this.reload();
			});
			div.appendChild(farmer);
			if(this.invoice.isEmitida() && !this.invoice.isNacional()) {
				this.invoice.setWithholdingFarmer(false);
				farmer.setDisabled(true);
			}
			farmer.checked = this.invoice.isWithholdingFarmer();
	
			const top  = button.getBoundingClientRect().top;
			const left = button.getBoundingClientRect().left;
			dialog.setContent(div, top, left, '225px');
			dialog.open();

			farmer.setWidth('200px');
			farmer.setMarginBottom('10px');

			surcharge.setWidth('200px');
			surcharge.setMarginBottom('10px');

			accrual.setWidth('200px');
			accrual.setMarginBottom('10px');
		});

		let table = new AonBasicTable();
		table.id = this.TAX_TABLE;
		card.setContent(table);

		table.addRow(); // ----- ROW 1

		// ----- TRANSACTION TYPE

		let transaction = createSelect(this.TRANSACTION_TYPE, MSG.TRANSACTION_TYPE);
		transaction.options = JSON.stringify(Transactions);
		transaction.value = this.invoice.transaction;
		transaction.readonly = this.invoice.isReadonly();
		transaction.addEventListener(EVENT.SELECT, () => {
			this.invoice.setTransaction(transaction.value);
			this.setFocus(transaction.id);
			this.reload();
		});
		table.addCell(transaction, '2');

		table.addRow(); // ----- ROW 2

		// ----- ACTIVITY TYPE

		let activity = createSelect(this.ACTIVITY, MSG.ACTIVITY);
		activity.readonly = this.invoice.isReadonly();
		activity.addEventListener(EVENT.SELECT, () => {
			this.invoice.setActivity(activity.value);
		});
		table.addCell(activity, '2');
		getCompanyActivities({}).then(activities => {
			let acts = activities.map(a => { return {value: a.id, name: a.description}});
			activity.options = JSON.stringify(acts);
			activity.value = this.invoice.getActivity();
		});

		// ----- TAXES
		
		let taxesTable = createTable(this.TAX_TABLE2, card.getContent());

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
				if(this.invoice.isEmitida() && !this.invoice.isNacional()) {
					// TODO
				} else { 
					this.setFocus(this.TAX_PERCENTAGE + this.invoice.taxes.length);
					this.invoice.addTax();
					this.reload();
				}
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
		});
		div.appendChild(irpf);
		if(!this.invoice.isNacional() && !this.invoice.isCcm()) {
			irpf.setDisabled(true);
		}
		irpf.checked = this.invoice.taxes.filter(r => TaxType.IRPF === r.type || TaxType.IRPF === r.tax).length > 0;
	}

	buildDetailCard(parent) {
		let card = this.getElement(this.DETAIL);
		if(!card) {
			card = createCard(this.DETAIL, MSG.INVOICE_CONCEPTS, parent);
			card.onToggle();
		}

		let table = this.getElement(this.DETAIL_TABLE);
		if(!table) table = createTable(this.DETAIL_TABLE, card.getContent());
		table.removeRows();
		for(let i = 0; i < this.invoice.details.length; i++) {
			let detail = this.invoice.details[i];
			this.printDetail(table, detail, i);
		}

		let div = this.createElement(TAG.DIV);
		card.addContent(div);

		let addButton = this.getElement(this.DETAIL_ADD);
		if(!this.invoice.isReadonly() && !addButton) {
			addButton = new AonIconButton();
			addButton.id = this.DETAIL_ADD;
			addButton.title = MSG.ADD_DETAIL;
			addButton.icon = MATERIAL_ICONS.ADD;
			addButton.addEventListener(EVENT.CLICK, () => {
				this.setFocus(this.DETAIL_DESCRIPTION + this.invoice.details.length);
				this.invoice.addDetail();
				this.reload();
				const i = this.invoice.details.length -1;
				const detail = this.invoice.details[i];
				this.printDetailDialog(detail, i);
			});
			div.appendChild(addButton);
		}
	}

	printDetail(table, detail, i) {
		table.addRow(); // ----- ROW i

		// ----- DETAIL CONCEPT | DESCRIPTION | PRODUCT
		
		let description = createSuggestion(this.DETAIL_DESCRIPTION + i, MSG.CONCEPT);
		description.addEventListener(EVENT.KEYUP, () => {
			if(description.value.length > 2) {
				let data = { value: description.value};
				getItems(data).then(r => {
					description.buildOptions(r.map(r => {return {
						name: r.name,
						value: r,
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
			detail.item = e.detail.item;
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
		
		let amount = createNumber(this.DETAIL_AMOUNT + i, MSG.AMOUNT);
		amount.format = CONSTANT.TRUE;
		amount.decimals = "2";
		amount.value = detail.amount;
		table.addCell(amount);
		amount.readonly = CONSTANT.TRUE;

		// ----- DETAIL OPTIONS

		let detailOptions = new AonIconButton();
		detailOptions.id = this.DETAIL_OPTIONS + i;
		detailOptions.title = MSG.OPTIONS;
		detailOptions.icon = MATERIAL_ICONS.EDIT;
		detailOptions.addEventListener(EVENT.CLICK, () => {
			this.printDetailDialog(detail, i);
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
			});
			table.addCell(detailDelete);
		}
	}

	printDetailDialog(detail, i) {
		let dialog = this.getApplication().getDialog();
		dialog.setTitle(MSG.DETAIL);
		dialog.addAcceptAction(() => {
			
		});
		
		let div = this.createElement(TAG.DIV);
		div.style.margin = '15px';
		dialog.setContent(div);

		let table = createTable(this.DIALOG + 'Detail', div);
		table.addRow(); // ----- ROW 1

		// ----- DETAIL CONCEPT | DESCRIPTION | PRODUCT
		
		let description = createSuggestion(this.DETAIL_DESCRIPTION + 'Dialog' + i, MSG.CONCEPT);
		description.addEventListener(EVENT.KEYUP, () => {
			if(description.value.length > 2) {
				let data = { value: description.value};
				getItems(data).then(r => {
					description.buildOptions(r.map(r => {return {
						name: r.name,
						value: r,
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
			detail.item = e.detail.item;
			detail.price = e.detail.item.price;
			this.invoice.setDetail(detail, i);
			this.reload();
			this.printDetailDialog(this.invoice.details[i], i);
		});

		table.addCell(description, '2');
		description.readonly = this.invoice.isReadonly();
		description.value = detail.description;


		table.addRow(); // ----- ROW 2

		// ----- DETAIL QUANTITY

		let quantity = createNumber(this.DETAIL_QUANTITY + 'Dialog' + i, MSG.QUANTITY);
		quantity.format = CONSTANT.TRUE;
		quantity.decimals = "2";
		quantity.addEventListener(EVENT.CHANGE, () => {
			detail.quantity = quantity.value;
			this.invoice.setDetail(detail, i);
			this.reload();
			this.printDetailDialog(this.invoice.details[i], i);
		});
		table.addCell(quantity);
		quantity.readonly = this.invoice.isReadonly();
		quantity.value = detail.quantity;
	
		// ----- DETAIL PRICE
	
		let price = createNumber(this.DETAIL_PRICE + 'Dialog' + i, MSG.PRICE);
		price.format = CONSTANT.TRUE;
		price.decimals = "2";
		price.addEventListener(EVENT.CHANGE, () => {
			detail.price = price.value;
			this.invoice.setDetail(detail, i);
			this.reload();
			this.printDetailDialog(this.invoice.details[i], i);
		});
		table.addCell(price);
		price.readonly = this.invoice.isReadonly();
		price.value = detail.price;

		table.addRow(); // ----- ROW 3

		// ----- DETAIL DISCOUNT
		
		let discount = createNumber(this.DETAIL_DISCOUNT + 'Dialog', '%Dto');//MSG.DISCOUNT
		discount.format = CONSTANT.TRUE;
		discount.decimals = "2";
		discount.addEventListener(EVENT.CHANGE, () => {
			detail.discount = discount.value;
			this.invoice.setDetail(detail, i);
			this.reload();
			this.printDetailDialog(this.invoice.details[i], i);
		});
		table.addCell(discount);
		discount.readonly = this.invoice.isReadonly();
		discount.value = detail.discount;

		// ----- DETAIL AMOUNT
		
		let amount = createNumber(this.DETAIL_AMOUNT + 'Dialog' + i, MSG.AMOUNT);
		amount.format = CONSTANT.TRUE;
		amount.decimals = "2";
		amount.value = detail.amount;
		table.addCell(amount);
		amount.readonly = CONSTANT.TRUE;		

		table.addRow(); // ----- ROW 3

		// ----- PREPAYMENT | SUPLIDO
		
		let prepayment = new AonSwitch();
		prepayment.id = this.DETAIL_PREPAYMENT + 'Dialog' + i;
		prepayment.title = 'Suplido';//MSG.DETAIL_PREPAYMENT;
		prepayment.addEventListener(EVENT.CHANGE, () => {
			detail.prepayment = prepayment.checked;
			this.invoice.setDetail(detail, i);
			this.reload();
			this.printDetailDialog(this.invoice.details[i], i);
		});
		table.addCell(prepayment);
		prepayment.readonly = this.invoice.isReadonly();
		prepayment.checked = detail.prepayment;

		// ----- DETAIL VAT
	
		let vat = createNumber(this.DETAIL_VAT + 'Dialog' + i, '%IVA');
		vat.options = JSON.stringify(TaxIVAPercentage);
		if(detail.prepayment === undefined) detail.prepayment = false;
		vat.readonly = this.invoice.isReadonly() || detail.prepayment;
		vat.addEventListener(EVENT.SELECT, () => {
			console.log(vat.value);
			detail.percentage = vat.value;
			this.invoice.setDetail(detail, i);
			this.reload();
			this.printDetailDialog(this.invoice.details[i], i);
		});
		table.addCell(vat);
		if(this.invoice.isEmitida() && !this.invoice.isNacional()) {
			detail.percentage = undefined;
			vat.setDisabled(true);
		}
		if(detail.percentage) vat.value = detail.percentage;

		dialog.open();
	}

	buildFinanceCard(parent) {
		let card = this.getElement(this.FINANCE);
		if(!card) {
			card = createCard(this.FINANCE, MSG.EXPIRATIONS, parent);
			card.onToggle();
		}
		
		let table = this.getElement(this.FINANCE_TABLE);
		if(!table) table = createTable(this.FINANCE_TABLE, card.getContent());

		table.removeRows();
		for(let i = 0; i < this.invoice.finances.length; i++) {
			let finance = this.invoice.finances[i];
			this.printFinance(table, finance, i);
		}

		let div = this.createElement(TAG.DIV);
		card.addContent(div);

		let addButton = this.getElement(this.FINANCE_ADD);
		if(!this.invoice.isReadonly() && !addButton) {
			addButton = new AonIconButton();
			addButton.id = this.FINANCE_ADD;
			addButton.title = MSG.ADD_FINANCE;
			addButton.icon = MATERIAL_ICONS.ADD;
			addButton.addEventListener('click', () => {
				this.setFocus(this.FINANCE_DUE_DATE + this.invoice.finances.length);
				this.invoice.addFinance();
				this.reload();
				const i = this.invoice.finances.length -1;
				const finance = this.invoice.finances[i];
				this.printFinanceDialog(finance, i);
			});
			div.appendChild(addButton);
		}
	}

	printFinance(table, finance, i) {
		table.addRow(); // ----- ROW i
		
		// ----- FINANCE DUE DATE

		let date = createDate(this.FINANCE_DUE_DATE + i, MSG.DATE);
		date.readonly = this.invoice.isReadonly();
		date.setDate(finance.due_date);
		date.addEventListener(EVENT.CHANGE, () => {
			finance.due_date = date.getDateValue();
			this.setFocus(date.id);
			this.invoice.setFinance(finance, i);
		});
		table.addCell(date);

		// ----- FINANCE AMOUNT
		
		let amount = createNumber(this.FINANCE_AMOUNT + i, MSG.AMOUNT);
		amount.format = CONSTANT.TRUE;
		amount.decimals = "2";
		amount.readonly = this.invoice.isReadonly();
		amount.addEventListener(EVENT.CHANGE, () => {
			this.setFocus(this.FINANCE_AMOUNT + i);
			finance.amount = amount.value;
			this.invoice.setFinance(finance, i);
		});
		table.addCell(amount);
		amount.value = finance.amount;

		// ----- FINANCE OPTIONS

		let financeOptions = new AonIconButton();
		financeOptions.id = this.FINANCE_OPTIONS + i;
		financeOptions.title = MSG.OPTIONS;
		financeOptions.icon = MATERIAL_ICONS.EDIT;
		financeOptions.addEventListener(EVENT.CLICK, () => {
			this.printFinanceDialog(finance, i);
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
			});
			table.addCell(financeDelete);
		}
	}

	printFinanceDialog(finance, i) {
		let dialog = this.getApplication().getDialog();
		dialog.setTitle(MSG.EXPIRATION);
		dialog.addAcceptAction(() => {
			
		});
		

		let div = this.createElement(TAG.DIV);
		div.style.margin = '15px';
		dialog.setContent(div);

		let table = createTable(this.DIALOG + 'Detail', div);

		table.addRow(); // ----- ROW 1

		// ----- FINANCE DUE DATE

		let date = createDate(this.FINANCE_DUE_DATE + 'Dialog' + i, MSG.DATE);
		date.readonly = this.invoice.isReadonly();
		date.setDate(finance.due_date);
		date.addEventListener(EVENT.CHANGE, () => {
			finance.due_date = date.getDateValue();
			this.setFocus(date.id);
			this.invoice.setFinance(finance, i);
		});
		table.addCell(date);

		table.addRow(); // ----- ROW 2

		// ----- FINANCE PAYMETHOD

		let paymethod = createSelect(this.FINANCE_PAYMETHOD + 'Dialog' + i, MSG.PAYMETHOD);
		paymethod.autocomplete = true;
		// paymethod.options = JSON.stringify(Paymethods);
		paymethod.readonly = this.invoice.isReadonly();
		paymethod.addEventListener(EVENT.SELECT, () => {
			this.setFocus(this.FINANCE_BANK_ACCOUNT + i);
			finance.paymethod = paymethod.value;
			this.invoice.setFinance(finance, i);
		});
		table.addCell(paymethod);

		getPaymethods({}).then(paymethods => {
			let pms = paymethods.map(pm => {return {name: pm.name, value: pm.id};});
			paymethod.options = JSON.stringify(pms);
			paymethod.value = finance.paymethod;
		});


		table.addRow(); // ----- ROW 3

		// ----- FINANCE BANK ACCOUNT | RBANK
		
		let bankAccount = createSuggestion(this.FINANCE_BANK_ACCOUNT + 'Dialog' + i, MSG.BANK_ACCOUNT);
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
	
		let amount = createNumber(this.FINANCE_AMOUNT + 'Dialog' +  i, MSG.AMOUNT);
		amount.format = CONSTANT.TRUE;
		amount.decimals = "2";
		amount.readonly = this.invoice.isReadonly();
		amount.addEventListener(EVENT.CHANGE, () => {
			this.setFocus(this.FINANCE_AMOUNT + i);
			finance.amount = amount.value;
			this.invoice.setFinance(finance, i);
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
			viewer.type = !this.getInvoice().file || this.getInvoice().isEmitida()
				? 'application/pdf' : this.getInvoice().file.content_type;
			
			let json = this.getInvoice();
			json.domain_id = LS.getDomainId();
			json.domain_name = LS.getDomainName();
			json.login = LS.getDomainLogin();
			
			viewer.file = !this.getInvoice().file || this.getInvoice().isEmitida()
				? '/ms/api/download_invoice_pdf?json=' + btoa(JSON.stringify(json))
				: this.getInvoice().file.url;
			viewer.width = fileDiv.offsetWidth;
			fileDiv.appendChild(viewer);
		}
	}

	buildOptions() {
		let aonInvoice = this.getApplication();
		let aonInvoiceToolbar = this.getElement(aonInvoice.TOOLBAR);
		aonInvoiceToolbar.removeButtons();
		aonInvoice.addToolbarOption('Options', 'more_vert', () => {
			let button = this.getElement(aonInvoiceToolbar.TOOL_SECTION + 'OptionsButton');
			const top  = button.getBoundingClientRect().top;
			const left = button.getBoundingClientRect().left;

			let d = document.getElementById(aonInvoice.OPTION_DIALOG);

			let send = ACTION.SEND_INVOICE;
			send.permission = true;
			send.backgroundColor = INVOICE.color;
			send.fn = () => this.sendInvoice();

			// let record = ACTION.RECORD_INVOICE;
			// record.permission = false;
			// record.backgroundColor = INVOICE.color;
			// record.fn = () => this.recordInvoice();

			let reject = ACTION.REJECT_INVOICE;
			reject.permission = true;
			reject.backgroundColor = INVOICE.color;
			reject.fn = () => this.rejectInvoice();

			let restore = ACTION.RESTORE_INVOICE;
			restore.permission = true;
			restore.backgroundColor = INVOICE.color;
			restore.fn = () => this.restoreInvoice();

			let addComment = ACTION.COMMENT;
			addComment.permission = true;
			addComment.backgroundColor = INVOICE.color;
			addComment.fn = () => this.addInvoiceComment();

			let deleteInvoice = ACTION.DELETE_TO_TRASH;
			deleteInvoice.permission = true;
			deleteInvoice.backgroundColor = INVOICE.color;
			deleteInvoice.fn = () => this.trashInvoice();

			let deleteForever = ACTION.DELETE_FOREVER;
			deleteForever.permission = true;
			deleteForever.backgroundColor = INVOICE.color;
			deleteForever.fn = () => this.removeInvoice();

			let rectify = ACTION.RECTIFY_INVOICE;
			rectify.permission = true;
			rectify.backgroundColor = INVOICE.color;
			rectify.fn = () => this.rectifyInvoice();

			// let duplicate = ACTION.DUPLICATE_INVOICE;
			// duplicate.permission = true;
			// duplicate.backgroundColor = INVOICE.color;
			// duplicate.fn = () => this.duplicateInvoice();

			let addFile = ACTION.ADD_FILE;
			addFile.permission = true;
			addFile.backgroundColor = INVOICE.color;
			addFile.fn = () => this.addInvoiceFile();

			let actions = [];
			if(this.invoice.isRejected()) {
				actions = [restore, deleteInvoice];
			} else if(this.invoice.isTrash()) {
				actions = [restore, deleteForever];
			}  else if(this.invoice.isInbox()){
				if(this.getDur().isAdmin() || this.getDur().isInvoiceManager()){
					actions = [addComment, send, deleteInvoice, reject] //, record];
				} else {
					actions = [addComment, deleteInvoice];
				}
			} else {
				actions = [send, rectify];
			}
			if(!this.invoice.file && !this.invoice.isEmitida()){
				actions.push(addFile);
			}

			d.setMenuOptions(actions, top, left);
			d.open();
		});
	  }
}
if(!window.customElements.get(TAG.AON_MOBILE_INVOICE)){
	window.customElements.define(TAG.AON_MOBILE_INVOICE, AonMobileInvoice);
}

