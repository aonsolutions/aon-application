import {AonElement} from '../../components/AonElement.js';
import {getDomainApps, getUser, getUserAppRole, setUserAppRole, setUser, changePassword} from  '../../services/service.js';
import {getApp} from  '../../services/app.js';

import '../../components/aon-card.js';
import '../../components/aon-icon.js';
import '../../components/aon-input.js';
import '../../components/aon-select.js';
import '../../components/aon-switch.js';

export class AonUser extends AonElement {

	SWITCH;
	SELECT;

	_user;

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

	get showApps() {
		return this.getAttribute('showApps');
	}

	set showApps(showApps) {
		this.setAttribute('showApps', showApps);
	}

	get showPassword() {
		return this.getAttribute('showPassword');
	}

	set showPassword(showPassword) {
		this.setAttribute('showPassword', showPassword);
	}

	get showInfo() {
		return this.getAttribute('showInfo');
	}

	set showInfo(showInfo) {
		this.setAttribute('showInfo', showInfo);
	}

	attributeChangedCallback(name, oldValue, newValue) {
		if('user' === name){
			this.initUser();
		}
		if('company' === name) {
			// let company = this.getAttribute('company') ? JSON.parse(this.getAttribute('company')) : undefined;
		}

		if('apps' === name) {
			this.initApps();
		}
	}

	constructor () {
		super();
		this.id = this.id || 'aonUser';
		this.SWITCH = this.id + 'Switch';
		this.SELECT = this.id + 'Select';

		this._user = this.getAttribute('user') ? JSON.parse(this.getAttribute('user')) : {};
	}

	connectedCallback () {
		this.innerHTML = `
			<div id="aonConfigurationUserDiv" style="width:50%;">
				<aon-card id="aonConfigurationUserCard"  title="USUARIO"></aon-card>
				<aon-card id="aonConfigurationUserInfoCard" title="INFORMACIÓN ADICIONAL"></aon-card>
			</div>
			<aon-card id="aonConfigurationUserSecurityCard" style="width:50%;" title="PERMISOS"></aon-card>
		`;
		this.build();
  }

	initApps() {
		let apps = this.getAttribute('apps') ? JSON.parse(this.getAttribute('apps')) : undefined;
		this.clearElement('aonUserRoleTable');
		this.buildAppSelect(undefined);
		apps.forEach((app, i) => {
			let application = getApp(app);
			if(application){
				this.buildAppSelect(application);
			}
		});
	}

	initUser() {
		let user = this.getAttribute('user') ? JSON.parse(this.getAttribute('user')) : {};
		this._user = user;
		let aonUserName = document.getElementById('aonConfigurationUserCardName');
		aonUserName.setAttribute('value', user && user.name && user.email ? user.name : '');
		let aonUserSurname = document.getElementById('aonConfigurationUserCardSurname');
		aonUserSurname.setAttribute('value', user && user.surname ? user.surname : '');
		let aonUserDocument = document.getElementById('aonConfigurationUserCardDocument');
		aonUserDocument.setAttribute('value', user && user.document ? user.document : '');
		let aonUserPhone = document.getElementById('aonConfigurationUserCardPhone');
		aonUserPhone.setAttribute('value', user && user.phone ? user.phone : '');
		let aonUserEmail = document.getElementById('aonConfigurationUserCardEmail');
		aonUserEmail.setAttribute('value', user && user.email ? user.email : '');

		let card2 = document.getElementById('aonConfigurationUserSecurityCard');
		card2.setVisible(this.hasSecurity());
		if(user && !this.hasAttribute('apps') && this.hasAttribute('showApps')) {
			getDomainApps().then(apps => {
				this.setAttribute('apps', JSON.stringify(apps));
			});
		}

		if(this.hasAttribute('showInfo')) {
			let data = {
				user: this._user.id
			}
			getUser(data).then(usr => {
				this.getElement('aonConfigurationUserInfoLogin').value = usr.login;
				this.getElement('aonConfigurationUserInfoAON').checked = usr.newAon;
			});
		}
	}

