import { AonElement } from '../../components/AonElement.js';
import './aon-movements-list.js';
import'./aon-alta-directa.js';

export class AonMovements extends AonElement {

    set id(id) {
        this.setAttribute('id', id);
    }

    get id() {
        return this.getAttribute('id');
    }
    
    constructor() {
        super();
        this.id = this.id || 'aonComunicaMovements';
        this.aonComunicaEl =  this.getElement('aonComunica');
    }


    connectedCallback() {
        this.innerHTML = `
            <aon-dialog-menu id="aonDialogAddOption" > </aon-dialog-menu>
        `;

        this.build();
        this.aonList();
    }

    build() {
        let aonComunica = this.aonComunicaEl;
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
    }

    async aonList(filter) {
        let aonComunica = this.aonComunicaEl;
        this.getElement(aonComunica.TOOLBAR).setAttribute('option', 'Movimientos');
        if (filter) aonComunica.setFilter(filter);
        else {
            aonComunica.setContentHTML(`<aon-movements-list id="${this.id}List" />`);
        }
    }

    aonAltaDirecta() {
        this.aonComunicaEl.setContentHTML(`<aon-alta-directa />`);
    }

}
window.customElements.define('aon-movements', AonMovements);
