import { AonElement } from "../../components/AonElement";
import { CONSTANT, TAG, EVENT, CSS, MSG } from "../../environments/environments";
import { AonMobileElaborationList } from "../warehouse/elaboration/aon-mobile-elaboration-list";
import * as ACTION from '../actions.js';
import { AonIconButton } from "../../components/aon-icon-button.js";
import { AonPaturpatSearch } from "./aon-paturpat-search.js";

export class AonPaturpatApplication extends AonElement {

  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
  }

  get title() {
    return this.getAttribute(CONSTANT.TITLE);
  }

  set title(title) {
    this.setAttribute(CONSTANT.TITLE, title);
  }

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize() {
    this.id = this.id || 'aonPaturpatList';
  }

  build() {
      this.buildTitle();
      this.buildContent();
  }

  buildTitle() {
      let titleDiv = this.createDiv(this.id + 'TitleDiv');
      titleDiv.innerHTML = this.title;
      titleDiv.style.fontSize = '24px';
      titleDiv.style.margin = '15px';
      this.appendChild(titleDiv);
  }

  buildContent() {
      let contentDiv = this.createDiv(this.id + 'ContentDiv');
      this.appendChild(contentDiv);
  }

  getContent() {
      return this.getElement(this.id + 'ContentDiv');
  }

  setContent(content) {
      let contentDiv = this.getContent();
      contentDiv.innerHTML = '';
      contentDiv.appendChild(content);
  }

  addFloatOption(action, fn) {
      let icon = this.createDiv();
      icon.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
      icon.id = "aonMobileFloatIcon-" + action.id;
      icon.innerHTML = action.icon;
      icon.style.backgroundColor = '#005f2c';
      icon.style.color = "white";
      icon.style.fontVariationSettings = "'FILL' 0, 'wght' 300, 'GRAD' 0, 'opsz' 24";
      icon.style.borderRadius = "30px";
      icon.style.padding = "15px";
      icon.style.boxShadow = "0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19)";
      icon.style.position = "fixed";
      icon.style.bottom = "80px";
      icon.style.right = "20px";
      icon.style.zIndex = "1000";
      icon.style.cursor = "pointer";
      this.getContent().appendChild(icon);
      icon.addEventListener(EVENT.CLICK, () => fn());
      return icon;
  }

  removeFloatOption() {
      let icon = this.getContent().querySelector("[id^='aonMobileFloatIcon-']");
      if (icon) {
          icon.remove();
      }
  }

  buildSearchBox(id, placeholder) {
      let search = new AonPaturpatSearch();
      search.id = id;
      search.placeholder = placeholder;
      this.getContent().appendChild(search);

      // this.getContent().innerHTML = '';
      // let div = this.createDiv();
      // div.style.margin = '10px';
      // this.getContent().appendChild(div);

      // let input = this.createElement(TAG.INPUT);
      // input.type = 'text';
      // input.id = id;
      // input.placeholder = placeholder;
      // input.style.width = '100%';
      // input.style.boxSizing = 'border-box';
      // input.style.padding = '10px';
      // input.style.fontSize = '16px';
      // input.style.border = '1px solid #ccc';
      // input.style.borderRadius = '10px';
      // div.appendChild(input);
   		// input.addEventListener(EVENT.KEYUP, (event) => {
      //   input.dispatchEvent(new CustomEvent(EVENT.SEARCH_NEW,{
			//     detail:{
			//     	event,
			// 	    search: input.value
      //       // ,
			//     	// ...this.getValues()
    	// 		}
	    // 	  }));
      // });

      // // El boton de filtro
      // let advancedButton = new AonIconButton();
      // advancedButton.id   = id + 'AdvancedButton';
      // advancedButton.icon = 'page_info';
      // advancedButton.style.position = 'absolute';
      // advancedButton.style.right = '20px';
      // advancedButton.style.marginTop = '5px';
      // div.appendChild(advancedButton);
      // advancedButton.addEventListener(EVENT.CLICK, () => {
      //   alert('Búsqueda avanzada no implementada aún.');
      // }); 
  }
    
}
if (!window.customElements.get(TAG.AON_PATURPAT_APPLICATION)) {
    window.customElements.define(TAG.AON_PATURPAT_APPLICATION, AonPaturpatApplication);
}