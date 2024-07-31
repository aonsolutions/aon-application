import { AonElement } from "../../components/AonElement.js";
import {
  getDomainUserRoles,
  downloadDocument,
  getSepaDocument,
} from "../../services/service.js";
import { DomainUserRoles } from "../../models/DomainUserRoles.js";

import "../../components/aon-table.js";

import {
  CONSTANT,
  MSG,
} from "../../environments/environments.js";
import * as ACTION from "../actions.js";
import * as LS from "../../services/localStorageService.js";
import { DOCUMENTAL } from "../../services/app.js";
import { AonTable } from "../../components/aon-table.js";
import { formatNumber } from "../../services/utils.js";
import { AonToolbar } from "../../components/aon-toolbar.js";
import { ToolbarType } from "../../models/enums.js";
import { AonSepaList } from "./aon-sepa-list.js";

export class AonSepa extends AonElement {
  more;
  _roles;

  TABLE;

  sepaDoc;

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

  constructor(sepaDoc) {
    super();
    this.sepaDoc = sepaDoc;
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
    this.TABLE = "aonDocumentalSepa";
  }

  build() {
    let toolbar = new AonToolbar();
    toolbar.id = "sepaToolbar";
    toolbar.type = ToolbarType.SECONDARY;
    toolbar.title = this.sepaDoc.description;

    this.appendChild(toolbar);
	if(this.sepaDoc.rattach) toolbar.addButton2(ACTION.DOWNLOAD_FILE, () => this.downloadSepaFile());
    toolbar.addButton2(ACTION.BACK, () => this.back());

    let aonDocumentalSepa = new AonTable();
    aonDocumentalSepa.id = this.TABLE;
    aonDocumentalSepa.setApp(DOCUMENTAL);
    this.appendChild(aonDocumentalSepa);

	let aonDocumentalSepaBody = aonDocumentalSepa.getElementsByTagName('tbody')[0];
	aonDocumentalSepaBody.style.height = "calc(100vh - 195px)";

    aonDocumentalSepa.addColumn(MSG.NAME, "string", "name", "70%");
	aonDocumentalSepa.addColumn("", "string", "total", "10%");
    aonDocumentalSepa.addColumn(MSG.AMOUNT, "number", "amount", "20%");

    this.init();
  }

  async init() {
    this.more = true;
    let aonDocumentalSepa = this.getElement(this.TABLE);

    if (aonDocumentalSepa) {
      let sepaDocument = await getSepaDocument({ fbatch: this.sepaDoc.id });

      let sepaDetails = sepaDocument.fbatch_details.map((sepaDetail) => {
        return {
          ...sepaDetail,
          name: !sepaDetail.finance ? "" : sepaDetail.finance.registryName,
          amount: this.getAmount(sepaDetail),
        };
      }).sort((a, b) => a.name.localeCompare(b.name));

      aonDocumentalSepa.removeRows();
      sepaDetails.forEach((sepaDetail, i) => {
        aonDocumentalSepa.addRow(sepaDetail);
      });

      // Total
      let row = aonDocumentalSepa.addRow({
        total: "Total",
        amount: this.getTotal(sepaDocument)
      });
      row.style.fontWeight = "600";
    }
  }

  getAmount(sepaDetail) {
    return formatNumber(
      !sepaDetail.finance ? 0 : sepaDetail.finance.amount,
      2,
      "EUR"
    );
  }

  getTotal(sepaDoc) {
    if (!sepaDoc.fbatch_details || sepaDoc.fbatch_details.length === 0) {
      return formatNumber(0, 2, "EUR");
    }

    let total = sepaDoc.fbatch_details.reduce(
      (t, detail) => t + detail.amount,
      0
    );
    return formatNumber(total, 2, "EUR");
  }

  back() {
	this.getApplication().setContent(new AonSepaList());
  }

  downloadSepaFile() {
    let data = {
      domainId: LS.getDomainId(),
      domainName: LS.getDomainName(),
      domainLogin: LS.getDomainLogin(),
      rattach: this.sepaDoc.rattach,
      type: "registry",
    };

    let json = btoa(JSON.stringify(data));
    downloadDocument(json);
  }
}
window.customElements.define("aon-sepa", AonSepa);
