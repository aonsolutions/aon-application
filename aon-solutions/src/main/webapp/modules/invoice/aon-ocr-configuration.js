import { AonElement } from "../../components/AonElement.js";
import { EVENT, TAG } from "../../environments/environments.js";
import { AonCard } from "../../components/aon-card.js";
import { AonSwitch } from "../../components/aon-switch.js";
import { AonSelect } from "../../components/aon-select.js";

export class AonOcrConfiguration extends AonElement {
    
    configuration;
    
    connectedCallback () {
        this.initialize();
        this.build();
  	}

    initialize() {
        this.id = this.id || 'aonInvoiceConfigurationCommunication';
        this.DIV = this.id + 'Div';
        this.PERSONALIZED = this.id + 'Personalized';
        this.ENVIRONMENT = this.id + 'Environment';
        // this.AUTO_ACCEPT = this.id + 'AutoAccept';
        this.AUTO_RECORD = this.id + 'AutoRecord';
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

        if(this.isConsole()) {
            let div0 = this.createDiv();
            div0.style.marginBottom = '10px';
            content.appendChild(div0)
            let personalized = new AonSwitch()
            personalized.id = this.AUTO_RECORD;
            personalized.title = 'Personalizado';
            div0.appendChild(personalized);
            personalized.checked = this.configuration.personalized;
            personalized.addEventListener(EVENT.CHANGE, () => this.configuration.personalized = personalized.checked);
        }

        let div1 = this.createDiv();
        div1.style.marginBottom = '10px';
        content.appendChild(div1);
        let env = new AonSelect();
        env.id = this.ENVIRONMENT;
        env.title = "Entorno"; 
        // env.disabled = !this.configuration.personalized || !this.isConsole();
        env.value = this.configuration.environment;
        env.setAlias('id', 'name');
		env.setOptions(this.configuration.environments);
        env.addEventListener(EVENT.SELECT,(e) => {
			this.configuration.environment = e.detail.id;
            this.configuration.apiKey = e.detail.apiKey;
		});
        div1.appendChild(env)

        // let div2 = this.createDiv();
        // div2.style.marginBottom = '10px';
        // content.appendChild(div2)
        // let autoAccept = new AonSwitch()
        // autoAccept.id = this.AUTO_ACCEPT;
        // autoAccept.title = 'Aceptar Facturas Automáticamente';
        // div2.appendChild(autoAccept);
        // autoAccept.checked = this.configuration.autoAccept;
        // autoAccept.addEventListener(EVENT.CHANGE, () => this.configuration.autoAccept = autoAccept.checked);

        let div3 = this.createDiv();
        div3.style.marginBottom = '10px';
        content.appendChild(div3)
        let autoRecord = new AonSwitch()
        autoRecord.id = this.AUTO_RECORD;
        autoRecord.title = 'Contabilizar Facturas Automáticamente';
        div3.appendChild(autoRecord);
        autoRecord.checked = this.configuration.autoRecord;
        autoRecord.addEventListener(EVENT.CHANGE, () => this.configuration.autoRecord = autoRecord.checked);
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