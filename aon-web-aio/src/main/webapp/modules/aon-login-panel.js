import { AonElement } from '../components/AonElement.js';
import { AonAvatar } from '../components/aon-avatar.js';
import { MSG, CONSTANT, CSS, EVENT, MATERIAL_ICONS, TAG } from "../environments/environments.js";
import { changePassword, closeSession, getAuth } from '../services/service.js';
import * as LS from '../services/localStorageService.js';
import { AonDialog } from '../components/aon-dialog.js';
import { createInput } from '../components/CreateComponent.js';

export class AonLoginPanel extends AonElement {

	CARD;
    NAME;
	LOGOUT;
	EDITBUTTON;
	CHANGEPASSWORD;

	EDIT_PASSWORD_DIALOG;

	rightPanel; // Añadido para guardar la referencia al rightPanel

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}
	
	connectedCallback() {
		this.initialize();
		this.build();
		document.addEventListener('click', this.handleDocumentClick.bind(this));
	}

	disconnectedCallback() {
		document.removeEventListener('click', this.handleDocumentClick.bind(this));
	}
	
	initialize() {
		this.id = this.id || 'aonLoginPanel';
		this.NAME = this.id + 'Name';
		this.CARD = this.id + 'Card';
		this.LOGOUT = this.id + 'Logout';
		this.CHANGEPASSWORD = this.id + 'ChangePassword';
		this.EDITBUTTON = this.id + 'EditButton';
		this.EDIT_PASSWORD_DIALOG = this.id + 'EditPasswordDialog';
	}

	build() {
		getAuth().then(auth => {
			this.create(auth);
		});
	}

	create(auth) {
		//auth = "";
		this.rightPanel = this.getElement("aonRightPanel"); 
		this.rightPanel.style.boxShadow = "0 24px 54px rgba(0,0,0,.15),0 4.5px 13.5px rgba(0,0,0,.08)";
		this.rightPanel.style.marginTop = '0px';
		this.rightPanel.style.height = '370px';
		
		let loginContent = this.createDiv("loginContent", "aonFlexColumn");

		let divGeneral = this.createDiv();
		divGeneral.style.display = "flex";
		divGeneral.style.alignItems = "center";
		divGeneral.style.gap = "1rem";
		divGeneral.style.width = "100%";
		divGeneral.style.padding = "0 1rem";
		
		
		let avatar = new AonAvatar();
		avatar.setAuth(auth);
		//avatar.setScale("1.8", "23px");
		loginContent.appendChild(avatar);

		let divUserInfo = this.createDiv();
		divUserInfo.className = "userPanelDivUserInfo";
		if ((!auth.name && !auth.email && !auth.document && !auth.phone) || !auth) {
			divUserInfo.appendChild(this.buildName(MSG.EXPIRED_SESSION));
			divUserInfo.style.marginBottom = "54px";
			divUserInfo.style.marginTop = "0px";
			divUserInfo.appendChild(this.buildInfo(MATERIAL_ICONS.ERROR, "Cierra sesion para reconectar"));
		} else {
			if (auth.name)
				divUserInfo.appendChild(this.buildName(auth.name));
			else
				divUserInfo.appendChild(this.buildName(MSG.NO_DATA));
			
			if (auth.email)
				divUserInfo.appendChild(this.buildInfo(MATERIAL_ICONS.MAIL, auth.email));
			else
				divUserInfo.appendChild(this.buildInfo(MATERIAL_ICONS.MAIL, MSG.NO_DATA));
			
			if (auth.phone)
				divUserInfo.appendChild(this.buildInfo(MATERIAL_ICONS.PHONE, auth.phone));
			else
				divUserInfo.appendChild(this.buildInfo(MATERIAL_ICONS.PHONE, MSG.NO_DATA));
			
			if (auth.document)
				divUserInfo.appendChild(this.buildInfo(MATERIAL_ICONS.ASSIGNMENT_IND, auth.document));
			else
				divUserInfo.appendChild(this.buildInfo(MATERIAL_ICONS.ASSIGNMENT_IND, MSG.NO_DATA));
		}
				
		/*if (auth.name) {
			let divConfiguration = this.createDiv();
			divConfiguration.className = 'aonUserConfigLink';
			divConfiguration.innerText = MSG.GO_CONFIGURATION;
			divConfiguration.addEventListener(EVENT.CLICK, () => {
				let aonConfiguration = new AonConfiguration();
				aonConfiguration.user = auth.name;
				this.rootPanel(aonConfiguration);
				let rightPanel = document.querySelector('aon-right-panel'); 
                if (rightPanel) {
                    rightPanel.close(); 
                }
				let companyy = this.getElement("aonHeaderCompanyListButton");
				let companyyy = this.getElement("aonHeaderCompanyList");
				companyy.style.display = "block";
				companyyy.style.display = "block";
			});
			divUserInfo.appendChild(divConfiguration);
		}*/
	
		divGeneral.appendChild(divUserInfo);
		loginContent.appendChild(divGeneral);
		
		if (auth.name) {
			let divPassword = this.createDiv();
			divPassword.className = 'aonUserPasswordLink configCardText';
			divPassword.innerText =  MSG.CHANGE_PASSWORD;
			divPassword.addEventListener("click", (e) => {
				this.editPassword();
			});
			
			loginContent.appendChild(divPassword);
		}
		
		let divLogout = this.createDiv();
		divLogout.id = this.LOGOUT;
		divLogout.className = 'divLogout configCardText';
		divLogout.addEventListener(EVENT.CLICK, () => {
			closeSession();
		});
		divLogout.appendChild(this.buildInfoLink(MATERIAL_ICONS.LOGOUT, MSG.CLOSE_SESSION));
		
		loginContent.appendChild(divLogout);
		
		let openButton = this.getElement("openNotificationButton");
		openButton.style.display = "none";

		this.appendChild(loginContent);
	}

	handleDocumentClick(event) {
		const aonHeaderUser = document.getElementById('aonHeaderUser');
		const aonHeaderHelp = document.getElementById('aonHeaderHelp');
		const aonHeaderConfig = document.getElementById('aonHeaderConfig');
		const aonHeaderNotification = document.getElementById('aonHeaderNotification');

		if (this.rightPanel &&
			this.rightPanel.style.visibility === "visible" &&
			!this.rightPanel.contains(event.target) &&
			!this.contains(event.target) &&
			!(aonHeaderUser && aonHeaderUser.contains(event.target)) &&
			!(aonHeaderHelp && aonHeaderHelp.contains(event.target)) &&
			!(aonHeaderConfig && aonHeaderConfig.contains(event.target)) &&
			!(aonHeaderNotification && aonHeaderNotification.contains(event.target))) {
			let rightPanel = document.querySelector('aon-right-panel'); 
			if (rightPanel) {
				rightPanel.close(); 
			}
		}
	}

	buildName(value) {
		let div = this.createDiv();
		div.className = "userPanelNameInfoDiv";

		let span = this.createDiv();
		span.className = CSS.AON_CARD_TEXT;
		span.classList.add(CSS.AON_TEXT_OVERFLOW);
		span.classList.add("userPanelNameSpan");
		span.innerHTML = value;
		div.appendChild(span);
		return div;
	}

	buildInfo(icon, value) {
		let div = this.createDiv();
		div.className = "userPanelNameInfoDiv";

		let i = this.createElement(TAG.I);
		i.className = CSS.MATERIAL_ICONS + " userPanelInfoI";
		i.innerHTML = icon;
		div.appendChild(i);

		let span = this.createDiv();
		span.className = CSS.AON_CARD_TEXT;
		span.classList.add(CSS.AON_TEXT_OVERFLOW);
		span.classList.add("userPanelInfoSpan");
		span.innerHTML = value;
		div.appendChild(span);
		return div;
	}

	buildInfoLink(icon, value) {
		let div = this.createDiv();
		div.className = "userPanelInfoLinkDiv";

		let i = this.createElement(TAG.I);
		i.className = CSS.MATERIAL_ICONS + " userPanelInfoLinkI";
		i.innerHTML = icon;
		div.appendChild(i);

		let span = this.createDiv();
		span.className = CSS.AON_CARD_TEXT + " userPanelInfoLinkSpan";
		span.innerHTML = value;
		div.appendChild(span);
		return div;
	}

	buildImage(letters) {
		let div = this.createDiv();
		div.className = "userPanelImage profile-letters";
		div.innerHTML = letters;
		return div;
	}

	editPassword() {
		let dialog = this.getElement(this.EDIT_PASSWORD_DIALOG);
		if (!dialog) {
			dialog = new AonDialog();
			dialog.id = this.EDIT_PASSWORD_DIALOG;
			this.appendChild(dialog);
		}
		
		dialog.clear();

		if(this.isMobile()) {
			dialog.type = "fullscreen";
		} else {
			dialog.width = '400px';
		}
		dialog.setTitle(MSG.CHANGE_PASSWORD);

		let div = document.createElement("div");

		let oldPassword = createInput("aonConfigurationUserCardOldPassword", "Contraseña");
		oldPassword.type = "password";
		div.appendChild(oldPassword);
	
		let newPassword = createInput("aonConfigurationUserCardNewPassword", "Repetir Contraseña");
		newPassword.type = "password";
		div.appendChild(newPassword);

		dialog.setContent(div);

		dialog.addAcceptAction(() => {
			changePassword({oldPassword:oldPassword.value, newPassword:newPassword.value}, this.sessionData).then(()=>{
				this.showToast({message:MSG.SAVED_DATA, type:CONSTANT.SUCCESS});
			}).catch(e=>this.showError(e))
		});
		dialog.open();
	}
}

if (!window.customElements.get(TAG.AON_LOGIN_PANEL)) {
	window.customElements.define(TAG.AON_LOGIN_PANEL, AonLoginPanel);
}
