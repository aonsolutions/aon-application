import {icons} from '../assets/icons/icons.js';

class AonIcon extends HTMLElement {

  static get observedAttributes() {
    return ['icon', 'color', 'size'];
  }

  get id() {
    return this.getAttribute('id');
  }

  set id(id) {
    this.setAttribute('id', id);
  }

  get icon() {
    return this.getAttribute('icon');
  }

  set icon(icon) {
    this.setAttribute('icon', icon);
  }

  get color() {
    return this.getAttribute('color');
  }

  set color(color) {
    this.setAttribute('color', color);
  }

  get size() {
    return this.getAttribute('size');
  }

  set size(size) {
    this.setAttribute('size', size);
  }

  attributeChangedCallback(name, oldValue, newValue) {
      this.build();
  }

  constructor () {
		super();
	}

	connectedCallback () {
    this.build();
  }

  build() {
    let icon = icons[this.getAttribute('icon')];
  	this.innerHTML = `
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 ${icon.width} ${icon.height}"
          width="${this.hasAttribute('size') ? this.getAttribute('size') : '24px'}"
          height="${this.hasAttribute('size') ? this.getAttribute('size') : '24px'}">
        <g id="${this.getAttribute('icon')}">
          <path d="${icon.path}" style="fill:${this.hasAttribute('color') ? this.getAttribute('color'): '#5f6368'};"/>
        </g>
      </svg>
		`;
  }
}

window.customElements.define('aon-icon', AonIcon);
