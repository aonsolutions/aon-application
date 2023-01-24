import { AonSelect } from '../../../../components/aon-select.js';
import { AonElement } from '../../../../components/AonElement.js';
import { EVENT, MSG } from '../../../../environments/environments.js';
import { getItems } from '../../../../services/productService.js';
import { saveRegistryItem } from '../../../../services/productService.js';
import { Customer } from '../../../../models/registry/Customer.js';
import { Item } from '../../../../models/product/Item.js';

export class AonTargetItemAdd extends AonElement {
	DIV;
	ITEM_SELECT;
	STATUS_SELECT;

	_items  = [];
	_customers = [];
	_status    = null;

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
		this.id = this.id || "aonTargetItem";
	}

	build(){
		this.DIV = document.createElement('div');
		this.DIV.style.display = "flex";
		this.DIV.style.flexDirection = "column";
		this.appendChild(this.DIV);
		this.buildSelectItem();
		this.buildSelectStatus();
	}

	buildSelectItem(){
		this.ITEM_SELECT = new AonSelect();
		this.ITEM_SELECT.title = MSG.PRODUCTS;
		this.ITEM_SELECT.id = this.id+"item";
		this.ITEM_SELECT.autocomplete = true;
		this.ITEM_SELECT.default = true;
		this.ITEM_SELECT.multiple = true;
		this.DIV.appendChild(this.ITEM_SELECT);

		const params = {page:1, perPage:20};
		let timeOut = null;

		const buildItems = (params) => {
			this.ITEM_SELECT.loading(true);
			getItems(params).then(opts => 
				this.ITEM_SELECT.setOptionsBuild( 
					opts.map(p=> ({...p, value: p.id})) 
				)
			)
			.finally(()=> 
				this.ITEM_SELECT.loading(false)
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

            // if(detail && detail.option){
            //     if(detail.add){ // add
            //     } else { // remove
            //     }
            // } else {
                this.setItems(selectable);
            // }
        });

	}

	buildSelectStatus(){
		this.STATUS_SELECT = new AonSelect();
		this.STATUS_SELECT.title = MSG.STATUS;
		this.STATUS_SELECT.id = this.id+"status";
		this.STATUS_SELECT.default = true;
		this.STATUS_SELECT.autocomplete = true;

		let options = [
			{name:MSG.ACTIVE, value:"ACTIVE"},
			{name:"Interesado", value:"INTERESTED"},
			{name:"Rechazado", value:"REFUSED"},
		];

		this.STATUS_SELECT.setOptions(options);

		this.DIV.appendChild(this.STATUS_SELECT);

		this.STATUS_SELECT.addEventListener(EVENT.CHANGE, ()=>{
			this.setStatus(this.STATUS_SELECT.value);
		})
		
	}

	async save(){
		let params = {
			status: this.getStatus(),
			items: this.getItems(),
			customers: this.getCustomers()
		}

		await saveRegistryItem(params);
		
		this.showMessage();
	}

	async remove(id){
		if(id){
			this.getItems().filter(r=> r.id === id).forEach(item=> item.remove());
		} else {
			this.getItems().forEach(item=> item.remove());
		}

		await this.save();
	}
}

if(!window.customElements.get("aon-target-item-add")){
	window.customElements.define("aon-target-item-add", AonTargetItemAdd);
}