import { AonApplication } from '../../components/aon-application.js';
import { AonElement } from '../../components/AonElement.js';
import { CONSTANT, EVENT, MATERIAL_ICONS, MSG } from '../../environments/environments.js';
import Apps from '../../services/app.js';
import {getWorkgroups} from '../../services/workgroupService.js';
import { AonMessengerChat } from './aon-messeger-chat.js';
import { AonMessengerList } from './aon-messenger-list.js';
import { MessengerOptions, MESSENGER_VIEWS, TAG_TYPE, TASK_FILTER, TASK_SOURCE, TASK_STATUS, TASK_STATUS_VALUE } from './MessengerEnums.js';
import { getTaskHolder, getTastHolders } from '../../services/taskHolderService.js';
import { getTaskStatusCount, getTaskOne, getCauInfo, getTaskCount, getTaskTags, saveTaskTag, deleteTaskTag } from '../../services/taskService.js';
import { AonInput } from '../../components/aon-input.js';
import { getDomainUserRoles } from '../../services/companyService.js';
import { DomainUserRoles } from '../../models/DomainUserRoles.js';
import { SigninSidenav } from '../timecontrol/signinEnums.js';
import { getCustomers } from '../../services/registryService.js';
// import { AonMessengerAyudat } from './aon-messenger-ayudat.js';

export class AonMessenger extends AonElement {
    AON_MESSENGER;
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

	addListFilter(obj) {
		this.setListFilter({...this.getListFilter, ...obj});
	}

	setListFilter(filter) {
		this._listFilter = filter;
	}

	getListFilter() {
		let filter =  this._listFilter || {page:0, perPage:30, status: TASK_STATUS.PENDING};
		return filter;
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
		this.TASK_HOLDER_ENTERPRISE = [];
		this.TASK_HOLDER = {};
		this._filter = {
			workgroup: undefined,
			workgroups: undefined,
			task_holder: this._filter.task_holder || undefined,
			sender: this._filter.sender || undefined,
			source: this._filter.source || undefined,
			status: TASK_STATUS.PENDING,
			email: undefined,
			page:0, 
			perPage:30
		};
	}

	build() {
		let title = this.cau ? MSG.SUPPORT + ' / CAU' : MSG.REQUESTS;
		this.applicationEl = this.createApplication(this.AON_MESSENGER, title, new AonApplication());

		this.isTaskHolder().then((exist) => {
			if(exist){
				localStorage.setItem("taskCau", this.cau ? 1 : 0);
				
				getCauInfo().then(cau=>{
					this.cauInfo = cau;
					const email = cau.auth.email;
					if(!email){
						this.showError({message:"Auth inexistente", type:CONSTANT.ERROR});
					} else {
						this.buildToolbar();

						if(!this.cau){
							this.loadWorkgroup();
						} else {
							this._filter.email = email;
							this.addListFilter(this._filter);
						}
						
						this.init();	
					}
				})
			}
		});		
	}

	async isTaskHolder(){
		this.TASK_HOLDER = await getTaskHolder({reload:false}).catch(e=>null);
		if(!this.cau && !(this.TASK_HOLDER && this.TASK_HOLDER.id)){
			this.showError({message:"Operario inexistente", type:CONSTANT.ERROR});
			return false;
		}
		return true;
	}

