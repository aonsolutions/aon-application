import { API_URL, COLORS, CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG } from "../../../environments/environments";
import { openFileUrl } from "../../../services/fileService";
import { getCustomers } from "../../../services/registryService";
import { domainName } from "../../../services/request";
import { getTastHoldersWorkGroup } from "../../../services/taskHolderService";
import { newComponent, setAttributes, setFullDate, setStyles, setTime, waitEl } from "../../../services/utils";
import { createFormVacation } from "../forms/vacation";
import { ICON_TYPES, MessengerOptions, MESSENGER_COMPONENTS, MESSENGER_IDS, MESSENGER_VIEWS, TASK_SOURCE, TASK_STATUS, WORKFLOW_TYPE, WORKFLOW_TYPES } from "../MessengerEnums";
import { createAction, createAonSwitch, createAonTextArea, createCardMessenger, createChatMessage, createCustomer, createInputContact, createProcessType, createReceiverDiv, createRequestType, createStartJustifiedColumn, createStartJustifiedRow, createTaskHolder, createTaskTag, createWorkgroup, LEFT, RIGHT } from "./creationUtils";

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

//FILL REQUEST TYPE
const fillRequestType = ({source}) => {
    const aonSelect = document.getElementById(MESSENGER_IDS.SOURCE_TASK);

    let sources = [
        {value: TASK_SOURCE.QUERY, name: MSG[TASK_SOURCE.QUERY.toUpperCase()] },
        {value: TASK_SOURCE.REQUEST, name: MSG[TASK_SOURCE.REQUEST.toUpperCase()] },
    ];

    let aonMessenger = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER);
    if(aonMessenger && aonMessenger.getApplicationParent().dur.hasCallCenter())
        sources.push({value: TASK_SOURCE.CAU, name: "Soporte" });

    aonSelect.options = JSON.stringify(sources);
    
    if(source){  aonSelect.value = source; } 
}

//FILL WORKGROUP
const fillWorkGroup = async (task, application) => {
    try {
        const aonSelect = await waitEl(`#${MESSENGER_IDS.WORKGROUP}`);

        const workgroups = application.getParent()._workgroups;
        if(workgroups && workgroups.length>0){
            aonSelect.options = JSON.stringify( workgroups.map( wg=> ({...wg, id: wg.value}) ) );
        }
        if(task.workgroup && task.workgroup.id){
            aonSelect.value = task.workgroup.id;
            if(task.task_holder && task.task_holder.id)
                fillTaskHolder( task.workgroup.id, task.task_holder.id);
        } 

        aonSelect.addEventListener(EVENT.CHANGE, ({detail})=>{
            if(detail){
                task.setWorkgroup(detail);
                fillTaskHolder(detail.value);
            }
        });
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

        aonSelect.addEventListener(EVENT.CHANGE, ({detail})=>{
            if(detail){
                const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
                aonMessengerChat.task.setTaskHolder(detail);
            } 
        });
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

        aonSelect.addEventListener(EVENT.CHANGE, ({detail})=>{
            if(detail){
                const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
                aonMessengerChat.task.setRegistry(detail);
            } 
        });
    }
}

//FILL TAG
const fillTag = async (task) => {
    const aonSelect = await waitEl(`#${MESSENGER_IDS.TASKTAG}`).catch(e=>null);
    const applicationParent = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER).getApplicationParent();
    if(aonSelect){
        aonSelect.clear();
        const tags = applicationParent._tags;
        if(tags){
            aonSelect.options = JSON.stringify( tags.map( c=> ({...c, value: c.id}) ) );
        }
        if(task.getDescriptionJson().tag) aonSelect.value = task.getDescriptionJson().tag;

        aonSelect.addEventListener(EVENT.CHANGE, ({detail})=>{
            task.setDescriptionJson({tag:detail.id});
        })
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
                const {id, comment, type, creation_date, modification_date, task_holder:{name, alias, id:taskHolderId}} = workflow;
                const me = taskHolderId == meId; // if taskHolder id is me
                let message = {
                    id,
                    type,
                    comment,
                    direction: me ? RIGHT: LEFT,
                    date: creation_date,
                    modification_date,
                }

                if(!me)message.name = alias || name;

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
const fillProcessType =  ({source_id}) => {
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
        const task = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT).task;
        task.setSourceId(detail.value)
        task.setTitle(task.getTitle() +" "+detail.name);
      }
    });
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

    const message = setStyles( createChatMessage(properties, chat),{
        opacity : 0,
        marginTop : '20px',
        transition : ".25s"
    });
    /**
     * Appearing animation
     */
    setTimeout(() => setStyles(message, { opacity : 1, marginTop : '10px'}), 100);
    return message;
} 

