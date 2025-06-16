import {AonElement} from '../../components/AonElement.js';
import {generateTokenJson, getUserListSpeed, getUserRoles} from  '../../services/service.js';
import {setUsers, setIndex, addUsers, getFilter, setFilter, getUsers} from './UserCache.js';

import { AonUser } from './aon-user.js';
import { CONSTANT, EVENT, MSG, TAG } from '../../environments/environments.js';
import { AonTable } from '../../components/aon-table.js';

import * as LS from '../../services/localStorageService.js';

export class AonUserList extends AonElement {

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	TABLE;
	more;
	filter;
	type;
	back;

	parent;

	constructor () {
		super();
	}

	connectedCallback() {
		this.initialize()
		this.build();
 	}

	initialize() {
		this.back = this.back || false;
		this.more = true;
		this.id = this.id || CONSTANT.AON_USER_LIST;
		this.TABLE = this.id + CONSTANT.TABLE.initCap();
		this.filter = this.filter || {
			filter: this.type || 'company',
			page: 1,
			perPage: 30		
		};
		if(this.back) 
			this.filter = getFilter();
	}

	build() {
		let table = this.createAonElement(new AonTable(), this.TABLE);
		this.appendChild(table);

		const btnSearch = this.getApplication().addSearchOption();
		let searchFn = (event) => this.search(event.detail);
		btnSearch.addEventListener(EVENT.SEARCH, searchFn);
		
		table.addColumn(MSG.NAME, CONSTANT.STRING, CONSTANT.NAME, '25%');
		table.addColumn(MSG.SURNAME, CONSTANT.STRING, CONSTANT.SURNAME, '25%');
		table.addColumn(MSG.EMAIL, CONSTANT.STRING, CONSTANT.EMAIL, '20%');
		table.addColumn(MSG.NIF, CONSTANT.NUMBER, CONSTANT.DOCUMENT, '5%');
		table.addColumn('', "fn", "option", '5%');
		table.addEventListener('more', () => {
			if(this.more) this.loadMore()
		});
		this.init();
	}

	search(value) {
		this.filter.value = value;
		this.init();		
	}

	init() {
		let table = document.getElementById(this.TABLE);
		if(table) {
			if(this.back) {
				this.back = false;
				getUsers().forEach((user, i) => {
					user.option =  this.getOptions(user);
					table.addRow(user, () => this.aonUser(user, i));
				});
			} else {
				this.filter.page = 1;
				getUserListSpeed(this.filter, this.sessionData).then(users => {
					setUsers(users);
					table.removeRows();
					users.forEach((user, i) => {
						user.option =  this.getOptions(user);
						table.addRow(user, () => this.aonUser(user, i));
					});
				});
			}
		}
	}

	loadMore() {
		this.more = false;
		let table = this.getElement(this.TABLE);
		if(table && this.filter.page) {
			this.filter.page = this.filter.page + 1;
			getUserListSpeed(this.filter, this.sessionData).then(users => {
				addUsers(users);
				if(users.length > 0)
					this.more = true;
				users.forEach((user, i) => {
					table.addRow(user, () => this.aonUser(user, i));
				});
			});
		}
	}

	getOptions(user) {
		return this.isBeta() || this.isAyudaT() ?
			[{
				name: "Suplantar",
				icon: "token",
				id:"supplant",
				fn: () => this.supplant(user)
			}
		] : [];
	}

	supplant(user) {
		let d = this.getApplication().getDialog();
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle("Suplantar Usuario");
		
		d.setContentHTML("Estás seguro de suplantar a " + user.login);
		d.addAcceptAction(() => {
			let data = {
				supUser: LS.getDomainLogin(),
				id: user.id,
				time: 0
			};
			generateTokenJson(data).then(token => {
				open(`https://${LS.getDomainName()}/app?token=${token.session_id}`, '_blank');
			}).catch(e => this.showError(e));
		});			
		d.open();
	}

	aonUser(user, index) {
		setIndex(index);
		setFilter(this.filter);
		getUserRoles({user: user.id}, this.sessionData).then(roles => {
			user.roles = roles;
			let aonUser = new AonUser();
			aonUser.sessionData = this.sessionData;
			aonUser.parent = this.parent;
			aonUser.id = 'aonUser-' + user.id;
			aonUser.setShowApps(true);
			aonUser.setShowToolbar(true);
			aonUser.setUser(user);
			aonUser.style.width = "100%";
			if(this.parent) {
				this.parent.innerHTML = '';
				this.parent.appendChild(aonUser);
			} else this.getApplication().setContent(aonUser);
		});
	
	}

	setType(type) {
		this.type = type || 'company';
	}

	setBack(back) {
		this.back = back || false;
	}
}
if(!window.customElements.get(TAG.AON_USER_LIST)){
	window.customElements.define(TAG.AON_USER_LIST, AonUserList);
}

