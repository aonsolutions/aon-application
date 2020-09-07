import {Apps} from  '../../services/app.js';
import {getDomainApps, setUserAppRole, setUser} from  '../../services/service.js';

import '../aon-select.js';

class AonCompany extends HTMLElement {

	static get observedAttributes() {
		return ['company'];
	}

	get company() {
		return this.getAttribute('company');
	}

	set company(company) {
		this.setAttribute('company', company);
	}

	attributeChangedCallback(name, oldValue, newValue) {
		if('company' === name) {
			this.initCompany();
		}
	}

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-card id="aonConfigurationCompanyCard" title="Empresa"></aon-card>
		`;
		this.build();
  }

	initCompany() {
		let company = this.getAttribute('company') ? JSON.parse(this.getAttribute('company')) : undefined;
		let aonCompanyName = document.getElementById('aonConfigurationUserCompanyCardName');
		aonCompanyName.setVisible(!this.isNew());
		aonUserName.setAttribute('value', user && user.name ? user.name : '');
		let aonUserSurname = document.getElementById('aonConfigurationUserCardSurname');
		aonUserSurname.setVisible(!this.isNew());
		aonUserSurname.setAttribute('value', user && user.surname ? user.surname : '');
		let aonUserDocument = document.getElementById('aonConfigurationUserCardDocument');
		aonUserDocument.setVisible(!this.isNew());
		aonUserDocument.setAttribute('value', user && user.document ? user.document : '');
		let aonUserPhone = document.getElementById('aonConfigurationUserCardPhone');
		aonUserPhone.setVisible(!this.isNew());
		aonUserPhone.setAttribute('value', user && user.phone ? user.phone : '');
		let aonUserEmail = document.getElementById('aonConfigurationUserCardEmail');
		aonUserEmail.setAttribute('value', user && user.email ? user.email : '');

		let card2 = document.getElementById('aonConfigurationUserSecurityCard');
		card2.setVisible(!this.isNew());
		if(user && !this.hasAttribute('apps')) {
			getDomainApps().then(apps => {
				this.setAttribute('apps', JSON.stringify(apps));
			});
		}
	}

	build() {
		let company = this.getAttribute('company') ? JSON.parse(this.getAttribute('company')) : undefined;
		let user = this.getAttribute('user') ? JSON.parse(this.getAttribute('user')) : undefined;

		let card = document.getElementById('aonConfigurationUserCard');
		let cardDiv = document.getElementById('aonConfigurationUserCard-div');
		cardDiv.style.width = '500px';
		card.addContent(`
			<form action="#" class="aon-margin-0">
				<aon-input-text class="aonWidth100" id="aonConfigurationUserCardEmail" description="Email" value=""></aon-input-text>
			</form>
			<form action="#" class="aon-margin-0">
				<aon-input-text class="aonWidth25" id="aonConfigurationUserCardName" description="Nombre" value=""></aon-input-text>
				<aon-input-text class="aonWidth75" id="aonConfigurationUserCardSurname" description="Apellidos" value=""></aon-input-text>
			</form>
			<form action="#" class="aon-margin-0">
				<aon-input-text class="aonWidth50" id="aonConfigurationUserCardDocument" description="DNI/NIE" value=""></aon-input-text>
				<aon-input-text class="aonWidth50" id="aonConfigurationUserCardPhone" description="Teléfono Móvil" value=""></aon-input-text>
			</form>

