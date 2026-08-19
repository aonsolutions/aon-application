import { AonReg } from "../aon-reg.js";
import { COLORS, CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, SIG_DOMAIN_ID, SIG_DOMAIN_NAME, TAG } from "../../../environments/environments.js";
import { AonCard } from "../../../components/aon-card.js";
import { AonSelect } from "../../../components/aon-select.js";
import { AonSwitch } from "../../../components/aon-switch.js";
import { AonBasicTable } from "../../../components/aon-basic-table.js";
import { Transactions } from "../../../services/transaction.js";
import { Customer } from "../../../models/registry/Customer.js";
import { getRelationShip, saveRelationShip, removeRelationShip, saveCustomer, getRelationShipCompany, getRegistryNotes, saveCustomerNote, getCustomerDomainAddInfo, removeCustomerDomainAddInfo, removeAonCustomerDomain, saveCustomerNotePro } from "../../../services/registryService.js";
import { AonCustomerList } from "./aon-customer-list.js";
import { getScopes } from "../../../services/documentalService.js";
import { getCustomerStatusTags, getDomainCompanies, saveCompany } from "../../../services/companyService.js";
import { AonBookingItemList } from "../target/item/aon-booking-item-list.js";
import { AonItemList } from "../target/item/aon-item-list.js";
import { AonSellerList } from "../seller/aon-seller-list.js";
import { AonToolbar } from "../../../components/aon-toolbar.js";
import { ToolbarType } from "../../../models/enums.js";
import { AonSellerSmallList } from "../seller/aon-seller-small-list.js";
import { AonBooking } from "../../marketplace/aon-booking.js";

import * as ACTION from '../../actions.js';
import * as GWT from '../../../gwt/gwt.js';
import * as LS from '../../../services/localStorageService.js';
import { AonUserList } from "../../user/aon-user-list.js";
import { AonIconButton } from "../../../components/aon-icon-button.js";
import { generateTokenJson, getUser } from "../../../services/userService.js";
import { AonNewDate } from "../../../components/aon-new-date.js";

export class AonCustomer extends AonReg {

	saveBool;
	ENTERPRISE_LINKED;
	office;
	clientFile;
	aonCustomerList;

	sigCustomerDomainName;
	sigCustomerDomainId;

	// Method to call GWT sync module
	linkSigDomain;

	connectedCallback() {
		this.customerInitialize();
		this.initialize();
		this.build();
	}

	async customerInitialize() {
		this.registry = this.registry || new Customer();
		this.saveBool = true;
		this.type = "customer";
		this.clientFile = this.clientFile;
		this.ENTERPRISE_LINKED = "enterpriseLinked";
		this.options = [
			{ title: MSG.GENERAL_DATA, fn: () => this.buildGeneralData() },
			{
				title: MSG.BANK_DATA, fn: () => {
					this.showSaveButton();
					//if(this.clientFile)
					//	this.hideSaveButton();
					this.buildBankData();
				}
			},
			//{ title: MSG.ADDITIONAL_DATA, fn: () => this.buildDataAdditional() }
		];

		if (this.office) {
			if (!this.clientFile) {
				this.options.push({ title: MSG.AGENTS, fn: () => this.buildSellerData() });
			}
			this.options.push({ title: MSG.EXPEDIENTS, fn: () => this.buildExpedienteData() });
			this.options.push({ title: MSG.CUSTOMER_FEE, fn: () => this.buildCustomerFee() });
			this.options.push({ title: MSG.INVOICES, fn: () => this.buildInvoices() });

			if (this.registry.registryCompany) {
				this.options.push({ title: MSG.BOOKING, fn: () => this.buildOfficeBookingData() });
				this.options.push({ title: MSG.USERS, fn: () => this.buildUsersData() });
			}
		}
	}

	build = () => {
		let toolbar = new AonToolbar();
		toolbar.id = this.REGISTRY_TOOLBAR;
		toolbar.type = ToolbarType.SECONDARY;
		toolbar.title = this.registry.id ? this.registry.name : 'NUEVO REGISTRO';

		this.appendChild(toolbar);
		toolbar.addButton2(ACTION.SAVE, () => this.save());

		if (!this.clientFile)
			toolbar.addButton2(ACTION.BACK, () => this.back());

		this.buildNotesToobar();

		if (this.registry.id && this.registry.getCreationUser) {
			toolbar.addButtonTitle(ACTION.AUDIT, () => this.audit());
		}

		this.buildTabs();

		let div = this.createElement(TAG.DIV);
		div.id = this.DIV;
		div.style.display = "flex";
		div.style.width = "100%";
		div.style.flexWrap = "wrap";
		div.style.height = 'calc(100vh - 15rem)';
		div.style.overflowY = 'auto';
		div.style.margin = '.5rem 0';
		this.appendChild(div);

		this.buildGeneralData();

	}

