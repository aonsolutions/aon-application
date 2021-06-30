import { AonMobileList } from "../../components/aon-mobile-list.js";
import { AonTable } from "../../components/aon-table.js";
import { AonElement } from "../../components/AonElement.js";
import { COLORS, CSS, EVENT, MATERIAL_ICONS } from "../../environments/environments.js";
import { DomainUserRoles } from "../../models/DomainUserRoles.js";
import { getDomainUserRoles } from "../../services/companyService.js";
import { getTasks } from "../../services/taskService.js";
import { formatDate, setFullDate, setTime } from "../../services/utils.js";
import { SigninSidenav } from "../signin//signinEnums.js";
import { firstLetters } from "../signin/time-control/utils.js";
import { MESSENGER_VIEWS } from "./MessengerEnums.js";

export class AonMessengerList extends AonElement {
  TABLE_ID;
  _list;
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
    this._list = [];
  }

  async build() {
    this.paintView();
    this.buildToolbar();
    await this.getTable();
  }

  paintView() {
    let aonTable = this.isMobile() ?  new AonMobileList() : new AonTable();
    aonTable.id = this.TABLE_ID;
    this.appendChild(aonTable);
  }

  buildToolbar() {
    this.applicationEl.removeToolbarOptions();
    if(this.isBeta()){
      if(this.isMobile()){
        this.applicationEl.addFloatOption(SigninSidenav.ADD, () => this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT));
      } else {
        this.applicationEl.addToolbarOption2(SigninSidenav.ADD, () => this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT));
      }
    }
    this.buildToolbarSearch();
  }

  buildToolbarSearch(){
    let btnSearch = this.applicationEl.addSearchOption();
    
    btnSearch.addEventListener(EVENT.SEARCH, ({detail}) => {
      console.log(detail);
    });
    btnSearch.addEventListener(EVENT.SEARCH_VALUE, ({detail})=>{
      this._list = [];
      console.log(detail);
    });
    
    // btnSearch.buildOptionsFilter(INPUTS);//INPUTS
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
      aonTable.addColumn("", "icon", "icon", "2%");
      aonTable.addColumn("Titulo", "string", "titleDescription", "30%");
      aonTable.addColumn("Fecha", "string", "dateParse", "20%");
      try {
        const resp = await this.getData();
        aonTable.removeRows();
        resp.map((res) => {
          const dateParse = firstLetters(setFullDate(res.date)) + " " + setTime(res.date);
					res.icon = MATERIAL_ICONS.INFO;
					res.icon_title = "status";
					res.icon_color = CSS.variable(COLORS.MATERIAL_BLUE);
          res.icon_class = "material-icons-outlined";
          aonTable.addRow({...res, dateParse}, (el) => {
            this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT,res);
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
            icon: MATERIAL_ICONS.INFO,
            icon_color: CSS.variable(COLORS.MATERIAL_BLUE),
            icon_class: "material-icons-outlined",
            title: res.title,
            subtitle: dateParse,
          };
          aonTable.addLi(options, idx, () => 
            this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, res)
          );
        });
      } catch (e) {
        console.log(e);
      }
    }
  }

  async getData() {
    let data = [];
    try {
      const datos = await getTasks();
      if (datos){
        datos.map(task=>{
          const titleDescription = `<span style="font-size:14px;font-weight: 500;">[${task.title}] ${task.description}</span>`;
          data.push({
            ...task,
            date:task.start_date,
            titleDescription
          });
        })
        
      } 
    } catch (error) {
      this.showError(error);
    }
    return data;
  }
}
window.customElements.define("aon-messenger-list", AonMessengerList);
