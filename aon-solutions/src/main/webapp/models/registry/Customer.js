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

    constructor(customer) {
        super(customer);
        this.tariff = customer.tariff;
        this.surcharge = customer.surcharge;
        this.withholding = customer.withholding;
        this.transaction = customer.transaction;
        this.status = customer.status;
        this.scope = customer.scope;
        this.eInvoice = customer.eInvoice;
        this.invoicingGroup = customer.invoicingGroup;
        this.projectGrouped = customer.projectGrouped;
        this.deliveryGrouped = customer.deliveryGrouped;
        this.deliveryValuated = customer.deliveryValuated;
        this.account = customer.account;
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

}