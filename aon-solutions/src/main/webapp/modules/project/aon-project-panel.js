import {AonElement} from '../../components/AonElement.js';
import { AonApplication } from '../../components/aon-application.js';
import { CONSTANT, MSG, TAG } from '../../environments/environments.js';
import { AonProjectList } from './aon-project-list.js';
import { AonProject } from './aon-project.js';
import { DocumentalSidenav } from '../documental/DocumentalEnums.js';
import { getProjectTypes, saveProjectType } from '../../services/projectService.js';
import { AonInput } from '../../components/aon-input.js';
import * as LS from '../../services/localStorageService.js';
import { ProjectType } from '../../models/project/ProjectType.js';

export class AonProjectPanel extends AonElement {

	AON_PROJECT_PANEL;
    AON_PROJECT_LIST;

	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
    	this.build();
 	}

	initialize(){
		this.AON_PROJECT_PANEL = 'aonProjectPanel';
        this.AON_PROJECT_LIST = this.AON_PROJECT_PANEL + CONSTANT.LIST.initCap();
	}

 	build() {
        this.createApplication(this.AON_PROJECT_PANEL, 'EXPENDIENTES', new AonApplication());
        this.buildToolbar();
        this.buildSidenav();
        this.buildContent();
	}

    buildToolbar() {
        this.getApplication().removeToolbarOptions();
        this.getApplication().addToolbarOption('Add', 'add', () => this.addProject());
    }

    buildSidenav() {
        this.addTypeOptions();
    }


    buildContent() {
        let list = new AonProjectList();
        list.id = this.AON_PROJECT_LIST;
        this.getApplication().setContent(list);
    }

    addProject() {
        let aonProject = new AonProject();
		aonProject.id = this.getApplication().id + 'Project';
		this.getApplication().setContent(aonProject);
    }

    // PROJECT TYPE

    addTypeOptions() {
        this.getApplication().addSidenavOptions2(DocumentalSidenav.TYPES, [], () => this.createType());
        this.loadTypes();
    }
  
    loadTypes() {
        getProjectTypes({}).then(types => {
            this.clearElementById(this.getApplication().SIDENAV + DocumentalSidenav.TYPES.id + 'List');
            types.forEach(item => {
                let option = {
                    name: item.description,
                    icon: 'label',
                    fn: () => {}, 
                    actions: [{
                      id: 'Delete',
                      icon: 'delete',
                      action: () => this.deleteType(item)
                    },{
                      id: 'Edit',
                      icon: 'edit',
                      action: () => this.editType(item)
                    }]
                };
                this.getApplication().addSidenavOptionsListValue(DocumentalSidenav.TYPES, option);
           });
       });
    }

    createType() {
        let d = document.getElementById(this.getApplication().DIALOG);
        d.clear();
        d.width = '400px';
        d.setTitle(MSG.ADD_TYPE);
        let aonInput = new AonInput();
        aonInput.id = this.id + 'AddType';
        aonInput.description = MSG.TYPE;
        d.setContent(aonInput);
        d.addAcceptAction(() => {
            if(!aonInput.value.isEmpty()){
                let data = new ProjectType().setDescription(aonInput.value);
                saveProjectType(data).then(() => {
                    this.loadTypes();
                });
            }
        });
        d.open();
    }
    
    deleteType() {
        alert("DELETE TYPE");
    }

    editType(){
        alert("EDIT TYPE");
    }

}
if(!window.customElements.get(TAG.AON_PROJECT_PANEL)) {
	window.customElements.define(TAG.AON_PROJECT_PANEL, AonProjectPanel);
}