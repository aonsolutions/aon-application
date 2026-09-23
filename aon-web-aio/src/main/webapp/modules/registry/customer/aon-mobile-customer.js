import { TAG } from '../../../environments/environments.js'; 
import { AonMobileReg } from '../aon-mobile-reg.js';
import { AonMobileCustomerList } from './aon-mobile-customer-list.js';
import { Customer } from '../../../models/registry/Customer.js';
import { saveCustomer } from '../../../services/registryService.js';

export class AonMobileCustomer extends AonMobileReg {
	
	saveBool = true;
	
	back() {
		let list = new AonMobileCustomerList();
		list.id = this.getApplication().id + 'CustomerList';
		this.getApplication().setContent(list);
	}

	setCustomer(customer) {
		this.registry = new Customer(customer);
	}

	save() {
		if (this.saveBool) {
			let medias = this.emails.concat(this.phones).concat(this.webs);
			this.registry.setMedia(medias);
			this.saveBool = false;

			saveCustomer(this.registry)
				.then((registry) => {
					this.registry.id = registry.id;
					this.saveBool = true;
					this.showToast({
						type: "success",
						message: "Datos Guardados Correctamente",
					});
				})
				.catch((error) => {
					this.saveBool = true;
					this.showToast(error);
				});
		}
	}
}

if(!window.customElements.get(TAG.AON_MOBILE_CUSTOMER)){
	window.customElements.define(TAG.AON_MOBILE_CUSTOMER, AonMobileCustomer);
}