import { AonElement } from "./AonElement.js";

import {CONSTANT, EVENT, TAG, MATERIAL_ICONS, MSG, CSS} from "../environments/environments.js";
import {getBooking, getDomains, updateDomainBooking} from "../services/domainsService.js";

import "../css/aon-domain-customer.css";
import { AonButton } from "./aon-button.js";

export class AonItemUpdate extends AonElement {
  onFinish = () => {};

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
    this.id = this.id || "aonItemUpdate";
  }

  build() {
    this.clear();

    let updateProductConfirmationContainer = document.createElement("div");
    updateProductConfirmationContainer.style.display = "flex";
    updateProductConfirmationContainer.style.flexDirection = "column";
    updateProductConfirmationContainer.style.justifyContent = "center";
    updateProductConfirmationContainer.style.alignItems = "center";
    updateProductConfirmationContainer.style.gap = "10px";
    updateProductConfirmationContainer.style.margin = "2em 0";

    let updateProductMessage = document.createElement("span");
    updateProductMessage.innerText = "¿Desea actualizar los productos contratados?";
    updateProductConfirmationContainer.appendChild(updateProductMessage);

    let aonButtonAccept = new AonButton();
    aonButtonAccept.title = MSG.ACCEPT;

    aonButtonAccept.addEventListener("click", async () => {
      aonButtonAccept.remove();
      updateProductMessage.innerText = "Cargando...";
      let linkedDomains = await getDomains({ linked: true });
      if (linkedDomains) {
        let domainsLength = linkedDomains.length;
        let errors = [];
        let counter = 1;
        for (const domainCompany of linkedDomains) {
          updateProductMessage.innerText = `Actualizando (${counter++}/${domainsLength})`;
          let domain = domainCompany.domain;
          let domainName = domain ? domain.name : null;
          let domainId = domain ? domain.id : null;
          console.log(domain);
          if (domainName && domainId) {
            let booking = await getBooking({
              domainName: domainName,
              domainId: domainId,
            });

            let error = await updateDomainBooking({
              domain: domainCompany,
              booking: booking,
            });
            if (error && error != []) {
              errors.push(error);
            }
          }
        }
        console.log(errors);

        if (errors && errors != []) {
          updateProductMessage.innerText = "Se produjeron varios errores, ¿desea descargar el registro?";
          let aonButtonDownload = new AonButton();
          aonButtonDownload.title = MSG.DOWNLOAD;
          aonButtonDownload.addEventListener("click", async () => {
            this.generateLogFile(errors);
            this.onFinish();
          });
          updateProductConfirmationContainer.appendChild(aonButtonDownload);
        } else {
          updateProductMessage.innerText = "El proceso concluyó sin errores";
        }
      }
    });

    updateProductConfirmationContainer.appendChild(aonButtonAccept);
    this.appendChild(updateProductConfirmationContainer);
  }

  generateLogFile(logs) {
    let text = this.createLogText(logs);
    let file = new Blob([text], { type: "text/plain" });

    let a = document.createElement("a");
    let url = URL.createObjectURL(file);
    a.href = url;
    a.download = "registro_errores.txt";
    a.style.display = "none";
    this.appendChild(a);
    a.click();
    setTimeout(() => {
      this.removeChild(a);
      window.URL.revokeObjectURL(url);
    }, 0);
  }

  createLogText(logs) {
    let logTxt = "";
    if (logs) {
      for (let log of logs) {
        let domainCompany = log.domain;
        let domain = domainCompany ? domainCompany.domain : null;
        let schema = domainCompany ? domainCompany.schema : "";

        let domainName = domain ? domain.name : "";
        let domainId = domain ? domain.id : "";

        logTxt += `DOMINIO: nombre: ${domainName}; id: ${domainId}; schema: ${schema}\n`;
        let errors = log.errors ? log.errors : [];

        let index = 0;
        for (let error of errors) {
          let barcode = error.barcode ? error.barcode : "";
          let domainType = error.domainType ? error.domainType : "";
          let app = error.app ? error.app : "";
          let msg = error.error ? error.error : "";
          logTxt += `\t{\n`;
          if (barcode) {
            logTxt += `\t\tCódigo de barras: ${barcode}\n`;
          }
          if (domainType) {
            logTxt += `\t\tTipo de dominio: ${domainType}\n`;
          }
          if (app) {
            logTxt += `\t\tAplicación: ${app}\n`;
          }
          if (barcode) {
            logTxt += `\t\tError: ${msg}\n`;
          }
          logTxt += `\t}${index++ < errors.length - 1 ? "," : ""}\n`;
        }
      }
      return logTxt;
    }
  }
}

if (!window.customElements.get(TAG.AON_ITEM_UPDATE)) {
  window.customElements.define(TAG.AON_ITEM_UPDATE, AonItemUpdate);
}