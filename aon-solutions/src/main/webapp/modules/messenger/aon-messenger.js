import {AonElement} from '../../components/AonElement.js';

export class AonMessenger extends AonElement {

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-application id="aonMessenger" title="MESSENGER"></aon-application>
		`;
		this.build();
 	}

 	build() {
		let aonMessenger = document.getElementById('aonMessenger');

		let options = [
			{
				name: 'Index',
				icon: 'message',
				fn: () => this.loadIndex()
			},
			{
				name: 'Create',
				icon: 'message',
				fn: () => this.loadCreate()
			},
			{
				name: 'Show',
				icon: 'message',
				fn: () => this.loadShow()
			}
		];
		aonMessenger.addSidenavOptions('OPCIONES', options);
		this.loadIndex();
	}

	loadIndex() {
		let aonMessenger = document.getElementById('aonMessenger');
		aonMessenger.setContentHTML('<iframe src="../../aon-suite/public/ticket/index.html" style="width:100%;height:100%;border:none;"></iframe>');
	}

	loadCreate() {
		let aonMessenger = document.getElementById('aonMessenger');
		aonMessenger.setContentHTML('<iframe src="../../aon-suite/public/ticket/create.html" style="width:100%;height:100%;border:none;"></iframe>');
	}

	loadShow() {
		let aonMessenger = document.getElementById('aonMessenger');
		aonMessenger.setContentHTML('<iframe src="../../aon-suite/public/ticket/show.html" style="width:100%;height:100%;border:none;"></iframe>');
	}
}
window.customElements.define('aon-messenger', AonMessenger);
