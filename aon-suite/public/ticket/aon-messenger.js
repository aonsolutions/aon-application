class AonMessenger extends HTMLElement {
    constructor () {
        super();
    }

    connectedCallback () {
        this.innerHTML = `<aon-application id="aonMessenger" title="MESSENGER"></aon-application>`;
        this.build();
    }

    build() {
        let aonMessenger = document.getElementById('aonMessenger');

        aonMessenger.addToolbarOption('Add', 'add', () => {this.loadCreate()});

        let options = [
            {
                name: 'Messenger',
                icon: 'inbox',
                fn: () => this.loadIndex()
            },
            {
                name: 'Crear nuevo',
                icon: 'create',
                fn: () => this.loadCreate()
            }
        ];
        aonMessenger.addSidenavOptions('OPCIONES', options);
        this.loadIndex();
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
