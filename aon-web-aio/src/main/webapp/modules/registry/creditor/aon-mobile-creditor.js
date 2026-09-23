import { TAG } from '../../../environments/environments.js'; 
import { Creditor } from '../../../models/registry/Creditor.js';
import { saveCreditor } from '../../../services/registryService.js';
import { AonMobileReg } from '../aon-mobile-reg.js';
import { AonMobileCreditorList } from './aon-mobile-creditor-list.js';

export class AonMobileCreditor extends AonMobileReg {

	saveBool = true;

	back() {
		let list = new AonMobileCreditorList();
		list.id = this.getApplication().id + 'CreditorList';
		this.getApplication().setContent(list);
	}

	setCreditor(creditor) {
		this.registry = new Creditor(creditor);
	}

	save() {
		if (this.saveBool) {
			let medias = this.emails.concat(this.phones).concat(this.webs);
			this.registry.setMedia(medias);
			this.saveBool = false;
	
			saveCreditor(this.registry)
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

if(!window.customElements.get(TAG.AON_MOBILE_CREDITOR)){
	window.customElements.define(TAG.AON_MOBILE_CREDITOR, AonMobileCreditor);
}