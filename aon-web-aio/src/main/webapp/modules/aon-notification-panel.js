import { AonElement } from 'aonsolutions/components/AonElement.js';
import { AonIcon } from 'aonsolutions/components/aon-icon.js';
import { AonNotification } from 'aonsolutions/modules/notification/aon-notification.js';
import { CreateComponent } from 'aonsolutions/components/CreateComponent.js';
import { CONSTANT, EVENT, MSG, TAG, MATERIAL_ICONS } from 'aonsolutions/environments/environments.js';
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
		let divGeneral = this.createDiv();
        divGeneral.id = this.DIV_GENERAL;
        divGeneral.className = "notificationPanelGeneralDiv";

        let divTodas = this.createDiv();
        divTodas.innerHTML = MSG.SEE_ALL.toUpperCase();
        divTodas.className = "notificationPanelAllDiv";
        divGeneral.appendChild(divTodas);
		divTodas.addEventListener(EVENT.CLICK, () => this.goAonNotification());

        this.loadMore(true);
        
		this.appendChild(divGeneral);
	}

    buildRow(res){
        let divPrincipal = this.createDiv();

        let divGeneral = this.createDiv();
        divGeneral.style.className = 'aonAppLi notifcationPanelRowGeneralDiv';

        let icon = new AonIcon();
        icon.icon = "aon_new_messenger";
        icon.className= 'notificationPanelRowIcon';
        divGeneral.appendChild(icon);

        let div = this.createDiv();
        div.className = 'notificationPanelRowDiv';

        let subDiv1 = this.createDiv();
        subDiv1.className = 'notificationPanelRowSubDiv1';
        subDiv1.innerHTML = res.title;
        div.appendChild(subDiv1);

        let subDiv2 = this.createDiv();
        subDiv2.className = 'notificationPanelRowSubDiv2';
        subDiv2.innerHTML = res.body;
        div.appendChild(subDiv2);

        let subDiv3 = this.createDiv();
        subDiv3.className = 'notificationPanelRowSubDiv3';
        subDiv3.innerHTML = firstLetters(AonDateUtils.setFullDate(res.date)) + " " + AonDateUtils.setTime(res.date);
        div.appendChild(subDiv3);

        divGeneral.appendChild(div);

        let span = this.createElement(TAG.SPAN);
        span.className = "material-icons notificationPanelRowSpan";
        span.innerHTML = "delete";

        divGeneral.appendChild(span);

        divPrincipal.appendChild(divGeneral);

        divPrincipal.className = 'notificationPanelRowPrincipalDiv';

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
