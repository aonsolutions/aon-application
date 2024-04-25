import { AonElement } from 'aonsolutions/components/AonElement.js';
import { MSG, CONSTANT, CSS, EVENT, MATERIAL_ICONS, TAG } from "aonsolutions/environments/environments.js";
import { AonSwitch } from "aonsolutions/components/aon-switch.js";

export class AonConfig extends AonElement {

	MENU_SWITCH;

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
		this.MENU_SWITCH = this.id + 'SwitchMenu';
	}

	build() {

		let span = this.createSpan();
		span.innerHTML = MSG.UPPER_MENU;
		span.style.marginLeft = '10px';
		this.appendChild(span);

		let rightPanelSwitchMenuButton = new AonSwitch();
		rightPanelSwitchMenuButton.id = this.MENU_SWITCH;
		rightPanelSwitchMenuButton.style.marginLeft = '10px';
		rightPanelSwitchMenuButton.style.right = '30px';
		rightPanelSwitchMenuButton.style.position = 'absolute';
		rightPanelSwitchMenuButton.style.top = "66px";
		this.appendChild(rightPanelSwitchMenuButton);

		let aonMenu = this.getElement("aonMenu");
		rightPanelSwitchMenuButton.checked = aonMenu.isTopNavVisible();
	
		rightPanelSwitchMenuButton.addEventListener(EVENT.CHANGE, () => {
            if(rightPanelSwitchMenuButton.isChecked()) {
				aonMenu.showTopNav();
            } else {
				aonMenu.showSideNav();
			};
			
	    });
	}

	getMenuButton() {
		return this.getElement(this.MENU_SWITCH);
	}

}
if(!window.customElements.get(TAG.AON_CONFIG)){
	window.customElements.define(TAG.AON_CONFIG, AonConfig);
}
