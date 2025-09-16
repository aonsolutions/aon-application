import { AonElement } from './AonElement.js';
import { CONSTANT, EVENT, TAG, MATERIAL_ICONS, MSG } from '../environments/environments.js';
import { AonNewInput } from './aon-new-input.js';
import { Bank } from '../modules/registry/bank/Bank.js';

export class AonIban extends AonElement {
  EDIT;
  INPUT;
  IBAN;
  BANK;
  BIC;

  bank;

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	get value() {
		return this.getAttribute(CONSTANT.VALUE);
	}

	set value(value) {
		this.setAttribute(CONSTANT.VALUE, value);
	}

  get title() {
    return this.getAttribute(CONSTANT.TITLE);
  }

  set title(title) {
    this.setAttribute(CONSTANT.TITLE, title);
  }

  get readonly() {
    return this.getAttribute(CONSTANT.READONLY);
  }

  set readonly(readonly) {
    this.setAttribute(CONSTANT.READONLY, readonly);
  }

	constructor() {
		super();
	}

  connectedCallback() {
    this.initialize();
    this.build();
	}

  initialize() {
    this.id = this.id || 'aonAddress';
    this.EDIT = this.id + 'Edit';
    this.INPUT = this.id + 'Input';
    this.IBAN = this.id + 'Iban';
    this.BANK = this.id + 'Bank';
    this.BIC = this.id + 'Bic';
    this.bank = this.bank || new Bank();
  }

  build() {
    this.clear();
    let aonInput      = new AonNewInput();
    aonInput.id       = this.INPUT;
    aonInput.title    = this.bank.title ? this.bank.title : MSG.BANK_ACCOUNT;
    aonInput.value    = this.bank.fullName;
    aonInput.readonly = CONSTANT.READONLY;
    this.appendChild(aonInput);

    this.buildBank();
    
    aonInput.addIconWithRemove(MATERIAL_ICONS.ACCOUNT_BALANCE, undefined, () => this.dispatchEvent(new Event(EVENT.DELETE)));

    this.getElement(aonInput.INPUT).style.cursor = 'pointer';
    aonInput.addEventListener(EVENT.CLICK, () => {
      if (!this.isReadonly()) {
        let divEdit = this.getElement(this.EDIT);
        if (divEdit.style.display === "block") {
          divEdit.style.display = "none";
        } else {
          divEdit.style.display = "block";
        }
      }
    });
  }

  buildBank() {
    let infoBank           = this.createElement(TAG.DIV);
		infoBank.style.display = "none";
    infoBank.id            = this.EDIT;
    infoBank.classList.add('info-iban-modal');
    this.appendChild(infoBank);
    
    let ibanInput = new AonNewInput();
    ibanInput.id = this.IBAN;
    ibanInput.title = 'IBAN';
    ibanInput.readonly = this.isReadonly();
    ibanInput.value = this.bank.getBankAccount().getIban();
    ibanInput.addEventListener(EVENT.CHANGE, () => {
      this.bank.setBankAccount(ibanInput.value);
      this.getElement(this.INPUT).value = this.bank.getBankAccount().getIban();
      this.getElement(this.BANK).value = this.bank.getBankAccount().getBank();
      this.getElement(this.BIC).value = this.bank.getBic();
      this.dispatchEvent(new Event(EVENT.CHANGE));
    });
    infoBank.appendChild(ibanInput);

    let bankInput = new AonNewInput();
    bankInput.id = this.BANK;
    bankInput.title = MSG.BANK;
    bankInput.readonly = true;
    bankInput.value = this.bank.getBankAccount().getBank();
    infoBank.appendChild(bankInput);

    let bicInput = new AonNewInput();
    bicInput.id = this.BIC;
    bicInput.title = MSG.BIC_SWIFT;
    bicInput.readonly = this.isReadonly();
    bicInput.value = this.bank.getBic();
    bicInput.addEventListener(EVENT.CHANGE, () => {
      this.bank.setBic(bicInput.value);
      this.dispatchEvent(new Event(EVENT.CHANGE));
    });

    infoBank.appendChild(bicInput);
  }

  isReadonly() {
    return this.hasAttribute(CONSTANT.READONLY) && this.getAttribute(CONSTANT.READONLY)
      && CONSTANT.FALSE !== this.getAttribute(CONSTANT.READONLY);
  }

  getBank() {
    return this.bank;
  }

  setBank(bank) {
    this.bank =  bank;
  }
}

if(!window.customElements.get(TAG.AON_IBAN)){
	window.customElements.define(TAG.AON_IBAN, AonIban);
}
