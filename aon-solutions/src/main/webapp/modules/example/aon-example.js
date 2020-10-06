import '../../components/aon-application.js';

class AonExample extends HTMLElement {

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-application id="aonExample" title="Example"></aon-application>
		`;
    this.build();
 	}

 	build() {
		let aonExample = document.getElementById('aonExample');

		aonExample.addToolbarOption('Add', 'add', () => {alert('Add Example')});


		let options = [
			{
				name: 'Prueba',
				icon: 'accessibility',
				fn: () => alert('PRUEBA!!')
			}
		];
		aonExample.addSidenavOptions('OPCIONES', options);
	}
}
window.customElements.define('aon-example', AonExample);
