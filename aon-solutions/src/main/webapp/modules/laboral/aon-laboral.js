import {loginbidoq} from  '../components/bidoq.js';
class AonLaboral extends HTMLElement {
    constructor () {
        super();
    }

    connectedCallback () {
        this.innerHTML = `
                <aon-application id="aonLaboral" title="LABORAL"></aon-application>
        `;
        this.build();
    }

    build() {
        let aonLaboral = document.getElementById('aonLaboral');

        aonLaboral.addToolbarOption('Alta', 'person_add_alt_1', () => this.loadNew('alta'));
        aonLaboral.addToolbarOption('Baja', 'person_remove_alt_1', () => this.loadNew('baja'));

        let employerOptions = [
            {
                name: 'Recientes',
                icon: 'access_time',
                fn: () => this.loadIndex()
            },
            {
                name: 'Altas',
                icon: 'arrow_drop_up',
                fn: () => this.loadIndex()
            },
            {
                name: 'Bajas',
                icon: 'arrow_drop_down',
                fn: () => this.loadIndex()
            },
            {
                name: 'Nóminas',
                icon: 'text_snippet',
                fn: () => this.loadNominas()
            }
        ];
        aonLaboral.addSidenavOptions('EMPLEADOS', employerOptions);

        let actionsOptions = [
            {
                name: 'Crear alta',
                icon: 'event_available',
                fn: () => this.loadNew('alta')
            },
            {
                name: 'Crear baja',
                icon: 'event_busy',
                fn: () => this.loadNew('baja')
            }
        ];
        aonLaboral.addSidenavOptions('ACCIONES', actionsOptions);
        this.loadIndex();

        //this.suiteOld();
    }

    loadIndex() {
        let aonLaboral = document.getElementById('aonLaboral');
        aonLaboral.setContentHTML('<iframe src="../../aon-suite/public/laboral/index.html" style="width:100%;height:100%;border:none;"></iframe>');
    }

    loadNominas() {
        let aonLaboral = document.getElementById('aonLaboral');
        aonLaboral.setContentHTML('<iframe src="../../aon-suite/public/laboral/nominas.html" style="width:100%;height:100%;border:none;"></iframe>');
    }

    loadNew(type) {
        let aonLaboral = document.getElementById('aonLaboral');
        aonLaboral.setContentHTML('<iframe src="../../aon-suite/public/laboral/new.html?id='+type+'" style="width:100%;height:100%;border:none;"></iframe>');
    }

	suiteOld() {
        let aonLaboral        = document.getElementById('aonLaboral');
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
        aonLaboral.addSidenavOptions('VISTA CLÁSICA', options);
        this.loadIndex();
    }

    async loadBidoq(){
        // Ponemos un cargando mientras
        const aonLaboral = document.getElementById('aonLaboral');
        aonLaboral.setContentHTML('<center class="lds-padding-top"><div class="lds-ripple"><div></div><div></div></div></center>');

        try {
            // Llamamos a Bidoq para hacer el Login
            const loginBidoq    = await loginbidoq();
            const response      = JSON.parse(loginBidoq);

            if (typeof response !== 'undefined') {
                if (typeof response.code !== 'undefined' && response.code === 0 && response.datos.ruta) {
                    // Abrir BIDOQ
                    window.open(response.datos.respuesta, '_blank');
                    aonLaboral.setContentHTML('');
                } else {
                    console.log(response);
                    alert(response.message);
                    aonLaboral.setContentHTML('');
                }
            } else {
                console.error('Ocurrió un error al intentar abrir BIDOQ');
                aonLaboral.setContentHTML('');
            }
        } catch (error) {
            console.error('Ocurrió un error: ' + error.message);
            aonLaboral.setContentHTML('');
        }
    }
}
window.customElements.define('aon-laboral', AonLaboral);
