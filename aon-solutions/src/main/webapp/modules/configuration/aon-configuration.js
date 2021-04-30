import { AonElement } from "../../components/AonElement.js";
import { getAuth, getDomainUserRoles, getCompanyOne, getCompanyMedia } from "../../services/service.js";
import {DomainUserRoles} from '../../models/DomainUserRoles.js';
// import { AonNotificationManual } from "./aon-notification-manual.js";
import "../../components/aon-card.js";
import "../../components/aon-input.js";
import "../../components/aon-address.js";
import "../marketplace/aon-marketplace.js";
import "../user/aon-user-list.js";
import "../user/aon-user.js";
import "../company/aon-company-list.js";

import { AonCompanyList } from "../company/aon-company-list.js";
import { AonCompany } from "../company/aon-company.js";
import { AonApplication } from '../../components/aon-application.js';

import { CONSTANT, MSG } from '../../environments/environments.js';

export class AonConfiguration extends AonElement {
  AON_CONFIGURATION;
  COMPANY;
  COMPANY_LIST;
  selected;

  dur;

  static get observedAttributes() {
    return ["company", "user"];
  }

  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
  }

  get company() {
    return this.getAttribute("company");
  }

  set company(company) {
    this.setAttribute("company", company);
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

  attributeChangedCallback(name, oldValue, newValue) {
    // if ("company" === name || "user" === name) {
    // }
  }

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.createApplication(this.AON_CONFIGURATION, MSG.SETTING, new AonApplication());

    getDomainUserRoles({}).then(r => {
      this.dur = new DomainUserRoles(r);
      this.build();
    }).catch(() => this.build());
  }

  initialize() {
    this.AON_CONFIGURATION = "aonConfiguration";
    this.COMPANY = this.AON_CONFIGURATION + "Company";
    this.COMPANY_LIST = this.AON_CONFIGURATION + "CompanyList";
  }

  build() {
    let aonConfiguration = this.getElement(this.AON_CONFIGURATION);

    let userOptions = [
      {
        name: MSG.MY_DATA,
        icon: "person",
        fn: () => this.buildPersonal(),
      },
    ];
    aonConfiguration.addSidenavOptions(
      MSG.MY_USER.toUpperCase(),
      userOptions
    );

    if (
      localStorage.getItem("aon_domain_id") &&
      localStorage.getItem("company") && this.dur.isAdmin()
    ) {
      let company = JSON.parse(localStorage.getItem("company"));
      let companyOptions = [];
      companyOptions.push({
        name: "Información General",
        icon: "business",
        fn: () => this.buildGeneral(),
      });
      companyOptions.push({
        name: MSG.USER_MANAGEMENT,
        icon: "people",
        fn: () => this.buildUser(),
      });
      if (!company.parentId) {
        companyOptions.push({
          name: "Gestión de Empresas",
          icon: "business",
          fn: () => this.buildCompanyList(),
        });
      }
      if (!this.isMobile()) {
        companyOptions.push({
          name: "Contratación",
          icon: "store_mall_directory",
          fn: () => this.buildStore(),
        });
      }
      // companyOptions.push({
      //   name: "Notificación manual",
      //   icon: "notifications",
      //   fn: () => this.buildNotification(),
      // });

      aonConfiguration.addSidenavOptions("EMPRESA", companyOptions);
    }

    this.buildPersonal();
  }

  buildPersonal() {
    getAuth().then((user) => {
      let aonConfiguration = this.getElement(this.AON_CONFIGURATION);

      let toolbar = this.getElement(aonConfiguration.TOOLBAR);
		  toolbar.setAttribute('option', MSG.MY_DATA);
      
      aonConfiguration.removeToolbarOptions();
      aonConfiguration.setContentHTML(
        '<aon-user id="aonUserPersonal" showPassword="true" onlyAuth="true" autosave="true"><aon-user>'
      );
      let aonUser = this.getElement("aonUserPersonal");
      aonUser.style.width = "100%";
      aonUser.setAttribute("user", JSON.stringify(user));
    });
  }

  buildGeneral() {
    let aonConfiguration = this.getElement(this.AON_CONFIGURATION);
    aonConfiguration.removeToolbarOptions();
    aonConfiguration.setContentHTML(`
			<div style="display:flex;">
				<aon-card id="aonConfigurationGeneralCard" style="width:50%;" flex="true" title="Información General"></aon-card>
				<aon-card id="aonConfigurationGeneral2Card" style="width:50%;" flex="true" title="Información Adicional"></aon-card>
			</div>
		`);
    getCompanyOne().then(cp => {
      let card = this.getElement("aonConfigurationGeneralCard");
      card.setContentHTML(`
        <form action="#" class="aon-margin-0">
          <aon-input class="aonWidth25" id="aonConfigurationGeneralNif" description="NIF" value="${
            cp.document
          }"></aon-input>
          <aon-input class="aonWidth75" id="aonConfigurationGeneralName" description="Razón Social" value="${
            cp.name
          }"></aon-input>
        </form>
        <form action="#" class="aon-margin-0">
          <aon-address class="aon-width-100" id="aonConfigurationGeneralAddress" title="Dirección" ></aon-address>
        </form>
      `);

      let address = document.getElementById('aonConfigurationGeneralAddress');
  		address.buildAddressValue(cp.address);
      getCompanyMedia().then(m => {
        let card2 = this.getElement("aonConfigurationGeneral2Card");
        card2.setContentHTML(`
    			<form action="#" class="aon-margin-0">
    				<aon-input class="aonWidth50" id="aonConfigurationGeneral2Phone" description="Teléfono" value="${m.fixed_phone}"></aon-input>
    				<aon-input class="aonWidth50" id="aonConfigurationGeneral2Fax" description="Fax" value="${m.fax}"></aon-input>
    			</form>

    			<form action="#" class="aon-margin-0">
    				<aon-input class="aon-width-100" id="aonConfigurationGeneral2Email" description="Email" value="${m.email}"></aon-input>
    			</form>

    			<form action="#" class="aon-margin-0">
    				<aon-input class="aon-width-100" id="aonConfigurationGeneral2Web" description="Web" value="${m.web}"></aon-input>
    			</form>

    			<!-- LOGO -->
    		`);
      });
    });
  }

  buildUser() {
    let aonConfiguration = this.getElement(this.AON_CONFIGURATION);
    aonConfiguration.removeToolbarOptions();

    aonConfiguration.addToolbarOption("UserShare", "share", () =>
      this.buildCreateUser(true)
    );
    aonConfiguration.addToolbarOption("UserAdd", "add", () =>
      this.buildCreateUser(false)
    );

    aonConfiguration.setContentHTML("<aon-user-list> </aon-user-list>");
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

  buildCompany(company) {
    let aonCompany = new AonCompany();
    aonCompany.id = this.getApplication().id + 'Company';
    aonCompany.company = company;
    this.getApplication().setContent(aonCompany);
  }

  // buildNotification() {
  //   let aonConfiguration = this.getElement(this.AON_CONFIGURATION);
  //   aonConfiguration.removeToolbarOptions();
  //   aonConfiguration.setContent(new AonNotificationManual());
  // }

  buildCreateUser(share) {
    let content =  this.getApplication().getContent();
    content.innerHTML =
      '<aon-user id="aonUserCreate" showApps="true" showToolbar="true" ><aon-user>';
    let aonUser = document.getElementById("aonUserCreate");
    aonUser.style.width = "100%";
    if (share) aonUser.setAttribute("share", share);
  }

  buildStore() {
    let aonConfiguration = this.getElement(this.AON_CONFIGURATION);
    aonConfiguration.removeToolbarOptions();

    aonConfiguration.setContentHTML(
      `<aon-marketplace id="aonMarketplace" > </aon-marketplace>`
    );

    if (this.getAttribute("company")) {
      let aonMarketplace = this.getElement("aonMarketplace");
      aonMarketplace.setAttribute("company", this.getAttribute("company"));
    }
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