		`);

		let name = document.getElementById('aonConfigurationUserCardName');
		name.setVisible(!this.isNew());
		name.onChange(() => {
			let user = this.getAttribute('user') ? JSON.parse(this.getAttribute('user')) : {};
			user.name = name.getAttribute('value');
			setUser(user).then(r => {
				this.setAttribute('user', JSON.stringify(r));
			});
		});

		let surname = document.getElementById('aonConfigurationUserCardSurname');
		surname.setVisible(!this.isNew());
		surname.onChange(() => {
			let user = this.getAttribute('user') ? JSON.parse(this.getAttribute('user')) : {};
			user.surname= surname.getAttribute('value');
			setUser(user).then(r => {
				this.setAttribute('user', JSON.stringify(r));
			});
		});

		let doc = document.getElementById('aonConfigurationUserCardDocument');
		doc.setVisible(!this.isNew());
		doc.onChange(() => {
			let user = this.getAttribute('user') ? JSON.parse(this.getAttribute('user')) : {};
			user.document= doc.getAttribute('value');
			setUser(user).then(r => {
				this.setAttribute('user', JSON.stringify(r));
			});
		});

		let phone = document.getElementById('aonConfigurationUserCardPhone');
		phone.setVisible(!this.isNew());
		phone.onChange(() => {
			let user = this.getAttribute('user') ? JSON.parse(this.getAttribute('user')) : {};
			user.phone= phone.getAttribute('value');
			console.log(JSON.stringify(user));
			setUser(user).then(r => {
				this.setAttribute('user', JSON.stringify(r));
			});
		});

		let email = document.getElementById('aonConfigurationUserCardEmail');
		email.onChange(() => {
			let user = this.getAttribute('user') ? JSON.parse(this.getAttribute('user')) : {};
			user.email = email.getAttribute('value');
			user.share = this.hasAttribute('share');
			setUser(user).then(r => {
				this.setAttribute('user', JSON.stringify(r));
			});
		});

		let card2 = document.getElementById('aonConfigurationUserSecurityCard');
		card2.setVisible(!this.isNew());
		let cardDiv2 = document.getElementById('aonConfigurationUserSecurityCard-div');
		cardDiv2.style.width = '500px';

		let table = document.createElement('table');
		table.setAttribute('id', 'aonUserRoleTable')
		table.style.width = '100%';

		cardDiv2.appendChild(table);
		componentHandler.upgradeAllRegistered();
	}

	buildAppSelect(app) {
		let user = this.getAttribute('user') ? JSON.parse(this.getAttribute('user')) : undefined;

		let tr = document.createElement('tr');
		let td1 = document.createElement('td');
		td1.style.width = '30px';
		td1.style.height = '40px';

		if(app){
			let img = document.createElement('img');
			img.style.width = '24px';
			img.src = app.logo;
			td1.appendChild(img);
		}


		let span2 = document.createElement('span');
		span2.style.padding = '10px';
		span2.style.fontWeight = 'bold';
		span2.style.color = '#5f6368';
		span2.innerHTML = app ? app.title : 'Administrador';

		let label = document.createElement('label');
		label.className = 'mdl-switch mdl-js-switch mdl-js-ripple-effect';
		label.for = 'switch-' + (app ? app.app : 'admin');
		label.style.width = '50px';

		let input = document.createElement('input');
		input.type = 'checkbox';
		input.id = 'switch-' + (app ? app.app : 'admin');
		input.className = 'mdl-switch__input';
		input.checked = app && !user.admin && user.apps ? user.apps[app.app] : user.admin;
		input.addEventListener('change', () => {
			let uar = {
				domain: localStorage.getItem('aon_domain_name'),
				app: app ? app.app : undefined,
				role: 'admin',
				user: user.id,
				active: input.checked
			};
			setUserAppRole(uar);
		});
		let span3 = document.createElement('span');
		span3.className = 'mdl-switch__label'

		label.appendChild(input);
		label.appendChild(span3);

		let span4 = document.createElement('span');
		let options = ['Administrador', 'Invitado'];
		let roleOptions = [
			{value:'admin', name:'Administrador'},
			{value:'guest', name:'Invitado'}
		];
		if(app){
			// span4.innerHTML = `<aon-select class="aon-width-25" id="aonSelectUserRol-${app.app}" description="Rol" options='${JSON.stringify(roleOptions)}' ></aon-select>`;
		}
		let td2 = document.createElement('td');
		td2.style.height = '40px';
		td2.appendChild(span2);

		let td3 = document.createElement('td');
		td3.style.height = '40px';
		td3.appendChild(label);

		let td4 = document.createElement('td');
		td4.style.height = '40px';
		if(app) td4.appendChild(span4);

		tr.appendChild(td1);
		tr.appendChild(td2);
		tr.appendChild(td3);
		tr.appendChild(td4);
		return tr;
	}

	isNew() {
		let user = this.getAttribute('company') ? JSON.parse(this.getAttribute('company')) : undefined;
		return user === undefined;
	}

	buildGeneral() {
		let company = this.getAttribute('company') ? JSON.parse(this.getAttribute('company')) : undefined;

		let content = document.getElementById('aonConfigurationContent');
		content.style.display = "flex";
		content.innerHTML = `
			<aon-card id="aonConfigurationGeneralCard" title="Información General"></aon-card>
			<aon-card id="aonConfigurationGeneral2Card" title="Información Adicional"></aon-card>
		`;
		let card = document.getElementById('aonConfigurationGeneralCard');
		let cardDiv = document.getElementById('aonConfigurationGeneralCard-div');
		cardDiv.style.width = '500px';
		card.addContent(`
			<form action="#" class="aon-margin-0">
				<aon-input-text class="aonWidth25" id="aonConfigurationGeneralNif" description="NIF" value="${company.document}"></aon-input-text>
				<aon-input-text class="aonWidth75" id="aonConfigurationGeneralName" description="Razón Social" value="${company.name}"></aon-input-text>
			</form>
			<form action="#" class="aon-margin-0">
				<aon-address class="aon-width-100" id="aonConfigurationGeneralAddress" description="Dirección"></aon-address>
			</form>
		`);

		let card2 = document.getElementById('aonConfigurationGeneral2Card');
		let cardDiv2 = document.getElementById('aonConfigurationGeneral2Card-div');
		cardDiv2.style.width = '500px';
		card2.addContent(`
			<form action="#" class="aon-margin-0">
				<aon-input-text class="aonWidth50" id="aonConfigurationGeneral2Phone" description="Teléfono" value=""></aon-input-text>
				<aon-input-text class="aonWidth50" id="aonConfigurationGeneral2Fax" description="Fax" value=""></aon-input-text>
			</form>

			<form action="#" class="aon-margin-0">
				<aon-input-text class="aon-width-100" id="aonConfigurationGeneral2Email" description="Email" value=""></aon-input-text>
			</form>

			<form action="#" class="aon-margin-0">
				<aon-input-text class="aon-width-100" id="aonConfigurationGeneral2Web" description="Web" value=""></aon-input-text>
			</form>

			<!-- LOGO -->
		`);

		componentHandler.upgradeAllRegistered();
	}

}

window.customElements.define('aon-company', AonCompany);
