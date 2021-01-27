import { AonElement } from "./AonElement.js";
import "./aon-dialog.js";
import { serializeForm } from "../services/utils.js";

export class AonFilter extends AonElement {
  DIALOG;
  DIALOG_EL;
  static get observedAttributes() {
    return [];
  }

  get id() {
    return this.getAttribute("id");
  }

  set id(id) {
    this.setAttribute("id", id);
  }

  get title() {
    return this.getAttribute("title");
  }

  set title(title) {
    this.setAttribute("title", title);
  }

  attributeChangedCallback(name, oldValue, newValue) {}

  constructor() {
    super();
    this.id = this.id || "aonFilter";
    this.DIALOG = this.id + "DialogMenuFilter";
  }

  connectedCallback() {
    this.paintView();
    this.build();
    this.eventListener();
  }

  build() {}
  eventListener(){}
  
  paintView() {
    this.innerHTML = `<aon-dialog id="${this.DIALOG}"> </aon-dialog>`;
  }

  openFilter() {
    let d = this.getElement(this.DIALOG);
    d.open();
  }

  setInputs(inputs) {
    let d = this.getElement(this.DIALOG);
    d.setTitle(this.title);
    if (!this.isMobile()) d.width = "400px";
    let htmlContent = `<form id="${this.id}Form">`;
    inputs.forEach((attribute) => {
      htmlContent = htmlContent + this.getInput(attribute);
    });

    htmlContent = htmlContent + `</form>`;

    d.setContentHTML(htmlContent);
    d.addAcceptAction(() => this.applyFilter());
  }

  dialogClear() {
    this.getElement(this.DIALOG).clear();
  }

  getValues() {
    const form = this.getElement(`${this.id}Form`);
    return serializeForm(form);
  }

  applyFilter() {
    this.dispatchEvent(
      new CustomEvent("applyFilter", {
        detail: this.getValues(),
      })
    );
  }

  getInput(attribute) {
    let { type } = attribute;
    let html = "";
    let newAttributes = this.convertObjToString(attribute, " ");
    switch (type) {
      case "text":
        html = `<aon-input ${newAttributes}></aon-input>`;
        break;
      case "select":
        html = `<aon-select ${newAttributes}></aon-select>`;
        break;
      case "date":
        html = `<aon-date ${newAttributes}></aon-date>`;
        break;
    }

    return html;
  }

  convertObjToString(obj, separator) {
    return Object.keys(obj).map((k) => `${k.trim()}="${obj[k]}"`).join(separator);
  }
}

window.customElements.define("aon-filter", AonFilter);
