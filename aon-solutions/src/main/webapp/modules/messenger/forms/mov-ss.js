import { AonDate } from "../../../components/aon-date.js";
import { AonInput } from "../../../components/aon-input.js";
import { AonSelect } from "../../../components/aon-select.js";
import { AonSwitch } from "../../../components/aon-switch.js";
import { AonNumber } from "../../../components/aon-number.js";
import { TAG, EVENT, MSG, CONSTANT, CSS, COLORS } from "../../../environments/environments.js";
import { getCccForActivity } from "../../../services/contractService.js";
import {  serializeForm } from "../../../services/utils.js";
import { newComponent, setAttributes, setStyles } from "../../../services/utilsComponents.js";
import { MESSENGER_IDS } from "../MessengerEnums.js";
import { createBtnAccept, createDivEditable } from "../shared/creationUtils.js";
import { getGrupoCotizacion, getOcupacion, getTipoContrato } from "../../../services/comunicaService.js";
import { addSpanDecimal } from "../../laboral/createComponent.js";


/**
 * 
 * @param {HTMLElement} card 
 */
 export const createFormMov = (card, aonMessengerChat) =>{
    const task = aonMessengerChat.task;
    card.flex = "true";
    card.getCardTitle().style.marginBottom = 0;
    
    let data = task.id ? JSON.parse(task.description) : {};

    const form  = setAttributes(document.createElement(TAG.FORM),{
        id:MESSENGER_IDS.FORM_DINAMIC,
        action:"#"
    });
    form.onsubmit = () => false;
    form.style.width = "100%";
    card.setContent(form);

    //-----------DATA ENTERPRISE
    createDataEnterprise(form, data);
    //-----------END DATA ENTERPRISE

    //---------------------DATA EMPLOYEE
    createDataEmployee(form, data)
    //---------------------END DATA EMPLOYEE

    //---------------------DATA CONTRACT
    createDataContract(form, data, aonMessengerChat);
    //---------------------END DATA CONTRACT

}

/**
 * 
 * @param {HTMLElement} form 
 * @param {Object} data 
 */
const createDataEnterprise = (form, data) => {

    let ctaCti = setAttributes(new AonSelect(),{ title: "Cuenta de cotización", id:"ctaCti", name:"ctaCti"});
    createDiv(form, ctaCti, {classes:[CSS.AON_COL_XS_12]})
    fillCtaCti(ctaCti, data);

    let regime = setAttributes(new AonInput(),{ id:"regime", name:"regime", visible:CONSTANT.FALSE });
    form.appendChild(regime);

    ctaCti.addEventListener(EVENT.CHANGE, ({ detail }) => regime.value = detail.cccRegimeCode);

}

/**
 * 
 * @param {HTMLElement} form 
 * @param {Object} data 
 */
 const createDataEmployee = (form, data) => {
    createTitle(form, "Datos del empleado");
    
    let nss = setAttributes(new AonInput(),{
        id:"nss",
        name:"nss",
        description:"NSS/NAF (Opcional)",
        value: data.nss ? data.nss : ""
    });
    createDiv(form, nss, {classes:[CSS.AON_COL_XS_6, CSS.AON_COL_MD_6]})

    let ipf = setAttributes(new AonInput(),{
        id: "ipf",
        name:"ipf",
        description: "DNI/NIE",
        value: data.ipf ? data.ipf : ""
    });
    createDiv(form, ipf, {classes:[CSS.AON_COL_XS_6, CSS.AON_COL_MD_6]})

    let surname = setAttributes(new AonInput(),{
        id:"surname",
        name:"surname",
        description:"Apellido 1",
        value: data.surname ? data.surname : ""
    });
    createDiv(form, surname, {classes:[CSS.AON_COL_XS_6, CSS.AON_COL_MD_6]})

    let lastSurname = setAttributes(new AonInput(),{
        id: "lastSurname",
        name:"lastSurname",
        description: "Apellido 2",
        value: data.lastSurname ? data.lastSurname : ""
    });
    createDiv(form, lastSurname, {classes:[CSS.AON_COL_XS_6, CSS.AON_COL_MD_6]})

    let name = setAttributes(new AonInput(),{
        id: "name",
        name:"name",
        description: "Nombre",
        value: data.name ? data.name : ""
    });
    createDiv(form, name, {classes:[CSS.AON_COL_XS_12]})

}

