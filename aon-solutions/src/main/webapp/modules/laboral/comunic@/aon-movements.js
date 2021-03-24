import { AonElement } from '../../../components/AonElement.js';

export class AonMovements extends AonElement {

    set id(id) {
        this.setAttribute('id', id);
    }

    get id() {
        return this.getAttribute('id');
    }

    constructor() {
        super();
        this.id = this.id || 'aonMovements';
        this.applicationEl = this.getApplication();
        this.applicationParentEl = this.getApplicationParent();
    }


    connectedCallback() {
        this.build();
    }

    build() {
        this.applicationEl.removeToolbarOptions();
        if (this.isMobile()) {
            let floatButton = this.getElement(`${this.applicationEl.id}FloatSpan`);
            if (!floatButton) {
                this.applicationEl.addFloatOption({
                    id: 'AddAlta',
                    name: 'addalta',
                    icon: 'add'
                }, () =>  this.aonAltaDirecta()); 
            }
        } else {
            this.applicationEl.addToolbarOption('Add', 'add', () => this.aonAltaDirecta());
        }
        this.getElement(this.applicationEl.TOOLBAR).setAttribute('option', 'Movimientos');
        this.applicationParentEl.showView("aonMovementsList");
    }

    aonAltaDirecta() {
        this.applicationParentEl.showView("aonAltaDirecta");
    }

}
window.customElements.define('aon-movements', AonMovements);
