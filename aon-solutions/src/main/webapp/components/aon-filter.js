import { AonElement } from "./AonElement.js";
import { serializeForm } from "../services/utils.js";
import { CONSTANT } from '../environments/environments.js';
import "./aon-dialog.js";

export class AonFilter extends AonElement {
  DIALOG;
  DIALOG_EL;
  static get observedAttributes() {
    return [];
  }

  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
  }

  get title() {
    return this.getAttribute(CONSTANT.TITLE);
  }

  set title(title) {
    this.setAttribute(CONSTANT.TITLE, title);
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
    if(d)d.open();
  }

  setInputs(inputs) {
    this.dialogClear();
    let d = this.getElement(this.DIALOG);
    d.setTitle(this.title);
    if (!this.isMobile()) d.width = "400px";
    let htmlContent = `<form id="${this.id}Form" action="#">`;
    inputs.forEach((attribute) => {
      htmlContent = htmlContent + this.getInput(attribute);
    });

    htmlContent = htmlContent + `</form>`;

    d.setContentHTML(htmlContent);
    d.addAcceptAction(() => this.applyFilter());
  }

  getDialog(){
    return this.getElement(this.DIALOG);
  }

  dialogClear() {
    this.getElement(this.DIALOG).clear();
  }

  getValues() {
    const form = this.getElement(`${this.id}Form`);
    return serializeForm(form);
  }

  getFormEl(){
    return this.getElement(`${this.id}Form`);
  }

  applyFilter() {
    this.dispatchEvent(
      new CustomEvent("applyFilter", {
        detail: this.getValues(),
      })
    );
  }

  getInput(attribute) {
    let { type, id } = attribute;
    let html = "";
    let newAttributes = this.convertObjToString(attribute, " ");
    switch (type) {
      case CONSTANT.TEXT:
        html = `<aon-input ${newAttributes}></aon-input>`;
        break;
      case CONSTANT.SELECT:
        html = `<aon-select ${newAttributes}></aon-select>`;
        break;
      case CONSTANT.DATE:
        html = `<aon-date ${newAttributes}></aon-date>`;
        break;
    }

    return html;
  }

  convertObjToString(obj, separator) {
    return Object.keys(obj).map((k) => `${k.trim()}="${obj[k]}"`).join(separator);
  }
}
if(!window.customElements.get('aon-filter')){
  window.customElements.define("aon-filter", AonFilter);
}
