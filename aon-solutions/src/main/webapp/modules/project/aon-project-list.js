import {AonElement} from '../../components/AonElement.js';
import * as ACTION from '../actions.js';
import { CSS, EVENT, MATERIAL_ICONS, MSG, TAG} from '../../environments/environments.js';
import { AonTable } from '../../components/aon-table.js';
import { AonProject } from './aon-project.js';
import { deleteProject, getProjects, getProjectTypes, saveProject } from '../../services/projectService.js';
import { AonIconButton } from '../../components/aon-icon-button.js';
import { AonHolderSimpleList} from './aon-holder-simple-list.js';
import { ProjectUtils } from './ProjectUtils.js';
import { Project } from '../../models/project/Project.js';
import { getWorkgroups } from '../../services/workgroupService.js';
import { getTastHolders } from '../../services/taskHolderService.js';

export class AonProjectList extends AonElement {
	more;
	filter;	
	TABLE;
	holders;
	HOLDERS_DIV;
	projectTypes;
	workgroups;
    taskHolders;

	connectedCallback () {
		this.initialize();
		this.build();
 	}

	initialize() {
		this.more = true;
		this.holders = [];
		this.projectTypes = [];
		this.workgroups = [];
		this.taskHolders = [];
		this.filter = this.filter || {
			page: 1,
			perPage: 50		
		}

	}

	build() {
		this.TABLE = new AonTable();
		this.TABLE.style.width = "100%";
		this.TABLE.id = 'aonRegistryTable';

		if(this.registry){
			this.buildHolderTable();
		} else {
			this.buildTable();
		}
		
		this.TABLE.addEventListener('more', () => {
			if(this.more) {
				this.loadMore()
			}
		});

		this.init();
 	}

	buildTable(){
		this.appendChild(this.TABLE);
		this.TABLE.addColumn(MSG.TYPE, 'string', 'typeName', '20%');
		this.TABLE.addColumn(MSG.NAME, 'string', 'name', '40%');
		this.TABLE.addColumn(MSG.HOLDER, 'string', 'registryName', '40%');
	}

	buildHolderTable(){
		let div = this.createElement(TAG.DIV);
        div.className = CSS.AON_FLEX;
		div.style.position = "relative";
        this.appendChild(div);

		div.appendChild(this.TABLE);
		this.TABLE.addColumn(MSG.TYPE, 'string', 'typeName', '30%');
		this.TABLE.addColumn(MSG.DATE, 'date', 'date', '20%');
		this.TABLE.addColumnIcon({title:MSG.ADD+" expediente", name:MATERIAL_ICONS.ADD, type:"string", width:"5%", id:"lettersHtml"}, 
		()=>{
			let project = new Project();
			if(this.registry){
				project.setRegistry(this.registry);
			}
			ProjectUtils.buildDialogProject(this, project);
		});

        this.HOLDERS_DIV = this.createElement(TAG.DIV);
        div.appendChild(this.HOLDERS_DIV);
		this.clearHolderList();
	}

	loadMore() {
		this.more = false;
		if(this.TABLE && this.filter.page) {
			this.filter.page = this.filter.page + 1;
			this.getData(this.filter)
			.then(projects => {
				if(projects.length > 0)
					this.more = true;
				projects.forEach((project) => {
					this.TABLE.addRow(project, () => this.buildProject(project), (e) => this.aonProjectContextMenu(e, project));
				});
			});
		}
	}

	init() {
		if(this.TABLE) {
			this.TABLE.removeRows();
			this.getData(this.getFilter())
			.then(projects => {
				projects.forEach((project) => {
					this.TABLE.addRow(project, () => this.buildProject(project), (e) => this.aonProjectContextMenu(e, project));
				});
			});	
		}
	}

	async getData(filter){
		try {
			const data = await getProjects(filter);
			return data
			.sort((a, b) => (a.name > b.name) - (a.name < b.name))
			.map(p =>{
				p.typeName = p.type.description;
				p.registryName = p.registry.name;
				return p;
			});
		} catch (error) {
			this.showError(error);
		}
		return [];
	}

	buildProject(project) {
		if(this.registry){
			this.buildHolders(project);
		} else {
			let aonProject = new AonProject();
			aonProject.id = this.id + 'Project';
			aonProject.project = project;
			this.getApplication().setContent(aonProject);
		}
	}

