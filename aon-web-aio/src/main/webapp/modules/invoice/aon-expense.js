import {AonElement} from '../../components/AonElement.js';
import { RegistryType, ToolbarType} from '../../models/enums.js';
import {AonToolbar} from "../../components/aon-toolbar.js";
import { CONSTANT, MSG, TAG } from '../../environments/environments.js'; 
import * as ACTION from '../actions.js';
import { createCard, createDate, createInput, createSelect, createTextarea, createNumber } from '../../components/CreateComponent.js';
import { AonRegistrySuggestion } from '../registry/aon-registry-suggestion.js';
import { getCompanyActivities } from '../../services/companyService.js';
import { getAccounts } from '../../services/accountingService.js';
import { getPaymethods } from '../../services/invoiceService.js';
import { AonExpenseList } from './aon-expense-list.js';

export class AonExpense extends AonElement {

    EXPENSE_TOOLBAR;
    EXPENSE_CARD;
    EXPENSE_DATE;
    EXPENSE_ACTIVITY;
    EXPENSE_EXPENSE;
    EXPENSE_DESCRIPTION;
    EXPENSE_REFERENCE;
    EXPENSE_AMOUNT;
    EXPENSE_PAYMENT;
    EXPENSE_COMMENTS;
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
        this.id = this.id || 'aonExpense';
        this.EXPENSE_TOOLBAR = this.id + "Toolbar";
        this.EXPENSE_CARD = this.id + "Card";
        this.EXPENSE_DATE = this.id + "Date";
        this.EXPENSE_ACTIVITY = this.id + "Activity";
        this.EXPENSE_EXPENSE = this.id + "Expense";
        this.EXPENSE_DESCRIPTION = this.id + "Description";
        this.EXPENSE_REFERENCE = this.id + "Reference";
        this.EXPENSE_AMOUNT = this.id + "Amount";
        this.EXPENSE_PAYMENT = this.id + "PaymentMethod";
        this.EXPENSE_COMMENTS = this.id + "Comments";
        this.DIV_GENERAL = this.id + 'Div';
        this.exampleObject = this.exampleObject || {};
    }

    build() {
        let toolbar = new AonToolbar();
        toolbar.id = this.EXPENSE_TOOLBAR;
        toolbar.type = ToolbarType.SECONDARY;   
        toolbar.title = MSG.EXPENSES; 
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
        let card = createCard(this.EXPENSE_CARD, MSG.OTHER_EXPENSES, parent);

        let div = this.createDiv();
        card.setContent(div);

        let activity = createSelect(this.EXPENSE_ACTIVITY, MSG.ACTIVITY, div);
        activity.setAlias("id", "description");
        getCompanyActivities({}).then(activities => {
            if (activities.length > 0) {
                activity.setOptions(activities);
                const principalActivity = activities.find(act => act.principal === true);
                if (principalActivity) activity.value = principalActivity.id;
            }
        });
    
        let date = createDate(this.EXPENSE_DATE, MSG.DATE, div);
        date.value = this.exampleObject.date;

        let creditor = new AonRegistrySuggestion();
        creditor.types = [RegistryType.CREDITOR];
        div.appendChild(creditor);

        let expense = createSelect(this.EXPENSE_EXPENSE, MSG.EXPENSE);
        expense.setAlias("id", "description")
        expense.autocomplete = true;
        div.appendChild(expense);
        getAccounts({code: "6"}).then(accounts => {
            expense.setOptions(accounts);
        })

        let descripition = createInput(this.EXPENSE_DESCRIPTION, MSG.DESCRIPTION, div);

        let subDiv = this.createElement(TAG.DIV);
        subDiv.style.display = "flex";
        div.appendChild(subDiv);

        let reference = createInput(this.EXPENSE_REFERENCE, MSG.REFERENCE, subDiv)
        reference.style.width = "50%";

        let amount = createNumber(this.EXPENSE_AMOUNT, MSG.AMOUNT, subDiv);
        amount.style.marginLeft = "2px";
        amount.style.width = "50%";

        let paymentMethod = createSelect (this.EXPENSE_PAYMENT,MSG.PAYMETHOD, div);
        getPaymethods({}).then(paymethods => {
            let pms = paymethods.map(pm => {return {name: pm.name, value: pm.id};});
            paymentMethod.options = JSON.stringify(pms);
            paymentMethod.value = finance.paymethod;
        });

        let comments = createTextarea(this.EXPENSE_COMMENTS, MSG.COMMENTS, div);
        this.getElement(this.EXPENSE_COMMENTS + "Textarea").style.height = "100px";
    }

    setExampleObject(exampleObject) {
        this.exampleObject = exampleObject;
    }

    back() {
        this.getApplication().setContent(new AonExpenseList());
    }
    
}
if(!window.customElements.get(TAG.AON_EXPENSE)){
    window.customElements.define(TAG.AON_EXPENSE, AonExpense);
}
