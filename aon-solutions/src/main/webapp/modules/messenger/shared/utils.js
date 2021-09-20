import { API_URL, COLORS, CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG } from "../../../environments/environments";
import { openFileUrl } from "../../../services/fileService";
import { domainName } from "../../../services/request";
import {  setAttributes, setFullDate, setStyles, setTime } from "../../../services/utils";
import { createFormVacation } from "../forms/vacation";
import { ICON_TYPES, MessengerOptions, MESSENGER_COMPONENTS, MESSENGER_IDS, MESSENGER_VIEWS, TASK_SOURCE, TASK_STATUS, WORKFLOW_TYPE, WORKFLOW_TYPES } from "../MessengerEnums";
import { createAonSwitch, createAonTextArea, createCardMessenger, createChatMessage, createCustomer, createInputContact, createProcessType, createProject, createReceiverDiv, createRequestType, createStartJustifiedColumn, createStartJustifiedRow, createTaskHolder, createTaskTag, createWorkgroup, RIGHT } from "./creationUtils";
import { fillCustomer, fillProcessType, fillProject, fillRequestType, fillTag, fillWorkGroup } from "./fill";
import * as LS from  "../../../services/localStorageService";

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


/**
 * Choose icon for the actions
 * @param {Object} message
 * @returns {Object} actionJson message new object
 */
