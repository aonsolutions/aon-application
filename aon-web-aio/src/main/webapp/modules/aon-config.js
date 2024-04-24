import { AonElement } from 'aonsolutions/components/AonElement.js';
import { MSG, CONSTANT, CSS, EVENT, MATERIAL_ICONS, TAG } from "aonsolutions/environments/environments.js";
import { AonIconButton } from 'aonsolutions/components/aon-icon-button.js';
import { AonSwitch } from "aonsolutions/components/aon-switch.js";

export class AonConfig extends AonElement {

	RIGHT_PANEL;
	MENU_SWITCH;
	CONF_TEXT;
    CLOSE_BUTTON;

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
		this.id = this.id || 'AonConfig';
		this.RIGHT_PANEL = this.id + 'ConfigRightPanel';
		this.CLOSE_BUTTON = this.id + 'ConfigCloseButton';
		this.CONF_TEXT = this.id + 'ConfigConfText';
		this.MENU_SWITCH = this.id + 'ConfigSwitchMenu';
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
		rightPanelConfText.style.paddingBottom = '20px';
		rightPanelConfText.style.visibility = "hidden";
		rightPanel.appendChild(rightPanelConfText)

		let span = this.createSpan();
		span.innerHTML = MSG.UPPER_MENU;
		span.style.marginLeft = '10px';
		rightPanel.appendChild(span);

		let rightPanelSwitchMenuButton = new AonSwitch();
		rightPanelSwitchMenuButton.id = this.MENU_SWITCH;
		rightPanelSwitchMenuButton.checked = true;
		rightPanelSwitchMenuButton.style.marginLeft = '10px';
		rightPanelSwitchMenuButton.style.right = '30px';
		rightPanelSwitchMenuButton.style.position = 'absolute';
		rightPanelSwitchMenuButton.style.visibility = "hidden";
		rightPanelSwitchMenuButton.style.top = "66px";
		rightPanel.appendChild(rightPanelSwitchMenuButton);

		rightPanelSwitchMenuButton.addEventListener(EVENT.CHANGE, () => {
            if(rightPanelSwitchMenuButton.isChecked()) {
				this.getElement('aonMenuSidenav').style.with = '0px';
				this.getElement('aonMenuSidenav').style.display = "none";
				this.getElement("rootPanel").style.marginLeft = '0px';
				this.getElement('aonMenuList').style.visibility = "hidden";
				this.getElement("aonMenuTopnav").style.height = '68px';
				this.getElement("rootPanel").style.marginTop = '69px';
				this.getElement("aonLogo").style.paddingLeft = '0px';
				this.getElement("aonLogo").style.width = '123px';
				rightPanel.style.marginTop = this.getElement("aonMenuTopnav").offsetHeight;
            } else {
				this.getElement('aonMenuSidenav').style.with = '68px';
				this.getElement('aonMenuSidenav').style.display = "";
				this.getElement("rootPanel").style.marginLeft = '69px';
				this.getElement('aonMenuList').style.visibility = "visible";
				this.getElement("aonMenuTopnav").style.height = '0px';
				rightPanel.style.marginTop = this.getElement("aonMenuTopnav").offsetHeight;
				this.getElement("rootPanel").style.height = rightPanel.offsetHeight;
				this.getElement("aonLogo").style.paddingLeft = '';
				this.getElement("aonLogo").style.width = '';
				this.getElement("rootPanel").style.marginTop = '0px';

			};
			
	    });

		rightPanelCloseButton.addEventListener(EVENT.CLICK, () => {
			this.close();
		});

	}

	toogle() {
		if(this.style.visibility === "visible") {
			this.close()
		} else this.open();
	}

	open(){
		this.getRightPanel().style.visibility = "visible";
		this.getCloseButton().style.visibility = "visible";
		this.getConfText().style.visibility = "visible";
		this.getMenuButton().style.visibility = "visible";
	}

	close(){
		this.getRightPanel().style.visibility = "hidden";
		this.getCloseButton().style.visibility = "hidden";
		this.getConfText().style.visibility = "hidden";
		this.getMenuButton().style.visibility = "hidden";
		this.dispatchEvent(new Event(EVENT.CLOSE));
	}

	getRightPanel() {
		return this.getElement(this.RIGHT_PANEL);
	}

	getCloseButton() {
		return this.getElement(this.CLOSE_BUTTON);
	}

	getConfText() {
		return this.getElement(this.CONF_TEXT);
	}
	
	getMenuButton() {
		return this.getElement(this.MENU_SWITCH);
	}

}
if(!window.customElements.get(TAG.AON_CONFIG)){
	window.customElements.define(TAG.AON_CONFIG, AonConfig);
}