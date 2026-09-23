import {AonElement} from '../../components/AonElement.js';
import { RegistryType, ToolbarType} from '../../models/enums.js';
import {AonToolbar} from "../../components/aon-toolbar.js";
import { CONSTANT, EVENT, MSG, TAG } from '../../environments/environments.js'; 
import * as ACTION from '../actions.js';
import { createCard, createDate, createInput, createSelect, createTextarea, createNumber } from '../../components/CreateComponent.js';
import { AonRegistrySuggestion } from '../registry/aon-registry-suggestion.js';
import { getCompanyActivities, getCompanyBanks } from '../../services/companyService.js';
import { deleteExpense, getAccounts, setExpense } from '../../services/accountingService.js';
import { AonExpenseList } from './aon-expense-list.js';
import { AonMobileExpenseList } from './aon-mobile-expense-list.js';
import { Expense } from './Expense.js';

export class AonExpense extends AonElement {

    EXPENSE_TOOLBAR;
    EXPENSE_CARD;
    EXPENSE_DATE;
    EXPENSE_ACTIVITY;
    EXPENSE_CREDITOR
    EXPENSE_EXPACCOUNT;
    EXPENSE_DESCRIPTION;
    EXPENSE_REFERENCE;
    EXPENSE_AMOUNT;
    EXPENSE_PAYMENT;
    EXPENSE_COMMENTS;
    DIV_GENERAL;

    expense

    get id() {
        return this.getAttribute(CONSTANT.ID);
    }

    set id(id) {
        this.setAttribute(CONSTANT.ID, id);
    }

    constructor (expense) {
        super();
        this.expense = expense;
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
        this.EXPENSE_CREDITOR = this.id + "Creditor";
        this.EXPENSE_ACTIVITY = this.id + "Activity";
        this.EXPENSE_EXPACCOUNT = this.id + "ExpAccount";
        this.EXPENSE_DESCRIPTION = this.id + "Description";
        this.EXPENSE_REFERENCE = this.id + "Reference";
        this.EXPENSE_AMOUNT = this.id + "Amount";
        this.EXPENSE_PAYMENT = this.id + "PaymentMethod";
        this.EXPENSE_COMMENTS = this.id + "Comments";
        this.DIV_GENERAL = this.id + 'Div';
    }

    build() {
        let toolbar = new AonToolbar();
        toolbar.id = this.EXPENSE_TOOLBAR;
        toolbar.type = ToolbarType.SECONDARY;   
        toolbar.title = MSG.EXPENSES; 
        this.appendChild(toolbar);

        toolbar.addButton2(ACTION.DELETE, () => this.delete());
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
        activity.setValue(this.expense.activity);
        activity.setAlias("id", "description");
        getCompanyActivities({}).then(activities => {
            if (activities.length > 0) {
                activity.setOptions(activities);
                const principalActivity = activities.find(act => act.principal === true);
                if (principalActivity) activity.value = principalActivity.id;
            }
        });
        activity.addEventListener(EVENT.CHANGE, () => {
            this.getExpense().setActivity(this.getActivity());
        })
    
        let date = createDate(this.EXPENSE_DATE, MSG.DATE, div);
        let fixedDate = this.fixDateFormat(this.expense.date);
        date.setDate(fixedDate);
        date.addEventListener(EVENT.CHANGE, () =>  {
            this.getExpense().setDate(this.getDate())
        });


        let creditor = new AonRegistrySuggestion();
        creditor.types = RegistryType.CREDITOR;
        creditor.id = this.EXPENSE_CREDITOR;
        if(this.getExpense().getCreditor())
            creditor.setRegistry(this.getExpense().getCreditor());
        creditor.addEventListener(EVENT.SELECT_REGISTRY, () => this.getExpense().setCreditor(this.getCreditor() ));
        div.appendChild(creditor);

        let expAccount = createSelect(this.EXPENSE_EXPACCOUNT, "Tipo de gasto");
        expAccount.setValue(this.expense.expAccount?this.expense.expAccount:null);
        expAccount.autocomplete = true;
        expAccount.setAlias("id", "description");
        div.appendChild(expAccount);
        
        let selectedAccount = this.expense.expAccount; 
        let filteredAccounts = []; 
        
        getAccounts({ code: ["63", "64", "65", "66", "67"], entryEnabled: true, active: true })
            .then(accounts => {
                filteredAccounts = accounts;        
                
                if (selectedAccount && !filteredAccounts.some(acc => acc.id === selectedAccount.id)) {
                    filteredAccounts.unshift(selectedAccount); 
                }

                expAccount.setOptions(filteredAccounts);
                if (selectedAccount) {
                    expAccount.value = selectedAccount.id; 
                }
            });
        
        expAccount.addEventListener(EVENT.CHANGE, () => {
            this.getExpense().setExpAccount(this.getExpAccount());
        });
           
        let description = createInput(this.EXPENSE_DESCRIPTION, MSG.CONCEPT, div);
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
        amount.format = CONSTANT.TRUE;
        amount.decimals = "2";
        amount.value = this.expense.amount;
        amount.style.marginLeft = "2px";
        amount.style.width = "50%";
        amount.addEventListener(EVENT.CHANGE, () =>{
            this.getExpense().setAmount(this.getAmount());
        });

        let paymentMethod = createSelect(this.EXPENSE_PAYMENT, MSG.PAYMETHOD, div);
        paymentMethod.setAlias("id", "alias");
        let opts = [];
        let selectedBankId = null;

        getCompanyBanks({ active: true })
            .then(banks => banks )
            .then(banks => getAccounts({ code: "570", entryEnabled: true, active: true }).then(accounts => [banks,accounts] ))
            .then(arr => {
                let banks = arr[0];
                if (banks) {
                    banks
                    .filter(b => b.active)
                    .forEach(b => {
                        b.bank  = true; 
                        opts.push(b);
                        if ( !this.expense.bank
                            && this.expense.cashAccount 
                            && this.expense.cashAccount.id
                            && b.account
                            && b.account.id
                            && b.account.id == this.expense.cashAccount.id) {
                                selectedBankId = b.id;
                        }
                    })
                }
                
                let accounts = arr[1];
                if (accounts) {
                    accounts.forEach(a => {
                        a.alias = a.description; 
                        a.bank  = false; 
                        opts.push(a); 
                    });
                }

                paymentMethod.setOptions(opts);
                if ( this.expense.bank && this.expense.bank.id) {
                    paymentMethod.value = this.expense.bank.id;
                } else if (this.expense.cashAccount && this.expense.cashAccount.id) {
                    if (selectedBankId)
                        paymentMethod.value = selectedBankId;
                    else {
                        paymentMethod.value = this.expense.cashAccount.id;
                    }    
                }
            })  
        ;

        paymentMethod.addEventListener(EVENT.CHANGE, ()=>{
            let pm = this.getPaymethods();
            if (!pm) {
                this.getExpense().setBank( null );
                this.getExpense().setCashAccount(null);
            } else if (pm.bank) {
                this.getExpense().setBank( pm );
                this.getExpense().setCashAccount(null);
            } else {
                this.getExpense().setBank( null );
                this.getExpense().setCashAccount(pm);
            }
        });

        let comments = createTextarea(this.EXPENSE_COMMENTS, MSG.COMMENTS, div);
        if(this.expense.comments)
            comments.setValue(this.expense.comments);
        comments.addEventListener(EVENT.CHANGE, () => {
            this.getExpense().setComments(this.getComments());
        })
        this.getElement(this.EXPENSE_COMMENTS + "Textarea").style.height = "100px";
        this.getElement(this.EXPENSE_COMMENTS + "Textarea").style.marginTop = "2px";
    }

