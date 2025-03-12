import { AonElement } from '../../components/AonElement.js';
import { CONSTANT, EVENT, MSG, TAG } from '../../environments/environments.js';
import { createList } from '../../components/CreateComponent.js';
import { AonExampleObject } from '../example/aon-example-object.js';
import { getIncomes } from '../../services/accountingService.js';
import { AonIncome } from './aon-income.js';
import { Income } from './Income.js';

export class AonIncomeList extends AonElement {

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

    incomeObject(incomingIncome) {
        console.log(incomingIncome);
        let income = new AonIncome();
        income.setIncome(new Income(incomingIncome));
        this.getApplication().setContent(income);
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
        table.addColumn(MSG.CONCEPT, 'string', 'concept', '825px');
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
            getIncomes().then(incomes => {
                if(incomes.length == 0){   
                    console.log("No hay datos!!!")
                }
                table.removeRows();
                incomes.forEach((income) => {
                    table.addRow(income, () => this.incomeObject(income));
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
                    table.addRow(example, () => this.incomeObject(example, i));
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
        this.getApplication().setContent(new AonIncome());
    }
}

if(!window.customElements.get(TAG.AON_INCOME_LIST)) {
    window.customElements.define(TAG.AON_INCOME_LIST, AonIncomeList);
}