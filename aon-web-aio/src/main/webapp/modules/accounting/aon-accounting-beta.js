import { AonElement } from "../../components/AonElement.js";
import { getAuth, getCompany, getDomainUserRoles, getRegistry, saveServiceAccount, getCompanyOne, getRelationShipCompany, getSiblingsOffice } from "../../services/service.js";
import {DomainUserRoles} from '../../models/DomainUserRoles.js';
import { AonApplication } from '../../components/aon-application.js';
import { AON_ICONS, CONSTANT, MATERIAL_ICONS, MSG, TAG, EVENT } from '../../environments/environments.js';
import * as JSF from '../aon-jsf-app.js';
import { AonRightPanel } from '../aon-right-panel.js';
import { AonIconButton } from "../../components/aon-icon-button.js";
import * as GWT from '../../gwt/gwt.js';

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
			fn: () => this.getApplication().setContent(new JSF.AonJsfAccountingParams()),
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
  
  buildObservations() {
	let rightSidenav = this.getApplication().getRightSidenav();
	this.clearElement(rightSidenav);
	
	let div = this.createElement(TAG.DIV);
	div.style = `
		display: flex;
		flex-direction: column;
		gap: 10px;
		height: 100%;
	`;
	div.id = "customerNotesId";

	if (rightSidenav.style.flexBasis === "0px" || rightSidenav.style.flexBasis.length == 0) {
		rightSidenav.appendChild(div);
		
		let loaderSpan = this.createElement(TAG.SPAN);
		loaderSpan.className = CONSTANT.SPIN;
		loaderSpan.style.display = 'flex';
		loaderSpan.style.height = '100%';
		loaderSpan.style.justifyContent = 'center';
		loaderSpan.style.alignItems = 'center';
		
		let aib = new AonIconButton();
		aib.id = 'spinLoader';
		aib.icon  = 'sync';
		aib.title = 'Cargando...';
		loaderSpan.appendChild(aib);
		
		div.appendChild(loaderSpan);
		
		GWT.iLoad(GWT.CUSTOMER_NOTES, div.id);
			
	}		
	this.getApplication().toogleRightSidenav();	
  }
  
  buildAccountingMenu() {
  	this.getApplication().setContent(new AonAccountingMenu());
  }

  getApplication() {
    return this.getElement(this.AON_ACCOUNTING_BETA);
  }
}

window.customElements.define("aon-accounting-beta", AonAccountingBeta);
