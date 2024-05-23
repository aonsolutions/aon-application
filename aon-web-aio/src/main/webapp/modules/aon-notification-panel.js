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
        divGeneral.style.marginTop = "-40px";

        let divTodas = this.createDiv();
        divTodas.innerHTML = 'VER TODAS';
        divTodas.style.cursor = 'pointer';
        divTodas.style.position = 'relative';
        divTodas.style.padding = '9px';
        divTodas.style.width = '97px';
        divTodas.style.top = '-20px';
        divTodas.style.left = '172px';

        divTodas.addEventListener(EVENT.MOUSEOVER, () => {
			divTodas.style.backgroundColor = 'rgba(225, 225, 227, 1)';
		});
        divTodas.addEventListener(EVENT.MOUSELEAVE, () => {
			divTodas.style.backgroundColor = 'transparent';
		});

        divGeneral.appendChild(divTodas);
		divTodas.addEventListener(EVENT.CLICK, () => this.goAonNotification());

        this.loadMore(true);
        
		this.appendChild(divGeneral);
	}

    buildRow(res){
        let divPrincipal = this.createDiv();

        let divGeneral = this.createDiv();
        divGeneral.style.className = 'aonAppLi';
        divGeneral.style.margin = "0.3rem 0.6rem";
        divGeneral.style.display = "flex";
        divGeneral.style.justifyContent = "space-between";
        divGeneral.style.cursor = "pointer";
        divGeneral.style.borderBottom = "1px solid rgba(225, 225, 227, 1)";

        let icon = new AonIcon();
        icon.icon = "aon_new_messenger";
        icon.color = "#1fd8b9";
        icon.title = "Solicitudes";
        icon.size = "50px";
        icon.style.marginTop = "5px";
        
        divGeneral.appendChild(icon);

        let div = this.createDiv();
        div.style.width = "70%";
        div.style.fontSize = "0.8rem";
        div.style.display = "flex";
        div.style.flexDirection = "column";
        div.style.rowGap = "4px";

        let subDiv1 = this.createDiv();
        subDiv1.style.fontWeight = '700';
        subDiv1.title = res.title;
        subDiv1.innerHTML = res.title;
        div.appendChild(subDiv1);

        let subDiv2 = this.createDiv();
        subDiv2.style.overflow = "hidden";
        subDiv2.style.whiteSpace = "nowrap";
        subDiv2.style.textOverflow = "ellipsis";
        subDiv2.title = res.body;
        subDiv2.innerHTML = res.body;
        div.appendChild(subDiv2);

        let subDiv3 = this.createDiv();
        subDiv3.style.fontSize = "0.8rem";
        subDiv3.style.color = "rgba (0,0,0,0.61)";
        subDiv3.innerHTML = firstLetters(AonDateUtils.setFullDate(res.date)) + " " + AonDateUtils.setTime(res.date);
        div.appendChild(subDiv3);

        divGeneral.appendChild(div);

        let span = this.createElement(TAG.SPAN);
        span.className = "material-icons";
        span.style.fontSize = "20px";
        span.style.cursor = "pointer";
        span.style.position = "relative";
        span.style.top = "18px";
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
