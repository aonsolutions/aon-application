import { AonElement } from '../../components/AonElement.js';

import '../../components/aon-dialog-menu.js';
import { AonAltaDirecta } from './aon-alta-directa.js';
import { AonMovementsList } from './aon-movements-list.js';

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
        this.build();
    }

    build() {
        this.paintView();
        this.aonComunicaEl.removeToolbarOptions();
        if (this.isMobile()) {
            let floatButton = this.getElement(`${this.aonComunicaEl.id}FloatSpan`);
            if (!floatButton) {
                this.aonComunicaEl.addFloatOption({
                    id: 'AddAlta',
                    name: 'addalta',
                    icon: 'add'
                }, () =>  this.aonAltaDirecta()); 
            }
        } else {
            this.aonComunicaEl.addToolbarOption('Add', 'add', () => this.aonAltaDirecta());
        }
        this.aonList();
    }

    paintView(){
        this.innerHTML = `<aon-dialog-menu id="aonDialogAddOption" > </aon-dialog-menu>`;
    }

    async aonList(filter) {
        this.getElement(this.aonComunicaEl.TOOLBAR).setAttribute('option', 'Movimientos');
        if (filter) this.aonComunicaEl.setFilter(filter);
        else {
            let aonMovementsList = new AonMovementsList();
            aonMovementsList.id = this.id+"List";
            this.aonComunicaEl.setContent(aonMovementsList);
        }
    }

    aonAltaDirecta() {
        this.aonComunicaEl.setContent(new AonAltaDirecta());
    }

}
window.customElements.define('aon-movements', AonMovements);
