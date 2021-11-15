import {AonElement} from '../../components/AonElement.js';
import { EVENT, MSG, TAG, CSS, COLORS } from "../../environments/environments.js";
import * as ACTION from '../actions.js';
import { AonSelect } from '../../components/aon-select.js';
import { AonSwitch } from '../../components/aon-switch.js';
import { setAttributes, setStyles } from '../../services/utilsComponents.js';
import { AonCard } from '../../components/aon-card.js';
import { AonToolbar } from '../../components/aon-toolbar.js';
import { ToolbarType } from '../../models/enums.js';
import { getWorkgroups } from '../../services/workgroupService.js';
import { getTastHoldersWorkGroup } from '../../services/taskHolderService.js';
import { serializeForm } from '../../services/utils.js';
import { getTaskAppParams, saveTaskAppParams } from '../../services/taskService.js';
import * as LS from '../../services/localStorageService.js';
import { APP_PARAMS_REQUEST } from './MessengerEnums.js';


export class AonMessengerConfig extends AonElement {
  FORM;
  CARD;
  CARD_TWO;
  TASK_HOLDERS;
  WORKGROUPS;
  get id() {
    return this.getAttribute('id');
  }

  set id(id) {
    this.setAttribute('id', id);
  }

  constructor () {
    super();
  }

  connectedCallback () {
    this.initialize();
    this.build();
  }

  initialize() {
    this.id = this.id || 'aonMessengerConfig';
    this.CARD = this.id+"card";
    this.CARD_TWO = this.id+"cardTwo";
    this.FORM = this.id+"form";
    this.TASK_HOLDERS =[];
    this.WORKGROUPS =[];
  }

  build(){
      this.buildToolbar();

      let form = setStyles(this.createElement(TAG.FORM),{display: "flex", flexWrap:"wrap" });
      form.id = this.FORM;
      
      this.appendChild(form);

      let divTwo = setStyles(this.createElement(TAG.DIV),{ flex:1 });
      form.appendChild(divTwo);

      let card = setAttributes(new AonCard(), { title:"Internas", id:this.CARD });
      divTwo.appendChild(card);

      let divThree = setStyles(this.createElement(TAG.DIV),{ flex:1 });
      form.appendChild(divThree);

      let cardE = setAttributes(new AonCard(), { title:"Generales",id:this.CARD_TWO });
      divThree.appendChild(cardE);

      this.getAppParams().then(params=>{
        this.buildForm(params);
      });
 
  }

  buildToolbar() {
    let toolbar = new AonToolbar();
    toolbar.id = "we23";
    toolbar.type = ToolbarType.SECONDARY;
    toolbar.title = "Parametros por defecto";
    this.appendChild(toolbar);
    toolbar.addButton2(ACTION.SAVE, () => this.save());
}

  buildForm(params){
    let cardFirst = this.getElement(this.CARD).getContent();

    let firstCard = this.createElement(TAG.DIV);
    cardFirst.appendChild(firstCard);
    this.buildFirstCard(firstCard, params);

    let cardSecond = this.getElement(this.CARD_TWO).getContent();
    let secondCard = this.createElement(TAG.DIV);
    cardSecond.appendChild(secondCard);
    this.buildSecondCard(secondCard, params);

  }

  async buildFirstCard(firstCard, params){
    
    await this.setWorkGroup();
    await this.setTaskHolder();

    const {APP_REQUESTS_INT_WORKGROUP, APP_REQUESTS_INT_TASK_HOLDER, APP_REQUESTS_EXT_WORKGROUP, APP_REQUESTS_EXT_TASK_HOLDER} = APP_PARAMS_REQUEST;

    let workgroupI = setAttributes(new AonSelect(),{ title: MSG.WORKGROUP, id:Math.random().toString(36).substring(7), name:APP_REQUESTS_INT_WORKGROUP, default:true});
    firstCard.appendChild(workgroupI);

    let taskHolderI = setAttributes(new AonSelect(),{ title: "Títular", id:Math.random().toString(36).substring(7), name:APP_REQUESTS_INT_TASK_HOLDER, default:true});
    firstCard.appendChild(taskHolderI);

    let div = setStyles(document.createElement(TAG.DIV),{ marginBottom: 0, marginTop:"25px" });
    div.className = CSS.AON_CARD_TITLE;
    div.innerText = "Externas";
    firstCard.appendChild(div);

    this.fillWorkGroup(workgroupI, params[APP_REQUESTS_INT_WORKGROUP]);
    this.fillTaskHolder(taskHolderI, params[APP_REQUESTS_INT_WORKGROUP], params[APP_REQUESTS_INT_TASK_HOLDER]);


    let workgroupE = setAttributes(new AonSelect(),{ title: MSG.WORKGROUP, id:Math.random().toString(36).substring(7), name:APP_REQUESTS_EXT_WORKGROUP, default:true});
    firstCard.appendChild(workgroupE);

    let taskHolderE = setAttributes(new AonSelect(),{ title: "Títular", id:Math.random().toString(36).substring(7), name:APP_REQUESTS_EXT_TASK_HOLDER, default:true});
    firstCard.appendChild(taskHolderE);


    this.fillWorkGroup(workgroupE, params[APP_REQUESTS_EXT_WORKGROUP]);
    this.fillTaskHolder(taskHolderE, params[APP_REQUESTS_EXT_WORKGROUP], params[APP_REQUESTS_EXT_TASK_HOLDER]);


    workgroupI.addEventListener(EVENT.CHANGE, ({detail})=>{
      if(detail && detail.id)
          this.fillTaskHolder(taskHolderI, detail.id);
    });

    workgroupE.addEventListener(EVENT.CHANGE, ({detail})=>{
        if(detail && detail.id)
            this.fillTaskHolder(taskHolderE, detail.id);
    });
  }

