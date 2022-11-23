import { AonElement } from "./AonElement.js";

import { CONSTANT } from "../environments/environments.js";
import { AonIcon } from "./aon-icon.js";

export class AonIconButton extends AonElement {
  BUTTON;
  BUTTON_ELEMENT;
  ICON;
  ICON_ELEMENT;
  AON_ICON;
  IMAGE;

  static get observedAttributes() {
    return [CONSTANT.DISABLED, CONSTANT.VISIBLE, "icon", "color"];
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
    return this.getAttribute("aonIcon");
  }

  set aonIcon(aonIcon) {
    this.setAttribute("aonIcon", aonIcon);
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

  attributeChangedCallback(name, oldValue, newValue) {
    this.initialize();
    if (CONSTANT.DISABLED === name) {
      this.getButton().disabled = newValue;
    }

    if (CONSTANT.VISIBLE === name) {
      if (
        this.getAttribute(CONSTANT.VISIBLE) != undefined &&
        "false" == this.getAttribute(CONSTANT.VISIBLE)
      ) {
        this.style.width = "0px";
        this.style.display = "none";
      } else {
        this.style.width = null;
        this.style.display = "block";
      }
    }

    if ("icon" === name) {
      let icon = this.getIcon();
      if (icon) icon.innerHTML = this.getAttribute("icon");
    }

    if ("image" === name) {
      let image = this.getElement(this.IMAGE);
      if (image) image.src = this.getAttribute("image");
    }

    if ("color" === name) {
      let button = this.getButton();
      if (button)
        button.style.color = this.getAttribute("color")
          ? this.getAttribute("color")
          : "#5f6368";
    }

    if ("background" === name) {
      let button = this.getButton();
      if (button)
        button.style.backgroundColor = this.hasAttribute("background")
          ? this.getAttribute("background")
          : "transparent";
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
    this.BUTTON = this.id + "IconButton";
    this.ICON = this.id + "Icon";
    this.AON_ICON = this.id + "AonIcon";
    this.IMAGE = this.id + "Image";
    this.BUTTON_ELEMENT = document.createElement("button");    
    this.ICON_ELEMENT   = document.createElement("i");
  }

  build() {
    this.clear();
    let background = this.hasAttribute("background")
      ? this.getAttribute("background")
      : "transparent";


    this.BUTTON_ELEMENT.id = this.BUTTON;
    this.BUTTON_ELEMENT.className = "aonIconButton";
    this.BUTTON_ELEMENT.style.color = this.getAttribute("color")
      ? this.getAttribute("color")
      : "#5f6368";
    this.BUTTON_ELEMENT.style.backgroundColor = background;
    if (this.hasAttribute(CONSTANT.TITLE)) {
      this.BUTTON_ELEMENT.title = this.getAttribute(CONSTANT.TITLE);
    }

    if (this.getAttribute(CONSTANT.DISABLED)) {
      this.BUTTON_ELEMENT.setAttribute(CONSTANT.DISABLED, true);
    }

    if (
      this.getAttribute(CONSTANT.VISIBLE) != undefined &&
      "false" == this.getAttribute(CONSTANT.VISIBLE)
    ) {
      this.style.width = "0px";
      this.style.display = "none";
    }

    if (!this.getAttribute("noHover")) {
      this.BUTTON_ELEMENT.addEventListener("mouseover", () => {
        this.BUTTON_ELEMENT.style.backgroundColor = "#f1f1f1";
        this.BUTTON_ELEMENT.style.color = "black";
      });

      this.BUTTON_ELEMENT.addEventListener("mouseleave", () => {
        this.BUTTON_ELEMENT.style.backgroundColor = background;
        this.BUTTON_ELEMENT.style.color = this.getAttribute("color")
          ? this.getAttribute("color")
          : "#5f6368";
      });

      this.addEventListener("click", () => {
        this.BUTTON_ELEMENT.style.backgroundColor = "#ddd";
      });
    }

    if (this.hasAttribute("icon")) {
      this.ICON_ELEMENT.id = this.ICON;
      this.ICON_ELEMENT.className = this.getAttribute("outlined")
        ? "material-icons-outlined"
        : "material-icons";
      this.ICON_ELEMENT.innerHTML = this.getAttribute("icon");
      this.BUTTON_ELEMENT.appendChild(this.ICON_ELEMENT);
    } else if (this.hasAttribute("image")) {
      let image = document.createElement("img");
      image.id = this.IMAGE;
      image.style.width = "24px";
      image.style.height = "24px";
      image.src = this.getAttribute("image");
      this.BUTTON_ELEMENT.appendChild(image);
    }

    if (this.hasAttribute("aonIcon")) {
      this.BUTTON_ELEMENT.innerHTML = "";
      let aonIcon = new AonIcon();
      aonIcon.id = this.AON_ICON;
      aonIcon.icon = this.aonIcon;
      this.BUTTON_ELEMENT.appendChild(aonIcon);
      
      if(this.hasAttribute("color")){
        aonIcon.color = this.getAttribute("color")
      }
    }

    this.appendChild(this.BUTTON_ELEMENT);
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
    } else this.getButton().removeAttribute(CONSTANT.DISABLED);
  }

  getIcon(){
    return this.ICON_ELEMENT;
  }
  
  getButton(){
    return this.BUTTON_ELEMENT;
  }
}
if(!window.customElements.get('aon-icon-button')){
  window.customElements.define("aon-icon-button", AonIconButton);
}
