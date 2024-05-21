import { AonElement } from 'aonsolutions/components/AonElement.js';
import { AonIcon } from 'aonsolutions/components/aon-icon.js';
import { AonNotification } from 'aonsolutions/modules/notification/aon-notification.js';
import { CreateComponent } from 'aonsolutions/components/CreateComponent.js';
import { CONSTANT, EVENT, MSG, TAG } from 'aonsolutions/environments/environments.js';
import { getApp } from 'aonsolutions/services/app.js';
import { getTotalNotification, saveAuthDevice, deleteAuthDevice, getNotification, markReadNotification } from 'aonsolutions/services/service.js';
import { createSpan } from 'aonsolutions/services/utilsComponents.js';
import { AonDateUtils } from 'aonsolutions/modules/utils/AonDateUtils.js';


export class AonNotificationPanel extends AonElement {

	AON_NOTIFICATION_ICON

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
		this.AON_NOTIFICATION_ICON = 'aonNotificationIconPanel';
	}

	build() {
		let div = this.createDiv();
		div.innerHTML = 'VER TODAS';
		div.addEventListener(EVENT.CLICK, () => this.goAonNotification());
		this.appendChild(div);
		this.append(this.buildView());

	}

	buildRow(parent, data) {

        const title = data.title;
        const body = (data.body || "").replace(/<[^>]+>|&nbsp;|\n/g, " ");
        const source = data.source;

        const notification  = document.createElement(TAG.DIV);
        notification.className = 'aonAppLi';
        notification.style = `
            padding: 0.3em 0.6em;
            display: flex;
            align-items: center;
            justify-content: space-between;
            cursor:pointer;
        `;

        notification.addEventListener(EVENT.CLICK, ()=>{
            this.markReadNotification(data);
            this.goNotification(data);
        })

        parent.appendChild(notification);

        let aonApp = { icon:'aon_app', color: '#002469', title:"" };

        try{
            const isComunica = (title || "").toLowerCase().includes("comunic");
            const app = isComunica ? "COMUNICA" : source;
    
            if(app){
                const appTmp = getApp(app);
                if(appTmp){
                    aonApp = appTmp;
                }
            }
        } catch(err){
            console.error(err);
        }
     
        const aonIcon = new AonIcon();
        aonIcon.icon = aonApp.icon;
        aonIcon.color = aonApp.color;
        aonIcon.title = aonApp.title;
        notification.appendChild(aonIcon);

        const text = document.createElement(TAG.DIV);
        text.style = `
         width:70%;
         font-size: 0.8em;
         display: flex;
         flex-direction: column;
         row-gap: 4px;
        `;
        notification.appendChild(text);
   
        const titleText = document.createElement(TAG.DIV);
        titleText.style = `font-weight: 700;`;
        titleText.innerHTML = title;
        titleText.title = title;
        text.appendChild(titleText);

        const contentText = document.createElement(TAG.DIV);
        contentText.style = `
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;`;
        contentText.innerHTML = body;
        contentText.title = body;
        text.appendChild(contentText);

        const time = document.createElement(TAG.DIV);
        time.style = `
        font-size: 0.8em;
        color: rgba(0, 0, 0, 0.61);`;
        time.innerHTML = AonDateUtils.setDateTimestampDay(new Date(data.date));
        text.appendChild(time);


        const icon = document.createElement(TAG.SPAN);
        icon.className = "material-icons";
        icon.textContent = "clear";
        icon.style = `
            font-size: 11px;
            cursor: pointer;
        `;
        icon.addEventListener(EVENT.CLICK, (ev) => {
            ev.preventDefault();
            ev.stopPropagation();
            this.markReadNotification(data);
            notification.remove();
        })
        notification.appendChild(icon);
    }

	getInfo(){
		this.getData()
        .then(notifications=>{
            content.innerHTML = "";
            if(notifications.length > 0){
                notifications
                .forEach(notification=>
                    this.buildRow(content, notification)
                )
            } else {
                this.buildRowEmpty(content);
            }
        });
	}

	buildRowEmpty(parent) {
        const notification  = document.createElement(TAG.DIV);
        notification.style = `padding: 0.5em 0.6em;`;
        notification.innerHTML = "No existen notificaciones pendientes.";
        parent.appendChild(notification);
    }

	buildView(){
        const notificationSpan = createSpan({id:this.AON_NOTIFICATION_ICON}).element;
        CreateComponent.createAonIconButton({
            attributes:{
                id:  "aonHeaderNotificationButton",
                icon: "notifications",
                noHover: true,
                color: this.color || '#5f6368'
            }
        }, notificationSpan);
        return notificationSpan;
    }

	getData() {
        return getNotification({page:1, perPage:10, status:"unread"});
    }


}
if(!window.customElements.get(TAG.AON_NOTIFICATION_PANEL)){
	window.customElements.define(TAG.AON_NOTIFICATION_PANEL, AonNotificationPanel);
}
