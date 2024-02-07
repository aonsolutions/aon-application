import { AonElement } from "../../components/AonElement.js";
import { EVENT, TAG } from "../../environments/environments.js";
import { AonCard } from "../../components/aon-card.js";
import { AonSwitch } from "../../components/aon-switch.js";

export class AonOcrConfiguration extends AonElement {
    
    configuration;
    
    connectedCallback () {
        this.initialize();
        this.build();
  	}

    initialize() {
        this.id = this.id || 'aonInvoiceConfigurationCommunication';
        this.DIV = this.id + 'Div';
        this.TEST = this.id + 'Test';
        this.CARD = this.id + 'Card';
        this.CARD_DIV = this.CARD + 'Div';

    }

    build() {
        let div = this.createElement(TAG.DIV);
        div.id = this.DIV;
        div.style.display = 'flex';
        div.style.width = '100%';
        this.appendChild(div);

        this.buildCard(div);
    }
    
    buildCard(parent) {
        let card = new AonCard();
		card.id = this.CARD;
        card.title = 'Configuración de Invofox';
        card.style.width = '50%';
        parent.appendChild(card);
        
        let content = this.createDiv(this.CARD_DIV);
		card.setContent(content);
    
        let test = new AonSwitch()
        test.id = this.TEST;
        test.title = 'Entorno de Pruebas';
        content.appendChild(test);
        test.checked = this.configuration.test;
        test.addEventListener(EVENT.CHANGE, () => this.configuration.test = test.checked);
    }

    getConfiguration() {
        return this.configuration;
    }

    setConfiguration(configuration) {
        this.configuration = configuration;
    }
}
if(!window.customElements.get(TAG.AON_OCR_CONFIGURATION)){
	window.customElements.define(TAG.AON_OCR_CONFIGURATION, AonOcrConfiguration);
}