import {AonElement} from '../../components/AonElement.js';
import {getDomainApps, getUser, getUserAppRole, setUserAppRole, setUser, deleteUser,
	 changePassword, getAuth, getDomainUserRoles} from  '../../services/service.js';
import {AllApps, EnterpriseApps, EmployeeApps, getApp} from  '../../services/app.js';
import {ToolbarType} from '../../models/enums.js';
import {UserAction} from './userEnums.js';
import {DomainUserRoles} from '../../models/DomainUserRoles.js';

import '../../components/aon-card.js';
import '../../components/aon-icon.js';
import '../../components/aon-input.js';
import '../../components/aon-select.js';
import '../../components/aon-switch.js';
import '../../components/aon-toolbar.js';

import * as CONSTANT from "../../environments/constants.js";
import * as MSG from "../../environments/msg.js";
import * as MATERIAL_ICONS from "../../environments/materialIcons.js";

export class AonUser extends AonElement {

	SWITCH;
	SELECT;
	TOOLBAR;

	_user;
	dur;
	apps;

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

	get showToolbar() {
		return this.getAttribute('showToolbar');
	}

	set showToolbar(showToolbar) {
		this.setAttribute('showToolbar', showToolbar);
	}

	get autosave() {
		return this.getAttribute('autosave');
	}

	set autosave(autosave) {
		this.setAttribute('autosave', autosave);
	}

	get onlyAuth() {
		return this.getAttribute('onlyAuth');
	}

	set onlyAuth(onlyAuth) {
		this.setAttribute('onlyAuth', onlyAuth);
	}

	attributeChangedCallback(name, oldValue, newValue) {
		this.initialize();
		if('user' === name){
			// this.initUser();
		}
		if('company' === name) {
			// let company = this.getAttribute('company') ? JSON.parse(this.getAttribute('company')) : undefined;
		}
	}

	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
		this._user = this.getAttribute('user') ? JSON.parse(this.getAttribute('user')) : {};

		this.innerHTML = `
			<aon-toolbar id="${this.TOOLBAR}" type="${ToolbarType.SECONDARY}" title="${MSG.AON_MSG_USER}"> </aon-toolbar>
			<div style="display:flex;width:100%;">
				<div id="aonConfigurationUserDiv" class="aonSubContent" style="width:50%;">
					<aon-card id="aonConfigurationUserCard"  title="${MSG.AON_MSG_USER}"></aon-card>
					<aon-card id="aonConfigurationUserInfoCard" title="${MSG.AON_MSG_ADDITIONAL_INFORMATION}"></aon-card>
				</div>
				<aon-card id="aonConfigurationUserSecurityCard" style="width:50%;" title="${MSG.AON_MSG_PERMISSIONS}"></aon-card>
			</div>
		`;

