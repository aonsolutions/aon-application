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

    constructor(supplier) {
        super(supplier);
        if(supplier) {
            this.tariff = supplier.tariff;
            this.withholding = supplier.withholding;
            this.withholdingFarmer = supplier.withholdingFarmer;
            this.vatAccrualPayment = supplier.vatAccrualPayment;
            this.transaction = supplier.transaction || 'NAC';
            this.status = supplier.status;
            this.scope = supplier.scope;
            this.purchaseValuated = supplier.purchaseValuated;
            this.account = supplier.account;
        } else {
            this.transaction = 'NAC';
            this.withholding = false;
            this.withholdingFarmer = false;
            this.vatAccrualPayment = false;
        }

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