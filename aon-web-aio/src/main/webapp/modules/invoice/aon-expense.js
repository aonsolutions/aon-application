import {AonElement} from '../../components/AonElement.js';
import { ToolbarType} from '../../models/enums.js';
import {AonToolbar} from "../../components/aon-toolbar.js";
import { CONSTANT, MSG, TAG } from '../../environments/environments.js'; 
import * as ACTION from '../actions.js';
import { createCard, createDate, createInput, createSelect, createTextarea, createNumber } from '../../components/CreateComponent.js';
import { AonNewSelect } from '../../components/aon-new-select.js';
import { AonRegistrySuggestion } from '../registry/aon-registry-suggestion.js';

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
    DIV;
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
        this.DIV = this.id + 'Div';
        this.exampleObject = this.exampleObject || {};
    }

    build() {
        let toolbar = new AonToolbar();
        toolbar.id = this.EXPENSE_TOOLBAR;
        toolbar.type = ToolbarType.SECONDARY;
        toolbar.title = MSG.EXPENSES; 
        this.appendChild(toolbar);

        toolbar.addButton2(ACTION.ADD, () => this.add());
        toolbar.addButton2(ACTION.BACK, () => this.back());

        let div = this.createDiv();
        div.id = this.DIV;
        div.style.width = "50%";
        this.appendChild(div);
        this.buildCard(div);
    }

    buildCard(parent) {
        let card = createCard(this.EXPENSE_CARD, MSG.OTHER_EXPENSES, parent);

        let div = this.createDiv();
        card.setContent(div);

        let activity = createSelect(this.EXPENSE_ACTIVITY, MSG.ACTIVITY, div);
    
        let date = createDate(this.EXPENSE_DATE, MSG.DATE, div);
        date.value = this.exampleObject.date;

        let customer = new AonRegistrySuggestion();
        div.appendChild(customer);

        let expense = createInput(this.EXPENSE_EXPENSE, MSG.EXPENSE, div);

        let descripition = createInput(this.EXPENSE_DESCRIPTION, MSG.DESCRIPTION, div);

        let subDiv = this.createElement(TAG.DIV);
        subDiv.style.display = "flex";
        div.appendChild(subDiv);

        let reference = createInput(this.EXPENSE_REFERENCE, MSG.REFERENCE, subDiv)

        let amount = createNumber(this.EXPENSE_AMOUNT, MSG.AMOUNT, subDiv);
        amount.style.marginLeft = "10px";

        let paymentMethod = createSelect (this.EXPENSE_PAYMENT,MSG.PAYMETHOD, div);

        let comments = createTextarea(this.EXPENSE_COMMENTS, MSG.COMMENTS, div);
        this.getElement(this.EXPENSE_COMMENTS + "Textarea").style.height = "100px";


    }

    setExampleObject(exampleObject) {
        this.exampleObject = exampleObject;
    }

    back() {
        alert("BACK EXAMPLE");
    }

    add() {
        alert("ADD EXAMPLE");
    }


}
if(!window.customElements.get(TAG.AON_EXPENSE)){
    window.customElements.define(TAG.AON_EXPENSE, AonExpense);
}
