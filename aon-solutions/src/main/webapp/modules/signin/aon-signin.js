import {AonElement} from '../../components/AonElement.js';

export class AonSignin extends AonElement {

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-application id="aonSignin" title="CONTROL DE HORARIO"></aon-application>
		`;
    this.build();
 	}

 	build() {
		let aonSignin = document.getElementById('aonSignin');

		let options = [
			{
				name: 'Administradores',
				icon: 'admin_panel_settings',
				fn: () => this.loadAdmin()
			},
			{
				name: 'Empleados',
				icon: 'people',
				fn: () => this.loadEmployee()
			},
			{
				name: 'Historial',
				icon: 'history',
				fn: () => this.loadHistory()
			}
		];
		aonSignin.addSidenavOptions('OPCIONES', options);
		this.loadAdmin();
	}

	loadAdmin() {
		let aonSignin = document.getElementById('aonSignin');
		aonSignin.setContentHTML('<iframe src="../../aon-suite/public/marcaje/administrador.html" style="width:100%;height:100%;border:none;"></iframe>');
	}

	loadEmployee() {
		let aonSignin = document.getElementById('aonSignin');
		aonSignin.setContentHTML('<iframe src="../../aon-suite/public/marcaje/empleado.html" style="width:100%;height:100%;border:none;"></iframe>');
	}

	loadHistory() {
		let aonSignin = document.getElementById('aonSignin');
		aonSignin.setContentHTML('<iframe src="../../aon-suite/public/marcaje/historial.html" style="width:100%;height:100%;border:none;"></iframe>');
	}
}
window.customElements.define('aon-signin', AonSignin);
