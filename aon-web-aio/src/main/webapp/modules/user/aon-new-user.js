import { AonElement } from '../../components/AonElement.js';
import { saveUser, deleteUser, changePassword, getAuth, sendUserInfoEmail, updateDurDefinedUsers, getUserRoles, getBookingDomainUserRoles, getUserDomainUserRoles, deleteUserScope, getScopes, getCompanyScopes, addUserScopes } from  '../../services/service.js';
import { AllAonApps, EnterpriseAonApps, EmployeeAonApps } from  '../../services/app.js';
import { Role, Roles, ToolbarType } from '../../models/enums.js';
import { DomainUserRoles } from '../../models/DomainUserRoles.js';
import { MSG, MATERIAL_ICONS, CONSTANT, TAG, CSS, COLORS, EVENT } from '../../environments/environments.js';
import { AonMobileUserList } from './aon-mobile-user-list.js';
import { AonUserList } from './aon-user-list.js';
import { getNextUser, getPreviousUser, getUsers, updateUser, deleteUserCache } from './UserCache.js';
import { AonToolbar } from '../../components/aon-toolbar.js';
import { createCard, createIcon, createInput, createSelect, createSwitch, createTable } from '../../components/CreateComponent.js';

import * as ACTION from '../actions.js';
import { AonScopeSimpleList } from '../scope/aon-scope-simple-list.js';
import { AonTab } from '../../components/aon-tab.js';
import { AonDialog } from '../../components/aon-dialog.js';
import { AonSelect } from '../../components/aon-select.js';

export class AonNewUser extends AonElement {

	SWITCH;
	SELECT;
	TOOLBAR;
	CONTENT;

	USER_CARD;
	AUTH_TABLE;
	AUTH_TABLE_EMAIL;
	AUTH_TABLE_NAME;
	AUTH_TABLE_SURNAME;
	AUTH_TABLE_DOCUMENT;
	AUTH_TABLE_PHONE;

	SECURITY_DIV;
	SECURITY_TABS;
	SECURITY_CARD;
	SECURITY_TABLE;
	SECURITY_TABLE_ICON;
	SECURITY_TABLE_DESCRIPTION;
	SECURITY_TABLE_ACTIVE;
	
	SCOPE_CARD;

	ADMIN_APP;
	DEV_APP;

