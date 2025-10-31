import {AonElement} from './AonElement.js';
import { CONSTANT, TAG, EVENT } from '../environments/environments.js';
//import '../css/aon-color.css';

export class AonColor extends AonElement {

	INPUT;
	TITLE;

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

  get value() {
    return this.getAttribute(CONSTANT.VALUE)
  }

  set value(value) {
    this.setAttribute(CONSTANT.VALUE, value);
  }

	get title() {
    return this.getAttribute(CONSTANT.TITLE)
  }

  set title(title) {
    this.setAttribute(CONSTANT.TITLE, title);
  }

  get disabled() {
    return this.getAttribute(CONSTANT.DISABLED)
  }

  set disabled(disabled) {
    this.setAttribute(CONSTANT.DISABLED, disabled);
  }

	get readonly() {
		return this.getAttribute(CONSTANT.READONLY);
	}

	set readonly(readonly) {
		this.setAttribute(CONSTANT.READONLY, readonly);
	}

  get name() {
		return this.getAttribute(CONSTANT.NAME);
	}

	set name(name) {
		this.setAttribute(CONSTANT.NAME, name);
	}

	constructor () {
		super();
	}

	connectedCallback () {
    this.initialize();
    let label = this.createElement(TAG.LABEL);
    label.className = "aonColor";
    this.appendChild(label);

    let input = this.createElement(TAG.INPUT);
    input.id   = this.INPUT;
    input.name = this.name || this.INPUT ;
    input.type = "color";
    if(this.disabled == "true") input.disabled = this.disabled;
    input.value = this.value || '#002469';
    label.appendChild(input);

    let span = this.createElement(TAG.SPAN);
    span.id = this.TITLE;
    span.style.marginLeft = '15px';
    label.appendChild(span);
		span.innerHTML = this.hasAttribute(CONSTANT.TITLE)
			? this.getAttribute(CONSTANT.TITLE) : CONSTANT.EMPTY;

    input.addEventListener(EVENT.CHANGE, () => {
      this.value = input.value;
    });

		if(this.hasAttribute(CONSTANT.READONLY)) {
			input.setAttribute(CONSTANT.READONLY, CONSTANT.READONLY);
		}
   
    this.value = this.value || '#002469';
	}

  initialize() {
    const id = this.id || `aonColor-${generateSimpleUUID()}`;
    this.id = id;
		this.INPUT = this.id + CONSTANT.INPUT.initCap();
		this.TITLE = this.id + CONSTANT.TITLE.initCap();
  }
}
if(!window.customElements.get('aon-color')){
	window.customElements.define('aon-color',  AonColor);
}
