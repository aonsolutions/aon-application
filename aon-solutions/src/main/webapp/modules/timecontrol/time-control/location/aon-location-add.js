import { AonElement } from "../../../../components/AonElement.js";
import { setValueName, serializeForm, waitEl } from "../../../../services/utils.js";
import { deleteLocation, saveLocation } from "../../../../services/service.js";
import { getPosition } from "../../../../services/maps.js";
import { ToolbarType } from "../../../../models/enums.js";
import { SIGNIN_VIEWS } from "../../signinEnums.js";
import * as ACTION from '../../../actions.js';
import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from "../../../../environments/environments.js";
import { createCard, createForm, createInput, createToolbar } from "../../../notification/createComponent.js";


export class AonLocationAdd extends AonElement {
  NAME;
  static get observedAttributes() {
    return [CONSTANT.DATA, CONSTANT.ADD];
  }

  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
  }

  get add() {
    return this.getAttribute(CONSTANT.ADD) == CONSTANT.TRUE;
  }

  set add(add) {
    this.setAttribute(CONSTANT.ADD, add);
  }

  get data() {
    return JSON.parse(this.getAttribute(CONSTANT.DATA));
  }

  set data(value) {
    this.setAttribute(CONSTANT.DATA, JSON.stringify(value));
  }


  constructor() {
    super();
    this.id = this.id || SIGNIN_VIEWS.AON_LOCATION_ADD;
    this.NAME =  MSG.LOCATION;
    this.TOOLBAR = this.id + "Toolbar";
    this.applicationEl = this.getApplication();
  }

  connectedCallback() {
    this.build();
  }

  attributeChangedCallback(name, oldValue, newValue) {
    if (CONSTANT.DATA == name && newValue) {
      this.setFormValues();
    }
    if (CONSTANT.ADD == name && newValue) {
      this.paintViewMap(undefined);
    }
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
    div2.classList.add(CSS.AON_COL_SM_6, CSS.AON_COL_XS_12);
    div.appendChild(div2);
    
    const aonCard = createCard({id: this.id+"Card", title:"Datos de la " +this.NAME, flex:"true"}, div2).getContent();

    let divG = this.createElement(TAG.DIV);
    divG.className = CSS.AON_COL_XS_10;
    aonCard.appendChild(divG);
    createInput({
      attributes:{
        name:"description",
        id:"description" ,
        description:MSG.NAME,
        type:"text"
      }
    }, divG);

    divG = this.createElement(TAG.DIV);
    divG.className = CSS.AON_COL_XS_2;
    aonCard.appendChild(divG);
    createInput({
      attributes:{
        name:"radio",
        id:"radio" ,
        description:MSG.RADIO,
        type:"number"
      }
    }, divG);


    createInput({
      attributes:{
        name:"latitude",
        id:"latitude" ,
        type:"text",
        visible:"false",
      }
    }, aonCard);

    createInput({
      attributes:{
        name:"longitude",
        id:"longitude" ,
        type:"text",
        visible:"false",
      }
    }, aonCard);

    createInput({
      attributes:{
        name:"id",
        id:"id" ,
        type:"text",
        visible:"false",
      }
    }, aonCard);

    const divMap = this.createElement(TAG.DIV);
    divMap.classList.add(CSS.AON_COL_SM_6, CSS.AON_COL_XS_12);
    divMap.id = "divMap";
    div.appendChild(divMap);
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

  async paintViewMap(data) {
    let divMap = await waitEl(`#divMap`);
    divMap.innerHTML = "";

    const aonMap = createCard({id: this.id+"Map", title:"Mapa", flex:"true"}, divMap).getContent();
    
    const zoom = 16;
    let iframeId = this.id + "Iframe";
    let iframe = this.createElement("iframe");
    iframe.id = iframeId;
    iframe.frameborder = 0;
    iframe.style.border = 0;
    iframe.style.height = "400px";
    iframe.style.width = "100%";
    let position = null;
    if (data && data.latitude && data.longitude) 
      position = { ...data}

    iframe = this.iframeOnload(iframe, zoom, position);
    
    aonMap.appendChild(iframe);
  }

  iframeOnload(iframe, zoom, position = null) {
    iframe.onload = async () => {

      const doc = iframe.contentDocument;
      const wd = iframe.contentWindow;

      await Promise.all([
        this.loadLink("https://unpkg.com/leaflet@1.7.1/dist/leaflet.css", doc),
        this.loadLink("https://unpkg.com/leaflet-control-geocoder/dist/Control.Geocoder.css", doc),
        this.loadScript("https://unpkg.com/leaflet@1.7.1/dist/leaflet.js", doc),
        this.loadScript("https://unpkg.com/leaflet-control-geocoder/dist/Control.Geocoder.js", doc)
      ]);

      if(!position)
        position = await getPosition().then(({ latitude, longitude }) => ({ latitude, longitude })).catch((e) => null);

      if (position) 
        this.initMap(doc, wd, position, zoom);
    }; //onload
    return iframe;
  }

  getFormValues() {
    const form = this.getElement(`${this.id}Form`);
    return serializeForm(form);
  }

  async setFormValues() {
    await waitEl('#latitude');
    const data = this.data;
    if (data) {
      const obj = { ...data };
      for (const property in obj)
        setValueName(property, obj[property]);
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
        this.showToast({ message: MSG.SAVED_DATA, type: CONSTANT.SUCCESS });
        if (id) { setValueName("id", id); }
      } catch (error) {
        this.showToast(error);
      }
      this.applicationEl.stopLoading();
    }
  }

  async delete() {
    this.applicationEl.confirmDialog(MSG.DELETE, `${MSG.DELETE_CONFIRM} ${this.NAME}?`, async()=>{
      this.applicationEl.startLoading();
      try {
        const data = this.getFormValues();
        await deleteLocation(data);
        this.showToast({ message: MSG.DELETED_DATA });
        this.back();
      } catch (error) {
        this.showToast(error);
      }
      this.applicationEl.stopLoading();
    });
  }

  initMap(doc, wd, pos, zoom){
      let geocoder, map, marker;
      const position = { lat: parseFloat(pos.latitude), lng: parseFloat(pos.longitude) };   
      let mapContainer = doc.createElement("div");
      mapContainer.style.width = "100%"; 
      mapContainer.style.height = "100%"; 
      mapContainer.id = "map";

      doc.body.appendChild(mapContainer);

      map = wd.L.map(mapContainer, {attributionControl: false}).setView(position, zoom);   

      new wd.L.tileLayer('http://{s}.google.com/vt/lyrs=m&x={x}&y={y}&z={z}',{ subdomains:['mt0','mt1','mt2','mt3']}).addTo(map);

      geocoder = wd.L.Control.Geocoder.nominatim({ geocodingQueryParams: {countrycodes: 'es'} });

      marker = this.addMarker(map, geocoder, doc, wd, position);

      this.setCoordinates(position);

      this.geocodeReverse(map, geocoder, doc, position);

      wd.L.Control.geocoder({
          placeholder: "Dirección",
          position:"topright",
          errorMessage: "Dirección no encontrada. Arrastre manualmente el marcador <br> a la ubicación (puede acercar o alejar la imagen)",
          defaultMarkGeocode: false,
          collapsed: false,
          geocoder: geocoder,
      }) .on('markgeocode', (result)=> {
          const geocode = result.geocode;

          if (marker) map.removeLayer(marker);

          const latlng = geocode.center;

          this.setCoordinates(latlng);

          marker = this.addMarker(map, geocoder, doc, wd, latlng).bindPopup(geocode.name).openPopup();

          map.fitBounds(geocode.bbox);
          map.invalidateSize();
      })
      .addTo(map);
  }

  setCoordinates(data) {
    let lat = data.latitude || data.lat;
    let lng = data.longitude || data.lng;
    if (lat && lng) {
      setValueName("latitude", lat);
      setValueName("longitude", lng);
    }
  }

  async geocodeReverse(map, geocoder, doc, {lat, lng}){
    this.geocodeLoading(doc, true);
    let data = await new Promise((resolve, reject) => {
      geocoder.reverse({lat, lng}, map.options.crs.scale(map.getZoom()), (results) => {
        try {
          let r = results[0];
          if (r && r.name) 
            resolve(r.name);
        } catch (error) { }
        reject(null);
     });
    });

    if(data)
      doc.querySelector(".leaflet-control-geocoder-form > input").value = data;
    
    this.geocodeLoading(doc, false);

  }

  geocodeLoading(doc, load = false){
    try {
      const classLoad = "leaflet-control-geocoder-throbber";
      let buttonParent = doc.querySelector("button.leaflet-control-geocoder-icon").parentNode;
      if(buttonParent)
        load ?  buttonParent.classList.add(classLoad) : buttonParent.classList.remove(classLoad);
    } catch (error) {}
  }

  addMarker(map, geocoder, doc, wd, {lat, lng}){
    let marker = wd.L.marker([lat, lng], { draggable: true }).addTo(map);
    marker.on('dragend',  () =>{
      this.setCoordinates(marker.getLatLng());
      this.geocodeReverse(map, geocoder, doc, marker.getLatLng());
    });
    return marker;
  } 

  loadLink(url, doc){ 
    return new Promise((resolve, reject) => {
      const link = doc.createElement('link');
      doc.head.appendChild(link);
      link.onload = resolve;
      link.onerror = reject;
      link.href = url;
      link.rel = "stylesheet";
      link.type = "text/css";
  });
}

  loadScript(url, doc) {
    return new Promise((resolve, reject) => {
      let script = doc.querySelector(`script[src="${url}"]`);
      if(!script){
          script = doc.createElement('script');
          doc.head.appendChild(script);
          script.onload = resolve;
          script.onerror = reject;
          script.src = url;
      } else resolve(true);
    });
  }

  back() {
    this.applicationEl.getParent().showView(SIGNIN_VIEWS.AON_LOCATION_LIST);
  }
}

window.customElements.define("aon-location-add", AonLocationAdd);
