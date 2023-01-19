import { AonSelect } from '../../../../components/aon-select.js';
import { AonElement } from '../../../../components/AonElement.js';
import { EVENT, MSG } from '../../../../environments/environments.js';
import { getProducts } from '../../../../services/productService.js';
import { Product } from '../../../../models/product/Product.js';
import { Customer } from '../../../../models/registry/Customer.js';

export class AonTargetProductAdd extends AonElement {
	DIV;
	PRODUCT_SELECT;
	STATUS_SELECT;

	_products  = [];
	_customers = [];
	_status    = null;

	setProducts(products) {
		this._products = [];
		products.forEach(product => this.addProduct(product));
	}

	getProducts() {
		return this._products || [];
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

	addProduct(product) {
		let products = this.getProducts();
		const isSome = products.some(item => item.id == product.id);

		if(!isSome) {
			products.push(new Product(product));
		}
	}

	removeProduct(product) {
		this.setProducts(
			this.getProducts().filter(({id}) => id !== product.id)
		);
	}

	addCustomer(customer) {
		let customers = this.getCustomers();
		const isSome = customers.some(item => item.id == customer.id);

		if(!isSome) {
			customers.push(new Customer(customer));
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
		this.id = this.id || "aonTargetProduct";
	}


	build(){
		this.DIV = document.createElement('div');
		this.DIV.style.display = "flex";
		this.DIV.style.flexDirection = "column";
		this.appendChild(this.DIV);
		this.buildSelectProduct();
		this.buildSelectStatus();
	}

	buildSelectProduct(){
		this.PRODUCT_SELECT = new AonSelect();
		this.PRODUCT_SELECT.title = MSG.PRODUCTS;
		this.PRODUCT_SELECT.id = this.id+"product";
		this.PRODUCT_SELECT.autocomplete = true;
		this.PRODUCT_SELECT.default = true;
		this.PRODUCT_SELECT.multiple = true;
		this.DIV.appendChild(this.PRODUCT_SELECT);

		const params = {page:1, perPage:20};
		let timeOut = null;

		const productsBuild = (params) => 
			getProducts(params).then(opts => 
				this.PRODUCT_SELECT.setOptionsBuild( 
					opts.map(p=> ({...p, value: p.id})) 
				)
			);
		

		productsBuild(params);
		
		this.PRODUCT_SELECT.addEventListener(EVENT.INPUT, async({target})=>{
			clearTimeout(timeOut);
			const value = target.value;
			if(value.length > 2){
				timeOut = setTimeout(async() =>{
					productsBuild({...params, value});
				}, 300);
			}
		});

		this.PRODUCT_SELECT.addEventListener(EVENT.SELECT, ({detail}) => {
            let selectable = (this.PRODUCT_SELECT.getSelectable() || []);

            // if(detail && detail.option){
            //     if(detail.add){ // add
            //     } else { // remove
            //     }
            // } else {
                this.setProducts(selectable);
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
			products: this.getProducts(),
			customers: this.getCustomers()
		}
		
		console.log(params);
		// saveRegistryItem
	}

}

if(!window.customElements.get("aon-target-product-add")){
	window.customElements.define("aon-target-product-add", AonTargetProductAdd);
}