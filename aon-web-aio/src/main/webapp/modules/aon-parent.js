import { AonElement } from '../components/AonElement.js';
import { closeSession, getCompanies, getUserNotice, getUser, getCompaniesBySchemas, getTimeControl, getContracts } from '../services/service.js';
import { CSS, EVENT, MATERIAL_ICONS, MSG, TAG, CONSTANT } from '../environments/environments.js';
import { AonApplication } from '../components/aon-application.js';
import * as LS from '../services/localStorageService.js';
import { AonDialogMenu } from '../components/aon-dialog-menu.js';
import { MenuApps, ClassicApps, Apps } from '../services/app.js';
import { AonSign } from "../modules/timecontrol/aon-sign.js";
import { AonDateUtils } from '../modules/utils/AonDateUtils.js';
import * as JSF from './aon-jsf-app.js';
import { AonIconButton } from '../components/aon-icon-button.js';
import * as ACTION from './actions.js';


export class AonParent extends AonElement {

	notice;
	filter;
	selected;
	companies;

	more;
	PARENT;
	APPS_DIV;
	ENTERPRISES;
	INBOX_INVOICES;
	PENDING_INVOICES;
	REJECTED_INVOICES;
	COMPANY_FILTER_TAB;
	COMPANY_TITLE_SPAN;
	TRAMIT_INVOICES;

	setFilter(filter) {
		this.filter = filter;
	}

	getFilter() {
		return this.filter;
	}

	constructor() {
		super();
		this.id = 'aonParent';
		this.PARENT = 'aonParent';
		this.APPS_DIV = "appsDiv";
		this.COMPANY_FILTER_TAB = "aonCompanyTabFilter";
		this.COMPANY_TITLE_SPAN = "aonCompanySpanTitle";
		this.ENTERPRISES = `${CONSTANT.ENTERPRISES.initCap()}All`;
		this.INBOX_INVOICES = `${CONSTANT.INVOICES.initCap()}Inbox`;
		this.PENDING_INVOICES = `${CONSTANT.INVOICES.initCap()}Pending`;
		this.REJECTED_INVOICES = `${CONSTANT.INVOICES.initCap()}Rejected`;
		this.TRAMIT_INVOICES = `${CONSTANT.INVOICES.initCap()}Tramit`;
		this.filter = { id: 'active', active: true, name: MSG.ACTIVES, };
	}

	connectedCallback() {
		this.init({ id: 'active', active: true, domainActive: true });

		let newFixedButton = this.getElement('newFixedButton');
		if (newFixedButton)
			newFixedButton.classList.add('hidden');

	}

	init(filter) {
		//TODO: aonParent.startLoader();

		let aonApplication = new AonApplication();
		aonApplication.setAttribute("sidenav_width", "300px");
		this.createApplication(this.PARENT, "", aonApplication);

		this.build();
		this.buildSidenav();
		this.select(filter, companies => {
			this.decorateTabs(companies);
			this.getApplication().updateSidenavCount(this.ENTERPRISES, companies?.length || 0);
			this.getApplication().updateSidenavTitle(CONSTANT.ENTERPRISES, `${MSG.ENTERPRISES}`);
		});

	}


