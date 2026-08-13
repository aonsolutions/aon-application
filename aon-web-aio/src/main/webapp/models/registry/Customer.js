import { Registry } from "./Registry.js";

export class Customer extends Registry {

    tariff;
    surcharge;
    withholding;
    transaction;
    status;
    expirationDate;
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
            this.expirationDate = customer.expirationDate || null;
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
            this.expirationDate = null;
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
    
    getExpirationDate() {
        return this.expirationDate;
    }

    setExpirationDate(expirationDate) {
        this.expirationDate = expirationDate;
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
    
    /** 'yyyy-MM-dd' -> Date local. new Date(str) lo leeria como UTC. */
	getExpirationDateAsDate() {
		if (!this.expirationDate) return null;
		if (this.expirationDate instanceof Date) return this.expirationDate;
 
		const parts = String(this.expirationDate).split("-");
		if (parts.length !== 3) return null;
 
		const year = Number(parts[0]);
		const month = Number(parts[1]);
		const day = Number(parts[2]);
 
		if (!year || !month || !day) return null;
 
		return new Date(year, month - 1, day);
	}
 
	/** Sin fecha se considera vencida: un BLOCKED sin fecha bloquea de inmediato. */
	isExpirationReached() {
		const date = this.getExpirationDateAsDate();
		if (!date) return true;
 
		const today = new Date();
		today.setHours(0, 0, 0, 0);
 
		return date.getTime() <= today.getTime();
	}
 
	/** BLOCKED con fecha aun no vencida: efectivamente activo. */
	isBlockScheduled() {
		return this.status === "BLOCKED" && !this.isExpirationReached();
	}
 
	/** Lo que hay que MOSTRAR. 'status' sigue siendo lo que se edita y se guarda. */
	getEffectiveStatus() {
		if (this.status !== "BLOCKED") return this.status;
 
		return this.isExpirationReached() ? "BLOCKED" : "ACTIVE";
	}
}