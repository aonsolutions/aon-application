import {AonElement} from '../../components/AonElement.js';
import './aon-movements-list.js';
import './aon-alta-directa.js';
export class AonMovements extends AonElement {

    get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

    constructor () {
        super();
    }


    connectedCallback () {
        this.innerHTML = `
            <aon-dialog id="aonDialogAddOption" type="menu" > </aon-dialog>
        `;

        this.storageTest();

        this.build();
    }

    build(){
        let aonMovements = this.getElement('aonComunica');
        aonMovements.removeToolbarOptions();
        aonMovements.addToolbarOption('Add', 'add', () => this.aonAltaDirecta());
        this.aonList();
    }

    storageTest(){
        // localStorage.setItem('aon_domain_id', 3348);
        // localStorage.setItem('aon_domain_name', 'altai-G90317447-ayudat.aonsolutions.net');
	}

    async aonList(filter){
        let aonComunica = this.getElement('aonComunica');
        if(filter) aonComunica.setFilter(filter);
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
