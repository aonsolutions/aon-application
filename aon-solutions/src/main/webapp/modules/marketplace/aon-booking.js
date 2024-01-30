import {AonElement} from '../../components/AonElement.js';
import {ConsultancyBookingApps, BookingApps, ClassicApps, ConsoleServices, Services, Packs, ENTERPRISE, BASIC_MANAGEMENT, STANDAR_MANAGEMENT,
	 PROFESSIONAL_MANAGEMENT, GARAGE, ACADEMY, HOTEL, OFFICE, COMMERCE, KIT_DIGITAL_ERP, KIT_DIGITAL_CRM, KIT_DIGITAL_FACE} from  '../../services/app.js';
import {getDomainUserRoles, getInvofoxConfiguration, saveInvofoxConfiguration, setDomainApp} from  '../../services/service.js';
import {DomainUserRoles} from '../../models/DomainUserRoles.js';
import {App, ToolbarType} from '../../models/enums.js';
import { AonToolbar } from '../../components/aon-toolbar.js';
import { AonSwitch } from '../../components/aon-switch.js';

import '../../components/aon-card.js';
import '../../components/aon-icon-button.js';
import '../../components/aon-icon.js';

import { CSS, MATERIAL_ICONS, MSG, TAG, EVENT } from '../../environments/environments.js'; 
import * as ACTION from '../actions.js';
import { AonInput } from '../../components/aon-input.js';
import { AonToast } from '../../components/aon-toast.js';
import { AonDialog } from '../../components/aon-dialog.js';
import { AonCheckbox } from '../../components/aon-checkbox.js';
import { AonSelect } from '../../components/aon-select.js';
import { AonIconButton } from '../../components/aon-icon-button.js';
import { AonBasicTable } from '../../components/aon-basic-table.js';
export class AonBooking extends AonElement {