	select(filter, callback) {

		this.clearSelectedTab(this.filter);
		//TODO: aonParent.startLoader();
		let limit = 100;
		return new Promise((resolve, reject) => {

			getCompanies({ limit }).then(companies => {
				let loc = companies.filter(r => this.isLocationCompany(r));
				let cps = companies.filter(r => r.id == LS.getDomainId());
				if (loc.length == 1 && !loc[0].parent) {
					this.companySelection(loc[0], true);
				} else if (cps.length == 1 && !cps[0].parent) {
					this.companySelection(cps[0], companies.length == 1);
				} else if (LS.getCompany() && !LS.getCompany().domainManagement) {
					this.companySelection(LS.getCompany(), companies.length == 1);
				} else if (companies.length === 1 && !companies[0].domainManagement) {
					this.companySelection(companies[0], true);
				} else {
					if (LS.getCompany()) {
						this.getAonHeader().showCompanyOption(LS.getCompany(), false);
					}

					let aonMenu = this.getElement('aonMenu');
					aonMenu.init()
						.then(() => {
							LS.setDomainLogin(aonMenu.getDur().getUser().login);
							LS.setDomainId(aonMenu.getDur().getDomain().getId());
							LS.setDomainName(aonMenu.getDur().getDomain().getName());
							this.getAonHeader().setCompanyName(aonMenu.getDur().getDomain().getDescription());
							aonMenu.open();
							aonMenu.closeEmptyApps();

						})
						.catch((err) => {
							reject(err);
						});

					this.selectTab(filter);
					//TODO: aonParent.stopLoader();
					this.page = 1;
					this.cleanCompanies();

					let filteredCompanies = this.filterCompanies(companies, filter);

					this.buildCompanies(filteredCompanies.slice(0, 30), filter);

					if (companies.length == limit) {
						getCompanies().then(companies => {
							this.cleanCompanies();
							let filteredCompanies = this.filterCompanies(companies, filter);
							this.buildCompanies(filteredCompanies.slice(0, 30), filter);
							resolve(companies);
							callback?.(companies);
						});
					}
					else {
						resolve(companies);
						callback?.(companies);
					}
				}
			}, () => closeSession());

			if (filter) {
				this.setFilter(filter);
			}
		});
	}

	decorateTabs(companies, filter = {}) {
		let companyFilterTabs = [{
			id: 'active',
			active: true,
			name: MSG.ACTIVES,
		}, {
			id: 'office',
			despacho: true,
			name: MSG.OFFICE
		}, {
			id: 'consultancy',
			entorno: true,
			name: MSG.ENVIRONMENT
		}, {
			id: 'shared',
			shared: true,
			name: MSG.SHARED
		}, {
			id: 'inactive',
			inactive: true,
			name: MSG.INACTIVES
		}
		];
		let companyTitleSpan = this.getElement(this.COMPANY_TITLE_SPAN);
		companyTitleSpan.classList.remove(CSS.AON_COMPANY_FILTER_LOADING);
		let tabCompanies = {};
		for (let companyFilterTab of companyFilterTabs) {
			let companyFilterTabCompanies = companies.filter(f => !this.isLocationCompany(f) && this.companyFilter(f, { ...companyFilterTab, ...filter }));
			let companyFilterTabSpan = this.getElement(`${this.COMPANY_FILTER_TAB}-${companyFilterTab.id}`);
			if (companyFilterTabCompanies.length === 0) {
				companyFilterTabSpan.parentElement.classList.add(CSS.AON_COMPANY_FILTER_EMPTY);
			}
			else {
				tabCompanies[companyFilterTab.id] = companyFilterTab;
				companyFilterTabSpan.parentElement.classList.remove(CSS.AON_COMPANY_FILTER_EMPTY);
				companyFilterTabSpan.innerHTML = `${companyFilterTab.name} (${companyFilterTabCompanies.length})`;
			}
		}

		if (tabCompanies[this.filter.id])
			return;

		for (let id in tabCompanies) {
			this.select(tabCompanies[id]);
			return;
		}
	}

	selectTab(filter) {
		this.getElement(`${this.COMPANY_FILTER_TAB}-${filter?.id}`)?.classList.add(CSS.AON_TAB_ITEM_TEXT_SELECTED);
	}

	clearSelectedTab(filter) {
		this.getElement(`${this.COMPANY_FILTER_TAB}-${filter?.id}`)?.classList.remove(CSS.AON_TAB_ITEM_TEXT_SELECTED);
	}

