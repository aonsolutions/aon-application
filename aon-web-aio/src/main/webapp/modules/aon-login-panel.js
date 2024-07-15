import { AonElement } from 'aonsolutions/components/AonElement.js';
import { AonAvatar } from 'aonsolutions/components/aon-avatar.js';
import { MSG, CONSTANT, CSS, EVENT, MATERIAL_ICONS, TAG } from "aonsolutions/environments/environments.js";
import {closeSession, getAuth, getUser } from  'aonsolutions/services/service.js';
import { AonConfiguration } from 'aonsolutions/modules/configuration/aon-configuration.js';

import * as LS from 'aonsolutions/services/localStorageService.js';

export class AonLoginPanel extends AonElement {

	CARD;
    NAME;
	LOGOUT;
	EDITBUTTON;
	CHANGEPASSWORD;

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}
	
	connectedCallback() {
		this.initialize();
		this.build();
	}

	initialize() {
		this.id = this.id || 'aonLoginPanel';
		this.NAME = this.id + 'Name';
		this.CARD = this.id + 'Card';
		this.LOGOUT = this.id + 'Logout';
		this.CHANGEPASSWORD = this.id+ 'ChangePassword';
		this.EDITBUTTON = this.id+ 'EditButton';
	}

	build() {
		getAuth().then( auth => {
			this.create(auth);
		});
	}

	create( auth ) {
	
		let rightPanel = this.getElement("aonRightPanel");
		rightPanel.style.boxShadow="0 24px 54px rgba(0,0,0,.15),0 4.5px 13.5px rgba(0,0,0,.08)";
		rightPanel.style.marginTop = '0px';
		rightPanel.style.height = '220px';

		let divGeneral = this.createDiv();
		divGeneral.style.display = "flex";
		
		let avatar = new AonAvatar();
		avatar.setAuth(auth);
		avatar.setScale("1.8","23px");
		this.appendChild(avatar);

		let divUserInfo = this.createDiv();
		divUserInfo.style.marginLeft = "34px";
		divUserInfo.style.marginTop = "-22px";
		divUserInfo.style.maxWidth = "205px";
		divUserInfo.style.marginBottom = "12px";
		if(!auth.name && !auth.email && !auth.document && !auth.phone){
			divUserInfo.appendChild(this.buildName(MSG.EXPIRED_SESSION));
			divUserInfo.style.marginBottom = "58px";
			divUserInfo.appendChild(this.buildInfo(MATERIAL_ICONS.ERROR,"Cierra sesion para reconectar"));
		}else{
			if(auth.name)
				divUserInfo.appendChild(this.buildName(auth.name));
			else
				divUserInfo.appendChild(this.buildName(MSG.NO_DATA))
			
			if(auth.email)
				divUserInfo.appendChild(this.buildInfo(MATERIAL_ICONS.MAIL,auth.email));
			else
				divUserInfo.appendChild(this.buildInfo(MATERIAL_ICONS.MAIL,MSG.NO_DATA))
			
			if(auth.phone)
				divUserInfo.appendChild(this.buildInfo(MATERIAL_ICONS.PHONE,auth.phone));
			else
				divUserInfo.appendChild(this.buildInfo(MATERIAL_ICONS.PHONE,MSG.NO_DATA));
			
			if(auth.document)
				divUserInfo.appendChild(this.buildInfo(MATERIAL_ICONS.ASSIGNMENT_IND,auth.document))
			else
				divUserInfo.appendChild(this.buildInfo(MATERIAL_ICONS.ASSIGNMENT_IND,MSG.NO_DATA));
		}
		
		if(auth.name) {
			let divConfiguration = this.createDiv();
			divConfiguration.className = 'aonUserConfigLink';
			divConfiguration.innerText = MSG.CONFIGURATION;
			divConfiguration.addEventListener(EVENT.CLICK, () => {
				let aonConfiguration = new AonConfiguration();
				aonConfiguration.user = auth.name;
				this.rootPanel(aonConfiguration);
			});
			divUserInfo.appendChild(divConfiguration);
		}
		
	
		divGeneral.appendChild(divUserInfo);
		this.appendChild(divGeneral);

		let divLogout = this.createDiv();
		divLogout.id = this.LOGOUT;
		divLogout.className = 'divLogout';
		divLogout.addEventListener(EVENT.CLICK, () => {
			closeSession();
		});
		divLogout.appendChild(this.buildInfoLink(MATERIAL_ICONS.LOGOUT,MSG.CLOSE_SESSION))
		this.appendChild(divLogout);

	}

	buildName(value) {
		let div = this.createDiv();
		div.style.marginTop = '5px';
		div.style.display = "flex";

		let span = this.createDiv();
		span.className = CSS.AON_CARD_TEXT;
		span.classList.add(CSS.AON_TEXT_OVERFLOW);
		span.style.fontWeight = "bold";
		span.style.fontSize = "16px	";
		span.innerHTML = value;
		div.appendChild(span);
		return div;
	}

	buildInfo(icon,value){
		let div = this.createDiv();
		div.style.marginTop = '5px';
		div.style.display = "flex";

		let i = this.createElement(TAG.I);
		i.className = CSS.MATERIAL_ICONS;
		i.style.marginRight = '5px';
		i.style.verticalAlign = "middle";
		i.style.marginTop = "0px";
		i.style.fontSize = "18px";
		i.innerHTML= icon;
		div.appendChild(i);

		let span = this.createDiv();
		span.className = CSS.AON_CARD_TEXT;
		span.classList.add(CSS.AON_TEXT_OVERFLOW);
		span.innerHTML = value;
		span.style.fontSize = "12px";
		div.appendChild(span);
		return div;
	}

	buildInfoLink(icon,value){
		let div = this.createDiv();
		div.style.marginTop = '8px';
		div.style.display = "flex";
		div.style.marginLeft = "15px";

		let i = this.createElement(TAG.I);
		i.className = CSS.MATERIAL_ICONS;
		i.style.marginRight = '5px';
		i.style.verticalAlign = "middle";
		i.style.marginTop = "0px";
		i.style.fontSize = "24px";
		i.innerHTML= icon;
		div.appendChild(i);

		let span = this.createDiv();
		span.className = CSS.AON_CARD_TEXT;
		span.innerHTML = value;
		span.style.fontSize = "12px";
		span.style.marginTop = "3px";
		div.appendChild(span);
		return div;
	}

	buildImage(letters){
		let div = this.createDiv();
		div.className = "profile-letters";
		div.style.scale = "2.6";
		div.style.marginLeft = "40px";
		div.style.marginTop = "15px";
		div.style.border = "none";
		div.style.backgroundColor = "var(--aonBlue)";
		div.style.color = "white";
		div.innerHTML = letters;
	
		return div;
	}

}
if(!window.customElements.get(TAG.AON_LOGIN_PANEL)){
	window.customElements.define(TAG.AON_LOGIN_PANEL, AonLoginPanel);
}
