import { AonElement } from '../../components/AonElement.js';
import { saveUser, deleteUser, changePassword, getAuth, getDomainUserRoles, sendUserInfoEmail, updateDurDefinedUsers, getUserRoles, getBookingDomainUserRoles } from  '../../services/service.js';
import { AllApps, EnterpriseApps, EmployeeApps, getApp } from  '../../services/app.js';
import { Role, ToolbarType } from '../../models/enums.js';
import { DomainUserRoles } from '../../models/DomainUserRoles.js';

import '../../components/aon-card.js';
import '../../components/aon-icon.js';
import '../../components/aon-input.js';
import '../../components/aon-select.js';
import '../../components/aon-switch.js';
import '../../components/aon-toolbar.js';

import { MSG, MATERIAL_ICONS, CONSTANT, TAG, CSS, COLORS, EVENT } from '../../environments/environments.js';

import * as ACTION from '../actions.js';
import { AonMobileUserList } from './aon-mobile-user-list.js';
import { AonUserList } from './aon-user-list.js';
import { getNextUser, getPreviousUser, getUsers, updateUser, deleteUserCache } from './UserCache.js';
import { AonSwitch } from '../../components/aon-switch.js';
import { AonToolbar } from '../../components/aon-toolbar.js';
import { AonCard } from '../../components/aon-card.js';
import { createInput } from '../../components/CreateComponent.js';

export class AonUser extends AonElement {

	SWITCH;
	SELECT;
	TOOLBAR;
	CONTENT;

	user;
	dur;
	apps;

	parent;

	get showApps() {
		return this.getAttribute(CONSTANT.SHOW_APPS);
	}

	set showApps(showApps) {
		this.setAttribute(CONSTANT.SHOW_APPS, showApps);
	}

	get share() {
		return this.getAttribute(CONSTANT.SHARE);
	}

	set share(share) {
		this.setAttribute(CONSTANT.SHARE, share);
	}

	get showPassword() {
		return this.getAttribute(CONSTANT.SHOW_PASSWORD);
	}

	set showPassword(showPassword) {
		this.setAttribute(CONSTANT.SHOW_PASSWORD, showPassword);
	}

	get showToolbar() {
		return this.getAttribute(CONSTANT.SHOW_TOOLBAR);
	}

	set showToolbar(showToolbar) {
		this.setAttribute(CONSTANT.SHOW_TOOLBAR, showToolbar);
	}

	get autosave() {
		return this.getAttribute(CONSTANT.AUTOSAVE);
	}

	set autosave(autosave) {
		this.setAttribute(CONSTANT.AUTOSAVE, autosave);
	}

	get onlyAuth() {
		return this.getAttribute(CONSTANT.ONLY_AUTH);
	}

	set onlyAuth(onlyAuth) {
		this.setAttribute(CONSTANT.ONLY_AUTH, onlyAuth);
	}

	constructor () {
		super();
	}

	connectedCallback () {
		this.init();
  	}

	build2() {
		this.buildToolbar();
		if(this.isMobile()) 
			this.buildMobileContent();
		else this.buildContent();
		
	}

	buildToolbar() {
		let toolbar = new AonToolbar();
		toolbar.id = this.TOOLBAR;
		toolbar.type = ToolbarType.SECONDARY;
		toolbar.title = MSG.USER;

		this.appendChild(toolbar);


		if(!this.hasAttribute('showToolbar')) toolbar.style.display = 'none';
		toolbar.removeButtons();
		if(!this.isOnlyAuth() && getUsers().length > 1) {
			toolbar.addButton2(ACTION.NEXT, () => this.next());
			toolbar.addButton2(ACTION.PREVIOUS, () => this.previous());
			toolbar.addSeparator();
		}
		if(!this.isOnlyAuth() && this.user && this.user.uuid)
			toolbar.addButton2(ACTION.SEND_EMAIL, () => this.sendEmail());
		if(!this.isAutosave())
			toolbar.addButton2(ACTION.SAVE, () => this.save());
		if(!this.isOnlyAuth() && this.user.id)
			toolbar.addButton2(ACTION.DELETE, () => this.delete());
		if(!this.isOnlyAuth()) 
			toolbar.addButton2(ACTION.BACK, () => this.back());
	}

