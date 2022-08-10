import { AonDate } from "../../../components/aon-date.js";
import { AonInput } from "../../../components/aon-input.js";
import { AonSelect } from "../../../components/aon-select.js";
import { AonSwitch } from "../../../components/aon-switch.js";
import { AonNumber } from "../../../components/aon-number.js";
import { TAG, EVENT, MSG, CONSTANT, CSS, COLORS } from "../../../environments/environments.js";
import { getCccForActivity } from "../../../services/contractService.js";
import { serializeForm, sortBy } from "../../../services/utils.js";
import { setAttributes, setStyles } from "../../../services/utilsComponents.js";
import { MESSENGER_IDS, MESSENGER_VIEWS, TASK_STATUS } from "../MessengerEnums.js";
import { TaskCreationUtils } from "../shared/TaskCreationUtils.js";
import { getOccupation, getRlce, getContractType, getQuoteGroup, getJourneyType, sendAlta } from "../../../services/comunicaService.js";

/**
 * @param {Task} task 
 * @param {HTMLElement} card 
 */
 export const createFormMov = (task, card) =>{
    card.flex = "true";
    card.getCardTitle().style.marginBottom = 0;
    
    let data = task.id ? JSON.parse(task.description) : {};

    const form = setAttributes(document.createElement(TAG.FORM),{
        id:MESSENGER_IDS.FORM_DINAMIC,
        action:"#"
    });
    form.onsubmit = () => false;
    form.style.width = "100%";
    card.setContent(form);

    //EMAIL ALTERNATIVE
    let alternative = setAttributes(new AonInput(),{ id:"alternative", name:"alternative", visible:CONSTANT.FALSE });
    alternative.value = data.alternative || (!task.id && task.auth && task.auth.email ? task.auth.email : "");
    form.appendChild(alternative);

    //DOMAIN SENDER
    let domain = setAttributes(new AonInput(),{ id:"domain", name:"domain", visible:CONSTANT.FALSE });
    domain.value = data.domain || (!task.id && task.domain && task.domain.id ? task.domain.id : "");
    form.appendChild(domain);

    //-----------DATA ENTERPRISE
    createDataEnterprise(form, data);
    //-----------END DATA ENTERPRISE

    //---------------------DATA EMPLOYEE
    createDataEmployee(form, data)
    //---------------------END DATA EMPLOYEE

    //---------------------DATA CONTRACT
    createDataContract(form, data, task);
    //---------------------END DATA CONTRACT
}

/**
 * 
 * @param {HTMLElement} form 
 * @param {Object} data 
 */
const createDataEnterprise = (form, data) => {

    let ctaCti = setAttributes(new AonSelect(),{ title: "Cuenta de cotización", id:"ctaCti", name:"ctaCti"});
    TaskCreationUtils.createDivGrid(form, ctaCti, {classes:[CSS.AON_COL_XS_12]})
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
    
    let ipf = setAttributes(new AonInput(),{
        id: "ipf",
        name:"ipf",
        description: "DNI/NIE",
        value: data.ipf ? data.ipf : ""
    });
    TaskCreationUtils.createDivGrid(form, ipf, {classes:[CSS.AON_COL_XS_6, CSS.AON_COL_MD_6]})

    let nss = setAttributes(new AonInput(),{
        id:"nss",
        name:"nss",
        description:"NSS/NAF (Opcional)",
        value: data.nss ? data.nss : ""
    });
    TaskCreationUtils.createDivGrid(form, nss, {classes:[CSS.AON_COL_XS_6, CSS.AON_COL_MD_6]})

    let name = setAttributes(new AonInput(),{
        id: "name",
        name:"name",
        description: MSG.NAME,
        value: data.name ? data.name : ""
    });
    TaskCreationUtils.createDivGrid(form, name, {classes:[CSS.AON_COL_XS_12]})

    let surname = setAttributes(new AonInput(),{
        id:"surname",
        name:"surname",
        description:"1er Apellido",
        value: data.surname ? data.surname : ""
    });
    TaskCreationUtils.createDivGrid(form, surname, {classes:[CSS.AON_COL_XS_6, CSS.AON_COL_MD_6]})

    let lastSurname = setAttributes(new AonInput(),{
        id: "lastSurname",
        name:"lastSurname",
        description: `2do Apellido (${MSG.OPTIONAL})`,
        value: data.lastSurname ? data.lastSurname : ""
    });
    TaskCreationUtils.createDivGrid(form, lastSurname, {classes:[CSS.AON_COL_XS_6, CSS.AON_COL_MD_6]})
}

