import { API_URL, COLORS, CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG } from "../../../environments/environments";
import { openFileUrl } from "../../../services/fileService";
import { getCustomers } from "../../../services/registryService";
import { domainName } from "../../../services/request";
import { getTastHoldersWorkGroup } from "../../../services/taskHolderService";
import { newComponent, setAttributes, setFullDate, setStyles, setTime, waitEl } from "../../../services/utils";
import { createFormVacation } from "../forms/vacation";
import { ICON_TYPES, MessengerOptions, MESSENGER_COMPONENTS, MESSENGER_IDS, MESSENGER_VIEWS, TASK_SOURCE, TASK_STATUS, WORKFLOW_TYPE, WORKFLOW_TYPES } from "../MessengerEnums";
import { createAction, createCardMessenger, createChatMessage, createCustomer, createDivEditable, createInputContact, createProcessType, createReceiverDiv, createStartJustifiedColumn, createStartJustifiedRow, createTaskHolder, createWorkgroup, LEFT, RIGHT, titleFirstDiv } from "./creationUtils";

/**
 * Build standard toolbar options 
 * @param {*} aonTextArea 
 */
export const buildTextareaToolbar =  (aonTextArea, task, file= false) => {
    const textAreaText = aonTextArea.querySelector("#" + aonTextArea.TEXTAREA);
    if(textAreaText) setStyles(textAreaText, {resize: "none"});
    /**
     * Bold format button **bold**
     */
    aonTextArea.addToolbarOptionLeft({
        id: MATERIAL_ICONS.FORMAT_BOLD,
        icon: MATERIAL_ICONS.FORMAT_BOLD,
        name:MSG.BOLD
    },() => documentExec("bold"));

    /**
     * Italic format buttton _italic_
     */
    aonTextArea.addToolbarOptionLeft({
        id: MATERIAL_ICONS.FORMAT_ITALIC,
        icon: MATERIAL_ICONS.FORMAT_ITALIC,
        name:"Cursiva"
    },() =>  documentExec("italic"));
    
        /**
     * List bulleted button - listItem
     */
     aonTextArea.addToolbarOptionLeft({
        id: MATERIAL_ICONS.FORMAT_LIST_NUMBERED,
        icon: MATERIAL_ICONS.FORMAT_LIST_NUMBERED,
        name:"Lista numerada"
    },() => documentExec("insertOrderedList") );

    /**
     * Link send button [Name](url)
     */
    aonTextArea.addToolbarOptionLeft({
        id: MATERIAL_ICONS.LINK,
        icon: MATERIAL_ICONS.LINK,
        name:"Insertar enlace"
    },() => createLink());

    //UNDERLINED
    aonTextArea.addToolbarOptionLeft({
        id: MATERIAL_ICONS.FORMAT_UNDERLINED,
        icon: MATERIAL_ICONS.FORMAT_UNDERLINED,
        name:"Subrayado"
    },() =>documentExec('underline'));

    //FORMAT_QUOTE
    aonTextArea.addToolbarOptionLeft({
        id: MATERIAL_ICONS.FORMAT_QUOTE,
        icon: MATERIAL_ICONS.FORMAT_QUOTE,
        name:"Cita"
    },() =>blockquote());


    //ATTACH
    aonTextArea.addToolbarOptionLeft({
        id: MATERIAL_ICONS.ATTACH_FILE,
        icon: MATERIAL_ICONS.ATTACH_FILE,
        name:MSG.ADD_FILE
    },() =>{});
}


const documentExec = (exec) => document.execCommand(exec) ? document.execCommand("normal") : document.execCommand(exec);

const createLink =() =>{
    const selection = document.getSelection();
    const url = prompt('URL:', 'https://');
    const aEl = setAttributes(document.createElement("a"),{
        target:"_blank",
        class:CSS.AON_LINK,
        href:url,
        title:url
    });
    aEl.textContent = selection.toString() ? selection : url;
    document.execCommand('insertHTML', false, aEl.outerHTML);
}