	buildContent() {
		let content = this.createDiv(this.CONTENT, CSS.AON_SUB_CONTENT);
		content.style.display = 'flex';
		content.style.width = '100%';	
		this.appendChild(content);

		let div = this.createDiv("aonConfigurationUserDiv");
		div.style.width = '50%';
		content.appendChild(div);

		let userCardTitle = MSG.USER + (this.user.login ? ' (' + this.user.login + ')' : '');
		let userCard = this.createAonElement(new AonCard(), "aonConfigurationUserCard", userCardTitle);
		div.appendChild(userCard);

		if(this.hasSecurity()) {
			let securityCard = this.createAonElement(new AonCard(), "aonConfigurationUserSecurityCard", MSG.PERMISSIONS);
			content.appendChild(securityCard);
		}
	}

	buildMobileContent() {
		let content = this.createDiv(this.CONTENT, CSS.AON_MOBILE_SUB_CONTENT);
		this.appendChild(content);
	}

	init() {
		this.initialize();
		this.user = this.user || {};
		let login = this.user.login ? ' (' + this.user.login + ')' : '';
		this.innerHTML = this.isMobile() 
			? `
				<aon-toolbar id="${this.TOOLBAR}" type="${ToolbarType.SECONDARY}" title="${MSG.USER}"> </aon-toolbar>
				<div class="aonMobileSubContent">
					<aon-card id="aonConfigurationUserCard"  title="${MSG.USER + login}"></aon-card>
					<aon-card id="aonConfigurationUserSecurityCard" title="${MSG.PERMISSIONS}"></aon-card>
				</div>
			` 
			: `
				<aon-toolbar id="${this.TOOLBAR}" type="${ToolbarType.SECONDARY}" title="${MSG.USER}"> </aon-toolbar>
				<div style="display:flex;width:100%;" class="aonSubContent">
					<div id="aonConfigurationUserDiv" style="width:50%;">
						<aon-card id="aonConfigurationUserCard"  title="${MSG.USER + login}"></aon-card>
					</div>
					<aon-card id="aonConfigurationUserSecurityCard" style="width:50%;" title="${MSG.PERMISSIONS}"></aon-card>
				</div>
			`;

		getBookingDomainUserRoles({}, this.sessionData).then(r => {
			
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
		this.CONTENT = this.CONTENT || this.id + 'Content';
		this.apps = [];
		this.user = this.user || {};
	}

	getDur() {
		return this.dur;
	}

	initApps() {
		
		this.clearElementById('aonUserRoleTable');
		if(this.isPersonalizado()) {
			this.buildAppSelect(undefined);
		}
		this.buildDevSelect();
		this.apps.forEach((app, i) => {
			let application = getApp(app);
			if(application){
				this.buildAppSelect(application);
			}
		});
	}

	initUser() {
		this.user = this.user || {};
		
		this.buildUserToolbar();
		
		this.buildStatusRegistry();
		
		let aonUserName = document.getElementById('aonConfigurationUserCardName');
		if(aonUserName)
			aonUserName.setAttribute('value', this.user && this.user.name && this.user.email ? this.user.name : '');
		let aonUserSurname = document.getElementById('aonConfigurationUserCardSurname');
		if(aonUserSurname)
			aonUserSurname.setAttribute('value', this.user && this.user.surname ? this.user.surname : '');
		let aonUserDocument = document.getElementById('aonConfigurationUserCardDocument');
		aonUserDocument.value = this.user.document;

		let aonUserPhone = document.getElementById('aonConfigurationUserCardPhone');
		aonUserPhone.setAttribute('value', this.user && this.user.phone ? this.user.phone : '');
		let aonUserEmail = document.getElementById('aonConfigurationUserCardEmail');
		aonUserEmail.value = this.user.email;

		let card2 = document.getElementById('aonConfigurationUserSecurityCard');
		card2.setVisible(this.hasSecurity());

		if(this.user && this.hasAttribute('showApps')) {
			this.buildPermissionButtons();
			for (let key in AllApps) {
				if(this.hasApp(AllApps[key])) {
					this.apps.push(AllApps[key].app);
				}
			}
		}
	}
	
	buildStatusRegistry(){
		let card = document.getElementById('aonConfigurationUserCard');
		const id = "statusDiv";

		let statusDiv = this.getElement(id);
		if(statusDiv) statusDiv.remove();

		const title = this.user.active ? "Activo" : "Inactivo";

		let color = "green";
		if(this.user.active){
			color = "green"
		} else {
			color = COLORS.ORANGE
		}

		statusDiv = this.createElement(TAG.DIV);
		statusDiv.id = id;
		statusDiv.title = MSG.STATUS;
		statusDiv.style.display = "flex";
		statusDiv.style.alignItems = "center";
		statusDiv.style.columnGap = "5px";
		statusDiv.style.border = "1px solid";
		statusDiv.style.borderColor = "lightgray";
		statusDiv.style.borderRadius = "10px";
		statusDiv.style.padding = "4px";
		statusDiv.style.cursor = "pointer";
		card.addSection2(statusDiv);

		let statusBox = this.createElement(TAG.DIV);
		statusBox.style.width           = "10px";
		statusBox.style.height          = "10px";
		statusBox.style.borderRadius    = "50%";
		statusBox.style.marginTop       = "3px";
		statusBox.style.backgroundColor = color;
		statusDiv.appendChild(statusBox);

		let statusText = this.createElement(TAG.DIV);
		statusText.innerText = title;
		statusText.style.fontSize = "14px";
		statusText.style.fontWeight = "500";
		statusText.style.color = "#5f6368";
		statusDiv.appendChild(statusText);

		let iconArrowDown = this.createElement(TAG.DIV);
		iconArrowDown.style.fontSize  = "18px";
		iconArrowDown.className = CONSTANT.MATERIAL_ICONS;
		iconArrowDown.innerText = MATERIAL_ICONS.KEYBOARD_ARROW_DOWN;
		statusDiv.appendChild(iconArrowDown);

		statusDiv.addEventListener(EVENT.CLICK, () => this.getOptionsStatus(iconArrowDown));
	}

	getOptionsStatus(element){
		const top = element.getBoundingClientRect().top + 24;
		const left = element.getBoundingClientRect().left + 3;
		let d = this.getApplication().getOptionDialog();

		let options = [
			{ 
				name: "Activar", 
				value:"ACTIVE",
				icon:"toggle_on", 
				fn:()=> {
					this.user.active = true;
					this.save();
				}
			},
			{ 
				name: "Inactivar", 
				value:"INACTIVE",
				icon:"toggle_off", 
				fn:()=> {
					this.user.active = false;
					this.save();
				}
			}
		];

		options = options.filter(opt => opt.value!=(this.user.active ? "ACTIVE" : "INACTIVE") );
		
		d.setMenuOptions(options, top, left);
		d.open();
	}

	buildPermissionButtons() {
		let card2 = this.getElement('aonConfigurationUserSecurityCard');
		card2.cleanSection2();

		if(this.user.portal) {
			card2.addTitleButton('Empresa', MATERIAL_ICONS.BUSINESS, this.isEnterprise(), () => this.selectionEnterprisePortal());
			card2.addTitleButton('Empleado', MATERIAL_ICONS.PERSON, this.isEmployee(), () => this.selectionEmployeePortal());
		}
	}

	selectionPersonalizado() {
		let enterprise = { role: 'ENTERPRISE', active: false, user: this.user.id};
		let employee = { role: 'EMPLOYEE', active: false, user: this.user.id};
		this.updateRoles([enterprise, employee]);
	}

	selectionEnterprisePortal() {
		this.user.roles = [Role.ENTERPRISE];
		this.apps.forEach((app, i) => {
			let application = getApp(app);
			if(application && EnterpriseApps.includes(application.app)){
				this.user.roles.push(application.app.toUpperCase());
				this.user.roles.push(application.app.toUpperCase() + '_PORTAL');
			}
		});
		this.initApps();
		this.buildPermissionButtons();
	}

	selectionEmployeePortal() {
		this.user.roles = [Role.EMPLOYEE];
		this.apps.forEach((app, i) => {
			let application = getApp(app);
			if(application && EmployeeApps.includes(application.app)){
				this.user.roles.push(application.app.toUpperCase());
			}
		});
		this.initApps();
		this.buildPermissionButtons();
	}

	buildUserToolbar() {
		let toolbar = this.getElement(this.TOOLBAR);
		if(!this.hasAttribute('showToolbar'))
		toolbar.style.display = 'none';
		toolbar.removeButtons();
		if(!this.isOnlyAuth() && getUsers().length > 1) {
			toolbar.addButton2(ACTION.NEXT, () => this.next());
			toolbar.addButton2(ACTION.PREVIOUS, () => this.previous());
			toolbar.addSeparator();
		}
		if(!this.isOnlyAuth() && this.user && this.user.uuid)
			toolbar.addButton2(ACTION.SEND_EMAIL, () => this.sendEmail());
		if(!this.isAutosave())
			toolbar.addButton2(ACTION.SAVE, () => this.save());
		if(!this.isOnlyAuth() && this.user.id)
			toolbar.addButton2(ACTION.DELETE, () => this.delete());
		if(!this.isOnlyAuth()) 
			toolbar.addButton2(ACTION.BACK, () => this.back());
		
	}

	build() {
		let card = document.getElementById('aonConfigurationUserCard');
		let html = `<form action="#" class="aon-margin-0">
				<aon-input class="aonWidth100" id="aonConfigurationUserCardEmail" description="Email" value=""></aon-input>
			</form>
			<form action="#" class="aon-margin-0">
				<aon-input class="aonWidth25" id="aonConfigurationUserCardName" description="${MSG.NAME}" value=""></aon-input>
				<aon-input class="aonWidth75" id="aonConfigurationUserCardSurname" description="${MSG.SURNAME}" value=""></aon-input>
			</form>
			<form action="#" class="aon-margin-0">
				<aon-input class="aonWidth50" id="aonConfigurationUserCardDocument" description="DNI/NIE" value=""></aon-input>
				<aon-input class="aonWidth50" id="aonConfigurationUserCardPhone" description="Teléfono Móvil" value=""></aon-input>
			</form>
			`;
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
			this.user.name = name.value;
			if(this.isAutosave()){
				this.save();
			}
		});

		let surname = document.getElementById('aonConfigurationUserCardSurname');
		surname.onChange(() => {
			this.user.surname= surname.value;
			if(this.isAutosave()){
				this.save();
			}
		});

		let doc = document.getElementById('aonConfigurationUserCardDocument');
		doc.onChange(() => {
			this.user.document= doc.getAttribute('value');
			if(this.isAutosave()){
				this.save();
			}
		});

		let phone = document.getElementById('aonConfigurationUserCardPhone');
		phone.onChange(() => {
			this.user.phone= phone.getAttribute('value');
			if(this.isAutosave()){
				this.save();
			}
		});

		let email = document.getElementById('aonConfigurationUserCardEmail');
		email.onChange(() => {
			this.user.email = email.getAttribute('value');
			this.user.shared = this.hasAttribute('share');
			if(this.isAutosave()){
				this.save();
			} else {
				getAuth({email: this.user.email, reload: true}, this.sessionData).then((auth) => {
					if(auth.uuid){
						this.user.name = auth.name || '';
						this.user.surname = auth.surname || '';
						this.user.document = auth.document || '';
						this.user.phone = auth.phone || '';
						this.initUser();
					}
				});
			}
		});

		if(!this.isOnlyAuth()){
			let portal = new AonSwitch();
			portal.id = 'aonConfigurationUserCardPortal';
			portal.title = MSG.ONLY_PORTAL;
			portal.disabled = this.user.portal && !this.getDur().checkUsers();
			portal.checked = this.user.portal;
			portal.style.marginLeft = '5px';
			portal.addEventListener('change', () => {
				this.user.portal = !this.user.portal;
				this.buildPermissionButtons();
				if(this.user.portal) this.selectionEnterprisePortal();
				else this.selectionPersonalizado();
			});
			card.getContent().appendChild(portal);
		}

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
				this.editPassword();
			});
		}

		if(this.isMobile()) {
			let div = this.getElement("aonConfigurationUserDiv");
			if(div) div.style.width = '100%'
			card2.style.width = '100%';
		}
	}

	editPassword() {
		let application = document.querySelector('aon-application');
		let d = document.getElementById(application.DIALOG);
		d.clear();
		if(this.isMobile()) {
            d.type = "fullscreen";
        } else {
            d.width = '400px';
        }
		d.setTitle(MSG.CHANGE_PASSWORD);

		let div = document.createElement("div");

        let oldPassword = createInput("aonConfigurationUserCardOldPassword", "Contraseña");
        oldPassword.type = "password";
        div.appendChild(oldPassword);

	    let newPassword = createInput("aonConfigurationUserCardNewPassword", "Repetir Contraseña");
        newPassword.type = "password";
        div.appendChild(newPassword);

		d.setContent(div);

		d.addAcceptAction(() => {
			// if(newPassword && newPassword.length>5){
				changePassword({oldPassword:oldPassword.value, newPassword:newPassword.value}, this.sessionData).then(()=>{
					this.showToast({message:MSG.SAVED_DATA, type:CONSTANT.SUCCESS});
				}).catch(e=>this.showError(e))
			// } else {
			// 	this.showToast({message:"La contraseña debe tener al menos 6 carácter", type:CONSTANT.ERROR});
			// }

		});
		d.open();
	}

	save() {	
		if(!this.isOnlyAuth())
			updateUser(this.user);
		if(!this.user.portal && (!this.user.roles || this.user.roles.length == 0)) {
			this.user.portal = true;
			this.user.roles = [Role.ENTERPRISE];
		}
		
		saveUser(this.user, this.sessionData).then(r => {
			
			getUserRoles({user: this.user.id}, this.sessionData).then(roles => {
				this.user = r;
				this.user.roles = roles;
				
				let definedUsers = getUsers().filter(f => !f.portal).length;
				this.getDur().definedUsers = definedUsers;
				updateDurDefinedUsers(definedUsers, this.sessionData);
				this.init();
				
			});
			
			
		}).catch(e => this.showError(e));
	}

	delete() {
    	let d = this.getApplication().getDialog();
   	 	d.clear();
    	if(!this.isMobile()) d.width = '400px';
    	d.setTitle(MSG.DELETE);
   	 	d.setContentHTML(`Estás seguro de eliminar el usuario`);
    	d.addAcceptAction(() => {
			let data = { user: this.user.id};
			deleteUser(data, this.sessionData).then(() => {
				deleteUserCache();
				this.back()
			});
    	});
    	d.open();
	}

	back() {
		if(this.isMobile()) {
			this.getApplication().setContent(new AonMobileUserList());
		} else {
			let aonUserList = new AonUserList();
			aonUserList.sessionData = this.sessionData;
			aonUserList.parent = this.parent;
			aonUserList.setBack(true);
			if(this.parent) {
				this.parent.innerHTML = '';
				this.parent.appendChild(aonUserList);
			} else this.getApplication().setContent(aonUserList);
		}
	}

	next() {
		let user = getNextUser();
		this.changeUser(user);
	}

	previous() {
		let user = getPreviousUser();
		this.changeUser(user);
	}

	changeUser(user) {
		getUserRoles({user: user.id}, this.sessionData).then(roles => {
			user.roles = roles;
			this.setUser(user);
			this.init();
		});
	}

	sendEmail() {
		let d = this.getApplication().getDialog();
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.SEND);
		d.setContentHTML('Al notificar los datos de usuario se generará una nueva contraseña.');
		d.addAcceptAction(() => {
			sendUserInfoEmail(this.user, this.sessionData).then(success => {
				this.showToast({message: "Se ha enviado un mail a " + this.user.email + ", desde la cuenta " + success.fromEmail + ", con la contraseña nueva", type: CONSTANT.SUCCESS, delay: 3000});
			  }, err => {
				let jsonError = JSON.parse(err);
				this.showToast({message: jsonError.message, type: CONSTANT.ERROR, delay: 5000});
			  });
		});
		d.open();
	}
	
	buildDevSelect() {
		if(this.isBeta()) {
			let table = this.getElement('aonUserRoleTable');

			let tr = document.createElement('tr');
			table.appendChild(tr);
			let td1 = document.createElement('td');
			td1.style.width = '30px';
			td1.style.height = '40px';
			tr.appendChild(td1);	

			let icon = document.createElement('i');
			icon.className = 'material-icons';
			icon.innerHTML = 'code';
			td1.appendChild(icon);

			let td2 = document.createElement('td');
			td2.style.height = '40px';
			tr.appendChild(td2)

			let span2 = document.createElement('span');
			span2.style.padding = '10px';
			span2.style.fontWeight = 'bold';
			span2.style.color = '#5f6368';
			span2.innerHTML = 'Desarrollador';
			td2.appendChild(span2);

			let td3 = document.createElement('td');
			td3.style.height = '40px';
			tr.appendChild(td3);
			let id = this.SWITCH + 'DEV';
			td3.innerHTML = `<aon-switch id="${id}"> </aon-switch>`;
			let aonSwitch = this.getElement(id);
			let active = this.isDev();
			aonSwitch.checked = active;
			
			let td4 = document.createElement('td');
			tr.appendChild(td4);

			aonSwitch.addEventListener('change', () => {
				this.updateRoles([{
					app: 'DEV',
					role: 'DEV',
					user: this.user.id,
					active: aonSwitch.isChecked()
				}]);
			});

		}
	}
	
	buildAppSelect(app) {
		if((this.isEmployee() && EmployeeApps.includes(app.app))
	 			|| (this.isEnterprise() && EnterpriseApps.includes(app.app))
				|| this.isPersonalizado()){
			let table = this.getElement('aonUserRoleTable');

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
			} else if(app.symbol) {
				let icon = this.createSpan();
				icon.id = this.APP + app.app + 'Icon';
				icon.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
				icon.innerHTML = app.symbol;
				icon.style.color = app.color;
				icon.style.paddingTop = '5px';
				icon.style.paddingLeft = '4px';
				td1.appendChild(icon);
			} else {
				td1.innerHTML =  `<aon-icon icon="${app.icon}" color="${app.color}" size="30px"></aon-icon>`;
			}

			let td2 = document.createElement('td');
			td2.style.height = '40px';
			tr.appendChild(td2)

			let span2 = document.createElement('span');
			span2.style.padding = '10px';
			span2.style.fontWeight = 'bold';
			span2.style.fontSize = '14px';
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
			if(this.isAdmin() && app) aonSwitch.disabled = true;

			let td4 = document.createElement('td');
			tr.appendChild(td4);

			aonSwitch.addEventListener('change', () => {
				
				let rolePortal = app ? app.app.toUpperCase() : 'ADMIN';
				let roleA = {
					app: app ? app.app : 'ADMIN',
					role: rolePortal,
					user: this.user.id,
					active: aonSwitch.isChecked()
				};
				let roles = [roleA];

				if (app && app.access && aonSwitch.isChecked() && this.isPersonalizado()) {
					let roleManager = app.app.toUpperCase() + '_MANAGER';
					let roleB = {
						app: app ? app.app : 'ADMIN',
						role: roleManager,
						user: this.user.id,
						active: aonSwitch.isChecked()
					};
					roles.push(roleB);
				}

				if (app && app.access && this.isEnterprise()) {
					let rolePortal = app.app.toUpperCase() + '_PORTAL';
					let roleC = {
						app: app ? app.app : 'ADMIN',
						role: rolePortal,
						user: this.user.id,
						active: aonSwitch.isChecked()
					};
					roles.push(roleC);
				}

				this.updateRoles(roles);
			});

			if(app && app.access && active && this.isPersonalizado()) {
				let selectId = this.SELECT + (app ? app.app : 'ADMIN');
				td4.innerHTML = `<aon-select id="${selectId}" title="Modo de Acceso"></aon-select>`;
				let select = this.getElement(selectId);

				select.options = JSON.stringify(app.access);
				select.value = this.getAccess(app);
				if(this.isAdmin()) select.disabled = true;
				select.addEventListener('change', () => {
					this.accessAction(app, select.value);
				});
			}
		}
	}

	isApp(app) {
		return this.user && this.user.roles && this.user.roles.includes(app.toUpperCase());
	}

	getAccess(app) {
		let manager = app.app.toUpperCase() + '_MANAGER';
		let portal = app.app.toUpperCase() + '_PORTAL';
		if(this.isAdmin() || (this.user.roles && this.user.roles.includes(manager))){
			return 'Asesor';
		} else if(this.user.roles && this.user.roles.includes(portal)) {
			return 'Empresa';
		} else if(app.access.length < 3) {
			return 'Empresa';
		} else return 'Empleado';
	}

	accessAction(app, value) {
		if(app) {
			let roles = [];
			let rol = app.app.toUpperCase()
			let manager = app.app.toUpperCase() + '_MANAGER';
			let portal = app.app.toUpperCase() + '_PORTAL';
			let role = {

			};
			if('Asesor' === value){
				roles.push({
					app: app ? app.app : 'ADMIN',
					user: this.user.id,
					role: manager,
					active: true
				});
			} else if('Empresa' === value) {
				roles.push({
					app: app ? app.app : 'ADMIN',
					user: this.user.id,
					role: manager,
					active: false
				});
				if(app.access.length > 2) {
					roles.push({
						app: app ? app.app : 'ADMIN',
						user: this.user.id,
						role: portal,
						active: true
					});
				}
			} else if('Empleado' === value) {
				roles.push({
					app: app ? app.app : 'ADMIN',
					user: this.user.id,
					role: manager,
					active: false
				});
				roles.push({
					app: app ? app.app : 'ADMIN',
					user: this.user.id,
					role: portal,
					active: false
				});
			}
			this.updateRoles(roles);
		}
	}

	isAutosave() {
		return this.hasAttribute(CONSTANT.AUTOSAVE) && this.getAttribute(CONSTANT.AUTOSAVE) != 'false';
	}

	isOnlyAuth() {
		return this.hasAttribute(CONSTANT.ONLY_AUTH) && this.getAttribute(CONSTANT.ONLY_AUTH) != 'false';
	}

	isShowApps() {
		return this.hasAttribute(CONSTANT.SHOW_APPS) && this.getAttribute(CONSTANT.SHOW_APPS) != 'false'
	}

	isAdmin() {
		return this.user.roles && this.user.roles.includes('ADMIN');
	}

	isDev() {
		return this.user.roles && this.user.roles.includes('DEV');
	}

	isEmployee() {
		return this.user.roles && this.user.roles.includes('EMPLOYEE') && !this.isEnterprise();
	}

	isEnterprise() {
		return this.user.roles && this.user.roles.includes('ENTERPRISE');
	}

	isPersonalizado() {
		return !this.isEmployee() && !this.isEnterprise();
	}

	hasSecurity() {
		return this.isShowApps() && this.user != null
				&& this.user.email != null && !this.user.email.isEmpty()
				&& this.user.id != null;
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

	updateRoles(roles) {
		this.user.roles = this.user && this.user.roles ? this.user.roles : [];
		roles.forEach(role => this.updateRole(role));
		this.initApps();
		this.buildPermissionButtons();
	}

	updateRole(role) {
        let bool = true;
		if(role === Role.EMPLOYEE) {
			this.user.roles = this.user.roles
				.filter(r => !r.includes('PORTAL') 
					&& !r.includes('MANAGER'));
		} else if(role === Role.ENTERPRISE) {
			this.user.roles = this.user.roles
				.filter(r => !r.includes('MANAGER'));
		}

        this.user.roles.forEach((item, i) => {
            if(item === role.role) {
                bool = false;
                if(!role.active){
                    this.user.roles = this.user.roles.filter(r => !r.includes(role.role));
                }
            }
        });

        if(bool && role.active) {
            this.user.roles.push(role.role);
        }
    }

	setUser(user) {
		this.user = user;
	}

	setShowToolbar(showToolbar) {
		// this.showToolbar = showToolbar;
		this.setAttribute(CONSTANT.SHOW_TOOLBAR, showToolbar);
	}

	setShowApps(showApps) {
		// this.showToolbar = showToolbar;
		this.setAttribute(CONSTANT.SHOW_APPS, showApps);
	}

	setOnlyAuth(onlyAuth) {
		// this.onlyAuth = onlyAuth;
		this.setAttribute(CONSTANT.ONLY_AUTH, onlyAuth);
	}

	setShare(share) {
		this.setAttribute(CONSTANT.SHARE, share);
	}

	setShowPassword(showPassword) {
		this.setAttribute(CONSTANT.SHOW_PASSWORD, showPassword);
	}

	setAutosave(autosave) {
		this.setAttribute(CONSTANT.AUTOSAVE, autosave);
	}
}
if(!window.customElements.get(TAG.AON_USER)){
	window.customElements.define(TAG.AON_USER, AonUser);
}
