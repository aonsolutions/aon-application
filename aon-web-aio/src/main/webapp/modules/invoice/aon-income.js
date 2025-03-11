import {AonElement} from '../../components/AonElement.js';
import { ToolbarType} from '../../models/enums.js';
import {AonToolbar} from "../../components/aon-toolbar.js";
import { CONSTANT, MSG, TAG } from '../../environments/environments.js'; 
import * as ACTION from '../actions.js';
import { createCard, createDate, createInput, createSelect, createTextarea, createNumber } from '../../components/CreateComponent.js';
import { AonCustomerSuggestion } from '../registry/customer/aon-customer-suggestion.js';
import { getCompanyActivities } from '../../services/companyService.js';
import { getAccounts } from '../../services/accountingService.js';
import { getPaymethods } from '../../services/invoiceService.js';
import { AonIncomeList } from './aon-income-list.js';

export class AonIncome extends AonElement {

    INCOME_TOOLBAR;
    INCOME_CARD;
    INCOME_DATE;
    INCOME_ACTIVITY;
    INCOME_INCOME;
    INCOME_DESCRIPTION;
    INCOME_REFERENCE;
    INCOME_AMOUNT;
    INCOME_PAYMENT;
    INCOME_COMMENTS;
    DIV_GENERAL;
    exampleObject;

    get id() {
        return this.getAttribute(CONSTANT.ID);
    }

    set id(id) {
        this.setAttribute(CONSTANT.ID, id);
    }

    constructor () {
        super();
    }

    connectedCallback () {
        this.initialize();
        this.build();
    }

    initialize() {
        this.id = this.id || 'aonIncome';
        this.INCOME_TOOLBAR = this.id + "Toolbar";
        this.INCOME_CARD = this.id + "Card";
        this.INCOME_DATE = this.id + "Date";
        this.INCOME_ACTIVITY = this.id + "Activity"
        this.INCOME_INCOME = this.id + "Income";
        this.INCOME_DESCRIPTION = this.id + "Description";
        this.INCOME_REFERENCE = this.id + "Reference";
        this.INCOME_AMOUNT = this.id + "Amount";
        this.INCOME_PAYMENT = this.id + "PaymentMethod";
        this.INCOME_COMMENTS = this.id + "Comments";
        this.DIV_GENERAL = this.id + 'Div';
        this.exampleObject = this.exampleObject || {};
    }

    build() {
        let toolbar = new AonToolbar();
        toolbar.id = this.INCOME_TOOLBAR;
        toolbar.type = ToolbarType.SECONDARY;
        toolbar.title = MSG.INCOMES; 
        this.appendChild(toolbar);

        toolbar.addButton2(ACTION.BACK, () => this.back());

        let div = this.createDiv();
        div.id = this.DIV_GENERAL; 
        if(this.isMobile())
            div.style.width = "100%";
        else
            div.style.width = "50%";
        
        this.appendChild(div);
        this.buildCard(div);
    }

    buildCard(parent) {
        let card = createCard(this.INCOME_CARD, MSG.OTHER_INCOMES, parent);

        let div = this.createDiv();
        card.setContent(div);

        let activity = createSelect(this.INCOME_ACTIVITY, MSG.ACTIVITY, div);
        activity.setAlias("id", "description");
        getCompanyActivities({}).then(activities => {
            if (activities.length > 0) {
                activity.setOptions(activities);
                const principalActivity = activities.find(act => act.principal === true);
                if (principalActivity) activity.value = principalActivity.id;
            }
        });
    
        let date = createDate(this.INCOME_DATE, MSG.DATE, div);
        date.value = this.exampleObject.date;

        let customer = new AonCustomerSuggestion();
        div.appendChild(customer);

        let income = createSelect(this.INCOME_INCOME, "Ingreso");
        income.autocomplete = true;
        income.setAlias("id", "description");
        div.appendChild(income);
        getAccounts({code: "7"}).then(accounts => {
            income.setOptions(accounts);
        })

        let descripition = createInput(this.INCOME_DESCRIPTION, MSG.DESCRIPTION, div);

        let subDiv = this.createElement(TAG.DIV);
        subDiv.style.display = "flex";
        div.appendChild(subDiv);

        let reference = createInput(this.INCOME_REFERENCE, MSG.REFERENCE, subDiv)
        reference.style.width = "50%";

        let amount = createNumber(this.INCOME_AMOUNT, MSG.AMOUNT, subDiv);
        amount.style.marginLeft = "10px";
        amount.style.width = "50%";

        let paymentMethod = createSelect (this.INCOME_PAYMENT, MSG.PAYMETHOD, div);
        getPaymethods({}).then(paymethods => {
            let pms = paymethods.map(pm => {return {name: pm.name, value: pm.id};});
            paymentMethod.options = JSON.stringify(pms);
            paymentMethod.value = finance.paymethod;
        });

        let comments = createTextarea(this.INCOME_COMMENTS, MSG.COMMENTS, div);
        this.getElement(this.INCOME_COMMENTS + "Textarea").style.height = "100px";
    }

    setExampleObject(exampleObject) {
        this.exampleObject = exampleObject;
    }

    back() {
        this.getApplication().setContent(new AonIncomeList());
    }

}
if(!window.customElements.get(TAG.AON_INCOME)){
    window.customElements.define(TAG.AON_INCOME, AonIncome);
}