/**
 * 
 * @param {HTMLElement} aonTextArea 
 * @returns {Array} [value, messageEl] message element html
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

    const messageEl = appendChatMessage({
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

    return [value, messageEl];
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

const changeFormProcess = ({value,name}, aonMessengerChat) => {
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

/**
 * 
 * @param {HTMLElement} aon-application
 * @param {HTMLElement} aon-messenger-chat columnns div form
 */
export const buildForm = (div, aonMessengerChat) => {
    const task = aonMessengerChat.task;
    const applicationParent = aonMessengerChat.applicationParentEl;
    //-----------------------CREATE FIRST CARD
    const aonCard = createCardMessenger(MSG.DATA, "");
    div.appendChild(aonCard);
    aonCard.getCard().style.margin = 0;
    if(task.source !== TASK_SOURCE.CAU && applicationParent.cauData && task.registry && task.registry.name){ // CARD TITLE REGISTRY
        const registryName = `[${task.registry.name}]`;
        aonCard.setTitleSection1(registryName)
    }
   //-----------------------END CREATE FIRST CARD

    //-----------------------CREATE DIV PROCESS
    const divProcess = createStartJustifiedColumn().element;
    divProcess.id = MESSENGER_IDS.PROCESS_DIV;
    div.appendChild(divProcess);
    //---------------------END CREATE DIV PROCESS


    const columnsDiv = createStartJustifiedColumn();
    aonCard.setContent(columnsDiv.element);
    
    //----------------TYPE REQUEST-----------
    const rowsDiv = createStartJustifiedRow();
    rowsDiv.element.style.width = "100%";
    columnsDiv.appendChild(rowsDiv.element);


    const columnsDivTwo = createStartJustifiedColumn();
    columnsDiv.appendChild(columnsDivTwo.element);

    //-----------------TYPE REQUEST
    const requestTypeSelect = createRequestType();
    requestTypeSelect.style.width = "50%";
    rowsDiv.appendChild(requestTypeSelect);
    if(task.id) requestTypeSelect.disabled = requestTypeSelect.readonly = true;
    requestTypeSelect.addEventListener(EVENT.CHANGE, ({detail})=>{
        task.cleanTask();
        if(detail){
            //------------------HTML CLEAN UP
            columnsDivTwo.element.innerHTML = "";
            divProcess.innerHTML = "";
            const aonTextArea = document.getElementById(MESSENGER_IDS.DESCRIPTION_TASK);
            if(aonTextArea)  aonTextArea.remove();
            //------------------HTML CLEAN UP
            
            if(detail.value){
                task.setSource(detail.value);
                task.setTitle(detail.name);
                if(detail.value === TASK_SOURCE.QUERY || detail.value === TASK_SOURCE.CAU){
                    formQuery(columnsDivTwo, aonMessengerChat);
                } else if(detail.value === TASK_SOURCE.REQUEST){
                    formRequest(columnsDivTwo, aonMessengerChat);
                }
            }
        }
    });
    fillRequestType(task);
    //-----------------END TYPE REQUEST

    const aonSwitch = createAonSwitch();
    aonSwitch.addEventListener(EVENT.CHANGE, (ev) => {
        // console.log(ev);
    });
    rowsDiv.appendChild(aonSwitch);
}


const formQuery = (columnsDiv, aonMessengerChat) => {
    const task = aonMessengerChat.task;
    const application = aonMessengerChat.applicationEl;
    const applicationParent = aonMessengerChat.applicationParentEl;

    //-------------------------TYPE REQUEST

    //-------------------------CAU--------------------------
    if(task.source === TASK_SOURCE.CAU && !applicationParent.cauData){
        // DIV CUSTOMER
        const rowsDivTwo = createStartJustifiedColumn();
        rowsDivTwo.element.style.width = "100%";
        columnsDiv.appendChild(rowsDivTwo.element);
        // CUSTOMER
        const customerSelect = createCustomer();
        customerSelect.default = true;
        customerSelect.style.width = "100%";
        rowsDivTwo.appendChild(customerSelect);
        fillCustomer(task);
        // DIV CONTACT
        const rowsDivThree = createStartJustifiedRow();
        rowsDivThree.element.style.width = "100%";
        columnsDiv.appendChild(rowsDivThree.element);
        //-----------------CONTACT
        const contact = createInputContact();
        contact.style.width = "100%";
        contact.addEventListener(EVENT.INPUT, ({target})=>{
          if(target.value) task.setGTaskId(target.value)
        });
        rowsDivThree.appendChild(contact);
        if(task.gtask_id) contact.value  = task.gtask_id;
        // TAG
        const tagSelect = createTaskTag();
        tagSelect.default = true;
        tagSelect.style.width = "100%";
        tagSelect.style.marginLeft = "5px";
        rowsDivThree.appendChild(tagSelect);
        fillTag(task);
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
      taskHolderSelect.default = true;
      taskHolderSelect.style.width = "100%";
      taskHolderSelect.style.marginLeft = "5px";
      rowsDiv.appendChild(taskHolderSelect);
    } else {   // addInfoCau
      task.setDescriptionJson({cauData:applicationParent.cauData});
    }

    addTaskDescription(aonMessengerChat);
}

