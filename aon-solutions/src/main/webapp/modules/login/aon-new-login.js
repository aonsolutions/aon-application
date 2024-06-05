import { AonElement } from "../../components/AonElement.js";
import { login, getManifest, rememberPassword, magicLink, getCompanies, getUser, mobileAction, MOBILE_ACTION } from "../../services/service.js";

import "../../components/aon-input.js";
import "../../components/aon-loader.js";
import "../../components/aon-dialog.js";
import "../../components/aon-toast.js";

import "../company/aon-mobile-desktop.js";

import { CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js'; 

import { webkitRequestMobile } from "../../services/request.js";
import { AonInput } from "../../components/aon-input.js";
import * as LS from '../../services/localStorageService.js';
import { AonLoader } from "../../components/aon-loader.js";
import { AonDialog } from "../../components/aon-dialog.js";
import { AonToast } from "../../components/aon-toast.js";
import { AonDialogMenu } from "../../components/aon-dialog-menu.js";
import { Language } from "../../models/Language.js";
import { AonEmail } from "../../components/aon-email.js";
import { AonNewInput } from "../../components/aon-new-input.js";
import { AonMobileParent } from "../company/aon-mobile-parent.js";
import { AonParent } from "aonparent";

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

  initialize() {
	
  }

  build() {
    this.className = CSS.AON_LOGIN;

    // Toolbar
    let toolbar = this.createElement(TAG.DIV);
    toolbar.className = CSS.AON_TOOLBAR2;
    this.appendChild(toolbar);

    let divLogoToolbar = this.createElement(TAG.DIV);
    divLogoToolbar.id = 'aonLoginLogoDiv';
    toolbar.appendChild(divLogoToolbar);

    let logoToolbar = this.createElement(TAG.IMG);
    logoToolbar.id = 'aonLoginLogoImgToolbar';
    logoToolbar.style.height = '25px';
    divLogoToolbar.appendChild(logoToolbar);

    if(!this.isMobile()) {
      let divLanguage = this.createElement(TAG.DIV);
      divLanguage.id = 'aonLoginLanguageDivToolbar';
      toolbar.appendChild(divLanguage);

      let spanLanguage = this.createElement(TAG.SPAN);
      spanLanguage.id = 'aonLoginLanguageSpanToolbar';
      spanLanguage.style.color = 'var(--gray-50)';
      spanLanguage.style.fontSize = '12px';
      spanLanguage.innerHTML = this.getLanguageText();
      spanLanguage.addEventListener(EVENT.MOUSEOVER, () => this.languageDialog());
      divLanguage.appendChild(spanLanguage);
    }

    // Content
    let divForm = this.createElement(TAG.DIV);
    divForm.id = "divForm";
    divForm.className = CSS.AON_LOGIN_FORM;
    this.appendChild(divForm);

    let divCompanyLogoForm = this.createElement(TAG.DIV);
    divCompanyLogoForm.id = "divCompanyLogoForm";
    divCompanyLogoForm.className = CSS.AON_LOGIN_FORM;
    divCompanyLogoForm.style.marginBottom = "2rem";
	divCompanyLogoForm.appendChild(this.companyLogo);
    divForm.appendChild(divCompanyLogoForm);

    let divTitleForm = this.createElement(TAG.DIV);
    divTitleForm.id = "divTitleForm";
    divTitleForm.className = CSS.AON_LOGIN_FORM;
    divTitleForm.style.marginBottom = "2rem";
    divForm.appendChild(divTitleForm);

    let h1 = this.createElement(TAG.H1);
    h1.className = CSS.AON_LOGIN_TITLE;
    h1.innerHTML = "Inicia Sesión";
    divTitleForm.appendChild(h1);

    let h2 = this.createElement(TAG.H1);
    h2.className = CSS.AON_LOGIN_SUB_TITLE;
    h2.innerHTML = MSG.ACCESS_TO_YOUR_AON_ACCOUNT;
    divTitleForm.appendChild(h2);

    // Form
    let divFormContent = this.createElement(TAG.DIV);
    divFormContent.classList.add(CSS.AON_LOGIN_FORM_CONTENT);
    divForm.appendChild(divFormContent);

    let aonLoader = new AonLoader();
    aonLoader.id = 'aonLoginLoader';
    aonLoader.style.display = 'none';
    divFormContent.appendChild(aonLoader);

    let userInput = this.createAonElement(this.userInput, 'aonLoginUser', MSG.USER);
    userInput.setRequired(true);
    userInput.addEventListener(EVENT.KEYUP, () => {
      this.getElement('aonLoginMagicLink').disabled = !userInput.value.includes('@'); 
    })
    divFormContent.appendChild(userInput);

    let passwordInput = this.createAonElement(new AonNewInput(), 'aonLoginPassword', MSG.PASSWORD);
    passwordInput.setRequired(true);
    passwordInput.type = 'password';
    divFormContent.appendChild(passwordInput);

    // Buttons
    let signIn = this.createElement(TAG.BUTTON);
    signIn.id = 'aonLoginSignin';
    signIn.className = CSS.AON_LOGIN_BUTTON;
    signIn.title = MSG.SIGN_IN;
    signIn.innerHTML = MSG.SIGN_IN.toUpperCase();
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
    magicLinkButton.title = MSG.SIGN_IN_WITHOUT_PASSWORD;
    magicLinkButton.innerHTML = MSG.SIGN_IN_WITHOUT_PASSWORD.toUpperCase();
    magicLinkButton.disabled = true;
    magicLinkButton.addEventListener(EVENT.CLICK, () => this.magicLink(userInput.value));
    divFormContent.appendChild(magicLinkButton);

    // Form Aon Version
    let divInfo = this.createElement(TAG.DIV);
    divInfo.style.color = '#666';
    divInfo.style.fontSize = '9px';
    divInfo.style.borderTop = '1px solid #ddd';
    divInfo.style.marginTop = '10px';
    divInfo.style.padding = '15px';
    divInfo.innerHTML = `
      <span>
        <a target="_blank" class="aonLink" href="http://www.aonsolutions.es">
          aonSolutions
        </a> ${MSG.REGISTERED_TRADEMARK_AON}
      </span>
      <div id="aonManifest"></div>`;
    divFormContent.appendChild(divInfo);

    let divMobiles = this.createElement(TAG.DIV);
    divMobiles.id = 'logosMobiles';
    divFormContent.appendChild(divMobiles);

    let dialog = new AonDialog();
    dialog.id = "aonDialogLogin";
    this.appendChild(dialog);

    let toast = new AonToast();
    toast.id = "aonLoginToast";
    this.appendChild(toast);
  }

  magicLink(value) {
    let loader = this.getElement("aonLoginLoader");
    loader.style.display = '';
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
  

  languageDialog() {
    let divLanguage = this.getElement('aonLoginLanguageDivToolbar');
    let spanLanguage = this.getElement('aonLoginLanguageSpanToolbar');
    const top  = spanLanguage.getBoundingClientRect().top;
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
      image: '../assets/img/aonIconCastellano.png',
      fn: () => LS.setLanguage(Language.SPANISH)
    }, {
      name: MSG.ENGLISH,
      image: '../assets/img/aonIconEnglish.png',
      fn: () => LS.setLanguage(Language.ENGLISH)
    }, {
      name: MSG.DEUTSCH,
      image: '../assets/img/aonIconDeutsch.png',
      fn: () => LS.setLanguage(Language.DEUTSCH)
    }, {
      name: MSG.BASQUE,
      image: '../assets/img/aonIconEuskera.png',
      fn: () => LS.setLanguage(Language.BASQUE)
    }, {
      name: MSG.CATALAN,
      image: '../assets/img/aonIconCatala.png',
      fn: () => LS.setLanguage(Language.CATALAN)
    }, {
      name: MSG.GALICIAN,
      image: '../assets/img/aonIconGalego.png',
      fn: () => LS.setLanguage(Language.GALICIAN)
    } ];

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
      } else return MSG.SPANISH;
  }

  connectedCallback() {
    this.initialize();
    this.build();
    
    this.buildLogo();
    if(!webkitRequestMobile() && this.isMobile()){ // si es app
      this.buildAppLogo();
    } else {
      this.getElement('logosMobiles').style.display = 'none';
    }

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
    
    if(!this.isMobile()) username.focus();
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
    let logoToolbar = this.getElement("aonLoginLogoImgToolbar");
    const hrefToolbar = window.location.href;
    let srcToolbar = "assets/aon-logo.svg";
    logoToolbar.src = srcToolbar;
    logoToolbar.addEventListener(EVENT.CLICK, ()=>{
      this.tag = this.tag + 1;
      if(this.tag >= 5){
        const BASE_URL_MOBILE = hrefToolbar.includes("aonsolutions.org") ? "https://aon.solutions/" : "https://aonsolutions.org";
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
    loader.style.display = '';
    loader.start();
    login(data)
      .then(() => {
        document.body.style.background = 'transparent';
        loader.stop();

        LS.removeDomain();
        this.getModule().buildHome();
        this.getModule().startLoading();
        // LS.setLanguage(Language.SPANISH);

        LS.setLeftMenu(true);
        LS.setTopMenu(true);
        getCompanies().then(companies => {
          this.getModule().stopLoading();
          
          //if(companies.length > 0){
          //  this.companySelection(companies[0], true);
          //} 
          
          if(companies.length === 1){
            this.companySelection(companies[0], true);
          } else {
            this.getElement("aonHome").showMenu(false);
           	this.rootPanel(this.isMobile()
              ? new AonMobileParent()
              : new AonParent());
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
if(!window.customElements.get(TAG.AON_NEW_LOGIN)){
	window.customElements.define(TAG.AON_NEW_LOGIN, AonNewLogin);
}
