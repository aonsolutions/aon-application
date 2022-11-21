import {AonElement} from '../../components/AonElement.js';
import { AonApplication } from '../../components/aon-application.js';
import { OfficeEnums } from './OfficeEnums.js';
import { EVENT, MSG } from '../../environments/environments.js';
import { getProjectTypes, saveProject} from '../../services/projectService.js';
import { DocumentalSidenav } from '../documental/DocumentalEnums.js';
import { AonCustomer } from '../registry/customer/aon-customer.js';
import { AonCustomerList } from '../registry/customer/aon-customer-list.js';
import { AonTaskHolder } from '../registry/taskholder/aon-taskholder.js';
import { AonTaskHolderList } from '../registry/taskholder/aon-taskholder-list.js';
import { OfficeUtils } from './OfficeUtils.js';
import { getTastHolders } from '../../services/taskHolderService.js';
import { getWorkgroups } from '../../services/workgroupService.js';
import { ProjectUtils } from '../project/ProjectUtils.js';
import { saveRelationShip } from '../../services/registryService.js';

export class AonOfficePanel extends AonElement {
    projectTypes;
    workgroups;
    taskHolders;
    customerSelected;
    customerSelectedAll;

    filterCustomers;
    
    setCustomerSelected(customerSelected){
        this.customerSelected = customerSelected;
    }

    getCustomerSelected(){
        return this.customerSelected.filter((value,index) => // remove repeated customersSelected
            this.customerSelected.findIndex((m) => m.id === value.id) === index 
        ) 
    }

    setCustomerSelectedAll(customerSelectedAll){
        this.customerSelectedAll = customerSelectedAll;
    }

    getCustomerSelectedAll(){
        return this.customerSelectedAll;
    }

    addFilterCustomers(filter){
        this.filterCustomers = { ...this.filterCustomers, ...filter};
    }

    setFilterCustomers(filter){
        this.filterCustomers = filter;
    }

    getFilterCustomers(){
        return this.filterCustomers;
    }

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
        this.setCustomerSelected([]);
        
        this.setFilterCustomers({
            page: 1,
            perPage:50,
            status:["ACTIVE", "BLOCKED"],
        });
	}

    build() {
        this.createApplication(this.id, MSG.OFFICE, new AonApplication());
        this.buildSidenav();

        this.showView(OfficeEnums.OfficeViews.AON_CUSTOMER_LIST, undefined, {...this.getFilterCustomers(), page:1 });
	}

    buildSidenav() {
        const application = this.getApplication();

        const {OfficeViews, OfficeOptions} = OfficeEnums;

        let options = [];

        let customer = OfficeOptions.AON_CUSTOMER;
        customer.fn = () => this.showView(OfficeViews.AON_CUSTOMER_LIST, undefined, {...this.getFilterCustomers(), page:1 });
        options.push(customer);

        let taskHolder = OfficeOptions.AON_TASK_HOLDER;
        taskHolder.fn = () => this.showView(OfficeViews.AON_TASK_HOLDER_LIST);
        options.push(taskHolder);

        application.addSidenavOptions(MSG.OFFICE, options);

        application.addSidenavOptions2({...DocumentalSidenav.TYPES, name:"Tipos de expediente"}, [], () => ProjectUtils.buildDialogProjectType(this));
        this.loadProjectType();
    }
    
    loadProjectType() {
        getProjectTypes({})
        .then(types => {
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
                        action: () => ProjectUtils.projectTypeDelete(this, item)
                    },{
                        id: 'Edit',
                        icon: 'edit',
                        action: () => ProjectUtils.buildDialogProjectType(this, item)
                    }]
                };
                this.getApplication().addSidenavOptionsListValue(DocumentalSidenav.TYPES, option);
            });
        });
    }

    addCustomerListSelectable(view){
        const application = this.getApplication();
        view.selectable = true;
        view.addEventListener(EVENT.SELECT, ({detail}) => {
            let {table:{selected, selectedAll}} = detail;

            this.setCustomerSelected(selected);
            this.setCustomerSelectedAll(selectedAll);

            if(selected.length > 0 ) {
                application.addToolbarOption2(OfficeEnums.OfficeSidenav.MORE_VERT, ({target}) => {
                    OfficeUtils.buildDialogMenu(this, target);
                });
			} else {
                this.removeActionFolder();
			}   
        });
    }

    removeActionFolder(){
        this.setCustomerSelected([]);
        this.setCustomerSelectedAll(false);
        this.getApplication().removeToolbarOption(OfficeEnums.OfficeSidenav.MORE_VERT);
    }

    async onSaveExpedientes(project){
        let selected = this.getCustomerSelected();
        let projects = selected
        .map(registry=> 
            project.clone()
            .setRegistry(registry)
        );

        if(projects.length){
            await saveProject({projects});

            this.showMessage();
        }
    }
    
	onSaveRelationByCustomers(add){
        const aonView = this.getElement(OfficeEnums.OfficeViews.AON_CUSTOMER_LIST);

        if(aonView){
            this.getApplication().startLoading();
    
            saveRelationShip({ add, customers: this.getCustomerSelected() })
            .then((resp)=> {
                if(resp.length){
                    OfficeUtils.builDialogRelationship(this, resp);
                } else {
                    this.showMessage(); 
                }
        
                const table = aonView.TABLE;
                if(table){
                    table.clearSelected();
                    aonView.setFilter({...this.getFilterCustomers(), page:1 });
                    this.setCustomerSelected([]);
                }
            })
            .catch((err)=> this.showError(err))
            .finally(()=>{
                this.getApplication().stopLoading();
            });
        }
	}

    openDialogRelationship(data){ //DELETE
        OfficeUtils.builDialogRelationship(this, data) 
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

    showView(view, data = undefined, filter = undefined){
        const officeViews = OfficeEnums.OfficeViews;
        const application = this.getApplication();
        this.removeActionFolder();
		return new Promise(async(resolve)=>{
			let aonView = undefined;
    
			switch(view){
                case officeViews.AON_OFFICE_PANEL:
					aonView = new AonOfficePanel();
				break;
                case officeViews.AON_CUSTOMER:
					aonView = new AonCustomer();
                    aonView.setCustomer();
                    aonView.back = () => this.showView(officeViews.AON_CUSTOMER_LIST, undefined, {...this.getFilterCustomers(), page:1 }); // overwrite function
				break;
                case officeViews.AON_CUSTOMER_LIST:
					aonView = new AonCustomerList();

                    this.addCustomerListSelectable(aonView);
                    
                    aonView.buildRegistry = (registry) =>{// overwrite function
                        application.startLoader();
                        aonView.getCustomerCustom(registry)
                        .then(customer => 
                            this.showView(officeViews.AON_CUSTOMER, {customer})
                        )
                        .catch(err => this.showError(err))
                        .finally(() => application.stopLoader());
                    }
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

				if(filter) {
                    aonView.filter = filter;
                    aonView.setFilter(filter);
                } 

                if(data){
                    if(data.customer){
                        aonView.setCustomer(data.customer);
                    } else {
                        aonView.data = data;
                    }
                }

				application.setContent(aonView);


                if([officeViews.AON_CUSTOMER_LIST].includes(view) && aonView.buildToolbar){
                    aonView.buildToolbar();
                }
			}

			resolve(aonView);
		});
    }  
}
if(!window.customElements.get("aon-office-panel")) {
	window.customElements.define("aon-office-panel", AonOfficePanel);
}