/**
 * 
 * @param {HTMLElement} form 
 * @param {Object} data 
 * @param {HTMLElement} aonMessengerChat aon-messenger-chat 
 */
 const createDataContract = (form, data, aonMessengerChat) => {
    const dur = aonMessengerChat.getDur();
    createTitle(form, "Datos del Contrato");

    let fra = setAttributes(new AonDate(),{ title: MSG.START_DATE, id:"fra", name:"fra"});
    createDiv(form, fra, {classes:[CSS.AON_COL_XS_6, CSS.AON_COL_MD_6]})
    if(data.fra) fra.setDate(new Date(data.fra))

    let category = setAttributes(new AonInput(),{
        id:"category",
        name:"category",
        description:"Categoria profesional",
        value: data.category ? data.category : "",
    });
    createDiv(form, category, {classes:[CSS.AON_COL_XS_6, CSS.AON_COL_MD_6]})

    let salaryCheck = setAttributes(new AonSwitch(),{ title: "Salario s/convenio", id:"salaryCheck", name:"salaryCheck", checked: data.salaryCheck == CONSTANT.FALSE ? CONSTANT.FALSE : CONSTANT.TRUE});
    createDiv(form, salaryCheck, {classes:[CSS.AON_COL_XS_12, CSS.AON_COL_MD_5], styles:{ marginBottom: "8px"} });

    let salaryType = setAttributes(new AonSelect(),{ title: "Tipo", id:"salaryType", name:"salaryType"});
    createDiv(form, salaryType, {classes:[CSS.AON_COL_XS_6, CSS.AON_COL_MD_3]});
    salaryType.setDisabled(salaryCheck.isChecked());
    fillSalaryType(salaryType, data.salaryType);

    let salary = setAttributes(new AonNumber(),{
        id:"salary", 
        name:"salary", 
        description:"Salario mensual",
        decimals:"2",
        format:CONSTANT.TRUE,
        value: data.salary ? data.salary : "",
    })
    createDiv(form, salary, {classes:[CSS.AON_COL_XS_6, CSS.AON_COL_MD_4]});
    if(salaryCheck.isChecked()) salary.disabled = CONSTANT.TRUE;

    salaryCheck.addEventListener(EVENT.CHANGE,({target})=>{
        salary.value = "";
        salaryType.clear();
        salary.disabled = target.checked;
        salaryType.setDisabled(target.checked);
    });

    let fullTimeCheck = setAttributes(new AonSwitch(),{ title: "Jornada completa", id:"fullTimeCheck", name:"fullTimeCheck", checked: data.fullTimeCheck == CONSTANT.FALSE ? CONSTANT.FALSE : CONSTANT.TRUE});
    createDiv(form, fullTimeCheck, {classes:[CSS.AON_COL_XS_12, CSS.AON_COL_MD_5], styles:{ marginBottom: "8px"} });

    let jornadaType = setAttributes(new AonSelect(),{ title: "Tipo", id:"jornadaType", name:"jornadaType"});
    createDiv(form, jornadaType, {classes:[CSS.AON_COL_XS_4, CSS.AON_COL_MD_3]});

    jornadaType.setDisabled(fullTimeCheck.isChecked());
    fillJornadaType(jornadaType, data.jornadaType);

    let hour = setAttributes(new AonNumber(),{
        id:"hour", 
        name:"hour", 
        decimals:"2",
        description:MSG.HOURS,
        format:CONSTANT.TRUE,
        value: data.hour ? data.hour : "",
    });

    createDiv(form, hour, {classes:[CSS.AON_COL_XS_4, CSS.AON_COL_MD_2]});
    if(fullTimeCheck.isChecked()) hour.disabled = CONSTANT.TRUE;

    jornadaType.addEventListener(EVENT.CHANGE,({detail})=> {
        if(detail && detail.value){
            hour.value = detail.value === 1 ? 40 : 8;
            hour.dispatchEvent(new Event(EVENT.CHANGE));
        } 
    })

    hour.addEventListener(EVENT.CHANGE,()=> calculoCoef(jornadaType.getDetail()) );

    let coef = setAttributes(new AonNumber(),{
        id:"coef", 
        name:"coef", 
        description:"Coef",
        decimals:"2",
        format:CONSTANT.TRUE,
        value: data.coef ? data.coef : "",
    })
    createDiv(form, coef, {classes:[CSS.AON_COL_XS_4, CSS.AON_COL_MD_2]});
    // addSpanDecimal(coef);
    if(fullTimeCheck.isChecked()) coef.disabled = CONSTANT.TRUE;

    fullTimeCheck.addEventListener(EVENT.CHANGE,({target})=>{
        coef.value    = hour.value = "";
        coef.disabled = hour.disabled = target.checked;
        jornadaType.clear();
        jornadaType.setDisabled(target.checked);
    });


    let durationCheck = setAttributes(new AonSwitch(),{ title: "Duración indefinida", id:"durationCheck", name:"durationCheck", checked: data.durationCheck == CONSTANT.FALSE ? CONSTANT.FALSE : CONSTANT.TRUE});
    createDiv(form, durationCheck, {classes:[CSS.AON_COL_XS_6, CSS.AON_COL_MD_5]});

    let frb = setAttributes(new AonDate(),{ title: MSG.END_DATE, id:"frb", name:"frb"});
    createDiv(form, frb, {classes:[CSS.AON_COL_XS_6, CSS.AON_COL_MD_7]});
    frb.disabledDate(durationCheck.isChecked());
    if(data.frb) frb.setDate(new Date(data.frb))

    durationCheck.addEventListener(EVENT.CHANGE,({target})=>{
        frb.value = "";
        frb.disabledDate(target.checked);
    });


    const divManager = createDiv(form, undefined, {});
    divManager.style.display = data.fra && dur.isComunicaManager() ? "block" : "none";
    //---------------------DATA RESTANT
    let contract = setAttributes(new AonSelect(),{ title: "Tipo de contrato", id:"contract", name:"contract", autocomplete: CONSTANT.OFF});
    createDiv(divManager, contract, {classes:[CSS.AON_COL_SM_4]});
    fillContract(contract, data.contract);

    let gc = setAttributes(new AonSelect(),{ title: "Grupo de cotización", id:"gc", name:"gc"});
    createDiv(divManager, gc, {classes:[CSS.AON_COL_SM_4]});
    fillGc(gc, data.gc);

    let ocup = setAttributes(new AonSelect(),{ title: "Ocupación", id:"ocup", name:"ocup"});
    createDiv(divManager, ocup, {classes:[CSS.AON_COL_SM_4]});
    fillOcu(ocup, data.ocup);


    // ------------OBSERVATION
    const observation = createDivEditable(undefined, MSG.OBSERVATION,  data.observation || "" , "observation" ,  MSG.TYPE_HERE);
    createDiv(form, observation, {classes:[CSS.AON_COL_XS_12]});

    let btnAccept = createBtnAccept();
    btnAccept.addEventListener(EVENT.CLICK, ()=> aonMessengerChat.getApplication().development() );
     
    createDiv(form, btnAccept, {classes:[CSS.AON_COL_XS_12, CSS.AON_COL_XS_OFFSET_4]});
}

