import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from '../environments/environments.js';
import { serializeForm } from '../services/utils.js';
import { setAttributes } from '../services/utilsComponents.js';
import { AonIconButton } from './aon-icon-button.js';
import {AonElement} from './AonElement.js';

import { AonSwitch } from './aon-switch.js';
import {AonInput} from './aon-input.js';
import {AonDate} from './aon-date.js';
import {AonSelect} from './aon-select.js';

import '../css/aon-search.css';
import { AonNewDate } from './aon-new-date.js';
import { AonNewSelect } from "./aon-new-select.js";

export class AonSearch extends AonElement {
    formComponents = [];

	static get observedAttributes() {
		return [CONSTANT.DISABLED];
	}

	constructor (newStyle = false) {
		super();
        this.newStyle = newStyle;
	}

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	get opened() {
		return this.getAttribute(CONSTANT.OPENED);
	}

	set opened(opened) {
		this.setAttribute(CONSTANT.OPENED, opened);
	}

	get value() {
		return this.getAttribute(CONSTANT.VALUE);
	}

	set value(value) {
		this.setAttribute(CONSTANT.VALUE, value);
	}

	get disabled() {
        return this.getAttribute(CONSTANT.DISABLED) == CONSTANT.TRUE;
    }

    set disabled(disabled) {
        this.setAttribute(CONSTANT.DISABLED, disabled);
    }

	attributeChangedCallback(name, oldValue, newValue) {
		if(CONSTANT.DISABLED === name){
			this.disabledInputSearch();
		}
	}

	connectedCallback () {
		this.initialize();
		this.build();
	}

	initialize() {
		this.id = this.id || CONSTANT.AON_SEARCH;
		this.SEARCH_BUTTON = this.id + CONSTANT.SEARCH_BUTTON.initCap();
		this.SEARCH_INPUT = this.id + CONSTANT.SEARCH_INPUT.initCap();
		this.ADVANCED_BUTTON = this.id + CONSTANT.ADVANCED_BUTTON.initCap();
		this.OPTIONS = this.id + CONSTANT.OPTIONS.initCap();
		this.SPAN = this.id + CONSTANT.SPAN.initCap();
	}

	build() {
		let isMobile = this.isMobile();
		let span = this.createElement(TAG.SPAN);
		span.id = this.SPAN;
		span.style.display = 'inline-flex';
		span.style.position = 'relative';

		this.appendChild(span);

		let searchButton = new AonIconButton();
		searchButton.id = this.SEARCH_BUTTON;
		searchButton.icon = MATERIAL_ICONS.SEARCH;
		span.appendChild(searchButton);
		let input = this.createElement(TAG.INPUT);
		input.id = this.SEARCH_INPUT;
		input.className = CSS.AON_SEARCH_INPUT;
		input.autocomplete = 'off';
		input.style.display = 'none';
		span.appendChild(input);

        // El boton de filtro
        let advancedButton = new AonIconButton();
        if(this.newStyle){
          // El boton de filtro lo modificamos
          advancedButton.id   = this.ADVANCED_BUTTON;
          advancedButton.icon = MATERIAL_ICONS.FILTER_ALT;
          this.appendChild(advancedButton);
        } else {
          // Dejamos el boton de filtro como estaba
          advancedButton.style.display = 'none';
          advancedButton.id   = this.ADVANCED_BUTTON;
          advancedButton.icon = MATERIAL_ICONS.FILTER_LIST;
          span.appendChild(advancedButton);
        }

		advancedButton.addEventListener(EVENT.CLICK, () => {
			this.openOrClose();
		});

		input.addEventListener(EVENT.KEYUP, () => {
			this.dispatchEventSearch(input.value, EVENT.KEYUP);
		});

        if(!this.newStyle){
          // La lupa sea un boton fuera del documental
          searchButton.addEventListener(EVENT.CLICK, () => {
              if(input.style.display === 'none'){
                  this.openSearch();
                  if(this.disabled) {
                      this.openOrClose();
                  } else {
                      input.focus();
                  }
              } else {
                  this.closeSearch();
              }
          });
      }

		let divOpts = this.createElement(TAG.DIV);
		divOpts.id = this.OPTIONS;
		divOpts.className = CSS.AON_INPUT_LIST_OPTIONS;
		divOpts.style.maxHeight = "none";
		divOpts.style.padding = "10px";
        if(!this.newStyle){
          divOpts.style.display = "none";
        } else {
          divOpts.style.display = "grid";
          divOpts.style.gap     = ".5rem";
        }
		if(!isMobile){
			input.style.width = "300px";
			divOpts.style.width = "380px";
		}

		this.appendChild(divOpts);

		this.disabledInputSearch();
	}

	dispatchEventSearch(value, event=undefined){
		this.dispatchEvent(new CustomEvent(EVENT.SEARCH,{detail: value}));

		this.dispatchEvent(new CustomEvent(EVENT.SEARCH_NEW,{
			detail:{
				event,
				search: value,
				...this.getValues()
			}
		}));

		this.buildBadge();
	}

