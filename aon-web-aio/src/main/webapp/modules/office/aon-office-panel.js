import { AonElement } from "../../components/AonElement.js";
import { AonApplication } from "../../components/aon-application.js";
import { OfficeEnums } from "./OfficeEnums.js";
import { EVENT, MATERIAL_ICONS, MSG } from "../../environments/environments.js";
import {
	deleteActivityType,
	getActivitiesType,
	getProjectTypes,
	saveProject,
} from "../../services/projectService.js";
import { DocumentalSidenav } from "../documental/DocumentalEnums.js";
import { AonCustomer } from "../registry/customer/aon-customer.js";
import { AonCustomerList } from "../registry/customer/aon-customer-list.js";
import { AonTaskHolder } from "../registry/taskholder/aon-taskholder.js";
import { AonTaskHolderList } from "../registry/taskholder/aon-taskholder-list.js";
import { OfficeUtils } from "./OfficeUtils.js";
import { getTastHolders } from "../../services/taskHolderService.js";
import { getWorkgroups } from "../../services/workgroupService.js";
import { ProjectUtils } from "../project/ProjectUtils.js";
import { getCustomer, saveRelationShip } from "../../services/registryService.js";
import { BOOKING_PANEL, LINK_DOMAINS } from "./ConsoleOptions.js";
import { AonLinkDomains } from "../domains/aon-link-domains.js";

import * as GWT from "../../gwt/gwt.js";
import { AonTarget } from "../registry/target/aon-target.js";
import { AonWorkgroup } from "../configuration/groups/aon-workgroup.js";

import { AonMobileCustomerList } from "../registry/customer/aon-mobile-customer-list.js";

export class AonOfficePanel extends AonElement {

	CUSTOMER_LIST
	TASK_HOLDER_LIST
	WORKGROUP;
	LINK_DOMAINS;

	projectTypes;
	workgroups;
	taskHolders;
	activitiesType;

	_customerSelected;
	_customerSelectedAll;

	_filterCustomers;
	_filterTaskHolders;

	setCustomerSelected(customerSelected) {
		this._customerSelected = customerSelected;
	}

	getCustomerSelected() {
		return this._customerSelected.filter(
			(
				value,
				index // remove repeated customersSelected
			) => this._customerSelected.findIndex((m) => m.id === value.id) === index
		);
	}

	setCustomerSelectedAll(customerSelectedAll) {
		this._customerSelectedAll = customerSelectedAll;
	}

	getCustomerSelectedAll() {
		return this._customerSelectedAll;
	}

	addFilterCustomers(filter) {
		this._filterCustomers = { ...this._filterCustomers, ...filter };
	}

	setFilterCustomers(filter) {
		this._filterCustomers = filter;
	}

	getFilterCustomers() {
		return this._filterCustomers;
	}

	addFilterTaskHolders(filter) {
		this._filterTaskHolders = { ...this._filterTaskHolders, ...filter };
	}

	setFilterTaskHolders(filter) {
		this._filterTaskHolders = filter;
	}

	getFilterTaskHolders() {
		return this._filterTaskHolders;
	}

	constructor() {
		super();
	}

	connectedCallback() {
		this.initialize();
		this.build();
	}

	initialize() {
		this.id = this.id || OfficeEnums.OfficeViews.AON_OFFICE_PANEL;
		this.CUSTOMER_LIST = this.id + 'CustomerList';
		this.TASK_HOLDER_LIST = this.id + 'TaskHolderList';
		this.WORKGROUP = this.id + 'Workgroup';
		this.LINK_DOMAINS = this.id + 'LinkDomains';
		this.projectTypes = [];
		this.workgroups = [];
		this.taskHolders = [];
		this.setCustomerSelected([]);

		if (!this.getFilterCustomers()) {
			this.setFilterCustomers({
				page: 1,
				perPage: 50,
				status: ["ACTIVE", "BLOCKED"],
				target: false
			});
		}

		if (!this.getFilterTaskHolders()) {
			this.setFilterTaskHolders({
				page: 1,
				perPage: 50
			});
		}
	}

