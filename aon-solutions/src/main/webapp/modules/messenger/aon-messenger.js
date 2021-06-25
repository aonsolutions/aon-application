import { AonApplication } from '../../components/aon-application.js';
import { AonInput } from '../../components/aon-input.js';
import { AonElement } from '../../components/AonElement.js';
import { MSG } from '../../environments/environments.js';
import Apps from '../../services/app.js';
import {getWorkgroups, saveWorkgroup, deleteWorkgroup} from '../../services/workgroupService.js';
import { AonMessengerChat } from './aon-messeger-chat.js';
import { AonMessengerList } from './aon-messenger-list.js';
import { MessengerOptions, MESSENGER_VIEWS, REQUEST_FILTER } from './MessengerEnums.js';

export class AonMessenger extends AonElement {
    AON_MESSENGER;
	FILTER;
	_workgroups;
	_filter;

	constructor () {
		super();
		this.FILTER = REQUEST_FILTER.ABIERTAS;
	}

	get filter() {
		return JSON.parse(this.FILTER);
	}

	set filter(filter){
		this.FILTER = JSON.stringify(filter);
	}

	connectedCallback () {
		this.initialize();
    	this.build();
 	}

	initialize(){
		this.AON_MESSENGER = MESSENGER_VIEWS.AON_MESSENGER;
		this._workgroups = [];
		this._filter = {
			workgroup: undefined
		};

	}

 	build() {
		this.paintView();
		this.applicationEl = this.getApplication();
		this.applicationParentEl = this.getApplicationParent();
		this.buildToolbar();
		this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST);
	}

	paintView(){
		this.createApplication(this.AON_MESSENGER, "Solicitudes", new AonApplication());
	}

	buildToolbar(){
		if(this.isMobile()){
			this.applicationEl.addMobileSidenavHeader(Apps.MESSENGER);
		}
        let messengerOpts = [];

		let list = MessengerOptions.AON_MESSENGER_LIST;
		list.fn = () => this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST);
		messengerOpts.push(list);

		let listClose = MessengerOptions.AON_MESSENGER_LIST_CLOSE;
		listClose.fn = () => this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST_CLOSE);
		messengerOpts.push(listClose);
		
		this.applicationEl.addSidenavOptions('Solicitudes', messengerOpts);
		this.addWorkGroupOptions();
	}


    addWorkGroupOptions() {
		let application = this.getApplication();
		application.addSidenavOptions2({
			id: 'Workgroup',
			name: MSG.WORKGROUP.toUpperCase()
		}, [], () => this.dialogWorkgroup({status:1}));
		this.loadWorkgroup();
	 }

	loadWorkgroup() {
		let application = this.getApplication();
		getWorkgroups().then( workgroup => {
		  this._workgroups = workgroup.map(t => ({value: t.id, description: t.description, name:t.description}));
		  this.clearElementById(application.SIDENAV+'WorkgroupList');
		  workgroup.forEach((item, i) => {
			let option = {
				name: item.description,
				icon: 'label',
				fn: () => {
					this._filter.workgroup = item.id;
					this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST);
				}
				};
				option.actions = [{
					id: 'Delete',
					icon: 'delete',
					action: () => this.deleteWorkgroup(item)
				},{
					id: 'Edit',
					icon: 'edit',
					action: () => this.dialogWorkgroup(item)
				}
				];
			application.addSidenavOptionsListValue({
				id: 'Workgroup',
				name: MSG.WORKGROUP.toUpperCase()
			}, option);
		  });
		});
	}

	dialogWorkgroup(workgroup={}) {
		let d = document.getElementById(this.getApplication().DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.EDIT_WORKGROUP);
		let aonInput = new AonInput();
		aonInput.id = "addWorkgroup";
		aonInput.description = MSG.WORKGROUP;
		if(workgroup.description) aonInput.value = workgroup.description;
		d.setContent(aonInput);
		d.addAcceptAction(() => {
			if(aonInput.value){
				const description = aonInput.value;
				saveWorkgroup({...workgroup, description}).then(() => {
					this.loadWorkgroup();
				});
			}
		});
		d.open();
	}

	deleteWorkgroup(workgroup) {
		console.log(workgroup);
		let d = document.getElementById(this.getApplication().DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.DELETE_WORKGROUP);
		d.setContentHTML(`Estás seguro de eliminar el grupo de trabajo ${workgroup.description}`);
		d.addAcceptAction(() => {
			deleteWorkgroup(workgroup).then(() => {
				this.loadWorkgroup();
			});
		});
		d.open();
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
              if(filter) aonView.filter = filter;
              if(data) aonView.data = data;
              this.applicationEl.setContent(aonView);
            }
          resolve(aonView);
        });
    }
}
window.customElements.define('aon-messenger', AonMessenger);
