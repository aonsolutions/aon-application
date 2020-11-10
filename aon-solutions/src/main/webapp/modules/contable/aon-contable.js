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
}
window.customElements.define('aon-contable', AonContable);
