import {AonElement} from '../../components/AonElement.js';
import {ToolbarType} from '../../models/enums.js';
import {AonToolbar} from "../../components/aon-toolbar.js";
import {AonCard} from "../../components/aon-card.js";
import {COLORS, CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js'; 
import { AonBasicTable } from '../../components/aon-basic-table.js';
import { AonInput } from '../../components/aon-input.js';
import { AonAddress } from '../../components/aon-address.js';
import { AonUpload } from '../../components/aon-upload.js';
import { AonIconButton } from '../../components/aon-icon-button.js';
import { Media } from '../../models/registry/Media.js';
import { getSegments, saveRegistry } from '../../services/registryService.js';
import { Registry } from '../../models/registry/Registry.js';
import { RegistrySegment } from '../../models/registry/RegistrySegment.js';
import { Address } from '../../models/registry/Address.js';
import { getReader } from '../../services/utils.js';
import { deleteAttach, getAttach, uploadAttach } from '../../services/fileService.js';
import { Countries } from '../../services/country.js';
import { AonSelect } from '../../components/aon-select.js';
import { AonTab } from '../../components/aon-tab.js';
import { Bank } from './bank/Bank.js';
import { AonIban } from '../../components/aon-iban.js';
import { AonNumber } from '../../components/aon-number.js';
import { getPaymethods } from '../../services/invoiceService.js';
import { AonDate } from '../../components/aon-date.js';
import * as GWT from '../../gwt/gwt.js';
import * as ACTION from '../actions.js';
import { AonDateUtils } from '../utils/AonDateUtils.js';

export class AonReg extends AonElement {

	registry;
	type; 
	showLogo;
	oneAddress;
	logo;
	options;

	segments;

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
		this.build();
	}

	initialize() {
		this.id = this.id || 'aonCompany';
		this.TABS = this.id + 'Tabs';
		this.REGISTRY_TOOLBAR = this.id + 'Toolbar';
		this.DIV = this.id + 'Div';
		this.GENERAL_CARD = this.id + 'GeneralCard';
		this.GENERAL_TABLE = this.GENERAL_CARD + 'Table';
		this.ADDRESS_TABLE = this.GENERAL_CARD + 'AddressTable';
		this.ADDRESS_ADD = this.ADDRESS_TABLE + 'Add';

		this.BANK_CARD = this.id + 'BankCard';
		this.BANK_TABLE = this.BANK_CARD + 'Table';
		this.BANK_ADD = this.BANK_TABLE + 'Add';

		this.INFO_CARD = this.id + 'InfoCard';
		this.INFO_TABLE = this.INFO_CARD + 'Table';

		this.MEDIA_CARD = this.id + 'MediaCard';
		this.EMAIL_TABLE = this.MEDIA_CARD + 'EmailTable';
		this.EMAIL_INPUT = this.EMAIL_TABLE + 'Input';
		this.EMAIL_ADD= this.EMAIL_TABLE + 'Add';
		
		this.PHONE_TABLE = this.MEDIA_CARD + 'PhoneTable';
		this.PHONE_INPUT = this.PHONE_TABLE + 'Input';
		this.PHONE_COMMENT_INPUT = this.PHONE_TABLE + 'CommentInput';
		this.PHONE_ADD= this.PHONE_TABLE + 'Add';

		this.WEB_TABLE = this.MEDIA_CARD + 'WebTable';
		this.WEB_INPUT = this.WEB_TABLE + 'Input';
		this.WEB_ADD= this.WEB_TABLE + 'Add';

		this.PAYMETHOD_CARD = this.id + 'Paymethod';
		this.PAYMETHOD_TABLE = this.PAYMETHOD_CARD + 'Table';
		this.PAYMETHOD_PAYMETHOD = this.PAYMETHOD_TABLE + 'Paymethod';
		this.PAYMETHOD_BANK = this.PAYMETHOD_TABLE + 'Bank';
		this.PAYMETHOD_NUMPAY = this.PAYMETHOD_TABLE + 'NumPay';
		this.PAYMETHOD_FIRSTPAY = this.PAYMETHOD_TABLE + 'FirstPay';
		this.PAYMETHOD_BETWEENPAY = this.PAYMETHOD_TABLE + 'BetweenPay';
		this.PAYMETHOD_PAYDAY = this.PAYMETHOD_TABLE + 'PayDay';

		this.REGISTRAL_CARD = this.id + 'Registral';
		this.REGISTRAL_TABLE= this.REGISTRAL_CARD + 'Table';
		this.REGISTRAL_CREATION_DATE = this.REGISTRAL_TABLE + 'CreationDate';
		this.REGISTRAL_RECORD_DATE = this.REGISTRAL_TABLE + 'RecordDate';
		this.REGISTRAL_DESCRIPTION = this.REGISTRAL_TABLE + 'Description';
		this.REGISTRAL_NOTARY = this.REGISTRAL_TABLE + 'Notary';
		this.REGISTRAL_PROTOCOL_NUMBER = this.REGISTRAL_TABLE + 'ProtocolNumber';
		this.REGISTRAL_INSCRIPTION = this.REGISTRAL_TABLE + 'Inscription';
		this.REGISTRAL_TOMO = this.REGISTRAL_TABLE + 'Tomo';
		this.REGISTRAL_SECTION = this.REGISTRAL_TABLE + 'Section';
		this.REGISTRAL_FOLIO = this.REGISTRAL_TABLE + 'Folio';
		this.REGISTRAL_HOJA = this.REGISTRAL_TABLE + 'Hoja';

		this.FISCAL_CARD = this.id + 'Fiscal';
		this.FISCAL_TABLE = this.FISCAL_CARD + 'Table';
		this.FISCAL_TRANSACTION = this.FISCAL_CARD + 'Transaction';
		this.FISCAL_SURCHARGE = this.FISCAL_CARD + 'Surcharge';
		this.FISCAL_WITHHOLDING = this.FISCAL_CARD + 'Withholding';
		this.FISCAL_WITHHOLDING_FARMER = this.FISCAL_CARD + 'WithholdingFarmer';
		this.FISCAL_VAT_ACCRUAL_PAYMENT = this.FISCAL_CARD + 'VatAccrualPayment';

		this.registry = this.registry || new Registry();
		this.showLogo = this.showLogo || false;
		this.emails = [];
		this.webs = [];
		this.phones = [];
		this.oneAddress = this.oneAddress || true;
		this.segments = [];
		this.options = this.options || [
			{ title: MSG.GENERAL_DATA, fn: () => this.buildGeneralData()},
			{ title: MSG.BANK_DATA, fn: () => this.buildBankData()},
			{ title: MSG.REGISTRATION_DATA, fn: () => this.buildRegistralData()},
			{ title: MSG.CERTIFICATES, fn: () => this.buildCertificates()}
		];

	}

	build() {
		let toolbar = new AonToolbar();
		toolbar.id = this.REGISTRY_TOOLBAR;
		toolbar.type = ToolbarType.SECONDARY;
		toolbar.title = this.registry.id ? this.registry.name : 'NUEVO REGISTRO';
		
		this.appendChild(toolbar);
		toolbar.addButton2(ACTION.SAVE, () => this.save());
		toolbar.addButton2(ACTION.BACK, () => this.back());
		
		if(this.registry.id && this.registry.getCreationUser){
			toolbar.addButtonTitle(ACTION.AUDIT, () => this.audit());
		}

		this.buildTabs();

		let div = this.createElement(TAG.DIV);
		div.id = this.DIV;
		div.style.display = "flex";
		div.style.width = "100%";
		this.appendChild(div);

		this.buildGeneralData();
	}

	buildTabs() {
		let tab = new AonTab();
		tab.id = this.TABS;
		tab.setOptions(this.options);
		this.appendChild(tab);
	}
	
	buildGeneralData() {
		let div = this.getElement(this.DIV);
		this.clearElement(div);
		this.buildGeneralCard(div);
		this.buildMediaCard(div);
	}

	buildBankData() {
		let div = this.getElement(this.DIV);
		this.clearElement(div);
		this.buildBankAccountCard(div);
		this.buildPaymethodCard(div);
	}

	buildCertificates() {
		let div = this.getElement(this.DIV);
		div.style.position = 'absolute';
		div.style.height = '100%';
		GWT.iLoad(GWT.MAIN_DIGITAL_CERTIFICATES, this.DIV);
	}

	buildRegistralData() {
		let div = this.getElement(this.DIV);
		this.clearElement(div);
		this.buildRegistralCard(div);
	}


	buildGeneralCard(parent){
		let card = new AonCard();
		card.id = this.GENERAL_CARD;
		card.title = MSG.GENERAL_INFORMATION;
		card.style.width = '50%';
		parent.appendChild(card);
		card.firstChild.firstChild.style.marginBottom = "5px";

		if(this.registry.id){
			if(this.isCustomer()){
				this.buildEnterpriseLinked();
			}

			if(this.registry.status){
				this.buildStatusRegistry();
			}
		}

		let div = this.createElement(TAG.DIV);
		card.setContent(div);

		let table = new AonBasicTable();
		table.id = this.GENERAL_TABLE;
		div.appendChild(table);

		table.addRow();

		let registrySegment = this.registry.getRegistrySegments()
		.map(s =>  new RegistrySegment(s))
		.filter(s => !s.isRemoved());

		let segmentsDiv = document.createElement(TAG.DIV);
		segmentsDiv.style.display = "flex";
		segmentsDiv.style.flexDirection = "row"
		segmentsDiv.style.width = "100%";
		segmentsDiv.style.flexWrap = "wrap";
		segmentsDiv.style.justifyContent = "flex-end";
		segmentsDiv.style.alignItems = "flex-start";
		segmentsDiv.style.gap = "10px";
		registrySegment.forEach((seg, ind) => {
			let segment = seg ? seg.segment : null;
			let segmentName = segment ? segment.name : '';
			if (segmentName) {
				let segmentLabel = document.createElement(TAG.DIV);
				segmentLabel.style.display = "flex";
				segmentLabel.style.alignItems = "center";
				segmentLabel.style.width = "fit-content";
				segmentLabel.style.maxWidth = "350px";
				segmentLabel.style.overflow = "hidden";
				segmentLabel.style.textOverflow = "ellipsis";
				segmentLabel.style.height =  "25px";
				segmentLabel.style.padding = "5px";
				segmentLabel.style.borderRadius  = "5px";
				segmentLabel.style.backgroundColor = "lavender";
				segmentLabel.style.color = "#1f2120";
				segmentLabel.style.textAlign = "center";
				segmentLabel.title = segmentName;
				segmentLabel.innerText = segmentName;
				segmentsDiv.appendChild(segmentLabel);
			}
		});
		let segmentsCell = table.addCell(segmentsDiv, 3);



		table.addRow();

		let nameInput = new AonInput();
		nameInput.id = 'aonConfigurationGeneralName';
		nameInput.description = MSG.BUSINESS_NAME + ' / ' + MSG.NAME;
		nameInput.value = this.registry.getName();
		nameInput.addEventListener(EVENT.CHANGE, () => this.registry.setName(nameInput.value));
		table.addCell(nameInput, 3);

		table.addRow();

		let countryInput = new AonSelect();
		countryInput.id = 'aonConfigurationGeneralCountry';
		countryInput.title = MSG.COUNTRY;
		countryInput.options = JSON.stringify(
		  Countries.map((c) => {
			return { value: c.iso2, name: c.iso2 };
		  })
		);
		countryInput.value = this.registry.getDocumentCountry();
		countryInput.addEventListener(EVENT.SELECT, () => 
			 this.registry.setDocumentCountry(countryInput.value));
		let countryTd = table.addCell(countryInput);
		countryTd.style.width = '20%';

		let documentInput = new AonInput();
		documentInput.id = 'aonConfigurationGeneralNif';
		documentInput.description = MSG.NIF;
		documentInput.value = this.registry.getDocument();
		documentInput.addEventListener(EVENT.CHANGE, () => this.registry.setDocument(documentInput.value));

		let td = table.addCell(documentInput);
		td.style.width = '25%';

		let aliasInput = new AonInput();
		aliasInput.id = 'aonConfigurationGeneralAlias';
		aliasInput.description = MSG.COMMERCIAL_NAME + ' / ' + MSG.ALIAS;
		aliasInput.value = this.registry.getAlias();
		aliasInput.addEventListener(EVENT.CHANGE, () => this.registry.setAlias(aliasInput.value));
		let td1 = table.addCell(aliasInput);
		td1.style.width = '55%';

		this.buildAddresses(div);

		if(this.showLogo && this.registry.getId()) {
			let uploadLogo = new AonUpload();
			uploadLogo.id = this.id + 'UploadLogo';
			uploadLogo.setMessage(MSG.ATTACH_FILES_DRAGGING_DROPPING_LOGO);
			uploadLogo.setDeleteMessage(MSG.DELETE_LOGO_CONFIRM);
			uploadLogo.addEventListener(EVENT.UPLOAD, (e) => {
			  getReader(e.detail).then(f => {
				let data = f
					.setAttachType('registry')
					.setAttachModule(this.registry.getId())
					.setType(0)
					.setId(this.logo ? this.logo.id: undefined);
				uploadAttach(data);
			  });
			});
			div.appendChild(uploadLogo);
			let filter = {
				attachType: 'registry',
				attachModule: this.registry.getId(),
				type: 0				
			};

			uploadLogo.addEventListener(EVENT.DELETE, (e) => {
				this.logo = {};
				deleteAttach(filter);
			});
			
			getAttach(filter).then(attach => {
				this.logo = attach;
				if(this.logo.id)
					uploadLogo.setAttach(attach);
			});
		}
	}

	buildStatusRegistry(){
		let card = this.getElement(this.GENERAL_CARD);
		const id = "statusDiv";

		let statusDiv = this.getElement(id);
		if(statusDiv) statusDiv.remove();

		const title = this.registry.status ? MSG[this.registry.status] : "";

		let color = CSS.variable(COLORS.ONLINE_GREEN);
		if(this.registry.status === "INACTIVE"){
			color = COLORS.ORANGE;
		} else if(this.registry.status === "BLOCKED"){
			color = "#DC4D30"; //CSS.variable(COLORS.MATERIAL_RED);
		}

		statusDiv = this.createElement(TAG.DIV);
		statusDiv.id = id;
		statusDiv.title = MSG.STATUS;
		statusDiv.style.display = "flex";
		statusDiv.style.columnGap = "5px";
		statusDiv.style.border = "1px solid";
		statusDiv.style.borderColor = "lightgray";
		statusDiv.style.borderRadius = "10px";
		statusDiv.style.padding = "4px";
		statusDiv.style.cursor = "pointer";
		card.addSection2(statusDiv);

		let statusBox = this.createElement(TAG.DIV);
		statusBox.style.width           = "10px";
		statusBox.style.height          = "10px";
		statusBox.style.borderRadius    = "50%";
		statusBox.style.marginTop       = "3px";
		statusBox.style.backgroundColor = color;
		statusDiv.appendChild(statusBox);

		let statusText = this.createElement(TAG.DIV);
		statusText.innerText = title;
		statusText.style.fontSize = "14px";
		statusText.style.fontWeight = "500";
		statusText.style.color = "#5f6368";
		statusDiv.appendChild(statusText);

		let iconArrowDown = this.createElement(TAG.DIV);
		iconArrowDown.style.fontSize  = "18px";
		iconArrowDown.className = CONSTANT.MATERIAL_ICONS;
		iconArrowDown.innerText = MATERIAL_ICONS.KEYBOARD_ARROW_DOWN;
		statusDiv.appendChild(iconArrowDown);

		statusDiv.addEventListener(EVENT.CLICK, () => this.getOptionsStatus(iconArrowDown));
	}

	getOptionsStatus(element){
		const top = element.getBoundingClientRect().top + 24;
		const left = element.getBoundingClientRect().left + 3;
		let d = this.getApplication().getOptionDialog();

		let options = [
			{ 
				name: "Activar", 
				value:"ACTIVE",
				icon:"toggle_on", 
				fn:()=> {
					this.registry.status = "ACTIVE";
					this.buildStatusRegistry();
					this.save();
				}
			},
			{ 
				name: "Inactivar", 
				value:"INACTIVE",
				icon:"toggle_off", 
				fn:()=> {
					this.registry.status = "INACTIVE";
					this.buildStatusRegistry();
					this.save();
				}
			},
			{ 
				name: "Bloquear", 
				value:"BLOCKED",
				icon:"block", 
				fn:()=> {
					this.registry.status = "BLOCKED";
					this.buildStatusRegistry();
					this.save();
				}
			}
		];

		if(this.registry.status){
			options = options.filter(opt => opt.value!=this.registry.status );
		}

		d.setMenuOptions(options, top, left);
		d.open();
	}

	buildMediaCard(parent){
		let card = new AonCard();
		card.id = this.MEDIA_CARD;
		card.title = MSG.CONTACT_DATA;
		card.style.width = '50%';
		parent.appendChild(card);

		let div = this.createElement(TAG.DIV);
		card.setContent(div);

		this.buildEmails(div);
		this.buildPhones(div);
		this.buildWebs(div);
	}

	buildBankAccountCard(parent){
		let card = new AonCard();
		card.id = this.BANK_CARD;
		card.title = MSG.BANK_ACCOUNTS;
		card.style.width = '50%';
		parent.appendChild(card);

		let div = this.createElement(TAG.DIV);
		card.setContent(div);

		this.buildBanks(div);	
	}


	buildPaymethodCard(parent){
		let card = new AonCard();
		card.id = this.PAYMETHOD_CARD;
		card.title = 'Forma de Pago Predeterminada';
		card.style.width = '50%';
		parent.appendChild(card);

		let div = this.createElement(TAG.DIV);
		card.setContent(div);

		let table = new AonBasicTable();
		table.id = this.PAYMETHOD_TABLE;
		div.appendChild(table);

		table.addRow();

		let paymethodSelect = new AonSelect();
		paymethodSelect.setAlias('id', 'name');
		paymethodSelect.id = this.PAYMETHOD_PAYMETHOD;
		paymethodSelect.title = MSG.PAYMETHOD;
		table.addCell(paymethodSelect, 4);
		getPaymethods({}).then(paymethods => {
			paymethodSelect.setOptions(paymethods);
			paymethodSelect.value = this.registry.getPaymethod().getPaymethod().id || paymethods[0].id;
		});
		paymethodSelect.addEventListener(EVENT.CHANGE, (e) => {
			this.registry.getPaymethod().getPaymethod().id = paymethodSelect.value;
			// TODO SI ES TRANSFERENCIA ACTUALIZAR DATOS BANK
		});


		table.addRow();

		let bankSelect = new AonSelect()
		bankSelect.id = this.PAYMETHOD_BANK;
		bankSelect.title = MSG.BANK_ACCOUNT;
		bankSelect.setAlias('id', 'fullName');
		bankSelect.setOptions(this.registry.getBanks());
		bankSelect.value = this.registry.getPaymethod().getBank().id;
		bankSelect.addEventListener(EVENT.CHANGE, (e) => this.registry.getPaymethod().getBank().id = bankSelect.value);
		table.addCell(bankSelect, 4);
		
		

		table.addRow();

		let numPayNumber = new AonNumber();
		numPayNumber.id = this.PAYMETHOD_NUMPAY;
		numPayNumber.description = 'Nº Pagos';
		numPayNumber.value = this.registry.getPaymethod().getNumberOfPymnts();
		numPayNumber.addEventListener(EVENT.CHANGE, () => this.registry.getPaymethod().setNumberOfPymnts(numPayNumber.value));
		table.addCell(numPayNumber, 1);

		let firstPayNumber = new AonNumber();
		firstPayNumber.id = this.PAYMETHOD_FIRSTPAY;
		firstPayNumber.description = 'Días 1º Pago';
		firstPayNumber.value = this.registry.getPaymethod().getDaysToFirstPymnt();
		firstPayNumber.addEventListener(EVENT.CHANGE, () => this.registry.getPaymethod().setDaysToFirstPymnt(firstPayNumber.value));
		table.addCell(firstPayNumber, 1);

		let betweenPayNumber = new AonNumber();
		betweenPayNumber.id = this.PAYMETHOD_BETWEENPAY;
		betweenPayNumber.description = 'Días entre Pagos';
		betweenPayNumber.value = this.registry.getPaymethod().getDaysBetweenPymnts();
		betweenPayNumber.addEventListener(EVENT.CHANGE, () => this.registry.getPaymethod().setDaysBetweenPymnts(betweenPayNumber.value));
		table.addCell(betweenPayNumber, 1);

		let payDayInput = new AonInput();
		payDayInput.id = this.PAYMETHOD_PAYDAY;
		payDayInput.description = 'Días Pago';
		payDayInput.value = this.registry.getPaymethod().getPymntDays();
		payDayInput.addEventListener(EVENT.CHANGE, () => this.registry.getPaymethod().setPymntDays(payDayInput.value));
		table.addCell(payDayInput, 1);		
	}

	buildRegistralCard(parent){
		let card = new AonCard();
		card.id = this.REGISTRAL_CARD;
		card.title = MSG.REGISTRATION_DATA;
		card.style.width = '50%';
		parent.appendChild(card);

		let div = this.createElement(TAG.DIV);
		card.setContent(div);

		let table = new AonBasicTable();
		table.id = this.REGISTRAL_TABLE;
		div.appendChild(table);

		table.addRow();
		let description = new AonInput()
		description.id = this.REGISTRAL_DESCRIPTION;
		description.description = MSG.DESCRIPTION;
		description.value = this.registry.getRecordData().getDescription();
		description.addEventListener(EVENT.CHANGE, () => {
			this.registry.getRecordData().setDescription(description.value);
		});
		table.addCell(description, 4);

		table.addRow();

		let creationDate = new AonDate();
		creationDate.id = this.REGISTRAL_CREATION_DATE;
		creationDate.value = this.registry.getRecordData().getCreationDate();
		creationDate.title = MSG.CREATION_DATE;
		creationDate.addEventListener(EVENT.CHANGE, () => {
			this.registry.getRecordData().setCreationDate(creationDate.value);
			if(this.autosave) this.save();
		});
		table.addCell(creationDate, 2);

		let recordDate = new AonDate();
		recordDate.id = this.REGISTRAL_RECORD_DATE;
		recordDate.title = MSG.REGISTRATION_DATE;
		recordDate.value = this.registry.getRecordData().getRecordDate();
		recordDate.addEventListener(EVENT.CHANGE, () => {
			this.registry.getRecordData().setRecordDate(recordDate.value);
			if(this.autosave) this.save();
		});
		table.addCell(recordDate, 2);

		table.addRow();
		
		let notary = new AonInput()
		notary.id = this.REGISTRAL_NOTARY;
		notary.description = MSG.NOTARY;
		notary.value = this.registry.getRecordData().getNotary();
		notary.addEventListener(EVENT.CHANGE, () => {
			this.registry.getRecordData().setNotary(notary.value);
		});
		table.addCell(notary, 4);

		table.addRow();

		let protocol = new AonInput()
		protocol.id = this.REGISTRAL_PROTOCOL_NUMBER;
		protocol.description = MSG.PROTOCOL;
		protocol.value = this.registry.getRecordData().getNumber();
		protocol.addEventListener(EVENT.CHANGE, () => {
			this.registry.getRecordData().setNumber(protocol.value);
		});
		table.addCell(protocol, 1);

		let inscription = new AonInput()
		inscription.id = this.REGISTRAL_INSCRIPTION;
		inscription.description = MSG.INSCRIPTION;
		inscription.value = this.registry.getRecordData().getRegistration();
		inscription.addEventListener(EVENT.CHANGE, () => {
			this.registry.getRecordData().setRegistration(inscription.value);
		})
		table.addCell(inscription, 3);

		table.addRow();

		let tomo = new AonInput()
		tomo.id = this.REGISTRAL_TOMO;
		tomo.description = MSG.VOLUME;
		tomo.value = this.registry.getRecordData().getVolume();
		tomo.addEventListener(EVENT.CHANGE, () => {
			this.registry.getRecordData().setVolume(tomo.value);
		});
		table.addCell(tomo, 1);

		let section = new AonInput()
		section.id = this.REGISTRAL_SECTION;
		section.description = MSG.SECTION;
		section.value = this.registry.getRecordData().getSection();
		section.addEventListener(EVENT.CHANGE, () => {
			this.registry.getRecordData().setSection(section.value);
		});
		table.addCell(section, 1);

		let folio = new AonInput()
		folio.id = this.REGISTRAL_FOLIO;
		folio.description = MSG.FOLIO;
		folio.value = this.registry.getRecordData().getPage();
		folio.addEventListener(EVENT.CHANGE, () => {
			this.registry.getRecordData().setPage(folio.value);
		});
		table.addCell(folio, 1);

		let hoja = new AonInput()
		hoja.id = this.REGISTRAL_HOJA;
		hoja.description = MSG.SHEET;
		hoja.value = this.registry.getRecordData().getSheet();
		hoja.addEventListener(EVENT.CHANGE, () => {
			this.registry.getRecordData().setSheet(hoja.value);
		});
		table.addCell(hoja, 1);		
	}

	buildInfoCard(parent) {

	}

	buildAddresses(parent) {
		let table = new AonBasicTable();
		table.id = this.ADDRESS_TABLE;
		parent.appendChild(table);

		if(!this.registry.getAddresses() || this.registry.getAddresses().length <= 0)
			this.registry.addAddress(new Address().setRegistry(this.registry.getId()).setMain(true));
		
		this.registry.getAddresses().filter(f => !this.oneAddress || f.isMain()).forEach((address, i) => 
			this.buildAddress(table, address, i));
	}

	buildAddress(table, address, i) {
		if(!address.isRemoved()){
			let rowNum = table.addRow();

			let aonAddress = new AonAddress();
			aonAddress.id = 'aonConfigurationGeneralAddress' + i;
			aonAddress.title = MSG.ADDRESS + ' ' + (this.registry.addresses.length > 1 ? i + 1 : '');
			aonAddress.setAddress(address);
			aonAddress.addEventListener(EVENT.CHANGE, () => this.registry.addresses[i] = aonAddress.getAddress());
			let td = table.addCell(aonAddress);
			td.style.width = '100%';

			let addAddress = new AonIconButton();
			addAddress.id = this.ADDRESS_ADD + i;
			addAddress.title = MSG.ADD;
			addAddress.icon = MATERIAL_ICONS.ADD_CIRCLE_OUTLINE;
			addAddress.visible = !this.oneAddress && this.registry.addresses.length === i+1;
			addAddress.addEventListener(EVENT.CLICK, () => {
				let number = this.registry.addresses.length;
				let aux = new Address().setRegistry(this.registry.getId())
				this.registry.addAddress(aux);
				addAddress.visible = false;
				this.buildAddress(table, aux, number);
			});
			table.addCell(addAddress);

			aonAddress.addEventListener(EVENT.DELETE, () => {
				if(table.getRowsCount() === 1) {
					let aux = new Address().setRegistry(this.registry.getId())
					aonAddress.setAddress(aux, true);
					this.registry.getAddresses()[i] = aux;
				} else {
					let last = this.getElement(this.ADDRESS_ADD + i).isVisible();
					this.registry.getAddresses()[i].remove();
					table.removeRow(rowNum);
					if(last) {
						let j = 0;
						this.registry.getAddresses().forEach((item, h) => {
							if(!item.isRemoved())
								j = h;
						});
						this.getElement(this.ADDRESS_ADD + j).visible = true;
					}
				}
			});
		}
	}

	buildBanks(parent) {
		let table = new AonBasicTable();
		table.id = this.BANK_TABLE;
		parent.appendChild(table);

		if(!this.registry.getBanks() || this.registry.getBanks().length <= 0)
			this.registry.addBank(new Bank().setRegistry(this.registry.getId()));
		
		this.registry.getBanks().forEach((bank, i) => 
			this.buildBank(table, bank, i));
	}

	buildBank(table, bank, i) {
		if(!bank.isRemoved()){
			let rowNum = table.addRow();

			let aonBank = new AonIban();
			aonBank.id = 'aonConfigurationGeneralBank' + i;
			aonBank.title = MSG.BANK_ACCOUNT + ' ' + (this.registry.getBanks().length > 1 ? i + 1 : '');
			aonBank.setBank(bank);
			aonBank.addEventListener(EVENT.CHANGE, () => {
				this.registry.banks[i] = aonBank.getBank();
				this.getElement(this.PAYMETHOD_BANK).setOptions(this.registry.getBanks().filter(f => !f.isRemoved()));
			});
			let td = table.addCell(aonBank);
			td.style.width = '100%';

			let addBank = new AonIconButton();
			addBank.id = this.BANK_ADD + i;
			addBank.title = MSG.ADD;
			addBank.icon = MATERIAL_ICONS.ADD_CIRCLE_OUTLINE;
			addBank.visible = this.registry.banks.length === i+1;
			addBank.addEventListener(EVENT.CLICK, () => {
				let number = this.registry.banks.length;
				let aux = new Bank().setRegistry(this.registry.getId())
				this.registry.addBank(aux);
				addBank.visible = false;
				this.buildBank(table, aux, number);
			});
			table.addCell(addBank);

			aonBank.addEventListener(EVENT.DELETE, () => {
				if(table.getRowsCount() === 1) {
					let aux = new Bank().setRegistry(this.registry.getId())
					aonBank.setBank(aux);
					this.registry.getBanks()[i] = aux;
				} else {
					let last = this.getElement(this.BANK_ADD + i).isVisible();
					this.registry.getBanks()[i].remove();
					this.getElement(this.PAYMETHOD_BANK).setOptions(this.registry.getBanks().filter(f => !f.isRemoved()));
					table.removeRow(rowNum);
					if(last) {
						let j = 0;
						this.registry.getBanks().forEach((item, h) => {
							if(!item.isRemoved())
								j = h;
						});
						this.getElement(this.BANK_ADD + j).visible = true;
					}
				}
			});
		}
	}

	// EMAILS

	buildEmails(parent) {
		this.emails = this.registry.getMedia()
			.filter(f => f.isEmail());

		let table = new AonBasicTable();
		table.id = this.EMAIL_TABLE;
		parent.appendChild(table);

		if(!this.emails || this.emails.length <= 0){
			let media = new Media()
				.setRegistry(this.registry.getId())
				.setMedia('EMAIL');
			this.emails = [media];
			this.buildEmail(table, media, 0);
		} else {
			this.emails.forEach( (email, i) =>
				this.buildEmail(table, email, i));
		}
	}

	buildEmail(table, email, i) {
		if(!email.isRemoved()) {
			let rowNum = table.addRow();

			let aonInput = new AonInput();
			aonInput.id = this.EMAIL_INPUT + i;
			aonInput.description = MSG.EMAIL + ' ' + (this.emails.length > 1 ? i + 1 : '');
			aonInput.value = email.getValue();
			aonInput.addEventListener(EVENT.CHANGE, () => this.emails[i].setValue(aonInput.value));

			let addEmail = new AonIconButton();
			addEmail.id = this.EMAIL_ADD + i;
			addEmail.title = MSG.ADD;
			addEmail.icon = MATERIAL_ICONS.ADD_CIRCLE_OUTLINE;
			addEmail.visible = this.emails.length === i+1;
			addEmail.addEventListener(EVENT.CLICK, () => {
				let number = this.emails.length;
				let media = new Media()
					.setRegistry(this.registry.getId())
					.setMedia('EMAIL');
				this.emails.push(media);
				addEmail.visible = false;
				this.buildEmail(table, media, number);
			});
			let td = table.addCell(aonInput);
			td.style.width = '100%';
			table.addCell(addEmail);
			aonInput.addIconWithRemove(MATERIAL_ICONS.MAIL, undefined, () => {
				if(table.getRowsCount() === 1) {
					aonInput.value = '';
					this.emails[i].setValue('');
				} else {
					let last = this.getElement(this.EMAIL_ADD + i).isVisible();
					this.emails[i].remove();
					table.removeRow(rowNum);
					if(last) {
						let j = 0;
						this.emails.forEach((item, h) => {
							if(!item.isRemoved())
								j = h;
						});
						this.getElement(this.EMAIL_ADD + j).visible = true;
					}
				}
			});
		}
	}

	// PHONES

	buildPhones(parent) {
		this.phones = this.registry.getMedia()
			.filter(f => f.isPhone());
		let table = new AonBasicTable();
		table.id = this.PHONE_TABLE;
		parent.appendChild(table);
	
		if(!this.phones || this.phones.length <= 0){
			let media = new Media()
				.setRegistry(this.registry.getId())
				.setMedia('CELLULAR');
			this.phones = [media];
			this.buildPhone(table, media, 0);
		} else {
			this.phones.forEach((phone, i) => 
				this.buildPhone(table, phone, i));
		}
	}
	
	buildPhone(table, phone, i) {
		if(!phone.isRemoved()){
			let rowNum = table.addRow();
	
			let aonInput = new AonInput();
			aonInput.id = this.PHONE_INPUT + i;
			aonInput.description = MSG.PHONE + ' ' + (this.phones.length > 1 ? i + 1 : '');
			aonInput.value = phone.getValue();
	
			aonInput.addEventListener(EVENT.CHANGE, () => this.phones[i].setValue(aonInput.value));

			let aonCommentInput = new AonInput();
			aonCommentInput.id = this.PHONE_COMMENT_INPUT + i;
			aonCommentInput.description = MSG.COMMENT + ' ' + (this.phones.length > 1 ? i + 1 : '');
			aonCommentInput.value = phone.getComment();
	
			aonCommentInput.addEventListener(EVENT.CHANGE, () => this.phones[i].setComment(aonCommentInput.value));
	
			let addPhone = new AonIconButton();
			addPhone.id = this.PHONE_ADD + i;
			addPhone.title = MSG.ADD;
			addPhone.icon = MATERIAL_ICONS.ADD_CIRCLE_OUTLINE;
			addPhone.visible = this.phones.length === i+1;
			addPhone.addEventListener(EVENT.CLICK, () => {
				let number = this.phones.length;
				let media = new Media()
					.setRegistry(this.registry.getId())
					.setMedia('CELLULAR');
				this.phones.push(media);
				addPhone.visible = false;
				this.buildPhone(table, media, number);
			});

			let td = table.addCell(aonInput);
			let tdComment = table.addCell(aonCommentInput);

			td.style.width = '40%';
			tdComment.style.width = '60%';
			
			table.addCell(addPhone);
			
			aonCommentInput.addIconWithRemove(MATERIAL_ICONS.PHONE, undefined, () => {
				if(table.getRowsCount() === 1) {
					aonInput.value = '';
					aonCommentInput.value = '';
					this.phones[i].setValue('');
				} else {
					let last = this.getElement(this.PHONE_ADD + i).isVisible();
					this.phones[i].remove();
					table.removeRow(rowNum);
					if(last) {
						let j = 0;
						this.phones.forEach((item, h) => {
							if(!item.isRemoved())
								j = h;
						});
						this.getElement(this.PHONE_ADD + j).visible = true;
					}
				}
			});
		}
	}

	// WEBS

	buildWebs(parent) {
		this.webs = this.registry.getMedia()
			.filter(f => f.isWeb());

		let table = new AonBasicTable();
		table.id = this.WEB_TABLE;
		parent.appendChild(table);
	
		if(!this.webs || this.webs.length <= 0){
			let media = new Media()
				.setRegistry(this.registry.getId())
				.setMedia('WEB');
			this.webs = [media];
			this.buildWeb(table, media, 0);
		} else {
			this.webs.forEach((web, i) => 
				this.buildWeb(table, web, i));
		}
	}

	//SEGMENTATIONS
	buildSegments(parent) {
		const table =  new AonBasicTable();
		table.id =  "segmentTable";
		parent.appendChild(table);

		let registrySegment = this.registry.getRegistrySegments()
		.map(s =>  new RegistrySegment(s))
		.filter(s => !s.isRemoved());

		if(!registrySegment || registrySegment.length <= 0){
			let registrySegment = new RegistrySegment();
			registrySegment.setRegistry(this.registry.getId());
			registrySegment.setDomain(this.registry.getDomain());
			this.registry.addRegistrySegment(registrySegment);
			this.buildSegment(table, registrySegment, 0);
		} else {
			this.registry.setRegistrySegments([]);
			registrySegment.forEach( (rsegment, i) =>{
				this.registry.addRegistrySegment(rsegment);
				this.buildSegment(table, rsegment, i)
			});
		}
	}

	buildSegment(table, registrySegment, i) {
		const rowNum = table.addRow();

		const rowCount = table.getRowsCount();
		
		let segment = new AonSelect();
		segment.id = "selectSegment" + rowNum;
		segment.title = "Segmento";
		segment.autocomplete = true;
		segment.addEventListener(EVENT.CHANGE, () => {
			if(segment.value){
				registrySegment.setSegment(segment.getDetail())
			}
		});
		
		table.addCell(segment).style.width = '100%';
	
		this.getSegments()
		.then(options=>{
			segment.setOptions(options);
			if(registrySegment.getSegment()){
				segment.value = registrySegment.getSegment().id;
			}
		})

		let removeIcon = new AonIconButton();
		removeIcon.id = "segmentRemove" +rowNum;
		removeIcon.title = MSG.DELETE;
		removeIcon.icon = MATERIAL_ICONS.CLOSE;
		removeIcon.addEventListener(EVENT.CLICK, () => {
			const count = table.getRowsCount();
			let childVisible = 1;
			
			registrySegment.remove(); //REMOVE

			if(count === 1){
				segment.clear();
			} else {
				table.removeRow(rowNum);
				childVisible = count-1;
			}

			let cell = table.getCell(childVisible, 2);
			if(cell && cell.firstChild){
				cell.firstChild.visible = true;
			}
		});
		
		table.addCell(removeIcon);

		this.querySelectorAll(`[id*='segmentAdd']`).forEach(el=>{
			el.visible = false;
		});

		let addSegment = new AonIconButton();
		addSegment.id = "segmentAdd"+rowNum;
		addSegment.title = MSG.ADD+" Segmento";
		addSegment.icon = MATERIAL_ICONS.ADD_CIRCLE_OUTLINE;
		addSegment.visible = rowCount === i+1
		addSegment.addEventListener(EVENT.CLICK, () => {
			addSegment.visible = false;
			let registrySegment = new RegistrySegment();
			registrySegment.setRegistry(this.registry.getId());
			registrySegment.setDomain(this.registry.getDomain());
			this.registry.addRegistrySegment(registrySegment);
			this.buildSegment(table, registrySegment, table.getRowsCount());
		});

		table.addCell(addSegment);
	}

	async getSegments(){
		if(!this.segments.length){
			try {
				const resp = await getSegments({domainName:this.registry.getDomain().getName()});
				this.segments = resp.map(s => ({...s, value:s.id}));
			} catch (error) {
				this.showError(error);
			}
		}
		
		return this.segments;
	}

	buildWeb(table, web, i) {
		if(!web.isRemoved()){
			let rowNum = table.addRow();
	
			let aonInput = new AonInput();
			aonInput.id = this.WEB_INPUT + i;
			aonInput.description = MSG.WEB + ' ' + (this.webs.length > 1 ? i + 1 : '');
			aonInput.value = web.getValue();
	
			aonInput.addEventListener(EVENT.CHANGE, () => this.webs[i].setValue(aonInput.value));
	
			let addWeb = new AonIconButton();
			addWeb.id = this.WEB_ADD + i;
			addWeb.title = MSG.ADD;
			addWeb.icon = MATERIAL_ICONS.ADD_CIRCLE_OUTLINE;
			addWeb.visible = this.webs.length === i+1;
			addWeb.addEventListener(EVENT.CLICK, () => {
				let number = this.webs.length;
				let media = new Media()
					.setRegistry(this.registry.getId())
					.setMedia('WEB');
				this.webs.push(media);
				addWeb.visible = false;
				this.buildWeb(table, media, number);
			});
			let td = table.addCell(aonInput);
			td.style.width = '100%';
			table.addCell(addWeb);
			aonInput.addIconWithRemove(MATERIAL_ICONS.LINK, undefined, () => {
				if(table.getRowsCount() === 1) {
					aonInput.value = '';
					this.webs[i].setValue('');
				} else {
					let last = this.getElement(this.WEB_ADD + i).isVisible();
					this.webs[i].remove();
					table.removeRow(rowNum);
					if(last) {
						let j = 0;
						this.webs.forEach((item, h) => {
							if(!item.isRemoved())
								j = h;
						});
						this.getElement(this.WEB_ADD + j).visible = true;
					}
				}
			});
		}
	}
	
	// ACTIONS

	back() {

	}

	save() {

		let medias = this.emails.concat(this.phones).concat(this.webs);
		this.registry.setMedia(medias);

		saveRegistry(this.registry).then(registry => {
			this.registry.id = registry.id;
			this.showToast({
				type: 'success',
	 			message: 'Datos Guardados Correctamente'
	 		});
		}).catch(error => {
	 		this.showToast(error);
	 	});
	}

	audit(){
		let dialog = this.getApplication().getDialog();
		dialog.clear();
		dialog.setTitle(MSG.AUDIT);
		if(this.isMobile()) {
		  dialog.type = 'fullscreen';
		} else {
		  dialog.width = '400px';
		}

		let div = this.createElement(TAG.DIV);
		div.style = `
			display: flex;
			flex-direction: column;
			gap: 10px;
			margin-top: 16px;
		`;
		dialog.setContent(div);


		if(this.registry.getCreationUser && this.registry.getCreationUser() && this.registry.getCreationDate()){
			let divCreation = this.createElement(TAG.DIV);
			divCreation.innerHTML = `<b>${MSG.CREATED_BY}</b>: ${this.registry.getCreationUser()} (${ AonDateUtils.setDateTimestampDay(this.registry.getCreationDate())})`;
			div.appendChild(divCreation);
		}
	
		if(this.registry.getModificationUser && this.registry.getModificationUser() && this.registry.getModificationDate()){
			let divModification = this.createElement(TAG.DIV);
			divModification.innerHTML = `<b>${MSG.MODIFIED_BY}</b>: ${this.registry.getModificationUser()} (${ AonDateUtils.setDateTimestampDay(this.registry.getModificationDate())})`;
			div.appendChild(divModification);
		}

		dialog.addAcceptAction(() => {});
		dialog.open();
	}

	setRegistry(registry) {
		this.registry = new Registry(registry);
	}

	setShowLogo(showLogo) {
		this.showLogo = showLogo;
	}

	isCustomer() {
		return this.type && 'customer' === this.type;		
	}
}

if(!window.customElements.get(TAG.AON_REG)){
	window.customElements.define(TAG.AON_REG, AonReg);
}
