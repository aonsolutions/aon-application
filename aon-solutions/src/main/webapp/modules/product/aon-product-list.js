import { AonElement } from '../../components/AonElement.js';
import { AonTable } from '../../components/components.js';
import { CONSTANT, MSG } from '../../environments/environments.js';
import { getProducts } from '../../services/productService.js';

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
		this.appendChild(table);
		table.addColumn(MSG.CODE, CONSTANT.STRING, CONSTANT.CODE, '25%');
		table.addColumn(MSG.NAME, CONSTANT.STRING, CONSTANT.NAME, '50%');
		table.addColumn(MSG.CATEGORY, CONSTANT.STRING, CONSTANT.CATEGORY, '25%');
		this.init();
	}

	init() {
		let table = this.getElement(this.TABLE);
		if(table) {
			getProducts(this.getFilter()).then(products => {
				table.removeRows();
				products.forEach((product, i) => {
					table.addRow(product, () => this.aonProduct(product));
				});
			});
		}
	}

	aonProduct(product) {
		// let aonProduct = new AonProduct();
		// aonProduct.setProduct(product);
		// this.getApplication().setContent(aonProduct);
	}

	getFilter() {
		return this.filter || {};
	}

	setFilter(filter) {
		this.filter;
		this.init();
	}
}
window.customElements.define('aon-product-list', AonProductList);
