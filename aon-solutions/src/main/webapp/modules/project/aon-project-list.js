import {AonElement} from '../../components/AonElement.js';

import { MSG, TAG} from '../../environments/environments.js';
import { AonTable } from '../../components/aon-table.js';
import { AonProject } from './aon-project.js';
import { getProjects } from '../../services/projectService.js';

export class AonProjectList extends AonElement {

	AON_PROJECT_TABLE;
	more;
	filter;	

	connectedCallback () {
		this.initialize();
		this.build();
 	}

	initialize() {
		this.more = true;
		this.AON_PROJECT_TABLE = 'aonRegistryTable';
		this.filter = {
			page: 1,
			perPage: 50		
		}
	}

	build() {
		let table = new AonTable();
		table.id = this.AON_PROJECT_TABLE;
		this.appendChild(table);

		table.addColumn(MSG.TYPE, 'string', 'typeName', '20%');
		table.addColumn(MSG.NAME, 'string', 'name', '40%');
		table.addColumn(MSG.HOLDER, 'string', 'registryName', '40%');
		table.addEventListener('more', () => {
			if(this.more) this.loadMore()
		});
		this.init();
 	}

	loadMore() {
		this.more = false;
		let table = this.getElement(this.AON_PROJECT_TABLE);
		if(table && this.filter.page) {
			this.filter.page = this.filter.page + 1;
			getProjects(this.filter).then(projects => {
				if(projects.length > 0)
					this.more = true;
				projects.forEach((project, i) => {
					table.addRow(project, () => this.buildProject(project));
				});
			});
		}
	}

	init() {
		let table = this.getElement(this.AON_PROJECT_TABLE);
		if(table) {
			table.removeRows();
			getProjects(this.getFilter()).then(projects => {
				projects.map(p => {
					p.typeName = p.type.description;
					p.registryName = p.registry.name;
					return p; 
				}).forEach((project, i) => {
					table.addRow(project, () => this.buildProject(project));
				});
			});	
		}
	}

	buildProject(project) {
		let aonProject = new AonProject();
		aonProject.id = this.id + 'Project';
		aonProject.project = project;
		this.getApplication().setContent(aonProject);
	}

	getFilter() {
		return this.filter || {};
	}

	setFilter(filter) {
		this.filter = filter;
		this.init();
	}
}

if(!window.customElements.get("aon-project-list")) {
	window.customElements.define("aon-project-list", AonProjectList);
}
