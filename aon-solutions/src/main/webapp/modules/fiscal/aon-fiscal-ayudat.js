import {AonElement} from '../../components/AonElement.js';
import { MSG } from '../../environments/environments.js';
import { AonApplication } from '../../components/aon-application.js';

class AonFiscalAyudat extends AonElement {
	AON_FISCAL_AYUDAT;
	year = 2020;
	quarter = 3;

	constructor () {
		super();
	}

	connectedCallback () {
		this.AON_FISCAL_AYUDAT = "aonFiscalAyudat";
		this.createApplication(this.AON_FISCAL_AYUDAT, MSG.FISCAL, new AonApplication());
		this.applicationEl = this.getApplication();
    	this.build();
 	}

 	build() {
		let yearOptions = [
			{
				name: '2020',
				icon: 'date_range',
				fn: () => this.loadIndex(2020, this.quarter)
			}
		];

		let quarterOptions = [
			{
				name: '1T',
				icon: 'date_range',
				fn: () => this.loadIndex(this.year, 1)
			},
			{
				name: '2T',
				icon: 'date_range',
				fn: () => this.loadIndex(this.year, 2)
			},
			{
				name: '3T',
				icon: 'date_range',
				fn: () => this.loadIndex(this.year, 3)
			},
			{
				name: '4T',
				icon: 'date_range',
				fn: () => this.loadIndex(this.year, 4)
			},
		];
		this.applicationEl.addSidenavOptions('AÑO', yearOptions);
		this.applicationEl.addSidenavOptions('TRIMESTRE', quarterOptions);
		this.loadIndex();
	}

	loadIndex(year = this.year, quarter = this.quarter) {
		this.year = year;
		this.quarter = quarter;
		this.applicationEl.setContentHTML('<iframe src="../../aon-suite/public/fiscal/index.html?year=' + this.year + '&quarter=' + this.quarter + '" style="width:100%;height:100%;border:none;"></iframe>');
	}
}
window.customElements.define('aon-fiscal-ayudat', AonFiscalAyudat);
