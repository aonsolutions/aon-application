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
import { getTastHolders } from '../../services/taskHolderService.js';
import { Project } from '../../models/project/Project.js';
import { TaskHolder } from '../../models/project/TaskHolder.js';

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
		this.project = new Project(this.project);
	}

	build() {
		let toolbar = new AonToolbar();
		toolbar.id = this.PROJECT_TOOLBAR;
		toolbar.type = ToolbarType.SECONDARY;
		toolbar.title = this.project.id 
            ? (this.project.name + ' - ' + this.project.type.description)
            : 'NUEVO EXPEDIENTE';
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
		let card = this.createCard(this.PROJECT_CARD, MSG.GENERAL_INFORMATION);
		parent.appendChild(card);

		let div = this.createElement(TAG.DIV);
		card.setContent(div);

		let table = new AonBasicTable();
		table.id = this.PROJECT_TABLE;
		div.appendChild(table);

		table.addRow();

		let typeSelect = this.createSelect(this.PROJECT_TYPE, MSG.TYPE);
		typeSelect.setAlias('id', 'description');
        getProjectTypes({}).then( types => {
            typeSelect.setOptions(types);
			typeSelect.value = this.project.type.id;
		});
		typeSelect.addEventListener(EVENT.CHANGE, (e) => this.project.setType(typeSelect.getDetail()));
        table.addCell(typeSelect, 2);
		
        table.addRow();

		let nameInput = this.createInput(this.PROJECT_NAME, MSG.NAME);
		nameInput.value = this.project.name;
		nameInput.addEventListener(EVENT.CHANGE, () => this.project.setName(nameInput.value));
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
			this.project.setRegistry(registry.getRegistry());
			if(this.autosave) this.save();
		});
		registry.addEventListener(EVENT.SELECT, () => {
			this.project.setRegistry(registry.getRegistry());
			if(this.autosave) this.save();
		});
		table.addCell(registry,2);
		registry.showAddress = false;

	    // ----- PROJECT HOLDER

		table.addRow();
		let workgroupSelect = this.createSelect(this.PROJECT_WORKGROUP, MSG.WORKGROUP);
		workgroupSelect.setAlias('id', 'description');
		getWorkgroups({}).then(workgroups => {
			workgroupSelect.setOptions(workgroups);
			workgroupSelect.value = this.project.getProjectHolder().getWorkgroup().getId();
		});
		workgroupSelect.addEventListener(EVENT.CHANGE, (e) => this.project.getProjectHolder().setWorkgroup(workgroupSelect.getDetail()));

		table.addCell(workgroupSelect);
		
		let taskHolderSelect = this.createSelect(this.PROJECT_TASK_HOLDER, 'Asignar a');
		taskHolderSelect.setValueAlias('id');
		let data = {workgroup: this.project.getProjectHolder().getWorkgroup().getId()};
		getTastHolders(data).then(taskHolders => {
			taskHolderSelect.setOptions(taskHolders);
			taskHolderSelect.value = this.project.getProjectHolder().getTaskHolder().getId();
		});
		taskHolderSelect.addEventListener(EVENT.CHANGE, (e) => this.project.getProjectHolder().setTaskHolder(taskHolderSelect.getDetail()));

		table.addCell(taskHolderSelect);
	}
	
	// ACTIONS

	back() {
		this.getApplication().getParent().buildContent();
	}

	save() {
		saveProject(this.project).then(project => this.setProject(project));
	}

	setProject(project) {
		this.project = new Project(project);
	}

	// Create Components

	createCard(id, title) {
		let card = new AonCard();
		card.id = id;
		card.title = title;
		card.style.width = '50%';
		return card;
	}

	createSelect(id, title) {
		let select = new AonSelect();
		select.id = id;
		select.title = title;
		return select;
	}

	createInput(id, title) {
		let select = new AonInput();
		select.id = id;
		select.description = title;
		return select;
	}
}

if(!window.customElements.get(TAG.AON_PROJECT)){
	window.customElements.define(TAG.AON_PROJECT, AonProject);
}