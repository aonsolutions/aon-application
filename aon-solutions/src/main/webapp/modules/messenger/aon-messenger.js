import { AonApplication } from '../../components/aon-application.js';
import { AonElement } from '../../components/AonElement.js';
import { MATERIAL_ICONS, MSG } from '../../environments/environments.js';
import Apps from '../../services/app.js';
import {getWorkgroups} from '../../services/workgroupService.js';
import { AonMessengerChat } from './aon-messeger-chat.js';
import { AonMessengerList } from './aon-messenger-list.js';
import { MessengerOptions, MESSENGER_VIEWS, TASK_STATUS } from './MessengerEnums.js';
import { getTaskHolder } from '../../services/taskHolderService.js';
import { getTaskStatusCount, getTaskOne, getCauInfo, getTaskCount, getTaskTags, saveTaskTag, deleteTaskTag } from '../../services/taskService.js';
import { AonInput } from '../../components/aon-input.js';
// import { AonMessengerAyudat } from './aon-messenger-ayudat.js';

export class AonMessenger extends AonElement {
    AON_MESSENGER;
	_workgroups;
	_tags;
	_filter={};
	TASK_HOLDER;
	cau; //BOOLEAN
	cauData;
	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
		// this.rootPanel(new AonMessengerAyudat());
    	this.build();
 	}

	initialize(){
		this.AON_MESSENGER = MESSENGER_VIEWS.AON_MESSENGER;
		this._workgroups = [];
		this._tags = [];
		this.TASK_HOLDER = {};
		this._filter = {
			workgroup: undefined,
			task_holder: this._filter.task_holder || undefined,
			sender: this._filter.sender || undefined,
			source: this._filter.source || undefined,
			status: TASK_STATUS.PENDING,
			page:0, 
			perPage:30
		};
	}

 	async build() {
		if(this.cau)
			this.cauData = await getCauInfo();
			
		localStorage.setItem("taskCau", this.cau ? 1 : 0);

		this.paintView();
		this.applicationEl = this.getApplication();
		this.applicationParentEl = this.getApplicationParent();
		this.buildToolbar();
		
		await getTaskHolder({reload:false}).then(task=>{
			this.TASK_HOLDER = task;
			this.updateCount();
		});
		if(this.data){
			this.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, this.data);
		} else if(this.value){
			const task = await getTaskOne({id:this.value});
			this.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, task);
		} else {
			this._filter.task_holder = this.TASK_HOLDER.id;
			this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined, this._filter);
			this.applicationEl.addToolbarTitle("Recibidas");
		}
	}

	paintView(){
		this.createApplication(this.AON_MESSENGER, MSG.TASKS, new AonApplication());
	}

	async buildToolbar(){
		if(this.isMobile()){
			this.applicationEl.addMobileSidenavHeader(Apps.MESSENGER);
		}

		this.taskNavBar();
		this.statusNavBar();
		if(!this.cau)
			this.groupNavBar();

		this.tagNavBar();
	}

	taskNavBar(){
		let messengerOpts = [
			{
				name: 'Recibidas',
				icon: MATERIAL_ICONS.MOVE_TO_INBOX,
				id: MATERIAL_ICONS.MOVE_TO_INBOX,
				fn: () =>{
					this._filter.task_holder = this.TASK_HOLDER.id;
					this._filter.sender = undefined;
					this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined,  this._filter);
				}
			},
			{
				name: MSG.SENT,
				icon: MATERIAL_ICONS.OUTBOX,
				id: MATERIAL_ICONS.OUTBOX,
				fn: () =>{
					this._filter.task_holder = undefined;
					this._filter.sender = this.TASK_HOLDER.id;
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
					this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined,  this._filter);
				}
			},
		];
		
		this.applicationEl.addSidenavOptions(MSG.TASKS, messengerOpts);
	}

	statusNavBar(){
		let messengerOpts = [
			{
				...MessengerOptions.AON_MESSENGER_LIST_OPEN,
				fn: () =>{
					let status = TASK_STATUS.PENDING;
					if(this._filter.status && this._filter.status === status) status = undefined;
					this._filter.status = status;
					this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined,  this._filter);
				}
			},
			{
				...MessengerOptions.AON_MESSENGER_LIST_CLOSE,
				fn: () =>{
					let status = TASK_STATUS.FINISHED;
					if(this._filter.status && this._filter.status === status) status = undefined;
					this._filter.status = status;
					this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined, this._filter);
				}
			},
			{
				...MessengerOptions.AON_MESSENGER_LIST_ARCHIVE,
				fn: () =>{
					let status = TASK_STATUS.DELETED;
					if(this._filter.status && this._filter.status === status) status = undefined;
					this._filter.status = status;
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
		this.loadWorkgroup();
	}

	loadWorkgroup() {
		let application = this.applicationEl;
		getWorkgroups({status:"ACTIVE"}).then( workgroup => {
		  this._workgroups = workgroup.map(t => ({value: t.id, description: t.description, name:t.description}));
		  this.clearElementById(application.SIDENAV+'WorkgroupList');
		  this._workgroups.forEach(item => {
			let option = {
				name: item.description,
				icon: 'people_alt',
				fn: () => {
					let id = item.id;
					if(this._filter.workgroup && this._filter.workgroup === item.id) id = undefined;
					this._filter.workgroup = id;
					this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined, this._filter);
				}
			};

			application.addSidenavOptionsListValue({
				id: 'Workgroup',
				name: MSG.WORKGROUP.toUpperCase()
			}, option);
		  });
		});
	}


    tagNavBar() {
		let application = this.applicationEl;
		application.addSidenavOptions2({
			id: 'Tag',
			name: MSG.TAG
		}, [],() =>this.dialogTag());
		this.loadTag();
	}

	loadTag() {
		let application = this.applicationEl;
		getTaskTags({type:"task_label"}).then(tags => {
		  this._tags =  tags.map(t => ({...t,value: t.id, description: t.name, name:t.name}));
		  this.clearElementById(application.SIDENAV+'TagList');
		  this._tags.forEach(item => {
			let option = {
				name: item.description,
				icon: 'label',
				fn: () => {},
				actions:[
					{
						id: 'Delete',
						icon: 'delete',
						action: () => this.deleteTag(item)
					},
					{
						id: 'Edit',
						icon: 'edit',
						action: () => this.dialogTag(item)
					}
				]
			};

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
				tag.type = "task_label";
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
		let filter = {};
		if(this._filter.source) filter.source = this._filter.source;
		
		if(this.TASK_HOLDER && this.TASK_HOLDER.id){
			getTaskCount({task_holder:this.TASK_HOLDER.id}).then(count=>{
				let sender =  count.sender || 0;
				let task_holder =  count.task_holder || 0;
				application.updateSidenavCount("Enviadas", sender);
				application.updateSidenavCount("Recibidas", task_holder);
			});
		}

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
}
window.customElements.define('aon-messenger', AonMessenger);
