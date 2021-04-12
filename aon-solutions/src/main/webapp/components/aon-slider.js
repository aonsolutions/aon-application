import {AonElement} from './AonElement.js';

import * as AON_TAG from "../../environments/aonTag.js";
import * as CONSTANT from "../../environments/constants.js";
import * as CSS from "../../environments/css.js";
import * as EVENT from "../../environments/aonEvent.js";


export class AonSlider extends AonElement {

	INPUT;

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

  get min() {
    return this.getAttribute(CONSTANT.MIN);
  }

  set min(min) {
    this.setAttribute(CONSTANT.MIN, min);
  }

  get max() {
    return this.getAttribute(CONSTANT.MAX);
  }

  set max(max) {
    this.setAttribute(CONSTANT.MAX, max);
  }

  get title() {
    return this.getAttribute(CONSTANT.TITLE);
  }

  set title(title) {
    this.setAttribute(CONSTANT.TITLE, title);
  }

	get value() {
		return this.getAttribute(CONSTANT.VALUE);
	}

	set value(value) {
		return this.setAttribute(CONSTANT.VALUE, value);
	}

	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
    let min = this.min || 0;
    let max = this.max || 100;

		this.innerHTML = `
      <label class="${CSS.AON_SLIDER}">
        <input id="${this.INPUT}" type="range" min="${min}" max="${max}">
        <span>${this.title}</span>
      </label>
		`;

		let input = this.getElement(this.INPUT);
		this.value = input.value;
		input.addEventListener(EVENT.CHANGE, (e) => {
			this.value = input.value;
			this.dispatchEvent(new Event(EVENT.CHANGE));
		});
	}

	initialize() {
		this.INPUT = this.id + 'Input';
	}
}

if(!window.customElements.get(AON_TAG.AON_SLIDER)){
	window.customElements.define(AON_TAG.AON_SLIDER,  AonSlider);
}
