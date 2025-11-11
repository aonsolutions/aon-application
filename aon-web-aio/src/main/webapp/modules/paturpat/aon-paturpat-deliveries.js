import { CONSTANT, TAG } from "../../environments/environments";
import { AonPaturpatApplication } from "./aon-paturpat-application.js";
import { AonMobileDeliveryList } from "../delivery/aon-mobile-delivery-list.js";

export class AonPaturpatDeliveries extends AonPaturpatApplication {

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
    this.buildDeliveries();
  }

  initialize() {
    this.id = this.id || 'aonPaturpatDeliveries';
    this.title = 'Albaranes de Venta';
  }

  buildDeliveries() {
      this.buildSearchBox(this.id + 'SearchDelivery', 'Buscar Albarán de Venta...');
  
      let div = this.createDiv(this.id + 'DeliveryListDiv');
      this.getContent().appendChild(div);

      let aonMobileDeliveryList = new AonMobileDeliveryList();
      aonMobileDeliveryList.id = this.id + 'DeliveryList';
      aonMobileDeliveryList.setContent = (content) => {
          content.addFloatOption = (action, fn) => {
              return this.addFloatOption(action, fn);
          };

          content.removeFloatOption = () => {
              this.removeFloatOption();
          };

          content.back = () => {
              this.getContent().innerHTML = '';
              this.buildDeliveries();
          }

          this.setContent(content);
      }

      aonMobileDeliveryList.getSearchButton = () => {
        return this.getElement(this.id + 'SearchDelivery');
      }

      div.appendChild(aonMobileDeliveryList);
  }
}
if (!window.customElements.get(TAG.AON_PATURPAT_DELIVERIES)) {
    window.customElements.define(TAG.AON_PATURPAT_DELIVERIES, AonPaturpatDeliveries);
}