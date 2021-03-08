import {AonElement} from '../../components/AonElement.js';
import '../../components/aon-application.js';

class AonFiscal extends AonElement {

	year = 2020;
	quarter = 3;

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-application id="aonFiscal" title="FISCAL"></aon-application>
		`;
    this.build();
 	}

 	build() {
		let aonFiscal = document.getElementById('aonFiscal');

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
		aonFiscal.addSidenavOptions('AÑO', yearOptions);
		aonFiscal.addSidenavOptions('TRIMESTRE', quarterOptions);
		this.loadIndex();
	}

	loadIndex(year = this.year, quarter = this.quarter) {
		this.year = year;
		this.quarter = quarter;

		let aonFiscal = document.getElementById('aonFiscal');
		aonFiscal.setContentHTML('<iframe src="../../aon-suite/public/fiscal/index.html?year=' + this.year + '&quarter=' + this.quarter + '" style="width:100%;height:100%;border:none;"></iframe>');
	}
}
window.customElements.define('aon-fiscal', AonFiscal);