	build() {
		this.createApplication(this.id, MSG.OFFICE, new AonApplication());
		this.buildSidenav();

		const { OfficeOptions } = OfficeEnums;
		let customerSideNavOpt = this.getElement("aonOfficePanelSidenavsideNavcustomer");
		customerSideNavOpt.click();
		
		let aonContentBeta = this.getElementsByClassName("aonContentBeta");
		aonContentBeta.style.height = 'auto';
		
		let rootPanel = this.getElement("rootPanel");
	    rootPanel.style.height = 'auto';
	    rootPanel.style.marginTop = '4rem';
		
	}

	buildSidenav() {
		const application = this.getApplication();

		// TODO FUTURE
		// OPTIONS.getOptions(this.isBeta(), this.isSig()).forEach(option => this.getApplication().addSidenavOptions3(option));

		const { ServiceOptions } = OfficeEnums;

		const { OfficeViews, OfficeOptions } = OfficeEnums;

		let options = [];

		let customer = OfficeOptions.AON_CUSTOMER;
		customer.fn = () =>
			this.showView(OfficeViews.AON_CUSTOMER_LIST, undefined, {
				...this.getFilterCustomers(),
				page: 1,
			});
		options.push(customer);
		
		let customerTags = OfficeOptions.AON_CUSTOMER_STATUS;
		customerTags.fn = () => this.showView(OfficeOptions.AON_CUSTOMER_STATUS.id);
		options.push(customerTags);
		
		let customerPayrollActivity = OfficeOptions.AON_CUSTOMER_PAYROLL_ACTIVITY;
		customerPayrollActivity.fn = () => this.showView(OfficeOptions.AON_CUSTOMER_PAYROLL_ACTIVITY.id);
		options.push(customerPayrollActivity);

		let taskHolder = OfficeOptions.AON_TASK_HOLDER;
		/*
		taskHolder.fn = () => this.showView(OfficeViews.AON_TASK_HOLDER_LIST, undefined, {
			...this.getFilterTaskHolders(),
			page: 1,
		});
		*/
		taskHolder.fn = () => this.showView(OfficeViews.AON_TASK_HOLDER_LIST);
		options.push(taskHolder);

		let workgroups = OfficeOptions.AON_WORKGROUP_LIST
		workgroups.fn = () => this.showView(OfficeViews.AON_WORKGROUP_LIST);
		options.push(workgroups);

		let seller = OfficeOptions.AON_SELLER_LIST
		seller.fn = () => this.showView(OfficeOptions.AON_SELLER_LIST.id);
		options.push(seller);

		let sellerWorkload = OfficeOptions.AON_SELLER_WORKLOAD
		sellerWorkload.fn = () => this.showView(OfficeOptions.AON_SELLER_WORKLOAD.id);
		options.push(sellerWorkload);
		
		let scope = OfficeOptions.AON_SCOPE;
		scope.fn = () => this.showView(OfficeOptions.AON_SCOPE.id);
		options.push(scope);

		if (this.isSig()) {
			let consoleOptions = [];
			let linkDomain = LINK_DOMAINS;
			linkDomain.fn = () => this.showView(LINK_DOMAINS.id);
			consoleOptions.push(linkDomain);

			let bookingPanel = BOOKING_PANEL;
			bookingPanel.fn = () => this.showView(BOOKING_PANEL.id);
			consoleOptions.push(bookingPanel);

			let service = ServiceOptions.AON_SERVICE;
			service.fn = () => this.showView(ServiceOptions.AON_SERVICE.id);
			consoleOptions.push(service);

			application.addSidenavOptions(MSG.CONSOLE, consoleOptions);
		}

		application.addSidenavOptions(MSG.OFFICE, options);

		let bookingOptions = [];

		let targetEnterprise = ServiceOptions.AON_TARGET_ENTERPRISE;
		targetEnterprise.fn = () => this.showView(ServiceOptions.AON_TARGET_ENTERPRISE.id);
		bookingOptions.push(targetEnterprise);

		window.addEventListener("message", (event) => {
			if ( (event.origin === "null" || event.origin === window.origin) 
				&& event.data?.type === "CUSTOMER_ENTERPRISE_DONE" ) {
				const customerRegistry = event.data.payload;
				this.getCustomerCustom(customerRegistry)
					.then(customer => {
						this.showView(OfficeEnums.OfficeViews.AON_CUSTOMER, { customer });
					});
			}
		});

		let salesEnterprise = ServiceOptions.AON_SALES_ENTERPRISE;
		salesEnterprise.fn = () => this.showView(ServiceOptions.AON_SALES_ENTERPRISE.id);
		bookingOptions.push(salesEnterprise);

		application.addSidenavOptions(MSG.BOOKING, bookingOptions);

		let types = {
			id: "Types",
			name: "Tipos de expediente",
			options: [],
		};

		application.addSidenavOptions3(types, ({ target }) =>
			this.getApplication().buildOptionsMenu(target, [
				{
					name: "Añadir expediente",
					icon: MATERIAL_ICONS.OPEN_IN_NEW,
					fn: () => ProjectUtils.buildDialogProjectType(this),
				},
				{
					name: "Añadir actividad",
					icon: MATERIAL_ICONS.OPEN_IN_NEW,
					fn: () => ProjectUtils.buildDialogActivityType(this),
				},
			])
		);

		this.loadProjectType();
	}

