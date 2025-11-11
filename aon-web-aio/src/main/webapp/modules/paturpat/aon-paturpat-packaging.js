import { CONSTANT, TAG, MSG } from "../../environments/environments";
import { AonPaturpatApplication } from "./aon-paturpat-application.js";
import { AonMobilePackaging } from "../warehouse/packaging/aon-mobile-packaging.js";

export class AonPaturpatPackaging extends AonPaturpatApplication {

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
    this.buildPackaging();
  }

  initialize() {
    this.id = this.id || 'aonPaturpatElaborations';
    this.title = MSG.PACKAGING;
  }

  buildPackaging() {
      let aonMobilePackaging = new AonMobilePackaging();
      aonMobilePackaging.id = this.id + 'Packaging';
      aonMobilePackaging.addFloatOption = (action, fn) => {
        this.addFloatOption(action, fn);
      };
      this.setContent(aonMobilePackaging);
  }
}
if (!window.customElements.get(TAG.AON_PATURPAT_PACKAGING)) {
    window.customElements.define(TAG.AON_PATURPAT_PACKAGING, AonPaturpatPackaging);
}