	companyFilter(company, filter) {
		if (!filter) {
			filter = {
				active: true
			};
		}

		let value = true;

		if (filter) {

			if (filter.value) {
				const document = company?.document?.toUpperCase().includes(filter.value.toUpperCase());
				const name = company?.name?.toUpperCase().includes(filter.value.toUpperCase());
				value &&= document || name;
			}

			if (filter.active) {
				value &&= company.active;
				value &&= !company.expired;
			}

			if (filter.inactive) {
				value &&= !company.active || company.expired;
			}

			if (filter.shared) {
				value &&= company.shared;
			} else {
				value &&= !company.shared;
			}

			if (filter.entorno) {
				value &&= company.domainManagement;
			} else {
				value &&= !company.domainManagement;
			}

			if (filter.despacho) {
				value &&= company.type === 'OFFICE';
			} else {
				value &&= company.type !== 'OFFICE';
			}

			if (filter.ids) {
				let found = filter.ids.find(id => company.id == id);
				console.log(found);
				value &&= found !== undefined;
			}
		}

		return value;
	}

	getNotices() {
		getUserNotice()
			.then(notice => {
				this.notice = notice;
				this.updateCount();
			})
			.catch(err => {
				this.notice = undefined;
				this.updateCount();
			})
			;
	}

	updateCount() {
		let inboxCount = 0;
		let rejectedCount = 0;
		let pendingCount = 0;
		let processedCount = 0;
		let processingCount = 0;
		let application = this.getApplication();

		if (this.notice?.invoice?.inbox?.count > 0)
			inboxCount = this.notice.invoice.inbox.count;

		if (this.notice?.invoice?.rejected?.count > 0)
			rejectedCount = this.notice.invoice.rejected.count;

		if (this.notice?.invoice?.pending?.count > 0)
			pendingCount = this.notice.invoice.pending.count;

		if (this.notice?.invoice?.processed?.count > 0)
			processedCount = this.notice.invoice.processed.count;

		if (this.notice?.invoice?.processing?.count > 0)
			processingCount = this.notice.invoice.processing.count

		application.updateSidenavCount(this.INBOX_INVOICES, inboxCount);
		application.updateSidenavCount(this.REJECTED_INVOICES, rejectedCount);
		application.updateSidenavCount(this.PENDING_INVOICES, pendingCount);
		application.updateSidenavCount(this.TRAMIT_INVOICES, processedCount + processingCount);
		application.updateSidenavTitle(CONSTANT.INVOICES, `${MSG.ACTIVITY}`);
	}

