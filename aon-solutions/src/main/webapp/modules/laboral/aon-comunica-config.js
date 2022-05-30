import {AonElement} from '../../components/AonElement.js';
import { MSG, TAG, CSS, COLORS, CONSTANT, MATERIAL_ICONS, EVENT } from "../../environments/environments.js";
import * as ACTION from '../actions.js';
import { AonSwitch } from '../../components/aon-switch.js';
import { AonTab } from '../../components/aon-tab.js';
import { setAttributes, setStyles } from '../../services/utilsComponents.js';
import { AonCard } from '../../components/aon-card.js';
import { AonToolbar } from '../../components/aon-toolbar.js';
import { ToolbarType } from '../../models/enums.js';
import { serializeForm, sortBy } from '../../services/utils.js';
import { getDomainUserRoles } from '../../services/companyService.js';
import { DomainUserRoles } from '../../models/DomainUserRoles.js';
import * as LS from '../../services/localStorageService.js';
import { getContractType, getQuoteGroup } from '../../services/comunicaService.js';
import { getApplicationParameters, saveApplicationParameter, } from '../../services/applicationParameterService.js';
import { APP_PARAMS_PAYROLL } from './PayrollEnums.js';
import { AonBasicTable } from '../../components/aon-basic-table.js';
import { AonInput } from '../../components/aon-input.js';
import { AonIconButton } from '../../components/aon-icon-button.js';

