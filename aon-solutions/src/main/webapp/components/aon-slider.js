import {AonElement} from './AonElement.js';

export class AonSlider extends AonElement {

	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

  get min() {
    return this.getAttribute('min')
  }

  set min(min) {
    this.setAttribute('min', min);
  }

  get max() {
    return this.getAttribute('max')
  }

  set max(max) {
    this.setAttribute('max', max);
  }

  get title() {
    return this.getAttribute('title')
  }

  set title(title) {
    this.setAttribute('title', title);
  }
	constructor () {
		super();
	}

	connectedCallback () {
    let min = this.min || 0;
    let max = this.max || 100;

		this.innerHTML = `
      <label class="aonSlider">
        <input type="range" min="${min}" max="${max}">
        <span>${this.title}</span>
      </label>
		`;
	}

}

window.customElements.define('aon-slider',  AonSlider);
