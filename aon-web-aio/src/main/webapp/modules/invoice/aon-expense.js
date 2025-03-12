import {AonElement} from '../../components/AonElement.js';
import { RegistryType, ToolbarType} from '../../models/enums.js';
import {AonToolbar} from "../../components/aon-toolbar.js";
import { CONSTANT, EVENT, MSG, TAG } from '../../environments/environments.js'; 
import * as ACTION from '../actions.js';
import { createCard, createDate, createInput, createSelect, createTextarea, createNumber } from '../../components/CreateComponent.js';
import { AonRegistrySuggestion } from '../registry/aon-registry-suggestion.js';
import { getCompanyActivities, getCompanyBanks } from '../../services/companyService.js';
import { getAccounts, setExpense } from '../../services/accountingService.js';
import { AonExpenseList } from './aon-expense-list.js';
import { Expense } from './Expense.js';

export class AonExpense extends AonElement {

    EXPENSE_TOOLBAR;
    EXPENSE_CARD;
    EXPENSE_DATE;
    EXPENSE_ACTIVITY;
    EXPENSE_EXPACCOUNT;
    EXPENSE_DESCRIPTION;
    EXPENSE_REFERENCE;
    EXPENSE_AMOUNT;
    EXPENSE_PAYMENT;
    EXPENSE_CREDITOR;
    EXPENSE_COMMENTS;
    DIV_GENERAL;
    exampleObject;

    expense

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
        this.expense = this.expense || new Expense();
        this.id = this.id || 'aonExpense';
        this.EXPENSE_TOOLBAR = this.id + "Toolbar";
        this.EXPENSE_CARD = this.id + "Card";
        this.EXPENSE_DATE = this.id + "Date";
        this.EXPENSE_ACTIVITY = this.id + "Activity";
        this.EXPENSE_EXPACCOUNT = this.id + "ExpAccount";
        this.EXPENSE_DESCRIPTION = this.id + "Description";
        this.EXPENSE_REFERENCE = this.id + "Reference";
        this.EXPENSE_AMOUNT = this.id + "Amount";
        this.EXPENSE_PAYMENT = this.id + "PaymentMethod";
        this.EXPENSE_CREDITOR = this.id + "Creditor";
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

        toolbar.addButton2(ACTION.SAVE, () => this.save());
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
        date.setDate(this.expense.date);
        date.addEventListener(EVENT.CHANGE, () =>  {
            this.getIncome().setDate(this.getDate())
        });

        let creditor = new AonRegistrySuggestion();
        creditor.id = this.EXPENSE_CREDITOR;
        creditor.types = [RegistryType.CREDITOR];
        if(this.getExpense().getCreditor())
            creditor.setCreditor(this.getExpense().getCreditor());
        div.appendChild(creditor);

        let expAccount = createSelect(this.EXPENSE_EXPACCOUNT, "Ingreso");
        expAccount.setValue(this.expense.expAccount);
        expAccount.autocomplete = true;
        expAccount.setAlias("id", "description");
        div.appendChild(expAccount);
        getAccounts({ code: "7", entryEnabled: true, active: true }).then(accounts => {
            expAccount.setOptions(accounts);
            expAccount.value = this.expense.expAccount.id;
        });
        expAccount.addEventListener(EVENT.CHANGE, () =>  {
            this.getExpense().setExpAccount(this.getExpAccount());
        });

        let description = createInput(this.EXPENSE_DESCRIPTION, MSG.DESCRIPTION, div);
        description.setValue(this.expense.concept);
        description.addEventListener(EVENT.CHANGE, () => {
            this.getExpense().setConcept(this.getConcept());
        });

        let subDiv = this.createElement(TAG.DIV);
        subDiv.style.display = "flex";
        div.appendChild(subDiv);

        let reference = createInput(this.EXPENSE_REFERENCE, MSG.REFERENCE, subDiv)
        reference.setValue(this.expense.referenceCode);
        reference.style.width = "50%";
        reference.addEventListener(EVENT.CHANGE, () => {
            this.getExpense().setReferenceCode(this.getReference());
        });

        let amount = createNumber(this.EXPENSE_AMOUNT, MSG.AMOUNT, subDiv);
        amount.value = this.expense.amount;
        amount.style.marginLeft = "2px";
        amount.style.width = "50%";
        amount.addEventListener(EVENT.CHANGE, () =>{
            this.getExpense().setAmount(this.getAmount());
        });

        let paymentMethod = createSelect (this.EXPENSE_PAYMENT,MSG.PAYMETHOD, div);
        paymentMethod.setAlias("id", "alias");
        let opciones = [];
        getCompanyBanks({ active: true }).then(banks => {
            banks
                .filter(b => b.active)
                .forEach(b => {
                    b.alias = b.alias; 
                    opciones.push(b); 
                });
        
            getAccounts({ code: "570", entryEnabled: true, active: true }).then(accounts => {
                accounts.forEach(a => {
                    a.alias = a.description; 
                    opciones.push(a); 
                });
        
                paymentMethod.setOptions(opciones);
                if(this.expense.cashAccount){
                    paymentMethod.value = this.expense.cashAccount.id;
                    paymentMethod.addEventListener(EVENT.CHANGE, () =>{
                        this.getExpense().setCashAccount(this.getPaymethods());
                    });
                }
                if(this.expense.bank){
                    paymentMethod.value = this.expense.bank.id;
                    paymentMethod.addEventListener(EVENT.CHANGE, () =>{
                        this.getExpense().setBank(this.getPaymethods());
                    })
                }
                    
            });
        });

        let comments = createTextarea(this.EXPENSE_COMMENTS, MSG.COMMENTS, div);
        comments.setValue(this.expense.comments);
        comments.addEventListener(EVENT.CHANGE, () => {
            this.getExepnse().setComments(this.getComments());
        })
        this.getElement(this.EXPENSE_COMMENTS + "Textarea").style.height = "100px";
        this.getElement(this.EXPENSE_COMMENTS + "Textarea").style.marginTop = "2px";
    }

    back() {
        this.getApplication().setContent(new AonExpenseList());
    }

    save() {
        console.log(JSON.stringify(this.expense));
        setExpense(this.expense);
    }

    getActivity() {
        return this.getElement(this.EXPENSE_ACTIVITY).getValue();
    }

    getDate() {
        return this.getElement(this.EXPENSE_DATE).getValue();
    }

    getCreditor() {
        return this.getElement(this.EXPENSE_CREDITOR).getRegistry();
    }

    getExpAccount() {
        return this.getElement(this.EXPENSE_EXPACCOUNT).getValueObject();
    }

    getConcept() {
        return this.getElement(this.EXPENSE_DESCRIPTION).getValue();
    }

    getReference() {
        return this.getElement(this.EXPENSE_REFERENCE).getValue();
    }

    getAmount() {
        return this.getElement(this.EXPENSE_AMOUNT).getValue();
    }

    getComments() {
        return this.getElement(this.EXPENSE_COMMENTS).getValue();
    }

    getPaymethods() {
        return this.getElement(this.EXPENSE_PAYMENT).getValueObject();
    }

    getExpense() {
        return this.expense;
    }

    setExpense(expense) {
        this.expense = expense;
    }
}
if(!window.customElements.get(TAG.AON_EXPENSE)){
    window.customElements.define(TAG.AON_EXPENSE, AonExpense);
}