/**
 * 
 * @param {HTMLElement} form 
 * @param {Object} data 
 * @param {Task} task 
 */
 const createDataContract = (form, data, task) => {
    const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
    const dur = aonMessengerChat.getDur();
    createTitle(form, `${MSG.DATA} del Contrato`);

    let fra = setAttributes(new AonDate(),{ title: MSG.START_DATE, id:"fra", name:"fra"});
    TaskCreationUtils.createDivGrid(form, fra, {classes:[CSS.AON_COL_XS_6, CSS.AON_COL_MD_6]})
    if(data.fra) fra.setDate(new Date(data.fra))

    let category = setAttributes(new AonInput(),{
        id:"category",
        name:"category",
        description:"Categoria profesional",
        value: data.category ? data.category : "",
    });
    TaskCreationUtils.createDivGrid(form, category, {classes:[CSS.AON_COL_XS_6, CSS.AON_COL_MD_6]})

    let salaryCheck = setAttributes(new AonSwitch(),{ title: "Salario s/convenio", id:"salaryCheck", name:"salaryCheck", checked: data.salaryCheck == CONSTANT.FALSE ? CONSTANT.FALSE : CONSTANT.TRUE});
    TaskCreationUtils.createDivGrid(form, salaryCheck, {classes:[CSS.AON_COL_XS_12, CSS.AON_COL_MD_5], styles:{ marginBottom: "8px"} });

    let salaryType = setAttributes(new AonSelect(),{ title: "Tipo", id:"salaryType", name:"salaryType"});
    TaskCreationUtils.createDivGrid(form, salaryType, {classes:[CSS.AON_COL_XS_6, CSS.AON_COL_MD_3]});
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
    TaskCreationUtils.createDivGrid(form, salary, {classes:[CSS.AON_COL_XS_6, CSS.AON_COL_MD_4]});
    if(salaryCheck.isChecked()) salary.disabled = CONSTANT.TRUE;

    salaryCheck.addEventListener(EVENT.CHANGE,({target})=>{
        salary.value = "";
        salaryType.clear();
        salary.disabled = target.checked;
        salaryType.setDisabled(target.checked);
    });

    let fullTimeCheck = setAttributes(new AonSwitch(),{ title: "Jornada completa", id:"fullTimeCheck", name:"fullTimeCheck", checked: data.fullTimeCheck == CONSTANT.FALSE ? CONSTANT.FALSE : CONSTANT.TRUE});
    TaskCreationUtils.createDivGrid(form, fullTimeCheck, {classes:[CSS.AON_COL_XS_12, CSS.AON_COL_MD_5], styles:{ marginBottom: "8px"} });

    let jornadaType = setAttributes(new AonSelect(),{ title: "Tipo", id:"jornadaType", name:"jornadaType"});
    TaskCreationUtils.createDivGrid(form, jornadaType, {classes:[CSS.AON_COL_XS_4, CSS.AON_COL_MD_3]});

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

    TaskCreationUtils.createDivGrid(form, hour, {classes:[CSS.AON_COL_XS_4, CSS.AON_COL_MD_2]});
    if(fullTimeCheck.isChecked()) hour.disabled = CONSTANT.TRUE;

    let coef = setAttributes(new AonNumber(),{
        id:"coef", 
        name:"coef", 
        description:"Coef",
        decimals:"2",
        format:CONSTANT.TRUE,
        value: data.coef ? data.coef : "",
    })
    TaskCreationUtils.createDivGrid(form, coef, {classes:[CSS.AON_COL_XS_4, CSS.AON_COL_MD_2]});
    // addSpanDecimal(coef);
    if(fullTimeCheck.isChecked()) coef.disabled = CONSTANT.TRUE;

    fullTimeCheck.addEventListener(EVENT.CHANGE,({target})=>{
        coef.value    = hour.value = "";
        coef.disabled = hour.disabled = target.checked;
        jornadaType.clear();
        jornadaType.setDisabled(target.checked);
    });

    let durationCheck = setAttributes(new AonSwitch(),{ title: "Duración indefinida", id:"durationCheck", name:"durationCheck", checked: data.durationCheck == CONSTANT.FALSE ? CONSTANT.FALSE : CONSTANT.TRUE});
    TaskCreationUtils.createDivGrid(form, durationCheck, {classes:[CSS.AON_COL_XS_6, CSS.AON_COL_MD_5]});

    let frb = setAttributes(new AonDate(),{ title: MSG.END_DATE, id:"frb", name:"frb"});
    TaskCreationUtils.createDivGrid(form, frb, {classes:[CSS.AON_COL_XS_6, CSS.AON_COL_MD_7]});
    frb.disabledDate(durationCheck.isChecked());
    if(data.frb) frb.setDate(new Date(data.frb))

    durationCheck.addEventListener(EVENT.CHANGE,({target})=>{
        frb.value = "";
        frb.disabledDate(target.checked);
    });

    const divManager = TaskCreationUtils.createDivGrid(form, undefined, {});
    divManager.style.display = data.fra && (dur.isComunicaManager() || dur.isSaltraManager()) ? "block" : "none";
    //---------------------DATA RESTANT
    let contract = setAttributes(new AonSelect(),{ title: "Tipo de contrato", id:"contract", name:"contract", autocomplete: CONSTANT.OFF});
    TaskCreationUtils.createDivGrid(divManager, contract, {classes:[CSS.AON_COL_XS_12, CSS.AON_COL_MD_4]});
    fillContract(contract, data.contract);

    let gc = setAttributes(new AonSelect(),{ title: "Grupo de cotización", id:"gc", name:"gc"});
    TaskCreationUtils.createDivGrid(divManager, gc, {classes:[CSS.AON_COL_XS_12, CSS.AON_COL_MD_4]});
    fillGc(gc, data.gc);

    let ocup = setAttributes(new AonSelect(),{ title: "Ocupación", id:"ocup", name:"ocup"});
    TaskCreationUtils.createDivGrid(divManager, ocup, {classes:[CSS.AON_COL_XS_12, CSS.AON_COL_MD_4]});
    fillOcu(ocup, data.ocup);

    let rlce = setAttributes(new AonSelect(),{ title: "RLCE (opcional)", id:"rlce", name:"rlce"});
    TaskCreationUtils.createDivGrid(divManager, rlce, {classes:[CSS.AON_COL_XS_12, CSS.AON_COL_MD_12]});
    fillRlce(rlce, data.rlce);

    jornadaType.addEventListener(EVENT.CHANGE,({detail})=> {
        if(detail && detail.value){
            hour.value = detail.value === "semanal" ? 40 : 8;
            hour.dispatchEvent(new Event(EVENT.CHANGE));
        } 
    });

    hour.addEventListener(EVENT.CHANGE,()=> calculoCoef(jornadaType.getDetail()) );

    // ------------OBSERVATION
    const observation = TaskCreationUtils.createDivEditable(undefined, MSG.OBSERVATION,  data.observation || "" , "observation" ,  MSG.TYPE_HERE);
    TaskCreationUtils.createDivGrid(form, observation, {classes:[CSS.AON_COL_XS_12]});

    if((dur.isComunicaManager() || dur.isSaltraManager()) && [TASK_STATUS.PENDING, TASK_STATUS.IN_PROGRESS].includes(task.status)){
        let btnAccept = TaskCreationUtils.createBtnAccept();
        btnAccept.addEventListener(EVENT.CLICK, ()=>  processAccept(aonMessengerChat) );
         
        TaskCreationUtils.createDivGrid(form, btnAccept, {classes:[CSS.AON_COL_XS_12, CSS.AON_COL_XS_OFFSET_4]});
    }
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
    TaskCreationUtils.createDivGrid(parent, title, {
        classes:[CSS.AON_COL_XS_12],
        styles:{ padding: "5px 0" }
    });
} 