	async getCustomerCustom(registryId) {
		if (registryId) {
			let data = {
				id: registryId,
				additional_info: ['ADDRESSES', 'MEDIA', 'BANKS', 'PAYMETHOD', 'RSEGMENT']
			};

			return getCustomer(data);
		}

		return null;
	}

	buildToolbarSearchOption(search) {
		const btnSearch = this.getApplication().addSearchOption();
		let searchFn = (event) => search(event.detail);
		btnSearch.addEventListener(EVENT.SEARCH_NEW, searchFn);
	}

	// LINK DOMAINS

	buildLinkDomainsToolbarOptions() {
		this.clearToolbar();
	}

	buildLinkDomains() {
		this.getApplication().setContent(new AonLinkDomains());
	}

	// CUSTOMER

	buildCustomerToolbarOptions() {
		this.clearToolbar();
		this.buildToolbarSearchOption((value) => this.buildCustomerList({ page: 1, perPage: 50, value }));
	}

	buildCustomerList(filter) {
		let customerList = this.getElement(this.CUSTOMER_LIST);
		if (customerList) customerList.setFilter(filter);
		else {
			customerList = this.isMobile()
				? new AonMobileCustomerList()
				: new AonCustomerList();
			customerList.id = this.CUSTOMER_LIST;
			customerList.filter = filter;
			this.getApplication().setContent(customerList);
		}
	}

	// TASK HOLDER

	buildTaskHolderToolbarOptions() {
		this.clearToolbar();
		this.buildToolbarSearchOption((value) => this.buildTaskHolderList({ page: 1, perPage: 50, value }));
	}

	buildTaskHolderList(filter) {
		let list = this.getElement(this.TASK_HOLDER_LIST);
		if (list) list.setFilter(filter);
		else {
			list = new AonTaskHolderList();
			list.id = this.CUSTOMER_LIST;
			list.filter = filter;
			this.getApplication().setContent(list);
		}
	}

	// WORKGROUP

	buildWorkgroup() {
		let workgroup = new AonWorkgroup();
		workgroup.id = this.WORKGROUP;
		this.getApplication().setContent(workgroup);
	}