const blockquote = ()=>{
    const selection = document.getSelection();
    const blockquoteEl = setStyles(document.createElement("blockquote"),{
        margin:"0px 0px 0px 0.8ex",
        borderLeft: "1px solid rgb(204, 204, 204)",
        paddingLeft: "1ex"
    });
    blockquoteEl.textContent = selection;
    document.execCommand('insertHTML', false, blockquoteEl.outerHTML);
}


//FILL WORKGROUP
const fillWorkGroup = async ({workgroup, task_holder}, application) => {
    try {
        const aonSelect = await waitEl(`#${MESSENGER_IDS.WORKGROUP}`);

        const workgroups = application.getParent()._workgroups;
        if(workgroups && workgroups.length>0){
            aonSelect.options = JSON.stringify( workgroups.map( wg=> ({...wg, id: wg.value}) ) );
        }
        if(workgroup && workgroup.id){
            aonSelect.value = workgroup.id;
            if(task_holder && task_holder.id)
                fillTaskHolder(workgroup.id, task_holder.id);
        } 

        aonSelect.addEventListener(EVENT.CHANGE, ({detail})=>{
            if(detail)
                fillTaskHolder(detail.value);
        })
       
    } catch (error) { console.log(error);}
}

//FILL TASKHOLDERS
const fillTaskHolder = async (workgroupId, taskHolderId=undefined) => {
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
    }
}

//FILL CUSTOMER
const fillCustomer = async ({registry}) => {
    const aonSelect = await waitEl(`#${MESSENGER_IDS.CUSTOMER_TASK}`).catch(e=>null);
    if(aonSelect){
        aonSelect.clear();
        const customers = await getCustomers();
        if(customers){
            aonSelect.options = JSON.stringify( customers.map( c=> ({...c, value: c.id}) ) );
        }
        if(registry && registry.id) aonSelect.value = registry.id;
    }
}


//FILL CHAT
export const fillChat = (workflows=[])=>{
    waitEl(`#${MESSENGER_IDS.MESSENGER_CHAT}`).then(chat=>{
        if(workflows.length == 0){
            let noMessage = newComponent({
                type : MESSENGER_COMPONENTS.ADVICE,
                id : MESSENGER_IDS.NO_MESSAGES,
                text : 'No hay mensajes en esta solicitud',
                styles : {
                    fontSize : '1em',
                    color : CSS.variable(COLORS.GRAYSON),
                }
            });
            noMessage.appendTo(chat);
        } else {
            const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
            const applicationParent = aonMessengerChat.getApplicationParent();
    
            const meId = applicationParent.TASK_HOLDER.id;
    
            workflows.forEach(workflow => {
                const {id, comment, type, modification_date, task_holder:{name, alias, id:taskHolderId}} = workflow;
                const me = taskHolderId == meId; // if taskHolder id is me
                const message = {
                    id,
                    type,
                    comment,
                    direction: me ? RIGHT: LEFT,
                    date: new Date(modification_date)
                }

                if(!me)message.name =alias;

                if (type == WORKFLOW_TYPES.COMMENT) {
                    createChatMessage(message, chat);
                } else{
                    message.name = name;
                    const actionJson = chooseIconMessage(message);
                    const action = createAction(actionJson, actionJson.comment);
                    action.appendTo(chat);
                }
            });
        }
   })
}

//FILL PROCESS TYPE
const fillProcessType = async ({source_id}) => {
    try {
        const aonSelect = await waitEl(`#${MESSENGER_IDS.PROCESS_TYPE}`);
        aonSelect.addEventListener(EVENT.CHANGE, ({detail})=>{
            if(detail && detail.value)
                changeFormProcess(detail);
        })
        if(aonSelect){
            aonSelect.clear();
            const typeProcess = [
                {
                    value:1,
                    name:"Solicitud de vacaciones"
                }
            ];
            if(typeProcess){
                aonSelect.options = JSON.stringify( typeProcess.map(tp=> tp) );
            }
            if(source_id) aonSelect.value = source_id;
        }
    } catch (error) {console.log(error);}
}

/**
 * Choose icon for the actions
 * @param {Object} message
 * @returns {Object} actionJson message new object
 */
