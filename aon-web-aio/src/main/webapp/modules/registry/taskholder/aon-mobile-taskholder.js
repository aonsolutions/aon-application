import { TaskHolder } from '../../../models/registry/TaskHolder.js';
import { AonMobileReg } from '../aon-mobile-reg.js';
import { AonMobileTaskHolderList } from './aon-mobile-taskholder-list.js';

export class AonMobileTaskHolder extends AonMobileReg {

	setTaskHolder(taskholder) {
		this.registry = new TaskHolder(taskholder);
	}

	back() {
		let list = new AonMobileTaskHolderList();
		list.id = this.getApplication().id + 'TaskHolderList';
		this.getApplication().setContent(list);
	}

}

if(!window.customElements.get("aon-mobile-taskholder")){
	window.customElements.define("aon-mobile-taskholder", AonMobileTaskHolder);
}