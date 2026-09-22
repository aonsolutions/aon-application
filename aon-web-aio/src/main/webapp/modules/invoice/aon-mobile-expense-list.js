import { AonMobileList } from '../../components/aon-mobile-list.js';
import { CONSTANT, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js';
import { getExpenses } from '../../services/accountingService.js';
import { formatNumber } from '../../services/utils.js';
import * as ACTION from '../actions.js';
import { AonExpense } from './aon-expense.js';
import { Expense } from './Expense.js';

export class AonMobileExpenseList extends AonMobileList {

    PER_PAGE = 30;

    more;

    constructor () {
        super();
    }

    connectedCallback () {
        this.initialize();
        this.init();
        this.addEventListener(EVENT.MORE, this.moreFn);
        this.buildOptions();
    }

    moreFn = () => {
        if(this.more)
            this.loadMore();
    };

    disconnectedCallback() {
        this.removeEventListener(EVENT.MORE, this.moreFn);
    }

    initialize() {
        this.id = this.id || 'aonMobileExpenseList';
        super.initialize();
        this.more = true;
    }

    buildOptions() {
        let application = this.getApplication();
        if(!application) return;
        application.removeFloatOption();
        application.addFloatOption(ACTION.ADD, () => this.add());
    }

    loadMore() {
        let filter = this.getFilter();
        if(!filter.page) return;
        this.more = false;
        filter.page = filter.page + 1;
        super.setFilter(filter);
        getExpenses(filter).then(expenses => {
            if(!Array.isArray(expenses)) expenses = [];
            if(expenses.length > 0)
                this.more = true;
            expenses.forEach((expense, i) => this.addRow(expense, i));
        });
    }

    init() {
        let filter = this.getFilter();
        filter.page = 1;
        filter.perPage = filter.perPage || this.PER_PAGE;
        super.setFilter(filter);
        this.more = true;

        this.build();
        getExpenses(filter).then(expenses => {
            if(!Array.isArray(expenses)) expenses = [];
            if(expenses.length == 0){
                this.empty();
            }
            expenses.forEach((expense, i) => this.addRow(expense, i));
        }).catch(e => {
            console.error("Error en getExpenses():", e);
            this.empty();
        });
    }

    addRow(expense, i) {
        let liValue = {
            icon: MATERIAL_ICONS.PAYMENT,
            title: this.getTitle(expense),
            subtitle: this.getSubtitle(expense),
            subtitleTwo: this.getSubtitleTwo(expense)
        }
        this.addLi(liValue, i, () => this.aonExpense(expense));
    }

    getTitle(expense) {
        if(expense.expAccount && expense.expAccount.description)
            return expense.expAccount.description;
        if(expense.creditor && expense.creditor.name)
            return expense.creditor.name;
        return expense.concept || MSG.EXPENSE;
    }

    getSubtitle(expense) {
        let values = [this.getDateStr(expense.date), formatNumber(expense.amount, 2, 2, "EUR")];
        return values.filter(value => value).join(' - ');
    }

    getSubtitleTwo(expense) {
        let values = [expense.referenceCode, this.getPaymethod(expense)];
        return values.filter(value => value).join(' - ');
    }

    getPaymethod(expense) {
        if(expense.bank && expense.bank.alias)
            return expense.bank.alias;
        return expense.cashAccount && expense.cashAccount.description
            ? expense.cashAccount.description : '';
    }

    getDateStr(date) {
        if(!date) return '';
        let d = new Date(date);
        if(isNaN(d.getTime())) return '';
        return d.getDate() + '/' + (d.getMonth() + 1) + '/' + d.getFullYear();
    }

    aonExpense(expense) {
        let model = new Expense(expense);
        model.id = expense.id;
        this.getApplication().setContent(new AonExpense(model));
    }

    add() {
        this.getApplication().setContent(new AonExpense(new Expense()));
    }

    get filter() {
        return this.getFilter();
    }

    set filter(filter) {
        super.setFilter(filter);
    }

    getFilter() {
        if(!this.hasAttribute(CONSTANT.FILTER)) return {};
        try {
            return JSON.parse(this.getAttribute(CONSTANT.FILTER));
        } catch(e) {
            return {};
        }
    }

    setFilter(filter) {
        super.setFilter(filter);
        this.init();
    }
}

if(!window.customElements.get(TAG.AON_MOBILE_EXPENSE_LIST)){
    window.customElements.define(TAG.AON_MOBILE_EXPENSE_LIST, AonMobileExpenseList);
}
