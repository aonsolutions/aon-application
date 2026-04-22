import {AonElement} from '../../components/AonElement.js';
import { ToolbarType} from '../../models/enums.js';

import {AonToolbar} from "../../components/aon-toolbar.js";
import {AonCard} from "../../components/aon-card.js";

import {CONSTANT, EVENT, MSG, TAG } from '../../environments/environments.js'; 

import * as ACTION from '../actions.js';
import { AonBasicTable } from '../../components/aon-basic-table.js';
import { Product } from '../../models/product/Product.js';
import { Item } from '../../models/product/Item.js';
import { getProductCategories, saveItem, saveProduct } from '../../services/productService.js';
import { TaxIVAPercentage, TaxIVAPercentage2, TaxRetentionPercentage, TaxRetentionPercentage2 } from '../invoice/invoiceEnums.js';
import { AonMobileProductList } from './aon-mobile-product-list.js';
import { AonProductList } from './aon-product-list.js';
import * as OPTION from '../invoice/InvoiceOptions.js';
import { createInput, createNumber, createSelect } from '../../components/CreateComponent.js';
import { getVats, getWithholdings } from '../../services/taxService.js';

export class AonProduct extends AonElement {

	PRODUCT_TOOLBAR;
    PRODUCT_CARD;
    PRODUCT_TABLE;
    PRODUCT_CODE;
	PRODUCT_NAME;
	PRODUCT_TYPE;
	PRODUCT_CATEGORY;
	PRODUCT_VAT;
	PRODUCT_RETENTION;

	ITEM_CARD;
	ITEM_TABLE;
	ITEM_BARCODE;
	ITEM_DESCRIPTION;
	ITEM_PURCHASE_PRICE;
	ITEM_PROFIT_PERCENT;
	ITEM_PRICE;
	ITEM_PVP;

