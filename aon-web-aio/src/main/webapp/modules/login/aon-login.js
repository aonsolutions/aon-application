import { AonElement } from "../../components/AonElement.js";
import { signin, getManifest, magicLink, getCompanies, getUser, MOBILE_ACTION, assignUserAuth, clearCompanyService } from "../../services/service.js";

import "../company/aon-mobile-desktop.js";

import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js';

import * as LS from '../../services/localStorageService.js';
import { AonLoader } from "../../components/aon-loader.js";
import { AonDialog } from "../../components/aon-dialog.js";
import { AonToast } from "../../components/aon-toast.js";
import { AonDialogMenu } from "../../components/aon-dialog-menu.js";
import { Language } from "../../models/Language.js";
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
import { AonAuth } from '../user/aon-auth.js';


export class AonLogin extends AonElement {
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
    this.style.display = "flex";
    this.style.height = "100vh";
    this.style.flexDirection = "column";

    // Toolbar
    let toolbar = this.createElement(TAG.DIV);
    toolbar.className = CSS.AON_TOOLBAR2;
    this.appendChild(toolbar);

    let divLogoToolbar = this.createElement(TAG.DIV);
    divLogoToolbar.id = "aonLoginLogoDiv";
    divLogoToolbar.className = CSS.AON_LOGIN_LOGO;
    toolbar.appendChild(divLogoToolbar);

    let divLanguage = this.createElement(TAG.DIV);
    divLanguage.id = "aonLoginLanguageDivToolbar";
    divLanguage.style.cursor = "pointer";
    divLanguage.addEventListener(EVENT.CLICK, () => this.languageDialog());
    toolbar.appendChild(divLanguage);

    let languageButton = new AonIconButton();
    languageButton.id = "aonLoginLanguageButton";
    languageButton.icon = MATERIAL_ICONS.LANGUAGE;
    languageButton.title = MSG.SELECT_LANGUAGE;
    if (LS.isDarkBetaTheme()) languageButton.color = "var(--aonNewWhite)";
    divLanguage.appendChild(languageButton);

    let spanLanguage = this.createElement(TAG.SPAN);
    spanLanguage.id = "aonLoginLanguageSpanToolbar";
    spanLanguage.innerHTML = this.getLanguageText();
    divLanguage.appendChild(spanLanguage);

