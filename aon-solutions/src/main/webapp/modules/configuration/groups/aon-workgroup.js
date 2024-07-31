import { AonIconButton } from "../../../components/aon-icon-button.js";
import { AonSearch } from "../../../components/aon-search.js";
import { AonSelect } from "../../../components/aon-select.js";
import { AonElement } from "../../../components/AonElement.js";
import {
  CONSTANT,
  CSS,
  EVENT,
  MATERIAL_ICONS,
  MSG,
  TAG,
} from "../../../environments/environments.js";
import {
  assignTaskHolderWorkgroup,
  removeTaskHolderWorkgroup,
  getTastHoldersList,
  getTastHoldersWithWorkgroupsList,
  getTaskHolderWorkGroups,
} from "../../../services/taskHolderService.js";
import { AonTaskHolderSimpleList } from "../../registry/taskholder/aon-taskholder-simple-list.js";
import { AonGroupList } from "./aon-group-list.js";

export class AonWorkgroup extends AonElement {
  taskHolderAddList;

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
    this.id = this.id || "aonWorkgroup";
    this.USER_LIST = this.id + "UserList";
    this.add = false;
    this.DIV = this.id + CONSTANT.DIV.initCap();
    this.LIST = this.id + CONSTANT.LIST.initCap();
    this.USERS = this.id + CONSTANT.USERS.initCap();
  }

  build() {
    // getTastHoldersWithWorkgroupsList()
    getTaskHolderWorkGroups().then((users) => {
      this.users = users;
    });
    let div = this.createElement(TAG.DIV);
    div.id = this.DIV;
    div.className = CSS.AON_FLEX;
    this.appendChild(div);

    let groupsDiv = this.createElement(TAG.DIV);
    groupsDiv.id = this.LIST;
    groupsDiv.style.width = "100%";
    let list = new AonGroupList();
    div.appendChild(groupsDiv);
    groupsDiv.appendChild(list);

    let usersDiv = this.createElement(TAG.DIV);
    usersDiv.id = this.USERS;
    usersDiv.style.display = "none";
    usersDiv.style.width = "50%";
    usersDiv.style.borderLeft = "1px solid #ddd";
    div.appendChild(usersDiv);

    list.addEventListener(EVENT.SELECT, (event) => {
      groupsDiv.style.width = "50%";
      usersDiv.style.display = "block";
      this.buildUsers(usersDiv, event.detail);
    });
  }

  buildUsers(parent, workgroup) {
    this.clearElement(parent);

    let div = this.createElement(TAG.DIV);
    div.style.borderBottom = "1px solid #ddd";
    div.style.height = "48px";
    parent.appendChild(div);

    let workgroupBack = new AonIconButton();
    workgroupBack.id = "workgroupBack";
    workgroupBack.title = MSG.CLOSE;
    workgroupBack.icon = MATERIAL_ICONS.CLOSE;
    workgroupBack.noHover = true;
    workgroupBack.style.top = "10px";
    workgroupBack.style.position = "relative";
    workgroupBack.style.right = "4px";
    div.appendChild(workgroupBack);

    workgroupBack.addEventListener(EVENT.CLICK, () => {
      this.clearHolderList();
    });

    let span = this.createElement(TAG.SPAN);
    span.innerHTML = workgroup.description;
    span.style.position = "absolute";
    span.style.margin = "20px";
    span.style.fontWeight = "500";
    span.style.color = "rgb(95, 99, 104)";
    div.appendChild(span);

    // let aonSearch = new AonSearch();
    // aonSearch.classList.add(CSS.AON_RIGHT_60);
    // aonSearch.style.top = '6px';
    // div.appendChild(aonSearch);
    // aonSearch.addEventListener(EVENT.SEARCH, (event) => this.loadUserList(workgroup, event.detail).init());

    let user = new AonIconButton();
    user.id = this.USERS + "User";
    user.icon = MATERIAL_ICONS.PERSON;
    user.style.position = "absolute";
    user.style.display = "none";
    user.style.top = "6px";
    user.classList.add(CSS.AON_RIGHT_20);
    div.appendChild(user);

    let addButton = new AonIconButton();
    addButton.id = this.USERS + "AddButton";
    addButton.icon = MATERIAL_ICONS.PERSON_ADD;
    addButton.style.position = "absolute";
    addButton.style.top = "6px";
    addButton.classList.add(CSS.AON_RIGHT_20);
    div.appendChild(addButton);

    this.add = false;
    let userList = this.loadUserList(workgroup);
    parent.appendChild(userList);

    user.addEventListener(EVENT.CLICK, () => {
      addButton.style.display = "block";
      user.style.display = "none";
      this.add = false;
      this.loadUserList(workgroup).init();
    });

    addButton.addEventListener(EVENT.CLICK, () => {
      this.showTaskHolderDialog(workgroup);

      // addButton.style.display = 'none';
      // user.style.display = 'block';
      // this.add = true;
      // this.loadUserList(workgroup).init();
    });

    // let searchInput = this.getElement("aonSearchSearchInput");
    // searchInput .placeholder = "Buscar por nombre";
  }

  showTaskHolderDialog(workgroup) {
    const application = this.getApplication();
    const dialog = application.getDialog();
    dialog.clear();

    if (application.isMobile()) {
      dialog.type = "fullscreen";
    } else {
      dialog.width = "40%";
    }

    dialog.autoclose = false;

    const title = MSG.ASSIGN + " operario";
    dialog.setTitle(title);

    let div = document.createElement("div");
    div.style.display = "flex";
    div.style.flexDirection = "column";
    dialog.setContent(div);

    this.buildFormProject(div, workgroup);

    dialog.addSendAction(async () => {
      if (!this.taskHolderAddList || this.taskHolderAddList.lenght == 0) {
        this.showMessageError("Debe elegir al menos un operario");
      } else {
        this.addTaskHolders(workgroup).then(() => {
          dialog.close();
          getTaskHolderWorkGroups().then((users) => {
            this.users = users;
            this.loadUserList(workgroup).init();
          });
        });
      }
    }, MSG.SAVE);

    dialog.open();
  }

