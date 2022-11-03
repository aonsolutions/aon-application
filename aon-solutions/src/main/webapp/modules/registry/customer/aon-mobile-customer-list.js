import {getCustomer, getCustomers} from '../../../services/service.js';
import { TAG} from '../../../environments/environments.js';
import { AonMobileRegistryList } from '../aon-mobile-registry-list.js';
import { AonMobileCustomer } from './aon-mobile-customer.js';

export class AonMobileCustomerList extends AonMobileRegistryList {

	getRegistries() {
		return getCustomers(this.filter2);
	}

	buildRegistry(registry) {
		let data = {
			id: registry.id,
			additional_info: ['ADDRESSES', 'MEDIA', 'BANKS', 'PAYMETHOD', 'RECORD_DATA']
		};
		getCustomer(data).then(r => {
			let aonRegistry = new AonMobileCustomer();
			aonRegistry.id = this.getApplication().id + 'Registry';
			aonRegistry.setCustomer(r);
			this.getApplication().setContent(aonRegistry);
		});
	}

	setFilter(filter) {
		this.filter2 = filter;	
		this.init();
	}
}

if(!window.customElements.get(TAG.AON_MOBILE_CUSTOMER_LIST)) {
	window.customElements.define(TAG.AON_MOBILE_CUSTOMER_LIST, AonMobileCustomerList);
}
