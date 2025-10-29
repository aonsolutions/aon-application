import { AonElement } from "./AonElement.js";
import { CONSTANT, TAG } from "../environments/environments.js";
import { AonIcon } from "./aon-icon.js";
// import * as LS from '../services/localStorageService.js';

export class AonIconButton extends AonElement {
  BUTTON;
  BUTTON_ELEMENT;
  ICON;
  ICON_ELEMENT;
  AON_ICON;
  IMAGE;

  static get observedAttributes() {
    return [CONSTANT.DISABLED, CONSTANT.VISIBLE,  CONSTANT.ICON, "color"];
  }

  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
  }

  get icon() {
    return this.getAttribute("icon");
  }

  set icon(icon) {
    this.setAttribute("icon", icon);
  }

  get aonIcon() {
    return this.getAttribute("icon");
  }

  set aonIcon(aonIcon) {
    this.setAttribute("icon", aonIcon);
  }

  get image() {
    return this.getAttribute("image");
  }

  set image(image) {
    this.setAttribute("image", image);
  }

  get color() {
    return this.getAttribute("color");
  }

  set color(color) {
    this.setAttribute("color", color);
  }

  get outlined() {
    return this.getAttribute("outlined");
  }

  set outlined(outlined) {
    this.setAttribute("outlined", outlined);
  }

  get noHover() {
    return this.getAttribute("noHover");
  }

  set noHover(noHover) {
    this.setAttribute("noHover", noHover);
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

  get title() {
    return this.getAttribute(CONSTANT.TITLE);
  }

  set title(title) {
    this.setAttribute(CONSTANT.TITLE, title);
  }

  get background() {
    return this.getAttribute("background");
  }

  set background(background) {
    this.setAttribute("background", background);
  }

  get backgroundColor() {
    return this.getAttribute("backgroundColor");
  }

  set backgroundColor(backgroundColor) {
    this.setAttribute("backgroundColor", backgroundColor);
  }

  attributeChangedCallback(name, oldValue, newValue) {
    this.initialize();
    if (CONSTANT.DISABLED === name) {
      this.getButton().disabled = newValue;
    }else if (CONSTANT.VISIBLE === name) {
      if (this.getAttribute(CONSTANT.VISIBLE) !== undefined && "false" === this.getAttribute(CONSTANT.VISIBLE)) {
        this.style.width = "0px";
        this.style.display = "none";
      } else {
        this.style.width = null;
        this.style.display = "block";
      }
    } else if (CONSTANT.ICON === name) {
      this.getIcon().innerHTML = this.getAttribute(CONSTANT.ICON);
    } else if ("image" === name) {
      let image = this.getElement(this.IMAGE);
      if (image) image.src = this.getAttribute("image");
    } else if ("color" === name) {
      this.getButton().style.color = this.getAttribute("color") ? this.getAttribute("color") : "#5f6368";
    } else if ("background" === name) {
      this.getButton().style.backgroundColor = this.hasAttribute("background") ? this.getAttribute("background") : "transparent";
    }
  }

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize() {
    this.BUTTON   = this.id + "IconButton";
    this.ICON     = this.id + "Icon";
    this.AON_ICON = this.id + "AonIcon";
    this.IMAGE    = this.id + "Image";
  }

  build() {
    this.clear();
    // let background = this.hasAttribute("background") ? this.getAttribute("background")  : "transparent";

    this.getButton().className = "aonIconButton";
    this.getButton().type = "button";
    // this.getButton().style.color = this.getAttribute("color") ? this.getAttribute("color") : "#5f6368";
    // this.getButton().style.backgroundColor = background;
    if (this.hasAttribute(CONSTANT.TITLE)) {
      this.getButton().title = this.getAttribute(CONSTANT.TITLE);
    }

    if (this.getAttribute(CONSTANT.DISABLED)) {
      this.getButton().setAttribute(CONSTANT.DISABLED, true);
    }

    if (this.getAttribute(CONSTANT.VISIBLE) !== undefined && "false" === this.getAttribute(CONSTANT.VISIBLE)) {
      this.style.width = "0px";
      this.style.display = "none";
    }

    if (this.hasAttribute("icon") || this.hasAttribute("aonIcon") || this.hasAttribute("aonicon")) {
      this.getButton().innerHTML = "";
      // llega como = icon, aonIcon, aonicon
      let iconType = this.getAttribute("icon") ? this.getAttribute("icon") : this.hasAttribute("aonIcon");
      iconType     = this.getAttribute("aonicon") ? this.getAttribute("aonicon") : iconType;
      let icon          = new AonIcon();
      icon.id           = this.AON_ICON;
      icon.icon         = iconType;
      this.ICON_ELEMENT = icon;
      this.getButton().appendChild(icon);
    } else if (this.hasAttribute("image")) {
      let image = this.createElement(TAG.IMG);
      image.id = this.IMAGE;
      image.style.width = "24px";
      image.style.height = "24px";
      image.src = this.getAttribute("image");
      this.getButton().appendChild(image);
    }

    this.appendChild(this.getButton());
  }

  getBackgroundHover() {
    return this.backgroundColor && !this.isLightColor(this.hexToRgb(this.backgroundColor)) ? "rgba(0,0,0,0.15)" : "rgba(255,255,255,0.15)";
  }

  clear() {
    this.innerHTML = '';
  }

  isVisible() {
    return !this.hasAttribute(CONSTANT.VISIBLE) ||
      (this.hasAttribute(CONSTANT.VISIBLE) && CONSTANT.FALSE !== this.getAttribute(CONSTANT.VISIBLE));
  }

  isDisabled() {
    return this.getButton().hasAttribute(CONSTANT.DISABLED);
  }

  setDisabled(disabled) {
    this.disabled = disabled;
    if(disabled) {
      this.getButton().disabled = disabled;
    } else { 
      this.getButton().removeAttribute(CONSTANT.DISABLED);
    }
  }

  setIcon(icon) {
    this.icon = icon;
    // El componente icon en el boton
    this.getIcon().icon = icon;
  }

  getIcon(){
    this.ICON_ELEMENT = this.ICON_ELEMENT || new AonIcon();
    this.ICON_ELEMENT.id  = this.ICON;
    return this.ICON_ELEMENT;
  }
  
  getButton(){
    this.BUTTON_ELEMENT = this.BUTTON_ELEMENT || this.createElement(TAG.BUTTON);
    this.BUTTON_ELEMENT.id  = this.BUTTON;
    return this.BUTTON_ELEMENT;
  }

  setColor(color) {
    this.color = color;
    if(this.getButton())
      this.getButton().style.color = this.color;
  }

  setBackgroundColor(color) {
    this.backgroundColor = color;
  }

  isLightColor(colorString) {
    const rgba = colorString.replace(/[^\d,]/g, '').split(',').map(Number);
    const [r, g, b] = rgba;

    const brightness = (r * 299 + g * 587 + b * 114) / 1000;
    return brightness > 128; 
  }

  hexToRgb(hex) {
    hex = hex.replace(/^#/, '');

    if (hex.length === 3) {
      hex = hex.split('').map(c => c + c).join('');
    }

    const r = parseInt(hex.substring(0, 2), 16);
    const g = parseInt(hex.substring(2, 4), 16);
    const b = parseInt(hex.substring(4, 6), 16);

    return `rgb(${r}, ${g}, ${b})`;
  }

}
if(!window.customElements.get('aon-icon-button')){
  window.customElements.define("aon-icon-button", AonIconButton);
}