	buildNotesToobar() {
		if (this.registry.id && this.registry.getCreationUser) {
			let toolbar = this.getElement(this.REGISTRY_TOOLBAR);

			toolbar.addButtonTitle(ACTION.NOTES, () => this.notes());

			getRegistryNotes({ registry: this.registry.getId() })
				.then(notes => {
					const existsNotes = notes.some(item => item.type === "MESSAGE");

					if (existsNotes) {
						let customerNotesButton;

						if (this.clientFile)
							customerNotesButton = this.getElement('aonConfigurationCustomerToolbarHeaderTitleSectionNotesButtonIcon');
						else
							customerNotesButton = this.getElement('aonCustomerOfficeToolbarHeaderTitleSectionNotesButtonIcon');

						customerNotesButton.style.color = 'green';
					}

					const existsObservation = notes.some(item => item.type === "OBSERVATION" && item.comments && item.comments.trim() !== "");
					if (existsObservation) this.notes();

				});
		}
	}

	buildGeneralData = () => {
		this.showSaveButton();

		let parent = this.getElement(this.DIV);
		parent.style.display = "flex";
		this.clearElement(parent);

		let generalCard = this.buildGeneralCard(parent);
		generalCard.classList.add('customerGeneralCard');

		this.buildMediaCard(parent);
		this.buildGeneralInformation(parent);
		this.buildSellerCard(parent);

		this.buildFiscalGeneralData();
	}

	buildFiscalGeneralData() {
		let generalDataTable = this.getElement(this.GENERAL_TABLE);

		generalDataTable.addRow();

		let transaction = new AonSelect();
		transaction.id = this.FISCAL_TRANSACTION;
		transaction.title = MSG.TRANSACTION_TYPE;
		transaction.options = JSON.stringify(Transactions);
		transaction.value = this.registry.getTransaction();
		transaction.addEventListener(EVENT.SELECT, () => {
			this.registry.setTransaction(transaction.value);
			if (this.autosave) this.save();
		});
		generalDataTable.addCell(transaction, 3);

		generalDataTable.addRow();

		let surcharge = new AonSwitch();
		surcharge.id = this.FISCAL_SURCHARGE;
		surcharge.title = MSG.SURCHARGE_RE;
		surcharge.checked = this.registry.isSurcharge();
		surcharge.addEventListener(EVENT.CHANGE, () => {
			this.registry.setSurcharge(surcharge.isChecked());
			if (this.autosave) this.save();
		});
		generalDataTable.addCell(surcharge, 1);

		let withholding = new AonSwitch();
		withholding.id = this.FISCAL_WITHHOLDING;
		withholding.title = MSG.IRPF;
		withholding.checked = this.registry.isWithholding();
		withholding.addEventListener(EVENT.CHANGE, () => {
			this.registry.setWithholding(withholding.isChecked());
			if (this.autosave) this.save();
		});
		generalDataTable.addCell(withholding, 1);
	}

	buildSellerCard(parent) {
		let card = new AonCard();
		card.id = "cardSeller";
		card.title = "Agentes";
		card.style.width = "50%";
		card.style.cursor = "pointer";
		parent.appendChild(card);

		card.addEventListener(EVENT.CLICK, () => {
			let sellerTab = this.getElement("aonCustomerOfficeTabsSpan2");
			if (sellerTab) sellerTab.click();

			// Scroll to top
			let aonOfficePanelContent = this.getElement("aonOfficePanelContent");
			aonOfficePanelContent.scrollTop = 0;
		});

		let div = this.createElement(TAG.DIV);
		card.setContent(div);

		let registryId = this.registry.getId();

		if (registryId) {
			let aonSellerList = new AonSellerSmallList();
			aonSellerList.style.width = "100%";
			aonSellerList.registry = this.registry;
			aonSellerList.filter = { page: 1, perPage: 200, registry: registryId };
			aonSellerList.parent = card;
			div.appendChild(aonSellerList);

			let sellerTableBody = this.getElement("aonSellerListTableTableBody");
			sellerTableBody.style.height = "auto";
		}
	}

	buildDataAdditional() {
		this.showSaveButton();

		if (this.clientFile)
			this.hideSaveButton();

		let parent = this.getElement(this.DIV);
		parent.style.display = "flex";
		this.clearElement(parent);

		this.buildGeneralInformation(parent);
		this.buildFiscalData(parent);
	}