	product;
	item;
	expense;

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
		this.build();
    }

	initialize() {
		this.id = this.id || 'aonProduct';
		this.PRODUCT_TOOLBAR = this.id + 'Toolbar';
        this.PRODUCT_CARD = this.id + 'Card';
		this.PRODUCT_TABLE = this.id + 'Table';
		this.PRODUCT_CODE = this.id + 'Code';
		this.PRODUCT_NAME = this.id + 'Name';
		this.PRODUCT_TYPE = this.id + 'Type';
		this.PRODUCT_CATEGORY = this.id + 'Category';
		this.PRODUCT_VAT = this.id + 'Vat';
		this.PRODUCT_RETENTION = this.id + 'Retention';

		this.ITEM_CARD = this.id + 'ItemCard';
		this.ITEM_TABLE = this.id + 'ItemTable';
		this.ITEM_BARCODE = this.id + 'ItemBarcode';
		this.ITEM_DESCRIPTION = this.id + 'ItemDescription';
		this.ITEM_PURCHASE_PRICE = this.id + 'ItemPurchasePrice';
		this.ITEM_PROFIT_PERCENT = this.id + 'ItemProfitPercent';
		this.ITEM_PRICE = this.id + 'ItemPrice';
		this.ITEM_PVP = this.id + 'ItemPvp';

		this.product = new Product(this.product);
		this.item = new Item(this.item);
		this.expense = this.expense || this.product.getType() === 'EXPENSE';
	}

	build() {
		let newTitle = this.expense ? MSG.NEW_EXPENSE : MSG.NEW_PRODUCT;
		let toolbar = new AonToolbar();
		toolbar.id = this.PRODUCT_TOOLBAR;
		toolbar.type = ToolbarType.SECONDARY;
		toolbar.title = this.product.getId()
            ? this.product.getName() : newTitle.toUpperCase(); 
		this.appendChild(toolbar);
		toolbar.addButton2(ACTION.SAVE, () => this.save());
		toolbar.addButton2(ACTION.BACK, () => this.back());

		let div = this.createElement(TAG.DIV);
		div.style.display = "flex";
		div.style.width = "100%";
		this.appendChild(div);

		this.buildGeneralCard(div);
		if(!this.expense)
			this.buildItemCard(div);
	}

	buildGeneralCard(parent){
		let card = this.createCard(this.PRODUCT_CARD, MSG.GENERAL_INFORMATION);
		parent.appendChild(card);

		let div = this.createElement(TAG.DIV);
		card.setContent(div);

		let table = new AonBasicTable();
		table.id = this.PRODUCT_TABLE;
		div.appendChild(table);

		table.addRow();

		let codeInput = createInput(this.PRODUCT_CODE, MSG.CODE);
		codeInput.value = this.product.getCode();
		codeInput.addEventListener(EVENT.CHANGE, () => this.product.setCode(codeInput.value));
        let td1 = table.addCell(codeInput);
		td1.style.width = '25%';

		let nameInput = createInput(this.PRODUCT_NAME, MSG.NAME);
		nameInput.value = this.product.getName();
		nameInput.addEventListener(EVENT.CHANGE, () => this.product.setName(nameInput.value));
       	let td2 = table.addCell(nameInput);
		td2.style.width = '75%';

		let table2 = new AonBasicTable();
		table2.id = this.PRODUCT_TABLE + '2';
		div.appendChild(table2);

        table2.addRow();
		if(!this.expense) {
			let types = [
				{name: MSG.SERVICE, value: 'SERVICE'},
				{name: MSG.COMMERCIAL_PRODUCT, value: 'COMMERCIAL_PRODUCT'},
				{name: MSG.SUPPLIED, value: 'PREPAYMENT'}];
			let typeSelect = createSelect(this.PRODUCT_TYPE, MSG.TYPE);
    	    typeSelect.setOptions(types);
			typeSelect.value = this.expense ? 'EXPENSE' : this.product.getType();
			
			typeSelect.addEventListener(EVENT.SELECT, (e) => {
				this.product.setType(typeSelect.getDetail().value);
				if(typeSelect.getDetail().value === 'PREPAYMENT') {
					let vat = this.getElement(this.PRODUCT_VAT);
					vat.value = '';
					vat.disabled = true;

					let ret = this.getElement(this.PRODUCT_RETENTION);
					ret.value = '';
					ret.disabled = true;
				} else {
					let vat = this.getElement(this.PRODUCT_VAT);
					vat.disabled = false;

					let ret = this.getElement(this.PRODUCT_RETENTION);
					ret.disabled = false;
				}
			});
		
        	table2.addCell(typeSelect);
		} else {
			this.product.setType('EXPENSE');
		}
		let categorySelect = createSelect(this.PRODUCT_CATEGORY, MSG.CATEGORY);
		categorySelect.setAlias('id', 'name');
		categorySelect.addEventListener(EVENT.SELECT, (e) =>  {
			this.product.category = categorySelect.getDetail();
		});

        getProductCategories({}).then( categories => {
            categorySelect.setOptions(categories);
			categorySelect.value = this.product.category.id;
		});
		
        table2.addCell(categorySelect, this.expense ? 2 : 1);

		table2.addRow();

		let vatSelect = createSelect(this.PRODUCT_VAT, MSG.VAT);
		vatSelect.setAlias(CONSTANT.ID, CONSTANT.NAME);
		getVats().then(vats => {
			vatSelect.setOptions(vats);
			vatSelect.setValueObject(this.product.vat);
		});

		vatSelect.addEventListener(EVENT.SELECT, () => {
			this.product.setVat(vatSelect.getValueObject());
			this.getElement(this.ITEM_PVP).value = this.getPvp();
		});
        table2.addCell(vatSelect);

		let retentionSelect = createSelect(this.PRODUCT_RETENTION, MSG.IRPF);
		retentionSelect.setAlias(CONSTANT.ID, CONSTANT.NAME);
		getWithholdings().then(withholdings => {
			retentionSelect.setOptions(withholdings);
			retentionSelect.setValueObject(this.product.retention);
		});

		retentionSelect.addEventListener(EVENT.SELECT, () => {
			this.product.setRetention(retentionSelect.getValueObject());
			this.getElement(this.ITEM_PVP).value = this.getPvp();
		});
		table2.addCell(retentionSelect);
 	}

	buildItemCard(parent){
		let card = this.createCard(this.ITEM_CARD, MSG.ADDITIONAL_INFORMATION);
		parent.appendChild(card);

		let div = this.createElement(TAG.DIV);
		card.setContent(div);

		let table = new AonBasicTable();
		table.id = this.ITEM_TABLE;
		div.appendChild(table);

		table.addRow();

		let barcodeInput = createInput(this.ITEM_BARCODE, MSG.BARCODE);
		barcodeInput.value = this.item.getBarcode();
		barcodeInput.addEventListener(EVENT.CHANGE, () => this.item.setBarcode(barcodeInput.value));
		table.addCell(barcodeInput, 4);

		table.addRow();

		let descriptionInput = createInput(this.ITEM_DESCRIPTION, MSG.DESCRIPTION);
		descriptionInput.value = this.item.getDescription();
		descriptionInput.addEventListener(EVENT.CHANGE, () => this.item.setDescription(descriptionInput.value));
       	table.addCell(descriptionInput, 4);

	 	table.addRow();
		
		let purchasePrice = createNumber(this.ITEM_PURCHASE_PRICE, MSG.PURCHASE_PRICE);
		purchasePrice.value = this.item.getPurchasePrice();
		purchasePrice.format = CONSTANT.TRUE;
		purchasePrice.decimals = "2";
		purchasePrice.addEventListener(EVENT.CHANGE, () => {
			this.item.setPurchasePrice(purchasePrice.value);
			this.getElement(this.ITEM_PROFIT_PERCENT).value = this.item.getProfitPercent();
			this.getElement(this.ITEM_PRICE).value = this.item.getPrice();
			this.getElement(this.ITEM_PVP).value = this.getPvp();
		});
		table.addCell(purchasePrice, 1);

		let profitPercent = createNumber(this.ITEM_PROFIT_PERCENT, '% ' + MSG.PROFIT);
		profitPercent.value = this.item.getProfitPercent();
		profitPercent.format = CONSTANT.TRUE;
		profitPercent.decimals = "2";
		profitPercent.addEventListener(EVENT.CHANGE, () => {
			this.item.setProfitPercent(profitPercent.value);
			this.getElement(this.ITEM_PRICE).value = this.item.getPrice();
			this.getElement(this.ITEM_PVP).value = this.getPvp();
		});
		table.addCell(profitPercent, 1);

		let price = createNumber(this.ITEM_PRICE, MSG.PRICE);
		price.value = this.item.getPrice();
		price.format = CONSTANT.TRUE;
		price.decimals = "2";
		price.addEventListener(EVENT.CHANGE, () => {
			this.item.setPrice(price.value);
			this.getElement(this.ITEM_PROFIT_PERCENT).value = this.item.getProfitPercent();
			this.getElement(this.ITEM_PVP).value = this.getPvp();
		});
		table.addCell(price, 1);

		let pvp = createNumber(this.ITEM_PVP, 'PVP');
		pvp.value = this.getPvp();
		pvp.readonly = true;
		pvp.format = CONSTANT.TRUE;
		pvp.decimals = "2";
		table.addCell(pvp, 1);
 	}
	
	// ACTIONS

	back() {
		let filter;
		if(this.getApplication().getParent().selectedOption.id === OPTION.EXPENSES.id){
			filter = {expense: true};
		} else if(this.getApplication().getParent().selectedOption.id === OPTION.PRODUCT.id) {
			filter = {expense: false};
		}
		let productList = this.isMobile() ? new AonMobileProductList() : new AonProductList();
		productList.id = this.PRODUCT_LIST;	
		productList.filter = filter;
		this.getApplication().setContent(productList);
	}

	save() {
		saveProduct(this.product).then(p => {
			this.product = new Product(p);
			this.item.setProduct(new Product(p));
			saveItem(this.item).then(item => {
				this.getApplication().getToast().start({
					type: CONSTANT.SUCCESS,
					message: MSG.SAVED_DATA
				});
				this.item = new Item(item);
			}).catch(e => this.showError(e));
		}).catch(e => this.showError(e));
	}

	getPvp() {
        let pvp = this.item.price;
		if(this.product.vat && this.product.vat.percentage)
			pvp = pvp + (this.item.price * this.product.vat.percentage / 100);
		if(this.product.retention && this.product.retention.percentage)
			pvp = pvp - (this.item.price * this.product.retention.percentage / 100);  
		return pvp;
    }


	setProduct(product) {
		this.product = new Product(product);
	}

	setItem(item) {
		this.item = new Item(item);
	}

	// Create Components

	createCard(id, title) {
		let card = new AonCard();
		card.id = id;
		card.title = title;
		card.style.width = '50%';
		return card;
	}
}

if(!window.customElements.get(TAG.AON_PRODUCT)){
	window.customElements.define(TAG.AON_PRODUCT, AonProduct);
}