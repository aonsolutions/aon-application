import { AonElement } from "./AonElement.js";

import "./aon-icon.js";
import { CONSTANT } from "./environments.js";

export class AonIconButton extends AonElement {
  BUTTON;
  ICON;
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
      this.getElement(this.BUTTON).disabled = newValue;
    }

    if (CONSTANT.VISIBLE === name) {
      if (
        this.getAttribute(CONSTANT.VISIBLE) != undefined &&
        "false" == this.getAttribute(CONSTANT.VISIBLE)
      ) {
        this.style.width = "0px";
        this.display = "none";
      } else {
        this.style.width = null;
        this.style.display = "block";
      }
    }

    if ("icon" === name) {
      let icon = this.getElement(this.ICON);
      if (icon) icon.innerHTML = this.getAttribute("icon");
    }

    if ("image" === name) {
      let image = this.getElement(this.IMAGE);
      if (image) image.src = this.getAttribute("image");
    }

    if ("color" === name) {
      let button = this.getElement(this.BUTTON);
      if (button)
        button.style.color = this.getAttribute("color")
          ? this.getAttribute("color")
          : "#5f6368";
    }

    if ("background" === name) {
      let button = this.getElement(this.BUTTON);
      if (button)
        button.backgroundColor = this.hasAttribute("background")
          ? this.getAttribute("background")
          : "transparent";
    }
  }

  constructor() {
    super();
  }

  initialize() {
    this.BUTTON = this.id + "IconButton";
    this.ICON = this.id + "Icon";
    this.AON_ICON = this.id + "AonIcon";
    this.IMAGE = this.id + "Image";
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  build() {
    this.clear();
    let background = this.hasAttribute("background")
      ? this.getAttribute("background")
      : "transparent";
    let button = document.createElement("button");
    button.setAttribute("id", this.BUTTON);
    button.className = "aonIconButton";
    button.style.color = this.getAttribute("color")
      ? this.getAttribute("color")
      : "#5f6368";
    button.style.backgroundColor = background;
    if (this.hasAttribute(CONSTANT.TITLE)) {
      button.title = this.getAttribute(CONSTANT.TITLE);
    }

    if (this.getAttribute(CONSTANT.DISABLED)) {
      button.setAttribute(CONSTANT.DISABLED, true);
    }

    if (
      this.getAttribute(CONSTANT.VISIBLE) != undefined &&
      "false" == this.getAttribute(CONSTANT.VISIBLE)
    ) {
      this.style.width = "0px";
      this.style.display = "none";
    }

    if (!this.getAttribute("noHover")) {
      button.addEventListener("mouseover", () => {
        button.style.backgroundColor = "#f1f1f1";
        button.style.color = "black";
      });

      button.addEventListener("mouseleave", () => {
        button.style.backgroundColor = background;
        button.style.color = this.getAttribute("color")
          ? this.getAttribute("color")
          : "#5f6368";
      });

      this.addEventListener("click", () => {
        button.style.backgroundColor = "#ddd";
      });
    }

    if (this.hasAttribute("icon")) {
      let icon = document.createElement("i");
      icon.id = this.ICON;
      icon.className = this.getAttribute("outlined")
        ? "material-icons-outlined"
        : "material-icons";
      icon.innerHTML = this.getAttribute("icon");
      button.appendChild(icon);
    } else if (this.hasAttribute("image")) {
      let image = document.createElement("img");
      image.id = this.IMAGE;
      image.style.width = "24px";
      image.style.height = "24px";
      image.src = this.getAttribute("image");
      button.appendChild(image);
    }
    if (this.hasAttribute("aonIcon")) {
      button.innerHTML = `<aon-icon id="${this.AON_ICON}" icon="${this.aonIcon}"></aon-icon>`;
    }
    this.appendChild(button);
  }

  clear() {
    this.innerHTML = '';
  }

  isVisible() {
    return !this.hasAttribute(CONSTANT.VISIBLE) ||
      (this.hasAttribute(CONSTANT.VISIBLE) && CONSTANT.FALSE !== this.getAttribute(CONSTANT.VISIBLE));
  }

  setDisabled(disabled) {
    this.disabled = disabled;
    this.getElement(this.BUTTON).disabled = disabled;
  }
  
  getButton(){
    return this.getElement(this.BUTTON);
  }
}
if(!window.customElements.get('aon-icon-button')){
  window.customElements.define("aon-icon-button", AonIconButton);
}
