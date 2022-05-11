import { AonApplication } from '../../components/aon-application.js';
import { AonElement } from '../../components/AonElement.js';
import { CONSTANT, EVENT, MATERIAL_ICONS, MSG } from '../../environments/environments.js';
import Apps from '../../services/app.js';
import {getWorkgroups} from '../../services/workgroupService.js';
import { AonMessengerChat } from './aon-messeger-chat.js';
import { AonMessengerList } from './aon-messenger-list.js';
import { APP_PARAMS_REQUEST, MessengerOptions, MESSENGER_VIEWS, TAG_TYPE, TASK_FILTER, TASK_SOURCE, TASK_STATUS } from './MessengerEnums.js';
import { getTaskHolder, getTastHolders } from '../../services/taskHolderService.js';
import { getTaskGeneralCount, getTaskOne, getCauInfo, getTaskCount, getTaskTags, saveTaskTag, deleteTaskTag, getTaskAppParams } from '../../services/taskService.js';
import { AonInput } from '../../components/aon-input.js';
import { getDomainUserRoles } from '../../services/companyService.js';
import { DomainUserRoles } from '../../models/DomainUserRoles.js';
import { SigninSidenav } from '../timecontrol/signinEnums.js';
import { getCustomers } from '../../services/registryService.js';
import { sortBy } from '../../services/utils.js';
import { getAppPermission } from './shared/fill.js';

