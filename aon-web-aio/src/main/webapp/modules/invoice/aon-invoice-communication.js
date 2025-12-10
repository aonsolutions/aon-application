import { AonElement } from "../../components/AonElement.js";
import { CONSTANT, CSS, EVENT, MSG, TAG } from "../../environments/environments.js";
import { AonBasicTable } from "../../components/aon-basic-table.js";
import { AonDialog } from "../../components/aon-dialog.js";
import { createCard, createDate, createInput, createSelect, createSwitch } from "../../components/CreateComponent.js";
import { isPersonaFisica, isValid } from "../../services/documentUtils.js";
import { AonToast } from "../../components/aon-toast.js";

export class AonInvoiceCommunication extends AonElement {
    
    configuration;
    
    connectedCallback () {
        this.initialize();
        this.build();
  	}

    initialize() {
        this.id = this.id || 'aonInvoiceConfigurationCommunication';
        this.DIV = this.id + 'Div';
        this.CARD = this.id + 'Card';
        this.CARD_TABLE = this.CARD + 'Table';
        this.CARD_DIV = this.CARD + 'Div';

        // FACTURAE
        this.FACTURAE = this.CARD_TABLE + 'Facturae';

        // TBAI
        this.TBAI_ACTIVE = this.CARD_TABLE + 'TbaiActive';
        this.TBAI_TEST = this.CARD_TABLE + 'TbaiTest';
        this.TBAI_REGISTRY_DATE = this.CARD_TABLE + 'TbaiRegistryDate';
        this.TBAI_INCLUDE_DATE = this.CARD_TABLE + 'TbaiIncludeDate';
        
        // VERIFACTU
        this.VERIFACTU_ACTIVE = this.CARD_TABLE + 'VerifactuActive';
        this.VERIFACTU_TEST = this.CARD_TABLE + 'VerifactuTest';
        this.VERIFACTU_REGISTRY_DATE = this.CARD_TABLE + 'VerifactuRegistryDate';
        this.VERIFACTU_INCLUDE_DATE = this.CARD_TABLE + 'VerifactuIncludeDate';

        // SII
        this.SII_ACTIVE = this.CARD_TABLE + 'SiiActive';
        this.SII_TEST = this.CARD_TABLE + 'SiiTest';
        this.SII_REGISTRY_DATE = this.CARD_TABLE + 'SiiRegistryDate';
        this.SII_INCLUDE_DATE = this.CARD_TABLE + 'SiiIncludeDate';
        this.SII_AUTOSEND = this.CARD_TABLE + 'SiiAutosend';
    }

    build() {
        let div = this.createElement(TAG.DIV);
        div.id = this.DIV;
        div.style.display = 'flex';
        div.style.width = '100%';
        this.appendChild(div);

        this.buildCard(div);
    }
    
    reload() {
        let parent = this.getElement(this.DIV);
        this.clearElement(parent);
        this.buildCard(parent);
    }

    buildCard(parent) {
        let card = createCard(this.CARD, MSG.COMMUNICATIONS);
        card.style.width = '50%';
        parent.appendChild(card);
        
        let content = this.createElement(TAG.DIV);
        content.id = this.CARD_DIV;
		card.setContent(content);

        let table = new AonBasicTable();
		table.id = this.CARD_TABLE;
		content.appendChild(table);

        table.addRow();
        this.buildAdministration(table);
        this.buildFacturae(table);
        
        if(this.isTicketBai()) {
            this.buildTicketBai(table);
        } else if(!this.isNavarra()){
            this.buildVerifactu(table);
        }
        
        this.buildSii(table);
    }

