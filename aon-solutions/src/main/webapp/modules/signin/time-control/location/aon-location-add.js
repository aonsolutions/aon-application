import { AonElement } from "../../../../components/AonElement.js";
import { setValueName, serializeForm, waitEl } from "../../../../services/utils.js";
import { deleteLocation, saveLocation } from "../../../../services/service.js";
import { getPosition } from "../../../../services/maps.js";
import { API_KEY_MAP } from "../../../../environments/constants.js";
import { UserAction } from "../../../user/userEnums.js";
import { ToolbarType } from "../../../../models/enums.js";
import { SIGNIN_VIEWS } from "../../signinEnums.js";
import "../../../../components/aon-card.js";
import "../../../../components/aon-input.js";
import "../../../../components/aon-number.js";
import { AON_MSG_DELETED_DATA, AON_MSG_SAVED_DATA } from "../../../../environments/msg.js";

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
    this.id = this.id || SIGNIN_VIEWS.AON_LOCATION_ADD;
    this.NAME = "Ubicación";
    this.TOOLBAR = this.id + "Toolbar";
    this.applicationEl = this.getApplication();
    this.TOAST = this.applicationEl.getToast();
  }

  connectedCallback() {
    this.build();
  }

  attributeChangedCallback(name, oldValue, newValue) {
    if ("data" == name && newValue) {
      this.setFormValues();
    }
    if ("add" == name && newValue) {
      this.paintViewMap(undefined);
    }
  }


  async build() {
    this.paintView();
    this.buildToolbar();
  }

  paintView() {

    const aonToolbar = `<aon-toolbar id="${this.TOOLBAR}" type="${ToolbarType.SECONDARY}"> </aon-toolbar>`;
    const form = `
        <form id="${this.id}Form" action="#" onsubmit="return false;">
            <div id="${this.id}Div">
                <div class="aonCol-sm-12">
                    <aon-card id="${this.id}Card" title="Datos de la ${this.NAME}" flex="true"></aon-card>
                </div>
            </div>
        </form>`;

    this.innerHTML = aonToolbar + form;

    this.applicationEl.removeToolbarOptions();

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

  buildToolbar(){
    const toolbarEl = this.getElement(this.TOOLBAR);
    toolbarEl.removeButtons();
    if(this.data && this.data.id){
      toolbarEl.addButton2(UserAction.DELETE, () =>this.delete());
      toolbarEl.title = "Edición";
    } else {
      toolbarEl.title = "Registro";
    }
    toolbarEl.addButton2(UserAction.SAVE, () => this.save());
    toolbarEl.addButton2(UserAction.BACK, () => this.back());
  }

  async paintViewMap(data) {
    let aonMap = await waitEl(`#${this.id}Map`);
    aonMap.innerHTML = "";
    const zoom = 16;
    let iframeId = this.id + "Iframe";
    let iframe = this.createElement("iframe");
    iframe.id = iframeId;
    iframe.frameborder = 0;
    iframe.style = "border:0;height: 400px;width: 100%;";
    if (data && data.latitude && data.longitude) {
      iframe.src = `https://maps.google.es/maps?q=${data.latitude},${data.longitude}&z=${zoom}&output=embed&hl=es`;
    } else {
      iframe = this.iframeOnload(iframe, zoom);
    }
    aonMap.appendChild(iframe);
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

  getFormValues() {
    const form = this.getElement(`${this.id}Form`);
    return serializeForm(form);
  }


  setCoordinates(data) {
    if (data && data.latitude && data.longitude) {
      setValueName("latitude", data.latitude);
      setValueName("longitude", data.longitude);
    }
  }

  async setFormValues() {
    await waitEl('#latitude');
    const data = this.data;
    if (data) {
      const obj = { ...data };
      for (const property in obj) setValueName(property, obj[property]);
      this.paintViewMap(data);
    }
  }

  async save() {
    const data = this.getFormValues();
    const count = Object.keys(data).length;
    if (count > 3) {
      this.applicationEl.startLoading();
      try {
        const { id } = await saveLocation({
          ...data,
          coordinates: `${data.latitude},${data.longitude}`,
        });
        this.TOAST.start({ message: AON_MSG_SAVED_DATA, type: "success" });
        if (id) { setValueName("id", id); }
      } catch (error) {
        this.TOAST.start({ message: error, type: "error"});
      }
      this.applicationEl.stopLoading();
    }
  }

  async delete() {
    this.applicationEl.confirmDialog("Eliminar", `Estas seguro de eliminar ${this.NAME}?`, async()=>{
      this.applicationEl.startLoading();
      try {
        const data = this.getFormValues();
        await deleteLocation(data);
        this.TOAST.start({ message: AON_MSG_DELETED_DATA });
        this.back();
      } catch (error) {
        this.TOAST.start({ message: error, type: "error"});
      }
      this.applicationEl.stopLoading();
    });
  }

  back() {
    this.applicationEl.back();
  }
}

window.customElements.define("aon-location-add", AonLocationAdd);
