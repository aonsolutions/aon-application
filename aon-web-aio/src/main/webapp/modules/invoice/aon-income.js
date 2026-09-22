import {AonElement} from '../../components/AonElement.js';
import { ToolbarType} from '../../models/enums.js';
import {AonToolbar} from "../../components/aon-toolbar.js";
import { CONSTANT, EVENT, MSG, TAG } from '../../environments/environments.js'; 
import * as ACTION from '../actions.js';
import { createCard, createDate, createInput, createSelect, createTextarea, createNumber } from '../../components/CreateComponent.js';
import { AonCustomerSuggestion } from '../registry/customer/aon-customer-suggestion.js';
import { getCompanyActivities, getCompanyBanks } from '../../services/companyService.js';
import { deleteIncome, getAccounts, setIncome } from '../../services/accountingService.js';
import { AonIncomeList } from './aon-income-list.js';
import { AonMobileIncomeList } from './aon-mobile-income-list.js';
import { Income } from './Income.js';

export class AonIncome extends AonElement {

    INCOME_TOOLBAR;
    INCOME_CARD;
    INCOME_DATE;
    INCOME_ACTIVITY;
    INCOME_EXPACCOUNT;
    INCOME_DESCRIPTION;
    INCOME_REFERENCE;
    INCOME_AMOUNT;
    INCOME_PAYMENT;
    INCOME_CUSTOMER;
    INCOME_COMMENTS;
    DIV_GENERAL;

    income;

    get id() {
        return this.getAttribute(CONSTANT.ID);
    }

    set id(id) {
        this.setAttribute(CONSTANT.ID, id);
    }

    constructor (income) {
        super();
        this.income = income;
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
        this.INCOME_EXPACCOUNT = this.id + "ExpAccount";
        this.INCOME_CUSTOMER = this.id + "Customer";
        this.INCOME_DESCRIPTION = this.id + "Description";
        this.INCOME_REFERENCE = this.id + "Reference";
        this.INCOME_AMOUNT = this.id + "Amount";
        this.INCOME_PAYMENT = this.id + "PaymentMethod";
        this.INCOME_COMMENTS = this.id + "Comments";
        this.DIV_GENERAL = this.id + 'Div';
    }

