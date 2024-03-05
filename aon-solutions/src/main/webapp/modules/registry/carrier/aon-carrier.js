import { AonReg } from '../aon-reg.js';
import { MSG, TAG } from '../../../environments/environments.js'; 
import { Carrier } from '../../../models/registry/Carrier.js';
import { saveCarrier } from '../../../services/registryService.js';
import { AonCarrierList } from './aon-carrier-list.js';

export class AonCarrier extends AonReg {

	saveBool;

	connectedCallback () {
		this.carrierInitialize();
		this.initialize();
		this.build();

  	}
	
	carrierInitialize() {
		this.saveBool = true;
		this.options = [
			{ title: MSG.GENERAL_DATA, fn: () => this.buildGeneralData()},
			{ title: MSG.BANK_DATA, fn: () => this.buildBankData()}
		];
	}	

	back() {
		let list = new AonCarrierList();
		list.id = this.getApplication().id + 'CarrierList';
		this.getApplication().setContent(list);
	}

	save() {
		if(this.saveBool) {
			let medias = this.emails.concat(this.phones).concat(this.webs);
			this.registry.setMedia(medias);
			this.saveBool = false;
			saveCarrier(this.registry).then(registry => {
				this.registry.id = registry.id;
				this.saveBool = true;
				this.showToast({
					type: 'success',
					 message: 'Datos Guardados Correctamente'
				 });
			}).catch(error => {
				this.saveBool = true;
				this.showToast(error);
			});
		}
	}

	setCarrier(carrier) {
		this.registry = new Carrier(carrier);
	}
}

if(!window.customElements.get(TAG.AON_CARRIER)){
	window.customElements.define(TAG.AON_CARRIER, AonCarrier);
}