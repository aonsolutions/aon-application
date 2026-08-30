import { AonIcon } from '../../components/aon-icon.js';
import {AonElement} from '../../components/AonElement.js';
import { CreateComponent } from '../../components/CreateComponent.js';
import { CONSTANT, CSS, EVENT, MSG, TAG } from '../../environments/environments.js';
import { getApp } from '../../services/app.js';
import { FirebaseService } from '../../services/firebaseService.js';
import { getTotalNotification, saveAuthDevice, deleteAuthDevice, getNotification, markReadNotification } from '../../services/service.js';
import { waitEl } from '../../services/utils.js';
import { createSpan } from '../../services/utilsComponents.js';
import { AonDateUtils } from '../utils/AonDateUtils.js';
import { AonNotification } from './aon-notification.js';
import { NotificationCreateComponent } from './createComponent.js';
import { NotificationUtils } from './utils/NotificationUtils.js';


export class AonNotificationIcon extends AonElement {

    AON_NOTIFICATION_ICON;
    BADGE;
    COUNT;
    color;
    AUTH_DEVICE;
    DIALOG;
    DIALOG_CONTENT;
    WINDOW_LISTENER;
    TIMEOUT;
    static get observedAttributes() {
        return [CONSTANT.BADGE];
    }
 
    attributeChangedCallback(name, oldValue, newValue) {}

	constructor () {
		super();
        this.COUNT = {
            notification: 0,
            messenger: 0
        }
		this.AON_NOTIFICATION_ICON = 'aonNotificationIcon';
        this.BADGE = this.AON_NOTIFICATION_ICON+ "Badge";
	}

    disconnectedCallback() {
        if(this.WINDOW_LISTENER){
            window.removeEventListener(EVENT.CLICK, this.WINDOW_LISTENER);
        }
    }

	connectedCallback () {
        this.build();
        this.observerListener();
	}

    build(){
        this.title  = MSG.NOTIFICATIONS;
        this.append(this.buildView());
        this.append(this.buildDialog());
    }

