import { AonElement } from "../../components/AonElement.js";
import { getCompany, getDomainUserRoles, getRegistryNotes } from "../../services/service.js";
import { DomainUserRoles } from '../../models/DomainUserRoles.js';
import { AonApplication } from '../../components/aon-application.js';
import { CONSTANT, MSG, TAG } from '../../environments/environments.js';
import { AonIconButton } from "../../components/aon-icon-button.js";
import * as GWT from '../../gwt/gwt.js';
import * as JSF from '../aon-jsf-app.js';
import { AonFiscalMenu } from './aon-fiscal-menu.js';
import * as LS from '../../services/localStorageService.js';
import * as ACTION from '../actions.js';

export class AonFiscalBeta extends AonElement {
	AON_FISCAL_BETA;
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
		this.createApplication(this.AON_FISCAL_BETA, MSG.FISCAL, new AonApplication());


		getDomainUserRoles({}).then(r => {
			this.dur = new DomainUserRoles(r);
			getCompany().then(company => {
				this.company = company;
				this.build();
			});

		}).catch(() => this.build());
	}

	initialize() {
		this.AON_FISCAL_BETA = "aonFiscalBeta";
		this.ROOT_PANEL = "rootPanel";
	}

	build() {
		this.buildToolbar();
		
		let aonFiscalBeta = this.getElement(this.AON_FISCAL_BETA);

		if (localStorage.getItem("aon_domain_id")) {
			let configurationOptions = [];
			configurationOptions.push({
				id: "parameters",
				icon: "settings_applications",
				name: "Parametros",
				fn: () => this.getApplication().setContent(new JSF.AonJsfFiscalParams()),
			});

			configurationOptions.push({
				id: "observations",
				icon: "speaker_notes",
				name: "Observaciones",
				fn: () => this.buildObservations(),
			});

			aonFiscalBeta.addSidenavOptions(MSG.CONFIGURATION.toUpperCase(), configurationOptions);
		}

		if (localStorage.getItem("aon_domain_id") && this.isBeta()) {
			let menuOptions = [];

			menuOptions.push({
				id: "options panel",
				icon: "dashboard",
				name: "Panel Contabilidad",
				fn: () => this.buildFiscalMenu(),
			});

			aonFiscalBeta.addSidenavOptions(MSG.MENU.toUpperCase(), menuOptions);
		}

		this.buildFiscalMenu();
	}
	
	buildToolbar(){
		this.getApplication().removeToolbarOptions();
		
		let company = LS.getCompany();
		
		window.addEventListener("message", (event) => {
			if ( (event.origin === "null" || event.origin === window.origin) 
				&& event.data?.type === "REGISTRY_NOTE_SAVED" ) {
				
				// This payload if needed info from GWT
				// const customerRegistry = event.data.payload;
				
				getRegistryNotes({registry: company.registry, source: 'FISCAL'})
					.then(notes => {
						let countNotes = notes.filter(item => item.date).length;
						let countObservations = notes.filter(item => !item.date && item.comments && item.comments.trim() !== "").length;
						
						countNotes += countObservations;
						
						let notesTitle = `${countNotes > 0 ? (countNotes == 1 ? countNotes + ' Anotación' : countNotes + ' Anotaciones') : 'Sin Anotaciones'}`;
						
						let titleToolbar = this.getElement("aonFiscalBetaToolbarHeaderToolSectionTitle");
						titleToolbar.innerHTML = notesTitle;
						
						let existsNotes = notes.some(item => item.date);
						let notesIcon = this.getElement("aonFiscalBetaToolbarHeaderToolSectionNotesButtonIcon");
						if(existsNotes)
							notesIcon.style.color = 'green';
						else
							notesIcon.style.color = 'rgb(95, 99, 104)';
					});
			}
		});
		
		getRegistryNotes({registry: company.registry, source: 'FISCAL'})
				.then(notes => {
					let countNotes = notes.filter(item => item.date).length;
					let countObservations = notes.filter(item => !item.date && item.comments && item.comments.trim() !== "").length;
					
					countNotes += countObservations;
					
					let notesTitle = `${countNotes > 0 ? (countNotes == 1 ? countNotes + ' Anotación' : countNotes + ' Anotaciones') : 'Sin Anotaciones'}`;
					
					this.getApplication().addTitleToolSection(notesTitle, false);
					
					this.getApplication().addToolbarOption2(ACTION.NOTES, () => this.buildObservations());
					
					let existsNotes = notes.some(item => item.date);
					
					if(existsNotes){
						let notesIcon = this.getElement("aonFiscalBetaToolbarHeaderToolSectionNotesButtonIcon");
						notesIcon.style.color = 'green';
					}
					
					let existsObservation = notes.some(item => !item.date && item.comments && item.comments.trim() !== "");
					if(existsObservation) this.buildObservations();
					
				});
	}

	buildObservations() {
		let rightSidenav = this.getApplication().getRightSidenav();
		this.clearElement(rightSidenav);

		let notesIcon = this.getElement("aonFiscalBetaToolbarHeaderToolSectionNotesButtonIcon");

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
			localStorage.setItem("notesSource", "FISCAL");

			GWT.iLoad(GWT.CUSTOMER_NOTES, div.id);
		}
		
		notesIcon.classList.toggle("material-icons-selected");

		this.getApplication().toogleRightSidenav();
	}

	buildFiscalMenu() {
		this.getApplication().setContent(new AonFiscalMenu());
	}

	getApplication() {
		return this.getElement(this.AON_FISCAL_BETA);
	}

	getOptions(){
		let aonFiscalMenu = new AonFiscalMenu();
		aonFiscalMenu.setDur(this.getDur());
		return aonFiscalMenu.getOptions();
	}

}

window.customElements.define("aon-fiscal-beta", AonFiscalBeta);
