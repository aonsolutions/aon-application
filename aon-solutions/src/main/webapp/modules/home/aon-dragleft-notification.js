import { AonCard } from '../../components/aon-card.js';
import { AonIcon } from '../../components/aon-icon.js';
import { getNotification } from 'aonsolutions/services/service.js'; 
import { AonDateUtils } from "aonsolutions/modules/utils/AonDateUtils.js";
import { AonDragLeft } from '../../components/aon-dragleft.js';
import { NotificationUtils } from '../notification/utils/NotificationUtils.js'
import { EVENT } from '../../environments/environments.js';

export class AonDragLeftNotification extends AonDragLeft {
  constructor() {
    super();
  }

  connectedCallback() {
    super.connectedCallback();
    this.loadNotifications();
  }

  async loadNotifications() {
    try {
      const notifications = await this.getNotifications();
      const cards = notifications.slice(0, 5).map((res, index) => this.createNotificationCard(res, index));
      this.setContent(cards);
    } catch (error) {
      console.error("Error al cargar las notificaciones:", error);
    }
  }
  async getNotifications() {
    const response = await getNotification({ page: 1, perPage: 5, status: "unread" });
    return response || [];
  }

  createNotificationCard(res, index) {
    const card = this.createDiv(`notification-card-${index}`);
    card.className = "aonDragLeftCard";

    const notificationDiv = document.createElement('div');
    notificationDiv.classList.add("aonDragLeftNotificationDiv");
    notificationDiv.addEventListener(EVENT.CLICK,()=>this.goNotification(res));

    const title = document.createElement('span');
    title.innerHTML = res.title;
    title.classList.add("aonDragLeftNotificationDivTitle");

    const divContent = document.createElement('div');
    divContent.classList.add("aonDragLeftNotificationDivContent");

    let icon = new AonIcon();
    icon.size = "35px";
    icon.icon = this.getNotificationIcon(res.source);
    divContent.appendChild(icon);

    const body = document.createElement('span');
    body.innerHTML = res.body;
    body.classList.add("aonEllipsis");
    body.classList.add("aonDragLeftNotificationDivBody");
    divContent.appendChild(body);

    const date = document.createElement('span');
    date.innerHTML = `( ${this.formatDate(res)} )`;
    date.classList.add("aonDragLeftNotificationDivDate");

    notificationDiv.appendChild(title);
    notificationDiv.appendChild(divContent);
    notificationDiv.appendChild(date);

    card.appendChild(notificationDiv); 

    return card;
  }

  getNotificationIcon(source) {
    switch (source) {
      case "DOCUMENTAL":
        return "aon_new_documental";
      case "MESSENGER":
        return "aon_new_messenger";
      case "COMUNICA":
        return "aon_new_payroll";
      case "INVOICE":
        return "aon_new_invoice";
      default:
        return "aon_new_documental"; 
    }
  }

  formatDate(res) {
    const inputDate = new Date(res.date);
    const today = new Date();
    const todayStart = new Date(today.getFullYear(), today.getMonth(), today.getDate());
    const yesterday = new Date(todayStart);
    yesterday.setDate(todayStart.getDate() - 1);

    const abbreviatedMonths = ["Ene.", "Feb.", "Mar.", "Abr.", "May.", "Jun.", "Jul.", "Ago.", "Sep.", "Oct.", "Nov.", "Dic."];
    
    let dayMonth = AonDateUtils.getDayMonth(inputDate);
    const [day, month] = dayMonth.split(' ');
    const monthIndex = new Date(inputDate).getMonth();
    const formattedMonth = abbreviatedMonths[monthIndex];
    const formattedDate = `${day} de ${formattedMonth}`;

    const inputDateStartOfDay = new Date(inputDate.getFullYear(), inputDate.getMonth(), inputDate.getDate());
    const differenceInTime = todayStart - inputDateStartOfDay;
    const differenceInDays = Math.ceil(differenceInTime / (1000 * 3600 * 24));

    if (differenceInDays < 7) {
      if (differenceInDays === 0) {
        return `Hoy, ${formattedDate}, ${AonDateUtils.setTime(inputDate)}`;
      }
      if (inputDateStartOfDay.toDateString() === yesterday.toDateString()) {
        return `Ayer, ${formattedDate}, ${AonDateUtils.setTime(inputDate)}`;
      }
      return `${AonDateUtils.getDayStr(inputDate)}, ${formattedDate}, ${AonDateUtils.setTime(inputDate)}`;
    }

    if (inputDate.getFullYear() === today.getFullYear()) {
      return `${formattedDate}, ${AonDateUtils.setTime(inputDate)}`;
    }

    return `${formattedDate} de ${inputDate.getFullYear()}`;
  }

  goNotification(data) {
    const aonComponent = NotificationUtils.getNotificationComponent(data);
    if(aonComponent){
      this.rootPanel(aonComponent);
    }
}
}

if (!window.customElements.get('aon-dragleft-notification')) {
  window.customElements.define('aon-dragleft-notification', AonDragLeftNotification);
}
