import { AonApplication } from '../../components/aon-application.js';
import { AonElement } from '../../components/AonElement.js';
import { AonMessengerChat } from './aon-messeger-chat.js';
import { AonMessengerList } from './aon-messenger-list.js';
import { MessengerOptions, MESSENGER_VIEWS, REQUEST_FILTER } from './MessengerEnums.js';

export class AonMessenger extends AonElement {
    AON_MESSENGER;
	FILTER

	constructor () {
		super();
		this.FILTER = REQUEST_FILTER.ABIERTAS;
	}

	get filter() {
		return JSON.parse(this.FILTER);
	}

	set filter(filter){
		this.FILTER = JSON.stringify(filter);
	}

	connectedCallback () {
		this.initialize();
    	this.build();
 	}

	initialize(){
		this.AON_MESSENGER = MESSENGER_VIEWS.AON_MESSENGER;
	}

 	build() {
		this.paintView();
		this.applicationEl = this.getApplication();
		this.applicationParentEl = this.getApplicationParent();
		this.buildToolbar();
		this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST);
	}

	paintView(){
		this.createApplication(this.AON_MESSENGER, "Solicitudes", new AonApplication());
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
