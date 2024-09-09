import { AonElement } from 'aonsolutions/components/AonElement.js';
import { AonAvatar } from 'aonsolutions/components/aon-avatar.js';
import { MSG, CONSTANT, CSS, EVENT, MATERIAL_ICONS, TAG } from "aonsolutions/environments/environments.js";
import { closeSession2, getAuth } from 'aonsolutions/services/service.js';
import { AonConfiguration } from 'aonsolutions/modules/configuration/aon-configuration.js';
import * as LS from 'aonsolutions/services/localStorageService.js';

export class AonLoginPanel extends AonElement {

	CARD;
    NAME;
	LOGOUT;
	EDITBUTTON;
	CHANGEPASSWORD;
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
		this.rightPanel.style.height = '218px';

		let divGeneral = this.createDiv();
		divGeneral.style.display = "flex";
		
		let avatar = new AonAvatar();
		avatar.setAuth(auth);
		avatar.setScale("1.8", "23px");
		this.appendChild(avatar);

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
		
		if (auth.name) {
			let divConfiguration = this.createDiv();
			divConfiguration.className = 'aonUserConfigLink';
			divConfiguration.innerText = MSG.CONFIGURATION;
			divConfiguration.addEventListener(EVENT.CLICK, () => {
				let aonConfiguration = new AonConfiguration();
				aonConfiguration.user = auth.name;
				this.rootPanel(aonConfiguration);
				let rightPanel = document.querySelector('aon-right-panel'); 
                if (rightPanel) {
                    rightPanel.close(); 
                }
			});
			divUserInfo.appendChild(divConfiguration);
		}
	
		divGeneral.appendChild(divUserInfo);
		this.appendChild(divGeneral);

		let divLogout = this.createDiv();
		divLogout.id = this.LOGOUT;
		divLogout.className = 'divLogout';
		divLogout.addEventListener(EVENT.CLICK, () => {
			closeSession2();
			LS.setNewTheme(true);
		});
		divLogout.appendChild(this.buildInfoLink(MATERIAL_ICONS.LOGOUT, MSG.CLOSE_SESSION));
		this.appendChild(divLogout);
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
}

if (!window.customElements.get(TAG.AON_LOGIN_PANEL)) {
	window.customElements.define(TAG.AON_LOGIN_PANEL, AonLoginPanel);
}
