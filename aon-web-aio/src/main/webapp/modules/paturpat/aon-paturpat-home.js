import { AonElement } from "../../components/AonElement";
import { CONSTANT, TAG } from "../../environments/environments";
import { AonPaturpatApps } from "./aon-paturpat-apps";

export class AonPaturpatHome extends AonElement {

  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
  }

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize() {
    this.id = this.id || 'aonPaturpatHome';
  }

  build() {
    let div = this.createDiv(this.id);
    div.style.display = 'flex';
    div.style.justifyContent = 'center';
    div.style.alignItems = 'center';
    this.appendChild(div);

    let img = this.createElement(TAG.IMG);
		img.src = '../assets/paturpat.png';
		img.style.maxWidth = '360px';
		img.style.maxHeight = '60px';
    img.style.marginTop = '60px';
    div.appendChild(img);

    let apps = new AonPaturpatApps();
    this.appendChild(apps);

  }
}
if (!window.customElements.get(TAG.AON_PATURPAT_HOME)) {
    window.customElements.define(TAG.AON_PATURPAT_HOME, AonPaturpatHome);
}