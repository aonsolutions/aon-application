import { AonElement } from '../components/AonElement.js';
import { MSG, CONSTANT, CSS, EVENT, TAG } from "../environments/environments.js";
import { AonSwitch } from "../components/aon-switch.js";
import { AonCard } from '../components/aon-card.js';
import { Language } from '../models/Language.js';
import * as LS from '../services/localStorageService.js';
import { AonConfiguration } from './configuration/aon-configuration.js';
import { AonConfigurationNew } from './configuration/aon-configuration-new.js';


export class AonConfig extends AonElement {

    TOP_NAV_SWITCH;
    SIDE_NAV_SWITCH;
    APPS_SWITCH;
    DARK_SWITCH;
    BRAND_SWITCH;
    LANG_CARD;
    THEMES_CARD;
    TYPE_CARD;
    ROOT_PANEL;


    get id() {
        return this.getAttribute(CONSTANT.ID);
    }

    set id(id) {
        this.setAttribute(CONSTANT.ID, id);
    }

    connectedCallback() {
        this.initialize();
        this.buildDur().then(() => {
            this.build();
        });
    }

    initialize() {
        this.id = this.id || 'aonConfig';
        this.TOP_NAV_SWITCH = this.id + 'TopSwitch';
        this.SIDE_NAV_SWITCH = this.id + 'SideSwitch';
        this.APPS_SWITCH = this.id + 'AppsSwitch';
        this.LANG_CARD = this.id + 'HelpLangCard';
        this.THEMES_CARD = this.id + 'ThemesCard';
        this.TYPE_CARD = this.id + 'TypeCard';
        this.ROOT_PANEL = 'rootPanel';

    }

