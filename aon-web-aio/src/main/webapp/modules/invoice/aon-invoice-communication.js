import { AonElement } from "../../components/AonElement.js";
import { CSS, EVENT, MSG, TAG } from "../../environments/environments.js";
import { AonCard } from "../../components/aon-card.js";
import { AonBasicTable } from "../../components/aon-basic-table.js";
import { AonSwitch } from "../../components/aon-switch.js";
import { AonInput } from "../../components/aon-input.js";
import { AonDialog } from "../../components/aon-dialog.js";
import { createDate, createSelect } from "../../components/CreateComponent.js";

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
        let card = new AonCard();
		card.id = this.CARD;
        card.title = 'Comunicaciones';
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
        } else if(!this.isNavarra() && this.isBeta()){
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
        administration.onChange(() => {
            this.configuration.administration = administration.value;
            if(this.isTicketBai() || this.isNavarra()) {
                this.configuration.verifactu.active = false;
            } else this.configuration.tbai.active = false;
            this.reload();
            this.dispatchEvent(new Event(EVENT.CHANGE));
        });

        table.addRow();
    }

    buildFacturae(table) {
        let facturae = new AonSwitch();
        facturae.id = this.FACTURAE;
		facturae.title = 'Facturae';//MSG.FACTURAE;
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
        let active = new AonSwitch();
        active.id = this.TBAI_ACTIVE;
 		active.title = MSG.TICKETBAI;
    	active.checked = this.configuration.tbai.active;
        if(!this.isConsole())
            active.disabled = this.configuration.tbai.active; 
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
            
            this.configuration.tbai.active = active.isChecked();
            this.dispatchEvent(new Event(EVENT.CHANGE));
	    });
    	table.addCell(active, 1).style.height = '50px';
        active.setWidth('110px');

        let test = new AonSwitch();
        test.id = this.TBAI_TEST;
		test.title = MSG.TEST_ENVIRONMENT;
	    test.checked = this.configuration.tbai.test;
        if(!this.configuration.tbai.test && !this.isConsole()) 
            test.disabled = true; 
        if(!this.configuration.tbai.active) {
            test.classList.add(CSS.AON_NONE);
        }
        test.addEventListener(EVENT.CHANGE, () => {
		    this.configuration.tbai.test = test.isChecked();
            this.dispatchEvent(new Event(EVENT.CHANGE));
		});
        table.addCell(test, 1).style.height = '50px';
    
        table.addRow();

        let tbaiRegistryDate = createSelect(this.TBAI_REGISTRY_DATE, 'Fecha Registro (TBAI)');
 
        if(!this.configuration.tbai.active) {
            tbaiRegistryDate.classList.add(CSS.AON_NONE);
        }
        
        table.addCell(tbaiRegistryDate, 1).style.height = '50px';
        tbaiRegistryDate.setOptions([
            {value: 'tax', name: 'Fecha IVA'},
            {value: 'audit', name: 'Fecha Auditoria'}
        ]);
        tbaiRegistryDate.value = this.configuration.tbai.registryDate;
        tbaiRegistryDate.onChange(() => {
            this.configuration.tbai.registryDate = tbaiRegistryDate.value;
            this.dispatchEvent(new Event(EVENT.CHANGE));
        });

        let tbaiIncludeDate = createDate(this.TBAI_INCLUDE_DATE, 'Fecha Inclusión TBAI');
        if(!this.configuration.tbai.active) {
            tbaiIncludeDate.classList.add(CSS.AON_NONE);
        }
        tbaiIncludeDate.onChange(() => {
            this.configuration.tbai.includeDate = tbaiIncludeDate.value;
            this.dispatchEvent(new Event(EVENT.CHANGE));
        });
        table.addCell(tbaiIncludeDate, 1).style.height = '50px';
        if(this.configuration.tbai.includeDate) {
            tbaiIncludeDate.value = this.configuration.tbai.includeDate;
        }
        table.addRow();
    }

    buildVerifactu(table) {
        let active = new AonSwitch();
        active.id = this.VERIFACTU_ACTIVE;
 		active.title = "Verifactu";
    	active.checked = this.configuration.verifactu.active;
        if(!this.isConsole())
            active.disabled = this.configuration.verifactu.active; 
        active.addEventListener(EVENT.CHANGE, () => {
            if(active.isChecked()) {
                this.getElement(this.VERIFACTU_TEST).classList.remove(CSS.AON_NONE);
                this.getElement(this.VERIFACTU_INCLUDE_DATE).classList.remove(CSS.AON_NONE);
                this.getElement(this.VERIFACTU_REGISTRY_DATE).classList.remove(CSS.AON_NONE);
            } else {
                this.getElement(this.VERIFACTU_TEST).classList.add(CSS.AON_NONE);
                this.getElement(this.VERIFACTU_INCLUDE_DATE).classList.add(CSS.AON_NONE);
                this.getElement(this.VERIFACTU_REGISTRY_DATE).classList.add(CSS.AON_NONE);
            }
      	    this.configuration.verifactu.active = active.isChecked();

            this.dispatchEvent(new Event(EVENT.CHANGE));
	    });
    	table.addCell(active, 1).style.height = '50px';
        active.setWidth('110px');

        let test = new AonSwitch();
        test.id = this.VERIFACTU_TEST;
		test.title = MSG.TEST_ENVIRONMENT;
	    test.checked = this.configuration.verifactu.test;
        if(!this.configuration.verifactu.test && !this.isConsole()) 
            test.disabled = true; 
        if(!this.configuration.verifactu.active) {
            test.classList.add(CSS.AON_NONE);
        }
        test.addEventListener(EVENT.CHANGE, () => {
		    this.configuration.verifactu.test = test.isChecked();
            this.dispatchEvent(new Event(EVENT.CHANGE));
		});
        table.addCell(test, 1).style.height = '50px';
    
        table.addRow();

        let verifactuRegistryDate = createSelect(this.VERIFACTU_REGISTRY_DATE, 'Fecha Registro (VERIFACTU)');
        if(!this.configuration.verifactu.active) {
            verifactuRegistryDate.classList.add(CSS.AON_NONE);
        }
        table.addCell(verifactuRegistryDate, 1).style.height = '50px';
        verifactuRegistryDate.setOptions([
            {value: 'tax', name: 'Fecha IVA'},
            {value: 'audit', name: 'Fecha Auditoria'}
        ]);
        verifactuRegistryDate.value = this.configuration.verifactu.registryDate;
        verifactuRegistryDate.onChange(() => {
            this.configuration.verifactu.registryDate = verifactuRegistryDate.value;
            this.dispatchEvent(new Event(EVENT.CHANGE));
        });

        let verifactuIncludeDate = createDate(this.VERIFACTU_INCLUDE_DATE, 'Fecha Inclusión VERIFACTU');
        if(!this.configuration.verifactu.active) {
            verifactuIncludeDate.classList.add(CSS.AON_NONE);
        }
        verifactuIncludeDate.onChange(() => {
            this.configuration.verifactu.includeDate = verifactuIncludeDate.value;
            this.dispatchEvent(new Event(EVENT.CHANGE));
        });
        table.addCell(verifactuIncludeDate, 1).style.height = '50px';
        if(this.configuration.verifactu.includeDate) {
            verifactuIncludeDate.value = this.configuration.verifactu.includeDate;
        }
        table.addRow();
    }

    buildSii(table) {
        let siiActive = new AonSwitch();
        siiActive.id = this.SII_ACTIVE;
		siiActive.title = "SII";
		siiActive.checked = this.configuration.sii.active;
		siiActive.addEventListener(EVENT.CHANGE, () => {
            if(siiActive.isChecked()) {
                this.getElement(this.SII_TEST).classList.remove(CSS.AON_NONE);
                this.getElement(this.SII_REGISTRY_DATE).classList.remove(CSS.AON_NONE);
                this.getElement(this.SII_INCLUDE_DATE).classList.remove(CSS.AON_NONE);
                this.getElement(this.SII_AUTOSEND).classList.remove(CSS.AON_NONE);
            } else {0
                this.getElement(this.SII_TEST).classList.add(CSS.AON_NONE);
                this.getElement(this.SII_REGISTRY_DATE).classList.add(CSS.AON_NONE);
                this.getElement(this.SII_INCLUDE_DATE).classList.add(CSS.AON_NONE);
                this.getElement(this.SII_AUTOSEND).classList.add(CSS.AON_NONE);
            }
			this.configuration.sii.active = siiActive.isChecked();
            this.dispatchEvent(new Event(EVENT.CHANGE));
		});
		table.addCell(siiActive, 1).style.height = '50px';
        siiActive.setWidth('110px');

        let siiTest = new AonSwitch();
        siiTest.id = this.SII_TEST;
		siiTest.title = MSG.TEST_ENVIRONMENT;
		siiTest.checked = this.configuration.sii.test;
        siiTest.disabled = !this.isBeta();
        if(!this.configuration.sii.active) {
            siiTest.classList.add(CSS.AON_NONE);
        }
        siiTest.addEventListener(EVENT.CHANGE, () => {
			this.configuration.sii.test = siiTest.isChecked();
            this.dispatchEvent(new Event(EVENT.CHANGE));
		});
        table.addCell(siiTest, 1).style.height = '50px';

        table.addRow();

        let siiRegistryDate = createSelect(this.SII_REGISTRY_DATE, 'Fecha Registro (SII)');
        if(!this.configuration.sii.active) {
            siiRegistryDate.classList.add(CSS.AON_NONE);
        }
        table.addCell(siiRegistryDate, 1).style.height = '50px';
        siiRegistryDate.setOptions([
            {value: 'tax', name: 'Fecha IVA'},
            {value: 'audit', name: 'Fecha Auditoria'}
        ]);
        siiRegistryDate.value = this.configuration.sii.registryDate;
        siiRegistryDate.onChange(() => {
            this.configuration.sii.registryDate = siiRegistryDate.value;
            this.dispatchEvent(new Event(EVENT.CHANGE));
        });

        let siiIncludeDate = createDate(this.SII_INCLUDE_DATE, 'Fecha Inclusión SII');
        if(!this.configuration.sii.active) {
            siiIncludeDate.classList.add(CSS.AON_NONE);
        }
        siiIncludeDate.onChange(() => {
            this.configuration.sii.includeDate = siiIncludeDate.value;
            this.dispatchEvent(new Event(EVENT.CHANGE));
        });
        table.addCell(siiIncludeDate, 1).style.height = '50px';
        siiIncludeDate.value = this.configuration.sii.includeDate;
        
        table.addRow();

        let siiAutosend = new AonSwitch();
        siiAutosend.id = this.SII_AUTOSEND;
		siiAutosend.title = 'Enviar al SII al aceptar factura';
		siiAutosend.checked = this.configuration.sii.autosend;
        if(!this.configuration.sii.active) {
            siiAutosend.classList.add(CSS.AON_NONE);
        }
		siiAutosend.addEventListener(EVENT.CHANGE, () => {
			this.configuration.sii.autosend = siiAutosend.isChecked();
            this.dispatchEvent(new Event(EVENT.CHANGE));
		});
		table.addCell(siiAutosend, 2).style.height = '50px';
        siiAutosend.setWidth('230px');
    }

    isTicketBai() {
        return this.configuration.administration === 'ALAVA'
            || this.configuration.administration === 'BIZKAIA'
            || this.configuration.administration === 'GIPUZKOA';
    }

    isNavarra() {
        return this.configuration.administration === 'NAVARRA';
    }


    getAdministrationConfiguration() {
        return this.configuration.administration;
    }

    getFacturaeConfiguration() {
        return this.configuration.eInvoice;
    }

    getSiiConfiguration() {
        return this.configuration.sii;
    }

    getVerifactuConfiguration() {
        return this.configuration.verifactu;
    }

    getTbaiConfiguration() {
        return this.configuration.tbai;
    }

    setConfiguration(configuration) {
        this.configuration = configuration;
    }

    buildPerson() {
        let div = this.createElement(TAG.DIV);
        let name = new AonInput();
		name.id = this.id + 'personName';
		name.title = MSG.NAME;
        name.description = MSG.NAME;
		name.value = this.configuration.company.name;
        div.appendChild(name);

        let surname1 = new AonInput();
		surname1.id = this.id + 'personSurname1';
		surname1.title = MSG.SURNAME + ' 1';
        surname1.description = MSG.SURNAME + ' 1';
        div.appendChild(surname1);
        
        let surname2 = new AonInput();
		surname2.id = this.id + 'personSurname2';
		surname2.title = MSG.SURNAME + ' 2';
        surname2.description = MSG.SURNAME + ' 2';
        div.appendChild(surname2);

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