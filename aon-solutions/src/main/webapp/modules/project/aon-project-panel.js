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
import { ProjectUtils } from './ProjectUtils.js';

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
        this.getApplication().addSidenavOptions2(DocumentalSidenav.TYPES, [], () => ProjectUtils.buildDialogProjectType(this));
        this.loadProjectType();
    }
  
    loadProjectType() {
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
                      action: () => ProjectUtils.projectTypeDelete(this, item)
                    },{
                      id: 'Edit',
                      icon: 'edit',
                      action: () =>  ProjectUtils.buildDialogProjectType(this, item)
                    }]
                };
                this.getApplication().addSidenavOptionsListValue(DocumentalSidenav.TYPES, option);
           });
       });
    }
}
if(!window.customElements.get(TAG.AON_PROJECT_PANEL)) {
	window.customElements.define(TAG.AON_PROJECT_PANEL, AonProjectPanel);
}