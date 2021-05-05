import { AonMobileList } from "../../components/aon-mobile-list.js";
import { AonTable } from "../../components/aon-table.js";
import { AonElement } from "../../components/AonElement.js";
import { getMessenger } from "../../services/messengerService.js";
import { setFullDate, setTime } from "../../services/utils.js";
import { firstLetters } from "../signin/time-control/utils.js";
import { MESSENGER_VIEWS } from "./MessengerEnums.js";
import { SigninSidenav } from  "../signin//signinEnums.js";
import { AonMessengerChat } from "./aon-messeger-chat.js";
import { getDomainUserRoles } from "../../services/companyService.js";
import { DomainUserRoles } from "../../models/DomainUserRoles.js";

export class AonMessengerList extends AonElement {
  TABLE_ID;

  static get observedAttributes() {
    return [""];
  }

  constructor() {
    super();
    this.id = this.id || MESSENGER_VIEWS.AON_MESSENGER_LIST;
    this.TOOLBAR = this.id + "Toolbar";
  }

  connectedCallback() {
    this.initialize();
    getDomainUserRoles({}).then(r => {
      this.dur = new DomainUserRoles(r);
      this.build();
  });
  }

  disconnectedCallback() {
    if (this.getApplication()){
      this.getApplication().removeFloatOption();
      this.getApplication().removeToolbarOptions();
    } 
  }

  initialize() {
    this.id = this.id || MESSENGER_VIEWS.AON_MESSENGER_LIST;
    this.TOOLBAR = this.id + "Toolbar";
    this.TABLE_ID = this.id + "Table";
    this.applicationEl = this.getApplication();
    this.applicationParentEl = this.getApplicationParent();
  }

  async build() {
    this.paintView();
    if(this.isMobile()) this.buildToolbarMobile();
    else this.buildToolbar();
    await this.getTable();
  }

  paintView() {
    let innerHTML = ``;
    let aonTableHtml = "";
    if (this.isMobile()) {
      const aonTable = new AonMobileList();
      aonTable.id = this.TABLE_ID;
      aonTableHtml = aonTable.outerHTML;
    } else {
      const aonTable = new AonTable();
      aonTable.id = this.TABLE_ID;
      aonTableHtml = aonTable.outerHTML;
    }
    this.innerHTML =  `${innerHTML} ${aonTableHtml}`;
  }


  buildToolbar() {
    this.applicationEl.removeToolbarOptions();
    if(this.isBeta())
      this.applicationEl.addToolbarOption2(SigninSidenav.ADD, () => {
      this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
    } );
    this.applicationEl.addToolbarOption2(SigninSidenav.FILTER, () => this.applicationEl.development());
  }

  buildToolbarMobile(){
    this.applicationEl.addFloatOption(SigninSidenav.ADD, () => {
      const chat = new AonMessengerChat();
      this.applicationEl.setContent(chat);
    });
    this.applicationEl.addToolbarOption2(SigninSidenav.FILTER, () => this.applicationEl.development());
  }



  async getTable(idDivAppend = undefined) {
    this.TABLE_ID = this.id + "Table";
    if (idDivAppend) {
      let aonTable = undefined;
      if (this.isMobile()) aonTable = new AonMobileList();
      else aonTable = new AonTable();
      aonTable.id = this.TABLE_ID;
      this.getElement(idDivAppend).appendChild(aonTable);
    }
    if (this.isMobile()) await this.getTableMobile();
    else await this.getTableDesk();
  }

  async getTableDesk() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      aonTable.removeColumns();
      aonTable.addColumn("Titulo", "string", "title", "30%");
      aonTable.addColumn("Fecha", "string", "dateParse", "20%");
      try {
        const resp = await this.getData();
        aonTable.removeRows();
        resp.map((res) => {
          const dateParse = firstLetters(setFullDate(res.date)) + " " + setTime(res.date);
          aonTable.addRow({...res, dateParse}, (el) => {
            this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT,{id: res.id});
          });
        });
      } catch (e) {
        console.log(e);
      }
    }
  }

  async getTableMobile() {
    const aonTable = this.getElement(this.TABLE_ID);
    if (aonTable) {
      try {
        const resp = await this.getData();
        aonTable.removeAllLi();
        resp.map((res, idx) => {
          const dateParse = firstLetters(setFullDate(res.date)) + " " + setTime(res.date);
          const options = {
            icon: idx%2==0 ? "unarchive" : "archive",
            title: res.title,
            subtitle: dateParse,
          };
          aonTable.addLi(options, idx, (el) => {
            this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT,{id: res.id});
          });
        });
      } catch (e) {
        console.log(e);
      }
    }
  }

  async getData() {
    let data = [];
    try {
      const datos = await getMessenger();
      if (datos) data = datos;
    } catch (error) {
      console.log(error);
    }
    return data;
  }
}
window.customElements.define("aon-messenger-list", AonMessengerList);
