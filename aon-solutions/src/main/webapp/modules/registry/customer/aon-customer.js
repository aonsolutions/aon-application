import { AonReg } from '../aon-reg.js';
import { COLORS, CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../../environments/environments.js'; 
import { AonCard } from '../../../components/aon-card.js';
import { AonSelect } from '../../../components/aon-select.js';
import { AonSwitch } from '../../../components/aon-switch.js';
import { AonBasicTable } from '../../../components/aon-basic-table.js';
import { Transactions } from '../../../services/transaction.js';
import { Customer } from '../../../models/registry/Customer.js';
import { getRelationShip, saveRelationShip, removeRelationShip, saveCustomer } from '../../../services/registryService.js';
import { AonCustomerList } from './aon-customer-list.js';
import { getScopes } from '../../../services/documentalService.js';
import { getDomainCompanies, saveCompany } from '../../../services/companyService.js';
import { AonItemList } from '../target/item/aon-item-list.js';
import { AonProjectList } from '../../project/aon-project-list.js';

export class AonCustomer extends AonReg {

	saveBool;
	ENTERPRISE_LINKED;
	connectedCallback () {
		this.customerInitialize();
		this.initialize();
		this.build();
	}
	
	customerInitialize() {
		this.saveBool = true;
		this.type = "customer";
		this.ENTERPRISE_LINKED = "enterpriseLinked";
		this.options = [
			{ title: MSG.GENERAL_DATA, fn: () => this.buildGeneralData()},
			{ title: MSG.BANK_DATA, fn: () => this.buildBankData()},
			{ title: MSG.ADDITIONAL_DATA, fn: () => this.buildDataAdditional()},
			{ title: "Expedientes", fn: () => this.buildExpedienteData()},
		];


		if(this.isSig() || this.isLocal()){
			this.options.push({ title: MSG.PRODUCTS, fn: () => this.buildItemData()});
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

	buildEnterpriseLinked(){
		if( (this.isBeta() || this.isSig()) ){
			const card = this.getElement(this.GENERAL_CARD);
		
			let divOne = this.getElement(this.ENTERPRISE_LINKED);
	
			if(!divOne){
				divOne =  this.createElement(TAG.DIV);
				divOne.id = this.ENTERPRISE_LINKED;
				card.addSection2(divOne);
			}
	
			getRelationShip({registry:this.registry.getId(), parentId:this.registry.getDomain().getParentId(), document: this.registry.getDocument()})
			.then(resp=>{
				this.buildEnterpriseLinkedView(resp);
			})
			.catch((err)=>{
				this.showError(err);
			});
		}
	}

	buildEnterpriseLinkedView(resp){
		const entepriseLinked = this.getElement(this.ENTERPRISE_LINKED);
		entepriseLinked.innerHTML = "";

		const {rrelationship, companies} = resp;
		
		const link = rrelationship && rrelationship.id;

		const color = link ? CSS.variable(COLORS.ONLINE_GREEN) : COLORS.ORANGE;
	
		let main = this.createElement(TAG.DIV);
		main.title = "Vinculo con empresa " + (link ? `(${rrelationship.comments})` : "(No existe)");
		main.style.display = "flex";
		main.style.columnGap = "5px";
		main.style.border = "1px solid";
		main.style.borderColor = "lightgray";
		main.style.borderRadius = "10px";
		main.style.padding = "4px";
		main.style.cursor = "pointer";
		main.style.marginRight = "6px";
		entepriseLinked.appendChild(main);

		let statusBox = this.createElement(TAG.DIV);
		statusBox.className = CONSTANT.MATERIAL_ICONS;
		statusBox.style.fontSize = "18px";
		statusBox.style.color    = color;
		statusBox.innerText      = link ? MATERIAL_ICONS.LINK : MATERIAL_ICONS.LINK_OFF;
		main.appendChild(statusBox);

		let statusText = this.createElement(TAG.DIV);
		statusText.innerText = link ? "Vinculado" : "Desvinculado";
		statusText.style.fontSize = "14px";
		statusText.style.fontWeight = "500";
		statusText.style.color = "#5f6368";
		main.appendChild(statusText);

		let iconArrowDown = this.createElement(TAG.DIV);
		iconArrowDown.style.fontSize = "18px";
		iconArrowDown.className = CONSTANT.MATERIAL_ICONS;
		iconArrowDown.innerText = MATERIAL_ICONS.KEYBOARD_ARROW_DOWN;

		if(link || !companies.length){
			main.appendChild(iconArrowDown);
		}

		if(link){
			main.addEventListener(EVENT.CLICK, () => {
				this.getOptionsLinked(iconArrowDown, rrelationship);
			});	
		} 
		else {
			statusText.style.marginRight = "5px";
			main.addEventListener(EVENT.CLICK, () => {
				const countCompany = companies.length;

				if(countCompany === 1){
					this.saveRegistryRelationship(companies[0]);
				} else if(countCompany > 1){
					this.openDialogCompany(companies);
				} else {
					this.getOptionsLinked(iconArrowDown);
				}
			});
		}
	}


	//EXPEDIENTE
	buildExpedienteData() {
		let main = this.getElement(this.DIV);
		this.clearElement(main);

		let registryId = this.registry.getId();
		
		if(registryId){
			let aonProjectList = new AonProjectList();
			aonProjectList.style.width = "100%";
			aonProjectList.registry = this.registry;
			aonProjectList.filter = { page: 1, perPage: 500, registry:registryId};
			main.appendChild(aonProjectList);
		}
	}

	//ITEMS PRODUCTS
	buildItemData() {
		let main = this.getElement(this.DIV);
		this.clearElement(main);

		let registryId = this.registry.getId();
		
		if(registryId){
			let aonItemList = new AonItemList();
			aonItemList.style.width = "100%";
			aonItemList.registry = this.registry;
			aonItemList.filter = { page: 1, perPage: 200, registry:registryId};
			main.appendChild(aonItemList);
		}
	}

	getOptionsLinked(element, rrelationship=undefined){
		let options = [];

		if(rrelationship){
			options.push({ 
				name: "Abrir", 
				value:"OPEN",
				icon:  MATERIAL_ICONS.OPEN_IN_NEW, 
				fn:()=> {
					if(rrelationship.comments){
						window.open("https://"+ rrelationship.comments);
					}
				}
			},
			{ 
				name: "Desvincular", 
				value: "UNLINK",
				icon: MATERIAL_ICONS.LINK_OFF, 
				fn:()=> {

					this.getApplication().startLoading();

					removeRelationShip(rrelationship)
					.then(()=> {
						this.showMessage();
						this.buildEnterpriseLinked();
					})
					.catch((err)=> this.showError(err))
					.finally(()=>{
						this.getApplication().stopLoading();
					});
				}
			});
		} else {
			options.push({ 
				name: "Vincular con una existente", 
				value: "LINK",
				icon:  MATERIAL_ICONS.LINK, 
				fn:()=> {
					this.openDialogCompany();
				}
			},
			{ 
				name: "Crear nueva empresa", 
				value: "ENTERPRISE_NEW",
				icon: MATERIAL_ICONS.OPEN_IN_NEW, 
				fn:()=> {
					this.getApplication().confirmDialog(MSG.REGISTER, `Desea registrar y vincular a ${this.registry.getName()} ?`, () => {
						this.getApplication().startLoading();
						saveCompany({...this.registry, id:null})
						.then((company)=>{
							console.log("company", company);
							this.saveRegistryRelationship(company);
						})
						.catch(err=>{
							this.showError(err);
						})
						.finally(()=>{
							this.getApplication().stopLoading();	
						});
					});
				}
			});
		}

		const top = element.getBoundingClientRect().top + 24;
		const left = element.getBoundingClientRect().left + 3;
		let d = this.getApplication().getOptionDialog();
		d.setMenuOptions(options, top, left);
		d.open();
	}

	openDialogCompany(companies=[]){
		const dialog = this.getApplication().getDialog();
		dialog.clear();
	
		if(this.isMobile()) {
			dialog.type = "fullscreen";
		} else  {
			dialog.width = "30%";
		}
	
		dialog.setTitle(companies.length ? "Sugerencias para el vinculo": MSG.ENTERPRISE);
	
		let div = document.createElement(TAG.DIV);
		div.style.display = "flex";
		div.style.flexDirection = "column";
		div.style.marginTop = "10px";
		dialog.setContent(div);

		let selectCompany   = new AonSelect();
		selectCompany.id    = "selectCompany";
		selectCompany.title = MSG.COMPANY;
		selectCompany.autocomplete = true;
		div.appendChild(selectCompany);

		if(companies.length){
			let timeOut = null;

			selectCompany.setOptions(companies.map(c=> ({...c, value:c.id})));
			
			selectCompany.addEventListener(EVENT.INPUT, async({target})=>{
				clearTimeout(timeOut);
				const value = target.value;
				if(value.length > 2 ){
					timeOut = setTimeout(async() =>{
						selectCompany.loading(true);
						const cs = await this.getDomainCompanies(value);
						selectCompany.setOptions(cs);
						selectCompany.loading(false);
					}, 300);
				}
			});
		} else {
			selectCompany.loading(true);
			this.getDomainCompanies()
			.then(companies=>{
				selectCompany.setOptions(companies);
			})
			.finally(()=>{
				selectCompany.loading(false);
			})
		}

		dialog.addSendAction(()=>{
			if(selectCompany.value){
				this.saveRegistryRelationship(selectCompany.getDetail());
				dialog.close();
			}
		}, MSG.LINK);

		dialog.open();
	}   

	async getDomainCompanies(value){
		let params = {parentId: this.registry.getDomain().getParentId()};
		if(value) params.value = value;

		let result = await getDomainCompanies(params);

		return result.filter(company => company && company.domain && company.domain.id!=this.registry.getDomain().getId())
		.map( c=> ({...c, value: c.id}));
	}

	saveRegistryRelationship(company){
		this.getApplication().startLoading();

		let relationship = {
			domain: this.registry.getDomain(),
			registry: this.registry.getId(),
			related_registry: company.id,
			comments: (company.domain && company.domain.name ? company.domain.name : "")
		}

		saveRelationShip(relationship)
		.then(()=> {
			this.showMessage();
			this.buildEnterpriseLinked();
		})
		.catch((err)=> this.showError(err))
		.finally(()=>{
			this.getApplication().stopLoading();
		});
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
			})
			.catch(error => {
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