export class AonMessenger extends AonElement {
    AON_MESSENGER;
	APP_PARAMS=[];
	_workgroups;
	_tags;
	_filter={};
	_listFilter;
	TASK_HOLDER;
	TASK_HOLDER_ENTERPRISE;
	cau; //BOOLEAN
	cauInfo;
	dur;

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
		if(this.type && this.type === TASK_SOURCE.CAU) {
			this.cau = 1;
			this._filter.source = TASK_SOURCE.CAU;
		}
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
			perPage:30
		};
	}

	build() {
		let title = this.cau ? MSG.SUPPORT + ' / CAU' : MSG.REQUESTS;
		this.applicationEl = this.createApplication(this.AON_MESSENGER, title, new AonApplication());

		
		this.isTaskHolder().then(async (exist) => {
			if(exist){
				localStorage.setItem("taskCau", this.cau ? 1 : 0);

				let promises = [getCauInfo()];

				if(!this.cau)
					promises.push(this.getMyWorkgroups());

				const [cauInfo] = await Promise.all(promises);

				this.cauInfo = cauInfo;

				const email = cauInfo.auth.email;

				if(!email){
					this.showError({message:"Auth inexistente", type:CONSTANT.ERROR});
				} else {
					this.buildToolbar();

					let promisesLoad = [];

					promisesLoad.push(this.loadTag());

                    if(!this.cau){
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
			
		if(this.isMobile()){
			this.applicationEl.addMobileSidenavHeader(Apps.MESSENGER);
			this.applicationEl.addFloatOption(SigninSidenav.ADD, () => 
				this.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, {source:TASK_SOURCE.QUERY})
			);
		} else {
			this.applicationEl.addToolbarOption2(SigninSidenav.ADD, () =>
				this.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, {source:TASK_SOURCE.QUERY})
			);
		}

		this.buildToolbarSearch();
			
		this.taskNavBar();
		this.statusNavBar();
		
		if(!this.cau){
			this.groupNavBar();
		}

		this.tagNavBar();
	}


	buildToolbarSearch(){
		let btnSearch = this.applicationEl.addSearchOption();
		btnSearch.addEventListener(EVENT.SEARCH, ({detail}) => {
		  this._filter.search = detail;
		  this.setListFilter({...this.getListFilter(), page:0, perPage:30, search:detail});
		  this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined, this.getListFilter());
		});
	
		if(!this.cau){
		  btnSearch.addEventListener(EVENT.SEARCH_VALUE, ({detail})=>{
			if(detail) {
			  this.setListFilter({...this.getListFilter(), 
				page:0, perPage:30, 
				task_holder:detail.task_holder, 
				registry: detail.registry,
				startDate: detail.startDate,
				sender:detail.sender, 
				// workgroup:detail.workgroup
			});
			  this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined, this.getListFilter());
			} 
		  });
		  btnSearch.buildOptionsFilter(TASK_FILTER);//INPUTS
		  this.searchValueDefault();
		} else {
			btnSearch.removeButtonAvanced();
		}
	}


	searchValueDefault(){
		let registryEl = this.getElement("registry");
		let taskHolderEl = this.getElement("task_holder");
		let senderEl = this.getElement("senderFilter");
		// let statusEl = this.getElement("status");
		// let workgroup = this.getElement("workgroup");
		getCustomers({reload:true, page:1, perPage:50}).then(customers=>{
		  registryEl.setOptions(customers.map(c=> ({...c, value: c.id})) );
		});
	
		registryEl.addEventListener(EVENT.INPUT,async({target})=>{
			const value = target.value;
			if(value.length > 2){
			  const cs = await getCustomers({reload:true, page:1, perPage:30, value});
			  registryEl.setOptions( cs.map( c=> ({...c, value: c.id}) ) );
			}
		});

		this.getTaskHoldersEnterprise().then(ths=>{
			senderEl.setOptions(ths);
			taskHolderEl.setOptions(ths);
		});
	
		// this.getMyWorkgroups().then(wgs=>
		// 	workgroup.setOptions(wgs)
		// );
	
		// statusEl.setOptions(TASK_STATUS_VALUE);
	}

	taskNavBar(){
		let messengerOpts = [];
		
		if(!this.cau){
			messengerOpts.push({
				name: 'Recibidas',
				icon: MATERIAL_ICONS.MOVE_TO_INBOX,
				id: MATERIAL_ICONS.MOVE_TO_INBOX,
				fn: () =>{

					if(this._filter.task_holder && !this._filter.sender){
						this._filter.sender = this._filter.task_holder;
					} else {
						this._filter.task_holder = this.TASK_HOLDER.id;
						this._filter.sender = undefined;
					}
		
					this._filter.workgroups = this.getWorkgroupsStr();
					this.addListFilter(this._filter);
					this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined,  this._filter);
				}
			},
			{
				name: MSG.SENT,
				icon: MATERIAL_ICONS.OUTBOX,
				id: MATERIAL_ICONS.OUTBOX,
				fn: () =>{
		
					if(this._filter.sender && !this._filter.task_holder){
						this._filter.task_holder = this._filter.sender;
					} else {
						this._filter.sender = this.TASK_HOLDER.id;
						this._filter.task_holder = undefined;
					}

					this._filter.workgroups = this.getWorkgroupsStr();
					this.addListFilter(this._filter);
					this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined,  this._filter);
				}
			});
		}

		messengerOpts.push({
			name: 'Todas',
			icon: MATERIAL_ICONS.ALL_INBOX,
			id: MATERIAL_ICONS.ALL_INBOX,
			fn: () =>{
				this._filter.task_holder = undefined;
				this._filter.sender = undefined;
				this._filter.workgroups = this.getWorkgroupsStr();

				if(!this.cau && !this.getDur().isMessengerManager()){
					let taskHolder = this.TASK_HOLDER.id ? this.TASK_HOLDER.id : undefined;
					this._filter.sender = taskHolder;
					this._filter.task_holder = taskHolder;
				}

				this.addListFilter(this._filter);
				
				this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined, this._filter);
			}
		});

		this.applicationEl.addSidenavOptions("BANDEJAS", messengerOpts);
	}

	statusNavBar(){
		let messengerOpts = [
			{
				...MessengerOptions.AON_MESSENGER_LIST_OPEN,
				fn: () =>{
					this._filter.status = TASK_STATUS.PENDING;
					this._filter.workgroups = this.getWorkgroupsStr();
					this.addListFilter(this._filter);
					this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined,  this._filter);
				}
			},
			{
				...MessengerOptions.AON_MESSENGER_LIST_IN_PROGRESS,
				fn: () =>{
					this._filter.status = TASK_STATUS.IN_PROGRESS;
					this._filter.workgroups = this.getWorkgroupsStr();
					this.addListFilter(this._filter);
					this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined,  this._filter);
				}
			},
			{
				...MessengerOptions.AON_MESSENGER_LIST_CLOSE,
				fn: () =>{
					this._filter.status = TASK_STATUS.FINISHED;
					this._filter.workgroups = this.getWorkgroupsStr();
					this.addListFilter(this._filter);
					this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined, this._filter);
				}
			},
			{
				...MessengerOptions.AON_MESSENGER_LIST_ARCHIVE,
				fn: () =>{
					this._filter.status = TASK_STATUS.DELETED;
					this._filter.workgroups = this.getWorkgroupsStr();
					this.addListFilter(this._filter);
					this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined, this._filter);
				}
			},
		];
		
		this.applicationEl.addSidenavOptions(MSG.STATUS, messengerOpts);
	}

    groupNavBar() {
		let application = this.applicationEl;
		application.addSidenavOptions2({
			id: 'Workgroup',
			name: MSG.WORKGROUP
		}, []);
	}


    tagNavBar() {
		let application = this.applicationEl;
		
		const fnTag = this.getDur().isMessengerManager() ? () => this.dialogTag() : null; 
		
		application.addSidenavOptions2({
			id: 'Tag',
			name: MSG.TAG
		}, [], fnTag);
	}
	
    tagNavBar() {
		let application = this.applicationEl;
		
		const fnTag = this.getDur().isMessengerManager() ? () => this.dialogTag() : null; 
		
		application.addSidenavOptions2({
			id: 'Tag',
			name: this.cau ? MSG.APPLICATION : MSG.TAG
		}, [], fnTag);
	}

	async loadWorkgroup() {
		let application = this.applicationEl;

		await this.getMyWorkgroups().then( workgroups => {
		  this.clearElementById(application.SIDENAV+'WorkgroupList');
		  let options = [];

		  if(this.getDur().isMessengerManager()){
			options.push({
				id:true,
				name: "SIN ASIGNAR",
				icon: MATERIAL_ICONS.GROUP_OFF,
				fn: () => {
					this._filter.workgroups = undefined;
					this._filter.task_holder = undefined;
					this._filter.sender = undefined;
					this._filter.workgroup = this._filter.workgroup === true ? undefined : true;
					this.addListFilter({...this._filter});
					this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined, this.getListFilter());
				}
			});
		  }

		  workgroups.forEach(item => 
			options.push({
				id:item.id,
				name: item.description,
				icon: MATERIAL_ICONS.PEOPLE_ALT,
				fn: () => {
					let filter = {};
					if(this._filter.workgroup == item.id){
						this._filter.workgroup  = undefined;
						this.addListFilter({...this._filter});
						filter = {...this.getListFilter()};
					} else {
						this._filter.workgroup  = item.id;
						this.addListFilter({...this._filter});
						filter = {...this.getListFilter(), sender:undefined, task_holder:undefined};
					}

					this.addBackgroundSidenav(filter);
					this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined, filter);
				}
			})
		  );

			application.addSidenavOptionsList({
				id: 'Workgroup',
				name: MSG.WORKGROUP.toUpperCase()
			}, options);
		});
	}

	async loadTag() {
		let application = this.applicationEl;
		const manager =  this.getDur().isMessengerManager();

		let params = {type:TAG_TYPE.TASK_LABEL};

		if(this.cau){
			let tags = getAppPermission(this.getDur()).map(app => app.tag);
			if(tags.length){
				params.tag = tags.join(",");
			}
		}

		await getTaskTags(params).then(tags => {
		  this._tags = sortBy(tags, "name", "asc").map(t => ({...t, value: t.id, description: t.name, name:t.name}));
		  this.clearElementById(application.SIDENAV+'TagList');
		  this._tags.forEach(item => {
			let option = {
				id:item.id,
				name: item.description,
				icon: MATERIAL_ICONS.LABEL,
				actions:[],
				fn: () => {
					this._filter.workgroups = this.getWorkgroupsStr();
					this._filter.tag = this._filter.tag == item.id ? undefined : item.id;
					this.addListFilter({...this._filter});
					this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined, this.getListFilter());
				}
			};

			if(manager){
				option.actions.push(
					{ id: 'Delete', icon: MATERIAL_ICONS.DELETE, action: () => this.deleteTag(item) },
					{ id: 'Edit', icon: MATERIAL_ICONS.EDIT, action: () => this.dialogTag(item) }
				);
			}

			application.addSidenavOptionsListValue({
				id: 'Tag',
				name: MSG.TAG.toUpperCase()
			}, option);
		  });
		})
	}

	addListFilter(obj) {
		this.setListFilter({...this.getListFilter, ...obj});
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
			this.applicationEl.removeBackgroundSidenavAll();

			if(filter.sender && filter.task_holder){
				this.applicationEl.addBackgroundSidenav(MATERIAL_ICONS.ALL_INBOX);
			} else if(filter.sender){
				this.applicationEl.addBackgroundSidenav(MATERIAL_ICONS.OUTBOX);
			} else if(filter.task_holder){
				this.applicationEl.addBackgroundSidenav(MATERIAL_ICONS.MOVE_TO_INBOX);
			} else {
				this.applicationEl.addBackgroundSidenav(MATERIAL_ICONS.ALL_INBOX);
			}

			switch (filter.status){
				case TASK_STATUS.IN_PROGRESS:
					this.applicationEl.addBackgroundSidenav(MessengerOptions.AON_MESSENGER_LIST_IN_PROGRESS.name);
					break;
				case TASK_STATUS.FINISHED:
					this.applicationEl.addBackgroundSidenav(MessengerOptions.AON_MESSENGER_LIST_CLOSE.name);
					break;
				case TASK_STATUS.DELETED:
					this.applicationEl.addBackgroundSidenav(MessengerOptions.AON_MESSENGER_LIST_ARCHIVE.name);
					break;
				default:
					this.applicationEl.addBackgroundSidenav(MessengerOptions.AON_MESSENGER_LIST_OPEN.name);
					break;
			}

			if(filter.workgroup){
				this.applicationEl.addBackgroundSidenav(filter.workgroup);
			}

			if(filter.tag){
				this.applicationEl.addBackgroundSidenav(filter.tag);
			}
		}
	}

	dialogTag(tag={}) {
		let d = this.getElement(this.getApplication().DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(tag && tag.id ? MSG.EDIT : MSG.ADD);

		let aonInput = new AonInput();
		aonInput.id = "addTag";
		aonInput.description = MSG.TAG;
		if(tag.name) aonInput.value = tag.name;
		d.setContent(aonInput);

		d.addAcceptAction(() => {
			if(aonInput.value){
				tag.name = aonInput.value;
				tag.type = TAG_TYPE.TASK_LABEL;
				saveTaskTag(tag).then(() => {
					this.showMessage();
					this.loadTag();
				}).catch(err=>{
					this.showError(err);
				});
			}
		});
		d.open();
	}

	deleteTag(tag) {
		let d = this.getElement(this.getApplication().DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.DELETE);
		d.setContentHTML(`Estás seguro de eliminar la ${MSG.TAG} ${tag.name}`);
		d.addAcceptAction(() => {
			deleteTaskTag(tag).then(() => {
				this.showMessage(`${MSG.TAG} eliminada!` );
				this.loadTag();
			}).catch(err=>{
				this.showError(err);
			});
		});
		d.open();
	}

	updateCount(){
		let application = this.applicationEl;
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

		if(this._filter.source) {
			filter.source = this._filter.source;
		}

		if(filterCount.workgroups){
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

		getTaskGeneralCount(filter).then(({status, workgroups, tag})=>{
			try {
				if(status){
					//----------------------- UPDATE COUNT---------------
					let listOpen = MessengerOptions.AON_MESSENGER_LIST_OPEN;
					application.updateSidenavCount(listOpen.id, status[TASK_STATUS.PENDING]);
	
					let listInProgress = MessengerOptions.AON_MESSENGER_LIST_IN_PROGRESS;
					application.updateSidenavCount(listInProgress.id, status[TASK_STATUS.IN_PROGRESS]);
		
					let listClose = MessengerOptions.AON_MESSENGER_LIST_CLOSE;
					application.updateSidenavCount(listClose.id, status[TASK_STATUS.DELETED]);
					
					let listTrash = MessengerOptions.AON_MESSENGER_LIST_ARCHIVE;
					application.updateSidenavCount(listTrash.id, status[TASK_STATUS.FINISHED]);
				}
	
				if(workgroups){
					for(const key in workgroups){
						application.updateSidenavCount(key,  workgroups[key]);
					}
				}

				if(this.cau){
					if(tag){
						for(const key in tag){
							application.updateSidenavCount(key, tag[key]);
						}
					}
				}
			} catch (error) {
				console.error(error);
			}
		});
	}

	getDur(){
		return this.dur;
	}
	
	getWorkgroupsStr(){
		let isManager = this.getDur().isMessengerManager();
		if(isManager || this.cau) return undefined;
		const wps = this._workgroups.map(({id})=> id)
		return wps.length ? wps.join(",") : 0;
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
				await getTaskAppParams({
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
			}
			if(aonView){
				aonView.id = view;
				if(filter) aonView.setFilter(filter);
				if(data) aonView.data = data;
				this.applicationEl.setContent(aonView);
			}
			resolve(aonView);
		});
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