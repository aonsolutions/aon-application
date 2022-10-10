import { AonDate } from "../../components/aon-date.js";
import { AonSelect } from "../../components/aon-select.js";
import { MSG, EVENT } from "../../environments/environments.js";
import { AonDateUtils } from "../utils/AonDateUtils.js";

/**
 * 
 * @param {AonHolderSimpleList} parent 
 */
const buildDialogHolder = (parent, holder) => {

    const application = parent.getApplication();
    const dialog = application.getDialog();

    dialog.width = '40%';
    
    dialog.clear();
    dialog.setTitle("Asignar responsables");

    let div = document.createElement('div');
    div.style.display = "flex";
    div.style.flexDirection = "column";
        
    dialog.setContent(div);

    buildFormExpediente(parent, div, holder);
    
    dialog.addSendAction(async()=>{

        application.startLoading();

        await parent
        .onSaveHolder(holder)
        .catch(err=> parent.showError(err));
    
        dialog.close();

        application.stopLoading();

    }, MSG.SAVE);

    dialog.open();
}   

const buildFormExpediente = (parent, div, holder) => {

    const idRandom = Math.floor(Math.random() * 10000000) + 1;

    let workgroup = new AonSelect();
    workgroup.title = MSG.WORKGROUP;
    workgroup.autocomplete = true;
    workgroup.id = "workgroup2"+idRandom;
    workgroup.default = true;
    div.appendChild(workgroup);

    let taskHolder = new AonSelect();
    taskHolder.title = "Asignar a";
    taskHolder.autocomplete = true;
    taskHolder.id = "taskHolder2"+idRandom;
    taskHolder.default = true;
    div.appendChild(taskHolder);

    let startDate = new AonDate();
    startDate.id = "date2"+idRandom;
    startDate.title = "Desde"; 
    startDate.addEventListener(EVENT.CHANGE, () => {
        holder.setStartDate(startDate.value);
    });
    div.appendChild(startDate);

    
    if(holder.getStartDate()){
        startDate.value = AonDateUtils.formatDateOrigin(holder.getStartDate());
    }

    parent.getWorkgroups().then(wgs=>{
        workgroup.setOptions(wgs);

        const value = holder.getWorkgroup().getId();

        if(value){
            workgroup.value = value;
        }

        workgroup.addEventListener(EVENT.CHANGE, () => holder.setWorkgroup(workgroup.getDetail()));
    });

    parent.getTaskHolders().then(ths=>{
        taskHolder.setOptions(ths);

        const value = holder.getTaskHolder().getId();

        if(value){
            taskHolder.value = value;
        }

        taskHolder.addEventListener(EVENT.CHANGE, () => holder.setTaskHolder(taskHolder.getDetail()));
    });
}



export const ProjectUtils = {
    buildDialogHolder
}