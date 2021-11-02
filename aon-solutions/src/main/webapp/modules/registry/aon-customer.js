import { AonReg } from './aon-reg.js';
import { EVENT, MSG, TAG } from '../../environments/environments.js'; 
import { AonCard } from '../../components/aon-card.js';
import { AonSelect } from '../../components/aon-select.js';
import { AonInput } from '../../components/aon-input.js';
import { AonSwitch } from '../../components/aon-switch.js';
import { AonBasicTable } from '../../components/aon-basic-table.js';
import { Transactions } from '../../services/transaction.js';
import { Customer } from '../../models/registry/Customer.js';

export class AonCustomer extends AonReg {

	connectedCallback () {
		this.customerInitialize();
		this.initialize();
		this.build();

  	}
	
	customerInitialize() {
		this.options = [
			{ title: MSG.GENERAL_DATA, fn: () => this.buildGeneralData()},
			{ title: MSG.BANK_DATA, fn: () => this.buildBankData()},
			{ title: MSG.FISCAL_DATA, fn: () => this.buildFiscalData()}
		];
	}

	buildFiscalData() {
		let parent = this.getElement(this.DIV);
		this.clearElement(parent);

		let card = new AonCard();
		card.id = this.FISCAL_CARD;
		card.title = MSG.FISCAL_DATA;
		card.style.width = '50%';
		parent.appendChild(card);

		let div = this.createElement(TAG.DIV);
		card.setContent(div);

		let table = new AonBasicTable();
		table.id = this.FISCAL_TABLE;
		div.appendChild(table);

		table.addRow();

		let transaction = new AonSelect()
		transaction.id = this.FISCAL_TRANSACTION;
		transaction.title = MSG.TRANSACTION_TYPE;
		transaction.options = JSON.stringify(Transactions);
		transaction.value = this.registry.getTransaction();
		transaction.addEventListener(EVENT.SELECT, () => {
			this.registry.setTransaction(transaction.value);
			if(this.autosave) this.save();
		});
		table.addCell(transaction, 2);

		table.addRow();

		let surcharge = new AonSwitch();
		surcharge.id = this.FISCAL_SURCHARGE;
		surcharge.title = MSG.SURCHARGE_RE;
		surcharge.checked = this.registry.isSurcharge();
		surcharge.addEventListener(EVENT.CHANGE, () => {
			this.registry.setSurcharge(surcharge.checked);
			if(this.autosave) this.save();
		});
		table.addCell(surcharge, 1);

		let withholding = new AonSwitch();
		withholding.id = this.FISCAL_SURCHARGE;
		withholding.title = MSG.IRPF;
		withholding.checked = this.registry.isWithholding();
		withholding.addEventListener(EVENT.CHANGE, () => {
			this.registry.setWithholding(withholding.checked);
			if(this.autosave) this.save();
		});
		table.addCell(withholding, 1);
	}	

	setCustomer(customer) {
		this.registry = new Customer(customer);
	}
}

if(!window.customElements.get(TAG.AON_CUSTOMER)){
	window.customElements.define(TAG.AON_CUSTOMER, AonCustomer);
}