const formRequest = (columnsDiv, aonMessengerChat) => {
    const task = aonMessengerChat.task;
    const application = aonMessengerChat.applicationEl;
  
    const receiverDiv = createReceiverDiv();
    columnsDiv.appendChild(receiverDiv.element);
  
    //-----------------TYPE PROCESS
    const typeProcess = createProcessType();
    typeProcess.default = true;
    typeProcess.style.width = "100%";
    receiverDiv.appendChild(typeProcess);
    typeProcess.addEventListener(EVENT.CHANGE, ({detail})=>{
        if(detail && detail.value)
            changeFormProcess(detail, aonMessengerChat);
    })
    fillProcessType(task);

    
     //-----------------WORKGROUP
    const workgroupSelect = createWorkgroup();
    workgroupSelect.default = true;
    workgroupSelect.style.width = "100%";
    workgroupSelect.style.marginLeft = "5px";
    receiverDiv.appendChild(workgroupSelect);
    fillWorkGroup(task, application);
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

export const downChat = () => {
    const chat = document.getElementById(MESSENGER_IDS.MESSENGER_CHAT);
    if(chat)
        chat.scrollTo(0, chat.scrollHeight); //GO DOWN

    addLine();

}
export const upChat = () => {
    const chat = document.getElementById(MESSENGER_IDS.MESSENGER_CHAT);
    if(chat)
        chat.scrollTo(0,0) //GO UP
    
}

const addLine = () => {
    /**
     * Setting the chat line once all is rendered
     * DO NOT change this, is compulsory.
     */
    const lined = document.querySelector(".continueLined");
    if (lined) lined.style.setProperty("--height", lined.scrollHeight + "px");
}

const addTaskDescription = (aonMessengerChat) => {
    const task = aonMessengerChat.task;
    const aonTextArea = createAonTextArea(`${MSG.WRITE_A_DESCRIPTION}...`);
    aonTextArea.id = MESSENGER_IDS.DESCRIPTION_TASK;

    if(!aonMessengerChat.isMobile()){ //-------------------------------------------------------DESKTOP
        const processDiv = document.getElementById(MESSENGER_IDS.PROCESS_DIV);
  
        setStyles(aonTextArea, { minHeight: '150x', maxHeight: '300px', position: 'relative'});
        processDiv.appendChild(aonTextArea);
        
        const label = aonTextArea.addLabelTextEnd();
        label.addEventListener(EVENT.CLICK, ()=> aonTextArea.clickFile());
    
        aonTextArea.addEventListener(EVENT.INPUT, ({target})=>{
          task.setFiles(target.FILES);
          if(target.value) task.setDescriptionJson({observation:target.value})
        });
        if(task && task.getDescriptionJson().observation) aonTextArea.value = task.getDescriptionJson().observation;
        checkFilesAddEventDescription(task);//check files description
        
    } else { //-------------------------------------------------MOBILE 
        const div = document.getElementById(MESSENGER_IDS.SECOND_DIV);
        setStyles(aonTextArea,{ height: "100%",  width: "100%", boxShadow : "none", marginTop : 0 });
        div.appendChild(aonTextArea);
        if(task && task.getDescriptionJson().observation) aonTextArea.value = task.getDescriptionJson().observation;

        // /**
        // * CHANGE STYLE AONTEXTAAREA
        // */
        let textAreaDiv = aonTextArea.getTextAreaDiv();
        if(textAreaDiv) textAreaDiv.style.padding = "20px";
        
        const aonTextAreaToolbar = aonTextArea.getToolbar();
        if(aonTextAreaToolbar){
            setStyles(aonTextAreaToolbar,{
                paddingLeft  : "calc(1.5em - 5px)",
                paddingRight : "calc(1.5em - 5px)",
                borderBottom : "1px solid #e0e0e0"
            });
        }
    }

    buildTextareaToolbar(aonTextArea, task, false);
}