const chooseIconMessage = ({type, date, name, comment}) => {
    const dateParse = setFullDate(date) + " " + setTime(date);
    
    let actionJson = {
        icon : MATERIAL_ICONS.INFO,
        type : ICON_TYPES.MATERIAL_OUTLINED,
        color : CSS.variable(COLORS.MATERIAL_BLUE),
        comment: `${WORKFLOW_TYPE(type)} por <b>${name ? name : null}</b> ${dateParse}`
    }

    if(WORKFLOW_TYPES.OPEN.indexOf(type)>=0 || WORKFLOW_TYPES.REOPEN.indexOf(type)>=0){
        actionJson.color = CSS.variable(COLORS.ONLINE_GREEN);
    }  else if(WORKFLOW_TYPES.CLOSE.indexOf(type)>=0){
        actionJson.icon = MATERIAL_ICONS.CHECK_CIRCLE_OUTLINE;
        actionJson.color = CSS.variable(COLORS.MATERIAL_RED);
    } else if(WORKFLOW_TYPES.DELETE.indexOf(type)>=0){
        actionJson.icon = MATERIAL_ICONS.ARCHIVE;
        actionJson.color = CSS.variable(COLORS.GRAYSON);
    } else if(WORKFLOW_TYPES.ASSIGN.indexOf(type)>=0){
        actionJson.comment = `${WORKFLOW_TYPE(type)} por <b>${name ? name : null}</b> a <b>${comment}</b> ${dateParse}`;
    }

    return actionJson;
}

/**
 * Append a new message to the chat with a little animation
 * @param {*} properties 
 */
const appendChatMessage = (properties) => {
    const noMessage = document.getElementById(MESSENGER_IDS.NO_MESSAGES);
    const chat = document.querySelector(MESSENGER_COMPONENTS.CHAT);
    
    if(noMessage)
        chat.removeChild(noMessage);

    const message = setStyles( createChatMessage(properties, chat).element,{
        opacity : 0,
        marginTop : '20px',
        transition : ".25s"
    });

    /**
     * Appearing animation
     */
    setTimeout(() => setStyles(message, { opacity : 1, marginTop : '10px'}), 100);
} 

/**
 * Send message to chat
 * @param {*} aonTextArea - The mensaje source
 * @returns void.
 */
export const sendMessage = async (aonTextArea) => {
    await checkFilesAndSend(aonTextArea); //CHECK FILES COMMENT AND SEND

    const value =  aonTextArea.value;
    if(!value || (value && !value.trim().length)) return ;

    const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
    const task = aonMessengerChat.task;
    const message = {
        type: WORKFLOW_TYPES.COMMENT,
        sender: "",
        comment: value,
        creation_date: new Date()
    }

    aonTextArea.clear();
    
    task.workflow.push(message);

    appendChatMessage({
        id: "id",
        name: message.sender,
        comment:message.comment,
        date: message.creation_date,
        direction : RIGHT
    });

    addLine();

    aonMessengerChat.data = task;

    const chat = document.querySelector(MESSENGER_COMPONENTS.CHAT);
    chat.scrollTo(0, chat.scrollHeight); //GO DOWN

    return value;
}

/**
 * 
 * @param {HTMLElement} textArea htmlElement textArea
 * check files and send uploadFile(taskAttach) 
 */
const checkFilesAndSend = async (textArea)=>{
    const btnSend = document.getElementById(MESSENGER_IDS.BUTTON_SEND);
    if(btnSend)btnSend.disabled = true;
    try {
        const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
        const textAreaDiv = textArea.getTextAreaDiv();
        const elements = textAreaDiv.querySelectorAll(`[${CONSTANT.TYPE}=${WORKFLOW_TYPES.AON_FILE}]`);
        const {id:taskId} = aonMessengerChat.task;
        const files = textArea.FILES;
        for await (const el of elements) {
            const fileId = el.dataset.id;
            const file = files.find(({id})=> id == fileId);
            if(file){
                const taskAttach = await aonMessengerChat.uploadFile({ file, taskId });
                if(taskAttach){
                    const json = {
                        domain_name: domainName(),
                        attach_type:"task",
                        domain_id:taskAttach.domain,
                        id:taskAttach.id
                    };
                    const jsonBase64 = btoa( JSON.stringify(json) );
                    
                    const linkTmp = `/${API_URL}/file/${jsonBase64}`;
    
                    if(file.contentType.indexOf("image")>=0)
                        el.src = linkTmp;
                    else 
                        el.href = linkTmp;
                }
            }
        }
    } catch (error) { console.log(error); }
    if(btnSend)btnSend.disabled = false;
}

