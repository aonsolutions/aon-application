import { AonReg } from '../aon-reg.js';
import { EVENT, MATERIAL_ICONS, MSG, TAG } from '../../../environments/environments.js'; 
import { AonTaskHolderList } from './aon-taskholder-list.js';
import { saveTastHolder } from '../../../services/taskHolderService.js';
import { TaskHolder } from '../../../models/registry/TaskHolder.js';
import { AonCard } from '../../../components/aon-card.js';
import { AonBasicTable } from '../../../components/aon-basic-table.js';
import { AonSelect } from '../../../components/aon-select.js';
import { getWorkgroups } from '../../../services/workgroupService.js';
import { AonIconButton } from '../../../components/aon-icon-button.js';
import { Workgroup } from '../../../models/project/Workgroup.js';
import { AonInput } from '../../../components/aon-input.js';

export class AonTaskHolder extends AonReg {

	saveBool;
	workgroups;

	connectedCallback () {
		this.taskHolderInitialize();
		this.initialize();
		this.build();
		this.buildTaskHolderData();
  	}
	
	taskHolderInitialize() {
		this.type = "taskholder";
		this.saveBool = true;
		this.options = [ 
			{ title: MSG.GENERAL_DATA, fn: () => this.buildTaskHolderData()},
			{ title: MSG.WORKGROUP, fn: () => this.buildDataAdditional()}
		];
		this.workgroups = [];
	}

	buildTaskHolderData(){
		let parent = this.getElement(this.DIV);
		this.clearElement(parent);
		this.buildTaskHolderInfo(parent);
	}

	buildTaskHolderInfo(parent) {
		let card = new AonCard();
		card.id = this.GENERAL_CARD;
		card.title = MSG.GENERAL_INFORMATION;
		card.style.width = '50%';
		parent.appendChild(card);
		card.firstChild.firstChild.style.marginBottom = "5px";

		if(this.registry.id && this.registry.status){
			this.buildStatusRegistry();
		}

		let div = this.createElement(TAG.DIV);
		card.setContent(div);

		let table = new AonBasicTable();
		table.id = this.GENERAL_TABLE;
		div.appendChild(table);

		table.addRow();

		let nameInput = new AonInput();
		nameInput.id = 'aonConfigurationGeneralName';
		nameInput.description = MSG.NAME;
		nameInput.value = this.registry.getName();
		nameInput.addEventListener(EVENT.CHANGE, () => this.registry.setName(nameInput.value));
		table.addCell(nameInput);

		let documentInput = new AonInput();
		documentInput.id = 'aonConfigurationGeneralNif';
		documentInput.description = MSG.NIF;
		documentInput.value = this.registry.getDocument();
		documentInput.addEventListener(EVENT.CHANGE, () => this.registry.setDocument(documentInput.value));

		let td = table.addCell(documentInput);
		td.style.width = '30%';

		table.addRow();

		this.buildEmails(table);
		this.buildPhones(table);
	}	

	buildDataAdditional(){
		let parent = this.getElement(this.DIV);
		this.clearElement(parent);
		this.buildGeneralInformation(parent);
	}

	buildGeneralInformation(parent) {
		let card = new AonCard();
		card.id = "cardAdditionalInformation";
		card.title = MSG.WORKGROUP;
		card.style.width = '50%';
		parent.appendChild(card);

		let div = this.createElement(TAG.DIV);
		card.setContent(div);

		let table = new AonBasicTable();
		card.id = "aonTableGeneralInformation";
		div.appendChild(table);

		table.addRow();
		let divWorkgroup = this.createElement(TAG.DIV);
		table.addCell(divWorkgroup);
		this.buildWorkgroups(divWorkgroup);
	}	

	//WORKGROUPS
	buildWorkgroups(parent) {
		const table =  new AonBasicTable();
		table.id =  "workgroupTable";
		parent.appendChild(table);

		let workgroups = this.registry.getWorkgroups()
		.map(w =>  new Workgroup(w))
		.filter(w => !w.isRemoved());

		if(!workgroups || workgroups.length <= 0){
			let workgroup = new Workgroup();
			workgroup.setDomain(this.registry.getDomain().getId());
			this.registry.addWorkgroup(workgroup);
			this.buildWorkgroup(table, workgroup, 0);
		} else {
			this.registry.setWorkgroups([]);
			workgroups.forEach( (workgroup, i) =>{
				this.registry.addWorkgroup(workgroup);
				this.buildWorkgroup(table, workgroup, i)
			});
		}
	}
	
