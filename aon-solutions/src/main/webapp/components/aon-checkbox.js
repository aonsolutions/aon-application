import { CONSTANT, EVENT, TAG } from '../environments/environments.js';
import {AonElement} from './AonElement.js';

export class AonCheckbox extends AonElement {

	INPUT;

	static get observedAttributes() {
		return [CONSTANT.VALUE];
	}

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	get name() {
		return this.getAttribute(CONSTANT.NAME);
	}

	set name(name) {
		this.setAttribute(CONSTANT.NAME, name);
	}


	get value() {
		return this.getAttribute(CONSTANT.VALUE);
	}

	set value(value) {
		this.setAttribute(CONSTANT.VALUE, value);
	}

	get description() {
		return this.getAttribute(CONSTANT.DESCRIPTION);
	}

	set description(description) {
		this.setAttribute(CONSTANT.DESCRIPTION, description);
	}

	get visible() {
		return this.getAttribute(CONSTANT.VISIBLE);
	}

	set visible(visible) {
		this.setAttribute(CONSTANT.VISIBLE, visible);
	}

	get disabled() {
		return this.getAttribute(CONSTANT.DISABLED);
	}

	set disabled(disabled) {
		this.setAttribute(CONSTANT.DISABLED, disabled);
	}

	attributeChangedCallback(name, oldValue, newValue) {
		if(CONSTANT.VALUE === name){
			let input = this.getElement(this.getAttribute(CONSTANT.ID) + 'Input');
			if(this.hasAttribute(CONSTANT.VALUE) && "true" === this.getAttribute(CONSTANT.VALUE)){
				input.setAttribute(CONSTANT.CHECKED, CONSTANT.CHECKED);
			} else {
				input.removeAttribute(CONSTANT.CHECKED);
			}
			
			this.dispatchEvent(new Event(EVENT.CHANGE));
		}
	}

	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
		this.appendChild(this.build());
	}

	initialize() {
		this.INPUT = this.id + CONSTANT.INPUT.initCap();
	}

	build() {
		if(this.hasAttribute(CONSTANT.VISIBLE) && 'false' == this.getAttribute(CONSTANT.VISIBLE)){
			this.style.width = '0px';
			this.style.display = 'none';
		}

		let input = this.createElement(TAG.INPUT);
		input.id = this.INPUT;
		input.name = this.name;

		input.setAttribute('type', 'checkbox');
		if(this.hasAttribute(CONSTANT.VALUE) && "true" === this.getAttribute(CONSTANT.VALUE)){
			input.setAttribute('checked', 'checked');
		}
		
		if(this.checked) 	
			input.checked = 'checked';

		if(this.hasAttribute(CONSTANT.DISABLED)){
			input.setAttribute(CONSTANT.DISABLED, CONSTANT.DISABLED);
		}
		input.addEventListener('change', () => {
			this.setAttribute(CONSTANT.VALUE, this.getElement(input.getAttribute(CONSTANT.ID)).checked);
		});

		const labelId = this.id+"Label";
		let label = this.getElement(labelId);
		if(!label){
			label = this.createElement('label');
			label.id = labelId;
			label.className = 'aonCheckbox';
			label.style.marginBottom = '0px';
	
			let span = this.createElement('span');
			span.innerHTML = this.getAttribute(CONSTANT.DESCRIPTION);
	
			label.appendChild(input);
			label.appendChild(span);
		}

		return label;
	}

	getValue() {
		return this.value && this.value === 'true';
	}

	clear(){
		this.value = "";
	}

	focus() {
		this.getElement(this.INPUT).focus();
	}
}
if(!window.customElements.get('aon-checkbox')){
	window.customElements.define('aon-checkbox',  AonCheckbox);
}
