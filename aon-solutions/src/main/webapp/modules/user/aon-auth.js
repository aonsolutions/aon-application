import { AonCard } from '../../components/aon-card.js';
import { AonInput } from '../../components/aon-input.js';
import { AonToolbar } from '../../components/aon-toolbar.js';
import {AonElement} from '../../components/AonElement.js';
import { DIV } from '../../environments/aonTag.js';
import { MSG, CONSTANT, TAG, CSS, EVENT } from '../../environments/environments.js';
import { ToolbarType } from '../../models/enums.js';
import { saveAuthDevice } from '../../services/authDeviceService.js';
import { changePassword, checkVerification, getAuth, sendVerification } from '../../services/authService.js';
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
		toolbar.addButton2(ACTION.CHANGE_PASSWORD, () => this.changePassword());
        toolbar.addButton2(ACTION.SAVE, () => this.save());
	}

    buildContent() {
		let div = this.createElement(TAG.DIV);
		div.className = this.isMobile() 
            ? CSS.AON_MOBILE_SUB_CONTENT 
            : CSS.AON_SUB_CONTENT;
		div.style.display = 'flex';
		div.style.width	= '100%';
		this.appendChild(div);

        let authCard = new AonCard();
		authCard.id = "aonConfigurationUserCard";
		authCard.title = MSG.USER;
		authCard.style.width = this.isMobile() ? '100%' : '50%';
		div.appendChild(authCard);
        this.buildAuthContent(authCard);
    }

    buildAuthContent(card) {
		let div = this.createElement(TAG.DIV);
		card.setContent(div);

		let email = new AonInput();
		email.id = 'aonConfigurationUserCardEmail';
		email.description = MSG.EMAIL;
		email.value = this.auth.email;
		div.appendChild(email);
		email.addEventListener(EVENT.CHANGE , () => {
			this.auth.email = email.value;
			if(this.isAutosave()){
				this.save();
			}
		});

		let name = new AonInput();
		name.id = 'aonConfigurationUserCardName';
		name.className = CSS.AON_WIDTH_25;
		name.description = MSG.NAME;
		name.value = this.auth.email;
		name.style.marginRight = '2px';
		div.appendChild(name);
		name.addEventListener(EVENT.CHANGE , () => {
			this.auth.name = name.value;
			if(this.isAutosave()){
				this.save();
			}
		});

		let surname = new AonInput();
		surname.id = 'aonConfigurationUserCardName';
		surname.className = CSS.AON_WIDTH_75;
		surname.description = MSG.SURNAME;
		surname.value = this.auth.surname;
		div.appendChild(surname);
		surname.addEventListener(EVENT.CHANGE , () => {
			this.auth.surname = surname.value;
			if(this.isAutosave()){
				this.save();
			}
		});

		let document = new AonInput();
		document.id = 'aonConfigurationUserCardDocument';
		document.className = CSS.AON_WIDTH_50;
		document.description = MSG.DOCUMENT;
		document.value = this.auth.document;
		document.style.marginRight = '2px';
		div.appendChild(document);
		document.addEventListener(EVENT.CHANGE , () => {
			this.auth.document = document.value;
			if(this.isAutosave()){
				this.save();
			}
		});

		let phone = new AonInput();
		phone.id = 'aonConfigurationUserCardPhone';
		phone.className = CSS.AON_WIDTH_50;
		phone.description = MSG.PHONE;
		phone.value = this.auth.phone;
		div.appendChild(phone);
		phone.addEventListener(EVENT.CHANGE , () => {
			this.auth.phone = phone.value;
			if(this.isAutosave()){
				this.save();
			}
		});
	}

    changePassword() {
        this.verification(() => {
			alert("CHANGE PASSWORD");
        });
    }

    save() {
        this.verification(() => {
			saveAuth(this.auth);
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
        let dialog = this.getApplication().getDialog();
		dialog.clear();
		if(this.isMobile()) {
            dialog.type = "fullscreen";
        } else {
            dialog.width = '400px';
        }
        dialog.setTitle(MSG.EMAIL_VERIFICATION);

        let div = this.createElement(TAG.DIV);
		div.style.marginTop = '20px';
		dialog.setContent(div);

        let codeInput = new AonInput();
        codeInput.description = MSG.VERIFICATION_CODE;
        div.appendChild(codeInput)
		codeInput.addEventListener(EVENT.CHANGE , () => {
            data.value = codeInput.value 
            checkVerification(data).then(check => {
				if(!check.result) {
					this.showError({message: MSG.WRONG_CODE, type:CONSTANT.ERROR});
				}
			});
		});

		dialog.addAcceptAction(() => {
			checkVerification(data).then(check => {
				if(check.result) fn();
			});
		});
		dialog.open();
    }

	changePasswordDialog() {
		let dialog = this.getApplication().getDialog();
		dialog.clear();
		if(this.isMobile()) {
            d.type = "fullscreen";
        } else {
            d.width = '400px';
        }
		d.setTitle(MSG.CHANGE_PASSWORD);

		let div = document.createElement("div");
		div.style.marginTop = '20px';
        let password = new AonInput();
        password.id = "aonConfigurationUserCardPassword";
        password.type = "password";
        password.description = MSG.NEW_PASSWORD;
        div.appendChild(password);

        let password2 = new AonInput();
        password2.id = "aonConfigurationUserCardPassword2";
        password2.type = "password";
        password2.description = MSG.REPEAT_PASSWORD;
        div.appendChild(password2);

		d.setContent(div);

		d.addAcceptAction(() => {
			if(password.value === password2.value) {
				let data = {
					password: password.value
				};
				changePassword(data)
				.then(()=>{
					this.showToast({message:MSG.SAVED_DATA, type:CONSTANT.SUCCESS});
				}).catch(e=>this.showError(e))
			} else {
				this.showToast({message:'Las contraseñas no coinciden.', type:CONSTANT.ERROR});
			}
		});
		d.open();
	}
}
if(!window.customElements.get(TAG.AON_AUTH)){
	window.customElements.define(TAG.AON_AUTH, AonAuth);
}