	init(){
		if(this.data){
			this.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, this.data);
		} else if(this.cau){
			this.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, this._filter);
		} else if(this.value){
			getTaskOne({id:this.value}).then(task=>this.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, task)).catch(e=>this.showError(e));
		} else {
			if(this._filter.sender)
				this.applicationEl.addBackgroundSidenav(MATERIAL_ICONS.OUTBOX);
			else if(this._filter.task_holder)
				this.applicationEl.addBackgroundSidenav(MATERIAL_ICONS.MOVE_TO_INBOX);
			else 
				this.applicationEl.addBackgroundSidenav(MATERIAL_ICONS.ALL_INBOX);

			this.applicationEl.addBackgroundSidenav(MessengerOptions.AON_MESSENGER_LIST_OPEN.name);
			this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined,  this._filter);
		}

		this.updateCount();
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
			this.tagNavBar();
		}
	}


	buildToolbarSearch(){
		let btnSearch = this.applicationEl.addSearchOption();
		btnSearch.addEventListener(EVENT.SEARCH, ({detail}) => {
		  this.setListFilter({...this.getListFilter(), page:0, perPage:30, search:detail});
		  this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined, this.getListFilter());
		});
	
		if(!this.cau){
		  btnSearch.addEventListener(EVENT.SEARCH_VALUE, ({detail})=>{
			if(detail) {
			  this.setListFilter({...this.getListFilter(), page:0, perPage:30, task_holder:detail.task_holder, registry: detail.registry, startDate: detail.startDate, workgroup: detail.workgroup, sender:detail.sender});
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
		let statusEl = this.getElement("status");
		let workgroup = this.getElement("workgroup");
		getCustomers({reload:true, page:1, perPage:50}).then(customers=>{
		  registryEl.setOptions(customers.map(c=> ({...c, value: c.id})) );
		});
	
		registryEl.addEventListener(EVENT.INPUT,async({target})=>{
			const value = target.value;
			if(value.length > 2){
			  const cs = await getCustomers({reload:true, page:1, perPage:30, value});
			  registryEl.setOptions( cs.map( c=> ({...c, value: c.id}) ) );
			}
		})
	
		this.getMyWorkgroups().then(wgs=>
			workgroup.setOptions(wgs)
		);
	
		this.getTaskHoldersEnterprise().then(ths=>{
			senderEl.setOptions(ths);
			taskHolderEl.setOptions(ths);
		});

		statusEl.setOptions(TASK_STATUS_VALUE);
	  }

	taskNavBar(){
		let messengerOpts = [
			{
				name: 'Recibidas',
				icon: MATERIAL_ICONS.MOVE_TO_INBOX,
				id: MATERIAL_ICONS.MOVE_TO_INBOX,
				fn: () =>{
					this._filter.workgroups = undefined;
					this._filter.sender = undefined;
					this._filter.task_holder = this.TASK_HOLDER.id;
					this._filter.status = TASK_STATUS.PENDING;
					this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined,  this._filter);
				}
			},
			{
				name: MSG.SENT,
				icon: MATERIAL_ICONS.OUTBOX,
				id: MATERIAL_ICONS.OUTBOX,
				fn: () =>{
					this._filter.task_holder = undefined;
					this._filter.workgroups = undefined;
					this._filter.sender = this.TASK_HOLDER.id;
					this._filter.status = TASK_STATUS.PENDING;
					this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined,  this._filter);
				}
			},
			{
				name: 'Todas',
				icon: MATERIAL_ICONS.ALL_INBOX,
				id: MATERIAL_ICONS.ALL_INBOX,
				fn: () =>{
					this._filter.task_holder = undefined;
					this._filter.sender = undefined;
					this._filter.workgroup = undefined;
					this._filter.status = TASK_STATUS.PENDING;
					this._filter.workgroups = this.getWorkgroupsStr();
					this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined,  this._filter);
				}
			},
		];
		
		this.applicationEl.addSidenavOptions("PENDIENTES", messengerOpts);
	}

	statusNavBar(){
		let messengerOpts = [
			{
				...MessengerOptions.AON_MESSENGER_LIST_OPEN,
				fn: () =>{
					this._filter.task_holder = undefined;
					this._filter.sender = undefined;
					this._filter.workgroup = undefined;
					this._filter.status = TASK_STATUS.PENDING;
					this._filter.workgroups = this.getWorkgroupsStr();
					this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined,  this._filter);
				}
			},
			{
				...MessengerOptions.AON_MESSENGER_LIST_CLOSE,
				fn: () =>{
					this._filter.task_holder = undefined;
					this._filter.sender = undefined;
					this._filter.workgroup = undefined;
					this._filter.status = TASK_STATUS.FINISHED;
					this._filter.workgroups = this.getWorkgroupsStr();
					this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined, this._filter);
				}
			},
			{
				...MessengerOptions.AON_MESSENGER_LIST_ARCHIVE,
				fn: () =>{
					this._filter.task_holder = undefined;
					this._filter.sender = undefined;
					this._filter.workgroup = undefined;
					this._filter.status = TASK_STATUS.DELETED;
					this._filter.workgroups = this.getWorkgroupsStr();
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

	loadWorkgroup() {
		let application = this.applicationEl;

		this.getMyWorkgroups().then( workgroups => {
		  this.clearElementById(application.SIDENAV+'WorkgroupList');
		  let options = [];
			options.push({
				name: "SIN ASIGNAR",
				icon: MATERIAL_ICONS.GROUP_OFF,
				fn: () => {
					this._filter.task_holder = undefined;
					this._filter.sender = undefined;
					this._filter.workgroup = true;
					this._filter.status = TASK_STATUS.PENDING;
					this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined, this._filter);
				}
			});
	
		  workgroups.forEach(item => 
			options.push({
				name: item.description,
				icon: MATERIAL_ICONS.PEOPLE_ALT,
				fn: () => {
					this._filter.task_holder = undefined;
					this._filter.sender = undefined;
					this._filter.status = TASK_STATUS.PENDING;
					this._filter.workgroup = item.id;
					this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined, this._filter);
				}
			})
		  );

			application.addSidenavOptionsList({
				id: 'Workgroup',
				name: MSG.WORKGROUP.toUpperCase()
			}, options);
		});
	}


    tagNavBar() {
		let application = this.applicationEl;
		const fnTag = this.getDur().isMessengerManager() ? () => this.dialogTag() : null; 
		application.addSidenavOptions2({
			id: 'Tag',
			name: MSG.TAG
		}, [], fnTag);
		this.loadTag();
	}

	loadTag() {
		let application = this.applicationEl;
		const manager =  this.getDur().isMessengerManager();
		getTaskTags({type:TAG_TYPE.TASK_LABEL}).then(tags => {
		  this._tags =  tags.map(t => ({...t,value: t.id, description: t.name, name:t.name}));
		  this.clearElementById(application.SIDENAV+'TagList');
		  this._tags.forEach(item => {
			let option = {
				name: item.description,
				icon: MATERIAL_ICONS.LABEL,
				actions:[],
				fn: () => {
					this._filter.task_holder = undefined;
					this._filter.sender = undefined;
					this._filter.workgroup = undefined;
					this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined, {...this._filter, search:item.description});
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
					this.loadTag();
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
				this.loadTag();
			});
		});
		d.open();
	}

	updateCount(){
		let application = this.applicationEl;
		let filterCount= {};
		if(this.cauInfo && this.cauInfo.auth)
			filterCount.email = this.cauInfo.auth.email;
		if(this.TASK_HOLDER.id)
			filterCount.task_holder = this.TASK_HOLDER.id;
			
		getTaskCount(filterCount).then(count=>{
			let sender =  count.sender || 0;
			let task_holder =  count.task_holder || 0;
			application.updateSidenavCount(MSG.SENT, sender);
			application.updateSidenavCount("Recibidas", task_holder);
		});

		let filter = {};
		const workgroupStr = this.getWorkgroupsStr();
		if(workgroupStr)
			filter.workgroups = workgroupStr;

		if(this._filter.source) 
			filter.source = this._filter.source;

		if((!this.TASK_HOLDER.id || this.cau) && this.cauInfo)
			filter.email = this.cauInfo.auth.email;
		else if(this.TASK_HOLDER.id && !this.getDur().isMessengerManager())
			filter.task_holder = this.TASK_HOLDER.id;

		getTaskStatusCount(filter).then(resp=>{
			let openCount = resp[TASK_STATUS.PENDING];
			let archiveCount = resp[TASK_STATUS.DELETED];
			let closeCount = resp[TASK_STATUS.FINISHED];
			
			if(resp[TASK_STATUS.IN_PROGRESS]) 
				openCount = openCount + resp[TASK_STATUS.IN_PROGRESS];
	
			//----------------------- UPDATE COUNT---------------
			let listOpen = MessengerOptions.AON_MESSENGER_LIST_OPEN;
			application.updateSidenavCount(listOpen.id, openCount);

			let listClose = MessengerOptions.AON_MESSENGER_LIST_CLOSE;
			application.updateSidenavCount(listClose.id, closeCount);
			
			let listTrash = MessengerOptions.AON_MESSENGER_LIST_ARCHIVE;
			application.updateSidenavCount(listTrash.id, archiveCount);
		});
	}

	getDur(){
		return this.dur;
	}
	
	getWorkgroupsStr(){
		let isManager = this.getDur().isMessengerManager();
		if(isManager) return undefined;
		const wps = this._workgroups.map(({id})=> id)
		return wps.length ? wps.join(",") : 0;
	}

	//MY WORKGROUPRS
	async getMyWorkgroups(){
		if(!this._workgroups.length){
			let isManager = this.getDur().isMessengerManager();
			let filter = {status:"ACTIVE"};
			if(!isManager && this.TASK_HOLDER.id) 
				filter.task_holder = this.TASK_HOLDER.id;
			await getWorkgroups(filter).then( workgroup => {
				this._workgroups = workgroup.map(t => ({...t, value: t.id, description: t.description, name:t.description}));
			});
		}
		return this._workgroups;
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