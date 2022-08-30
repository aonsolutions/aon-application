import { AonElement } from "../../components/AonElement.js";
import { getAuth, getCompany, getDomainUserRoles, getRegistry, saveServiceAccount } from "../../services/service.js";
import {DomainUserRoles} from '../../models/DomainUserRoles.js';
import "../../components/aon-card.js";
import "../../components/aon-input.js";
import "../marketplace/aon-marketplace.js";
import "../user/aon-user-list.js";
import "../company/aon-company-list.js";

import { AonCompanyList } from "../company/aon-company-list.js";
import { AonCompany } from "../company/aon-company.js";
import { AonApplication } from '../../components/aon-application.js';
import { CONSTANT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js';
import { AonUserList } from "../user/aon-user-list.js";
import { AonMobileUserList } from "../user/aon-mobile-user-list.js";
import * as ACTION from '../actions.js';
import { CONFIGURATION, INVOICE, MESSENGER, AON_SALTRA, COMUNICA, API_SERVICE } from "../../services/app.js";
import { AonUser } from "../user/aon-user.js";
import { AonWorkgroup } from "./groups/aon-workgroup.js";
import { AonReg } from "../registry/aon-reg.js";
import * as LS from '../../services/localStorageService.js';
import { Registry } from "../../models/registry/Registry.js";
import { AonInvoiceConfiguration } from "../invoice/aon-invoice-configuration.js";
import { AonMessengerConfig } from "../messenger/aon-messenger-config.js";
import { AonBooking } from '../marketplace/aon-booking.js';
import { AonComunicaConfig } from "../laboral/aon-comunica-config.js";
import { AonServiceAccountList } from "../user/aon-service-account-list.js";
import { AonInput } from "../../components/aon-input.js";
import { AonDate } from "../../components/aon-date.js";
import { AonNewsList } from "../news/news/aon-news-list.js";

export class AonConfiguration extends AonElement {
  AON_CONFIGURATION;
  COMPANY;
  COMPANY_LIST;

  selected;
  company;
  dur;

  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
  }

  get user() {
    return this.getAttribute("user");
  }

  set user(user) {
    this.setAttribute("user", user);
  }

  get option() {
    return this.getAttribute(CONSTANT.OPTION);
  }

  set option(option) {
    this.setAttribute(CONSTANT.OPTION, option);
  }

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.createApplication(this.AON_CONFIGURATION, MSG.SETTING, new AonApplication());

    getDomainUserRoles({}).then(r => {
      this.dur = new DomainUserRoles(r);
      getCompany().then(company => {
        this.company = company;
        this.build();
      });

    }).catch(() => this.build());
  }

  initialize() {
    this.AON_CONFIGURATION = "aonConfiguration";
    this.COMPANY = this.AON_CONFIGURATION + "Company";
    this.COMPANY_LIST = this.AON_CONFIGURATION + "CompanyList";
  }

  build() {
    let aonConfiguration = this.getElement(this.AON_CONFIGURATION);

    if(this.isMobile()){
			aonConfiguration.addMobileSidenavHeader(CONFIGURATION);
		}

    let userOptions = [
      {
        name: MSG.MY_DATA,
        icon: "person",
        fn: () => this.buildPersonal(),
      }
    ];
 
    aonConfiguration.addSidenavOptions(
      MSG.USER.toUpperCase(),
      userOptions
    );

    if ( localStorage.getItem("aon_domain_id") ) {

      let companyOptions = [];

      if(!this.dur.isEmployee() && !this.isMobile()){
        companyOptions.push({
          name: MSG.GENERAL_INFORMATION,
          icon: MATERIAL_ICONS.BUSINESS,
          fn: () => this.buildGeneral(),
        });
      }

      if(this.dur.isAdmin()){
        companyOptions.push({
          name: MSG.USER_MANAGEMENT,
          icon: MATERIAL_ICONS.PEOPLE,
          fn: () => this.buildUser(),
        });
        if (!this.company.domain.parentId) {
          companyOptions.push({
            name: MSG.COMPANY_MANAGEMENT,
            icon: MATERIAL_ICONS.BUSINESS,
            fn: () => this.buildCompanyList(),
          });
        }

        companyOptions.push({
          name: MSG.GROUP_MANAGEMENT,
          icon: MATERIAL_ICONS.GROUPS,
          fn: () => this.buildGroups(),
        });

        if(this.dur.isApiService()){
          companyOptions.push({
            name: MSG.SERVICE_ACCOUNTS,
            icon: MATERIAL_ICONS.API,
            fn: () => this.buildServiceAccount(),
          });
        }

        if (!this.isMobile()) {
          companyOptions.push({
            name: MSG.HIRING,
            icon: MATERIAL_ICONS.STORE_MALL_DIRECTORY,
            fn: () => this.buildStore(),
          });
        }
      }
      aonConfiguration.addSidenavOptions(MSG.COMPANY.toUpperCase(), companyOptions);
    }

    if (localStorage.getItem("aon_domain_id")) {
      let appOptions = [];
      if (this.dur.isInvoice()) {
        appOptions.push({
          name: INVOICE.title,
          aonIcon: {
            icon: 'aon_app',
            color: INVOICE.color
          },
          fn: () => this.buildInvoiceConfiguration(),
        });
      }
      
      if(!this.dur.isEmployee()){
        appOptions.push({
          id: MESSENGER.title,
          name: MESSENGER.title,
          aonIcon: {
            icon: MESSENGER.icon,
            color: MESSENGER.color
          },
          fn: () => this.buildMessengerConfiguration(),
        });
      }

      if( this.dur.isSaltraManager() ) {
        appOptions.push({
          id: AON_SALTRA.title,
          name: AON_SALTRA.title,
          aonIcon: {
            icon: AON_SALTRA.icon,
            color: AON_SALTRA.color
          },
          fn: () => this.buildComunicaConfiguration(),
        });
      } else if(this.dur.isComunicaManager()){
        appOptions.push({
          id:  COMUNICA.title,
          name: COMUNICA.title,
          aonIcon: {
            icon: COMUNICA.icon,
            color: COMUNICA.color
          },
          fn: () => this.buildComunicaConfiguration(),
        });
      } 

      if(this.isBeta()){
        appOptions.push({
          id:  "notice",
          icon: "rss_feed",
          name: MSG.NOTIFICATIONS,
          fn: () => this.buildNews(),
        });
      }

      aonConfiguration.addSidenavOptions(MSG.APPLICATIONS.toUpperCase(), appOptions);
    }


    this.buildPersonal();
  }

  buildPersonal() {
    getAuth().then((user) => {
      let aonConfiguration = this.getElement(this.AON_CONFIGURATION);

      let toolbar = this.getElement(aonConfiguration.TOOLBAR);
		  toolbar.setAttribute('option', MSG.MY_DATA);
      
      aonConfiguration.removeToolbarOptions();

      let aonUser = new AonUser();
      aonUser.id = 'aonUserPersonal';
      aonUser.setShowPassword(true);
      aonUser.setShowToolbar(true);
      aonUser.setOnlyAuth(true);
      aonUser.setUser(user);
      aonUser.style.width = "100%";
  
      aonConfiguration.setContent(aonUser);	
    });
  }

  buildGeneral() {
    let data = {
			id: LS.getCompany().registry,
			additional_info: ['ADDRESSES', 'MEDIA', 'BANKS', 'PAYMETHOD', 'RECORD_DATA']
		};
    
    getRegistry(data).then(cp => {
      let aonRegistry = new AonReg();
			aonRegistry.id = this.getApplication().id + 'Registry';
      aonRegistry.setShowLogo(true);
			aonRegistry.setRegistry(cp);
			this.getApplication().setContent(aonRegistry);
    });
  }

  buildUser() {
    let aonConfiguration = this.getElement(this.AON_CONFIGURATION);
    aonConfiguration.removeToolbarOptions();

    if(this.isMobile()) {
			aonConfiguration.addFloatOption(ACTION.ADD, () => this.buildCreateUser(false));
		} else {
      aonConfiguration.addToolbarOption("UserShare", "share", () =>
        this.buildCreateUser(true)
      );
      aonConfiguration.addToolbarOption("UserAdd", "add", () =>
        this.buildCreateUser(false)
      );
    }
    
    let userList = this.isMobile() 
        ? new AonMobileUserList() 
        : new AonUserList();
    aonConfiguration.setContent(userList);
    
    // const btnSearch = aonConfiguration.addSearchOption();
    // btnSearch.addEventListener(EVENT.SEARCH, (event) => {
    //   userList.setValue(event.detail);
    // });
  }

  buildServiceAccount() {
    let aonConfiguration = this.getElement(this.AON_CONFIGURATION);
    aonConfiguration.removeToolbarOptions();

    if(this.isMobile()) {
			aonConfiguration.addFloatOption(ACTION.ADD, () => this.createServiceAccount());
		} else {
      aonConfiguration.addToolbarOption("ServiceAccountAdd", "add", () =>
        this.createServiceAccount()
      );
    }

    let serviceAccountList = this.isMobile() 
      ? new AonMobileUserList() 
      : new AonServiceAccountList();
    aonConfiguration.setContent(serviceAccountList);
  }

  createServiceAccount() {
    let aonConfiguration = this.getElement(this.AON_CONFIGURATION);
    let d = document.getElementById(aonConfiguration.DIALOG);

    let div = this.createElement(TAG.DIV);
    
    let input =  this.createAonElement(new AonInput(), CONSTANT.NAME, MSG.NAME);
    input.description = MSG.NAME;
    div.appendChild(input);

    // let expireDate =  this.createAonElement(new AonDate(), CONSTANT.DATE, MSG.EXPIRATION_DATE);
    // div.appendChild(expireDate);

    d.clear();
    if(!this.isMobile()) d.width = '400px';
    d.setTitle(MSG.CREATE_SERVICE_ACCOUNT);
    d.setContent(div);
    d.addAcceptAction(() => {
      saveServiceAccount({name:input.value}).then(() => {
        this.getElement(CONSTANT.AON_SERVICE_ACCOUNT_LIST).reload();
      });
    });
    d.open();
  }

	buildInvoiceConfiguration() {
    this.getApplication().setContent(new AonInvoiceConfiguration());
    // this.getApplication().setContent(new AonInvoicePrint());
	}
  
  buildMessengerConfiguration(){
    this.getApplication().setContent(new AonMessengerConfig());
  }

  buildComunicaConfiguration(){
    this.getApplication().setContent(new AonComunicaConfig());
  }

  buildNews(){
    this.getApplication().setContent(new AonNewsList());
  }

  buildCompanyList() {
    let aonConfiguration = this.getApplication();
    aonConfiguration.removeToolbarOptions();

    aonConfiguration.addToolbarOption("UserAdd", "add", () => this.buildCompany());

    let aonCompanyList = new AonCompanyList();
    aonCompanyList.id = this.COMPANY_LIST;
    aonCompanyList.filter = {parent: true};
    aonConfiguration.setContent(aonCompanyList);
  }

  buildCompany() {
    let aonCompany = new AonCompany();
    aonCompany.id = this.getApplication().id + 'Company';
    let reg = new Registry();
    reg.domain = undefined;
    aonCompany.options = [
      { title: MSG.GENERAL_DATA, fn: () => aonCompany.buildGeneralData()},
			{ title: MSG.BANK_DATA, fn: () => aonCompany.buildBankData()},
			{ title: MSG.REGISTRATION_DATA, fn: () => aonCompany.buildRegistralData()}
    ];
    aonCompany.setRegistry(reg);
    this.getApplication().setContent(aonCompany);
  }

	buildCreateUser(share) {
		let aonUser = new AonUser();
		aonUser.id = 'aonUserCreate';
		aonUser.setShowApps(true);
		aonUser.setShowToolbar(true);
		aonUser.style.width = "100%";
		if(share) aonUser.setAttribute('share', share);
		
		this.getApplication().setContent(aonUser);	
	}

  buildStore() {
    let aonConfiguration = this.getElement(this.AON_CONFIGURATION);
    aonConfiguration.removeToolbarOptions();
    let marketplace = new AonBooking();
    marketplace.id = 'aonMarketplace';
    aonConfiguration.setContent(marketplace);
  }

  buildGroups(){
    this.getApplication().setContent(new AonWorkgroup());
  }

  hiddenGeneral() {
    return false;
  }

  hiddenUser() {
    return false;
  }

  hiddenCompany() {
    return false;
  }

  hiddenStore() {
    return false;
  }

  getApplication() {
    return this.getElement(this.AON_CONFIGURATION);
  }
}

window.customElements.define("aon-configuration", AonConfiguration);
