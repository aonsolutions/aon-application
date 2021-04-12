import {AonElement} from '../components/AonElement.js';
import {rootPanel} from '../services/gwtLoader.js';
import {setPosition} from '../services/maps.js';
import { FirebaseService } from '../services/firebaseService.js';
import './login/aon-login.js';
import './register/aon-register.js';
import './aon-home.js';
import './company/aon-parent.js';
import './company/aon-mobile-desktop.js';
import { saveAuthDevice } from '../services/service.js';
import { AonDialog } from '../components/aon-dialog.js';

export class AonModule extends AonElement {
	AON_LOGIN;
	AON_REGISTER;
	AON_HOME;
	AON_DESKTOP;
	AON_PARENT;

	constructor () {
		super();
		this.AON_LOGIN = 'aonLogin';
		this.AON_REGISTER = 'aonRegister';
		this.AON_HOME = 'aonHome';
		this.AON_DESKTOP = 'aonDesktop';
		this.AON_PARENT = 'aonParent';
	}

	connectedCallback () {
		this.observerListener();
		this.setWindowApp();

		let loginDiv= this.createElement('div');
		loginDiv.id = this.AON_LOGIN;
		loginDiv.style.display = 'none';
		this.appendChild(loginDiv);
		loginDiv.innerHTML = '<aon-login></aon-login>';

		let homeDiv = this.createElement('div');
		homeDiv.id = this.AON_HOME;
		homeDiv.style.display = 'none';
		homeDiv.innerHTML = '<aon-home></aon-home>';
		this.appendChild(homeDiv);

		let registerDiv= this.createElement('div');
		registerDiv.id = this.AON_REGISTER;
		registerDiv.style.display = 'none';
		this.appendChild(registerDiv);
		registerDiv.innerHTML = '<aon-register></aon-register>';
		this.load();
	}

	observerListener(){
		window.addEventListener('userAuth', ()=>{
			this.initializeFB();
		});
	}

	load() {

		if(localStorage.getItem('aon_session_id')){
			document.getElementById("aonLogin").style.display = 'none';
			document.getElementById("aonHome").style.display = 'block';
			localStorage.removeItem('aon_domain_id');
			localStorage.removeItem('aon_domain_name');
			localStorage.removeItem('aon_domain_login');

			rootPanel(this.isMobile()
			 	? '<aon-mobile-desktop id="aonDesktop"></aon-mobile-desktop>'
			 	: '<aon-parent id="aonParent"></aon-parent>');

			window.dispatchEvent( new Event('userAuth') );

		} else {
			this.getElement(this.AON_LOGIN).style.display = 'block';
			this.getElement(this.AON_HOME).style.display = 'none';
		}


	}

	setWindowApp(){
		window.setPosition = (pos) => setPosition(pos);

		window.setTokenFCM =  (token) => {
			window.tokenFCM = token;	
			this.saveTokenFcm(token);	
		} 

		window.setNotificationAction = (data) =>  {
			console.log("data Notification1>", typeof data, data);
			const id = 'aonDialogNotify';
			const dialog = this.getElement(id) || new AonDialog();
			dialog.id = id;
			if(dialog){ this.appendChild(dialog);}
			dialog.clear();
			dialog.setContentHTML(data.body);
			if (!this.isMobile()) dialog.width = '400px';
			dialog.setTitle(data.title);
			dialog.open();
			dialog.addAcceptAction(() => {
				console.log("aceptar");
			});
		}
	}

	async initializeFB()  {
		try{
			let token = undefined;
			if(!this.isMobile()) { // initialize observer message firebase desk
				const firebaseSrv = new FirebaseService();
				token = await firebaseSrv.getTokenFB();
				if (token) {
					window.tokenFCM = token;
					const messaging = firebaseSrv.getMessagingObject();
					messaging.onMessage(
						(payload) => firebaseSrv.pushNotification(payload),
						(err) => console.log(err)
					);
					this.saveTokenFcm(token);
				}
			} 
		} catch(e){}
  	}

	saveTokenFcm(tokenFCM){
		console.log("TOKEN FCM", tokenFCM);
		saveAuthDevice({tokenFCM});
	}

}
window.customElements.define('aon-module',  AonModule);