export const chooseIconMessage = ({type, date, name, comment}) => {
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
 * @param {HTMLElement} aon-messenger-chat 
 * @returns {Array} [value, messageEl] message element html
 */
export const sendMessage = async (aonTextArea, aonMessengerChat) => {
    await checkFilesAndSend(aonTextArea); //CHECK FILES COMMENT AND SEND

    const value =  aonTextArea.value;
    if(!value || (value && !value.trim().length)) return ;

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
    
    aonMessengerChat.data = task;

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
    const isClient = "OFFICE" !== LS.getCompany().type;
    //-----------------------CREATE FIRST CARD
    const aonCard = createCardMessenger(MSG.DATA, "");
    div.appendChild(aonCard);
    aonCard.getCard().style.margin = 0;
    if(task.source !== TASK_SOURCE.CAU && applicationParent.cau && task.registry && task.registry.name){ // CARD TITLE REGISTRY
        const registryName = `[${task.registry.name}]`;
        aonCard.setTitleSection1(registryName)
    }

    if(applicationParent.cauData && applicationParent.cauData.auth && applicationParent.cauData.auth.email){
        task.setGTaskId(applicationParent.cauData.auth.email);
    }
   //-----------------------END CREATE FIRST CARD

    //-----------------------CREATE DIV PROCESS
    const divProcess = createStartJustifiedColumn().element;
    divProcess.id = MESSENGER_IDS.PROCESS_DIV;
    div.appendChild(divProcess);
    //---------------------END CREATE DIV PROCESS


    const columnsDiv = createStartJustifiedColumn();
    aonCard.setContent(columnsDiv.element);

    const rowsDiv = createStartJustifiedRow();
    rowsDiv.element.style.width = "100%";
    columnsDiv.appendChild(rowsDiv.element);


    const columnsDivTwo = createStartJustifiedColumn().element;
    columnsDiv.appendChild(columnsDivTwo);

    //-----------------TYPE REQUEST
    const requestTypeSelect = createRequestType();
    requestTypeSelect.style.width = "100%";
    rowsDiv.appendChild(requestTypeSelect);
    if(task.id) requestTypeSelect.disabled = requestTypeSelect.readonly = true;
    //-----------------END TYPE REQUEST
    if(isClient){
        const btnInternal = createAonSwitch();
        rowsDiv.appendChild(btnInternal);
        if(task.id) btnInternal.disabled =  true;
        btnInternal.checked = task.getDescriptionJson().external ? true : false;
        btnInternal.addEventListener(EVENT.CHANGE, () => {
            changeRequestType(aonMessengerChat, task, requestTypeSelect, columnsDivTwo, divProcess);
            task.setDescriptionJson({external:btnInternal.isChecked()});
        });
    }
 
    requestTypeSelect.addEventListener(EVENT.CHANGE, ()=>changeRequestType(aonMessengerChat, task, requestTypeSelect, columnsDivTwo, divProcess));
    fillRequestType(task, aonMessengerChat);
}

/**
 * 
 * @param {HTMLElement} aonMessengerChat aon-messenger-chat
 * @param {Task} task Class task
 * @param {Select} requestTypeSelect aon select type request
 * @param {HTMLElement} columnsDivTwo appendChild
 * @param {HTMLElement} divProcess div process
 */
 const changeRequestType = (aonMessengerChat, task, requestTypeSelect, columnsDivTwo, divProcess) =>{
    task.cleanTask();
    const btnInternal = document.getElementById(MESSENGER_IDS.INTERNAL_TASK);
    const detail = requestTypeSelect.getDetail();
    if(detail){
        //------------------HTML CLEAN UP
        columnsDivTwo.innerHTML = "";
        divProcess.innerHTML = "";
        const aonTextArea = document.getElementById(MESSENGER_IDS.DESCRIPTION_TASK);
        if(aonTextArea)  aonTextArea.remove();
        //------------------HTML CLEAN UP
        
        if(detail.value){
            task.setSource(detail.value);
            task.setTitle(detail.name);
            if(detail.value === TASK_SOURCE.REQUEST){
                formRequest(columnsDivTwo, aonMessengerChat, btnInternal ? btnInternal.isChecked() : false);
            } else {
                formQuery(columnsDivTwo, aonMessengerChat, btnInternal ? btnInternal.isChecked() : false);
            }
        }
    }
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

/**
 * downChat down chat
 */
export const downChat = () => {
    const chat = document.getElementById(MESSENGER_IDS.MESSENGER_CHAT);
    if(chat){
        chat.scrollTo(0, chat.scrollHeight); //GO DOWN
        addLine(chat);
    }
}
/**
 * upChat up chat
 */
export const upChat = () => {
    const chat = document.getElementById(MESSENGER_IDS.MESSENGER_CHAT);
    if(chat)
        chat.scrollTo(0,0) //GO UP   
}

/**
 * 
 * @param {HTMLElement} chat add line element html
 * @returns null
 */
const addLine = (chat) => chat.style.setProperty("--height", chat.scrollHeight + "px");

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

/**
 * 
 * @param {HTMLElement} columnsDiv columns div append html
 * @param {HTMLElement} aonMessengerChat aon-messenger-chat
 * @param {Boolean} forManager btn by gestor(forManager)
 */
const formQuery = (columnsDiv, aonMessengerChat, forManager = false) => {
    const task = aonMessengerChat.task;
    const applicationParent = aonMessengerChat.applicationParentEl;
    const isClient = "OFFICE" !== LS.getCompany().type;
    //-------------------------CAU--------------------------
    if((task.source === TASK_SOURCE.CAU && !applicationParent.cau) || forManager){
        //-------------------------- DIV ADD CUSTOMER AND CONTACT
        let rowsDivThree =  createStartJustifiedRow().element;
        if(!isClient){
            rowsDivThree = addCustomerAndContact(task, aonMessengerChat, columnsDiv);
        } else {
            rowsDivThree.style.width = "100%";
            columnsDiv.appendChild(rowsDivThree);
        }
        if(forManager || isClient){
            // ------------------PROJECT
            const projectSelect = createProject();
            projectSelect.default = true;
            projectSelect.style.width = "100%";
            if(!isClient) projectSelect.style.marginLeft = "5px";
            rowsDivThree.appendChild(projectSelect);
            fillProject(task);
        } else {
            // -------------------------------------TAG
            const tagSelect = createTaskTag();
            tagSelect.default = true;
            tagSelect.style.width = "100%";
            tagSelect.style.marginLeft = "5px";
            rowsDivThree.appendChild(tagSelect);
            fillTag(task, aonMessengerChat);
        }
    } 

    if(!isClient || !forManager){
        if(!applicationParent.cau){
            //--------------------------DIV WORKGROUP AND TASKHOLDER
            addTaskHolderAndWorkgroup(task, aonMessengerChat, columnsDiv);
        } else {   // addInfoCau
            task.setDescriptionJson({cauData:applicationParent.cauData});
        }
    }

    addTaskDescription(aonMessengerChat);
}

/**
 * 
 * @param {HTMLElement} columnsDiv columns div append html
 * @param {HTMLElement} aonMessengerChat aon-messenger-chat
 * @param {Boolean} internal btn by gestor(internal)
 */
const formRequest = (columnsDiv, aonMessengerChat, forManager = false) => {
    const task = aonMessengerChat.task;
    const isClient = "OFFICE" !== LS.getCompany().type;

    const divTwo = createReceiverDiv().element;
    columnsDiv.appendChild(divTwo);

    //-----------------TYPE PROCESS
    const typeProcess = createProcessType();
    typeProcess.default = true;
    typeProcess.style.width = "100%";
    divTwo.appendChild(typeProcess);
    typeProcess.addEventListener(EVENT.CHANGE, ({detail})=>{
        if(detail && detail.value)
            changeFormProcess(detail, aonMessengerChat);
    })
    fillProcessType(task, aonMessengerChat);
        
    if(forManager){
        //--------- DIV PROJECT
        let rowsDivThree =  createStartJustifiedRow().element;
        rowsDivThree.style.width = "100%";
        columnsDiv.appendChild(rowsDivThree);
        // ------------------PROJECT
        const projectSelect = createProject();
        projectSelect.default = true;
        projectSelect.style.width = "100%";
        rowsDivThree.appendChild(projectSelect);
        fillProject(task);
    }

    if(!isClient) {
        //--------------------------DIV WORKGROUP AND TASKHOLDER
        addTaskHolderAndWorkgroup(task, aonMessengerChat, columnsDiv);
    }
}

const addTaskHolderAndWorkgroup = (task, aonMessengerChat, columnsDiv) => {
    const rowDiv = createStartJustifiedRow().element;
    rowDiv.style.width = "100%";
    columnsDiv.appendChild(rowDiv);
    //-----------------WORKGROUP
    const workgroupSelect = createWorkgroup();
    workgroupSelect.style.width = "100%";
    rowDiv.appendChild(workgroupSelect);
    fillWorkGroup(task, aonMessengerChat);

    //-----------------TASK HOLDER
    const taskHolderSelect = createTaskHolder();
    taskHolderSelect.default = true;
    taskHolderSelect.style.width = "100%";
    taskHolderSelect.style.marginLeft = "5px";
    rowDiv.appendChild(taskHolderSelect);
}

const addCustomerAndContact = (task, aonMessengerChat, columnsDiv) => {
    const rowsDivTwo = createStartJustifiedColumn().element;
    rowsDivTwo.style.width = "100%";
    columnsDiv.appendChild(rowsDivTwo);
    // CUSTOMER
    const customerSelect = createCustomer();
    customerSelect.default = true;
    customerSelect.style.width = "100%";
    rowsDivTwo.appendChild(customerSelect);
    fillCustomer(task, aonMessengerChat);
    // DIV CONTACT
    const rowsDivThree = createStartJustifiedRow().element;
    rowsDivThree.style.width = "100%";
    columnsDiv.appendChild(rowsDivThree);
    //-----------------CONTACT
    const contact = createInputContact();
    contact.style.width = "100%";
    contact.addEventListener(EVENT.INPUT, ({target})=>{
        if(target.value) task.setGTaskId(target.value)
    });
    rowsDivThree.appendChild(contact);
    if(task.gtask_id) contact.value  = task.gtask_id;

    return rowsDivThree;
}