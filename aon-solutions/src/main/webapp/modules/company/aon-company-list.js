import {AonElement} from '../../components/AonElement.js';
import {getDomainCompanies} from '../../services/service.js';

import '../../components/aon-table.js';

import { AonCompany } from "../company/aon-company.js";

import * as AON_TAG from "../../environments/aonTag.js";
import * as CONSTANT from "../../environments/constants.js";
import * as CSS from "../../environments/css.js";
import * as EVENT from "../../environments/aonEvent.js";
import * as MSG from "../../environments/msg.js";

export class AonCompanyList extends AonElement {

	AON_COMPANY_TABLE;
	static get observedAttributes() {
		return [CONSTANT.FILTER];
	}

	get filter() {
    return this.getAttribute(CONSTANT.FILTER);
  }

  set filter(filter) {
    this.setAttribute(CONSTANT.FILTER, filter);
  }

	attributeChangedCallback(name, oldValue, newValue) {
		this.initialize();
		if(CONSTANT.FILTER === name) {
			this.init();
		}
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-table id='${this.AON_COMPANY_TABLE}'></aon-table>
			`;
		this.build();
 	}

	initialize() {
		this.AON_COMPANY_TABLE = 'aonCompanyTable';
	}

	build() {
	 let aonTable = this.getElement(this.AON_COMPANY_TABLE);
	 aonTable.addColumn(MSG.AON_MSG_BUSINESS_NAME, 'string', 'name', '80%');
	 aonTable.addColumn('CIF', 'string', 'document', '20%');

	 // INFO
	 // aonInvoiceTable.addColumn('', '', '');

	 this.init();
 }

	init() {
		let aonTable = this.getElement(this.AON_COMPANY_TABLE);
		if(aonTable) {
			getDomainCompanies({parent:true}).then(companies => {
				aonTable.removeRows();
				companies.forEach((company, i) => {
					aonTable.addRow(company, () => this.buildCompany(company));
				});
			});
		}
	}

	buildCompany(company) {
		let aonCompany = new AonCompany();
		aonCompany.id = this.getApplication().id + 'Company';
		aonCompany.company = company;
		this.getApplication().setContent(aonCompany);
	}

	getFilter() {
		return this.filter || {};
	}
}

if(!window.customElements.get(AON_TAG.AON_COMPANY_LIST)) {
	window.customElements.define(AON_TAG.AON_COMPANY_LIST, AonCompanyList);
}
