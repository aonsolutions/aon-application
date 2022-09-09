import { CONSTANT, EVENT, MSG, TAG } from '../../environments/environments.js'; 
import { AonBasicTable } from '../../components/aon-basic-table.js';
import { Category } from '../../models/category/Category.js';
import { CategoryService } from '../../services/categoryService.js';
import { AonElement } from '../../components/AonElement.js';
import { setAttributes } from '../../services/utilsComponents.js';
import { CreateComponent } from '../../components/CreateComponent.js';
import { AonAutosizeTextarea } from '../../components/aon-autosize-textarea.js';
import { getScopes } from "../../services/documentalService.js";

export class AonCategoryAdd extends AonElement {

    category;
    type;
    id;
    data;
    
	connectedCallback () {
		this.initialize();
        this.build();
        this.initGets();
  	}
	
    initialize() {
        this.id = "aonCategoryAdd";
        this.setCategory(this.data);

        // console.log(this.category);
	}

	build() {

        const form = CreateComponent.createForm(this.id+"Form");
        this.appendChild(form);

		let table = new AonBasicTable();
		table.id = this.id+"Table";
		form.appendChild(table);

		table.addRow();
        const nameEl = CreateComponent.createAonInput({
            attributes:{
                id:this.id+"category",
                description:MSG.NAME,
                required:true,
                value: this.category.getName() || ""
            },
            events:{
                keyup: ({target}) => {
                    this.category.setName(target.value);
                }
            }
        });
        table.addCell(nameEl);
       
		table.addRow();
        const descriptionEl = setAttributes(new AonAutosizeTextarea(),{
            id: this.id+"description",
            title: MSG.DESCRIPTION,
            required:true
        });
        
        descriptionEl.value = this.category.getDescription() || "";
  
        descriptionEl.addEventListener(EVENT.KEYUP, ({target})=>{
            this.category.setDescription(target.value);
        });
        table.addCell(descriptionEl);


		table.addRow();
        const scope = CreateComponent.createAonSelect({
            attributes:{
                id:this.id+"scope",
                title:MSG.SCOPE,
                default:CONSTANT.TRUE,
                autocomplete: CONSTANT.OFF
            },
            events:{
                change: ({target}) => {
                    this.category.setScope(target.value);
                }
            }
        });
        table.addCell(scope);


        table.addRow();
        const urlEl = CreateComponent.createAonInput({
            attributes:{
                id:this.id+"url",
                description:"URL",
                value: this.category.getUrl() || ""
            },
            events:{
                keyup: ({target}) => {
                    this.category.setUrl(target.value);
                }
            }
        });
        table.addCell(urlEl);
	}	

    async initGets() {
        await Promise.all([
            this.getScopes(),
        ]).catch(e=> console.log(e));
    }

    async getScopes(){
        const scopes = await getScopes();
        if(scopes.length){
          const scope = this.category.getScope();

          const options = scopes.map(scope => ({...scope, value:scope.id}));
          let scopeEl = document.getElementById(this.id+"scope");
          scopeEl.setOptions(options);

          if(scope){
            scopeEl.value = scope;
          }
        }
    }
    
	async save() {
        try{
            const {id} = await CategoryService.saveCategory(this.category);
            
            this.category.setId(id);

            this.showToast({
                type: 'success',
                message: 'Datos Guardados Correctamente'
            });
            
            return true;
        } catch(error){
            this.showToast(error);
        }
        return false;
	}

	setCategory(category) {
		this.category = new Category(category);
        if(this.type){
            this.category.setType(this.type);
        }
	}

    async delete(){
        try {
            await CategoryService.deleteCategory(this.category);
            this.showToast({message:MSG.DELETED_DATA});
            return true;
        } catch (error) {
            this.showError(error);
        }
        return false;
    }

    isEdit(){
        return this.category.getId();
    }
}

if(!window.customElements.get("aon-category-add")){
	window.customElements.define("aon-category-add", AonCategoryAdd);
}