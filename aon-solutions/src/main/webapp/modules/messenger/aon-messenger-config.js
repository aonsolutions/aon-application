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
import { saveApplicationParameter, getApplicationParametersIsSig } from '../../services/applicationParameterService.js';
import * as LS from '../../services/localStorageService.js';
import { APP_PARAMS_REQUEST } from './MessengerEnums.js';
import { getDomainUserRoles } from '../../services/companyService.js';
import { DomainUserRoles } from '../../models/DomainUserRoles.js';


export class AonMessengerConfig extends AonElement {
  FORM;
  CARD;
  CARD_TWO;
  TASK_HOLDERS;
  WORKGROUPS;
  dur;
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
    getDomainUserRoles({}).then(r => {
      this.dur = new DomainUserRoles(r);
      this.build();
    }).catch((err) =>this.showError(err));
  
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

      this.getAppParams().then(params=>
        this.buildForm(params)
      );
 
  }

  buildToolbar() {
    let toolbar = new AonToolbar();
    toolbar.id = Math.random().toString(36).substring(7);
    toolbar.type = ToolbarType.SECONDARY;
    toolbar.title = "Parametros Generales";
    this.appendChild(toolbar);
    toolbar.addButton2(ACTION.SAVE, () => this.save());
}

  buildForm(params){
    Promise.all([
      this.setWorkGroup(),
      this.setTaskHolder()
    ]).then(()=>{
      let form = this.getElement(this.FORM);
      let divFirst = this.createElement(TAG.DIV);
      form.appendChild(divFirst);
      this.cardInternal(divFirst, params);

      if(this.dur.getDomain().isOffice()){
        divFirst.style.flex = 1;
        let divSecond = setStyles(this.createElement(TAG.DIV),{ flex:1 });
        form.appendChild(divSecond);
        this.cardExternal(divSecond, params);
      }
    });


  }

  cardInternal(divFirst, params){

    let card = setAttributes(new AonCard(), { title:"Internas", id:this.CARD });
    divFirst.appendChild(card);

    let cardContent = this.getElement(this.CARD).getContent();

    let divContent = this.createElement(TAG.DIV);
    cardContent.appendChild(divContent);
    
    const {APP_REQUESTS_INT_WORKGROUP, APP_REQUESTS_INT_TASK_HOLDER , APP_REQUESTS_INT_OPENED, APP_REQUESTS_INT_CLOSED,  APP_REQUESTS_INT_EMAIL_OPENED, APP_REQUESTS_INT_EMAIL_CLOSED, APP_REQUESTS_INT_COMMENT, APP_REQUESTS_INT_ASSIGN} = APP_PARAMS_REQUEST;

    let workgroup = setAttributes(new AonSelect(),{ title: MSG.WORKGROUP, id:this.getIdRand(), name:APP_REQUESTS_INT_WORKGROUP, default:true});
    divContent.appendChild(workgroup);

    let taskHolder = setAttributes(new AonSelect(),{ title: "Títular", id:this.getIdRand(), name:APP_REQUESTS_INT_TASK_HOLDER, default:true});
    divContent.appendChild(taskHolder);


    this.fillWorkGroup(workgroup, params[APP_REQUESTS_INT_WORKGROUP]);
    this.fillTaskHolder(taskHolder, params[APP_REQUESTS_INT_WORKGROUP], params[APP_REQUESTS_INT_TASK_HOLDER]);

    workgroup.addEventListener(EVENT.CHANGE, ({detail})=>{
        if(detail && detail.id)
            this.fillTaskHolder(taskHolder, detail.id);
    });


    let textC = setStyles(document.createElement(TAG.DIV),{ marginBottom: "8px", marginTop:"19px" });
    textC.className = CSS.AON_CARD_TITLE;
    textC.innerText = "Comunicación";
    divContent.appendChild(textC);

    let text = setStyles(this.createElement(TAG.DIV),{ fontWeight:500, color:CSS.variable(COLORS.AON_GRAY), marginBottom:4});
    text.innerText = "Enviar Notificación al:";
    divContent.appendChild(text);
    
    let div = setStyles(this.createElement(TAG.DIV),{display:"flex", flexWrap:"wrap", columnGap: "10px"});
    divContent.appendChild(div);

    let opened = setAttributes(new AonSwitch(),{id:this.getIdRand(), name:APP_REQUESTS_INT_OPENED, title: "Abrir", checked:params[APP_REQUESTS_INT_OPENED]});
    div.appendChild(opened);

    let closed = setAttributes(new AonSwitch(),{id:this.getIdRand(), name:APP_REQUESTS_INT_CLOSED, title: "Cerrar", checked:params[APP_REQUESTS_INT_CLOSED]});
    div.appendChild(closed);

    let comment = setAttributes(new AonSwitch(),{id:this.getIdRand(), name:APP_REQUESTS_INT_COMMENT, title: "Comentar", checked:params[APP_REQUESTS_INT_COMMENT]});
    div.appendChild(comment);

    let assign = setAttributes(new AonSwitch(),{id:this.getIdRand(), name:APP_REQUESTS_INT_ASSIGN, title: "Asignar", checked:params[APP_REQUESTS_INT_ASSIGN]});
    div.appendChild(assign);


    // --------------------PARAMS SEND EMAIL--------------------------------
    let textEmail = setStyles(this.createElement(TAG.DIV),{ fontWeight:500, color:CSS.variable(COLORS.AON_GRAY), marginBottom:"4px", marginTop:"13px"});
    textEmail.innerText = "Enviar Correo al:";
    divContent.appendChild(textEmail);
    
    let divEmail = setStyles(this.createElement(TAG.DIV),{display:"flex", flexWrap:"wrap", columnGap: "10px"});
    divContent.appendChild(divEmail);

    let openedEmail = setAttributes(new AonSwitch(),{id:this.getIdRand(), name:APP_REQUESTS_INT_EMAIL_OPENED, title: "Abrir", checked:params[APP_REQUESTS_INT_EMAIL_OPENED]});
    divEmail.appendChild(openedEmail);

    let closedEmail = setAttributes(new AonSwitch(),{id:this.getIdRand(), name:APP_REQUESTS_INT_EMAIL_CLOSED, title: "Cerrar", checked:params[APP_REQUESTS_INT_EMAIL_CLOSED]});
    divEmail.appendChild(closedEmail);
  }

  cardExternal(divSecond, params){

    let card = setAttributes(new AonCard(), { title:"Externas",id:this.CARD_TWO });
    divSecond.appendChild(card);

    let cardContent = this.getElement(this.CARD_TWO).getContent();
    let divContent = this.createElement(TAG.DIV);
    cardContent.appendChild(divContent);

    const {APP_REQUESTS_EMAIL_RATING, APP_REQUESTS_EXT_WORKGROUP, APP_REQUESTS_EXT_TASK_HOLDER , APP_REQUESTS_EXT_OPENED, APP_REQUESTS_EXT_CLOSED, APP_REQUESTS_EXT_EMAIL_OPENED, APP_REQUESTS_EXT_EMAIL_CLOSED, APP_REQUESTS_EXT_COMMENT, APP_REQUESTS_EXT_ASSIGN} = APP_PARAMS_REQUEST;

    let workgroup = setAttributes(new AonSelect(),{ title: MSG.WORKGROUP, id:this.getIdRand(), name:APP_REQUESTS_EXT_WORKGROUP, default:true});
    divContent.appendChild(workgroup);

    let taskHolder = setAttributes(new AonSelect(),{ title: "Títular", id:this.getIdRand(), name:APP_REQUESTS_EXT_TASK_HOLDER, default:true});
    divContent.appendChild(taskHolder);


    this.fillWorkGroup(workgroup, params[APP_REQUESTS_EXT_WORKGROUP]);
    this.fillTaskHolder(taskHolder, params[APP_REQUESTS_EXT_WORKGROUP], params[APP_REQUESTS_EXT_TASK_HOLDER]);

    workgroup.addEventListener(EVENT.CHANGE, ({detail})=>{
        if(detail && detail.id){
          this.fillTaskHolder(taskHolder, detail.id);
        }
    });


    let textC = setStyles(document.createElement(TAG.DIV),{ marginBottom: "8px", marginTop:"19px" });
    textC.className = CSS.AON_CARD_TITLE;
    textC.innerText = "Comunicación";
    divContent.appendChild(textC);

    let text = setStyles(this.createElement(TAG.DIV),{ fontWeight:500, color:CSS.variable(COLORS.AON_GRAY), marginBottom:"4px"});
    text.innerText = "Enviar Notificación al:";
    divContent.appendChild(text);
    
    let div = setStyles(this.createElement(TAG.DIV),{display:"flex", flexWrap:"wrap", columnGap: "10px"});
    divContent.appendChild(div);

    let opened = setAttributes(new AonSwitch(),{id:this.getIdRand(), name:APP_REQUESTS_EXT_OPENED, title: "Abrir", checked:params[APP_REQUESTS_EXT_OPENED]});
    div.appendChild(opened);

    let closed = setAttributes(new AonSwitch(),{id:this.getIdRand(), name:APP_REQUESTS_EXT_CLOSED, title: "Cerrar", checked:params[APP_REQUESTS_EXT_CLOSED]});
    div.appendChild(closed);

    let comment = setAttributes(new AonSwitch(),{id:this.getIdRand(), name:APP_REQUESTS_EXT_COMMENT, title: "Comentar", checked:params[APP_REQUESTS_EXT_COMMENT]});
    div.appendChild(comment);

    let assign = setAttributes(new AonSwitch(),{id:this.getIdRand(), name:APP_REQUESTS_EXT_ASSIGN, title: "Asignar", checked:params[APP_REQUESTS_EXT_ASSIGN]});
    div.appendChild(assign);


    // --------------------PARAMS SEND EMAIL--------------------------------
    let textEmail = setStyles(this.createElement(TAG.DIV),{ fontWeight:500, color:CSS.variable(COLORS.AON_GRAY), marginBottom:"4px", marginTop:"13px"});
    textEmail.innerText = "Enviar Correo al:";
    divContent.appendChild(textEmail);
    
    let divEmail = setStyles(this.createElement(TAG.DIV),{display:"flex", flexWrap:"wrap", columnGap: "10px"});
    divContent.appendChild(divEmail);

    let openedEmail = setAttributes(new AonSwitch(),{id:this.getIdRand(), name:APP_REQUESTS_EXT_EMAIL_OPENED, title: "Abrir", checked:params[APP_REQUESTS_EXT_EMAIL_OPENED]});
    divEmail.appendChild(openedEmail);

    let closedEmail = setAttributes(new AonSwitch(),{id:this.getIdRand(), name:APP_REQUESTS_EXT_EMAIL_CLOSED, title: "Cerrar", checked:params[APP_REQUESTS_EXT_EMAIL_CLOSED]});
    divEmail.appendChild(closedEmail);

    let rating = setAttributes(new AonSwitch(),{id:this.getIdRand(), name:APP_REQUESTS_EMAIL_RATING, title: "Cerrar para calificación", checked:params[APP_REQUESTS_EMAIL_RATING]});
    divEmail.appendChild(rating);
  }

  getIdRand(){
    return Math.random().toString(36).substring(7);
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
      
      await saveApplicationParameter({params:arr});
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

    let resp = await getApplicationParametersIsSig({params});
    resp.map(param => {
      newResp[param.name] = param.value;
    });
    return newResp;
  }
}

window.customElements.define('aon-messenger-config',  AonMessengerConfig);