    buildAdministration(table) {
        let administration = createSelect(this.ADMINISTRATION, 'Administración');
        table.addCell(administration, 1).style.height = '50px';
        administration.setOptions([
            {value: 'ALAVA', name: 'Araba/Alava'},
            {value: 'BIZKAIA', name: 'Bizkaia'},
            {value: 'GIPUZKOA', name: 'Gipuzkoa'},
            {value: 'NAVARRA', name: 'Navarra'},
            {value: 'COMMON_TERRITORY', name: 'Territorio Común'},
            {value: 'CANARIAS', name: 'A.T. Canaria'}
        ]);
        administration.value = this.configuration.administration;
        administration.addEventListener(EVENT.CHANGE, () => {
            this.configuration.administration = administration.value;
            this.configuration.communication.administration = administration.value;
            if(this.isTicketBai() || this.isNavarra()) {
                this.configuration.communication.verifactu = false;
            } else this.configuration.communication.tbai = false;
            this.reload();
            this.dispatchEvent(new Event(EVENT.CHANGE));
        });

        table.addRow();
    }

    buildFacturae(table) {
        let facturae = createSwitch(this.FACTURAE, 'Facturae');
        facturae.checked = this.configuration.eInvoice;
        facturae.addEventListener(EVENT.CHANGE, () => {
			this.configuration.eInvoice = facturae.isChecked();
            this.dispatchEvent(new Event(EVENT.CHANGE));
		});
		table.addCell(facturae, 1).style.height = '50px';
        facturae.setWidth('110px');
                table.addRow();
    }

    buildTicketBai(table) {
        if(!isValid(this.configuration.company.document)) {
            this.showError('No es posible activar Ticket Bai. El documento de la empresa no es válido.');
        }
        let active = createSwitch(this.TBAI_ACTIVE, MSG.TICKETBAI);
        active.checked = this.configuration.communication.tbai;
        if(!this.isConsole())
            active.disabled = this.configuration.communication.tbai || !isValid(this.configuration.company.document);
        active.addEventListener(EVENT.CHANGE, () => {
            if(active.isChecked()) {
                if(this.configuration.administration === 'BIZKAIA' && !this.configuration.company.legalPerson){
                    this.buildPerson();
                }
                this.getElement(this.TBAI_TEST).classList.remove(CSS.AON_NONE);
                this.getElement(this.TBAI_INCLUDE_DATE).classList.remove(CSS.AON_NONE);
                this.getElement(this.TBAI_REGISTRY_DATE).classList.remove(CSS.AON_NONE);
            } else {
                this.getElement(this.TBAI_TEST).classList.add(CSS.AON_NONE);
      	        this.getElement(this.TBAI_INCLUDE_DATE).classList.add(CSS.AON_NONE);
                this.getElement(this.TBAI_REGISTRY_DATE).classList.add(CSS.AON_NONE);
            }

            this.configuration.communication.tbai = active.isChecked();
            this.dispatchEvent(new Event(EVENT.CHANGE));
	    });
    	table.addCell(active, 1).style.height = '50px';
        active.setWidth('110px');

        let test = createSwitch(this.TBAI_TEST, MSG.TEST_ENVIRONMENT);
        test.checked = this.configuration.communication.tbaiTest;
        if(!this.configuration.communication.tbaiTest && !this.isConsole()) 
            test.disabled = true; 
        if(!this.configuration.communication.tbai) {
            test.classList.add(CSS.AON_NONE);
        }
        test.addEventListener(EVENT.CHANGE, () => {
		    this.configuration.communication.tbaiTest = test.isChecked();
            this.dispatchEvent(new Event(EVENT.CHANGE));
		});
        table.addCell(test, 1).style.height = '50px';
    
        table.addRow();

        let tbaiRegistryDate = createSelect(this.TBAI_REGISTRY_DATE, 'Fecha Registro (TBAI)');

        if(!this.configuration.communication.tbai) {
            tbaiRegistryDate.classList.add(CSS.AON_NONE);
        }
        
        table.addCell(tbaiRegistryDate, 1).style.height = '50px';
        tbaiRegistryDate.setOptions([
            {value: 'tax', name: 'Fecha IVA'},
            {value: 'audit', name: 'Fecha Auditoria'}
        ]);
        tbaiRegistryDate.value = this.configuration.communication.tbaiRegistryDate;
        tbaiRegistryDate.addEventListener(EVENT.CHANGE, () => {
            this.configuration.communication.tbaiRegistryDate = tbaiRegistryDate.value;
            this.dispatchEvent(new Event(EVENT.CHANGE));
        });

        let tbaiIncludeDate = createDate(this.TBAI_INCLUDE_DATE, 'Fecha Inclusión TBAI');
        if(!this.configuration.communication.tbai) {
            tbaiIncludeDate.classList.add(CSS.AON_NONE);
        }
        if(this.configuration.communication.tbaiIncludeDate) {
            tbaiIncludeDate.setDate(this.configuration.communication.tbaiIncludeDate);
        }
        tbaiIncludeDate.addEventListener(EVENT.CHANGE, () => {
            this.configuration.communication.tbaiIncludeDate = tbaiIncludeDate.getDateValue();
            this.dispatchEvent(new Event(EVENT.CHANGE));
        });

        table.addCell(tbaiIncludeDate, 1).style.height = '50px';

        table.addRow();
    }

