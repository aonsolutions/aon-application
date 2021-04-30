import { AonElement } from "../../components/AonElement.js";
import { AonCard } from "../../components/aon-card.js";
import { scrollInfinite, serializeForm, setAttributes, setFullDate, setStyles, setTime } from "../../services/utils.js";
import { firstLetters } from "../signin/time-control/utils.js";
import { AonTabs } from "../../components/aon-tabs.js";
import { Swipe } from "../../components/swipe.js";
import { getDomainUserRoles, getNotification, getTastHolders, markReadNotification, sendNotification } from "../../services/service.js";
import { AonMessengerList } from "../messenger/aon-messenger-list.js";
import { AonDocumental } from "../documental/aon-documental.js";
import { createButtonClose, createContent, createLi, createTitle, createDivFooter, createDivFooter1, createAonNotification, createUl, createForm, createSpanFloat } from "./createComponent.js";
import { AonIconButton } from "../../components/aon-icon-button.js";
import { AonDialog } from "../../components/aon-dialog.js";
import { CONSTANT, EVENT } from "../../environments/environments.js";
import { AonInput } from "../../components/aon-input.js";
import { AonSelect } from "../../components/aon-select.js";
import { DomainUserRoles } from "../../models/DomainUserRoles.js";
import { TYPE_USER, NOTIFICATION_TABS, badgeUpdate } from "./NotificationEnums.js";

export class AonNotification extends AonElement {
  AON_NOTIFICATION;
  AON_TABS;
  MORE;
  DIALOG;
  TASK_HOLDERS;
  dur;
  static get observedAttributes() {
    return [CONSTANT.DATA];
  }

  get data() {
    return this.getAttribute(CONSTANT.DATA) ? JSON.parse(this.getAttribute(CONSTANT.DATA)) : null;
  }

  set data(data) {
    this.setAttribute(CONSTANT.DATA, data);
  }

  getFilter() {
		return this.hasAttribute(CONSTANT.FILTER)? JSON.parse(this.getAttribute(CONSTANT.FILTER))	: {page:0, peerPage:10};
	}

	setFilter(filter) {
		return this.setAttribute(CONSTANT.FILTER, JSON.stringify(filter));
	}

