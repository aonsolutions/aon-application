import {AonElement} from '../../components/AonElement.js';
import {getUsers} from  '../../services/service.js';
import {setUsers, setIndex} from './UserCache.js';


import './aon-user.js'
import '../../components/aon-table.js';
import { AonUser } from './aon-user.js';
import { EVENT } from '../../environments/environments.js';

export class AonUserList extends AonElement {

	users; 
	filter;

	constructor () {
		super();
	}

	connectedCallback() {
		this.innerHTML = `
			<aon-table id='aonUserTable'></aon-table>
		`;
		this.initialize()
		this.build();
 	}

	initialize() {
		this.filter = this.filter || {filter: 'company'};
	}

	build() {

		const btnSearch = this.getApplication().addSearchOption();
		let searchFn = (event) => this.search(event.detail);
		btnSearch.addEventListener(EVENT.SEARCH, searchFn);

		let table = this.getElement('aonUserTable');
		table.addColumn('Nombre', 'string', 'name', '25%');
		table.addColumn('Apellidos', 'string', 'surname', '25%');
		table.addColumn('Email', 'string', 'email', '25%');
		table.addColumn('DNI/NIE', 'number', 'document', '25%');
		// INFO
		// aonInvoiceTable.addColumn('', '', '');

		this.init();
	}


	setFilter(filter) {
		this.filter = filter;
	}

	search(value) {
		const usrs = this.users.filter( f => (f.name && f.name.includes(value)) || (f.surname && f.surname.includes(value)) 
			|| (f.document && f.document.includes(value)) || (f.email && f.email.includes(value)));
		setUsers(usrs);
		let table = this.getElement('aonUserTable');

		table.removeRows();
		usrs.forEach((user, i) => {
			table.addRow(user, () => this.aonUser(user, i));
		});
	}

	init() {
		let filter = {
			filter: this.hasAttribute('filter') ? this.getAttribute('filter') : 'company'
		};
		let table = document.getElementById('aonUserTable');
		if(table) {
			getUsers(filter).then(users => {
				setUsers(users);
				this.users = users;
				table.removeRows();
				users.forEach((user, i) => {
					table.addRow(user, () => this.aonUser(user, i));
				});
			});
		}
	}

	aonUser(user, index) {
		setIndex(index);

		let aonUser = new AonUser();
		aonUser.id = 'aonUser-' + user.id;
		aonUser.setShowApps(true);
		aonUser.setShowToolbar(true);
		aonUser.setUser(user);
		aonUser.style.width = "100%";

		this.getApplication().setContent(aonUser);	
	}

	// setValue(value) {
	// 	let filter = {
	// 		filter: this.hasAttribute('filter') ? this.getAttribute('filter') : 'company'
	// 	};
	// 	let table = document.getElementById('aonUserTable');
	// 	if(table) {
	// 		getUsers(filter).then(users => {
	// 			table.removeRows();
	// 			users.filter(f => 
	// 				f.name.toLowerCase().includes(value.toLowerCase()) || f.surname.toLowerCase().includes(value.toLowerCase()) 
	// 				|| f.email.toLowerCase().includes(value.toLowerCase()) || f.document.toLowerCase().includes(value.toLowerCase())
	// 			).forEach((user, i) => {
	// 				table.addRow(user, () => this.aonUser(user, i));
	// 			});
	// 		});
	// 	}
	// }

}
window.customElements.define('aon-user-list', AonUserList);
