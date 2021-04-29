import { AonDialog } from '../../components/aon-dialog.js';
import { AonIconButton } from '../../components/aon-icon-button.js';
import {AonElement} from '../../components/AonElement.js';
import { CONSTANT, EVENT } from '../../environments/environments.js';
import { FirebaseService } from '../../services/firebaseService.js';
import { getTotalNotification } from '../../services/service.js';
import { waitEl } from '../../services/utils.js';
import { AonNotification } from './aon-notification.js';

export class AonNotificationIcon extends AonElement {

    AON_NOTIFICATION_ICON;
    BADGE;
    COUNT;
    static get observedAttributes() {
        return [CONSTANT.BADGE];
    }
 
    attributeChangedCallback(name, oldValue, newValue) {
    }

	constructor () {
		super();
        this.COUNT = {
            notification: 0,
            messenger: 0
        }
		this.AON_NOTIFICATION_ICON = 'aonNotificationIcon';
        this.BADGE = this.AON_NOTIFICATION_ICON+ "Badge";
	}

	connectedCallback () {
        this.build();
        this.observerListener();
	}
    

    build(){
        this.append(this.getView());
        this.getTotalNotification();
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
        const notificationSpan = await waitEl("#"+this.AON_NOTIFICATION_ICON);
        const total = this.getTotalCount();
        const badge = this.getElement(this.BADGE)  || this.createElement("span");
        badge.id = this.BADGE;
        if(total && total > 0){
            badge.textContent = total;
            badge.style = /**/`position: absolute; top: 22px;right: 3px;padding: 1px 4px;border-radius: 50%;background: red;color: white;font-size: 10px;font-weight: 800;`;
            notificationSpan.appendChild(badge);   
        } else {
            badge.remove();
        }
    }

    observerListener(){
		window.addEventListener('userAuth', ()=>{
			this.initializeFB();
		});

        window.addEventListener(EVENT.RECEIVED_NOTIFICATION, ({detail})=>{
			this.getTotalNotification();
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
            this.rootPanel(aonNotification);
        } catch(e){
            console.log(e);
        }
        return data;
    }

    async getTotalNotification(){
        if(this.isBeta()){
            this.COUNT = await getTotalNotification();
            this.changeBadge();
        }
    }

    getTotalCount(){
        return Object.keys(this.COUNT).reduce((acc, value) => acc + this.COUNT[value], 0);
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