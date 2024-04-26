import { AonElement } from 'aonsolutions/components/AonElement.js';
import { MSG, CONSTANT, CSS, EVENT, MATERIAL_ICONS, TAG } from "aonsolutions/environments/environments.js";
import { AonSwitch } from "aonsolutions/components/aon-switch.js";
import { AonCard } from 'aonsolutions/components/aon-card.js';
import { Language } from 'aonsolutions/models/Language.js';
import * as LS from 'aonsolutions/services/localStorageService.js';

export class AonConfig extends AonElement {

	MENU_SWITCH;
	LANG_CARD;

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
		this.LANG_CARD = this.id + 'HelpLangCard';
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

		let rightPanelLangCard = new AonCard();
		rightPanelLangCard.id = this.LANG_CARD;
		rightPanelLangCard.title = MSG.SELECT_LANGUAGE;
		rightPanelLangCard.style.width = "90%";
		rightPanelLangCard.style.height = "fit-content";
		rightPanelLangCard.style.marginLeft = "10px";
		this.appendChild(rightPanelLangCard);

		let cardDivv = this.getElement(rightPanelLangCard.CARD);
		cardDivv.style.boxShadow = '0 2px 4px rgba(0,0,0,.1)';
		cardDivv.style.borderRadius = '2px';

		let divGenerall = this.createDiv();
		divGenerall.appendChild(this.buildLanguageData(MSG.SPANISH , Language.SPANISH));
		divGenerall.appendChild(this.buildLanguageData(MSG.ENGLISH , Language.ENGLISH));
		divGenerall.appendChild(this.buildLanguageData(MSG.DEUTSCH , Language.DEUTSCH));
		divGenerall.appendChild(this.buildLanguageData(MSG.BASQUE , Language.BASQUE));
		divGenerall.appendChild(this.buildLanguageData(MSG.CATALAN , Language.CATALAN));
		divGenerall.appendChild(this.buildLanguageData(MSG.GALICIAN , Language.GALICIAN));
		console.log(JSON.stringify(divGenerall));
		rightPanelLangCard.setContent(divGenerall);

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

	buildLanguageData(value,language) {
		let div = this.createDiv();
		div.style.marginTop = '5px';
		div.style.title = "Idioma";
		div.style.cursor = "pointer";

		let i = this.createElement(TAG.I);
		i.className = CSS.MATERIAL_ICONS;
		i.style.marginRight = '5px';
		i.style.verticalAlign = "middle";
		div.appendChild(i);
		
		let span = this.createElement(TAG.SPAN);
		span.className = CSS.AON_CARD_TEXT;
		span.innerHTML = value;
		div.appendChild(span);

		if(language == LS.getLanguage()) {
			i.innerHTML = "done";
			span.style.fontWeight = "bold";
		}else{
			i.innerHTML= "language";
		}

		div.addEventListener(EVENT.CLICK, () => {
			LS.setRightPanel(CONSTANT.TRUE);
			LS.setLanguage(language);
		})
	
		return div;		
	}

	getMenuButton() {
		return this.getElement(this.MENU_SWITCH);
	}

}
if(!window.customElements.get(TAG.AON_CONFIG)){
	window.customElements.define(TAG.AON_CONFIG, AonConfig);
}
