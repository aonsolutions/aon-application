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

    if(documents && documents.length === 0){
      let emptyMessage = this.createElement(TAG.DIV);
      emptyMessage.innerHTML = "No existen documentos";
      emptyMessage.style.fontWeight = "bold";

      content.style.height = "100%";
      content.appendChild(emptyMessage);

      let documentalCard = this.getElement("documentalCard");
      documentalCard.style.display = "none";
    } else {
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
        description.className = CSS.AON_DOCUMENTAL_ELLIPSIS;
        description.style.fontSize = ".9rem";
        description.style.color = "var(--aonDocumental)";
        description.style.fontWeight = "500";
        description.title = document.title;
        description.innerHTML = document.title;
        leftContent.appendChild(description);

        let rightContent = this.createElement(TAG.DIV);
        rightContent.className = CSS.AON_FLEX_COLUMN;
        rightContent.style.alignItems = "center";

        let date = this.createElement(TAG.SPAN);
        date.classList.add("aonDocumentalCardDate");
        date.innerHTML = document.date;
        rightContent.appendChild(date);

        let describeDate = this.createElement(TAG.SPAN);
        describeDate.classList.add("aonDocumentalCardDescribeDate");
        describeDate.innerHTML = this.describeDate(this.changeDateFormat(document.date));
        rightContent.appendChild(describeDate);

        row.appendChild(leftContent);
        row.appendChild(rightContent);

        content.appendChild(row);
      }
    }
  }

  describeDate(date) {
    if(!date) {
      return "Formating Err"
    }
    
    var today = new Date();
    today.setHours(0,0,0,0);
    var yesterday = new Date(today);
    yesterday.setDate(today.getDate() - 1);
    var week = new Date(today);
    week.setDate(today.getDate() - 7);
    var month = new Date(today);
    month.setDate(today.getDate() - 30);
    var quarter = new Date(today);
    quarter.setDate(today.getDate() - 90);
    var halfYear = new Date(today);
    halfYear.setDate(today.getDate() - 180);
    var year = new Date(today);
    year.setDate(today.getDate() - 365);

    if (date >= today) {
        return 'Hoy';
    } else if (date >= yesterday) {
        return 'Ayer';
    } else if (date >= week) {
        return 'Última semana';
    } else if (date >= month) {
        return 'Último mes';
    } else if (date >= quarter) {
        return 'Último trimestre';
    } else if (date >= halfYear) {
        return 'Último semestre';
    } else if (date >= year) {
        return 'Último año';
    } else {
        return 'Más de un año';
    }
  }

  changeDateFormat(dateString) {
    if(dateString && dateString.includes("/")){
      const [day, month, year] = dateString.split("/");
      return new Date(year, month - 1, day);
    } else {
      return undefined;
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
