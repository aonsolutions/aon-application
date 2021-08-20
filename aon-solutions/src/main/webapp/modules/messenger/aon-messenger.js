import { AonApplication } from '../../components/aon-application.js';
import { AonElement } from '../../components/AonElement.js';
import { MATERIAL_ICONS, MSG } from '../../environments/environments.js';
import Apps from '../../services/app.js';
import {getWorkgroups} from '../../services/workgroupService.js';
import { AonMessengerChat } from './aon-messeger-chat.js';
import { AonMessengerList } from './aon-messenger-list.js';
import { MessengerOptions, MESSENGER_VIEWS, TASK_STATUS } from './MessengerEnums.js';
import { getTaskHolder } from '../../services/taskHolderService.js';
import { getTaskStatusCount } from '../../services/taskService.js';
// import { AonMessengerAyudat } from './aon-messenger-ayudat.js';

export class AonMessenger extends AonElement {
    AON_MESSENGER;
	_workgroups;
	_filter={};
	TASK_HOLDER;
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
		this.paintView();
		this.applicationEl = this.getApplication();
		this.applicationParentEl = this.getApplicationParent();
		this.buildToolbar();
		
		await getTaskHolder({reload:false}).then(task=>this.TASK_HOLDER = task);

		if(this.data){
			this.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, this.data);
		} else {
			this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined, this._filter);
		}
	}

	paintView(){
		this.createApplication(this.AON_MESSENGER, `${MSG.REQUESTS} / ${MSG.TASKS}`, new AonApplication());
	}

	async buildToolbar(){
		if(this.isMobile()){
			this.applicationEl.addMobileSidenavHeader(Apps.MESSENGER);
		}

		this.taskNavBar();
		this.statusNavBar();
		this.groupNavBar();
	}

	taskNavBar(){
		let messengerOpts = [
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
			{
				name: 'Enviadas',
				icon: MATERIAL_ICONS.OUTBOX,
				id: MATERIAL_ICONS.OUTBOX,
				fn: () =>{
					this._filter.task_holder = undefined;
					this._filter.sender = this.TASK_HOLDER.id;
					this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined,  this._filter);
				}
			},
			{
				name: 'Recibidas',
				icon: MATERIAL_ICONS.MOVE_TO_INBOX,
				id: MATERIAL_ICONS.MOVE_TO_INBOX,
				fn: () =>{
					this._filter.sender = undefined;
					this._filter.task_holder = this.TASK_HOLDER.id;
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
		this.updateStatusSidenavCount();
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
		getWorkgroups().then( workgroup => {
		  this._workgroups = workgroup.map(t => ({value: t.id, description: t.description, name:t.description}));
		  this.clearElementById(application.SIDENAV+'WorkgroupList');
		  workgroup.forEach((item, i) => {
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


	updateStatusSidenavCount(){
		let application = this.applicationEl;
		getTaskStatusCount().then(res=>{
			let openCount = 0;
			let archiveCount = 0;
			let closeCount = 0;
			// for(const key in res){
			// 	console.log(value);
			// }
			res.map(r=>{
				if(r.status ===0)
					archiveCount = r.count;
				else if(r.status ===1 || r.status ===2)
					openCount = r.count;
				else if(r.status ===3)
					closeCount = r.count;
			})
			
			// UPDATE COUNT
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