saveData(taskholder, workgroup) {
    return new Promise(async (resolve, reject) => {
      let data = {
        task_holder: taskholder.id,
        workgroup: workgroup.id,
      };
      await assignTaskHolderWorkgroup(data);
      resolve();
    });
  }

  async addTaskHolders(workgroup) {
    for (const taskHolder of this.taskHolderAddList) {
      await this.saveData(taskHolder, workgroup);
    }
  }

  buildFormProject(div, workgroup) {
    const idRandom = Math.floor(Math.random() * 10000000) + 1;

    let ITEM_SELECT = new AonSelect();
    ITEM_SELECT.title = "Operarios";
    ITEM_SELECT.id = "OperariosList";
    ITEM_SELECT.autocomplete = true;
    ITEM_SELECT.default = true;
    ITEM_SELECT.multiple = true;
    div.appendChild(ITEM_SELECT);

    const buildItems = () => {
      ITEM_SELECT.loading(true);

      let workgroupTaskHolder = this.users.filter(
        (f) => f.workgroup.id == workgroup.id
      );

      getTastHoldersList()
        .then((opts) => {
          opts = opts
            .filter((p) => {
              return !workgroupTaskHolder.some(
                (wth) => wth.task_holder.id == p.id
              );
            })
            .map((p) => ({ ...p, value: p.id }));

          opts.sort((a, b) => a.name.localeCompare(b.name));

          ITEM_SELECT.setOptionsBuild(opts);
        })
        .finally(() => {
          ITEM_SELECT.loading(false);
        });
    };

    buildItems();

    ITEM_SELECT.addEventListener(EVENT.SELECT, ({ detail }) => {
      this.taskHolderAddList = ITEM_SELECT.getSelectable();
    });
  }

  isRepeatTaskHolder(th) {
    return this.users.some((user) => user.task_holder.id == th.id);
  }

  clearHolderList() {
    let workgroupList = this.getElement(this.LIST);
    let userList = this.getElement(this.USERS);
    if (workgroupList && userList) {
      workgroupList.style.width = "100%";
      userList.style.display = "none";
      userList.style.width = "50%";
      userList.style.borderLeft = "1px solid #ddd";
    }
  }

  async addUser(taskholder, workgroup) {
    let data = {
      task_holder: taskholder.id,
      workgroup: workgroup.id,
    };
    await assignTaskHolderWorkgroup(data);
  }

  async removeUser(taskholder, workgroup) {
    this.getApplication().confirmDialog(
      MSG.DELETE,
      MSG.DELETE_CONFIRM + " el agente " + taskholder.task_holder.name,
      async () => {
        this.getApplication().startLoading();

        try {
          let data = {
            task_holder: taskholder.task_holder.id,
            workgroup: workgroup.id,
          };
          await removeTaskHolderWorkgroup(data);
          this.showToast({ message: MSG.DELETED_DATA });
          getTaskHolderWorkGroups().then((users) => {
            this.users = users;
            this.loadUserList(workgroup).init();
          });
        } catch (error) {
          console.log(error);
          this.showError(error);
        }

        this.getApplication().stopLoading();
      }
    );
  }

  loadUserList(workgroup, search) {
    search = search || "";
    let userList =
      this.getElement(this.USER_LIST) || new AonTaskHolderSimpleList();
    userList = new AonTaskHolderSimpleList();
    userList.id = this.USER_LIST;
    let filteredUsers = this.users.filter(
      (f) =>
        (this.add &&
          !f.workgroup.id ==
            workgroup.id) /*!f.workgroups.map(r => r.id).includes(workgroup.id)*/ ||
        (!this.add &&
          f.workgroup.id ==
            workgroup.id) /*f.workgroups.map(r => r.id).includes(workgroup.id)*/
      /*&& (
                (f.email && f.email.toUpperCase().includes(search.toUpperCase())) 
                || ((f.name ? f.name : '') + ' ' + (f.surname ? f.surname : '')).toUpperCase().includes(search.toUpperCase())
            )*/
    );
    // .map(user => {
    //     user,

    // });

    let taskoHolderType = {
      ADMIN: 1,
      USER: 2,
    };

    filteredUsers.sort((a, b) => {
      // Primero compara por prioridad de statusText
      if (
        taskoHolderType[a.task_holder_workgroup_type] <
        taskoHolderType[b.task_holder_workgroup_type]
      ) {
        return -1;
      }
      if (
        taskoHolderType[a.task_holder_workgroup_type] >
        taskoHolderType[b.task_holder_workgroup_type]
      ) {
        return 1;
      }
      // Si el statusText y el endDate son los mismos, compara alfabéticamente por sellerName
      return a.task_holder.name.localeCompare(b.task_holder.name);
    });

    userList.users = filteredUsers;

    let icon = this.add ? MATERIAL_ICONS.PERSON_ADD : MATERIAL_ICONS.DELETE;
    let fn = this.add
      ? (user) => this.addUser(user, workgroup)
      : (user) => this.removeUser(user, workgroup);
    userList.option = {
      icon,
      fn,
    };
    return userList;
  }
}
if (!window.customElements.get(TAG.AON_WORKGROUP)) {
  window.customElements.define(TAG.AON_WORKGROUP, AonWorkgroup);
}
