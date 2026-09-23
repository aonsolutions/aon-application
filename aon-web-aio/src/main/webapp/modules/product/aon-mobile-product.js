import { AonBasicTable } from '../../components/aon-basic-table.js';
import { AonToolbar } from '../../components/aon-toolbar.js';
import { createCard, createInput, createNumber, createSelect } from '../../components/CreateComponent.js';
import { CONSTANT, EVENT, MSG, TAG } from '../../environments/environments.js';
import { ToolbarType } from '../../models/enums.js';
import { getProductCategories } from '../../services/productService.js';
import { getVats, getWithholdings } from '../../services/taxService.js';
import * as ACTION from '../actions.js';
import { AonMobileProductList } from './aon-mobile-product-list.js';
import { AonProduct } from './aon-product.js';

export class AonMobileProduct extends AonProduct {

	constructor () {
		super();
	}

	initialize() {
		this.id = this.id || 'aonMobileProduct';
		super.initialize();
	}

	build() {
		this.buildToolbar();

		let div = this.createElement(TAG.DIV);
		div.id = this.id + 'Div';
		this.appendChild(div);

		this.buildGeneralCard(div);
		if(!this.expense)
			this.buildItemCard(div);
	}

	buildToolbar() {
		let newTitle = this.expense ? MSG.NEW_EXPENSE : MSG.NEW_PRODUCT;
		let toolbar = new AonToolbar();
		toolbar.id = this.PRODUCT_TOOLBAR;
		toolbar.type = ToolbarType.SECONDARY;
		toolbar.title = this.product.getId()
			? this.product.getName() : newTitle.toUpperCase();
		this.appendChild(toolbar);
		toolbar.addButton2(ACTION.SAVE, () => this.save());
		toolbar.addButton2(ACTION.BACK, () => this.back());
	}

	buildGeneralCard(parent) {
		let card = createCard(this.PRODUCT_CARD, MSG.GENERAL_INFORMATION, parent);

		let table = new AonBasicTable();
		table.id = this.PRODUCT_TABLE;
		card.setContent(table);

		table.addRow();

		let codeInput = createInput(this.PRODUCT_CODE, MSG.CODE);
		codeInput.value = this.product.getCode();
		codeInput.addEventListener(EVENT.CHANGE, () => this.product.setCode(codeInput.value));
		table.addCell(codeInput);

		table.addRow();

		let nameInput = createInput(this.PRODUCT_NAME, MSG.NAME);
		nameInput.value = this.product.getName();
		nameInput.addEventListener(EVENT.CHANGE, () => this.product.setName(nameInput.value));
		table.addCell(nameInput);

		if(!this.expense) {
			table.addRow();

			let types = [
				{name: MSG.SERVICE, value: 'SERVICE'},
				{name: MSG.COMMERCIAL_PRODUCT, value: 'COMMERCIAL_PRODUCT'},
				{name: MSG.SUPPLIED, value: 'PREPAYMENT'}];
			let typeSelect = createSelect(this.PRODUCT_TYPE, MSG.TYPE);
			typeSelect.setOptions(types);
			typeSelect.value = this.product.getType();
			typeSelect.addEventListener(EVENT.SELECT, () => {
				this.product.setType(typeSelect.getDetail().value);
				this.setTaxesDisabled(typeSelect.getDetail().value === 'PREPAYMENT');
			});
			table.addCell(typeSelect);
		} else {
			this.product.setType('EXPENSE');
		}

		table.addRow();

		let categorySelect = createSelect(this.PRODUCT_CATEGORY, MSG.CATEGORY);
		categorySelect.setAlias(CONSTANT.ID, CONSTANT.NAME);
		categorySelect.addEventListener(EVENT.SELECT, () => {
			this.product.category = categorySelect.getDetail();
		});
		getProductCategories({}).then(categories => {
			categorySelect.setOptions(categories);
			categorySelect.value = this.product.category && this.product.category.id
				? this.product.category.id : categories[0] && categories[0].id;
		});
		table.addCell(categorySelect);

		table.addRow();

		let vatSelect = createSelect(this.PRODUCT_VAT, MSG.VAT);
		vatSelect.setAlias(CONSTANT.ID, CONSTANT.NAME);
		getVats().then(vats => {
			vatSelect.setOptions(vats);
			vatSelect.setValueObject(this.product.vat);
		});
		vatSelect.addEventListener(EVENT.SELECT, () => {
			this.product.setVat(vatSelect.getValueObject());
			this.refreshPvp();
		});
		table.addCell(vatSelect);

		table.addRow();

		let retentionSelect = createSelect(this.PRODUCT_RETENTION, MSG.IRPF);
		retentionSelect.setAlias(CONSTANT.ID, CONSTANT.NAME);
		getWithholdings().then(withholdings => {
			retentionSelect.setOptions(withholdings);
			retentionSelect.setValueObject(this.product.retention);
		});
		retentionSelect.addEventListener(EVENT.SELECT, () => {
			this.product.setRetention(retentionSelect.getValueObject());
			this.refreshPvp();
		});
		table.addCell(retentionSelect);
	}