    build() {
        let sideNavDiv = this.createDiv();
        sideNavDiv.className = CSS.AON_CONFIG_SIDE_NAV;

        let sideNavTitle = this.createSpan();
        sideNavTitle.className = `${CSS.AON_CONFIG_SIDE_NAV}Title`;
        sideNavTitle.innerHTML = MSG.SIDE_MENU;
        sideNavDiv.appendChild(sideNavTitle);

        let topNavDiv = this.createDiv();
        topNavDiv.className = CSS.AON_CONFIG_TOP_NAV;

        let topNavTitle = this.createSpan();
        topNavTitle.className = `${CSS.AON_CONFIG_TOP_NAV}Title`;
        topNavTitle.innerHTML = MSG.SUITE_MENU;
        topNavDiv.appendChild(topNavTitle);

        let topNavSwitch = new AonSwitch();
        topNavSwitch.id = this.TOP_NAV_SWITCH;
        topNavSwitch.checked = aonMenu.isTopNavVisible();
        topNavDiv.appendChild(topNavSwitch);

        this.appendChild(topNavDiv);

        if (this.isCSSLoaded("beta")) {
            let appsDiv = this.createDiv();
            appsDiv.className = CSS.AON_CONFIG_APPS;

            let appsTitle = this.createSpan();
            appsTitle.className = `${CSS.AON_CONFIG_APPS}Title`;
            appsTitle.innerHTML = "Mostrar todas las apps";
            appsDiv.appendChild(appsTitle);

            let appsSwitch = new AonSwitch();
            appsSwitch.id = this.APPS_SWITCH;
            appsSwitch.checked = LS.isAppMenu();
            appsDiv.appendChild(appsSwitch);

            this.appendChild(appsDiv);

            appsSwitch.addEventListener(EVENT.CHANGE, () => {
                LS.setAppMenu(appsSwitch.checked);
                aonMenu.buildMenuTopnav();
                aonMenu.reloadTopNav();
            });
        }
        
        if (!this.getDur().isEmployee()) {
            let configDiv = this.createSpan();
            configDiv.className = "configCardText";

            let configContentIndexI = this.createElement(TAG.I);
            configContentIndexI.className = CSS.MATERIAL_ICONS;
            configContentIndexI.classList.add("aonHelpI");
            configContentIndexI.innerHTML = "settings";
            configDiv.appendChild(configContentIndexI);

            let configContentIndexSpan = this.createDiv();
            configContentIndexSpan.className = CSS.AON_CARD_TEXT;
            configContentIndexSpan.classList.add("aonHelpSpan2");
            configContentIndexSpan.innerHTML = this.getDur().isConsultancy() ? MSG.ENVIRONMENT : MSG.COMPANY;
            configDiv.appendChild(configContentIndexSpan);
            this.appendChild(configDiv);

            configDiv.addEventListener(EVENT.CLICK, () => {
				let aonConfiguration;
				if(this.getDur().isConsultancy())
					aonConfiguration = new AonConfigurationNew();
				else
                	aonConfiguration = new AonConfiguration();
                	
                this.rootPanel(aonConfiguration);
                let rightPanel = document.querySelector('aon-right-panel');
                if (rightPanel) {
                    rightPanel.close();
                }
            });
        }

        if (LS.isFutureTheme() && this.dur.domain.domainType === 'OFFICE') {
            let fixedButtonDiv = this.createDiv();
            fixedButtonDiv.className = CSS.AON_CONFIG_APPS;
            fixedButtonDiv.style.width = '100%';

            let fixedButtonTitle = this.createSpan();
            fixedButtonTitle.className = `${CSS.AON_CONFIG_APPS}Title`;
            fixedButtonTitle.innerHTML = "Anclar botón de 'Nuevo'";
            fixedButtonDiv.appendChild(fixedButtonTitle);

            let fixedButtonSwitch = new AonSwitch();
            fixedButtonSwitch.id = 'fixedButtonSwitch';
            console.log('LS.getFixedButton()', LS.getFixedButton());
            fixedButtonSwitch.checked = LS.getFixedButton() === 'on';
            fixedButtonDiv.appendChild(fixedButtonSwitch);

            this.appendChild(fixedButtonDiv);

            fixedButtonSwitch.addEventListener(EVENT.CHANGE, () => {
                LS.setFixedButton(LS.getFixedButton() === 'on' ? 'off' : 'on');

                let newFixedButton = this.getElement('newFixedButton');
                let aonMenuAppHover = this.getElement('aonMenuList-new');

                if (LS.getFixedButton() === 'on') {
                    newFixedButton.classList.remove('hidden');
                    aonMenuAppHover.classList.add('hidden');
                } else {
                    newFixedButton.classList.add('hidden');
                    aonMenuAppHover.classList.remove('hidden');
                }

            });
            
            let hideSidenavButtonDiv = this.createDiv();
            hideSidenavButtonDiv.className = CSS.AON_CONFIG_APPS;
            hideSidenavButtonDiv.style.width = '100%';
            hideSidenavButtonDiv.style.paddingTop = '0';

            let hideSidenavButtonTitle = this.createSpan();
            hideSidenavButtonTitle.className = `${CSS.AON_CONFIG_APPS}Title`;
            hideSidenavButtonTitle.innerHTML = "Ocultar menú lateral";
            hideSidenavButtonDiv.appendChild(hideSidenavButtonTitle);

            let hideSidenavButtonSwitch = new AonSwitch();
            hideSidenavButtonSwitch.id = 'hideSidenavButtonSwitch';
            hideSidenavButtonSwitch.checked = LS.getHideSidenav() === 'on';
            hideSidenavButtonDiv.appendChild(hideSidenavButtonSwitch);

            this.appendChild(hideSidenavButtonDiv);

            hideSidenavButtonSwitch.addEventListener(EVENT.CHANGE, () => {
                LS.setHideSidenav(LS.getHideSidenav() === 'on' ? 'off' : 'on');

				let aonHome = this.getElement('aonHome');
				aonHome.collapseSidenavMenu();
            });
        }

        let themesCard = new AonCard();
        themesCard.id = this.THEMES_CARD;
        themesCard.title = MSG.THEME_SELECTION;
        themesCard.className = "rightPanelLangCard";
        this.appendChild(themesCard);

        let themesCardDiv = this.getElement(themesCard.CARD);
        themesCardDiv.style.boxShadow = 'none';

        let themesDiv = this.createDiv();
        themesDiv.appendChild(this.buildThemeData(MSG.STANDARD, '/css/theme/aon.css'));
        themesDiv.appendChild(this.buildThemeData(MSG.CLASSIC, '/css/theme/classic.css'));
        themesDiv.appendChild(this.buildThemeData(MSG.DARK, '/css/theme/dark.css'));
        themesDiv.appendChild(this.buildThemeData(MSG.FUTURE, '/css/theme/future.css'));
        themesCard.setContent(themesDiv);

        let langCard = new AonCard();
        langCard.id = this.LANG_CARD;
        langCard.title = MSG.SELECT_LANGUAGE;
        langCard.className = "rightPanelLangCard";
        this.appendChild(langCard);

        let langCardDiv = this.getElement(langCard.CARD);
        langCardDiv.style.boxShadow = 'none';

        let langsDiv = this.createDiv();
        langsDiv.appendChild(this.buildLanguageData(MSG.SPANISH, Language.SPANISH));
        langsDiv.appendChild(this.buildLanguageData("English", Language.ENGLISH));
        langsDiv.appendChild(this.buildLanguageData("Français", Language.FRENCH));
        langsDiv.appendChild(this.buildLanguageData("Deutsch", Language.DEUTSCH));
        langsDiv.appendChild(this.buildLanguageData("Euskara", Language.BASQUE));
        langsDiv.appendChild(this.buildLanguageData("Català", Language.CATALAN));
        langsDiv.appendChild(this.buildLanguageData("Galego", Language.GALICIAN));
        langCard.setContent(langsDiv);

        topNavSwitch.addEventListener(EVENT.CHANGE, () => {
            let topnav = this.getElement("aonMenuTopnav");

            if (LS.isTopMenu()) {
                aonMenu.showTopNav();
            } else {
                aonMenu.hideTopNav();
            };

        });

        let openButton = this.getElement("openNotificationButton");
        openButton.style.display = "none";
    }

