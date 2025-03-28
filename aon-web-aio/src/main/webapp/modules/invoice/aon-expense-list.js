import { AonElement } from '../../components/AonElement.js';
import { CONSTANT, EVENT, MSG, TAG } from '../../environments/environments.js';
import { createList } from '../../components/CreateComponent.js';
import { AonExampleObject } from '../example/aon-example-object.js';
import { getExpenses, getIncomes } from '../../services/accountingService.js';
import { AonIncome } from './aon-income.js';
import * as LS from '../../services/localStorageService.js';
import { Income } from './Income.js';
import { AonExpense } from './aon-expense.js';
import { Expense } from './Expense.js';

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

    expenseObject(incomingExpense) {
        let expense = new AonExpense( new Expense(incomingExpense) );
        this.getApplication().setContent(expense);
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

        table.addColumn(MSG.DATE, 'date', 'date', '150px');
        table.addColumn(MSG.REFERENCE, 'string', 'referenceCode', '150px');
        table.addColumn("Gasto", 'string', 'expenseDescription', '300px');
        table.addColumn(MSG.CONCEPT, 'string', 'concept', '300px');
        table.addColumn(MSG.PAYMETHOD, 'string', 'paymethodDescription', '300px');
        table.addColumn(MSG.AMOUNT, 'double', 'formattedAmount', '200px');
    
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
            getExpenses( this.filter ).then(expenses => {
                if(expenses.length == 0){   
                    console.log("No hay datos!!!")
                }
                table.removeRows();
                expenses.forEach((expense) => {
                    if(expense.expAccount)
                        expense.expenseDescription = expense.expAccount.description;
                    expense.paymethodDescription = expense.cashAccount.description;
                    expense.formattedAmount = this.formatAmount(expense.amount);
                    table.addRow(expense, () => this.expenseObject(expense));
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
                    table.addRow(example, () => this.expenseObject(example, i));
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

    formatAmount(amount) {
        const num = parseFloat(amount);  
        if (isNaN(num)) return amount; 
        return num.toLocaleString('es-ES', { useGrouping: true, minimumFractionDigits: 0, maximumFractionDigits: 2 }) + ' €';
    }
    
    add(){
        let inc = new AonExpense( new Expense() );
        this.getApplication().setContent(inc);
    }
}

if(!window.customElements.get(TAG.AON_EXPENSE_LIST)) {
    window.customElements.define(TAG.AON_EXPENSE_LIST, AonExpenseList);
}