	buildGeneralInformation(parent) {
		let card = new AonCard();
		card.id = "cardAdditionalInformation";
		card.title = MSG.ADDITIONAL_INFORMATION;
		card.style.width = "50%";
		parent.appendChild(card);

		let div = this.createElement(TAG.DIV);
		card.setContent(div);

		let table = new AonBasicTable();
		card.id = "aonTableGeneralInformation";
		div.appendChild(table);

		table.addRow();

		let scope = new AonSelect();
		scope.id = "selectScope";
		scope.title = MSG.SCOPE;
		scope.autocomplete = true;
		scope.addEventListener(EVENT.SELECT, () => {
			this.registry.setScope(scope.getDetail());
			if (this.autosave) this.save();
		});

		table.addCell(scope);

		table.addRow();

		if (!this.clientFile) {
			let divSegment = this.createElement(TAG.DIV);
			table.addCell(divSegment);
			this.buildSegments(divSegment);
		}

		if (this.clientFile) {
			let company = LS.getCompany();
			getRelationShipCompany({
				url: company.domain,
				relatedRegistry: company.registry
			}).then(relationshipCompany => {
				getScopes({
					searchDomain: relationshipCompany.rrelationship.domain.id
				}).then((scopes) => {
					const registryScope = this.registry.getScope();
					const scopeId = registryScope && registryScope.id ? registryScope.id : null;

					scope.setOptions(scopes.map((c) => ({ ...c, value: c.id })));

					if (scopeId) {
						scope.value = scopeId;
					}
				});
			});
		} else
			getScopes().then((scopes) => {
				const registryScope = this.registry.getScope();
				const scopeId =
					registryScope && registryScope.id ? registryScope.id : null;

				scope.setOptions(scopes.map((c) => ({ ...c, value: c.id })));

				if (scopeId) {
					scope.value = scopeId;
				}
			});
	}

	buildFiscalData(parent) {
		let card = new AonCard();
		card.id = this.FISCAL_CARD;
		card.title = MSG.FISCAL_DATA;
		card.style.width = "50%";
		parent.appendChild(card);

		let div = this.createElement(TAG.DIV);
		card.setContent(div);

		let table = new AonBasicTable();
		table.id = this.FISCAL_TABLE;
		div.appendChild(table);

		table.addRow();

		let transaction = new AonSelect();
		transaction.id = this.FISCAL_TRANSACTION;
		transaction.title = MSG.TRANSACTION_TYPE;
		transaction.options = JSON.stringify(Transactions);
		transaction.value = this.registry.getTransaction();
		transaction.addEventListener(EVENT.SELECT, () => {
			this.registry.setTransaction(transaction.value);
			if (this.autosave) this.save();
		});
		table.addCell(transaction, 2);

		table.addRow();

		let surcharge = new AonSwitch();
		surcharge.id = this.FISCAL_SURCHARGE;
		surcharge.title = MSG.SURCHARGE_RE;
		surcharge.checked = this.registry.isSurcharge();
		surcharge.addEventListener(EVENT.CHANGE, () => {
			this.registry.setSurcharge(surcharge.isChecked());
			if (this.autosave) this.save();
		});
		table.addCell(surcharge, 1);

		let withholding = new AonSwitch();
		withholding.id = this.FISCAL_WITHHOLDING;
		withholding.title = MSG.IRPF;
		withholding.checked = this.registry.isWithholding();
		withholding.addEventListener(EVENT.CHANGE, () => {
			this.registry.setWithholding(withholding.isChecked());
			if (this.autosave) this.save();
		});
		table.addCell(withholding, 1);
	}

	buildEnterpriseLinked() {
		if (this.office) {
			const card = this.getElement(this.GENERAL_CARD);

			let divOne = this.getElement(this.ENTERPRISE_LINKED);

			if (!divOne) {
				divOne = this.createElement(TAG.DIV);
				divOne.id = this.ENTERPRISE_LINKED;
				card.addSection2(divOne);
			}

			if (this.isSig()) {
				getCustomerDomainAddInfo({
					registry: this.registry.getId()
				})
					.then((resp) => {
						this.buildSigEnterpriseLinkedView(resp);

						if (resp && resp.length > 0) {
							this.sigCustomerDomainName = resp[0].domainName;
							this.sigCustomerDomainId = resp[0].domainId;

							if (!this.existTabOption(MSG.BOOKING))
								this.addTabOption({ title: MSG.BOOKING, fn: () => this.buildOfficeBookingData() });

							if (!this.existTabOption(MSG.USERS))
								this.addTabOption({ title: MSG.USERS, fn: () => this.buildUsersData() });
						}
					})
					/*
					.catch((err) => {
						this.showError(err);
					})
					*/
					;
			} else {
				getRelationShip({
					registry: this.registry.getId(),
					parentId: this.registry.getDomain().getParentId(),
					document: this.registry.getDocument(),
				})
					.then((resp) => {
						console.log("getRelationShip", resp);
						this.buildEnterpriseLinkedView(resp);
					})
					/*
					.catch((err) => {
						this.showError(err);
					})
					*/
					;
			}
		}
	}

