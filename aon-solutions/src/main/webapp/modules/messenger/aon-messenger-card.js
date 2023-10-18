import { AonElement } from "../../components/AonElement.js";
import { getDomainUserRoles } from "../../services/companyService.js";
import { DomainUserRoles } from "../../models/DomainUserRoles.js";
import { TAG, EVENT, CSS } from "../../environments/environments.js";
import { getTasks } from "../../services/taskService.js";
import { TaskUtils } from "../messenger/utils/TaskUtils.js";
import { sortBy } from "../../services/utils.js";
import { TASK_STATUS } from "../messenger/MessengerEnums.js";
import { getTaskHolder } from "../../services/taskHolderService.js";
import { TaskListUtils } from "../messenger/utils/TaskListUtils.js";
import * as LS from "../../services/localStorageService.js";
import { AonDateUtils } from "../utils/AonDateUtils.js";
import {getWorkgroups} from '../../services/workgroupService.js';
import { NotificationUtils } from "../notification/utils/NotificationUtils.js";

export class AonMessengerCard extends AonElement {
  AON_FISCAL;
  MODELS = [];
  BANKS=[];
  dur;
  TABLE_ID;
  content;
  _filter;
  TASK_HOLDER;
  clickEvent;
  
  constructor(fn) {
    super();
    this.clickEvent = fn;
  }

  connectedCallback() {
    this.initialize();
    getDomainUserRoles({ reload: true }).then((r) => {
      this.dur = new DomainUserRoles(r);
      this.build();
    });
  }

  initialize() {}

  getDur() {
    return this.dur;
  }

  build() {
    this.paintView();
    this.buildToolbar();
  }

  paintView() {
    let cardContent = this.createElement(TAG.DIV);
    cardContent.className = CSS.AON_FLEX_COLUMN;
    cardContent.style.justifyContent = "space-between";
    cardContent.style.height = "100%";
    cardContent.style.alignItems = "flex-start";
    cardContent.id = "messengerCard";
    
    let tableContent = this.createElement(TAG.DIV);
    tableContent.className = CSS.AON_FLEX_COLUMN;
    tableContent.style.width = "100%";
    tableContent.id = "messengerCardTable";

    let showAllMessage = this.createElement(TAG.DIV);
    showAllMessage.className = CSS.AON_FLEX;
    showAllMessage.classList.add(CSS.AON_MESSENGER_CARD_BUTTON);
    showAllMessage.id = "messengerCardMessage";
    showAllMessage.addEventListener(EVENT.CLICK, this.clickEvent);

    cardContent.appendChild(tableContent);
    cardContent.appendChild(showAllMessage);

    this.appendChild(cardContent);
  }

  buildToolbar() {
    this.getMeseggers().then(messengers => {
      this.getTable(this.getDataDesktop(messengers));
      this.getMessage(messengers.length);
    });
  }

