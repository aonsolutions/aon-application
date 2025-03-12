import { CONSTANT } from "../../environments/environments.js";
import { RegistryType } from "../../models/enums.js";
import { round, now } from "../../services/utils.js";
import { getSurchargeByVat, TaxType, WithholdingType } from "./invoiceEnums.js";
import * as LS from '../../services/localStorageService.js';

export class Income {

  domain; 
  activity;
  date;
  customer;
  expAccount;
  concept;
  referenceCode;
  amount;
  bank;
  cashAccount;
  comments;

  accountEntry; 
  finance; 



  constructor(income) {
    this.buildObject(income);
  }

  buildObject(income) {
    if(income) {
      this.domain = income.domain || LS.getDomainId();
      this.activity = income.activity;
      this.date = income.date;
      this.customer = income.customer;
      this.expAccount = income.expAccount;
      this.concept = income.concept;
      this.referenceCode = income.referenceCode;
      this.amount = income.amount;
      this.bank = income.bank;
      this.cashAccount = income.cashAccount;
      this.comments = income.comments;
      this.accountEntry = income.accountEntry;
      this.finance = income.finance;
    }
    
  }

  getDomain() {
    return this.domain;
  }

  setDomain(domain) {
    this.domain = domain;
    return this;
  }

  getActivity() {
    return this.activity;
  }

  setActivity(activity) {
    this.activity = activity;
    return this; 
  }

  getDate() {
    return this.date;
  }

  setDate(date) {
    this.date = date;
    return this;
  }

  getCustomer() {
    return this.customer;
  }

  setCustomer(customer) {
    this.customer = customer;
    return this;
  }

  getExpAccount() {
    return this.expAccount;
  }

  setExpAccount(expAccount) {
    this.expAccount = expAccount;
    return this;
  }

  getConcept() {
    return this.concept;
  }

  setConcept(concept) {
    this.concept = concept;
    return this;
  }

  getReferenceCode() {
    return this.referenceCode;
  }

  setReferenceCode(referenceCode) {
    this.referenceCode = referenceCode;
    return this;
  }

  getAmount() {
    return this.amount;
  }

  setAmount(amount) {
    this.amount = amount;
    return this;
  }

  getBank() {
    return this.bank;
  }

  setBank(bank) {
    this.bank = bank;
    return this;
  }

  getCashAccount() {
    return this.cashAccount;
  }

  setCashAccount(cashAccount) {
    this.cashAccount = cashAccount;
    return this;
  }

  getComments() {
    return this.comments;
  }

  setComments(comments) {
    this.comments = comments;
    return this;
  }

  getAccountEntry() {
    return this.accountEntry;
  }

  setAccountEntry(accountEntry) {
    this.accountEntry = accountEntry;
    return this;
  }

  getFinance() {
    return this.finance;
  }

  setFinance(finance) {
    this.finance = finance;
    return this;
  }

}