	buildEnterpriseLinkedView(resp) {
		const entepriseLinked = this.getElement(this.ENTERPRISE_LINKED);
		entepriseLinked.innerHTML = "";

		const { rrelationship, companies } = resp;

		const link = rrelationship && rrelationship.id;

		const color = link ? CSS.variable(COLORS.ONLINE_GREEN) : COLORS.ORANGE;

		let main = this.createElement(TAG.DIV);
		main.title =
			"Vinculo con empresa " +
			(link ? `(${rrelationship.comments})` : "(No existe)");
		main.style.display = "flex";
		main.style.columnGap = "5px";
		main.style.border = "1px solid";
		main.style.borderColor = "lightgray";
		main.style.borderRadius = "10px";
		main.style.padding = "4px";
		main.style.cursor = "pointer";
		main.style.marginRight = "6px";
		entepriseLinked.appendChild(main);

		let statusBox = this.createElement(TAG.DIV);
		statusBox.className = CONSTANT.MATERIAL_ICONS;
		statusBox.style.fontSize = "18px";
		statusBox.style.color = color;
		statusBox.innerText = link ? MATERIAL_ICONS.LINK : MATERIAL_ICONS.LINK_OFF;
		main.appendChild(statusBox);

		let statusText = this.createElement(TAG.DIV);
		statusText.innerText = link ? "Vinculado" : "Desvinculado";
		statusText.style.fontSize = "14px";
		statusText.style.fontWeight = "500";
		statusText.style.color = "#5f6368";
		main.appendChild(statusText);

		let iconArrowDown = this.createElement(TAG.DIV);
		iconArrowDown.style.fontSize = "18px";
		iconArrowDown.className = CONSTANT.MATERIAL_ICONS;
		iconArrowDown.innerText = MATERIAL_ICONS.KEYBOARD_ARROW_DOWN;

		if (link || !companies.length) {
			main.appendChild(iconArrowDown);
		}

		if (link) {
			main.addEventListener(EVENT.CLICK, () => {
				this.getOptionsLinked(iconArrowDown, rrelationship);
			});
		} else {
			statusText.style.marginRight = "5px";
			main.addEventListener(EVENT.CLICK, () => {
				const countCompany = companies.length;

				if (countCompany > 0) {
					this.openDialogCompany(companies);
				} else {
					this.getOptionsLinked(iconArrowDown);
				}
			});
		}
	}

	buildSigEnterpriseLinkedView(resp) {
		const entepriseLinked = this.getElement(this.ENTERPRISE_LINKED);
		entepriseLinked.innerHTML = "";

		const link = resp && resp.length > 0;

		const color = link ? CSS.variable(COLORS.ONLINE_GREEN) : COLORS.ORANGE;

		let main = this.createElement(TAG.DIV);
		main.title = "Vinculo con empresa " + (link ? `(${resp && resp.length > 1 ? 'Múltiple' : resp[0].domainName})` : "(No existe)");
		main.style.display = "flex";
		main.style.columnGap = "5px";
		main.style.border = "1px solid";
		main.style.borderColor = "lightgray";
		main.style.borderRadius = "10px";
		main.style.padding = "4px";
		main.style.cursor = "pointer";
		main.style.marginRight = "6px";
		entepriseLinked.appendChild(main);

		let statusBox = this.createElement(TAG.DIV);
		statusBox.className = CONSTANT.MATERIAL_ICONS;
		statusBox.style.fontSize = "18px";
		statusBox.style.color = color;
		statusBox.innerText = link ? MATERIAL_ICONS.LINK : MATERIAL_ICONS.LINK_OFF;
		main.appendChild(statusBox);

		let statusText = this.createElement(TAG.DIV);
		statusText.innerText = link ? "Vinculado" : "Desvinculado";
		statusText.style.fontSize = "14px";
		statusText.style.fontWeight = "500";
		statusText.style.color = "#5f6368";
		main.appendChild(statusText);

		let iconArrowDown = this.createElement(TAG.DIV);
		iconArrowDown.style.fontSize = "18px";
		iconArrowDown.className = CONSTANT.MATERIAL_ICONS;
		iconArrowDown.innerText = MATERIAL_ICONS.KEYBOARD_ARROW_DOWN;

		main.appendChild(iconArrowDown);

		main.addEventListener(EVENT.CLICK, () => {
			this.getSigOptionsLinked(iconArrowDown, resp);
		});
	}

	hideSaveButton() {
		let saveBtn;
		if (this.clientFile)
			saveBtn = this.getElement("aonConfigurationCustomerToolbarHeaderToolSectionSaveButton");
		else
			saveBtn = this.getElement("aonCustomerOfficeToolbarHeaderToolSectionSaveButton");
		if (saveBtn) saveBtn.style.display = 'none';
	}

	showSaveButton() {
		let saveBtn;
		if (this.clientFile)
			saveBtn = this.getElement("aonConfigurationCustomerToolbarHeaderToolSectionSaveButton");
		else
			saveBtn = this.getElement("aonCustomerOfficeToolbarHeaderToolSectionSaveButton");
		if (saveBtn) saveBtn.style.display = 'block';
	}

	//EXPEDIENTE
	buildExpedienteData() {
		this.hideSaveButton();

		let main = this.getElement(this.DIV);
		main.style.display = "flex";
		this.clearElement(main);

		main.style.height = 'calc(100vh - 15rem)';
		main.style.overflowY = 'auto';
		main.style.margin = '.5rem 0';

		localStorage.setItem("customer", this.registry.getId());

		if (this.clientFile) {
			let company = LS.getCompany();

			getRelationShipCompany({
				url: company.domain,
				relatedRegistry: company.registry
			}).then(relationshipCompany => {
				localStorage.setItem("officeDomain", relationshipCompany.rrelationship.domain.id);
				GWT.iLoad(GWT.PROJECT, this.DIV);
			});
		} else
			GWT.iLoad(GWT.PROJECT, this.DIV);
	}