    buildVerifactu(table) {
        if(!isValid(this.configuration.company.document)) {
            this.showError('No es posible activar Verifactu. El documento de la empresa no es válido.');
        }
        let active = createSwitch(this.VERIFACTU_ACTIVE, MSG.VERIFACTU);
        active.checked = this.configuration.communication.verifactu;
        if(!this.isConsole())
            active.disabled = this.configuration.communication.verifactu || !isValid(this.configuration.company.document); 
        active.addEventListener(EVENT.CHANGE, () => {
            if(active.isChecked()) {
                // this.getElement(this.VERIFACTU_TEST).classList.remove(CSS.AON_NONE);
                const date = this.getVerifactuDate();
                this.configuration.communication.verifactuIncludeDate = date;
                let verifactuIncludeDate = this.getElement(this.VERIFACTU_INCLUDE_DATE);
                verifactuIncludeDate.setDate(date);
                verifactuIncludeDate.classList.remove(CSS.AON_NONE);
            } else {
                // this.getElement(this.VERIFACTU_TEST).classList.add(CSS.AON_NONE);
                this.getElement(this.VERIFACTU_INCLUDE_DATE).classList.add(CSS.AON_NONE);
            }
      	    this.configuration.communication.verifactu = active.isChecked();

            this.dispatchEvent(new Event(EVENT.CHANGE));
	    });
    	table.addCell(active, 1).style.height = '50px';
        active.setWidth('110px');

        // let test = createSwitch(this.VERIFACTU_TEST, MSG.TEST_ENVIRONMENT);
        // test.checked = this.configuration.communication.verifactuTest;
        // test.disabled = !this.isConsole() || !this.configuration.communication.verifactuTest;
        // if(!this.configuration.communication.verifactu) {
        //     test.classList.add(CSS.AON_NONE);
        // }
        // test.addEventListener(EVENT.CHANGE, () => {
		//     this.configuration.communication.verifactuTest = test.isChecked();
        //     this.dispatchEvent(new Event(EVENT.CHANGE));
		// });
        // table.addCell(test, 1).style.height = '50px';

        let verifactuIncludeDate = createDate(this.VERIFACTU_INCLUDE_DATE, 'Fecha Inclusión Verifactu');
        if(!this.configuration.communication.verifactu) {
            verifactuIncludeDate.classList.add(CSS.AON_NONE);
        }
        if(this.configuration.communication.verifactuIncludeDate) {
            verifactuIncludeDate.setDate(this.configuration.communication.verifactuIncludeDate);
            verifactuIncludeDate.disabled = true;
        }
        verifactuIncludeDate.addEventListener(EVENT.CHANGE, () => {
            if(this.checkVerifactuDate(verifactuIncludeDate))
                this.configuration.communication.verifactuIncludeDate = verifactuIncludeDate.getDateValue();
            this.dispatchEvent(new Event(EVENT.CHANGE));
        });

        table.addCell(verifactuIncludeDate, 1).style.height = '50px';
    
        table.addRow();
    }