	TOOLBAR;
	USER_NUMBER;
	APP;
	SAVE_DIALOG;
	EDIT_TYPE_DIALOG;
	CONFIGURATION_DIALOG;
	apps;
	users;
	definedUsers;
	dur;
	domainPayer;

	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
		getDomainUserRoles({reload: true}).then(r => {
			this.dur = new DomainUserRoles(r);
			this.domainPayer = this.dur.isDomainPayer();
			this.apps = this.dur.getDomainApps();
			if(!this.dur.getDomain().isConsultancy() && this.dur.getParentDomain().isConsultancy())
				this.completeDomainApps(this.dur);
			this.users = this.dur.maxDefinedUsers;
			this.definedUsers = this.dur.definedUsers;
			this.build(this.dur);
		});
	}

	completeDomainApps(dur) {
		if(dur.hasProfessionalManagement() && !this.apps.includes(App.PROFESSIONAL_MANAGEMENT)) {
		  this.apps.push(App.PROFESSIONAL_MANAGEMENT);
		} else if(dur.hasStandarManagement() && !this.apps.includes(App.STANDAR_MANAGEMENT)){
		  this.apps.push(App.STANDAR_MANAGEMENT);
		} else if(dur.hasBasicManagement() && !this.apps.includes(App.BASIC_MANAGEMENT)){
		  this.apps.push(App.BASIC_MANAGEMENT);
		}
	}

	initialize() {
		this.id = this.id || 'aonBooking';
		this.APP = this.id + 'App';
		this.TOOLBAR = this.id + 'Toolbar';
		this.USER_NUMBER = this.id + 'UserNumber';
		this.SAVE_DIALOG = this.id + 'SaveDialog';
		this.EDIT_TYPE_DIALOG = this.id + 'EditTypeDialog';
		this.CONFIGURATION_DIALOG = this.id + 'ConfigurationDialog';
		this.apps = [];
	}

	build(dur) {
		let toolbar = new AonToolbar();
		toolbar.id = this.TOOLBAR;
		toolbar.type = ToolbarType.SECONDARY;
		toolbar.title = 'CONTRATACIÓN';
		this.appendChild(toolbar);
		toolbar.addButton2(ACTION.SAVE, () => this.saveDialog());

		let content = this.createElement(TAG.DIV);
		content.className = CSS.AON_SUB_CONTENT;
		this.appendChild(content);

		this.buildUser(content);

		if(dur.getDomain().isConsultancy()){
			this.buildTitle(content, 'Packs');
			this.buildApps(content, Packs, dur);
		} else {
			this.buildTitle(content,  this.dur.getDomain().isKitDigital() 
				? 'Kit Digital' : 'Gestión');
			this.buildApps(content, this.getGestionPacks(), dur);
		}

		if(!this.dur.getDomain().isKitDigital()) {
			
			if(dur.getDomain().isConsultancy()) {
				this.buildTitle(content, MSG.APPLICATIONS);
				this.buildApps(content, ConsultancyBookingApps, dur);
			} else if(!this.dur.isEnterpriseChild()) {
				this.buildTitle(content, MSG.APPLICATIONS);
				this.buildApps(content, BookingApps, dur);
			}
			this.buildTitle(content, MSG.SERVICES);
			if(this.isConsole() || this.isBeta()) 
				this.buildApps(content, ConsoleServices, dur);
			else this.buildApps(content, Services, dur);
	
			this.buildTitle(content, MSG.CLASSIC_APPLICATIONS);
			this.buildApps(content, ClassicApps, dur);
		}
	}
	
	getGestionPacks(){
		if(this.dur.getDomain().isGarage())
			return this.dur.isEnterpriseChild()
				? {GARAGE}
				: {GARAGE, BASIC_MANAGEMENT, STANDAR_MANAGEMENT, PROFESSIONAL_MANAGEMENT};
		else if(this.dur.getDomain().isHotel())
			return this.dur.isEnterpriseChild()
				? {HOTEL}
				: {HOTEL, BASIC_MANAGEMENT, STANDAR_MANAGEMENT, PROFESSIONAL_MANAGEMENT};
		else if(this.dur.getDomain().isAcademy())
			return this.dur.isEnterpriseChild()
				? {ACADEMY}
				: {ACADEMY, BASIC_MANAGEMENT, STANDAR_MANAGEMENT, PROFESSIONAL_MANAGEMENT};
		else if(this.dur.getDomain().isCommerce())
			return this.dur.isEnterpriseChild()
				? {COMMERCE}
				: {COMMERCE, BASIC_MANAGEMENT, STANDAR_MANAGEMENT, PROFESSIONAL_MANAGEMENT};
		else if(this.dur.getDomain().isOffice())
			return this.dur.isEnterpriseChild()
				? {OFFICE}
				: {OFFICE, BASIC_MANAGEMENT, STANDAR_MANAGEMENT, PROFESSIONAL_MANAGEMENT};
		else if(this.dur.getDomain().isKitDigital()) 
			return {KIT_DIGITAL_FACE, KIT_DIGITAL_CRM, KIT_DIGITAL_ERP}
		else return this.dur.isEnterpriseChild()
				? {ENTERPRISE}
				:{ENTERPRISE, BASIC_MANAGEMENT, STANDAR_MANAGEMENT, PROFESSIONAL_MANAGEMENT} ;

	}

	getTypeName(){
		if(this.dur.getDomain().isGarage())
			return 'Taller';
		else if(this.dur.getDomain().isHotel())
			return 'Hotel';
		else if(this.dur.getDomain().isAcademy())
			return 'Academia';
		else if(this.dur.getDomain().isCommerce())
			return 'Comercio';
		else if(this.dur.getDomain().isOffice())
			return 'Despacho';
		else if(this.dur.getDomain().isKitDigital()) 
			return 'Kit Digital'
		else return 'Empresa';

	}

	buildUser(content) {
		this.buildTitle(content, this.dur.getDomain().isDomainManagement() && this.isBeta()
			? 'USUARIOS Y EMPRESAS' : MSG.USERS);

		let div = this.createElement(TAG.DIV); 
		div.style.display = 'flex';
		content.appendChild(div);

		let div1 = this.createElement(TAG.DIV); 
		div1.style.marginLeft = '60px';
		div1.style.width = '250px';
		div.appendChild(div1);

		let users = new AonInput();
		users.id = 'users1';
		users.description = 'Usuarios' + ' (Activos: '+ this.definedUsers+ ')';
		users.value = this.users;
		div1.appendChild(users);
		users.onChange(r => this.users = users.value);
		users.addIconWithRemove(MATERIAL_ICONS.PERSON, undefined, () => users.value = '0');

		if(this.dur.getDomain().isDomainManagement() && this.isBeta()){
			let div2 = this.createElement(TAG.DIV); 
			div2.style.marginLeft = '10px';
			div2.style.width = '250px';
			div.appendChild(div2);

			let companies = new AonSelect();
			companies.id = 'companies';
			companies.value = -1;
			companies.title = 'Empresas';
			companies.setOptions([
				{name: '10 Empresas', value: 10},
				{name: '15 Empresas', value: 15},
				{name: 'Ilimitadas', value: -1}
			]);
			div2.appendChild(companies);
		}
	} 

	buildApps(content, apps, dur) {
		let ul = document.createElement(TAG.UL);
		ul.classList.add(CSS.AON_UL);
		ul.classList.add(CSS.AON_LIST_GROUP_TOP);
		ul.style.marginLeft = '60px';
		ul.style.marginRight = '60px';
		content.appendChild(ul);
		for (let key in apps){
			const app = apps[key];
			let contratado = this.hasApp(dur, app.app.toUpperCase());
			let li = document.createElement('li');
		  	li.classList.add(CSS.AON_LIST_GROUP_ITEM);
		    li.classList.add(CSS.AON_APP_LI);
			let span = document.createElement('span');
			span.style.margin = '20px';

			if(app.icon) {
				let color = contratado || app.app.includes('pack') || app.domainType ? app.color : 'lightgray';
				span.innerHTML = `<aon-icon id="${this.APP + app.app + 'Icon'}" icon="${app.icon}" color="${color}" size="30px"></aon-icon>`;
			} else {
				let img = document.createElement('img');
				img.style.width = '30px';
				img.src = app.logo;
				span.appendChild(img);
			}

			let span2 = document.createElement('span');
			span2.className = 'aonAppTitle';
			span2.innerHTML = app.title;
			span.appendChild(span2);
			if(app.domainType && this.dur.isConsoleUser()) {
				span2.style.cursor = 'pointer';
				span2.addEventListener(EVENT.CLICK, () => this.editTypeDialog());
			}

			if(app.subtitle){
				let span3 = document.createElement('span');
				span3.innerHTML = app.subtitle;
				span3.style.color = 'gray';
				span3.style.fontWeight = '400';
				span2.appendChild(span3);
			}

			let buttons = document.createElement('span');
			buttons.id = this.APP + app.app + 'Buttons';
			buttons.style.position = 'absolute';
			buttons.style.right = '10px';
			buttons.style.top = '20px';

			let price = document.createElement('span');
			price.id = this.APP + app.app + 'Price';
			price.style.margin = '10px';
			price.style.color = 'gray';
			price.innerHTML = app.price;
			buttons.appendChild(price);

			if(app.configuration) {
				let configuration = new AonIconButton();
				configuration.id = this.APP + app.app + 'Configuration';
				configuration.icon = MATERIAL_ICONS.SETTINGS;
				configuration.style.position = 'absolute';
				configuration.style.right = '150px'
				configuration.style.top = '-10px'
				configuration.addEventListener(EVENT.CLICK, () => this.configurationDialog(app.app));
				buttons.appendChild(configuration);
			}

			// let parentContract = document.createElement('span');
			// parentContract.id = this.APP + app.app + 'ParentContract';
			// parentContract.style.color = '#002469';
			// parentContract.style.opacity = '0.5';
			// parentContract.innerHTML = 'Contratado en el entorno';
			// parentContract.style.display = 'none';
			// buttons.appendChild(parentContract);

			let contract = new AonSwitch();
			contract.id = this.APP + app.app + 'Contract';

			if(!this.dur.getDomain().isConsultancy()){
				let hasParentApp = this.hasParentApp(dur, app.app.toUpperCase());
				let hasPack = this.hasPack(dur, app.app.toUpperCase());
				let activateLabel =  hasParentApp || hasPack ? 'Activar' : 'Contratar';
				let deactivateLabel = hasParentApp || hasPack ? 'Desactivar' : 'Descontratar'; 
				contract.title = contratado ? deactivateLabel : activateLabel;
			} else contract.title = contratado ? 'Descontratar' : 'Contratar';

			contract.checked = contratado;
			buttons.appendChild(contract);

			// if(this.hasParentApp(dur, app.app.toUpperCase())) {
			// 	contract.style.display = 'none';
			// 	parentContract.style.display = 'inline';
			// } else 
			if(this.isDisabled(dur, app.app.toUpperCase()) ||  app.disabled){
				contract.style.display = 'none';
				let pack = this.getPack(dur, app.app.toUpperCase());
				if(pack) {
					let message = document.createElement('span');
					message.id = this.APP + app.app + 'Text';
					message.style.color = '#002469';
					message.style.opacity = '0.5';
					buttons.appendChild(message);
					message.innerHTML = 'Incluido en ' + pack.title;
					message.style.display = 'inline';
				}
			}

			contract.addEventListener(EVENT.CHANGE, (e) => {
				e.preventDefault();
				e.stopPropagation();
				this.activate(app, contract.isChecked());
			});

			if(!app.domainType)
				span.appendChild(buttons);
			else if (this.dur.isConsoleUser()){
				let domainPayer = new AonSwitch();
				domainPayer.id = this.APP + app.app + 'DomainPayment';
				domainPayer.title = 'Dominio Pagador';
				domainPayer.checked = this.domainPayer;

				domainPayer.addEventListener(EVENT.CHANGE, (e) => {
					e.preventDefault();
					e.stopPropagation();
					this.domainPayer = domainPayer.checked;
				});

				this.clearElement(buttons);
				buttons.appendChild(domainPayer);

				span.appendChild(buttons);
			}
			li.appendChild(span);
			ul.appendChild(li);
		}
	}

	activate(app, contract, text){
		let contractIcon = this.getElement(this.APP + app.app + 'Icon');
		if(contractIcon)
			contractIcon.color = contract || app.app.includes('pack') ? app.color : 'lightgray';
		let contractSwitch = this.getElement(this.APP + app.app + 'Contract');
		if(contractSwitch) contractSwitch.checked = contract;

		if(contract) {
			if(!this.apps.includes(app.app.toUpperCase()))
				this.apps.push(app.app.toUpperCase());
		} else if(this.apps.includes(app.app.toUpperCase())) {
			this.apps.forEach((r, i) => {
				if(r === app.app.toUpperCase())
					this.apps.splice(i, 1);
			});
		}

		if(contractSwitch) {
			if(!this.dur.getDomain().isConsultancy()){
				let hasParentApp = this.hasParentApp(this.dur, app.app.toUpperCase());
				let hasPack = this.hasPack(this.dur, app.app.toUpperCase());
				let activateLabel =  hasParentApp || hasPack ? 'Activar' : 'Contratar';
				let deactivateLabel = hasParentApp || hasPack ? 'Desactivar' : 'Descontratar'; 
				contractSwitch.title = contract ? deactivateLabel : activateLabel;
			} else contractSwitch.title = contract ? 'Descontratar' : 'Contratar';

			if((contract && text)){
				contractSwitch.style.display = 'none';
				let message = this.getElement(this.APP + app.app + 'Text');
				if(!message) {
					message = document.createElement('span');
					message.id = this.APP + app.app + 'Text';
					message.style.color = '#002469';
					message.style.opacity = '0.5';
					this.getElement(this.APP + app.app + 'Buttons').appendChild(message);
				}
				message.innerHTML = text;
				message.style.display = 'inline';
			} else {
				contractSwitch.style.display = 'inline';
				let message = this.getElement(this.APP + app.app + 'Text');
				if(message) {
					message.style.display = 'none';
				}
			}
			
			if(app.disabled || this.isDisabled2(app.app.toUpperCase())) {
				contractSwitch.style.display = 'none';
			}
		}

		if(app.app === Packs.SUITE.app) {
			this.includePack(Packs.PORTAL, app, contract);
			this.includePack(Packs.PAYROLL, app, contract);
			this.includePack(Packs.FISCAL_ACCOUNTING, app, contract);
		}

		if(app.app === STANDAR_MANAGEMENT.app) {
			this.includePack(BASIC_MANAGEMENT, app, contract);
		}

		if(app.app === PROFESSIONAL_MANAGEMENT.app) {
			this.includePack(BASIC_MANAGEMENT, app, contract);
			this.includePack(STANDAR_MANAGEMENT, app, contract);
		
		}

		if(app.apps) {
			for (let i = 0; i < app.apps.length ; i++) {
				// REVISAR
				// if(!this.hasParentApp(this.dur, app.apps[i].app.toUpperCase()))
					this.activate(app.apps[i], contract, this.dur.getDomain().isConsultancy() ? 'Incluido en ' + app.title : undefined);
			}
		}
	}


	includePack(pack, includePack, contract) {

		let contractIcon = this.getElement(this.APP + pack.app + 'Icon');
		if(contractIcon)
			contractIcon.color = contract ? pack.color : 'lightgray';

		if(this.apps.includes(pack.app.toUpperCase())) {
			this.apps.forEach((r, i) => {
				if(r === pack.app.toUpperCase())
					this.apps.splice(i, 1);
			});
		}

		let contractSwitch = this.getElement(this.APP + pack.app + 'Contract');

		if(contractSwitch) {
			let hasParentApp = this.hasParentApp(this.dur, pack.app.toUpperCase());
			let hasPack = this.hasPack(this.dur, pack.app.toUpperCase());
			let activateLabel =  hasParentApp || hasPack ? 'Activar' : 'Contratar';
			let deactivateLabel = hasParentApp || hasPack ? 'Desactivar' : 'Descontratar'; 
			contractSwitch.title = contract ? deactivateLabel : activateLabel;

			contractSwitch.checked = contract;
			if((contract)){
				contractSwitch.style.display = 'none';
				let message = this.getElement(this.APP + pack.app + 'Text');
				if(!message) {
					message = document.createElement('span');
					message.id = this.APP + pack.app + 'Text';
					message.style.color = '#002469';
					message.style.opacity = '0.5';
					this.getElement(this.APP + pack.app + 'Buttons').appendChild(message);
				}
				message.innerHTML = 'Incluido en ' + includePack.title;
				message.style.display = 'inline';
			} else {
				contractSwitch.style.display = 'inline';
				let message = this.getElement(this.APP + pack.app + 'Text');
				if(message) {
					message.style.display = 'none';
				}
			}
		}
	}

	saveDialog() {
		let dialog = this.getElement(this.SAVE_DIALOG);
		if(!dialog){
			dialog = new AonDialog();
			dialog.id = this.SAVE_DIALOG;
			this.appendChild(dialog);
		}

		let div = this.createElement(TAG.DIV);
		
		let span = this.createElement(TAG.SPAN);
		span.style.fontWeight = 'bold';
		span.style.color = 'red';
		span.innerHTML= 'Los cambios realizados pueden suponer variaciones en la facturación.';
		div.appendChild(span);
		
		let table = new AonBasicTable();
		table.style.top = '20px';
		table.style.position = 'relative'; 
		div.appendChild(table);

		dialog.clear();
		dialog.setTitle(MSG.BOOKING);
		dialog.setContent(div);
		dialog.addAcceptAction(() => this.save());
		dialog.open();
		dialog.getButtonAccept().disabled = true;

		table.addRow();

		let checkBox = new AonCheckbox();

		let td = table.addCell(checkBox)
		td.style.width = '15px';
		let span3 = this.createElement(TAG.SPAN);
		span3.innerHTML = 'He leido las <a target="_blank" class="aonLink" href="http://aonsolutions.es/docs/aon_condiciones_generales_del_contrato.pdf">condiciones de servicio</a> y estoy de acuerdo con las mismas'
		table.addCell(span3);

		checkBox.addEventListener(EVENT.CHANGE, () => {
			dialog.getButtonAccept().disabled = !checkBox.isChecked();
		});
	}

	editTypeDialog() {
		let types = [{
			name: 'Empresa',
			value: 'ENTERPRISE',
		}, {
			name: 'Garaje',
			value: 'GARAGE',
		}, {
			name: 'Academia',
			value: 'ACADEMY',
		}, {
			name: 'Hotel',
			value: 'HOTEL',
		}, {
			name: 'Despacho',
			value: 'OFFICE',
		}, {
			name: 'Genérico',
			value: 'GENERIC',
		}, {
			name: 'Comercio',
			value: 'Commerce',
		}];

		let type = new AonSelect();
		type.id = this.TYPE;
		type.title = MSG.TYPE;
		type.setOptions(types);
		type.value = this.getDur().getDomain().domainType;

		let dialog = this.getElement(this.EDIT_TYPE_DIALOG);
		if(!dialog){
			dialog = new AonDialog();
			dialog.id = this.EDIT_TYPE_DIALOG;
			this.appendChild(dialog);
		}
		dialog.clear();
		dialog.setTitle(MSG.CHANGE_TYPE);
		dialog.setContent(type);
		dialog.addAcceptAction(() => this.changeType(type.value));
		dialog.open();
	}

	changeType(val) {
		this.dur.domain.type = val;
	}

	save() {
		let tID = this.id + 'Toast';
		let toast = this.getElement(tID);
		if(!toast){
			toast = new AonToast(); 
			toast.id = tID;
			this.appendChild(toast);
		}

		if(this.definedUsers > this.users) {
			toast.start({
				type: 'error',
				message: 'El número de usuarios no puede ser mayor que el número de usuarios contratados'
			});
		} else {
			setDomainApp({
				type: this.dur.domain.type,
				apps: this.apps,
				users: this.users,
				domainPayer: this.domainPayer
			}).then(() => {
				toast.start({
					type: 'success',
					message: 'Datos Guardados Correctamente'
				});
				getDomainUserRoles({reload:true}).then(r => {
					//this.build(new DomainUserRoles(r));
				});
			});
	
		}
	}

	buildTitle(content, title) {
		let div = document.createElement('div');
		div.style.color = 'gray';
		div.style.paddingTop = '20px';
		div.style.paddingBottom = '20px';
		div.style.marginLeft = '60px';
		div.innerHTML = title.toUpperCase();
		content.appendChild(div);
	}

	hasApp(dur, app) {
		if(App.ACCOUNTING === app)
			return dur.hasAccounting();
		else if(App.FISCAL === app)
			return dur.hasFiscal();
		else if(App.PAYROLL === app)
			return dur.hasPayroll();
		else if(App.COMUNICA === app)
			return dur.hasComunica();
		else if(App.DOCUMENTAL === app)
			return dur.hasDocumental();
		else if(App.TIMECONTROL === app)
			return dur.hasTimeControl();
		else if(App.INVOICE === app)
			return dur.hasInvoice();
		else if(App.MESSENGER === app)
			return dur.hasMessenger();
		else if(App.PACK_SUITE === app)
			return dur.hasPackSuite();
		else if(App.PACK_PORTAL === app)
			return dur.hasPackPortal();
		else if(App.PACK_PAYROLL === app)
			return dur.hasPackPayroll();
		else if(App.PACK_FISCAL_ACCOUNTING === app)
			return dur.hasPackFiscalAccounting();
		else if(App.AIO === app)
			return dur.hasAon();
		else if(App.BASIC_MANAGEMENT === app)
			return dur.hasBasicManagement() || dur.hasStandarManagement() || dur.hasProfessionalManagement();
		else if(App.STANDAR_MANAGEMENT === app)
			return dur.hasStandarManagement() || dur.hasProfessionalManagement();
		else if(App.PROFESSIONAL_MANAGEMENT === app)
			return dur.hasProfessionalManagement();
		else if(App.WAREHOUSE === app)
			return dur.hasWarehouse();
		else if(App.MARKETING === app)
			return dur.hasMarketing();
		else if(App.TREASURY === app)
			return dur.hasTreasury();
		else if(App.GROUPWARE === app)
			return dur.hasGroupware();
		else if(App.COMMERCIAL === app)
			return dur.hasCommercial();
		else return dur.hasApp(app);
	}

	hasParentApp(dur, app) {
		if(App.ACCOUNTING === app){
			return dur.hasParentAccounting();
		} else if(App.FISCAL === app)
			return dur.hasParentFiscal();
		else if(App.PAYROLL === app)
			return dur.hasParentPayroll();
		else if(App.COMUNICA === app)
			return dur.hasParentComunica();
		else if(App.DOCUMENTAL === app)
			return dur.hasParentDocumental();
		else if(App.TIMECONTROL === app)
			return dur.hasParentTimeControl();
		else if(App.INVOICE === app)
			return dur.hasParentInvoice();
		else if(App.MESSENGER === app)
			return dur.hasParentMessenger();
		else if(App.PACK_SUITE === app)
			return dur.hasParentPackSuite();
		else if(App.PACK_PORTAL === app)
			return dur.hasParentPackPortal();
		else if(App.PACK_PAYROLL === app)
			return dur.hasParentPackPayroll();
		else if(App.PACK_FISCAL_ACCOUNTING === app)
			return dur.hasParentPackFiscalAccounting();
		else if(App.AIO === app)
			return dur.hasParentAon();
		else if(App.BASIC_MANAGEMENT === app) {
			return dur.hasParentBasicManagement();
		} else if(App.STANDAR_MANAGEMENT === app) {
			return dur.hasParentStandarManagement();
		} else if(App.PROFESSIONAL_MANAGEMENT === app) {
			return dur.hasParentProfessionalManagement();
		} else if(App.WAREHOUSE === app) {
			return dur.hasParentWarehouse();
		} else if(App.TREASURY === app) {
			return dur.hasParentTreasury();
		} else if(App.GROUPWARE === app) {
			return dur.hasParentGroupware();
		} else if(App.MARKETING === app) {
			return dur.hasParentMarketing();
		} else if(App.COMMERCIAL === app) {
			return dur.hasParentCommercial();
		} else return dur.hasParentApp(app);

		// return false;
	}

	isDisabled(dur, app){
		if(App.ACCOUNTING === app)
			return dur.hasPackSuite() || dur.hasPackFiscalAccounting();
		else if(App.FISCAL === app)
			return dur.hasPackSuite() || dur.hasPackFiscalAccounting();
		else if(App.PAYROLL === app)
			return dur.hasPackSuite() || dur.hasPackPayroll();
		else if(App.COMUNICA === app)
			return dur.hasPackSuite() || dur.hasPackPayroll();
		else if(App.DOCUMENTAL === app)
			return dur.hasPackSuite() || dur.hasPackPortal();
		else if(App.TIMECONTROL === app)
			return dur.hasPackSuite() || dur.hasPackPortal();
		else if(App.INVOICE === app)
			return dur.hasPackSuite() || dur.hasPackPortal() || 
				(!dur.hasBasicManagement() && !dur.hasParentBasicManagement()
				&& !dur.hasStandarManagement() && !dur.hasParentStandarManagement()
				&& !dur.hasProfessionalManagement() && !dur.hasParentProfessionalManagement()
				&& !dur.hasParentInvoice());
		else if(App.MESSENGER === app)
			return dur.hasPackSuite() || dur.hasPackPortal();
		else if(App.PACK_PORTAL === app)
			return dur.hasPackSuite();
		else if(App.PACK_PAYROLL === app)
			return dur.hasPackSuite();
		else if(App.PACK_FISCAL_ACCOUNTING === app)
			return dur.hasPackSuite();
		else if(App.AIO === app)
			return false;
		else if(App.BANK === app) {
			return false;
		} else if(App.TREASURY === app) {
			return !dur.hasStandarManagement() && !dur.hasParentStandarManagement()
				&& !dur.hasProfessionalManagement() && !dur.hasParentProfessionalManagement();
		}  else if(App.COMMERCIAL === app) {
			return !dur.hasBasicManagement() && !dur.hasParentBasicManagement()
				&& !dur.hasStandarManagement() && !dur.hasParentStandarManagement()
				&& !dur.hasProfessionalManagement() && !dur.hasParentProfessionalManagement();
		} else if(App.WAREHOUSE === app) {
			return !dur.hasProfessionalManagement() && !dur.hasParentProfessionalManagement();
		} else if(App.GROUPWARE === app) {
			return !dur.hasProfessionalManagement() && !dur.hasParentProfessionalManagement();
		} else if(App.MARKETING === app) {
			return !dur.hasStandarManagement() && !dur.hasParentStandarManagement()
				&& !dur.hasProfessionalManagement() && !dur.hasParentProfessionalManagement();
		} else if(App.BASIC_MANAGEMENT === app) {
			return dur.hasStandarManagement() || dur.hasProfessionalManagement();
		} else if(App.STANDAR_MANAGEMENT === app) {
			return dur.hasProfessionalManagement();
		} else return false;
	}

	isDisabled2(app) {
		if(App.INVOICE === app){
			return !this.dur.getDomain().isConsultancy() && (!this.apps.includes(App.BASIC_MANAGEMENT) 
			&& !this.apps.includes(App.STANDAR_MANAGEMENT) && !this.apps.includes(App.PROFESSIONAL_MANAGEMENT)
			&& !this.dur.hasParentInvoice());
		} else if(App.TREASURY === app) {
			return !this.apps.includes(App.STANDAR_MANAGEMENT) && !this.apps.includes(App.PROFESSIONAL_MANAGEMENT);
		}  else if(App.COMMERCIAL === app) {
			return !this.apps.includes(App.BASIC_MANAGEMENT) && !this.apps.includes(App.STANDAR_MANAGEMENT) && !this.apps.includes(App.PROFESSIONAL_MANAGEMENT);
		} else if(App.WAREHOUSE === app) {
			return !this.apps.includes(App.PROFESSIONAL_MANAGEMENT);
		} else if(App.GROUPWARE === app) {
			return !this.apps.includes(App.PROFESSIONAL_MANAGEMENT);
		} else if(App.MARKETING === app) {
			return !this.apps.includes(App.STANDAR_MANAGEMENT) && !this.apps.includes(App.PROFESSIONAL_MANAGEMENT);
		} else return false;	
	}

	getPack(dur, app){
		if(dur.hasPackSuite() && (App.DOCUMENTAL === app || App.TIMECONTROL === app
				|| App.INVOICE === app || App.MESSENGER === app || App.ACCOUNTING === app 
				|| App.FISCAL === app) || App.PAYROLL === app || App.COMUNICA === app
				|| App.PACK_PORTAL === app || App.PACK_FISCAL_ACCOUNTING === app
				|| App.PACK_PAYROLL === app) {
			return Packs.SUITE;
		} else if(dur.hasPackFiscalAccounting() && (App.ACCOUNTING === app || App.FISCAL === app)){
			return Packs.FISCAL_ACCOUNTING;
		} else if(  dur.hasPackPortal() && (App.DOCUMENTAL === app || App.TIMECONTROL === app
				|| App.INVOICE === app || App.MESSENGER === app)) {
			return Packs.PORTAL;
		} else if(  dur.hasPackPayroll() && (App.PAYROLL === app || App.COMUNICA === app
				|| App.TIMECONTROL === app)) {
			return Packs.PORTAL;
		} else if(dur.hasStandarManagement() && App.BASIC_MANAGEMENT === app) {
			return STANDAR_MANAGEMENT;
		} else if(dur.hasProfessionalManagement() && (App.BASIC_MANAGEMENT === app || App.STANDAR_MANAGEMENT === app)) {
			return PROFESSIONAL_MANAGEMENT;
		}
		return undefined;
	}

	hasPack(dur, app) {
		return (dur.hasBasicManagement() && (App.INVOICE === app || App.COMMERCIAL === app)) 
			|| (dur.hasStandarManagement() && (App.INVOICE === app || App.COMMERCIAL === app
				|| App.TREASURY === app || App.MARKETING === app )) 
			|| (dur.hasProfessionalManagement() && (App.INVOICE === app || App.COMMERCIAL === app
				|| App.TREASURY === app || App.MARKETING === app
				|| App.WAREHOUSE === app || App.GROUPWARE === app));
	}

	configurationDialog(app) {
		if(App.INVOFOX === app.toUpperCase()){

			getInvofoxConfiguration({}).then(invofoxConfiguration => {
				let dialog = this.getElement(this.CONFIGURATION_DIALOG);
				if(!dialog){
					dialog = new AonDialog();
					dialog.id = this.CONFIGURATION_DIALOG;
					this.appendChild(dialog);
				}
				let div = this.createDiv();
				dialog.clear();
				dialog.setTitle("Configuración de Invofox");
				dialog.setContent(div);
			
				let test = new AonSwitch()
				test.id = this.APP + app + 'Test';
				test.title = 'Entorno de Pruebas';
				div.appendChild(test);

				test.checked = invofoxConfiguration.test;

				dialog.addAcceptAction(() => saveInvofoxConfiguration({test: test.checked})
				.then(() => {}));
				dialog.open();
			});
		}
	}
}
if(!window.customElements.get('aon-booking')){
	window.customElements.define('aon-booking', AonBooking);
}
