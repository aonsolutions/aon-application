import {AonElement} from './AonElement.js';
import {icons} from '../assets/icons/icons.js';
import { CONSTANT } from '../environments/environments.js';

export class AonIcon extends AonElement {

  static get observedAttributes() {
    return ['icon', 'color', 'size'];
  }

  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
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
    if('size' === name){
      let svg = this.querySelector('svg');
      if(svg) {
        svg.style.width = newValue;
        svg.style.height = newValue;
      }
    } else if('color' === name){
      let path = this.querySelector('path');
      if(path) {
        path.style.fill = newValue;
      }
    }
  }

  constructor () {
		super();
	}

	connectedCallback () {
    this.build();
  }

  build() {
    let icon = icons[this.getAttribute('icon')];
    let left = icon.left || 0;
    let top = icon.top || 0;
    if(icon && icon.paths) {
      let html = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="${left} ${top} ${icon.width} ${icon.height}"
        width="${this.hasAttribute('size') ? this.getAttribute('size') : '24px'}"
        height="${this.hasAttribute('size') ? this.getAttribute('size') : '24px'}">
        <g id="${this.getAttribute('icon')}">`;
      icon.paths.forEach(p => {
          html = html + `
            <path d="${p.path}" style="fill:${p.fill ? p.fill : (this.hasAttribute('color') ? this.getAttribute('color'): '#5f6368')};"/>
          `
      });
      html = html + `</g></svg>`;
      this.innerHTML = html;
    } else if (icon && icon.path) {
      this.innerHTML = `
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="${left} ${top} ${icon.width} ${icon.height}"
          width="${this.hasAttribute('size') ? this.getAttribute('size') : '24px'}"
          height="${this.hasAttribute('size') ? this.getAttribute('size') : '24px'}">
        <g id="${this.getAttribute('icon')}">
          <path d="${icon.path}" style="fill:${this.hasAttribute('color') ? this.getAttribute('color'): '#5f6368'};"/>
        </g>
      </svg>
		  `;
    }
    let svg = this.querySelector('svg');
    svg.style.verticalAlign = 'middle';
  }
}
if(!window.customElements.get('aon-icon')){
  window.customElements.define('aon-icon', AonIcon);
}
