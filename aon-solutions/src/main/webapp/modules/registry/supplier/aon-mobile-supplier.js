import { TAG } from '../../../environments/environments.js'; 
import { AonMobileReg } from '../aon-mobile-reg.js';
import { AonMobileSupplierList } from './aon-mobile-supplier-list.js';

export class AonMobileSupplier extends AonMobileReg {

	back() {
		let list = new AonMobileSupplierList();
		list.id = this.getApplication().id + 'SupplierList';
		this.getApplication().setContent(list);
	}

}

if(!window.customElements.get(TAG.AON_MOBILE_SUPPLIER)){
	window.customElements.define(TAG.AON_MOBILE_SUPPLIER, AonMobileSupplier);
}