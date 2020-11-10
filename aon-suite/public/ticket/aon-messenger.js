import {loginbidoq} from  '../components/bidoq.js';

class AonMessenger extends HTMLElement {
    constructor () {
        super();
    }

    connectedCallback () {
        this.innerHTML = `<aon-application id="aonMessenger" title="SOLICITUDES"></aon-application>`;
        this.build();
    }

    build() {
        let aonMessenger = document.getElementById('aonMessenger');

        aonMessenger.addToolbarOption('Add', 'add', () => {this.loadCreate()});

        let options = [
            {
                name: 'Consultas',
                icon: 'inbox',
                fn: () => this.loadIndex()
            },
            {
                name: 'Trámites',
                icon: 'inbox',
                fn: () => this.loadIndex()
            },
            {
                name: 'Nueva solicitud',
                icon: 'add',
                fn: () => this.loadCreate()
            }
        ];
        aonMessenger.addSidenavOptions('OPCIONES', options);
        this.loadIndex();
        
        this.suiteOld();
    }

    suiteOld() {
        let aonMessenger      = document.getElementById('aonMessenger');
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
        aonMessenger.addSidenavOptions('VISTA CLÁSICA', options);
        this.loadIndex();
    }

    async loadBidoq(){
        // Ponemos un cargando mientras
        const aonMessenger = document.getElementById('aonMessenger');
        aonMessenger.setContentHTML('<center class="lds-padding-top"><div class="lds-ripple"><div></div><div></div></div></center>');

        try {
            // Llamamos a Bidoq para hacer el Login
            const loginBidoq    = await loginbidoq();
            const response      = JSON.parse(loginBidoq);
            
            if (typeof response !== 'undefined') {
                if (typeof response.code !== 'undefined' && response.code === 0 && response.datos.ruta) {
                    // Abrir BIDOQ
                    window.open(response.datos.respuesta, '_blank');
                    aonMessenger.setContentHTML('');
                } else {
                    console.log(response);
                    alert(response.message);
                    aonMessenger.setContentHTML('');
                }
            } else {
                console.error('Ocurrió un error al intentar abrir BIDOQ');
                aonMessenger.setContentHTML('');
            }
        } catch (error) {
            console.error('Ocurrió un error: ' + error.message);
            aonMessenger.setContentHTML('');
        }
    }
    
    loadIndex() {
        let aonMessenger = document.getElementById('aonMessenger');
        aonMessenger.setContentHTML('<iframe src="./index.html" style="width:100%;height:100%;border:none;"></iframe>');
    }

    loadCreate() {
        let aonMessenger = document.getElementById('aonMessenger');
        aonMessenger.setContentHTML('<iframe src="./create.html" style="width:100%;height:100%;border:none;"></iframe>');
    }

    loadShow() {
        let aonMessenger = document.getElementById('aonMessenger');
        aonMessenger.setContentHTML('<iframe src="./show.html" style="width:100%;height:100%;border:none;"></iframe>');
    }
}
window.customElements.define('aon-messenger', AonMessenger);