	//ITEMS PRODUCTS
	buildItemData() {
		this.hideSaveButton();

		let main = this.getElement(this.DIV);
		main.style.display = "flex";
		this.clearElement(main);

		let registryId = this.registry.getId();

		if (registryId) {
			let aonItemList = new AonItemList();
			aonItemList.style.width = "100%";
			aonItemList.registry = this.registry;
			aonItemList.filter = {
				page: 1,
				perPage: 200,
				registry: registryId,
				type: "TARGET",
			};
			main.appendChild(aonItemList);
		}
	}


	buildOfficeBookingData() {
		this.hideSaveButton();

		let main = this.getElement(this.DIV);
		main.style.display = "block";
		main.style.height = 'calc(100vh - 15rem)';
		main.style.overflowY = 'auto';
		main.style.margin = '.5rem 0';
		this.clearElement(main);

		let booking = new AonBooking();
		booking.sessionData = this.getSessionData();
		booking.fromCustomer = true;
		main.appendChild(booking);
	}

	buildUsersData() {
		this.hideSaveButton();

		let main = this.getElement(this.DIV);
		main.style.display = "block";
		main.style.height = 'calc(100vh - 15rem)';
		main.style.overflowY = 'auto';
		main.style.margin = '.5rem 0';
		this.clearElement(main);

		let userList = new AonUserList();
		userList.sessionData = this.getSessionData();
		userList.parent = main;
		userList.fromCustomer = true;
		main.appendChild(userList);
	}

	getSessionData() {
		return {
			session_id: LS.getToken(),
			domain_name: this.sigCustomerDomainName || (this.registry.registryCompany ? this.registry.registryCompany.domain.name : LS.getDomainName()),
			domain_id: this.sigCustomerDomainId || (this.registry.registryCompany ? this.registry.registryCompany.domain.id : LS.getDomainId()),
			domain_login: LS.getDomainLogin()
		};
	}

	//BOOKING PRODUCTS
	buildBookingData() {
		this.hideSaveButton();

		let main = this.getElement(this.DIV);
		main.style.display = "flex";
		this.clearElement(main);

		let registryId = this.registry.getId();

		if (registryId) {
			let aonItemList = new AonBookingItemList();
			aonItemList.style.width = "100%";
			aonItemList.registry = this.registry;
			aonItemList.filter = {
				page: 1,
				perPage: 200,
				registry: registryId,
				type: "BOOKING",
			};
			main.appendChild(aonItemList);
		}
	}

	//SELLERS
	buildSellerData() {
		this.hideSaveButton();

		let main = this.getElement(this.DIV);
		main.style.display = "flex";
		main.style.height = 'calc(100vh - 15rem)';
		main.style.overflowY = 'auto';
		main.style.margin = '.5rem 0';
		this.clearElement(main);

		let registryId = this.registry.getId();

		if (registryId) {
			let aonSellerList = new AonSellerList();
			aonSellerList.style.width = "100%";
			aonSellerList.registry = this.registry;
			aonSellerList.filter = { page: 1, perPage: 200, registry: registryId };
			main.appendChild(aonSellerList);
		}
	}

	buildCustomerFee() {
		this.hideSaveButton();

		let div = this.getElement(this.DIV);
		div.style.display = "flex";
		this.clearElement(div);

		div.style.height = 'calc(100vh - 15rem)';
		div.style.overflowY = 'auto';
		div.style.margin = '.5rem 0';

		localStorage.setItem("customer", this.registry.getId());

		if (this.clientFile) {
			let company = LS.getCompany();

			getRelationShipCompany({
				url: company.domain,
				relatedRegistry: company.registry
			}).then(relationshipCompany => {
				localStorage.setItem("officeDomain", relationshipCompany.rrelationship.domain.id);
				GWT.iLoad(GWT.CUSTOMER_FEE, this.DIV);
			});
		} else
			GWT.iLoad(GWT.CUSTOMER_FEE, this.DIV);
	}

	buildInvoices() {
		this.hideSaveButton();

		let div = this.getElement(this.DIV);
		div.style.display = "flex";
		this.clearElement(div);

		div.style.height = 'calc(100vh - 15rem)';
		div.style.overflowY = 'auto';
		div.style.margin = '.5rem 0';

		localStorage.setItem("customer", this.registry.getId());

		if (this.clientFile) {
			let company = LS.getCompany();

			getRelationShipCompany({
				url: company.domain,
				relatedRegistry: company.registry
			}).then(relationshipCompany => {
				localStorage.setItem("officeDomain", relationshipCompany.rrelationship.domain.id);
				GWT.iLoad(GWT.CUSTOMER_INVOICE, this.DIV);
			});
		} else
			GWT.iLoad(GWT.CUSTOMER_INVOICE, this.DIV);

	}

