import {  EVENT, MSG } from "../../../environments/environments.js";
import { getProjects} from "../../../services/projectService.js";
import { getCustomer, getCustomers } from "../../../services/registryService.js";
import { getTaskProcess, getTaskTags } from "../../../services/taskService.js";
import { sortBy, waitEl } from "../../../services/utils.js";
import { getAppsByDur } from "../../../services/app.js";
import { MESSENGER_DIRECTION, MESSENGER_IDS, MESSENGER_VIEWS, TAG_TYPE, TASK_SOURCE, WORKFLOW_TYPES } from "../MessengerEnums.js";
import { TaskCreationUtils} from "./TaskCreationUtils.js";
import { chooseIconMessage } from "./utils.js";

/**
 * fill typeRequest (Tipo de solicitud)
 * @param {Task} task
 */
const fillRequestType = (task) => {
    const aonSelect = document.getElementById(MESSENGER_IDS.SOURCE_TASK);
    const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);

    let source = task.getSource();

    let sources = [
        {value: TASK_SOURCE.QUERY, name: MSG[TASK_SOURCE.QUERY.toUpperCase()] }
    ];

    if(aonMessengerChat.getDur().hasCallCenter() || (source && source == TASK_SOURCE.CAU) ){
        sources.push({value: TASK_SOURCE.CAU, name: "Call Center" });
        
        sources.push({value: TASK_SOURCE.GROUPED, name: MSG.GROUPED });

        if(!task.getId() && aonMessengerChat.isSig()){
            source = TASK_SOURCE.CAU;
        }
    }
    
    if(!aonMessengerChat.isCau()){
        sources.push({value: TASK_SOURCE.REQUEST, name: MSG.FORMALITIES });
        
        sources.push({value: TASK_SOURCE.TASK, name: MSG.TASK });
    }

    if(TASK_SOURCE.MANUAL === source){
        sources.unshift({value: TASK_SOURCE.MANUAL, name: "MANUAL" }); 
    }
    
    aonSelect.setOptions(sources);

    if(source){
        aonSelect.value = source;
    }
}

const fillAdvisory = async (task) => {
    const aonSelect = document.getElementById(MESSENGER_IDS.ADVISORY_TASK);
    const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
    if(aonSelect){
        aonSelect.loading(true);
        const domain = task.getDomain();
        try {
            let offices = await aonMessengerChat.getOfficeProjects();

            aonSelect.setOptions(offices.map(office => ({...office, value:office.domain.id, name:office.domain.description})));
            aonSelect.addEventListener(EVENT.CHANGE,  ({detail})=>{
                if(detail && detail.value){
                    task.setDomain(detail.domain);
                    task.setRegistry(detail.registry);
                    aonMessengerChat.onChangeWhAndTh(detail.appParams);
                    
                    if(task.isExternal() || !task.getId() ){
                        task.setSender({});
                    }

                    task.setWorkflowTmp({...task.getWorkflowTmp(), domain: task.domain.id});
                    fillProject(task, detail.projects);
                }
            });
        
            if(domain && domain.id && task.getId()){
                aonSelect.value = domain.id; 
            }
            else if(1===offices.length){
                aonSelect.setIndexOf(0);
            }

            if( (domain && domain.id && task.getId()) || 1 === offices.length){
                aonSelect.setDisabled(true);
            }
        } catch (error) {
            console.log(error);
        }
        aonSelect.loading(false);
    }
}

/**
 * fill typeRequest (Tipo de solicitud) 
 * @param {Task} Class task
 * @param {Array} Array optionals
 */
const fillProject = async (task, projects =[], registry = undefined) => {
    const aonSelect = document.getElementById(MESSENGER_IDS.PROJECT_TASK);
    if(aonSelect){
        aonSelect.loading(true);
        const project = task.getProject();
        try {
            const aonSelectParet = aonSelect.parentNode;

            aonSelectParet.style.display = "none";

            if( projects.length === 0 && registry ){
                projects = await getProjects({ registry: task.getRegistry().id });
            }
              
            aonSelect.setOptions(projects.map(pj => ({...pj, value:pj.id, name:pj.type.description})));
            
            let display = "block";

            if(project && project.id){
                aonSelect.value = project.id; 
            } else if(1===projects.length && !task.id){
                aonSelect.setIndexOf(0);
                display = "none";
            } else if(!projects.length){
                display = "none";
            }

            aonSelectParet.style.display = display;

            aonSelect.addEventListener(EVENT.CHANGE, ({detail})=>{
                if(detail && detail.id){
                    task.setProject(detail);
                } else { 
                    task.setProject({});
                }
            });
        } catch (error) {
            console.log(error);
        }
        aonSelect.loading(false);
    }
}

/**
 * fill workgroup (Grupo de trabajo)
 * @param {Task} Task
 */
