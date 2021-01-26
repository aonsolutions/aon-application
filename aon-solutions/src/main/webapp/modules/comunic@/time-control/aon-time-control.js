import { AonElement } from "../../../components/AonElement.js";

import "./aon-presence-list.js";

export class AonTimeControl extends AonElement {
  static get observedAttributes() {
    return [];
  }

  get id() {
    return this.getAttribute("id");
  }

  set id(id) {
    this.setAttribute("id", id);
  }

  attributeChangedCallback(name, oldValue, newValue) {}

  constructor() {
    super();
    this.aonComunicaEl = this.getElement("aonComunica");
    this.aonComunicaToolbar = this.getElement("aonComunicaToolbar");
  }

  connectedCallback() {
    this.paintView();
    this.build();
  }

  paintView() {
    this.aonComunicaEl.removeToolbarOptions();
    this.aonComunicaEl.setContentHTML(
      `<aon-presence-list id="aonPresenceList"></aon-presence-list>`
    );
  }

  async build() {}
}
window.customElements.define("aon-time-control", AonTimeControl);
