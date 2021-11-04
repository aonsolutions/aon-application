import { TAG } from '../../../environments/environments.js'; 
import { AonMobileReg } from '../aon-mobile-reg.js';
import { AonMobileCustomerList } from './aon-mobile-customer-list.js';

export class AonMobileCustomer extends AonMobileReg {

	back() {
		let list = new AonMobileCustomerList();
		list.id = this.getApplication().id + 'CustomerList';
		this.getApplication().setContent(list);
	}

}

if(!window.customElements.get(TAG.AON_MOBILE_CUSTOMER)){
	window.customElements.define(TAG.AON_MOBILE_CUSTOMER, AonMobileCustomer);
}