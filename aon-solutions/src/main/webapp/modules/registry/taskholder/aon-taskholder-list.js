import { EVENT, MSG } from '../../../environments/environments.js';
import { TaskHolder } from '../../../models/registry/TaskHolder.js';
import { CreateComponent } from '../../../components/CreateComponent.js';
import { getTaskHolderNoCache, getTastHoldersList, getUserList, saveTastHolder} from '../../../services/service.js';
import { AonRegistryList } from '../aon-registry-list.js';
import { AonTaskHolder } from './aon-taskholder.js';
import * as ACTION from '../../actions.js';

export class AonTaskHolderList extends AonRegistryList {

	build(){
		this.filter = {
			page: 1,
			perPage: 50		
		}
		super.build();

		this.buildToolbar();
	}

	getRegistries() {
		return getTastHoldersList(this.filter);
	}

	async buildRegistry(registry) {
		let data = {
			id: registry.id,
			additional_info: ['ADDRESSES', 'MEDIA']
		};
		const r = await getTaskHolderNoCache(data);
		let aonTaskHolder = new AonTaskHolder();
		aonTaskHolder.id = this.getApplication().id + 'TaskHolder';
		aonTaskHolder.setTaskHolder(r);
		this.getApplication().setContent(aonTaskHolder);
	}

	buildToolbar() {
		this.getApplication().removeToolbarOptions();
		this.getApplication().addToolbarOption2(ACTION.ADD, () => this.add());
		this.buildSearch();
	}

	buildSearch(){
		let timeOut = null;
	
		const btnSearch = this.getApplication().addSearchOption();
	
		btnSearch.addEventListener(EVENT.SEARCH, ({detail}) => {
			clearTimeout(timeOut);
			timeOut = setTimeout(() => {
			  this.setFilter({active:true, search:detail});
			}, 300);
		});
	}

	add() {
		const application = this.getApplication();
		const dialog = application.getDialog();
		dialog.clear();
		dialog.setTitle("Registrar operario");

		if(this.isMobile()){
			dialog.type = "fullscreen";
		} else {
			dialog.width = "300px";
		}
	
		const div = document.createElement("div");
		dialog.setContent(div);
	
		const aonSelect = CreateComponent.createAonSelect({
		  attributes:{
			  name:"user",
			  id:"user",
			  autocomplete: "off",
			  title:MSG.USERS
		  }
		}, div);
	
		
		aonSelect.loading(true);

		getUserList({task_holder_empty:true, filter:"all"})
		.then(users =>{
		  const options = users.map(user =>({...user, value: user.id, name: user.name+" "+user.surname}));
		  aonSelect.setOptions(options)
		})
		.finally(() =>{
		  aonSelect.loading(false);
		});
	
		dialog.addSendAction(() =>{
		  const detail = aonSelect.getDetail();
			if(detail.id){
			  application.startLoading();
			  
			  let taskHolder = new TaskHolder();
			  taskHolder.setUser(detail.id);
			  taskHolder.setDocument(detail.document);
			  taskHolder.setName(detail.name);
			  
			  saveTastHolder(taskHolder)
			  .then(th => {
				this.showMessage();
				this.buildRegistry(th);
			  })
			  .catch(err => this.showError(err))
			  .finally(() =>{
				dialog.close();
				application.stopLoading();
			  })
			}
		  }, 
		  MSG.SAVE
		);
	
		dialog.open();
	}
}

if(!window.customElements.get("aon-taskholder-list")) {
	window.customElements.define("aon-taskholder-list", AonTaskHolderList);
}
