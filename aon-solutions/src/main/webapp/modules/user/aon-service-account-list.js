import {AonElement} from '../../components/AonElement.js';
import {getUserListSpeed} from  '../../services/service.js';
import { CONSTANT, MSG, TAG } from '../../environments/environments.js';
import { AonTable } from '../../components/aon-table.js';

export class AonServiceAccountList extends AonElement {

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	TABLE;

	constructor () {
		super();
	}

	connectedCallback() {
		this.initialize()
		this.build();
 	}

	initialize() {
		this.id = this.id || CONSTANT.AON_SERVICE_ACCOUNT_LIST;
		this.TABLE = this.id + CONSTANT.TABLE.initCap();
	}

	build() {
		let table = this.createAonElement(new AonTable(), this.TABLE);
		this.appendChild(table);

		table.addColumn(MSG.NAME, CONSTANT.STRING, CONSTANT.NAME, '25%');
		table.addColumn(MSG.EMAIL, CONSTANT.STRING, CONSTANT.EMAIL, '75%');
		
		this.init();
	}

	search(value) {
		this.filter.value = value;
		this.init();		
	}

	init() {
		getUserListSpeed({type:'SERVICE', page: 1, perPage: 30})
		.then(users => {
			users.forEach((user, i) => {
				this.getElement(this.TABLE).addRow(user, () => {});
			});
		});
	}
	
}
if(!window.customElements.get(TAG.AON_SERVICE_ACCOUNT_LIST)){
	window.customElements.define(TAG.AON_SERVICE_ACCOUNT_LIST, AonServiceAccountList);
}

