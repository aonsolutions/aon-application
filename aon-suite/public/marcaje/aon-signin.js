import {loginbidoq} from  '../components/bidoq.js';
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
				name: 'Marcaje desde administrador',
				icon: 'admin_panel_settings',
				fn: () => this.loadAdmin()
			},
			{
				name: 'Historial de marcajes',
				icon: 'history',
				fn: () => this.loadHistory()
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
        
        this.suiteOld();
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

	suiteOld() {
        let aonSignin         = document.getElementById('aonSignin');
        // Datos de mientras de pruebas
        const aon_domain_name = 'altai-G90317447-ayudat.aonsolutions.net';
        const aon_session_id  = 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ7XCJzY2hlbWFcIjpcImF5dWRhdC1hb25zb2x1dGlvbnMtbmV0XCIsXCJzY2hlbWFfZmlyc3RfZG9tYWluXCI6XCIwMDIyNDIwMzllLWF5dWRhdC5hb25zb2x1dGlvbnMubmV0XCIsXCJ1dWlkXCI6XCJFNkFGMjg1NEI2NjYxMUVBODMyMzA2QTBCREQ3MkE0NlwifSIsImlzcyI6ImF1dGgwIiwiaWF0IjoxNjAwNzkzNDgyfQ.4O-z1Hldqz1WAmX7kcsBkRlb0zy64ucYXQIoLnDL7mA';
   
        let options = [
            {
                name: 'aonSolutions',
                img : '../img/aon.png',
                fn: () => open('https://' + aon_domain_name + '/login?token=' + aon_session_id, '_blank')
            },
            {
                name: 'Bidoq',
                img : '../img/bidoq.png',
                fn  : () => this.loadBidoq()
            }
        ];
        aonSignin.addSidenavOptions('VISTA CLÁSICA', options);
        this.loadIndex();
    }

    async loadBidoq(){
        // Ponemos un cargando mientras
        const aonSignin = document.getElementById('aonSignin');
        aonSignin.setContentHTML('<center class="lds-padding-top"><div class="lds-ripple"><div></div><div></div></div></center>');

        try {
            // Llamamos a Bidoq para hacer el Login
            const loginBidoq    = await loginbidoq();
            const response      = JSON.parse(loginBidoq);
            
            if (typeof response !== 'undefined') {
                if (typeof response.code !== 'undefined' && response.code === 0 && response.datos.ruta) {
                    // Abrir BIDOQ
                    window.open(response.datos.respuesta, '_blank');
                    aonSignin.setContentHTML('');
                } else {
                    console.log(response);
                    alert(response.message);
                    aonSignin.setContentHTML('');
                }
            } else {
                console.error('Ocurrió un error al intentar abrir BIDOQ');
                aonSignin.setContentHTML('');
            }
        } catch (error) {
            console.error('Ocurrió un error: ' + error.message);
            aonSignin.setContentHTML('');
        }
    }
}
window.customElements.define('aon-signin', AonSignin);