	buildWorkgroup(table, workgroup, i) {
		const rowNum = table.addRow();

		const rowCount = table.getRowsCount();
		
		let workgroupSelect = new AonSelect();
		workgroupSelect.id = "selectworkgroupSelect" + rowNum;
		workgroupSelect.title = MSG.WORKGROUP+ " "+rowNum;
		workgroupSelect.autocomplete = true;
		workgroupSelect.addEventListener(EVENT.CHANGE, () => {
			if(workgroupSelect.value){
				workgroup.setId(workgroupSelect.value)
			}
		});
		
		table.addCell(workgroupSelect).style.width = '100%';
	
		this.getWorkgroups()
		.then(options=>{
			const workgroupId = workgroup.getId();

			options = options.filter(w => w.id == workgroupId || !this.registry.getWorkgroups().some(wr => wr.id== w.id && !wr.removed) );

			workgroupSelect.setOptions(options);

			if(workgroupId){
				workgroupSelect.value = workgroupId;
			}
		})

		let removeIcon = new AonIconButton();
		removeIcon.id = "workgroupRemove" +rowNum;
		removeIcon.title = MSG.DELETE;
		removeIcon.icon = MATERIAL_ICONS.CLOSE;
		removeIcon.addEventListener(EVENT.CLICK, () => {
			const count = table.getRowsCount();
			let childVisible = 1;
			
			workgroup.remove(); //REMOVE

			if(count === 1){
				workgroupSelect.clear();
			} else {
				table.removeRow(rowNum);
				childVisible = count-1;
			}

			let cell = table.getCell(childVisible, 2);
			if(cell && cell.firstChild){
				cell.firstChild.visible = true;
			}
		});
		
		table.addCell(removeIcon);

		this.querySelectorAll(`[id*='workgroupAdd']`).forEach(el=>{
			el.visible = false;
		});

		let add = new AonIconButton();
		add.id = "workgroupAdd"+rowNum;
		add.title = MSG.ADD+" "+MSG.WORKGROUP;
		add.icon = MATERIAL_ICONS.ADD_CIRCLE_OUTLINE;
		add.visible = rowCount === i+1
		add.addEventListener(EVENT.CLICK, () => {
			add.visible = false;
			let workgroup = new Workgroup();
			workgroup.setDomain(this.registry.getDomain().getId());
			this.registry.addWorkgroup(workgroup);
			this.buildWorkgroup(table, workgroup, table.getRowsCount());
		});

		table.addCell(add);
	}
	
	async getWorkgroups(){
		if(!this.workgroups.length){
			try {
				let resp = await getWorkgroups();
				this.workgroups = resp.map(res => ({...res, value:res.id, name: res.description, statusText: res.active ? MSG.ACTIVE : MSG.INACTIVE}) );
			} catch (error) {
				this.showError(error);
			}
		}
		return this.workgroups;
	}

	back() {
		let list = new AonTaskHolderList();
		list.id = this.getApplication().id + 'TaskHolderList';
		this.getApplication().setContent(list);
	}

	save() {
		if(this.saveBool) {
			this.saveBool = false;
			let medias = this.emails.concat(this.phones).concat(this.webs);
			this.registry.setMedia(medias);
			
			saveTastHolder(this.registry).then(registry => {
				this.registry.id = registry.id;
				this.saveBool = true;
				this.showToast({
					type: 'success',
					 message: 'Datos Guardados Correctamente'
				 });
			})
			.catch(error => {
				this.saveBool = true;
				this.showToast(error);
		    });
		}
	}

	setTaskHolder(taskholder) {
		this.registry = new TaskHolder(taskholder);
	}
}

if(!window.customElements.get("aon-taskholder")){
	window.customElements.define("aon-taskholder", AonTaskHolder);
}