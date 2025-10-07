import { AonElement } from "../../components/AonElement.js";
import { sortBy } from "../../services/utils.js";
import * as LS from "../../services/localStorageService.js";
import { NotificationUtils } from "../notification/utils/NotificationUtils.js";

import { getDomainUserRoles } from "../../services/companyService.js";
import { getTasks } from "../../services/taskService.js";
import { getTaskHolder } from "../../services/taskHolderService.js";
import { getWorkgroups } from '../../services/workgroupService.js';

import { TAG, EVENT, CSS } from "../../environments/environments.js";
import { TASK_STATUS } from "../messenger/MessengerEnums.js";
import { DomainUserRoles } from "../../models/DomainUserRoles.js";
import { TaskUtils } from "../messenger/utils/TaskUtils.js";
import { TaskListUtils } from "../messenger/utils/TaskListUtils.js";
import { AonDateUtils } from "../utils/AonDateUtils.js";

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
    this.getMeseggers().then(messengers => {
      this.getTable(this.getDataDesktop(messengers));
    });
  }

  async getMeseggers() {
    let data   = [];
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
        rightContent: this.getDateParseNew(dato.date),
        senderRecipt : this.getSenderRecipt(dato),
        newTitle: this.getTitleDesktop(dato),
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
    let rightContent = this.createElement(TAG.DIV);
    rightContent.classList.add("right");

    if(!date) {
      console.log("Formating Err");
      return rightContent;
    }
    
    let dateDescription = this.createElement(TAG.SPAN);
    dateDescription.classList.add("aonMessengerCardDate");
    
    let dateTime = this.createElement(TAG.SPAN);
    dateTime.classList.add("aonMessengerCardDate");
    
    var today = new Date();
    today.setHours(0,0,0,0);
    var yesterday = new Date(today);
    yesterday.setDate(today.getDate() - 1);

    if (date >= today) {
        dateDescription.innerHTML = "Hoy";
        dateTime.innerHTML = AonDateUtils.timeParserHHMM(date);
        rightContent.appendChild(dateDescription);
        rightContent.appendChild(dateTime);
    } else if (date >= yesterday) {
        dateDescription.innerHTML = "Ayer";
        dateTime.innerHTML = AonDateUtils.timeParserHHMM(date);
        rightContent.appendChild(dateDescription);
        rightContent.appendChild(dateTime);
    } else {
        dateDescription.innerHTML = AonDateUtils.getDayStr(new Date(date));
        dateTime.innerHTML = AonDateUtils.getDayMonthOrFullShort(date);
        rightContent.appendChild(dateDescription);
        rightContent.appendChild(dateTime);
    }

    return rightContent;
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

  getSenderRecipt(messenger) {
    let div = this.createElement(TAG.DIV);
    div.classList = "sender-receipt";

    let sender  = this.getSender(messenger);
    let receipt = this.getReceipt(messenger);

    div.appendChild(sender);
    div.appendChild(document.createTextNode(" " + receipt));

    return div;
  }

  getTitleDesktop(messenger) {
    let div = this.createElement(TAG.DIV);
    div.classList = "content-titles";

    let content       = this.createElement(TAG.DIV);
    content.classList = "content-title-one";
    content.innerHTML = messenger.title;
    content.title     = messenger.title;
    div.appendChild(content);

    let description = messenger.description;
    try {
      description = JSON.parse(messenger.description).observation;
    } catch (e) {}

    if (description) description = description.replace(/<[^>]+>|&nbsp;|\n/g, " ");

    let descriptionContent = this.createElement(TAG.DIV);
    descriptionContent.classList = "content-title-two";
    descriptionContent.innerHTML = `(${messenger.newNumber}) ${description}`;
    descriptionContent.title = description;
    div.appendChild(descriptionContent);

    return div;
  }

  getSender(res) {
    let sender       = this.createElement(TAG.DIV);
    sender.classList = "sender";

    let senderText = "";
    if (res.registry && res.registry.name) {
      senderText = `${res.registry.name}`;
    } else if (res.sender && res.sender.name) {
      senderText = `${res.sender.name}`;
    } else {
      senderText = "System";
    }

    sender.innerHTML = senderText;
    sender.title     = senderText;
  
    return sender;
  };

  getReceipt(res) {
    let receipt = "";

    if(res.task_holder && res.task_holder.name){
      receipt = `para ${res.task_holder.name}`;
    } else if(res.workgroup && res.workgroup.description){
      receipt = `para ${res.workgroup.description}`;
    }
  
    return receipt;
  };

  getTable(messengers) {
    if(messengers.length == 0){
      this.innerHTML = "No existen solicitudes";
      return;
    }

    let maxIndex = messengers.length > 5 ? 5 : messengers.length;
    
    for (let index = 0; index < maxIndex; index++) {
      const messenger = messengers[index];

      let row = this.createElement(TAG.DIV);
      row.classList = CSS.AON_MESSENGER_CARD_ROW;
      row.addEventListener(EVENT.CLICK, () => {
        messenger.fn();
        // () => messenger.fn;
      });
      
      // Titulo - de pada quien
      let senderRecipt = messenger.senderRecipt;
      row.appendChild(senderRecipt);
      // Contenido
      let rowContent       = this.createElement(TAG.DIV);
      rowContent.classList = CSS.AON_MESSENGER_CARD_ROW_CONTENT;
      row.appendChild(rowContent);

      let leftContent       = this.createElement(TAG.DIV);
      leftContent.className = "left";

      let icon = messenger.lettersHtml;
      leftContent.appendChild(icon);

      let title = messenger.newTitle;
      leftContent.appendChild(title);

      rowContent.appendChild(leftContent);
      rowContent.appendChild(messenger.rightContent);

      this.appendChild(row);
    }
  }

}
window.customElements.define("aon-messenger-card", AonMessengerCard);
