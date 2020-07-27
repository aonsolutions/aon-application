(function() {

	const html = `
	<table class="mdl-data-table mdl-js-data-table mdl-data-table--selectable mdl-shadow--2dp aonTable">
	  <thead>
	    <tr>
	      <th class="mdl-data-table__cell--non-numeric">Nombre</th>
	      <th class="mdl-data-table__cell--non-numeric">Apellidos</th>
	      <th class="mdl-data-table__cell--non-numeric">Email</th>
	      <th class="mdl-data-table__cell--non-numeric">DNI/NIE</th>
	      <th class="mdl-data-table__cell--non-numeric"></th>
	    </tr>
	  </thead>
	  <tbody id="user-tbody">

	  </tbody>
	</table>
	`;

class AonUserList extends HTMLElement {

	_users;

	static get observedAttributes() {
		return ['users'];
	}

	get users() {
		return JSON.parse(this.getAttribute('users'));
	}

	set users(value) {
		 this.setAttribute('users', JSON.stringify(value));
	}

	attributeChangedCallback(name, oldValue, newValue) {
		if('users' === name) {
				let tbody = document.getElementById('user-tbody');
				this._users = JSON.parse(newValue);
				tbody.innerHTML = '';
				this.build();
		}
	}

	constructor () {
		super();

		// if(this.hasAttribute('users')) {
		// 	this._users =  JSON.parse(this.getAttribute('users'));
		// }
	}

	connectedCallback () {
		this.innerHTML = html;
		getUsers().then(users => {
			console.log(users);
			this._users = users;
			this.build();
		});
 	}

 	build() {
		let tbody = document.getElementById('user-tbody');
		for(let i = 0; i < this._users.length; i++) {
			let user = this._users[i];
			var event = new CustomEvent('select', { 'detail': user });
			let tr = document.createElement('tr');
			tr.style.cursor = 'pointer';

			let td1 = document.createElement('td');
			td1.className = 'mdl-data-table__cell--non-numeric';
			td1.innerHTML = user.name;
			td1.addEventListener('click', () => this.dispatchEvent(event));

			let td2 = document.createElement('td');
			td2.className = 'mdl-data-table__cell--non-numeric';
			td2.innerHTML = user.surname;
			td2.addEventListener('click', () => this.dispatchEvent(event));

			let td3 = document.createElement('td');
			td3.className = 'mdl-data-table__cell--non-numeric';
			td3.innerHTML = user.email;
			td3.addEventListener('click', () => this.dispatchEvent(event));

			let td4 = document.createElement('td');
			td4.innerHTML = user.document;
			td4.addEventListener('click', () => this.dispatchEvent(event));

			// TODO:
			let td5 = document.createElement('td');
			td5.className = 'mdl-data-table__cell--non-numeric';
			td5.innerHTML = 'INFO';
			td5.addEventListener('click', () => this.dispatchEvent(event));

			tr.appendChild(td1);
			tr.appendChild(td2);
			tr.appendChild(td3);
			tr.appendChild(td4);
			tr.appendChild(td5);


			tbody.appendChild(tr);
		}

	}

}
window.customElements.define('aon-user-list', AonUserList);

})();
