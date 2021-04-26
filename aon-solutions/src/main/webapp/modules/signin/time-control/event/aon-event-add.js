import { AonElement } from "../../../../components/AonElement.js";
import {
  setValueName,
  serializeForm,
  setTime,
  formatDateOrigin,
  isEmptyObject,
  waitEl,
  handleError,
} from "../../../../services/utils.js";
import {
  deleteTimeControl,
  getLocation,
  getStatus,
  saveTimeControlDetail,
} from "../../../../services/service.js";
import { ToolbarType } from "../../../../models/enums.js";
import { INPUTS_ALL } from "../../../../environments/constants.js";
import "../../../../components/aon-card.js";
import "../../../../components/aon-input.js";
import "../../../../components/aon-date.js";
import "../../../../components/aon-select.js";
import { SIGNIN_VIEWS } from "../../signinEnums.js";

import * as ACTION from '../../../actions.js';
import { MSG } from "../../../../environments/environments.js";

export class AonEventAdd extends AonElement {
  ACTION;
  TITLE;
  TOOLBAR;
  START_DATE
  TASK_HOLDER;
  static get observedAttributes() {
    return ["data"];
  }

  get id() {
    return this.getAttribute("id");
  }

  set id(id) {
    this.setAttribute("id", id);
  }

  get data() {
    return JSON.parse(this.getAttribute("data"));
  }

  set data(value) {
    if (value) this.setAttribute("data", JSON.stringify(value));
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

    const aonToolbar = `<aon-toolbar id="${this.TOOLBAR}" type="${ToolbarType.SECONDARY}"> </aon-toolbar>`;
    const aonCardCoordinate = ` <div class="aonCol-sm-12"><aon-card id="${this.id}CardCoordinate" title="Coordenadas" visible="false" flex="true"></aon-card> </div>`;
    const form = `
        <form id="${this.id}Form" action="#" onsubmit="return false;">
            <div id="${this.id}Div">
                <div class="aonCol-sm-12">
                    <aon-card id="${this.id}CardEvent" title="Datos del evento" flex="true"></aon-card>
                </div>
                ${aonCardCoordinate}
            </div>
        </form>`;

    this.innerHTML = aonToolbar + form;

    this.applicationEl.removeToolbarOptions();

    let aonCardEvent = this.getElement(`${this.id}CardEvent`);
    aonCardEvent.setContentHTML(`
            <div class="aonCol-sm-6 aonCol-md-3">
             <aon-input name="name" id="name" description="Nombre" type="text"></aon-input>
            </div>
            <div class="aonCol-sm-6 aonCol-md-2">
                <aon-select name="status" id="status" title="Estado"></aon-select>
            </div>
            <div class="aonCol-sm-6 aonCol-md-3">
                <aon-select name="location" id="location" title="Ubicación"></aon-select>
            </div>
            <div class="aonCol-sm-6 aonCol-md-2">
                <aon-date name="date" id="date" title="Fecha"></aon-date>
            </div>
            <div class="aonCol-sm-6 aonCol-md-2">
              <aon-input name="time" id="time" description="Hora" type="time"></aon-input>
            </div>
            <aon-input name="id" id="id" type="text" visible="false"></aon-input>
            <aon-input name="coordinates" id="coordinates" type="text" visible="false"></aon-input>
        `);
    if (!this.isMobile()) this.paintViewMap();
  }

  async paintViewMap() {
    let aonMap = await waitEl(`#${this.id}CardCoordinate`);
    if (!isEmptyObject(this.data) && !isEmptyObject(this.data.coordinates)) {
      const { coordinates } = this.data;
      const zoom = 16;
      let iframeId = this.id + "Iframe";
      let iframe = this.createElement("iframe");
      iframe.id = iframeId;
      iframe.frameborder = 0;
      iframe.style = "border:0;height: 400px;width: 100%;";
      iframe.src = `https://maps.google.es/maps?q=${coordinates.latitude},${coordinates.longitude}&z=${zoom}&output=embed&hl=es`;
      aonMap.setContent(iframe);
      aonMap.setAttribute("visible", true);
    }
  }