	async loadProjectType() {
		const [types, activitiesType] = await Promise.all([
			getProjectTypes({}),
			getActivitiesType({}),
		]);

		this.projectTypes = (types || []).map((r) => ({
			...r,
			name: r.description,
			value: r.id,
		}));
		this.activitiesType = (activitiesType || []).map((r) => ({
			...r,
			name: r.description,
			value: r.id,
		}));

		this.loadActivitiesType();

		this.clearElementById(
			this.getApplication().SIDENAV + DocumentalSidenav.TYPES.id + "List"
		);

		this.projectTypes.forEach((item) => {
			let option = {
				name: item.description,
				icon: MATERIAL_ICONS.LABEL,
				actions: [
					{
						id: MATERIAL_ICONS.MORE_VERT,
						icon: MATERIAL_ICONS.MORE_VERT,
						action: ({ target }) =>
							this.getApplication().buildOptionsMenu(target, [
								{
									name: "Añadir actividad",
									icon: MATERIAL_ICONS.OPEN_IN_NEW,
									fn: () =>
										ProjectUtils.buildDialogActivityType(this, null, item.id),
								},
								{
									name: MSG.EDIT,
									icon: MATERIAL_ICONS.EDIT,
									fn: () => ProjectUtils.buildDialogProjectType(this, item),
								},
								{
									name: MSG.DELETE,
									icon: MATERIAL_ICONS.DELETE,
									fn: () => ProjectUtils.projectTypeDelete(this, item),
								},
							]),
					},
				],
			};

			const opt = this.activitiesType.filter((a) => a.projectType == item.id);
			if (opt.length) {
				option.options = opt.map((activity) => ({
					name: activity.description,
					icon: MATERIAL_ICONS.HDR_AUTO,
					actions: [
						{
							id: MATERIAL_ICONS.MORE_VERT + "2",
							icon: MATERIAL_ICONS.MORE_VERT,
							action: ({ target }) =>
								this.getApplication().buildOptionsMenu(target, [
									{
										name: MSG.EDIT,
										icon: MATERIAL_ICONS.EDIT,
										fn: () =>
											ProjectUtils.buildDialogActivityType(
												this,
												activity,
												activity.projectType
											),
									},
									{
										name: MSG.DELETE,
										icon: MATERIAL_ICONS.DELETE,
										fn: () => {
											this.getApplication().confirmDialog(
												MSG.DELETE,
												MSG.DELETE_CONFIRM,
												async () => {
													this.getApplication().startLoading();
													try {
														await deleteActivityType(activity);
														this.showToast({ message: MSG.DELETED_DATA });
														this.loadProjectType();
													} catch (error) {
														this.showToast(error);
													}
													this.getApplication().stopLoading();
												}
											);
										},
									},
								]),
						},
					],
				}));
			}

			this.getApplication().addSidenavOptionsListValue(
				DocumentalSidenav.TYPES,
				option
			);
		});
	}

	loadActivitiesType() {
		let sidenav = this.getElement(
			this.getApplication().SIDENAV + "ActivitiesAll"
		);
		if (sidenav) sidenav.remove();

		const opt = this.activitiesType.filter((a) => !a.projectType);
		if (opt.length) {
			let options = opt.map((activity) => ({
				name: activity.description,
				icon: MATERIAL_ICONS.SHARE,
				actions: [
					{
						id: MATERIAL_ICONS.MORE_VERT + "2",
						icon: MATERIAL_ICONS.MORE_VERT,
						action: ({ target }) =>
							this.getApplication().buildOptionsMenu(target, [
								{
									name: MSG.EDIT,
									icon: MATERIAL_ICONS.EDIT,
									fn: () =>
										ProjectUtils.buildDialogActivityType(
											this,
											activity,
											activity.projectType
										),
								},
								{
									name: MSG.DELETE,
									icon: MATERIAL_ICONS.DELETE,
									fn: () => {
										this.getApplication().confirmDialog(
											MSG.DELETE,
											MSG.DELETE_CONFIRM,
											async () => {
												this.getApplication().startLoading();
												try {
													await deleteActivityType(activity);
													this.showToast({ message: MSG.DELETED_DATA });
													this.loadProjectType();
												} catch (error) {
													this.showToast(error);
												}
												this.getApplication().stopLoading();
											}
										);
									},
								},
							]),
					},
				],
			}));

			this.getApplication().addSidenavOptions3(
				{ id: "ActivitiesAll", name: "Otras actividades", options },
				() => {
					ProjectUtils.buildDialogActivityType(this);
				}
			);
		}
	}

