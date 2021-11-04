import { AonReg } from '../aon-reg.js';
import { EVENT, MSG, TAG } from '../../../environments/environments.js'; 
import { AonCard } from '../../../components/aon-card.js';
import { AonSelect } from '../../../components/aon-select.js';
import { AonSwitch } from '../../../components/aon-switch.js';
import { AonBasicTable } from '../../../components/aon-basic-table.js';
import { Transactions } from '../../../services/transaction.js';
import { Supplier } from '../../../models/registry/Supplier.js';
import { AonSupplierList } from './aon-supplier-list.js';
import { saveSupplier } from '../../../services/registryService.js';

export class AonSupplier extends AonReg {

	connectedCallback () {
		this.supplierInitialize();
		this.initialize();
		this.build();

  	}
	
	supplierInitialize() {
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

		table.addRow();

		let withholdingFarmer = new AonSwitch();
		withholdingFarmer.id = this.FISCAL_WITHHOLDING_FARMER;
		withholdingFarmer.title = MSG.WITHHOLDING_FARMER;
		withholdingFarmer.checked = this.registry.isWithholdingFarmer();
		withholdingFarmer.addEventListener(EVENT.CHANGE, () => {
			this.registry.setWithholdingFarmer(withholdingFarmer.isChecked());
			if(this.autosave) this.save();
		});
		table.addCell(withholdingFarmer, 2);
	}	

	back() {
		let list = new AonSupplierList();
		list.id = this.getApplication().id + 'SupplierList';
		this.getApplication().setContent(list);
	}

	save() {
		let medias = this.emails.concat(this.phones).concat(this.webs);
		this.registry.setMedia(medias);

		saveSupplier(this.registry).then(registry => {
			this.registry.id = registry.id;
			this.showToast({
				type: 'success',
	 			message: 'Datos Guardados Correctamente'
	 		});
		}).catch(error => {
	 		this.showToast(error);
	 	});
	}

	setSupplier(supplier) {
		this.registry = new Supplier(supplier);
	}
}

if(!window.customElements.get(TAG.AON_SUPPLIER)){
	window.customElements.define(TAG.AON_SUPPLIER, AonSupplier);
}