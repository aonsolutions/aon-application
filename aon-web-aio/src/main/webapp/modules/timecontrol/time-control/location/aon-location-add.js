import { AonElement } from "../../../../components/AonElement.js";
import { setValueName, serializeForm, waitEl } from "../../../../services/utils.js";
import { deleteLocation, saveLocation } from "../../../../services/service.js";
import { ToolbarType } from "../../../../models/enums.js";
import { SIGNIN_VIEWS } from "../../signinEnums.js";
import * as ACTION from '../../../actions.js';
import { CONSTANT, CSS, EVENT, MSG, TAG } from "../../../../environments/environments.js";
import { AonMap } from "../../../../components/aon-map.js";
import { CreateComponent } from "../../../../components/CreateComponent.js";


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
    if (CONSTANT.DATA == name && newValue) 
      this.setFormValues();
    else if (CONSTANT.ADD == name && newValue) 
      this.paintViewMap(undefined);
  }


  build() {
    this.applicationEl.removeToolbarOptions();
    this.paintView();
    this.buildToolbar();
  }

  paintView() {
    CreateComponent.createAonToolbar({ id:this.TOOLBAR, type:ToolbarType.SECONDARY}, this);

    const form = CreateComponent.createForm(this.id+"Form");
    this.appendChild(form);

    let div = this.createElement(TAG.DIV);
    div.id = this.id+"Div";

    const className = this.isMobile() ? CSS.AON_MOBILE_SUB_CONTENT : CSS.AON_SUB_CONTENT;
    div.className = className;
    form.appendChild(div);

    let div2 = this.createElement(TAG.DIV);
    div2.classList.add(CSS.AON_COL_XS_12);
    div.appendChild(div2);
    
    const aonCard = CreateComponent.createAonCard({id: this.id+"Card", title:"Datos de la " +this.NAME, flex:"true"}, div2).getContent();

    let divG = this.createElement(TAG.DIV);
    divG.classList.add(CSS.AON_COL_SM_5, CSS.AON_COL_XS_10);
    aonCard.appendChild(divG);

    CreateComponent.createAonInput({
      attributes:{
        name:"description",
        id:"description" ,
        description:MSG.NAME,
        type:"text"
      }
    }, divG);

    divG = this.createElement(TAG.DIV);
    divG.classList.add(CSS.AON_COL_SM_1, CSS.AON_COL_XS_2);
    aonCard.appendChild(divG);

    CreateComponent.createAonInput({
      attributes:{
        name:"radio",
        id:"radio" ,
        description:MSG.RADIO,
        type:"number"
      }
    }, divG);

    divG = this.createElement(TAG.DIV);
    divG.classList.add(CSS.AON_COL_SM_6, CSS.AON_COL_XS_12);
    aonCard.appendChild(divG);

     CreateComponent.createAonInput({
      attributes:{
        name:"direction",
        id:"direction" ,
        type:"text",
        description:"Dirección",
        disabled:true
      }
    }, divG);
    
  
    CreateComponent.createAonInput({
      attributes:{
        name:"latitude",
        id:"latitude" ,
        type:"text",
        visible:"false",
      }
    }, aonCard);

    CreateComponent.createAonInput({
      attributes:{
        name:"longitude",
        id:"longitude" ,
        type:"text",
        visible:"false",
      }
    }, aonCard);

    CreateComponent.createAonInput({
      attributes:{
        name:"id",
        id:"id" ,
        type:"text",
        visible:"false",
      }
    }, aonCard);

    const divMap = this.createElement(TAG.DIV);
    divMap.classList.add(CSS.AON_COL_XS_12);
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

    let position = null;
    if (data && data.latitude && data.longitude) 
      position = { lat: data.latitude, lng: data.longitude}

    let aonMap = new AonMap();
    aonMap.POSITION = position;
    aonMap.geocoder = true;
    aonMap.addEventListener(EVENT.COORDINATES, ({detail})=>{
      this.setCoordinates(detail);
    });

    aonMap.addEventListener(EVENT.GEOCODE, ({detail})=>{
      if(detail && detail.name)
        this.getElement("direction").value = detail.name
    });

    const cardContentMap = CreateComponent.createAonCard({id: this.id+"Map", title:"Mapa", flex:"true"}, divMap).getContent();
    cardContentMap.appendChild(aonMap);
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

  deleteManual(id){
    deleteLocation({id});
  }

  setCoordinates(data) {
    let lat = data.latitude || data.lat;
    let lng = data.longitude || data.lng;
    if (lat && lng) {
      setValueName("latitude", lat);
      setValueName("longitude", lng);
    }
  }

  back() {
    this.applicationEl.getParent().showView(SIGNIN_VIEWS.AON_LOCATION_LIST);
  }
}

window.customElements.define("aon-location-add", AonLocationAdd);
