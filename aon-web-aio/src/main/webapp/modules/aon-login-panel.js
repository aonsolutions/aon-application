import { AonElement } from 'aonsolutions/components/AonElement.js';
import { MSG, CONSTANT, CSS, EVENT, MATERIAL_ICONS, TAG } from "aonsolutions/environments/environments.js";
import { AonIconButton } from 'aonsolutions/components/aon-icon-button.js';
import {closeSession, getTimeControl, saveTimeControl, clearDurum, getDomainUserRoles} from  'aonsolutions/services/service.js';
import { AonCard } from 'aonsolutions/components/aon-card.js';
import * as LS from 'aonsolutions/services/localStorageService.js';
import {  getManifest} from "aonsolutions/services/service.js";
import { AonSwitch } from "aonsolutions/components/aon-switch.js";
import { getSupport, setSupport } from 'aonsolutions/services/supportService.js';

export class AonLoginPanel extends AonElement {

	CARD;
    NAME;
	LOGOUT;
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
		this.id = this.id || 'AonLoginPanel';
		this.NAME = this.id + 'Name';
		this.CARD = this.id + 'Card';
		this.LOGOUT = this.id + 'Logout';
		this.CHANGEPASSWORD = this.id+ 'ChangePassword';
	}

	build() {
		let rightPanel = this.getElement("aonRightPanel");
		rightPanel.style.boxShadow="0 24px 54px rgba(0,0,0,.15),0 4.5px 13.5px rgba(0,0,0,.08)";
		rightPanel.style.marginTop = '0px';
		rightPanel.style.height = '200px';

		let divGeneral = this.createDiv();
		divGeneral.style.display = "flex";;

		let divImagen = this.createDiv();
		divImagen.appendChild(this.buildImage("AM"));
		divGeneral.appendChild(divImagen);

		let divUserInfo = this.createDiv();
		divUserInfo.style.marginLeft = "27px";
		divUserInfo.style.marginTop = "-22px";
		divUserInfo.style.maxWidth = "205px";
		divUserInfo.appendChild(this.buildName("Alejandro Millán Molinero"));
		divUserInfo.appendChild(this.buildInfo(MATERIAL_ICONS.MAIL,"alejandromillanmolinero@gmail.com"));
		divUserInfo.appendChild(this.buildInfo(MATERIAL_ICONS.PHONE,"633143977"));
		divUserInfo.appendChild(this.buildInfo(MATERIAL_ICONS.ASSIGNMENT_IND,"53980921J"))
		divGeneral.appendChild(divUserInfo);
		this.appendChild(divGeneral);

		let divLogout = this.createDiv();
		divLogout.id = this.LOGOUT;
		divLogout.style.borderTop = "1px solid rgba(0,0,0,.08)";
		divLogout.style.color= "inherit";
		divLogout.style.backgroundColor= "rgba(0,0,0,.04)";
		divLogout.style.height = "43px";
		divLogout.style.width = "100%";
		divLogout.style.marginTop = "12px";
		divLogout.style.cursor = "pointer";
		divLogout.style.transition = "background-color 0.1s"; 
		divLogout.style.backgroundColor = "rgba(0,0,0,.04)";
		divLogout.addEventListener("mouseover", function() {
			this.style.backgroundColor = "rgba(0,0,0,.1)"; 
		});

		divLogout.addEventListener("mouseout", function() {
			this.style.backgroundColor = "rgba(0,0,0,.04)";
		});
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
		div.style.scale = "3";
		div.style.marginLeft = "40px";
		div.style.marginTop = "19px";
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