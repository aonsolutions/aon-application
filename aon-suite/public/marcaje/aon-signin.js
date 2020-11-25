class AonSignin extends HTMLElement {

	constructor() {
		super();
	}

	connectedCallback() {
		this.innerHTML = `
			<aon-application id="aonSignin" title="CONTROL DE HORARIO"></aon-application>
		`;
		this.build();
	}

	build() {
		let aonSignin = document.getElementById('aonSignin');

		aonSignin.addToolbarOption('Add', 'add', () => { alert('Add Example') });

		let options = [
			{
				name: 'Marcaje de empleado',
				icon: 'people',
				fn: () => this.loadEmployee()
			},
			{
				name: 'Historial de marcajes',
				icon: 'history',
				fn: () => this.loadHistory()
			},
			{
				name: 'Marcaje desde administrador',
				icon: 'admin_panel_settings',
				fn: () => this.loadAdmin()
			},	
			{
				name: 'Solicitud de vacaciones',
				icon: 'flight_takeoff',
				fn: () => this.loadSolicitarVacaciones()
			},
			{
				name: 'Historial de solicitudes',
				icon: 'history',
				fn: () => this.loadHistorialSolicitudes()
			}
		];
		aonSignin.addSidenavOptions('OPCIONES', options);
		this.loadEmployee();
	}

	loadAdmin() {
		let aonSignin = document.getElementById('aonSignin');
		aonSignin.setContentHTML('<iframe src="./administrador.html" style="width:100%;height:100%;border:none;"></iframe>');
	}

	loadEmployee() {
		let aonSignin = document.getElementById('aonSignin');
		aonSignin.setContentHTML('<iframe src="./empleado.html" style="width:100%;height:100%;border:none;"></iframe>');
	}

	loadHistory() {
		let aonSignin = document.getElementById('aonSignin');
		aonSignin.setContentHTML('<iframe src="./historial.html" style="width:100%;height:100%;border:none;"></iframe>');
	}

	loadSolicitarVacaciones() {
		let aonSignin = document.getElementById('aonSignin');
		aonSignin.setContentHTML('<iframe src="./solicitar-vacaciones.html" style="width:100%;height:100%;border:none;"></iframe>');
	}

	loadHistorialSolicitudes() {
		let aonSignin = document.getElementById('aonSignin');
		aonSignin.setContentHTML('<iframe src="./historial-solicitudes.html" style="width:100%;height:100%;border:none;"></iframe>');
	}
}
window.customElements.define('aon-signin', AonSignin);
