
import { AonSelect } from '../../components/aon-select.js';
import { AonButton } from '../../components/aon-button.js';
import { AonSimpleList } from '../../components/aon-simple-list.js';
import { COLORS, CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js';
import { getDomainCompanies, saveCompany } from '../../services/companyService.js';
import { saveRelationShip } from '../../services/registryService.js';

export class AonOfficeLinkSimpleList extends AonSimpleList {
    option;
    _data;

    getData(){
        return this._data || [];
    }

    setData(data){
        this._data = data;
        return this;
    }
	
    constructor () {
        super();
    }

    connectedCallback () {
        this.init();
    }

    init() {
        this.initialize();
        this.build();

        const ul = this.getElement(this.UL);
        ul.style.maxHeight = "500px";
        ul.classList.add(CSS.MATERIAL_SCROLL);

        this.buildRows();
    }

    buildRows(){
        if(this.getData().length) {
            this.removeAllLi();
            this.getData().sort((a, b) => a.success - b.success).forEach((data, i) => this.addRow(data, i));
        }  else {
            this.empty("Sin resultados");
        }
    }

    addRow(data, i) {
        const {message, success, customer} = data;

        const color = success ? CSS.variable(COLORS.ONLINE_GREEN) : CSS.variable(COLORS.MATERIAL_RED);
        const icon  = success ? MATERIAL_ICONS.CHECK_CIRCLE : MATERIAL_ICONS.ERROR;

        const iconHTML = this.getIcon({title:message, icon, color});

        const liValue = {
            iconHTML,
            title: customer.name,
            subtitle: message,
            option: success ? null : this.getOptionsLink(data)
        }

        this.addLi(liValue, i, () => {}, null, () => {});
    }

    getIcon({title, icon, color}){
        let element = this.createElement(TAG.I);
        element.className = CONSTANT.MATERIAL_ICONS;
        element.title     = title;
        element.innerText = icon;
        element.style.color = color;
        element.style.marginRight = "10px";
        element.style.marginTop = "9px";
        return element;
    }

    getOptionsLink({customer}){
        return [
            { 
				name: "Vincular con una existente", 
				value: "LINK",
				icon:  MATERIAL_ICONS.LINK, 
				fn:(element)=> {
                    this.openDialogCompanies(customer, element);
				}
			},
            { 
				name: "Crear nueva empresa", 
				value: "ENTERPRISE_NEW",
				icon: MATERIAL_ICONS.OPEN_IN_NEW, 
				fn:()=> {
					if(confirm(`Desea registrar y vincular a ${customer.name} ?`)){
						this.getApplication().startLoading();
						saveCompany({...customer, id:null})
						.then((company)=>{
							this.saveRegistryRelationship(customer, company);
						})
						.catch(err=>{
							this.showError(err);
						})
						.finally(()=>{
							this.getApplication().stopLoading();	
						});
					}
				}
			}
        ];
    }

	openDialogCompanies(customer, el){
		const boundingClientRect = el.getBoundingClientRect();
		const top = boundingClientRect.top;
		const left = boundingClientRect.left;

		const dialogMenu = this.getDialogMenu();

		const {div, selectCompany, button} = this.buildSelectCompany(customer, []);

		button.addEventListener(EVENT.CLICK, async () => {
			if(selectCompany.value){
				await this.saveRegistryRelationship(customer, selectCompany.getDetail());
				dialogMenu.close();
			}
		});

		dialogMenu.setContent(div, top, left);
		dialogMenu.open();
    }

    buildSelectCompany(customer, companies=[]){

		let div = document.createElement(TAG.DIV);
		div.style.padding = "12px";
	
		let selectCompany   = new AonSelect();
		selectCompany.id    = "selectCompany"+ customer.id;
		selectCompany.title = MSG.COMPANY;
		selectCompany.autocomplete = true;
		div.appendChild(selectCompany);

		let button = new AonButton();
		button.title = "Aceptar";
		div.appendChild(button);

		if(companies.length){
			let timeOut = null;

			selectCompany.setOptions(companies.map(c=> ({...c, value:c.id})));
			
			selectCompany.addEventListener(EVENT.INPUT, async({target})=>{
				clearTimeout(timeOut);
				const value = target.value;
				if(value.length > 2 ){
					timeOut = setTimeout(async() =>{
						selectCompany.loading(true);
						const cs = await getDomainCompanies(customer, value);
						selectCompany.setOptions(cs);
						selectCompany.loading(false);
					}, 300);
				}
			});
		} else {
			selectCompany.loading(true);
			this.getDomainCompanies(customer)
			.then(companies=>{
				selectCompany.setOptions(companies);
			})
			.finally(()=>{
				selectCompany.loading(false);
			})
		}

        return {div, selectCompany, button};
	}   

	async getDomainCompanies(customer, value=undefined){
		let params ={parentId: customer.domain.parentId};
		if(value) params.value = value;

		let result = await getDomainCompanies(params);

		return result.filter(company => company && company.domain && company.domain.id!=customer.domain.id)
		.map( c=> ({...c, value: c.id}));
	}

	async saveRegistryRelationship(customer, company){
		this.getApplication().startLoading();

		try {
			await saveRelationShip({
				domain: customer.domain,
				registry: customer.id,
				related_registry: company.id,
				comments: (company.domain && company.domain.name ? company.domain.name : "")
			})
			.then(()=> {
				this.showMessage();
				this.restartData(customer);
			})
		} catch (error) {
			this.showError(err)
		}

		this.getApplication().stopLoading();
	}

	restartData(customer){
		this.getData()
		.forEach(data=>{
			if(data && data.customer && data.customer.id == customer.id){
				data.message = "";
				data.success = true;
			}
		})
		this.buildRows();
	}
	
}
if(!window.customElements.get('aon-office-link-simple-list')){
    window.customElements.define('aon-office-link-simple-list', AonOfficeLinkSimpleList);
}
