import { EVENT, MSG } from "../../../environments/environments";
import { getOfficeProjects, getProjectsByRegistry } from "../../../services/projectService";
import { getCustomers } from "../../../services/registryService";
import { getTastHoldersWorkGroup } from "../../../services/taskHolderService";
import { waitEl } from "../../../services/utils";
import { MESSENGER_DIRECTION, MESSENGER_IDS, TASK_SOURCE, WORKFLOW_TYPES } from "../MessengerEnums";
import { createAction, createChatMessage, createNoMessage} from "./creationUtils";
import { chooseIconMessage } from "./utils";

/**
 * fill typeRequest (Tipo de solicitud)
 * @param {Task} {source} tipo de tarea
 * @param {HTMLElement} aon-messenger-chat component
 */
export const fillRequestType = ({source}, aonMessengerChat) => {
    const aonSelect = document.getElementById(MESSENGER_IDS.SOURCE_TASK);
    
    let sources = [
        {value: TASK_SOURCE.QUERY, name: MSG[TASK_SOURCE.QUERY.toUpperCase()] },
        {value: TASK_SOURCE.REQUEST, name: MSG.FORMALITIES },
    ];

    if(TASK_SOURCE.MANUAL === source)
        sources.unshift({value: TASK_SOURCE.MANUAL, name: "MANUAL" }); 

    if(aonMessengerChat.getApplicationParent().dur.hasCallCenter())
        sources.push({value: TASK_SOURCE.CAU, name: "Soporte" });

    aonSelect.options = JSON.stringify(sources);
    
    if(source){  aonSelect.value = source; } 
}


/**
 * fill typeRequest (Tipo de solicitud)
 * @param {Task} Class task
 */
 export const fillProject = async (task) => {
    const aonSelect = document.getElementById(MESSENGER_IDS.PROJECT_TASK);
    if(aonSelect){
        const project = task.getProject();
        try {
            let projects = [];
            let registry = task.getRegistry();
            if(registry.id){
                let filter = {
                    registryId:registry.id,
                    domainId:registry.domain.id,
                    domainName:registry.domain.name
                };
                projects = await getProjectsByRegistry(filter);
            } else {
                projects = await getOfficeProjects();
            }


            aonSelect.options = JSON.stringify(projects.map(pj => ({...pj, value:pj.id, name:pj.type.description})));
            
            if(project && project.id){ aonSelect.value = project.id; } 
        
            aonSelect.addEventListener(EVENT.CHANGE,({detail})=>{
                if(detail && detail.id)
                    task.setProject(detail);
                else 
                    task.setProject({});
            })
        } catch (error) {
            console.log(error);
        }
    }
}


/**
 * fill workgroup (Grupo de trabajo)
 * @param {Task} task Class task
 * @param {HTMLElement} aon-messenger-chat component
 */
export const fillWorkGroup = async (task, aonMessengerChat) => {
    try {
        const aonSelect = await waitEl(`#${MESSENGER_IDS.WORKGROUP}`);
        const workgroups = aonMessengerChat.getApplicationParent()._workgroups;
        if(workgroups && workgroups.length>0){
            aonSelect.options = JSON.stringify( workgroups.map( wg=> ({...wg, id: wg.value}) ) );
        }
        if(task.workgroup && task.workgroup.id){
            aonSelect.value = task.workgroup.id;
            if(task.task_holder && task.task_holder.id)
                fillTaskHolder(aonMessengerChat, task.workgroup.id, task.task_holder.id);
        } 

        aonSelect.addEventListener(EVENT.CHANGE, ({detail})=>{
            if(detail)
                task.setWorkgroup(detail);
            fillTaskHolder(aonMessengerChat, detail.value);
        });
    } catch (error) { console.log(error);}
}

/**
 * fill taskHolder (Titular de la tarea)
 * @param {HTMLElement} aon-messenger-chat component
 * @param {Integer} workgroupId 
 * @param {Integer} taskHolderId
 */
export const fillTaskHolder = async (aonMessengerChat, workgroupId=0, taskHolderId=undefined) => {
    const aonSelect = await waitEl(`#${MESSENGER_IDS.TASKHOLDER}`).catch(e=>null);
    if(aonSelect){
        aonSelect.clear();
        const taskHolders = await getTastHoldersWorkGroup({workgroupId});
        if(taskHolders){
            aonSelect.options = JSON.stringify(
                taskHolders.map( th=> ({...th, value: th.id}) )
            );
        }
        if(taskHolderId) aonSelect.value = taskHolderId;

        aonSelect.addEventListener(EVENT.CHANGE, ({detail})=>{
            if(detail){
                aonMessengerChat.task.setTaskHolder(detail);
            } 
        });
    }
}

