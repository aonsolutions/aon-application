import { AonElement } from "../../components/AonElement.js";
import { login, getManifest, rememberPassword, magicLink, getCompanies, getUser, mobileAction, MOBILE_ACTION } from "../../services/service.js";

import "../../components/aon-input.js";
import "../../components/aon-loader.js";
import "../../components/aon-dialog.js";
import "../../components/aon-toast.js";

import "../company/aon-mobile-desktop.js";

import { CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js'; 

import { AonInput } from "../../components/aon-input.js";
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
    this.style.display = "flex";
    this.style.height = "100vh";
    this.style.flexDirection = "column";

    // Toolbar
    let toolbar = this.createElement(TAG.DIV);
    toolbar.className = CSS.AON_TOOLBAR2;
    this.appendChild(toolbar);

    let divLogoToolbar = this.createElement(TAG.DIV);
    divLogoToolbar.id = 'aonLoginLogoDiv';
	  divLogoToolbar.className = CSS.AON_LOGIN_LOGO;
    toolbar.appendChild(divLogoToolbar);

    let divLanguage = this.createElement(TAG.DIV);
    divLanguage.id = 'aonLoginLanguageDivToolbar';
    toolbar.appendChild(divLanguage);

    let languageButton = new AonIconButton();
    languageButton.id = "aonLoginLanguageButton";
    languageButton.icon = MATERIAL_ICONS.LANGUAGE;
    languageButton.title = MSG.SELECT_LANGUAGE;
    if(LS.isDarkBetaTheme()) languageButton.color = "var(--aonNewWhite)";
    languageButton.addEventListener(EVENT.CLICK, () => this.languageDialog());
    divLanguage.appendChild(languageButton);

    let spanLanguage = this.createElement(TAG.SPAN);
    spanLanguage.id = 'aonLoginLanguageSpanToolbar';
    spanLanguage.innerHTML = this.getLanguageText();
    divLanguage.appendChild(spanLanguage);

    // Content
    this.createLoginPanel();
  }

  createLoginPanel(){
    let magicLinkContent = this.getElement("magicLinkContent");
    if(magicLinkContent) this.removeChild(magicLinkContent);

    let loginContent = this.createElement(TAG.DIV);
    loginContent.id = "loginContent";
    loginContent.className = CSS.AON_FLEX_COLUMN;
    loginContent.style.height = "100%"
    loginContent.style.justifyContent = "center";
    
    let loginDivForm = this.createElement(TAG.DIV);
    loginDivForm.id = "loginDivForm";
    loginDivForm.className = CSS.AON_LOGIN_FORM;
    loginContent.appendChild(loginDivForm);

    let divCompanyLogoForm = this.createElement(TAG.DIV);
    divCompanyLogoForm.id = "divCompanyLogoForm";
    divCompanyLogoForm.className = CSS.AON_LOGIN_FORM;
    divCompanyLogoForm.style.marginBottom = "2rem";
	  divCompanyLogoForm.appendChild(this.companyLogo);
    loginDivForm.appendChild(divCompanyLogoForm);

    let divTitleForm = this.createElement(TAG.DIV);
    divTitleForm.id = "divTitleForm";
    divTitleForm.style.display = "flex";
    divTitleForm.style.flexDirection = "column";
    divTitleForm.style.alignItems = "center";
    divTitleForm.style.marginBottom = "2rem";
    loginDivForm.appendChild(divTitleForm);

    let h1 = this.createElement(TAG.H1);
    h1.className = CSS.AON_LOGIN_TITLE;
    h1.innerHTML = MSG.LOGIN;
    divTitleForm.appendChild(h1);

    let h2 = this.createElement(TAG.H1);
    h2.className = CSS.AON_LOGIN_SUB_TITLE;
    h2.innerHTML = MSG.LOGIN_SUBTITLE;
    divTitleForm.appendChild(h2);

    // Form
    let divFormContent = this.createElement(TAG.DIV);
    divFormContent.classList.add(CSS.AON_LOGIN_FORM_CONTENT);
    loginDivForm.appendChild(divFormContent);

    let aonLoader = new AonLoader();
    aonLoader.id = 'aonLoginLoader';
    aonLoader.style.display = 'none';
    divFormContent.appendChild(aonLoader);

    if(this.userInput && this.userInput.innerHTML) this.userInput.innerHTML = "";
    let userInput = this.createAonElement(this.userInput, 'aonLoginUser', MSG.USER);
    userInput.setRequired(true);
    userInput.addEventListener(EVENT.KEYUP, (event) => this.onEnter(event));
    divFormContent.appendChild(userInput);

    let passwordInput = createInput('aonLoginPassword', MSG.PASSWORD);
    passwordInput.setRequired(true);
    passwordInput.type = 'password';
    passwordInput.addEventListener(EVENT.KEYUP, (event) => this.onEnter(event));
    divFormContent.appendChild(passwordInput);

    // Buttons
    let signIn = this.createElement(TAG.BUTTON);
    signIn.id = 'aonLoginSignin';
    signIn.className = CSS.AON_LOGIN_BUTTON;
    getManifest().then(
		  (manifest) => {
				let version = MSG.VERSION + ": " + manifest.build_date;
        signIn.title = version;
			}
		);
    signIn.innerHTML = MSG.SIGN_IN.toUpperCase();
    signIn.addEventListener(EVENT.CLICK, () => this.signin());
    divFormContent.appendChild(signIn);

    let dividerButtons = this.createElement(TAG.DIV);
    dividerButtons.className = CSS.AON_DIVIDER_BUTTONS;
    let dividerSpan = this.createElement(TAG.SPAN);
    dividerSpan.className = CSS.AON_DIVIDER_SPAN,
    dividerSpan.innerHTML = MSG.OR_ACCESS;
    dividerButtons.appendChild(dividerSpan);
    divFormContent.appendChild(dividerButtons);

    let magicLinkButton = this.createElement(TAG.BUTTON);
    magicLinkButton.id = 'aonLoginMagicLink';
    magicLinkButton.className = CSS.AON_MAGIC_BUTTON;
    magicLinkButton.title = MSG.MAGIC_LINK;
    magicLinkButton.innerHTML = MSG.SIGN_IN_WITHOUT_PASSWORD.toUpperCase();
    magicLinkButton.addEventListener(EVENT.CLICK, () => this.createMagicLinkPanel());
    if(!LS.isDarkBetaTheme())
      magicLinkButton.addEventListener(EVENT.MOUSEOVER, () => signIn.className = "aonMagicButtonHover");
    magicLinkButton.addEventListener("mouseout", () => signIn.className = CSS.AON_LOGIN_BUTTON);
    divFormContent.appendChild(magicLinkButton);

    // Moviles
    let divMobiles = this.createElement(TAG.DIV);
    divMobiles.id = 'logosMobiles';
    divMobiles.classList.add("aonNewLoginDivMobiles");
    divFormContent.appendChild(divMobiles);

    let dialog = new AonDialog();
    dialog.id = "aonDialogLogin";
    loginContent.appendChild(dialog);

    let toast = new AonToast();
    toast.id = "aonLoginToast";
    loginContent.appendChild(toast);

    this.appendChild(loginContent);
  }

  createMagicLinkPanel(){
    let loginContent = this.getElement("loginContent");
    if(loginContent){
      aonLoginUser
      this.removeChild(loginContent);
    } 

    let magicLinkContent = this.createElement(TAG.DIV);
    magicLinkContent.id = "magicLinkContent";
    magicLinkContent.className = CSS.AON_FLEX_COLUMN;
    magicLinkContent.style.height = "100%"
    magicLinkContent.style.justifyContent = "center";
    
    let magicLinkDivForm = this.createElement(TAG.DIV);
    magicLinkDivForm.id = "magicLinkDivForm";
    magicLinkDivForm.className = CSS.AON_LOGIN_FORM;
    magicLinkContent.appendChild(magicLinkDivForm);

    let divCompanyLogoForm = this.createElement(TAG.DIV);
    divCompanyLogoForm.id = "divCompanyLogoForm";
    divCompanyLogoForm.className = CSS.AON_LOGIN_FORM;
    divCompanyLogoForm.style.marginBottom = "2rem";
	  divCompanyLogoForm.appendChild(this.companyLogo);
    magicLinkDivForm.appendChild(divCompanyLogoForm);

    let divTitleForm = this.createElement(TAG.DIV);
    divTitleForm.id = "divTitleForm";
    divTitleForm.style.display = "flex";
    divTitleForm.style.flexDirection = "column";
    divTitleForm.style.alignItems = "center";
    divTitleForm.style.marginBottom = "2rem";
    magicLinkDivForm.appendChild(divTitleForm);

    let h1 = this.createElement(TAG.H1);
    h1.className = CSS.AON_LOGIN_TITLE;
    h1.innerHTML = "Acceso sin contraseña";
    divTitleForm.appendChild(h1);

    let h2 = this.createElement(TAG.H1);
    h2.className = CSS.AON_LOGIN_SUB_TITLE;
    h2.innerHTML = "Introduzca su mail de acceso";
    divTitleForm.appendChild(h2);

    // Form
    let divFormContent = this.createElement(TAG.DIV);
    divFormContent.classList.add(CSS.AON_LOGIN_FORM_CONTENT);
    magicLinkDivForm.appendChild(divFormContent);

    let aonLoader = new AonLoader();
    aonLoader.id = 'aonLoginLoader';
    aonLoader.style.display = 'none';
    divFormContent.appendChild(aonLoader);

    if(this.userInput && this.userInput.innerHTML) this.userInput.innerHTML = "";
    let userInput = this.createAonElement(this.userInput, 'aonMagicLinkUser', MSG.EMAIL);
    userInput.setRequired(true);
    userInput.addEventListener(EVENT.KEYUP, (event) => this.onEnter(event));
    divFormContent.appendChild(userInput);

    // Buttons
    let signIn = this.createElement(TAG.BUTTON);
    signIn.id = 'aonMagicLinkSignin';
    signIn.className = CSS.AON_LOGIN_BUTTON;
    getManifest().then(
		  (manifest) => {
				let version = MSG.VERSION + ": " + manifest.build_date;
        signIn.title = version;
			}
		);
    signIn.innerHTML = MSG.ACCESS.toUpperCase();
    signIn.addEventListener(EVENT.CLICK, () => this.magicLink(userInput.value));
    divFormContent.appendChild(signIn);

    let dividerButtons = this.createElement(TAG.DIV);
    dividerButtons.className = CSS.AON_DIVIDER_BUTTONS;
    let dividerSpan = this.createElement(TAG.SPAN);
    dividerSpan.className = CSS.AON_DIVIDER_SPAN,
    dividerSpan.innerHTML = MSG.OR_ACCESS;
    dividerButtons.appendChild(dividerSpan);
    divFormContent.appendChild(dividerButtons);

    let backButton = this.createElement(TAG.BUTTON);
    backButton.id = 'backButton';
    backButton.className = CSS.AON_MAGIC_BUTTON;
    backButton.title = MSG.BACK;
    backButton.innerHTML = MSG.BACK.toUpperCase();
    backButton.addEventListener(EVENT.CLICK, () => this.createLoginPanel());
    if(!LS.isDarkBetaTheme())
      backButton.addEventListener(EVENT.MOUSEOVER, () => signIn.className = "aonMagicButtonHover");
    backButton.addEventListener("mouseout", () => signIn.className = CSS.AON_LOGIN_BUTTON);
    divFormContent.appendChild(backButton);

    // Moviles
    let divMobiles = this.createElement(TAG.DIV);
    divMobiles.id = 'logosMobiles';
    divMobiles.classList.add("aonNewLoginDivMobiles");
    divFormContent.appendChild(divMobiles);

    let dialog = new AonDialog();
    dialog.id = "aonDialogLogin";
    magicLinkContent.appendChild(dialog);

    let toast = new AonToast();
    toast.id = "aonLoginToast";
    magicLinkContent.appendChild(toast);

    this.appendChild(magicLinkContent);
  }

  magicLink(value) {
    let loader = this.getElement("aonLoginLoader");
    loader.style.display = '';
    loader.start();
    
    let data = {
      "email" : value,
      "url" : window.location.href
    }
    magicLink(data).then(()=> {
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
  

  languageDialog() {
    let divLanguage = this.getElement('aonLoginLanguageDivToolbar');
    let spanLanguage = this.getElement('aonLoginLanguageSpanToolbar');
    const top  = spanLanguage.getBoundingClientRect().top + 25;
    const left = spanLanguage.getBoundingClientRect().left;
    let d = this.getElement('aonHeaderDialogHelpOption');
    if(!d) {
      d = new AonDialogMenu();
      d.id = 'aonHeaderDialogHelpOption';
      divLanguage.appendChild(d);
    }  
    d.getContent().addEventListener(EVENT.MOUSELEAVE, () => d.close());

    let options = [{
      name: MSG.SPANISH,
      title: MSG.SPANISH,
      permission: true,
      image: '../assets/img/aonIconCastellano.png',
      fn: () => LS.setLanguage(Language.SPANISH)
    }, {
      name: MSG.ENGLISH,
      title: MSG.ENGLISH,
      permission: true,
      image: '../assets/img/aonIconEnglish.png',
      fn: () => LS.setLanguage(Language.ENGLISH)
    }, {
      name: MSG.FRENCH,
      title: MSG.FRENCH,
      permission: true,
      image: '../assets/img/aonIconFrancais.png',
      fn: () => LS.setLanguage(Language.FRENCH)
    }, {
      name: MSG.DEUTSCH,
      title: MSG.DEUTSCH,
      permission: true,
      image: '../assets/img/aonIconDeutsch.png',
      fn: () => LS.setLanguage(Language.DEUTSCH)
    }, {
      name: MSG.BASQUE,
      title: MSG.BASQUE,
      permission: true,
      image: '../assets/img/aonIconEuskera.png',
      fn: () => LS.setLanguage(Language.BASQUE)
    }, {
      name: MSG.CATALAN,
      title: MSG.CATALAN,
      permission: true,
      image: '../assets/img/aonIconCatala.png',
      fn: () => LS.setLanguage(Language.CATALAN)
    }, {
      name: MSG.GALICIAN,
      title: MSG.GALICIAN,
      permission: true,
      image: '../assets/img/aonIconGalego.png',
      fn: () => LS.setLanguage(Language.GALICIAN)
    }];

    d.setMenuOptions(options, top, left);
    d.open();
  }

  getLanguageText() {
      if(LS.getLanguage() && Language.BASQUE === LS.getLanguage()){
        return MSG.BASQUE;
      } else if(LS.getLanguage() && Language.CATALAN === LS.getLanguage()){
        return MSG.CATALAN;
      } else if(LS.getLanguage() && Language.DEUTSCH === LS.getLanguage()){
        return MSG.DEUTSCH;
      } else if(LS.getLanguage() && Language.ENGLISH === LS.getLanguage()){
        return MSG.ENGLISH;
      } else if(LS.getLanguage() && Language.GALICIAN === LS.getLanguage()){
        return MSG.GALICIAN;
      } else if(LS.getLanguage() && Language.FRENCH === LS.getLanguage()){
        return MSG.FRENCH;
      } else return MSG.SPANISH;
  }

  connectedCallback() {
    this.initialize();
    this.build();
    
    this.buildLogo();
    if(!UA.isApp() && this.isMobile()){ // si es app
      this.buildAppLogo();
    } else {
      this.getElement('logosMobiles').style.display = 'none';
    }
  
    let aonManifest = this.getElement("aonManifest");
    if(aonManifest){
      getManifest().then(
        (manifest) => (aonManifest.innerHTML = MSG.VERSION + ": " + manifest.build_date)
      );
    }
  
    let username = this.getElement("aonLoginUser");
    let useremail = this.getElement("aonMagicLinkUser");

    if(username) username.focus();
    else if(useremail) useremail.focus();
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

  buildLogo() {
    let logoToolbar = this.getElement("aonLoginLogoDiv");
    const hrefToolbar = window.location.href;
    //let srcToolbar = "assets/aon-logo.svg";
    //logoToolbar.src = srcToolbar;
    logoToolbar.addEventListener(EVENT.CLICK, ()=>{
      this.tag = this.tag + 1;
      if(this.tag >= 5){
        const url = hrefToolbar.includes("aonsolutions.org") ? "https://aon.solutions/" : "https://aonsolutions.org";
        let ionicData = {
          action: MOBILE_ACTION.SET_BASE_URL,
          BASE_URL_MOBILE: url
        }
        changeUrl(ionicData, url);
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
    loader.style.display = '';
    loader.start();
    login(data)
      .then(() => {
        // document.body.style.background = 'transparent';
        loader.stop();

        LS.removeDomain();
        this.getModule().buildHome();
        this.getModule().startLoading();
		    let limit = 100;
        getCompanies({limit}).then(companies => {
          this.getModule().stopLoading();
          
          if(companies.length === 1){
            this.companySelection(companies[0], true);
          } else {
            this.getElement("aonHome").showMenu(false);
           	this.rootPanel(this.isMobile()
              ? new AonMobileParent()
              : new AonParent());
			  
          }
        });
      })
      .catch((e) => {
        console.log(e);
        loader.stop();
        let error = JSON.parse(e);
        let toast = this.getElement('aonLoginToast');
        toast.start(error);
      });
  }

  companySelection(company, onlyOne) {
    localStorage.setItem('company', JSON.stringify(company));
    localStorage.setItem("aon_domain_id", company.id);
    localStorage.setItem("aon_domain_name", company.domain);
    localStorage.setItem("aon_domain_document", company.document);
    localStorage.setItem("onlyOne", onlyOne);

    let home = this.getElement('aonHome');
    home.showMenu(true);

    let aonHeader = this.getElement(home.AON_HEADER);
    aonHeader.showCompanyOption(company, onlyOne);

    if(!this.isMobile()){
      let aonMenu = this.getElement('aonMenu');
      aonMenu.clear();
      aonMenu.init().then(() => aonMenu.open());
    } else aonHeader.companyIn(onlyOne);

    getUser().then(user => {
      localStorage.setItem('aon_domain_login', user.login);
      if(UA.isMobile()){
        this.rootPanel(new AonMobileHome());
      } else {
        this.rootPanel(new AonDesktop());
        let portal = LS.isLeftMenu();
        LS.setPortalChecked(portal);
      }

    });
  }

  onEnter(event) {
    if (event.keyCode === 13) {
      event.preventDefault();
      let aonLoginSignin = this.getElement("aonLoginSignin");
      if(aonLoginSignin) this.getElement("aonLoginSignin").click();
      else this.getElement("aonMagicLinkSignin").click();
    }
  }
  
}
if(!window.customElements.get(TAG.AON_NEW_LOGIN)){
	window.customElements.define(TAG.AON_NEW_LOGIN, AonNewLogin);
}
