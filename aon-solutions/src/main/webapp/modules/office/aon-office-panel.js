import {AonElement} from '../../components/AonElement.js';
import { AonApplication } from '../../components/aon-application.js';
import { OfficeEnums } from './OfficeEnums.js';
import { CONSTANT, MSG, TAG } from '../../environments/environments.js';
import { getProjectTypes, saveProjectType } from '../../services/projectService.js';
import { AonInput } from '../../components/aon-input.js';
import { ProjectType } from '../../models/project/ProjectType.js';
import { DocumentalSidenav } from '../documental/DocumentalEnums.js';
import { AonCustomer } from '../registry/customer/aon-customer.js';
import { AonCustomerList } from '../registry/customer/aon-customer-list.js';
import { SigninSidenav } from '../timecontrol/signinEnums.js';
import { AonTaskHolder } from '../taskholder/aon-taskholder.js';
import { AonTaskHolderList } from '../taskholder/aon-taskholder-list.js';
import { OfficeUtils } from './OfficeUtils.js';
import { getTastHolders } from '../../services/taskHolderService.js';
import { getWorkgroups } from '../../services/workgroupService.js';

export class AonOfficePanel extends AonElement {
    projectTypes;
    workgroups;
    taskHolders;
	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
    	this.build();
 	}

	initialize(){
		this.id = this.id || OfficeEnums.OfficeViews.AON_OFFICE_PANEL;
        this.projectTypes = [];
        this.workgroups   = [];
        this.taskHolders  = [];
	}

 	build() {
        this.createApplication(this.id, MSG.OFFICE, new AonApplication());
        this.buildSidenav();
        
        this.showView(OfficeEnums.OfficeViews.AON_CUSTOMER_LIST);
	}

    buildSidenav() {
        const application = this.getApplication();

        const {OfficeViews, OfficeOptions} = OfficeEnums;
        
 
        let options = [];

        let customer = OfficeOptions.AON_CUSTOMER;
        customer.fn = () => this.showView(OfficeViews.AON_CUSTOMER_LIST);
        options.push(customer);

        let taskHolder = OfficeOptions.AON_TASK_HOLDER;
        taskHolder.fn = () => this.showView(OfficeViews.AON_TASK_HOLDER_LIST);
        options.push(taskHolder);

        application.addSidenavOptions(MSG.OFFICE, options);

        application.addSidenavOptions2({...DocumentalSidenav.TYPES, name:"Tipos de expediente"}, [], () => this.createType());
        this.loadProjectType();
    }


    loadProjectType() {
        getProjectTypes({}).then(types => {
            this.projectTypes = (types || []).map((r) => ({...r, name: r.description, value: r.id}));
            
            this.clearElementById(this.getApplication().SIDENAV + DocumentalSidenav.TYPES.id + 'List');
            types.forEach(item => {
                let option = {
                    name: item.description,
                    icon: 'label',
                    fn: () => {}, 
                    actions: [{
                      id: 'Delete',
                      icon: 'delete',
                      action: () => this.getApplication().development()
                    },{
                      id: 'Edit',
                      icon: 'edit',
                      action: () => this.getApplication().development()
                    }]
                };
                this.getApplication().addSidenavOptionsListValue(DocumentalSidenav.TYPES, option);
           });
       });
    }

    createType() {
        let d = this.getApplication().getDialog();
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
                    this.loadProjectType();
                });
            }
        });
        d.open();
    }

    showView(view, data = undefined, filter = undefined){
        const officeViews = OfficeEnums.OfficeViews;
		return new Promise(async(resolve)=>{
			let aonView = undefined;
			switch(view){
                case officeViews.AON_OFFICE_PANEL:
					aonView = new AonOfficePanel();
				break;
                case officeViews.AON_CUSTOMER:
					aonView = new AonCustomer();
				break;
                case officeViews.AON_CUSTOMER_LIST:
					aonView = new AonCustomerList();
                    this.addEventSelectable(aonView);
				break;
                case officeViews.AON_TASK_HOLDER:
					aonView = new AonTaskHolder();
				break;
                case officeViews.AON_TASK_HOLDER_LIST:
					aonView = new AonTaskHolderList();
				break;
			}
			if(aonView){
				aonView.id = view;
				if(filter) aonView.setFilter(filter);
				if(data) aonView.data = data;
				this.getApplication().setContent(aonView);

                this.buildToobar(view);
			}
			resolve(aonView);
		});
    }

    addEventSelectable(view){
        view.selectable = true;
        view.addEventListener("select", ({detail}) => {
            let {parent, table} = detail;

            let selected = table.selected;

            if(selected.length >= 1) {
				this.addCustomerActions(selected);
			} else if(selected.length === 0){
				this.removeCustomerActions();
			}
        });
    }

	addCustomerActions(selected) {
        const application = this.getApplication();
        const toolbar = application.getToolbar();
        if(toolbar){
            this.removeCustomerActions();

            toolbar.addSeparator();
            application.addToolbarOption2(SigninSidenav.ADD, () =>this.showView(officeViews.AON_CUSTOMER) );

            toolbar.addSeparator();
            application.addToolbarOption2(OfficeEnums.OfficeSidenav.ADD_FOLDER, () =>{ 
                OfficeUtils.buildDialog(this, selected);
            });
        }
	}

	removeCustomerActions() {
        const application = this.getApplication();
        const toolbar = application.getToolbar();
        if(toolbar){
            toolbar.removeSeparators();
            application.removeToolbarOption(OfficeEnums.OfficeSidenav.ADD_FOLDER);
        }
	}
    

    buildToobar(view){
        const application = this.getApplication();
        const officeViews = OfficeEnums.OfficeViews;

        if([officeViews.AON_CUSTOMER, officeViews.AON_CUSTOMER_LIST].includes(view)){

            application.removeFloatOption();
            application.removeToolbarOptions();

            application.addToolbarOption2(SigninSidenav.ADD, () =>this.showView(officeViews.AON_CUSTOMER) );
        }
    }

    async getProjectTypes(){
        return this.projectTypes;
    }
        
	async getWorkgroups(){
		if(this.workgroups.length==0){
			let wgs = await getWorkgroups().catch(()=> []);
			this.workgroups = wgs.map((r) => ({...r, name: r.description, value: r.id}))
		} 
		return this.workgroups;
	}

	async getTaskHolders(){
		if(this.taskHolders.length==0){
			let ths = await getTastHolders().catch(()=> []);
			this.taskHolders = ths.map((r) => ({...r, value: r.id}))
		} 
		return this.taskHolders;
	}
}
if(!window.customElements.get("aon-office-panel")) {
	window.customElements.define("aon-office-panel", AonOfficePanel);
}