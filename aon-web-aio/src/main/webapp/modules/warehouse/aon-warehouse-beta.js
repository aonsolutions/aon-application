import { AonElement } from "../../components/AonElement.js";
import { getCompany, getRegistryNotes, getDomainUserRoles } from "../../services/service.js";
import { DomainUserRoles } from '../../models/DomainUserRoles.js';
import { AonApplication } from '../../components/aon-application.js';
import { CONSTANT, MSG, TAG } from '../../environments/environments.js';
import { AonIconButton } from "../../components/aon-icon-button.js";
import * as GWT from '../../gwt/gwt.js';
import * as LS from '../../services/localStorageService.js';
import * as ACTION from '../actions.js';
import { AonWarehouseMenu } from "./aon-warehouse-menu.js";

export class AonWarehouseBeta extends AonElement {
	AON_WAREHOUSE_BETA;
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
		this.createApplication(this.AON_WAREHOUSE_BETA, MSG.WAREHOUSE, new AonApplication());
		
		getDomainUserRoles({}).then(r => {
			this.dur = new DomainUserRoles(r);
			getCompany().then(company => {
				this.company = company;
				this.build();
			});

		}).catch(() => this.build());
	}

	initialize() {
		this.AON_WAREHOUSE_BETA = "aonWarehouseBeta";
		this.ROOT_PANEL = "rootPanel";
	}

	build() {
		this.buildToolbar();
		this.buildWarehouseMenu();
		this.getApplication().closeSidenav();
	}
	
	buildToolbar(){
		this.getApplication().removeToolbarOptions();
		
		let company = LS.getCompany();
		
		window.addEventListener("message", (event) => {
			if ( (event.origin === "null" || event.origin === window.origin) 
				&& event.data?.type === "REGISTRY_NOTE_SAVED" ) {
				
				// This payload if needed info from GWT
				// const customerRegistry = event.data.payload;
				
				getRegistryNotes({registry: company.registry, source: 'WAREHOUSE'})
					.then(notes => {
						let countNotes = notes.filter(item => item.date).length;
						let countObservations = notes.filter(item => !item.date && item.comments && item.comments.trim() !== "").length;
						
						countNotes += countObservations;
						
						let notesTitle = `${countNotes > 0 ? (countNotes == 1 ? countNotes + ' Anotación' : countNotes + ' Anotaciones') : 'Sin Anotaciones'}`;
						
						let titleToolbar = this.getElement("aonWarehouseBetaToolbarHeaderToolSectionTitle");
						titleToolbar.innerHTML = notesTitle;
						
						let existsNotes = notes.some(item => item.date);
						let notesIcon = this.getElement("aonWarehouseBetaToolbarHeaderToolSectionNotesButtonIcon");
						if(existsNotes)
							notesIcon.style.color = 'green';
						else
							notesIcon.style.color = 'rgb(95, 99, 104)';
					});
			}
		});
		
		getRegistryNotes({registry: company.registry, source: 'WAREHOUSE'})
				.then(notes => {
					let countNotes = notes.filter(item => item.date).length;
					let countObservations = notes.filter(item => !item.date && item.comments && item.comments.trim() !== "").length;
					
					countNotes += countObservations;
					
					let notesTitle = `${countNotes > 0 ? (countNotes == 1 ? countNotes + ' Anotación' : countNotes + ' Anotaciones') : 'Sin Anotaciones'}`;
					
					this.getApplication().addTitleToolSection(notesTitle, false);
					
					this.getApplication().addToolbarOption2(ACTION.NOTES, () => this.buildObservations());
					
					let existsNotes = notes.some(item => item.date);
					
					if(existsNotes){
						let notesIcon = this.getElement("aonWarehouseBetaToolbarHeaderToolSectionNotesButtonIcon");
						notesIcon.style.color = 'green';
					}
					
					let existsObservation = notes.some(item => !item.date && item.comments && item.comments.trim() !== "");
					if(existsObservation) this.buildObservations();
					
				});
	}

	buildObservations() {
		let rightSidenav = this.getApplication().getRightSidenav();
		this.clearElement(rightSidenav);

		let notesIcon = this.getElement("aonWarehouseBetaToolbarHeaderToolSectionNotesButtonIcon");

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
			localStorage.setItem("notesSource", "WAREHOUSE");

			GWT.iLoad(GWT.CUSTOMER_NOTES, div.id);
		}
		
		notesIcon.classList.toggle("material-icons-selected");

		this.getApplication().toogleRightSidenav();
	}


	buildWarehouseMenu() {
		this.getApplication().setContent(new AonWarehouseMenu());
		if (this.rightPanel && this.rightPanel.isOpen()) {
			this.rightPanel.close();
			this.getApplication().style.gridTemplateColumns = "";
		}
	}

	getApplication() {
		return this.getElement(this.AON_WAREHOUSE_BETA);
	}
}

window.customElements.define("aon-warehouse-beta", AonWarehouseBeta);