	user;
	userDur;
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
		this.initialize();
    	this.build();
  	}

	initialize() {
		this.id = this.id || 'aonUser';
		
		this.USER_CARD = this.id + CONSTANT.USER.initCap() + CONSTANT.CARD.initCap();
		this.SECURITY_DIV = this.id + CONSTANT.SECURITY.initCap() + 'Div';
		this.SECURITY_TABS = this.id + CONSTANT.SECURITY.initCap() + CONSTANT.TABS.initCap();
		this.SCOPE_CARD = this.id + CONSTANT.SCOPE.initCap() + CONSTANT.CARD.initCap();
		this.SECURITY_CARD = this.id + CONSTANT.SECURITY.initCap() + CONSTANT.CARD.initCap();

		this.AUTH_TABLE = this.USER_CARD + CONSTANT.AUTH.initCap() + CONSTANT.TABLE.initCap();
		this.AUTH_TABLE_EMAIL = this.AUTH_TABLE + CONSTANT.EMAIL.initCap();
		this.AUTH_TABLE_NAME = this.AUTH_TABLE + CONSTANT.NAME.initCap();
		this.AUTH_TABLE_SURNAME = this.AUTH_TABLE + CONSTANT.SURNAME.initCap();
		this.AUTH_TABLE_DOCUMENT = this.AUTH_TABLE + CONSTANT.DOCUMENT.initCap();
		this.AUTH_TABLE_PHONE = this.AUTH_TABLE + CONSTANT.PHONE.initCap();

		this.SECURITY_TABLE = this.id + CONSTANT.SECURITY.initCap() + CONSTANT.TABLE.initCap();
		this.SECURITY_TABLE_ICON = this.SECURITY_TABLE + CONSTANT.ICON.initCap();
		this.SECURITY_TABLE_DESCRIPTION = this.SECURITY_TABLE + CONSTANT.DESCRIPTION.initCap();
		this.SECURITY_TABLE_ACTIVE = this.SECURITY_TABLE + CONSTANT.ACTIVE.initCap();

		this.SWITCH = this.SWITCH || this.id + 'Switch';
		this.SELECT = this.SELECT || this.id + 'Select';
		this.TOOLBAR = this.TOOLBAR || this.id + 'Toolbar';
		this.CONTENT = this.CONTENT || this.id + 'Content';
		this.user = this.user || {};

		this.ADMIN_APP = {
			app: 'admin',
			title: "Administrador",
		  	description: "Administrador",
		  	symbol: MATERIAL_ICONS.SECURITY,
			is: (dur) => new DomainUserRoles(dur).isAdmin(),
			has: (dur) => {
				let d = new DomainUserRoles(dur);
				return !d.isEmployee() && !d.isEnterprise();
			}
		}

		this.DEV_APP = {
			app: 'dev',
			title: "Desarrollador",
		  	description: "Desarrollador",
		  	symbol: MATERIAL_ICONS.CODE,
			is: (dur) => new DomainUserRoles(dur).isDev(),
			has: (dur) => {
				let d = new DomainUserRoles(dur);
				return !d.isEmployee() && !d.isEnterprise() && this.isBeta();
			}
		}
		
		this.apps = [this.ADMIN_APP, this.DEV_APP].concat(AllAonApps);
	}

	build() {
		this.getUserDomainUserRoles().then(dur => {
			this.userDur = new DomainUserRoles(dur);

			if(this.userDur.isEnterprise()) this.apps = EnterpriseAonApps;
			if(this.userDur.isEmployee()) this.apps = EmployeeAonApps;
			this.apps = this.apps.filter(app => app.has(dur));

			this.user.roles = Roles.filter(f => f.is(this.userDur)).map(r => r.value);
			this.buildToolbar();

			if(this.isMobile()) 
				this.buildMobileContent();
			else this.buildContent();
		});
	}

	getUserDomainUserRoles() {
		return this.user.id 
			? getUserDomainUserRoles({user: this.user.id}, this.sessionData) 
			: getBookingDomainUserRoles({}, this.sessionData);
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

		this.buildUserCard(div)

		if(this.hasSecurity()) {
			this.buildSecurity(content);
		}
	}

	buildMobileContent() {
		let content = this.createDiv(this.CONTENT);
		this.appendChild(content);
		this.buildUserCard(content);
		
		if(this.hasSecurity()) {
			this.buildSecurityCard(content);
		}
	}

	buildUserCard(parent) {
		let card = createCard(this.USER_CARD, MSG.USER + (this.user.login ? ' (' + this.user.login + ')' : ''), parent);
		
		this.buildStatus(card);

		let div = this.createDiv();
		div.style.marginBottom = '10px';
		card.setContent(div);

		let table = createTable(this.AUTH_TABLE, div);

		table.addRow();

		let email = createInput(this.AUTH_TABLE_EMAIL, MSG.EMAIL);
		email.value = this.user.email || '';
		table.addCell(email, 4);
		email.addEventListener(EVENT.CHANGE, () => {
			this.user.email = email.value;
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

		table.addRow();

		let name = createInput(this.AUTH_TABLE_NAME, MSG.NAME);
		name.value = this.user.name || '';		
		table.addCell(name, 1);
		name.addEventListener(EVENT.CHANGE, () => {
			this.user.name = name.value;
			if(this.isAutosave()){
				this.save();
			}
		});

		let surname = createInput(this.AUTH_TABLE_SURNAME, MSG.SURNAME);
		surname.value = this.user.surname || '';
		table.addCell(surname, 3);
		surname.addEventListener(EVENT.CHANGE, () => {
			this.user.surname = surname.value;
			if(this.isAutosave()){
				this.save();
			}
		});

		table.addRow();

		let document = createInput(this.AUTH_TABLE_DOCUMENT, MSG.DOCUMENT);
		document.value = this.user.document || '';
		table.addCell(document, 2);
		document.addEventListener(EVENT.CHANGE, () => {
			this.user.document = document.value;
			if(this.isAutosave()){
				this.save();
			}
		});

		let phone = createInput(this.AUTH_TABLE_PHONE, MSG.PHONE);
		phone.value = this.user.phone || '';
		table.addCell(phone, 2);
		phone.addEventListener(EVENT.CHANGE, () => {
			this.user.phone = phone.value;
			if(this.isAutosave()){
				this.save();
			}
		});
	
		if(!this.isOnlyAuth()){
			let portal = createSwitch('aonConfigurationUserCardPortal', MSG.ONLY_PORTAL);
			portal.disabled = this.user.portal && !this.userDur.checkUsers();
			portal.checked = this.user.portal;
			portal.style.marginLeft = '5px';
			portal.checked = this.user.portal;
			portal.addEventListener(EVENT.CHANGE, () => {
				this.user.portal = !this.user.portal;
				this.buildPermissionButtons();
				if(this.user.portal) this.selectionEnterprisePortal();
				else this.selectionPersonalizado();
			});
			card.getContent().appendChild(portal);
		}
	}

	buildSecurity(parent) {
		let securityDiv = this.createDiv(this.SECURITY_DIV);
		if(!this.isMobile()) securityDiv.style.width = '50%';
		parent.appendChild(securityDiv);
		this.buildSecurityTabs(securityDiv);
	}

	buildSecurityTabs(div) {
		if(!this.isMobile()){
			let tabDiv = this.createDiv();
			const options = [
				{ title: MSG.PERMISSIONS, fn: () => this.buildSecurityCard(tabDiv)},
				{ title: MSG.SCOPES, fn: () => this.buildScopeCard(tabDiv)}
			];

			let tab = new AonTab();
			tab.id = this.SECURITY_TABS;
			tab.setOptions(options);
			div.appendChild(tab);		
			div.appendChild(tabDiv);
			options[0].fn();
		} else this.buildSecurityCard(div);
	}

	buildSecurityCard(parent) {
		parent.innerHTML = '';
		let card = createCard(this.SECURITY_CARD, MSG.PERMISSIONS, parent);
	
		let div = this.createDiv();
		card.setContent(div);
	
		let table = createTable(this.SECURITY_TABLE, div);

		this.buildPermissionButtons();
		
		this.apps.forEach((app, i) => {
			this.buildSecurityOption(table, app, i)
		});
	}

	buildScopeCard(parent) {
		parent.innerHTML = '';
		let card = createCard(this.SCOPE_CARD, MSG.SCOPES, parent);
		card.addTitleButton(MSG.ADD_SCOPE, MATERIAL_ICONS.ADD, false, () => this.addUserScope(parent));
		card.addTitleButton(MSG.ADD_ALL_SCOPES, "library_add", false, () => this.addAllUserScopes(parent));
		card.addTitleButton(MSG.DELETE_ALL_SCOPES, MATERIAL_ICONS.DELETE_SWEEP, false, () => this.deleteAllUserScopes(parent));
		


		let div = this.createDiv();
		card.setContent(div);
	
		let scopeList = new AonScopeSimpleList();
		scopeList.id = "aonScopeSimpleList";
		scopeList.scopes = this.userDur.scopes || [];
		scopeList.option = {
      		icon: MATERIAL_ICONS.DELETE,
      		fn: (scope) => this.deleteUserScope(parent,scope)
		};
		div.appendChild(scopeList);
	}

	addAllUserScopes(parent) {
		let d = this.getApplication().getDialog();
		d.clear();
		d.setTitle(MSG.ADD_ALL_SCOPES);
		d.width = '400px';
		d.setContentHTML("¿Estás seguro de añadir todos los ámbitos al usuario?");
		d.addAcceptAction(() => {
			getCompanyScopes().then( scopes => {
				addUserScopes({user: this.user.id, scopes: scopes
					.filter(s => !(this.userDur.scopes || []).map(us => us.id).includes(s.id))
					.map(s => s.id)}).then(() => {
					this.showMessage("Ámbitos añadidos al usuario");
					this.userDur.scopes = scopes;
					this.buildScopeCard(parent);
				}).catch(e => this.showError(e));
			});
		});
		d.open();		
	}

	addUserScope(parent) {
		getCompanyScopes().then( scopes => {
			let scopeSelect = new AonSelect();
	    	scopeSelect.title = MSG.SCOPES;
	    	scopeSelect.id = "userScopesSelect";
	   	 	scopeSelect.autocomplete = true;
	    	scopeSelect.default = true;
	    	scopeSelect.multiple = true;
		
			let d = this.getApplication().getDialog();
			d.clear();
			d.setTitle(MSG.ADD_SCOPE);
			d.setContent(scopeSelect);
			d.addAcceptAction(() => {
				let scps = scopeSelect.getSelectable().map(r => r.value);
				addUserScopes({user: this.user.id, scopes: scps}).then(() => {
					this.showMessage("Ámbito añadido al usuario");
					this.userDur.scopes = (this.userDur.scopes || []).concat(scopes.filter(s => scps.includes(s.id)));
					this.buildScopeCard(parent);
				}).catch(e => this.showError(e));
			});
			d.open();
			scopeSelect.setOptions(scopes
				.filter(s => !(this.userDur.scopes || []).map(us => us.id).includes(s.id))	
				.map(s => {
				  return {
					value: s.id,
					name: s.name
				  }
			}));
		});
	}

	deleteUserScope(parent, scope) {
	    let d = this.getApplication().getDialog();
		d.clear();
		d.setTitle(MSG.DELETE_SCOPE);
		d.width = '400px';
		d.setContentHTML("¿Estás seguro de eliminar el ámbito " + scope.name + " del usuario?");
		d.addAcceptAction(() => {
			deleteUserScope({user: this.user.id, scope: scope.id}).then(() => {
				this.showMessage("Ámbito eliminado del usuario");
				this.userDur.scopes = (this.userDur.scopes || []).filter(s => s.id != scope.id);
				this.buildScopeCard(parent);
			}).catch(e => this.showError(e));
		});
		d.open();
	}

	deleteAllUserScopes(parent) {
	    let d = this.getApplication().getDialog();
		d.clear();
		d.setTitle(MSG.DELETE_SCOPE);
		d.width = '400px';
		d.setContentHTML("¿Estás seguro de eliminar todos los ámbitos del usuario?");
		d.addAcceptAction(() => {
			deleteUserScope({user: this.user.id, all: true}).then(() => {
				this.showMessage("Todos los ámbitos eliminados del usuario");
				this.userDur.scopes = [];
				this.buildScopeCard(parent);
			}).catch(e => this.showError(e));
		});
		d.open();
	}

	initApps() {
		this.userDur.domainUserRoles = this.user.roles;
		this.userDur.oldUserRoles = [];

		this.apps = this.apps.filter(app => app.has(this.userDur));
		
		let table = this.getElement(this.SECURITY_TABLE);
		
		table.removeRows();
	
		this.apps.forEach((app, i) => {
			this.buildSecurityOption(table, app, i)
		});
	}

	buildSecurityOption(table, app, i) {
		let idSuffix = app.app.initCap() + i;
		table.addRow("aonHeight62");
		
		let icon = createIcon(this.SECURITY_TABLE_ICON + idSuffix,
			app.symbol || app.icon, app.symbol ? "MATERIAL" : "AON");
		table.addCell(icon);

		let span = this.createSpan(this.SECURITY_TABLE_DESCRIPTION + idSuffix);
		span.innerHTML = app.title || app.description;
		table.addCell(span);

		let active = createSwitch(this.SECURITY_TABLE_ACTIVE + i);
		active.checked = app.is(this.userDur);
		active.disabled = "admin" != app.app && this.userDur.isAdmin() && active.isChecked();
		active.style.paddingRight = '10px';
		
		active.addEventListener(EVENT.CHANGE, () => {
			this.activeAction(app, active.isChecked());
		});
		table.addCell(active);

		if(app.access && app.is(this.userDur) && this.isPersonalizado()) {
			let accessSelect = createSelect(this.SECURITY_TABLE_ACCESS + idSuffix, "Modo de Acceso");
			accessSelect.setOptions(app.access);
			accessSelect.value = this.getAccess(app);
			if(this.userDur.isAdmin()) accessSelect.disabled = true;
			accessSelect.addEventListener(EVENT.SELECT, () => {
				this.accessAction(app, accessSelect.value);
			});
			table.addCell(accessSelect);
		}
	}
		
	buildStatus(card){
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

		let statusBox = this.createDiv();
		statusBox.style.width           = "10px";
		statusBox.style.height          = "10px";
		statusBox.style.borderRadius    = "50%";
		statusBox.style.marginTop       = "3px";
		statusBox.style.backgroundColor = color;
		statusDiv.appendChild(statusBox);

		let statusText = this.createDiv();
		statusText.innerText = title;
		statusText.style.fontSize = "14px";
		statusText.style.fontWeight = "500";
		statusText.style.color = "#5f6368";
		statusDiv.appendChild(statusText);

		let iconArrowDown = this.createDiv();
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
		let card2 = this.getElement(this.SECURITY_CARD);
		card2.cleanSection2();

		if(this.user.portal) {
			card2.addTitleButton(MSG.ENTERPRISE, MATERIAL_ICONS.BUSINESS, this.isEnterprise(), () => this.selectionEnterprisePortal());
			card2.addTitleButton(MSG.EMPLOYEE, MATERIAL_ICONS.PERSON, this.isEmployee(), () => this.selectionEmployeePortal());
		}
	}

	selectionPersonalizado() {
		this.updateRole({ role: Role.ENTERPRISE, active: false, user: this.user.id});
		this.updateRole({ role: Role.EMPLOYEE, active: false, user: this.user.id});
		this.apps = [this.ADMIN_APP, this.DEV_APP].concat(AllAonApps);
		this.initApps();
	}

	selectionEnterprisePortal() {
		this.user.roles = [Role.ENTERPRISE];
		this.apps = EnterpriseAonApps.filter(app => app.has(this.userDur));
		this.apps.forEach(app => {
			this.user.roles.push(app.app.toUpperCase());
			this.user.roles.push(app.app.toUpperCase() + '_PORTAL');
		});
		this.initApps();
		this.buildPermissionButtons();
	}

	selectionEmployeePortal() {
		this.user.roles = [Role.EMPLOYEE];
		this.apps = EmployeeAonApps.filter(app => app.has(this.userDur));
		this.apps.forEach(app => this.user.roles.push(app.app.toUpperCase()));
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

		let div = this.createDiv();

		let oldPassword = createInput("aonConfigurationUserCardOldPassword");
        oldPassword.type = "password";
        oldPassword.description = MSG.PASSWORD;
        div.appendChild(oldPassword);

		let newPassword = createInput("aonConfigurationUserCardNewPassword")
        newPassword.type = "password";
        newPassword.description = MSG.NEW_PASSWORD; 
        div.appendChild(newPassword);

		d.setContent(div);
		d.addAcceptAction(() => {
			changePassword({oldPassword:oldPassword.value, newPassword:newPassword.value}, this.sessionData).then(()=>{
				this.showToast({message:MSG.SAVED_DATA, type:CONSTANT.SUCCESS});
			}).catch(e=>this.showError(e))
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
			this.showMessage(MSG.SAVED_DATA);
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
		this.changeUser(getNextUser());
	}

	previous() {
		this.changeUser(getPreviousUser());
	}

	changeUser(user) {
		this.clear();
		this.setUser(user);
		this.initialize();
		this.build();
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

	activeAction(app, active) {
		let rolePortal = app ? app.app.toUpperCase() : 'ADMIN';
		let roles = [this.createRole(app, rolePortal, active)];

		if (app && app.access && this.isPersonalizado()) {
			let roleManager = app.app.toUpperCase() + '_MANAGER';
			roles.push(this.createRole(app, roleManager, active));
		}

		if (app && app.access && this.isEnterprise()) {
			let rolePortal = app.app.toUpperCase() + '_PORTAL';
			roles.push(this.createRole(app, rolePortal, active));
		}

		this.updateRoles(roles);
	}

	accessAction(app, value) {
		if(app) {
			let roles = [];
			let manager = app.app.toUpperCase() + '_MANAGER';
			let portal = app.app.toUpperCase() + '_PORTAL';

			if('Asesor' === value){
				roles.push(this.createRole(app, manager, true));
			} else if('Empresa' === value) {
				roles.push(this.createRole(app, manager, false));
				if(app.access.length > 2) 
					roles.push(this.createRole(app, portal, true));		
			} else if('Empleado' === value) {
				roles.push(this.createRole(app, manager, false));
				roles.push(this.createRole(app, portal, false));
			}
			this.updateRoles(roles);
		}
	}

	createRole(app, role, active) {
		return {
			app: app ? app.app : 'ADMIN',
			user: this.user.id,
			role,
			active
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
		return this.userDur.isAdmin();
	}

	isEmployee() {
		return this.userDur.isEmployee();
	}

	isEnterprise() {
		return this.userDur.isEnterprise();
	}

	isPersonalizado() {
		return !this.isEmployee() && !this.isEnterprise();
	}

	hasSecurity() {
		return this.isShowApps() && this.user != null
			&& this.user.email != null && !this.user.email.isEmpty()
			&& this.user.id != null;
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
				console.log(role.active, typeof role.active);
                if(role.active == false){
                    this.user.roles = this.user.roles.filter(r => r !== role.role);
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
		this.setAttribute(CONSTANT.SHOW_TOOLBAR, showToolbar);
	}

	setShowApps(showApps) {
		this.setAttribute(CONSTANT.SHOW_APPS, showApps);
	}

	setOnlyAuth(onlyAuth) {
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
if(!window.customElements.get(TAG.AON_NEW_USER)){
	window.customElements.define(TAG.AON_NEW_USER, AonNewUser);
}
