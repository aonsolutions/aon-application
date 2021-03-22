import { AonElement } from '../../../components/AonElement.js';
import { AonAltaDirecta } from './aon-alta-directa.js';
import { AonMovementsList } from './aon-movements-list.js';
import '../../../components/aon-dialog-menu.js';


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
    }


    connectedCallback() {
        this.build();
    }

    build() {
        this.paintView();
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
        this.aonList();
    }

    paintView(){
        this.innerHTML = `<aon-dialog-menu id="aonDialogAddOption" > </aon-dialog-menu>`;
    }

    async aonList(filter) {
        this.getElement(this.applicationEl.TOOLBAR).setAttribute('option', 'Movimientos');
        if (filter) this.applicationEl.setFilter(filter);
        else {
            let aonMovementsList = new AonMovementsList();
            aonMovementsList.id = this.id+"List";
            this.applicationEl.setContent(aonMovementsList);
        }
    }

    aonAltaDirecta() {
        this.applicationEl.setContent(new AonAltaDirecta());
    }

}
window.customElements.define('aon-movements', AonMovements);
