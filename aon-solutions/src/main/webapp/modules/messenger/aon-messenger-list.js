import { AonMobileList } from "../../components/aon-mobile-list.js";
import { AonTable } from "../../components/aon-table.js";
import { AonElement } from "../../components/AonElement.js";
import { CONSTANT, EVENT, MSG, TAG } from "../../environments/environments.js";
import { getTaskOne, getTasks } from "../../services/taskService.js";
import { sortBy } from "../../services/utils.js";
import { MESSENGER_VIEWS, TASK_SOURCE, TASK_STATUS } from "./MessengerEnums.js";
import { AonMessenger } from "./aon-messenger.js";
import { addTasks, setIndexTask, setTasks } from "./TaskCache.js";
import { TaskUtils } from "./utils/TaskUtils.js";
import { TaskListUtils } from "./utils/TaskListUtils.js";
import { getTaskHolder } from "../../services/taskHolderService.js";
import * as LS from "../../services/localStorageService.js";
import { SigninSidenav } from "../timecontrol/signinEnums.js";

export class AonMessengerList extends AonElement {
  MORE;
  TASK_HOLDER;
  AON_TABLE;
  ROWS;
  static get observedAttributes() {
    return [];
  }

  setFilter(filter) {
    return this.setAttribute(CONSTANT.FILTER, JSON.stringify(filter));
  }

  getFilter() {
    return this.hasAttribute(CONSTANT.FILTER)
      ? JSON.parse(this.getAttribute(CONSTANT.FILTER))
      : { page: 0, perPage: 30, status: TASK_STATUS.PENDING };
  }

  constructor() {
    super();
    this.id = this.id || MESSENGER_VIEWS.AON_MESSENGER_LIST;
    this.TOOLBAR = this.id + "Toolbar";
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  disconnectedCallback() {}

  initialize() {
    this.id = this.id || MESSENGER_VIEWS.AON_MESSENGER_LIST;
    this.TOOLBAR = this.id + "Toolbar";
    this.INDEX = 0;
    this.MORE  = true;
    this.applicationEl = this.getApplication();
    this.applicationParentEl = this.getApplicationParent();
    this.TASK_HOLDER = this.applicationParentEl.TASK_HOLDER;
    this.AON_TABLE = null;
    this.ROWS = [];
  }

  async build() {
    this.paintTable();
    if (this.isMobile()) {
      this.applicationEl.addFloatOption(SigninSidenav.ADD, () =>
        this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, {
          source: TASK_SOURCE.QUERY,
        })
      );
    }
  }

  async paintTable(divNotification) {
    this.AON_TABLE = this.isMobile() ? new AonMobileList() : new AonTable();
    this.AON_TABLE.id = this.id + "Table";
    if (divNotification) {
      await this.isFromNotification(this.AON_TABLE, divNotification);
    } else {
      this.appendChild(this.AON_TABLE);
    }

    if (this.isMobile()) {
      this.AON_TABLE.removeAllLi();
    } else {
      this.AON_TABLE.removeColumns();
      this.AON_TABLE.addColumn("", "html", "lettersHtml", "2%");
      this.AON_TABLE.addColumn(MSG.ISSUE, "html", "newTitle", "73%");
      this.AON_TABLE.addColumn("Asignado", "html", "assigned", "5%");
      this.AON_TABLE.addColumn(MSG.DATE, "html", "dateParse", "20%");
    }
    
    this.AON_TABLE.addEventListener(EVENT.MORE,() =>{
      if(this.MORE){
        this.loadMore(false)
      }
    });

    await this.loadMore(true);
  }

  async isFromNotification(aonTable, divNotification) {
    divNotification.appendChild(aonTable);
    const th = await getTaskHolder({ reload: false }).catch(() => null);
    if (th) {
      this.TASK_HOLDER = th;
      let filter = this.getFilter();
      filter.task_holder = th.id;
      this.setFilter(filter);
    }
  }

  async loadMore(reload) {
  
    this.AON_TABLE.loading(true);
    
    const datos = await this.getData();

    this.AON_TABLE.loading(false);

    if (reload) {
      setTasks(datos);
    } else {
      addTasks(datos);
    }

    if (this.isMobile()) {
      if (reload) this.AON_TABLE.removeAllLi();
      this.getDataMobile(datos);
    } else {
      if (reload) this.AON_TABLE.removeRows();
      this.getDataDesktop(datos);
    }

    if (reload && datos.length <= 0) {
      this.AON_TABLE.empty();
    }
  }

