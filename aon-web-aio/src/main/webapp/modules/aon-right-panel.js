import { AonElement } from 'aonsolutions/components/AonElement.js';
import { MSG, CONSTANT, CSS, EVENT, MATERIAL_ICONS, TAG } from "aonsolutions/environments/environments.js";
import { AonIconButton } from 'aonsolutions/components/aon-icon-button.js';
import { AonSwitch } from "aonsolutions/components/aon-switch.js";

export class AonRightPanel extends AonElement {

	RIGHT_PANEL;
	ABOUT_BUTTON;
	CLOSE_BUTTON;
	MENU_SWITCH;
	CONF_TEXT;
	ABOUT_TEXT;

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
		this.id = this.id || 'AonRightPanel';
		this.RIGHT_PANEL = this.id + 'RightPanel';
		this.ABOUT_BUTTON = this.id + 'AboutButton';
		this.CLOSE_BUTTON = this.id + 'CloseButton';
		this.CONF_TEXT = this.id + 'ConfText';
		this.ABOUT_TEXT = this.id + 'AboutText';
		this.MENU_SWITCH = this.id + 'SwitchMenu'
	}

	build() {
		let rightPanel = this.createDiv(this.RIGHT_PANEL, "rightPanel");
		rightPanel.style.width = '320px';
		rightPanel.style.marginTop = this.getElement("aonMenuTopnav").offsetHeight;
		rightPanel.style.backgroundColor = '#faf9f8';
		rightPanel.style.visibility = "hidden";
		this.appendChild(rightPanel);

		let rightPanelCloseButton = new AonIconButton(); 
		rightPanelCloseButton.id = this.CLOSE_BUTTON;
		rightPanelCloseButton.icon ='close';
		rightPanelCloseButton.style.cursor = "pointer";
		rightPanelCloseButton.style.position = "fixed";
		rightPanelCloseButton.style.right = '10px';
		rightPanelCloseButton.style.visibility = "hidden";
		rightPanel.appendChild(rightPanelCloseButton)
		

		let rightPanelConfText = this.createElement(TAG.H1);
		rightPanelConfText.id = this.CONF_TEXT;
		rightPanelConfText.innerHTML = MSG.CONFIGURATION;
		rightPanelConfText.style.marginTop = '18px';
		rightPanelConfText.style.marginLeft = '10px'
		rightPanelConfText.style.fontSize = '16px';
		rightPanelConfText.style.fontWeight = '500';
		rightPanelConfText.style.paddingBottom = '32px';
		rightPanelConfText.style.visibility = "hidden";
		rightPanel.appendChild(rightPanelConfText)


		let rightPanelAboutText = this.createElement(TAG.H1);
		rightPanelAboutText.id = this.ABOUT_TEXT;
		rightPanelAboutText.innerHTML = MSG.ABOUT;
		rightPanelAboutText.style.marginTop = '28px';
		rightPanelAboutText.style.marginLeft = '10px';
		rightPanelAboutText.style.fontSize = '16px';
		rightPanelAboutText.style.fontWeight = '500';
		rightPanelAboutText.style.paddingBottom = '32px';
		rightPanelAboutText.style.visibility = "hidden";
		rightPanel.appendChild(rightPanelAboutText)

		let rightPanelAboutButton = new AonIconButton(); 
		rightPanelAboutButton.id = this.ABOUT_BUTTON;
		rightPanelAboutButton.icon ='info';
		rightPanelAboutButton.style.cursor = "pointer";
		rightPanelAboutButton.style.position = "fixed";
		rightPanelAboutButton.style.right = '10px';
		rightPanelAboutButton.style.marginTop = '-72px';
		rightPanelAboutButton.style.marginRight = '180px';
		rightPanelAboutButton.style.visibility = "hidden";
		rightPanel.appendChild(rightPanelAboutButton);

		let rightPanelSwitchMenuButton = new AonSwitch();
		rightPanelSwitchMenuButton.id = this.MENU_SWITCH;
		rightPanelSwitchMenuButton.checked = true;
		rightPanelSwitchMenuButton.title = MSG.UPPER_MENU;
		rightPanelSwitchMenuButton.style.marginLeft = '10px';
		rightPanelSwitchMenuButton.style.visibility = "hidden";
		rightPanel.appendChild(rightPanelSwitchMenuButton);

		rightPanelSwitchMenuButton.addEventListener(EVENT.CHANGE, () => {
            if(rightPanelSwitchMenuButton.isChecked()) {
				this.getElement('aonMenuSidenav').style.with = '0px';
				this.getElement('aonMenuSidenav').style.display = "none";
				this.getElement("rootPanel").style.marginLeft = '0px';
				this.getElement('aonMenuList').style.visibility = "hidden";
				this.getElement("aonMenuTopnav").style.height = '68px';
				this.getElement("rootPanel").style.marginTop = '69px';
				this.getElement
				rightPanel.style.marginTop = this.getElement("aonMenuTopnav").offsetHeight;
            } else {
				this.getElement('aonMenuSidenav').style.with = '68px';
				this.getElement('aonMenuSidenav').style.display = "";
				this.getElement("rootPanel").style.marginLeft = '69px';
				this.getElement('aonMenuList').style.visibility = "visible";
				this.getElement("aonMenuTopnav").style.height = '0px';
				rightPanel.style.marginTop = this.getElement("aonMenuTopnav").offsetHeight;
				this.getElement("rootPanel").style.height = rightPanel.offsetHeight;
				this.getElement("rootPanel").style.marginTop = '0px';

			};
	    });

		rightPanelCloseButton.addEventListener(EVENT.CLICK, () => {
			this.close();
		});

		rightPanelAboutButton.addEventListener(EVENT.CLICK, () => {
			alert("Acerca de")
		});
	}

	toogle() {
		if(this.style.visibility === "visible") {
			this.close();
		} else this.open();
	}

	open(){
		this.getRightPanel().style.visibility = "visible";
		this.getAboutButton().style.visibility = "visible";
		this.getCloseButton().style.visibility = "visible";
		this.getConfText().style.visibility = "visible";
		this.getAboutText().style.visibility = "visible";
		this.getMenuButton().style.visibility = "visible";
	}

	close(){
		this.getRightPanel().style.visibility = "hidden";
		this.getAboutButton().style.visibility = "hidden";
		this.getCloseButton().style.visibility = "hidden";
		this.getConfText().style.visibility = "hidden";
		this.getAboutText().style.visibility = "hidden";
		this.getMenuButton().style.visibility = "hidden";
		this.dispatchEvent(new Event(EVENT.CLOSE));
	}

	getRightPanel() {
		return this.getElement(this.RIGHT_PANEL);
	}

	getAboutButton() {
		return this.getElement(this.ABOUT_BUTTON);
	}

	getCloseButton() {
		return this.getElement(this.CLOSE_BUTTON);
	}

	getConfText() {
		return this.getElement(this.CONF_TEXT);
	}
	
	getAboutText() {
		return this.getElement(this.ABOUT_TEXT);
	}

	getMenuButton() {
		return this.getElement(this.MENU_SWITCH);
	}
}
if(!window.customElements.get(TAG.AON_RIGHT_PANEL)){
	window.customElements.define(TAG.AON_RIGHT_PANEL, AonRightPanel);
}