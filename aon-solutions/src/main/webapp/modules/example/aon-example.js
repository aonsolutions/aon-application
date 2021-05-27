import {AonElement} from '../../components/AonElement.js';
import { AonApplication } from '../../components/aon-application.js';

export class AonExample extends AonElement {

	AON_EXAMPLE;

	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
    	this.build();
 	}

	initialize(){
		this.AON_EXAMPLE = 'aonExample';
	}

 	build() {
		this.paintView();
		this.applicationEl = this.getApplication();
		this.applicationParentEl = this.getApplicationParent();
	}

	paintView(){
		this.createApplication(this.AON_EXAMPLE, "Example", new AonApplication())
	}

	showView(view, data, filter = undefined){
		return new Promise(async(resolve)=>{
		  let aonView = undefined;
			switch(view){
				case "VIEW_ID":
					// aonView = new AonComponent();
				break;
			}
			aonView.id = view;
			if(filter) aonView.filter = filter;
			this.applicationEl.setContent(aonView);
		  resolve(true);
		});
	  }
}
window.customElements.define('aon-example', AonExample);
