import { saveCompany } from "../../services/service.js";
import {CONSTANT, TAG } from '../../environments/environments.js'; 
import { AonReg } from '../registry/aon-reg.js';

import '../../components/aon-address.js';
import '../../components/aon-input.js';

export class AonCompany extends AonReg {

	save() {
		let medias = this.emails.concat(this.phones).concat(this.webs);
		this.registry.setMedia(medias);
		if(this.registry.addresses.length  == 0) {
			let error = {
				type: CONSTANT.ERROR,
				message: 'La dirección está vacia'
			};
			this.showToast(error);
		} else if(!this.registry.addresses[0].zip || !this.registry.addresses[0].province) {
			let error = {
				type: CONSTANT.ERROR,
				message: 'El código postal o la provincia están vacias'
			};
			this.showToast(error);
		} else {
			saveCompany(this.registry).then(cp => {
				this.registry.id = cp.id;
				this.showToast({
					type: 'success',
					message: 'Datos Guardados Correctamente'
				});
			}).catch(error => {
				this.showToast(error);
			});
		}
	}
}

if(!window.customElements.get(TAG.AON_COMPANY)){
	window.customElements.define(TAG.AON_COMPANY, AonCompany);
}
