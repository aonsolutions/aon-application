import {AonElement} from '../../components/AonElement.js';
import { AonApplication } from '../../components/aon-application.js';
import { OfficeEnums } from './OfficeEnums.js';
import { EVENT, MSG } from '../../environments/environments.js';
import { getProjectTypes, saveProject} from '../../services/projectService.js';
import { DocumentalSidenav } from '../documental/DocumentalEnums.js';
import { AonCustomer } from '../registry/customer/aon-customer.js';
import { AonCustomerList } from '../registry/customer/aon-customer-list.js';
import { SigninSidenav } from '../timecontrol/signinEnums.js';
import { AonTaskHolder } from '../registry/taskholder/aon-taskholder.js';
import { AonTaskHolderList } from '../registry/taskholder/aon-taskholder-list.js';
import { OfficeUtils } from './OfficeUtils.js';
import { getTastHolders } from '../../services/taskHolderService.js';
import { getWorkgroups } from '../../services/workgroupService.js';
import { getScopes } from '../../services/documentalService.js';
import { ProjectUtils } from '../project/ProjectUtils.js';

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

            // application.removeToolbarOptions();

            application.addToolbarOption2(SigninSidenav.ADD, () => this.showView(officeViews.AON_CUSTOMER) );

            let btnSearch = application.getSearchButton();

            if(!btnSearch && officeViews.AON_CUSTOMER_LIST === view){
                let aonView = this.getElement(officeViews.AON_CUSTOMER_LIST);
                let timeOut = null;

                btnSearch = application.addSearchOption();

                btnSearch.addEventListener(EVENT.SEARCH_NEW, ({detail}) => {
                    clearTimeout(timeOut);
                    timeOut = setTimeout(() => {
                        this.addFilterCustomers({
                            value:detail.search,
                            scope:detail.scope,
                            projectType: detail.projectType,
                            status: OfficeUtils.getCustomerStatus(detail)
                        });
                        let filter = {...this.getFilterCustomers(), page:1 };
                        console.log(aonView, detail);
                        aonView.setFilter(filter);
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

        let projectTypeEl = this.getElement("projectType");
        getProjectTypes({})
        .then(t => {
            let types = (t || []).map((r) => ({...r, name: r.description, value: r.id}));

            projectTypeEl.setOptions(types);

            const value = filter.projectType;
            if(value){
                projectTypeEl.value = value;
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

                this.buildToobar(view);
			}

			resolve(aonView);
		});
    }  
}
if(!window.customElements.get("aon-office-panel")) {
	window.customElements.define("aon-office-panel", AonOfficePanel);
}