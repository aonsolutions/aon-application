import {AonElement} from '../../components/AonElement.js';
import {getUsers} from  '../../services/service.js';

import './aon-user.js'
import '../../components/aon-table.js';

export class AonUserList extends AonElement {

	static get observedAttributes() {
		return ['filter'];
	}

	get filter() {
    return this.getAttribute('filter');
  }

  set filter(filter) {
    this.setAttribute('filter', filter);
  }

	attributeChangedCallback(name, oldValue, newValue) {
		if('filter' === name) {
			this.init();
		}
	}

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-table id='aonUserTable'></aon-table>
		`;
		this.build();
 	}

	build() {
		let table = this.getElement('aonUserTable');
		table.addColumn('Nombre', 'string', 'name', '25%');
		table.addColumn('Apellidos', 'string', 'surname', '25%');
		table.addColumn('Email', 'string', 'email', '25%');
		table.addColumn('DNI/NIE', 'number', 'document', '25%');
		// INFO
		// aonInvoiceTable.addColumn('', '', '');

		this.init();
	}

	init() {
		let filter = {
			filter: this.hasAttribute('filter') ? this.getAttribute('filter') : 'company'
		};
		let table = document.getElementById('aonUserTable');
		if(table) {
			getUsers(filter).then(users => {
				table.removeRows();
				users.forEach((user, i) => {
					table.addRow(user, () => this.aonUser(user));
				});
			});
		}
	}

	aonUser(user) {
		let content = this.parentElement;
		content.innerHTML = '<aon-user id="aonUser-' + user.id + '" showApps="true" showInfo="true" showToolbar="true"><aon-user>';
		let aonUser = document.getElementById('aonUser-' + user.id);
		aonUser.style.width = "100%";
		aonUser.setAttribute('user', JSON.stringify(user));
	}

}
window.customElements.define('aon-user-list', AonUserList);
