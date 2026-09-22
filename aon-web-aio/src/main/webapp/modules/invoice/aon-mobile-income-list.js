import { AonMobileList } from '../../components/aon-mobile-list.js';
import { CONSTANT, EVENT, MATERIAL_ICONS, TAG } from '../../environments/environments.js';
import { getIncomes } from '../../services/accountingService.js';
import { formatNumber } from '../../services/utils.js';
import * as ACTION from '../actions.js';
import { AonIncome } from './aon-income.js';
import { Income } from './Income.js';

export class AonMobileIncomeList extends AonMobileList {

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
        this.id = this.id || 'aonMobileIncomeList';
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
        getIncomes(filter).then(incomes => {
            if(!Array.isArray(incomes)) incomes = [];
            if(incomes.length > 0)
                this.more = true;
            incomes.forEach((income, i) => this.addRow(income, i));
        });
    }

    init() {
        let filter = this.getFilter();
        filter.page = 1;
        filter.perPage = filter.perPage || this.PER_PAGE;
        super.setFilter(filter);
        this.more = true;

        this.build();
        getIncomes(filter).then(incomes => {
            if(!Array.isArray(incomes)) incomes = [];
            if(incomes.length == 0){
                this.empty();
            }
            incomes.forEach((income, i) => this.addRow(income, i));
        }).catch(e => {
            console.error("Error en getIncomes():", e);
            this.empty();
        });
    }

    addRow(income, i) {
        let liValue = {
            icon: MATERIAL_ICONS.ACCOUNT_BALANCE_WALLET,
            title: this.getTitle(income),
            subtitle: this.getSubtitle(income),
            subtitleTwo: this.getSubtitleTwo(income)
        }
        this.addLi(liValue, i, () => this.aonIncome(income));
    }

    getTitle(income) {
        if(income.expAccount && income.expAccount.description)
            return income.expAccount.description;
        if(income.customer && income.customer.name)
            return income.customer.name;
        return income.concept || 'Ingreso';
    }

    getSubtitle(income) {
        let values = [this.getDateStr(income.date), formatNumber(income.amount, 2, 2, "EUR")];
        return values.filter(value => value).join(' - ');
    }

    getSubtitleTwo(income) {
        let values = [income.referenceCode, this.getPaymethod(income)];
        return values.filter(value => value).join(' - ');
    }

    getPaymethod(income) {
        if(income.bank && income.bank.alias)
            return income.bank.alias;
        return income.cashAccount && income.cashAccount.description
            ? income.cashAccount.description : '';
    }

    getDateStr(date) {
        if(!date) return '';
        let d = new Date(date);
        if(isNaN(d.getTime())) return '';
        return d.getDate() + '/' + (d.getMonth() + 1) + '/' + d.getFullYear();
    }

    aonIncome(income) {
        let model = new Income(income);
        model.id = income.id;
        this.getApplication().setContent(new AonIncome(model));
    }

    add() {
        this.getApplication().setContent(new AonIncome(new Income()));
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

if(!window.customElements.get(TAG.AON_MOBILE_INCOME_LIST)){
    window.customElements.define(TAG.AON_MOBILE_INCOME_LIST, AonMobileIncomeList);
}
