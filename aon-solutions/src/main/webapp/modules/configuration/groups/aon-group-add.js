import { AonElement } from "../../../components/AonElement.js";
import { setValueName, serializeForm } from "../../../services/utils.js";
import { ToolbarType } from "../../../models/enums.js";
import * as ACTION from '../../actions.js';
import { CONSTANT, CSS, MSG, TAG } from "../../../environments/environments.js";
import { createCard, createForm, createInput, createToolbar } from "../../notification/createComponent.js";
import { AonGroupList } from "./aon-group-list.js";
import {deleteWorkgroup, saveWorkgroup} from '../../../services/workgroupService.js';
import { AonSwitch } from "../../../components/aon-switch.js";

export class AonGroupAdd extends AonElement {
  static get observedAttributes() {
    return [CONSTANT.DATA];
  }

  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
  }

  get data() {
    let data = this.getAttribute(CONSTANT.DATA) ? this.getAttribute(CONSTANT.DATA) : "{}";
    return JSON.parse(data);
  }

  set data(value) {
    this.setAttribute(CONSTANT.DATA, JSON.stringify(value));
  }


  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  attributeChangedCallback(name, oldValue, newValue) {
  }

  initialize(){
    this.id = this.id || "aonGroupAdd";
    this.TOOLBAR = this.id + "Toolbar";
    this.applicationEl = this.getApplication();
  }

  build() {
    this.applicationEl.removeToolbarOptions();
    this.paintView();
    this.buildToolbar();
  }

  paintView() {
    createToolbar({ id:this.TOOLBAR, type:ToolbarType.SECONDARY}, this);

    const form = createForm(this.id+"Form");
    this.appendChild(form.element);

    let div = this.createElement(TAG.DIV);
    div.id = this.id+"Div";
    const className = this.isMobile() ? CSS.AON_MOBILE_SUB_CONTENT : CSS.AON_SUB_CONTENT;
    div.className = className;
    form.appendChild(div);

    let div2 = this.createElement(TAG.DIV);
    div2.className = CSS.AON_COL_SM_6;
    div.appendChild(div2);
    
    const aonCard = createCard({id: this.id+"Card", title:"Datos del " +MSG.GROUP, flex:"true"}, div2).getContent();

    let divG = this.createElement(TAG.DIV);
    divG.className = CSS.AON_COL_XS_9;
    aonCard.appendChild(divG);
    createInput({
      attributes:{
        name:"description",
        id:"description" ,
        description:MSG.NAME,
        type:"text",
        value: this.data.description || ""
      }
    }, divG);

    divG = this.createElement(TAG.DIV);
    divG.className = CSS.AON_COL_XS_3;
    aonCard.appendChild(divG);

    if(this.data.id){
      let aonSwitch = new AonSwitch();
      aonSwitch.id = "status";
      aonSwitch.name = "status";
      aonSwitch.title = "Activo";
      aonSwitch.checked = this.data.status ? true : false;
      divG.appendChild(aonSwitch);
    }

    createInput({
      attributes:{
        name:"id",
        id:"id" ,
        type:"text",
        visible:"false",
        value: this.data.id || ""
      }
    }, aonCard);
  }

  buildToolbar(){
    const toolbarEl = this.getElement(this.TOOLBAR);
    toolbarEl.removeButtons();
    if(this.data && this.data.id){
      toolbarEl.addButton2(ACTION.DELETE, () =>this.delete());
      toolbarEl.title = MSG.EDIT;
    } else {
      toolbarEl.title = MSG.REGISTER;
    }
    toolbarEl.addButton2(ACTION.SAVE, () => this.save());
    toolbarEl.addButton2(ACTION.BACK, () => this.back());
  }

  getFormValues() {
    const form = this.getElement(`${this.id}Form`);
    const values = serializeForm(form);
    if(!this.data.id) values.status = true;
    return values;
  }

  async save() {
    const data = this.getFormValues();
    const count = Object.keys(data).length;
    if (count) {
      this.applicationEl.startLoading();
      try {
        const { id } = await saveWorkgroup(data);
        this.showToast({ message: MSG.SAVED_DATA, type: CONSTANT.SUCCESS });
        if (id) { setValueName("id", id); }
      } catch (error) {
        this.showToast(error);
      }
      this.applicationEl.stopLoading();
    }
  }

  async delete() {
    this.applicationEl.confirmDialog(MSG.DELETE, `${MSG.DELETE_CONFIRM}?`, async()=>{
      this.applicationEl.startLoading();
      try {
        const data = this.getFormValues();
        await deleteWorkgroup(data);
        this.showToast({ message: MSG.DELETED_DATA });
        this.back();
      } catch (error) {
        this.showToast(error);
      }
      this.applicationEl.stopLoading();
    });
  }

  back() {
    this.applicationEl.setContent(new AonGroupList());
  }
}

window.customElements.define("aon-group-add", AonGroupAdd);
