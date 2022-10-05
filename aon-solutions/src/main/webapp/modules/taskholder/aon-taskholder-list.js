import { AonElement } from "../../components/AonElement.js";
import { AonTable } from "../../components/aon-table.js";
import { getTastHoldersList, getUserList, saveTastHolder } from "../../services/service.js";
import { CONSTANT, MSG, EVENT } from "../../environments/environments.js";
import { TaskHolderEnums } from "./TaskHolderEnums.js";
import { CreateComponent } from "../../components/CreateComponent.js";
import { TaskHolder } from "../../models/project/TaskHolder.js";


export class AonTaskHolderList extends AonElement {
  TABLE;
  filter;
  static get observedAttributes() {
    return [];
  }

  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
  }

  attributeChangedCallback(name, oldValue, newValue) {}

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  disconnectedCallback() {}

  paintView() {
    this.TABLE = new AonTable();
    this.TABLE.id = this.id + "Table";
    this.appendChild(this.TABLE);
  }
  
  initialize(){
    this.id = this.id || TaskHolderEnums.TASK_HOLDER_VIEWS.AON_TASK_HOLDER_LIST;
    this.applicationEl = this.getApplication();
    this.applicationParentEl = this.getApplicationParent();
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
    this.buildSearch();
  }

  buildSearch(){
    let timeOut = null;

    const btnSearch = this.applicationEl.addSearchOption();

    btnSearch.addEventListener(EVENT.SEARCH, ({detail}) => {
        clearTimeout(timeOut);
        timeOut = setTimeout(() => {
          this.setFilter({active:true, search:detail});
        }, 300);
    });
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
      aonTable.addColumn(MSG.ALIAS, "string", "alias", "30%");
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
      data = await getTastHoldersList(this.getFilter());
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
      const detail = aonSelect.getDetail();
        if(detail.id){
          application.startLoading();
          
          let taskHolder = new TaskHolder();
          taskHolder.setUser(detail.id);
          taskHolder.setDocument(detail.document);
          taskHolder.setName(detail.name);
          
          saveTastHolder(taskHolder)
          .then(th => {
            this.showMessage();
            this.edit(th);
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

	getFilter() {
		return this.filter || {active:true};
	}

	setFilter(filter) {
		this.filter = filter;
		this.getTable();
	}

  edit(data){
    this.applicationParentEl.showView(TaskHolderEnums.TASK_HOLDER_VIEWS.AON_TASK_HOLDER, data);
  }
}
window.customElements.define("aon-taskholder-list", AonTaskHolderList);
