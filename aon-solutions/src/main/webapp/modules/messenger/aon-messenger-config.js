import {AonElement} from '../../components/AonElement.js';
import { EVENT, MSG, TAG, CSS } from "../../environments/environments.js";
import * as ACTION from '../actions.js';
import { AonSelect } from '../../components/aon-select.js';
import { setAttributes, setStyles } from '../../services/utilsComponents.js';
import { AonCard } from '../../components/aon-card.js';
import { AonToolbar } from '../../components/aon-toolbar.js';
import { ToolbarType } from '../../models/enums.js';
import { getWorkgroups } from '../../services/workgroupService.js';
import { getTastHoldersWorkGroup } from '../../services/taskHolderService.js';
import { serializeForm } from '../../services/utils.js';
import { getTaskAppParams, saveTaskAppParams } from '../../services/taskService.js';
import * as LS from '../../services/localStorageService.js';

export class AonMessengerConfig extends AonElement {
  FORM;
  APP_PARAMS;
  get id() {
    return this.getAttribute('id');
  }

  set id(id) {
    this.setAttribute('id', id);
  }

  constructor () {
    super();
  }

  connectedCallback () {
    this.initialize();
    this.build();
  }

  initialize() {
    this.id = this.id || 'aonMessengerConfig';
    this.FORM = this.id+"form";
    this.APP_PARAMS = ["APP_DEFAULT_REQUESTS_WORKGROUP", "APP_DEFAULT_REQUESTS_TASK_HOLDER"]
  }

  build(){
      this.buildToolbar();

      let divOne = setStyles(this.createElement(TAG.DIV),{
        // display: "flex",
        // alignItems: "center",
        // justifyContent: "center"
      });

      this.appendChild(divOne);

      let divTwo = setStyles(this.createElement(TAG.DIV),{ width: "50%" });
      divOne.appendChild(divTwo);

      let card = setAttributes(new AonCard(), { 
        title:"Parametros por defecto"
      });

      divTwo.appendChild(card);
      
      this.buildForm(card.getContent());
  }

  buildToolbar() {
    let toolbar = new AonToolbar();
    toolbar.id = "we23";
    toolbar.type = ToolbarType.SECONDARY;
    toolbar.title = MSG.SAVE;
    this.appendChild(toolbar);
    toolbar.addButton2(ACTION.SAVE, () => this.save());
}

  buildForm(cardContent){
    let form = setAttributes(this.createElement(TAG.FORM), {id:this.FORM});
    cardContent.appendChild(form);
    
    
    getTaskAppParams({params:this.APP_PARAMS}).then(params=>{
        let workgroupId, taskHolderId;
        params.map(param=>{
            if(param.value) {
                let value = parseInt(param.value);
                if(param.name === this.APP_PARAMS[0])
                    workgroupId = value;
                else if(param.name === this.APP_PARAMS[1])
                    taskHolderId = value;
            }
        });

        let workgroup = setAttributes(new AonSelect(),{ title: MSG.WORKGROUP, id:"workgroup", name:"workgroup", default:true});
        form.appendChild(workgroup);

        let taskHolder = setAttributes(new AonSelect(),{ title: "Títular", id:"task_holder", name:"task_holder", default:true});
        form.appendChild(taskHolder);

        this.fillWorkGroup(workgroup, workgroupId);
        this.fillTaskHolder(taskHolder, workgroupId, taskHolderId);

        workgroup.addEventListener(EVENT.CHANGE, ({detail})=>{
            if(detail && detail.id)
                this.fillTaskHolder(taskHolder, detail.id);
        });
    });
  }

  async save(){
    this.getApplication().startLoading();
    try {
      let domainId  = LS.getDomainId();
      const {workgroup, task_holder} = serializeForm(this.getElement(this.FORM));
      let arr = [
          {name: this.APP_PARAMS[0], value: workgroup, domain: domainId},
          {name:this.APP_PARAMS[1], value: task_holder, domain: domainId}
      ]
      await saveTaskAppParams({appParams:arr});
      this.showMessage();
    } catch (error) {
      this.showError(error);
    }

    this.getApplication().stopLoading();
  }



  fillWorkGroup(workgroup, workgroupId = undefined){
    getWorkgroups({status:"ACTIVE"}).then(wgs=>{
        workgroup.setOptions( wgs.map(t => ({...t, value: t.id, description: t.description, name:t.description})))
        if(workgroupId) workgroup.value = workgroupId;
    });
  }

  fillTaskHolder(taskHolder, workgroup = undefined, task_holder= undefined){
    taskHolder.clear();
    getTastHoldersWorkGroup({workgroup, active:1}).then(ths=>{
        taskHolder.setOptions( ths.map( th=> ({...th, value: th.id}) ) );
        if(task_holder) taskHolder.value = task_holder;
    });
  }
}

window.customElements.define('aon-messenger-config',  AonMessengerConfig);
