import { AonDialogMenu } from '../../components/aon-dialog-menu.js';
import {AonElement} from '../../components/AonElement.js';
import { CreateComponent } from '../../components/CreateComponent.js';
import { CONSTANT, EVENT, TAG } from '../../environments/environments.js';
import { FirebaseService } from '../../services/firebaseService.js';
import { getTotalNotification, saveAuthDevice, deleteAuthDevice } from '../../services/service.js';
import { waitEl } from '../../services/utils.js';
import { createSpan } from '../../services/utilsComponents.js';
import { AonNotification } from './aon-notification.js';
import { NotificationCreateComponent } from './createComponent.js';

export class AonNotificationIcon extends AonElement {

    AON_NOTIFICATION_ICON;
    BADGE;
    COUNT;
    color;
    AUTH_DEVICE;
    DIALOG;
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
        this.append(this.buildView());
        this.append(this.buildDialog());
    }

    observerListener(){
        window.addEventListener(EVENT.RECEIVED_NOTIFICATION, ()=>{
			this.getTotalNotification();
		});

        this.addEventListener(EVENT.CLICK, ()=> {
            // if(this.isMobile()){
                this.goAonNotification()
            // } else {
            //     this.openDialog();
            // }
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
        this.DIALOG = new AonDialogMenu();
        this.DIALOG.id = 'notificationDialogE';
        return this.DIALOG;
    }

    async changeBadge(){
        const notificationSpan = await waitEl("#"+this.AON_NOTIFICATION_ICON);
        notificationSpan.style.position = "relative";
        const total = this.getTotalCount();
        const badge = this.getElement(this.BADGE) || NotificationCreateComponent.createBadge(this.BADGE).element;
        if(total && total > 0){
            // badge.textContent = total;
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
            console.log(e);
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
        const top = this.getBoundingClientRect().top;
        const left = this.getBoundingClientRect().left;
        // options = options.map(({ aonIcon, icon, name, fn }) => {
        //   return {
        //     aonIcon,
        //     icon,
        //     name,
        //     fn: () => fn(ev),
        //   };
        // });
    
        dialog.setContent(null, top, left);
        dialog.open();
    }

    async getTotalNotification(){
        this.COUNT = await getTotalNotification();
        this.changeBadge();
    }
    
    deleteToken(){
        let tokenFCM = window.tokenFCM;
        if(tokenFCM){
            deleteAuthDevice({tokenFCM}).then(console.log).catch(console.log);
        }
    }

    getTotalCount(){
        return Object.keys(this.COUNT).reduce((acc, value) => acc + this.COUNT[value], 0);
    }

}
window.customElements.define(TAG.AON_NOTIFICATION_ICON,  AonNotificationIcon);