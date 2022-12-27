import { AonElement } from '../../components/AonElement.js';
import { CONSTANT, TAG } from '../../environments/environments.js';

export class AonSalesList extends AonElement {
    
    TABLE;

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
		this.id = this.id || CONSTANT.AON_SALES_LIST;
		this.TABLE = this.id + CONSTANT.TABLE.initCap();
	}

    build() {
		let table = new AonTable();
		table.id = this.TABLE;
		this.appendChild(table);
		table.addColumn(MSG.CODE, CONSTANT.STRING, CONSTANT.CODE, '25%');
		table.addColumn(MSG.NAME, CONSTANT.STRING, CONSTANT.NAME, '50%');
		table.addColumn(MSG.CATEGORY, CONSTANT.STRING, CONSTANT.CATEGORY_NAME, '25%');
		this.init();
	}

}

if(!window.customElements.get(TAG.AON_SALES_LIST)) {
    window.customElements.define(TAG.AON_SALES_LIST, AonSalesList);
}