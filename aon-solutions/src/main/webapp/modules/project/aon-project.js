import {AonElement} from '../../components/AonElement.js';
import {RegistryType, ToolbarType} from '../../models/enums.js';

import '../../components/aon-address.js';
import '../../components/aon-input.js';

import {AonToolbar} from "../../components/aon-toolbar.js";
import {AonCard} from "../../components/aon-card.js";

import {CONSTANT, EVENT, MSG, TAG } from '../../environments/environments.js'; 

import * as ACTION from '../actions.js';
import { AonBasicTable } from '../../components/aon-basic-table.js';
import { AonInput } from '../../components/aon-input.js';
import { AonRegistry } from '../../components/aon-registry.js';
import { AonSelect } from '../../components/aon-select.js';
import { getProjectTypes, saveProject } from '../../services/projectService.js';

import * as LS from '../../services/localStorageService.js';
import { getWorkgroups } from '../../services/workgroupService.js';

export class AonProject extends AonElement {
    PROJECT_TOOLBAR;
    PROJECT_CARD;
    PROJECT_TABLE;
    PROJECT_TYPE;
    PROJECT_NAME;
    PROJECT_REGISTRY;
	PROJECT_WORKGROUP;
	PROJECT_TASK_HOLDER;

	project;

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
		this.build();
    } 

	initialize() {
		this.id = this.id || 'aonProject';
		this.PROJECT_TOOLBAR = this.id + 'Toolbar';
        this.PROJECT_CARD = this.id + 'Card';
		this.PROJECT_TABLE = this.id + 'Table';
        this.PROJECT_TYPE = this.id + 'Type';
        this.PROJECT_NAME = this.id + 'Name';
        this.PROJECT_REGISTRY = this.id + 'Registry';
		this.PROJECT_WORKGROUP = this.id + 'Workgroup';
		this.PROJECT_TASK_HOLDER = this.id + 'TaskHolder';
        this.project = this.project || {
			name: '',
			domain: {id: LS.getDomainId(), name: LS.getDomainName()},
            type: {},
            registry: {},
            alias: '',
            date: new Date().getTime(),
			active: true
		}
	}

	build() {
		let toolbar = new AonToolbar();
		toolbar.id = this.PROJECT_TOOLBAR;
		toolbar.type = ToolbarType.SECONDARY;
		toolbar.title = this.project.id 
            ? (this.project.name + ' - ' + this.project.type.description)
            : 'NUEVO PROYECTO';
		this.appendChild(toolbar);
		toolbar.addButton2(ACTION.SAVE, () => this.save());
		toolbar.addButton2(ACTION.BACK, () => this.back());

		let div = this.createElement(TAG.DIV);
		div.style.display = "flex";
		div.style.width = "100%";
		this.appendChild(div);

		this.buildGeneralCard(div);
	}

	buildGeneralCard(parent){
		let card = new AonCard();
		card.id = this.PROJECT_CARD;
		card.title = MSG.GENERAL_INFORMATION;
		card.style.width = '50%';
		parent.appendChild(card);

		let div = this.createElement(TAG.DIV);
		card.setContent(div);

		let table = new AonBasicTable();
		table.id = this.PROJECT_TABLE;
		div.appendChild(table);

		table.addRow();

		let typeInput = new AonSelect();
		typeInput.id = this.PROJECT_TYPE;
		typeInput.title = MSG.TYPE;
        getProjectTypes({}).then( types => {
            typeInput.options =  JSON.stringify(types.map(t => {
                t.value = t.id;
                t.name = t.description;
                return t;
            }));
			typeInput.value = this.project.type.id;
		});
		typeInput.addEventListener(EVENT.CHANGE, (e) => this.project.type = typeInput.getDetail());
        table.addCell(typeInput, 2);
		
        table.addRow();
		let nameInput = new AonInput();
		nameInput.id = this.PROJECT_NAME;
		nameInput.description = MSG.NAME;
		nameInput.value = this.project.name;
		nameInput.addEventListener(EVENT.CHANGE, () => this.project.name = nameInput.value);
        table.addCell(nameInput, 2);
		
        table.addRow();


        // ----- REGISTRY

		let registry = new AonRegistry();
		registry.showAddress = false;
		registry.id = this.PROJECT_REGISTRY;
		registry.types = RegistryType.CUSTOMER;
		registry.value = this.project.registry;
		registry.setRegistry(this.project.registry);
		registry.addEventListener(EVENT.CHANGE, () => {
			this.project.registry = registry.getRegistry();
			if(this.autosave) this.save();
		});
		registry.addEventListener(EVENT.SELECT, () => {
			this.project.registry = registry.getRegistry();
			if(this.autosave) this.save();
		});
		table.addCell(registry,2);
		registry.showAddress = false;

	    // ----- PROJECT HOLDER

		table.addRow();
		let workgroupSelect = new AonSelect();
		workgroupSelect.id = this.PROJECT_WORKGROUP;
		workgroupSelect.title = MSG.WORKGROUP;
		getWorkgroups({}).then(workgroups => {
			workgroupSelect.options =  JSON.stringify(workgroups
			.map(t => {
         		t.value = t.id;
         		t.name = t.description;
				return t;
			}));
			//workgroupSelect.value = this.project.type.id;
		});
		table.addCell(workgroupSelect);
		
		let taskHolderSelect = new AonSelect();
		taskHolderSelect.id = this.PROJECT_TASK_HOLDER;
		taskHolderSelect.title = 'Asignar a';
		
		table.addCell(taskHolderSelect);

	}
	
	// ACTIONS

	back() {
		this.getApplication().getParent().buildContent();
	}

	save() {
		saveProject(this.project).then(project => this.project = project);
	}
}

if(!window.customElements.get(TAG.AON_PROJECT)){
	window.customElements.define(TAG.AON_PROJECT, AonProject);
}