	getOptionsLinked(element, rrelationship = undefined) {
		let options = [];

		if (rrelationship) {
			options.push(
				{
					name: "Abrir",
					value: "OPEN",
					icon: MATERIAL_ICONS.OPEN_IN_NEW,
					fn: () => {
						if (rrelationship.comments) {
							window.open("https://" + rrelationship.comments);
						}
					},
				}
			);

			options.push(
				{
					name: "Desvincular",
					value: "UNLINK",
					icon: MATERIAL_ICONS.LINK_OFF,
					fn: () => {
						this.getApplication().startLoading();

						removeRelationShip(rrelationship)
							.then(() => {
								this.showMessage();
								this.buildEnterpriseLinked();
							})
							.catch((err) => this.showError(err))
							.finally(() => {
								this.getApplication().stopLoading();
							});
					},
				}
			);
		} else {
			options.push(
				{
					name: "Vincular empresa",
					value: "LINK",
					icon: MATERIAL_ICONS.LINK,
					fn: () => {
						this.openDialogCompany();
					},
				}
			);
		}

		if (options && options.length > 0) {
			const top = element.getBoundingClientRect().top + 24;
			const left = element.getBoundingClientRect().left + 3;
			let d = this.getApplication().getOptionDialog();
			d.setMenuOptions(options, top, left);
			d.open();
		}
	}

	getSigOptionsLinked(element, resp) {
		let options = [];

		if (resp && resp.length > 0) {
			options.push(
				{
					name: "Acceder",
					value: "access",
					icon: MATERIAL_ICONS.OPEN_IN_NEW,
					fn: () => {
						if (resp.length === 1)
							this.suplant(resp[0].domainName);
						else
							this.openSigSuplantCompany(resp);
					},
				}
			);

			options.push(
				{
					name: "Desvincular",
					value: "UNLINK",
					icon: MATERIAL_ICONS.LINK_OFF,
					fn: () => {
						if (resp.length === 1)
							this.unlinkSigCompany(resp[0]);
						else
							this.openSigUnlinkCompany(resp);
					},
				}
			);

		} else {
			options.push(
				{
					name: "Vincular",
					value: "LINK",
					icon: MATERIAL_ICONS.LINK,
					fn: () => {
						this.linkSigDomain(this.registry.alias);
					},
				}
			);
		}
		/*
		else {
			options.push(
				{
					name: "Crear nueva empresa",
					value: "ENTERPRISE_NEW",
					icon: MATERIAL_ICONS.OPEN_IN_NEW,
					fn: () => {
						this.getApplication().confirmDialog(
							MSG.REGISTER,
							`Desea registrar y vincular a ${this.registry.getName()} ?`,
							() => {
								this.getApplication().startLoading();
								saveCompany({ ...this.registry, id: null })
									.then((company) => {
										console.log("company", company);
										this.saveRegistryRelationship(company);
									})
									.catch((err) => {
										this.showError(err);
									})
									.finally(() => {
										this.getApplication().stopLoading();
									});
							}
						);
					},
				}
			);
		}
		*/

		if (options && options.length > 0) {
			const top = element.getBoundingClientRect().top + 24;
			const left = element.getBoundingClientRect().left + 3;
			let d = this.getApplication().getOptionDialog();
			d.setMenuOptions(options, top, left);
			d.open();
		}
	}

	suplant(url) {
		getUser().then(user => {
			//console.log("User");
			//console.log(user);

			let d = this.getApplication().getDialog();
			d.clear();

			if (!this.isMobile()) d.width = '400px';
			d.setTitle(MSG.IMPERSONATE_USER);

			d.setContentHTML("Estás seguro de suplantar a " + user.login);
			d.addAcceptAction(() => {
				let data = {
					supUser: LS.getDomainLogin(),
					id: user.id,
					time: 0
				};
				generateTokenJson(data).then(token => {
					open(`https://${url}/app?token=${token.session_id}`, '_blank');
				}).catch(e => this.showError(e));
			});
			d.open();
		});
	}
	
	async openCustomerInactiveBloqued() {

		if (!this.office) {
			this.buildStatusRegistry();
			this.save();
			return;
		}

		const isBlocked = this.registry.status === "BLOCKED";

		const dialog = this.getApplication().getDialog();
		dialog.clear();

		if (this.isMobile()) {
			dialog.type = "fullscreen";
		} else {
			dialog.width = "30%";
		}

		const title = isBlocked
			? "Motivo bloqueo"
			: (this.registry.status === "INACTIVE" ? "Motivo inactividad" : "Motivo activo");

		dialog.setTitle(title);

		let div = document.createElement(TAG.DIV);
		div.style.display = "flex";
		div.style.flexDirection = "column";
		div.style.marginTop = "10px";
		dialog.setContent(div);

		let selectTag = new AonSelect();
		selectTag.id = "selectTag";
		selectTag.title = "Motivo";
		div.appendChild(selectTag);

		selectTag.loading(true);
		getCustomerStatusTags()
			.then((tags) => {
				const options = tags.map((c) => ({ ...c, value: c.id }));
				selectTag.setOptions(options);
			})
			.finally(() => {
				selectTag.loading(false);
			});

		// Solo en BLOCKED hay fecha de expiracion
		let datePicker = null;

		if (isBlocked) {
			datePicker = new AonNewDate();
			datePicker.id = "aonDBloquedDatePicker";
			datePicker.title = "F. Expiración Empresa";

			// Se anade antes del setDate: connectedCallback tiene que haber
			// montado el input para que setDate pueda escribir su valor
			div.appendChild(datePicker);

			const current = this.toDateValue(this.registry.expirationDate);
			if (current) datePicker.setDate(current);
		}

		dialog.addSendAction(async () => {
			if (!selectTag.value) return;

			dialog.close();
			this.getApplication().startLoading();

			// null => el backend normaliza a hoy (BLOCKED siempre lleva fecha)
			const date = datePicker ? datePicker.getDateValue() : null;

			// IMPRESCINDIBLE: el save() posterior manda el registry completo.
			// Si la fecha no viaja aqui, CustomerAutoComplete la normalizaria
			// a hoy y perderiamos la fecha elegida, dejando cliente y dominio
			// desincronizados.
			this.registry.setExpirationDate(date);

			try {
				await this.saveNote(selectTag.getDetail().name, date);
				this.buildStatusRegistry();
				this.save();
			} catch (err) {
				this.showError(err);
			} finally {
				this.getApplication().stopLoading();
			}
		}, MSG.SAVE);

		dialog.open();
	}

