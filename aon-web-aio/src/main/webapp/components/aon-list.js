import { AonElement } from './AonElement.js';
import { CONSTANT, TAG, EVENT } from '../environments/environments.js';
import { createList } from './CreateComponent.js';
import { addObjects, initializeObjects } from './ObjectCache.js';

export class AonList extends AonElement {

    get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

    columns;
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
        this.id = this.id || 'aonObjectList';
		this.TABLE = this.id + CONSTANT.TABLE.initCap();
        this.filter = this.filter || {
            page:1,
            perPage:30
        }
        this.more = true;
    }

    aonObject(object, i) {
      // REDEFINIR
    }

	build() {
		let table = createList(this.TABLE);
        table.selectable = 'true';
		this.appendChild(table);

		const btnSearch = this.getApplication().addSearchOption();
		let searchFn = (event) => this.search(event.detail);
        btnSearch.addEventListener(EVENT.SEARCH_NEW, searchFn);

        this.columns.forEach(column => table.addColumnObject(column));

		table.addEventListener(EVENT.MORE, this.moreFn);
		this.init();
	}

	search(detail) {
		if(detail.search) this.filter.value = detail.search;
		this.init();
	}

	init() {
		let table = this.getElement(this.TABLE);
		if(table) {
            initializeObjects();
            this.getObjects().then(objects => {
                addObjects(objects);
                if(objects.length == 0){   
                    this.empty();
                }
                table.removeRows();

                objects.forEach((object, i) => {
                    table.addRow(object, () => this.aonObject(object, i));
                });

            });
		}
	}

	loadMore() {
		this.more = false;
		let table = this.getTable();
        if(table && this.filter.page) {
            this.filter.page = this.filter.page + 1;
            this.getObjects(this.getFilter()).then(objects => {
                addObjects(objects);
                this.more = objects.length > 0;
                objects.forEach((object, i) => {
                    table.addRow(object, () => this.aonObject(object, i));
                });
            });
        }
	}

    getTable() {
        return this.getElement(this.TABLE);
    }

    getFilter() {
        return this.filter;
    }

    setFilter(filter) {
        this.filter = filter;
    }

    getObjects() {
        // REDEFINIR!!
        return new Promise((resolve, reject) => resolve([]));
    }
}

if(!window.customElements.get(TAG.AON_LIST)) {
    window.customElements.define(TAG.AON_LIST, AonList);
}