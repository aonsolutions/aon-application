import {AonElement} from '../../components/AonElement.js';
import '../../components/aon-application.js';

export class AonMessengerAyudat extends AonElement {
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
    }

    loadIndex() {
        let aonMessenger = document.getElementById('aonMessenger');
        aonMessenger.setContentHTML('<iframe src="../../aon-suite/public/ticket/index.html" style="width:100%;height:100%;border:none;"></iframe>');
    }

    loadCreate() {
        let aonMessenger = document.getElementById('aonMessenger');
        aonMessenger.setContentHTML('<iframe src="../../aon-suite/public/ticket/create.html" style="width:100%;height:100%;border:none;"></iframe>');
    }

    loadShow() {
        let aonMessenger = document.getElementById('aonMessenger');
        aonMessenger.setContentHTML('<iframe src="../../aon-suite/public/ticket/show.html" style="width:100%;height:100%;border:none;"></iframe>');
    }
}
window.customElements.define('aon-messenger-ayudat', AonMessengerAyudat);
