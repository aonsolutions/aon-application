import { AonElement } from "../../components/AonElement.js";
import { getCompany, getDomainUserRoles, getRegistryNotes } from "../../services/service.js";
import { DomainUserRoles } from '../../models/DomainUserRoles.js';
import { AonApplication } from '../../components/aon-application.js';
import { CONSTANT, MSG, TAG } from '../../environments/environments.js';
import * as JSF from '../aon-jsf-app.js';
import { AonIconButton } from "../../components/aon-icon-button.js";
import * as GWT from '../../gwt/gwt.js';
import * as LS from '../../services/localStorageService.js';
import * as ACTION from '../actions.js';

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
		this.buildToolbar();
		
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

		if (localStorage.getItem("aon_domain_id") && this.isBeta()) {
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
	
	buildToolbar(){
		this.getApplication().removeToolbarOptions();
		
		let company = LS.getCompany();
		
		window.addEventListener("message", (event) => {
			if ( (event.origin === "null" || event.origin === window.origin) 
				&& event.data?.type === "REGISTRY_NOTE_SAVED" ) {
				
				// This payload if needed info from GWT
				// const customerRegistry = event.data.payload;
				
				getRegistryNotes({registry: company.registry, source: 'ACCOUNTING'})
					.then(notes => {
						let countNotes = notes.filter(item => item.date).length;
						let countObservations = notes.filter(item => !item.date && item.comments && item.comments.trim() !== "").length;
						
						countNotes += countObservations;
						
						let notesTitle = `${countNotes > 0 ? (countNotes == 1 ? countNotes + ' Anotación' : countNotes + ' Anotaciones') : 'Sin Anotaciones'}`;
						
						let titleToolbar = this.getElement("aonAccountingBetaToolbarHeaderToolSectionTitle");
						titleToolbar.innerHTML = notesTitle;
						
						let existsNotes = notes.some(item => item.date);
						let notesIcon = this.getElement("aonAccountingBetaToolbarHeaderToolSectionNotesButtonIcon");
						if(existsNotes)
							notesIcon.style.color = 'green';
						else
							notesIcon.style.color = 'rgb(95, 99, 104)';
					});
			}
		});
		
		getRegistryNotes({registry: company.registry, source: 'ACCOUNTING'})
				.then(notes => {
					let countNotes = notes.filter(item => item.date).length;
					let countObservations = notes.filter(item => !item.date && item.comments && item.comments.trim() !== "").length;
					
					countNotes += countObservations;
					
					let notesTitle = `${countNotes > 0 ? (countNotes == 1 ? countNotes + ' Anotación' : countNotes + ' Anotaciones') : 'Sin Anotaciones'}`;
					
					this.getApplication().addTitleToolSection(notesTitle, false);
					
					this.getApplication().addToolbarOption2(ACTION.NOTES, () => this.buildObservations());
					
					const existsNotes = notes.some(item => item.date);
					
					if(existsNotes){
						let notesIcon = this.getElement("aonAccountingBetaToolbarHeaderToolSectionNotesButtonIcon");
						notesIcon.style.color = 'green';
					}
					
					let existsObservation = notes.some(item => !item.date && item.comments && item.comments.trim() !== "");
					if(existsObservation) this.buildObservations();
					
				});
	}

	buildObservations() {
		let rightSidenav = this.getApplication().getRightSidenav();
		this.clearElement(rightSidenav);
		
		let notesIcon = this.getElement("aonAccountingBetaToolbarHeaderToolSectionNotesButtonIcon");

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

			// Loader
			let loaderSpan = this.createElement(TAG.SPAN);
			loaderSpan.className = CONSTANT.SPIN;
			loaderSpan.style.display = 'flex';
			loaderSpan.style.height = '100%';
			loaderSpan.style.justifyContent = 'center';
			loaderSpan.style.alignItems = 'center';

			let aib = new AonIconButton();
			aib.id = 'spinLoader';
			aib.icon = 'sync';
			aib.title = 'Cargando...';
			loaderSpan.appendChild(aib);

			div.appendChild(loaderSpan);

			let company = LS.getCompany();

			localStorage.setItem("customer", company.registry);
			localStorage.setItem("notesSource", "ACCOUNTING");

			GWT.iLoad(GWT.CUSTOMER_NOTES, div.id);
		}
		
		notesIcon.classList.toggle("material-icons-selected");

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
