import { AonElement } from "../../../../components/AonElement.js";
import { setValueName, serializeForm } from "../../../../services/utils.js";
import { deleteLocation, saveLocation } from "../../../../services/service.js";
import { getPosition } from "../../../../services/maps.js";
import "../../../../components/aon-card.js";
import "../../../../components/aon-input.js";
import "../../../../components/aon-number.js";
import "../../../../components/aon-date.js";
import "../../../../components/aon-suggestion.js";
import "../../../../components/aon-select.js";
import "../../../../components/aon-switch.js";
import "../../../../components/aon-icon-button.js";
import "./aon-location-list.js";
import { API_KEY_MAP } from "../../../../environments/constants.js";

export class AonLocationAdd extends AonElement {
  TOAST;
  NAME;
  static get observedAttributes() {
    return ["data", "add"];
  }

  get id() {
    return this.getAttribute("id");
  }

  set id(id) {
    this.setAttribute("id", id);
  }

  get add() {
    return this.getAttribute("add") == "true";
  }

  set add(add) {
    this.setAttribute("add", add);
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
    this.aonSigninToolbar.setAttribute("option", `Registrar ${this.NAME}`);
    this.TOAST = this.getElement(`${this.aonSigninEl.id}Toast`);
  }

  connectedCallback() {
    this.paintView();
    this.build();
    this.eventListener();
  }

  disconnectedCallback() {}

  attributeChangedCallback(name, oldValue, newValue) {
    if ("data" == name && newValue) {
      this.setFormValues();
    }
    if ("add" == name && newValue) {
      this.paintViewMap(undefined);
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
              .gm-inset{
                display: none !important;
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
            <div class="aonCol-xs-12">
             <div id="${this.id}Map" title="Mapa"></div>
            </div>
            <aon-input name="latitude" id="latitude" type="text" visible="false"></aon-input>
            <aon-input name="longitude" id="longitude" type="text" visible="false"></aon-input>
            <aon-input name="id" id="id" type="text" visible="false"></aon-input>
        `);
  }

  build() {
    if (this.isMobile()) this.buildToolbarMobile();
    else this.buildToolbarDesk();
  }

  eventListener() {}

  buildToolbarDesk() {
    const toolbar = this.getElement(this.TOOLBAR);
    if (toolbar) {
      toolbar.removeButtons();
      toolbar.addButton2(
        {
          id: "Save",
          name: "Guardar",
          icon: "save",
        },
        () => this.formSubmit()
      );

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
  }

  buildToolbarMobile() {
    this.aonSigninEl.removeToolbarOptions();
    this.aonSigninEl.addToolbarOption("Save", "save", () => this.formSubmit());
  }

  paintViewMap(data) {
    let aonMap = this.getElement(`${this.id}Map`);
    aonMap.innerHTML = "";
    const zoom = 16;
    let iframeId = this.id + "Iframe";
    let iframe = this.createElement("iframe");
    iframe.id = iframeId;
    iframe.frameborder = 0;
    // max-width:640px;
    iframe.style = "border:0;height: 400px;width: 100%;";
    if (data && data.latitude && data.longitude) {
      iframe.src = `https://maps.google.es/maps?q=${data.latitude},${data.longitude}&z=${zoom}&output=embed&hl=es`;
    } else {
      iframe = this.iframeOnload(iframe, zoom);
    }
    aonMap.appendChild(iframe);
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

  iframeOnload(iframe, zoom) {
    iframe.onload = async () => {
      const setCoordinates = (ev) => this.setCoordinates(ev);
      let doc = iframe.contentDocument;
      let pos = await getPosition()
        .then(({ latitude, longitude }) => ({ latitude, longitude }))
        .catch((e) => null);
      if (pos) {
        iframe.contentWindow.showNewMap = function () {
          let mapContainer = doc.createElement("div");
          mapContainer.setAttribute("style", "width: 100%; height:100%;");
          doc.body.appendChild(mapContainer);
          const mapOptions = {
            center: new this.google.maps.LatLng(pos.latitude, pos.longitude),
            zoom,
            mapTypeId: this.google.maps.MapTypeId.ROADMAP,
          };
          setCoordinates({ latitude: pos.latitude, longitude: pos.longitude }); //setFormValues default lat lng
          const map = new this.google.maps.Map(mapContainer, mapOptions);
          const position = new this.google.maps.LatLng(
            pos.latitude,
            pos.longitude
          );
          // add marker map
          const marker = new this.google.maps.Marker({
            position,
            map,
            title: "Ubicación del sitio de trabajo!",
            draggable: true,
          });

          // ev drag
          this.google.maps.event.addListener(marker, "dragend", (ev) =>
            setCoordinates({
              latitude: ev.latLng.lat(),
              longitude: ev.latLng.lng(),
            })
          );
        };

        let script = document.createElement("script");
        script.type = "text/javascript";
        script.src = `https://maps.googleapis.com/maps/api/js?key=${API_KEY_MAP}&hl=es&callback=showNewMap`;
        iframe.contentDocument
          .getElementsByTagName("head")[0]
          .appendChild(script);
      } //pos
    }; //onload
    return iframe;
  }

  setCoordinates(data) {
    if (data && data.latitude && data.longitude) {
      setValueName("latitude", data.latitude);
      setValueName("longitude", data.longitude);
    }
  }

  setFormValues() {
    const data = this.data;
    if (data) {
      this.aonSigninToolbar.setAttribute("option", "Modificar " + this.NAME);
      const obj = { ...data };
      for (const property in obj) setValueName(property, obj[property]);
      this.paintViewMap(data);
    }
  }

  formSubmit() {
    this.save();
  }

  async save() {
    const data = this.getFormValues();
    const count = Object.keys(data).length;
    if (count > 3) {
      try {
        const { id } = await saveLocation({
          ...data,
          coordinates: `${data.latitude},${data.longitude}`,
        });
        this.TOAST.start({ message: `Datos guardados!`, type: "success" });
        this.back();
        if (id) {
          setValueName("id", id);
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
