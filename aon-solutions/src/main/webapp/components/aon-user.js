import {Apps} from  '../services/app.js';
import './aon-select.js';

class AonUser extends HTMLElement {

	static get observedAttributes() {
		return ['user', 'company', 'apps'];
	}

	get company() {
		return this.getAttribute('company');
	}

	set company(company) {
		this.setAttribute('company', company);
	}

	get user() {
		return this.getAttribute('user');
	}

	set user(user) {
		this.setAttribute('user', user);
	}

	get apps() {
		return this.getAttribute('apps');
	}

	set apps(apps) {
		this.setAttribute('apps', apps);
	}

	attributeChangedCallback(name, oldValue, newValue) {
		if('user' === name){
			this.initUser();
		}
		if('company' === name) {
			// let company = this.getAttribute('company') ? JSON.parse(this.getAttribute('company')) : undefined;
		}

		if('apps' === name) {
			let apps = this.getAttribute('apps') ? JSON.parse(this.getAttribute('apps')) : undefined;
			let table = document.getElementById('aonUserRoleTable');
			table.appendChild(this.buildAppSelect(undefined));
			for(let key in apps){
				if(apps[key]) table.appendChild(this.buildAppSelect(this.getApp(key)));
			}
			componentHandler.upgradeAllRegistered();
		}
	}

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-card id="aonConfigurationUserCard" title="USUARIO"></aon-card>
			<aon-card id="aonConfigurationUserSecurityCard" title="PERMISOS"></aon-card>
		`;
		this.build();
  }

	initUser() {
		let user = this.getAttribute('user') ? JSON.parse(this.getAttribute('user')) : undefined;
		let aonUserName = document.getElementById('aonConfigurationUserCardName');
		aonUserName.setAttribute('value', user && user.name ? user.name : '');
		let aonUserSurname = document.getElementById('aonConfigurationUserCardSurname');
		aonUserSurname.setAttribute('value', user && user.surname ? user.surname : '');
		let aonUserDocument = document.getElementById('aonConfigurationUserCardDocument');
		aonUserDocument.setAttribute('value', user && user.document ? user.document : '');
		let aonUserPhone = document.getElementById('aonConfigurationUserCardPhone');
		aonUserPhone.setAttribute('value', user && user.phone ? user.phone : '');
		let aonUserEmail = document.getElementById('aonConfigurationUserCardEmail');
		aonUserEmail.setAttribute('value', user && user.email ? user.email : '');
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

		let email = document.getElementById('aonConfigurationUserCardEmail');
		email.onChange(() => {
			let user = this.getAttribute('user') ? JSON.parse(this.getAttribute('user')) : {};
			user.email = email.getAttribute('value');
			setUser(user).then(r => {
				alert('aaaaaaaaaaaaa');
				alert(r);
			});

		});

		let card2 = document.getElementById('aonConfigurationUserSecurityCard');
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

	getApp(app) {
		switch(app.toLowerCase()){
			case Apps.INVOICE.app:
				return Apps.INVOICE;
			case Apps.DOCUMENTAL.app:
				return Apps.DOCUMENTAL;
			case Apps.HELPDESK.app:
				return Apps.HELPDESK;
			case Apps.ACCOUNTING.app:
				return Apps.ACCOUNTING;
			case Apps.FISCAL.app:
				return Apps.FISCAL;
			case Apps.PAYROLL.app:
				return Apps.PAYROLL;
			case Apps.OCR.app:
				return Apps.OCR;
			case Apps.AIO.app:
				return Apps.AIO;
			case Apps.SELFCONTA.app:
				return Apps.SELFCONTA;
			case Apps.SALTRA.app:
				return Apps.SALTRA;
			case Apps.BIDOQ.app:
				return Apps.BIDOQ;
			case Apps.ALMA.app:
				return Apps.ALMA;
			case Apps.LEARNING.app:
				return Apps.LEARNING;
		}
	}

}

window.customElements.define('aon-user', AonUser);