	build() {

		let parentDiv = this.createDiv();
		parentDiv.className = CSS.AON_PARENT_DIV;
		this.getApplication().setContent(parentDiv);

		let aonDialogEnterpriseOptions = new AonDialogMenu();
		aonDialogEnterpriseOptions.id = "aonDialogEnterpriseOptions";
		parentDiv.appendChild(aonDialogEnterpriseOptions);

		let welcomeDiv = this.createDiv();
		welcomeDiv.className = CSS.AON_WELCOME_DIV;

		let welcomeSpan = this.createSpan();
		this.getWelcomeMessage().then(msg => {
			welcomeSpan.innerHTML = msg;
			this.getApplication()?.getToolbar()?.attributeChangedCallback(CONSTANT.TITLE, null, msg);
		});
		welcomeSpan.classList.add(CSS.AON_WELCOME_MESSAGE);
		welcomeDiv.appendChild(welcomeSpan);

		/*
		let welcomeImg = this.createElement(TAG.IMG);
		welcomeImg.onerror = () => 	welcomeImg.style.display = 'none'; // Hide image if it fails to load
		welcomeImg.onload = () => this.getApplication().openRightSidenav() ;// Show image if it loads successfully 
		this.getWelcomeImage().then( img => welcomeImg.src = img );
		this.getWelcomeMessage().then( msg  => welcomeImg.title = msg );
		welcomeImg.classList.add(CSS.AON_WELCOME_LOGO);
		this.getApplication().getRightSidenav().appendChild(welcomeImg);		
		*/

		// Companies
		let companyDiv = this.createDiv();
		companyDiv.className = CSS.AON_COMPANY_DIV;

		let companyTitleDiv = this.createDiv();
		companyTitleDiv.className = CSS.AON_COMPANY_TITLE_DIV;

		let userOption = new AonDialogMenu();
		userOption.id = 'aonCompanyDialogOption';
		this.appendChild(userOption);

		let companyTitleSpan = this.createSpan();
		companyTitleSpan.id = this.COMPANY_TITLE_SPAN;
		companyTitleSpan.innerHTML = MSG.COMPANY_SELECTION;
		companyTitleSpan.classList.add("aonCompanyTitleSpan");
		companyTitleSpan.classList.add(CSS.AON_COMPANY_FILTER_LOADING);
		companyTitleDiv.appendChild(companyTitleSpan);

		let companyFilterTabDiv = this.createDiv();
		companyFilterTabDiv.id = this.COMPANY_FILTER_TAB;
		companyFilterTabDiv.className = 'companyFilterTab';

		let defaultFilter = {
			type: undefined,
			active: undefined,
			inactive: undefined,
			shared: undefined,
			entorno: undefined,
			despacho: undefined,
			domainActive: undefined,
		};

		let filterOptions = [
			{
				id: 'active',
				name: MSG.ACTIVES,
				icon: 'domain',
				fn: () => this.select({ ...this.getFilter(), ...defaultFilter, ...{ id: 'active', active: true, domainActive: true, name: MSG.ACTIVES } })
			}, {
				id: 'inactive',
				name: MSG.INACTIVES,
				icon: 'domain_disabled',
				fn: () => this.select({ ...this.getFilter(), ...defaultFilter, ...{ id: 'inactive', inactive: true, domainActive: false, name: MSG.INACTIVES } })
			}, {
				id: 'shared',
				name: MSG.SHARED,
				icon: MATERIAL_ICONS.SHARE,
				fn: () => this.select({ ...this.getFilter(), ...defaultFilter, ...{ id: 'shared', shared: true, name: MSG.SHARED } })
			}, {
				id: 'consultancy',
				name: MSG.ENVIRONMENT,
				icon: MATERIAL_ICONS.APARTMENT,
				fn: () => this.select({ ...this.getFilter(), ...defaultFilter, ...{ id: 'consultancy', entorno: true, type: "CONSULTANCY", name: MSG.ENVIRONMENT } })
			}, {
				id: 'office',
				name: MSG.OFFICE,
				icon: 'work',
				fn: () => this.select({ ...this.getFilter(), ...defaultFilter, ...{ id: 'office', despacho: true, type: "OFFICE", name: MSG.OFFICE } })
			}
		];


		let filterTabs = this.createElement(TAG.DIV);
		filterTabs.className = CSS.AON_TAB;
		for (let filterOption of filterOptions) {
			let companyFilterTabA = this.createElement(TAG.A);
			companyFilterTabA.className = CSS.AON_TAB_ITEM;
			companyFilterTabA.addEventListener(EVENT.CLICK, (ev) => {
				filterOption.fn(ev)
			});

			let companyFilterTabSpan = this.createElement(TAG.SPAN);
			companyFilterTabSpan.innerHTML = filterOption.name;
			companyFilterTabSpan.className = CSS.AON_TAB_ITEM_TEXT;
			companyFilterTabSpan.id = `${this.COMPANY_FILTER_TAB}-${filterOption.id}`;
			companyFilterTabA.appendChild(companyFilterTabSpan);

			filterTabs.appendChild(companyFilterTabA);
		}
		
		companyFilterTabDiv.appendChild(filterTabs);

		// Options button (create/delete enterprises)
		let optionsSpan = this.createElement(TAG.SPAN);
		optionsSpan.className = 'aonLiSpanSubtitle';
		optionsSpan.innerHTML = 'Opciones';
		
		let enterpriseSpanButton = this.createElement(TAG.SPAN);
		let aonIconB = new AonIconButton();
		aonIconB.id = "enterpriseButtonsIconOption";
		aonIconB.icon = MATERIAL_ICONS.MORE_VERT;
		enterpriseSpanButton.appendChild(aonIconB);
		enterpriseSpanButton.addEventListener(EVENT.CLICK, () => {
			let optionsButtons = [
				{
					...ACTION.ADD,
					name: 'Crear Empresa',
					fn: () => this.rootPanel(new JSF.AonJsfNewDomain)
				},
				{
					...ACTION.DELETE,
					name: 'Borrar Empresa',
					fn: () => this.rootPanel(new JSF.AonJsfRemoveDomain)
				}
			];

			let top = enterpriseSpanButton.getBoundingClientRect().top + 10;
			let left = enterpriseSpanButton.getBoundingClientRect().left + 50;
			let d = this.getElement('aonDialogEnterpriseOptions');
			let options = optionsButtons.map(({ icon, aonIcon, name, fn }) => {
				return {
					aonIcon,
					icon,
					name,
					fn: () => fn(),
				};
			});

			d.setMenuOptions(options, top, left);
			d.open();
		});
		
		companyFilterTabDiv.appendChild(optionsSpan);
		companyFilterTabDiv.appendChild(enterpriseSpanButton);

		companyDiv.appendChild(companyTitleDiv);
		companyDiv.appendChild(companyFilterTabDiv);

		let ul = this.createElement(TAG.UL);
		ul.id = "UlCompanies";
		ul.classList.add(CSS.AON_UL);
		ul.classList.add(CSS.NO_SCROLLBAR);
		ul.style.overflowY = 'auto';
		ul.style.width = "100%";

		companyDiv.appendChild(ul);
		ul.addEventListener("scroll", () => {
			let scrollTop = ul.scrollTop;
			let offsetHeight = ul.offsetHeight;
			let scrollHeight = ul.scrollHeight;

			if ((scrollTop + offsetHeight) >= (0.75 * scrollHeight)) {
				this.loadMore();
			}
		});

		let contentDiv = this.createDiv();
		contentDiv.appendChild(companyDiv);

		let welcomeImg = this.createElement(TAG.IMG);
		welcomeImg.onerror = () => welcomeImg.style.display = 'none'; // Hide image if it fails to load
		this.getWelcomeImage().then(img => welcomeImg.src = img);
		this.getWelcomeMessage().then(msg => welcomeImg.title = msg);
		welcomeImg.classList.add(CSS.AON_WELCOME_LOGO);

		parentDiv.appendChild(welcomeImg);
		parentDiv.appendChild(welcomeDiv);
		parentDiv.appendChild(contentDiv);

		const interval = setInterval(() => {
			let totalBottom = this.getTotalBottom(ul);
			let totalOffsetTop = this.getTotalOffsetTop(ul);
			if (totalOffsetTop) {
				clearInterval(interval);
				ul.style.maxHeight = `calc(100vh - ${totalOffsetTop + totalBottom}px)`;
			}
		}, 100);

		const appsInterval = setInterval(() => {
			let apps = this.getElement("applications");
			if (apps) {
				clearInterval(appsInterval);
				apps.addEventListener(EVENT.CLICK, () => {
					let companyy = this.getElement("aonHeaderCompanyListButton");
					let companyyy = this.getElement("aonHeaderCompanyList");
					companyy.style.display = "block";
					companyyy.style.display = "block";
					let logo = this.getElement("aonLogo");
					logo.addEventListener("click", function handleClick() {
						companyy.style.display = "none";
						companyyy.style.display = "none";
						logo.removeEventListener("click", handleClick);
					});
				});
			}
		}, 100);
		if (!LS.isOnlyOne())
			LS.setCompanySelected(false);
		else
			LS.setCompanySelected(true);
	}


