import {AonElement} from '../../components/AonElement.js';
import './aon-user.js'
import {getUsers} from  '../../services/service.js';

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
		table.addColumn('Nombre', 'string', 'name');
		table.addColumn('Apellidos', 'string', 'surname');
		table.addColumn('Email', 'string', 'email');
		table.addColumn('DNI/NIE', 'number', 'document');
		// INFO
		// aonInvoiceTable.addColumn('', '', '');

		this.init();
	}

	init() {
		let table = document.getElementById('aonUserTable');
		if(table) {
			getUsers().then(users => {
				table.removeRows();
				users.forEach((user, i) => {
					table.addRow(user, () => this.aonUser(user));
				});
			});
		}
	}

	aonUser(user) {
		let content = document.getElementById('aonConfigurationContent');
		content.innerHTML = '<aon-user id="aonUser-' + user.id + '" showApps="true" ><aon-user>';
		let aonUser = document.getElementById('aonUser-' + user.id);
		aonUser.style.display = "flex";
		aonUser.style.width = "100%";
		aonUser.setAttribute('user', JSON.stringify(user));
	}

}
window.customElements.define('aon-user-list', AonUserList);
