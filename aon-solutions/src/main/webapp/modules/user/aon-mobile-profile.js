import { AonAvatar } from '../../components/aon-avatar.js';
import { AonDialog } from '../../components/aon-dialog.js';
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

        let configuration = new AonIconButton();
        configuration.id = this.id + 'Configuration';
        configuration.icon = 'settings';
        configuration.style.right = '20px';
        configuration.style.position = 'absolute';
        configuration.style.marginTop = '5px';
        configuration.addEventListener(EVENT.CLICK, () => 
            this.rootPanelHtml('<aon-configuration id="aon-configuration"></aon-configuration>'));
        div.appendChild(configuration);

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
        aonIconButton.color = 'white'
        aonIconButton.background = 'red';
        aonIconButton.style.opacity = '0.5';
        aonIconButton.noHover = true;
        span.appendChild(aonIconButton);
        this.appendChild(span);
        aonIconButton.addEventListener(EVENT.CLICK, () => this.closeSession());
    }

    closeSession() {
        let d = new AonDialog();
        this.appendChild(d);
        d.clear();
    	if(!this.isMobile()) d.width = '400px';
    	d.setTitle('Cerrar Sesión');
   	 	d.setContentHTML(`Estás seguro de cerrar sesión`);
    	d.addAcceptAction(() => {
            closeSession();
    	});
    	d.open();
    }
}

if(!window.customElements.get(TAG.AON_MOBILE_PROFILE)){
    window.customElements.define(TAG.AON_MOBILE_PROFILE,  AonMobileProfile);
}