import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from '../environments/environments.js';
import { serializeForm, setAttributes } from '../services/utils.js';
import { AonIconButton } from './aon-icon-button.js';
import {AonElement} from './AonElement.js';

import {AonInput} from './aon-input.js';
import {AonDate} from './aon-date.js';
import {AonSelect} from './aon-select.js';


export class AonSearch extends AonElement {

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
		searchButton.title = MSG.SEARCH;
		span.appendChild(searchButton);



		let input = this.createElement(TAG.INPUT);
		input.id = this.SEARCH_INPUT;
		input.title = MSG.SEARCH;
		input.placeholder = MSG.SEARCH;
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
			this.open();
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

		let options = this.createElement(TAG.DIV);
		options.id = this.OPTIONS;
		options.style.padding = "0 10px";
		options.className = CSS.AON_INPUT_LIST_OPTIONS;
		options.style.maxHeight = "none";
		this.appendChild(options);
	}

	 open(){
	  	let div = this.getElement(this.OPTIONS);
		div.style.width = this.clientWidth;
		if(div.classList.contains('is-visible')) 
			div.classList.remove('is-visible');
		else 
			div.classList.add('is-visible');
	  }

	  buildOptions(options) {
		this.clearElementById(this.OPTIONS);
		  let div = this.getElement(this.OPTIONS);
		div.style.width = this.getElement(this.SPAN).clientWidth;
		// div.classList.add('is-visible');
		// div.innerHTML = 'HOLAA';
		//   div.appendChild(ul)
	
		document.addEventListener('click', function (event) {
			let isClickInside = this.contains(event.target);
			if (!isClickInside) {
				if (div.classList.contains('is-visible')) {
					div.classList.remove('is-visible');
				}
			}
		});
	  }

	  buildOptionsFilter(inputs){
		this.clearElementById(this.OPTIONS);
		let div = this.getElement(this.OPTIONS);
		let form = this.createElement(TAG.FORM);
		form.id  = this.id+"Form";
		form.action = "#";
		div.appendChild(form);
		inputs.forEach((attributes) => {
			let el = this.getInput(attributes);
			if(el) form.appendChild(el);
		});
		let button = this.createElement(TAG.BUTTON);
		button.textContent = "Aceptar";
		button.className = "aonButton";
		button.style.padding = "0.5rem 1rem"; 
		// button.style.top = "184px";
		// button.style.position = "absolute";
		// button.style.left = 0;
		// button.style.right = 0;
		// button.style.marginRight = "auto";
		// button.style.marginLeft  = "auto";
		div.appendChild(button);
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
		}
		return html;
	  }


		getValues() {
			const form = this.getElement(`${this.id}Form`);
			return serializeForm(form);
		}


	  closeOptions() {
		let div = this.getElement(this.OPTIONS);
		if (div && div.classList.contains('is-visible')) {
		  div.classList.remove('is-visible');
		}
	  }
}
if(!window.customElements.get(TAG.AON_SEARCH)){
	window.customElements.define(TAG.AON_SEARCH, AonSearch);
}
