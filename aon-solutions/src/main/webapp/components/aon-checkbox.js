import { CONSTANT } from '../environments/environments.js';
import {AonElement} from './AonElement.js';

export class AonCheckbox extends AonElement {

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
				input.setAttribute('checked', 'checked');
			} else input.removeAttribute('checked');
		}
	}

	constructor () {
		super();
	}

	connectedCallback () {
		this.appendChild(this.build());
	}

	build() {
		if(this.hasAttribute(CONSTANT.VISIBLE) && 'false' == this.getAttribute(CONSTANT.VISIBLE)){
			this.style.width = '0px';
			this.style.display = 'none';
		}

		let input = this.createElement('input');
		input.setAttribute(CONSTANT.ID, this.getAttribute(CONSTANT.ID) + 'Input');

		input.name = this.name;

		input.setAttribute('type', 'checkbox');
		if(this.hasAttribute(CONSTANT.VALUE) && "true" === this.getAttribute(CONSTANT.VALUE)){
			input.setAttribute('checked', 'checked');
		}
		if(this.hasAttribute(CONSTANT.DISABLED)){
			input.setAttribute(CONSTANT.DISABLED, CONSTANT.DISABLED);
		}
		input.addEventListener('change', () => {
			this.setAttribute(CONSTANT.VALUE, this.getElement(input.getAttribute(CONSTANT.ID)).checked);
		});

		let label = this.createElement('label');
		label.className = 'aonCheckbox';
		label.style.marginBottom = '0px';

		let span = this.createElement('span');
		span.innerHTML = this.getAttribute(CONSTANT.DESCRIPTION);

		label.appendChild(input);
		label.appendChild(span);

		return label;
	}

	getValue() {
		return this.value && this.value === 'true';
	}
}
if(!window.customElements.get('aon-checkbox')){
	window.customElements.define('aon-checkbox',  AonCheckbox);
}
