import { AonElement } from 'aonsolutions/components/AonElement.js';
import { MSG, CONSTANT, CSS, EVENT, TAG } from "aonsolutions/environments/environments.js";
import { AonSwitch } from "aonsolutions/components/aon-switch.js";
import { AonCard } from 'aonsolutions/components/aon-card.js';
import { Language } from 'aonsolutions/models/Language.js';
import { loadTheme } from '..';
import * as LS from 'aonsolutions/services/localStorageService.js';

export class AonConfig extends AonElement {

	TOP_SWITCH;
	LEFT_SWITCH;
	APPS_SWITCH;
	DARK_SWITCH;
	WHITE_BRAND_SWITCH;
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
		this.APPS_SWITCH = this.id + 'SwitchApps';
		this.DARK_SWITCH = this.id + 'DarkSwitch';
		this.WHITE_BRAND_SWITCH = this.id + 'WhiteBrandSwitch';
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

		let span3 = this.createSpan();
		span3.innerHTML = "Mostrar todas las apps";
		span3.style.left = '-81px';
		span3.style.position = "relative";
		span3.style.top = '35px';
		this.appendChild(span3);

		let rightPanelAppsButton = new AonSwitch();
		rightPanelAppsButton.id = this.APPS_SWITCH;
		rightPanelAppsButton.style.marginLeft = '10px';
		rightPanelAppsButton.style.right = '30px';
		rightPanelAppsButton.style.position = "absolute";
		rightPanelAppsButton.style.top = '122px';
		rightPanelAppsButton.checked = LS.isAppMenu();
		this.appendChild(rightPanelAppsButton);

		let span4 = this.createSpan();
		span4.innerHTML = "Modo oscuro";
		span4.style.left = '11px';
		span4.style.position  = "relative";
		span4.style.top = '50px';
		span4.style.display = "flex";
		this.appendChild(span4);

		let rightPanelDarkButton = new AonSwitch();
		rightPanelDarkButton.id = this.DARK_SWITCH;
		rightPanelDarkButton.style.marginLeft = '10px';
		rightPanelDarkButton.style.right = '30px';
		rightPanelDarkButton.style.position = "absolute";
		rightPanelDarkButton.style.top = '155px';
		rightPanelDarkButton.checked = LS.isDarkTheme();
		this.appendChild(rightPanelDarkButton);

		let span5 = this.createSpan();
		span5.innerHTML = "Marca blanca";
		span5.style.left = "11px";
		span5.style.position = "relative";
		span5.style.top = "65px";
		span5.style.display = "flex";
		this.appendChild(span5);

		let rightPanelWhiteBrandButton = new AonSwitch();
		rightPanelWhiteBrandButton.id = this.WHITE_BRAND_SWITCH;
		rightPanelWhiteBrandButton.style.marginLeft = '10px';
		rightPanelWhiteBrandButton.style.right = '30px';
		rightPanelWhiteBrandButton.style.position = "absolute";
		rightPanelWhiteBrandButton.style.top = '190';
		rightPanelWhiteBrandButton.checked = LS.isWhiteBrand();
		this.appendChild(rightPanelWhiteBrandButton);

		let rightPanelLangCard = new AonCard();
		rightPanelLangCard.id = this.LANG_CARD;
		rightPanelLangCard.title = MSG.SELECT_LANGUAGE;
		let title = this.getElement(rightPanelLangCard.TITLE);
		rightPanelLangCard.style.width = "90%";
		rightPanelLangCard.style.height = "fit-content";
		if(LS.isDarkTheme())
			rightPanelLangCard.style.color = "white";
		rightPanelLangCard.style.marginLeft = "10px";
		this.appendChild(rightPanelLangCard);

		let cardDivv = this.getElement(rightPanelLangCard.CARD);
		cardDivv.style.boxShadow = '0 2px 4px rgba(0,0,0,.1)';
		cardDivv.style.backgroundColor = "var(--aonCardColor)"
		cardDivv.style.borderRadius = '2px';
		cardDivv.style.marginTop = '60px';

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

		rightPanelAppsButton.addEventListener(EVENT.CHANGE, () => {
			LS.setAppMenu(rightPanelAppsButton.checked);
			aonMenu.buildMenuTopnav();
			aonMenu.reloadTopNav();
		});

		rightPanelDarkButton.addEventListener(EVENT.CHANGE, () => {
			LS.setDarkTheme(rightPanelDarkButton.checked);
			if (rightPanelDarkButton.checked) {
				loadTheme("dark");
			} else if (!rightPanelDarkButton.checked){
				loadTheme("aon");
			}
			location.reload();
		});

		rightPanelWhiteBrandButton.addEventListener(EVENT.CHANGE, () => {
			LS.setWhiteBrand(rightPanelWhiteBrandButton.checked);
		})
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