
import { AonBasicTable } from "../../../components/aon-basic-table.js";
import { AonDate } from "../../../components/aon-date.js";
import { AonIconButton } from "../../../components/aon-icon-button.js";
import { AonInput } from "../../../components/aon-input.js";
import { AonSelect } from "../../../components/aon-select.js";
import { AonSwitch } from "../../../components/aon-switch.js";
import { TAG, EVENT, MSG, MATERIAL_ICONS, CONSTANT, CSS, COLORS } from "../../../environments/environments.js";
import { getGrupoCotizacion, getOcupacion, getTipoContrato } from "../../../services/comunicaService.js";
import {  serializeForm } from "../../../services/utils.js";
import { setAttributes, setStyles } from "../../../services/utilsComponents.js";
import { partTime } from "../../laboral/createComponent.js";
import { MESSENGER_IDS } from "../MessengerEnums.js";

/**
 * 
 * @param {HTMLElement} card 
 */
 export const createFormMov = (card, aonMessengerChat) =>{
    const task = aonMessengerChat.task;
    
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
    createDataContract(form, data);
    //---------------------END DATA CONTRACT

}

/**
 * 
 * @param {HTMLElement} form 
 * @param {Object} data 
 */
const createDataEnterprise = (form, data) => {
    const row =setStyles(document.createElement(TAG.DIV),{ display:"flex" });
    form.appendChild(row);

    const columnOne = setStyles(document.createElement(TAG.DIV),{ width:"100%" });
    row.appendChild(columnOne);

    let ctaCti = setAttributes(new AonSelect(),{ title: "Cuenta de cotización", id:"ctaCti", name:"ctaCti"});
    columnOne.appendChild(ctaCti);
    fillCtaCti(ctaCti, data.ctaCti);

    let regime = setAttributes(new AonInput(),{ id:"regime", name:"regime", visible:CONSTANT.FALSE });
    columnOne.appendChild(regime);

    ctaCti.addEventListener(EVENT.CHANGE, ({ detail }) => regime.value = detail.cccRegimeCode);

}

/**
 * 
 * @param {HTMLElement} form 
 * @param {Object} data 
 */
 const createDataEmployee = (form, data) => {
    createTitle(form, "Datos del empleado");

    const [,column1, column2] = createRowColumns(form, 2);

    let nss = setAttributes(new AonInput(),{
        id:"nss",
        name:"nss",
        description:"NSS/NAF (Opcional)",
        value: data.nss ? data.nss : ""
    });
    column1.appendChild(nss);

    let ipf = setAttributes(new AonInput(),{
        id: "ipf",
        name:"ipf",
        description: "DNI/NIE",
        value: data.ipf ? data.ipf : ""
    });
    column2.appendChild(ipf);

    const [,column3, column4] = createRowColumns(form, 2);

    let surname = setAttributes(new AonInput(),{
        id:"surname",
        name:"surname",
        description:"Apellido 1",
        value: data.surname ? data.surname : ""
    });
    column3.appendChild(surname);

    let lastSurname = setAttributes(new AonInput(),{
        id: "lastSurname",
        name:"lastSurname",
        description: "Apellido 2",
        value: data.lastSurname ? data.lastSurname : ""
    });
    column4.appendChild(lastSurname);

    const [,column5] = createRowColumns(form, 1);

    let name = setAttributes(new AonInput(),{
        id: "name",
        name:"name",
        description: "Nombre",
        value: data.name ? data.name : ""
    });
    column5.appendChild(name);

}

