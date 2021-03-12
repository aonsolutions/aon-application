import { AonElement } from "../../../../components/AonElement.js";
import {
  setValueName,
  serializeForm,
  setTime,
  formatDateOrigin,
  isEmptyObject,
  waitEl,
} from "../../../../services/utils.js";
import {
  deleteTimeControl,
  getLocation,
  getStatus,
  saveTimeControlDetail,
} from "../../../../services/service.js";
import { ToolbarType } from "../../../../models/enums.js";
import { UserAction } from "../../../user/userEnums.js";
import { INPUTS_ALL } from "../../../../environments/constants.js";
import "../../../../components/aon-card.js";
import "../../../../components/aon-input.js";
import "../../../../components/aon-date.js";
import "../../../../components/aon-select.js";


export class AonEventAdd extends AonElement {
  ACTION;
  TOAST;
  TITLE;
  TOOLBAR;
  START_DATE
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
    this.id = this.id || "aonEventAdd";
    this.TOOLBAR = this.id + "Toolbar";
    this.aonSigninEl = this.getApplication();
    this.TOAST = this.aonSigninEl.getToast();
    this.aonSigninParentEl = this.aonSigninEl.getParent();
    this.aonSigninEl.addToolbarTitle("Registrar evento");
    this.aonSigninParentEl.periodSideNavDisplay(false);
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
    const initHtml = `
            <style>
              .aonCard{
                position: relative;
                display: -ms-flexbox;
                display: flex;
                -ms-flex-direction: column;
                flex-direction: column;
                min-width: 0;
                word-wrap: break-word;
                background-color: #fff;
                background-clip: border-box;
                border: 1px solid rgba(0,0,0,.125);
                border-radius: .25rem;
              }
            </style>
        `;
    const aonToolbar = `<aon-toolbar id="${this.TOOLBAR}" type="${ToolbarType.SECONDARY}"> </aon-toolbar>`;
    const aonCardCoordinate = ` <div class="aonCol-sm-12"><aon-card id="${this.id}CardCoordinate" title="Coordenadas" visible="false"></aon-card> </div>`;
    const form = `
        <form id="${this.id}Form" action="#" onsubmit="return false;">
            <div id="${this.id}Div">
                <div class="aonCol-sm-12">
                    <aon-card id="${this.id}CardEvent" title="Datos del evento"></aon-card>
                </div>
                ${aonCardCoordinate}
            </div>
        </form>`;

    this.innerHTML = aonToolbar + initHtml + form;

    this.aonSigninEl.removeToolbarOptions();

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
      if(!this.aonSigninParentEl.isEmployee()){
        toolbarEl.addButton2(UserAction.DELETE, () => this.delete());
      }
      toolbarEl.title = "Edición";
    } else {
      toolbarEl.title = "Registro";
    }
    if(!this.aonSigninParentEl.isEmployee()){
      toolbarEl.addButton2(UserAction.SAVE, () => this.save());
    }
    toolbarEl.addButton2(UserAction.BACK, () => this.back());
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
      if (data.task_holder) {data.name = data.task_holder.name;}
      if (!date.isValid()) {date = new Date();}
      data.date = date;
      data.time = setTime(date);

      for (const property in data) {
        const value = data[property];
        if (value) setValueName(property, value);
      }
      this.getElement("name").disabled = "disabled";
    }
    
    if(this.aonSigninParentEl.isEmployee()) this.formRead();
  }

  async save() {
    this.aonSigninEl.startLoading();
    try {
      let formValues = this.getFormValues();
      const data = {
        ...formValues,
        task_holder: this.data.task_holder.id,
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
      this.TOAST.start({
        message: "Datos guardados!",
        type: "success",
        delay: 3000,
      });
    } catch (error) {
      this.TOAST.start({ message: error, type: "error" });
    }
    this.aonSigninEl.stopLoading();
  }

  async delete() {
    const data = this.getFormValues();
    if (confirm(`Estas seguro de eliminarlo?`)) {
      this.aonSigninEl.startLoader();
      try {
        await deleteTimeControl(data);
        this.TOAST.start({ message: `Datos eliminados!` });
        this.back();
      } catch (error) {
        this.TOAST.start({ message: error, type: "error" });
      }
      this.aonSigninEl.stopLoader();
    }
  }

  formRead() {
    [...this.getElement(`${this.id}Form`).querySelectorAll(INPUTS_ALL)].map(el => {
      el.readonly = true;
    });
  }

  back() {
    let data= undefined;
    if(this.data) data = {...this.data, start_date:this.data.date};
    if(this.START_DATE) data.start_date = this.START_DATE;
    this.aonSigninParentEl.showView("aonEventDetailList", data);
  }
}

window.customElements.define("aon-event-add", AonEventAdd);
