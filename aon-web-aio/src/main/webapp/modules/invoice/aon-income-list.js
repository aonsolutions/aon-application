import { CONSTANT, EVENT, MSG, TAG } from '../../environments/environments.js';
import { getIncomes } from '../../services/accountingService.js';
import { AonIncome } from './aon-income.js';
import { Income } from './Income.js';
import { AonList } from '../../components/aon-list.js';
export class AonIncomeList extends AonList {

    incomes;

    constructor () {
        super();
    }
    connectedCallback () {
        this.initialize();
        this.buildIncomeList();
    }

    initialize() {
        this.id = this.id || 'aonIncomeList';
        this.TABLE = this.id + CONSTANT.TABLE.initCap();
        this.filter = this.filter || {
            page:1,
            perPage:30
        }
        this.more = this.incomes ? false : true;
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
                name: 'Ingreso',
                type: 'string',
                id: 'incomeDescription',
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

    buildIncomeList() {
        const btnadd = this.getApplication().addOption("add","add", () => this.add());
        this.build();
    }
    
    aonObject(incomingIncome) {
        let income = new AonIncome( new Income(incomingIncome) );
        this.getApplication().setContent(income);
    }

    getObjects() {
        return new Promise((resolve, reject) => {
            getIncomes(this.filter)
                .then(incomes => {  
                    if (!Array.isArray(incomes)) {
                        incomes = []; 
                    }
                    incomes.forEach(income => {
                        if (income.expAccount) 
                            income.incomeDescription = income.expAccount.description;
                        
                        if (income.bank && income.bank.alias)
                            income.paymethodDescription = income.bank.alias;
                        else
                            income.paymethodDescription = income.cashAccount?.description || "N/A"
    
                        income.formattedAmount = this.formatAmount(income.amount);
    
                    });
                    resolve(incomes); 
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
        let inc = new AonIncome( new Income() );
        this.getApplication().setContent(inc);
    }

    getIncomes() {
        return this.incomes;
    }

    setIncomes(incomes) {
        this.incomes = incomes;
    }
    
}

if(!window.customElements.get(TAG.AON_INCOME_LIST)) {
    window.customElements.define(TAG.AON_INCOME_LIST, AonIncomeList);
}