	build() {
		let company = this.getAttribute('company') ? JSON.parse(this.getAttribute('company')) : undefined;
		let user = this.getAttribute('user') ? JSON.parse(this.getAttribute('user')) : undefined;

		let card = document.getElementById('aonConfigurationUserCard');
		let html = `<form action="#" class="aon-margin-0">
				<aon-input class="aonWidth100" id="aonConfigurationUserCardEmail" description="Email" value=""></aon-input>
			</form>
			<form action="#" class="aon-margin-0">
				<aon-input class="aonWidth25" id="aonConfigurationUserCardName" description="Nombre" value=""></aon-input>
				<aon-input class="aonWidth75" id="aonConfigurationUserCardSurname" description="Apellidos" value=""></aon-input>
			</form>
			<form action="#" class="aon-margin-0">
				<aon-input class="aonWidth50" id="aonConfigurationUserCardDocument" description="DNI/NIE" value=""></aon-input>
				<aon-input class="aonWidth50" id="aonConfigurationUserCardPhone" description="Teléfono Móvil" value=""></aon-input>
			</form>`;
		if(this.hasAttribute('showPassword')) {
			html = html +
			`<form action="#" class="aon-margin-0">
				<table style="width:100%">
				<tr>
					<td>
						<aon-input  id="aonConfigurationUserCardPassword" type="password" description="Contraseña" value="12345678" disabled="true"></aon-input>
					</td>
					<td style="padding-bottom:20px; width:40px">
						<aon-icon-button id="aonConfigurationUserCardPasswordEdit" icon="edit"></aon-icon-button>
					</td>
				</tr>
				<table>
			</form>
			`;
		}
		card.setContentHTML(html);

		let name = document.getElementById('aonConfigurationUserCardName');
		name.onChange(() => {
			this._user.name = name.value;
			this.save();
		});

		let surname = document.getElementById('aonConfigurationUserCardSurname');
		surname.onChange(() => {
			this._user.surname= surname.value;
			this.save();
		});

		let doc = document.getElementById('aonConfigurationUserCardDocument');
		doc.onChange(() => {
			this._user.document= doc.getAttribute('value');
			this.save();
		});

		let phone = document.getElementById('aonConfigurationUserCardPhone');
		phone.onChange(() => {
			this._user.phone= phone.getAttribute('value');
			this.save();
		});

		let email = document.getElementById('aonConfigurationUserCardEmail');
		email.onChange(() => {
			this._user.email = email.getAttribute('value');
			this._user.share = this.hasAttribute('share');
			this.save();
		});

		let card2 = document.getElementById('aonConfigurationUserSecurityCard');
		card2.setVisible(this.hasSecurity());

		let table = document.createElement('table');
		table.setAttribute('id', 'aonUserRoleTable')
		table.style.width = '100%';
		card2.setContent(table);

		let editPassword = this.getElement('aonConfigurationUserCardPasswordEdit');
		if(editPassword) {
			editPassword.addEventListener('click', (e) => {
				e.preventDefault();
				this.editPassword()
			});
		}
		let card3 = document.getElementById('aonConfigurationUserInfoCard');
		card3.setVisible(this.hasAttribute('showInfo'));
		if(this.hasAttribute('showInfo')) {
			card3.setContentHTML = '';
			let table = document.createElement('table');
			card3.setContent(table);
			table.style.width = '100%';
			let tr = document.createElement('tr');
			table.appendChild(tr);

			let td1 = document.createElement('td');
			tr.appendChild(td1)
			td1.innerHTML = `<aon-input id="aonConfigurationUserInfoLogin" description="Identificador" disabled="true"></aon-input>`;

			let td2 = document.createElement('td');
			tr.appendChild(td2)
			td2.innerHTML = `<aon-switch id="aonConfigurationUserInfoAON" title="nuevo AON" disabled="true"></aon-switch>`;
		}

		if(this.isMobile()) {
			let div = this.getElement("aonConfigurationUserDiv");
			if(div) div.style.width = '100%'
			card2.style.width = '100%';
		}

	}

