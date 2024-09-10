import { AonElement } from 'aonsolutions/components/AonElement.js';
import { AonIcon } from 'aonsolutions/components/aon-icon.js';
import { AonIconButton } from 'aonsolutions/components/aon-icon-button.js';
import { AonNotification } from 'aonsolutions/modules/notification/aon-notification.js';
import { CreateComponent } from 'aonsolutions/components/CreateComponent.js';
import { CONSTANT, EVENT, MSG, TAG, MATERIAL_ICONS, CSS } from 'aonsolutions/environments/environments.js';
import { NOTIFICATION } from  "../services/app.js";
import { getApp } from 'aonsolutions/services/app.js';
import { getTotalNotification, saveAuthDevice, deleteAuthDevice, getNotification, markReadNotification } from 'aonsolutions/services/service.js';
import { AonDateUtils } from "aonsolutions/modules/utils/AonDateUtils.js";
import { NotificationUtils } from "aonsolutions/modules/notification/utils/NotificationUtils.js";
import { AonApplication } from "aonsolutions/components/aon-application.js";


export const firstLetters = (l) => l.replace(/^.{1}/g, l[0].toUpperCase());

export class AonNotificationPanel extends AonElement {

    DIV_GENERAL;
    AON_NOTIFICATION_PANEL;

    get id() {
        return this.getAttribute(CONSTANT.ID);
    }

    set id(id) {
        this.setAttribute(CONSTANT.ID, id);
    }

    disconnectedCallback() {
        if(this.WINDOW_LISTENER){
            window.removeEventListener(EVENT.CLICK, this.WINDOW_LISTENER);
        }
    }

    connectedCallback () {
        this.initialize();
        this.build();   
    }

    goAonNotification(){
        try {
            this.rootPanel(new AonNotification());
        } catch(e){
            console.log(e);
        }
    }

    initialize() {
        this.id = this.id || 'aonNotificationPanel';
        this.DIV_GENERAL = this.id+'DivGeneral';
        this.AON_NOTIFICATION_PANEL = this.id + 'Application';
        this.OPEN_BUTTON = this.id+ 'OpenButton';
    }

	async build() {
        let header = this.getElement("aonHeaderWeb");
        let apps = this.getElement("aonMenuLeftop-applications");
        let aonHeader = this.getElement("aonHeader");
        let applications = this.getElement("applications");
        applications.className = "";

		let divGeneral = this.createDiv();
        divGeneral.id = this.DIV_GENERAL;
        divGeneral.classList.add("notificationPanelGeneralDiv");

        let openButton =new AonIconButton();
        openButton.id = this.OPEN_BUTTON;
        openButton.icon = "open_in_new";
        openButton.title = "Ver en pantalla completa";
        openButton.classList.add("notificationPanelAllDiv");

        divGeneral.appendChild(openButton);
        let welcome = this.getElement("aonCompanyTabFilter");
        if(!welcome){
            openButton.addEventListener(EVENT.CLICK, () =>  {
                this.goAonNotification();
                let rightPanel = document.querySelector('aon-right-panel'); 
                if (rightPanel) {
                    rightPanel.close(); 
                }
                header.className = "aonHeader aonHeaderNotification";
                apps.className = "aonMenuLeftop aonMenuLeftopNotification";
                aonHeader.buildApp(NOTIFICATION);
                aonHeader.setVisibleLogo(false);
                aonHeader.setVisibleApp(true);
            });
            
        } 
        
        let enterprise = this.getElement("aonHeaderCompanyListButton");
        enterprise.addEventListener(EVENT.CLICK, () => {
            apps.classList.remove("aonMenuLeftopNotification");
        });

        let notifications = await this.getData();  
    
        if (notifications.length === 0) {
            divGeneral.appendChild(this.buildNoNotifications());
        } else {
            this.loadMore(true);
        }

        this.appendChild(divGeneral);
        
    }

    buildNoNotifications(){
        let div = this.createDiv();
        div.id = this.ID + "NoNotifications";
        div.className = "notificationPanelNoNotification";
        
        let span = this.createElement(TAG.SPAN);
        span.innerHTML = "No hay notificaciones pendientes";
        div.appendChild(span);
        return div;
    }

