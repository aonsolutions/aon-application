import {AonElement} from '../../components/AonElement.js';
import { AonApplication } from '../../components/aon-application.js';
import { CONSTANT, EVENT, TAG } from '../../environments/environments.js';
import { AonProjectList } from './aon-project-list.js';
import { AonProject } from './aon-project.js';
import { DocumentalSidenav } from '../documental/DocumentalEnums.js';
import { getProjectTypes } from '../../services/projectService.js';
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
        let application = this.getApplication();
        let timeOut = undefined;
        application.removeToolbarOptions();
        application.addToolbarOption('Add', 'add', () => this.addProject());

        const btnSearch = application.addSearchOption();
        btnSearch.addEventListener(EVENT.SEARCH_NEW, ({detail}) => {
            clearTimeout(timeOut);
            timeOut = setTimeout(() => {
                this.buildContent({
                    page: 1,
                    perPage: 50,
                    search: detail.search	
                });
            }, 300);
        });
    }

    buildSidenav() {
        this.addTypeOptions();
    }

    buildContent(filter) {
        let list = new AonProjectList();
        list.id = this.AON_PROJECT_LIST;
        if(filter){
            list.filter = filter;
        }
        this.getApplication().setContent(list);
    }

    addProject() {
        let aonProject = new AonProject();
		aonProject.id = this.getApplication().id + 'Project';
		this.getApplication().setContent(aonProject);
    }

    addTypeOptions() {
        let data = DocumentalSidenav.TYPES;
        data.options = [];
        this.getApplication().addSidenavOptions3(data, () => ProjectUtils.buildDialogProjectType(this));
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