    // Content
    this.createLoginPanel();
  }

  createLoginPanel() {
    let magicLinkContent = this.getElement("magicLinkContent");
    if (magicLinkContent) this.removeChild(magicLinkContent);

    let loginContent = this.createElement(TAG.DIV);
    loginContent.id = "loginContent";
    loginContent.className = CSS.AON_FLEX_COLUMN;
    loginContent.style.height = "100%";
    loginContent.style.justifyContent = "center";
    
    let loginDivForm = this.createElement(TAG.DIV);
    loginDivForm.id = "loginDivForm";
    loginDivForm.className = CSS.AON_LOGIN_FORM;
    loginContent.appendChild(loginDivForm);
    
    let welcomeImg = this.createElement(TAG.IMG);
	
	welcomeImg.onerror = () => 	 {
		welcomeImg.onerror = () => welcomeImg.style.display = 'none';
		welcomeImg.src = getComputedStyle(document.body).getPropertyValue(`--${CONSTANT.DEFAULT_WELCOME_IMG}`); 
	}; // Hide image if it fails to load
	
	this.getWelcomeImage().then( img => welcomeImg.src = img );
	this.getWelcomeMessage().then( msg  => welcomeImg.title = msg );
	welcomeImg.classList.add(CSS.AON_WELCOME_LOGO_LOGIN);
	loginDivForm.appendChild(welcomeImg);

    this.createTitlePanel(loginDivForm, MSG.LOGIN, MSG.LOGIN_SUBTITLE);

    // Form
    let divFormContent = this.createElement(TAG.DIV);
    divFormContent.classList.add(CSS.AON_LOGIN_FORM_CONTENT);
    loginDivForm.appendChild(divFormContent);

    this.createLoader(divFormContent);

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
   
   			if(username.length == 0 || password.length == 0) return;
	        this.login();
    	}
    });
    
    passwordInput.addEventListener('keyup', (event) => {
	    if (event.key === 'Enter') {
	        if(userInput.value.length == 0 || passwordInput.value.length == 0) return;
	        this.login();
    	}
    });

    // Buttons
    let signIn = this.createElement(TAG.BUTTON);
	signIn.type = 'submit';
    signIn.id = "aonLoginSignin";
    signIn.className = CSS.AON_LOGIN_BUTTON;
    signIn.innerHTML = MSG.SIGN_IN.toUpperCase();
    signIn.addEventListener(EVENT.CLICK, () => this.login());
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
    magicLinkContent.style.height = "100%";
    magicLinkContent.style.justifyContent = "center";

    let magicLinkDivForm = this.createElement(TAG.DIV);
    magicLinkDivForm.id = "magicLinkDivForm";
    magicLinkDivForm.className = CSS.AON_LOGIN_FORM;
    magicLinkContent.appendChild(magicLinkDivForm);

    this.createTitlePanel(
      magicLinkDivForm,
      "Acceso sin contraseña",
      "Introduzca su mail de acceso"
    );

    // Form
    let divFormContent = this.createElement(TAG.DIV);
    divFormContent.classList.add(CSS.AON_LOGIN_FORM_CONTENT);
    magicLinkDivForm.appendChild(divFormContent);

    this.createLoader(divFormContent);

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

    this.createDivider(divFormContent, "O");

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
    divTitleForm.style.display = "flex";
    divTitleForm.style.flexDirection = "column";
    divTitleForm.style.alignItems = "center";
    divTitleForm.style.marginBottom = "2rem";
    parent.appendChild(divTitleForm);

    let h1 = this.createElement(TAG.H1);
    h1.className = CSS.AON_LOGIN_TITLE;
    h1.innerHTML = title;
    divTitleForm.appendChild(h1);

    let h2 = this.createElement(TAG.H1);
    h2.className = CSS.AON_LOGIN_SUB_TITLE;
    h2.innerHTML = subtitle;
    divTitleForm.appendChild(h2);
  }

  createLoader(parent) {
    let aonLoader = new AonLoader();
    aonLoader.id = "aonLoginLoader";
    aonLoader.style.display = "none";
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
    let divMobiles = this.createElement(TAG.DIV);
    divMobiles.id = "logosMobiles";
    divMobiles.classList.add("aonNewLoginDivMobiles");
    parent.appendChild(divMobiles);

    if (!UA.isApp() && this.isMobile()) this.buildAppLogo(divMobiles);
  }

  createToast(parent) {
    let toast = new AonToast();
    toast.id = "aonLoginToast";
    parent.appendChild(toast);
  }

  magicLink(value) {
    let loader = this.getElement("aonLoginLoader");
    loader.style.display = "";
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
          message: "El mensaje se ha enviado correctamente.",
        });
      })
      .catch((e) => {
        loader.stop();
        let error = JSON.parse(e);
        let toast = this.getElement("aonLoginToast");
        toast.start(error);
      });
  }

  languageDialog() {
    let divLanguage = this.getElement("aonLoginLanguageDivToolbar");
    let spanLanguage = this.getElement("aonLoginLanguageSpanToolbar");
    const top = spanLanguage.getBoundingClientRect().top;
    const left = spanLanguage.getBoundingClientRect().left + 25;
    let d = this.getElement("aonHeaderDialogHelpOption");
    if (!d) {
      d = new AonDialogMenu();
      d.id = "aonHeaderDialogHelpOption";
      divLanguage.appendChild(d);
    }

    let options = [
      {
        name: MSG.SPANISH,
        title: MSG.SPANISH,
        permission: true,
        image: "../assets/img/aonIconCastellano.png",
        fn: () => LS.setLanguage(Language.SPANISH),
      },
      {
        name: MSG.ENGLISH,
        title: MSG.ENGLISH,
        permission: true,
        image: "../assets/img/aonIconEnglish.png",
        fn: () => LS.setLanguage(Language.ENGLISH),
      },
      {
        name: MSG.FRENCH,
        title: MSG.FRENCH,
        permission: true,
        image: "../assets/img/aonIconFrancais.png",
        fn: () => LS.setLanguage(Language.FRENCH),
      },
      {
        name: MSG.DEUTSCH,
        title: MSG.DEUTSCH,
        permission: true,
        image: "../assets/img/aonIconDeutsch.png",
        fn: () => LS.setLanguage(Language.DEUTSCH),
      },
      {
        name: MSG.BASQUE,
        title: MSG.BASQUE,
        permission: true,
        image: "../assets/img/aonIconEuskera.png",
        fn: () => LS.setLanguage(Language.BASQUE),
      },
      {
        name: MSG.CATALAN,
        title: MSG.CATALAN,
        permission: true,
        image: "../assets/img/aonIconCatala.png",
        fn: () => LS.setLanguage(Language.CATALAN),
      },
      {
        name: MSG.GALICIAN,
        title: MSG.GALICIAN,
        permission: true,
        image: "../assets/img/aonIconGalego.png",
        fn: () => LS.setLanguage(Language.GALICIAN),
      },
    ];

    d.setMenuOptions(options, top, left);
    d.open();

    d.getContent().addEventListener(EVENT.MOUSELEAVE, () => {
      console.log("Close lenguage");
      d.close();
    });
  }

  getLanguageText() {
    if (LS.getLanguage() && Language.BASQUE === LS.getLanguage()) {
      return MSG.BASQUE;
    } else if (LS.getLanguage() && Language.CATALAN === LS.getLanguage()) {
      return MSG.CATALAN;
    } else if (LS.getLanguage() && Language.DEUTSCH === LS.getLanguage()) {
      return MSG.DEUTSCH;
    } else if (LS.getLanguage() && Language.ENGLISH === LS.getLanguage()) {
      return MSG.ENGLISH;
    } else if (LS.getLanguage() && Language.GALICIAN === LS.getLanguage()) {
      return MSG.GALICIAN;
    } else if (LS.getLanguage() && Language.FRENCH === LS.getLanguage()) {
      return MSG.FRENCH;
    } else return MSG.SPANISH;
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
    divMobiles.style.textAlign = "center";
    const playStore = this.createElement(TAG.A);
    playStore.href =
      "https://play.google.com/store/apps/details?id=aon.solutions";
    playStore.target = "_blank";
    const imgPlayStore = this.createElement("img");
    imgPlayStore.src = "assets/playstore.png"; //: "assets/playstore-disabled.png";
    imgPlayStore.style.height = "38px";
    playStore.appendChild(imgPlayStore);
    divMobiles.appendChild(playStore);

    const appStore = this.createElement(TAG.A);
    appStore.href =
      "https://itunes.apple.com/es/app/aon-solutions/id1538461097";
    appStore.target = "_blank";
    const imgAppStore = this.createElement("img");
    imgAppStore.src = "assets/appstore.png"; //: "assets/appstore-disabled.png";
    imgAppStore.style.height = "40px";
    imgAppStore.style.filter = "grayscale(100%)";
    appStore.appendChild(imgAppStore);
    divMobiles.appendChild(appStore);
  }

  buildLogo() {
    let logoToolbar = this.getElement("aonLoginLogoDiv");
    const hrefToolbar = window.location.href;
    //let srcToolbar = "assets/aon-logo.svg";
    //logoToolbar.src = srcToolbar;
    logoToolbar.addEventListener(EVENT.CLICK, () => {
      this.tag = this.tag + 1;
      if (this.tag >= 5) {
        const url = hrefToolbar.includes("aonsolutions.org")
          ? "https://aon.solutions/"
          : "https://aonsolutions.org";
        let ionicData = {
          action: MOBILE_ACTION.SET_BASE_URL,
          BASE_URL_MOBILE: url,
        };
        changeUrl(ionicData, url);
        this.tag = 0;
      }
    });
  }

  login() {
	  const username = this.getElement("aonLoginUser").value;
	  const password = this.getElement("aonLoginPassword").value;
	  const data = {
		  username: username,
		  password: password,
	  };

	  let loader = this.getElement("aonLoginLoader");
	  loader.style.display = "";
	  loader.start();
	  signin(data)
		  .then((token) => {
			  loader.stop();

			  LS.removeDomain();
			  this.getModule().buildHome();
			  this.getModule().startLoading();
			  let limit = 100;
			  getCompanies({ limit }).then((companies) => {
				  this.getModule().stopLoading();

				  if (companies.length === 1) {
					  this.companySelection(companies[0], true);
				  } else {
					  this.getElement("aonHome").showMenu(false);
					  this.rootPanel(
						  this.isMobile() ? new AonMobileParent() : new AonParent()
					  );
				  }

				  LS.setDomainLogin(username);
				  this.buildDur().then(dur => {
					  if ( this.isUser(dur) && !this.hasAuth(dur)) {
						  LS.setDomainLogin(dur.getUser().login);
						  LS.setDomainId(dur.getDomain().getId());
						  LS.setDomainName(dur.getDomain().getName());

						  let dialog = new AonDialog();
						  dialog.id = "aonAuthDialog" ;
						  document.body.appendChild(dialog);
						  dialog.setTitle(MSG.EMAIL_ADD);
						  dialog.setDescription(MSG.EMAIL_ADD_DESCRIPTION);
						  dialog.close = () =>{ /* do nothing*/ };
						  dialog.hideElement(dialog.BUTTON_CLOSE);

						  let aonAuth = new AonAuth()
							  .onclose((event) => assignUserAuth({ email: event?.detail?.email, user: dur?.user?.id }).then(() => {
								dialog.remove(); 
								this.relogin(username, password); 
							}))
						  dialog.setContent(aonAuth);
						  dialog.getContent().style.height = '80%';
						  dialog.open();

						  //this.rootPanel(new AonAuth().onclose((event) => assignUserAuth({ email: event?.detail?.email, user: dur?.user?.id }).then(() => { this.relogin(username, password); })));
					  }
				  });
			  });
		  })
		  .catch((e) => {
			  console.log(e);
			  loader.stop();
			  let error = JSON.parse(e);
			  let toast = this.getElement("aonLoginToast");
			  toast.start(error);
		  });
  }
  
  signin() {
    const username = this.getElement("aonLoginUser").value;
    const password = this.getElement("aonLoginPassword").value;
    const data = {
      username: username,
      password: password,
    };

    let loader = this.getElement("aonLoginLoader");
    loader.style.display = "";
    loader.start();
    signin(data)
      .then(() => {
        // document.body.style.background = 'transparent';
        loader.stop();

        LS.removeDomain();
        this.getModule().buildHome();
        this.getModule().startLoading();
        let limit = 100;
        getCompanies({ limit }).then((companies) => {
          this.getModule().stopLoading();
  	  if (companies.length === 1) {
            this.companySelection(companies[0], true);
          } else {
            this.getElement("aonHome").showMenu(false);
            this.rootPanel(
              this.isMobile() ? new AonMobileParent() : new AonParent()
            );
          }
        });
      })
      .catch((e) => {
        console.log(e);
        loader.stop();
        let error = JSON.parse(e);
        let toast = this.getElement("aonLoginToast");
        toast.start(error);
      });
  }

  relogin(username, password) {
	
	clearCompanyService();
	
    signin({username, password})
      .then(() => {
		
		  getCompanies({ limit: 100 }).then((companies) => {

			  if (companies.length === 1) {
				  this.companySelection(companies[0], true);
			  } else {
				  this.getElement("aonHome").showMenu(false);
				  this.rootPanel(
					  this.isMobile() ? new AonMobileParent() : new AonParent()
				  );
			  }
		  });
      })
      .catch((e) => {
        console.log(e);
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
  
	useJaas() {
		const queryString = window.location.search;
		const searchParams = new URLSearchParams(queryString);
		return searchParams.get('jaas') != null;
	
	}
	
	isUser(dur) {
		return dur?.user?.id || dur?.user?.login;
	}

	hasAuth(dur) {
		return dur?.user?.uuid && dur.user.uuid.length > 0;
	}

	isConsole(company) {
		return company.type == 'ADMIN' && company.id === 0;
	}
	
	isPasswordExpired(dur) {
        return dur?.user?.expirationDate && new Date(dur.user.expirationDate) < new Date();
    }
	
	getWelcomeImage() {
		return new Promise((resolve, reject) => {
        resolve(`${window.location.protocol}//${LS.getCompany()?.domain || window.location.hostname}:${window.location.port}/aonDocuments/company.logo`);
		});
    }
	
	getWelcomeMessage() {
		return new Promise((resolve, reject) => {
			if ( LS.getCompany()?.name ) {
				resolve(`<span style='font-weight:lighter;' >${MSG.ENVIRONMENT}</span> <span style='font-weight:bolder;'>${LS.getCompany()?.name}</span>`);
			} else if ( this.getDur() ) {
				resolve(`<span style='font-weight:lighter;'  >${MSG.ENVIRONMENT}</span> <span style='font-weight:bolder;'>${this.getDur().domain.description}</span>`)
			}  
			else  {
				this.buildDur()
				.then( dur =>  resolve(`<span style='font-weight:lighter;' >${MSG.ENVIRONMENT}</span> <span style='font-weight:bolder;'>${dur.domain.description}</span>`))
				.catch( err  => resolve( `<span style='font-weight:bolder;'>${MSG.WELCOME_TO_AON_SOLUTIONS}</span>` ) );
			} 
		});  
	}
	
	
}
if(!window.customElements.get(TAG.AON_LOGIN)){
	window.customElements.define(TAG.AON_LOGIN, AonLogin);
}
