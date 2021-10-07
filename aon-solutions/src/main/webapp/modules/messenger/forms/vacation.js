
import { AonBasicTable } from "../../../components/aon-basic-table.js";
import { AonDate } from "../../../components/aon-date.js";
import { AonIconButton } from "../../../components/aon-icon-button.js";
import { AonSelect } from "../../../components/aon-select.js";
import { TAG, EVENT, MSG, MATERIAL_ICONS } from "../../../environments/environments.js";
import { formatDateOrigin, serializeForm } from "../../../services/utils.js";
import { setAttributes } from "../../../services/utilsComponents.js";
import { MESSENGER_IDS } from "../MessengerEnums.js";
import { createDivEditable } from "../shared/creationUtils.js";

/**
 * 
 * @param {HTMLElement} card 
 */
 export const createFormVacation = (card, aonMessengerChat) =>{
    const task = aonMessengerChat.task;
    
    let data = task.id ? JSON.parse(task.description) : {};

    const form  = setAttributes(document.createElement(TAG.FORM),{
        id:MESSENGER_IDS.FORM_DINAMIC,
        action:"#"
    });
    form.onsubmit = () => false;
    form.style.width = "100%";
    card.setContent(form);

    if(task.id){
        let aonSelect = setAttributes(new AonSelect(),{ title: "Estado", id:"status", name:"status"});
        form.appendChild(aonSelect);
        aonSelect.options = JSON.stringify(getStatus());
        if(data.status) aonSelect.value = data.status;
    }


    let table = setAttributes(new AonBasicTable(),{ id:"tableVacation" });
    form.appendChild(table);
 
    const div = document.createElement(TAG.DIV);
    form.appendChild(div);
    
    let i = 0;
    if(data.dates && data.dates.length){
        data.dates.forEach(dt=> addDates(table, dt, i++) );
    } else {
        addDates(table, undefined, i++);
    }

    //ADD BUTTON 
    let addButton  = setAttributes(new AonIconButton(),{
        id:"addButton",
        title:MSG.ADD_DETAIL,
        icon:MATERIAL_ICONS.ADD
    });
    addButton.addEventListener(EVENT.CLICK, () => addDates(table, undefined, i++) );
    div.appendChild(addButton);

    //OBSERVATION
    createDivEditable(form, MSG.OBSERVATION,  data.observatio0n || "" , "observation" ,  MSG.TYPE_HERE);
}

/**
 * 
 * @param {HTMLElement} table html table
 * @param {Object} data data object default
 * @param {Number} i row numericw
 */
const addDates = (table, data={}, i) =>{
    const rowIndex = table.addRow(); // ----- RETURN ROW INDEX
    
    //DATE INI
    let startDate = setAttributes(new AonDate(),{
        id:"startDate" + i,
        title:MSG.START_DATE,
    });

    table.addCell(startDate);
    startDate.value = data.startDate || formatDateOrigin(new Date());

    //DATE END
    let endDate = setAttributes(new AonDate(),{
        id: "endDate" + i,
        title:MSG.END_DATE
    });

    table.addCell(endDate);
    if(data.endDate) endDate.value = data.endDate;

    // ----- BUTTON DELETE
    let dataDelete = setAttributes(new AonIconButton(),{
        id:"Delete" + i,
        title:MSG.DELETE,
        icon:MATERIAL_ICONS.REMOVE_CIRCLE
    });

    dataDelete.addEventListener(EVENT.CLICK, () => {
        if(table.getRowsCount() > 1) table.removeRow(rowIndex);
    });
    table.addCell(dataDelete);
}

/**
 * 
 * @returns json form vacacion json
 */
export const getFormVacationJson = ()=>{
    const form = document.getElementById(MESSENGER_IDS.FORM_DINAMIC);
    if(form){
        let observation = form.querySelector("#observation").innerText;
        //DATES
        let dates = [];
        [...form.querySelectorAll("table tr")].map(tr=>{
            let startDate = tr.querySelector("[id*=startDate]");
            let endDate = tr.querySelector("[id*=endDate]");
            if(startDate && endDate && startDate.value && endDate.value)
                dates.push({startDate: startDate.value, endDate: endDate.value});
        })

        const formSerialize = serializeForm(form);
        return { ...formSerialize, dates, observation};
    }
    return null;
}


const getStatus = () => [
    {
        name:MSG.ACCEPT,
        value: 1
    },
    {
        name:MSG.REJECT,
        value: 2
    }
];