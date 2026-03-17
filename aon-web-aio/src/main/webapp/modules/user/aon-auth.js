import { AonDialog } from '../../components/aon-dialog.js';
import { AonToast } from '../../components/aon-toast.js';
import { AonToolbar } from '../../components/aon-toolbar.js';
import {AonElement} from '../../components/AonElement.js';
import { createCard, createInput } from '../../components/CreateComponent.js';
import { MSG, CONSTANT, TAG, CSS, EVENT } from '../../environments/environments.js';
import { ToolbarType } from '../../models/enums.js';
import { changePassword, checkVerification, getAuth, saveAuth, sendVerification } from '../../services/authService.js';
import * as ACTION from '../actions.js';

export class AonAuth extends AonElement {
 
	auth;
    
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
		getAuth().then(r => {
			this.auth = r;
			this.initialize();
			this.build();
		});
  	}
    
    initialize() {
    	this.id = this.id || 'aonAuth';
    }

    build() {
        this.buildToolbar();
        this.buildContent();
    }

    buildToolbar(){
		let toolbar = new AonToolbar();
		toolbar.id = this.TOOLBAR;
		toolbar.type = ToolbarType.SECONDARY;
		toolbar.title = MSG.USER;
		this.appendChild(toolbar);

		toolbar.removeButtons();
		toolbar.addButton2(ACTION.CHANGE_PASSWORD, () => this.changePasswordDialog());
		toolbar.addButton2(ACTION.SAVE, () => this.save().then(( auth ) => this.dispatchEvent(new CustomEvent(EVENT.CLOSE, { detail: auth }))).catch(() => {}));
	}

    buildContent() {
		let div = this.createElement(TAG.DIV);
		div.className = this.isMobile() 
            ? CSS.AON_MOBILE_SUB_CONTENT 
            : CSS.AON_SUB_CONTENT;
		div.style.display = 'flex';
		div.style.width	= '100%';
		this.appendChild(div);

        let authCard = createCard("aonConfigurationUserCard", MSG.USER);
		authCard.style.width = this.isMobile() ? '100%' : '50%';
		div.appendChild(authCard);
        this.buildAuthContent(authCard);
    }

    buildAuthContent(card) {
		let div = this.createElement(TAG.DIV);
		card.setContent(div);

		let email = createInput('aonConfigurationUserCardEmail', MSG.EMAIL);
		email.value = this.auth?.email || '';
		div.appendChild(email);
		email.addEventListener(EVENT.CHANGE , () => {
			this.auth.email = email.value;
		});

		let name = createInput('aonConfigurationUserCardName', MSG.NAME);
		name.className = CSS.AON_WIDTH_25;
		name.description = MSG.NAME;
		name.value = this.auth?.name || '';
		name.style.marginRight = '2px';
		div.appendChild(name);
		name.addEventListener(EVENT.CHANGE , () => {
			this.auth.name = name.value;
		});

		let surname = createInput('aonConfigurationUserCardSurname', MSG.SURNAME);
		surname.className = CSS.AON_WIDTH_75;
		surname.description = MSG.SURNAME;
		surname.value = this.auth?.surname || '';
		div.appendChild(surname);
		surname.addEventListener(EVENT.CHANGE , () => {
			this.auth.surname = surname.value;
		});

		let document = createInput('aonConfigurationUserCardDocument', MSG.DOCUMENT);
		document.className = CSS.AON_WIDTH_50;
		document.description = MSG.DOCUMENT;
		document.value = this.auth?.document || '';
		document.style.marginRight = '2px';
		div.appendChild(document);
		document.addEventListener(EVENT.CHANGE , () => {
			this.auth.document = document.value;
		});

		let phone = createInput('aonConfigurationUserCardPhone', MSG.PHONE);
		phone.className = CSS.AON_WIDTH_50;
		phone.description = MSG.PHONE;
		phone.value = this.auth?.phone || '';
		div.appendChild(phone);
		phone.addEventListener(EVENT.CHANGE , () => {
			this.auth.phone = phone.value;
		});
	}

    changePassword(data) {
        this.verification(() =>
			changePassword(data)
				.then(()=>{
					this.showToast({message:MSG.SAVED_DATA, type:CONSTANT.SUCCESS});
				}).catch(e=>this.showError(e)));
    }

	save() {
		return new Promise((resolve, reject) => {
			this.verification(() => {
				saveAuth(this.auth).then((auth) => resolve(auth) ).catch(reject);
			});
		});
	}


    verification(fn) {
		getAuth().then(auth => {
			sendVerification(auth).then(r => {
				this.verificationDialog(r, fn);
			});
		});
    }

    verificationDialog(data, fn) {
        let dialog = this.getDialog();
		dialog.clear();
		if(this.isMobile()) {
            dialog.type = "fullscreen";
        } else {
            dialog.width = '400px';
        }
        dialog.setTitle(MSG.EMAIL_VERIFICATION);
		dialog.setDescription(MSG.EMAIL_VERIFICATION_DESCRIPTION);

        let div = this.createElement(TAG.DIV);
		div.style.marginTop = '20px';
		dialog.setContent(div);

        let codeInput = createInput("aonAuthCode", MSG.VERIFICATION_CODE);
        div.appendChild(codeInput)
		codeInput.addEventListener(EVENT.CHANGE , () => {
            data.value = codeInput.value 
            checkVerification(data).then(check => {
				if(!check.result) {
					codeInput.addError("El código no es correcto");
					this.showError({message: MSG.WRONG_CODE, type:CONSTANT.ERROR});
				} else codeInput.removeError();
			});
		});

		dialog.addAcceptAction(() => {
			checkVerification(data).then(check => {
				if(check.result) fn(); 
				else this.showError({message: MSG.WRONG_CODE, type:CONSTANT.ERROR});
			});
		});
		dialog.open();
    }

	changePasswordDialog() {
		let dialog = this.getDialog();
		dialog.clear();
		if(this.isMobile()) {
            dialog.type = "fullscreen";
        } else {
            dialog.width = '400px';
        }
		dialog.setTitle(MSG.CHANGE_PASSWORD);

		let div = document.createElement("div");
		div.style.marginTop = '20px';
        let password = createInput("aonConfigurationUserCardPassword", MSG.NEW_PASSWORD);
        password.type = "password";
        div.appendChild(password);

        let password2 = createInput("aonConfigurationUserCardPassword2", MSG.REPEAT_PASSWORD);
        password2.type = "password";
        div.appendChild(password2);

		dialog.setContent(div);

		dialog.addAcceptAction(() => {
			if(password.value === password2.value) {
				let data = {
					password: password.value
				};
				this.changePassword(data);
			} else {
				this.showToast({message:'Las contraseñas no coinciden.', type:CONSTANT.ERROR});
			}
		});
		d.open();
	}

	getDialog() {
		let dialog = new AonDialog();
		document.body.appendChild(dialog);
		dialog.addEventListener(EVENT.CLOSE, () => {
			dialog.remove();
		});
		return dialog;	
	}

	showError(e) {
	    this.showToast(e);
  	}

	showToast(obj) {
        let toast = this.getElement('aonAuthToast');
		if(!toast){
			toast = new AonToast();
			toast.id = 'aonAuthToast';
	        document.body.appendChild(toast);
		}
        toast.start(obj);   
	}
	
	onclose(fn) {
        this.addEventListener(EVENT.CLOSE, fn);
		return this;
    }
}
if(!window.customElements.get(TAG.AON_AUTH)){
	window.customElements.define(TAG.AON_AUTH, AonAuth);
}