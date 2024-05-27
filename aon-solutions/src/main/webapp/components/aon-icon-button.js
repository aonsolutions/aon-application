import { AonElement } from "./AonElement.js";
import { CONSTANT, TAG } from "../environments/environments.js";
import { AonIcon } from "./aon-icon.js";

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
    }else if (CONSTANT.VISIBLE === name) {
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
    this.BUTTON = this.id + "IconButton";
    this.ICON = this.id + "Icon";
    this.AON_ICON = this.id + "AonIcon";
    this.IMAGE = this.id + "Image";
  }

  build() {
    this.clear();
    let background = this.hasAttribute("background") ? this.getAttribute("background")  : "transparent";

    this.getButton().className = "aonIconButton";
    this.getButton().style.color = this.getAttribute("color") ? this.getAttribute("color") : "#5f6368";
    this.getButton().style.backgroundColor = background;
    if (this.hasAttribute(CONSTANT.TITLE)) {
      this.getButton().title = this.getAttribute(CONSTANT.TITLE);
    }

    if (this.getAttribute(CONSTANT.DISABLED)) {
      this.getButton().setAttribute(CONSTANT.DISABLED, true);
    }

    if (
      this.getAttribute(CONSTANT.VISIBLE) != undefined &&
      "false" == this.getAttribute(CONSTANT.VISIBLE)
    ) {
      this.style.width = "0px";
      this.style.display = "none";
    }
    let header = this.getElement("aonHeaderWeb");
    if (!this.getAttribute("noHover")) {
      this.getButton().addEventListener("mouseover", () => {
        this.getButton().style.backgroundColor = "#f1f1f1";
        this.getButton().style.color = "black";
      });
      
      this.getButton().addEventListener("mouseleave", () => {
        this.getButton().style.backgroundColor = background;
        if((!header.style.backgroundColor)&&(this.getButton().id=="aonHeaderHelpButtonIconButton"||this.getButton().id=="aonHeaderConfigButtonIconButton"||this.getButton().id=="aonHeaderNotificationButtonIconButton"||this.getButton().id=="aonHeaderUserButtonIconButton"))
          this.getButton().style.color = "#5f6368";
        else if(header.style.backgroundColor&&(this.getButton().id=="aonHeaderHelpButtonIconButton"||this.getButton().id=="aonHeaderConfigButtonIconButton"||this.getButton().id=="aonHeaderNotificationButtonIconButton"||this.getButton().id=="aonHeaderUserButtonIconButton"))
          this.getButton().style.color = "white";
        else 
          this.getButton().style.color = "#5f6368";
      });

      this.addEventListener("click", () => {
        this.getButton().style.backgroundColor = "#ddd";
      });
    }

    if (this.hasAttribute("icon")) {
      this.getIcon().className = this.getAttribute("outlined") ? "material-icons-outlined"   : "material-icons";
      this.getIcon().innerHTML = this.getAttribute("icon");
      this.getButton().appendChild(this.getIcon());
    } else if (this.hasAttribute("image")) {
      let image = this.createElement(TAG.IMG);
      image.id = this.IMAGE;
      image.style.width = "24px";
      image.style.height = "24px";
      image.src = this.getAttribute("image");
      this.getButton().appendChild(image);
    }

    if (this.hasAttribute("aonIcon")) {
      this.getButton().innerHTML = "";
      let aonIcon = new AonIcon();
      aonIcon.id = this.AON_ICON;
      aonIcon.icon = this.aonIcon;
      this.getButton().appendChild(aonIcon);
      
      if(this.hasAttribute("color")){
        aonIcon.color = this.getAttribute("color")
      }
    }

    this.appendChild(this.getButton());
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

  getIcon(){
    this.ICON_ELEMENT = this.ICON_ELEMENT || this.createElement(TAG.I);
    this.ICON_ELEMENT.id  = this.ICON;
    return this.ICON_ELEMENT;
  }
  
  getButton(){
    this.BUTTON_ELEMENT = this.BUTTON_ELEMENT || this.createElement(TAG.BUTTON);
    this.BUTTON_ELEMENT.id  = this.BUTTON;
    return this.BUTTON_ELEMENT;
  }

}
if(!window.customElements.get('aon-icon-button')){
  window.customElements.define("aon-icon-button", AonIconButton);
}