  async initLists() {
    await this.listStatus();
    await this.listLocation();
    this.setValues();
  }

  eventListener() {
    let location = this.getElement("location");
    location.addEventListener("change", ({ detail }) => {
      if (detail && detail.coordinates) {
        const coordinates = detail.coordinates;
        setValueName(
          "coordinates",
          `${coordinates.latitude},${coordinates.longitude}`
        );
      }
    });

    let aonSubmit = this.getElement(`${this.id}Submit`);
    if (aonSubmit) aonSubmit.addEventListener("click", () => this.save());
  }

  buildToolbar() {
    let toolbarEl = this.getElement(this.TOOLBAR);
    toolbarEl.removeButtons();
    if (this.data && this.data.id) {
      if(!this.applicationParentEl.isEmployee()){
        toolbarEl.addButton2(ACTION.DELETE, () => this.delete());
      }
      toolbarEl.title = "Edición";
    } else {
      toolbarEl.title = "Registro";
    }
    if(!this.applicationParentEl.isEmployee()){
      toolbarEl.addButton2(ACTION.SAVE, () => this.save());
    }
    toolbarEl.addButton2(ACTION.BACK, () => this.back());
  }

  getFormValues() {
    const form = this.getElement(`${this.id}Form`);
    return serializeForm(form);
  }

  async listStatus() {
    let status = this.getElement("status");
    try {
      const resp = await getStatus();
      status.options = JSON.stringify(
        resp.map((r) => {
          return {
            ...r,
            name: `${r.name}`,
            value: r.value,
          };
        })
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
      data.time = setTime(date);
      data.name = this.TASK_HOLDER.name;
      for (const property in data) {
        const value = data[property];
        if (value) setValueName(property, value);
      }
      this.getElement("name").disabled = "disabled";
    }
    
    if(this.applicationParentEl.isEmployee()) this.formRead();
  }

  async save() {
    this.applicationEl.startLoading();
    try {
      let formValues = this.getFormValues();
      const data = {
        ...formValues,
        task_holder: this.TASK_HOLDER.id,
        date: new Date(
          formatDateOrigin(formValues.date) + " " + formValues.time
        ).getTime(),
      };
      const { id, date:start_date } = await saveTimeControlDetail(data);
      if (id) {
        setValueName("id", id);
      }
      if(start_date){
        this.START_DATE =  formatDateOrigin(new Date(start_date));
      }
      this.applicationEl.getToast().start({
        message: MSG.SAVED_DATA,
        type: "success",
        delay: 3000,
      });
    } catch (error) {
      this.applicationEl.getToast().start(handleError(error));
    }
    this.applicationEl.stopLoading();
  }

  delete() {
    this.applicationEl.confirmDialog("Eliminar", "Estas seguro de eliminarlo?", async()=>{
      const data = this.getFormValues();
      this.applicationEl.startLoader();
      try {
        await deleteTimeControl(data);
        this.applicationEl.getToast().start({ message: MSG.DELETED_DATA });
        this.back();
      } catch (error) {
        this.applicationEl.getToast().start(handleError(error));
      }
      this.applicationEl.stopLoader();
    });
  }

  formRead() {
    [...this.getElement(`${this.id}Form`).querySelectorAll(INPUTS_ALL)].map(el => {
      el.readonly = true;
    });
  }

  back() {
    let data = undefined;
    let startDate = formatDateOrigin(this.data.date);
    if(this.data) data = {...this.data, start_date:startDate};
    if(this.START_DATE) startDate = formatDateOrigin(this.START_DATE);
    this.applicationParentEl.DATE_TMP = {...this.applicationParentEl.DATE_TMP, startDate};
    this.applicationParentEl.showView(SIGNIN_VIEWS.AON_EVENT_DETAIL_LIST, data);
  }
}

window.customElements.define("aon-event-add", AonEventAdd);
