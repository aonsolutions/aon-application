import { Registry } from "./Registry.js";

export class Supplier extends Registry {

    tariff;
    withholding;
    withholdingFarmer;
    vatAccrualPayment;
    transaction;
    status;
    scope;
    purchaseValuated;
    account;

    constructor(customer) {
        super(customer);
        this.tariff = customer.tariff;
        this.withholding = customer.withholding;
        this.withholdingFarmer = customer.withholdingFarmer;
        this.vatAccrualPayment = customer.vatAccrualPayment;
        this.transaction = customer.transaction;
        this.status = customer.status;
        this.scope = customer.scope;
        this.purchaseValuated = customer.purchaseValuated;
        this.account = customer.account;
    }

    isWithholdingFarmer() {
        return this.withholdingFarmer;
    }

    setWithholdingFarmer(withholdingFarmer) {
        this.withholdingFarmer = withholdingFarmer;
        return this;
    }

    isWithholding() {
        return this.withholding;
    }

    setWithholding(withholding) {
        this.withholding = withholding;
        return this;
    }

    isVatAccrualPayment() {
        return this.vatAccrualPayment;
    }

    setVatAccrualPayment(vatAccrualPayment) {
        this.vatAccrualPayment = vatAccrualPayment;
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