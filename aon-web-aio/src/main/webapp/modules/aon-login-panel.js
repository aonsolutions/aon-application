import { AonElement } from '../components/AonElement.js';
import { AonAvatar } from '../components/aon-avatar.js';
import { AonIcon } from '../components/aon-icon';
import { MSG, CONSTANT, CSS, EVENT, MATERIAL_ICONS, TAG } from "../environments/environments.js";
import { closeSession, getAuth } from '../services/service';
import { AonUser } from '../modules/user/aon-user';
import * as LS from '../services/localStorageService.js';

export class AonLoginPanel extends AonElement {
	CARD;
    NAME;
	LOGOUT;
	EDITBUTTON;
	CHANGEPASSWORD;
	rightPanel; // Agregado para guardar la referencia al rightPanel

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
      if(!this.isNewStyle()){
        this.rightPanel = this.getElement("aonRightPanel");
        this.rightPanel.style.boxShadow = "0 24px 54px rgba(0,0,0,.15),0 4.5px 13.5px rgba(0,0,0,.08)";
        this.rightPanel.style.marginTop = '0px';
        this.rightPanel.style.height = '234px';
      }
      let loginContent = this.createDiv("loginContent", "aonFlexColumn");

      let divGeneral = this.createDiv();
      if(!this.isNewStyle()){
        divGeneral.style.display = "flex";
        divGeneral.style.alignItems = "center";
        divGeneral.style.gap = "1rem";
        divGeneral.style.width = "100%";
        divGeneral.style.padding = "0 1rem";
      }
      let avatar = new AonAvatar();
      avatar.setAuth(auth);
      if(!this.isNewStyle()){
        divGeneral.appendChild(avatar);
      } else {
        loginContent.appendChild(avatar);
      }
      let divUserInfo = this.createDiv();
      divUserInfo.className = "userPanelDivUserInfo";
      if ((!auth.name && !auth.email && !auth.document && !auth.phone) || !auth) {
        divUserInfo.appendChild(this.buildName(MSG.EXPIRED_SESSION));
        if(!this.isNewStyle()){
          divUserInfo.style.marginBottom = "54px";
          divUserInfo.style.marginTop = "0px";
        }
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
      if(!this.isNewStyle()){
        divGeneral.appendChild(divUserInfo);
      } else {
        loginContent.appendChild(divUserInfo);
      }
      if (auth.name) {
        if(!this.isNewStyle()){
          let divPassword = this.createDiv();
          divPassword.className = 'aonUserPasswordLink';
          divPassword.innerText =  MSG.CHANGE_PASSWORD;
          divPassword.addEventListener("click", (e) => {
              let aonUser = new AonUser();
              aonUser.editPassword();
          });
          divUserInfo.appendChild(divPassword);
        } else {
          let buttonEditPass = this.createElement(TAG.BUTTON);
          buttonEditPass.innerText =  MSG.CHANGE_PASSWORD;
          buttonEditPass.addEventListener("click", (e) => {
            
            console.log('editar el pass');
            
              let aonUser = new AonUser();
              aonUser.editPassword();
              let rightPanel = this.closest('aon-right-panel');
              rightPanel?.close?.();
          });
          loginContent.appendChild(buttonEditPass);
        }
      }
      let buttonLogout        = this.createElement(TAG.BUTTON);
      buttonLogout.id         = this.LOGOUT;
      buttonLogout.className  = 'divLogout';
      this.buildbuttonLogout(MATERIAL_ICONS.LOGOUT, MSG.CLOSE_SESSION, buttonLogout);
      buttonLogout.addEventListener(EVENT.CLICK, () => {
        closeSession();
        if(!this.isNewStyle()){
          LS.setNewTheme(true);
        }
      });
      if(!this.isNewStyle()){
        loginContent.appendChild(divGeneral);
      }
      loginContent.appendChild(buttonLogout);

      if(!this.isNewStyle()){
        let openButton = this.getElement("openNotificationButton");
        openButton.style.display = "none";
      }
      this.appendChild(loginContent);
	}

	handleDocumentClick(event) {
      const aonHeaderUser         = document.getElementById('aonHeaderUser');
      const aonHeaderHelp         = document.getElementById('aonHeaderHelp');
      const aonHeaderConfig       = document.getElementById('aonHeaderConfig');
      const aonHeaderNotification = document.getElementById('aonHeaderNotification');
      
      if(!this.isNewStyle()){
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
 		i.setAttribute("data-icon", icon);
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

	buildbuttonLogout(icon, value, button) {
      if(!this.isNewStyle()){
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
        
        button.appendChild(div);
      } else {
        // Icono
        let i   = new AonIcon();
		i.icon  = icon;
		button.appendChild(i);
        button.innerHTML += value;
      }
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