/**
 * fill customer (Clientes)
 * @param {Task} {registry} registry de customer
 * @param {HTMLElement} aon-messenger-chat component
 */
export const fillCustomer = async ({registry}, aonMessengerChat) => {
    const aonSelect = await waitEl(`#${MESSENGER_IDS.CUSTOMER_TASK}`).catch(e=>null);
    if(aonSelect){
        aonSelect.clear();
        const customers = await getCustomers();
        if(customers){
            aonSelect.options = JSON.stringify( customers.map( c=> ({...c, value: c.id}) ) );
        }
        if(registry && registry.id) aonSelect.value = registry.id;

        aonSelect.addEventListener(EVENT.CHANGE, ({detail})=>{
            if(detail){
                aonMessengerChat.task.setRegistry(detail);
                fillProject(aonMessengerChat.task);
            } 
        });

        // aonSelect.addEventListener(EVENT.CHANGE, ({detail})=>{
        //     if(detail)
        //         task.setWorkgroup(detail);
        //     fillTaskHolder(aonMessengerChat, detail.value);
        // });
    }
}

/**
 * fill tag (Etiquetas)
 * @param {Task} Class task 
 * @param {HTMLElement} aon-messenger-chat component
 */
// export const fillTag = async (task, aonMessengerChat) => {
//     const aonSelect = await waitEl(`#${MESSENGER_IDS.TASKTAG}`).catch(e=>null);
//     const applicationParent = aonMessengerChat.getApplicationParent();
//     if(aonSelect){
//         aonSelect.clear();
//         const tags = applicationParent._tags;
//         if(tags){
//             aonSelect.options = JSON.stringify( tags.map( c=> ({...c, value: c.id}) ) );
//         }
//         if(task.getDescriptionJson().tag) aonSelect.value = task.getDescriptionJson().tag;

//         aonSelect.addEventListener(EVENT.CHANGE, ({detail})=>{
//             task.setDescriptionJson({tag:detail.id});
//         })
//     }
// }

/**
 * fill processType (Titular de la tarea)
 * @param {Task} Class task 
 * @param {HTMLElement} aon-messenger-chat component
 */
export const fillProcessType =  ({source_id}, aonMessengerChat) => {
    const aonSelect = document.getElementById(MESSENGER_IDS.PROCESS_TYPE);
    aonSelect.clear();
    const typeProcess = [
        { value:1, name:"Solicitud de vacaciones"},
    ];
    if(typeProcess){
        aonSelect.options = JSON.stringify( typeProcess.map(tp=> tp) );
    }
    if(source_id) aonSelect.value = source_id;

    aonSelect.addEventListener(EVENT.CHANGE, ({detail})=>{
      if(detail && detail.value) {
        const task = aonMessengerChat.task;
        task.setSourceId(detail.value)
        task.setTitle(task.getTitle() +" "+detail.name);
      }
    });
}

/**
 * fill chat (Chats)
 * @param {Array} workflows array de flujo de trabajo
 * @param {HTMLElement} aon-messenger-chat component
 */
export const fillChat = (workflows=[], aonMessengerChat)=>{
    waitEl(`#${MESSENGER_IDS.MESSENGER_CHAT}`).then(chat=>{
        if(workflows.length == 0){
            let noMessage = createNoMessage();
            noMessage.appendTo(chat);
        } else {
            const task = aonMessengerChat.task;
            const meId = aonMessengerChat.getApplicationParent().TASK_HOLDER.id;

            workflows.forEach(workflow => {
                const {id, comment, type, creation_date, notification_user, notification_date, email, task_holder:{name, alias, id:taskHolderId}} = workflow;
                const me = (taskHolderId == meId) || (email ===task.auth.email); // if taskHolder id is me
                let message = {
                    id,
                    type,
                    comment,
                    direction: me ? MESSENGER_DIRECTION.RIGHT : MESSENGER_DIRECTION.LEFT,
                    date: creation_date,
                    notification_date,
                    notification_user
                }

                if(!me) message.name = alias || name || email;

                if (type == WORKFLOW_TYPES.COMMENT) {
                    createChatMessage(message, chat);
                } else{
                    message.name = name || email;
                    const actionJson = chooseIconMessage(message);
                    const action = createAction(actionJson, actionJson.comment);
                    action.appendTo(chat);
                }
            });
        }
   })
}