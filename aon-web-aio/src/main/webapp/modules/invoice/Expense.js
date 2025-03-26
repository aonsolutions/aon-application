import * as LS from '../../services/localStorageService.js';

export class Expense {

  domain; 
  activity;
  date;
  //creditor;
  expAccount;
  concept;
  referenceCode;
  amount;
  bank;
  cashAccount;
  comments;

  accountEntry; 
  finance; 

  constructor(expense) {
    this.buildObject(expense);
    if(expense)
      this.isNew = false;
    else
      this.isNew = true
  }

  buildObject(expense) {
    if(expense) {
      this.domain = expense.domain || LS.getDomainId();
      this.activity = expense.activity;
      this.date = expense.date;
      //this.creditor = expense.creditor;
      this.expAccount = expense.expAccount;
      this.concept = expense.concept;
      this.referenceCode = expense.referenceCode;
      this.amount = expense.amount;
      this.bank = expense.bank;
      this.cashAccount = expense.cashAccount;
      this.comments = expense.comments;
      this.accountEntry = expense.accountEntry;
      this.finance = expense.finance;
    } else {
      this.domain = LS.getDomainId();
      this.date = new Date();
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

  // getCreditor() {
  //   return this.creditor;
  // }

  // setCreditor(creditor) {
  //   this.creditor = creditor;
  //   return this;
  // }

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

  getNew() {
    return this.isNew;
  }

}

