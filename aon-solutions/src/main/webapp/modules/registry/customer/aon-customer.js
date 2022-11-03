import { AonReg } from '../aon-reg.js';
import { EVENT, MSG, TAG } from '../../../environments/environments.js'; 
import { AonCard } from '../../../components/aon-card.js';
import { AonSelect } from '../../../components/aon-select.js';
import { AonSwitch } from '../../../components/aon-switch.js';
import { AonBasicTable } from '../../../components/aon-basic-table.js';
import { Transactions } from '../../../services/transaction.js';
import { Customer } from '../../../models/registry/Customer.js';
import { saveCustomer } from '../../../services/registryService.js';
import { AonCustomerList } from './aon-customer-list.js';
import { getScopes } from '../../../services/documentalService.js';

export class AonCustomer extends AonReg {

	saveBool;

	connectedCallback () {
		this.customerInitialize();
		this.initialize();
		this.build();
  	}
	
	customerInitialize() {
		this.type = "customer";
		this.saveBool = true;
		this.options = [
			{ title: MSG.GENERAL_DATA, fn: () => this.buildGeneralData()},
			{ title: MSG.BANK_DATA, fn: () => this.buildBankData()},
			{ title: MSG.ADDITIONAL_DATA, fn: () => this.buildDataAdditional()},
		];

		if(this.isBeta()){
			this.options.push({ title: "Expedientes", fn: () => this.buildExpedienteData()});
		}
	}

	buildDataAdditional(){
		let parent = this.getElement(this.DIV);
		this.clearElement(parent);

		this.buildGeneralInformation(parent);
		this.buildFiscalData(parent);
	}

	buildGeneralInformation(parent) {
		let card = new AonCard();
		card.id = "cardAdditionalInformation";
		card.title = MSG.ADDITIONAL_INFORMATION;
		card.style.width = '50%';
		parent.appendChild(card);

		let div = this.createElement(TAG.DIV);
		card.setContent(div);

		let table = new AonBasicTable();
		card.id = "aonTableGeneralInformation";
		div.appendChild(table);

		table.addRow();

		let scope = new AonSelect()
		scope.id = "selectScope";
		scope.title = MSG.SCOPE;
		scope.autocomplete = true;
		scope.addEventListener(EVENT.SELECT, () => {
			this.registry.setScope(scope.getDetail());
			if(this.autosave) this.save();
		});

		table.addCell(scope);

		table.addRow();

		let divSegment = this.createElement(TAG.DIV);
		table.addCell(divSegment);
		this.buildSegments(divSegment);

		getScopes()
		.then(scopes=>{
			const registryScope = this.registry.getScope();
			const scopeId = registryScope && registryScope.id ? registryScope.id : null;

			scope.setOptions(scopes.map(c=> ({...c, value: c.id})) );

			if(scopeId){
				scope.value = scopeId;
			}
		});
	}	

	buildFiscalData(parent) {
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
			this.registry.setSurcharge(surcharge.isChecked());
			if(this.autosave) this.save();
		});
		table.addCell(surcharge, 1);

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
		let list = new AonCustomerList();
		list.id = this.getApplication().id + 'CustomerList';
		this.getApplication().setContent(list);
	}

	save() {
		if(this.saveBool) {
			let medias = this.emails.concat(this.phones).concat(this.webs);
			this.registry.setMedia(medias);
			this.saveBool = false;
			saveCustomer(this.registry).then(registry => {
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

	setCustomer(customer) {
		this.registry = new Customer(customer);
	}
}

if(!window.customElements.get(TAG.AON_CUSTOMER)){
	window.customElements.define(TAG.AON_CUSTOMER, AonCustomer);
}