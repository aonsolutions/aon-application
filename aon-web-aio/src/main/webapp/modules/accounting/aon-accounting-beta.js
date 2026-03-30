import { AonElement } from "../../components/AonElement.js";
import { getCompany } from "../../services/service.js";
import { AonApplication } from '../../components/aon-application.js';
import { CONSTANT, MSG } from '../../environments/environments.js';
import * as JSF from '../aon-jsf-app.js';
import { AonNewUpload } from '../../components/aon-new-upload.js'
import { getCompanyActivities } from "../../services/companyService.js";
import { AonUploadToast } from "../../components/aon-upload-toast.js";
import { generateJobId } from "../invoice/InvoiceUtils.js";

import { AonAccountingMenu } from './aon-accounting-menu.js';

export class AonAccountingBeta extends AonElement {

	AON_ACCOUNTING_BETA;
	company;

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

		this.buildDur().then(() => {
			getCompany().then(company => {
				this.company = company;
				this.build();
			});
		});
	}

	initialize() {
		this.id = "aonAccountingBetaId";
		this.AON_ACCOUNTING_BETA = "aonAccountingBeta";
	}

	build() {
		this.buildToolbar();
		
		let aonAccountingBeta = this.getApplication();

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
				fn: () => this.getApplication().buildObservations('ACCOUNTING'),
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
		if (localStorage.getItem("aon_domain_id")&&(this.getDur().isInvoice() && (this.getDur().isOcr() || this.getDur().isInvofox()))) {
	       let utilitiesOptions = {
				id: "uploadInvoice",
				title: MSG.UTILITIES.toUpperCase(),
				name: MSG.UTILITIES.toUpperCase(),
			}

			let uploadInv = new AonNewUpload();
	       	uploadInv.id = "accountingUploadInvoice";
	       	uploadInv.setMessage(MSG.UPLOAD_INVOICE);
	       	uploadInv.setType("Invoice");
			this.getApplication().addSidenavWidget2(utilitiesOptions, uploadInv);	
		}
		
		this.buildAccountingMenu();
	}
	
	buildToolbar() {
		this.getApplication().removeToolbarOptions();
		this.getApplication().addCompanyNotes(this.company.id, 'ACCOUNTING');
	}

	buildAccountingMenu() {
		this.getApplication().setContent(new AonAccountingMenu());
	}
	
	getOptions(){
		let aonAccountingMenu = new AonAccountingMenu();
		aonAccountingMenu.setDur(this.getDur());
		return aonAccountingMenu.getOptions();
	}

	uploadInvoiceAccounting(input, files) {
		getCompanyActivities({}).then(activities => {
			let data = { uploaded: 0 };
			if(activities.length > 1) {
				activities.push({
					id: "all",
					description: "TODAS"
        		});
				let activity =  createSelect(this.ACTIVITY, MSG.ACTIVITY);
				activity.setAlias("id", "description");
				if(activities.length > 0) {
					activity.setOptions(activities);
					activity.value = activities[0].id;
				}
	
				let d = new AonDialog();
				let rootPanel = document.getElementById("rootPanel");
				rootPanel.appendChild(d);
				d.clear();
	
				d.setTitle(MSG.UPLOAD_INVOICE);
				d.setContent(activity);
				d.addAcceptAction(() => {
					data.activity = activity.getValueObject().id;
					let uploadToast = this.getElement('aonUploadToast');
					if (!uploadToast) {
						uploadToast = new AonUploadToast();
						this.appendChild(uploadToast);
					}
			
					uploadToast.setJobId(generateJobId());
					for (let file of files) {
						uploadToast.addFile("invoice", file, data);
					}
				});
				d.open();
			} else {
				if(activities.length > 0) {
					data.activity = activities[0].id;
				}

				let uploadToast = this.getElement('aonUploadToast');
				if (!uploadToast) {
					uploadToast = new AonUploadToast();
					this.appendChild(uploadToast);
				}
				uploadToast.setJobId(generateJobId());
				for (let file of files) {
					uploadToast.addFile("invoice", file, data);
				}
			}
		});
	}
}
if(!window.customElements.get("aon-accounting-beta")) {
	window.customElements.define("aon-accounting-beta", AonAccountingBeta);
}