	addCustomerListSelectable(view) {
		const application = this.getApplication();
		view.selectable = true;
		view.addEventListener(EVENT.SELECT, ({ detail }) => {
			let {
				table: { selected, selectedAll },
			} = detail;

			this.setCustomerSelected(selected);
			this.setCustomerSelectedAll(selectedAll);

			if (selected.length > 0) {
				application.addToolbarOption2(
					OfficeEnums.OfficeSidenav.MORE_VERT,
					({ target }) => {
						application.buildOptionsMenu(target, [
							{
								name: "Asignar expediente",
								value: "assignedExpediente",
								icon: MATERIAL_ICONS.OPEN_IN_NEW,
								fn: () => {
									OfficeUtils.buildDialogExpediente(this);
								},
							},
							{
								name: "Asignar productos",
								value: "assignedProduct",
								icon: MATERIAL_ICONS.OPEN_IN_NEW,
								fn: () => {
									OfficeUtils.buildDialogProducts(this);
								},
							},
							{
								name: "Actualizar productos",
								value: "assignedProduct",
								icon: MATERIAL_ICONS.AUTORENEW,
								fn: () => {
									OfficeUtils.buildDialogProductsUpdate(this);
								},
							},
							{
								name: "Vincular empresa",
								value: "LINK",
								icon: MATERIAL_ICONS.LINK,
								fn: () => {
									this.onSaveRelationByCustomers(true);
								},
							},
							{
								name: "Desvincular Empresa",
								value: "UNLINK",
								icon: MATERIAL_ICONS.LINK_OFF,
								fn: () => {
									this.onSaveRelationByCustomers(false);
								},
							},
						]);
					}
				);
			} else {
				this.removeActionFolder();
			}
		});
	}

	removeActionFolder() {
		this.setCustomerSelected([]);
		this.setCustomerSelectedAll(false);
		this.getApplication().removeToolbarOption(
			OfficeEnums.OfficeSidenav.MORE_VERT
		);
	}

	async onSaveExpedientes(project) {
		let selected = this.getCustomerSelected();
		let projects = selected.map((registry) =>
			project.clone().setRegistry(registry)
		);

		if (projects.length) {
			await saveProject({ projects });

			this.showMessage();
		}
	}

	onSaveRelationByCustomers(add) {
		const aonView = this.getElement(OfficeEnums.OfficeViews.AON_CUSTOMER_LIST);

		if (aonView) {
			this.getApplication().startLoading();

			saveRelationShip({ add, customers: this.getCustomerSelected() })
				.then((resp) => {
					if (resp.length) {
						OfficeUtils.builDialogRelationship(this, resp);
					} else {
						this.showMessage();
					}

					const table = aonView.TABLE;
					if (table) {
						table.clearSelected();
						aonView.setFilter({ ...this.getFilterCustomers(), page: 1 });
						this.setCustomerSelected([]);
					}
				})
				.catch((err) => this.showError(err))
				.finally(() => {
					this.getApplication().stopLoading();
				});
		}
	}

	openDialogRelationship(data) {
		//DELETE
		OfficeUtils.builDialogRelationship(this, data);
	}

	async getProjectTypes() {
		return this.projectTypes;
	}

	async getWorkgroups() {
		if (this.workgroups.length == 0) {
			let wgs = await getWorkgroups().catch(() => []);
			this.workgroups = wgs.map((r) => ({
				...r,
				name: r.description,
				value: r.id,
			}));
		}
		return this.workgroups;
	}

	async getTaskHolders() {
		if (this.taskHolders.length == 0) {
			let ths = await getTastHolders().catch(() => []);
			this.taskHolders = ths.map((r) => ({ ...r, value: r.id }));
		}
		return this.taskHolders;
	}

	clearToolbar() {
		let aonOffice = this.getApplication();
		let toolbar = this.getElement(aonOffice.TOOLBAR);
		toolbar.removeButtons();
	}