/**
 * 
 * @returns json form vacacion json
 */
 export const getFormMovJson = ()=>{
    const form = document.getElementById(MESSENGER_IDS.FORM_DINAMIC);
    if(form){
        const formSerialize = serializeForm(form);
        let observation = form.querySelector("#observation").innerText;
        return { ...formSerialize, observation };
    }
    return null;
}


/**
 * 
 * @param {HTMLElement} parent 
 * @param {String} text 
 */
const createTitle = (parent, text) => {
    let title = setStyles(document.createElement(TAG.SPAN),{ fontSize: "16px", fontWeight:750, color:CSS.variable(COLORS.AON_DARK_GRAY) });
    title.innerHTML = text;
    createDiv(parent, title, {
        classes:[CSS.AON_COL_XS_12],
        styles:{
            padding: "5px 0"
        }
    });
} 

/**
 * 
 * @param {HTMLElement} parent appenchild
 * @param {HTMLElement} child element add Optional
 * @param {Object} properties 
 * @returns 
 */
const createDiv = (parent, child, properties)=> {

    const div = newComponent({ type: TAG.DIV, ...properties }).element;

    parent.appendChild(div);

    if(child) div.appendChild(child);

    return div;
}



//-----------FILL CTACTI
const fillCtaCti = (aonSelect, data) => {

    getCccForActivity().then(({cccs})=>{
        
        let options = [];
        for (const key in cccs) {
            const ccc = cccs[key];
            options.push(ccc);
        }

        options = options.filter( (v,index, self)=>self.findIndex((m) => m.ccc === v.ccc) === index ).map(r => ({ ...r, name: `${r.cccRegimeCode} - ${r.ccc}`, value: r.ccc }));

        if(data.ctaCti &&  data.regime){
            const exists = options.some(v => v.ccc === data.ctaCti);
            if(!exists)  options.push({ccc: data.ctaCti, cccRegimeCode: data.regime, name: `${data.regime} - ${data.ctaCti}`, value: data.ctaCti });
        }

        aonSelect.setOptions( options );

        if(data.ctaCti) aonSelect.value = data.ctaCti;
    });
}