    build() {
        let toolbar = new AonToolbar();
        toolbar.id = this.INCOME_TOOLBAR;
        toolbar.type = ToolbarType.SECONDARY;
        toolbar.title = MSG.INCOMES; 
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
        let card = createCard(this.INCOME_CARD, MSG.OTHER_INCOMES, parent);
    
        let div = this.createDiv();
        card.setContent(div);
    
        let activity = createSelect(this.INCOME_ACTIVITY, MSG.ACTIVITY, div);
        activity.setValue(this.income.activity);
        activity.setAlias("id", "description");
        getCompanyActivities({}).then(activities => {
            if (activities.length > 0) {
                activity.setOptions(activities);
                const principalActivity = activities.find(act => act.principal === true);
                if (principalActivity) activity.value = principalActivity.id;
            }
        });
        activity.addEventListener(EVENT.CHANGE, () => {
            this.getIncome().setActivity(this.getActivity());
        })
    
        let date = createDate(this.INCOME_DATE, MSG.DATE, div);
        let fixedDate = this.fixDateFormat(this.income.date);
        date.setDate(fixedDate);
        date.addEventListener(EVENT.CHANGE, () =>  {
            this.getIncome().setDate((this.getDate()));
        });

        let customer = new AonCustomerSuggestion();
        customer.id = this.INCOME_CUSTOMER;
        if(this.getIncome().getCustomer())
            customer.setCustomer(this.getIncome().getCustomer());
        customer.addEventListener(EVENT.SELECT_REGISTRY, () => this.getIncome().setCustomer(this.getCustomer() ));
        
        div.appendChild(customer);
    
        let expAccount = createSelect(this.INCOME_EXPACCOUNT, "Tipo de Ingreso");
        expAccount.setValue(this.income.expAccount?this.income.expAccount:null);
        expAccount.autocomplete = true;
        expAccount.setAlias("id", "description");
        div.appendChild(expAccount);
        
        let selectedAccount = this.income.expAccount; 
        let filteredAccounts = []; 
        
        getAccounts({ code: ["74", "75", "76", "77"], entryEnabled: true, active: true })
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
            this.getIncome().setExpAccount(this.getExpAccount());
        });
        
        let description = createInput(this.INCOME_DESCRIPTION, MSG.CONCEPT, div);
        description.setValue(this.income.concept) ;
        description.addEventListener(EVENT.CHANGE, () =>  {
            this.getIncome().setConcept(this.getConcept());
        });
    
        let subDiv = this.createElement(TAG.DIV);
        subDiv.style.display = "flex";
        div.appendChild(subDiv);
    
        let reference = createInput(this.INCOME_REFERENCE, MSG.REFERENCE, subDiv)
        reference.setValue(this.income.referenceCode);
        reference.style.width = "50%";
        reference.addEventListener(EVENT.CHANGE, () => {
            this.getIncome().setReferenceCode(this.getReference());
        });
    
        let amount = createNumber(this.INCOME_AMOUNT, MSG.AMOUNT, subDiv);
        amount.format = CONSTANT.TRUE;
		amount.decimals = "2";
        amount.value = this.income.amount;
        amount.style.marginLeft = "10px";
        amount.style.width = "50%";
        amount.addEventListener(EVENT.CHANGE, ()=>{
            this.getIncome().setAmount(this.getAmount());
        });
    
        let paymentMethod = createSelect(this.INCOME_PAYMENT, MSG.PAYMETHOD, div);
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
                        if ( !this.income.bank
                            && this.income.cashAccount 
                            && this.income.cashAccount.id
                            && b.account
                            && b.account.id
                            && b.account.id == this.income.cashAccount.id) {
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
                if ( this.income.bank && this.income.bank.id) {
                    paymentMethod.value = this.income.bank.id;
                } else if (this.income.cashAccount && this.income.cashAccount.id) {
                    if (selectedBankId)
                        paymentMethod.value = selectedBankId;
                    else {
                        paymentMethod.value = this.income.cashAccount.id;
                    }    
                }
            })  
        ;

        paymentMethod.addEventListener(EVENT.CHANGE, ()=>{
            let pm = this.getPaymethods();
            if (!pm) {
                this.getIncome().setBank( null );
                this.getIncome().setCashAccount(null);
            } else if (pm.bank) {
                this.getIncome().setBank( pm );
                this.getIncome().setCashAccount(null);
            } else {
                this.getIncome().setBank( null );
                this.getIncome().setCashAccount(pm);
            }
        });

        let comments = createTextarea(this.INCOME_COMMENTS, MSG.COMMENTS, div);
        if(this.income.comments)
            comments.setValue(this.income.comments);
        comments.addEventListener(EVENT.CHANGE, () => {
            this.getIncome().setComments(this.getComments());
        });

        this.getElement(this.INCOME_COMMENTS + "Textarea").style.height = "100px";
        this.getElement(this.INCOME_COMMENTS + "Textarea").style.marginTop = "2px";

    }
    
    back() {
        let list = this.isMobile() ? new AonMobileIncomeList() : new AonIncomeList();
        this.getApplication().setContent(list);
    }

    save() {
        setIncome(this.income)
            .then( r => { 
                if(this.getIncome().getNew() == true)
                    this.showMessage("Ingreso grabado correctamente");
                else if(this.getIncome().getNew() == false)
                    this.showMessage("Ingreso modificado correctamente");
                this.setIncome(new Income(r))
            })
            .catch( e => this.showError(e));
    }

    delete() {
        this.getApplication().confirmDialog("Ingreso", "¿Desea borrar el ingreso?", null, MSG.CONFIRM);
        this.getElement("aonInvoiceDialogDialogActionAccept").addEventListener(EVENT.CLICK, ()=> this.deleteAction());
    }

    deleteAction(){
        deleteIncome(this.income)
            .then( r => { 
                this.back()
                this.showMessage("Ingreso borrado correctamente") })
            .catch( e => this.showError(e));
    }

    getActivity() {
        return this.getElement(this.INCOME_ACTIVITY).getValue();
    }

    getDate() {
        return this.getElement(this.INCOME_DATE).getValue();
    }

    getCustomer() {
        return this.getElement(this.INCOME_CUSTOMER).getCustomer();
    }

    getExpAccount() {
        return this.getElement(this.INCOME_EXPACCOUNT).getValueObject();
    }

    getConcept() {
        return this.getElement(this.INCOME_DESCRIPTION).getValue();
    }

    getReference() {
        return this.getElement(this.INCOME_REFERENCE).getValue();
    }

    getAmount() {
        return this.getElement(this.INCOME_AMOUNT).getValue();
    }

    getComments() {
        return this.getElement(this.INCOME_COMMENTS).getValue();
    }

    getPaymethods() {
        return this.getElement(this.INCOME_PAYMENT).getValueObject();
    }

    getIncome() {
        return this.income;
    }

    setIncome(income){
        this.income = income;
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
if(!window.customElements.get(TAG.AON_INCOME)){
    window.customElements.define(TAG.AON_INCOME, AonIncome);
}
