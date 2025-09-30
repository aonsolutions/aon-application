import { AonElement } from "../../components/AonElement.js";
import { login, getManifest, magicLink, getCompanies, getUser, MOBILE_ACTION } from "../../services/service.js";
import { CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js';
import * as LS from '../../services/localStorageService.js';
import { AonLoader } from "../../components/aon-loader.js";
import { AonToast } from "../../components/aon-toast.js";
import { AonEmail } from "../../components/aon-email.js";
import { AonMobileParent } from "../company/aon-mobile-parent.js";
import { AonParent } from "aonparent";
import { AonIconButton } from "../../components/aon-icon-button.js";
import { createInput } from "../../components/CreateComponent.js";
import { changeUrl } from '../../services/actionService.js';

import * as UA from '../../services/userAgentService.js';
import { AonMobileHome } from "../home/aon-mobile-home.js";
import { AonDesktop } from "../company/aon-desktop.js";

import * as GWT from '../../gwt/gwt.js';

import { AonRightPanel } from '../aon-right-panel.js';
import { AonConfig } from '../aon-config.js';

export class AonNewLogin extends AonElement {
  tag;
  userInput;
  companyLogo;

  constructor(userInput, companyLogo) {
    super();
    this.tag = 0;
    this.userInput = userInput || new AonEmail();
    this.companyLogo = companyLogo || this.createElement(TAG.SPAN);
  }

  initialize() {}

  build() {
    // Toolbar
    let toolbar = this.createElement(TAG.DIV);
    toolbar.className = CSS.AON_TOOLBAR2;
    this.appendChild(toolbar);

    let divLogoToolbar = this.createElement(TAG.DIV);
    divLogoToolbar.id = "aon-logo";
    divLogoToolbar.className = CSS.AON_LOGIN_LOGO;
    toolbar.appendChild(divLogoToolbar);

    const rightPanel = new AonRightPanel();
    this.appendChild(rightPanel);
    rightPanel.setTitle(MSG.CONFIGURATION);
    rightPanel.setContent(new AonConfig());

    let headerConfig    = this.createElement(TAG.DIV);
    headerConfig.id     = "loginConfig";
    headerConfig.title  = MSG.CONFIGURATION;

    let headerConfigButton  = new AonIconButton();
    headerConfigButton.id   = "loginConfigButton";
    headerConfigButton.icon = "settings";
    headerConfigButton.addEventListener(EVENT.CLICK, (e) => {
      e.stopPropagation();
      rightPanel.toogle();
    });
    headerConfig.appendChild(headerConfigButton);

    toolbar.appendChild(headerConfig);

    // Content
    this.createLogo();
    this.createLoginPanel();
  }
  
  // Se inyecta el logo por css dependiendo del tema
  createLogo(){
    let logoContent = this.createElement(TAG.DIV);
    logoContent.id = "logoContent";
    let logo = this.createElement(TAG.DIV);
    logo.id = "logoSVG";
    logoContent.appendChild(logo);
    this.appendChild(logoContent);
  }

  createLoginPanel() {
    let magicLinkContent = this.getElement("magicLinkContent");
    if (magicLinkContent) this.removeChild(magicLinkContent);

    let loginContent = this.createElement(TAG.DIV);
    loginContent.id = "loginContent";
    loginContent.className = CSS.AON_FLEX_COLUMN;
    if(!this.isNewStyle()){
      loginContent.style.height = "100%";
      loginContent.style.justifyContent = "center";
    } 

    let loginDivForm = this.createElement(TAG.DIV);
    loginDivForm.id = "loginDivForm";
    loginDivForm.className = CSS.AON_LOGIN_FORM;
    loginContent.appendChild(loginDivForm);
    // se carga antes
    if(this.isNewStyle()){
      this.createLoader(loginDivForm);
    }
    // se carga textos
    this.createTitlePanel(loginDivForm, MSG.LOGIN, MSG.LOGIN_SUBTITLE);
    // Form
    let divFormContent = this.createElement(TAG.DIV);
    divFormContent.classList.add(CSS.AON_LOGIN_FORM_CONTENT);
    loginDivForm.appendChild(divFormContent);
    // se carga en el form
    if(!this.isNewStyle()){
      this.createLoader(divFormContent);
    }

    if (this.userInput && this.userInput.innerHTML)
      this.userInput.innerHTML = "";
    let userInput = this.createAonElement(
      this.userInput,
      "aonLoginUser",
      MSG.USER
    );
    userInput.setRequired(true);
    divFormContent.appendChild(userInput);

    let passwordInput = createInput("aonLoginPassword", MSG.PASSWORD);
    passwordInput.setRequired(true);
    passwordInput.type = "password";
    divFormContent.appendChild(passwordInput);
    
    userInput.addEventListener('keyup', (event) => {
	    if (event.key === 'Enter') {
			const username = this.getElement("aonLoginUser").value;
    		const password = this.getElement("aonLoginPassword").value;
   			if(username.length === 0 || password.length === 0) return;
	        this.signin();
    	}
    });
    
    passwordInput.addEventListener('keyup', (event) => {
	    if (event.key === 'Enter') {
	        if(userInput.value.length === 0 || passwordInput.value.length === 0) return;
	        this.signin();
    	}
    });

    // Buttons
    let signIn = this.createElement(TAG.BUTTON);
	  signIn.type = 'submit';
    signIn.id = "aonLoginSignin";
    signIn.className = CSS.AON_LOGIN_BUTTON;
    signIn.innerHTML = MSG.SIGN_IN.toUpperCase();
    signIn.addEventListener(EVENT.CLICK, () => this.signin());
    divFormContent.appendChild(signIn);

    getManifest().then((manifest) => {
      let version = MSG.VERSION + ": " + manifest.build_date;
      signIn.title = version;
    });

    this.createDivider(divFormContent, MSG.OR_ACCESS);
    
    let magicLinkButton = this.createElement(TAG.BUTTON);
    magicLinkButton.id = "aonLoginMagicLink";
    magicLinkButton.className = CSS.AON_MAGIC_BUTTON;
    magicLinkButton.title = MSG.MAGIC_LINK;
    magicLinkButton.innerHTML = MSG.SIGN_IN_WITHOUT_PASSWORD.toUpperCase();
    magicLinkButton.addEventListener(EVENT.CLICK, () =>
      this.createMagicLinkPanel()
    );
    if (!LS.isDarkBetaTheme())
      magicLinkButton.addEventListener(
        EVENT.MOUSEOVER,
        () => (signIn.className = "aonMagicButtonHover")
      );
    magicLinkButton.addEventListener(
      "mouseout",
      () => (signIn.className = CSS.AON_LOGIN_BUTTON)
    );
    divFormContent.appendChild(magicLinkButton);

    // Moviles
    this.createMobileApps(divFormContent);

    // Toast
    this.createToast(loginContent);

    this.appendChild(loginContent);
  }

  createMagicLinkPanel() {
    let loginContent = this.getElement("loginContent");
    if (loginContent) this.removeChild(loginContent);

    let magicLinkContent = this.createElement(TAG.DIV);
    magicLinkContent.id = "magicLinkContent";
    magicLinkContent.className = CSS.AON_FLEX_COLUMN;
    if(!this.isNewStyle()){
      magicLinkContent.style.height = "100%";
      magicLinkContent.style.justifyContent = "center";
    }

    let magicLinkDivForm = this.createElement(TAG.DIV);
    magicLinkDivForm.id = "magicLinkDivForm";
    magicLinkDivForm.className = CSS.AON_LOGIN_FORM;
    magicLinkContent.appendChild(magicLinkDivForm);

    // se carga antes
    if(this.isNewStyle()){
      this.createLoader(magicLinkDivForm);
    }

    this.createTitlePanel(
      magicLinkDivForm,
      "Acceso sin contraseña",
      "Introduzca su mail de acceso"
    );

    // Form
    let divFormContent = this.createElement(TAG.DIV);
    divFormContent.classList.add(CSS.AON_LOGIN_FORM_CONTENT);
    magicLinkDivForm.appendChild(divFormContent);

    // se carga en el form
    if(!this.isNewStyle()){
      this.createLoader(divFormContent);
    }

    if (this.userInput && this.userInput.innerHTML)
      this.userInput.innerHTML = "";
    let userInput = this.createAonElement(
      this.userInput,
      "aonMagicLinkUser",
      MSG.EMAIL
    );
    userInput.setRequired(true);
    divFormContent.appendChild(userInput);
    
     userInput.addEventListener('keydown', (event) => {
	    if (event.key === 'Enter') {
	        if(userInput.value.length == 0) return;
	        this.magicLink(userInput.value);
    	}
    });

    // Buttons
    let signIn = this.createElement(TAG.BUTTON);
    signIn.id = "aonMagicLinkSignin";
    signIn.className = CSS.AON_LOGIN_BUTTON;
    signIn.innerHTML = "RECIBIR CORREO DE ACCESO";
    signIn.addEventListener(EVENT.CLICK, (event) => {
		event.preventDefault();
		this.magicLink(userInput.value);
	});
    divFormContent.appendChild(signIn);

    getManifest().then((manifest) => {
      let version = MSG.VERSION + ": " + manifest.build_date;
      signIn.title = version;
    });
    
    if(!this.isNewStyle()){
      this.createDivider(divFormContent, "O");
    }

    let backButton = this.createElement(TAG.BUTTON);
    backButton.id = "backButton";
    backButton.className = CSS.AON_MAGIC_BUTTON;
    backButton.title = MSG.BACK;
    backButton.innerHTML = "Volver Pantalla Inicio Sesi\u00f3n".toUpperCase();
    backButton.addEventListener(EVENT.CLICK, () => this.createLoginPanel());
    if (!LS.isDarkBetaTheme())
      backButton.addEventListener(
        EVENT.MOUSEOVER,
        () => (signIn.className = "aonMagicButtonHover")
      );
    backButton.addEventListener(
      "mouseout",
      () => (signIn.className = CSS.AON_LOGIN_BUTTON)
    );
    divFormContent.appendChild(backButton);

    // Moviles
    this.createMobileApps(divFormContent);

    // Toast
    this.createToast(magicLinkContent);

    this.appendChild(magicLinkContent);
  }

  createTitlePanel(parent, title, subtitle) {
    let divTitleForm = this.createElement(TAG.DIV);
    divTitleForm.id = "divTitleForm";
    if(!this.isNewStyle()){
      divTitleForm.style.display = "flex";
      divTitleForm.style.flexDirection = "column";
      divTitleForm.style.alignItems = "center";
      divTitleForm.style.marginBottom = "2rem";
    }
    parent.appendChild(divTitleForm);

    if(!this.isNewStyle()){
      let h1 = this.createElement(TAG.H1);
      h1.className = CSS.AON_LOGIN_TITLE;
      h1.innerHTML = title;
      divTitleForm.appendChild(h1);

      let h2 = this.createElement(TAG.H1);
      h2.className = CSS.AON_LOGIN_SUB_TITLE;
      h2.innerHTML = subtitle;
      divTitleForm.appendChild(h2);
    } else {
      let title = this.createElement(TAG.DIV);
      title.className = CSS.AON_LOGIN_TITLE;
      title.innerHTML = subtitle;
      divTitleForm.appendChild(title);
    }
  }

  createLoader(parent) {
    let aonLoader = new AonLoader();
    aonLoader.id = "aonLoginLoader";
    if(!this.isNewStyle()){
      aonLoader.style.display = "none";
    }
    parent.appendChild(aonLoader);
  }

  createDivider(parent, text) {
    let dividerButtons = this.createElement(TAG.DIV);
    dividerButtons.className = CSS.AON_DIVIDER_BUTTONS;
    let dividerSpan = this.createElement(TAG.SPAN);
    dividerSpan.className = CSS.AON_DIVIDER_SPAN;
    dividerSpan.innerHTML = text;
    dividerButtons.appendChild(dividerSpan);
    parent.appendChild(dividerButtons);
  }

  createMobileApps(parent) {
    if(!this.isNewStyle()){
      if (!UA.isApp() && this.isMobile()){
        let divMobiles = this.createElement(TAG.DIV);
        divMobiles.id = "logosMobiles";
        divMobiles.classList.add("aonNewLoginDivMobiles");
        parent.appendChild(divMobiles);
        this.buildAppLogo(divMobiles);
      }
    } else {
      // No se carga si accedes desde la APP
      if (!UA.isApp()){
        let divMobiles = this.createElement(TAG.DIV);
        divMobiles.id = "logosMobiles";
        divMobiles.classList.add("aonNewLoginDivMobiles");
        parent.appendChild(divMobiles);
        this.buildAppLogo(divMobiles);
      }
    }
  }

  createToast(parent) {
    let toast = new AonToast();
    toast.id = "aonLoginToast";
    parent.appendChild(toast);
  }

  magicLink(value) {
    let loader = this.getElement("aonLoginLoader");
    if(!this.isNewStyle()){
      loader.style.display = "";
    }
    loader.start();

    let data = {
      email: value,
      url: window.location.href,
    };
    magicLink(data)
      .then(() => {
        loader.stop();
        let toast = this.getElement("aonLoginToast");
        toast.start({
          type: "success",
          message: "El mensaje se ha enviado correctamente."
        });
      })
      .catch((e) => {
        loader.stop();
        let error = JSON.parse(e);
        let toast = this.getElement("aonLoginToast");
        toast.start(error);
      });
  }

  connectedCallback() {
    this.initialize();
    this.build();

    this.buildLogo();

    let username = this.getElement("aonLoginUser");
    let useremail = this.getElement("aonMagicLinkUser");

    if (username) username.focus();
    else if (useremail) useremail.focus();
  }

  buildAppLogo(divMobiles) {
    //const div  = this.getElement('logosMobiles');
    if(!this.isNewStyle()){
      divMobiles.style.textAlign = "center";
    } 
    const playStore = this.createElement(TAG.A);
    playStore.href = "https://play.google.com/store/apps/details?id=aon.solutions";
    playStore.target = "_blank";
    const imgPlayStore = this.createElement("img");
    imgPlayStore.src = "assets/playstore.png"; //: "assets/playstore-disabled.png";
    if(!this.isNewStyle()){
      imgPlayStore.style.height = "38px";
    } 
    playStore.appendChild(imgPlayStore);
    divMobiles.appendChild(playStore);

    const appStore = this.createElement(TAG.A);
    appStore.href = "https://itunes.apple.com/es/app/aon-solutions/id1538461097";
    appStore.target = "_blank";
    const imgAppStore = this.createElement("img");
    imgAppStore.src = "assets/appstore.png"; //: "assets/appstore-disabled.png";
    if(!this.isNewStyle()){
      imgAppStore.style.height = "40px";
      imgAppStore.style.filter = "grayscale(100%)";
    } 
    appStore.appendChild(imgAppStore);
    divMobiles.appendChild(appStore);
  }

  buildLogo() {
    let logoToolbar = this.getElement("aon-logo");
    const hrefToolbar = window.location.href;
    //let srcToolbar = "assets/aon-logo.svg";
    //logoToolbar.src = srcToolbar;
    // logoToolbar.addEventListener(EVENT.CLICK, () => {
    //   this.tag = this.tag + 1;
    //   if (this.tag >= 5) {
    //     const url = hrefToolbar.includes("aonsolutions.org")
    //       ? "https://aon.solutions/"
    //       : "https://aonsolutions.org";
    //     let ionicData = {
    //       action: MOBILE_ACTION.SET_BASE_URL,
    //       BASE_URL_MOBILE: url,
    //     };
    //     changeUrl(ionicData, url);
    //     this.tag = 0;
    //   }
    // });
  }

  signin() {
    const username = this.getElement("aonLoginUser").value;
    const password = this.getElement("aonLoginPassword").value;
    const data = {
      username: username,
      password: password,
    };

    let loader = this.getElement("aonLoginLoader");
    if(!this.isNewStyle()){
      loader.style.display = "";
    }
    loader.start();
    login(data)
      .then(() => {
        loader.stop();
        let actualCompanyName = window.location.hostname;
        LS.removeDomain();
        this.getModule().buildHome();
        this.getModule().startLoading();
        let limit = 100;
        getCompanies({ limit }).then((companies) => {
          this.getModule().stopLoading();
		  if (companies.length === 1) {
            this.companySelection(companies[0], true);
          } else if(companies.length > 1 ) {
            for (let company of companies) {
              if (company.domain === actualCompanyName) {
                localStorage.setItem("company", JSON.stringify(company));
                localStorage.setItem("aon_domain_id", company.id);
                localStorage.setItem("aon_domain_name", company.domain);
                localStorage.setItem("aon_domain_document", company.document);
                localStorage.setItem("onlyOne", true);
                this.isMobile() ? new AonMobileParent() : new AonDesktop()
              }
            }
			let cps = companies.filter(r => {
				return r.id === parseInt(LS.getDomainId());
			});
			if(cps.length > 0 && !cps[0].parent){
				this.companySelection(cps[0], true);
			} else {				
	            this.getElement("aonHome").showMenu(false);
	            this.rootPanel(
	              this.isMobile() ? new AonMobileParent() : new AonParent()
	            );
			}
          }
        });
      })
      .catch((e) => {
        //console.log(e);
        loader.stop();
        let error = JSON.parse(e);
        let toast = this.getElement("aonLoginToast");
        toast.start(error);
      });
  }

  companySelection(company, onlyOne) {
    localStorage.setItem("company", JSON.stringify(company));
    localStorage.setItem("aon_domain_id", company.id);
    localStorage.setItem("aon_domain_name", company.domain);
    localStorage.setItem("aon_domain_document", company.document);
    localStorage.setItem("onlyOne", onlyOne);

    let home = this.getElement("aonHome");
    home.showMenu(true);

    let aonHeader = this.getElement(home.AON_HEADER);
    aonHeader.showCompanyOption(company, onlyOne);

    if (!this.isMobile()) {
      let aonMenu = this.getElement("aonMenu");
      aonMenu.clear();
      aonMenu.init().then(() => aonMenu.open());
    } else aonHeader.companyIn(onlyOne);

    getUser().then((user) => {
      localStorage.setItem("aon_domain_login", user.login);
      if (UA.isMobile()) {
        this.rootPanel(new AonMobileHome());
      } else if(this.isConsole(company)){
		GWT.iLoad(GWT.CONSOLE);
	  } else {
        this.rootPanel(new AonDesktop());
        let portal = LS.isLeftMenu();
        LS.setPortalChecked(portal);
      }
    });
  }

  isConsole(company) {
	return company.type == 'ADMIN' && company.id === 0;
  }
}

if(!window.customElements.get(TAG.AON_NEW_LOGIN)){
	window.customElements.define(TAG.AON_NEW_LOGIN, AonNewLogin);
}
