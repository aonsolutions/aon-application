import {AonElement} from '../../components/AonElement.js';
import {getUserListSpeed, generateToken, deleteUser} from  '../../services/service.js';
import { CONSTANT, MSG, TAG } from '../../environments/environments.js';
import { AonTable } from '../../components/aon-table.js';
import * as LS from '../../services/localStorageService.js';
import { AonSelect } from '../../components/aon-select.js';

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
		table.addColumn(MSG.EMAIL, CONSTANT.STRING, CONSTANT.EMAIL, '70%');
		table.addColumn('', "fn", "option", '5%');
		this.init();
	}

	search(value) {
		this.filter.value = value;
		this.init();		
	}

	init() {
		getUserListSpeed({type:'SERVICE', page: 1, perPage: 30})
		.then(users => {
			users.map((user, i) => {
				user.option =  this.getOptions(user);
				this.getElement(this.TABLE).addRow(user, () => {});
			});
		});
	}

	getOptions(user) {
		return [{
				name: "Generar Token",
				icon: "token",
				id:"tokenGenerate",
				fn: () => this.generateToken(user)
			},
			{
				name: "Borrar",
				icon: "delete",
				id:"delete",
				fn: () => this.deleteServiceAccount(user)
			}
		];

	}	

	generateToken(user) {
		let d = this.getApplication().getDialog();
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle("Generar Token");

		let select = this.createAonElement(new AonSelect(), "generateTokenTime", "Expira en");
		select.setOptions([{
			value: 0,
			name: '1 mes'
		}, {
			value: 1,
			name: '3 meses'
		}, {
			value: 2,
			name: '1 año'
		}, {
			value: 3,
			name: 'Nunca'
		}]);

		d.setContent(select);
		d.addAcceptAction(() => {
			let data = {
				domainId: LS.getDomainId(),
				domainName: LS.getDomainName(),
				domainLogin: LS.getDomainLogin(),
				id: user.id,
				time: select.value
			};
			let json = btoa(JSON.stringify(data));
			generateToken(json);
		});			
		d.open();
	}

	deleteServiceAccount(user) {
		deleteUser({user: user.id}).then(() => this.reload());
	}

	reload() {
		this.getElement(this.TABLE).removeRows();
		this.init();
	}
}
if(!window.customElements.get(TAG.AON_SERVICE_ACCOUNT_LIST)){
	window.customElements.define(TAG.AON_SERVICE_ACCOUNT_LIST, AonServiceAccountList);
}

