import { AonSelect } from '../../../components/aon-select.js';
import { AonDate } from '../../../components/aon-date.js';
import { AonElement } from '../../../components/AonElement.js';
import { EVENT, MSG } from '../../../environments/environments.js';
import { getItems, updateRegistryItem } from '../../../services/productService.js';
import { saveRegistryItem } from '../../../services/productService.js';
import { Customer } from '../../../models/registry/Customer.js';
import { Item } from '../../../models/product/Item.js';
import { RegistryItemStatus, RegistrySellerStatus, RegistrySellerType } from '../../../models/enums.js';
import { deleteRegistrySeller, getSellers, saveRsellers, updateRegistrySeller } from '../../../services/commercialService.js';

export class AonSellerAdd extends AonElement {
	DIV;
	ITEM_SELECT;
	STATUS_SELECT
	START_DATE_INPUT
	END_DATE_INPUT;

	_sellers  = [];
	_seller = null;
	_customers = [];
	_status    = null;
	_startDate = null;
	_endDate = null;
	_type    = null;

	_selectedRSeller;

	setSellers(items) {
		this._sellers = [];
		items.forEach(item => this.addSeller(item));
	}

	getSellers() {
		return this._sellers || [];
	}

	setCustomers(customers) {
		this._customers = [];
		customers.forEach(customer => this.addCustomer(customer));
	}

	getCustomers() {
		return this._customers || [];
	}

	setStatus(status) {
		this._status = status;
	}

	getStatus() {
		return this._status;
	}

	setType(type) {
		this._type = type;
	}

	getType() {
		return this._type;
	}

	setStartDate(startDate) {
		this._startDate = startDate;
	}

	getStartDate() {
		return this._startDate;
	}

	setEndDate(endDate) {
		this._endDate = endDate;
	}

	getEndDate() {
		return this._endDate;
	}

	setSeller(seller) {
		this._seller = seller;
	}

	getSeller() {
		return this._seller;
	}

	setSelectedRSeller(selectedRSeller) {
		this._selectedRSeller = selectedRSeller;
	}

	getSelectedRSeller() {
		return this._selectedRSeller;
	}

	addSeller(seller) {
		let items = this.getSellers();
		const isSome = items.some(({id}) => id == seller.id);

		if(!isSome) {
			items.push(seller);
		}
	}

	removeItem(item) {
		this.setSellers(
			this.getSellers().filter(({id}) => id !== item.id)
		);
	}

	addCustomer(customer) {
		let customers = this.getCustomers();
		const isSome = customers.some(item => item.id == customer.id);

		if(!isSome) {
			customers.push( new Customer(customer) );
		}
	}

	removeCustomer(customer) {
		this.setCustomers(
			this.getCustomers().filter(({id}) => id !== customer.id)
		);
	}

	constructor() {
		super();
	}

	connectedCallback () {
		this.initialize();
		this.build();
    }

	initialize() {
		this.id = this.id || "aonTargetItem";
	}

	build(){
		this.DIV = document.createElement('div');
		this.DIV.style.display = "flex";
		this.DIV.style.flexDirection = "column";
		this.appendChild(this.DIV);
		this.buildSelectItem();
		this.START_DATE_INPUT = new AonDate();
		this.START_DATE_INPUT.id = this.id+"startDateInput";
		this.START_DATE_INPUT.title = MSG.START_DATE;
		this.DIV.appendChild(this.START_DATE_INPUT);
		if (this.getSelectedRSeller() && this.getSelectedRSeller().start_date) {
			this.START_DATE_INPUT.setDate(this.getSelectedRSeller().start_date);
			this.setStartDate(this.START_DATE_INPUT.value);
		}
		this.START_DATE_INPUT.addEventListener("change", event => {
			this.setStartDate(this.START_DATE_INPUT.value);
		});

		this.END_DATE_INPUT = new AonDate();
		this.END_DATE_INPUT.id = this.id+"endDateInput";
		this.END_DATE_INPUT.title = MSG.END_DATE;
		this.DIV.appendChild(this.END_DATE_INPUT);
		if (this.getSelectedRSeller() && this.getSelectedRSeller().end_date) {
			this.END_DATE_INPUT.setDate(this.getSelectedRSeller().end_date);
			this.setEndDate(this.END_DATE_INPUT.value);
		}
		this.END_DATE_INPUT.addEventListener("change", event => {
			this.setEndDate(this.END_DATE_INPUT.value);
		});

		this.buildSelectStatus();
		this.buildSelectType();
		if (this.getSelectedRSeller() && this.getSelectedRSeller().status) {
			this.STATUS_SELECT.value = this.getSelectedRSeller().status;
		}
	}

