import { AonDialog } from '../../components/aon-dialog.js';
import { AonIconButton } from '../../components/aon-icon-button.js';
import {AonElement} from '../../components/AonElement.js';
import { FirebaseService } from '../../services/firebaseService.js';
import { getNotification } from '../../services/notificationService.js';
import { waitEl } from '../../services/utils.js';
import { AonNotification } from './aon-notification.js';

export class AonNotificationIcon extends AonElement {

    AON_NOTIFICATION_ICON;
    BADGE;
    NOTIFICATIONS;
    static get observedAttributes() {
        return ["badge"];
    }
    
	get badge() {
		return this.getAttribute('badge') ? parseInt(this.getAttribute('badge')) : 0;
	}

	set badge(badge) {
		this.setAttribute('badge', badge);
	}
    attributeChangedCallback(name, oldValue, newValue) {
        if ("badge" === name) this.changeBadge();
    }

	constructor () {
		super();
		this.AON_NOTIFICATION_ICON = 'aonNotificationIcon';
        this.BADGE = this.AON_NOTIFICATION+ "Badge";
        this.NOTIFICATIONS = [];
	}

	connectedCallback () {
        this.build();
        this.observerListener();
	}
    

    build(){
        this.append(this.getView());
        this.getNotifications();
    }


    getView(){
        const notificationSpan = this.createElement("span");
        notificationSpan.id = this.AON_NOTIFICATION_ICON;
        const aonIconButton = new AonIconButton();
        // aonIconButton.noHover = "true";
        aonIconButton.id = "aonHeaderNotificationButton";
        aonIconButton.icon = "notifications";
        notificationSpan.appendChild(aonIconButton);
        return notificationSpan;
    }

    async changeBadge(){
        const badge = this.getElement(this.BADGE)  || this.createElement("span");
        if(this.badge && this.badge > 0){
            const notificationSpan = await waitEl("#"+this.AON_NOTIFICATION_ICON);
            badge.textContent = this.badge;
            badge.id = this.BADGE;
            badge.style = /**/`position: absolute; top: -1px;right: 3px;padding: 1px 4px;border-radius: 50%;background: red;color: white;font-size: 10px;font-weight: 800;`;
            notificationSpan.appendChild(badge);   
        } else {
            badge.remove();
        }
    }

    observerListener(){
		window.addEventListener('userAuth', ()=>{
			this.initializeFB();
		});

        window.addEventListener('receivedNotification', ({detail})=>{
			this.getNotifications();
            this.showNotificationTest(detail);
		});

        this.addEventListener('click', (ev)=>this.goAonNotification());
	}


	async initializeFB()  {
		try{
			let token = undefined;
			if(!this.isMobile()) { // initialize observer message firebase desk
				const firebaseSrv = new FirebaseService();
				token = await firebaseSrv.getTokenFB();
				if (token) {
					window.tokenFCM = token;
					const messaging = firebaseSrv.getMessagingObject();
					messaging.onMessage(
						(payload) => firebaseSrv.pushNotification(payload),
						(err) => console.log(err)
					);
                    let aonModule = document.querySelector('aon-module');
					if(aonModule) aonModule.saveTokenFcm(token);
				}
			} 
		} catch(e){}
  	}

    async goAonNotification(){
        if(!this.isBeta()) return true;
        const aonNotification = new AonNotification();
        let data = [];
        try {
            const datos = await this.getNotifications();
            if(datos)aonNotification.data = JSON.stringify(datos);
            this.rootPanel(aonNotification)
        } catch(e){
            console.log(e);getNotifications
        }
        return data;
    }

    async getNotifications(){
        if(this.isBeta()){
            this.NOTIFICATIONS = await getNotification();
            this.badge = this.NOTIFICATIONS.length;
        }
        return this.NOTIFICATIONS;
    }


    showNotificationTest(data){
        if(data){
            console.log("data", data);
            const id = 'aonDialogNotify';
            const dialog = this.getElement(id) || new AonDialog();
            dialog.id = id;
            if(dialog){ this.appendChild(dialog);}
            dialog.clear();
            dialog.setContentHTML(data.body);
            if (!this.isMobile()) dialog.width = '400px';
            dialog.setTitle(data.title);
            dialog.open();
            dialog.addAcceptAction(() => {
                this.goAonNotification();
            });
        }
    }

}
window.customElements.define('aon-notification-icon',  AonNotificationIcon);