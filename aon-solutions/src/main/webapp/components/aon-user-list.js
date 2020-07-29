import './aon-user.js'

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
	constructor () {
		super();
		this.innerHTML = html;
		getUsers().then(users => {
			this.build(users);
		});
	}

	connectedCallback () {

 	}

 	build(users) {
		let tbody = document.getElementById('user-tbody');
		for(let i = 0; i < users.length; i++) {
			let user = users[i];
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
			td5.innerHTML = '<aon-icon-button id="aonUserListSecurityButton-'+ user.id +'" icon="security"></aon-icon-button>';
			td5.addEventListener('click', () => this.dispatchEvent(event));

			tr.appendChild(td1);
			tr.appendChild(td2);
			tr.appendChild(td3);
			tr.appendChild(td4);
			tr.appendChild(td5);


			tbody.appendChild(tr);

			let aonUserListSecurityButton = document.getElementById('aonUserListSecurityButton-' + user.id);
			aonUserListSecurityButton.addEventListener('click', () => {
				let content = document.getElementById('aon-configuration-content');
				content.innerHTML = '<aon-user id="aonUser-' + user.id + '" ><aon-user>';
				let aonUser = document.getElementById('aonUser-' + user.id);
				aonUser.style.display = "flex";
				aonUser.style.width = "100%";
				aonUser.setAttribute('user', JSON.stringify(user));
				getDomainApps().then(apps => {
					aonUser.setAttribute('apps', JSON.stringify(apps));
				});
			})
		}

	}

}
window.customElements.define('aon-user-list', AonUserList);

})();
