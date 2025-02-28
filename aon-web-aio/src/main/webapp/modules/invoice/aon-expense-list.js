import { AonElement } from '../../components/AonElement.js';
import { CONSTANT, EVENT, MSG, TAG } from '../../environments/environments.js';
import { createList } from '../../components/CreateComponent.js';
import { AonExampleObject } from '../example/aon-example-object.js';
import { getExpenses, getIncomes } from '../../services/accountingService.js';
import { AonExpense } from './aon-expense.js';

export class AonExpenseList extends AonElement {

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
        this.id = this.id || 'aonExampleList';
        this.TABLE = this.id + CONSTANT.TABLE.initCap();
        this.filter = this.filter || {
            page: 1,
            perPage: 30
        };
        this.more = true;
    }

    aonExampleObject(object, i) {
        let data = {
            id: object.id,
        }
        this.getExample(data).then(r => {
            let aonExampleObject = new AonExampleObject();
            aonExampleObject.setExampleObject(r);
            this.getApplication().setContent(aonExampleObject);
        });
    }
    
    setFilter(filter) {
        this.filter = filter;
    }

    build() {
        let table = createList(this.TABLE);
        table.selectable = 'true';
        this.appendChild(table);

        const btnadd = this.getApplication().addOption("add","add", () => this.add());

        const btnSearch = this.getApplication().addSearchOption();
        let searchFn = (event) => this.search(event.detail);
        btnSearch.addEventListener(EVENT.SEARCH_NEW, searchFn);

        table.addColumn(MSG.DATE, 'date', 'date', '120px');
        table.addColumn(MSG.DESCRIPTION, 'string', 'description', '825px');
        table.addColumn(MSG.AMOUNT, 'double', 'amount', 'auto');
        

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
            getExpenses().then(expenses => {
                if(expenses.length == 0){   
                    this.empty();
                }
                table.removeRows();

                expenses.forEach((expense, i) => {
                    console.log(JSON.stringify(expense))
                    table.addRow(expense, () => this.aonExampleObject(expense, i));
                });

            });
        }
    }

    loadMore() {
        this.more = false;
        let table = this.getElement(this.TABLE);
        if(table && this.filter.page) {
            this.filter.page = this.filter.page + 1;
            this.getExamples(this.filter).then(examples => {
                if(examples.length == 0)
                    this.more = false;
                else this.more = true;
                examples.forEach((example, i) => {
                    table.addRow(example, () => this.aonExampleObject(example, i));
                });
            });
        }
    }

    // SERVICE

    getExamples(filter) {
        return new Promise((resolve, reject) => {
              resolve([
                {id:1, date: '01-01-2025', example: 'Example 1'},
                {id:2, date: '01-01-2025', example: 'Example 2'},
                {id:3, date: '01-01-2025', example: 'Example 3'},
                {id:4, date: '01-01-2025', example: 'Example 4'}
            ]);
        }); 
    }

    getExample(filter) {
        return new Promise((resolve, reject) => {
            resolve({id: filter.id, date: '01-01-2025', example: `Example ${filter.id}`});
        });
    }
    
    add(){
        this.getApplication().setContent(new AonExpense());
    }
}

if(!window.customElements.get(TAG.AON_EXPENSE_LIST)) {
    window.customElements.define(TAG.AON_EXPENSE_LIST, AonExpenseList);
}