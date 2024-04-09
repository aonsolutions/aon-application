import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from '../environments/environments.js';
import { serializeForm } from '../services/utils.js';
import { setAttributes } from '../services/utilsComponents.js';
import { AonIconButton } from './aon-icon-button.js';
import {AonElement} from './AonElement.js';

import {AonInput} from './aon-input.js';
import {AonDate} from './aon-date.js';
import {AonSelect} from './aon-select.js';

import '../css/aon-search.css';
import { AonNewDate } from './aon-new-date.js';

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

		let advancedButton = new AonIconButton();
		advancedButton.style.display = 'none';
		advancedButton.id = this.ADVANCED_BUTTON;
		advancedButton.icon = MATERIAL_ICONS.ARROW_DROP_DOWN;
		span.appendChild(advancedButton);

		advancedButton.addEventListener(EVENT.CLICK, () => {
			this.openOrClose();
		})
		
		input.addEventListener(EVENT.KEYUP, () => {
			this.dispatchEventSearch(input.value, EVENT.KEYUP);
		});

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

		let divOpts = this.createElement(TAG.DIV);
		divOpts.id = this.OPTIONS;
		divOpts.className = CSS.AON_INPUT_LIST_OPTIONS;
		divOpts.style.maxHeight = "none";
		divOpts.style.padding = "10px";
		divOpts.style.display = "none";
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

	buildBadge(){
		let count = Object.values(this.getValues()).length;

		const id = this.id+"CountFilter";

		let div = this.getElement(id);

		if(div){
			div.remove();
		}

		if(count){
			div = this.createElement(TAG.DIV);
			div.id = id;
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
			div.title = `${count} ${MSG.FILTERS}`;
			this.getElement(this.SPAN).appendChild(div);
		}
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

	openSearch(){
		let span = this.getElement(this.SPAN);
		let advancedButton = this.getElement(this.ADVANCED_BUTTON);
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
			advancedButton.style.display = 'block';
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
			advancedButton.style.display = 'none';
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
			this.dispatchEventSearch(input.value, EVENT.CLICK)
			this.openOrClose();
		});
		divOpts.appendChild(button);
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
			case CONSTANT.NEW_DATE:
				html = setAttributes(new AonNewDate(), attributes);
			break;
			case CONSTANT.HTML_ELEMENT:
				html = setAttributes(attributes.element, {...attributes, element: ""});
			break;
		}
		return html;
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
