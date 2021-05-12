import { AonIconButton } from '../../components/aon-icon-button.js';
import {AonElement} from '../../components/AonElement.js';
import { CONSTANT, EVENT } from '../../environments/environments.js';
import { FirebaseService } from '../../services/firebaseService.js';
import { getTotalNotification } from '../../services/service.js';
import { waitEl } from '../../services/utils.js';
import { AonNotification } from './aon-notification.js';
import { createBadge, createSpan } from './createComponent.js';

export class AonNotificationIcon extends AonElement {

    AON_NOTIFICATION_ICON;
    BADGE;
    COUNT;
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

	connectedCallback () {
        this.build();
        this.observerListener();
	}
    

    build(){
        this.append(this.getView());
    }


    getView(){
        const aonIconButton = new AonIconButton();
        aonIconButton.id = "aonHeaderNotificationButton";
        aonIconButton.icon = "notifications";
        const notificationSpan = createSpan({id:this.AON_NOTIFICATION_ICON});
        notificationSpan.appendChild(aonIconButton);
        return notificationSpan.element;
    }

    async changeBadge(){
        const notificationSpan = await waitEl("#"+this.AON_NOTIFICATION_ICON);
        const total = this.getTotalCount();
        const badge = this.getElement(this.BADGE)  ||  createBadge(this.BADGE).element;
        if(total && total > 0){
            badge.textContent = total;
            notificationSpan.appendChild(badge);   
        } else {
            badge.remove();
        }
    }

    observerListener(){
		window.addEventListener(EVENT.USER_AUTH, ()=>{
			this.initializeFB();
            this.getTotalNotification();
		});

        window.addEventListener(EVENT.RECEIVED_NOTIFICATION, ()=>{
			this.getTotalNotification();
		});

        this.addEventListener(EVENT.CLICK, ()=>this.goAonNotification());
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
                    let aonModule = this.getModule();
					if(aonModule) aonModule.saveTokenFcm(token);
				}
			} 
		} catch(e){}
  	}

    async goAonNotification(){
        try {
            if(!this.isBeta()) return true;
            const aonNotification = new AonNotification();
            this.rootPanel(aonNotification);
        } catch(e){
            console.log(e);
        }
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

}
window.customElements.define('aon-notification-icon',  AonNotificationIcon);