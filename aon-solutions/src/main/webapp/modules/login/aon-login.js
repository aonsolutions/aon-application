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
import { AonDialogMenu } from "../../components/aon-dialog-menu.js";
import { Language } from "../../models/Language.js";
import * as COLORS from "../../environments/colors.js";
import { AonNewInput } from "../../components/aon-new-input.js";
import { AonEmail } from "../../components/aon-email.js";

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
      document.body.style.background = `linear-gradient(to right, ${this.getSecondaryColor()}, ${this.getPrimaryColor()})`;
    }

    let div = this.createElement(TAG.DIV);
    div.className = CSS.AON_FORM_CENTER;
    this.appendChild(div);

    let div2 = this.createElement(TAG.DIV);
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
    
    let divLanguage = this.createElement(TAG.DIV);
    divLanguage.id = 'aonLoginLanguageDiv';
    divLanguage.style.display = 'flex';
    divLanguage.style.justifyContent = 'right';
    divLanguage.style.marginBottom = '5px';
    div2.appendChild(divLanguage);

    let spanLanguage = this.createElement(TAG.SPAN);
    spanLanguage.id = 'aonLoginLanguageSpan';
    spanLanguage.style.color = 'lightgray';
    spanLanguage.innerHTML = this.getLanguageText();
    if(this.isMobile()) {
      spanLanguage.addEventListener(EVENT.CLICK, () => this.languageDialog());
    } else {
      spanLanguage.addEventListener(EVENT.MOUSEOVER, () => this.languageDialog());
    }

    divLanguage.appendChild(spanLanguage);
    

    let divLogo = this.createElement(TAG.DIV);
    divLogo.id = 'aonLoginLogoDiv';
    div2.appendChild(divLogo);

    let logo = this.createElement(TAG.IMG);
    logo.id = 'aonLoginLogoImg';
    logo.style.width = '200px';
    logo.style.marginLeft = '40px';
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

    let userInput = this.createAonElement(
      LS.isNewTheme() ? new AonEmail() : new AonInput()
      , 'aonLoginUser', MSG.USER);
    // userInput.setRequired(true);
    userInput.addEventListener(EVENT.KEYUP, () => {
      this.getElement('aonLoginMagicLink').setDisabled(!userInput.value.includes('@')); 
    })
    divContent.appendChild(userInput);

    let passwordInput = this.createAonElement(
      LS.isNewTheme() ? new AonNewInput() : new AonInput()
      , 'aonLoginPassword', MSG.PASSWORD);
    // passwordInput.setRequired(true);
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
    magicLinkButton.setIcon(MATERIAL_ICONS.AUTO_FIX_HIGH);
    magicLinkButton.setTitle(MSG.SIGN_IN_WITHOUT_PASSWORD);
    magicLinkButton.setDisabled(true);
    magicLinkButton.setColor(this.getSecondaryColor());
    magicLinkButton.addEventListener(EVENT.CLICK, () => this.magicLink(userInput.value));
    divButtons.appendChild(magicLinkButton);

    if(this.isBeta()){
      let certificateButton = new AonButton();
      certificateButton.id = 'aonLoginCertificate';
      certificateButton.style.width = '100%';
      certificateButton.setIcon(MATERIAL_ICONS.SECURITY);
      certificateButton.setTitle(MSG.SIGN_IN_WITH_CERTIFICATE);
      certificateButton.setColor("black");
      certificateButton.addEventListener(EVENT.CLICK, () =>  LS.setNewTheme(true));
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
          aonSolutions
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
  

  languageDialog() {
    let spanLanguage = this.getElement('aonLoginLanguageSpan');
    const top  = spanLanguage.getBoundingClientRect().top;
    const left = spanLanguage.getBoundingClientRect().left;
    let d = this.getElement('aonHeaderDialogHelpOption');
    if(!d) {
      d = new AonDialogMenu();
      d.id = 'aonHeaderDialogHelpOption';
      this.appendChild(d);
    }  
    if(!this.isMobile())
      d.getContent().addEventListener(EVENT.MOUSELEAVE, () => d.close());

    let options = [{
      name: MSG.SPANISH,
      title: MSG.SPANISH,
      image: '../assets/img/aonIconCastellano.png',
      backgroundColor: 'white',
      permission: true,
      language: true,
      fn: () => LS.setLanguage(Language.SPANISH)
    }, {
      name: MSG.ENGLISH,
      title: MSG.ENGLISH,
      image: '../assets/img/aonIconEnglish.png',
      backgroundColor: 'white',
      permission: true,
      language: true,
      fn: () => LS.setLanguage(Language.ENGLISH)
    }, {
      name: MSG.DEUTSCH,
      title: MSG.DEUTSCH,
      image: '../assets/img/aonIconDeutsch.png',
      backgroundColor: 'white',
      permission: true,
      language: true,
      fn: () => LS.setLanguage(Language.DEUTSCH)
    }, {
      name: MSG.BASQUE,
      title: MSG.BASQUE,
      image: '../assets/img/aonIconEuskera.png',
      backgroundColor: 'white',
      permission: true,
      language: true,
      fn: () => LS.setLanguage(Language.BASQUE)
    }, {
      name: MSG.CATALAN,
      title: MSG.CATALAN,
      image: '../assets/img/aonIconCatala.png',
      backgroundColor: 'white',
      permission: true,
      language: true,
      fn: () => LS.setLanguage(Language.CATALAN)
    }, {
      name: MSG.GALICIAN,
      title: MSG.GALICIAN,
      image: '../assets/img/aonIconGalego.png',
      backgroundColor: 'white',
      permission: true,
      language: true,
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
    let logo = this.getElement("aonLoginLogoImg");
    const href = window.location.href;
    let src = "assets/aon-logo.svg";
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
        this.getModule().startLoading();
        getCompanies().then(companies => {
          this.getModule().stopLoading();
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
if(!window.customElements.get(TAG.AON_LOGIN)){
	window.customElements.define(TAG.AON_LOGIN, AonLogin);
}
