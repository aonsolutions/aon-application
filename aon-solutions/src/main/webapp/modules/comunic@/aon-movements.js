import { AonElement } from '../../components/AonElement.js';
import './aon-movements-list.js';
import'./aon-alta-directa.js';

export class AonMovements extends AonElement {

    get id() {
        return this.getAttribute('id');
    }

    set id(id) {
        this.setAttribute('id', id);
    }

    constructor() {
        super();
    }


    connectedCallback() {
        this.innerHTML = `
            <aon-dialog-menu id="aonDialogAddOption" > </aon-dialog-menu>
        `;

        this.build();
    }

    build() {
        let aonComunica = this.getElement('aonComunica');
        aonComunica.removeToolbarOptions();

        if (this.isMobile()) {
            let floatButton = this.getElement('aonComunicaFloatSpan');
            if (!floatButton) {
                aonComunica.addFloatOption({
                    id: 'AddAlta',
                    name: 'addalta',
                    icon: 'add'
                }, () =>  this.aonAltaDirecta());
            }

        } else {
            aonComunica.addToolbarOption('Add', 'add', () => this.aonAltaDirecta());
        }

        this.aonList();
    }

    async aonList(filter) {
        let aonComunica = this.getElement('aonComunica');
        this.getElement(aonComunica.TOOLBAR).setAttribute('option', 'Movimientos');
        if (filter) aonComunica.setFilter(filter);
        else {
            aonComunica.setContentHTML(`<aon-movements-list />`);
        }
    }

    aonAltaDirecta() {
        let aonComunica = this.getElement('aonComunica');
        aonComunica.setContentHTML(`<aon-alta-directa />`);
    }

}
window.customElements.define('aon-movements', AonMovements);
