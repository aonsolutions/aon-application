import { Registry } from "./Registry.js";

export class Target extends Registry {

    tariff;
    surcharge;
    withholding;
    transaction;
    status;
    scope;
    advertising;

    //AUDIT
    creation_user;
    creation_date;
    modification_user;
    modification_date;

    constructor(target) {
        super(target);
        if(target) {
            this.tariff = target.tariff;
            this.surcharge = target.surcharge;
            this.withholding = target.withholding;
            this.transaction = target.transaction || 'NAC';
            this.status = target.status;
            this.scope = target.scope;
            this.advertising = target.advertising;

            //--------AUDIT
            this.creation_user     = target.creation_user;
            this.creation_date     = target.creation_date;
            this.modification_user = target.modification_user;
            this.modification_date = target.modification_date;
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