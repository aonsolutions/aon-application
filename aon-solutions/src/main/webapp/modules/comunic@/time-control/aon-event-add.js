import { AonElement } from "../../../components/AonElement.js";
import {
  setValueName,
  serializeForm,
  setTime,
} from "../../../services/utils.js";
import { getLocation, getStatus } from "../../../services/service.js";
import "../../../components/aon-card.js";
import "../../../components/aon-input.js";
import "../../../components/aon-number.js";
import "../../../components/aon-date.js";
import "../../../components/aon-suggestion.js";
import "../../../components/aon-select.js";
import "../../../components/aon-switch.js";
import "../../../components/aon-icon-button.js";
import "./aon-event-list.js";

export class AonEventAdd extends AonElement {
  _contrato;
  ACTION;
  TOAST;
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
    this.setAttribute("data", JSON.stringify(value));
  }

  constructor() {
    super();
    this.ACTION = "CREATE";
    this.id = this.id || "aonEventAdd";
    this.TOOLBAR = this.id + "Toolbar";
    this.aonComunicaEl = this.getElement("aonComunica");
    this.aonComunicaToolbar = this.getElement(
      `${this.aonComunicaEl.id}Toolbar`
    );
    this.aonComunicaToolbar.setAttribute("option", "Registrar evento");
    this.TOAST = this.getElement(`${this.aonComunicaEl.id}Toast`);
  }

  connectedCallback() {
    this.paintView();
    this.build();
    this.eventListener();
  }

  attributeChangedCallback(name, oldValue, newValue) {
    // if ("data" == name && newValue) {}
  }

  paintView() {
    const toolbarMobile = !this.isMobile()
      ? `<aon-toolbar id="${this.TOOLBAR}" type="secondary" title="Evento"> </aon-toolbar>`
      : "";
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
            ${toolbarMobile} 
        `;

    const form = `
        <form id="${this.id}Form" action="#" onsubmit="return false;">
            <div id="${this.id}Div">
                <div class="aonCol-sm-12">
                    <aon-card id="${this.id}CardEvent" title="Datos del evento"></aon-card>
                </div>
            </div>
        </form>`;

    this.innerHTML = initHtml + form;

    this.aonComunicaEl.removeToolbarOptions();

    const buttonSubmit = this.isMobile()
      ? `<div class="aonCol-sm-12 aonCol-md-12"><br/> <div class="offset-4" id="${this.id}DivSubmit"> 
        </div>`
      : "";

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
            ${buttonSubmit}
        `);

    if (!this.isMobile()) this.buildToolbar();
    else {
      this.getElement(
        `${this.id}DivSubmit`
      ).innerHTML = `<button class="aonButton" type="button" id="${this.id}Submit">Guardar</button>`;
    }
  }

  build() {
    this.initLists();
  }

  async initLists() {
    await this.listStatus();
    await this.listLocation();
    if (this.data) this.edit();
  }

  eventListener() {
    let aonSubmit = this.getElement(`${this.id}Submit`);
    if (aonSubmit) aonSubmit.addEventListener("click", () => this.formSubmit());
  }

  buildToolbar() {
    const toolbar = this.getElement(this.TOOLBAR);
    toolbar.removeButtons();

    toolbar.addButton2(
      {
        id: "Delete",
        name: "Eliminar",
        icon: "delete_forever",
      },
      () => this.removeData()
    );
    toolbar.addButton2(
      {
        id: "Save",
        name: "Comunicar",
        icon: "send",
      },
      () => this.formSubmit()
    );

    toolbar.addButton2(
      {
        id: "Previous",
        name: "Volver",
        icon: "arrow_back",
      },
      () => this.back()
    );
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
            name: `${r.name}`,
            value: `${r.name}`,
          };
        })
      );
    } catch (error) {}
  }

  edit() {
    const data = this.data;
    if (data) {
      this.ACTION = "UPDATE";
      this.aonComunicaToolbar.setAttribute("option", "Modificar evento");
      const newTime = setTime(new Date(data.date));
      let obj = {
        ...data,
        time: newTime,
      };

      for (const property in obj) {
        setValueName(property, obj[property]);
      }
      this.getElement("name").disabled = "disabled";
    }
  }

  removeData(){
    const data = this.getFormValues();
    if (confirm(`Estas seguro de eliminarlo?`)) {
      this.aonComunicaEl.startLoader();
      try {
        // await postDeleteData(data);
        this.TOAST.start({ message: `Datos eliminados!` });
        this.back();
      } catch (error) {
        this.TOAST.start({ message: error, type: 'error' });
      }
      this.aonComunicaEl.stopLoader();
    }
  }

  formSubmit() {
    switch (this.ACTION) {
      case "CREATE":
        this.save();
        break;
      case "UPDATE":
        this.update();
        break;
      default:
        break;
    }
  }

  async save() {
    this.aonComunicaEl.startLoading();
    console.log("save");
    console.table(this.getFormValues());
    try {
      //   await postData(this.getFormValues());
      this.TOAST.start({
        message: "Datos registrados!",
        type: "success",
        delay: 3000,
      });
      //   this.back();
    } catch (error) {
      this.TOAST.start({ message: error, type: "error" });
    }
    this.aonComunicaEl.stopLoading();
  }

  async update() {
    this.aonComunicaEl.startLoading();
    console.log("update");
    console.table(this.getFormValues());
    try {
      //   await postUpdateData(this.getFormValues());
      this.TOAST.start({
        message: "Datos Actualizados!",
        type: "primary",
        delay: 3000,
      });
      // this.back();
    } catch (error) {
      this.TOAST.start({ message: error, type: "error" });
    }
    this.aonComunicaEl.stopLoading();
  }

  back() {
    this.aonComunicaEl.setContentHTML(`<aon-time-control></aon-time-control>`);
  }
}

window.customElements.define("aon-event-add", AonEventAdd);
