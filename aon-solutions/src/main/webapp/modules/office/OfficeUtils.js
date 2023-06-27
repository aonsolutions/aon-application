import { AonDate } from "../../components/aon-date.js";
import { AonSelect } from "../../components/aon-select.js";
import { AonSwitch } from "../../components/aon-switch.js";
import { MSG, EVENT } from "../../environments/environments.js";
import { Project } from "../../models/project/Project.js";
import { updateAllTargetItem } from "../../services/productService.js";
import { AonItemAdd } from "../registry/target/item/aon-item-add.js";
import { AonOfficeLinkSimpleList } from "./aon-office-link-simple-list.js";



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

/**
 * 
 * @param {AonOfficePanel} aonOfficePanel 
 */
const buildDialogProducts = (aonOfficePanel) => {
    const application = aonOfficePanel.getApplication();
    const dialog = application.getDialog();
    dialog.autoclose = false;
    dialog.width = '40%';
    dialog.clear();
    dialog.setTitle(MSG.ASSIGN+" "+MSG.PRODUCTS);

    let aonTargetItemAdd = new AonItemAdd();

    dialog.setContent(aonTargetItemAdd);
    
    dialog.addSendAction(async()=>{
        aonTargetItemAdd.setCustomers(aonOfficePanel.getCustomerSelected());

        application.startLoading();

        await aonTargetItemAdd
        .save()
        .catch(err=> aonOfficePanel.showError(err));
    
        dialog.close();
        application.stopLoading();

    }, MSG.SAVE);

    dialog.open();
}   
/**
 * 
 * @param {AonOfficePanel} aonOfficePanel 
 */
const buildDialogProductsUpdate = (aonOfficePanel) => {
    const application = aonOfficePanel.getApplication();
    const dialog = application.getDialog();
    dialog.autoclose = false;
    dialog.width = '40%';
    dialog.clear();
    dialog.setTitle(MSG.UPDATE+" "+MSG.PRODUCTS);

    let div = document.createElement("div");
    div.style.display = "flex";
    div.style.flexDirection = "column";
    div.style.width = "100%";
    dialog.setContent(div);

    let aonDate = new AonDate();
    aonDate.id = "aonDate1";
    aonDate.title = "A partir de las facturas con fecha:";
    div.appendChild(aonDate);

    let aonSwitch = new AonSwitch();
    aonSwitch.style.width= "100%";
    aonSwitch.id = "switchWeas";
    aonSwitch.title = "¿Desea inactivar los productos con fecha anterior?";
    div.appendChild(aonSwitch);
    
    dialog.addSendAction(async()=>{
        if (aonDate.getValue()){
            application.startLoading();
    
            await updateAllTargetItem({
              start_date: aonDate.getValue(),
              question: aonSwitch.isChecked()
            })
            .then(() => aonOfficePanel.showMessage())
            .catch((err) => aonOfficePanel.showError(err));
        
            dialog.close();
            application.stopLoading();
        }

    }, MSG.SAVE);

    dialog.open();
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
    buildDialogExpediente,
    buildDialogProducts,
    buildDialogProductsUpdate,
    getCustomerStatus,
    builDialogRelationship
}