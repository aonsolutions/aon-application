import { CONSTANT, EVENT, MSG, TAG } from '../../environments/environments.js';
import { getExpenses } from '../../services/accountingService.js';
import { AonExpense } from './aon-expense.js';
import { Expense } from './Expense.js';
import { AonList } from '../../components/aon-list.js';
export class AonExpenseList extends AonList {

    expenses;

    constructor () {
        super();
    }

    connectedCallback () {
        this.initialize();
        this.buildExpenseList();
    }

    initialize() {
        this.id = this.id || 'aonExpenseList';
        this.TABLE = this.id + CONSTANT.TABLE.initCap();
        this.filter = this.filter || {
            page:1,
            perPage:30
        }
        this.more = this.expenses ? false : true;
        this.columns = [{
                name: MSG.DATE,
                type: 'date',
                id: 'date',
                width: '150px'
            }, {
                name: MSG.REFERENCE,
                type: 'string',
                id: 'referenceCode',
                width: '150px'
            }, {
                name: 'Gasto',
                type: 'string',
                id: 'expenseDescription',
                width: '300px'
            }, {
                name: MSG.CONCEPT,
                type: 'string',
                id: 'concept',
                width: '300px'
            }, {
                name: MSG.PAYMETHOD,
                type: 'string',
                id: 'paymethodDescription',
                width: '300px'
            }, {
                name: MSG.AMOUNT,
                type: 'double',
                id: 'formattedAmount',
                width: '200px'
            }];
    }

    buildExpenseList() {
        this.build();
        const btnadd = this.getApplication().addOption("add","add", () => this.add());
    }

    aonObject(incomingExpense) {
        let expense = new AonExpense( new Expense(incomingExpense) );
        this.getApplication().setContent(expense);
    }

    getObjects() {
        return new Promise((resolve, reject) => {
            let table = this.getElement(this.TABLE);
            getExpenses(this.filter)
                .then(expenses => {  
                    if (!Array.isArray(expenses)) {
                        expenses = []; 
                    }
                    table.removeRows();
                    expenses.forEach(expense => {
                        if (expense.expAccount) 
                            expense.incomeDescription = expense.expAccount.description;
                        
                        if (expense.bank && expense.bank.alias)
                            expense.paymethodDescription = expense.bank.alias;
                        else
                        expense.paymethodDescription = expense.cashAccount?.description || "N/A"
    
                        expense.formattedAmount = this.formatAmount(expense.amount);
                        table.addRow(expense, () => this.expenseObject(expense));
                    });
    
                    resolve(expenses); 
                })
                .catch(error => {
                    console.error("Error en getObjects():", error);
                    resolve([]);
                });
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

    getExpenses() {
        return this.expenses;
    }

    setExpenses(expenses) {
        this.expenses = expenses;
    }
}

if(!window.customElements.get(TAG.AON_EXPENSE_LIST)) {
    window.customElements.define(TAG.AON_EXPENSE_LIST, AonExpenseList);
}