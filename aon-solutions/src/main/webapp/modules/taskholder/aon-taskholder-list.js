import { AonElement } from "../../components/AonElement.js";
import { AonTable } from "../../components/aon-table.js";
import { getLocation, getTastHolders, getTastHoldersList, getUserList, saveTastHolder } from "../../services/service.js";
// import { SIGNIN_VIEWS } from "./TaskHolderEnums.js";
import { CONSTANT, MSG } from "../../environments/environments.js";
import { TaskHolderEnums } from "./TaskHolderEnums.js";
import { CreateComponent } from "../../components/CreateComponent.js";


export class AonTaskHolderList extends AonElement {
  TABLE;
  static get observedAttributes() {
    return [CONSTANT.FILTER];
  }

  get filter() {
    return JSON.parse(this.getAttribute(CONSTANT.FILTER));
  }

  set filter(filter) {
    this.setAttribute(CONSTANT.FILTER, JSON.stringify(filter));
  }

  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
  }

  attributeChangedCallback(name, oldValue, newValue) {
    if (CONSTANT.FILTER === name) this.getTable();
  }

  constructor() {
    super();
    this.id = this.id || TaskHolderEnums.TASK_HOLDER_VIEWS.AON_TASK_HOLDER_LIST;
    this.applicationEl = this.getApplication();
    this.applicationParentEl = this.getApplicationParent();
  }

  connectedCallback() {
    this.build();
  }

  disconnectedCallback() {}

  paintView() {
    this.TABLE = new AonTable();
    this.TABLE.id = this.id + "Table";
    this.appendChild(this.TABLE);
  }

  build() {
    this.paintView();
    this.getTable();
    this.buildToolbar();
  }

  buildToolbar() {
    this.applicationEl.removeToolbarOptions();

    const {TaskHolderSidenav} = TaskHolderEnums;

    this.applicationEl.addToolbarOption2(TaskHolderSidenav.ADD, () => this.add());
  }

  async getTable() {
    this.applicationEl.startLoader();
    
    await this.getTableDesk();
    
    this.applicationEl.stopLoader();
  }

  async getTableDesk() {
    const aonTable = this.TABLE;
    if (aonTable) {
      aonTable.removeColumns();
      aonTable.addColumn(MSG.NAME, "string", "name", "30%");
      aonTable.addColumn("Alias", "string", "alias", "30%");
      aonTable.addColumn(MSG.DOCUMENT, "string", "document", "30%");
      try {
        const resp = await this.getData();
        aonTable.removeRows();
        resp.map((res) => {
          aonTable.addRow(res, () => this.edit(res));
        });
      } catch (e) {
        console.log(e);
      }
    }
  }

  async getData() {
    this.applicationEl.startLoader();
    let data = [];
    try {
      data = await getTastHoldersList({active:true});
    } catch (e) {
      console.log(e);
    }
    this.applicationEl.stopLoader();
    return data;
  }

  add() {
    const application = this.getApplication();
    const dialog = this.applicationEl.getDialog();
    dialog.clear();
    dialog.width = "300px";

    dialog.setTitle("Registrar operario");

    const div = document.createElement("div");
    dialog.setContent(div);

    const aonSelect = CreateComponent.createAonSelect({
      attributes:{
          name:"user",
          id:"user",
          title:MSG.USERS,
          autocomplete: CONSTANT.OFF
      }
    }, div);

    
    aonSelect.loading(true);
    getUserList({task_holder_empty:true, filter:"all"})
    .then(users =>{
      const options = users.map(user =>({...user, value: user.id, name: user.name+" "+user.surname}));
      aonSelect.setOptions(options)
    }).finally(() =>{
      aonSelect.loading(false);
    });

    dialog.addSendAction(() =>{
      const value = aonSelect.value;

        if(value){
          application.startLoading();
          saveTastHolder({user:value, active:true})
          .then(th => {
            this.showMessage();
            this.edit({id:th.id});
          })
          .catch(err => this.showError(err))
          .finally(() =>{
            dialog.close();
            application.stopLoading();
          })
        }
      }, 
      MSG.SAVE
    );

    dialog.open();
  }

  edit(data){
    this.applicationParentEl.showView(TaskHolderEnums.TASK_HOLDER_VIEWS.AON_TASK_HOLDER, data);
  }
}
window.customElements.define("aon-taskholder-list", AonTaskHolderList);