	loadMore() {
		//TODO: this.getApplication().startLoader();
		getCompanies().then(companies => {
			let first = this.page * 30;
			this.page = this.page + 1;
			this.buildCompanies(this.filterCompanies(companies, this.filter).slice(first, first + 30), this.filter);
		})
			.finally(() => {
				//TODO: this.getApplication().stopLoader();
			});



	}

	createButton(icon, title, fn) {
		let button = new AonIconButton();
		button.noHover = true;
		button.icon = icon;
		button.title = title;

		let innerButton = button.getButton();
		innerButton.style.height = "30px";
		innerButton.style.minWidth = "30px";
		innerButton.style.width = "30px";

		let innerIcon = button.getIcon();
		innerIcon.style.fontSize = "1.3rem";

		button.addEventListener(EVENT.CLICK, (ev) => {
			ev.stopPropagation();
			fn(ev);
		});

		return button;
	}

	buildSidenav() {

		let enterprisesOptions = {
			id: CONSTANT.ENTERPRISES,
			app: ClassicApps.AON_SOLUTIONS,
			name: `<span class="${CSS.AON_COMPANY_FILTER_LOADING}">${MSG.ENTERPRISES.toUpperCase()}</span>`,
			options: [{
				id: this.ENTERPRISES,
				name: MSG.ALL2,
				icon: MATERIAL_ICONS.BUSINESS,
				app: ClassicApps.AON_SOLUTIONS,
				fn: () => {
					let enterprisesFilter = { ids: undefined, count: undefined };
					this.select({ ...this.getFilter(), ...enterprisesFilter }, companies => this.decorateTabs(companies, enterprisesFilter));
				},
			}]
		};

		this.getApplication().addSidenavOptions3(enterprisesOptions);

		let invoiceOptions = {
			id: CONSTANT.INVOICES,
			app: Apps.INVOICE,
			name: `<span class="${CSS.AON_COMPANY_FILTER_LOADING}">${MSG.ACTIVITY.toUpperCase()}</span>`,
			options: [
				{
					id: this.TRAMIT_INVOICES,
					name: MSG.DOCUMENTS_IN_PROCESS,
					icon: MATERIAL_ICONS.EDIT_DOCUMENT,
					app: Apps.INVOICE,
					fn: () => {
						let processedDomains = this.notice?.invoice?.processed?.domains || [];
						let processingDomains = this.notice?.invoice?.processing?.domains || [];
						let domains = processedDomains.concat(processingDomains);
						domains = [...new Set(domains)];

						let domainCount = {};
						let processedCount = this.notice?.invoice?.processed?.domainCount || [];
						let processingCount = this.notice?.invoice?.processing?.domainCount || [];
						for (var key in processedCount) {
							domainCount[key] = domainCount[key] ? domainCount[key] + processedCount[key] : processedCount[key];
						}


						for (var key in processingCount) {
							domainCount[key] = domainCount[key] ? domainCount[key] + processingCount[key] : processingCount[key];
						}

						let tramitFilter = { ids: domains, count: domainCount };
						this.select({ ...this.getFilter(), ...tramitFilter }, companies => this.decorateTabs(companies, tramitFilter));
					},
				},
				{
					id: this.REJECTED_INVOICES,
					name: MSG.DOCUMENTS_UNDER_REVIEW,
					icon: MATERIAL_ICONS.REPORT,
					app: Apps.INVOICE,
					fn: () => {
						let reviewFilter = { ids: this.notice?.invoice?.rejected?.domains, count: this.notice?.invoice?.rejected?.domainCount };
						this.select({ ...this.getFilter(), ...reviewFilter }, companies => this.decorateTabs(companies, reviewFilter));
					},
				},
				{
					id: this.PENDING_INVOICES,
					name: MSG.UNACCOUNT_INVOICES,
					icon: MATERIAL_ICONS.LABEL_IMPORTANT,
					app: Apps.INVOICE,
					fn: () => {
						let unaccountedFilter = { ids: this.notice?.invoice?.pending?.domains, count: this.notice?.invoice?.pending?.domainCount };
						this.select({ ...this.getFilter(), ...unaccountedFilter }, companies => this.decorateTabs(companies, unaccountedFilter));
					},
				},
				{
					id: this.INBOX_INVOICES,
					name: MSG.DRAFTS + "/" + MSG.PROFORMA,
					icon: MATERIAL_ICONS.INBOX,
					app: Apps.INVOICE,
					fn: () => {
						let draftsFilter = { ids: this.notice?.invoice?.inbox?.domains, count: this.notice?.invoice?.inbox?.domainCount };
						this.select({ ...this.getFilter(), ...draftsFilter }, companies => this.decorateTabs(companies, draftsFilter));
					},
				}
			]
		};


		this.getApplication().addSidenavOptions3(invoiceOptions);

		let helpOptions = {
			id: CONSTANT.HELP,
			name: MSG.HELP.toUpperCase(),
			app: Apps.HOME,
			options: [{
				id: CONSTANT.HELP.initCap() + "Notifications",
				name: MSG.NOTIFICATIONS,
				icon: MATERIAL_ICONS.RSS_FEED,
				app: Apps.HOME,
				fn: () => this.rootPanel(new JSF.AonJsfHelpNotification())
			}, {
				id: CONSTANT.HELP.initCap() + "ContentIndex",
				name: MSG.CONTENT_INDEX,
				icon: MATERIAL_ICONS.SCHOOL,
				app: Apps.HOME,
				fn: () => this.rootPanel(new JSF.AonJsfHelpContent())
			},
			]
		};

		this.getApplication().addSidenavOptions3(helpOptions);

		let appsDiv = this.createDiv();
		appsDiv.id = this.APPS_DIV;
		this.getApplication().getSidenav().appendChild(appsDiv);


		getTimeControl()
			.then(r => {
				let option = {
					id: "signing",
					title: MSG.SIGNING.toUpperCase(),
					name: MSG.SIGNING.toUpperCase(),
					app: Apps.TIMECONTROL
				}

				let aonSign = new AonSign();
				this.getApplication().addSidenavWidget2(option, aonSign);

				aonSign.buildSignin(r);
				let aonHeader = this.getElement('aonHeader');
				aonHeader?.timeControlStatus(r);
			});

		this.getNotices();

	}