	buildHolders(project) {
        this.HOLDERS_DIV.innerHTML = "";
		this.HOLDERS_DIV.style.display = 'block';
		this.TABLE.style.width = "50%";

        let div = this.createElement(TAG.DIV);
        div.style.borderBottom = '1px solid #ddd';
        div.style.height = '50px';
		div.style.display = 'flex';
        this.HOLDERS_DIV.appendChild(div);
        
        let span = this.createElement(TAG.SPAN);
        span.innerHTML = MSG.ADVISERS+" de "+ project.name;
        span.style.top = '5px';
        span.style.margin =  '20px';
        span.style.fontWeight = '500';
        span.style.color = 'rgb(95, 99, 104)';
        div.appendChild(span);


        let holderIcon = new AonIconButton();
        holderIcon.id =  'holderIcon';
		holderIcon.title = MSG.ADD+" "+MSG.ADVISER;
        holderIcon.icon = MATERIAL_ICONS.ADD;
		holderIcon.style.marginLeft = 'auto';
        holderIcon.style.top = '10px';
		holderIcon.style.position = 'relative';
		holderIcon.style.right = '4px';
        div.appendChild(holderIcon);


		const holderList = this.loadHolderList(project);
		this.HOLDERS_DIV.appendChild(holderList);

		holderIcon.addEventListener(EVENT.CLICK, () => {
			holderList.buildAdd({project: project.id});
        });
    }

	clearHolderList(){
		if(this.TABLE && this.HOLDERS_DIV){
			this.TABLE.style.width = "100%";
			this.HOLDERS_DIV.style.display = 'none';
			this.HOLDERS_DIV.style.width = "50%";
			this.HOLDERS_DIV.style.borderLeft = '1px solid #ddd';
		}
	}

	loadHolderList(project) {
		const id = "holderList";
		let holderList = this.getElement(id) || new AonHolderSimpleList();
        holderList.id = id;
		holderList.filter = {project: project.id, active:true}
  
        return holderList;
    }

	getFilter() {
		return this.filter || {};
	}

	setFilter(filter) {
		this.filter = filter;
		this.init();
	}

    async onSaveProject(p){
        this.getApplication().startLoading();
        try {
			let project = await saveProject(p);
            this.showMessage();
            this.init();
			this.buildProject(project);
        } catch (error) {
            this.showError(error);
        }
        this.getApplication().stopLoading();
    }

	onDeleteProject(project) {
		this.getApplication()
		.confirmDialog(MSG.DELETE, MSG.DELETE_CONFIRM, async ()=>{
			this.getApplication().startLoading();
			try {
				await deleteProject(project);
				this.showToast({ message: MSG.DELETED_DATA });
				this.init();
				this.clearHolderList();
			} catch (error) {
				this.showError(error);
			}
			this.getApplication().stopLoading();	
		});
	}

	aonProjectContextMenu(e, project) {
		e.preventDefault();
		e.stopPropagation();
		let d = this.getApplication().getOptionDialog();
		let rect = e.target.getBoundingClientRect();
    	let x = e.clientX - rect.left;
		let y = e.clientY - rect.top;

	    const top  = rect.top + y;
	    const left = rect.left + x;

		let edit = ACTION.EDIT;
		edit.fn = () =>{
			ProjectUtils.buildDialogProject(this,  new Project(project))
		};

		let remove = ACTION.DELETE;
		remove.fn = () => this.onDeleteProject(project);

		let actions = [edit, remove];
		
		d.setMenuOptions(actions, top, left);
		d.open();
	}

	async getProjectTypes(){
		if(!this.projectTypes.length){
			const types = await getProjectTypes({})
			.catch((error)=> {
				this.showError(error);
				return [];
			});
			this.projectTypes = types.map((r) => ({...r, name: r.description, value: r.id}));
		}
		return this.projectTypes;
	}

    async getWorkgroups(){
		if(this.workgroups.length==0){
			let wgs = await getWorkgroups()
			.catch((error)=> {
				this.showError(error);
				return [];
			});
			this.workgroups = wgs.map((r) => ({...r, name: r.description, value: r.id}))
		} 
		return this.workgroups;
	}

	async getTaskHolders(){
		if(this.taskHolders.length==0){
			let ths = await getTastHolders()
			.catch((error)=> {
				this.showError(error);
				return [];
			});
			this.taskHolders = ths.map((r) => ({...r, value: r.id}))
		} 
		return this.taskHolders;
	}
}

if(!window.customElements.get("aon-project-list")) {
	window.customElements.define("aon-project-list", AonProjectList);
}