//-----------FILL CTACTI
const fillCtaCti = (aonSelect, data) => {

    getCccForActivity().then(({cccs})=>{
        
        let opts = [];
        for (const key in cccs) {
            const ccc = cccs[key];
            opts.push(ccc);
        }

        const options = opts.filter( (v,index)=>opts.findIndex((m) => m.ccc === v.ccc) === index ).map(r => ({ ...r, name: `${r.cccRegimeCode} - ${r.ccc}`, value: r.ccc }));

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
    aonSelect.setOptions( getJourneyType() );
    if(jornadaType) aonSelect.value = jornadaType;
}

//-----------FILL CONTRACT
const fillContract = (aonSelect, contract) => {
    getContractType().then(resp=>{
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
    getQuoteGroup().then(resp=>{
        resp = sortBy(resp, 'name', 'asc');
        let options = resp.map(r =>  ({ ...r, name: `${r.name}`, value: r.value}) );

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
    getOccupation().then(resp=>{
        resp = sortBy(resp, 'value', 'asc');
        let options = resp.map(r =>  ({ ...r, name: `${r.name}`, value: r.value}));

        if(ocup){
            const exists = options.some(v => v.value === ocup);
            if(!exists)  options.push({name: ocup, value: ocup});
        }

        aonSelect.setOptions( options );

        if(ocup) aonSelect.value = ocup;
    })
}
//-----------FILL RLCE
const fillRlce = (aonSelect, rlce) => {
    getRlce().then(resp=>{

        let options = sortBy( resp.map(r =>  ({ ...r, name: `${r.value} - ${r.name}`, value: r.value})), 'value', 'asc');

        if(rlce){
            const exists = options.some(v => v.value === rlce);
            if(!exists)  options.push({name: rlce, value: rlce});
        }

        aonSelect.setOptions( options );

        if(rlce) aonSelect.value = rlce;
    });
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


const processAccept = async (aonMessengerChat) => {
    let application = aonMessengerChat.getApplication();
    application.startLoading();
    try {
        const data = getFormMovJson();
        let newData = {
            ...data,
            fecha: data.fra,
            name: `${data.name} ${data.surname} ${data.lastSurname || ""}`
        }
        if(data.ocu) newData.ocupacion = data.ocu;
        if(data.coef) newData.coefparcial = parseInt(data.coef);

        //---SEND MOV TGSS
        await sendAlta(newData);

        //---CLOSE TASK
        await aonMessengerChat.updateTaskStatus(TASK_STATUS.FINISHED, `${MSG.REQUEST} tramitada`);
    } catch (err) {
        console.log(err);
        aonMessengerChat.showError(err);
    }

    application.stopLoading();
}