	cleanCompanies() {
		let ul = this.getElement("UlCompanies");
		while (ul.firstChild) {
			ul.removeChild(ul.lastChild);
		}
	}

	buildCompanies(companies, filter) {
		let ul = this.getElement("UlCompanies");
		for (let company of companies) {
			ul.appendChild(this.buildLi(company, 'transparent', filter?.count?.[company.domain]));
		}
	}

	buildLi(company, color, count) {
		let li = this.createElement(TAG.LI);
		li.className = 'aonLiBeta';
		li.addEventListener(EVENT.CLICK, () => {
			this.companySelection(company, false);
			let portal = LS.isLeftMenu();
			LS.setPortalChecked(portal);
		});

		let companySpan = this.createElement(TAG.SPAN);
		companySpan.className = 'aonLiSpan';

		let icon = this.getIcon(company);

		let iconI = this.createElement(TAG.I);
		iconI.className = 'material-icons aonAvatar';
		iconI.innerHTML = icon;
		iconI.setAttribute('icon', icon);


		let nameSpan = this.createElement(TAG.SPAN);
		nameSpan.innerHTML = company.name;

		let detailSpan = this.createElement(TAG.SPAN);
		detailSpan.className = 'aonLiSpanSubtitle';
		let docSpan = this.createElement(TAG.SPAN);
		docSpan.innerHTML = company.document || `<span class='${CSS.AON_INPUT_BOX_LABEL_SPAN_WARNING}' >Por favor, introduzca un CIF/NIF/Documento válido.</span>`;

		let expirationSpan = this.createElement(TAG.SPAN);
		expirationSpan.className = CSS.AON_INPUT_BOX_LABEL_SPAN_WARNING;
		let expired = company.expired ? 'he expirado' : 'expira';
		expirationSpan.innerHTML = company.expirationDate ? ` El periodo de contratación ${expired} el ${AonDateUtils.formatDate(AonDateUtils.parse(company.expirationDate))}  ` : '';

		detailSpan.appendChild(docSpan);
		detailSpan.appendChild(expirationSpan);

		companySpan.appendChild(iconI);
		companySpan.appendChild(nameSpan);
		companySpan.appendChild(detailSpan);
		li.appendChild(companySpan);

		let countSpan = this.createElement(TAG.SPAN);
		countSpan.className = 'aonLiSpanSubtitle';
		countSpan.style.fontWeight = "bold";
		countSpan.innerHTML = count || '';
		li.appendChild(countSpan);

		if (company.active && !company.expired) {
			let sp = this.createElement(TAG.SPAN);
			let i2 = this.createElement(TAG.I);
			i2.className = 'material-icons aonAvatar';
			i2.innerHTML = MATERIAL_ICONS.KEYBOARD_ARROW_RIGHT;
			sp.appendChild(i2);
			sp.addEventListener(EVENT.CLICK, (event) => {
				this.open(company);
				event.stopPropagation();
			});
			sp.title = `Abrir ${company.name} en una pestaña nueva`;
			li.appendChild(sp);
		}

		return li;
	}

