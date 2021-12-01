import { AonElement } from "../../components/AonElement.js";
import { EVENT, MSG, TAG } from "../../environments/environments.js";
import { AonCard } from "../../components/aon-card.js";
import { AonBasicTable } from "../../components/aon-basic-table.js";
import { AonSwitch } from "../../components/aon-switch.js";
import { AonSelect } from "../../components/aon-select.js";
import { getCertificates } from "../../services/documentalService.js";

export class AonInvoiceCommunication extends AonElement {
    
    configuration;
    
    connectedCallback () {
        this.initialize();
        this.build();
  	}

    initialize() {
        this.id = this.id || 'aonInvoiceConfigurationCommunication';
        this.DIV = this.id + 'Div';
        this.TBAI_CARD = this.id + 'Card';
        this.TBAI_TABLE = this.TBAI_CARD + 'Table';
        this.TBAI_DIV = this.TBAI_CARD + 'Div';
        this.TBAI_ACTIVE = this.TBAI_TABLE + 'Active';
        this.TBAI_DEFAULT_CERTIFICATE = this.TBAI_TABLE + 'DefaultCertificate';
        

    }

    build() {
        let div = this.createElement(TAG.DIV);
        div.id = this.DIV;
        div.style.display = 'flex';
        div.style.width = '100%';
        this.appendChild(div);

        this.buildTbai(div);
        this.buildSii(div);
        this.buildEInvoice(div);
    }
    
	buildTbai(parent) {
        let card = new AonCard();
		card.id = this.TBAI_CARD;
        card.title = 'TICKET BAI';
        card.style.width = '50%';
        parent.appendChild(card);
        
        let content = this.createElement(TAG.DIV);
        content.id = this.TBAI_DIV;
		card.setContent(content);

        let table = new AonBasicTable();
		table.id = this.TBAI_TABLE;
		content.appendChild(table);

        table.addRow();

        let active = new AonSwitch();
        active.id = this.TBAI_ACTIVE;
		active.title = MSG.ACTIVATE;
		active.checked = this.configuration.tbai.active;
		active.addEventListener(EVENT.CHANGE, () => {
			this.configuration.tbai.active = active.isChecked();
            this.dispatchEvent(new Event(EVENT.CHANGE));
		});
		table.addCell(active, 1);


        let test = new AonSwitch();
        test.id = this.TBAI_TEST;
		test.title = MSG.TEST_ENVIRONMENT;
		test.checked = true; //this.configuration.tbai.test;
        test.disabled = true;
        table.addCell(test, 1); 
    }

    buildSii() {
   
    }

    buildEInvoice() {
   
    }

    getTbaiConfiguration() {
        return this.configuration.tbai;
    }

    setConfiguration(configuration) {
        this.configuration = configuration;
    }
}
if(!window.customElements.get(TAG.AON_INVOICE_COMMUNICATION)){
	window.customElements.define(TAG.AON_INVOICE_COMMUNICATION, AonInvoiceCommunication);
}