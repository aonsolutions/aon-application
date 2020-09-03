import {login, getManifest, rememberPassword} from  '../services/service.js';
import {rootPanel} from '../services/gwtLoader.js';
import './aon-inputText.js';
import './aon-parent.js';


class AonLogin extends HTMLElement {

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<!-- Wide card with share menu button -->
			<style>

				.form-center {
					display: flex;
					justify-content: center;
				}

				.aon-login-form {
				  padding-left: 20px;
				  padding-top: 20px;
				  padding-right: 20px;
				}

				.aon-login-border {
				  border-radius: 8px;
				  border: 1px solid #dadce0;
				  width: 300px;
				  padding-top: 20px;
				  top: 25%;
				  position: absolute;
				}

				.sign-in {
					right: 0px;
					position: absolute;
				}

				.aon-login-card {
					padding-bottom:50px;
					padding-top: 20px;
					padding-left: 20px;
					padding-right: 20px;
					height:350px;
					margin-top: 15% !important;

				    font-size: 16px;
				    background: #fff;
					border-radius: 2px;
					box-sizing: border-box;
				}

				.aon-input {
					position: relative;
					font-size: 16px;
					display: inline-block;
					box-sizing: border-box;
					width: 300px;
					max-width: 100%;
					margin: 0;
					padding: 20px 0;
				}

				.logo {
				  width: 250px;
				}

				.aon-login-info2 {
				  color: #666;
				  font-size: 10px;
				  border-top: 1px solid #ccc;
				  margin-top: 60px;
				  font-size: 9px;
				  font-weight: normal;
				  padding: 15px;
				}

				.remember-button {
					padding: 0px !important;
					width: 100%;
				}

				.aonErrorPanel {
				  min-width: 150px;
				  max-width: 250px;
				  width: 100%;
				  color: red;
				}

			</style>
			<div class="form-center">
		  	<div class="aon-login-border">
			    <!-- <form class="aon-login-form form-mobile-div"> -->
						<div class="aon-login-form" style="max-width:250px;">
							<div style="margin-bottom:15px;">
								<img class="logo" src="aon-solutions/assets/logo.png"/>
							</div>

							<div id="aonLoginLoading" class="mdl-progress mdl-js-progress mdl-progress__indeterminate"></div>

							<div id="aonLoginError" class="aonErrorPanel" style="display:none;">
			          <span id="aonLoginErrorMessage">errorMsg</span>
			        </div>

							<form action="#">
								<aon-input-text id="aonLoginUser" description="Usuario"></aon-input-text>
							</form>

							<form action="#">
								<aon-input-text id="aonLoginPassword" description="Contraseña" type="password"></aon-input-text>
							</form>

							<div style="position:relative;padding-bottom:5px; margin-bottom: 5px;">
								<button id="aonLoginRemember" class="mdl-button mdl-js-button remember-button" type="submit" >¿Has olvidado tu contraseña?</button>
							</div>
							<div style="position:relative;">
								<button id="aonLoginSignin"class="mdl-button mdl-js-button mdl-button--raised sign-in" type="submit">Iniciar Sesión</button>
							</div>

						</div>
					<!-- </form> -->
			    <div class="aon-login-info2">
			      <a target="_blank" style="color:#3677E1;" href="http://www.aonsolutions.es">
			        <span class="aon-outputText">aon Solutions</span>
			      </a>
			      <span class="aon-outputText">
			        es una marca registrada de AON SOLUTIONS, S.L.
			      </span>

			      <div id="aonManifest">

			      </div>
			    </div>

					<dialog class="mdl-dialog">
    				<h4> Recuperar Contraseña</h4>
    				<div>
							<form action="#">
								<aon-input-text id="aonLoginRememberEmail" description="Email"></aon-input-text>
							</form>
      			</div>
    				<div class="mdl-dialog__actions">
      				<button id="aonLoginRememberSend" type="button" class="mdl-button">Enviar</button>
      				<button type="button" class="mdl-button close">Cancelar</button>
    				</div>
  				</dialog>
				</div>
			</div>
			`;

			let aonManifest = document.getElementById('aonManifest');
			getManifest().then(r => {
				let manifest = JSON.parse(r);
				aonManifest.innerHTML = 'Version: ' + manifest.build_date;
			});

			let username = document.getElementById("aonLoginUser");
			username.addEventListener('keyup', event => this.onEnter(event));
			let password = document.getElementById("aonLoginPassword");
			password.addEventListener('keyup', event => this.onEnter(event));

			let loading = document.getElementById('aonLoginLoading');
			loading.style.display = 'none';

			let signin = document.getElementById('aonLoginSignin');
			signin.addEventListener('click', () => this.signin());



			let dialog = document.querySelector('dialog');
			if (! dialog.showModal) {
				dialogPolyfill.registerDialog(dialog);
			}

			let aonLoginRemember = document.getElementById('aonLoginRemember');
			aonLoginRemember.addEventListener('click', function() {
      	dialog.showModal();
    	});

    	dialog.querySelector('.close').addEventListener('click', function() {
      	dialog.close();
    	});

			let aonLoginRememberSend = document.getElementById('aonLoginRememberSend');
			aonLoginRememberSend.addEventListener('click', function() {
	     	dialog.close();
				rememberPassword(document.getElementById('aonLoginRememberEmail').value);
			});
	}

	signin() {
		const username = document.getElementById("aonLoginUser").value;
		const password = document.getElementById("aonLoginPassword").value;
		const data = {
				username: username,
				password: password
		}

		let loading = document.getElementById('aonLoginLoading');
		loading.style.display = 'block';
		login(data).then(() => {
			loading.style.display = 'none';

			localStorage.removeItem('aon_domain_id');
			localStorage.removeItem('aon_domain_name');
			rootPanel('<aon-parent id="aonParent"></aon-parent>');
		}).catch(error => {
			loading.style.display = 'none';
			let err = JSON.parse(error);

			let aonLoginError = document.getElementById('aonLoginError');
			aonLoginError.style.display = 'block';

			let aonLoginErrorMessage = document.getElementById('aonLoginErrorMessage');
			aonLoginErrorMessage.innerHTML = err.message;
		});
	}

	onEnter(event) {
		if (event.keyCode === 13) {
    	event.preventDefault();
    	document.getElementById('aonLoginSignin').click();
  	}
	}


}

window.customElements.define('aon-login', AonLogin);