//-----------FILL SALARYTYPE
const fillSalaryType = (aonSelect, salaryType) => {
    let options = [
        {name: 'Bruto', value:1},
        {name: 'Neto', value: 2}
    ];

    aonSelect.setOptions( options );

    if(salaryType) aonSelect.value = salaryType;
}

//-----------FILL JORNADATYPE
const fillJornadaType = (aonSelect, jornadaType) => {
    let options = [
        {name: 'Semanal', value:1},
        {name: 'Diaria', value: 2}
    ];

    aonSelect.setOptions( options );

    if(jornadaType) aonSelect.value = jornadaType;
}

//-----------FILL CONTRACT
const fillContract = (aonSelect, contract) => {
    getTipoContrato().then(resp=>{
        let options = resp.map(r =>  ({ ...r, name: `${r.value} - ${r.name}`, value: r.value}));

        if(contract){
            const exists = options.some(v => v.value === contract);
            if(!exists)  options.push({name: contract, value: contract});
        }

        aonSelect.setOptions( options );

        if(contract) aonSelect.value = contract;
    })
}

//-----------FILL GRUPO DE COTIZACION
const fillGc = (aonSelect, gc) => {
    getGrupoCotizacion().then(resp=>{
        let options = resp.map(r =>  ({ ...r, name: `${r.value} - ${r.name}`, value: r.value}) );

        if(gc){
            const exists = options.some(v => v.value === gc);
            if(!exists)  options.push({name: gc, value: gc});
        }

        aonSelect.setOptions( options );

        if(gc) aonSelect.value = gc;
    })
}

//-----------FILL OCUPACION
const fillOcu = (aonSelect, ocup) => {
    getOcupacion().then(resp=>{
        let options = resp.map(r =>  ({ ...r, name: `${r.value} - ${r.name}`, value: r.value}));

        if(ocup){
            const exists = options.some(v => v.value === ocup);
            if(!exists)  options.push({name: ocup, value: ocup});
        }

        aonSelect.setOptions( options );

        if(ocup) aonSelect.value = ocup;
    })
}

const calculoCoef = ({value}) =>  {
    if(value){
        let horas_convenio = value === 1 ? 40 : 8;
        let horas = document.getElementById('hour').value;
        let coef = '';
        if ( horas > 0) {
            let calc = Math.round(parseFloat((parseFloat(horas) / parseFloat(horas_convenio)) * 1000));
            if (calc > 0 && calc <= 999) coef = calc;
        }
        document.getElementById('coef').value = coef;
    }
}