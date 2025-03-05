import { Registry } from "./Registry.js";

export class Customer extends Registry {

    tariff;
    surcharge;
    withholding;
    transaction;
    status;
    scope;
    eInvoice;
    invoicingGroup;
    projectGrouped;
    deliveryGrouped;
    deliveryValuated;
    account;

    //AUDIT
    creation_user;
    creation_date;
    modification_user;
    modification_date;

    constructor(customer) {
        super(customer);
        if(customer) {
            this.tariff = customer.tariff;
            this.surcharge = customer.surcharge;
            this.withholding = customer.withholding;
            this.transaction = customer.transaction || 'NAC';
            this.status = customer.status;
            this.scope = customer.scope;
            this.eInvoice = customer.eInvoice;
            this.invoicingGroup = customer.invoicingGroup;
            this.projectGrouped = customer.projectGrouped;
            this.deliveryGrouped = customer.deliveryGrouped;
            this.deliveryValuated = customer.deliveryValuated;
            this.account = customer.account;

            //--------AUDIT
            this.creation_user     = customer.creation_user;
            this.creation_date     = customer.creation_date;
            this.modification_user = customer.modification_user;
            this.modification_date = customer.modification_date;
        } else {
            this.transaction = 'NAC';
            this.status = 'ACTIVE';
            this.withholding = false;
            this.surcharge = false;
        }   

    }

    isSurcharge() {
        return this.surcharge;
    }

    setSurcharge(surcharge) {
        this.surcharge = surcharge;
        return this;
    }

    isWithholding() {
        return this.withholding;
    }

    setWithholding(withholding) {
        this.withholding = withholding;
        return this;
    }

    getTransaction() {
        return this.transaction;
    }

    setTransaction(transaction) {
        this.transaction = transaction;
        return this;
    }

    getAccount() {
        return this.account;
    }

    setAccount(account) {
        this.account = account;
        return this;
    }

    getScope() {
        return this.scope;
    }

    setScope(scope) {
        this.scope = scope;
        return this;
    }

    getStatus() {
        return this.status;
    }

    setStatus(status) {
        this.status = status;
        return this;
    }

    getCreationUser() {
        return this.creation_user;
    }

    setCreationUser(creation_user) {
        this.creation_user = creation_user;
        return this;
    }
    
    getCreationDate() {
        return this.creation_date;
    }

    setCreationDate(creation_date) {
        this.creation_date = creation_date;
        return this;
    }

    getModificationUser() {
        return this.modification_user;
    }

    setModificationUser(modification_user) {
        this.modification_user = modification_user;
        return this;
    }
    
    getModificationDate() {
        return this.modification_date;
    }

    setModificationDate(modification_date) {
        this.modification_date = modification_date;
        return this;
    }
}