const fillWorkGroup = async (task) => {
    try {
        const aonSelect = await waitEl(`#${MESSENGER_IDS.WORKGROUP}`);
        const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
        aonSelect.loading(true);

        const workgroup = task.getWorkgroup();

        let options = await aonMessengerChat.getWorkgroup(workgroup);

        aonSelect.setOptions(options);

        if(workgroup && workgroup.id){
            aonSelect.value = workgroup.id;
        }

        aonSelect.addEventListener(EVENT.CHANGE, ({detail})=>{
            if(detail){
                task.setWorkgroup(detail);
            }
           
            fillTaskHolder(task);
        });

        aonSelect.loading(false);
    } catch (error) { console.log(error);}
}

/**
 * fill taskHolder (Titular de la tarea)
 * @param {Task} aon-messenger-chat component
 */
const fillTaskHolder = async (task) => {
    const aonSelect = await waitEl(`#${MESSENGER_IDS.TASKHOLDER}`).catch(e=>null);
    const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
    if(aonSelect){
        aonSelect.loading(true);
        try {
            const taskHolder = task.getTaskHolder();

            const workgroup = task.getWorkgroup().id;

            let options = await aonMessengerChat.getTaskHolderByWorkgroup(taskHolder, {workgroup});

            aonSelect.setOptions(options);

            let exist = false;

            if(taskHolder && taskHolder.id) {
                aonSelect.value = taskHolder.id;
                exist = options.some(t=> t.id ===taskHolder.id);
            }

            if(!exist) aonSelect.clear();

            aonSelect.addEventListener(EVENT.CHANGE, ({detail})=>{
                if(detail){
                    task.setTaskHolder(detail);
                }
            });
        } catch (error) {
            console.log(error);
        }
        aonSelect.loading(false);
    }
}

/**
 * fill customer (Clientes)
 * @param {Task} {registry} registry de customer
 * @param {HTMLElement} aon-messenger-chat component
 */
const fillCustomer = async (task) => {
    const aonSelect = await waitEl(`#${MESSENGER_IDS.CUSTOMER_TASK}`).catch(e=>null);
    if(aonSelect){
        const registry = task.getRegistry();

        aonSelect.clear();
        aonSelect.loading(true);

        try {
            const customers = await getCustomers({reload:false, page:1, perPage:50});
            
            let options = [];
            if(customers && customers.length>0)
                options = customers.map( c=> ({...c, value: c.id}) ) ;
            
            const exist = options.some(({id})=> id  === registry.id );
            if( !exist && registry.id && registry.name){
                options.push({...registry, value:registry.id});
            }

            aonSelect.setOptions( options );

            aonSelect.addEventListener(EVENT.INPUT, async({target})=>{
                const value = target.value;
                if(value.length >0){
                    getCustomers({reload:true, page:1, perPage:30, value}).then(cs=>{
                        aonSelect.setOptionsBuild( cs.map( c=> ({...c, value: c.id}) ) );
                    });
                }
            });

            if(registry && registry.id) aonSelect.value = registry.id;
    
            aonSelect.addEventListener(EVENT.CHANGE, ({detail})=>{
                if(detail){
                    task.setRegistry(detail);
                    let registryId = task.getRegistry().id;
                    fillProject(task, undefined,  registryId);

                    const contact = document.getElementById(MESSENGER_IDS.GTASK_ID_TASK);
                    if(!task.getGTaskId() && contact){
                        getCustomer({ id:registryId, additional_info: ['MEDIA']})
                        .then(resp=>{
                            const media = (resp.media || []).find(m => m.media ==="email");
                            if(media && media.value){
                                const email = media.value;
                                contact.value = email;
                                task.setGTaskId(email);
                            }
                        });
                    }
                } 
            });
        } catch (error) { }
        aonSelect.loading(false);
    }
}

/**
 * fill processType 
 * @param {Task} Class task 
 * @param {HTMLElement} aon-messenger-chat component
 */
const fillProcessType =  (task) => {
    const aonSelect = document.getElementById(MESSENGER_IDS.PROCESS_TYPE);
    const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
    const source_id = task.getSourceId();
    aonSelect.clear();
    const dur = aonMessengerChat.getDur();

    let options = [];

    if(dur.isPayroll()){
        options.push(getTaskProcess(1));
    }  

    if(dur.isPayrollManager() || dur.isPayrollPortal()){
        options.push(getTaskProcess(2));
    }
    
    if( dur.isTimecontrol() ){
        options.push(getTaskProcess(3));
    }

    if(source_id && !options.some(({value})=> value ==source_id)){
        options.push(getTaskProcess(source_id));
    }
    
    aonSelect.setOptions( options );

    if(source_id) {
        aonSelect.value = source_id;
    }

    aonSelect.addEventListener(EVENT.CHANGE, ({detail})=>{
      if(detail && detail.value) {
        task.setSourceId(detail.value)
        task.setTitle(task.getTitle() +" "+detail.name);
      }
    });
}

/**
 * fill chat (Chats)
 * @param {HTMLElement} aon-messenger-chat component
 * @param {Array} workflows array de flujo de trabajo
 */
