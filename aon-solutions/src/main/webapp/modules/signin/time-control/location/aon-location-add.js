import { AonElement } from "../../../../components/AonElement.js";
import { setValueName, serializeForm, waitEl } from "../../../../services/utils.js";
import { deleteLocation, saveLocation } from "../../../../services/service.js";
import { getPosition } from "../../../../services/maps.js";
import { URL_MAP } from "../../../../environments/constants.js";
import { ToolbarType } from "../../../../models/enums.js";
import { SIGNIN_VIEWS } from "../../signinEnums.js";
import * as ACTION from '../../../actions.js';
import { CONSTANT, CSS, MSG, TAG } from "../../../../environments/environments.js";
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
    div2.className = CSS.AON_COL_SM_12;
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
        type:"text"
      }
    }, divG);


    divG = this.createElement(TAG.DIV);
    divG.className = CSS.AON_COL_XS_12;
    aonCard.appendChild(divG);
    let divM = this.createElement(TAG.DIV);
    divM.id = this.id+"Map";
    divM.title = "Mapa";
    divG.appendChild(divM);

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
    let aonMap = await waitEl(`#${this.id}Map`);
    aonMap.innerHTML = "";
    const zoom = 16;
    let iframeId = this.id + "Iframe";
    let iframe = this.createElement("iframe");
    iframe.id = iframeId;
    iframe.frameborder = 0;
    iframe.style.border = 0;
    iframe.style.height = "400px";
    iframe.style.width = "100%";
    if (data && data.latitude && data.longitude) {
      iframe.loading = "lazy";
      iframe.src = `${CONSTANT.URL_MAP_EMBED}&q=${data.latitude},${data.longitude}&zoom=${zoom}&language=es`;
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
          mapContainer.style.width = "100%"; 
          mapContainer.style.height = "100%"; 
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
        script.src = URL_MAP;
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

  back() {
    this.applicationEl.getParent().showView(SIGNIN_VIEWS.AON_LOCATION_LIST);
  }
}

window.customElements.define("aon-location-add", AonLocationAdd);
