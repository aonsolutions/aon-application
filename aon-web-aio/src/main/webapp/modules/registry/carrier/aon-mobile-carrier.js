import { TAG } from '../../../environments/environments.js'; 
import { AonMobileReg } from '../aon-mobile-reg.js';
import { AonMobileCarrierList } from './aon-mobile-carrier-list.js';

export class AonMobileCarrier extends AonMobileReg {

	back() {
		let list = new AonMobileCarrierList();
		list.id = this.getApplication().id + 'CarrierList';
		this.getApplication().setContent(list);
	}

}

if(!window.customElements.get(TAG.AON_MOBILE_CARRIER)){
	window.customElements.define(TAG.AON_MOBILE_CARRIER, AonMobileCarrier);
}