	/**
	 * Convierte 'yyyy-MM-dd' en un Date local.
	 * No usamos new Date(str) porque lo interpreta como UTC y desplazaria
	 * el dia en zonas con offset negativo.
	 */
	toDateValue(value) {
		if (!value) return null;
		if (value instanceof Date) return value;

		const parts = String(value).split("-");
		if (parts.length !== 3) return null;

		const year = Number(parts[0]);
		const month = Number(parts[1]);
		const day = Number(parts[2]);

		if (!year || !month || !day) return null;

		return new Date(year, month - 1, day);
	}

	async saveNote(tagName, date) {
		const params = {
			tagName: tagName,
			customerId: this.registry.id,
			status: this.registry.status,
			date: date,
			isSig: this.isSig()
		}

		if (params.isSig) {
			let headers = { domain_name: LS.getDomainName(), domain_id: LS.getDomainId() };
			//params.domain_name = LS.getDomainName();
			//params.domain_id = LS.getDomainId();
			await saveCustomerNotePro(params, headers);
		} else
			await saveCustomerNote(params);
	}

	openDialogCompany(companies = []) {
		const dialog = this.getApplication().getDialog();
		dialog.clear();

		if (this.isMobile()) {
			dialog.type = "fullscreen";
		} else {
			dialog.width = "30%";
		}

		dialog.setTitle(
			companies.length ? "Sugerencias para el vinculo" : MSG.ENTERPRISE
		);

		let div = document.createElement(TAG.DIV);
		div.style.display = "flex";
		div.style.flexDirection = "column";
		div.style.marginTop = "10px";
		dialog.setContent(div);

		let selectCompany = new AonSelect();
		selectCompany.id = "selectCompany";
		selectCompany.title = MSG.COMPANY;
		selectCompany.autocomplete = true;
		div.appendChild(selectCompany);

		if (companies.length) {
			let timeOut = null;

			selectCompany.setOptions(companies.map((c) => ({ ...c, value: c.id })));

			selectCompany.addEventListener(EVENT.INPUT, async ({ target }) => {
				clearTimeout(timeOut);
				const value = target.value;
				if (value.length > 2) {
					timeOut = setTimeout(async () => {
						selectCompany.loading(true);
						const cs = await this.getDomainCompanies(value);
						selectCompany.setOptions(cs);
						selectCompany.loading(false);
					}, 300);
				}
			});
		} else {
			selectCompany.loading(true);
			this.getDomainCompanies()
				.then((companies) => {
					selectCompany.setOptions(companies);
				})
				.finally(() => {
					selectCompany.loading(false);
				});
		}

		dialog.addSendAction(() => {
			if (selectCompany.value) {
				this.saveRegistryRelationship(selectCompany.getDetail());
				dialog.close();
			}
		}, MSG.LINK);

		dialog.open();
	}

	openSigSuplantCompany(resp = []) {
		const dialog = this.getApplication().getDialog();
		dialog.clear();

		if (this.isMobile()) dialog.type = "fullscreen";
		else dialog.width = "30%";

		dialog.setTitle(MSG.ACCESS_LINKED_COMPANY);

		let div = document.createElement(TAG.DIV);
		div.style.display = "flex";
		div.style.flexDirection = "column";
		div.style.marginTop = "10px";
		dialog.setContent(div);

		let selectCompany = new AonSelect();
		selectCompany.id = "selectSupplantCompany";
		selectCompany.title = MSG.COMPANY;
		selectCompany.autocomplete = true;
		div.appendChild(selectCompany);

		if (resp.length) {
			selectCompany.setOptions(resp.map((c) => ({ ...c, name: c.domainName, value: c.domainName })));
		}

		dialog.addSendAction(() => {
			if (selectCompany.value) {
				dialog.close();
				this.suplant(selectCompany.value);
			}
		}, 'Acceder');

		dialog.open();
	}

