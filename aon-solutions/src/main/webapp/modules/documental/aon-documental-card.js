import { AonElement } from "../../components/AonElement.js";
import { DomainUserRoles } from "../../models/DomainUserRoles.js";
import { CSS, TAG, EVENT } from "../../environments/environments.js";
import {getDocuments, getDomainUserRoles} from '../../services/service.js';
import { NotificationUtils } from "../notification/utils/NotificationUtils.js";

export class AonDocumentalCard extends AonElement {
  
  dur;
  BANKS = [];
  _filter;

  constructor() {
    super();
    this._filter = {
      type: 'all',
      page:1,
      per_page:30,
      domain: localStorage.getItem('aon_domain_id')
    };
  }

  connectedCallback() {
    this.initialize();
    getDomainUserRoles({ reload: true }).then((r) => {
      this.dur = new DomainUserRoles(r);
      this.build();
    });
  }

  initialize() {
    this.id = "aonDocumentalCard";
  }

  getDur() {
    return this.dur;
  }

  build() {
    this.paintView();
    this.buildToolbar();
  }

  paintView() {
    this.style.display = "flex";
    this.style.flexDirection = "column";
    this.style.justifyContent = "space-between";
    this.style.height = "100%";

    let cardContent = this.createElement(TAG.DIV);
    cardContent.className = CSS.AON_FLEX_COLUMN;
    cardContent.id = "documentalCardTable";
    this.appendChild(cardContent);

    let totalDiv = this.createElement(TAG.DIV);
    totalDiv.id = "documentalTotalDiv";
    this.appendChild(totalDiv);
  }

  buildToolbar() {
    getDocuments(this._filter).then(documents => {
      this.getTable(documents);
    });
  }

  getTable(documents) {
    let content = this.getElement("documentalCardTable");
    this.removeAllChildNodes(content);

    let maxLength = documents.length > 6 ? 6 : documents.length;

    for (let index = 0; index < maxLength; index++) {
      const document = documents[index];
      
      let row = this.createElement(TAG.DIV);
      row.className = CSS.AON_DOCUMENTAL_CARD_ROW;
      row.addEventListener(EVENT.CLICK, () => {
        this.goDocumentalDocument(document);
      });

      let leftContent = this.createElement(TAG.DIV);
      leftContent.className = CSS.AON_FLEX;
      leftContent.style.alignContent = "center";
      leftContent.style.flexDirection = "column";

      let description = this.createElement(TAG.SPAN);
      description.className = CSS.AON_ELLIPSIS;
      description.style.fontSize = "1rem";
      description.style.color = "var(--aonDocumental)";
      description.style.fontWeight = "500";
      description.title = document.title;
      description.innerHTML = document.title;
      leftContent.appendChild(description);

      let rightContent = this.createElement(TAG.DIV);
      rightContent.className = CSS.AON_FLEX;
      rightContent.style.alignItems = "center";
      rightContent.style.gap = "1rem";

      let date = this.createElement(TAG.SPAN);
      date.style.color = "rgb(120, 120, 133)";
      date.style.fontSize = ".8rem";
      date.innerHTML = document.date;
      rightContent.appendChild(date);

      row.appendChild(leftContent);
      row.appendChild(rightContent);

      content.appendChild(row);
    }
  }

  removeAllChildNodes(parent) {
    while (parent.firstChild) {
        parent.removeChild(parent.firstChild);
    }
  }

  async goDocumentalDocument(document) {
    let data = {source: "DOCUMENTAL", source_id: document.id, domain: document.domain};
    const aonComponent = NotificationUtils.getNotificationComponent(data);
    if(aonComponent){
      this.rootPanel(aonComponent);
    }
  }

}
window.customElements.define("aon-documental-card", AonDocumentalCard);