/**
 * 
 * @param {HTMLElement} parent check html and add event 
 * @param {*} json 
 */
export const checkFilesAddEventClick = (parent)=>{
    const elements = parent.querySelectorAll(`[${CONSTANT.TYPE}=${CONSTANT.AON_FILE}]`);
    for (const element of elements) {
        const url = element.src || element.href;
        if(url) {
            element.addEventListener(EVENT.CLICK, (ev)=>{
                ev.preventDefault();
                openFileUrl(url);
            });
        } 
    }
}

/**
 * Description add event click img or file
 * @param {Task} task class task
 */
export const checkFilesAddEventDescription = (task)=>{
    const descriptionEl = document.getElementById(MESSENGER_IDS.DESCRIPTION_TASK);
    if(task && descriptionEl){
        const observation = task.getDescriptionJson().observation;
        if(observation) {
            descriptionEl.value = observation;
            checkFilesAddEventClick(descriptionEl.getTextAreaDiv());
        }
    }
}

const changeFormProcess = ({value,name}) => {
    const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
    const task = aonMessengerChat.task;
    const processDiv = document.getElementById(MESSENGER_IDS.PROCESS_DIV);
    processDiv.innerHTML = "";
    //CREATE CARD
    let aonCard = createCardMessenger("aonCardProcess", name);
    processDiv.appendChild(aonCard);
    aonCard.getCard().style.margin = 0;
    aonCard.getCard().style.marginTop = "10px";
    if(task.id){ //BUTTON SHOW JSON
        aonCard.addTitleButton(MSG.VIEW, MATERIAL_ICONS.VISIBILITY, false, () => {
            let d = aonMessengerChat.applicationEl.getDialog();
             if(d){
                d.clear();
                if (!aonMessengerChat.isMobile()) d.width = '400px';
                d.setTitle("JSON");
                d.setContent(jsonDiv());
                d.addAcceptAction(() => {});
                d.open();
              }
        });
    }

    if(value===1){ //FORM VACATION
        createFormVacation(aonCard, aonMessengerChat);
    } 
}

const jsonDiv = ()=> {
    const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
    const task = aonMessengerChat.task;
    const pre  = setStyles(document.createElement("pre"),{
        backgroundColor: "ghostwhite",
        border: "1px solid silver",
        padding: "10px 20px",
        margin: "20px",
        whiteSpace: "pre-wrap"
    });
    const code = document.createElement("code");
    code.style.color = "brown";
    pre.appendChild(code); 
    code.textContent = JSON.stringify(task.getDescriptionJson(), undefined, 2);
    return pre;
}

export const addLine = () => {
    /**
     * Setting the chat line once all is rendered
     * DO NOT change this, is compulsory.
     */
    const lined = document.querySelector(".continueLined");
    if (lined) lined.style.setProperty("--height", lined.scrollHeight + "px");
}