	openSigUnlinkCompany(resp = []) {
		const dialog = this.getApplication().getDialog();
		dialog.clear();

		if (this.isMobile()) dialog.type = "fullscreen";
		else dialog.width = "30%";

		dialog.setTitle(MSG.ACCESS_LINKED_COMPANY);

		let div = document.createElement(TAG.DIV);
		div.style.display = "flex";
		div.style.flexDirection = "column";
		div.style.marginTop = "10px";
		dialog.setContent(div);

		let selectCompany = new AonSelect();
		selectCompany.id = "selectSupplantCompany";
		selectCompany.title = MSG.COMPANY;
		selectCompany.autocomplete = true;
		div.appendChild(selectCompany);

		if (resp.length) {
			selectCompany.setOptions(resp.map((c) => ({ ...c, name: c.domainName + " (" + c.schema + ")", value: JSON.stringify(c) })));
		}

		dialog.addSendAction(() => {
			if (selectCompany.value) {
				dialog.close();
				this.unlinkSigCompany(JSON.parse(selectCompany.value));
			}
		}, 'Desvincular');

		dialog.open();
	}

	async unlinkSigCompany(relation) {
		let headers = { domain_name: relation.domainName, domain_id: relation.domainId };
		await removeAonCustomerDomain({ ...relation }, headers)
			.then(() => {
				this.showMessage();
				this.buildEnterpriseLinked();
			})
			.catch((err) => this.showError(err));

		await removeCustomerDomainAddInfo(relation)
			.then(async () => {
				this.showMessage();
				this.buildEnterpriseLinked();
			})
			.catch((err) => this.showError(err))
			.finally(() => {
				this.getApplication().stopLoading();
			});

	}

	async getDomainCompanies(value) {
		let params = { parentId: this.registry.getDomain().getParentId() };
		if (value) params.value = value;

		let result = await getDomainCompanies(params);

		return result
			.filter(
				(company) =>
					company &&
					company.domain &&
					company.domain.id != this.registry.getDomain().getId()
			)
			.map((c) => ({ ...c, value: c.id }));
	}

	saveRegistryRelationship(company) {
		this.getApplication().startLoading();

		let relationship = {
			domain: this.registry.getDomain(),
			registry: this.registry.getId(),
			related_registry: company.id,
			comments:
				company.domain && company.domain.name ? company.domain.name : "",
		};

		saveRelationShip(relationship)
			.then(() => {
				this.showMessage();
				this.buildEnterpriseLinked();
			})
			.catch((err) => this.showError(err))
			.finally(() => {
				this.getApplication().stopLoading();
			});
	}

	back() {
		if (!this.aonCustomerList) {
			this.aonCustomerList = new AonCustomerList();
			this.aonCustomerList.id = this.getApplication().id + "CustomerList";
		}
		this.getApplication().setContent(this.aonCustomerList);
	}

	save() {
		if (this.saveBool) {
			let medias = this.emails.concat(this.phones).concat(this.webs);
			this.registry.setMedia(medias);
			this.saveBool = false;

			saveCustomer(this.registry)
				.then((registry) => {
					this.registry.id = registry.id;
					this.saveBool = true;

					let aonTab = this.getElement(this.TABS);
					let selectedTab = aonTab.getSelectedTab();
					selectedTab && selectedTab.click();

					this.showToast({
						type: "success",
						message: "Datos Guardados Correctamente",
					});
				})
				.catch((error) => {
					this.saveBool = true;
					this.showToast(error);
				});
		}
	}

	setCustomer(customer) {
		this.registry = new Customer(customer);
	}

	setOffice(office) {
		this.office = office;
	}

	setClientFile(clientFile) {
		this.clientFile = clientFile;
	}

	notes() {
		let rightSidenav = this.getApplication().getRightSidenav();
		this.clearElement(rightSidenav);

		let notesIcon;

		if (this.clientFile) {
			notesIcon = this.getElement("aonConfigurationCustomerToolbarHeaderTitleSectionNotesButtonIcon");
		} else
			notesIcon = this.getElement("aonCustomerOfficeToolbarHeaderTitleSectionNotesButtonIcon");

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

			localStorage.setItem("customer", this.registry.getId());

			if (this.clientFile) {
				let company = LS.getCompany();

				getRelationShipCompany({
					url: company.domain,
					relatedRegistry: company.registry
				}).then(relationshipCompany => {
					localStorage.setItem("officeDomain", relationshipCompany.rrelationship.domain.id);
					GWT.iLoad(GWT.CUSTOMER_NOTES, div.id);
				});


			} else
				GWT.iLoad(GWT.CUSTOMER_NOTES, div.id);

		}

		notesIcon.classList.toggle("material-icons-selected");

		this.getApplication().toogleRightSidenav();

	}

	setCustomerList(aonCustomerList) {
		this.aonCustomerList = aonCustomerList;
	}
}

if (!window.customElements.get(TAG.AON_CUSTOMER)) {
	window.customElements.define(TAG.AON_CUSTOMER, AonCustomer);
}
