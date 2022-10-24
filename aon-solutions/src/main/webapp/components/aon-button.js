import {AonElement} from './AonElement.js';
import {icons} from '../assets/icons/icons.js';
import { CONSTANT, CSS, TAG } from '../environments/environments.js';

export class AonButton extends AonElement {

  BUTTON;
  ICON;
  TEXT;

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

  get title() {
    return this.getAttribute('title');
  }

  set title(title) {
    this.setAttribute('title', title);
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

  constructor () {
		super();
	}

	connectedCallback () {
    this.initialize();
    this.build();
  }

  initialize() {
    this.id = this.id || 'aonButton';
    this.BUTTON = this.id + CONSTANT.BUTTON.initCap();
    this.ICON = this.BUTTON + CONSTANT.ICON.initCap();
    this.TEXT = this.BUTTON + CONSTANT.TEXT.initCap();
  }

  build() {
    let button = this.createElement(TAG.BUTTON);
    button.id = this.BUTTON;
    button.className = CSS.AON_BUTTON;
    this.appendChild(button);

    if(this.color) {
      button.style.backgroundColor =this.color;
    }

    if(this.icon) {
      button.style.display = 'flex';
      button.style.fontSize = '13px';
      let icon = this.createElement(TAG.I);
      icon.id = this.ICON;
      icon.className = CSS.MATERIAL_ICONS;
      icon.innerHTML = this.getIcon();
      button.appendChild(icon);
    }

    if(this.title) {
      let text = this.createElement(TAG.SPAN);
      text.id = this.TEXT;
      text.innerHTML = this.title;
      button.appendChild(text);
    }
  }

  getId() {
    return this.id;
  }

  setId(id) {
    this.id = id;
  }

  getIcon() {
    return this.icon;
  }

  setIcon(icon) {
    this.icon = icon;
  }

  getTitle() {
    return this.title;
  }

  setTitle(title) {
    this.title = title;
  }
  
  getColor() {
    return this.color;
  }

  setColor(color) {
    this.color = color;
  }
}
if(!window.customElements.get(TAG.AON_BUTTON)){
  window.customElements.define(TAG.AON_BUTTON, AonButton);
}
