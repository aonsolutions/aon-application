import { TAG } from '../../../environments/environments.js'; 
import { Creditor } from '../../../models/registry/Creditor.js';
import { AonMobileReg } from '../aon-mobile-reg.js';
import { AonMobileCreditorList } from './aon-mobile-creditor-list.js';

export class AonMobileCreditor extends AonMobileReg {

	back() {
		let list = new AonMobileCreditorList();
		list.id = this.getApplication().id + 'CreditorList';
		this.getApplication().setContent(list);
	}

	setCreditor(creditor) {
		this.registry = new Creditor(creditor);
	}
}

if(!window.customElements.get(TAG.AON_MOBILE_CREDITOR)){
	window.customElements.define(TAG.AON_MOBILE_CREDITOR, AonMobileCreditor);
}