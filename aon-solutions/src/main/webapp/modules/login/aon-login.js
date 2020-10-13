import {login, getManifest, rememberPassword} from  '../../services/service.js';
import {rootPanel} from '../../services/gwtLoader.js';
import '../../components/aon-input.js';
import '../company/aon-parent.js';


class AonLogin extends HTMLElement {

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<!-- Wide card with share menu button -->
			<style>

				.aon-login-form {
				  padding-top: 20px;
				}

				.sign-in {
					right: 0px;
					position: absolute;
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
					margin-left: 25px;
				}

				.aon-login-info2 {
				  color: #666;
				  font-size: 10px;
				  border-top: 1px solid #ccc;
				  margin-top: 10px;
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
			<div class="aonFormCenter">
		  	<div class="aonVerticalCenter aonWidth300">
			  	<div id="aonLoginLogoDiv">
						<img id="aonLoginLogoImg" class="logo"/>
					</div>
						<div class="aon-login-form" style="width:300px;">
							<div class="aonColorSecondary" style="font-weight: bold;padding-bottom: 20px;">
								INICIO DE SESIÓN
							</div>

							<div id="aonLoginLoading" class="mdl-progress mdl-js-progress mdl-progress__indeterminate"></div>

							<div id="aonLoginError" class="aonErrorPanel" style="display:none;">
			          <span id="aonLoginErrorMessage">errorMsg</span>
			        </div>

							<div>
								<aon-input id="aonLoginUser" description="Usuario" filled="true"></aon-input>
							</div>

							<div>
								<aon-input id="aonLoginPassword" description="Contraseña" type="password" filled="true"></aon-input>
							</div>

							<div style="padding-bottom: 20px;">
								Si olvidaste tus datos de acceso haz <a id="aonLoginRemember" class="aonColorSecondary">click aquí</a>
							</div>

							<div style="position:relative;">
								<button id="aonLoginSignin" type="submit" style="width:100%">Iniciar Sesión</button>
							</div>

						</div>
					<!-- </form> -->
			    <div class="aon-login-info2">
    				<span >
							<a target="_blank" href="http://www.aonsolutions.es">
			        	aon Solutions
			      	</a>
			        es una marca registrada de AON SOLUTIONS, S.L.
			      </span>

			      <div id="aonManifest">

			      </div>
			    </div>

					<dialog class="mdl-dialog">
    				<h4> Recuperar Contraseña</h4>
    				<div>
							<form action="#">
								<aon-input id="aonLoginRememberEmail" description="Email"></aon-input>
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

			this.buildLogo();

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
			loading.style.marginBottom = '20px';
			loading.style.display = 'none';

			let signin = document.getElementById('aonLoginSignin');
			signin.addEventListener('click', () => this.signin());



			let dialog = document.querySelector('dialog');
			if (! dialog.showModal) {
				dialogPolyfill.registerDialog(dialog);
			}

			let aonLoginRemember = document.getElementById('aonLoginRemember');
			aonLoginRemember.addEventListener('click', () => {
      	dialog.showModal();
    	});

    	dialog.querySelector('.close').addEventListener('click', () => {
      	dialog.close();
    	});

			let aonLoginRememberSend = document.getElementById('aonLoginRememberSend');
			aonLoginRememberSend.addEventListener('click', () => {
	     	dialog.close();
				rememberPassword(document.getElementById('aonLoginRememberEmail').value);
			});
	}

	buildLogo() {
		let logo = document.getElementById('aonLoginLogoImg');
		if(window.location.href.includes('ayudat')){
			logo.src = 'assets/ayudat-logo.png';
		} else if(window.location.href.includes('translogia') || window.location.href.includes('tedi')){
			logo.src = '../assets/tedi-logo.png';
		} else logo.src = 'assets/aon-logo.png';
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
