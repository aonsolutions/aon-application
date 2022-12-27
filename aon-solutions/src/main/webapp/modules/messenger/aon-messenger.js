import { AonApplication } from '../../components/aon-application.js';
import { AonElement } from '../../components/AonElement.js';
import { CONSTANT, EVENT, MATERIAL_ICONS, MSG } from '../../environments/environments.js';
import {Apps, getAppsByDur} from '../../services/app.js';
import {getWorkgroups} from '../../services/workgroupService.js';
import { AonMessengerChat } from './aon-messeger-chat.js';
import { AonMessengerList } from './aon-messenger-list.js';
import { APP_PARAMS_REQUEST, MessengerOptions, MessengerSidenav, MESSENGER_VIEWS, TAG_TYPE, TASK_FILTER, TASK_SOURCE, TASK_STATUS } from './MessengerEnums.js';
import { getTaskHolder, getTastHolders } from '../../services/taskHolderService.js';
import { getTaskStatusCount, getTaskGeneralCount, getTaskOne, getCauInfo, getTaskCount, getTaskTags, saveTaskTag, deleteTaskTag, getTaskExcel } from '../../services/taskService.js';
import { getApplicationParametersIsSig } from '../../services/applicationParameterService.js';
import { AonInput } from '../../components/aon-input.js';
import { getDomainUserRoles } from '../../services/companyService.js';
import { DomainUserRoles } from '../../models/DomainUserRoles.js';
import { SigninSidenav } from '../timecontrol/signinEnums.js';
import { removeEmpty, sortBy, waitEl } from '../../services/utils.js';
import { getNotificationByDomain, markReadNotification } from '../../services/notificationService.js';
import { getCustomers } from '../../services/registryService.js';
import * as GWT from '../../gwt/gwt.js';
import { TaskUtils } from './utils/TaskUtils.js';
import { getAuth } from '../../services/authService.js';

export class AonMessenger extends AonElement {
    AON_MESSENGER;
	_listFilter;
	TASK_HOLDER;
	TASK_HOLDER_ENTERPRISE;
	cau; //BOOLEAN
	cauInfo;
	dur;
	TIMEOUT;
	_workgroups;
	_tags;
	APP_PARAMS=[];
	_filter={};

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	get type() {
		return this.getAttribute(CONSTANT.TYPE);
	}

