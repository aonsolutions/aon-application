import { AonElement } from "../../components/AonElement.js";
import { AonCard } from "../../components/aon-card.js";
import { setFullDate, setTime } from "../../services/utils.js";
import { firstLetters } from "../signin/time-control/utils.js";
import { AonTabs } from "../../components/aon-tabs.js";
import { getNotification } from "../../services/service.js";
import { AonMessengerList } from "../messenger/aon-messenger-list.js";

export class AonNotification extends AonElement {
  AON_NOTIFICATION;
  AON_TABS;
  static get observedAttributes() {
    return ["data"];
  }

  get data() {
    return this.getAttribute("data")
      ? JSON.parse(this.getAttribute("data"))
      : null;
  }

  set data(data) {
    this.setAttribute("data", data);
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
      },
      // {
      //     name:"Facturas",
      //     id:"invoice",
      //     icon: "report",
      //  }
    ]);

    this.appendChild(aonTabs);
    this.appendChild(this.createContentDiv());
  }

  async notificationView() {
  
    try {
      const aonNotification = this.getElement(this.AON_NOTIFICATION);
      aonNotification.style.width = "80%";
      const datos = await getNotification();
      if (datos.length > 0) {
        datos.map((data, idx) => {
          const aonCard = this.createCard(data);
          aonCard.flex = "true";
          aonCard.addEventListener("click", (ev) => {
            this.goNotification(data);
          });
          const spanContent = this.createElement("span");
          spanContent.style = "font-size: 14px;font-family: Times New Roman, Times, serif; word-wrap: break-word;";
          spanContent.innerHTML = `${data.body}`;
          aonCard.setContent(spanContent);
          const divFooter = this.createElement("div");
          divFooter.style.display = "flex";
          divFooter.style.marginTop = "10px";
          divFooter.style.fontWeight = "800";
          divFooter.style.fontSize = "10px";
          const div1 = this.createElement("div");
          div1.innerHTML =
            firstLetters(setFullDate(data.date)) + " " + setTime(data.date);
          div1.style.marginLeft = "auto";
          divFooter.appendChild(div1);
          aonCard.setContent(divFooter);
        });
      } else {
        this.createCard({
          id: 0,
          title: "No existen notificaciones pendientes",
        });
      }
      await this.changeBadgeComponent();
    } catch (error) {}
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
    const aonNotification = this.getElement(this.AON_NOTIFICATION) || this.createElement("div");
    aonNotification.id = this.AON_NOTIFICATION;
    aonNotification.style.margin = "auto";
    aonNotification.style.marginTop = "21px";
    aonNotification.style.width = "80%";
    return aonNotification;
  }

  goNotification(data) {
    let aonCard = this.getElement(this.AON_NOTIFICATION + "Card" + data.id);
    if (aonCard) {
      aonCard.setBackground("#fff");
      const icon = this.getElement(`${data.id}Icon`);
      if (icon) {
        icon.remove();
        this.changeBadgeComponent(-1);
      }
      console.log(data);
    }
  }

  /**
   *
   * @param {title, id} data
   * @returns
   */
  createCard(data) {
    const aonNotificationEl = this.getElement(this.AON_NOTIFICATION);
    const aonCard = new AonCard();
    aonCard.id = this.AON_NOTIFICATION + "Card" + data.id;
    aonCard.style.cursor = "pointer";
    let title = `<span class="aonColorPrimary">${data.title}</span>`;
    if (!data.read)
      title = /*html*/ `<span style="color:red;" class="material-icons" id ="${data.id}Icon">error</span>${title}`;
    aonCard.title = title;
    aonNotificationEl.appendChild(aonCard);
    if (!data.read) aonCard.setBackground(`rgb(0, 36, 105, 0.1)`);
    const label = this.createElement("label");
    label.textContent = "×";
    label.style =
      "float: right;margin-top: -23px;margin-right: -19px;cursor: pointer;padding: 10px;";
    label.addEventListener("click", (ev) =>{
        ev.stopPropagation();
        this.removeFadeOutNotify(aonCard, 600)
    });
    aonCard.getCardTitle().appendChild(label);
    aonCard.getCard().classList.add("aonCardFlex");
    return aonCard;
  }

  removeFadeOutNotify(el, speed) {
    if (el) {
      const seconds = speed / 1000;
      const divCard = el.getCard();
      divCard.style.transition = "opacity " + seconds + "s ease";
      divCard.style.opacity = 0;
      setTimeout(() => {
        el.parentNode.removeChild(el);
        this.changeBadgeComponent(-1);
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
        this.removeFadeOutNotify(el, 600);
      });
    }
  }

  async changeBadgeComponent(number = 0) {
    let aonNotify = this.aonNotifyIconEl;

    if (aonNotify) {
      if (number == 0) { //--------DELETE
        await aonNotify.getPending();
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
}
window.customElements.define("aon-notification", AonNotification);
