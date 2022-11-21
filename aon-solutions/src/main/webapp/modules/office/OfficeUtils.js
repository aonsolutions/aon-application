import { AonSelect } from "../../components/aon-select.js";
import { MSG, EVENT, MATERIAL_ICONS } from "../../environments/environments.js";
import { Project } from "../../models/project/Project.js";
import { AonOfficeLinkSimpleList } from "./aon-office-link-simple-list.js";

/**
 * 
 * @param {AonOfficePanel} aonOfficePanel 
 * @param {HTMLElement} element 
 */
const buildDialogMenu = (aonOfficePanel, element) => {

    const application = aonOfficePanel.getApplication();
	let options = [
        { 
            name: "Vincular empresa", 
            value: "LINK",
            icon:  MATERIAL_ICONS.LINK, 
            fn:()=> {
                aonOfficePanel.onSaveRelationByCustomers(true);
            }
        },
        { 
            name: "Desvincular Empresa", 
            value: "UNLINK",
            icon: MATERIAL_ICONS.LINK_OFF, 
            fn:()=> {
                aonOfficePanel.onSaveRelationByCustomers(false);
            }
        },
        { 
            name: "Asignar expediente", 
            value: "assignedExpediente",
            icon: MATERIAL_ICONS.OPEN_IN_NEW, 
            fn:()=> {
                buildDialogExpediente(aonOfficePanel);
            }
        }
    ];
    

    const top = element.getBoundingClientRect().top + 24;
    const left = element.getBoundingClientRect().left + 3;
    let d = application.getOptionDialog();
    d.setMenuOptions(options, top, left);
    d.open();
}   


/**
 * 
 * @param {AonOfficePanel} aonOfficePanel 
 */
const buildDialogExpediente = (aonOfficePanel) => {

    let project = new Project();

    const application = aonOfficePanel.getApplication();
    const dialog = application.getDialog();
    dialog.autoclose = false;
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



/**
 * 
 * @param {AonOfficePanel} aonOfficePanel 
 */
const builDialogRelationship = (aonOfficePanel, data) => {
    const application = aonOfficePanel.getApplication();
    const dialog = application.getDialog();
    dialog.autoclose = false;
    dialog.width = '60%';
    dialog.clear();
    dialog.setTitle("Vinculaciones");

    let simpleList = new AonOfficeLinkSimpleList();
    simpleList.setData(data);

    dialog.setContent(simpleList);
    
    dialog.addAcceptAction(()=>{});

    dialog.open();
}   

export const OfficeUtils = {
    buildDialogMenu,
    getCustomerStatus,
    builDialogRelationship
}