	open(company) {
		let companyForm = document.createElement(TAG.FORM);
		companyForm.style.display = 'none';
		companyForm.target = `${company.name}`;
		companyForm.action = `${location.protocol}//${company.domain}:${location.port}`;
		let tokenInput = this.createElement(TAG.INPUT);
		tokenInput.type = 'hidden';
		tokenInput.name = 'token';
		tokenInput.value = LS.getToken();
		companyForm.appendChild(tokenInput);
		this.appendChild(companyForm);
		companyForm.addEventListener(EVENT.SUBMIT, () => companyForm.remove());
		companyForm.submit();
	}

	getAonHeader() {
		return document.querySelector(TAG.AON_HEADER);
	}

	getIcon(company) {
		return this.getAonHeader()?.getIcon(company);

	}

	companySelection(company, onlyOne) {
		this.getAonHeader()?.showCompanyOption(company, onlyOne);
		this.getAonHeader()?.companySelection(company, onlyOne);
	}

	getCompaniesSchemas() {
		getCompaniesBySchemas(this.getFilter())
			.then(console.log);

		return "Consultando...."
	}

	getTotalOffsetTop(element) {
		let totalOffsetTop = 0;
		for (let el = element; el; el = el.offsetParent) {
			totalOffsetTop += el.offsetTop;
		}
		return totalOffsetTop;
	}

