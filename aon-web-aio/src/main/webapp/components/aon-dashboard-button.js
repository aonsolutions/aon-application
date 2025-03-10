import {AonElement} from './AonElement.js';
import { CONSTANT, CSS, TAG } from '../environments/environments.js';

export class AonDashboardButton extends AonElement {

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

  get logo() {
    return this.getAttribute('logo');
  }

  set logo(logo) {
    this.setAttribute('logo', logo);
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

  get disabled() {
    return this.getAttribute('disabled');
  }

  set disabled(disabled) {
    this.setAttribute('disabled', disabled);
  }

  get message() {
    return this.getAttribute('message');
  }

  set message(message) {
    this.setAttribute('message', message);
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
    button.className = CSS.AON_DASHBOARD_BUTTON;
    button.disabled = this.isDisabled();
    this.appendChild(button);

    if(this.color) {
      button.style.backgroundColor =this.color;
    }

    if(this.icon) {
      let icon = this.createElement(TAG.I);
      icon.id = this.ICON;
      icon.className = CSS.MATERIAL_ICONS;
      icon.innerHTML = this.getIcon();
      button.appendChild(icon);
    }

    if(this.logo){
      let spanIcon = this.createElement(TAG.SPAN);
		  spanIcon.innerHTML = `<aon-icon icon="${this.logo}" color="black" size="30px"></aon-icon>`;
		  button.appendChild(spanIcon);
    }

    if(this.message) {
      let text = this.createElement(TAG.SPAN);
      text.id = this.TEXT;
      text.innerHTML = this.message;
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

  getLogo() {
    return this.logo;
  }

  setLogo(logo) {
    this.logo = logo;
  }

  setMessage(message) {
    this.message = message;
  }
  
  getColor() {
    return this.color;
  }

  setColor(color) {
    this.color = color;
  }

  isDisabled() {
    return this.disabled;
  }

  setDisabled(disabled) {
    this.disabled = disabled;
    let button = this.getElement(this.BUTTON);
    if(button) button.disabled = disabled;
  }
}
if(!window.customElements.get(TAG.AON_DASHBOARD_BUTTON)){
  window.customElements.define(TAG.AON_DASHBOARD_BUTTON, AonDashboardButton);
}
