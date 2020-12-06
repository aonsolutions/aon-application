import {AonElement} from '../../components/AonElement.js';
import {getDocuments} from '../../services/service.js';

import '../../components/aon-table.js';

import * as CONSTANT from "../../environments/constants.js";
import * as MSG from "../../environments/msg.js";

export class AonDocumentalList extends AonElement {

	more;

	TABLE;

	static get observedAttributes() {
		return [];
	}

	get filter() {
    return this.getAttribute(CONSTANT.FILTER);
  }

  set filter(filter) {
    this.setAttribute(CONSTANT.FILTER, filter);
  }

	attributeChangedCallback(name, oldValue, newValue) {

	}

	constructor () {
		super();
		this.more = true;
		this.TABLE = 'aonDocumentalTable';
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-table id='${this.TABLE}' selectable='true'></aon-table>
			`;
		this.build();
 	}

 	build() {
		let aonDocumentalTable = this.getElement(this.TABLE);
		aonDocumentalTable.addColumn(MSG.AON_MSG_DATE, 'date', 'date', '20%');
		aonDocumentalTable.addColumn(MSG.AON_MSG_NAME, 'string', 'title', '60%');
		aonDocumentalTable.addColumn(MSG.AON_MSG_SIZE, 'string', 'size', '15%');

		// INFO
		// aonInvoiceTable.addColumn('', '', '');
		this.init();
		aonDocumentalTable.addEventListener('more', () => {
			if(this.more)
				this.loadMore()
		});
	}

	loadMore() {
		let aonDocumentalTable = this.getElement(this.TABLE);
		let filter = this.getFilter();

		if(aonDocumentalTable && filter.page) {
			filter.page = filter.page + 1;
			this.setFilter(filter);
			getDocuments(filter).then(documents => {
				if(documents.length == 0)
					this.more = false;
				documents.forEach((doc, i) => {
					aonDocumentalTable.addRow(doc, () => this.aonDocument(doc, i));
				});
			});
		}
	}

	init() {
		this.more = true;
		let aonDocumentalTable = this.getElement(this.TABLE);
		if(aonDocumentalTable) {
			getDocuments(this.getFilter()).then(documents => {
				aonDocumentalTable.removeRows();
				documents.forEach((doc, i) => {
					aonDocumentalTable.addRow(doc, () => this.aonDocument(doc, i));
				});
			});
		}
	}

	aonDocument(doc, i) {
		let ad = document.querySelector('aon-documental');
		ad.aonDocument(doc);
	}

	getFilter() {
		return this.hasAttribute('filter')
 			? JSON.parse(this.getAttribute('filter'))
			: {status: 'inbox'};
	}

	setFilter(filter) {
		return this.setAttribute('filter', JSON.stringify(filter));
	}
}
window.customElements.define('aon-documental-list', AonDocumentalList);
