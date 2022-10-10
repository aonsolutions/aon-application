import {AonElement} from '../../components/AonElement.js';

import { CSS, EVENT, MATERIAL_ICONS, MSG, TAG} from '../../environments/environments.js';
import { AonTable } from '../../components/aon-table.js';
import { AonProject } from './aon-project.js';
import { getProjects } from '../../services/projectService.js';
import { AonSearch } from '../../components/aon-search.js';
import { AonIconButton } from '../../components/aon-icon-button.js';
import { AonHolderSimpleList} from './aon-holder-simple-list.js';

export class AonProjectList extends AonElement {

	AON_PROJECT_TABLE;
	more;
	filter;	
	TABLE;
	holders;
	HOLDERS_DIV;

	connectedCallback () {
		this.initialize();
		this.build();
 	}

	initialize() {
		this.more = true;
		this.holders = [];
		this.filter = this.filter || {
			page: 1,
			perPage: 50		
		}

	}

	build() {
		this.TABLE = new AonTable();
		this.TABLE.style.width = "100%";
		this.TABLE.id = 'aonRegistryTable';

		if(this.viewholders){
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
		this.TABLE.addColumnIcon(MATERIAL_ICONS.ADD, "string", "lettersHtml", "1%", ()=>{
			this.getApplication().development();
			console.log("add");
		});

        this.HOLDERS_DIV = this.createElement(TAG.DIV);
        this.HOLDERS_DIV.style.display = 'none';
        this.HOLDERS_DIV.style.width = "50%";
        this.HOLDERS_DIV.style.borderLeft = '1px solid #ddd';
        div.appendChild(this.HOLDERS_DIV);
	}

	loadMore() {
		this.more = false;
		if(this.TABLE && this.filter.page) {
			this.filter.page = this.filter.page + 1;
			getProjects(this.filter)
			.then(projects => {
				if(projects.length > 0)
					this.more = true;
				projects.forEach((project, i) => {
					this.TABLE.addRow(project, () => this.buildProject(project));
				});
			});
		}
	}

	init() {
		if(this.TABLE) {
			this.TABLE.removeRows();
			getProjects(this.getFilter())
			.then(projects => {
				projects.map(p => ({...p, typeName:p.type.description, registryName:p.registry.name}))
				.forEach((project, i) => {
					this.TABLE.addRow(project, () => this.buildProject(project));
				});
			});	
		}
	}

	buildProject(project) {
		if(this.viewholders){
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
        div.style.height = '48px';
        this.HOLDERS_DIV.appendChild(div);
        
        let span = this.createElement(TAG.SPAN);
        span.innerHTML = project.name;
        span.style.position = 'absolute';
        span.style.margin =  '20px';
        span.style.fontWeight = '500';
        span.style.color = 'rgb(95, 99, 104)';
        div.appendChild(span);


        let holderIcon = new AonIconButton();
        holderIcon.id =  'holderIcon';
        holderIcon.icon = MATERIAL_ICONS.ADD;
        holderIcon.style.top = '6px';
		holderIcon.style.position = 'absolute';
        holderIcon.classList.add(CSS.AON_RIGHT_20);
        div.appendChild(holderIcon);


		const holderList = this.loadHolderList(project);
		this.HOLDERS_DIV.appendChild(holderList);


		holderIcon.addEventListener(EVENT.CLICK, () => {
			holderList.buildAdd({project: project.id});
        });
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
}

if(!window.customElements.get("aon-project-list")) {
	window.customElements.define("aon-project-list", AonProjectList);
}
