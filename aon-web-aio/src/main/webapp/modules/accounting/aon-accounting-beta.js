import { AonElement } from "../../components/AonElement.js";
import { getAuth, getCompany, getDomainUserRoles, getRegistry, saveServiceAccount, getCompanyOne, getRelationShipCompany, getSiblingsOffice } from "../../services/service.js";
import {DomainUserRoles} from '../../models/DomainUserRoles.js';
import { AonApplication } from '../../components/aon-application.js';
import { AON_ICONS, CONSTANT, MATERIAL_ICONS, MSG, TAG, EVENT } from '../../environments/environments.js';

import { AonAccountingMenu } from './aon-accounting-menu.js';


export class AonAccountingBeta extends AonElement {
  AON_ACCOUNTING_BETA;
  ROOT_PANEL;
  dur;


  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
  }

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.createApplication(this.AON_ACCOUNTING_BETA, MSG.ACCOUNTING, new AonApplication());

	
    getDomainUserRoles({}).then(r => {
      this.dur = new DomainUserRoles(r);
      getCompany().then(company => {
        this.company = company;
        this.build();
      });

    }).catch(() => this.build());
  }

  initialize() {
    this.AON_ACCOUNTING_BETA = "aonAccountingBeta";
	this.ROOT_PANEL = "rootPanel";
  }

  build() {
    let aonAccountingBeta = this.getElement(this.AON_ACCOUNTING_BETA);

    if (localStorage.getItem("aon_domain_id")) {
      let configurationOptions = [];
      configurationOptions.push({
			id: "parameters",
			icon: "settings_applications",
			name: "Parametros",
			fn: () => this.buildParameters(),
	  });
	  
	  configurationOptions.push({
	  		id: "observations",
	  		icon: "speaker_notes",
			name: "Observaciones",
			fn: () => this.buildObservations(),
	  });

      aonAccountingBeta.addSidenavOptions(MSG.CONFIGURATION.toUpperCase(), configurationOptions);
    }
	
	if ( localStorage.getItem("aon_domain_id") && this.isBeta()) {
		let menuOptions = [];
		
		menuOptions.push({
			id: "options panel",
			icon: "dashboard",
			name: "Panel Contabilidad",
			fn: () => this.buildAccountingMenu(),
		});
		
		aonAccountingBeta.addSidenavOptions(MSG.MENU.toUpperCase(), menuOptions);
	}

    this.buildAccountingMenu();
  }

  buildParameters() {
	alert("En construcción");
  }
  
  buildObservations() {
	alert("En construcción");
  }
  
  buildAccountingMenu() {
  	this.getApplication().setContent(new AonAccountingMenu());
  }

  getApplication() {
    return this.getElement(this.AON_ACCOUNTING_BETA);
  }
}

window.customElements.define("aon-accounting-beta", AonAccountingBeta);
