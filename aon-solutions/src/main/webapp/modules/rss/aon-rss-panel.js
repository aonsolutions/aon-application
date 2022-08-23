import { AonElement } from "../../components/AonElement.js";
import { getDomainUserRoles } from "../../services/companyService.js";
import { DomainUserRoles } from "../../models/DomainUserRoles.js";
import { MSG, MATERIAL_ICONS } from "../../environments/environments.js";
import { RssEnums } from "./RssEnums.js";
import { AonApplication } from "../../components/aon-application.js";
import { AonRssList } from "./rss/aon-rss-list.js";
import { AonRssAdd } from "./rss/aon-rss-add.js";


export class AonRssPanel extends AonElement {
  AON_RSS;
  MODELS = [];
  BANKS=[];
  dur;
  _filter;
  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    getDomainUserRoles({ reload: true })
    .then((r) => {
      this.dur = new DomainUserRoles(r);
      this.build();
    });
  }

  initialize() {
    this.AON_RSS = RssEnums.RSS_VIEWS.AON_RSS;
    this._filter = {
      year: undefined,
      period: undefined,
      model: undefined
    }
  }

  getDur() {
    return this.dur;
  }

  build() {
    this.paintView();
    this.buildToolbar();
  }

  paintView() {
    this.createApplication(this.AON_TAX, "RSS", new AonApplication());
    this.applicationEl = this.getApplication();
  }

  buildToolbar() {
  
  }

  showView(view, data, filter = undefined) {
    const {RSS_VIEWS} = RssEnums;
    return new Promise(async (resolve) => {
      let aonView = undefined;
      switch (view) {
        case RSS_VIEWS.AON_RSS_PANEL:
          aonView = new AonRssPanel();
        break;
        case RSS_VIEWS.AON_RSS_ADD:
          aonView = new AonRssAdd();
        break;
        case RSS_VIEWS.AON_RSS_LIST:
          aonView = new AonRssList();
        break;
      }
      
      if (aonView) {      
        aonView.id = view;
        if (filter) aonView.filter = filter;
        if(data) aonView.data = data;
        this.applicationEl.setContent(aonView);
      }
      resolve(aonView);
    });
  }
}
window.customElements.define("aon-rss-panel", AonRssPanel);
