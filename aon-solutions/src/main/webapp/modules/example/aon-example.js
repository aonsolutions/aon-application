import {AonElement} from '../../components/AonElement.js';
import '../../components/aon-application.js';

export class AonExample extends AonElement {

	AON_EXAMPLE;

	constructor () {
		super();
		this.AON_EXAMPLE = 'aonExample';
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-application id="${this.AON_EXAMPLE}" title="Example"></aon-application>
		`;
    this.build();
 	}

 	build() {
		let aonExample = this.getElement(this.AON_EXAMPLE);

		aonExample.addToolbarOption('Add', 'add', () => {alert('Add Example')});

		let options = [{
			name: 'Prueba',
			icon: 'accessibility',
			fn: () => alert('PRUEBA!!')
		}];
		aonExample.addSidenavOptions('OPCIONES', options);
	}
}
window.customElements.define('aon-example', AonExample);
