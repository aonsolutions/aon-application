import { AonSelect } from '../../../../components/aon-select.js';
import { AonDate } from '../../../../components/aon-date.js';
import { AonElement } from '../../../../components/AonElement.js';
import { EVENT, MSG } from '../../../../environments/environments.js';
import { getItems, updateRegistryItem } from '../../../../services/productService.js';
import { saveRegistryItem } from '../../../../services/productService.js';
import { Customer } from '../../../../models/registry/Customer.js';
import { Item } from '../../../../models/product/Item.js';
import { BookingItemStatus, RegistryItemStatus } from '../../../../models/enums.js';
import { AonDateUtils } from '../../../utils/AonDateUtils.js';

export class AonBookingItemAdd extends AonElement {
	DIV;
	ITEM_SELECT;
	STATUS_SELECT
	START_DATE_INPUT
	END_DATE_INPUT;

	_items  = [];
	_item = null;
	_customers = [];
	_status    = null;
	_startDate = null;
	_endDate = null;

	_selectedRItem;

	setItems(items) {
		this._items = [];
		items.forEach(item => this.addItem(item));
	}

	getItems() {
		return this._items || [];
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

	setItem(item) {
		this._item = item;
	}

	getItem() {
		return this._item;
	}

	setSelectedRItem(selectedRItem) {
		this._selectedRItem = selectedRItem;
	}

	getSelectedRItem() {
		return this._selectedRItem;
	}

	addItem(item) {
		let items = this.getItems();
		const isSome = items.some(({id}) => id == item.id);

		if(!isSome) {
			items.push( new Item(item) );
		}
	}

	removeItem(item) {
		this.setItems(
			this.getItems().filter(({id}) => id !== item.id)
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
		this.id = this.id || "aonBookingItem";
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
		if (this.getSelectedRItem() && this.getSelectedRItem().start_date) {
			this.START_DATE_INPUT.setDate(this.getSelectedRItem().start_date);
			this.setStartDate(this.START_DATE_INPUT.value);
		}
		this.START_DATE_INPUT.addEventListener("change", event => {
			this.setStartDate(this.START_DATE_INPUT.value);
		});

		this.END_DATE_INPUT = new AonDate();
		this.END_DATE_INPUT.id = this.id+"endDateInput";
		this.END_DATE_INPUT.title = MSG.END_DATE;
		this.DIV.appendChild(this.END_DATE_INPUT);
		if (this.getSelectedRItem() && this.getSelectedRItem().end_date) {
			this.END_DATE_INPUT.setDate(this.getSelectedRItem().end_date);
			this.setEndDate(this.END_DATE_INPUT.value);
		}
		this.END_DATE_INPUT.addEventListener("change", event => {
			this.setEndDate(this.END_DATE_INPUT.value);
		});

		this.buildSelectStatus();
		if (this.getSelectedRItem() && this.getSelectedRItem().bookingStatus) {
			this.STATUS_SELECT.value = this.getSelectedRItem().bookingStatus;
		}
	}

	buildSelectItem(){
		this.ITEM_SELECT = new AonSelect();
		this.ITEM_SELECT.title = MSG.PRODUCTS;
		this.ITEM_SELECT.id = this.id+"item";
		this.ITEM_SELECT.autocomplete = true;
		this.ITEM_SELECT.default = true;
		if (!this.getSelectedRItem()) {
			this.ITEM_SELECT.multiple = true;
		}
		this.DIV.appendChild(this.ITEM_SELECT);

		const params = {page:1, perPage:20};
		let timeOut = null;
		let firstTime = true;

		const buildItems = (params) => {
			this.ITEM_SELECT.loading(true);
			getItems(params).then(opts => 
				this.ITEM_SELECT.setOptionsBuild( 
					opts.map(p=> ({...p, value: p.id})) 
				)
			)
			.finally(()=> {
				this.ITEM_SELECT.loading(false);
				if (firstTime) {
					firstTime = false;
					if (this.getSelectedRItem() && this.getSelectedRItem().item && this.getSelectedRItem().item.id) {
						this.ITEM_SELECT.value = this.getSelectedRItem().item.id;
						this.setItem(this.ITEM_SELECT.value);
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
			console.log("seleccion")
            // if(detail && detail.option){
            //     if(detail.add){ // add
            //     } else { // remove
            //     }
            // } else {
				this.setItems(selectable);
				this.setItem(value);
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
		for(let status in BookingItemStatus) {
			options.push({name: BookingItemStatus[status], value:status});
		}

		this.STATUS_SELECT.setOptions(options);

		this.DIV.appendChild(this.STATUS_SELECT);
		if (this.getSelectedRItem()) {
			this.STATUS_SELECT.value = this.getSelectedRItem().status;
		}

		this.STATUS_SELECT.addEventListener(EVENT.CHANGE, ()=>{
			this.setStatus(this.STATUS_SELECT.value);
		});
		
	}

	async save(remove){
		let error = false;
		let params = {
			type: "BOOKING",
			items: this.getItems(),
			customers: this.getCustomers(),
			start_date: this.getStartDate(),
			end_date: this.getEndDate(),
		}
		if (this.getStatus()) {
			params.bookingStatus = this.getStatus();
		}
		
		if (!this.getSelectedRItem()) {
			error = this.checkError(params, "save", remove);
			if (!error) {
				await saveRegistryItem(params);
			}
		} else {
			params.ritem = this.getSelectedRItem();
			params.item = this.getItem();
			error = this.checkError(params, "update", remove);
			if (!error) {
				await updateRegistryItem(params);
			}
		}
		
		if (!error) {
			this.showMessage();
		}
	}

	async remove(id){
		if(id){
			this.getItems().filter(r=> r.id === id).forEach(item=> item.remove());
		} else {
			this.getItems().forEach(item=> item.remove());
		}

		await this.save(true);
	}

	checkError(params, type, remove) {
		if (type == "update") {
			if (!params.item) {
				this.showMessageError(MSG.PRODUCT_NOT_EMPTY);
				return true;
			} else if (!remove && !params.bookingStatus) {
				this.showMessageError(MSG.STATUS_NOT_EMPTY);
				return true;
			}
		} else {
			if (!remove && !params.bookingStatus) {
				this.showMessageError(MSG.PRODUCT_NOT_EMPTY);
				return true;
			} else if(!remove && (!this.getItems() || this.getItems().length === 0)) {
				this.showMessageError(MSG.PRODUCT_MUST_BE_SELECTED);
				return true;
			}
		}
		return false;
	}
}

if(!window.customElements.get("aon-booking-item-add")){
	window.customElements.define("aon-booking-item-add", AonBookingItemAdd);
}