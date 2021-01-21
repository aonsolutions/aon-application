import { AonElement } from "../../components/AonElement.js";
import {
  login,
  getManifest,
  rememberPassword,
} from "../../services/service.js";
import { rootPanel } from "../../services/gwtLoader.js";

import "../../components/aon-input.js";
import "../../components/aon-loader.js";
import "../../components/aon-dialog.js";
import "../../components/aon-toast.js";

import "../company/aon-mobile-desktop.js";
import "../company/aon-parent.js";

import * as MSG from "../../environments/msg.js";

export class AonLogin extends AonElement {
  constructor() {
    super();
  }

  connectedCallback() {
    this.innerHTML = `
			<!-- Wide card with share menu button -->
			<style>

				.aon-login-form {
				  padding-top: 20px;
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

							<aon-loader id="aonLoginLoader"></aon-loader>

							<div id="aonLoginError" class="aonErrorPanel" style="display:none;">
			          <span id="aonLoginErrorMessage">errorMsg</span>
			        </div>

							<div>
								<aon-input id="aonLoginUser" description="Usuario" filled="true"></aon-input>
							</div>

							<div>
								<aon-input id="aonLoginPassword" description="Contraseña" type="password" filled="true"></aon-input>
							</div>

							<div style="padding-bottom: 10px;">
								Si olvidaste tus datos de acceso haz <a id="aonLoginRemember" class="aonLink aonColorSecondary">click aquí</a>
							</div>

              <div style="padding-bottom: 20px;">
                Si no tienes cuenta <a id="aonBtnRegister" class="aonLink aonColorSecondary"> Registrate </a>
              </div>

							<div style="position:relative;">
								<button class="aonButton" id="aonLoginSignin" type="submit" style="width:100%">Iniciar Sesión</button>
							</div>

						</div>
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

				</div>
			</div>

			<aon-dialog id="aonDialogLogin" width="400px"></aon-dialog>
			<aon-toast id="aonLoginToast"></aon-toast>
			`;

    this.buildLogo();

    this.aonDialogLoginRemember();

    let aonManifest = this.getElement("aonManifest");
    getManifest().then(
      (manifest) => (aonManifest.innerHTML = "Version: " + manifest.build_date)
    );

    let username = this.getElement("aonLoginUser");
    username.addEventListener("keyup", (event) => this.onEnter(event));
    let password = this.getElement("aonLoginPassword");
    password.addEventListener("keyup", (event) => this.onEnter(event));

    let signin = this.getElement("aonLoginSignin");
    signin.addEventListener("click", () => this.signin());

    let aonLoginRemember = this.getElement("aonLoginRemember");
    aonLoginRemember.addEventListener("click", () =>
      this.getElement("aonDialogLogin").open()
    );

    let aonBtnRegister = this.getElement("aonBtnRegister");
    aonBtnRegister.addEventListener("click", () => {
      this.hideElement("aonLogin");
      this.showElement("aonRegister");
    });
  }

  aonDialogLoginRemember() {
    let dialog = this.getElement("aonDialogLogin");
    dialog.setTitle(MSG.AON_MSG_RECOVER_PASSWORD);
    dialog.setContentHTML(`
			<form action="#">
				<aon-input id="aonLoginRememberEmail" description="Email"></aon-input>
			</form>`);
    dialog.addAcceptAction(() =>
      rememberPassword(this.getElement("aonLoginRememberEmail").value)
    );
  }

  buildLogo() {
    let logo = document.getElementById("aonLoginLogoImg");
    if (window.location.href.includes("ayudat")) {
      logo.src = "assets/ayudat-logo4.png";
    } else if (
      window.location.href.includes("translogia") ||
      window.location.href.includes("tedi")
    ) {
      logo.src = "../assets/ayudat-logo4.png";
    } else logo.src = "assets/aon-logo.png";
  }

  signin() {
    const username = document.getElementById("aonLoginUser").value;
    const password = document.getElementById("aonLoginPassword").value;
    const data = {
      username: username,
      password: password,
    };

    let loader = document.getElementById("aonLoginLoader");
    loader.start();
    login(data)
      .then(() => {
        loader.stop();
        localStorage.removeItem("aon_domain_id");
        localStorage.removeItem("aon_domain_name");
        localStorage.removeItem("aon_domain_login");
        rootPanel(
          this.isMobile()
            ? '<aon-mobile-desktop id="aonParent"></aon-mobile-desktop>'
            : '<aon-parent id="aonParent"></aon-parent>'
        );
      })
      .catch((e) => {
        loader.stop();
        let error = JSON.parse(e);
        let aonLoginError = document.getElementById("aonLoginError");
        aonLoginError.style.display = "block";

        let aonLoginErrorMessage = document.getElementById(
          "aonLoginErrorMessage"
        );
        aonLoginErrorMessage.innerHTML = error.message;
        let toast = this.getElement('aonLoginToast');
        toast.start(error);
      });
  }

  onEnter(event) {
    if (event.keyCode === 13) {
      event.preventDefault();
      document.getElementById("aonLoginSignin").click();
    }
  }
}

window.customElements.define("aon-login", AonLogin);
