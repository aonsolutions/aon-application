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
        if (table) {
            initializeObjects();
            this.getObjects().then(objects => {
                if (Array.isArray(objects) && objects.length === 0) {
                    this.empty();
                }else{
                    table.removeRows();
                    objects.forEach((object, i) => {
                        table.addRow(object, () => this.aonObject(object, i));
                    });
                }
            }).catch(error => {
                console.error("Error al obtener los objetos en init():", error);
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

    empty() {
        let tableWrapper = this.getTable();
        if (!tableWrapper) return;
    
        let table = tableWrapper.querySelector("table");
        if (!table) return;
    
        let tbody = table.querySelector("tbody");
        if (!tbody) {
            tbody = document.createElement("tbody");
            table.appendChild(tbody);
        } else {
            tbody.innerHTML = ""; 
        }
        let columnsCount = (this.columns && this.columns.length) ? this.columns.length : 1;
        let tr = document.createElement("tr");
        let td = document.createElement("td");
    
        td.colSpan = columnsCount;
        td.textContent = "No hay datos disponibles";

        tr.style.border = "0px";
        tr.classList.add("no-hover");
        
        td.style.textAlign = "center";
        td.style.padding = "10px";
        td.style.border = "0px";
        
        tr.appendChild(td);
        tbody.appendChild(tr);
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