    isCSSLoaded(cssFileName) {
        for (let sheet of document.styleSheets) {
            if (sheet.href && sheet.href.includes(cssFileName)) {
                return true;
            }
        }
        return false;
    }

    buildThemeData(value, theme) {
        let div = this.createDiv();
        div.style.title = "Temas";
        div.className = "configPanelLanguageDiv";

        let i = this.createElement(TAG.I);
        i.className = CSS.MATERIAL_ICONS + " configPanelLanguageI";
        div.appendChild(i);

        let span = this.createElement(TAG.SPAN);
        span.className = CSS.AON_CARD_TEXT;
        span.innerHTML = value;
        div.appendChild(span);

        if (theme == LS.getTheme()) {
            i.innerHTML = "done";
            span.style.fontWeight = "bold";
            div.classList.add('selected');
        } else {
            i.innerHTML = "palette";
        }

        div.addEventListener(EVENT.CLICK, () => {
            div.classList.toggle('selected');
            LS.setTheme(theme);
        })

        return div;
    }

    buildLanguageData(value, language) {
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

        if (language == LS.getLanguage()) {
            i.innerHTML = "done";
            span.style.fontWeight = "bold";
            div.classList.add('selected');
        } else {
            i.innerHTML = "language";
        }

        div.addEventListener(EVENT.CLICK, () => {
            div.classList.toggle('selected');
            LS.setLanguage(language);
        })

        return div;
    }

    getTopButton() {
        return this.getElement(this.TOP_NAV_SWITCH);
    }
}
if (!window.customElements.get(TAG.AON_CONFIG)) {
    window.customElements.define(TAG.AON_CONFIG, AonConfig);
}
