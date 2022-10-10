import {AonElement} from '../../components/AonElement.js';
import { AonApplication } from '../../components/aon-application.js';
import { OfficeEnums } from './OfficeEnums.js';
import { CONSTANT, EVENT, MSG } from '../../environments/environments.js';
import { getProjectTypes, saveProject, saveProjectType } from '../../services/projectService.js';
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
import { getScopes } from '../../services/documentalService.js';
import { AonCheckbox } from '../../components/aon-checkbox.js';

export class AonOfficePanel extends AonElement {
    projectTypes;
    workgroups;
    taskHolders;
    customerSelected;

    filterCustomers;
    
    setCustomerSelected(customerSelected){
        this.customerSelected = customerSelected;
    }

    getCustomerSelected(){
        return this.customerSelected
        .filter((value,index) => // remove repeated customersSelected
            this.customerSelected.findIndex((m) => m.id === value.id) === index 
        ) 
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
            status:["ACTIVE", "BLOCKED"]
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

        application.addSidenavOptions2({...DocumentalSidenav.TYPES, name:"Tipos de expediente"}, [], () => this.createType());
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

    addCustomerListSelectable(view){
        const application = this.getApplication();
        view.selectable = true;
        view.addEventListener("select", ({detail}) => {
            let {table:{selected}} = detail;

            this.setCustomerSelected(selected);

            if(selected.length > 0 ) {
                application.addToolbarOption2(OfficeEnums.OfficeSidenav.ADD_FOLDER, () => OfficeUtils.buildDialog(this));
			} else {
                this.removeActionFolder();
			}   
        });
    }

    removeActionFolder(){
        this.setCustomerSelected([]);
        this.getApplication().removeToolbarOption(OfficeEnums.OfficeSidenav.ADD_FOLDER);
    }

    buildToobar(view){
        const application = this.getApplication();
        const officeViews = OfficeEnums.OfficeViews;
        
        if([officeViews.AON_CUSTOMER, officeViews.AON_CUSTOMER_LIST].includes(view)){

            application.removeToolbarOptions();

            application.addToolbarOption2(SigninSidenav.ADD, () => this.showView(officeViews.AON_CUSTOMER) );

            if(officeViews.AON_CUSTOMER_LIST === view){
                let aonView = this.getElement(officeViews.AON_CUSTOMER_LIST);
                let timeOut = null;

                const btnSearch = application.addSearchOption();

                btnSearch.addEventListener(EVENT.SEARCH_NEW, ({detail}) => {
                    console.log(detail);
           
                    timeOut = setTimeout(() => {
                        this.addFilterCustomers({
                            value:detail.search,
                            scope:detail.scope,
                            status: OfficeUtils.getCustomerStatus(detail)
                        });
                        aonView.setFilter({...this.getFilterCustomers(), page:1 });
                    }, 300);
                });

                btnSearch.buildOptionsFilter(OfficeEnums.CustomerFilter);//INPUTS
                this.searchValueDefault();
            }
        }
    }

    searchValueDefault(){
        let filter =  this.getFilterCustomers() || {};

		let scopeEl = this.getElement("scope");
        getScopes().then(scopes=>{
            scopeEl.setOptions(scopes.map(c=> ({...c, value: c.id})) );

            const value = filter.scope;
            if(value){
                scopeEl.value = value;
            }
        })

        let active = this.getElement("active");
        active.value = (filter.status ||  []).includes("ACTIVE");
        
        let inactive = this.getElement("inactive");
        inactive.value = (filter.status ||  []).includes("INACTIVE");
  
        let blocked = this.getElement("blocked");
        blocked.value = (filter.status ||  []).includes("BLOCKED");
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

                this.buildToobar(view);
			}

			resolve(aonView);
		});
    }

    // mutate(mutations) {
    //     mutations.forEach((mutation)=> {
    //         if ( mutation.type === 'childList' ) {
    //             const nodes = mutation.addedNodes;
    //             for(const node of nodes) {
    //                 const tagName = node.tagName  ? node.tagName.toLowerCase(): undefined;
    //                 if(tagName === 'aon-customer'){
    //                     this.removeActionFolder();
    //                     break;
    //                 } 
    //             }
    //         }
    //     });
    // }
      
}
if(!window.customElements.get("aon-office-panel")) {
	window.customElements.define("aon-office-panel", AonOfficePanel);
}