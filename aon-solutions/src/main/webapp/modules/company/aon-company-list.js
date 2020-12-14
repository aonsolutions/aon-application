import {AonElement} from '../../components/AonElement.js';
import {getCompanies} from '../../services/service.js';

import '../../components/aon-table.js';

export class AonCompanyList extends AonElement {

	AON_COMPANY_TABLE;
	static get observedAttributes() {
		return ['filter'];
	}

	get filter() {
    return this.getAttribute('filter');
  }

  set filter(filter) {
    this.setAttribute('filter', filter);
  }

	attributeChangedCallback(name, oldValue, newValue) {
		if('filter' === name) {
			this.init();
		}
	}

	constructor () {
		super();
		this.AON_COMPANY_TABLE = 'aonCompanyTable';
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-table id='${this.AON_COMPANY_TABLE}'></aon-table>
			`;
		this.build();
 	}

	build() {
	 let aonTable = this.getElement(this.AON_COMPANY_TABLE);
	 aonTable.addColumn('Razón Social', 'string', 'name', '80%');
	 aonTable.addColumn('CIF', 'string', 'document', '20%');

	 // INFO
	 // aonInvoiceTable.addColumn('', '', '');

	 this.init();
 }

	init() {
		let aonTable = this.getElement(this.AON_COMPANY_TABLE);
		if(aonTable) {
			getCompanies(this.getFilter()).then(companies => {
				aonTable.removeRows();
				companies.forEach((company, i) => {
					aonTable.addRow(company, () =>
						this.dispatchEvent(new CustomEvent('select', {company})));
				});
			});
		}
	}

	getFilter() {
		return this.hasAttribute('filter')
			? JSON.parse(this.filter)
			: {};
	}
}
window.customElements.define('aon-company-list', AonCompanyList);
