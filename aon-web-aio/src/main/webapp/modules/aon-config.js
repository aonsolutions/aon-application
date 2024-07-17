import { AonElement } from 'aonsolutions/components/AonElement.js';
import { MSG, CONSTANT, CSS, EVENT, TAG } from "aonsolutions/environments/environments.js";
import { AonSwitch } from "aonsolutions/components/aon-switch.js";
import { AonCard } from 'aonsolutions/components/aon-card.js';
import { Language } from 'aonsolutions/models/Language.js';
import { loadTheme } from '..';
import * as LS from 'aonsolutions/services/localStorageService.js';

export class AonConfig extends AonElement {

	TOP_NAV_SWITCH;
	SIDE_NAV_SWITCH;
	APPS_SWITCH;
	DARK_SWITCH;
	BRAND_SWITCH;
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
		this.TOP_NAV_SWITCH = this.id + 'TopSwitch';
		this.SIDE_NAV_SWITCH = this.id + 'SideSwitch';
		this.APPS_SWITCH = this.id + 'AppsSwitch';
		this.DARK_SWITCH = this.id + 'DarkSwitch';
		this.BRAND_SWITCH = this.id + 'BrandSwitch';
		this.LANG_CARD = this.id + 'HelpLangCard';
		this.TYPE_CARD = this.id + 'TypeCard';
	}

	build() {


		let topNavDiv = this.createDiv();
		topNavDiv.className = CSS.AON_CONFIG_TOP_NAV;
		
		let topNavTitle = this.createSpan(); 
		topNavTitle.className =  `${CSS.AON_CONFIG_TOP_NAV}Title`;
		topNavTitle.innerHTML = MSG.SUITE_MENU;
		topNavDiv.appendChild(topNavTitle);
		
		let topNavSwitch = new AonSwitch();
		topNavSwitch.id = this.TOP_NAV_SWITCH;
		topNavSwitch.checked = LS.isTopMenu();
		topNavDiv.appendChild(topNavSwitch);

		this.appendChild(topNavDiv);


		let sideNavDiv= this.createDiv();
		sideNavDiv.className = CSS.AON_CONFIG_SIDE_NAV;

		let sideNavTitle = this.createSpan(); 
		sideNavTitle.className =  `${CSS.AON_CONFIG_SIDE_NAV}Title`;
		sideNavTitle.innerHTML = MSG.PORTAL_MENU;
		sideNavDiv.appendChild(sideNavTitle);

		let sideNavSwitch = new AonSwitch();
		sideNavSwitch.id = this.SIDE_NAV_SWITCH;
		sideNavSwitch.checked =  LS.isLeftMenu();
		sideNavDiv.appendChild(sideNavSwitch);

		this.appendChild(sideNavDiv);

		let appsDiv = this.createDiv();
		appsDiv.className = CSS.AON_CONFIG_APPS;

		let appsTitle = this.createSpan(); 
		appsTitle.className =  `${CSS.AON_CONFIG_APPS}Title`;
		appsTitle.innerHTML = "Mostrar todas las apps";
		appsDiv.appendChild(appsTitle);

		let appsSwitch = new AonSwitch();
		appsSwitch.id = this.APPS_SWITCH;
		appsSwitch.checked = LS.isAppMenu();
		appsDiv.appendChild(appsSwitch);

		this.appendChild(appsDiv);

		let darkDiv = this.createDiv();
		darkDiv.className = CSS.AON_CONFIG_DARK;

		let darkTitle = this.createSpan(); 
		darkTitle.className =  `${CSS.AON_CONFIG_DARK}Title`;
		darkTitle.innerHTML = "Modo oscuro";
		darkDiv.appendChild(darkTitle);

		let darkSwitch = new AonSwitch();
		darkSwitch.id = this.DARK_SWITCH;
		darkSwitch.checked = LS.isDarkTheme();
		darkDiv.appendChild(darkSwitch);

		this.appendChild(darkDiv);

		let brandDiv = this.createDiv();
		brandDiv.className = CSS.AON_CONFIG_BRAND;

		let brandTitle = this.createSpan(); 
		brandTitle.className =  `${CSS.AON_CONFIG_DARK}Title`;
		brandTitle.innerHTML = "Marca blanca";
		brandDiv.appendChild(brandTitle);

		let brandSwitch = new AonSwitch();
		brandSwitch.id = this.BRAND_SWITCH;
		brandSwitch.checked = LS.isWhiteBrand();
		brandDiv.appendChild(brandSwitch);

		this.appendChild(brandDiv);

		let langCard = new AonCard();
		langCard.id = this.LANG_CARD;
		langCard.title = MSG.SELECT_LANGUAGE;
		langCard.className = "rightPanelLangCard";
		this.appendChild(langCard);

		let langCardDiv = this.getElement(langCard.CARD);
		langCardDiv.style.boxShadow = '0 2px 4px rgba(0,0,0,.1)';
		langCardDiv.style.backgroundColor = "var(--aonCardColor)"
		langCardDiv.style.borderRadius = '2px';

		let langsDiv = this.createDiv();
		langsDiv.appendChild(this.buildLanguageData(MSG.SPANISH , Language.SPANISH));
		langsDiv.appendChild(this.buildLanguageData(MSG.ENGLISH , Language.ENGLISH));
		langsDiv.appendChild(this.buildLanguageData(MSG.FRENCH , Language.FRENCH));
		langsDiv.appendChild(this.buildLanguageData(MSG.DEUTSCH , Language.DEUTSCH));
		langsDiv.appendChild(this.buildLanguageData(MSG.BASQUE , Language.BASQUE));
		langsDiv.appendChild(this.buildLanguageData(MSG.CATALAN , Language.CATALAN));
		langsDiv.appendChild(this.buildLanguageData(MSG.GALICIAN , Language.GALICIAN));
		langCard.setContent(langsDiv);

		topNavSwitch.addEventListener(EVENT.CHANGE, () => {
			LS.setTopMenu(topNavSwitch.checked);

      
      if(LS.isTopMenu()) {
				aonMenu.showTopNav();
            } else {
				aonMenu.hideTopNav();
			};
			
	    });

		sideNavSwitch.addEventListener(EVENT.CHANGE, () => {
			LS.setLeftMenu(sideNavSwitch.checked);
			if(LS.isLeftMenu()) {
				aonMenu.showSideNav();
            } else {
				aonMenu.hideSideNav();
			};
			
	    });

		appsSwitch.addEventListener(EVENT.CHANGE, () => {
			LS.setAppMenu(appsSwitch.checked);
			aonMenu.buildMenuTopnav();
			aonMenu.reloadTopNav();
		});

		darkSwitch.addEventListener(EVENT.CHANGE, () => {
			LS.setDarkTheme(darkSwitch.checked);
			if (darkSwitch.checked) {
				loadTheme("dark");
			} else if (!darkSwitch.checked){
				loadTheme("aon");
			}
			location.reload();
		});

		brandSwitch.addEventListener(EVENT.CHANGE, () => {
			LS.setWhiteBrand(brandSwitch.checked);
		})

		
	}

	buildLanguageData(value,language) {
		let div = this.createDiv();
		div.style.title = "Idioma";
		div.className = "configPanelLanguageDiv";

		let i = this.createElement(TAG.I);
		i.className = CSS.MATERIAL_ICONS + " configPanelLanguageI";
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
		return this.getElement(this.TOP_NAV_SWITCH);
	}

	getLeftButton() {
		return this.getElement(this.SIDE_NAV_SWITCH);
	}


}
if(!window.customElements.get(TAG.AON_CONFIG)){
	window.customElements.define(TAG.AON_CONFIG, AonConfig);
}