	showView(view, data = undefined, filter = undefined) {
		const officeViews = OfficeEnums.OfficeViews;
		const { ServiceOptions, OfficeOptions } = OfficeEnums;

		const application = this.getApplication();
		application.startLoader();

		this.removeActionFolder();

		// Clean sidenav		
		let rightSidenav = this.getApplication().getRightSidenav();
		rightSidenav.style.flexBasis = "0px";
		this.clearElement(rightSidenav);
		
		// Clean isSig LS
		localStorage.removeItem("isSig");

		return new Promise(async (resolve) => {
			let aonView = undefined;
			switch (view) {
				case BOOKING_PANEL.id:
					GWT.iLoad(GWT.BOOKING_PANEL, this.getApplication().CONTENT);
					break;
				case ServiceOptions.AON_SERVICE.id:
					this.clearToolbar();
					application.closeSidenav();
					GWT.iLoad(GWT.PRODUCT_MODULE, this.getApplication().CONTENT);
					break;
				case ServiceOptions.AON_SALES_ENTERPRISE.id:
					this.clearToolbar();
					GWT.iLoad(GWT.SALES_ENTERPRISE_MODULE, this.getApplication().CONTENT);
					break;
				case ServiceOptions.AON_TARGET_ENTERPRISE.id:
					this.clearToolbar();
					GWT.iLoad(GWT.TARGET_ENTERPRISE_MODULE, this.getApplication().CONTENT);
					break;
				case LINK_DOMAINS.id:
					aonView = new AonLinkDomains();
					break;
				case OfficeOptions.AON_SELLER_LIST.id:
					this.clearToolbar();
					GWT.iLoad(GWT.SELLER_MODULE, this.getApplication().CONTENT);
					break;
				case OfficeOptions.AON_SELLER_WORKLOAD.id:
					this.clearToolbar();
					GWT.iLoad(GWT.SELLER_WORKLOAD_MODULE, this.getApplication().CONTENT);
					break;
				case OfficeOptions.AON_SCOPE.id:
					this.clearToolbar();
					GWT.iLoad(GWT.SCOPE_MODULE, this.getApplication().CONTENT);
					break;
				case OfficeOptions.AON_CUSTOMER_STATUS.id:
					this.clearToolbar();
					localStorage.setItem("tagType", "CUSTOMER_STATUS");
					GWT.iLoad(GWT.TAG_MODULE, this.getApplication().CONTENT);
					break;
				case OfficeOptions.AON_CUSTOMER_PAYROLL_ACTIVITY.id:
					this.clearToolbar();
					localStorage.setItem("isSig", this.isSig());
					GWT.iLoad(GWT.CUSTOMER_PAYROLL_ACTIVITY_MODULE, this.getApplication().CONTENT);
					break;
				case officeViews.AON_OFFICE_PANEL:
					aonView = new AonOfficePanel();
					break;
				case officeViews.AON_CUSTOMER:
					let searchPanel = this.getElement("aonOfficePanelToolbarHeaderToolSectionSearch");
					if(searchPanel) searchPanel.style.display = "none";

					if (this.getFilterCustomers().type == "false") {
						aonView = new AonTarget();
						aonView.back = () => {if(searchPanel) searchPanel.style.display = "block";

							this.showView(officeViews.AON_CUSTOMER_LIST, undefined, {
								...this.getFilterCustomers(),
								page: 1,
							}); // overwrite function
						};
					} else {
						aonView = new AonCustomer();
						aonView.setOffice(true);
						aonView.back = () => {
							if(searchPanel) searchPanel.style.display = "block";
							this.showView(officeViews.AON_CUSTOMER_LIST, undefined, {
								...this.getFilterCustomers(),
								page: 1,
							}); // overwrite function
						};
					}
					break;
				case officeViews.AON_CUSTOMER_LIST:
					aonView = new AonCustomerList(this);
					aonView.setOffice(true);
					this.addCustomerListSelectable(aonView);

					aonView.buildRegistry = (registry) => {
						// overwrite function
						application.startLoader();
						aonView
							.getCustomerCustom(registry)
							.then((customer) =>
								this.showView(officeViews.AON_CUSTOMER, { customer })
							)
							.catch((err) => this.showError(err))
							.finally(() => application.stopLoader());
					};
					break;
				case officeViews.AON_TASK_HOLDER:
					aonView = new AonTaskHolder();
					break;
				case officeViews.AON_TASK_HOLDER_LIST:
					this.clearToolbar();
					GWT.iLoad(GWT.TASK_HOLDER_MODULE, this.getApplication().CONTENT);
					break;
				case officeViews.AON_WORKGROUP_LIST:
					aonView = new AonWorkgroup();
					break;
			}
			if (aonView) {
				aonView.id = view;

				if (filter) {
					aonView.filter = filter;
					aonView.setFilter(filter);
				}

				if (data) {

					if (data.customer) {
						aonView.setCustomer(data.customer);
					} else {
						aonView.data = data;
					}
				}

				application.setContent(aonView);

				if (
					[officeViews.AON_CUSTOMER_LIST].includes(view) &&
					aonView.buildToolbar
				) {
					aonView.buildToolbar();
				}
			}

			application.stopLoader();

			resolve(aonView);
		});
	}
}
if (!window.customElements.get("aon-office-panel")) {
	window.customElements.define("aon-office-panel", AonOfficePanel);
}
