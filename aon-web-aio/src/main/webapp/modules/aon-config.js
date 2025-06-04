import { AonElement } from '../components/AonElement.js';
import { MSG, CONSTANT, CSS, EVENT, TAG } from "../environments/environments.js";
import { AonCard } from '../components/aon-card.js';
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
    // Card themas
    let themesCard = new AonCard();
    themesCard.id = this.THEMES_CARD;
    themesCard.title = MSG.THEME_SELECTION;
    themesCard.className = "rightPanelLangCard";
    this.appendChild(themesCard);
    themesCard.setContent(this.buildThemeData());
    setupThemeToggleButtons();
    
    // Card language
    let langCard = new AonCard();
    langCard.id = this.LANG_CARD;
    langCard.title = MSG.SELECT_LANGUAGE;
    langCard.className = "rightPanelLangCard";
    this.appendChild(langCard);
    let langsDiv = this.createDiv();
    langsDiv.appendChild(this.buildLanguageData(MSG.SPANISH , Language.SPANISH));
    langsDiv.appendChild(this.buildLanguageData("English" , Language.ENGLISH));
    langsDiv.appendChild(this.buildLanguageData("Français" , Language.FRENCH));
    langsDiv.appendChild(this.buildLanguageData("Deutsch" , Language.DEUTSCH));
    langsDiv.appendChild(this.buildLanguageData("Euskara" , Language.BASQUE));
    langsDiv.appendChild(this.buildLanguageData("Català" , Language.CATALAN));
    langsDiv.appendChild(this.buildLanguageData("Galego" , Language.GALICIAN));
    langCard.setContent(langsDiv);
  }

  buildThemeData(){
    // Obt�n el modo activo actual
    const currentMode = localStorage.getItem('theme-mode') || 'auto';
    // Theme Mode Selector
    const themeSelector = this.createElement(TAG.DIV);
    themeSelector.className = "theme-toggle-container";

    ['light', 'dark', 'auto'].forEach(mode => {
      const div = this.createElement(TAG.DIV);
      div.className = "theme-toggle-btn";
      div.setAttribute('data-theme-mode', mode);
      // Marca el bot�n activo
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

  buildLanguageData(value,language) {
    let div = this.createDiv();
    div.style.title = "Idioma";
    div.className = "configPanelLanguageDiv";

    let i = this.createElement(TAG.I);
    i.className = CSS.MATERIAL_ICONS + " configPanelLanguageI";
    i.setAttribute("data-icon", "language");
    i.innerHTML= "language";
    div.appendChild(i);

    let span = this.createElement(TAG.SPAN);
    span.className = CSS.AON_CARD_TEXT;
    span.innerHTML = value;
    div.appendChild(span);

    if(language == LS.getLanguage()) {
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
