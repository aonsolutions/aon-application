import {getDomainCompanies} from '../../services/service.js';
import { TAG} from '../../environments/environments.js';
import { AonRegistryList } from '../registry/aon-registry-list.js';

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
}

if(!window.customElements.get(TAG.AON_COMPANY_LIST)) {
	window.customElements.define(TAG.AON_COMPANY_LIST, AonCompanyList);
}
