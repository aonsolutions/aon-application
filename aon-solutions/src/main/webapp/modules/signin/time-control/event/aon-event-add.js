import { AonElement } from "../../../../components/AonElement.js";
import {
  setValueName,
  serializeForm,
  setTime,
  formatDateOrigin,
  isEmptyObject,
} from "../../../../services/utils.js";
import {
  deleteTimeControl,
  getLocation,
  getStatus,
  saveTimeControl,
} from "../../../../services/service.js";
import "../../../../components/aon-card.js";
import "../../../../components/aon-input.js";
import "../../../../components/aon-number.js";
import "../../../../components/aon-date.js";
import "../../../../components/aon-suggestion.js";
import "../../../../components/aon-select.js";
import "../../../../components/aon-switch.js";
import "../../../../components/aon-icon-button.js";
import { AonEventDetailList } from "./aon-event-detail-list.js";
import { ToolbarType } from "../../../../models/enums.js";
import { UserAction } from "../../../user/userEnums.js";

export class AonEventAdd extends AonElement {
  ACTION;
  TOAST;
  TITLE
  TOOLBAR;
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
    if(value) this.setAttribute("data", JSON.stringify(value));
  }

  constructor() {
    super();
    this.id = this.id || "aonEventAdd";
    this.aonSigninEl = this.getElement("aonSignin");
    this.aonSigninEl.addToolbarTitle("Registrar evento");
    this.TOAST = this.getElement(`${this.aonSigninEl.id}Toast`);
    this.aonSigninParentEl = this.aonSigninEl.getParent();
    this.aonSigninParentEl.periodSideNavDisplay(false);
    this.TOOLBAR = this.id + "Toolbar";
  }

  connectedCallback() {
    this.build();
    this.eventListener();
  }

  attributeChangedCallback(name, oldValue, newValue) {}

  async build() {
    this.paintView();
    await this.initLists();
    if (this.isMobile()) this.buildToolbarMobile();
    else this.buildToolbarDesk();
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
    const aonToolbar = this.isMobile() ? "" : `<aon-toolbar id="${this.TOOLBAR}" type="${ToolbarType.SECONDARY}" title="EVENTO"> </aon-toolbar>`;
    const form = `
          ${aonToolbar}
        <form id="${this.id}Form" action="#" onsubmit="return false;">
            <div id="${this.id}Div">
                <div class="aonCol-sm-12">
                    <aon-card id="${this.id}CardEvent" title="Datos del evento"></aon-card>
                </div>
            </div>
        </form>`;

    this.innerHTML = initHtml + form;

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
      
  }
  
  async initLists() {
    await this.listStatus();
    await this.listLocation();
    if (this.data) this.setValues();
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
  }

  buildToolbarDesk() {
    let eventToolbar = this.getElement(this.TOOLBAR);
		eventToolbar.removeButtons();

		eventToolbar.addButton2(UserAction.SAVE, () => this.formSubmit());
    if(this.data && this.data.id){
      eventToolbar.addButton2(UserAction.DELETE, () => this.delete());
    }
		eventToolbar.addButton2(UserAction.BACK, () => this.back());
  }

  buildToolbarMobile() {
    this.aonSigninEl.removeToolbarOptions();
    this.aonSigninEl.addToolbarOption("Save", "save", () => this.formSubmit());
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
      const data = this.data;
      if(isEmptyObject(data.coordinates)) delete data.coordinates;
      else if(data.coordinates){
        data.coordinates = data.coordinates.latitude+","+data.coordinates.longitude;
      }
    
      let date = new Date(data.date);
      if (!date.isValid()) { date = new Date();}

      const newTime = setTime(date);
      let obj = {
        ...data,
        time: newTime,
        date: date
      };
      if (data.id) {
        this.aonSigninEl.addToolbarTitle("Modificar evento");
        if (data.location && data.location.id) {
          obj["location"] = data.location.id;
        }
      }
      for (const property in obj) {
        const value = obj[property];
        if(value) setValueName(property, value);
      }
      this.getElement("name").disabled = "disabled";
    }
  }

  formSubmit() {
    this.save();
  }

  async save() {
    console.log(this.data);
    this.aonSigninEl.startLoading();
    let formValues = this.getFormValues();
    const data = {
      ...formValues,
      task_holder: this.data.task_holder.id,
      date: new Date(
        formatDateOrigin(formValues.date) + " " + formValues.time
      ).getTime(),
    };
    try {
      const { id } = await saveTimeControl(data);
      if (id) {
        setValueName("id", id);
      }
      this.TOAST.start({
        message: "Datos guardados!",
        type: "success",
        delay: 3000,
      });
      // this.back();
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
        this.TOAST.start({ message: `En desarrollo!` });
        this.back();
      } catch (error) {
        this.TOAST.start({ message: error, type: "error" });
      }
      this.aonSigninEl.stopLoader();
    }
  }

  back() {
    this.aonSigninEl.back();
    let aonEventDetailList = new AonEventDetailList();
    if(this.data && this.data.date){
      const startDate = formatDateOrigin(new Date(this.data.date));
      aonEventDetailList.DATE_TASK ={
        startDate,
        endDate:startDate
      }
    }
    this.aonSigninEl.setContent(aonEventDetailList);
  }
}

window.customElements.define("aon-event-add", AonEventAdd);