    buildRow(res){
        let header = this.getElement("aonHeaderWeb");
        let apps = this.getElement("aonMenuLeftop-applications");
        let aonHeader = this.getElement("aonHeader");
        let divPrincipal = this.createDiv();
        divPrincipal.classList.add("notificationPanelRowPrincipalDiv");

        let divGeneral = this.createDiv();
        divGeneral.classList.add("notifcationPanelRowGeneralDiv");

        if(res.source){
            let icon = new AonIcon();
            icon.size = "40px";
            icon.classList.add("notificationPanelRowIcon");
            if(res.source == "DOCUMENTAL")
                icon.icon = "aon_new_documental";
            else if (res.source == "MESSENGER")
                icon.icon = "aon_new_messenger";
            else if (res.source == "COMUNICA")
                icon.icon = "aon_new_payroll";
            else if (res.source == "INVOICE")
                icon.icon = "aon_new_invoice";
            icon.color = "grey";
            icon.title = "Solicitudes";
            
            divGeneral.appendChild(icon);

        }else{
            let noti = this.createElement(TAG.SPAN);
            noti.className = "material-icons notificationPanelRowNoti";
            noti.innerHTML = "campaign";
            divGeneral.appendChild(noti);
        }

        let div = this.createDiv();
        div.classList.add("notificationPanelRowDiv");

        let subDiv1 = this.createDiv();
        subDiv1.classList.add("notificationPanelRowSubDiv1");
        subDiv1.title = res.title;
        subDiv1.innerHTML = res.title;

        let subDiv2 = this.createDiv();
        subDiv2.classList.add("notificationPanelRowSubDiv2");
        subDiv2.title = res.title;
        subDiv2.innerHTML = res.body;
        
        let subDiv3 = this.createDiv();
        subDiv3.classList.add("notificationPanelRowSubDiv3");
        subDiv3.innerHTML = "( "+firstLetters(this.formatDate(res))+" )";
        div.appendChild(subDiv1);   
        div.appendChild(subDiv3);
        div.appendChild(subDiv2);

        divGeneral.appendChild(div);

        let span = this.createElement(TAG.SPAN);
        span.className = "material-icons";
        span.classList.add("notificationPanelRowSpan");
        span.title = "Marcar como leído";
        span.innerHTML = "visibility_off";
        span.style.visibility = "hidden";

        divGeneral.appendChild(span);

        divPrincipal.appendChild(divGeneral);

        divPrincipal.addEventListener(EVENT.MOUSEOVER, () => {
            divPrincipal.style.backgroundColor = 'rgba(225, 225, 227, 1)';
            span.style.visibility = "visible";
        });

        divPrincipal.addEventListener(EVENT.MOUSELEAVE, () => {
            divPrincipal.style.backgroundColor = 'transparent';
            span.style.visibility = "hidden";
        });

        divPrincipal.addEventListener(EVENT.CLICK, () => {
            this.markReadNotification(res);
            divGeneral.style.display = "none";
            header.className = "aonHeader aonHeaderNotification";
            apps.className = "aonMenuLeftop aonMenuLeftopNotification";
            aonHeader.buildApp(NOTIFICATION);
            aonHeader.setVisibleLogo(false);
            aonHeader.setVisibleApp(true);
            this.goNotification(res);
        });
        
        span.addEventListener(EVENT.CLICK, (event) => {
            event.stopPropagation(); 
            this.markReadNotification(res);
            divGeneral.style.display = "none";
        });
        

        return divPrincipal;
    }

    formatDate(res) {
        const inputDate = new Date(res.date);
        const today = new Date();
        const todayStart = new Date(today.getFullYear(), today.getMonth(), today.getDate());
        const yesterday = new Date(todayStart);
        yesterday.setDate(todayStart.getDate() - 1);
    
        const abbreviatedMonths = ["Ene.", "Feb.", "Mar.", "Abr.", "May.", "Jun.", "Jul.", "Ago.", "Sep.", "Oct.", "Nov.", "Dic."];
    
        let dayMonth = AonDateUtils.getDayMonth(inputDate);
        dayMonth = dayMonth.split(' ');
        const day = dayMonth[0];
        const monthIndex = new Date(inputDate).getMonth();
        const month = abbreviatedMonths[monthIndex];
        dayMonth = `${day} de ${month}`;
    
        const inputDateStartOfDay = new Date(inputDate.getFullYear(), inputDate.getMonth(), inputDate.getDate());
    
        const differenceInTime = todayStart - inputDateStartOfDay;
        const differenceInDays = Math.ceil(differenceInTime / (1000 * 3600 * 24));
    
        if (differenceInDays < 7) {
            if (differenceInDays === 0) {
                return `${MSG.TODAY}, ${dayMonth}, ${AonDateUtils.setTime(inputDate)}`;
            }
            if (inputDateStartOfDay.toDateString() === yesterday.toDateString()) {
                return `${MSG.YESTERDAY}, ${dayMonth}, ${AonDateUtils.setTime(inputDate)}`;
            }
            return `${AonDateUtils.getDayStr(inputDate)}, ${dayMonth}, ${AonDateUtils.setTime(inputDate)}`;
        }
    
        if (inputDate.getFullYear() === today.getFullYear()) {
            return `${dayMonth}, ${AonDateUtils.setTime(inputDate)}`;
        }
    
        return `${dayMonth} de ${inputDate.getFullYear()}`;
    }
    
    

    
    async loadMore(reload) {
    
       // let application = this.getApplication();
    
      // application.startLoader();
        
        const datos = await this.getData();
    /*
        if (reload){
        this.DIV_GENERAL.innerHTML ="";
        } 
    */
        datos.forEach((res) => {
          this.getElement(this.DIV_GENERAL).appendChild(
            this.buildRow(res)
          );
        });
    
        //application.stopLoader();
    }

 
    getData() {
        return getNotification({page:1, perPage:30, status:"unread"});
    }

    markReadNotification(res) {
        if (res.status === 0) {
            markReadNotification(res);
            res.status = 1;
    
            let aonNotificationIcon = document.querySelector("aon-notification-icon");
            if (aonNotificationIcon) { 
                aonNotificationIcon.getTotalNotification();
            }
    
            this.checkIfAllRead();
        }
    }

    checkIfAllRead() {
    const notifications = this.getElementsByClassName("notificationPanelRowPrincipalDiv");

    let allRead = true;

    for (let notification of notifications) {
        if (notification.style.display !== "none") {
            allRead = false;
            break;
        }
    }

    if (allRead) {
        let divGeneral = this.getElement(this.DIV_GENERAL);
        divGeneral.innerHTML = ""; 
        divGeneral.appendChild(this.buildNoNotifications());
    }
}

    

    goNotification(data) {
        const aonComponent = NotificationUtils.getNotificationComponent(data);
        if(aonComponent){
          this.rootPanel(aonComponent);
        }
    }
    

    getApplication() {
        return document.querySelector(TAG.AON_APPLICATION);
    }
    


}
if(!window.customElements.get(TAG.AON_NOTIFICATION_PANEL)){
    window.customElements.define(TAG.AON_NOTIFICATION_PANEL, AonNotificationPanel);
}