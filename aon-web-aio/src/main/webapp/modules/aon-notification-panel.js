import { AonElement } from 'aonsolutions/components/AonElement.js';
import { AonIcon } from 'aonsolutions/components/aon-icon.js';
import { AonNotification } from 'aonsolutions/modules/notification/aon-notification.js';
import { CreateComponent } from 'aonsolutions/components/CreateComponent.js';
import { CONSTANT, EVENT, MSG, TAG, MATERIAL_ICONS } from 'aonsolutions/environments/environments.js';
import { NOTIFICATION } from  "../services/app.js";
import { getApp } from 'aonsolutions/services/app.js';
import { getTotalNotification, saveAuthDevice, deleteAuthDevice, getNotification, markReadNotification } from 'aonsolutions/services/service.js';
import { AonDateUtils } from "aonsolutions/modules/utils/AonDateUtils.js";
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
    }

    build() {
        let header = this.getElement("aonHeaderWeb");
        let apps = this.getElement("aonMenuLeftop-applications");
        let aonHeader = this.getElement("aonHeader");

        let divGeneral = this.createDiv();
        divGeneral.id = this.DIV_GENERAL;
        divGeneral.classList.add("notificationPanelGeneralDiv");

        let divTodas = this.createDiv();
        divTodas.innerHTML = MSG.SEE_ALL.toUpperCase();
        divTodas.classList.add("notificationPanelAllDiv");

        divGeneral.appendChild(divTodas);
        divTodas.addEventListener(EVENT.CLICK, () =>  {
            this.goAonNotification()
            header.className = "aonHeader aonHeaderNotification";
            apps.className = "aonHeader aonMenuLeftopNotification";
            aonHeader.buildApp(NOTIFICATION);
        });

        this.loadMore(true);
        
        this.appendChild(divGeneral);
    }

    buildRow(res){
        let divPrincipal = this.createDiv();
        divPrincipal.classList.add("notificationPanelRowPrincipalDiv");

        let divGeneral = this.createDiv();
        divGeneral.classList.add("notifcationPanelRowGeneralDiv");

        let icon = new AonIcon();
        icon.icon = "aon_new_messenger";
        icon.color = "#1fd8b9";
        icon.title = "Solicitudes";
        icon.size = "50px";
        icon.classList.add("notificationPanelRowIcon");
        
        divGeneral.appendChild(icon);

        let div = this.createDiv();
        div.classList.add("notificationPanelRowDiv");

        let subDiv1 = this.createDiv();
        subDiv1.classList.add("notificationPanelRowSubDiv1");
        subDiv1.title = res.title;
        subDiv1.innerHTML = res.title;
        div.appendChild(subDiv1);

        let subDiv2 = this.createDiv();
        subDiv2.classList.add("notificationPanelRowSubDiv2");
        subDiv2.title = res.body;
        subDiv2.innerHTML = res.body;
        div.appendChild(subDiv2);

        let subDiv3 = this.createDiv();
        subDiv3.classList.add("notificationPanelRowSubDiv3");
        subDiv3.innerHTML = firstLetters(AonDateUtils.setFullDate(res.date)) + " " + AonDateUtils.setTime(res.date);
        div.appendChild(subDiv3);

        divGeneral.appendChild(div);

        let span = this.createElement(TAG.SPAN);
        span.className = "material-icons";
        span.classList.add("notificationPanelRowSpan");
        span.innerHTML = "delete";

        divGeneral.appendChild(span);

        divPrincipal.appendChild(divGeneral);

        divPrincipal.addEventListener(EVENT.MOUSEOVER, () => {
            divPrincipal.style.backgroundColor = 'rgba(225, 225, 227, 1)';
        });

        divPrincipal.addEventListener(EVENT.MOUSELEAVE, () => {
            divPrincipal.style.backgroundColor = 'transparent';
        });

        return divPrincipal;
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
        return getNotification({page:1, perPage:10, status:"read"});
    }

    getApplication() {
        return document.querySelector(TAG.AON_APPLICATION);
      }
    


}
if(!window.customElements.get(TAG.AON_NOTIFICATION_PANEL)){
    window.customElements.define(TAG.AON_NOTIFICATION_PANEL, AonNotificationPanel);
}