	buildSelectItem(){
		this.ITEM_SELECT = new AonSelect();
		this.ITEM_SELECT.title = MSG.PRODUCTS;
		this.ITEM_SELECT.id = this.id+"seller";
		this.ITEM_SELECT.autocomplete = true;
		this.ITEM_SELECT.default = true;
		if (!this.getSelectedRSeller()) {
			this.ITEM_SELECT.multiple = true;
		}
		this.DIV.appendChild(this.ITEM_SELECT);

		const params = {page:1, perPage:20};
		let timeOut = null;
		let firstTime = true;

		const buildItems = (params) => {
			this.ITEM_SELECT.loading(true);
			getSellers(params).then(opts => 
				this.ITEM_SELECT.setOptionsBuild( 
					opts.map(p=> ({...p, value: p.id})) 
				)
			)
			.finally(()=> {
				this.ITEM_SELECT.loading(false);
				if (firstTime) {
					firstTime = false;
					if (this.getSelectedRSeller() && this.getSelectedRSeller().seller && this.getSelectedRSeller().seller.id) {
						this.ITEM_SELECT.value = this.getSelectedRSeller().seller.id;
						this.setSeller(this.ITEM_SELECT.value);
					}
					this.ITEM_SELECT.closeOptions();
				}
			}
			);
		}

		buildItems(params);

		
		this.ITEM_SELECT.addEventListener(EVENT.INPUT, async({target})=>{
			clearTimeout(timeOut);
			const value = target.value;
			if(value.length > 2){
				timeOut = setTimeout(async() =>{
					buildItems({...params, value});
				}, 300);
			}
		});

		this.ITEM_SELECT.addEventListener(EVENT.SELECT, ({detail}) => {
            let selectable = (this.ITEM_SELECT.getSelectable() || []);
			let value = this.ITEM_SELECT.value;
            // if(detail && detail.option){
            //     if(detail.add){ // add
            //     } else { // remove
            //     }
            // } else {
				this.setSellers(selectable);
				this.setSeller(value);
            // }
        });

	}

	buildSelectStatus() {
		this.STATUS_SELECT = new AonSelect();
		this.STATUS_SELECT.title = MSG.STATUS;
		this.STATUS_SELECT.id = this.id+"status";
		this.STATUS_SELECT.default = true;
		this.STATUS_SELECT.autocomplete = true;

		let options = [];
		for(let status in RegistrySellerStatus) {
			options.push({name: RegistrySellerStatus[status], value:status});
		}

		this.STATUS_SELECT.setOptions(options);

		this.DIV.appendChild(this.STATUS_SELECT);
		if (this.getSelectedRSeller()) {
			this.STATUS_SELECT.value = this.getSelectedRSeller().status;
		}

		this.STATUS_SELECT.addEventListener(EVENT.CHANGE, ()=>{
			this.setStatus(this.STATUS_SELECT.value);
		});
		
	}

	buildSelectType() {
		this.STATUS_SELECT = new AonSelect();
		this.STATUS_SELECT.title = MSG.TYPE;
		this.STATUS_SELECT.id = this.id+"TYPE";
		this.STATUS_SELECT.default = true;
		this.STATUS_SELECT.autocomplete = true;

		let options = [];
		for(let type in RegistrySellerType) {
			options.push({name: RegistrySellerType[type], value: type});
		}

		this.STATUS_SELECT.setOptions(options);

		this.DIV.appendChild(this.STATUS_SELECT);
		if (this.getSelectedRSeller()) {
			this.STATUS_SELECT.value = this.getSelectedRSeller().status;
		}

		this.STATUS_SELECT.addEventListener(EVENT.CHANGE, ()=>{
			this.setStatus(this.STATUS_SELECT.value);
		});
		
	}

	async save(remove){
		let error = false;
		let params = {
			type: this.getType(),
			sellers: this.getSellers(),
			customers: this.getCustomers(),
			start_date: this.getStartDate(),
			end_date: this.getEndDate(),
		}
		if (this.getStatus()) {
			params.status = this.getStatus();
		}
		
		if (!this.getSelectedRSeller()) {
			error = this.checkError(params, "save", remove);
			if (!error) {
				await saveRsellers(params);
			}
		} else {
			params.rseller = this.getSelectedRSeller();
			params.seller = this.getSeller();
			error = this.checkError(params, "update", remove);
			if (!error) {
				await updateRegistrySeller(params);
			}
		}
		
		if (!error) {
			this.showMessage();
		}
	}

	async delete(id) {
		let params = {id: id};
		await deleteRegistrySeller(params);
	}

	async remove(id){
		if(id){
			this.getSellers().filter(r=> r.id === id).forEach(item=> item.removed = true);
		} else {
			this.getSellers().forEach(item=> item.removed = true);
		}

		await this.delete(id);
	}

	checkError(params, type, remove) {
		if (type == "update") {
			if (!params.item) {
				this.showMessageError(MSG.PRODUCT_NOT_EMPTY);
				return true;
			}
		} else {
			if(!remove && (!this.getSellers() || this.getSellers().length === 0)) {
				this.showMessageError(MSG.PRODUCT_MUST_BE_SELECTED);
				return true;
			} else if(!remove && (!this.getStartDate())) {
				this.showMessageError("La fecha de inicio no puede quedar vacía");
				return true;
			}
		}
		return false;
	}
}

if(!window.customElements.get("aon-seller-add")){
	window.customElements.define("aon-seller-add", AonSellerAdd);
}