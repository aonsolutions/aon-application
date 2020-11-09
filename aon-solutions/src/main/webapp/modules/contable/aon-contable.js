import {loginbidoq} from  '../components/bidoq.js';

class AonContable extends HTMLElement {
    constructor () {
        super();
    }

    connectedCallback () {
        this.innerHTML = `
            <aon-application id="aonContable" title="CONTABLE"></aon-application>
        `;
        this.build();
    }

    build() {
        let aonContable = document.getElementById('aonContable');

        let documentOptions = [
            {
                name: 'Recientes',
                icon: 'access_time',
                fn: () => this.loadIndex()
            },
            {
                name: 'Ver todos',
                icon: 'text_snippet',
                fn: () => this.loadIndex()
            }
        ];
        aonContable.addSidenavOptions('DOCUMENTOS', documentOptions);

        let categoryOptions = [
            {
                name: 'Resumen',
                icon: 'folder',
                fn: () => this.loadIndex()
            },
            {
                name: 'PPyGG',
                icon: 'folder',
                fn: () => this.loadPPYGG()
            },
            {
                name: 'Balance',
                icon: 'folder',
                fn: () => this.loadBalance()
            }
        ];

        aonContable.addSidenavOptions('CATEGORIAS', categoryOptions);
        this.loadIndex();

        //this.suiteOld();
    }

    loadIndex() {
        let aonContable = document.getElementById('aonContable');
        aonContable.setContentHTML('<iframe src="../../aon-suite/public/contable/index.html" style="width:100%;height:100%;border:none;"></iframe>');
    }

    loadPPYGG() {
        let aonContable = document.getElementById('aonContable');
        aonContable.setContentHTML('<iframe src="../../aon-suite/public/contable/ppygg.html" style="width:100%;height:100%;border:none;"></iframe>');
    }

    loadBalance() {
        let aonContable = document.getElementById('aonContable');
        aonContable.setContentHTML('<iframe src="../../aon-suite/public/contable/balance.html" style="width:100%;height:100%;border:none;"></iframe>');
    }

	suiteOld() {
        let aonContable       = document.getElementById('aonContable');
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
        aonContable.addSidenavOptions('VISTA CLÁSICA', options);
        this.loadIndex();
    }

    async loadBidoq(){
        // Ponemos un cargando mientras
        const aonContable = document.getElementById('aonContable');
        aonContable.setContentHTML('<center class="lds-padding-top"><div class="lds-ripple"><div></div><div></div></div></center>');

        try {
            // Llamamos a Bidoq para hacer el Login
            const loginBidoq    = await loginbidoq();
            const response      = JSON.parse(loginBidoq);

            if (typeof response !== 'undefined') {
                if (typeof response.code !== 'undefined' && response.code === 0 && response.datos.ruta) {
                    // Abrir BIDOQ
                    window.open(response.datos.respuesta, '_blank');
                    aonContable.setContentHTML('');
                } else {
                    console.log(response);
                    alert(response.message);
                    aonContable.setContentHTML('');
                }
            } else {
                console.error('Ocurrió un error al intentar abrir BIDOQ');
                aonContable.setContentHTML('');
            }
        } catch (error) {
            console.error('Ocurrió un error: ' + error.message);
            aonContable.setContentHTML('');
        }
    }
}
window.customElements.define('aon-contable', AonContable);
