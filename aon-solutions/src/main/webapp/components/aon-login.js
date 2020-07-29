import './aon-inputText.js';

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

			</style>
			<div class="form-center">
		  	<div class="aon-login-border">
			    <!-- <form class="aon-login-form form-mobile-div"> -->
						<div class="aon-login-form" style="max-width:250px;">
							<div style="margin-bottom:15px;">
								<img class="logo" src="aon-solutions/assets/logo.png"/>
							</div>

							<form action="#">
								<aon-input-text id="user" description="Usuario"></aon-input-text>
							</form>

							<form action="#">
								<aon-input-text id="password" description="Contraseña" type="password"></aon-input-text>
							</form>

							<div style="position:relative;padding-bottom:5px; margin-bottom: 5px;">
								<button class="mdl-button mdl-js-button remember-button" type="submit" >¿Has olvidado tu contraseña?</button>
							</div>
							<div style="position:relative;">
								<button class="mdl-button mdl-js-button mdl-button--raised sign-in" type="submit" onclick="login()">Iniciar Sesión</button>
							</div>

						</div>
					<!-- </form> -->
			    <div class="aon-login-info2">
			      <a target="_blank" href="http://www.aonsolutions.es">
			        <span class="aon-outputText">aon Solutions</span>
			      </a>
			      <span class="aon-outputText">
			        es una marca registrada de AON SOLUTIONS, S.L.
			      </span>

			      <div id="aonManifest">

			      </div>
			    </div>

					<!-- <div class="aon-login-card">
						<div style="margin-bottom:15px;">
							<img style="width:300px;" src="aon-solutions/assets/logo.png"/>
						</div>

						<form action="#">
							<aon-input-text id="user" description="Usuario"></aon-input-text>
						</form>

						<form action="#">
							<aon-input-text id="password" description="Contraseña" type="password"></aon-input-text>
						</form>

						<div style="position:relative;padding-bottom:5px; margin-bottom: 5px;">
							<button class="mdl-button mdl-js-button" type="submit" >¿Has olvidado tu contraseña?</button>
						</div>
						<div style="position:relative;">
							<button class="mdl-button mdl-js-button mdl-button--raised sign-in" type="submit" onclick="login()">Iniciar Sesión</button>
						</div>
					</div> -->
				</div>
			</div>
			`;

			let aonManifest = document.getElementById('aonManifest');
			getManifest().then(r => {
				let manifest = JSON.parse(r);
				aonManifest.innerHTML = 'Version: ' + manifest.build_date;
			});

  }
}

window.customElements.define('aon-login', AonLogin);
