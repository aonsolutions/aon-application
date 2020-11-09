import {loginbidoq} from  '../components/bidoq.js';

class AonFiscal extends HTMLElement {

	year = 2020;
	quarter = 3;

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-application id="aonFiscal" title="FISCAL"></aon-application>
		`;
    this.build();
 	}

 	build() {
		let aonFiscal = document.getElementById('aonFiscal');

		let yearOptions = [
			{
				name: '2020',
				icon: 'date_range',
				fn: () => this.loadIndex(2020, this.quarter)
			}
		];
		let quarterOptions = [
			{
				name: '1T',
				icon: 'date_range',
				fn: () => this.loadIndex(this.year, 1)
			},
			{
				name: '2T',
				icon: 'date_range',
				fn: () => this.loadIndex(this.year, 2)
			},
			{
				name: '3T',
				icon: 'date_range',
				fn: () => this.loadIndex(this.year, 3)
			},
			{
				name: '4T',
				icon: 'date_range',
				fn: () => this.loadIndex(this.year, 4)
			},
		];
		aonFiscal.addSidenavOptions('AÑO', yearOptions);
		aonFiscal.addSidenavOptions('TRIMESTRE', quarterOptions);
		this.loadIndex();

    //this.suiteOld();
	}

	loadIndex(year = this.year, quarter = this.quarter) {
		this.year = year;
		this.quarter = quarter;

		let aonFiscal = document.getElementById('aonFiscal');
		aonFiscal.setContentHTML('<iframe src="../../aon-suite/public/fiscal/index.html?year=' + this.year + '&quarter=' + this.quarter + '" style="width:100%;height:100%;border:none;"></iframe>');
	}

	suiteOld() {
        let aonFiscal         = document.getElementById('aonFiscal');
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
        aonFiscal.addSidenavOptions('VISTA CLÁSICA', options);
        this.loadIndex();
    }

    async loadBidoq(){
        // Ponemos un cargando mientras
        const aonFiscal = document.getElementById('aonFiscal');
        aonFiscal.setContentHTML('<center class="lds-padding-top"><div class="lds-ripple"><div></div><div></div></div></center>');

        try {
            // Llamamos a Bidoq para hacer el Login
            const loginBidoq    = await loginbidoq();
            const response      = JSON.parse(loginBidoq);

            if (typeof response !== 'undefined') {
                if (typeof response.code !== 'undefined' && response.code === 0 && response.datos.ruta) {
                    // Abrir BIDOQ
                    window.open(response.datos.respuesta, '_blank');
                    aonFiscal.setContentHTML('');
                } else {
                    console.log(response);
                    alert(response.message);
                    aonFiscal.setContentHTML('');
                }
            } else {
                console.error('Ocurrió un error al intentar abrir BIDOQ');
                aonFiscal.setContentHTML('');
            }
        } catch (error) {
            console.error('Ocurrió un error: ' + error.message);
            aonFiscal.setContentHTML('');
        }
    }
}
window.customElements.define('aon-fiscal', AonFiscal);
