import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from '../environments/environments.js';
import { serializeForm, setAttributes } from '../services/utils.js';
import { AonIconButton } from './aon-icon-button.js';
import {AonElement} from './AonElement.js';

import {AonInput} from './aon-input.js';
import {AonDate} from './aon-date.js';
import {AonSelect} from './aon-select.js';


export class AonSearch extends AonElement {

	static get observedAttributes() {
		return [CONSTANT.DISABLED];
	}

	constructor () {
		super();
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
		
		let span = this.createElement(TAG.SPAN);
		span.id = this.SPAN;
		span.style.display = 'inline-flex';

		this.appendChild(span);
		
		let searchButton = new AonIconButton();
		searchButton.id = this.SEARCH_BUTTON;
		searchButton.icon = MATERIAL_ICONS.SEARCH;
		span.appendChild(searchButton);
		let input = this.createElement(TAG.INPUT);
		input.id = this.SEARCH_INPUT;

		input.style.display = 'none';
		input.style.outline = 'none';
		input.style.border = 'none';
		input.style.marginTop = '1px';
		input.style.height = '37px';
		span.appendChild(input);

		let advancedButton = new AonIconButton();
		advancedButton.style.display = 'none';
		advancedButton.id = this.ADVANCED_BUTTON;
		advancedButton.icon = MATERIAL_ICONS.ARROW_DROP_DOWN;
		span.appendChild(advancedButton);

		advancedButton.addEventListener(EVENT.CLICK, () => {
			this.openOrClose();
		})
		
		input.addEventListener(EVENT.KEYUP, () => {
			this.dispatchEvent(new CustomEvent(EVENT.SEARCH,{detail: input.value}));
		});

		searchButton.addEventListener(EVENT.CLICK, () => {
			if(input.style.display === 'none'){
				if(this.isMobile()) {
					this.style.position = 'absolute';
					this.style.width = '100%';
					this.style.background = 'white';
					span.style.width = '100%';
					advancedButton.style.position = 'absolute';
					advancedButton.style.right = '0px';		
				}
				input.style.display = 'block';
				advancedButton.style.display = 'block';
				span.style.borderBottom = '2px solid #002469';
				if(this.disabled) 
					this.openOrClose();
				else 
					input.focus();
			} else {
				if(this.isMobile()) {
					this.style.position = null;
					this.style.width = null;
					this.style.background = 'transparent';
					span.style.width = '100%';
					advancedButton.style.position = 'absolute';
					advancedButton.style.right = '0px';		
				}
				input.value = '';
				this.dispatchEvent(new CustomEvent(EVENT.SEARCH,{detail: input.value}));
				input.style.display = 'none';
				advancedButton.style.display = 'none';
				span.style.borderBottom = '0px';
				this.closeOptions();
			}
		});

		let divOpts = this.createElement(TAG.DIV);
		divOpts.id = this.OPTIONS;
		divOpts.className = CSS.AON_INPUT_LIST_OPTIONS;
		divOpts.style.maxHeight = "none";
		divOpts.style.padding = "10px";
		divOpts.style.display = "none";

		this.appendChild(divOpts);

		this.disabledInputSearch();

	}

	 openOrClose(){
	  	let divOpts = this.getElement(this.OPTIONS);
		if(divOpts.innerHTML.length){
			divOpts.style.width = this.clientWidth;
			if(divOpts.classList.contains('is-visible')){
				this.closeOptions();
			} else {
				divOpts.style.display = "block";
				divOpts.classList.add('is-visible');
			}
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

	//   buildOptions(options) {
		// this.clearElementById(this.OPTIONS);
		// let divOpts = this.getElement(this.OPTIONS);
		// divOpts.style.width = this.getElement(this.SPAN).clientWidth;
		// divOpts.classList.add('is-visible');
		// divOpts.innerHTML = 'HOLAA';
		//   divOpts.appendChild(ul)
	
		// document.addEventListener('click', function (event) {
		// 	let isClickInside = this.contains(event.target);
		// 	if (!isClickInside) {
		// 		if (divOpts.classList.contains('is-visible')) {
		// 			divOpts.classList.remove('is-visible');
		// 		}
		// 	}
		// });
	//   }
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
		let input = this.getElement(this.SEARCH_INPUT);
		inputs.forEach((attributes) => {
			let el = this.getInput(attributes);
			if(el) divOpts.appendChild(el);
		});

		let button = this.createElement(TAG.BUTTON);
		button.textContent = MSG.ACCEPT;
		button.classList.add(CSS.AON_BUTTON, CSS.AON_FLEX);
		button.style.padding = "0.5rem 1rem"; 
		button.style.margin = "9px auto 0 auto";
		button.addEventListener(EVENT.CLICK, ()=>{
			this.dispatchEvent(new CustomEvent(EVENT.SEARCH_VALUE,{
				detail: {
					search:input.value,
					...this.getValues()
				}
			}));
			this.openOrClose();
		});
		divOpts.appendChild(button);
	  }

	  setContent(el){
		this.clearElementById(this.OPTIONS);
		let divOpts = this.getElement(this.OPTIONS);
		divOpts.appendChild(el);
	  }

	  getInput(attributes) {
		let html = undefined;
		switch (attributes.type) {
		  case CONSTANT.TEXT:
			html = setAttributes(new AonInput(), attributes);
			break;
		  case CONSTANT.SELECT:
			html = setAttributes(new AonSelect(), attributes);
			break;
		  case CONSTANT.DATE:
			html = setAttributes(new AonDate(), attributes);
			break;
		  case CONSTANT.HTML_ELEMENT:
			const element = attributes.element;
			delete attributes.type;
			delete attributes.element;
			html = setAttributes(element, attributes);
			break;
		}
		return html;
	  }

 	 getValues() {
		let divOpts = this.getElement(this.OPTIONS);
		return serializeForm(divOpts);
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