    observerListener(){
        window.addEventListener(EVENT.RECEIVED_NOTIFICATION, ()=>{
			this.getTotalNotification();
		});

        
        this.addEventListener(EVENT.CLICK, ()=> {
            if(this.isMobile()){
                this.goAonNotification()
            } else {
                this.openDialog();
            }
        });

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

    buildDialog(){
        const dialog = document.createElement(TAG.DIV);
        dialog.id = "notificationDialogE";
        dialog.className = CSS.AON_BOX_SHADOW;
        dialog.style = `
            cursor: default;
            position: absolute;
            z-index: 999;
            top: 41x;
            right: -5px;
            width: 250px;
            font-weight: 300;
            background: white;
            box-sizing: border-box;
            border-radius: 5px;
            animation-name: dropPanel;
            animation-iteration-count: 1;
            animation-timing-function: all;
            animation-duration: 0.75s;
            display: none;
            user-select: none;
        `;


        this.WINDOW_LISTENER = (ev) => {
            if(!this.contains(ev.target)){
                this.closeDialog();
            }
        };
        
        window.addEventListener(EVENT.CLICK, this.WINDOW_LISTENER);

        this.DIALOG = dialog;

        return dialog;
    }

    async changeBadge(){
        const notificationSpan = await waitEl("#"+this.AON_NOTIFICATION_ICON);
        notificationSpan.style.position = "relative";
        const total = this.getTotalCount();
        const badge = this.getElement(this.BADGE) || NotificationCreateComponent.createBadge(this.BADGE).element;
        if(total && total > 0){
            notificationSpan.appendChild(badge);   
        } else {
            badge.remove();
        }
    }

	async initializeFB()  {
		try{
            await waitEl("script[src*='firebase-messaging']");
			if(!this.isMobile()) { // initialize observer message firebase desk
				const firebaseSrv = new FirebaseService();
				let token = await firebaseSrv.getTokenFB();
				if (token) {
					window.tokenFCM = token;
					const messaging = firebaseSrv.getMessagingObject();
					messaging.onMessage(
						(payload) => firebaseSrv.pushNotification(payload),
						(err) => console.log(err)
					);
					this.AUTH_DEVICE = await saveAuthDevice({tokenFCM:token});
				}
			} 
		} catch(e){
            // console.log("e");
            console.log("Firebase not supported this browser. Probably no https, or no localhost ...) ");
        }
  	}
      
    async goAonNotification(){
        try {
            const aonNotification = new AonNotification();
            this.rootPanel(aonNotification);
        } catch(e){
            console.log(e);
        }
    }

    openDialog(){
        const dialog = this.DIALOG;
        dialog.innerHTML = "";
        
        const div = document.createElement(TAG.DIV);
        div.style = `
            display: flex;
            justify-content: center;
            align-items: center;
        `;
        div.className = CSS.AON_NOTIFICATION_CONTENT_BETA;
        div.onclick = (ev)=>{
            ev.preventDefault();
            ev.stopPropagation();
        }
        this.DIALOG_CONTENT = div;
        dialog.appendChild(div);

        const box = document.createElement(TAG.DIV);
        box.style = `
            width: 100%;
            transition: all 0.4s;

        `;

        div.appendChild(box);

        const header = document.createElement(TAG.DIV);
        header.style = `
            width: 100%;
            display: flex;
            justify-content: space-between;
            padding:0.6em;
            margin:0;
        `;
        box.appendChild(header);
  
        const pHeader = document.createElement('p');
        pHeader.style = `padding:0;margin:0;color:#5F6368;font-weight: 700;`;
        pHeader.innerHTML = MSG.NOTIFICATIONS;
        header.appendChild(pHeader);

        const aHeader = document.createElement(TAG.A);
        aHeader.style = `
        text-decoration: none;
        padding:0;margin:0;
        color: rgba(0, 0, 0, 0.61);
        font-size:12px;
        cursor:pointer;`;
        aHeader.innerHTML = MSG.SEE_ALL;
        aHeader.title = MSG.SEE_ALL;
        aHeader.addEventListener(EVENT.CLICK, ()=> {
            this.closeDialog();
            this.goAonNotification()
        });
        header.appendChild(aHeader);

        const content = document.createElement(TAG.DIV);
        box.appendChild(content);

        content.innerHTML = "Cargando...";

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

        this.open();
    }

    open(){
        this.DIALOG.style.display = 'block';
    }

    closeDialog(){
		this.DIALOG.style.display = 'none';
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

    buildRowEmpty(parent) {
        const notification  = document.createElement(TAG.DIV);
        notification.style = `padding: 0.5em 0.6em;`;
        notification.innerHTML = "No existen notificaciones pendientes.";
        parent.appendChild(notification);
    }

    goNotification(data) {
        const aonComponent = NotificationUtils.getNotificationComponent(data);
        if(aonComponent){
            this.closeDialog();
            this.rootPanel(aonComponent);
        }
    }

    getData() {
        return getNotification({page:1, perPage:10, status:"unread"});
    }

    async getTotalNotification(){
        clearTimeout(this.TIMEOUT);
        this.TIMEOUT = setTimeout(async()=>{
            this.COUNT = await getTotalNotification();
            this.changeBadge();
        }, 500);
    }

    markReadNotification(data){
        markReadNotification(data)
        .then(()=>{
            this.getTotalNotification();
        });
    }
    
    deleteToken(){
        const tokenFCM = window.tokenFCM;
        if(tokenFCM){
            deleteAuthDevice({tokenFCM}).then(console.log).catch(console.log);
        }
    }

    getTotalCount(){
        return Object.keys(this.COUNT).reduce((acc, value) => acc + this.COUNT[value], 0);
    }
}
window.customElements.define(TAG.AON_NOTIFICATION_ICON,  AonNotificationIcon);