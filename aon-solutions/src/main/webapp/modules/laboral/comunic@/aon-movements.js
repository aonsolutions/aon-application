import { AonElement } from '../../../components/AonElement.js';
import { PAYROLL_VIEWS } from '../PayrollEnums.js';

export class AonMovements extends AonElement {

    set id(id) {
        this.setAttribute('id', id);
    }

    get id() {
        return this.getAttribute('id');
    }

    constructor() {
        super();
        this.id = this.id || PAYROLL_VIEWS.AON_MOVEMENTS;
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
        this.applicationParentEl.showView(PAYROLL_VIEWS.AON_MOVEMENTS_LIST);
    }

    aonAltaDirecta() {
        this.applicationParentEl.showView(PAYROLL_VIEWS.AON_ALTA_DIRECTA);
    }

}
window.customElements.define('aon-movements', AonMovements);
