import { AonElement } from "../../components/AonElement.js";
import { login, getManifest, rememberPassword, magicLink, getCompanies, getUser, mobileAction, MOBILE_ACTION } from "../../services/service.js";

import "../../components/aon-input.js";
import "../../components/aon-loader.js";
import "../../components/aon-dialog.js";
import "../../components/aon-toast.js";

import "../company/aon-mobile-desktop.js";
import "../company/aon-parent.js";

import { CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js'; 

import { webkitRequestMobile } from "../../services/request.js";
import { AonInput } from "../../components/aon-input.js";
import * as LS from '../../services/localStorageService.js';
import { AonLoader } from "../../components/aon-loader.js";
import { AonButton } from "../../components/aon-button.js";
import { AonDialog } from "../../components/aon-dialog.js";
import { AonToast } from "../../components/aon-toast.js";

export class AonLogin extends AonElement {
  tag;
  constructor() {
    super();
    this.tag = 0;
  }


  initialize() {

  }

  build() {
    if(!this.isMobile()){
      document.body.style.background = 'linear-gradient(to right, #ffff,aliceblue, #002469)';
    }

    let div = this.createElement(TAG.DIV);
    div.className = CSS.AON_FORM_CENTER;
    this.appendChild(div);


    let div2 = this.createElement(TAG.DIV);
    div2.classList.add(CSS.AON_VERTICAL_CENTER);
    div2.classList.add(CSS.AON_WIDTH_300);
    div2.style.top = '47%';
    if(!this.isMobile()){
      div2.style.background = 'white';
      div2.style.padding = '20px';
      div2.style.width = '340px';
      div2.style.borderRadius = '10px';
      div2.style.boxShadow = '0 2px 1px -1px #0003, 0 1px 1px #00000024, 0 1px 3px #0000001f';
    }
    div.appendChild(div2);

    let divLogo = this.createElement(TAG.DIV);
    divLogo.id = 'aonLoginLogoDiv';
    div2.appendChild(divLogo);

    let logo = this.createElement(TAG.IMG);
    logo.id = 'aonLoginLogoImg';
    logo.style.width = '250px';
    logo.style.marginLeft = '25px';
    divLogo.appendChild(logo);

    let divContent = this.createElement(TAG.DIV);
    divContent.style.paddingTop = '20px';
    divContent.style.width = '300px';
    div2.appendChild(divContent);

    let divTitle = this.createElement(TAG.DIV);
    divTitle.className = CSS.AON_COLOR_SECONDARY;
    divTitle.style.fontWeight = 'bold';
    divTitle.style.paddingBottom = '20px'; 
    divTitle.innerHTML = MSG.SIGN_IN.toUpperCase();
    divContent.appendChild(divTitle);

    let aonLoader = new AonLoader();
    aonLoader.id = 'aonLoginLoader';
    divContent.appendChild(aonLoader);

    let userInput = this.createAonElement(new AonInput(), 'aonLoginUser', MSG.USER);

    userInput.addEventListener(EVENT.KEYUP, () => {
      this.getElement('aonLoginMagicLink').setDisabled(!userInput.value.includes('@')); 
    })
    divContent.appendChild(userInput);

    let passwordInput = this.createAonElement(new AonInput(), 'aonLoginPassword', MSG.PASSWORD);
    passwordInput.type = 'password';
    divContent.appendChild(passwordInput);


    let divButtons = this.createElement(TAG.DIV);
    divButtons.style.display = 'flex';
    divButtons.style.flexDirection = 'column';
    divButtons.style.rowGap = '10px';
    divContent.appendChild(divButtons);


    let signIn = new AonButton();
    signIn.id = 'aonLoginSignin';
    signIn.style.width = '100%';
    signIn.setIcon(MATERIAL_ICONS.LOGIN);
    signIn.setTitle(MSG.SIGN_IN);
    divButtons.appendChild(signIn);

    let magicLinkButton = new AonButton();
    magicLinkButton.id = 'aonLoginMagicLink';
    magicLinkButton.style.width = '100%';
    magicLinkButton.setIcon(MATERIAL_ICONS.AUTO_FIX_NORMAL);
    magicLinkButton.setTitle(MSG.SIGN_IN_WITHOUT_PASSWORD);
    magicLinkButton.setDisabled(true);
    magicLinkButton.setColor("#12ccd1");
    magicLinkButton.addEventListener(EVENT.CLICK, () => this.magicLink(userInput.value));
    divButtons.appendChild(magicLinkButton);

    if(this.isBeta()){
      let certificateButton = new AonButton();
      certificateButton.id = 'aonLoginCertificate';
      certificateButton.style.width = '100%';
      certificateButton.setIcon(MATERIAL_ICONS.SECURITY);
      certificateButton.setTitle(MSG.SIGN_IN_WITH_CERTIFICATE);
      certificateButton.setColor("black");
      certificateButton.addEventListener(EVENT.CLICK, () => alert("En desarrollo"));
      divButtons.appendChild(certificateButton);
    }


    let divInfo = this.createElement(TAG.DIV);
    divInfo.style.color = '#666';
    divInfo.style.fontSize = '9px';
    divInfo.style.borderTop = '1px solid #ddd';
    divInfo.style.marginTop = '10px';
    divInfo.style.padding = '15px';
    divInfo.innerHTML = `
      <span>
        <a target="_blank" class="aonLink" href="http://www.aonsolutions.es">
          aon Solutions
        </a> ${MSG.REGISTERED_TRADEMARK_AON}
      </span>
      <div id="aonManifest"></div>`;
    div2.appendChild(divInfo);

    let divMobiles = this.createElement(TAG.DIV);
    divMobiles.id = 'logosMobiles';
    div2.appendChild(divMobiles);

    let dialog = new AonDialog();
    dialog.id = "aonDialogLogin";
    this.appendChild(dialog);

    let toast = new AonToast();
    toast.id = "aonLoginToast";
    this.appendChild(toast);
  }

  magicLink(value) {
    let loader = this.getElement("aonLoginLoader");
    loader.start();
    magicLink(value).then(()=> {
      loader.stop();
      let toast = this.getElement('aonLoginToast');
      toast.start({type:'success', message:'El mensaje se ha enviado correctamente.'});
    }).catch(e => {
      loader.stop();
      let error = JSON.parse(e);
      let toast = this.getElement('aonLoginToast');
      toast.start(error);
    });
  }
  
  connectedCallback() {
    if(this.isBeta()) {
      this.initialize();
      this.build();
    } else {
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

			<aon-dialog id="aonDialogLogin"></aon-dialog>
			<aon-toast id="aonLoginToast"></aon-toast>
			`;
    }
    this.buildLogo();
    if(!webkitRequestMobile() && this.isMobile()){ // si es app
      this.buildAppLogo();
    }

    if(!this.isBeta()) this.aonDialogLoginRemember();

    let aonManifest = this.getElement("aonManifest");
    getManifest().then(
      (manifest) => (aonManifest.innerHTML = MSG.VERSION + ": " + manifest.build_date)
    );

    let username = this.getElement("aonLoginUser");
    username.addEventListener(EVENT.KEYUP, (event) => this.onEnter(event));
    let password = this.getElement("aonLoginPassword");
    password.addEventListener(EVENT.KEYUP, (event) => this.onEnter(event));

    let signin = this.getElement("aonLoginSignin");
    signin.addEventListener(EVENT.CLICK, () => this.signin());
    if(!this.isBeta()) {
      let aonLoginRemember = this.getElement("aonLoginRemember");
      aonLoginRemember.addEventListener(EVENT.CLICK, () =>{
        const dialog = this.getElement("aonDialogLogin");
        if (!this.isMobile()) dialog.width = '400px';
        dialog.open()
      });
    }
  }

  buildAppLogo(){
    const div  = this.getElement('logosMobiles');
    div.style.textAlign = "center";
    const playStore = this.createElement(TAG.A);
    playStore.href = "https://play.google.com/store/apps/details?id=aon.solutions";
    playStore.target = "_blank";
    const imgPlayStore = this.createElement('img');
    imgPlayStore.src= "assets/playstore.png"; //: "assets/playstore-disabled.png";
    imgPlayStore.style.height = "38px";
    playStore.appendChild(imgPlayStore);
    div.appendChild(playStore);

    const appStore = this.createElement(TAG.A);
    appStore.href = "https://itunes.apple.com/es/app/aon-solutions/id1538461097";
    appStore.target = "_blank";
    const imgAppStore = this.createElement('img');
    imgAppStore.src =  "assets/appstore.png" //: "assets/appstore-disabled.png";
    imgAppStore.style.height = "40px";
    imgAppStore.style.filter = "grayscale(100%)";
    appStore.appendChild(imgAppStore);
    div.appendChild(appStore);
  }

  aonDialogLoginMagicLink() {
    let dialog = this.getElement("aonDialogLogin");
    if (!this.isMobile()) dialog.width = '400px';
    dialog.setTitle("MAGIC LINK");
    let form = this.createElement("form");
    form.action = "#";
    let aonInput = new AonInput();
    aonInput.id = "aonLoginMagicLinkEmail";
    aonInput.description = "Email";
    aonInput.autocomplete = "on";
    form.appendChild(aonInput);
    dialog.setContent(form);
    
    dialog.addAcceptAction(() => aonInput.value && aonInput.value.length>3 ? magicLink(aonInput.value) : null);
    dialog.open();
  }

  aonDialogLoginRemember() {
    let dialog = this.getElement("aonDialogLogin");
    dialog.setTitle(MSG.RECOVER_PASSWORD);
    let form = this.createElement("form");
    form.action = "#";
    let aonInput = new AonInput();
    aonInput.id = "aonLoginRememberEmail";
    aonInput.description = "Email";
    aonInput.autocomplete = "on";
    form.appendChild(aonInput);
    dialog.setContent(form);
    
    dialog.addAcceptAction(() => aonInput.value && aonInput.value.length>3 ? rememberPassword(aonInput.value) : null);
  }

  buildLogo() {
    let logo = this.getElement("aonLoginLogoImg");
    const href = window.location.href;
    let src = "assets/aon-logo2.png"; 
    if (href.includes("ayudat")) {
      src = "assets/img/ayudat-logo.png";
    } else if (href.includes("translogia") || href.includes("tedi")) {
      src = "assets/ayudat-logo4.png";
    } else if (href.includes("aonsolutions.org")){
     src = "assets/beta-logo.svg";
    }

    logo.src = src;
    logo.addEventListener(EVENT.CLICK, ()=>{
      this.tag = this.tag + 1;
      if(this.tag >= 5){
        const BASE_URL_MOBILE = href.includes("aonsolutions.org") ? "https://aon.solutions/" : "https://aonsolutions.org";
        mobileAction({
          action:MOBILE_ACTION.SET_BASE_URL,
          BASE_URL_MOBILE
        });
        this.tag = 0;
      }
		})
  }

  signin() {
    const username = this.getElement("aonLoginUser").value;
    const password = this.getElement("aonLoginPassword").value;
    const data = {
      username: username,
      password: password,
    };

    let loader = this.getElement("aonLoginLoader");
    loader.start();
    login(data)
      .then(() => {
        document.body.style.background = 'transparent';
        loader.stop();
        LS.removeDomain();
        this.getModule().buildHome();
        getCompanies().then(companies => {
          if(companies.length === 1){
            this.companySelection(companies[0], true);
          } else {
            this.getElement("aonHome").showMenu(false);
            this.rootPanelHtml(this.isMobile()
              ? '<aon-mobile-parent id="aonParent"></aon-mobile-parent>'
              : '<aon-parent id="aonParent"></aon-parent>');
          }
          // this.getElement("aonLogin").style.display = 'none';
          // let homeDiv = this.getElement("aonHomeDiv");
          // homeDiv.style.display = 'block';
        });
      })
      .catch((e) => {
        console.log(e);
        loader.stop();
        let error = JSON.parse(e);
        if(!this.isBeta()) {
          let aonLoginError = this.getElement("aonLoginError");
          aonLoginError.style.display = "block";

          let aonLoginErrorMessage = this.getElement("aonLoginErrorMessage");
          aonLoginErrorMessage.innerHTML = error.message;
        }
        let toast = this.getElement('aonLoginToast');
        toast.start(error);
      });
  }

  companySelection(company, onlyOne) {
    localStorage.setItem('company', JSON.stringify(company));
    localStorage.setItem("aon_domain_id", company.id);
    localStorage.setItem("aon_domain_name", company.domain);
    localStorage.setItem("onlyOne", onlyOne);

    let home = this.getElement('aonHome');
    home.showMenu(true);

    let aonHeader = this.getElement(home.AON_HEADER);
    aonHeader.showCompanyOption(company, onlyOne);

    if(!this.isMobile()){
      let aonMenu = this.getElement('aonMenu');
      aonMenu.clear();
      aonMenu.init();
    } else aonHeader.companyIn(onlyOne);

    getUser().then(user => {
      localStorage.setItem('aon_domain_login', user.login);
      this.rootPanelHtml(this.isMobile()
          ? '<aon-mobile-desktop id="aonDesktop"></aon-mobile-desktop>'
          : '<aon-desktop id="aonDesktop"></aon-desktop>');
    });
  }

  onEnter(event) {
    if (event.keyCode === 13) {
      event.preventDefault();
      this.getElement("aonLoginSignin").click();
    }
  }
}

window.customElements.define("aon-login", AonLogin);
