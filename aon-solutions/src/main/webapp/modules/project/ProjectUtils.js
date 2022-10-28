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
 * @param {Project} project 
 */
 const buildDialogProject = (parent, project) => {
    const application = parent.getApplication();
    const dialog = application.getDialog();
    dialog.clear();

    if(parent.isMobile()) {
        dialog.type = "fullscreen";
    } else  {
        dialog.width = '40%';
    }

    dialog.autoclose = false;

    const title = (project && project.id ? MSG.EDIT : MSG.ADD)+" expediente";
    dialog.setTitle(title);

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

    parent.getProjectTypes().then(types=>{
        type.setOptions(types);

        const typeId = project.getType().getId() || 0;
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
        workgroup.setOptions(wgs);

        const workgroupId = projectHolder.getWorkgroup().getId();
        if(workgroupId){
            workgroup.value = workgroupId;
        } 
        workgroup.addEventListener(EVENT.CHANGE, () =>{
            projectHolder.setWorkgroup(workgroup.getDetail());

            onChangeTaskHolder(project, workgroup, taskHolder.getSelectable())
        });
    });
 
    parent.getTaskHolders().then(ths=>{
        let options = ths.filter(th => !isRepeatTaskHolder(project, th));
        taskHolder.setOptions(options);
        taskHolder.addEventListener(EVENT.SELECT, () => {
            onChangeTaskHolder(project, workgroup, taskHolder.getSelectable())
        });
    });
}

/**
 * 
 * @param {Project} project 
 * @param {HTMLElement} selectWorkgroup 
 * @param {Array} selectable 
 */
const onChangeTaskHolder = (project, selectWorkgroup, selectable) => {
    const projectHolder = project.getProjectHolder();

    let workgroup = null;
    if(selectWorkgroup.getDetail() && selectWorkgroup.getDetail().id){
        workgroup = selectWorkgroup.getDetail();
    } else if(projectHolder.getWorkgroup().getId()){
        workgroup = projectHolder.getWorkgroup();
    }

    let projectHolders = (selectable || []).map(taskHolder => new ProjectHolder({taskHolder, workgroup}));
    project.setProjectHolders(projectHolders);
}

/**
 * 
 * @param {AonHolderSimpleList} parent 
 * @param {ProjectHolder} holder 
 */
const buildDialogHolder = (parent, holder) => {

    const application = parent.getApplication();
    const dialog = application.getDialog();
    dialog.clear();

    if(parent.isMobile()) {
        dialog.type = "fullscreen";
    } else  {
        dialog.width = '40%';
    }

    const title = holder && holder.id ? MSG.EDIT : MSG.ADD;

    dialog.setTitle(title);

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

/**
 * 
 * @param {AonHolderSimpleList} parent 
 * @param {HTMLElement} div 
 * @param {ProjectHolder} holder 
 */
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

    let datesDiv = document.createElement("div");
    datesDiv.style.display   = "flex";
    datesDiv.style.columnGap = "10px";
    div.appendChild(datesDiv);

    let startDate = new AonDate();
    startDate.id = "date2startDate"+idRandom;
    startDate.title = "Desde"; 
    startDate.style.width = "50%";
    startDate.addEventListener(EVENT.CHANGE, () => {
        holder.setStartDate(startDate.value);
    });
    datesDiv.appendChild(startDate);

    if(holder.getStartDate()){
        startDate.value = AonDateUtils.formatDateOrigin(holder.getStartDate());
    }

    let endDate = new AonDate();
    endDate.id = "date2EndDate"+idRandom;
    endDate.title = `Hasta (${MSG.OPTIONAL})`; 
    endDate.style.width = "50%";
    endDate.addEventListener(EVENT.CHANGE, () => {
        holder.setEndDate(endDate.value);
    });
    datesDiv.appendChild(endDate);
    
    if(holder.getEndDate()){
        endDate.value = AonDateUtils.formatDateOrigin(holder.getEndDate());
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
    const dialog = application.getDialog();
    dialog.clear();

    if(parent.isMobile()) {
        dialog.type = "fullscreen";
    } else  {
        dialog.width = '40%';
    }
    
    dialog.setTitle(isEdit ? MSG.EDIT : MSG.ADD);

    let aonInput = new AonInput();
    aonInput.id = "eeeInputType";
    aonInput.description = MSG.TYPE;
    if(projectType.getDescription()) {
        aonInput.value = projectType.getDescription();
    }

    dialog.setContent(aonInput);
    dialog.addAcceptAction(() => {
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
    dialog.open();
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

const isRepeatTaskHolder = (project, th) => {
    return project.getProjectHolders().some(holder => holder.taskHolder && holder.taskHolder.id && holder.taskHolder.id == th.id);
}

export const ProjectUtils = {
    buildDialogProject,
    buildDialogHolder,
    buildDialogProjectType,
    projectTypeDelete
}