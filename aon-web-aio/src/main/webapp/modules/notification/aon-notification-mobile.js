import { AonElement } from "../../components/AonElement.js";
import { AonCard } from "../../components/aon-card.js";
import { setStyles } from "../../services/utilsComponents.js";
import { firstLetters } from "../timecontrol/time-control/utils.js";
import { AonTabs } from "../../components/aon-tabs.js";
import { Swipe } from "../../components/swipe.js";
import { getDomainUserRoles, getNotification, getTastHolders, markReadNotification } from "../../services/service.js";
import { AonMessengerList } from "../messenger/aon-messenger-list.js";
import {  NotificationCreateComponent } from "./createComponent.js";
import { AonDialog } from "../../components/aon-dialog.js";
import { CONSTANT, CSS, EVENT, MSG } from "../../environments/environments.js";
import { DomainUserRoles } from "../../models/DomainUserRoles.js";
import { NotificationEnums } from "./NotificationEnums.js";
import { NotificationUtils } from "./utils/NotificationUtils.js";
import { AonToast } from "../../components/aon-toast.js";
import { AonDateUtils } from "../utils/AonDateUtils.js";
import { CreateComponent } from "../../components/CreateComponent.js";

export class AonNotificationMobile extends AonElement {
  CONTENT;
  AON_TABS;
  MORE;
  DIALOG;
  TASK_HOLDERS; 
  dur;
  UL;
  TOAST;
  NOTIFICATIONS;
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
		return this.hasAttribute(CONSTANT.FILTER)? JSON.parse(this.getAttribute(CONSTANT.FILTER))	: {page:0, perPage:10, status:"unread"};
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
    this.id = NotificationEnums.NOTIFICATION_IDS.AON_NOTIFICATION_MOBILE;
    this.CONTENT = this.id+"Content";
    this.TOAST = this.id+"Toast";
    this.DIALOG = this.id+"Dialog";
    this.AON_TABS = this.id + "aonTabs";
    this.UL = this.id+"Ul";
    this.TASK_HOLDERS = [];
    this.NOTIFICATIONS = [];
    this.aonNotifyIconEl = document.querySelector("aon-notification-icon");
  }

  getDur() {
    return this.dur;  
  }

  async build() {
    let toast = new AonToast();
    toast.id = this.TOAST;
    this.appendChild(toast);
    this.eventListener();

    this.paintView();
    
      
    this.changeTabs(0);
    
    let elementScroll = this.getElement(this.ROOT_PANEL);
    elementScroll.classList.add(CSS.MATERIAL_SCROLL);
    this.scrollInfinite(elementScroll, async()=>{
      await this.loadMore();
    })
  }
    
  eventListener(){
    window.addEventListener(EVENT.RECEIVED_NOTIFICATION, ()=>{
			this.notificationView();
		});
  }

  paintView() {
    const aonTabs = new AonTabs();
    aonTabs.id = this.AON_TABS;
    aonTabs.addEventListener(EVENT.CHANGE, ({ detail }) => {
      if (detail) this.changeTabs(detail.position);
    });
    aonTabs.setButtons(NotificationEnums.NOTIFICATION_TABS);

    this.appendChild(aonTabs);
    this.appendChild(this.createContentDiv());
  }

  async notificationView() {
    try {
      this.MORE = true;
      let filter = this.getFilter();
      filter.page = 0;
      this.setFilter(filter);
      const aonNotification = this.getElement(this.CONTENT);
      aonNotification.style.width = "80%";
      if(!this.isMobile()){
        aonNotification.style.margin = "auto";
      }

      NotificationCreateComponent.createUl(this.UL).appendTo(aonNotification);
      // ------ BUTTON FLOAT ADD NOTIFICATION
      if(!this.getDur().isEmployee()) {
        this.addFloatButton();
      }
      
      await this.loadMore();
      this.changeBadgeComponent();
    } catch (error) {
      console.log(error);
    }
  }

  async loadMore() {
	  const aonNotification = this.getElement(this.CONTENT);
		let filter = this.getFilter();
		if(aonNotification && this.MORE) {
			filter.page = filter.page + 1;
			this.setFilter(filter);
      const datos = await getNotification(filter);

      if(datos.length == 0) {
        this.MORE = false;
        if(filter.page<=1)
          this.createCard({
            id: 0,
            status:1,
            title: MSG.NO_NOTIFICATIONS,
            body: "No existen notificaciones pendientes."
          });
      } else {
        this.removeFadeOutNotify(0, 0);
        
        datos.map((data) => {
          this.addNotification(data);
          const aonCard = this.createCard(data, true);
          aonCard.flex = "true";
          aonCard.addEventListener(EVENT.CLICK, () =>  this.goNotification(data));
          const divFooter = NotificationCreateComponent.createDivFooter();
          aonCard.setContent(divFooter.element);
          const dateText = firstLetters(AonDateUtils.setFullDate(data.date)) + " " + AonDateUtils.setTime(data.date);
          NotificationCreateComponent.createDivFooter1(dateText).appendTo(divFooter)
        });
      }
		}
    this.eventSwipe();
	}

  createContentDiv() {
    return this.getElement(this.CONTENT) || NotificationCreateComponent.createAonNotification(this.CONTENT).element;
  }

  goNotification(data) {
    const aonComponent = NotificationUtils.getNotificationComponent(data);
    if(aonComponent){
        this.rootPanel(aonComponent);
    }
  }
  
  /**
   *
   * @param {title, id} data
   * @returns
   */
  createCard(data, close = false) {
    const ulEl = this.getElement(this.UL);
    const idCard = this.id + "Card" + data.id;
    
    if(this.getElement(idCard)) {
      this.getElement(idCard).remove();
    }

    const aonCard = new AonCard();
    aonCard.style.cursor = "pointer";
    aonCard.id = idCard;    
    
    NotificationCreateComponent.createLi(data).appendTo(ulEl).appendChild(aonCard);

    if(close){
      this.buttonClose(aonCard, data);
    }

    const content = NotificationCreateComponent.createContent(data.body);
    aonCard.setContent(content.element);
    if (!data.status) {
      aonCard.setBackground(`rgb(0, 36, 105, 0.1)`);
    }

    aonCard.getContent().classList.add(CSS.IMG_MAX_WIDTH);

    const titleEl = aonCard.getCardTitle1();
    titleEl.style.whiteSpace = "pre-wrap";
    titleEl.innerHTML = NotificationCreateComponent.createTitle(data.title).element.outerHTML;
    return aonCard;
  }

  changeTabs(position = 0) {
    let aonNotification = this.getElement(this.CONTENT);
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

  async messengerView() {
    try {
      const aonNotification = this.getElement(this.CONTENT);
      if (aonNotification) {
        aonNotification.style.width = "100%";
        const aonMessengerList = new AonMessengerList();
        aonMessengerList.paintTable(aonNotification);
      }
    } catch (error) {
      console.log(error);
    }
  }

  markReadNotification(data, save= true){
    const id = data.id;
    if(id){
      if(save){
        markReadNotification(data).catch(console.log);
      }

      this.changeBadgeComponent(-1);
      let aonCard = this.getElement(this.id + "Card" + id);
      if (aonCard) {
        aonCard.setBackground("#fff");
        const icon = this.getElement(`${id}Icon`);
        if (icon) {
          icon.remove();
        }
      }
    }
  }

  eventSwipe(){    
    let tasks = document.querySelectorAll(`#${this.id} ul > li`);
    if(tasks.length){
      new Swipe(tasks).onDelete(({dataset})=>{
        if(dataset && dataset.id ) {
          const noti = this.getNotificationById(parseInt(dataset.id));
          if(noti) {
            markReadNotification(noti).catch(console.log);
            this.markReadNotification(noti, false);
          }
        }
      });
    }
  }

  buttonClose(aonCard, data){
    let button = NotificationCreateComponent.createButtonClose()
    button.element.addEventListener(EVENT.CLICK, (ev)=>{
      ev.stopPropagation();
      this.removeFadeOutNotify(data, 600);
    });
    button.appendTo( aonCard.getCardTitle() );
  }

  removeFadeOutNotify(data=0, speed=0) {
    if(data.id){
      const notificationLi = document.querySelector(`[data-id='${data.id}']`);
      if(notificationLi){
        this.markReadNotification(data);
        const aonCardN = notificationLi.firstChild;
        if(aonCardN){
          const seconds = speed / 1000;

          setStyles(aonCardN.getCard(),{
            transition: "opacity " + seconds + "s ease",
            opacity: 0
          });

          setTimeout(() => {
            notificationLi.removeChild(aonCardN);
          }, speed);

        }
      }
    }
  }

  deleteAll() {
    let aonNotify = this.aonNotifyIconEl;
    if (aonNotify) {
      aonNotify.NOTIFICATIONS.map((notification) => {
        this.removeFadeOutNotify(notification.id, 600);
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
        aonTabs.updateBadge(NotificationEnums.badgeUpdate(count));
      }
    }
  }

  addFloatButton(){
    let aonNotification = this.getElement(this.CONTENT);
    const dialog = this.getElement(this.DIALOG) || new AonDialog();
    dialog.id = this.DIALOG;
    aonNotification.appendChild(dialog);
    const span = NotificationCreateComponent.createSpanFloat().element;

    if(!this.isMobile()){
      setStyles(span,{ bottom:0, top:76, right: "7%" });
    }

    aonNotification.appendChild(span);
    let icon = CreateComponent.createAonIconButton({
      attributes:{
        id:span.id+"Button",
        icon:"add",
        title:`${MSG.CREATE} ${MSG.NOTIFICATION}`,
      },
      events:{
        click: () => NotificationUtils.openDialog(this, dialog)
      }
    }, span);
    let btn = icon.getButton(); 
    btn.classList.add("addNotification", CSS.PULSE);
  }


  async listTaskHolder(){
    if(this.TASK_HOLDERS.length <= 0){
      let result = await getTastHolders().catch(e=>null);
      if(result) this.TASK_HOLDERS = result.map(r=>({name:r.name,value:r.id}));
    }
    return this.TASK_HOLDERS;
  }

    /**
   * 
   * @param {element html or undefined} element 
   * @param {*} fn return end elment
   */
  scrollInfinite(element, fn) {
    if(element){
      element.addEventListener(EVENT.SCROLL, async ({target:{scrollTop, scrollHeight, offsetHeight}}) => {
        if (scrollTop >= (scrollHeight - offsetHeight)) fn();
      });
    } else {
      element = document.body;
      window.addEventListener(EVENT.SCROLL, ()=>{
        if ( (element.scrollTop + element.clientHeight) >= element.scrollHeight) fn();
      }); 
    }
  }

  addNotification(notification){
    this.NOTIFICATIONS.push(notification);
  }

  getNotifications(){
    return this.NOTIFICATIONS;
  }

  getNotificationById(id){
    return this.NOTIFICATIONS.find(n  => n.id === id);
  }

  showToast(obj) {
    if(typeof obj === "string")  obj = JSON.parse(obj);
    const toast = this.getElement(this.TOAST);
    if(toast) toast.start(obj);
  }
}
window.customElements.define("aon-notification-mobile", AonNotificationMobile);