/**
 * 
 * @param {HTMLElement} form 
 * @param {Object} data 
 */
 const createDataContract = (form, data) => {
     
    createTitle(form, "Datos del Contrato");

    const [,column0, column] = createRowColumns(form, 2);

    let fra = setAttributes(new AonDate(),{ title: MSG.START_DATE, id:"fra", name:"fra"});
    column0.appendChild(fra);
    if(data.fra) fra.setDate(new Date(data.fra))

    let category = setAttributes(new AonInput(),{
        id:"category",
        name:"category",
        description:"Categoria profesional",
        value: data.category ? data.category : "",
    });
    column.appendChild(category);

    const [row1, column2, column3] = createRowColumns(form, 2);
    row1.style.margin = "10px 0";

    let duration = setAttributes(new AonSwitch(),{ title: "Duración indefinida", id:"duration", name:"duration", checked: data.duration == CONSTANT.FALSE ? CONSTANT.FALSE : CONSTANT.TRUE});
    column2.appendChild(duration);

    let frb = setAttributes(new AonDate(),{ title: MSG.END_DATE, id:"frb", name:"frb"});
    frb.style.display =  data.duration == CONSTANT.FALSE ? "block" : "none";
    column3.appendChild(frb);
    if(data.frb) frb.setDate(new Date(data.frb))

    duration.addEventListener(EVENT.CHANGE,({target})=>{
        frb.value = "";
        frb.style.display = target.checked ? "none": "block";
    });


    const [row2,column4, column5] = createRowColumns(form, 2);
    row2.style.margin = "10px 0";

    let fullTimeCheck = setAttributes(new AonSwitch(),{ title: "Jornada completa", id:"fullTimeCheck", name:"fullTimeCheck", checked: data.fullTimeCheck == CONSTANT.FALSE ? CONSTANT.FALSE : CONSTANT.TRUE});
    column4.appendChild(fullTimeCheck);

    let fullTime = setAttributes(new AonInput(),{
        id:"fullTime",
        name:"fullTime",
        description:"Tiempo",
        value: data.fullTime ? data.fullTime : "",
        visible: data.fullTimeCheck == CONSTANT.FALSE ? CONSTANT.TRUE : CONSTANT.FALSE
    });
    column5.appendChild(fullTime);

    fullTimeCheck.addEventListener(EVENT.CHANGE,({target})=>{
        fullTime.value = "";
        fullTime.visible = target.checked ? CONSTANT.FALSE:  CONSTANT.TRUE;
    });


    const [row3, column6, column7] = createRowColumns(form, 2);
    row3.style.margin = "10px 0";

    let salatyType = setAttributes(new AonSwitch(),{ title: "Salario s/convenio", id:"salatyType", name:"salatyType", checked: data.salatyType==CONSTANT.FALSE ? CONSTANT.FALSE : CONSTANT.TRUE});
    column6.appendChild(salatyType);

    let salary = setAttributes(new AonInput(),{
        id:"salary",
        name:"salary",
        description:"Salario",
        value: data.salary ? data.salary : "",
        visible: data.salatyType == CONSTANT.FALSE ? CONSTANT.TRUE : CONSTANT.FALSE
    });
    column7.appendChild(salary);

    salatyType.addEventListener(EVENT.CHANGE,({target})=>{
        salary.value = "";
        salary.visible = target.checked ? CONSTANT.FALSE : CONSTANT.TRUE;
    });
}

//-----------FILL
const fillCtaCti = (aonSelect, value) => {
    let options = [
        {value: "99999999999", name:"0111 - 99999999999", cccRegimeCode:'0111' }
    ];

    aonSelect.setOptions( options );
    if(value) aonSelect.value = value;
}

/**
 * 
 * @returns json form vacacion json
 */
 export const getFormMovJson = ()=>{
    const form = document.getElementById(MESSENGER_IDS.FORM_DINAMIC);
    if(form){
        const formSerialize = serializeForm(form);
        return { ...formSerialize };
    }
    return null;
}


/**
 * 
 * @param {HTMLElement} parent 
 * @param {String} text 
 */
const createTitle = (parent, text) => {
    const [row,column] = createRowColumns(parent, 1);
    row.style.margin = "10px 0";
    let title = setStyles(document.createElement(TAG.SPAN),{ fontSize: "16px", fontWeight:750, color:CSS.variable(COLORS.AON_DARK_GRAY) });
    title.innerHTML = text;
    column.appendChild(title);
} 

/**
 * 
 * @param {HTMLElement} parent appendchild 
 * @param {Number} columns totals columns
 * @returns Arrays{HMLElement} row and columns
 */
const createRowColumns = (parent, columns) => {
    const [row] = createRows(parent, 1);
    return [row, ...createColumns(row, columns)];
}


/**
 * 
 * @param {HTMLElement} parent appendchild 
 * @param {Number} columns totals rows
 * @returns Arrays{HMLElement} rows
 */
 const createRows = (parent, rows) => {
    let arr = [];
    for (let index = 0; index < rows; index++) {
        const row = setStyles(document.createElement(TAG.DIV),{ display:"flex" });
        parent.appendChild(row);
        arr.push(row);
    }
    return arr;
}

/**
 * 
 * @param {HTMLElement} parent appendchild 
 * @param {Number} columns totals columns
 * @returns Arrays{HMLElement} columns
 */
const createColumns = (parent, columns) => {
    let arr = [];
    for (let index = 0; index < columns; index++) {
        const column = setStyles(document.createElement(TAG.DIV),{ width:"100%"});
        if(index>0) column.style.paddingLeft = "5px";
        parent.appendChild(column);
        arr.push(column);
    }
    return arr;
} 