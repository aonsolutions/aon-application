import {AonElement} from '../../components/AonElement.js';
import { MSG, TAG, CSS, COLORS, CONSTANT } from "../../environments/environments.js";
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
import { getApplicationParameters, saveApplicationParameter } from '../../services/applicationParameterService.js';
import { APP_PARAMS_PAYROLL } from './PayrollEnums.js';

export class AonComunicaConfig extends AonElement {
  FORM;
  CARD;
  CARD_TWO;
  CONTRACT_TYPES;
  APP_PARAMS;
  QUOTE_GROUP;
  PARAMS_CONTRACT;
  PARAMS_QUOTE_GROUP;
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
    this.id = this.id || 'AonComunicaConfig';
    this.CARD = this.id+"card";
    this.CARD_TWO = this.id+"cardTwo";
    this.FORM_CONTRACT_TYPES = this.id+"formContractType";
    this.FORM_QUOTE_GROUP = this.id+"formQuoteGroup";
    this.CONTRACT_TYPES =[];
    this.QUOTE_GROUP =[];
    this.APP_PARAMS =[];
  }

  build(){
    this.buildToolbar();
    this.buildTabs();
    this.buildCard();

    Promise.all([
      this.getAppParams(),
      this.setContractTypes(),
      this.setQuoteGroup()
    ]).then(()=>{
      this.buildContractType();
      this.buildQuoteGroup();
      this.showContractType();
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
    const options = [
      { 
        title: MSG.TYPE_CONTRACT, 
        fn: () => {
          this.showContractType();
        }
      },
      { 
        title: MSG.QUOTE_GROUP, 
        fn: () => {
          this.showQuoteGroup();
        }
      }
    ];

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
  }

  buildContractType(){
    let aonCard = this.getElement(this.CARD);

    let div = this.createElement(TAG.DIV);
    div.className = CSS.FLEX_COLUMN;
    aonCard.addContent(div);

    let text = setStyles(this.createElement(TAG.DIV),{ fontWeight:500, color:CSS.variable(COLORS.AON_GRAY), marginBottom:4});
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

    let text = setStyles(this.createElement(TAG.DIV),{ fontWeight:500, color:CSS.variable(COLORS.AON_GRAY), marginBottom:4});
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

  isCheckedContract(value){
    const { APP_COMUNICA_CONTRACTS } = this.APP_PARAMS;
    return APP_COMUNICA_CONTRACTS ? APP_COMUNICA_CONTRACTS.split(',').some(v => v==value) : false;
  }

  isCheckedQuoteGroup(value){
    const { APP_COMUNICA_QUOTE_GROUP } = this.APP_PARAMS;
    return APP_COMUNICA_QUOTE_GROUP ? APP_COMUNICA_QUOTE_GROUP.split(',').some(v => v==value) : false;
  }

  showContractType(){
    this.getElement(this.FORM_CONTRACT_TYPES).style.display = "block";
    this.getElement(this.FORM_QUOTE_GROUP).style.display = "none";
  }
  
  showQuoteGroup(){
    this.getElement(this.FORM_CONTRACT_TYPES).style.display = "none";
    this.getElement(this.FORM_QUOTE_GROUP).style.display = "block";
  }

  getIdRand(){
    return Math.random().toString(36).substring(7);
  }

  getForm(){

    let contractType = [];
    let quoteGroup = [];

    const formContractType = serializeForm(this.getElement(this.FORM_CONTRACT_TYPES));
    const formQuoteGroup = serializeForm(this.getElement(this.FORM_QUOTE_GROUP));

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

    return {
      [APP_PARAMS_PAYROLL.APP_COMUNICA_CONTRACTS] : contractType.join(','),
      [APP_PARAMS_PAYROLL.APP_COMUNICA_QUOTE_GROUP]: quoteGroup.join(',')
    };
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

	async getAppParams(){
		if(!this.APP_PARAMS.length){
			try {
				await getApplicationParameters({
					params:[
            APP_PARAMS_PAYROLL.APP_COMUNICA_CONTRACTS,
            APP_PARAMS_PAYROLL.APP_COMUNICA_QUOTE_GROUP
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
}

window.customElements.define('aon-comunica-config',  AonComunicaConfig);
