import {  EVENT, MSG } from "../../../environments/environments.js";
import {Apps} from "../../../services/app.js";
import { getProjects} from "../../../services/projectService.js";
import { getCustomers } from "../../../services/registryService.js";
import { getTastHoldersWorkGroup } from "../../../services/taskHolderService.js";
import { getTaskProcess } from "../../../services/taskService.js";
import { waitEl } from "../../../services/utils.js";
import { MESSENGER_DIRECTION, MESSENGER_IDS, TASK_SOURCE, WORKFLOW_TYPES } from "../MessengerEnums.js";
import { createAction, createChatMessage, createNoMessage} from "./creationUtils.js";
import { chooseIconMessage } from "./utils.js";

/**
 * fill typeRequest (Tipo de solicitud)
 * @param {Task} {source} tipo de tarea
 * @param {HTMLElement} aon-messenger-chat component
 */
export const fillRequestType = ({source}, aonMessengerChat) => {
    const aonSelect = document.getElementById(MESSENGER_IDS.SOURCE_TASK);
    let sources = [
        {value: TASK_SOURCE.QUERY, name: MSG[TASK_SOURCE.QUERY.toUpperCase()] }
    ];

    if(!aonMessengerChat.isCau())
     sources.push({value: TASK_SOURCE.REQUEST, name: MSG.FORMALITIES });

    if(TASK_SOURCE.MANUAL === source)
        sources.unshift({value: TASK_SOURCE.MANUAL, name: "MANUAL" }); 

    if(aonMessengerChat.getDur().hasCallCenter())
        sources.push({value: TASK_SOURCE.CAU, name: "Soporte" });

    aonSelect.setOptions(sources);
    
    if(source){  aonSelect.value = source; } 
}

export const fillAdvisory = async (task, aonMessengerChat) => {
    const aonSelect = document.getElementById(MESSENGER_IDS.ADVISORY_TASK);
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
                    task.setAppParams(detail.appParams);
                    task.changeWhAndTh();
                    if(task.isExternal() || !task.id )
                        task.setSender({});

                    task.setWorkflowTmp({...task.getWorkflowTmp(), domain: task.domain.id});
                    fillProject(aonMessengerChat.task, detail.projects);
                }
            });
        
            if(domain && domain.id && task.getId())
                aonSelect.value = domain.id; 
            else if(1===offices.length)
                aonSelect.setIndexOf(0);

            if( (domain && domain.id && task.getId()) || 1 === offices.length)
                aonSelect.setDisabled(true);
        } catch (error) {
            console.log(error);
        }
        aonSelect.loading(false);
    }
}


/**
 * fill typeRequest (Tipo de solicitud) RE
 * @param {Task} Class task
 * @param {Array} Array optionals
 */
export const fillProject = async (task, projects =[], registry = undefined) => {
    const aonSelect = document.getElementById(MESSENGER_IDS.PROJECT_TASK);
    if(aonSelect){
        aonSelect.loading(true);
        const project = task.getProject();
        try {
            const aonSelectParet = aonSelect.parentNode;

            aonSelectParet.style.display = "none";
            
            // if(projects.length ===0 && (task.id && !task.isExternal()) )

            if( projects.length === 0 && registry )
                projects = await getProjects({ registry: task.getRegistry().id });
              
            aonSelect.setOptions(projects.map(pj => ({...pj, value:pj.id, name:pj.type.description})));
            
            let display = "block";
    
            if(project && project.id)
                aonSelect.value = project.id; 
            else if(1===projects.length && !task.id){
                aonSelect.setIndexOf(0);
                display = "none";
            } else if(!projects.length)
               display = "none";

            aonSelectParet.style.display = display;

            aonSelect.addEventListener(EVENT.CHANGE, ({detail})=>{
                if(detail && detail.id)
                    task.setProject(detail);
                else 
                    task.setProject({});
            });

            // if(!task.isExternal() && task.getId())
            //     aonSelect.setDisabled(true);
        } catch (error) {
            console.log(error);
        }
        aonSelect.loading(false);
    }
}

/**
 * fill workgroup (Grupo de trabajo)
 * @param {HTMLElement} aon-messenger-chat component
 */
