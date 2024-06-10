import { AonElement } from 'aonsolutions/components/AonElement.js';
import { MSG, CONSTANT, CSS, EVENT, TAG } from "aonsolutions/environments/environments.js";
import { AonSwitch } from "aonsolutions/components/aon-switch.js";
import { AonCard } from 'aonsolutions/components/aon-card.js';
import { Language } from 'aonsolutions/models/Language.js';
import * as LS from 'aonsolutions/services/localStorageService.js';

export class AonConfig extends AonElement {

	TOP_SWITCH;
	LEFT_SWITCH;
	LANG_CARD;
	TYPE_CARD;

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
		this.id = this.id || 'aonConfig';
		this.TOP_SWITCH = this.id + 'SwitchTop';
		this.LEFT_SWITCH = this.id + 'SwitchLeft';
		this.LANG_CARD = this.id + 'HelpLangCard';
		this.TYPE_CARD = this.id + 'TypeCard';
	}

	build() {

		let div = this.createDiv();
		div.innerHTML = MSG.SUITE_MENU;
		div.style.marginLeft = '10px';
		div.style.position = "relative";
		div.style.top = "-15px";
		this.appendChild(div);

		let rightPanelSwitchTopButton = new AonSwitch();
		rightPanelSwitchTopButton.id = this.TOP_SWITCH;
		rightPanelSwitchTopButton.style.marginLeft = '10px';
		rightPanelSwitchTopButton.style.right = '30px';
		rightPanelSwitchTopButton.style.position = 'absolute';
		rightPanelSwitchTopButton.style.top = "-1px";
		rightPanelSwitchTopButton.checked = LS.isTopMenu();
		div.appendChild(rightPanelSwitchTopButton);

		let span2= this.createSpan();
		span2.innerHTML = MSG.PORTAL_MENU;
		span2.style.marginLeft = '10px';
		span2.style.position = "relative";
		span2.style.top = '0px';
		this.appendChild(span2);

		let rightPanelSwitchLeftButton = new AonSwitch();
		rightPanelSwitchLeftButton.id = this.LEFT_SWITCH;
		rightPanelSwitchLeftButton.style.marginLeft = '10px';
		rightPanelSwitchLeftButton.style.right = '30px';
		rightPanelSwitchLeftButton.style.position = 'absolute';
		rightPanelSwitchLeftButton.style.top = "90px";
		rightPanelSwitchLeftButton.checked =  LS.isLeftMenu();
		this.appendChild(rightPanelSwitchLeftButton);

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
		divGenerall.appendChild(this.buildLanguageData(MSG.FRENCH , Language.FRENCH));
		divGenerall.appendChild(this.buildLanguageData(MSG.DEUTSCH , Language.DEUTSCH));
		divGenerall.appendChild(this.buildLanguageData(MSG.BASQUE , Language.BASQUE));
		divGenerall.appendChild(this.buildLanguageData(MSG.CATALAN , Language.CATALAN));
		divGenerall.appendChild(this.buildLanguageData(MSG.GALICIAN , Language.GALICIAN));
		rightPanelLangCard.setContent(divGenerall);

		rightPanelSwitchTopButton.addEventListener(EVENT.CHANGE, () => {
			LS.setTopMenu(rightPanelSwitchTopButton.checked);
			if(LS.isTopMenu()) {
				aonMenu.showTopNav();
            } else {
				aonMenu.hideTopNav();
			};
			
	    });

		rightPanelSwitchLeftButton.addEventListener(EVENT.CHANGE, () => {
			LS.setLeftMenu(rightPanelSwitchLeftButton.checked);
			if(LS.isLeftMenu()) {
				aonMenu.showSideNav();
            } else {
				aonMenu.hideSideNav();
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

	getTopButton() {
		return this.getElement(this.TOP_SWITCH);
	}

	getLeftButton() {
		return this.getElement(this.LEFT_SWITCH);
	}


}
if(!window.customElements.get(TAG.AON_CONFIG)){
	window.customElements.define(TAG.AON_CONFIG, AonConfig);
}
