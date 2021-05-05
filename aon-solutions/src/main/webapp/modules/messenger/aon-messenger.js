import {AonElement} from '../../components/AonElement.js';
import { MessengerOptions, MESSENGER_VIEWS } from './MessengerEnums.js';
import { AonMessengerList } from './aon-messenger-list.js';
import { AonMessengerAyudat } from './aon-messenger-ayudat.js';
import '../../components/aon-application.js';
import { setClasses } from '../../services/utils.js';
//import { AonMessengerChat } from './aon-messeger-chat.js';

class AonMessenger extends AonElement {
    AON_MESSENGER;
	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
    	this.build();
 	}

	initialize(){
		this.AON_MESSENGER = MESSENGER_VIEWS.AON_MESSENGER;
	}

 	build() {
		 //this.paintView();
		 //this.applicationEl = this.getApplication();
		 //this.applicationParentEl = this.getApplicationParent();
		 //this.buildToolbar();
		// this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST);
		this.rootPanel(new AonMessengerAyudat()); //DELETE 
	}

	paintView(){
		this.innerHTML = /*html*/`<aon-application id="${this.AON_MESSENGER}" title="Solicitudes"></aon-application>`;
	}

	buildToolbar(){
        let messengerOpts = [];

		let list = MessengerOptions.AON_MESSENGER_LIST;
		list.fn = () => this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST);
		messengerOpts.push(list);

		let listClose = MessengerOptions.AON_MESSENGER_LIST_CLOSE;
		listClose.fn = () => this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST_CLOSE);
		messengerOpts.push(listClose);
		
		this.applicationEl.addSidenavOptions('Solicitudes', messengerOpts);
	}

	showView(view, data = undefined, filter = undefined){
        return new Promise(async(resolve)=>{
          let aonView = undefined;
          if(!this.getElement(view)){
            switch(view){
              case MESSENGER_VIEWS.AON_MESSENGER_LIST:
                aonView = new AonMessengerList();
                break;
			  case MESSENGER_VIEWS.AON_MESSENGER_CHAT:
				aonView = new AonMessengerChat();
				break;
            }
            if(aonView){
              aonView.id = view;
              if(filter) aonView.filter = filter;
              if(data) aonView.data = data;
              this.applicationEl.setContent(aonView);
            }
          }
          resolve(aonView);
        });
      }

}
window.customElements.define('aon-messenger', AonMessenger);