  buildSecondCard(secondCard, params){

    const {APP_REQUESTS_NOTI_OPENED, APP_REQUESTS_NOTI_CLOSED, APP_REQUESTS_NOTI_COMMENT, APP_REQUESTS_NOTI_ASSIGN, APP_REQUESTS_EMAIL_RATING} = APP_PARAMS_REQUEST;

    let divTwo = setStyles(this.createElement(TAG.DIV),{margin:"0 0 7"});
    secondCard.appendChild(divTwo);

    let rating = setAttributes(new AonSwitch(),{id:"rating", name:APP_REQUESTS_EMAIL_RATING, title: "Enviar calificación al cerrar", checked:params[APP_REQUESTS_EMAIL_RATING]});
    rating.style.margin ="10 0 0";
    divTwo.appendChild(rating);

    let text = setStyles(this.createElement(TAG.DIV),{ fontWeight:500, color:CSS.variable(COLORS.AON_GRAY), marginBottom:4});
    text.innerText = "Enviar Notificación al:";
    secondCard.appendChild(text);
    
    let div = setStyles(this.createElement(TAG.DIV),{display:"flex", flexWrap:"wrap", columnGap: "10px"});
    secondCard.appendChild(div);

    let opened = setAttributes(new AonSwitch(),{id:"opened", name:APP_REQUESTS_NOTI_OPENED, title: "Abrir", checked:params[APP_REQUESTS_NOTI_OPENED]});
    div.appendChild(opened);

    let closed = setAttributes(new AonSwitch(),{id:"closed", name:APP_REQUESTS_NOTI_CLOSED, title: "Cerrar", checked:params[APP_REQUESTS_NOTI_CLOSED]});
    div.appendChild(closed);

    let comment = setAttributes(new AonSwitch(),{id:"comment", name:APP_REQUESTS_NOTI_COMMENT, title: "Comentar", checked:params[APP_REQUESTS_NOTI_COMMENT]});
    div.appendChild(comment);

    let assign = setAttributes(new AonSwitch(),{id:"assign", name:APP_REQUESTS_NOTI_ASSIGN, title: "Asignar", checked:params[APP_REQUESTS_NOTI_ASSIGN]});
    div.appendChild(assign);
  }

  fillWorkGroup(workgroup, workgroupId = undefined){
    if(workgroupId) workgroupId = parseInt(workgroupId);
    let wgs = this.getWorkgroups();

    workgroup.setOptions( wgs.map(t => ({...t, value: t.id, description: t.description, name:t.description})))
    if(workgroupId) workgroup.value = workgroupId;
  }

  fillTaskHolder(taskHolder, workgroup = undefined, task_holder= undefined){
    // if(workgroup) workgroup = parseInt(workgroup);
    if(task_holder) task_holder = parseInt(task_holder);
    // taskHolder.clear();
    let ths = this.getTaskHolders();
    taskHolder.setOptions( ths.map( th=> ({...th, value: th.id}) ) );
    if(task_holder) taskHolder.value = task_holder;
  }

  async save(){
    this.getApplication().startLoading();
    try {
      let domainId  = LS.getDomainId();
      const json = serializeForm(this.getElement(this.FORM));
      let arr = [];
      for (let name in APP_PARAMS_REQUEST) 
        arr.push({name, value: json[name], domain: domainId});
      
      await saveTaskAppParams({appParams:arr});
      this.showMessage();
    } catch (error) {
      this.showError(error);
    }

    this.getApplication().stopLoading();
  }

  async setWorkGroup(){
    this.WORKGROUPS = await getWorkgroups({status:"ACTIVE"}).catch(e=>{
      console.log("error",e);
      return [];
    });
  }

  async setTaskHolder(){
    this.TASK_HOLDERS = await getTastHoldersWorkGroup({active:1}).catch(e=>{
      console.log("error",e);
      return [];
    });
  }


  getWorkgroups(){
    return this.WORKGROUPS;
  }

  getTaskHolders(){
    return this.TASK_HOLDERS;
  }

  async getAppParams(){
    let params = [];
    let newResp=[];
    for (let name in APP_PARAMS_REQUEST) 
      params.push(name);

    let resp = await getTaskAppParams({params});
    resp.map(param => {
      newResp[param.name] = param.value;
    });
    return newResp;
  }
}

window.customElements.define('aon-messenger-config',  AonMessengerConfig);
