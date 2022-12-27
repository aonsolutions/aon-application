import { AonAvatar } from '../../components/aon-avatar.js';
import { AonDialog } from '../../components/aon-dialog.js';
import { AonIconButton } from '../../components/aon-icon-button.js';
import { AonInput } from '../../components/aon-input.js';
import { AonElement } from '../../components/AonElement.js';
import { CONSTANT, EVENT, MSG, TAG } from '../../environments/environments.js';
import { changePassword, getAuth, insertAvatar } from '../../services/authService.js';
import { downscaleImage } from '../../services/compressImg.js';
import { getReader } from '../../services/utils.js';

export class AonMobileProfile extends AonElement {

    INPUT_FILE;
    AVATAR;
    DIALOG;

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
        this.initialize();
        getAuth().then(r => {
            this.auth = r;
            this.build();
        });
    }

    initialize() {
        this.id = this.id || CONSTANT.AON_MOBILE_PROFILE;
        this.INPUT_FILE = this.id + 'InputFile';
        this.AVATAR = "aonAvatar";
        this.DIALOG = this.id+"Dialog"; 
    }

    build() {
        let input = document.createElement(TAG.INPUT);
        input.id = this.INPUT_FILE;
        input.multiple = true;
        input.type = "file";
        input.style.display = "none";
        input.name = "file";
        input.addEventListener(EVENT.CHANGE, ({target}) => this.uploadAvatarFile(target.files[0]));
        this.appendChild(input);

        let aonDialog = new AonDialog();
        aonDialog.id = this.DIALOG;
        this.appendChild(aonDialog);

        let div = this.createElement(TAG.DIV);
        div.style.margin = '20px';
        this.appendChild(div);  

        let avatar = new AonAvatar();
        avatar.id = this.AVATAR;
        avatar.src = auth.avatar;
        avatar.addEventListener(EVENT.CLICK, () => this.uploadAvatar());
        div.appendChild(avatar);

        let name = this.createElement(TAG.SPAN);
        name.style.fontWeight = 'bold';
        name.style.marginLeft = '20px';
        name.innerHTML = auth.name + ' ' + auth.surname;
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

        this.buildOption('mail', auth.email);
        this.buildOption('fingerprint', auth.document);
        this.buildOption('smartphone', auth.phone);
        this.buildOption('password', 'Cambiar Contraseña', () => this.editPassword());
        // this.addCloseSessionButton();
    }

    buildOption(icon, value, fn) {
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

        if(fn){
            div.addEventListener(EVENT.CLICK, fn)
        }
    }

	editPassword() {
		let d = document.getElementById(this.DIALOG);
		d.clear();
		if(this.isMobile()) {
            d.type = "fullscreen";
        } else {
            d.width = '400px';
        }

		d.setTitle(MSG.CHANGE_PASSWORD);

        let div = document.createElement("div");

        let oldPassword = new AonInput();
        oldPassword.id = "oldPassword";
        oldPassword.type = "password";
        oldPassword.description = "Contraseña";
        div.appendChild(oldPassword);

        let newPassword = new AonInput();
        newPassword.id = "newPassword";
        newPassword.type = "password";
        newPassword.description = "Repetir Contraseña";
        div.appendChild(newPassword);

		d.setContent(div);
        
		d.addAcceptAction(() => {
            const oldPs = oldPassword.value;
            const newPs = newPassword.value;

            changePassword({oldPassword: oldPs, newPassword: newPs})
            .then(()=>{
                this.showToast({message:MSG.SAVED_DATA, type:CONSTANT.SUCCESS});
            })
            .catch(error=>{
                if(typeof error === 'string'){
                    error = JSON.parse(error);
                }

                if(error.message){
                    alert(error.message);
                }
            });
		});
		d.open();
	}

    uploadAvatar() {
        // this.getElement(this.INPUT_FILE).click();
    }

    async uploadAvatarFile(file) {
        try {
            let reader = await getReader(file);
            if (reader) {
                if (reader.contentType.indexOf("image") >= 0) {
                    //compress 500kB / file, 500kb, quality default 0.9, maxResolution 1280
                    reader = await downscaleImage(reader, undefined, undefined, undefined);
                } 
                await insertAvatar(reader);
                const aonAvatar = this.getElement(this.AVATAR);
                if(aonAvatar){
                    const auth = await getAuth();
                    aonAvatar.src = auth.avatar;
                }
            }
        } catch (error) { console.log(error); }
    }
}

if(!window.customElements.get(TAG.AON_MOBILE_PROFILE)){
    window.customElements.define(TAG.AON_MOBILE_PROFILE,  AonMobileProfile);
}