import { AonDate } from "../../components/aon-date.js";
import { AonInput } from "../../components/aon-input.js";
import { AonSelect } from "../../components/aon-select.js";
import { MSG, EVENT } from "../../environments/environments.js";
import { ProjectHolder } from "../../models/project/ProjectHolder.js";
import { ProjectType } from "../../models/project/ProjectType.js";
import { ActivityType } from "../../models/ActivityType.js";
import { deleteProjectType, saveActivityType, saveProjectType } from "../../services/projectService.js";
import { AonDateUtils } from "../utils/AonDateUtils.js";
import { AonCheckbox } from "../../components/aon-checkbox.js";

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

    const title = (project && project.id ? MSG.EDIT : MSG.ASSIGN)+" expediente";
    dialog.setTitle(title);

    let div = document.createElement('div');
    div.style.display = "flex";
    div.style.flexDirection = "column";
    dialog.setContent(div);

    buildFormProject(parent, project, div);

    dialog.addSendAction(async()=>{

        await parent.onSaveProject(project)

        dialog.close();

    }, MSG.SAVE);

    dialog.open();
}   

/**
 * 
 * @param {AonProjectList} parent 
 * @param {Project} project
 * @param {HTMLElement} div 
 */
const buildFormProject = (parent, project, div) => {
    const projectHolder = project.getProjectHolder();

    const idRandom = Math.floor(Math.random() * 10000000) + 1;

    let projectType = new AonSelect();
    projectType.title = MSG.TYPE;
    projectType.autocomplete = true;
    projectType.id = "projectType2"+idRandom;
    div.appendChild(projectType);

    let activityType = new AonSelect();
    activityType.title = MSG.ACTIVITY;
    activityType.autocomplete = true;
    activityType.id = "activityType2"+idRandom;
    activityType.default = true;
    activityType.multiple = true;
    div.appendChild(activityType);

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

    Promise.all([
        parent.getProjectTypes(),
        parent.getActivitiesType()
    ])
    .then(([types, activitiesType])=>{
        projectType.setOptions(types);
        activityType.setOptions(activitiesType);


        //CHECKBOX SELECTED VALUE DEFAULT
        project.getProjectActivities()
        .filter(d=> d.activityType && d.activityType.id)
        .forEach((d)=> activityType.addSelectableByValue(d.activityType.id));

        projectType.addEventListener(EVENT.CHANGE, () => {
            const detail = projectType.getDetail();
            if(detail){
                project.setType(detail);
                project.setName(detail.description);
                activityType.setOptions(activitiesType.filter(a => !a.projectType || (a.projectType == projectType.value)));
            }
        });

        activityType.addEventListener(EVENT.SELECT, ({detail}) => {
            let projectActivities = (activityType.getSelectable() || [])
            .map(a=> ({activityType:a}));
            
            if(detail && detail.option){
                if(detail.add){
                    project.addProjectActivity({activityType:detail.option});
                } else {
                    project.removeProjectActivity({activityType:detail.option});
                }
            } else {
                project.setProjectActivities(projectActivities);
            }
        });

        const typeId = project.getType().getId() || 0;
        if(typeId){
            projectType.value = typeId;
        } else if(types.length===1){
            projectType.setIndexOf(0);
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


/**
 * 
 * @param {HTMLElement} parent 
 * @param {ProjectHolder} type 
 */
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

/**
 * 
 * @param {HTMLElement} parent 
 * @param {ActivityType} type 
 * @param {number} projectType 
 */
const buildDialogActivityType = (parent, type=null, projectTypeId=null) => {
    const application = parent.getApplication();
    const dialog = application.getDialog();
    dialog.clear();


    let activityType = new ActivityType(type);
    activityType.setProjectType(projectTypeId);

    dialog.setTitle(activityType.getId() ? MSG.EDIT : MSG.ADD);

    if(parent.isMobile()) {
        dialog.type = "fullscreen";
    } else  {
        dialog.width = '40%';
    }

    let div = document.createElement("div");
    dialog.setContent(div);

    let aonInput = new AonInput();
    aonInput.id = "activityType98";
    aonInput.description = MSG.ACTIVITY;
    if(activityType.getDescription()) {
        aonInput.value = activityType.getDescription();
    }
    div.appendChild(aonInput);

    let aonCheckbox = new AonCheckbox();
    aonCheckbox.id = "checkboxProjecType";
    aonCheckbox.checked = projectTypeId ? true : false;
    aonCheckbox.description = "Vincular actividad con:"
    div.appendChild(aonCheckbox);

    let projectType = new AonSelect();
    projectType.title = "Tipo de proyecto";
    projectType.autocomplete = true;
    projectType.id = "projectType72";
    projectType.default = true;
    projectType.style.display = projectTypeId ? "block" : "none";
    div.appendChild(projectType);

    aonCheckbox.addEventListener(EVENT.CHANGE, () => {
        let check = aonCheckbox.isChecked(); 
        projectType.style.display = check ? "block" : "none";
        
        if(!check){
            activityType.setProjectType(null);
        }
    });


    parent.getProjectTypes().then(types=>{
        projectType.setOptions(types);

        const typeId = activityType.getProjectType();
        if(typeId){
            projectType.value = typeId;
        } 

        projectType.addEventListener(EVENT.CHANGE, () => {
            if(projectType.value){
                activityType.setProjectType(projectType.value);
            }
        });

        if(!typeId && types.length===1){
            projectType.setIndexOf(0);
        }
    });

    dialog.addAcceptAction(() => {
        if(aonInput.value){
            activityType.setDescription(aonInput.value);
            activityType.setDirty(true);
            saveActivityType(activityType)
            .then(() => {
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
    buildDialogActivityType,
    projectTypeDelete
}