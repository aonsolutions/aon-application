import { AonElement } from "../../components/AonElement.js";
import { login, getManifest, rememberPassword, getCompanies, getUser, getUserAppRole, actionMobile } from "../../services/service.js";
import { rootPanel } from "../../services/gwtLoader.js";

import "../../components/aon-input.js";
import "../../components/aon-loader.js";
import "../../components/aon-dialog.js";
import "../../components/aon-toast.js";

import "../company/aon-mobile-desktop.js";
import "../company/aon-parent.js";

import { EVENT, MSG } from '../../environments/environments.js'; 

import { webkitRequestMobile } from "../../services/request.js";

export class AonLogin extends AonElement {
  tag;
  constructor() {
    super();
    this.tag = 0;
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

              <!--<div style="padding-bottom: 20px;">
                Si no tienes cuenta <a id="aonBtnRegister" class="aonLink aonColorSecondary"> Registrate </a>
              </div>-->

							<div style="position:relative;">
								<button class="aonButton" id="aonLoginSignin" type="submit" style="width:100%">Iniciar Sesión</button>
							</div>

						</div>
			    <div class="aon-login-info2">
    				<span>
							<a target="_blank" href="http://www.aonsolutions.es">
			        	aon Solutions
			      	</a>
			        es una marca registrada de AON SOLUTIONS, S.L.
			      </span>
			      <div id="aonManifest">
			      </div>
			    </div>

          <div id="logosMobiles"></div>

				</div>
			</div>

			<aon-dialog id="aonDialogLogin" width="400px"></aon-dialog>
			<aon-toast id="aonLoginToast"></aon-toast>
			`;

    this.buildLogo();
    if(!webkitRequestMobile() && this.isMobile()){ // si es app
      this.buildAppLogo();
    }

    this.aonDialogLoginRemember();

    let aonManifest = this.getElement("aonManifest");
    getManifest().then(
      (manifest) => (aonManifest.innerHTML = "Version: " + manifest.build_date)
    );

    let username = this.getElement("aonLoginUser");
    username.addEventListener(EVENT.KEYUP, (event) => this.onEnter(event));
    let password = this.getElement("aonLoginPassword");
    password.addEventListener(EVENT.KEYUP, (event) => this.onEnter(event));

    let signin = this.getElement("aonLoginSignin");
    signin.addEventListener(EVENT.CLICK, () => this.signin());

    let aonLoginRemember = this.getElement("aonLoginRemember");
    aonLoginRemember.addEventListener(EVENT.CLICK, () =>
      this.getElement("aonDialogLogin").open()
    );

  }


  buildAppLogo(){
    const div  = this.getElement('logosMobiles');
    div.style.textAlign = "center";
    const playStore = this.createElement('a');
    playStore.href = "https://play.google.com/store/apps/details?id=aon.solutions";
    playStore.target = "_blank";
    const imgPlayStore = this.createElement('img');
    imgPlayStore.src= "assets/playstore.png"; //: "assets/playstore-disabled.png";
    imgPlayStore.style.height = "38px";
    playStore.appendChild(imgPlayStore);
    div.appendChild(playStore);

    const appStore = this.createElement('a');
    appStore.href = "https://itunes.apple.com/es/app/aon-solutions/id1538461097";
    appStore.target = "_blank";
    const imgAppStore = this.createElement('img');
    imgAppStore.src =  "assets/appstore.png" //: "assets/appstore-disabled.png";
    imgAppStore.style.height = "40px";
    imgAppStore.style.filter = "grayscale(100%)";
    appStore.appendChild(imgAppStore);
    div.appendChild(appStore);
  }

  aonDialogLoginRemember() {
    let dialog = this.getElement("aonDialogLogin");
    dialog.setTitle(MSG.RECOVER_PASSWORD);
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
    let src = "assets/aon-logo.png"; 
    if (window.location.href.includes("ayudat")) {
      src = "assets/ayudat-logo4.png";
    } else if (window.location.href.includes("translogia") || window.location.href.includes("tedi")) {
      src = "assets/ayudat-logo4.png";
    } else if (window.location.href.includes("aonsolutions.org")){
     src = "assets/beta-logo.svg";
    }
    logo.src = src;
    logo.addEventListener(EVENT.CLICK, ()=>{
      this.tag = this.tag + 1;
      if(this.tag >= 5 && !window.location.href.includes("aonsolutions.org")){
        actionMobile({
          action:"setBaseUrl",
          BASE_URL_MOBILE: "https://aonsolutions.org"
        });
        this.tag = 0;
      }
		})
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
        localStorage.removeItem('aon_domain_id');
        localStorage.removeItem('aon_domain_name');
        localStorage.removeItem('aon_domain_login');
        getCompanies().then(companies => {
          if(companies.length === 1){
            this.companySelection(companies[0]);
          } else {
            this.getElement("aonHome").showMenu(false);
            rootPanel(this.isMobile()
              ? '<aon-mobile-parent id="aonParent"></aon-mobile-parent>'
              : '<aon-parent id="aonParent"></aon-parent>');
          }
          this.getElement("aonLogin").style.display = 'none';
          let homeDiv = this.getElement("aonHomeDiv");
          homeDiv.style.display = 'block';
        });
        window.dispatchEvent( new Event('userAuth') );
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

  companySelection(company) {
    localStorage.setItem('company', JSON.stringify(company));
    localStorage.setItem("aon_domain_id", company.id);
    localStorage.setItem("aon_domain_name", company.domain);

    let home = this.getElement('aonHome');
    home.showMenu(true);

    let aonHeader = this.getElement(home.AON_HEADER);
    aonHeader.showCompanyOption(company);

    if(!this.isMobile()){
      let aonMenu = this.getElement('aonMenu');
      aonMenu.clear();
      aonMenu.init();
    }

    getUser().then(user => {
      localStorage.setItem('aon_domain_login', user.login);
      getUserAppRole().then(user => {
        if(!this.isMobile()){
          aonHeader.setAttribute('company', JSON.stringify(company));
          aonHeader.setAttribute('user', JSON.stringify(user));
        }
        rootPanel(this.isMobile()
          ? '<aon-mobile-desktop id="aonDesktop"></aon-mobile-desktop>'
          : '<aon-desktop id="aonDesktop"></aon-desktop>');
        let aonDesktop = document.getElementById('aonDesktop');
        aonDesktop.setAttribute('company', JSON.stringify(company));
        aonDesktop.setAttribute('user', JSON.stringify(user));
      });
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