export const fillWorkGroup = async (aonMessengerChat) => {
    try {
        const task = aonMessengerChat.task;
        const aonSelect = await waitEl(`#${MESSENGER_IDS.WORKGROUP}`);
        const workgroup = task.getWorkgroup();
        aonSelect.loading(true);
        const workgroups = await aonMessengerChat.getWorkGroups();
        let options = [];
        if(workgroups && workgroups.length>0)
            options = workgroups.map( wg=> ({...wg, id: wg.value}) );
        
        const exist = options.some(({id})=> id  === workgroup.id );
        if( !exist && workgroup.id && workgroup.description)
            options.push({...workgroup, value:workgroup.id, name:workgroup.description});
        
        aonSelect.setOptions(options);
        
        if(task.workgroup && task.workgroup.id)
            aonSelect.value = task.workgroup.id;

        aonSelect.addEventListener(EVENT.CHANGE, ({detail})=>{
            if(detail)
                task.setWorkgroup(detail);
            fillTaskHolder(aonMessengerChat);
        });

        aonSelect.loading(false);
    } catch (error) { console.log(error);}
}

/**
 * fill taskHolder (Titular de la tarea)
 * @param {HTMLElement} aon-messenger-chat component
 * @param {Integer} workgroup 
 */
export const fillTaskHolder = async (aonMessengerChat) => {
    const aonSelect = await waitEl(`#${MESSENGER_IDS.TASKHOLDER}`).catch(e=>null);
    const task = aonMessengerChat.task;
    if(aonSelect){
        aonSelect.clear();
        const workgroup = task.getWorkgroup().id;
        const taskHolders = await getTastHoldersWorkGroup({workgroup, active:1});

        let options = [];
        if(taskHolders && taskHolders.length>0){
            options = taskHolders.map( th=> ({...th, value: th.id}) )
        } else if(task.task_holder.id && task.task_holder.name) {
            options = [{...task.task_holder, value:task.task_holder.id}];
        }

        aonSelect.setOptions(options);
        
        if(task.task_holder && task.task_holder.id) 
            aonSelect.value = task.task_holder.id;

        aonSelect.addEventListener(EVENT.CHANGE, ({detail})=>{
            if(detail){
                task.setTaskHolder(detail);
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
    const task = aonMessengerChat.task;
    if(aonSelect){
        aonSelect.clear();
        aonSelect.loading(true);
        try {

            const customers = await getCustomers({reload:false, page:1, perPage:50});

            let options = [];
            if(customers && customers.length>0)
                options = customers.map( c=> ({...c, value: c.id}) ) ;
            
            const exist = options.some(({id})=> id  ===registry.id );
            if( !exist && registry.id && registry.name){
                options.push({...registry, value:registry.id});
            }

            aonSelect.setOptions( options );

            aonSelect.addEventListener(EVENT.INPUT, async({target})=>{
                const value = target.value;
                if(value.length > 2){
                    const cs = await getCustomers({reload:true, page:1, perPage:30, value});
                    aonSelect.setOptions( cs.map( c=> ({...c, value: c.id}) ) );
                }
            });

            if(registry && registry.id) aonSelect.value = registry.id;
    
            aonSelect.addEventListener(EVENT.CHANGE, ({detail})=>{
                if(detail){
                    task.setRegistry(detail);
                    fillProject(task, undefined,  task.getRegistry().id);
                } 
            });
        } catch (error) { }
        aonSelect.loading(false);
    }
}

/**
 * fill processType (Titular de la tarea)
 * @param {Task} Class task 
 * @param {HTMLElement} aon-messenger-chat component
 */
export const fillProcessType =  ({source_id}, aonMessengerChat) => {
    const aonSelect = document.getElementById(MESSENGER_IDS.PROCESS_TYPE);
    aonSelect.clear();
    const dur = aonMessengerChat.getDur();

    let options = [];
    if(!dur.isPayrollManager() && !dur.isPayrollPortal())
        options.push(getTaskProcess(1));
    else if(!dur.isPayrollManager() && dur.isPayrollPortal())
        options.push(getTaskProcess(2));
    
    if( !dur.isTimecontrolManager() && !dur.isTimecontrolPortal() )
        options.push(getTaskProcess(3));

    if(source_id && !options.some(({value})=> value ==source_id))
        options.push(getTaskProcess(source_id));
    
    aonSelect.setOptions( options );

    if(source_id) 
        aonSelect.value = source_id;

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
 * @param {HTMLElement} aon-messenger-chat component
 * @param {Array} workflows array de flujo de trabajo
 */
export const fillChat = (aonMessengerChat, workflows=[])=>{
    waitEl(`#${MESSENGER_IDS.MESSENGER_CHAT}`).then(chat=>{
        if(workflows.length == 0){
            let noMessage = createNoMessage();
            noMessage.appendTo(chat);
        } else {
            const task = aonMessengerChat.task;
            const meId = aonMessengerChat.getApplicationParent().TASK_HOLDER.id;

            workflows.forEach(workflow => {
                const {id, comment, type, creation_date, notification_user, notification_date, email, task_holder:{name, id:taskHolderId}} = workflow;
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

                if(!me) message.name = name || email;
                if (type == WORKFLOW_TYPES.COMMENT) {
                    createChatMessage(message, chat);
                } else{
                    message.name = name || email;
                    const actionJson = chooseIconMessage(message);
                    const submessage =  message.comment && WORKFLOW_TYPES.CLOSE.indexOf(type)>=0 ? message.comment : null;
                    const action = createAction(actionJson, actionJson.comment, submessage);
                    action.appendTo(chat);
                }
            });
        }
   })
}


/**
 * fill processType (Titular de la tarea)
 * @param {Task} Class task 
 */
 export const fillTypeRequestCau =  (aonMessengerChat) => {
    const task = aonMessengerChat.task;
    const aonSelect = document.getElementById(MESSENGER_IDS.TYPE_REQUEST_CAU);
    aonSelect.clear();

    let value = task.getDescriptionJson().type;

    const options = [
        { value:1, name:"CONSULTA"},
        { value:2, name:"ERROR"},
        { value:3, name:"SERVICIOS"},
        { value:4, name:"SUGERENCIAS"},
        { value:5, name:"eMail"},
    ];

    if(options)
        aonSelect.setOptions( options );

    aonSelect.addEventListener(EVENT.CHANGE, ({detail})=>{
      let title = "";
      if(detail && detail.value) {
        title = detail.name;
        task.setDescriptionJson({type:detail.value});
        const selectApp = document.getElementById(MESSENGER_IDS.SELECT_APP);
        if(selectApp) title = detail.name+ " / "+ selectApp.getText();
        
        task.setTitle(title);
      }
    });

    if(value) aonSelect.value = value;
}


/**
 * fill processType (Titular de la tarea)
 * @param {Task} Class task 
 */
 export const fillSelectAppCau =  (aonMessengerChat) => {
    const task = aonMessengerChat.task;
    const aonSelect = document.getElementById(MESSENGER_IDS.SELECT_APP);
    aonSelect.clear();

    try {
        let value = task.getDescriptionJson().app;

        const apps = getAppPermission(aonMessengerChat.getDur());
        let options = apps.map(app => ({value:app.app, name:app.title}));

        if(options)
            aonSelect.setOptions( options );

        aonSelect.addEventListener(EVENT.CHANGE, ({detail})=>{
            let title = "";
            if(detail && detail.value) {
                task.setDescriptionJson({app:detail.value});
                const selectTypeRequest = document.getElementById(MESSENGER_IDS.TYPE_REQUEST_CAU);
                if(selectTypeRequest) title = selectTypeRequest.getText() +" / "+detail.name;
            }
            task.setTitle(title);
        });
        
        if(value) aonSelect.value = value;
    } catch (error) {
        console.log(error);
    }
}



const getAppPermission = (dur) => {
  let apps = [];
  if( dur.isAccounting())
    apps.push(Apps.ACCOUNTING);

  if(dur.isFiscal())
    apps.push(Apps.FISCAL);

  if((dur.isComunicaManager() || dur.isComunicaPortal() ) && !dur.isPayroll())
   apps.push(Apps.COMUNICA);

  if(dur.isPayroll())
    apps.push(Apps.PAYROLL);

  if(dur.isDocumental())
    apps.push(Apps.DOCUMENTAL);

  if(dur.isTimecontrol())
    apps.push(Apps.TIMECONTROL);

  if(dur.isInvoice())
    apps.push(Apps.INVOICE);

  return apps;
}