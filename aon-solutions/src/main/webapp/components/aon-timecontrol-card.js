import { AonCard } from './aon-card.js';
import { Apps } from '../services/app.js';
import { getTimeControl } from  '../services/service.js';
import { MSG, CSS, EVENT, TAG, CONSTANT } from '../environments/environments.js'; 
import { AonSign } from '../modules/timecontrol/aon-sign.js';
import { AonStatistics } from '../modules/timecontrol/time-control/statistics/aon-statistics.js';

export class AonTimeControlCard extends AonCard {

	
	constructor () {
		super();
		this.setApp(Apps.TIMECONTROL);
		this.id = CONSTANT.TIMECONTROL;
		this.message = MSG.TIMECONTROL;
		this.classList.add(CSS.AON_DASHBOARD_CARD);
	}
	
	build() {
	
		getTimeControl().then(r => {
			super.build();
			this.addEventListener(EVENT.CLICK_TITLE, () => this.appSelection(Apps.TIMECONTROL.app));
			this.getCardTitle1().style.cursor = 'pointer';
			let staticsDiv = this.createElement(TAG.DIV);
			staticsDiv.style.height = "14rem";
			staticsDiv.style.minWidth = "15rem";
			staticsDiv.style.maxWidth = "25rem";
			staticsDiv.style.margin = "0 auto";
			staticsDiv.appendChild(new AonStatistics());
	
			let aonSign = new AonSign();
	
			this.clearContent();
			this.setContent(staticsDiv);
			this.setContent(aonSign);
	

			aonSign.buildSignin(r);
			let aonHeader = this.getElement('aonHeader');
			aonHeader?.timeControlStatus(r);
	
/*			this.firstChild.style.minHeight = "28rem";
			this.firstChild.style.margin = '0';
*/
		})
		.catch( error => this.onError?.(error) )
		;
	}
	
} 

if(!window.customElements.get('aon-timecontrol-card')){
  window.customElements.define('aon-timecontrol-card',  AonTimeControlCard);
}
