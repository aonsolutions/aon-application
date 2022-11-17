
import { AonSelect } from '../../components/aon-select.js';
import { AonSimpleList } from '../../components/aon-simple-list.js';
import { COLORS, CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js';
import { getDomainCompanies } from '../../services/companyService.js';

export class AonOfficeLinkSimpleList extends AonSimpleList {

    option;
    _data;

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

        let elementHTML = this.createElement(TAG.DIV);

        const liValue = {
            iconHTML,
            elementHTML,
            title: customer.name,
            subtitle: message,
            option: success ? null : this.getOptionsLink(data, elementHTML)
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

    getOptionsLink({customer}, elementHTML){
        return [
            { 
				name: "Vincular con una existente", 
				value: "LINK",
				icon:  MATERIAL_ICONS.LINK, 
				fn:()=> {
					this.getApplication().development();
                    console.log("Vincular a una existente");
                    // this.buildSelectCompany(customer, [], elementHTML);
				}
			},
            { 
				name: "Crear nueva empresa", 
				value: "ENTERPRISE_NEW",
				icon: MATERIAL_ICONS.OPEN_IN_NEW, 
				fn:()=> {
					this.getApplication().development();
                    console.log(`Desea registrar y vincular a ${customer.name} ?`);
					// this.getApplication().confirmDialog(MSG.REGISTER, `Desea registrar y vincular a ${customer.name} ?`, () => {
					// 	this.getApplication().startLoading();
					// 	saveCompany({...this.registry, id:null})
					// 	.then((company)=>{
					// 		console.log("company", company);
					// 		this.saveRegistryRelationship(company);
					// 	})
					// 	.catch(err=>{
					// 		this.showError(err);
					// 	})
					// 	.finally(()=>{
					// 		this.getApplication().stopLoading();	
					// 	});
					// });
				}
			}
        ];
    }

    buildSelectCompany(customer, companies=[], elementHTML){

        console.log(customer);

		let div = document.createElement(TAG.DIV);
		div.style.display = "flex";
        elementHTML.appendChild(div);

		let selectCompany   = new AonSelect();
		selectCompany.id    = "selectCompany";
		selectCompany.title = MSG.COMPANY;
		selectCompany.autocomplete = true;
		div.appendChild(selectCompany);

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


		// 	if(selectCompany.value){
		// 		this.saveRegistryRelationship(selectCompany.getDetail());
		// 		dialog.close();
		// 	}

        return div
	}   

	async getDomainCompanies(customer, value=undefined){
		let params ={parentId: customer.domain.parentId};
		if(value) params.value = value;

		let result = await getDomainCompanies(params);

		return result.filter(company => company && company.domain && company.domain.id!=customer.domain.id)
		.map( c=> ({...c, value: c.id}));
	}

    getData(){
        return this._data || [];
    }

    setData(data){
        this._data = data;
        return this;
    }
}
if(!window.customElements.get('aon-office-link-simple-list')){
    window.customElements.define('aon-office-link-simple-list', AonOfficeLinkSimpleList);
}
