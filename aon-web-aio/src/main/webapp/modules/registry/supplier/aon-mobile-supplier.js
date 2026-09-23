import { TAG } from '../../../environments/environments.js'; 
import { Supplier } from '../../../models/registry/Supplier.js';
import { AonMobileReg } from '../aon-mobile-reg.js';
import { AonMobileSupplierList } from './aon-mobile-supplier-list.js';
import { saveSupplier } from '../../../services/registryService.js';
export class AonMobileSupplier extends AonMobileReg {

	saveBool = true;

	back() {
		let list = new AonMobileSupplierList();
		list.id = this.getApplication().id + 'SupplierList';
		this.getApplication().setContent(list);
	}

	setSupplier(supplier) {
		this.registry = new Supplier(supplier);
	}

	save() {
		if (this.saveBool) {
			let medias = this.emails.concat(this.phones).concat(this.webs);
			this.registry.setMedia(medias);
			this.saveBool = false;

			saveSupplier(this.registry)
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

if(!window.customElements.get(TAG.AON_MOBILE_SUPPLIER)){
	window.customElements.define(TAG.AON_MOBILE_SUPPLIER, AonMobileSupplier);
}