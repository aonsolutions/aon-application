import { CONSTANT, TAG, EVENT, CSS, MSG } from "../../environments/environments";
import { AonMobileElaborationList } from "../warehouse/elaboration/aon-mobile-elaboration-list";
import * as ACTION from '../actions.js';
import { AonPaturpatApplication } from "./aon-paturpat-application.js";

export class AonPaturpatElaborations extends AonPaturpatApplication {

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
    this.buildElaborations();
  }

  initialize() {
    this.id = this.id || 'aonPaturpatElaborations';
    this.title = MSG.ELABORATIONS;
  }

  buildElaborations() {
      this.addFloatOption(ACTION.ADD, () => this.addElaboration());
      this.buildSearchBox(this.id + 'SearchElaboration', 'Buscar Elaboración...');
  
      let div = this.createDiv(this.id + 'ElaborationListDiv');
      this.appendChild(div);

      let aonMobileElaborationList = new AonMobileElaborationList();
      aonMobileElaborationList.id = this.id + 'ElaborationList';
      
      aonMobileElaborationList.setContent = (content) => {
        this.setContent(content);
      }

      aonMobileElaborationList.getSearchButton = () => {
        return this.getElement(this.id + 'SearchElaboration');
      }

      div.appendChild(aonMobileElaborationList);
  }

  addElaboration() {
      alert('Añadir Elaboración');
  }
}
if (!window.customElements.get(TAG.AON_PATURPAT_ELABORATIONS)) {
    window.customElements.define(TAG.AON_PATURPAT_ELABORATIONS, AonPaturpatElaborations);
}