    checkVerifactuDate(verifactuIncludeDate) {
        const selectedDate = verifactuIncludeDate.getDate();
        const now = new Date();
        const nowDate = new Date(now.getFullYear(), now.getMonth(), now.getDate());
        const maxDate = isPersonaFisica(this.configuration.company.document) ? new Date(2026, 6, 1) : new Date(2026, 0, 1);
        const minDate = new Date(2025, 11, 1);

        if(selectedDate < minDate) {
            this.showError('La fecha es anterior al 1 de Diciembre de 2025.');
            verifactuIncludeDate.setDate(this.getVerifactuDate());
            return false;
        }

        if(now < maxDate && selectedDate > maxDate) {
            this.showError(isPersonaFisica(this.configuration.company.document) 
                ? 'La fecha es posterior al 1 de Julio de 2026.'
                : 'La fecha es posterior al 1 de Enero de 2026.');
            verifactuIncludeDate.setDate(this.getVerifactuDate());
            return false;
        }
        if(selectedDate < nowDate) {
            this.showError('La fecha seleccionada no puede ser anterior a la fecha actual.');
            verifactuIncludeDate.setDate(this.getVerifactuDate());
            return false;
        }
        return true;
    }
    
    getVerifactuDate () {
        const now = new Date();
        const date = new Date(2026, 0, 1);
        const nowDate = new Date(now.getFullYear(), now.getMonth(), now.getDate());
        return date > nowDate ? date : nowDate;
    }