		getDomainUserRoles({}).then(r => {
			this.dur = new DomainUserRoles(r);
			this.buildUserToolbar();
			this.build();
			this.initUser();
			this.initApps();
		});
  }

	initialize() {
		this.id = this.id || 'aonUser';
		this.SWITCH = this.SWITCH || this.id + 'Switch';
		this.SELECT = this.SELECT || this.id + 'Select';
		this.TOOLBAR = this.TOOLBAR || this.id + 'Toolbar';
		this.apps = this.apps || [];
	}

	getDur() {
		return this.dur;
	}

	initApps() {
		this.clearElement('aonUserRoleTable');
		if(this.isPersonalizado()) {
			this.buildAppSelect(undefined);
		}
		this.apps.forEach((app, i) => {
			let application = getApp(app);
			if(application){
				this.buildAppSelect(application);
			}
		});
	}

	initUser() {
		let user = this.getAttribute('user') ? JSON.parse(this.getAttribute('user')) : {};
		this._user = user;
		this.buildUserToolbar();
		let aonUserName = document.getElementById('aonConfigurationUserCardName');
		if(aonUserName)
			aonUserName.setAttribute('value', user && user.name && user.email ? user.name : '');
		let aonUserSurname = document.getElementById('aonConfigurationUserCardSurname');
		if(aonUserSurname)
			aonUserSurname.setAttribute('value', user && user.surname ? user.surname : '');
		let aonUserDocument = document.getElementById('aonConfigurationUserCardDocument');
		if(user && user.document && !user.document.isEmpty()) {
			aonUserDocument.disabled = true;
			aonUserDocument.value = user.document;
		} else aonUserDocument.value = '';
		let aonUserPhone = document.getElementById('aonConfigurationUserCardPhone');
		aonUserPhone.setAttribute('value', user && user.phone ? user.phone : '');
		let aonUserEmail = document.getElementById('aonConfigurationUserCardEmail');
		if(user && user.email && !user.email.isEmpty()) {
			aonUserEmail.disabled = true;
			aonUserEmail.value = user.email;
		} else aonUserEmail.value = '';

		let card2 = document.getElementById('aonConfigurationUserSecurityCard');
		card2.setVisible(this.hasSecurity());

		if(user && this.hasAttribute('showApps')) {
			this.buildPermissionButtons();
			for (let key in AllApps){
				if(this.hasApp(AllApps[key])){
					this.apps.push(AllApps[key].app);
				}
			}
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

	buildPermissionButtons() {
		let card2 = this.getElement('aonConfigurationUserSecurityCard');
		card2.cleanSection2();
		card2.addTitleButton('Personalizado', MATERIAL_ICONS.TUNE, this.isPersonalizado(), () => {
			let user = this.getAttribute('user') ? JSON.parse(this.getAttribute('user')) : undefined;

			let enterprise = { role: 'ENTERPRISE', active: false, user: user.id};
			let employee = { role: 'EMPLOYEE', active: false, user: user.id};
			this.saveModeRole([enterprise, employee]);
		});

		card2.addTitleButton('Empresa', MATERIAL_ICONS.BUSINESS, this.isEnterprise(), () => {
			let user = this.getAttribute('user') ? JSON.parse(this.getAttribute('user')) : undefined;

			let roles = [{ role: 'ENTERPRISE', active: true, user: user.id}];
			this.apps.forEach((app, i) => {
				let application = getApp(app);

				if(application && EnterpriseApps.includes(application.app)){
					let rol1 = {app:application.app, role: application.app.toUpperCase(), active: true, user: user.id};
					let rol2 = {app:application.app, role: application.app.toUpperCase() + '_PORTAL', active: true, user: user.id};
					roles.push(rol1);
					roles.push(rol2);
				}
			});
			this.saveModeRole(roles);
		});

		card2.addTitleButton('Empleado', MATERIAL_ICONS.PERSON, this.isEmployee(), () => {
			let user = this.getAttribute('user') ? JSON.parse(this.getAttribute('user')) : undefined;

			let enterprise = { role: 'ENTERPRISE', active: false, user: user.id};
			let employee = { role: 'EMPLOYEE', active: true, user: user.id};
			let roles = [enterprise, employee];
			this.apps.forEach((app, i) => {
				let application = getApp(app);
				if(application && EmployeeApps.includes(application.app)){
					let rol = {app:application.app, role: application.app.toUpperCase(), active: true, user: user.id};
					roles.push(rol);
				}
			});
			this.saveModeRole(roles);
		});
	}

	saveModeRole(roles) {
		let user = this.getAttribute('user') ? JSON.parse(this.getAttribute('user')) : undefined;
		setUserAppRole({roles}).then(() => {
			getUserAppRole({user:user.id}).then(r => {
				user.roles = r;
				this._user.roles = r;
				this.setAttribute('user', JSON.stringify(user));
				this.initApps();
				this.buildPermissionButtons();
			});
		});
	}


	buildUserToolbar() {
		let userToolbar = this.getElement(this.TOOLBAR);
		if(!this.hasAttribute('showToolbar') || this.isMobile())
			userToolbar.style.display = 'none';
		userToolbar.removeButtons();

		if(!this.isAutosave())
			userToolbar.addButton2(UserAction.SAVE, () => this.save());
		if(this._user.id)
			userToolbar.addButton2(UserAction.DELETE, () => this.delete());
		userToolbar.addButton2(UserAction.BACK, () => this.back());
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
			if(this.isAutosave()){
				this.save();
			}
		});

		let surname = document.getElementById('aonConfigurationUserCardSurname');
		surname.onChange(() => {
			this._user.surname= surname.value;
			if(this.isAutosave()){
				this.save();
			}
		});

		let doc = document.getElementById('aonConfigurationUserCardDocument');
		doc.onChange(() => {
			this._user.document= doc.getAttribute('value');
			if(this.isAutosave()){
				this.save();
			}
		});

		let phone = document.getElementById('aonConfigurationUserCardPhone');
		phone.onChange(() => {
			this._user.phone= phone.getAttribute('value');
			if(this.isAutosave()){
				this.save();
			}
		});

		let email = document.getElementById('aonConfigurationUserCardEmail');
		email.onChange(() => {
			this._user.email = email.getAttribute('value');
			this._user.share = this.hasAttribute('share');
			if(this.isAutosave()){
				this.save();
			} else {
				getAuth({email: this._user.email}).then((auth) => {
					if(auth.uuid){
						this._user.name = auth.name || "";
						this._user.surname = auth.surname || "";
						this._user.document = auth.document || "";
						this._user.phone = auth.phone || "";
						this.setAttribute('user', JSON.stringify(this._user));
					}
				});
			}
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
			this.initApps();
		}).catch(e => {
			if(!this.isOnlyAuth()) {
				let aonApplication = document.querySelector('aon-application');
				let toast = this.getElement(aonApplication.TOAST);
				toast.start(JSON.parse(e));
			}
		});
	}

	delete() {
		let aonApplication = document.querySelector('aon-application');
    let d = document.getElementById(aonApplication.DIALOG);
    d.clear();
    if(!this.isMobile()) d.width = '400px';
    d.setTitle(MSG.AON_MSG_DELETE);
    d.setContentHTML(`Estás seguro de eliminar el usuario`);
    d.addAcceptAction(() => {
			let data = { user: this._user.id};
			deleteUser(data).then(() => this.back());
    });
    d.open();
	}

	back() {
		let aonApplication = document.querySelector('aon-application');
		aonApplication.setContentHTML("<aon-user-list> </aon-user-list>");
	}

	buildAppSelect(app) {
		if((this.isEmployee() && EmployeeApps.includes(app.app))
	 			|| (this.isEnterprise() && EnterpriseApps.includes(app.app))
				|| this.isPersonalizado()){
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
				let rolePortal = app ? app.app.toUpperCase() : 'ADMIN';
				let roleA = {
					app: app ? app.app : 'ADMIN',
					role: rolePortal,
					user: user.id,
					active: aonSwitch.isChecked()
				};
				let roles = [roleA];

				if (app && app.access && aonSwitch.isChecked() && this.isPersonalizado()) {
					let roleManager = app.app.toUpperCase() + '_MANAGER';
					let roleB = {
						app: app ? app.app : 'ADMIN',
						role: roleManager,
						user: user.id,
						active: aonSwitch.isChecked()
					};
					roles.push(roleB);
				}

				if (app && app.access && aonSwitch.isChecked() && this.isEnterprise()) {
					let rolePortal = app.app.toUpperCase() + '_PORTAL';
					let roleC = {
						app: app ? app.app : 'ADMIN',
						role: rolePortal,
						user: user.id,
						active: aonSwitch.isChecked()
					};
					roles.push(roleC);
				}

				setUserAppRole({roles}).then(() => {
					getUserAppRole({user:user.id}).then(r => {
						// if(!aonSwitch.isChecked() && r.contains(role.app)) {
						// 	r.forEach((item, i) => {
						// 		if(item == role.app){
						// 			r.splice(i, 1);
						// 		}
						// 	});
						// }

						user.roles = r;
						this._user.roles = r;
						this.setAttribute('user', JSON.stringify(user));
						if(!app) {
							this.initApps();
						}
						if(app && app.access && aonSwitch.isChecked() && this.isPersonalizado()) {
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

			if(app && app.access && active && this.isPersonalizado()) {
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
			let roles = [];
			let rol = app.app.toUpperCase()
			let manager = app.app.toUpperCase() + '_MANAGER';
			let portal = app.app.toUpperCase() + '_PORTAL';
			let role = {

			};
			if('Asesor' === value){
				roles.push({
					app: app ? app.app : 'ADMIN',
					user: user.id,
					role: manager,
					active: true
				});
			} else if('Empresa' === value) {
				roles.push({
					app: app ? app.app : 'ADMIN',
					user: user.id,
					role: manager,
					active: false
				});
				if(app.access.length > 2) {
					roles.push({
						app: app ? app.app : 'ADMIN',
						user: user.id,
						role: portal,
						active: true
					});
				}
			} else if('Empleado' === value) {
				roles.push({
					app: app ? app.app : 'ADMIN',
					user: user.id,
					role: manager,
					active: false
				});
				roles.push({
					app: app ? app.app : 'ADMIN',
					user: user.id,
					role: portal,
					active: false
				});
			}
			setUserAppRole({roles});
		}
	}

	isAutosave() {
		return this.hasAttribute('autosave') && this.getAttribute('autosave') != 'false';
	}

	isOnlyAuth() {
		return this.hasAttribute('onlyAuth') && this.getAttribute('onlyAuth') != 'false';
	}

	isShowApps() {
		return this.hasAttribute('showApps') && this.getAttribute('showApps') != 'false'
	}

	isAdmin() {
		console.log(JSON.stringify(this._user.roles));
		return this._user.roles && this._user.roles.includes('ADMIN');
	}

	isEmployee() {
		return this._user.roles && this._user.roles.includes('EMPLOYEE') && !this.isEnterprise();
	}

	isEnterprise() {
		return this._user.roles && this._user.roles.includes('ENTERPRISE');
	}

	isPersonalizado() {
		return !this.isEmployee() && !this.isEnterprise();
	}

	hasSecurity() {
		return this.hasAttribute('showApps') && this._user != null
				&& this._user.email != null && !this._user.email.isEmpty()
				&& this._user.id != null;
	}

	hasApp(app) {
		if(AllApps.ACCOUNTING.app === app.app)
			return this.getDur().hasAccounting();
		else if(AllApps.FISCAL.app === app.app)
			return this.getDur().hasFiscal();
		else if(AllApps.PAYROLL.app === app.app)
			return this.getDur().hasPayroll();
		else if(AllApps.COMUNICA.app === app.app)
			return this.getDur().hasComunica();
		else if(AllApps.DOCUMENTAL.app === app.app)
			return this.getDur().hasDocumental();
		else if(AllApps.TIMECONTROL.app === app.app)
			return this.getDur().hasTimeControl();
		else if(AllApps.INVOICE.app === app.app)
			return this.getDur().hasInvoice();
		else if(AllApps.MESSENGER.app === app.app)
			return this.getDur().hasMessenger();
		else return this.getDur().hasApp(app.app.toUpperCase());
	}
}

window.customElements.define('aon-user', AonUser);
