import { AonElement } from '../../components/AonElement.js';
import { CONSTANT, EVENT, MSG, TAG } from '../../environments/environments.js';
import { getAccountEntries } from '../../services/accountingService.js';
import { createList } from '../../components/CreateComponent.js';

export class AonAccountEntryList extends AonElement {

    get id() {
        return this.getAttribute(CONSTANT.ID);
    }

    set id(id) {
        this.setAttribute(CONSTANT.ID, id);
    }

    TABLE;
    more;
    filter;

    constructor () {
        super();
    }

    connectedCallback () {
        this.initialize();
        this.build();
    }

    moreFn = () => {
        if(this.more)
            this.loadMore()
    };

    disconnectedCallback() {
        let table = this.getElement(this.TABLE);
        if(table) table.removeEventListener('more', this.moreFn);
    }

    initialize() {
        this.id = this.id || 'aonAccountEntryList';
        this.TABLE = this.id + CONSTANT.TABLE.initCap();
        this.filter = this.filter || {
            page: 1,
            perPage: 30
        };
        this.more = true;
    }

    

    aonAccountEntry(accountEntry, i) {
        this.getApplication().development();
    }
    

    setFilter(filter) {
        this.filter = filter;
    }

    build() {
        let table = createList(this.TABLE);
        table.selectable = 'true';
        this.appendChild(table);

        const btnSearch = this.getApplication().addSearchOption();
        let searchFn = (event) => this.search(event.detail);
        btnSearch.addEventListener(EVENT.SEARCH_NEW, searchFn);

        table.addColumn(MSG.DATE, 'date', 'date', '10%');
        table.addColumn(MSG.DESCRIPTION, 'string', 'comments', 'auto');
        aonInvoiceTable.addColumn(MSG.AMOUNT, 'number', 'totalParse', '100px');

        table.addEventListener(EVENT.MORE, this.moreFn);
        this.init();
    }

    search(detail) {
        if(detail.search) this.filter.value = detail.search;
        this.init();
    }

    init() {
        let table = document.getElementById(this.TABLE);
        if(table) {
            getAccountEntries(this.filter).then(accountEntries => {
                if(accountEntries.length == 0){   
                    this.empty();
                }
                table.removeRows();

                accountEntries.forEach((accountEntry, i) => {
                    table.addRow(accountEntry, () => this.aonAccountEntry(accountEntry, i));
                });
            });    
        }
    }

    loadMore() {
        this.more = false;
        let table = this.getElement(this.TABLE);
        if(table && this.filter.page) {
            this.filter.page = this.filter.page + 1;
            getAccountEntries(this.filter).then(accountEntries => {
                if(accountEntries.length == 0)
                    this.more = false;
                else this.more = true;
                accountEntries.forEach((accountEntry, i) => {
                    table.addRow(accountEntry, () => this.aonAccountEntry(accountEntry, i));
                });
            });
        }
    }
}

if(!window.customElements.get(TAG.AON_ACCOUNT_ENTRY_LIST)) {
    window.customElements.define(TAG.AON_ACCOUNT_ENTRY_LIST, AonAccountEntryList);
}