    buildSii(table) {
        if(!isValid(this.configuration.company.document)) {
            this.showError('No es posible activar SII. El documento de la empresa no es válido.');
        }
        let siiActive = createSwitch(this.SII_ACTIVE, MSG.SII);
        siiActive.checked = this.configuration.communication.sii;
        if(!this.isConsole())
            siiActive.disabled = !this.configuration.communication.sii && !isValid(this.configuration.company.document);
        siiActive.addEventListener(EVENT.CHANGE, () => {
            if(siiActive.isChecked()) {
                this.getElement(this.SII_TEST).classList.remove(CSS.AON_NONE);
                this.getElement(this.SII_REGISTRY_DATE).classList.remove(CSS.AON_NONE);
                this.getElement(this.SII_INCLUDE_DATE).classList.remove(CSS.AON_NONE);
                this.getElement(this.SII_AUTOSEND).classList.remove(CSS.AON_NONE);
            } else {
                this.getElement(this.SII_TEST).classList.add(CSS.AON_NONE);
                this.getElement(this.SII_REGISTRY_DATE).classList.add(CSS.AON_NONE);
                this.getElement(this.SII_INCLUDE_DATE).classList.add(CSS.AON_NONE);
                this.getElement(this.SII_AUTOSEND).classList.add(CSS.AON_NONE);
            }
			this.configuration.communication.sii = siiActive.isChecked();
            this.dispatchEvent(new Event(EVENT.CHANGE));
		});
		table.addCell(siiActive, 1).style.height = '50px';
        siiActive.setWidth('110px');

        let siiTest = createSwitch(this.SII_TEST, MSG.TEST_ENVIRONMENT);
        siiTest.checked = this.configuration.communication.siiTest;
        siiTest.disabled = !this.isBeta();
        if(!this.configuration.communication.sii) {
            siiTest.classList.add(CSS.AON_NONE);
        }
        siiTest.addEventListener(EVENT.CHANGE, () => {
			this.configuration.communication.siiTest = siiTest.isChecked();
            this.dispatchEvent(new Event(EVENT.CHANGE));
		});
        table.addCell(siiTest, 1).style.height = '50px';

        table.addRow();

        let siiRegistryDate = createSelect(this.SII_REGISTRY_DATE, 'Fecha Registro (SII)');
        if(!this.configuration.communication.sii) {
            siiRegistryDate.classList.add(CSS.AON_NONE);
        }
        table.addCell(siiRegistryDate, 1).style.height = '50px';
        siiRegistryDate.setOptions([
            {value: 'tax', name: 'Fecha IVA'},
            {value: 'audit', name: 'Fecha Auditoria'}
        ]);
        siiRegistryDate.value = this.configuration.communication.siiRegistryDate;
        siiRegistryDate.addEventListener(EVENT.CHANGE, () => {
            this.configuration.communication.siiRegistryDate = siiRegistryDate.value;
            this.dispatchEvent(new Event(EVENT.CHANGE));
        });

        let siiIncludeDate = createDate(this.SII_INCLUDE_DATE, 'Fecha Inclusión SII');
        if(!this.configuration.communication.sii) {
            siiIncludeDate.classList.add(CSS.AON_NONE);
        }
        if(this.configuration.communication.siiIncludeDate) 
            siiIncludeDate.setDate(this.configuration.communication.siiIncludeDate);
        siiIncludeDate.addEventListener(EVENT.CHANGE, () => {
            this.configuration.communication.siiIncludeDate = siiIncludeDate.getDateValue();
            this.dispatchEvent(new Event(EVENT.CHANGE));
        });

        table.addCell(siiIncludeDate, 1).style.height = '50px';

        table.addRow();

        let siiAutosend = createSwitch(this.SII_AUTOSEND, 'Enviar al SII al aceptar factura');
        siiAutosend.checked = this.configuration.communication.siiAutosend;
        if(!this.configuration.communication.sii) {
            siiAutosend.classList.add(CSS.AON_NONE);
        }
		siiAutosend.addEventListener(EVENT.CHANGE, () => {
			this.configuration.communication.siiAutosend = siiAutosend.isChecked();
            this.dispatchEvent(new Event(EVENT.CHANGE));
		});
		table.addCell(siiAutosend, 2).style.height = '50px';
        siiAutosend.setWidth('230px');
    }

    showError(error) {
        this.showToast({
            type: CONSTANT.ERROR,
            message: error
        });
    }

    showToast(object) {
        let toast = this.getElement(this.id + 'Toast');
        if(!toast){
            toast = new AonToast();
            toast.id = this.id + 'Toast';
            this.appendChild(toast);
        }
        toast.start(object);
    }
 
    isTicketBai() {
        return this.configuration.administration === 'ALAVA'
            || this.configuration.administration === 'BIZKAIA'
            || this.configuration.administration === 'GIPUZKOA';
    }

    isNavarra() {
        return this.configuration.administration === 'NAVARRA';
    }

    getConfiguration() {
        return this.configuration;
    }

    setConfiguration(configuration) {
        this.configuration = configuration;
    }

    buildPerson() {
        let div = this.createElement(TAG.DIV);

        let name = createInput(this.id + 'personName', MSG.NAME, div);
        name.setValue(this.configuration.company.name);

        let surname1 = createInput(this.id + 'personSurname1', MSG.SURNAME + ' 1', div);
        let surname2 = createInput(this.id + 'personSurname2', MSG.SURNAME + ' 2', div);

        let d = new AonDialog();
        d.id = this.id + 'PersonDialog';
        this.appendChild(d);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.NAME);
		d.setContent(div);
		d.addAcceptAction(() => {
            this.configuration.company.person = {
                name: name.value,
                surname1: surname1.value,
                surname2: surname2.value
            };
			if(this.autosave) this.save();
		});
		d.open();
	}
}
if(!window.customElements.get(TAG.AON_INVOICE_COMMUNICATION)){
	window.customElements.define(TAG.AON_INVOICE_COMMUNICATION, AonInvoiceCommunication);
}