  attributeChangedCallback(name, oldValue, newValue) {}

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    getDomainUserRoles({reload:true}).then(r=>{
      this.dur = new DomainUserRoles(r);
      this.build();
    });
  }

  initialize() {
    this.AON_NOTIFICATION = "aonNotification";
    this.DIALOG = this.AON_NOTIFICATION+"Dialog";
    this.AON_TABS = this.AON_NOTIFICATION + "aonTabs";
    this.TASK_HOLDERS = [];
    this.aonNotifyIconEl = document.querySelector("aon-notification-icon");
  }

  getDur() {
    return this.dur;  
  }

  async build() {
    this.eventListener();
    if (this.isMobile()) { 
      this.paintMobile();
    } else {
      this.paintDesk();
    }
    this.notificationView();

    let elementScroll = this.isMobile() ? this.getElement(this.ROOT_PANEL) : undefined;
    scrollInfinite(elementScroll , async()=>{
      await this.loadMore();
    })
  }
    
  eventListener(){
    window.addEventListener(EVENT.RECEIVED_NOTIFICATION, ()=>{
			this.notificationView();
		});
  }

  paintDesk() {
    this.appendChild(this.createContentDiv());
  }

  async paintMobile() {
    const aonTabs = new AonTabs();
    aonTabs.id = this.AON_TABS;
    aonTabs.addEventListener(EVENT.CHANGE, ({ detail }) => {
      if (detail) {
        this.changeTabs(detail.position);
      }
    });
    aonTabs.setButtons(NOTIFICATION_TABS);

    this.appendChild(aonTabs);
    this.appendChild(this.createContentDiv());
  }

  async notificationView() {
    try {
      this.MORE = true;
      let filter = this.getFilter();
      filter.page = 0;
      this.setFilter(filter);
      const aonNotification = this.getElement(this.AON_NOTIFICATION);
      aonNotification.style.width = "80%";
      
      const idUl = this.AON_NOTIFICATION+"Ul";
      createUl(idUl).appendTo(aonNotification);
      // ------ BUTTON FLOAT ADD NOTIFICATION
      if(!this.getDur().isEmployee()) {
        this.createButtonFloat();
      }
      
      await this.loadMore();
      this.changeBadgeComponent();
    } catch (error) {
      console.log(error);
    }
  }

  async loadMore() {
	  const aonNotification = this.getElement(this.AON_NOTIFICATION);
		let filter = this.getFilter();
		if(aonNotification && this.MORE) {
			filter.page = filter.page + 1;
			this.setFilter(filter);
      const datos = await getNotification(filter);
      if(datos.length == 0) {
        this.MORE = false;
        if(filter.page<=1){
          this.createCard({
            id: 0,
            status:1,
            title: "Sin notificaciones",
            body: "No existen notificaciones pendientes."
          });
        }
      } else {
        datos.map((data) => {
          const aonCard = this.createCard(data, true);
          aonCard.flex = "true";
          aonCard.addEventListener(EVENT.CLICK, () => {
            this.goNotification(data);
          });

          const divFooter = createDivFooter();
          aonCard.setContent(divFooter.element);
          const dateText = firstLetters(setFullDate(data.date)) + " " + setTime(data.date);
          createDivFooter1(dateText).appendTo(divFooter)
        });
      }
		}
    this.swipe();
	}


  async messengerView() {
    try {
      const aonNotification = this.getElement(this.AON_NOTIFICATION);
      if (aonNotification) {
        aonNotification.style.width = "100%";
        const aonMessengerList = new AonMessengerList();
        aonMessengerList.getTable(aonNotification.id);
      }
    } catch (error) {
      console.log(error);
    }
  }

  createContentDiv() {
    return this.getElement(this.AON_NOTIFICATION) || createAonNotification(this.AON_NOTIFICATION).element;
  }

  async goNotification(data) {
    const {id, source, source_id} = data;
    this.markReadNotification(id);
    if(source && source_id){
      let aonComponent = null;
      switch(source){
        case "DOCUMENTAL":
          aonComponent =  new AonDocumental();
          aonComponent.value = source_id;
          break;
      }

      if(aonComponent){
        this.rootPanel(aonComponent);
      }
    }
  }

  /**
   *
   * @param {title, id} data
   * @returns
   */
  createCard(data, close = false) {
    const ulEl = this.getElement(this.AON_NOTIFICATION+"Ul");

    const idCard = this.AON_NOTIFICATION + "Card" + data.id;
    if(this.getElement(idCard)) this.getElement(idCard).remove();

    const aonCard = new AonCard();
    aonCard.style.cursor = "pointer";
    aonCard.id = idCard;    
    
    createLi(data).appendTo(ulEl).appendChild(aonCard);

    if(close){
      this.createButtonClose(aonCard, data.id);
    }
    const content = createContent(data.body);
    aonCard.setContent(content.element);
    if (!data.status) {
      aonCard.setBackground(`rgb(0, 36, 105, 0.1)`);
    }

    const titleEl =  aonCard.getSection1();
    titleEl.style.display="block";
    titleEl.innerHTML = createTitle(data.title).element.outerHTML;
    return aonCard;
  }

  changeTabs(position = 0) {
    let aonNotification = this.getElement(this.AON_NOTIFICATION);
    if (aonNotification) aonNotification.innerHTML = "";
    switch (position) {
      case 0:
        this.notificationView();
        break;
      case 1:
        this.messengerView();
        break;
    }
  }

  markReadNotification(id){
    this.changeBadgeComponent(-1);
    try {markReadNotification({id});} catch (error) { console.log(error)} //markRead
    let aonCard = this.getElement(this.AON_NOTIFICATION + "Card" + id);
    if (aonCard) {
      aonCard.setBackground("#fff");
      const icon = this.getElement(`${id}Icon`);
      if (icon) {
        icon.remove();
      }
    }
  }

  swipe(){    
    let tasks = document.querySelectorAll(`#${this.AON_NOTIFICATION} ul > li`);
    if(tasks.length){
      new Swipe(tasks).onDelete(({dataset})=>{
        if(dataset && dataset.id ) {
          this.markReadNotification(dataset.id);
        }
      });
    }
  }

  createButtonClose(aonCard, id){
    let button = createButtonClose()
    button.element.addEventListener(EVENT.CLICK, (ev)=>{
      ev.stopPropagation();
      this.removeFadeOutNotify(aonCard, 600, id)
    });
    button.appendTo( aonCard.getCardTitle() );
  }

  removeFadeOutNotify(el, speed, notificationId) {
    if (el) {
      const seconds = speed / 1000;
      setStyles(el.getCard(),{
        transition: "opacity " + seconds + "s ease",
        opacity: 0
      })
      setTimeout(() => {
        el.parentNode.removeChild(el);
        this.markReadNotification(notificationId);
      }, speed);
    }
  }

  deleteAll() {
    let aonNotify = this.aonNotifyIconEl;
    if (aonNotify) {
      aonNotify.NOTIFICATIONS.map((notification) => {
        let el = this.getElement(
          this.AON_NOTIFICATION + "Card" + notification.id
        );
        this.removeFadeOutNotify(el, 600, notification.id);
      });
    }
  }

  async changeBadgeComponent(number = 0) {
    let aonNotify = this.aonNotifyIconEl;
    if (aonNotify) {
      if (number == 0) { //--------DELETE
        await aonNotify.getTotalNotification();
      }
      const count = aonNotify.COUNT;
      count.notification = count.notification + number;
      aonNotify.changeBadge();
      const aonTabs = this.getElement(this.AON_TABS);
      if (aonTabs) {
        aonTabs.updateBadge(badgeUpdate(count));
      }
    }
  }

  createButtonFloat(){
    let aonNotification = this.getElement(this.AON_NOTIFICATION);
    const dialog = this.getElement(this.DIALOG) || new AonDialog();
    dialog.id = this.DIALOG;
    aonNotification.appendChild(dialog);
    const span = createSpanFloat();
   
    let aonIconButtonEl = setAttributes(new AonIconButton(),{
      id:"aonNotificationFloatSpanButton",
      icon:"add",
      title:"Crear notificatión",
      background:"#f1f1f1"
    });
    aonIconButtonEl.addEventListener(EVENT.CLICK, ()=>this.openDialog());

    span.appendChild(aonIconButtonEl);

    aonNotification.appendChild(span.element);
  }

  openDialog() {
    const dialog = 	this.getElement(this.DIALOG);
    dialog.clear();
    if (!this.isMobile()) dialog.width = "500px";
    dialog.setContent(this.getDialogHtml());
    dialog.setTitle("Notificación");
    dialog.open();
    dialog.addSendAction(() =>  this.sendNotification(), "Enviar");
  }

  getDialogHtml(){
    const form = createForm(`${this.AON_NOTIFICATION}Form`);

    let tipoSelect = setAttributes(new AonSelect(),{
      name:"type",
      id:"type",
      title:"Tipo",
      options : JSON.stringify(TYPE_USER),
      value : TYPE_USER[0].value
    });
    tipoSelect.addEventListener(EVENT.CHANGE, ({detail})=>{
      if(detail && detail.value) this.hiddenElement(detail.value);
    });
    form.appendChild(tipoSelect);

    let taskHolders = setAttributes(new AonSelect(),{
      name:"task_holder",
      id:"task_holder",
      title:"Trabajador",
    });
    this.listTaskHolder().then(r => taskHolders.options = JSON.stringify(r));
    form.appendChild(taskHolders);

    let aonInputEmail = setAttributes(new AonInput(),{
      name:"email",
      id:"email",
      description:"Correo",
      type:"text",
      hidden:true
    });
    form.appendChild(aonInputEmail);

    let aonInputTitle = setAttributes(new AonInput(),{
      name:"title", 
      id:"title", 
      description:"Título",
      type:"text"
    });
    form.appendChild(aonInputTitle);

    let aonInputBody = setAttributes(new AonInput(),{
      name:"body",
      id:"body",
      description:"Mensaje",
      type:"text"
    });
    form.appendChild(aonInputBody);

    return form.element;
  }

  hiddenElement(value){
    let taskHolderEl = this.getElement("task_holder");
    let emailEl = this.getElement("email");
      if("employee"===value){
          taskHolderEl.hidden = false;
          emailEl.setAttribute(CONSTANT.HIDDEN, true);
      } else {
          taskHolderEl.hidden = true;
          emailEl.removeAttribute(CONSTANT.HIDDEN);
      }
  }

  async listTaskHolder(){
    if(this.TASK_HOLDERS.length<= 0){
      let result = await getTastHolders().catch(e=>null);
      if(result) this.TASK_HOLDERS = result.map(r=>({name:r.name,value:r.id}));
    }
    return this.TASK_HOLDERS;
  }

  async sendNotification(){
    const formData = serializeForm(this.getElement(`${this.AON_NOTIFICATION}Form`));
    if(formData.title && formData.body) {
      let dialog = this.getElement(this.DIALOG);
      let buttonAccept = dialog.getButtonAccept();
      buttonAccept.disabled = true;
      try {
          await sendNotification(formData);
      } catch (error) {
        console.log(error);
      }
      buttonAccept.disabled = false;
      dialog.close();
    }

  }

}
window.customElements.define("aon-notification", AonNotification);