  async getMeseggers() {
    let data = [];
    let filter = { page: 1, perPage: 30, status: TASK_STATUS.PENDING };

    const taskholder = await getTaskHolder({ reload: false }).catch(() => null);
    if (taskholder) {
      this.TASK_HOLDER = taskholder;
      filter.task_holder = taskholder.id;
    }

    let filterWorkgroup = {status:"ACTIVE"};
    let isManager = this.getDur().isMessengerManager();
    if(!isManager && this.TASK_HOLDER) {
      filterWorkgroup.task_holder = this.TASK_HOLDER.id;
    }

    await getWorkgroups(filterWorkgroup).then( workgroup => {
      filter.workgroups = workgroup.map(t => t.id);
    });

    try {
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

  getDataDesktop(datos) {
    try {
      const documents = this.getDocuments();
      return datos.map((dato, idx) => ({
        ...dato,
        dateParse: this.getDateParseNew(dato.date),
        newTitle: this.getTitleDesktop(dato, documents.document, documents.documentTh),
        assigned: TaskListUtils.getAssignedHtml(dato, documents.domainId),
        lettersHtml: TaskListUtils.getIcon(dato),
        fn: () => this.goMessengerChat(dato, idx)
      }));
    } catch (e) {
      console.log(e);
    }
  }

  async goMessengerChat(res, idx) {
    let data = {source: "MESSENGER", source_id: res.id, domain: res.domain};
    const aonComponent = NotificationUtils.getNotificationComponent(data);
    if(aonComponent){
      this.rootPanel(aonComponent);
    }
  }

  getDateParseNew(date) {
    return window.innerWidth < 768 ? AonDateUtils.getDayMonthOrFull(date) : AonDateUtils.setDateTpDay(date);
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

  getTitleDesktop(messenger, document, documentTh) {
    let div = this.createElement(TAG.DIV);
    div.className = CSS.AON_FLEX_COLUMN;
    div.style.alignItems = "flex-start";

    let title = this.createElement(TAG.SPAN);
    title.innerHTML = messenger.title;
    title.title = messenger.title;
    title.className = CSS.AON_ELLIPSIS;
    title.style.fontSize = "1.1rem";
    title.style.color = "rgb(246, 101, 90)";
    title.style.fontWeight = "500";

    div.appendChild(title);

    let content = this.createElement(TAG.SPAN);
    content.className = CSS.AON_ELLIPSIS;
    div.appendChild(content);

    let sender = this.getSender(messenger, document, documentTh);

    let description = messenger.description;
    try {
      description = JSON.parse(messenger.description).observation;
    } catch (e) {}

    if (description) {
      const dText = description.replace(/<[^>]+>|&nbsp;|\n/g, " ");
      content.innerHTML = `<b>${sender}</b> ${dText}`;
      content.title = dText;
    } else {
      content.innerHTML = `<b>${sender}</b> ${description}`;
      content.title = description;
    }

    return div;
  }

  getSender(res, document, documentTh) {
    let sender = "";
    if (res.registry && res.registry.name && document !== res.registry.document) {
      sender = `${res.registry.name} ${sender}`;
    } else if (res.sender && res.sender.name && documentTh !== res.sender.document) {
      sender = `${res.sender.name} ${sender}`;
    } else if (res.workgroup && res.workgroup.description) {
      // GRUPO ASIGNADO
      sender = res.workgroup.description;
    } else {
      sender = "SIN GRUPO ASIGNADO";
    }
  
    return sender;
  };

  getTable(messengers) {
    let content = this.getElement("messengerCardTable");
    let maxIndex = messengers.length > 3 ? 3 : messengers.length;
    
    for (let index = 0; index < maxIndex; index++) {
      const messenger = messengers[index];
      
      let row = this.createElement(TAG.DIV);
      row.classList = CSS.AON_MESSENGER_CARD_ROW;
      row.addEventListener(EVENT.CLICK, () => {
        messenger.fn();
        // () => messenger.fn;
      });

      let leftContent = this.createElement(TAG.DIV);
      leftContent.className = CSS.AON_FLEX;
      leftContent.style.gap = ".5rem";
      leftContent.style.alignContent = "center";
      leftContent.style.alignItems = "center";

      let icon = messenger.lettersHtml;
      icon.firstElementChild.style.color = "rgb(246, 101, 90)";
      icon.firstElementChild.firstElementChild.style.fontSize = "30px";
      leftContent.appendChild(icon);

      let title = messenger.newTitle;
      leftContent.appendChild(title);

      let rightContent = this.createElement(TAG.DIV);
      rightContent.className = CSS.AON_FLEX;
      rightContent.style.alignItems = "center";
      rightContent.style.gap = ".5rem";

      let assigned = messenger.assigned;
      rightContent.appendChild(assigned);

      let date = this.createElement(TAG.SPAN);
      date.style.whiteSpace = "nowrap";
      date.style.minWidth = window.innerWidth < 768 ? "5rem" : "9rem";
      date.style.textAlign = "right";
      date.innerHTML = messenger.dateParse;

      rightContent.appendChild(date);

      row.appendChild(leftContent);
      row.appendChild(rightContent);

      content.appendChild(row);
    }

    if(messengers.length == 0){
      let messengerCard = this.getElement("messengerCard");
      messengerCard.style.display = "none";
    }
  }

  getMessage(messageLenght) {
    let message = this.getElement("messengerCardMessage");

    let showAll = this.createElement(TAG.SPAN);
    showAll.style.color = "rgb(246, 101, 90)";
    showAll.style.fontWeight = "500";
    showAll.innerHTML = "Ver todos los mensajes";

    let budget = this.createElement(TAG.SPAN);
    budget.className = CSS.AON_BADGE;
    budget.innerHTML = messageLenght;

    message.appendChild(showAll);
    message.appendChild(budget);
  }

}
window.customElements.define("aon-messenger-card", AonMessengerCard);
