import { AonElement } from '../components/AonElement.js';
import { MSG, CONSTANT, CSS, EVENT, TAG } from "../environments/environments.js";
import { AonCard } from '../components/aon-card';
import { AonIcon } from '../components/aon-icon';
import { AonConfiguration } from '../modules/configuration/aon-configuration';
import { Language } from '../models/Language.js';
import * as LS from '../services/localStorageService.js';
import {setupThemeToggleButtons} from './utils/theme';

export class AonConfig extends AonElement {
  TOP_NAV_SWITCH;
  SIDE_NAV_SWITCH;
  APPS_SWITCH;
  DARK_SWITCH;
  BRAND_SWITCH;
  LANG_CARD;
  THEMES_CARD;
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
    this.LANG_CARD = this.id + 'HelpLangCard';
    this.THEMES_CARD = this.id + 'ThemesCard';
    this.TYPE_CARD = this.id + 'TypeCard';
  }

  build() {
    let sideNavDiv        = this.createDiv();
    sideNavDiv.className  = CSS.AON_CONFIG_SIDE_NAV;

    let sideNavTitle       = this.createSpan();
    sideNavTitle.className = `${CSS.AON_CONFIG_SIDE_NAV}Title`;
    sideNavTitle.innerHTML = MSG.SIDE_MENU;
    sideNavDiv.appendChild(sideNavTitle);
    // Miramos ti tenemos session
    let session = localStorage.getItem('aon_session_id');
    if(session){
      if(!this.isNewStyle()){
        let configDiv = this.createSpan();
        configDiv.className = "configCardText";

        let configContentIndexI = this.createElement(TAG.I);
        configContentIndexI.className = CSS.MATERIAL_ICONS;
        configContentIndexI.classList.add("aonHelpI");
        configContentIndexI.innerHTML= "construction";
        configDiv.appendChild(configContentIndexI);

        let configContentIndexSpan = this.createDiv();
        configContentIndexSpan.className = CSS.AON_CARD_TEXT;
        configContentIndexSpan.classList.add("aonHelpSpan2");
        configContentIndexSpan.innerHTML = "Cofiguración Datos de Empresa";
        configDiv.appendChild(configContentIndexSpan);
        this.appendChild(configDiv);

        configDiv.addEventListener(EVENT.CLICK, () => {
            let aonConfiguration = new AonConfiguration();
            this.rootPanel(aonConfiguration);
            let rightPanel = document.querySelector('aon-right-panel');
            if (rightPanel) {
               rightPanel.close();
            }
        });
      } else {
        let buttonConfig = this.createElement(TAG.BUTTON);
        // Icono
        let i   = new AonIcon();
        i.icon  = "construction";
        buttonConfig.appendChild(i);
        buttonConfig.innerHTML += "empresa";
        this.appendChild(buttonConfig);
        buttonConfig.addEventListener(EVENT.CLICK, () => {
            let aonConfiguration = new AonConfiguration();
            this.rootPanel(aonConfiguration);
            let rightPanel = this.closest('aon-right-panel');
            rightPanel?.close?.();
        });
      }
    }
    // Card themas
    let themesCard       = new AonCard();
    themesCard.id        = this.THEMES_CARD;
    themesCard.title     = MSG.THEME_SELECTION;
    themesCard.className = "card-list-buttom";
    this.appendChild(themesCard);
    themesCard.setContent(this.buildThemeData());
    setupThemeToggleButtons();
    // Card language
    let langCard       = new AonCard();
    langCard.id        = this.LANG_CARD;
    langCard.title     = MSG.SELECT_LANGUAGE;
    langCard.className = "card-list-buttom";
    this.appendChild(langCard);
    langCard.setContent(this.buildLanguage());
  }

  buildThemeData(){
    // Obten el modo activo actual
    const currentMode = localStorage.getItem('theme-mode') || 'auto';
    // Theme Mode Selector
    const themeSelector = this.createElement(TAG.DIV);
    themeSelector.className = "buttoms";

    ['light', 'dark', 'auto'].forEach(mode => {
      const div = this.createElement(TAG.DIV);
      div.className = "theme-toggle-btn buttom";
      div.setAttribute('data-theme-mode', mode);
      // Marca el boton activo
      if (mode === currentMode) {
        div.classList.add('active');
      }
      // Icono
      const i = this.createElement(TAG.I);
      i.setAttribute("data-icon", "palette");
      div.appendChild(i);
      // Texto
      const span = this.createElement(TAG.SPAN);
      span.textContent = mode.charAt(0).toUpperCase() + mode.slice(1);
      div.appendChild(span);
      // Agregar al selector
      themeSelector.appendChild(div);
    });

    return themeSelector;
  }
  
  buildLanguage() {
    const div = this.createElement(TAG.DIV);
    div.className = "buttoms";

    div.appendChild(this.buildLanguageData(MSG.SPANISH, Language.SPANISH));
    div.appendChild(this.buildLanguageData(MSG.ENGLISH, Language.ENGLISH));
    div.appendChild(this.buildLanguageData(MSG.FRENCH, Language.FRENCH));
    div.appendChild(this.buildLanguageData(MSG.DEUTSCH, Language.DEUTSCH));
    div.appendChild(this.buildLanguageData(MSG.BASQUE, Language.BASQUE));
    div.appendChild(this.buildLanguageData(MSG.CATALAN, Language.CATALAN));
    div.appendChild(this.buildLanguageData(MSG.GALICIAN, Language.GALICIAN));
    return div;    
  }
  
  buildLanguageData(value,language) {
    let div = this.createDiv();
    div.style.title = "Idioma";
    div.className = "configPanelLanguageDiv buttom";

    let i = this.createElement(TAG.I);
    i.className = CSS.MATERIAL_ICONS + " configPanelLanguageI";
    i.setAttribute("data-icon", "language");
    i.innerHTML= "language";
    div.appendChild(i);

    let span = this.createElement(TAG.SPAN);
    span.className = CSS.AON_CARD_TEXT;
    span.innerHTML = value;
    div.appendChild(span);
    
    // Seleccionamos el idioma marcado, si no esta marcado ninguno ponemos el castellano
    if(language == LS.getLanguage() || (language == Language.SPANISH && LS.getLanguage() === '' )) {
      div.classList.add('active');
    }

    div.addEventListener(EVENT.CLICK, () => {
        LS.setLanguage(language);
    });

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
