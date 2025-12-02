import { Account } from "../../../models/Account.js";
import { BankAccount } from "./BankAccount.js";
import * as LS from '../../../services/localStorageService.js';

export class Bank {

    id;
    domain;
    registry;
    bankAccount;
    bank_account;
    iban;
    bank;
    bic;
    sufix;
    alias;
    active;
    account;
    dirty;
    removed;

    fullName;

    constructor(bank) {
        if(bank) {
           this.id = bank.id;
           this.domain = bank.domain || LS.getDomainId();
           this.registry = bank.registry;
           this.bankAccount = new BankAccount(bank.bank_account);
           this.bank_account = bank.bank_account;
           this.iban = this.getBankAccount().getIban();
           this.bank = this.getBankAccount().getBank();
           this.bic = bank.bic || this.getBankAccount().getBic();
           this.sufix = bank.sufix;
           this.account = new Account(bank.account);
           this.alias = bank.alias || '';
           this.active = bank.active || true;
           this.dirty = bank.dirty || false;
           this.removed = bank.removed || false;
           this.fullName = this.formatIban() + ' - ' + this.bank;
		   this.balanceDate = bank.balanceDate ? bank.balanceDate : null;
        } else {
            this.domain = LS.getDomainId();
            this.bankAccount = new BankAccount();
            this.alias = '';
            this.sufix = '';
            this.account = new Account();
            this.bic = '';
            this.active = true;
            this.dirty = false;
            this.removed = false;
            this.fullName = '';
        }
    }

    getId() {
        return this.id;
    }

    setId(id) {
        this.id = id;
        return this;
    }

    getDomain() {
        return this.domain;  
    }

    setDomain(domain) {
        this.domain = domain;
        return this;
    }

    getRegistry() {
        return this.registry;
    }

    setRegistry(registry) {
        this.registry = registry;
        return this;
    }

    getAlias() {
        return this.alias;
    }

    setAlias(alias) {
        this.setDirty(true);
        this.alias = alias;
        return this;
    }

    getBankAccount() {
        return this.bankAccount;
    }

    setBankAccount(bankAccount) {
        this.setDirty(true);
        this.bankAccount = new BankAccount(bankAccount);
        this.bank_account = this.bankAccount.getIban();
        this.iban = this.bankAccount.getIban();
        this.bank = this.bankAccount.getBank();
        this.fullName = this.iban + ' - ' + this.bank;
        this.setBic(this.bankAccount.getBic());
        return this;
    }

    getBic() {
        return this.bic;
    }

    setBic(bic) {
        this.setDirty(true);
        this.bic = bic;
        return this;
    }

    getSufix() {
        return this.sufix;
    }

    setSufix(sufix) {
        this.setDirty(true);
        this.sufix = sufix;
        return this;
    }

    getAccount() {
        return this.getAccount();
    }

    setAccount(account) {
        this.setDirty(true);
        this.account = new Account(account);
        return this;
    } 

    isActive() {
        return this.active;
    }

    setActive(active){
        this.setDirty(true);
        this.active = active;
        return this;
    }

    isDirty() {
        return this.dirty;
    }

    setDirty(dirty) {
        this.dirty = dirty;
        return this;
    }

    isRemoved() {
        return this.removed; 
    }

    setRemoved(removed) {
        this.removed = removed;
        return this;
    }

    remove() {
        this.setRemoved(true);
    }

    formatIban() {
        let EVERY_FOUR_CHARS =/(.{4})(?!$)/g;
        return this.iban.replace(EVERY_FOUR_CHARS, "$1" + ' ');
    }


} 