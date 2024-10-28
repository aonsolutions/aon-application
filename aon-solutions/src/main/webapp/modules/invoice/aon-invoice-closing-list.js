import { AonElement } from '../../components/AonElement.js';
import { CONSTANT, MSG, TAG, EVENT } from '../../environments/environments.js';
import { addElements, setElements } from './ElementCache.js';
import { createList } from '../../../components/CreateComponent.js';
import { getInvoiceClosing } from '../../services/invoiceService.js';

export class AonInvoiceClosingList extends AonElement {

	more;
	filter;
	TABLE;

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	constructor () {
		super();
		this.more = true;
	}

	connectedCallback () {
		this.initialize();
 	}

	initialize() {
		this.id = this.id || 'aonInvoiceClosingList';
		this.TABLE = this.id + 'Table';
		this.filter = this.filter || {
			page: 1,
			perPage: 50
		};
	}

 	build() {
		let aonTable = createList(this.TABLE);
		aonTable.selectable = 'true';
		this.appendChild(aonTable);
		aonTable.addColumn(MSG.ID, 'string', 'id', '100px');
		aonTable.addColumn(MSG.DESCRIPTION, 'string', 'description', 'auto');
		aonTable.addColumn(MSG.START_DATE, 'string', 'startDate', '100px');
		aonTable.addColumn(MSG.END_DATE, 'string', 'endDate', '100px');
		aonTable.addColumn("Hash", 'string', 'hash', '200px');

		this.init();
		aonTable.addEventListener('more', () => {
			if(this.more)
				this.loadMore();
		});

		this.buildSearch();
	}

	buildSearch(){
		const btnSearch = this.getApplication().addSearchOption();
		let searchFn = (event) => this.search(event.detail);
		btnSearch.addEventListener(EVENT.SEARCH_NEW, searchFn);
    }

	search(detail) {
		this.filter.value = detail.search;
		this.filter.page = 1;
		this.filter.perPage = 50;
		this.init();
	}

	loadMore() {
		let aonTable = document.getElementById(this.TABLE);
		if(aonTable && this.filter.page) {
			this.filter.page = this.filter.page + 1;
			getInvoiceClosing(this.filter).then(closing => {
				if(closing.length == 0)
					this.more = false;
				addElements(closing);
				closing.forEach((cl, i) => {
					aonTable.addRow(cl, () => {}, (e) => {});
				});
			});
		}
	}

	init() {
		this.more = true;
		let aonTable = this.getElement(this.TABLE);
		if(aonTable) {
			getInvoiceClosing(this.getFilter()).then(closing => {
				setElements(closing);
				
				aonTable.removeRows();
				aonTable.selected = [];
				this.removeElaborationActions();
				closing.forEach((cl, i) => {
					aonTable.addRow(cl, () => {}, (e) => {});
				});
			});
		}
	}

	getFilter() {
		return this.filter || {
			page: 1,
			perPage: 50
		};
	}

	setFilter(filter) {
		this.filter = filter;
	}
}
if(!window.customElements.get(TAG.AON_INVOICE_CLOSING_LIST)) {
	window.customElements.define(TAG.AON_INVOICE_CLOSING_LIST, AonInvoiceClosingList);
}