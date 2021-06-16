import { AonAvatar } from '../../components/aon-avatar.js';
import { AonIconButton } from '../../components/aon-icon-button.js';
import { AonElement } from '../../components/AonElement.js';
import { CONSTANT, EVENT, TAG } from '../../environments/environments.js';
import { closeSession, getAuth } from '../../services/authService.js';

export class AonMobileProfile extends AonElement {

    get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

    constructor () {
		super();
	}

	connectedCallback () {
        this.initialize();
        getAuth().then((user) => {
            this.build(user);
        });
    }

    initialize() {
        this.id = this.id || CONSTANT.AON_MOBILE_PROFILE;
    }

    build(user) {
        let div = this.createElement(TAG.DIV);
        div.style.margin = '20px';
        this.appendChild(div);  

        let avatar = new AonAvatar();
        div.appendChild(avatar);

        let name = this.createElement(TAG.SPAN);
        name.style.fontWeight = 'bold';
        name.style.marginLeft = '20px';
        name.innerHTML = user.name + ' ' + user.surname;
        div.appendChild(name);

        this.buildOption('mail', user.email);
        this.buildOption('fingerprint', user.document);
        this.buildOption('smartphone', user.phone);
        this.buildOption('password', 'Cambiar Contraseña');
        this.addCloseSessionButton();
    }

    buildOption(icon, value) {
        let div = this.createElement(TAG.DIV);
        div.style.margin = '20px';
        div.style.marginLeft = '40px';
        this.appendChild(div);  
        
        let i = this.createElement(TAG.I);
        i.className = 'material-icons';
        i.innerHTML = icon;
        div.appendChild(i);
       
        let val = this.createElement(TAG.SPAN);
        val.style.marginLeft = '20px';
        val.style.marginTop = '3px';
        val.style.position = 'absolute';
        val.innerHTML = value;
        div.appendChild(val);
    }

    addCloseSessionButton() {
        let span = this.getElement(this.id + "FloatSpan") || this.createElement(TAG.SPAN);
        span.id = this.id + "FloatSpan";
        span.style.position = "fixed";
        let n = (window.innerWidth / 5 - 40) / 2;
        span.style.right = n + 'px';
        span.style.bottom = this.isSab() ? "80px" : "70px";
        let aonIconButton = new AonIconButton();
        aonIconButton.icon = 'input';
        aonIconButton.id = this.id + "CloseSessionButton";
        aonIconButton.title = 'Cerrar Sesión';
        aonIconButton.background = "#f1f1f1";
        span.appendChild(aonIconButton);
        this.appendChild(span);
        aonIconButton.addEventListener(EVENT.CLICK, closeSession);
    }
}

if(!window.customElements.get(TAG.AON_MOBILE_PROFILE)){
    window.customElements.define(TAG.AON_MOBILE_PROFILE,  AonMobileProfile);
}