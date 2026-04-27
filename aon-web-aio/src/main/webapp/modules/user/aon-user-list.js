import {AonElement} from '../../components/AonElement.js';
import {generateTokenJson, getSigUserListSpeed, getUserListSpeed, getUserRoles} from  '../../services/service.js';
import {setUsers, setIndex, addUsers, getFilter, setFilter, getUsers} from './UserCache.js';

import { CONSTANT, EVENT, MSG, TAG } from '../../environments/environments.js';
import { AonTable } from '../../components/aon-table.js';

import * as LS from '../../services/localStorageService.js';
import { AonNewUser } from './aon-new-user.js';

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
	
	fromCustomer;

	parent;

	constructor () {
		super();
	}

	connectedCallback() {
		this.initialize()
		this.build();
 	}


    disconnectedCallback() {
		let application = this.getApplication();
		if(application) {
			application.removeToolbarOptions();
		}
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
		this.buildApplicationToolbarOptions();

		let table = this.createAonElement(new AonTable(), this.TABLE);
		this.appendChild(table);
		
		table.addColumn(MSG.NAME, CONSTANT.STRING, CONSTANT.NAME, '25%');
		table.addColumn(MSG.SURNAME, CONSTANT.STRING, CONSTANT.SURNAME, '25%');
		table.addColumn(MSG.EMAIL, CONSTANT.STRING, CONSTANT.EMAIL, '20%');
		table.addColumn(MSG.NIF, CONSTANT.NUMBER, CONSTANT.DOCUMENT, '5%');
		table.addColumn('', "icons", "icons", '5%');
		table.addEventListener('more', () => {
			if(this.more) this.loadMore()
		});
		this.init();
	}

	buildApplicationToolbarOptions() {
		let application = this.getApplication();
		if(application) {
			application.addToolbarOption("UserAdd", "add", () => this.buildUser());

			const btnSearch = application.addSearchOption(true);
			let searchFn = (event) => this.search(event.detail);
			btnSearch.addEventListener(EVENT.SEARCH, searchFn);
		}
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
					user.icons =  this.getOptions(user);
					table.addRow(user, () => this.aonUser(user, i));
				});
			} else {
				this.filter.page = 1;
				if(this.isSig() && this.fromCustomer){
					
					// Set default values if not sessionData given
					if(!this.sessionData){
						this.sessionData = {
							session_id: LS.getToken(),
							domain_name: LS.getDomainName(),
							domain_id: LS.getDomainId(),
							domain_login: LS.getDomainLogin()
				 		}
					}
					
					getSigUserListSpeed(this.filter, this.sessionData).then(users => {
						setUsers(users);
						table.removeRows();
						users.forEach((user, i) => {
							user.icons =  this.getOptions(user);
							table.addRow(user, () => this.aonUser(user, i));
						});
					});
				} else {
					getUserListSpeed(this.filter, this.sessionData).then(users => {
						setUsers(users);
						table.removeRows();
						users.forEach((user, i) => {
							user.icons =  this.getOptions(user);
							table.addRow(user, () => this.aonUser(user, i));
						});
					});
				}
				
			}
		}
	}

	loadMore() {
		this.more = false;
		let table = this.getElement(this.TABLE);
		if(table && this.filter.page) {
			this.filter.page = this.filter.page + 1;
			
			if(this.isSig()){
				getSigUserListSpeed(this.filter, this.sessionData).then(users => {
					addUsers(users);
					if(users.length > 0)
						this.more = true;
					users.forEach((user, i) => {
						table.addRow(user, () => this.aonUser(user, i));
					});
				});
			} else {
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
	}

	getOptions(user) {
		return [{
				id:"supplant",
				color: "#5f6368",
				title: 'Suplantar',
				icon: "social_distance",
				fn: () => this.supplant(user)
			}]
		 ;
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
			
			let domainName = this.sessionData ? this.sessionData.domain_name : LS.getDomainName();
			
			// this.sessionData can be undefined, then will get current sessionData in generateTokenJson method
			generateTokenJson(data, this.sessionData).then(token => {
				open(`https://${domainName}/?token=${token.session_id}`, '_blank');
			}).catch(e => this.showError(e));
		});			
		d.open();
	}

	aonUser(user, index) {
		setIndex(index);
		setFilter(this.filter);
		getUserRoles({user: user.id}, this.sessionData).then(roles => {
			user.roles = roles;
			this.buildUser(user);
		});
	
	}

	buildUser(user) {
		let aonUser = new AonNewUser();
		aonUser.sessionData = this.sessionData;
		aonUser.parent = this.parent;
		aonUser.id = 'aonUser-' + (user ? user.id : 'new');
		aonUser.setShowApps(true);
		aonUser.setShowToolbar(true);
		aonUser.setUser(user);
		aonUser.style.width = "100%";
		if(this.parent) {
			this.parent.innerHTML = '';
			this.parent.appendChild(aonUser);
		} else this.getApplication().setContent(aonUser);
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

