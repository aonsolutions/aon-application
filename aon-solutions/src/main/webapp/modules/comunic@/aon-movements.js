import {AonElement} from '../../components/AonElement.js';
import './aon-movements-list.js';
import './aon-alta-directa.js';
export class AonMovements extends AonElement {
    constructor () {
        super();
    }


    connectedCallback () {
        this.innerHTML = `
            <aon-dialog id="aonDialogAddOption" type="menu" > </aon-dialog>
        `;
        this.build();
    }

    build(){
        let aonMovements = this.getElement('aonComunica');
        aonMovements.removeToolbarOptions();
        aonMovements.addToolbarOption('Add', 'add', () => this.aonAltaDirecta());
        this.aonList();
    }

    async aonList(filter){
        let list = this.getElement('aonComunica')
        if(filter) list.setFilter(filter);
        else {
            let aonComunica = this.getElement('aonComunica');
            aonComunica.setContentHTML(`<aon-movements-list />`);
        }
    }
    
    aonAltaDirecta() {
        let aonComunica = this.getElement('aonComunica');
        aonComunica.setContentHTML(`<aon-alta-directa />`);
    }

}
window.customElements.define('aon-movements', AonMovements);