    back() {
        let list = this.isMobile() ? new AonMobileExpenseList() : new AonExpenseList();
        this.getApplication().setContent(list);
    }

    save() {
        console.log("CREDITOR!!!:"+ JSON.stringify(this.getCreditor()));    
        console.log(JSON.stringify(this.expense));
        setExpense(this.expense)
            .then( r => { 
                if(this.getExpense().getNew() == true)
                    this.showMessage("Gasto grabado correctamente");
                else if(this.getExpense().getNew() == false)
                    this.showMessage("Gasto modificado correctamente");
                this.setExpense(new Expense(r))
            })
            .catch( e => this.showError(e));
    }

    delete() {
        this.getApplication().confirmDialog("Gasto", "¿Desea borrar el gasto?", null, MSG.CONFIRM);
        this.getElement("aonInvoiceDialogDialogActionAccept").addEventListener(EVENT.CLICK, ()=> this.deleteAction());
    }

    deleteAction() {
        deleteExpense(this.expense)
            .then( r => { 
                this.back() 
                this.showMessage("Gasto borrado correctamente") })
            .catch( e => this.showError(e))
    }


    getActivity() {
        return this.getElement(this.EXPENSE_ACTIVITY).getValue();
    }

    getDate() {
        return this.getElement(this.EXPENSE_DATE).getValue();
    }

    getExpAccount() {
        return this.getElement(this.EXPENSE_EXPACCOUNT).getValueObject();
    }

    getConcept() {
        return this.getElement(this.EXPENSE_DESCRIPTION).getValue();
    }

    getCreditor() {
        return this.getElement(this.EXPENSE_CREDITOR).getRegistry();
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

    fixDateFormat(dateString) {
        if (dateString !== undefined && dateString !== null) {
            let strDate = dateString.toString(); 
            let parts = strDate.includes("/") ? strDate.split("/") : strDate.split("-"); 
    
            if (parts.length === 3) {
                let day = parts[0].padStart(2, '0');
                let month = parts[1].padStart(2, '0');
                let year = parts[2];
    
                return `${year}-${month}-${day}`; 
            }
        }
        return dateString; 
    }
}
if(!window.customElements.get(TAG.AON_EXPENSE)){
    window.customElements.define(TAG.AON_EXPENSE, AonExpense);
}