	editPassword() {
		let content = this.parentElement;
		let application = content.parentElement;
		let d = document.getElementById(application.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle("Cambiar Contraseña");
		d.setContentHTML(`
			<aon-input  id="aonConfigurationUserCardOldPassword" type="password" description="Contraseña Actual" value=""></aon-input>
			<aon-input  id="aonConfigurationUserCardNewPassword" type="password" description="Nueva Contraseña" value=""></aon-input>
		`);
		d.addAcceptAction(() => {
			let oldPassword = this.getElement('aonConfigurationUserCardOldPassword').value;
			let newPassword = this.getElement('aonConfigurationUserCardNewPassword').value;
			changePassword({oldPassword, newPassword});
		});
		d.open();
	}

	save() {
		setUser(this._user).then(r => {
			this.setAttribute('user', JSON.stringify(r));
			this._user = r;
			getDomainApps().then(apps => {
				this.setAttribute('apps', JSON.stringify(apps));
				this.initApps();
			});

		}).catch(e => {
			let aonApplication = document.querySelector('aon-application');
			let toast = this.getElement(aonApplication.TOAST);
			toast.start(JSON.parse(e));
		});
	}

	buildAppSelect(app) {
		let table = this.getElement('aonUserRoleTable');
		let user = this.getAttribute('user') ? JSON.parse(this.getAttribute('user')) : undefined;

		let tr = document.createElement('tr');
		table.appendChild(tr);
		let td1 = document.createElement('td');
		td1.style.width = '30px';
		td1.style.height = '40px';
		tr.appendChild(td1);

		if(!app) {
			let icon = document.createElement('i');
			icon.className = 'material-icons';
			icon.innerHTML = 'security';
			td1.appendChild(icon);
		} else if(app.logo){
			let img = document.createElement('img');
			img.style.width = '24px';
			img.src = app.logo;
			td1.appendChild(img);
		} else {
			td1.innerHTML =  `<aon-icon icon="${app.icon}" color="${app.color}" size="30px"></aon-icon>`;
		}

		let td2 = document.createElement('td');
		td2.style.height = '40px';
		tr.appendChild(td2)

		let span2 = document.createElement('span');
		span2.style.padding = '10px';
		span2.style.fontWeight = 'bold';
		span2.style.color = '#5f6368';
		span2.innerHTML = app ? app.title : 'Administrador';
		td2.appendChild(span2);

		let td3 = document.createElement('td');
		td3.style.height = '40px';
		tr.appendChild(td3);
		let id = this.SWITCH + (app ? app.app : 'ADMIN');
		td3.innerHTML = `<aon-switch id="${id}"> </aon-switch>`;
		let aonSwitch = this.getElement(id);
		let active = this.isAdmin() || this.isApp(app ? app.app : 'ADMIN');
		aonSwitch.checked = active;

		let td4 = document.createElement('td');
		tr.appendChild(td4);

		aonSwitch.addEventListener('change', () => {
			let roles = [app ? app.app.toUpperCase() : 'ADMIN'];

			if (app && app.access && aonSwitch.isChecked()) {
				let roleManager = app.app.toUpperCase() + '_MANAGER';
				roles.push(roleManager);
			}

			let role = {
				domain: localStorage.getItem('aon_domain_name'),
				app: app ? app.app : 'ADMIN',
				roles,
				user: user.id,
				active: aonSwitch.isChecked()
			};

			setUserAppRole(role).then(() => {
				getUserAppRole({user:user.id}).then(r => {
					if(!role.active && r.contains(role.app)) {
						r.forEach((item, i) => {
							if(item == role.app){
								r.splice(i, 1);
							}
						});
					}

					user.roles = r;
					this._user.roles = r;
					this.setAttribute('user', JSON.stringify(user));
					if(!app) {
						this.initApps();
					}
					if(app && app.access && aonSwitch.isChecked()) {
						let selectId = this.SELECT + (app ? app.app : 'ADMIN');
						td4.innerHTML = `<aon-select id="${selectId}" title="Modo de Acceso"></aon-select>`;
						let select = this.getElement(selectId);
						select.options = JSON.stringify(app.access);
						select.value = this.getAccess(app);
						select.addEventListener('change', () => {
							this.accessAction(app, select.value);
						});
					} else {
						td4.innerHTML = '';
					}
				});
			});
		});

		if(app && app.access && active) {
			let selectId = this.SELECT + (app ? app.app : 'ADMIN');
			td4.innerHTML = `<aon-select id="${selectId}" title="Modo de Acceso"></aon-select>`;
			let select = this.getElement(selectId);

			select.options = JSON.stringify(app.access);
			select.value = this.getAccess(app);
			select.addEventListener('change', () => {
				this.accessAction(app, select.value);
			});
		}
	}

	isApp(app) {
		let user = this.getAttribute('user') ? JSON.parse(this.getAttribute('user')) : undefined;
		return user.roles && user.roles.includes(app.toUpperCase())
	}

	getAccess(app) {
		let user = this.getAttribute('user') ? JSON.parse(this.getAttribute('user')) : undefined;
		let manager = app.app.toUpperCase() + '_MANAGER';
		let portal = app.app.toUpperCase() + '_PORTAL';
		if(this.isAdmin() || (user.roles && user.roles.includes(manager))){
			return 'Asesor';
		} else if(user.roles && user.roles.includes(portal)) {
			return 'Empresa';
		} else if(app.access.length < 3) {
			return 'Empresa';
		} else return 'Empleado';
	}

	accessAction(app, value) {
		if(app) {
			let user = this.getAttribute('user') ? JSON.parse(this.getAttribute('user')) : undefined;
			let role = {
				domain: localStorage.getItem('aon_domain_name'),
				app: app ? app.app : 'ADMIN',
				user: user.id,
			};
			let rol = app.app.toUpperCase()
			let manager = app.app.toUpperCase() + '_MANAGER';
			let portal = app.app.toUpperCase() + '_PORTAL';
			if('Asesor' === value){
				role.roles = [manager];
				role.active = true;
				setUserAppRole(role);
			} else if('Empresa' === value) {
				role.roles = [manager];
				role.active = false;
				setUserAppRole(role);
				if(app.access.length > 2) {
					role.roles = [portal];
					role.active = true;
					setUserAppRole(role);
				}
			} else if('Empleado' === value) {
				role.roles = [manager, portal];
				role.active = false;
				setUserAppRole(role);
			}
		}
	}

	isAdmin() {
		console.log(JSON.stringify(this._user.roles));
		return this._user.roles && this._user.roles.includes('ADMIN');
	}

	hasSecurity() {
		return this.hasAttribute('showApps') && this._user != null
				&& this._user.email != null && !this._user.email.isEmpty()
				&& this._user.id != null;
	}



}

window.customElements.define('aon-user', AonUser);
