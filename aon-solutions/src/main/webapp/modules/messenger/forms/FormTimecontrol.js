import { AonDate } from "../../../components/aon-date.js";
import { AonInput } from "../../../components/aon-input.js";
import { AonSelect } from "../../../components/aon-select.js";
import { TAG, MSG, CSS, EVENT, CONSTANT } from "../../../environments/environments.js";
import { getStatus, getTimeControlDetail, saveTimeControlDetail } from "../../../services/timeControlService.js";
import { serializeForm, sortBy } from "../../../services/utils.js";
import { setAttributes } from "../../../services/utilsComponents.js";
import { firstLetters } from "../../timecontrol/time-control/utils.js";
import { AonDateUtils } from "../../utils/AonDateUtils.js";
import { MESSENGER_IDS, MESSENGER_VIEWS, TASK_STATUS } from "../MessengerEnums.js";
import { TaskCreationUtils } from "../utils/TaskCreationUtils.js";

/**
 * 
 * @param {Task} task 
 * @param {HTMLElement} card 
 */
 const createForm = (task, card) =>{

    card.flex = "true";

    const form = setAttributes(document.createElement(TAG.FORM),{ id:MESSENGER_IDS.FORM_DINAMIC, action:"#" });
    form.onsubmit = () => false;
    form.style.width = "100%";
    card.setContent(form);

    //-----------DATA FORM
    createDataForm(task, form);
    //-----------END DATA ENTERPRISE
}

/**
 * 
 * @returns json form vacacion json
 */
 const getFormJson = ()=>{
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
 * @param {HTMLElement} form 
 * @param {HTMLElement} aon-messenger-chat
 */
const createDataForm = (task, form) => {
    const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
    let data = task.getDescriptionJson();
    
    const taskHolderId = data.task_holder || task.myTaskHolder.id;

    let taskHolder = setAttributes(new AonInput(),{name:"task_holder", value: taskHolderId}) ;
    taskHolder.style.display = "none"; 
    form.appendChild( taskHolder );
   
    let times = setAttributes(new AonSelect(),{ title: "Seleccione registro a modificar", id:"timeId", name:"timeId"});
    TaskCreationUtils.createDivGrid(form, times, {classes:[CSS.AON_COL_XS_12]})

    fillTimeControl(times, data.timeId, taskHolderId);
    if(data.timeId)
        times.setDisabled(CONSTANT.TRUE);

    let date = setAttributes(new AonDate(),{ title: `Nueva ${MSG.DATE}`, id:"date", name:"date"});
    TaskCreationUtils.createDivGrid(form, date, {classes:[CSS.AON_COL_XS_6, CSS.AON_COL_MD_6]})
    if(data.date) date.setDate(new Date(data.date));

    let time = setAttributes(new AonInput(), {
        name:"time",
        id:"time",
        type:"time",
        description: `Nueva ${MSG.HOUR}`,
        value: data.time ?  data.time : ""
    });
    TaskCreationUtils.createDivGrid(form, time, {classes:[CSS.AON_COL_XS_6, CSS.AON_COL_MD_6]})

    if(!task.id) times.addEventListener(EVENT.CHANGE,({detail})=>{
        if(detail.date){
            date.value = AonDateUtils.formatDateOrigin(detail.date);
            time.value = AonDateUtils.setTime(detail.date);
        }
    });

    //OBSERVATION
    const observation = TaskCreationUtils.createDivEditable(undefined, MSG.OBSERVATION,  data.observation || "" , "observation" ,  MSG.TYPE_HERE);
    TaskCreationUtils.createDivGrid(form, observation, {classes:[CSS.AON_COL_XS_12]});

    let dur = aonMessengerChat.getDur();

    if(task.id && [TASK_STATUS.PENDING, TASK_STATUS.IN_PROGRESS].includes(task.status) && (dur.isTimecontrolManager() && aonMessengerChat.getDur().isTimecontrolPortal())){

        let btnAccept = TaskCreationUtils.createBtnAccept();
        btnAccept.addEventListener(EVENT.CLICK, ()=> processAccept(times.getDetail(), {date: date.value, time: time.value}, aonMessengerChat) );
         
        TaskCreationUtils.createDivGrid(form, btnAccept, {
            styles:{
                textAlign: "center"
            }
        });
    }

}

//-----------FILL
const fillTimeControl = (aonSelect, timeId, taskHolderId) => {
    let filter = { 
      startDate: AonDateUtils.formatDateOrigin( new Date().addDay(-7)),
      endDate:AonDateUtils.formatDateOrigin( new Date()),
      taskHolderId
    }
    getTimeControlDetail(filter).then(res=>{
        const options = sortBy(res, "date", "desc").map(r => ({...r, name: `${firstLetters(AonDateUtils.setDateTimestampDay(r.date))} - ${getStatus(r.status.toLowerCase()).name}` , value:r.id}));
        aonSelect.setOptions( options );
        if(timeId) aonSelect.value = timeId;
    });
}

const processAccept = async (tm,{date, time},aonMessengerChat) => {
    let application = aonMessengerChat.getApplication();
    application.startLoading();
    try {
        if(tm.id){
            let data = {
                ...tm, 
                task_holder:tm.task_holder.id,
                date: new Date( AonDateUtils.formatDateOrigin(date) + " " + time ).getTime()
            }
            
            if(tm.coordinates && tm.coordinates.latitude && tm.coordinates.longitude) {
                data.coordinates = tm.coordinates.latitude + "," + tm.coordinates.longitude;
            }

            await saveTimeControlDetail(data);
            await aonMessengerChat.updateTaskStatus(TASK_STATUS.FINISHED, `${MSG.REQUEST} tramitada`);
        } else {
            aonMessengerChat.showError({message:`Seleccione registro a modificar`, type:CONSTANT.ERROR});
        }
    } catch (err) {
        console.log(err);
        aonMessengerChat.showError(err)
    }
    application.stopLoading();
}


export const FormTimecontrol = {
    createForm,
    getFormJson
}