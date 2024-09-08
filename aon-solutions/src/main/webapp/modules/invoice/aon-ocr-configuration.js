import { AonElement } from "../../components/AonElement.js";
import { CSS, EVENT, TAG, MSG, CONSTANT } from "../../environments/environments.js";
import { AonSwitch } from "../../components/aon-switch.js";
import { invofoxLogin } from "../../services/invofoxService.js";
import { AonIcon } from "../../components/aon-icon.js";
import { createCard, createEmail, createInput, createSelect } from "../../components/CreateComponent.js";

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

        this.USER = this.id + 'User';
        this.PASSWORD = this.id + 'Password';
        this.SIGN_IN = this.id + 'SignIn';
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
        let card = createCard(this.CARD, 'Configuración de Invofox');
        card.style.width = '50%';
        parent.appendChild(card);
        
        let content = this.createDiv(this.CARD_DIV);
		card.setContent(content);

        if(this.configuration.loginRequired) {
            this.buildInvofoxLogin(content);
        } else this.buildInvofoxConfiguration(content);
    }

    reload(content, configuration) {
        this.configuration = configuration;
        this.clearElement(content);
        
        if(this.configuration.loginRequired) {
            this.buildInvofoxLogin(content);
        } else this.buildInvofoxConfiguration(content);
    }

    buildInvofoxLogin(content) {
        let aonIcon = new AonIcon();
        aonIcon.icon = "invofox";
        aonIcon.size = "100px"
        aonIcon.style.marginLeft = '40%';
        content.appendChild(aonIcon);

        let userInput = createEmail(this.USER, MSG.USER);
        userInput.setRequired(true);
        content.appendChild(userInput);
    
        let passwordInput = createInput(this.PASSWORD, MSG.PASSWORD);
        passwordInput.setRequired(true);
        passwordInput.type = 'password';
        content.appendChild(passwordInput);
    
        // Buttons
        let signIn = this.createElement(TAG.BUTTON);
        signIn.id = this.SIGN_IN;
        signIn.className = CSS.AON_LOGIN_BUTTON;
        signIn.title = MSG.SIGN_IN;
        signIn.innerHTML = MSG.SIGN_IN.toUpperCase();
        signIn.addEventListener(EVENT.CLICK, () => {
            let data = {
                user: userInput.value,
                password: passwordInput.value
            }
            invofoxLogin(data).then(r => {
                this.reload(content, r);
            }).catch(e => {

            });
        });
        content.appendChild(signIn);
    }

    buildInvofoxConfiguration(content) {
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
        let env = createSelect(this.ENVIRONMENT, "Entorno"); 
        let enabled = this.configuration.personalized || this.isConsole() || this.isBeta();
        if(!enabled) env.disabled = CONSTANT.DISABLED;
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