  getDataDesktop(datos) {
    try {
      const isCau = this.isCau();

      const documents = this.getDocuments();
      this.ROWS = [];
      datos.forEach((res, idx) => {
        const rowEl = this.AON_TABLE.addRow(
          {
            ...res,
            dateParse: TaskListUtils.getDateParseNew(res),
            newTitle: this.getTitleDesktop(res, documents.document, documents.documentTh),
            assigned: TaskListUtils.getAssignedHtml(res, documents.domainId),
            lettersHtml: TaskListUtils.getIcon(res),
          },
          () => this.goMessengerChat(res, idx)
        );

        if(rowEl){
          rowEl.style.padding = "14px 0px 7px";
          const firstChild = rowEl.firstChild;
          
          if(firstChild){
            firstChild.style.verticalAlign = "top"; //TODO
          }         
        }
      });

      datos.forEach((t) => {
        let row = this.ROWS.find((x) => x.id === t.id);
        if (row && row.parent) {
          TaskListUtils.addTaskChilds(t, row, documents, isCau);
        }
      });
    } catch (e) {
      console.log(e);
    }
  }

  /**
   * @return boolean}
   */
  getIsMyTask(task){
    const parent = this.applicationParentEl;
    const myTaskHolderId = parent.TASK_HOLDER ? parent.TASK_HOLDER.id : undefined;
    const myWorkgroups   = parent._workgroups;
    return TaskUtils.isMyTask(task, myTaskHolderId, myWorkgroups);
  }

  getDataMobile(datos) {
    try {
      const company = LS.getCompany();
      const document = company ? company.document : undefined;
      const documentTh = this.TASK_HOLDER ? this.TASK_HOLDER.document : undefined;
      datos.map((res, idx) => {
        const li = this.AON_TABLE.addLi(
          {
            title: TaskListUtils.getTitleMobile(res, document, documentTh),
            subtitle: TaskListUtils.getSubtitleMobileOne(res),
            subtitleTwo: TaskListUtils.getSubtitleMobileTwo(res),
            ...TaskListUtils.getIconList(res),
          },
          idx,
          () => this.goMessengerChat(res, idx)
        );
        li.dataset.taskId = res.id;
      });
    } catch (e) {
      console.log(e);
    }
  }

  async getData() {
    let data = [];
    try {
      let filter = this.getFilter();
      filter.page = filter.page + 1;
      this.setFilter(filter);
      let tasks = await getTasks(filter);

      if (tasks.length == 0) {
        this.MORE = false;
      } else {
        data = tasks
          .filter((v, idx) => tasks.findIndex((m) => m.id === v.id) === idx)
          .map((task) => ({
            ...task,
            date: task.start_date,
            startDate: new Date(task.start_date),
            newNumber: TaskUtils.taskNumberParse(task.number),
          }));
      }
      data = sortBy(data, "startDate", "desc");
    } catch (error) {
      console.log("error>>", error);
      this.showError(error);
    }
    return data;
  }

  getTitleDesktop(res, document, documentTh) {
    let div = this.createElement(TAG.DIV);
    div.style.position = "relative";

    this.ROWS.push({
      id: res.id,
      parent: div,
    });

    div.appendChild(TaskListUtils.getTitleHtmlDesktop(res));

    div.appendChild(TaskListUtils.getSubTitleHtml(res, document, documentTh));

    return div;
  }

  /**
   * is cau
   * @returns {boolean}
   */
  isCau() {
    return parseInt(localStorage.getItem("taskCau") || 0);
  }

  async goMessengerChat(res, idx) {
    setIndexTask(idx);
    if (!this.applicationParentEl) {
      let aonMessenger = new AonMessenger();
      aonMessenger.data = res;
      this.rootPanel(aonMessenger);
    } else if (res && res.id) {
      this.applicationEl.startLoading();
      try {
        let params = { id: res.id };

        if(res.domain && res.domain.id &&  res.domain.name){
          params.domainName = res.domain.name;
          params.domainId = res.domain.id
        }

        const data = await getTaskOne(params);
        this.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, data);
      } catch (err) {
        this.showError(err);
      }
      this.applicationEl.stopLoading();
    }
  }

  getDocuments() {
    const company = LS.getCompany();
    const domainId = parseInt(LS.getDomainId());
    const document = company ? company.document : undefined;
    const documentTh = this.TASK_HOLDER ? this.TASK_HOLDER.document : undefined;
    return {
      company,
      domainId,
      document,
      documentTh,
    };
  }
}
window.customElements.define("aon-messenger-list", AonMessengerList);