const fillChat = (task, meId, workflows=[])=>{
    let aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
    const taskMainId = aonMessengerChat.task.id;
    const aonTab = document.getElementById(MESSENGER_IDS.AON_TAB);
    waitEl(`#${MESSENGER_IDS.MESSENGER_CHAT}`).then(chat=>{
        chat.innerHTML = "";
        if(workflows.length == 0){
            let noMessage = TaskCreationUtils.createNoMessage();
            noMessage.appendTo(chat);
        } else {

            workflows = sortBy(workflows, "id");

            const firstComment = workflows.find(w=> WORKFLOW_TYPES.OPEN.includes(w.type));
            const observation = task.getDescriptionJson().observation;
          
            if(!firstComment){
                const date = (task.getCreationDate() || new Date().getTime());
                workflows.unshift({
                    id: "noIdDescription",
                    type:WORKFLOW_TYPES.OPEN,
                    comment: observation,
                    me:true,
                    direction: MESSENGER_DIRECTION.RIGHT,
                    creation_user: task.getCreationUser(),
                    date: date,
                    notification_date: date,
                    creation_date: date,
                    notification_user: null,
                    task_holder:{},
                    email:null
                });
            } else if(firstComment && !firstComment.comment){
                workflows = workflows.map(w=>{
                    if(w.id == firstComment.id){
                        w.comment = observation;
                    }
                    return w;
                })
            }

            const getNumber = (taskWorkflow)=>{
                if(taskMainId === task.id && taskWorkflow && taskMainId !== taskWorkflow && aonTab){
                    const element = aonTab.getTabByDatasetId(taskWorkflow);
                    if(element && element.dataset && element.dataset.number){
                        return element.dataset.number;
                    }
                    return "parent";
                } 
                return null;
            }

            workflows.forEach(workflow => {
                const {id, comment, type, task:taskWorkflow, creation_date, creation_user, notification_user, notification_date, email, task_holder:{name, id:taskHolderId}} = workflow;
                const me = (taskHolderId == meId) || (email ===task.auth.email); // if taskHolder id is me
                const userName = name || email || creation_user;

                let message = {
                    id,
                    type,
                    comment,
                    me,
                    notification_date,
                    notification_user,
                    task:taskWorkflow,
                    number: getNumber(taskWorkflow),
                    date: creation_date,
                    direction: me ? MESSENGER_DIRECTION.RIGHT : MESSENGER_DIRECTION.LEFT
                }

                if(!me){
                    message.name = userName;
                }

                if (type == WORKFLOW_TYPES.COMMENT) {
                    TaskCreationUtils.createChatMessageNew(message, chat);
                } else {
                    message.name = userName;
      
                    const actionJson = chooseIconMessage(message);

                    const submessage = message.comment && WORKFLOW_TYPES.CLOSE.indexOf(type)>=0 ? message.comment : null;
                    const action = TaskCreationUtils.createAction(actionJson, actionJson.comment, submessage);
                    action.appendTo(chat);

                    if(WORKFLOW_TYPES.OPEN.includes(type) && message.comment){
                        TaskCreationUtils.createMessageOpen({comment: message.comment, id:message.id, me, date: message.date, task:message.task, number:message.number }, action.element);
                    }
                }
            });
        }
   })
}

/**
 * fill processType Tipo de incidencia
 * @param {Task} Class task 
 */
const fillTypeRequestCau =  async (task) => {
    const aonSelect = document.getElementById(MESSENGER_IDS.TYPE_REQUEST_CAU);
    if(aonSelect){
        aonSelect.clear();
        aonSelect.loading(true);
        try {
            const tags  = await getTaskTags({type:TAG_TYPE.TASK_TYPE});
            const options = tags.map(t => ({...t,value: t.id, description: t.name, name:t.name}));
    
            if(options){
                aonSelect.setOptions( options );
            }
    
            aonSelect.addEventListener(EVENT.CHANGE, ({detail})=>{
                if(detail && detail.value) {
                   task.setTaskType(detail);
                }
            });
            const value = task.getTaskType();
            if(value) aonSelect.value = value;
        }catch(e){}
        aonSelect.loading(false);
    }
}

/**
 * fill processType Aplicacion
 * @param {Task} Class task 
 */
const fillSelectAppCau =  (task) => {
    const aonSelect = document.getElementById(MESSENGER_IDS.SELECT_APP);
    const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
    if(aonSelect){
        aonSelect.clear();
        try {
            let prev = {};
            const apps = getAppsByDur(aonMessengerChat.getDur());
            let options = apps.map(app => ({...app, value:app.tag, name:app.title}));
    
            if(options){
                aonSelect.setOptions( options );
            }
    
            aonSelect.addEventListener(EVENT.CHANGE, ({detail})=>{
                if(detail && detail.value) {
                    if(!task.id && prev.name) {
                        task.removeTagName(prev.name);
                    } 

                    const tag = { name:detail.tag, color:detail.color, type:TAG_TYPE.TASK_LABEL };
                    prev = tag;
                    task.addTag(tag);
                }
            });
        } catch (error) {
            console.log(error);
        }
    }
}

export const TaskFill = {
    fillRequestType,
    fillAdvisory,
    fillProject,
    fillWorkGroup,
    fillTaskHolder,
    fillCustomer,
    fillProcessType,
    fillChat,
    fillTypeRequestCau,
    fillSelectAppCau
};