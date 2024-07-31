import { AonElement } from "../../components/AonElement.js";
import {
  getSepaDocuments,
  getDocuments,
  downloadDocuments,
  sendDocumentMail,
  updateFiles,
  deleteFile,
  getDomainUserRoles,
  downloadSepaDocument,
  downloadDocument,
} from "../../services/service.js";
import { DomainUserRoles } from "../../models/DomainUserRoles.js";

import "../../components/aon-table.js";

import {
  CONSTANT,
  MATERIAL_ICONS,
  MSG,
} from "../../environments/environments.js";
import * as ACTION from "../actions.js";
import * as LS from "../../services/localStorageService.js";
import { DOCUMENTAL } from "../../services/app.js";
import { AonTable } from "../../components/aon-table.js";
import { formatNumber } from "../../services/utils.js";
import { AonSepa } from "./aon-sepa.js";

export class AonSepaList extends AonElement {
  more;
  _roles;

  TABLE;

  static get observedAttributes() {
    return [];
  }

  get filter() {
    return this.getAttribute(CONSTANT.FILTER);
  }

  set filter(filter) {
    this.setAttribute(CONSTANT.FILTER, filter);
  }

  attributeChangedCallback(name, oldValue, newValue) {
    this.initialize();
  }

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    getDomainUserRoles({}).then((r) => {
      this._roles = new DomainUserRoles(r);
      this.build();
    });
  }

  initialize() {
    this.more = true;
    this.TABLE = "aonDocumentalSepaTable";
  }

  build() {
    let aonDocumentalSepaTable = new AonTable();
    aonDocumentalSepaTable.id = this.TABLE;
    aonDocumentalSepaTable.setApp(DOCUMENTAL);
    this.appendChild(aonDocumentalSepaTable);

    aonDocumentalSepaTable.addColumn(MSG.DATE, "date", "issueDate", "20%");
    aonDocumentalSepaTable.addColumn(MSG.NAME, "string", "description", "40%");
    aonDocumentalSepaTable.addColumn(MSG.AMOUNT, "number", "amount", "15%");
    aonDocumentalSepaTable.addColumn("Nóminas", "string", "details", "15%");
    aonDocumentalSepaTable.addColumn("", "icons", "icons", "10%");

    this.init();

    aonDocumentalSepaTable.addEventListener("more", () => {
      if (this.more) this.loadMore();
    });
  }

  async loadMore() {
    let aonDocumentalSepaTable = this.getElement(this.TABLE);
    let filter = this.getFilter();

    if (aonDocumentalSepaTable && filter.page) {
      filter.page = filter.page + 1;
      this.setFilter(filter);

      let sepaDocuments = await getSepaDocuments(this.getFilter());

      let sepaDocumentsFormat = sepaDocuments.map((sepaDoc) => {
        return {
          ...sepaDoc,
          amount: this.getAmount(sepaDoc),
          details: !sepaDoc.fbatch_details ? 0 : sepaDoc.fbatch_details.length,
          icons: this.buildIcons(sepaDoc),
        };
      });

      if (sepaDocumentsFormat.length == 0) this.more = false;
      sepaDocumentsFormat.forEach((doc, i) => {
        aonDocumentalSepaTable.addRow(
          doc,
          () => this.getApplication().setContent(new AonSepa(doc))
        );
      });
    }
  }

  async init() {
    this.more = true;
    let aonDocumentalSepaTable = this.getElement(this.TABLE);
    if (aonDocumentalSepaTable) {
      let sepaDocuments = await getSepaDocuments(this.getFilter());

      let sepaDocumentsFormat = sepaDocuments.map((sepaDoc) => {
        return {
          ...sepaDoc,
          amount: this.getAmount(sepaDoc),
          details: !sepaDoc.fbatch_details ? 0 : sepaDoc.fbatch_details.length,
          icons: this.buildIcons(sepaDoc),
        };
      });

      aonDocumentalSepaTable.removeRows();
      sepaDocumentsFormat.forEach((doc, i) => {
        aonDocumentalSepaTable.addRow(
          doc,
          () => this.getApplication().setContent(new AonSepa(doc))
        );
      });
    }
  }

  getAmount(sepaDoc) {
    if (!sepaDoc.fbatch_details || sepaDoc.fbatch_details.length === 0) {
      return formatNumber(0, 2, "EUR");
    }

    let total = sepaDoc.fbatch_details.reduce(
      (t, detail) => t + detail.amount,
      0
    );
    return formatNumber(total, 2, "EUR");
  }

  buildIcons(sepaDoc) {
	if(!sepaDoc.rattach) return;

    return [
    //   {
    //     icon: MATERIAL_ICONS.VISIBILITY,
    //     color: "grey",
    //     title: "Ver nóminas",
    //     fn: () => this.getApplication().setContent(new AonSepa(doc)),
    //   },
      {
        icon: MATERIAL_ICONS.FILE_DOWNLOAD,
        color: "grey",
        title: "Descargar",
        fn: () => this.downloadSepaFile(sepaDoc.rattach)
      },
    ];
  }

  downloadSepaFile(rattach) {
    let data = {
      domainId: LS.getDomainId(),
      domainName: LS.getDomainName(),
      domainLogin: LS.getDomainLogin(),
      rattach: rattach,
      type: "registry",
    };

    let json = btoa(JSON.stringify(data));
    downloadDocument(json);
  }

  getFilter() {
    return this.hasAttribute("filter")
      ? JSON.parse(this.getAttribute("filter"))
      : {
          page: 1,
          per_page: 50,
        };
  }

  setFilter(filter) {
    return this.setAttribute("filter", JSON.stringify(filter));
  }
}
window.customElements.define("aon-sepa-list", AonSepaList);
