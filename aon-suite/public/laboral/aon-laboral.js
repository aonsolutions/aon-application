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
    }

    loadIndex() {
        let aonLaboral = document.getElementById('aonLaboral');
        aonLaboral.setContentHTML('<iframe src="./index.html" style="width:100%;height:100%;border:none;"></iframe>');
    }
    
    loadNominas() {
        let aonLaboral = document.getElementById('aonLaboral');
        aonLaboral.setContentHTML('<iframe src="./nominas.html" style="width:100%;height:100%;border:none;"></iframe>');
    }
    
    loadNew(type) {
        let aonLaboral = document.getElementById('aonLaboral');
        aonLaboral.setContentHTML('<iframe src="./new.html?id='+type+'" style="width:100%;height:100%;border:none;"></iframe>');
    }
}
window.customElements.define('aon-laboral', AonLaboral);
