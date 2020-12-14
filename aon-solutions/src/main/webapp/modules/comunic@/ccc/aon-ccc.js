import { AonElement } from '../../../components/AonElement.js';

import '../../../components/aon-dialog-menu.js';

export class AonCcc extends AonElement {
    ACTION;
    set id(id) {
        this.setAttribute('id', id);
    }

    get id() {
        return this.getAttribute('id');
    }

    get data() {
        return JSON.parse(this.getAttribute('data'));
    }

    set data(value) {
        this.setAttribute('data', JSON.stringify(value));
    }

    constructor() {
        super();
        this.ACTION = 'CREATE';
        this.id = this.id || 'aonComunicaCcc';
        this.aonComunicaEl = this.getElement('aonComunica');
        this.TOOLBAR = this.id + 'Toolbar';
        this.aonComunicaToolbar = this.getElement('aonComunicaToolbar');
    }



    attributeChangedCallback(name, oldValue, newValue) {
        if ("data" == name && newValue) {
            this.ACTION = "UPDATE";
            this.edit(this.data);
        }
    }

    connectedCallback() {
        this.paintView();
        this.build();
    }

    paintView() {
        this.innerHTML = `<aon-dialog-menu id="aonDialogAddOption" > </aon-dialog-menu>`;
    }

    build(){
        this.aonComunicaToolbar.setAttribute('option', 'Cuenta de Cotización');
    }


    edit(data) {

    }

}
window.customElements.define('aon-ccc', AonCcc);
