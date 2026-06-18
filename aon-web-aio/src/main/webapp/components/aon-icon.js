import {AonElement} from './AonElement.js';
import {icons} from '../assets/icons/icons.js';
import { CONSTANT, CSS, TAG } from '../environments/environments.js';

export class AonIcon extends AonElement {

  TYPES = {
    MATERIAL: 'MATERIAL',
    AON: 'AON',
    AON_SYMBOL: 'AON_SYMBOL',
    IMAGE: 'IMAGE'
  };
  type;

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
      let symbol = this.querySelector('i.' + CSS.AON_SYMBOLS_OUTLINED);
      if(symbol) {
        symbol.style.fontSize = newValue;
      }
    } else if('color' === name){
      let path = this.querySelector('path');
      if(path) {
        path.style.fill = newValue;
      }
      let symbol = this.querySelector('i.' + CSS.AON_SYMBOLS_OUTLINED);
      if(symbol) {
        symbol.style.color = newValue;
      }
    }
  }

  constructor () {
		super();
	}

	connectedCallback () {
    this.initialize();
    this.build();
  }

  initialize() {
    this.type = this.type || (this.hasAttribute('aonSymbol') ? this.TYPES.AON_SYMBOL : this.TYPES.AON);
  }

  build() {
    if(this.TYPES.MATERIAL === this.type) {
      this.buildMaterialIcon();
    } else if(this.TYPES.AON_SYMBOL === this.type) {
      this.buildAonSymbolIcon();
    } else if(this.TYPES.IMAGE === this.type) {
      this.buildImageIcon();
    } else this.buildAonIcon();
  }

  buildMaterialIcon() {
    let icon = this.createElement(TAG.I);
    icon.id = this.id + CONSTANT.MATERIAL.initCap();
    icon.className = CSS.MATERIAL_ICONS;
    icon.innerHTML = this.icon;
	icon.setAttribute('icon', this.icon);
    this.appendChild(icon);
  }

  buildAonSymbolIcon() {
    let icon = this.createElement(TAG.I);
    icon.id = this.id + CONSTANT.AON_SYMBOL.initCap();
    icon.className = CSS.AON_SYMBOLS_OUTLINED;
    icon.innerHTML = this.icon;
    icon.setAttribute('icon', this.icon);
    if(this.hasAttribute('size')) {
      icon.style.fontSize = this.getAttribute('size');
    }
    if(this.hasAttribute('color')) {
      icon.style.color = this.getAttribute('color');
    }
    this.appendChild(icon);
  }

  buildImageIcon() {
		let img = document.createElement(TAG.IMG);
		img.style.width = '24px';
		img.src = this.icon;
		this.appendChild(img);
  }

  buildAonIcon() {
    let icon = icons[this.getAttribute('icon')];
    let left = icon.left || 0;
    let top = icon.top || 0;
    let transform = icon.transform || "";
    
    if(icon && icon.paths) {
      let html = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="${left} ${top} ${icon.width} ${icon.height}"
        width="${this.hasAttribute('size') ? this.getAttribute('size') : '24px'}"
        height="${this.hasAttribute('size') ? this.getAttribute('size') : '24px'}">
        <g id="${this.getAttribute('icon')}" transform="${transform}">`;
      icon.paths.forEach(p => {
        if(p.type && p.type === 'circle') {
          html = html + `
          <circle cx="${p.cx}" cy="${p.cy}" r="${p.r} style="fill:"${p.fill};" />
          `;
        } else {
          html = html + `
          <path d="${p.path}" style="fill:${p.fill ? p.fill : (this.hasAttribute('color') ? this.getAttribute('color'): '#5f6368')};"/>
          `;
        } 

      });
      html = html + `</g></svg>`;
      this.innerHTML = html;
    } else if (icon && icon.path) {
      this.innerHTML = `
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="${left} ${top} ${icon.width} ${icon.height}"
          width="${this.hasAttribute('size') ? this.getAttribute('size') : '24px'}"
          height="${this.hasAttribute('size') ? this.getAttribute('size') : '24px'}">
        <g id="${this.getAttribute('icon')}" transform="${transform}">
          <path d="${icon.path}" style="fill:${this.hasAttribute('color') ? this.getAttribute('color'): '#5f6368'};"/>
        </g>
      </svg>
		  `;
    }
    let svg = this.querySelector('svg');
    svg.style.verticalAlign = 'middle';
  }
}
if(!window.customElements.get(TAG.AON_ICON)){
  window.customElements.define(TAG.AON_ICON, AonIcon);
}