	getTotalBottom(element) {
		let totalBottom = 0;
		for (let el = element; el; el = el.parentElement) {
			let style = getComputedStyle(el);
			totalBottom += parseFloat(style.paddingBottom);
			totalBottom += parseFloat(style.marginBottom);
		}
		return totalBottom;
	}

	filterCompanies(companies, filter) {
		let filteredCompanies = companies.filter(f => this.companyFilter(f, filter));
		return filteredCompanies.sort((c1, c2) => (filter.count?.[c2.domain] || 0) - (filter.count?.[c1.domain] || 1));
	}

	isSharedCompany(company) {
		return !company?.parentId || company?.parentId != LS.getCompany()?.id;
	}

	isLocationCompany(company) {
		return company?.domain?.toUpperCase() == window?.location?.hostname?.toUpperCase()
			|| company?.domain?.toUpperCase() == LS.getCompany()?.domain?.toUpperCase();
	}

	getWelcomeImage() {
		return new Promise((resolve, reject) => {
			resolve(`${window.location.protocol}//${LS.getCompany()?.domain || window.location.hostname}:${window.location.port}/aonDocuments/company.logo`);
		});
	}

	getWelcomeMessage() {
		return new Promise((resolve, reject) => {
			if (LS.getCompany()?.name) {
				resolve(`<span style='font-weight:lighter;' >${MSG.ENVIRONMENT}</span> <span style='font-weight:bolder;'>${LS.getCompany()?.name}</span>`);
			} else if (this.getDur()) {
				resolve(`<span style='font-weight:lighter;'  >${MSG.ENVIRONMENT}</span> <span style='font-weight:bolder;'>${this.getDur().domain.description}</span>`)
			}
			else {
				this.buildDur()
					.then(dur => resolve(`<span style='font-weight:lighter;' >${MSG.ENVIRONMENT}</span> <span style='font-weight:bolder;'>${dur.domain.description}</span>`))
					.catch(err => resolve(`<span style='font-weight:bolder;'>${MSG.WELCOME_TO_AON_SOLUTIONS}</span>`));
			}
		});

	}

}


if (!window.customElements.get(TAG.AON_PARENT)) {
	console.log('Define <aon-new-parent> ^-^');
	window.customElements.define(TAG.AON_PARENT, AonParent);
}
