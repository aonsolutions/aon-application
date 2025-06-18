import { AonElement } from "../../components/AonElement.js";
import { getAuth, getCompany, getDomainUserRoles, getRegistry, saveServiceAccount, getCompanyOne, getRelationShipCompany, getSiblingsOffice } from "../../services/service.js";
import {DomainUserRoles} from '../../models/DomainUserRoles.js';
import { AonApplication } from '../../components/aon-application.js';
import { AON_ICONS, CONSTANT, MATERIAL_ICONS, MSG, TAG, EVENT } from '../../environments/environments.js';

import { AonPayrollMenu } from './aon-payroll-menu.js';
import { AonRightPanel } from '../aon-right-panel.js';


export class AonPayrollBeta extends AonElement {
  AON_PAYROLL_BETA;
  ROOT_PANEL;
  dur;
  righPanel;


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
    this.createApplication(this.AON_PAYROLL_BETA, MSG.PAYROLL, new AonApplication());

	
    getDomainUserRoles({}).then(r => {
      this.dur = new DomainUserRoles(r);
      getCompany().then(company => {
        this.company = company;
        this.build();
      });

    }).catch(() => this.build());
  }

  initialize() {
    this.AON_PAYROLL_BETA = "aonPayrollBeta";
	this.ROOT_PANEL = "rootPanel";
  }

  build() {
    let aonPayrollBeta = this.getElement(this.AON_PAYROLL_BETA);

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

      aonPayrollBeta.addSidenavOptions(MSG.CONFIGURATION.toUpperCase(), configurationOptions);
    }
	
	if ( localStorage.getItem("aon_domain_id") && this.isBeta()) {
		let menuOptions = [];
		
		menuOptions.push({
			id: "options panel",
			icon: "dashboard",
			name: "Panel Contabilidad",
			fn: () => this.buildPayrollMenu(),
		});
		
		aonPayrollBeta.addSidenavOptions(MSG.MENU.toUpperCase(), menuOptions);
	}

    this.buildPayrollMenu();
  }

  buildParameters() {
	alert("En construcción");
  }
  
  buildObservations() {
	let aonPayrollBeta = this.getElement(this.AON_PAYROLL_BETA);

  	// Si ya existe el panel, lo eliminamos y restauramos layout
  	let existing = aonPayrollBeta.querySelector("aon-right-panel");
  	if (existing) {
  		existing.remove();
  		aonPayrollBeta.style.gridTemplateColumns = "";
  		return;
  	}

	this.righPanel = new AonRightPanel();
  	this.righPanel.id = "observationsPanel";
	this.righPanel.style.marginLeft = "10px";

  	aonPayrollBeta.appendChild(this.righPanel);
  	aonPayrollBeta.style.display = "grid";
  	aonPayrollBeta.style.gridTemplateColumns = "1fr 300px";
  	aonPayrollBeta.style.transition = "grid-template-columns 0.3s ease";

  	this.righPanel.setTitle("Observaciones");
  	this.righPanel.open("100%", "0px", "0 0 12px rgba(0, 0, 0, 0.2)");

  	this.righPanel.addEventListener("close", () => {
  		aonPayrollBeta.style.gridTemplateColumns = "";
  	});
  }

  
  buildPayrollMenu() {
  	this.getApplication().setContent(new AonPayrollMenu());
	if (this.rightPanel && this.rightPanel.isOpen()) {
	  this.rightPanel.close();
	  this.getApplication().style.gridTemplateColumns = "";
	}
  }

  getApplication() {
    return this.getElement(this.AON_PAYROLL_BETA);
  }
}

window.customElements.define("aon-payroll-beta", AonPayrollBeta);
