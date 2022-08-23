import { AonElement } from "../../../../components/AonElement.js";
import { setValueName, serializeForm, isEmptyObject } from "../../../../services/utils.js";
import { deleteTimeControl, getLocation, getStatus, saveTimeControlDetail, getTimeControlHistoric } from "../../../../services/service.js";
import { ToolbarType } from "../../../../models/enums.js";
import { SIGNIN_VIEWS } from "../../signinEnums.js";
import { CONSTANT, EVENT, MATERIAL_ICONS, MSG, TAG } from "../../../../environments/environments.js";
import { createFormEvent, createCardEvent } from "../../createComponent.js";
import { CreateComponent } from "../../../../components/CreateComponent.js";
import { AonMessenger } from "../../../messenger/aon-messenger.js";
import { TASK_SOURCE } from "../../../messenger/MessengerEnums.js";
import { AON_TAGS } from "../../../../environments/aonTag.js";
import { AonDateUtils } from "../../../utils/AonDateUtils.js";
import { AonMap } from "../../../../components/aon-map.js";
import * as ACTION from '../../../actions.js';
import { setStyles } from "../../../../services/utilsComponents.js";
import { AonBasicTable } from "../../../../components/aon-basic-table.js";


export class AonEventAdd extends AonElement {
  ACTION;
  TITLE;
  TOOLBAR;
  START_DATE
  TASK_HOLDER;
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
    return JSON.parse(this.getAttribute(CONSTANT.DATA));
  }

  set data(value) {
    if (value) this.setAttribute(CONSTANT.DATA, JSON.stringify(value));
  }

  constructor() {
    super();
    this.id = this.id || SIGNIN_VIEWS.AON_EVENT_ADD;
    this.TOOLBAR = this.id + "Toolbar";
    this.applicationEl = this.getApplication();
    this.applicationParentEl = this.getApplicationParent();
    this.applicationEl.addToolbarTitle("Registrar evento");
    this.applicationParentEl.periodSideNavDisplay(false);
    this.TASK_HOLDER = this.applicationParentEl.TASK_HOLDER;
  }

  connectedCallback() {
    this.build();
  }

  attributeChangedCallback(name, oldValue, newValue) {}

  async build() {
    this.paintView();
    await this.initLists();
    this.buildToolbar();
    this.eventListener();
  }

  paintView() {

    CreateComponent.createAonToolbar({ id: this.TOOLBAR, type: ToolbarType.SECONDARY}, this);
  
    createFormEvent(this.id, this);

    this.applicationEl.removeToolbarOptions();

    let aonCardEvent = this.getElement(`${this.id}CardEvent`);
    createCardEvent(aonCardEvent.getContent());

    if (!isEmptyObject(this.data) && !isEmptyObject(this.data.coordinates)) 
      this.paintViewMap();
  }

  async paintViewMap() {
    let cardCoordinate = document.querySelector(`#${this.id}CardCoordinate`);
    const { coordinates } = this.data;
    let aonMap = new AonMap();
    aonMap.POSITION = coordinates;
    cardCoordinate.setContent(aonMap);
    
    cardCoordinate.setAttribute("visible", true);
  }


  async initLists() {
    this.listStatus();
    await this.listLocation().catch(()=>null);
    this.setValues();
  }

  eventListener() {
    let location = this.getElement("location");
    location.addEventListener(EVENT.CHANGE, ({ detail }) => {
      if (detail && detail.coordinates) {
        const coordinates = detail.coordinates;
        setValueName("coordinates", `${coordinates.latitude},${coordinates.longitude}`);
      }
    });

    let aonSubmit = this.getElement(`${this.id}Submit`);
    if (aonSubmit) aonSubmit.addEventListener(EVENT.CLICK, () => this.save());
  }

  buildToolbar() {
    let toolbarEl = this.getElement(this.TOOLBAR);
    toolbarEl.removeButtons();
    if (this.data && this.data.id) {

      if(this.applicationParentEl.isEmployee() && this.applicationParentEl.getDur().isMessenger()){
        toolbarEl.addButton2({
          id: "request",
          name: "Solicitar",
          icon: MATERIAL_ICONS.ASSIGNMENT
        }, () => this.goMessenger());
      }

      if(!this.applicationParentEl.isEmployee()){
        toolbarEl.addButton2({
          id: 'historic',
          name: MSG.HISTORIC,
          icon: MATERIAL_ICONS.ASSIGNMENT
        }, () => this.paintHistoric());
        
        toolbarEl.addButton2(ACTION.DELETE, () => this.delete());
      }
      toolbarEl.title = MSG.EDIT;
    } else {
      toolbarEl.title = MSG.REGISTER;
    }
    if(!this.applicationParentEl.isEmployee()){
      toolbarEl.addButton2(ACTION.SAVE, () => this.save());
    }
    toolbarEl.addButton2(ACTION.BACK, () => this.back());
  }

  getFormValues() {
    const serialize = serializeForm(this.getElement(`${this.id}Form`));
    return {
      ...serialize,
      task_holder: this.TASK_HOLDER.id,
      date: new Date( AonDateUtils.formatDateOrigin(serialize.date) + " " + serialize.time ).getTime(),
    };
  }

  listStatus() {
    let status = this.getElement("status");
    try {
      const resp = getStatus();
      status.options = JSON.stringify(
        resp.map((r) => ({...r, name: `${r.name}`, value: r.value}))
      );
    } catch (error) {}
  }

  async listLocation() {
    let location = this.getElement("location");
    try {
      const resp = await getLocation();
      location.options = JSON.stringify(
        resp.map((r) => {
          return {
            ...r,
            name: `${r.description}`,
            value: `${r.id}`,
          };
        })
      );
    } catch (error) {}
  }

  setValues() {
    if (this.data) {
      let data = this.data;
      let date = new Date(data.date);
      if (data.coordinates) {data.coordinates = data.coordinates.latitude + "," + data.coordinates.longitude;}
      if (data.location && data.location.id) {data.location = data.location.id;}
      if (!date.isValid()) {date = new Date();}
      data.date = date;
      data.time = AonDateUtils.setTime(date);
      data.name = this.TASK_HOLDER.name;
      for (const property in data) {
        const value = data[property];
        if (value) setValueName(property, value);
      }
      this.getElement("name").disabled = "disabled";
    }
    
    if(this.applicationParentEl.isEmployee()) 
      this.formRead();
  }

  async save() {
    this.applicationEl.startLoading();
    try {
      let formValues = this.getFormValues();
      const { id, date:start_date } = await saveTimeControlDetail(formValues);
      if (id) setValueName("id", id);
 
      if(start_date) this.START_DATE =  AonDateUtils.formatDateOrigin(new Date(start_date));

      this.showToast({
        message: MSG.SAVED_DATA,
        type: CONSTANT.SUCCESS,
        delay: 3000,
      });
    } catch (error) {
      this.showToast(error);
    }
    this.applicationEl.stopLoading();
  }

  delete() {
    this.applicationEl.confirmDialog(MSG.DELETE, `${MSG.DELETE_CONFIRM}lo?`, async()=>{
      const data = this.getFormValues();
      this.applicationEl.startLoader();
      try {
        await deleteTimeControl(data);
        this.showToast({ message: MSG.DELETED_DATA });
        this.back();
      } catch (error) {
        this.showToast(error);
      }
      this.applicationEl.stopLoader();
    });
  }

  formRead() {
    [...this.getElement(`${this.id}Form`).querySelectorAll(AON_TAGS)].map(el => el.readonly = true);
  }

  back() {
    let data = undefined;
    let startDate = AonDateUtils.formatDateOrigin(this.data.date);
    if(this.data) data = {...this.data, start_date:startDate};
    if(this.START_DATE) startDate = AonDateUtils.formatDateOrigin(this.START_DATE);
    this.applicationParentEl.DATE_TMP = {...this.applicationParentEl.DATE_TMP, startDate};
    this.applicationParentEl.showView(SIGNIN_VIEWS.AON_EVENT_DETAIL_LIST, data);
  }

  async paintHistoric(){
    try {
      const historics = await this.getHistoric();
      let d = this.getApplication().getDialog();
      if(d){
          const div = this.createElement("div");
          const length = historics.length;

          d.clear();
          if (!this.isMobile()) 
              d.width = '70%';
          d.setTitle(MSG.HISTORIC);
          d.setContent(div);
          d.addAcceptAction(() => {});
          d.open();

          let table = new AonBasicTable();
          div.appendChild(table);

          if(length > 0 && historics[0] && historics[0].last_modification_date){
            table.addRow();
            table.addCell(this.lastModification(historics[0]), 5);
          }

          table.addRow();
          table.addCell(this.creationHeader(MSG.USER));
          table.addCell(this.creationHeader(MSG.LAST_MODIFICATION));
          table.addCell(this.creationHeader("F. Registro anterior"));
          table.addCell(this.creationHeader(MSG.LOCATION));
          table.addCell(this.creationHeader(MSG.STATUS));

          for(let i = length>1 ? 1 : 0; i < length; i++){
            const historic = historics[i];
            table.addRow();
            table.addCell(this.creationTd(historic.creation_user));
            table.addCell(this.creationTd(historic.creation_date));
            table.addCell(this.creationTd(historic.registration_date));
            table.addCell(this.creationTd(historic.location));
            table.addCell(this.creationTd(historic.status));
          }
      }
    } catch (error) {
      console.log(error);
    }
  }

  creationHeader(text){
    return this.creationEle(text);
  }

  creationTd(text){
    const td = this.creationEle(text);
    td.style.fontWeight = 400;
    return td;
  }

  creationEle(text){
    const elem = setStyles(this.createElement(TAG.DIV),{
      color: "#5f6368",
      fontWeight: 500
    });
    elem.innerHTML = text ? text : "";
    return elem;
  }

  lastModification(historic){
    let div = this.creationEle(`<span style="color:#5f6368; font-weight: 500;">${MSG.LAST_MODIFICATION}</span> (${historic.last_modification_user}) ${historic.last_modification_date}`);
    div.style.marginBottom="7px";
    div.style.fontWeight=400;
    return div;
  }

  async getHistoric(){
    const resp = await getTimeControlHistoric({id:this.data.id});
    return resp.map((tm,idx)=> {
      let obj = {};
      if(idx===0){
        if(tm.creation_date){
          obj = { creation_user: tm.creation_user, creation_date: AonDateUtils.setDateTimestamp(new Date(tm.creation_date)) };

          if(tm.modification_user) obj.last_modification_user = tm.modification_user;
          if(tm.modification_date) obj.last_modification_date = AonDateUtils.setDateTimestamp(new Date(tm.modification_date));
        }
      } else {
        obj = {
          registration_date:  AonDateUtils.setDateTimestamp(new Date(tm.date)),
          creation_user: tm.creation_user,
          creation_date: AonDateUtils.setDateTimestamp(new Date(tm.creation_date)),
          location: tm.location && tm.location.id ? tm.location.name : null,
          status: getStatus(tm.status).name
        }
      }
      return obj;
    });
  }

  goMessenger(){
    const form = this.getFormValues();

    const description = JSON.stringify({timeId: form.id, date:form.date, time:form.time, task_holder: form.task_holder});
    let data = { source: TASK_SOURCE.REQUEST, source_id: 3, description };
    let aonMessenger = new AonMessenger();
    aonMessenger.data = data;
    this.rootPanel(aonMessenger);
  }
}

window.customElements.define("aon-event-add", AonEventAdd);
