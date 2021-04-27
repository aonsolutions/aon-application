import { AonElement } from "../../components/AonElement.js";
import { AonCard } from "../../components/aon-card.js";
import { newComponent, scrollInfinite, setFullDate, setTime } from "../../services/utils.js";
import { firstLetters } from "../signin/time-control/utils.js";
import { AonTabs } from "../../components/aon-tabs.js";
import { Swipe } from "../../components/swipe.js";
import { getNotification, markReadNotification } from "../../services/service.js";
import { AonMessengerList } from "../messenger/aon-messenger-list.js";
import { AonDocumental } from "../documental/aon-documental.js";
import { createContent, createLi, createTitle, createDivFooter, createDivFooter1, createAonNotification, createUl } from "./createComponent.js";

export class AonNotification extends AonElement {
  AON_NOTIFICATION;
  AON_TABS;
  MORE;
  static get observedAttributes() {
    return ["data"];
  }

  get data() {
    return this.getAttribute("data") ? JSON.parse(this.getAttribute("data")) : null;
  }

  set data(data) {
    this.setAttribute("data", data);
  }

  getFilter() {
		return this.hasAttribute('filter')? JSON.parse(this.getAttribute('filter'))	: {page:0, peerPage:10};
	}

	setFilter(filter) {
		return this.setAttribute('filter', JSON.stringify(filter));
	}

  attributeChangedCallback(name, oldValue, newValue) {}

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize() {
    this.AON_NOTIFICATION = "aonNotification";
    this.AON_TABS = this.AON_NOTIFICATION + "aonTabs";
    this.aonNotifyIconEl = document.querySelector("aon-notification-icon");
  }

  async build() {
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

  paintDesk() {
    this.appendChild(this.createContentDiv());
  }

  async paintMobile() {
    const aonTabs = new AonTabs();
    aonTabs.id = this.AON_TABS;
    aonTabs.addEventListener("change", ({ detail }) => {
      if (detail) {
        this.changeTabs(detail.position);
      }
    });
    aonTabs.setButtons([
      {
        name: "Notificaciones",
        id: "notification",
        icon: "notifications",
      },
      {
        name: "Solicitudes",
        id: "messenger",
        icon: "assignment",
      }
    ]);

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
          aonCard.addEventListener("click", () => {
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
    newComponent({
      type: "label",
      text: "×",
      styles:{
        float: "right",
        marginTop: "-23px",
        marginRight: "-19px",
        cursor: "pointer",
        padding: "10px",
      },
      events:{
        click: (ev) => {
            ev.stopPropagation();
            this.removeFadeOutNotify(aonCard, 600, id)
        }
      }
    }).appendTo( aonCard.getCardTitle() );
  }

  removeFadeOutNotify(el, speed, notificationId) {
    if (el) {
      const seconds = speed / 1000;
      const divCard = el.getCard();
      divCard.style.transition = "opacity " + seconds + "s ease";
      divCard.style.opacity = 0;
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
        aonTabs.updateBadge([
          {
            id: "notification",
            badge: count.notification,
          },
          {
            id: "messenger",
            badge: count.messenger,
          },
        ]);
      }
    }
  }
}
window.customElements.define("aon-notification", AonNotification);
