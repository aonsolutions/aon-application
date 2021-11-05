import { AonReg } from '../aon-reg.js';
import { EVENT, MSG, TAG } from '../../../environments/environments.js'; 
import { AonCard } from '../../../components/aon-card.js';
import { AonSelect } from '../../../components/aon-select.js';
import { AonSwitch } from '../../../components/aon-switch.js';
import { AonBasicTable } from '../../../components/aon-basic-table.js';
import { Transactions } from '../../../services/transaction.js';
import { Creditor } from '../../../models/registry/Creditor.js';
import { AonCreditorList } from './aon-creditor-list.js';
import { saveCreditor } from '../../../services/registryService.js';

export class AonCreditor extends AonReg {

	saveBool;

	connectedCallback () {
		this.creditorInitialize();
		this.initialize();
		this.build();

  	}
	
	creditorInitialize() {
		this.saveBool = true;
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

		let vatAccrualPayment = new AonSwitch();
		vatAccrualPayment.id = this.FISCAL_VAT_ACCRUAL_PAYMENT;
		vatAccrualPayment.title = MSG.VAT_ACCRUAL_PAYMENT;
		vatAccrualPayment.checked = this.registry.isVatAccrualPayment();
		vatAccrualPayment.addEventListener(EVENT.CHANGE, () => {
			this.registry.setVatAccrualPayment(vatAccrualPayment.isChecked());
			if(this.autosave) this.save();
		});
		table.addCell(vatAccrualPayment, 1);

		let withholding = new AonSwitch();
		withholding.id = this.FISCAL_WITHHOLDING;
		withholding.title = MSG.IRPF;
		withholding.checked = this.registry.isWithholding();
		withholding.addEventListener(EVENT.CHANGE, () => {
			this.registry.setWithholding(withholding.isChecked());
			if(this.autosave) this.save();
		});
		table.addCell(withholding, 1);
	}	

	back() {
		let list = new AonCreditorList();
		list.id = this.getApplication().id + 'CreditorList';
		this.getApplication().setContent(list);
	}

	save() {
		if(this.saveBool) {
			this.saveBool = false;
			let medias = this.emails.concat(this.phones).concat(this.webs);
			this.registry.setMedia(medias);
	
			saveCreditor(this.registry).then(registry => {
				this.registry.id = registry.id;
				this.saveBool = true;
				this.showToast({
					type: 'success',
					 message: 'Datos Guardados Correctamente'
				 });
			}).catch(error => {
				this.saveBool = true;
				this.showToast(error);
			 });
		}
	}

	setCreditor(creditor) {
		this.registry = new Creditor(creditor);
	}
}

if(!window.customElements.get(TAG.AON_CREDITOR)){
	window.customElements.define(TAG.AON_CREDITOR, AonCreditor);
}