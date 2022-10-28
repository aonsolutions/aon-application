import { AonReg } from '../aon-reg.js';
import { MSG } from '../../../environments/environments.js'; 
import { AonTaskHolderList } from './aon-taskholder-list.js';
import { saveTastHolder } from '../../../services/taskHolderService.js';
import { TaskHolder } from '../../../models/registry/TaskHolder.js';

export class AonTaskHolder extends AonReg {

	saveBool;

	connectedCallback () {
		this.taskHolderInitialize();
		this.initialize();
		this.build();

  	}
	
	taskHolderInitialize() {
		this.saveBool = true;
		this.options = [
			{ title: MSG.GENERAL_DATA, fn: () => this.buildGeneralData()},
			// { title: MSG.FISCAL_DATA, fn: () => this.buildAditionalData()}
		];
	}

	// buildAditionalData() {
	// 	let parent = this.getElement(this.DIV);
	// 	this.clearElement(parent);

	// 	let card = new AonCard();
	// 	card.id = this.FISCAL_CARD;
	// 	card.title = MSG.FISCAL_DATA;
	// 	card.style.width = '50%';
	// 	parent.appendChild(card);

	// 	let div = this.createElement(TAG.DIV);
	// 	card.setContent(div);

	// 	let table = new AonBasicTable();
	// 	table.id = this.FISCAL_TABLE;
	// 	div.appendChild(table);

	// 	table.addRow();

	// 	let transaction = new AonSelect()
	// 	transaction.id = this.FISCAL_TRANSACTION;
	// 	transaction.title = MSG.TRANSACTION_TYPE;
	// 	transaction.options = JSON.stringify(Transactions);
	// 	transaction.value = this.registry.getTransaction();
	// 	transaction.addEventListener(EVENT.SELECT, () => {
	// 		this.registry.setTransaction(transaction.value);
	// 		if(this.autosave) this.save();
	// 	});
	// 	table.addCell(transaction, 2);

	// 	table.addRow();

	// 	let vatAccrualPayment = new AonSwitch();
	// 	vatAccrualPayment.id = this.FISCAL_VAT_ACCRUAL_PAYMENT;
	// 	vatAccrualPayment.title = MSG.VAT_ACCRUAL_PAYMENT;
	// 	vatAccrualPayment.checked = this.registry.isVatAccrualPayment();
	// 	vatAccrualPayment.addEventListener(EVENT.CHANGE, () => {
	// 		this.registry.setVatAccrualPayment(vatAccrualPayment.isChecked());
	// 		if(this.autosave) this.save();
	// 	});
	// 	table.addCell(vatAccrualPayment, 1);

	// 	let withholding = new AonSwitch();
	// 	withholding.id = this.FISCAL_WITHHOLDING;
	// 	withholding.title = MSG.IRPF;
	// 	withholding.checked = this.registry.isWithholding();
	// 	withholding.addEventListener(EVENT.CHANGE, () => {
	// 		this.registry.setWithholding(withholding.isChecked());
	// 		if(this.autosave) this.save();
	// 	});
	// 	table.addCell(withholding, 1);
	// }	

	back() {
		let list = new AonTaskHolderList();
		list.id = this.getApplication().id + 'TaskHolderList';
		this.getApplication().setContent(list);
	}

	save() {
		if(this.saveBool) {
			this.saveBool = false;
			let medias = this.emails.concat(this.phones).concat(this.webs);
			this.registry.setMedia(medias);
	
			saveTastHolder(this.registry).then(registry => {
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

	setTaskHolder(taskholder) {
		this.registry = new TaskHolder(taskholder);
	}
}

if(!window.customElements.get("aon-taskholder")){
	window.customElements.define("aon-taskholder", AonTaskHolder);
}