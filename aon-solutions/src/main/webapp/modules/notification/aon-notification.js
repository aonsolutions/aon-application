import {AonElement} from '../../components/AonElement.js';
import { AonCard } from '../../components/aon-card.js';
import { setFullDate, setTime } from '../../services/utils.js';
import { firstLetters } from '../signin/time-control/utils.js';
import { AonTabs } from '../../components/aon-tabs.js';
import { AonSwitch } from '../../components/aon-switch.js';

export class AonNotification extends AonElement {

	AON_NOTIFICATION;
    static get observedAttributes() {
        return ["data"];
    }
    
	get data() {
		return this.getAttribute('data') ? JSON.parse(this.getAttribute('data')) : null;
	}

	set data(data) {
		this.setAttribute('data', data);
	}

    attributeChangedCallback(name, oldValue, newValue) {
    }

	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
    	this.build();
 	}

	initialize(){
		this.AON_NOTIFICATION = 'aonNotification';
        this.aonNotifyIconEl = document.querySelector('aon-notification-icon');
	}

 	async build() {
        if(this.isMobile()){
            this.paintMobile();
        } else {
            this.paintDesk();
        }
        this.notificationView();
	}

    paintDesk(){
        this.appendChild(this.createContentDiv());
    }

    async paintMobile(){
        const aonTabs = new AonTabs();
        aonTabs.addEventListener('change', ({detail})=>{
            if(detail){
                console.log("position", detail.position);
            }
        })
        aonTabs.setButtons([
            {
                name:"Notificationes",
                id:"notification",
                icon: "notifications",
            },
            {
               name:"Solicitudes",
               id:"solicitudes",
               icon: "assignment",
            },
            // {
            //     name:"Facturas",
            //     id:"invoice",
            //     icon: "report",
            //  }
        ]);

        this.appendChild(aonTabs);

        this.appendChild(this.createContentDiv())

        aonTabs.updateBadge([
           {
               id:"notification",
               badge: 2,
           },
           {
              id:"solicitudes",
              badge: 1,
           }
       ]);

    //    this.createSwitchButton(aonTabs);
    }

    createContentDiv(){
        const div = this.getElement(this.AON_NOTIFICATION) || this.createElement("div");
        div.id = this.AON_NOTIFICATION;
        div.style.margin = "auto";
        div.style.marginTop = "21px";
        div.style.width = "80%";
        return div;
    }


    async createSwitchButton(aonTabs){
        const tabsMenu = await aonTabs.getContent();
        const div = this.createElement('div');
        div.style.marginTop = "15px";
        div.style.color = "grey !important";
        div.style.fontSize = "12px";
        div.style.fontWeight= "500";
        const aonSwitch = new AonSwitch();
        aonSwitch.addEventListener("change", ({target})=>{
            console.log(target.checked);
        });
        aonSwitch.title = "Todas";
        div.appendChild(aonSwitch);
        tabsMenu.appendChild(div);
        const titleSwitch = this.getElement(aonSwitch.TITLE);
        if(titleSwitch)titleSwitch.style.color = "grey";
    }

	async notificationView(){
        const datos = this.data;
        if(datos.length > 0){
            datos.map((data,idx)=>{
                const aonCard = this.createCard(data);
                aonCard.flex = "true";
                aonCard.addEventListener("click",(ev)=>{
                    this.goInfo(data);
                });
                const spanContent = this.createElement('span');
                spanContent.style = "font-size: 14px;font-family: Times New Roman, Times, serif; word-wrap: break-word;";
                spanContent.innerHTML = `${data.body}`;
                aonCard.setContent(spanContent);
                const divFooter = this.createElement('div');
                divFooter.style.display ="flex";
                divFooter.style.marginTop ="10px";
                divFooter.style.fontWeight ="800";
                divFooter.style.fontSize = "10px";
                const div1 = this.createElement("div");
                div1.innerHTML = firstLetters(setFullDate(data.date))+" "+setTime(data.date);
                div1.style.marginLeft = "auto";
                divFooter.appendChild(div1);
                aonCard.setContent(divFooter);
            });
        } else {
         this.createCard({id:0,title:"No existen notificaciones pendientes"});
        }
	}


    goInfo(data){
        let aonCard = this.getElement(this.AON_NOTIFICATION+"Card"+data.id);
        if(aonCard){
            console.log(data);
        }
    }
    /**
     * 
     * @param {title, id} data 
     * @returns 
     */
    createCard(data){
        const aonNotificationEl = this.getElement(this.AON_NOTIFICATION);
        const aonCard = new AonCard();
        aonCard.id = this.AON_NOTIFICATION+"Card"+data.id;
        aonCard.style.cursor="pointer";
        let title = `<span class="aonColorPrimary">${data.title}</span>`;
        if(!data.read) title = /*html*/`<span style="color:red;" class="material-icons">error</span>${title}` ;
        aonCard.title = title;
        aonNotificationEl.appendChild(aonCard);
        if(!data.read) aonCard.setBackground(`rgb(0, 36, 105, 0.1)`);
        const label = this.createElement('label');
        label.textContent = "×";
        label.style = 'float: right;margin-top: -23px;margin-right: -19px;cursor: pointer;padding: 10px;';
        label.addEventListener('click',(ev)=> this.removeFadeOut(aonCard, 600))
        aonCard.getCardTitle().appendChild(label);
        aonCard.getCard().classList.add('aonCardFlex');
        return aonCard;
    }

     removeFadeOut( el, speed ) {
        if(el){
            const seconds = speed/1000;
            const divCard = el.getCard();
            divCard.style.transition = "opacity "+seconds+"s ease";
            divCard.style.opacity = 0;
            setTimeout(()=> {
                el.parentNode.removeChild(el);
                this.changeBadge(-1)
            }, speed);
        }
    }

    deleteAll(){
        let aonNotify = this.aonNotifyIconEl;
        if(aonNotify){
            aonNotify.NOTIFICATIONS.map(notification=>{
                let el = this.getElement(this.AON_NOTIFICATION+"Card"+notification.id);
                this.removeFadeOut(el, 600);
            });
        }

    }

    changeBadge(number){
        let aonNotify = this.aonNotifyIconEl;
        if(aonNotify){
            aonNotify.badge = aonNotify.badge +number;
        }
    }

}
window.customElements.define('aon-notification', AonNotification);
