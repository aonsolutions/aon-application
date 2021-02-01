import { AonElement } from "../../../../components/AonElement.js";
import { setValueName, serializeForm } from "../../../../services/utils.js";
import { deleteLocation, saveLocation } from "../../../../services/service.js";
import "../../../../components/aon-card.js";
import "../../../../components/aon-input.js";
import "../../../../components/aon-number.js";
import "../../../../components/aon-date.js";
import "../../../../components/aon-suggestion.js";
import "../../../../components/aon-select.js";
import "../../../../components/aon-switch.js";
import "../../../../components/aon-icon-button.js";
import "./aon-location-list.js";

export class AonLocationAdd extends AonElement {
  TOAST;
  NAME;
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
    this.NAME = "Ubicación";
    this.id = this.id || "aonLocationAdd";
    this.TOOLBAR = this.id + "Toolbar";
    this.aonSigninEl = this.getElement("aonSignin");
    this.aonSigninToolbar = this.getElement(`${this.aonSigninEl.id}Toolbar`);
    this.aonSigninToolbar.setAttribute("option", "Registrar" + this.NAME);
    this.TOAST = this.getElement(`${this.aonSigninEl.id}Toast`);
  }

  connectedCallback() {
    this.paintView();
    this.build();
    this.eventListener();
  }

  attributeChangedCallback(name, oldValue, newValue) {
    if ("data" == name && newValue) {
      this.setValues();
    }
  }

  paintView() {
    const toolbarMobile = !this.isMobile()
      ? `<aon-toolbar id="${this.TOOLBAR}" type="secondary" title="${this.NAME}"> </aon-toolbar>`
      : "";

    let initHtml = `
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
                    <aon-card id="${this.id}Card" title="Datos de la ${this.NAME}"></aon-card>
                </div>
                <div class="aonCol-sm-12">
                  <aon-card id="${this.id}CardMap" title="Mapa" hidden></aon-card>
                 </div>
            </div>
        </form>`;

    this.innerHTML = initHtml + form;

    this.aonSigninEl.removeToolbarOptions();

    let aonCard = this.getElement(`${this.id}Card`);
    aonCard.setContentHTML(`
            <div class="aonCol-xs-10">
              <aon-input name="description" id="description" description="Nombre" type="text"></aon-input>
            </div>
            <div class="aonCol-xs-2">
              <aon-number name="radio" id="radio" description="Radio" type="text"></aon-number>
            </div>
            <div class="aonCol-xs-6">
              <aon-input name="latitude" id="latitude" description="Latitud" type="text"></aon-input>
            </div>
            <div class="aonCol-xs-6">
              <aon-input name="longitude" id="longitude" description="Longitud" type="text"></aon-input>
            </div>
            <aon-input name="id" id="id" type="text" visible="false"></aon-input>
        `);

    if (!this.isMobile()) this.buildToolbar();
  }

  build() {}

  eventListener() {
    const form = this.getElement(`${this.id}Form`);
    form.addEventListener("change", (e) => {
      this.formSubmit();
    });
  }

  buildToolbar() {
    const toolbar = this.getElement(this.TOOLBAR);
    toolbar.removeButtons();

    toolbar.addButton2(
      {
        id: "Delete",
        name: "Eliminar",
        icon: "delete",
      },
      () => this.removeData()
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

  paintViewMap(data){
    let aonMap = this.getElement(`${this.id}CardMap`);
    if(data && data.latitude && data.longitude){
      aonMap.setContentHTML(`<iframe src="https://maps.google.es/maps?q=${data.latitude},${data.longitude}&z=16&output=embed" id="iframeMap" frameborder="0" style="border:0;height: 400px;width: 100%;" allowfullscreen></iframe>`);
      aonMap.hidden = false;
    } else {
      aonMap.hidden = true;
    }
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

  setValues() {
    const data = this.data;
    if (data) {
      this.aonSigninToolbar.setAttribute("option", "Modificar " + this.NAME);
      const obj = { ...data };
      for (const property in obj) {
        setValueName(property, obj[property]);
        if (property.includes("latitude") || property.includes("longitude")) {
          this.getElement(property).setAttribute("disabled", "disabled");
        }
      }
      this.paintViewMap(data);
    }
  }

  formSubmit() {
    this.save();
  }

  async save() {
    const data = this.getFormValues();
    let count = Object.keys(data).length;
    if (count > 3) {
      try {
        const { id } = await saveLocation({
          ...data,
          coordinates: `${data.latitude},${data.longitude}`,
        });
        if (id) {
          setValueName("id", id);
          this.paintViewMap(data);
        }
      } catch (error) {}
    }
  }

  async removeData() {
    const data = this.getFormValues();
    if (confirm(`Estas seguro de eliminar la ${this.NAME}?`)) {
      this.aonSigninEl.startLoader();
      try {
        await deleteLocation(data);
        this.TOAST.start({ message: `Datos eliminados!` });
        this.back();
      } catch (error) {
        this.TOAST.start({ message: error, type: "error" });
      }
      this.aonSigninEl.stopLoader();
    }
  }

  back() {
    this.aonSigninEl.setContentHTML(`<aon-location-list></aon-location-list>`);
  }
}

window.customElements.define("aon-location-add", AonLocationAdd);
