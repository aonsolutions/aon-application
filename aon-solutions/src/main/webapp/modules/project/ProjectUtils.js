import { AonDate } from "../../components/aon-date.js";
import { AonInput } from "../../components/aon-input.js";
import { AonSelect } from "../../components/aon-select.js";
import { MSG, EVENT } from "../../environments/environments.js";
import { ProjectHolder } from "../../models/project/ProjectHolder.js";
import { ProjectType } from "../../models/project/ProjectType.js";
import { deleteProjectType, saveProjectType } from "../../services/projectService.js";
import { AonDateUtils } from "../utils/AonDateUtils.js";



/**
 * 
 * @param {AonProjectList} parent 
 */
 const buildDialogProject = (parent, project) => {

    const application = parent.getApplication();
    const dialog = application.getDialog();

    dialog.width = '40%';
    
    dialog.clear();
    dialog.setTitle(MSG.ADD+" expediente");

    let div = document.createElement('div');
    div.style.display = "flex";
    div.style.flexDirection = "column";
        
    dialog.setContent(div);

    buildFormProject(parent, div, project);
    
    dialog.addSendAction(async()=>{

        await parent.onSaveProject(project)

        dialog.close();

    }, MSG.SAVE);

    dialog.open();
}   

/**
 * 
 * @param {AonProjectList} parent 
 * @param {HTMLElement} div 
 * @param {Project} project
 */
const buildFormProject = (parent, div, project) => {
    const projectHolder = project.getProjectHolder();

    const idRandom = Math.floor(Math.random() * 10000000) + 1;

    let type = new AonSelect();
    type.title = MSG.TYPE;
    type.autocomplete = true;
    type.id = "projectType2"+idRandom;
    div.appendChild(type);
    
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
    taskHolder.multiple = true;
    div.appendChild(taskHolder);

    parent.getProjectTypes().
    then(types=>{
        const typeId = project.getType().getId() || 0;

        type.setOptions(types);

        if(typeId){
            type.value = typeId;
        } 

        type.addEventListener(EVENT.CHANGE, () => {
    
            const detail = type.getDetail();
            project.setType(detail);

            project.setName(detail.description);

        });

        if(!typeId && types.length===1){
            type.setIndexOf(0);
        }
    });

    parent.getWorkgroups().then(wgs=>{
        const workgroupId = projectHolder.getWorkgroup().getId();

        workgroup.setOptions(wgs);

        if(workgroupId){
            workgroup.value = workgroupId;
        } 

        workgroup.addEventListener(EVENT.CHANGE, () => projectHolder.setWorkgroup(workgroup.getDetail()));
    });


    parent.getTaskHolders().then(ths=>{
        taskHolder.setOptions(ths);

        taskHolder.addEventListener(EVENT.SELECT, ({detail}) => {
            let projectHolders = (detail || []).map(taskHolder => new ProjectHolder({taskHolder}));
            project.setProjectHolders(projectHolders);
        });
    });

}

/**
 * 
 * @param {AonHolderSimpleList} parent 
 */
const buildDialogHolder = (parent, holder) => {

    const application = parent.getApplication();
    const dialog = application.getDialog();

    dialog.width = '40%';
    
    dialog.clear();
    dialog.setTitle(MSG.ADD+" "+MSG.ADVISER);

    let div = document.createElement('div');
    div.style.display = "flex";
    div.style.flexDirection = "column";
        
    dialog.setContent(div);

    buildFormHolder(parent, div, holder);
    
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

const buildFormHolder = (parent, div, holder) => {

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


const buildDialogProjectType = (parent, type) => {
    let projectType = new ProjectType(type);
    const isEdit = projectType.getId();
    const application = parent.getApplication();
    const d = application.getDialog();
    d.clear();
    if(!parent.isMobile()) {
        d.width = '400px';
    }
    d.setTitle(isEdit ? MSG.EDIT : MSG.ADD);

    let aonInput = new AonInput();
    aonInput.id = "eeeInputType";
    aonInput.description = MSG.TYPE;
    if(projectType.getDescription()) {
        aonInput.value = projectType.getDescription();
    }

    d.setContent(aonInput);
    d.addAcceptAction(() => {
        if(aonInput.value){
            projectType.setDescription(aonInput.value);
            projectType.setDirty(true);
            saveProjectType(projectType).then(() => {
                parent.showMessage();
                parent.loadProjectType();
            }).catch(err=>{
                parent.showError(err);
            });
        }
    });
    d.open();
}


const projectTypeDelete = (parent, type) => {
    let application = parent.getApplication();
    application.confirmDialog(MSG.DELETE, MSG.DELETE_CONFIRM, async()=>{
        application.startLoading();
        try {
          await deleteProjectType(type);
          parent.showToast({ message: MSG.DELETED_DATA });
          parent.loadProjectType();
        } catch (error) {
          parent.showToast(error);
        }
      application.stopLoading();
    });
}

export const ProjectUtils = {
    buildDialogProject,
    buildDialogHolder,
    buildDialogProjectType,
    projectTypeDelete
}