	dispatchCleanEventSearch(value, event=undefined){
      let eventType = EVENT.RESET_FILTER;
      let clear     = false;
      
      // Por ahora solo esta en el menu documental limpienado filtros, ya que veo que en otras vistas hace cosas raras
      // Nota: Esto deberia disparar RESET_FILTER siempre, habra que controlar donde se llame su correcto funcionamiento en ese filtro concreto
      /*
      if(this.isBetaDoc()){
        eventType = EVENT.SEARCH_NEW;
        clear     = true;
        value     = '';
        this.clearFormData();
      }
      */
	 
      this.dispatchEvent(new CustomEvent(eventType,{
          detail:{
              event,
              search: value,
              ...this.getValues()
          }
      }));
      this.buildBadge(clear);
	}

	buildBadge(clear = false){
		let count = Object.values(this.getValues()).length;
		const id  = this.id+"CountFilter";
		let div   = this.getElement(id);

		if(div){
          div.remove();
		}

		if(count && !clear){
          div       = this.createElement(TAG.DIV);
          div.id    = id;
          div.style = `
              position: relative; 
              background: #002469;
              top: 14px;
              right: 5px;
              border-radius: 50%;
              color: white;
              font-size: 10px;
              font-weight: 800;
              text-align: center;
              height: 14px;
              width: 14px;
              line-height: 14px;
          `;
          div.textContent = count;
          div.title       = `${count} ${MSG.FILTERS}`;
          this.getElement(this.SPAN).appendChild(div);
		}
	}

	openOrClose(){
		let divOpts = this.getElement(this.OPTIONS);
		if(divOpts.innerHTML.length){
			divOpts.style.width = this.clientWidth;
			if(divOpts.classList.contains('is-visible')){
				let advanceButton  = this.getElement(this.ADVANCED_BUTTON);
                if(this.newStyle){
                  advanceButton.icon =  MATERIAL_ICONS.FILTER_ALT;
                } else {
                  advanceButton.icon =  MATERIAL_ICONS.FILTER_LIST;
                } 
				this.closeOptions();
			} else {
				let advanceButton  = this.getElement(this.ADVANCED_BUTTON);
				advanceButton.icon =  MATERIAL_ICONS.CLOSE;
                if(!this.newStyle){
                  divOpts.style.display = "block";
                } else {
                  divOpts.style.display = "grid";
                }
				divOpts.classList.add('is-visible');
			}
		}
	}

	openSearch(){
		let span = this.getElement(this.SPAN);
        let advancedButton = this.getElement(this.ADVANCED_BUTTON);
		if(this.newStyle){
          const searchButton = this.getElement(this.SEARCH_BUTTON);
          // Que no tenga opcion del cursor el hijo
          const searchButtonSelector        = searchButton.querySelector('button');  
          searchButtonSelector.style.cursor = 'default';
        }
        
		let input = this.getElement(this.SEARCH_INPUT);
		if(span && advancedButton && input){
			if(this.isMobile()) {
				this.style.position = 'absolute';
				this.style.width = '100%';
				this.style.background = 'white';
				span.style.width = '100%';
				advancedButton.style.position = 'absolute';
				advancedButton.style.right = '0px';
			}
			input.style.display = 'block';
            if(!this.newStyle){
              advancedButton.style.display = 'block';
            } 
			span.style.borderBottom = '2px solid #002469';
		}
	}

	closeSearch(){
		let span = this.getElement(this.SPAN);
		let advancedButton = this.getElement(this.ADVANCED_BUTTON);
		let input = this.getElement(this.SEARCH_INPUT);
		if(span && advancedButton && input){
			if(this.isMobile()) {
				this.style.position = null;
				this.style.width = null;
				this.style.background = 'transparent';
				span.style.width = '100%';
				advancedButton.style.position = 'absolute';
				advancedButton.style.right = '0px';		
			} else {
				this.clearValues();
			}
			input.value = '';
			this.dispatchEventSearch(input.value, EVENT.CLOSE);
			input.style.display = 'none';
            if(!this.newStyle){
              advancedButton.style.display = 'none';
            }
			span.style.borderBottom = '0px';
			this.closeOptions();
		}
	}

	disabledInputSearch(){
		let searchButton = this.getElement(this.SEARCH_BUTTON);
		let input =  this.getElement(this.SEARCH_INPUT);
		if(input && searchButton){
			let searchText = "";
			if(this.disabled){
				input.disabled = this.disabled;
			} else {
				searchText = MSG.SEARCH;
				input.removeAttribute(CONSTANT.DISABLED);
			}
			input.title = searchText;
			input.placeholder = searchText;
			searchButton.title = searchText;
		}
	}

	getButtonAvanced(){
		return this.getElement(this.ADVANCED_BUTTON);
	}

