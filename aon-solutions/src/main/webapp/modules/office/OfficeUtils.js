import { AonSelect } from "../../components/aon-select.js";
import { MSG, EVENT } from "../../environments/environments.js";
import { Project } from "../../models/project/Project.js";

/**
 * 
 * @param {AonOfficePanel} aonOfficePanel 
 */
const buildDialog = (aonOfficePanel) => {

    const application = aonOfficePanel.getApplication();
    const dialog = application.getDialog();

    let project = new Project();

    dialog.width = '40%';
    
    dialog.clear();
    dialog.setTitle("Asignar expediente");
        
    dialog.setContent(buildFormExpediente(aonOfficePanel, project));
    
    dialog.addSendAction(async()=>{

        application.startLoading();

        await aonOfficePanel
        .onSaveExpedientes(project)
        .catch(err=> aonOfficePanel.showError(err));
    
        dialog.close();

        application.stopLoading();

 
    }, MSG.SAVE);

    dialog.open();
}   

const buildFormExpediente = (aonOfficePanel, project) => {


    const idRandom = Math.floor(Math.random() * 10000000) + 1;

    let div = document.createElement('div');
    div.style.display = "flex";
    div.style.flexDirection = "column";

    let type = new AonSelect();
    type.title =  MSG.TYPE;
    type.autocomplete = true;
    type.id = "projectType1"+idRandom;
    div.appendChild(type);

    let workgroup = new AonSelect();
    workgroup.title = MSG.WORKGROUP;
    workgroup.autocomplete = true;
    workgroup.id = "workgroup1"+idRandom;
    workgroup.default = true;
    div.appendChild(workgroup);

        
    let taskHolder = new AonSelect();
    taskHolder.title = "Asignar a";
    taskHolder.autocomplete = true;
    taskHolder.id = "taskHolder1"+idRandom;
    taskHolder.default = true;
    div.appendChild(taskHolder);

    aonOfficePanel.getProjectTypes().
    then(types=>{

        let options = types;

        type.setOptions(options);

        type.addEventListener(EVENT.CHANGE, () => {
    
            const detail = type.getDetail();
            project.setType(detail);

            project.setName(detail.description);

        });

    });

    aonOfficePanel.getWorkgroups().then(wgs=>{
        let options = wgs;
        
        workgroup.setOptions(options);

        workgroup.addEventListener(EVENT.CHANGE, () => project.getProjectHolder().setWorkgroup(workgroup.getDetail()));
    });

    aonOfficePanel.getTaskHolders().then(ths=>{
        taskHolder.setOptions(ths);

        taskHolder.addEventListener(EVENT.CHANGE, () => project.getProjectHolder().setTaskHolder(taskHolder.getDetail()));
    });

    return div;
}


const getCustomerStatus = (detail) => {
    let status = [];

    if(detail){
        if(detail.active == "true"){
            status.push("ACTIVE");
        }
    
        if(detail.inactive == "true"){
            status.push("INACTIVE");
        }
    
        if(detail.blocked == "true"){
            status.push("BLOCKED");
        }
    }

    return status.length > 0 ? status : ["ACTIVE", "BLOCKED"];
}

export const OfficeUtils = {
    buildDialog,
    getCustomerStatus
}