export class AonComunicaConfig extends AonElement {
  FORM;
  CARD;
  CARD_TWO;
  CARD_THREE;
  CONTRACT_TYPES;
  APP_PARAMS;
  APP_PARAMS_PARENT;
  QUOTE_GROUP;
  PARAMS_CONTRACT;
  PARAMS_QUOTE_GROUP;
  dur;
  EMAIL_MAX_LENGTH;
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
    this.id = this.id || 'AonComunicaConfig';
    this.CARD = this.id+"card";
    this.CARD_TWO = this.id+"cardTwo";
    this.CARD_THREE = this.id+"cardThree";
    this.FORM_CONTRACT_TYPES = this.id+"formContractType";
    this.FORM_QUOTE_GROUP = this.id+"formQuoteGroup";
    this.FORM_EMAILS = this.id+"formEmails";
    this.CONTRACT_TYPES =[];
    this.QUOTE_GROUP =[];
    this.APP_PARAMS =[];
    this.APP_PARAMS_PARENT = [];
    this.EMAIL_MAX_LENGTH = 96;
  }

  build(){
    this.buildToolbar();
    this.buildTabs();
    this.buildCard();

    let promises = [
      this.getAppParams(),
      this.setContractTypes(),
      this.setQuoteGroup()
    ];

    if(this.isChild()){
      promises.push( this.getAppParamsParent());
    }

    Promise.all(promises).then(()=>{
      this.buildContractType();
      this.buildQuoteGroup();
      this.buildEmails();
      if(this.isChild()){
        this.showForm(this.FORM_CONTRACT_TYPES);
      } else {
        this.showForm(this.FORM_EMAILS);
      }
    });
  }

  buildToolbar() {
    let toolbar = new AonToolbar();
    toolbar.id = Math.random().toString(36).substring(7);
    toolbar.type = ToolbarType.SECONDARY;
    toolbar.title = "Parametros Generales";
    this.appendChild(toolbar);
    toolbar.addButton2(ACTION.SAVE, () => this.save());
 
  }

  buildTabs(){
    let tab = new AonTab();
    tab.id = this.getIdRand();
    let options = [];

    if(this.isChild()){
      options.push({ 
        title: MSG.TYPE_CONTRACT, 
        fn: () => {
          this.showForm(this.FORM_CONTRACT_TYPES);
        }
      },
      { 
        title: MSG.QUOTE_GROUP, 
        fn: () => {
          this.showForm(this.FORM_QUOTE_GROUP);
        }
      });
    } 
    
    options.push({ 
      title: MSG.EMAIL, 
      fn: () => {
        this.showForm(this.FORM_EMAILS);
      }
    });

    tab.setOptions(options);

    this.appendChild(tab);
  }

  buildCard(){
    let div = setStyles(this.createElement(TAG.DIV),{display: "flex", flexWrap:"wrap", width:"100%" });
    this.appendChild(div);

    let formOne = setAttributes(this.createElement(TAG.FORM), {id:this.FORM_CONTRACT_TYPES});
    formOne.style.width = "50%";
    div.appendChild(formOne);

    let card = setAttributes(new AonCard(), { id:this.CARD });
    formOne.appendChild(card);


    let formTwo = setAttributes(this.createElement(TAG.FORM), {id:this.FORM_QUOTE_GROUP});
    formTwo.style.width = "50%";
    div.appendChild(formTwo);

    let cardTwo = setAttributes(new AonCard(), { id:this.CARD_TWO });
    formTwo.appendChild(cardTwo);


    let formThree = setAttributes(this.createElement(TAG.FORM), {id:this.FORM_EMAILS});
    formThree.style.width = "50%";
    div.appendChild(formThree);

    let cardThree = setAttributes(new AonCard(), { id:this.CARD_THREE });
    formThree.appendChild(cardThree);
  }

  buildContractType(){
    let aonCard = this.getElement(this.CARD);

    let div = this.createElement(TAG.DIV);
    div.className = CSS.FLEX_COLUMN;
    aonCard.addContent(div);

    let text = setStyles(this.createElement(TAG.DIV),{ fontWeight:500, color:CSS.variable(COLORS.AON_GRAY), marginBottom:"4px"});
    text.innerText = "Contratos activos";
    div.appendChild(text);

    const width = "100%";
    this.getContractTypes().forEach(({name, value})=>{
        let aonSwitch = setAttributes(new AonSwitch(),{id:this.getIdRand(), name:value, title: `${value} - ${name}`, checked:this.isCheckedContract(value)});
        aonSwitch.style.margin = "7px 0";
        div.appendChild(aonSwitch);
        aonSwitch.setLabelWidth(width);
    });        
  }

  buildQuoteGroup(){
    let aonCard = this.getElement(this.CARD_TWO);

    let div = this.createElement(TAG.DIV);
    div.className = CSS.FLEX_COLUMN;
    aonCard.addContent(div);

    let text = setStyles(this.createElement(TAG.DIV),{ fontWeight:500, color:CSS.variable(COLORS.AON_GRAY), marginBottom:"4px"});
    text.innerText = "Grupos de cotización activos";
    div.appendChild(text);

    const width = "100%";
    this.getQuoteGroup().forEach(({name, value})=>{
      let aonSwitch = setAttributes(new AonSwitch(),{id:this.getIdRand(), name:value, title: name, checked:this.isCheckedQuoteGroup(value)});
      aonSwitch.style.margin = "7px 0";
      div.appendChild(aonSwitch);
      aonSwitch.setLabelWidth(width);
    }); 
  }

  buildEmails(){
    let aonCard = this.getElement(this.CARD_THREE);
    aonCard.setTitleSection1(MSG.EMAIL);

    let div = this.createElement(TAG.DIV);
    div.className = CSS.FLEX_COLUMN;
    aonCard.addContent(div);


    let emailsParent = this.getEmailsParent();
    if(emailsParent.length){
      let titleOne = setStyles(this.createElement(TAG.DIV),{ fontWeight:"500", color:CSS.variable(COLORS.AON_GRAY), marginBottom:"4px"});
      titleOne.innerText = "Correos predefinidos";
      div.appendChild(titleOne);
  
      const child = document.createElement(TAG.DIV);
      child.innerText = emailsParent.join(", ");
      child.style.marginBottom = "15px";

      div.appendChild(child);
        
      let titleTwo = setStyles(this.createElement(TAG.DIV),{ fontWeight:"500", color:CSS.variable(COLORS.AON_GRAY), marginBottom:"4px"});
      titleTwo.innerText = "Correos adicionales";
      div.appendChild(titleTwo);

    }

    let table = new AonBasicTable();
    table.id = this.getIdRand();
    div.appendChild(table);
    
    if(this.getEmails().length){
      this.getEmails().forEach((email,idx)=>{
        this.buildEmail(table, email, idx);
      });
    } else {
      this.buildEmail(table, undefined, 0);
    }
  }

  isCheckedContract(value){
    const { APP_COMUNICA_CONTRACTS } = this.APP_PARAMS;
    return APP_COMUNICA_CONTRACTS ? APP_COMUNICA_CONTRACTS.split(',').some(v => v==value) : false;
  }

  isCheckedQuoteGroup(value){
    const { APP_COMUNICA_QUOTE_GROUP } = this.APP_PARAMS;
    return APP_COMUNICA_QUOTE_GROUP ? APP_COMUNICA_QUOTE_GROUP.split(',').some(v => v==value) : false;
  }
  
  buildEmail(table, email, i) {
		let rowNum = table.addRow();

    let rowCount = table.getRowsCount();

    let aonInput = new AonInput();
    aonInput.id = this.getIdRand() + i;
    aonInput.name = aonInput.id;
    aonInput.description = MSG.EMAIL + ' ' + (rowCount > 1 ? i + 1 : '');
    aonInput.value = email || "";

    this.querySelectorAll(`[id*='addEmail']`).forEach(el=>{
      el.visible = false;
    });

    let addEmailId = "addEmail"+this.getIdRand();
    let addEmail = new AonIconButton();
    addEmail.id = addEmailId + i;
    addEmail.title = MSG.ADD;
    addEmail.icon = MATERIAL_ICONS.ADD_CIRCLE_OUTLINE;
    addEmail.visible = rowCount === i+1;

    addEmail.addEventListener(EVENT.CLICK, (ev) => {
      ev.stopPropagation();
      ev.preventDefault();
      addEmail.visible = false;
      if(table.getRowsCount() <3){
        this.buildEmail(table, undefined, rowCount);
      }
    });
    let td = table.addCell(aonInput);
    td.style.width = '100%';
    table.addCell(addEmail);

    aonInput.onInput((ev)=>{
      const length = ev.target.value.length;
      const other = this.getEmailsLength(aonInput.name);
      const total = length + other;
      if(total > this.EMAIL_MAX_LENGTH){
        console.log("stop", "rpevent");
        ev.stopPropagation();
        ev.preventDefault();
        return false;
      }
    });

    aonInput.addIconWithRemove(MATERIAL_ICONS.MAIL, undefined, (ev) => {
      ev.stopPropagation();
      ev.preventDefault();
      let count = table.getRowsCount();
      let childVisible = 1;
      if(count === 1 ) {
        aonInput.value = '';
      } else {
        table.removeRow(rowNum);
        childVisible = count-1;
      }

      let cell = table.getCell(childVisible, 1);
      if(cell && cell.firstChild){
        cell.firstChild.visible = true;
      }
    });
	}

  showForm(id){
    this.querySelectorAll("form[id]").forEach(el=>{
      let display = id && el.id === id ? "block" : "none";
      el.style.display = display;
    });
  }

  getIdRand(){
    return Math.random().toString(36).substring(7);
  }

  getForm(){

    let contractType = [];
    let quoteGroup = [];
    let emails = [];

    const formContractType = serializeForm(this.getElement(this.FORM_CONTRACT_TYPES));
    const formQuoteGroup = serializeForm(this.getElement(this.FORM_QUOTE_GROUP));
    const formEmail = serializeForm(this.getElement(this.FORM_EMAILS));

    for(const key in formContractType){
      if(formContractType[key] === CONSTANT.TRUE){
        contractType.push(key);
      }
    }

    for(const key in formQuoteGroup){
      if(formQuoteGroup[key] === CONSTANT.TRUE){
        quoteGroup.push(key);
      }
    }

    for(const key in formEmail){
      emails.push(formEmail[key]);
    }

    return {
      [APP_PARAMS_PAYROLL.APP_COMUNICA_CONTRACTS] : contractType.join(','),
      [APP_PARAMS_PAYROLL.APP_COMUNICA_QUOTE_GROUP]: quoteGroup.join(','),
      [APP_PARAMS_PAYROLL.APP_COMUNICA_EMAILS]: emails.join(',').substring(0,96)
    };
  }

  getEmailsLength(nameExclude){
    const formEmail = serializeForm(this.getElement(this.FORM_EMAILS));
    let emails = [];
    for(const key in formEmail){
      if(key != nameExclude){
        emails.push(formEmail[key]);
      }
    }

    let values = emails.join(",");

    return values.length;
  }

  async save(){
    this.getApplication().startLoading();

    try {
      let domainId  = LS.getDomainId();
      const form = this.getForm();

      let params = [];
      for (let name in APP_PARAMS_PAYROLL) 
        params.push({name, value: form[name], domain: domainId});

      await saveApplicationParameter({params});
      this.showMessage();
    } catch (error) {
      this.showError(error);
    }

    this.getApplication().stopLoading();
  }

  async setContractTypes(){
    const resp = await getContractType().catch(e=>{
      console.log("error",e);
      return [];
    });
    this.CONTRACT_TYPES = resp.filter(q => q.value>0);
  }

  async setQuoteGroup(){
   const resp = await getQuoteGroup().catch(e=>{
      console.log("error",e);
      return [];
    });
    this.QUOTE_GROUP = sortBy(resp.filter(q => q.value>0), 'name', 'asc');
  }

  getContractTypes(){
    return this.CONTRACT_TYPES;
  }

  getQuoteGroup(){
    return this.QUOTE_GROUP;
  }

  getEmails(){
    const { APP_COMUNICA_EMAILS } = this.APP_PARAMS;
    return APP_COMUNICA_EMAILS ? APP_COMUNICA_EMAILS.split(',') : [];
  }

  getEmailsParent(){
    const { APP_COMUNICA_EMAILS } = this.APP_PARAMS_PARENT;
    return APP_COMUNICA_EMAILS ? APP_COMUNICA_EMAILS.split(',') : [];
  }

	async getAppParams(){
		if(!this.APP_PARAMS.length){
			try {
				await getApplicationParameters({
					params:[
            APP_PARAMS_PAYROLL.APP_COMUNICA_CONTRACTS,
            APP_PARAMS_PAYROLL.APP_COMUNICA_QUOTE_GROUP,
            APP_PARAMS_PAYROLL.APP_COMUNICA_EMAILS
					]
				}).then(params=>{
					let newResp = [];
					params
					.filter(p => p.value)
					.forEach(p => 
						newResp[p.name] = p.value
					);
					this.APP_PARAMS = newResp;
				});
			} catch (e) {
				console.log("error getAppParams", e);
			}
		}
		return this.APP_PARAMS;
	}

  async getAppParamsParent(){
		if(!this.APP_PARAMS_PARENT.length){
			try {
				await getApplicationParameters({
					params:[
            // APP_PARAMS_PAYROLL.APP_COMUNICA_CONTRACTS,
            // APP_PARAMS_PAYROLL.APP_COMUNICA_QUOTE_GROUP,
            APP_PARAMS_PAYROLL.APP_COMUNICA_EMAILS
					],
          parent:true
				}).then(params=>{
					let newResp = [];
					params
					.filter(p => p.value)
					.forEach(p => 
						newResp[p.name] = p.value
					);
					this.APP_PARAMS_PARENT = newResp;
				});
			} catch (e) {
				console.log("error getAppParams", e);
			}
		}
		return this.APP_PARAMS_PARENT;
	}

  isChild(){
    return this.dur && this.dur.domain && this.dur.domain.parentId;
  }
}

window.customElements.define('aon-comunica-config',  AonComunicaConfig);
