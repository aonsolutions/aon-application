import {AonElement} from '../../components/AonElement.js';
import '../../components/aon-application.js';

export class AonImports extends AonElement {

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-application id="aonImports" title="Carga de Dastos"></aon-application>
		`;
    this.build();
 	}

 	build() {
		let aonImports = document.getElementById('aonImports');

		aonImports.addToolbarOption('Add', 'add', () => {alert('Add Example')});


		let options = [
			{
				name: 'Historial',
				icon: 'history',
				fn: () => alert('HISTORIAL')
			}
		];
		aonImports.addSidenavOptions('OPCIONES', options);
	}
}
window.customElements.define('aon-imports', AonImports);
