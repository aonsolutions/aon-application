import { AonElement } from "../../components/AonElement.js";
import {  getTaskHolderNoCache, saveTastHolder } from "../../services/service.js";
import { ToolbarType } from "./../../models/enums.js";
import { TaskHolderEnums } from "./TaskHolderEnums.js";
import { TaskHolderComponent } from "./TaskHolderComponent.js";
import * as ACTION from '../actions.js';
import { CONSTANT, CSS, EVENT, MSG, TAG } from "../../environments/environments.js";
import { CreateComponent } from "../../components/CreateComponent.js";
import { AonTab } from "../../components/aon-tab.js";
import { setStyles } from "../../services/utilsComponents.js";
import { TaskHolder } from "../../models/project/TaskHolder.js";


export class AonTaskHolder extends AonElement {
  AON_TAB;
  CONTENT;
  taskholder;
  static get observedAttributes() {
    return [CONSTANT.ADD];
  }

  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
  }
  
  get data() {
    return JSON.parse(this.getAttribute(CONSTANT.DATA));
  }

  set data(value) {
    this.setAttribute(CONSTANT.DATA, JSON.stringify(value));
  }


  constructor() {
    super();
  }

  connectedCallback() {
    this.build();
  }

  attributeChangedCallback(name, oldValue, newValue) {}

  build() {
    this.initialize();

    if(this.data && this.data.id){
      getTaskHolderNoCache({id:this.data.id})
      .then((th)=>{
        this.taskholder = new TaskHolder(th);
        this.buildToolbar();
        this.buildTabs();
        this.buildContent();
        this.buildFormData();
      });
    }
  }

  initialize(){
    this.id = TaskHolderEnums.TASK_HOLDER_VIEWS.AON_TASK_HOLDER;
  }

  buildTabs(){
    const tab = new AonTab();
    tab.id = this.id+"aonTab";
    
    let options = [];

    options.push({ 
      title: "Datos", 
      fn: () => {
        this.buildFormData();
      }
    });

    tab.setOptions(options);

    this.AON_TAB = tab;

    this.appendChild(tab);
  }

  buildContent() {
    this.CONTENT = setStyles(this.createElement(TAG.DIV),{display: "flex", flexWrap:"wrap", width:"50%" });
    this.appendChild(this.CONTENT);
  }

  buildToolbar(){
    this.TOOLBAR = CreateComponent.createAonToolbar({ id:this.id + "Toolbar", type:ToolbarType.SECONDARY}, this);

    if(this.data && this.data.id){
      this.TOOLBAR.addButton2(ACTION.DELETE, () =>this.delete());
      this.TOOLBAR.title = MSG.EDIT;
    } else {
      this.TOOLBAR.title = MSG.REGISTER;
    }
    this.TOOLBAR.addButton2(ACTION.SAVE, () => this.save());
    this.TOOLBAR.addButton2(ACTION.BACK, () => this.back());
  }


  buildFormData(){
    this.CONTENT.innerHTML = "";
    TaskHolderComponent.createFormAdd(this, this.CONTENT, this.taskholder);
  }

  async save() {
    this.getApplication().startLoading();
    try {
      await saveTastHolder(this.taskholder);
      this.showToast({ message: MSG.SAVED_DATA, type: CONSTANT.SUCCESS });
    } catch (error) {
      this.showToast(error);
    }
    this.getApplication().stopLoading();
  }

  async delete() {
    this.getApplication().confirmDialog(MSG.DELETE, `${MSG.DELETE_CONFIRM}?`, async()=>{
      this.getApplication().startLoading();
      try {
        this.taskholder.setActive(false);
        await saveTastHolder(this.taskholder);
        this.showToast({ message: MSG.DELETED_DATA });
        this.back();
      } catch (error) {
        this.showToast(error);
      }
      this.getApplication().stopLoading();
    });
  }

  back() {
    this.getApplication().getParent().showView(TaskHolderEnums.TASK_HOLDER_VIEWS.AON_TASK_HOLDER_LIST);
  }
}

window.customElements.define("aon-taskholder-add", AonTaskHolder);