	removeButtonAvanced(){
		if(this.getButtonAvanced()){
			this.getButtonAvanced().remove();
		}
	}
	/**
	 * 
	 * @param {array} inputs  examples [{
		type: "select",
		id: "period",
		name: "period",
		title: "Período",
	},{
		type: "date",
		name: "startDate",
		id: "startDate",
		title: "Desde",
	}]
	*/
	buildOptionsFilter(inputs){
		let divOpts = this.getElement(this.OPTIONS);
		divOpts.innerHTML = "";
		this.formComponents = [];

		let input = this.getElement(this.SEARCH_INPUT);
		inputs.forEach((attributes) => {
			let el = this.getInput(attributes);
			if(el) divOpts.appendChild(el);
		});

		let buttonsPanel = this.createElement(TAG.DIV);
		buttonsPanel.style.display = "flex";
		buttonsPanel.style.justifyContent = "center";
		buttonsPanel.style.alignItems = "center";
		buttonsPanel.style.marginTop = "1rem";

		let reset = this.createElement(TAG.BUTTON);
		reset.textContent = "Limpiar";
		reset.classList.add(CSS.AON_BUTTON, CSS.AON_FLEX);
		reset.style.padding = "0.5rem 1rem"; 
		reset.style.margin = "9px auto 0 auto";
		reset.style.background = "transparent";
		reset.style.border = "1px solid var(--aonBlue)";
		reset.style.color = "black";
		reset.addEventListener(EVENT.CLICK, (e)=>{
			e.stopPropagation();
			this.dispatchCleanEventSearch(input.value, EVENT.CLICK);
			this.openOrClose();
		});
		buttonsPanel.appendChild(reset);

		let button = this.createElement(TAG.BUTTON);
		button.textContent = MSG.ACCEPT;
		button.classList.add(CSS.AON_BUTTON, CSS.AON_FLEX);
		button.style.padding = "0.5rem 1rem"; 
		button.style.margin = "9px auto 0 auto";
		button.addEventListener(EVENT.CLICK, ()=>{
			this.dispatchEventSearch(input.value, EVENT.CLICK)
			this.openOrClose();
		});
		buttonsPanel.appendChild(button);

		divOpts.appendChild(buttonsPanel);
	}

	setContent(el){
		this.clearElementById(this.OPTIONS);
		let divOpts = this.getElement(this.OPTIONS);
		divOpts.appendChild(el);
	}

	addContent(el){
		let divOpts = this.getElement(this.OPTIONS);
		divOpts.insertBefore(divOpts.lastChild, el);
	}

    getInput(attributes) {
      let html      = undefined;
      let component = undefined;

      switch (attributes.type) {
        case CONSTANT.CHECKBOX:
          component = new AonSwitch();
          html = setAttributes(component, attributes);
        break;
        case CONSTANT.TEXT:
          component = new AonInput();
          html = setAttributes(component, attributes);
        break;
        case CONSTANT.SELECT:
          component = !this.newStyle 
            ? new AonSelect()
            : new AonNewSelect();
          html = setAttributes(component, attributes);
        break;
        case CONSTANT.DATE:
          component = new AonDate();
          html = setAttributes(component, attributes);
        break;
        case CONSTANT.NEW_DATE:
          component = new AonNewDate();
          html = setAttributes(component, attributes);
        break;
        case CONSTANT.HTML_ELEMENT:
          component = attributes.element;
          html = setAttributes(component, {...attributes, element: ""});
        break;
      }

      // Si hemos creado un componente, lo aniadimos al array
      if (component) {
        // Asi tenemos los componetes que se montan para limpiar el filtro o lo que se quiera
        this.formComponents.push(component);
      }

      return html;
    }

    clearFormData() {
      // limpiamos los datos del buscar principal
      this.getElement(this.SEARCH_INPUT).value = '';
      // Iteramos sobre todos los componentes en el array formComponents
      // Solo agregado por ahora inputs del documental, probar los demas!!!!!!!!!!!!!!!!!!!!!!!!!
      this.formComponents.forEach(component => {
        if (component.tagName === TAG.AON_NEW_SELECT.toUpperCase() && typeof component.setValue === 'function') {
          // Limpiar select (valor vacio)
          component.clear('');
        } else if (component.tagName === TAG.AON_SWITCH.toUpperCase() && typeof component.checked !== undefined) {
          // Limpiar checkbox (desmarcar)
          component.checked = false;
        } else if (component.tagName === TAG.AON_NEW_DATE.toUpperCase() && typeof component.setDate === 'function') {
          // Limpiar fecha
          component.setDate('');
        }
      });
    }

	clearValues() {
		const names = this.getValues();
		for(let name in names){
			let elem = this.querySelector(`[name=${name}]`);
			if(elem && elem.clear){
				elem.clear();
			}
		}
	}

    getValues() {
		let divOpts = this.getElement(this.OPTIONS);
		return divOpts ? serializeForm(divOpts) : {};
    }

    closeOptions() {
		let divOpts = this.getElement(this.OPTIONS);
		if (divOpts && divOpts.classList.contains('is-visible')) {
		  divOpts.style.display = "none";
		  divOpts.classList.remove('is-visible');
		}
    }
}
if(!window.customElements.get(TAG.AON_SEARCH)){
	window.customElements.define(TAG.AON_SEARCH, AonSearch);
}