	set type(type) {
		this.setAttribute(CONSTANT.TYPE, type);
	}

	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
		getDomainUserRoles({}).then(r => {
			this.dur = new DomainUserRoles(r);
			this.build();
		});
	}

	disconnectedCallback(){
		localStorage.removeItem("taskCau");
	}

	initialize(){
		// const period = TaskUtils.getPeriodMessenger("last_3_months");

		this.id = MESSENGER_VIEWS.AON_MESSENGER;
		this.AON_MESSENGER = MESSENGER_VIEWS.AON_MESSENGER;
		this._workgroups = [];
		this._tags = [];
		this.TASK_HOLDER = {};
		this.TASK_HOLDER_ENTERPRISE = [];
		this._filter = {
			workgroup: undefined,
			workgroups: undefined,
			task_holder: this._filter.task_holder || undefined,
			sender: this._filter.sender || undefined,
			source: this._filter.source || undefined,
			status: TASK_STATUS.PENDING,
			email: undefined,
			search: undefined,
			page:0, 
			perPage:30,
			// period: period.value,
			// startDate: period.startDate,
			// endDate: period.endDate
		};
				
		if(this.type && this.type === TASK_SOURCE.CAU) {
			this.cau = 1;
			this._filter.source = TASK_SOURCE.CAU;
		}
		
	}

	build() {
		
		let title = this.cau ? MSG.SUPPORT + ' / CAU' : MSG.REQUESTS;

		this.createApplication(this.AON_MESSENGER, title, new AonApplication());

		
		this.isTaskHolder().then(async (exist) => {
			if(exist){
				localStorage.setItem("taskCau", this.cau ? 1 : 0);

				this.cauInfo = await getCauInfo();
				this.cauInfo.auth = await getAuth();

				if(!this.cau){
					await this.getMyWorkgroups();
				}

				const email = cauInfo.auth.email;

				if(!email && this.cau){
					this.showError({message:"Auth inexistente", type:CONSTANT.ERROR});
				} else {
					this.buildToolbar();

					let promisesLoad = [];

					promisesLoad.push(this.loadTag(TAG_TYPE.TASK_LABEL));

                    if(!this.cau){
						promisesLoad.push(this.loadTag(TAG_TYPE.TASK_TYPE));
						promisesLoad.push(this.loadWorkgroup());
                    } else {
                        this._filter.email = email;
                        this.addListFilter(this._filter);
                    }

                    this.init();	

					Promise.all(promisesLoad).then(()=>{
						this.updateCount();
					});

				}
			}
		});		

		if(!this.cau){
			this.getAppParams(); 
		}
	}
	
	init(){
		if(this.data){
			this.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, this.data);
		} else if(this.cau){
			this.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, this._filter);
		} else if(this.value){
			getTaskOne({id:this.value}).then(task=>this.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, task)).catch(e=>this.showError(e));
		} else if(this.TASK_HOLDER && this.TASK_HOLDER.id){
			if(!this._filter.sender) {
				this._filter.task_holder = this.TASK_HOLDER.id;
			}

			this._filter.workgroups = this.getWorkgroupsStr();
			this.addListFilter(this._filter);
			this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined,  this._filter);
		} else {
			this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined,  this._filter);
		}
		
		this.addBackgroundSidenav(this._filter);
	}


	async isTaskHolder(){
		this.TASK_HOLDER = await getTaskHolder({reload:false}).catch(e=>null);
		if(!this.cau && !(this.TASK_HOLDER && this.TASK_HOLDER.id)){
			this.showError({message:"Operario inexistente", type:CONSTANT.ERROR});
			return false;
		}
		return true;
	}

	async buildToolbar(){
		const application = this.getApplication();
		if(this.isMobile()){
			application.addMobileSidenavHeader(Apps.MESSENGER);
			application.addFloatOption(SigninSidenav.ADD, () => 
				this.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, {source:TASK_SOURCE.QUERY})
			);
		} else {
			application.addToolbarOption2(SigninSidenav.ADD, () =>
				this.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, {source:TASK_SOURCE.QUERY})
			);

			if(!this.cau){
				application.addToolbarOption2({...SigninSidenav.SYNCHRONIZE, name:MSG.UPDATE}, () => this.rootPanel(new AonMessenger()) );

				if(this.getDur().isMessengerManager() || this.isLocal()){
					application.addToolbarOption2(SigninSidenav.EXCEL, () => this.getExcel());

					application.addToolbarOption2(MessengerSidenav.GRAPHIC, () =>
						this.showView(MESSENGER_VIEWS.AON_MESSENGER_GRAPHIC)
					);
				}
			}
		}

		this.buildToolbarSearch();
			
		if(this.cau){
			this.inboxNavBarCau();
		} else {
			this.inboxNavBar();
		}
		
		this.statusNavBar();
		
		if(!this.cau){
			this.groupNavBar();
		}

		this.tagNavBar();
	}

	buildToolbarSearch(){
		const opened = !this.isMobile();

		let btnSearch = this.getApplication().addSearchOption(opened);
		let timeOut = null;
		
		btnSearch.addEventListener(EVENT.SEARCH_NEW, ({detail}) => {
		  clearTimeout(timeOut);
		  
		  timeOut = setTimeout(() => {
			this._filter.search = detail.search;
			this.setListFilter({
				...this.getListFilter(), 
				page:0, 
				perPage:30, 
				search: detail.search,
				registry: detail.registry,
				searchtask_holder:detail.searchtask_holder, 
				searchsender:detail.searchsender,
				period: detail.period,
				startDate: detail.startDate,
				endDate: detail.endDate
			  });
			this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined, this.getListFilter());
		  }, 300);
		});
	
		if(this.cau){
			btnSearch.removeButtonAvanced();
		} else {
			btnSearch.buildOptionsFilter(TASK_FILTER);//INPUTS
			this.searchValueDefault();
		}
	}


	searchValueDefault(){

		let registryEl = this.getElement("registry");
		getCustomers({reload:true, page:1, perPage:50}).then(customers=>{
		  registryEl.setOptions(customers.map(c=> ({...c, value: c.id})) );
		});

		let timeOut = null;
		registryEl.addEventListener(EVENT.INPUT, async({target})=>{
			clearTimeout(timeOut);
			const value = target.value;
			if(value.length > 2){
				timeOut = setTimeout(async() =>{
					const cs = await getCustomers({reload:true, page:1, perPage:30, value});
					registryEl.setOptions( cs.map( c=> ({...c, value: c.id}) ) );
				}, 300);
			}
		});

		let taskHolderEl = this.getElement("task_holder");
		let senderEl = this.getElement("senderFilter");
		this.getTaskHoldersEnterprise()
		.then(ths=>{
			senderEl.setOptions(ths);
			taskHolderEl.setOptions(ths);
		});

		const periodEl    = this.getElement("period");
		const startDateEl = this.getElement("startDate");
		const endDateEl   = this.getElement("endDate");

		if(periodEl && startDateEl && endDateEl){

			//------------------PERIOD---------
			periodEl.setOptions(TaskUtils.getPeriodMessenger());

			//------VALUE DEFAULTS
			if(this._filter.period) periodEl.value = this._filter.period;
			if(this._filter.startDate) startDateEl.value = this._filter.startDate;
			if(this._filter.endDate) endDateEl.value = this._filter.endDate;
			//------END VALUE DEFAULTS

			periodEl.addEventListener(EVENT.CHANGE, ({detail}) => {
				if(detail.startDate && detail.endDate){
					startDateEl.value = detail.startDate;
					endDateEl.value = detail.endDate;
				} else {
					startDateEl.value = "";
					endDateEl.value = "";
				}
			});
			// ----------PERIOD END ------------

			startDateEl.addEventListener(EVENT.CHANGE, ()=>periodEl.value = "personalized");
			endDateEl.addEventListener(EVENT.CHANGE, ()=>periodEl.value = "personalized");
		}
	}

	inboxNavBar(){
		let messengerOpts = [];
		
		messengerOpts.push({
			name: 'Recibidas',
			icon: MATERIAL_ICONS.MOVE_TO_INBOX,
			id: MATERIAL_ICONS.MOVE_TO_INBOX,
			fn: () =>{

				if(this.TASK_HOLDER && this.TASK_HOLDER.id){
					this._filter.sender = undefined;
					this._filter.task_holder = this.TASK_HOLDER.id;
				}

				this.addListFilter(this._filter);
				this.updateStatusCount();
				this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined,  this.getListFilter());
			}
		},
		{
			name: MSG.SENT,
			icon: MATERIAL_ICONS.OUTBOX,
			id: MATERIAL_ICONS.OUTBOX,
			fn: () =>{

				if(this.TASK_HOLDER && this.TASK_HOLDER.id){
					this._filter.task_holder = undefined;
					this._filter.sender = this.TASK_HOLDER.id;
				}

				this.addListFilter(this._filter);
				this.updateStatusCount();
				this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined, this.getListFilter());
			}
		},{
			name: 'Todas',
			icon: MATERIAL_ICONS.ALL_INBOX,
			id: MATERIAL_ICONS.ALL_INBOX,
			fn: () =>{
				this._filter.task_holder = undefined;
				this._filter.sender = undefined;

				this.addListFilter(this._filter);
				this.updateStatusCount();
				this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined, this.getListFilter());
			}
		});

		this.getApplication().addSidenavOptions("MI BANDEJA", messengerOpts);
	}

	inboxNavBarCau(){
		let messengerOpts = [
			{
				name: 'Mi bandeja',
				icon: MATERIAL_ICONS.MOVE_TO_INBOX,
				id: MATERIAL_ICONS.MOVE_TO_INBOX,
				fn: () =>{
					this._filter.document = undefined;
					this.addListFilter(this._filter);
					this.updateCount();
					this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined,  this._filter);
				}
			},
			{
				name: 'Todas',
				icon: MATERIAL_ICONS.ALL_INBOX,
				id: MATERIAL_ICONS.ALL_INBOX,
				fn: () =>{
					this._filter.document = this.getDocumentEnterprise();
					this.addListFilter(this._filter);
					this.updateCount();
					this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined, this._filter);
				}
			}
		];

		this.getApplication().addSidenavOptions("BANDEJA", messengerOpts);
	}


	statusNavBar(){
		let messengerOpts = [
			{
				...MessengerOptions.AON_MESSENGER_LIST_OPEN,
				fn: () =>{
					this._filter.status = TASK_STATUS.PENDING;
					this.addListFilter(this._filter);
					this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined,  this.getListFilter());
				}
			},
			{
				...MessengerOptions.AON_MESSENGER_LIST_IN_PROGRESS,
				fn: () =>{
					this._filter.status = TASK_STATUS.IN_PROGRESS;
					this.addListFilter(this._filter);
					this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined,  this.getListFilter());
				}
			},
			{
				...MessengerOptions.AON_MESSENGER_LIST_CLOSE,
				fn: () =>{
					this._filter.status = TASK_STATUS.FINISHED;
					this.addListFilter(this._filter);
					this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined, this.getListFilter());
				}
			},
			{
				...MessengerOptions.AON_MESSENGER_LIST_ARCHIVE,
				fn: () =>{
					this._filter.status = TASK_STATUS.DELETED;
					this.addListFilter(this._filter);
					this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined, this.getListFilter());
				}
			},
		];
		
		this.getApplication().addSidenavOptions(MSG.STATUS, messengerOpts);
	}

    groupNavBar() {
		this.getApplication().addSidenavOptions2({
			id: 'Workgroup',
			name: MSG.WORKGROUP
		}, []);
	}

    tagNavBar() {

		const fnTag = this.getDur().isMessengerManager() && !this.cau ? () => this.dialogTag({}, TAG_TYPE.TASK_LABEL) : null; 
		
		this.getApplication().addSidenavOptions2({
			id: TAG_TYPE.TASK_LABEL,
			name: this.cau ? MSG.APPLICATION : MSG.TAG
		}, [], fnTag);

		if(!this.cau){
			const fnTagType = this.getDur().isMessengerManager() ? () => this.dialogTag({}, TAG_TYPE.TASK_TYPE) : null; 
			this.getApplication().addSidenavOptions2({
				id: TAG_TYPE.TASK_TYPE,
				name: MSG.TYPE
			}, [], fnTagType);
		}
	}

	async loadWorkgroup() {

		await this.getMyWorkgroups().then( workgroups => {
			this.clearElementById(this.getApplication().SIDENAV+'WorkgroupList');
			let options = [];

		//   if(this.getDur().isMessengerManager()){
			options.push({
				id:true,
				name: "SIN ASIGNAR",
				icon: MATERIAL_ICONS.GROUP_OFF,
				fn: () => {
					this._filter.workgroups = undefined;
					this._filter.task_holder = undefined;
					this._filter.sender = undefined;
					this._filter.workgroup = this._filter.workgroup === true ? undefined : true;
					this.addListFilter(this._filter);
					this.updateStatusCount();
					this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined, this.getListFilter());
				}
			});
		//   }

			workgroups.forEach(item => {
				options.push({
					id:item.id,
					name: item.description,
					icon: MATERIAL_ICONS.PEOPLE_ALT,
					fn: () => {

						if(this._filter.workgroup == item.id){
							this._filter.workgroup  = undefined;
							this._filter.workgroups = this.getWorkgroupsStr()
						} else {
							this._filter.workgroup   = item.id;
							this._filter.workgroups  = undefined;
						}

						this.addListFilter(this._filter);

						this.updateStatusCount();

						this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined,  this.getListFilter());
					}
				})
			});

			if(workgroups.length>1){
				options.push({
					id:MATERIAL_ICONS.GROUPS,
					name: "Todos",
					icon: MATERIAL_ICONS.GROUPS,
					fn: () => {
						this._filter.task_holder = undefined;
						this._filter.sender = undefined;
						this._filter.workgroup = undefined;
						this._filter.workgroups = this.getWorkgroupsStr(true);

						this.addListFilter(this._filter);

						this.updateStatusCount();

						this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined, this.getListFilter());
					}
				});
			}

			this.getApplication().addSidenavOptionsList({
				id: 'Workgroup',
				name: MSG.WORKGROUP.toUpperCase()
			}, options);
		});
	}

	async loadTag(type){
		if(type === TAG_TYPE.TASK_LABEL){
			await this.loadTagLabel();
		} else if(type === TAG_TYPE.TASK_TYPE && !this.cau){
			await this.loadTagType();
		}
	}

	async loadTagLabel() {
		const type = TAG_TYPE.TASK_LABEL;

		let params = {type};

		if(this.cau){
			let tags = getAppsByDur(this.getDur()).map(app => app.tag);
			if(tags.length){
				params.tag = tags.join(",");
			}
		}

		const resp = await this.getTags(params);

		this._tags = resp;

		this.clearElementById(this.getApplication().SIDENAV+type+'List');

		resp.forEach(item => {
			let option = {
				id:item.id,
				name: item.description,
				icon: MATERIAL_ICONS.LABEL,
				actions:[],
				fn: () => {
					this._filter.tag = this._filter.tag == item.id ? undefined : item.id;
					this.addListFilter(this._filter);
					this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined, this.getListFilter());
				}
			};

			if(this.getDur().isMessengerManager() && !this.cau){
				option.actions.push(
					{ id: 'Delete', icon: MATERIAL_ICONS.DELETE, action: () => this.deleteTag(item) },
					{ id: 'Edit', icon: MATERIAL_ICONS.EDIT, action: () => this.dialogTag(item, type) }
				);
			}

			this.getApplication().addSidenavOptionsListValue({
				id: type,
				name: MSG.TAG.toUpperCase()
			}, option);
		});
	}

	async loadTagType() {
		if(!this.cau){
			const type = TAG_TYPE.TASK_TYPE;

			const resp = await this.getTags({type});
	
			this.clearElementById(this.getApplication().SIDENAV+type+'List');
	
			resp.forEach(item => {
				let option = {
					id:item.id,
					name: item.description,
					icon: MATERIAL_ICONS.LABEL,
					actions:[],
					fn: () => {
						this._filter.tag = this._filter.tag == item.id ? undefined : item.id;
						this.addListFilter(this._filter);
						this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined, this.getListFilter());
					}
				};
	
				if(this.getDur().isMessengerManager()){
					option.actions.push(
						{ id: 'Delete', icon: MATERIAL_ICONS.DELETE, action: () => this.deleteTag(item) },
						{ id: 'Edit', icon: MATERIAL_ICONS.EDIT, action: () => this.dialogTag(item, type) }
					);
				}
				this.getApplication().addSidenavOptionsListValue({
					id: type,
					name: MSG.TYPE.toUpperCase()
				}, option);
			});
		}
	}

	async getTags(params) {
		let resp = await getTaskTags(params)
		resp = sortBy(resp, "name", "asc").map(t => ({...t, value: t.id, description: t.name, name:t.name}));
		return resp;
	}

	addListFilter(filter) {
		this.setListFilter({...this.getListFilter(), ...filter});
	}

	setListFilter(filter) {
		this._listFilter = filter;
		this.addBackgroundSidenav(filter);
	}

	getListFilter() {
		return this._listFilter || {page:0, perPage:30, status: TASK_STATUS.PENDING};
	}

	addBackgroundSidenav(filter){
		if(filter){
			const application = this.getApplication();
			application.removeBackgroundSidenavAll();
			if(this.cau){
				application.addBackgroundSidenav(filter.document ? MATERIAL_ICONS.ALL_INBOX: MATERIAL_ICONS.MOVE_TO_INBOX);
			} else {
				
				if(filter.workgroup){
					application.addBackgroundSidenav(filter.workgroup);
				} else if(filter.workgroups && filter.workgroups.includes("all")){
					application.addBackgroundSidenav(MATERIAL_ICONS.GROUPS);
				} 

				if(filter.sender && filter.task_holder){
					application.addBackgroundSidenav(MATERIAL_ICONS.ALL_INBOX);
				} else if(filter.sender){
					application.addBackgroundSidenav(MATERIAL_ICONS.OUTBOX);
				} else if(filter.task_holder){
					application.addBackgroundSidenav(MATERIAL_ICONS.MOVE_TO_INBOX);
				} else {
					application.addBackgroundSidenav(MATERIAL_ICONS.ALL_INBOX);
				}
			}
	
			switch (filter.status){
				case TASK_STATUS.IN_PROGRESS:
					application.addBackgroundSidenav(MessengerOptions.AON_MESSENGER_LIST_IN_PROGRESS.name);
					break;
				case TASK_STATUS.FINISHED:
					application.addBackgroundSidenav(MessengerOptions.AON_MESSENGER_LIST_CLOSE.name);
					break;
				case TASK_STATUS.DELETED:
					application.addBackgroundSidenav(MessengerOptions.AON_MESSENGER_LIST_ARCHIVE.name);
					break;
				default:
					application.addBackgroundSidenav(MessengerOptions.AON_MESSENGER_LIST_OPEN.name);
					break;
			}

			if(filter.workgroup){
				application.addBackgroundSidenav(filter.workgroup);
			}

			if(filter.tag){
				application.addBackgroundSidenav(filter.tag);
			}
		}
	}

	dialogTag(tag={}, type) {
		const isEdit = tag && tag.id;

		let d = this.getApplication().getDialog();
		d.clear();
		
		if(this.isMobile()) {
			d.type ="fullscreen";
		} else {
			d.width = '400px';
		}
		d.setTitle(isEdit ? MSG.EDIT : MSG.ADD);

		let aonInput = new AonInput();
		aonInput.id = "addTag";
		aonInput.description = type === TAG_TYPE.TASK_LABEL ? MSG.TAG : MSG.TYPE;
		if(tag.name) {
			aonInput.value = tag.name;
		}

		d.setContent(aonInput);
		d.addAcceptAction(() => {
			if(aonInput.value){
				tag.name = aonInput.value;
				tag.type = type;
				saveTaskTag(tag).then(() => {
					this.showMessage();
					this.loadTag(type);
				}).catch(err=>{
					this.showError(err);
				});
			}
		});
		d.open();
	}

	deleteTag(tag) {
		let d = this.getApplication().getDialog();
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.DELETE);
		d.setContentHTML(`Estás seguro de eliminar la ${MSG.TAG} ${tag.name}`);
		d.addAcceptAction(() => {
			deleteTaskTag(tag).then(() => {
				this.showMessage(`Eliminada!`);
				this.loadTag(tag.tag_type);
			}).catch(err=>{
				this.showError(err);
			});
		});
		d.open();
	}

	updateCount(){
		let application = this.getApplication();
		let filterCount= {};
		if(this.cauInfo && this.cauInfo.auth && this.cauInfo.auth.email){
			filterCount.email = this.cauInfo.auth.email;
		}

		if(this.cau){
			filterCount.task_holder = 1;
			filterCount.sender = 1;
		} else {
			if(this.TASK_HOLDER.id){
				filterCount.task_holder = this.TASK_HOLDER.id;
			}
				
			const workgroupStr = this.getWorkgroupsStr();

			if(workgroupStr){
				filterCount.workgroups = workgroupStr;
			}
		}
		
		if(!this.cau){
			getTaskCount(filterCount).then(count=>{
				let task_holder =  count.task_holder || 0;
				let sender =  count.sender || 0;
				application.updateSidenavCount(MATERIAL_ICONS.MOVE_TO_INBOX, task_holder);
				application.updateSidenavCount(MATERIAL_ICONS.OUTBOX, sender);
			});	
		}

		let filter = {};

		if(this._filter.document) {
			filter.document = this._filter.document;
		}

		if(filterCount.workgroups && !this.cau){
			filter.workgroups = filterCount.workgroups;
		}

		if(filterCount.email){
			filter.email = filterCount.email;
		}

		if(filterCount.task_holder && !this.getDur().isMessengerManager()){
			filter.task_holder = filterCount.task_holder;
		}

		if(this._tags.length){
			filter.tag = this._tags.map(t=> t.id).join(',');
		}

		getTaskGeneralCount(filter).then(({workgroups, tag})=>{
			try {
				if(workgroups){
					for(const key in workgroups){
						application.updateSidenavCount(key,  workgroups[key]);
					}
				}

				if(this._tags.length){
					this._tags.forEach(({id}) => application.updateSidenavCount(id, 0));
				}

				if(tag){
					for(const key in tag){
						application.updateSidenavCount(key, tag[key]);
					}
				}
			} catch (error) {
				console.error(error);
			}
		});

		this.updateStatusCount();
	}

	updateStatusCount(){
		if(!this.cau){
			const application = this.getApplication();

			const workgroupStr = this.getWorkgroupsStr();

			let filter = {};

			let filters = this.getListFilter();

			if(this.cauInfo && this.cauInfo.auth && this.cauInfo.auth.email){
				filter.email = this.cauInfo.auth.email;
			}
	
			if(filters.task_holder){
				filter.task_holder = filters.task_holder;
			}

			if(filters.sender){
				filter.sender = filters.sender;
			}

			if(filters.workgroup){
				filter.workgroup = filters.workgroup;
			} else if(workgroupStr){
				filter.workgroups = workgroupStr;
			}
		
			getTaskStatusCount(filter).then(({status})=>{
				try {
					if(status){
						//----------------------- UPDATE COUNT---------------
						let listOpen = MessengerOptions.AON_MESSENGER_LIST_OPEN;
						application.updateSidenavCount(listOpen.id, status[TASK_STATUS.PENDING]);
		
						let listInProgress = MessengerOptions.AON_MESSENGER_LIST_IN_PROGRESS;
						application.updateSidenavCount(listInProgress.id, status[TASK_STATUS.IN_PROGRESS]);
			
						let listClose = MessengerOptions.AON_MESSENGER_LIST_CLOSE;
						application.updateSidenavCount(listClose.id, status[TASK_STATUS.FINISHED]);
						
						let listTrash = MessengerOptions.AON_MESSENGER_LIST_ARCHIVE;
						application.updateSidenavCount(listTrash.id, status[TASK_STATUS.DELETED]);
					}
				} catch (error) {
					console.error(error);
				}
			});
		}
		this.getNotifications();
	}

	getDur(){
		return this.dur;
	}
	
	getWorkgroupsStr(all=false){
		// let isManager = this.getDur().isMessengerManager();
		// if(isManager || this.cau) return undefined;
		if(this.cau) return undefined;
		const wps = this._workgroups.map(({id})=> id);
		if(wps.length){
			let join = wps.join(",");
			return all ? join+",all" : join;
		}
		return 0;
	}

	//MY WORKGROUPRS
	async getMyWorkgroups(){
		if(!this._workgroups.length){

			let filter = {status:"ACTIVE"};

			let isManager = this.getDur().isMessengerManager();

			if(!isManager && this.TASK_HOLDER.id) {
				filter.task_holder = this.TASK_HOLDER.id;
			}

			await getWorkgroups(filter).then( workgroup => {
				this._workgroups = workgroup.map(t => ({...t, value: t.id, description: t.description, name:t.description}));
			});
		}
		return this._workgroups;
	}

	async getAppParams(){
		if(!this.APP_PARAMS.length){
			try {
				await getApplicationParametersIsSig({
					params:[
						APP_PARAMS_REQUEST.APP_REQUESTS_INT_TASK_HOLDER, 
						APP_PARAMS_REQUEST.APP_REQUESTS_EXT_TASK_HOLDER, 
						APP_PARAMS_REQUEST.APP_REQUESTS_INT_WORKGROUP, 
						APP_PARAMS_REQUEST.APP_REQUESTS_EXT_WORKGROUP
					]
				}).then(params=>{
					let newResp = [];
					params
					.filter(p => p.value)
					.forEach(p => 
						newResp[p.name] = p.value
					);
					this.APP_PARAMS = newResp;
				});
			} catch (e) {
				console.log("error getAppParams", e);
			}
		}
		return this.APP_PARAMS;
	}

	loadGraph(){
		const contentId = this.getApplication().CONTENT;
    
		this.clearElementById(contentId);

		GWT.load(GWT.TASK_STAT, contentId);

		waitEl(`#${contentId} > div:first-child`)
		.then(element=>{
			element.style.inset = '0px';
		});
	}


	showView(view, data = undefined, filter = undefined){
		return new Promise(async(resolve)=>{
			let aonView = undefined;
			switch(view){
				case MESSENGER_VIEWS.AON_MESSENGER_LIST:
					aonView = new AonMessengerList();
				break;
				case MESSENGER_VIEWS.AON_MESSENGER_CHAT:
					aonView = new AonMessengerChat();
				break;
				case MESSENGER_VIEWS.AON_MESSENGER_GRAPHIC:
					this.loadGraph();
				break;
			}
			if(aonView){
				aonView.id = view;
				if(filter) aonView.setFilter(filter);
				if(data) aonView.data = data;
				this.getApplication().setContent(aonView);
			}
			resolve(aonView);
		});
    }

	async getNotifications(){
		try {
			clearTimeout(this.TIMEOUT);
			this.TIMEOUT = setTimeout(() =>{
				getNotificationByDomain({read:false, source: "MESSENGER"})
				.then(notifications=>{
					if(this.isMobile()){
						this.createBadgeMobile(notifications);
					} else {
						this.createBadgeDesktop(notifications);
					}
				});
			}, 300);
		} catch (err){
			console.log(err);
		}
	}

	createBadgeDesktop(notifications){
		notifications.forEach(({source_id:taskId})=>{
			let selectors = `span[data-task-id='${taskId}'], span[data-task-child*='${taskId}']`;
			waitEl(selectors).then(()=>{
				document.querySelectorAll(selectors)
				.forEach(icon=>{
					const isChild = icon.hasAttribute('data-is-child');
					icon.appendChild(this.createSpanBadgeUnread(isChild));
				});
			});
		});
	}

	createBadgeMobile(notifications){
		notifications.forEach(({source_id:taskId})=>{
			let selectors = `li[data-task-id='${taskId}'] > span`;
			waitEl(selectors).then(()=>{
				document.querySelectorAll(selectors)
				.forEach(span=>{
					let badge = this.createSpanBadgeUnread(false);
					badge.style.left  = "21px";
					badge.style.top   = "22px";
					badge.style.right = "";
					span.appendChild(badge);
				});
			});
		});
	}

	createSpanBadgeUnread(isChild){
		let span = document.createElement("span");
		span.style = `
			position: absolute; 
			right: -4px; 
			top: 4px; 
			padding: ${isChild ? "3": "4"}px; 
			border-radius: 50%; 
			background: rgb(220, 77, 48); 
			color: white; 
			font-size: 10px; 
			font-weight: 800;
		`
		return span;
	}

	getDocumentEnterprise(){
		let document = undefined;
		if(this.cauInfo && this.cauInfo.company && this.cauInfo.company.document){
			document = this.cauInfo.company.document;
		}
		return document;
	}

	async getExcel(){
		this.getApplication().startLoading();
		try{
			const filter = removeEmpty(this.getListFilter());
			await getTaskExcel(filter);
		} catch(err){
			this.showError(err)
			console.log(err);
		}
		this.getApplication().stopLoading();
	}

	markReadNotification(taskId){
		markReadNotification({source:"MESSENGER",source_id:taskId});
	}

	async getTaskHoldersEnterprise(){
		if(this.TASK_HOLDER_ENTERPRISE.length<=0){
			const ths = await getTastHolders();
			this.TASK_HOLDER_ENTERPRISE = ths.map( c=> ({...c, value: c.id})  );
		}
		return this.TASK_HOLDER_ENTERPRISE;
	}
}
window.customElements.define('aon-messenger', AonMessenger);