export const buildFormQuery = (div, aonMessengerChat) => {
    const task = aonMessengerChat.task;
    const application = aonMessengerChat.applicationEl;
    const applicationParent = aonMessengerChat.applicationParentEl;

    const aonCard = createCardMessenger(MSG.DATA, "");
    div.appendChild(aonCard);
    aonCard.getCard().style.margin = 0;

    const columnsDiv = createStartJustifiedColumn();
    aonCard.setContent(columnsDiv.element);

    //----------------ISSUE-----------
    const titleDiv = titleFirstDiv(MSG.ISSUE);
    columnsDiv.appendChild(titleDiv);
    const title = createDivEditable(task.title, MESSENGER_IDS.TITLE_TASK, MSG.TYPE_HERE);
    titleDiv.appendChild(title); 
    //-------------------------END ISSUE

    if(task.source === TASK_SOURCE.CAU && !applicationParent.cauData){
      // DIV CUSTOMER
      const rowsDivTwo = createStartJustifiedRow();
      rowsDivTwo.element.style.width = "100%";
      columnsDiv.appendChild(rowsDivTwo.element);
      // CUSTOMER
      const customerSelect = createCustomer();
      customerSelect.style.width = "100%";
      rowsDivTwo.appendChild(customerSelect);
          //-----------------TASK HOLDER
      const contact = createInputContact();
      contact.style.width = "100%";
      contact.style.marginLeft = "5px";
      rowsDivTwo.appendChild(contact);
      if(task.gtask_id) contact.value  = task.gtask_id;
      fillCustomer(task);
    } else if(task.registry && task.registry.name){ // CARD TITLE REGISTRY
        const registryName = `[${task.registry.name}]`;
        aonCard.setTitleSection1(registryName)
    }


    if(!applicationParent.cauData){
      //DIV WORKGROUP AND TASKHOLDER
      const rowsDiv = createStartJustifiedRow();
      rowsDiv.element.style.width = "100%";
      columnsDiv.appendChild(rowsDiv.element);

      //-----------------WORKGROUP
      const workgroupSelect = createWorkgroup();
      workgroupSelect.style.width = "100%";
      rowsDiv.appendChild(workgroupSelect);
      fillWorkGroup(task, application);

      //-----------------TASK HOLDER
      const taskHolderSelect = createTaskHolder();
      taskHolderSelect.style.width = "100%";
      taskHolderSelect.style.marginLeft = "5px";
      rowsDiv.appendChild(taskHolderSelect);
    } else {   // addInfoCau
      task.setDescriptionJson({cauData:applicationParent.cauData});
    }
}

export const buildFormRequest = (div, aonMessengerChat) => {
    const task = aonMessengerChat.task;
    const application = aonMessengerChat.applicationEl;

    const aonCard = createCardMessenger(MSG.DATA, "");
    div.appendChild(aonCard);
    aonCard.getCard().style.margin = 0;
  
    const receiverDiv = createReceiverDiv();
    aonCard.setContent(receiverDiv.element);
  
    //-----------------TYPE PROCESS
    const typeProcess = createProcessType();
    typeProcess.style.width = "100%";
    receiverDiv.appendChild(typeProcess);
    fillProcessType(task);
    
     //-----------------WORKGROUP
    const workgroupSelect = createWorkgroup();
    workgroupSelect.style.width = "100%";
    workgroupSelect.style.marginLeft = "5px";
    receiverDiv.appendChild(workgroupSelect);
    fillWorkGroup(task, application);
  
  
    // DIV PROCESS
    const divProcess = createStartJustifiedColumn().element;
    divProcess.id = MESSENGER_IDS.PROCESS_DIV;
    div.appendChild(divProcess);
}

/**
 * 
 * @param {Object} source,status 
 * @returns icon, icon_color
 */
export const getIconJson =({source,status}) => {
    const {AON_MESSENGER_LIST_OPEN,AON_MESSENGER_LIST_CLOSE,AON_MESSENGER_LIST_ARCHIVE} = MessengerOptions;
    let icon = MATERIAL_ICONS.INFO;
    let icon_color = AON_MESSENGER_LIST_OPEN.icon_color;

    if(source===TASK_SOURCE.CAU) 
      icon = MATERIAL_ICONS.SUPPORT_AGENT;
    else if(source===TASK_SOURCE.REQUEST) 
      icon = MATERIAL_ICONS.ASSIGNMENT;
    if(status === TASK_STATUS.FINISHED) 
      icon_color = AON_MESSENGER_LIST_CLOSE.icon_color;
    else if(status === TASK_STATUS.DELETED) 
      icon_color = AON_MESSENGER_LIST_ARCHIVE.icon_color;
    
    return {
      icon,
      icon_color
    }
}