	buildItemCard(parent) {
		let card = createCard(this.ITEM_CARD, MSG.ADDITIONAL_INFORMATION, parent);

		let table = new AonBasicTable();
		table.id = this.ITEM_TABLE;
		card.setContent(table);

		table.addRow();

		let barcodeInput = createInput(this.ITEM_BARCODE, MSG.BARCODE);
		barcodeInput.value = this.item.getBarcode();
		barcodeInput.addEventListener(EVENT.CHANGE, () => this.item.setBarcode(barcodeInput.value));
		table.addCell(barcodeInput);

		table.addRow();

		let descriptionInput = createInput(this.ITEM_DESCRIPTION, MSG.DESCRIPTION);
		descriptionInput.value = this.item.getDescription();
		descriptionInput.addEventListener(EVENT.CHANGE, () => this.item.setDescription(descriptionInput.value));
		table.addCell(descriptionInput);

		table.addRow();

		let purchasePrice = this.createPrice(this.ITEM_PURCHASE_PRICE, MSG.PURCHASE_PRICE);
		purchasePrice.value = this.item.getPurchasePrice();
		purchasePrice.addEventListener(EVENT.CHANGE, () => {
			this.item.setPurchasePrice(purchasePrice.value);
			this.getElement(this.ITEM_PROFIT_PERCENT).value = this.item.getProfitPercent();
			this.getElement(this.ITEM_PRICE).value = this.item.getPrice();
			this.refreshPvp();
		});
		table.addCell(purchasePrice);

		table.addRow();

		let profitPercent = this.createPrice(this.ITEM_PROFIT_PERCENT, '% ' + MSG.PROFIT);
		profitPercent.value = this.item.getProfitPercent();
		profitPercent.addEventListener(EVENT.CHANGE, () => {
			this.item.setProfitPercent(profitPercent.value);
			this.getElement(this.ITEM_PRICE).value = this.item.getPrice();
			this.refreshPvp();
		});
		table.addCell(profitPercent);

		table.addRow();

		let price = this.createPrice(this.ITEM_PRICE, MSG.PRICE);
		price.value = this.item.getPrice();
		price.addEventListener(EVENT.CHANGE, () => {
			this.item.setPrice(price.value);
			this.getElement(this.ITEM_PROFIT_PERCENT).value = this.item.getProfitPercent();
			this.refreshPvp();
		});
		table.addCell(price);

		table.addRow();

		let pvp = this.createPrice(this.ITEM_PVP, 'PVP');
		pvp.value = this.getPvp();
		pvp.readonly = true;
		table.addCell(pvp);
	}

	// ACTIONS

	back() {
		let productList = new AonMobileProductList();
		productList.id = this.id + 'List';
		productList.filter = {expense: this.expense};
		this.getApplication().setContent(productList);
	}

	setTaxesDisabled(disabled) {
		let vat = this.getElement(this.PRODUCT_VAT);
		let retention = this.getElement(this.PRODUCT_RETENTION);
		if(disabled) {
			vat.value = '';
			retention.value = '';
		}
		vat.disabled = disabled;
		retention.disabled = disabled;
	}

	refreshPvp() {
		let pvp = this.getElement(this.ITEM_PVP);
		if(pvp) pvp.value = this.getPvp();
	}

	// Create Components

	createPrice(id, title) {
		let number = createNumber(id, title);
		number.format = CONSTANT.TRUE;
		number.decimals = "2";
		return number;
	}
}

if(!window.customElements.get(TAG.AON_MOBILE_PRODUCT)){
	window.customElements.define(TAG.AON_MOBILE_PRODUCT, AonMobileProduct);
}
