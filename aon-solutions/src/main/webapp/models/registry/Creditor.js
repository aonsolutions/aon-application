import { Registry } from "./Registry.js";

export class Creditor extends Registry {
    
    withholding;
    vatAccrualPayment;
    transaction;
    status;
    scope;
    account;

    constructor(creditor) {
        super(creditor);
        this.withholding = creditor.withholding;
        this.vatAccrualPayment = creditor.vatAccrualPayment;
        this.transaction = creditor.transaction;
        this.status = creditor.status;
        this.scope = creditor.scope;
        this.account = creditor.account;
    }

    isVatAccrualPayment() {
        return this.vatAccrualPayment;
    }

    setVatAccrualPayment(vatAccrualPayment) {
        this.vatAccrualPayment = vatAccrualPayment;
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