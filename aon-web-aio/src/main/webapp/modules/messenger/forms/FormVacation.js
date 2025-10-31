
import { AonBasicTable } from "../../../components/aon-basic-table.js";
import { AonNewDate } from "../../../components/aon-new-date.js";
import { AonIconButton } from "../../../components/aon-icon-button.js";
import { TAG, EVENT, MSG, MATERIAL_ICONS } from "../../../environments/environments.js";
import { saveVacation } from "../../../services/contractService.js";
import { serializeForm } from "../../../services/utils.js";
import { setAttributes } from "../../../services/utilsComponents.js";
import { AonDateUtils } from "../../utils/AonDateUtils.js";
import { MESSENGER_IDS, MESSENGER_VIEWS, TASK_STATUS } from "../MessengerEnums.js";
import { TaskCreationUtils } from "../utils/TaskCreationUtils.js";

/**
 * 
 * @param {HTMLElement} card 
 * @param {Task} task 
 */
 const createForm = (task, card) =>{
    const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
    const dur = aonMessengerChat.getDur();

    let data = task.id ? JSON.parse(task.description) : {};

    const form  = setAttributes(document.createElement(TAG.FORM),{
        id:MESSENGER_IDS.FORM_DINAMIC,
        action:"#"
    });
    form.onsubmit = () => false;
    form.style.width = "100%";
    card.setContent(form);

    let table = setAttributes(new AonBasicTable(),{ id:"tableVacation" });
    form.appendChild(table);
 
    const div = document.createElement(TAG.DIV);
    form.appendChild(div);
    
    let i = 0;
    if(data.dates && data.dates.length){
        data.dates.forEach(dt=> addDates(table, i++, dt) );
    } else {
        addDates(table, i++);
    }

    //ADD BUTTON 
    let addButton  = setAttributes(new AonIconButton(),{
        id:"addButton",
        title:MSG.ADD_DETAIL,
        icon:MATERIAL_ICONS.ADD
    });
    addButton.addEventListener(EVENT.CLICK, () => addDates(table,i++) );
    div.appendChild(addButton);

    //OBSERVATION
    TaskCreationUtils.createDivEditable(form, MSG.OBSERVATION,  data.observation || "" , "observation" ,  MSG.TYPE_HERE);

    if(task.id && [TASK_STATUS.PENDING, TASK_STATUS.IN_PROGRESS].includes(task.status) && (dur.isPayrollManager() || dur.isPayrollPortal()) ){
        let btnAccept = TaskCreationUtils.createBtnAccept();
        btnAccept.addEventListener(EVENT.CLICK, ()=> processAccept(task) );
           
        TaskCreationUtils.createDivGrid(form, btnAccept, {
            styles:{
                textAlign: "center"
            }
        });
    }
}

/**
 * 
 * @returns json form vacacion json
 */
 const getFormJson = ()=>{
    const form = document.getElementById(MESSENGER_IDS.FORM_DINAMIC);


console.log('Vacation (getFormJson) *****')
console.log(form)

if(form){
    let observation = form.querySelector("#observation textarea")?.value.trim() ?? '';
    console.log(observation)
        //DATES
        let dates = [];
        [...form.querySelectorAll("table tr")].map(tr=>{
            const startDate = tr.querySelector("[id*=startDate]");
            const endDate = tr.querySelector("[id*=endDate]");
            if(startDate && endDate && startDate.date && endDate.date){
                dates.push({startDate: startDate.getDateValue(), endDate: endDate.getDateValue()});
            }
        })

        const formSerialize = serializeForm(form);
        return { ...formSerialize, dates, observation};
    }
    return null;
}


/**
 * 
 * @param {HTMLElement} table html table
 * @param {Number} i row numeric
 * @param {Object} data data object default
 */
const addDates = (table, i, data={}) =>{
    const rowIndex = table.addRow(); // ----- RETURN ROW INDEX
    
    //DATE INI
    let startDate = setAttributes(new AonNewDate(),{
        id:"startDate" + i,
        title:MSG.START_DATE,
    });

    table.addCell(startDate);
    const startDateDate = data.startDate || new Date();
    startDate.setDate(startDateDate);

    //DATE END
    let endDate = setAttributes(new AonNewDate(),{
        id: "endDate" + i,
        title:MSG.END_DATE
    });

    table.addCell(endDate);
    if(data.endDate){
        const endtDateDate = data.endDate;
        endDate.setDate(endtDateDate);
    }

    // ----- BUTTON DELETE
    let dataDelete = setAttributes(new AonIconButton(),{
        id:"Delete" + i,
        title:MSG.DELETE,
        icon:MATERIAL_ICONS.REMOVE_CIRCLE
    });

    dataDelete.addEventListener(EVENT.CLICK, () => {
        if(table.getRowsCount() > 1) {
            table.removeRow(rowIndex);
        }
    });
    table.addCell(dataDelete);
}

const processAccept = async (task) => {
    const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
    const application = aonMessengerChat.getApplication();
    application.startLoading();
    try {
        const registry = task.sender.id; 
        const params = {...getFormJson(), registry};
   
        await saveVacation(params);
        await aonMessengerChat.updateTaskStatus(TASK_STATUS.FINISHED, `${MSG.REQUEST} tramitada`);
    } catch (err) {
        console.log(err);
        aonMessengerChat.showError(err)
    }

    application.stopLoading();
}


export const FormVacation = {
    createForm,
    getFormJson
}