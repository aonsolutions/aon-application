import { AonTable } from '../../components/aon-table.js';
import { AonElement } from '../../components/AonElement.js';
import { CONSTANT, MSG } from '../../environments/environments.js';
import { getItem, getProducts } from '../../services/productService.js';
import { AonProduct } from './aon-product.js';
import * as OPTION from '../invoice/InvoiceOptions.js';
import { INVOICE } from '../../services/app.js';

export class AonProductList extends AonElement {

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
		this.id = this.id || CONSTANT.AON_PRODUCT;
		this.TABLE = this.id + CONSTANT.TABLE.initCap();
	}

	build() {
		let table = new AonTable();
		table.id = this.TABLE;
		table.setApp(INVOICE);
		this.appendChild(table);
		table.addColumn(MSG.CODE, CONSTANT.STRING, CONSTANT.CODE, '25%');
		table.addColumn(MSG.NAME, CONSTANT.STRING, CONSTANT.NAME, '50%');
		table.addColumn(MSG.CATEGORY, CONSTANT.STRING, CONSTANT.CATEGORY_NAME, '25%');
		this.init();
	}

	init() {
		let table = this.getElement(this.TABLE);
		if(table) {
			getProducts(this.getFilter()).then(products => {
				table.removeRows();
				products.map(p => {
					p[CONSTANT.CATEGORY_NAME] = p.category.name || '';
					return p;
				}).forEach((product, i) => {
					table.addRow(product, () => this.aonProduct(product));
				});
			});
		}
	}

	aonProduct(product) {
		if(product && product.id){
			getItem({product: product.id}).then(item => {
				let aonProduct = new AonProduct();
				aonProduct.expense = this.isExpense();
				aonProduct.setProduct(product);
				aonProduct.setItem(item);
				this.getApplication().setContent(aonProduct);				
			})
		} else {
			let aonProduct = new AonProduct();
			aonProduct.expense = this.isExpense();
			this.getApplication().setContent(aonProduct);
		}	
	}

	getFilter() {
		return this.filter || {};
	}

	setFilter(filter) {
		this.filter = filter;
		this.init();
	}

	isExpense() {
		return this.getApplication().getParent().selectedOption.id === OPTION.EXPENSES.id
	}
}
window.customElements.define('aon-product-list', AonProductList);
