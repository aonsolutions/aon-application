import {getDomainCompanies, getRegistry} from '../../services/service.js';
import { TAG, MSG} from '../../environments/environments.js';
import { AonRegistryList } from '../registry/aon-registry-list.js';
import { AonReg } from '../registry/aon-reg.js';

export class AonCompanyList extends AonRegistryList {

	build(){
		this.filter = {
			parent:true,
			page: 1,
			perPage: 50		
		}
		super.build();
	}

	getRegistries() {
		return getDomainCompanies(this.filter);
	}

	buildRegistry(registry) {
		let data = {
			id: registry.id,
			additional_info: ['ADDRESSES', 'MEDIA', 'BANKS', 'PAYMETHOD', 'RECORD_DATA']
		};
		getRegistry(data).then(r => {
			let aonRegistry = new AonReg();
			aonRegistry.id = this.getApplication().id + 'Registry';
			aonRegistry.options = [
				{ title: MSG.GENERAL_DATA, fn: () => aonRegistry.buildGeneralData()},
				{ title: MSG.BANK_DATA, fn: () => aonRegistry.buildBankData()},
				{ title: MSG.REGISTRATION_DATA, fn: () => aonRegistry.buildRegistralData()}
			];
			aonRegistry.setRegistry(r);
			this.getApplication().setContent(aonRegistry);
		});
	}
}

if(!window.customElements.get(TAG.AON_COMPANY_LIST)) {
	window.customElements.define(TAG.AON_COMPANY_LIST, AonCompanyList);
}
