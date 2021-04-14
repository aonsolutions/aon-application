import {AonElement} from '../../components/AonElement.js';
import { AonCard } from '../../components/aon-card.js';
import { setFullDate, setTime } from '../../services/utils.js';
import { firstLetters } from '../signin/time-control/utils.js';

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
	}

 	build() {
        this.innerHTML = /*html*/`<div id ="${this.AON_NOTIFICATION}"></div>`;
        this.paintView();
	}

	async paintView(){
        this.getElement(this.AON_NOTIFICATION).style = "margin: auto;width: 80%;";
        const datos = this.data;
        if(datos.length > 0){
            datos.map((data,idx)=>{
                let aonCard = this.createCard(data);
                aonCard.flex = "true";
                aonCard.addEventListener("click",(ev)=>{
                    this.goInfo(data);
                });
                // aonCard.addEventListener("dragend", (event) => {
                //     event.preventDefault();
                //     console.log("dragover");
                // });
            
                let spanContent = this.createElement('span');
                spanContent.style = "font-size: 14px;font-family: Times New Roman, Times, serif; word-wrap: break-word;";
                spanContent.innerHTML = `${data.body}`;
                aonCard.setContent(spanContent);
                const divFooter = this.createElement('div');
                divFooter.style.display ="flex";
                divFooter.style.marginTop ="10px";
                divFooter.style.fontWeight ="800";
                divFooter.style.fontSize = "10px";
                const div1 = this.createElement("div");
                div1.innerHTML =  firstLetters(setFullDate(data.date));
                const div2 = this.createElement("div");
                div2.innerHTML = setTime(data.date);
                div2.style.marginLeft = "auto";
                divFooter.appendChild(div1);
                divFooter.appendChild(div2);
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
        let title = data.title;
        if(!data.read) title = /*html*/`<span style="color:red;" class="material-icons">report</span>${data.title}` ;
        aonCard.title = title;
        aonNotificationEl.appendChild(aonCard);
        if(!data.read) aonCard.setBackground(`rgb(0, 36, 105, 0.1)`);
        const label = this.createElement('label');
        label.textContent = "×";
        label.style = 'float: right;margin-top: -23px;margin-right: -19px;cursor: pointer;padding: 10px;';
        label.addEventListener('click',(ev)=> this.removeFadeOut(aonCard, 1000))
        aonCard.getCardTitle().appendChild(label);
        aonCard.getCard().classList.add('aonCardFlex');
        return aonCard;
    }

     removeFadeOut( el, speed ) {
        let seconds = speed/1000;
        let divCard = el.getCard();
        divCard.style.transition = "opacity "+seconds+"s ease";
        divCard.style.opacity = 0;
        setTimeout(()=> {
            el.parentNode.removeChild(el);
            this.changeBadge(-1)
        }, speed);
    }

    changeBadge(number){
        let aonNotify = document.querySelector('aon-notification-icon');
        if(aonNotify){
            aonNotify.badge = aonNotify.badge +number;
        }
    }

}
window.customElements.define('aon-notification', AonNotification);
