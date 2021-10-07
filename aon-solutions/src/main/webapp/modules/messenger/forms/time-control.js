import { AonDate } from "../../../components/aon-date.js";
import { AonInput } from "../../../components/aon-input.js";
import { AonSelect } from "../../../components/aon-select.js";
import { TAG, MSG, CSS, EVENT, CONSTANT } from "../../../environments/environments.js";
import { getStatus, getTimeControlDetail, saveTimeControlDetail } from "../../../services/timeControlService.js";
import {  formatDateOrigin, serializeForm, setDateTimestampDay, setTime, sortBy } from "../../../services/utils.js";
import { newComponent, setAttributes } from "../../../services/utilsComponents.js";
import { firstLetters } from "../../signin/time-control/utils.js";
import { MESSENGER_IDS, TASK_STATUS } from "../MessengerEnums.js";
import { createDivEditable } from "../shared/creationUtils.js";


/**
 * 
 * @param {HTMLElement} card 
 */
 export const createFormTimeControl = (card, aonMessengerChat) =>{

    card.flex = "true";

    const form  = setAttributes(document.createElement(TAG.FORM),{ id:MESSENGER_IDS.FORM_DINAMIC, action:"#" });
    form.onsubmit = () => false;
    form.style.width = "100%";
    card.setContent(form);

    //-----------DATA FORM
    createDataForm(form, aonMessengerChat);
    //-----------END DATA ENTERPRISE

}

/**
 * 
 * @param {HTMLElement} form 
 * @param {HTMLElement} aon-messenger-chat
 */
const createDataForm = (form, aonMessengerChat) => {
    const task = aonMessengerChat.task;
    let data = task.id ? JSON.parse(task.description) : {};

    let times = setAttributes(new AonSelect(),{ title: "Seleccione registro a modificar", id:"timeId", name:"timeId"});
    createDiv(form, times, {classes:[CSS.AON_COL_XS_12]})
    fillTimeControl(times, data.timeId, task);
    if(task.id)
        times.setDisabled(CONSTANT.TRUE);

    let date = setAttributes(new AonDate(),{ title: MSG.DATE, id:"date", name:"date"});
    createDiv(form, date, {classes:[CSS.AON_COL_XS_6, CSS.AON_COL_MD_6]})
    if(data.date) date.setDate(new Date(data.date));
    else date.setDate(new Date());

    let time = setAttributes(new AonInput(), {
        name:"time",
        id:"time",
        type:"time",
        description:"Hora",
        value: data.time || setTime(new Date())
    });
    createDiv(form, time, {classes:[CSS.AON_COL_XS_6, CSS.AON_COL_MD_6]})

    if(!task.id) times.addEventListener(EVENT.CHANGE,({detail})=>{
        if(detail.date){
            date.value = formatDateOrigin(detail.date);
            time.value = setTime(detail.date);
        }
    });

    //OBSERVATION
    const observation = createDivEditable(undefined, MSG.OBSERVATION,  data.observation || "" , "observation" ,  MSG.TYPE_HERE);
    createDiv(form, observation, {classes:[CSS.AON_COL_XS_12]});

    let dur = aonMessengerChat.getDur();

    if(task.id && [TASK_STATUS.PENDING, TASK_STATUS.IN_PROGRESS].includes(task.status) && (dur.isTimecontrolManager() && aonMessengerChat.getDur().isTimecontrolPortal())){
        const btnSubmit = document.createElement(TAG.BUTTON);
        btnSubmit.className = CSS.AON_BUTTON;
        btnSubmit.style.marginTop = "15px";
        btnSubmit.textContent =  MSG.ACCEPT;
        btnSubmit.addEventListener(EVENT.CLICK, ()=>saveTimeControl(times.getDetail(), {date: date.value, time: time.value}, aonMessengerChat));
        createDiv(form, btnSubmit, {classes:[CSS.AON_COL_XS_12, CSS.AON_COL_XS_OFFSET_4]});
    }

}

//-----------FILL
const fillTimeControl = (aonSelect, timeId, task) => {
    let filter = { 
      startDate: formatDateOrigin( new Date().addDay(-7)),
      endDate:formatDateOrigin( new Date()),
      taskHolderId: task.myTaskHolder.id
    }
    getTimeControlDetail(filter).then(res=>{
        const options = sortBy(res, "date", "desc").map(r => ({...r, name: `${firstLetters(setDateTimestampDay(r.date))} - ${getStatus(r.status.toLowerCase()).name}` , value:r.id}));
        aonSelect.setOptions( options );

        if(timeId) aonSelect.value = timeId;
    });
}


/**
 * 
 * @returns json form vacacion json
 */
 export const getFormTimeJson = ()=>{
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
 * @param {HTMLElement} parent appenchild
 * @param {HTMLElement} child element add
 * @param {Object} properties 
 * @returns 
 */
const createDiv = (parent, child, properties)=> {

    const div = newComponent({ type: TAG.DIV, ...properties }).element;

    parent.appendChild(div);

    div.appendChild(child);

    return div;
}


const saveTimeControl = async (tm,{date, time},aonMessengerChat) => {
    aonMessengerChat.getApplication().startLoading();
    try {
        if(tm.id){
            let data = {
                ...tm, 
                task_holder:tm.task_holder.id,
                coordinates: tm.coordinates.latitude + "," + tm.coordinates.longitude,
                date: new Date( formatDateOrigin(date) + " " + time ).getTime()
            }
            await saveTimeControlDetail(data);
            await aonMessengerChat.updateTaskStatus(TASK_STATUS.FINISHED);
        }
    } catch (err) {
        console.log(err);
        aonMessengerChat.showError(err)
    }

    aonMessengerChat.getApplication().stopLoading();
}